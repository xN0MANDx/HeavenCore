package pl.nomand.heavencore.users;

public class UserTemp {

    private GUI openedGUI;
    private boolean openedInventory;

    public UserTemp() {}

    public GUI getOpenedGUI() {
        return openedGUI;
    }

    public void setOpenedGUI(GUI openedGUI) {
        this.openedGUI = openedGUI;
    }

    public boolean isOpenedInventory() {
        return openedInventory;
    }

    public void setOpenedInventory(boolean openedInventory) {
        this.openedInventory = openedInventory;
    }
}
