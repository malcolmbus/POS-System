package posSystem;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Class name: ItemList Authors: Kendrick Tsz-Kin Yeung
 * Date: 9 Apr 2019
 *
 * Description:
 */
public class ItemList extends ArrayList<Item> {

    File file;
    Scanner input;
    final int RECORD_SIZE = 122;
    static RandomAccessFile raf;
    ArrayList<Item> matchedItems = new ArrayList<>();
    final static Class<?> referenceClass = POSSystem.class;
    final static URL url
            = referenceClass.getProtectionDomain().getCodeSource().getLocation();

    /**
     * Default constructor
     */
    public ItemList() {
    }

    /**
     * This method will loop through all the items stored inside the arrayList,
     * and will write all the item names into a string array
     *
     * @return an array of item names
     */
    public String[] getItemNames() {
        String[] names = new String[this.size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = this.get(i).getItemName().substring(0, 42);
        }
        return names;
    }

    /**
     * This method will loop through all the items stored inside the arrayList,
     * and will write all the item ID into a string array
     *
     * @return an array of item ID
     */
    public String[] getItemIDs() {
        String[] IDs = new String[this.size()];
        for (int i = 0; i < IDs.length; i++) {
            IDs[i] = this.get(i).getItemId();
        }
        return IDs;
    }

    /**
     * This method will loop through the arrayList and get the item that the ID
     * match the given ID
     *
     * @param ID ID of an Item
     * @return found item with same ID
     */
    public Item searchID(String ID) {
        Item foundItem = null;
        for (Item item : this) {
            if (item.getItemId().equals(ID)) {
                foundItem = item;
                break;
            }
        }
        return foundItem;
    }

    /**
     * A read method that reads data from the datafile and write into the
     * arrayList
     *
     * @throws IOException
     */
    public void readFile() throws IOException {
        File f;
        try {
            f = new File(getJarPath() + ".\\datas\\inventory.dat");
            raf = new RandomAccessFile(f, "rw");
            this.clear();
            long recNum = raf.length() / RECORD_SIZE;
            raf.seek(0);
            for (int i = 0; i < recNum; i++) {
                String id = readString(raf, 5);
                double price = raf.readDouble();
                String name = readString(raf, 50);
                int inventory = raf.readInt();
                this.add(new Item(id, price, name, inventory));
            }
        } catch (URISyntaxException ex) {
        }
    }

    /**
     * A write method that reads data from the arraylist and write into the
     * Inventory file
     *
     * @throws IOException
     */
    public void update() throws IOException {
        File f;
        try {
            f = new File(getJarPath() + ".\\datas\\inventory.dat");
            raf = new RandomAccessFile(f, "rw");
            raf.setLength(this.size() * RECORD_SIZE);
            raf.seek(0);
            for (Item item : this) {
                String id = item.getItemId();
                id = prepStringField(id, 5);
                raf.writeChars(id);
                double price = item.getItemPrice();
                raf.writeDouble(price);
                String name = item.getItemName();
                name = prepStringField(name, 50);
                raf.writeChars(name);
                int inventory = item.getInventory();
                raf.writeInt(inventory);
            }
        } catch (URISyntaxException ex) {
        }
    }

    /**
     * This method will prepare the String field that fits the record size
     *
     * @param value name of an Item
     * @param size assigned size of a string
     * @return a string that fits the record size
     */
    private static String prepStringField(String value, int size) {
        if (value.length() < size) {
            int numSpaces = size - value.length();
            for (int i = 1; i <= numSpaces; i++) {
                value += " ";
            }
        } else {
            value = value.substring(0, size);
        }
        return value;
    }

    /**
     * This method will read a string with assigned length and return the string
     *
     * @param raf a RandomAccessFile object
     * @param size assigned size of a string
     * @return the string in the record
     * @throws IOException
     */
    private String readString(RandomAccessFile raf, int size)
            throws IOException {
        String n = "";
        for (int i = 0; i < size; i++) {
            n += String.valueOf(raf.readChar());
        }
        return n;
    }

    public static String getJarPath() throws URISyntaxException {
        final File jarPath = new File(url.toURI()).getParentFile();
        return jarPath.getPath();
    }
}
