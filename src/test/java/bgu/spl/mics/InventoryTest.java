package bgu.spl.mics;


import bgu.spl.mics.application.passiveObjects.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class InventoryTest {

    private List<String> gadgets;
    private Inventory inventory;

    @BeforeEach
    public void setUp() {
        inventory = Inventory.getInstance();
        gadgets = inventory.getGadgets();
    }

    @Test
    void Load() {
        try {
            inventory.load(null);
            fail("Can't load a null array");
        }catch(Exception e){
            //Test pass
        }
        String[] list = {"car", "gun", "hammer", "pistol"};
        inventory.load(list);
        assertTrue(gadgets.containsAll(Arrays.asList(list)), "All the gadgets should be in gadgets");
        // Not needed but safer
        gadgets.removeAll(Arrays.asList(list));
        assertTrue(gadgets.isEmpty(), "gadgets should be empty");
    }

    @Test
    void GetItem() {
        String object = "gun";
        assertFalse(inventory.getItem(object));
        gadgets.add(object);
        assertTrue(inventory.getItem(object));
        gadgets.remove(object);
        assertFalse(inventory.getItem(object));
    }


}
