package pl.nomand.heavencore.common;

import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;

public class Utils {

    private static final DecimalFormat df = new DecimalFormat("0.00");

    public static double getRandomDouble(double min, double max) {
        Random r = new Random();
        return min + (max - min) * r.nextDouble();
    }

    public static float getRandomFloat(float min, float max) {
        Random r = new Random();
        return min + (max - min) * r.nextFloat();
    }

    public static int getRandomInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    public static boolean chanceOf(double chance) {
        if (chance == 0d)
            return false;

        return Utils.getRandomDouble(0.0, 100.0) <= chance;
    }

    public static boolean chanceOf(float chance) {
        if (chance == 0f)
            return false;

        return Utils.getRandomFloat(0.0f, 100.0f) <= chance;
    }

    public static boolean chanceOf(int chance) {
        return Utils.getRandomInt(1, 100) <= chance;
    }

    public static void sendActionBar(Player player, String message) {
        PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public static String deleteZero(double x) {
        String ret;
        if (x == 0.0) {
            ret = "0";
        } else if (x % Math.ceil(x) == 0) {
            ret = (long) Math.ceil(x) + "";
        } else {
            ret = String.format(Locale.US, "%.2f", x);

            if (ret.substring(ret.length() - 3).equals(".00")) {
                ret = ret.substring(0, ret.length()-3);
            } else if (ret.substring(ret.length() - 1).equals("0")) {
                ret = ret.substring(0, ret.length()-1);
            }
        }
        return ret;
    }

    public static String deleteZero(float x) {
        String ret;
        if (x == 0.0) {
            ret = "0";
        } else if (x % Math.ceil(x) == 0) {
            ret = (long) Math.ceil(x) + "";
        } else {
            ret = String.format(Locale.US, "%.2f", x);

            if (ret.substring(ret.length() - 3).equals(".00")) {
                ret = ret.substring(0, ret.length()-3);
            } else if (ret.substring(ret.length() - 1).equals("0")) {
                ret = ret.substring(0, ret.length()-1);
            }
        }
        return ret;
    }

    public static boolean hasFreeSlot(Player p) {
        return Utils.getInventoryFreeSlots(p) > 0;
    }

    public static int getInventoryFreeSlots(Player p) {
        int slots = 0;

        for (ItemStack is : p.getInventory().getContents())
            if (is == null || is.getType() == Material.AIR)
                slots++;

        return slots;
    }

    public static double convertDouble(double x) {
        return Double.parseDouble(df.format(x));
    }

    public static Object getPrivateField(String fieldName, Class clazz, Object object) {
        Field field;
        Object o = null;

        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            o = field.get(object);
        } catch(NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return o;
    }

    public static String convertNumberFormat(long x) {
        DecimalFormatSymbols customSymbols = DecimalFormatSymbols.getInstance(Locale.US);
        customSymbols.setGroupingSeparator(' ');
        return new DecimalFormat("#,###;-#,###", customSymbols).format(x);
    }

}
