package posSystem;

import java.text.DecimalFormat;

/**
 * Class name: Item Authors: Kendrick Tsz-Kin Yeung Date: 9 Apr 2019
 *
 * Description: This class create an Item type that stores the information of an
 * Item
 */
public class Item implements Comparable<Item>{

    private String itemId;
    private Double itemPrice;
    private String itemName;
    private int inventory;
    private int purchaseQty;
    private static DecimalFormat df2 = new DecimalFormat(".00");

    /**
     * Default constructor
     */
    public Item() {
    }

    /**
     * Constructor that will creates a new Item with details of this new item
     *
     * @param itemId ID of the new item
     * @param itemPrice price of the new item
     * @param itemName name of the new item
     * @param inventory inventory of the new item
     */
    public Item(String itemId, Double itemPrice, String itemName, int inventory) {
        setItemId(itemId);
        setItemPrice(itemPrice);
        setItemName(itemName);
        setInventory(inventory);
    }

    /**
     * Accessor for the itemId
     *
     * @return the ID of the item
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * Mutator for the itemId
     *
     * @param itemId item's new ID
     */
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    /**
     * Accessor for the itemPrice
     *
     * @return the price of the item
     */
    public Double getItemPrice() {
        return itemPrice;
    }

    /**
     * Mutator for the itemPrice
     *
     * @param itemPrice item's new price
     */
    public void setItemPrice(Double itemPrice) {
        this.itemPrice = Double.parseDouble(df2.format(itemPrice));
    }

    /**
     * Accessor for the itemPrice
     *
     * @return the price of the item
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * Mutator for the itemName
     *
     * @param itemName item's new name
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    /**
     * Accessor for the itemPrice
     *
     * @return the price of the item
     */
    public Integer getInventory() {
        return inventory;
    }

    /**
     * Mutator for the inventory
     *
     * @param inventory item's new inventory
     */
    public void setInventory(int inventory) {
        this.inventory = inventory;
    }

    /**
     * Accessor for the purchaseQty
     *
     * @return the purchaseQty of the item
     */
    public int getPurchaseQty() {
        return purchaseQty;
    }

    /**
     * Mutator for the purchaseQty
     *
     * @param purchaseQty item's purchaseQty
     */
    public void setPurchaseQty(int purchaseQty) {
        this.purchaseQty = purchaseQty;
    }

    /**
     * This method will update the new inventory when called
     */
    public void updateInventory() {
        inventory -= purchaseQty;
    }

    /**
     * This method will calculate the total price of the item when it was being
     * selected
     *
     * @return the total price
     */
    public double getTotalPrice() {
        return itemPrice * purchaseQty;
    }

    /**
     * This method will get the item's information in one string that will be added
     * in the receipt
     * @return the receipt line of item
     */
    public String toReceipt() {
        return String.format("\n      %25s %5.2f\n\t\tQty: %2d @ %1.2f",
                getItemName().substring(0, 25),
                getTotalPrice(), getPurchaseQty(), getItemPrice());
    }

    @Override
    public String toString() {
        return String.format("%5s\t%-30s\t%-5.2f\t", itemId,
                itemName, itemPrice);
    }

    @Override
    public int compareTo(Item o) {
        return this.getItemId().compareTo(o.getItemId());
    }
}
