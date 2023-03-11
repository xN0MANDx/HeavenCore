package pl.nomand.heavencore.pets;


import de.tr7zw.nbtapi.NBTItem;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pl.nomand.heavencore.common.Utils;
import pl.nomand.heavencore.pets.bonuses.Bon;
import pl.nomand.heavencore.pets.bonuses.BonType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Pet {

    public static final double ADD = Math.PI/20;

    private final PetManager manager;
    private final Player owner;
    private final PetTemplate template;

    // Permanent
    private final List<Bon> bons;
    private final PetExperience experience;
    private String customName;

    // Temp Attributes
    private final Set<Player> viewers = new HashSet<>();
    private boolean spawned;
    private int taskId;
    private Location location;
    private double height = 0;
    private EntityArmorStand entity;
    private PacketPlayOutEntityEquipment skullPacket;
//    private WrapperPlayServerEntityEquipment skullPacket;

    // Loading From Item
    public Pet(Player owner, ItemStack item, PetManager manager) {
        this.manager = manager;
        this.owner = owner;

        NBTItem ni = new NBTItem(item);

        this.template = manager.getTemplate(ni.getString("pet"));

        if (ni.hasKey("pet.customName"))
            this.customName = ni.getString("pet.customName");

        experience = new PetExperience(this, ni.getInteger("pet.level"), ni.getLong("pet.exp"));

        bons = new ArrayList<>();
        for(BonType type : BonType.values())
            if (ni.hasKey("pet.bons."+type))
                bons.add(new Bon(type, ni.getDouble("pet.bons."+type)));
    }

    // Direct Loading Of New Pet
    public Pet(Player owner, PetTemplate template, PetManager manager) {
        this.manager = manager;
        this.owner = owner;
        this.template = template;
        this.experience = new PetExperience(this, 0, 0);
        this.bons = new ArrayList<>(template.getDefaultBons());
    }

    // Loading Pet From User File
    public Pet(Player owner, PetManager manager, YamlConfiguration yml) {
        this.manager = manager;
        this.owner = owner;

        template = manager.getTemplate(yml.getString("pet.template"));

        if (yml.isSet("pet.customName"))
            customName = yml.getString("pet.customName");

        bons = new ArrayList<>();
        if (yml.isSet("pet.bons")) {
            for (String x : yml.getConfigurationSection("pet.bons").getKeys(false)) {
                BonType bonType = BonType.valueOf(x);
                double value = yml.getDouble("pet.bons." + x);
                bons.add(new Bon(bonType, value));
            }
        }

        experience = new PetExperience(this, yml);
    }

    public void save(YamlConfiguration yml) {
        yml.set("pet.template", template.getId());
        yml.set("pet.customName", customName);

        for(Bon bon : bons)
            yml.set("pet.bons."+bon.getType().toString(), bon.getValue());

        experience.save(yml);
    }

    // Animations & Movement

    private void tick() {
        if (!spawned || !owner.isOnline())
            return;

        this.height += ADD;
        if (this.height > Math.PI*2)
            height = 0;

        double sin = Math.sin(this.height);

        Location loc = owner.getLocation();

        loc.setPitch(0);
        loc.setYaw(loc.getYaw() - 60);

        Vector dir = loc.getDirection();
        dir.setY(-0.25 + sin *0.125);
        dir.multiply(1.5);

        Location t = loc.clone().subtract(dir);

//        if (t.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
//            t = loc.add(dir);
//            if (t.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
//                t = owner.getLocation().add(0, 0.4, 0);
//            }
//        }

        this.entity.setLocation(t.getX(), t.getY(), t.getZ(), t.getYaw() + 60, t.getPitch());

        location = t;

        updatePositionView();
    }

    // Handlers

    public void spawn() {
        if (spawned) return;

        location = owner.getLocation();
        initEntity(owner.getWorld());

        manager.getMain().getUserManager().getUser(owner).addBons(this.bons);

        showToEveryone();
        spawned = true;

        BukkitRunnable run = new BukkitRunnable() {
            public void run() {
                tick();
            }
        };
        run.runTaskTimer(manager.getMain(), 1, 1);
        taskId = run.getTaskId();
    }

    public void despawn() {
        if (!spawned) return;

        manager.getMain().getUserManager().getUser(owner).resetBons();

        hideFromEveryone();
        spawned = false;

        Bukkit.getScheduler().cancelTask(taskId);
    }

    private void initEntity(World world) {
        ItemStack skull = template.getSkull();

        entity = new EntityArmorStand(((CraftWorld) world).getHandle());
        entity.setGravity(false);
        entity.setCustomName("§8[§fLvl "+experience.getLevel()+"§8] "+getName());
        entity.setCustomNameVisible(true);
        entity.setInvisible(true);
        entity.setEquipment(4, template.getNetSkull());
        entity.setSmall(true);

        skullPacket = new PacketPlayOutEntityEquipment(entity.getBukkitEntity().getEntityId(), 4, template.getNetSkull());
    }

    public void updatePositionView() {
        viewers.forEach(this::sendTeleportPacket);
    }

    public void showTo(Player p) {
        if (viewers.contains(p))
            return;

        CraftPlayer player = (CraftPlayer) p;
        player.getHandle().playerConnection.sendPacket(getSpawnPacket());
        player.getHandle().playerConnection.sendPacket(skullPacket);

        viewers.add(p);
    }

    public void hideFrom(Player p) {
        if (!viewers.contains(p))
            return;

        CraftPlayer player = (CraftPlayer) p;
        player.getHandle().playerConnection.sendPacket(getDestroyPacket());
        viewers.remove(p);
    }

    private void showToEveryone() {
        location.getWorld().getPlayers().forEach(this::showTo);
    }

    private void hideFromEveryone() {
        new ArrayList<>(viewers).forEach(this::hideFrom);
    }

    // Packets

    private PacketPlayOutSpawnEntityLiving getSpawnPacket() {
        return new PacketPlayOutSpawnEntityLiving(this.entity);
    }

    private PacketPlayOutEntityTeleport getTeleportPacket() {
        return new PacketPlayOutEntityTeleport(this.entity);
    }

    private PacketPlayOutEntityDestroy getDestroyPacket() {
        return new PacketPlayOutEntityDestroy(this.entity.getBukkitEntity().getEntityId());
    }

    private void sendTeleportPacket(Player p) {
        CraftPlayer player = (CraftPlayer) p;
        player.getHandle().playerConnection.sendPacket(getTeleportPacket());
    }

    // Item

    public ItemStack toItem() {
        ItemStack item = template.getSkull().clone();
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setDisplayName(customName != null ? customName : template.getName());
        List<String> lore = new ArrayList<>();

        meta.setDisplayName("§7[Lvl "+experience.getLevel()+"] "+getName());

        lore.add(template.getRarity().getName());
        lore.add("");

        if (bons.size() > 0) {
            lore.add("§6Bonusy");
            bons.forEach(bon -> lore.add("§7"+bon.getType().getName()+": §a+"+ Utils.deleteZero(bon.getValue())+(bon.getType().isUnit() ? "" : "%") ));
            lore.add("");
        }

        if (experience.getLevel() != experience.getMaxLevel()) {
            lore.add("§7Postep do poziomu "+(experience.getLevel()+1)+": §e"+experience.getPercentExp());
            lore.add(experience.getProgressBar(20, '2', 'f')+" §e"+experience.getExp()+"§6/§e"+experience.getRequirementExp());
        } else {
            lore.add("§7Osiagnieto najwiekszy poziom.");
        }

        meta.setLore(lore);
        item.setItemMeta(meta);

        // NBT TAGS

        NBTItem ni = new NBTItem(item);

        ni.setString("pet", template.getId());
        if (customName != null)
            ni.setString("pet.customName", getName());
        ni.setInteger("pet.level", experience.getLevel());
        ni.setLong("pet.exp", experience.getExp());

        for(Bon bon : bons)
            ni.setDouble("pet.bons."+bon.getType(), bon.getValue());

        // FINAL ITEMSTACK

        return ni.getItem();
    }

    // Getters & Setters

    public String getName() {
        return ChatColor.translateAlternateColorCodes('&', customName != null ? customName : template.getName());
    }

    public List<Bon> getBons() {
        return bons;
    }

    public PetExperience getExperience() {
        return experience;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public Player getOwner() {
        return owner;
    }

    public Location getLocation() {
        return location;
    }

    public PetTemplate getTemplate() {
        return template;
    }

    public PetManager getManager() {
        return manager;
    }
}
