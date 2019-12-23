package bgu.spl.mics.application.passiveObjects;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;

/**
 * That's where Q holds his gadget (e.g. an explosive pen was used in GoldenEye, a geiger counter in Dr. No, etc).
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {
    private List<String> gadgets;

    private Inventory() {// For the singleton
        this.gadgets = new LinkedList<>();
    }

    /**
     * Static inner class (Bill Push singleton method)
     * That way we are sure the class instance is only defined once !
     */
    private static class InventoryHolder {
        private static Inventory instance = new Inventory();
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static Inventory getInstance() {
        return InventoryHolder.instance;
    }

    /**
     * Initializes the inventory. This method adds all the items given to the gadget
     * inventory.
     * <p>
     *
     * @param inventory Data structure containing all data necessary for initialization
     *                  of the inventory.
     */
    public void load(String[] inventory) {
        this.gadgets.addAll(Arrays.asList(inventory));// Changed from array to list and added to gadgets
    }

    public List<String> getGadgets() {
        return this.gadgets;
    }

    /**
     * acquires a gadget and returns 'true' if it exists.
     * <p>
     *
     * @param gadget Name of the gadget to check if available
     * @return ‘false’ if the gadget is missing, and ‘true’ otherwise
     */
    public boolean getItem(String gadget) {
        if (this.gadgets.contains(gadget)) {// Means that we have the gadget
            synchronized (this) {// In order that no two threads would try to access the same gadget
                this.gadgets.remove(gadget);
            }
            return true;
        } else
            return false;
    }

    /**
     * <p>
     * Prints to a file name @filename a serialized object List<Gadget> which is a
     * List of all the reports in the diary.
     * This method is called by the main method in order to generate the output.
     */
    public void printToFile(String filename) throws IOException {
        FileWriter fileWriter = new FileWriter(filename);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        for (String gadget : gadgets) {
            printWriter.println(gadget);
        }
        printWriter.close();
    }
}
