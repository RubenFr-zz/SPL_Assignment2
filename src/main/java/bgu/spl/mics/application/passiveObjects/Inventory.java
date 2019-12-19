package bgu.spl.mics.application.passiveObjects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * That's where Q holds his gadget (e.g. an explosive pen was used in GoldenEye, a geiger counter in Dr. No, etc).
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {

    private final List<String> gadgets;

    private Inventory() {
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
        gadgets.addAll(Arrays.asList(inventory));
    }

    public List<String> getGadgets() {
        return gadgets;
    }

    /**
     * acquires a gadget and returns 'true' if it exists.
     * <p>
     *
     * @param gadget Name of the gadget to check if available
     * @return ‘false’ if the gadget is missing, and ‘true’ otherwise
     * <p>
     * After checking the gadget is in the list we lock the array so that if two threads
     * wish to get the same item only one get it
     */
    public boolean getItem(String gadget) {
        if (gadgets.contains(gadget)) {
            synchronized (gadgets) {
                if (gadgets.contains(gadget)) {
                    gadgets.remove(gadget);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * <p>
     * Prints to a file name @filename a serialized object List<Gadget> which is a
     * List of all the reports in the diary.
     * This method is called by the main method in order to generate the output.
     */
    public void printToFile(String filename) throws IOException {
        JsonObject jsonObject = new JsonObject();
        JsonArray inventory = new JsonArray();
        for ( String gadget : gadgets)
            inventory.add(gadget);
        jsonObject.add("Inventory", inventory);

        FileWriter file = new FileWriter(filename);
        file.write(new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject));
        file.close();
    }
}
