package posSystem;

import java.io.IOException;
import java.util.*;
import javafx.collections.*;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

/**
 * Class name: CheckOutPane Authors: Kendrick Tsz-Kin Yeung, Malcolm Busari
 * Date: 9 Apr 2019
 *
 * Description:
 */
public class CheckOutPane extends GridPane {

    private static TableView table;
    private TableColumn itemId, itemName, itemPrice, qty;
    private static ComboBox<String> cbxItems;
    private TextField txtAmt;
    private Button btnSearch, btnPlus, btnMinus, btnSelect,
            btnRemove, btnConfirm, btnCheck;
    private static Label lblPriceText, lblPrice;
    private ReceiptScene receipt;
    static ArrayList<Item> selectedItems = new ArrayList<>();
    POSSystem main = POSSystem.getInstance();

    public CheckOutPane() {
        getComponents();
    }

    /**
     * This method will call the readFile() method from the ItemList itemList
     * that was declared in the POSSystem, then will call the getItemNames()
     * method from the Mainpane to write all available items in the ItemList
     * class, then will get all the necessary components for this Pane.
     */
    private void getComponents() {
        try {
            main.itemList.readFile();
            Collections.sort(main.itemList);
        } catch (IOException ex) {
        }
        table = new TableView();
        itemId = new TableColumn("ID");
        itemId.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        itemId.getStyleClass().add("table-view-header");
        itemName = new TableColumn("Name");
        itemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        itemName.getStyleClass().add("table-view-header");
        itemPrice = new TableColumn("Price");
        itemPrice.setCellValueFactory(new PropertyValueFactory<>("itemPrice"));
        itemPrice.getStyleClass().add("table-view-header");
        qty = new TableColumn("Qty");
        qty.setCellValueFactory(new PropertyValueFactory("purchaseQty"));
        qty.getStyleClass().add("table-view-header");
        table.getColumns().addAll(itemId, itemName, itemPrice, qty);

        cbxItems = new ComboBox();
        cbxItems.getItems().setAll(main.itemList.getItemNames());
        cbxItems.setPromptText(String.format("%-30s", "Select.."));
        cbxItems.getStyleClass().add("checkout-pane-combobox");

        btnSearch = new Button("_Search...");
        btnSearch.setOnAction(e -> eventCode(e));
        btnSearch.getStyleClass().add("checkout-pane-search-button");
        btnSearch.setMnemonicParsing(true);
        txtAmt = new TextField("0");
        txtAmt.setEditable(false);
        txtAmt.setMouseTransparent(true);
        btnPlus = new Button("+");
        btnPlus.setOnAction(e -> eventCode(e));
        btnMinus = new Button("-");
        btnMinus.setOnAction(e -> eventCode(e));

        btnSelect = new Button("Select _this item");
        btnSelect.setOnAction(e -> eventCode(e));
        btnSelect.getStyleClass().add("checkout-pane-select-button");
        btnSelect.setMnemonicParsing(true);
        btnRemove = new Button("_Remove");
        btnRemove.setOnAction(e -> eventCode(e));
        btnRemove.getStyleClass().add("checkout-pane-remove-button");
        btnRemove.setMnemonicParsing(true);
        btnConfirm = new Button("_Confirm");
        btnConfirm.setOnAction(e -> eventCode(e));
        btnConfirm.getStyleClass().add("checkout-pane-confirm-button");
        btnConfirm.setMnemonicParsing(true);
        btnCheck = new Button("Check Inventory");
        btnCheck.setOnAction(e -> eventCode(e));
        btnCheck.getStyleClass().add("checkout-pane-check-button");

        HBox pnlSelRem = new HBox(10, btnSelect, btnRemove);

        lblPriceText = new Label("Total:");
        lblPriceText.getStyleClass().add("checkout-pane-label-price");
        lblPrice = new Label(String.format("$%8.2f", 0.00));
        lblPrice.getStyleClass().add("checkout-pane-label-price");

        HBox pnlPrice = new HBox(lblPriceText, lblPrice);

        this.add(table, 0, 0, 4, 10);
        this.add(cbxItems, 4, 0, 3, 1);
        this.add(btnSearch, 4, 1, 3, 1);
        this.add(txtAmt, 4, 2);
        this.add(btnPlus, 5, 2);
        this.add(btnMinus, 6, 2);
        this.add(pnlSelRem, 4, 4, 5, 4);
        this.add(pnlPrice, 4, 8, 3, 1);
        this.add(btnConfirm, 4, 9, 3, 1);
        this.add(btnCheck, 0, 10, 6, 1);
        this.getColumnConstraints().add(new ColumnConstraints(80));
        this.getColumnConstraints().add(new ColumnConstraints(80));
        this.getColumnConstraints().add(new ColumnConstraints(80));
        this.getColumnConstraints().add(new ColumnConstraints(80));

        this.setAlignment(Pos.CENTER);
        this.setVgap(10);
        this.setHgap(5);
    }

    private void eventCode(Event e) {
        if (e.getSource() == btnCheck) {
            main.inventory = new InventoryScene();
            main.inventory.showAndWait();
        }
        if (e.getSource() == btnRemove) {
            deleteSelectedItem();
        }
        if (e.getSource() == btnConfirm) {
            if (selectedItems.size() > 0) {
                receipt = new ReceiptScene();
                receipt.showAndWait();
            } else {
                showWarning("No item was selected to be checkout");
            }
        }
        if (e.getSource() == btnSearch) {
            changeSelection(foundItem(searchItem(main)));
            
        }
        if (e.getSource() == btnPlus) {
            Integer newNum = Integer.parseInt(txtAmt.getText()) + 1;
            txtAmt.setText(newNum.toString());
        }
        if (e.getSource() == btnMinus) {
            if (Integer.parseInt(txtAmt.getText()) > 0) {
                Integer newNum = Integer.parseInt(txtAmt.getText()) - 1;
                txtAmt.setText(newNum.toString());
            }
        }
        if (e.getSource() == btnSelect) {
            addSelectedItem();
        }
    }

    /**
     * This method will add an item that was selected in the comboBox and
     * refresh the tableView of selected items
     */
    private void addSelectedItem() {
        table.getItems().clear();
        int index = cbxItems.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            if (Integer.parseInt(txtAmt.getText()) > main.itemList.get(index).getInventory()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Selected quantity: " + Integer.parseInt(txtAmt.getText())
                        + ", inventory left: " + main.itemList.get(index).getInventory());

                ObservableList<Item> data = FXCollections.observableArrayList(selectedItems);
                table.setItems(data);
                txtAmt.setText("0");
                alert.showAndWait();
            } else {
                boolean exist = false;
                main.itemList.get(index).setPurchaseQty(Integer.parseInt(txtAmt.getText()));
                for (Item selectedItem : selectedItems) {
                    if (selectedItem.getItemId().equals(main.itemList.get(index).getItemId())) {
                        if (Integer.parseInt(txtAmt.getText()) == 0) {
                            selectedItems.remove(selectedItem);
                        } else {
                            selectedItem = main.itemList.get(index);
                        }
                        exist = true;
                        break;
                    }
                }
                if (!exist && Integer.parseInt(txtAmt.getText()) > 0) {
                    selectedItems.add(main.itemList.get(index));
                }
                ObservableList<Item> data = FXCollections.observableArrayList(selectedItems);
                table.setItems(data);
                txtAmt.setText("0");
                displayTotal();
            }
        } else {
            showWarning("Please select an Item");
        }
    }

    /**
     * This method will delete the selected item in the tableView from the
     * itemList after being confirmed by the user
     */
    private void deleteSelectedItem() {
        int index = table.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            String deleteItems = "Are you sure to delete: ";
            ObservableList<Item> itemSelected, allItems;
            allItems = table.getItems();
            itemSelected = table.getSelectionModel().getSelectedItems();
            for (int i = 0; i < itemSelected.size(); i++) {
                deleteItems += itemSelected.get(i).getItemName().trim() + " ";
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, deleteItems,
                    ButtonType.YES, ButtonType.NO);
            alert.setTitle("Delete");
            alert.setHeaderText(null);
            Optional<ButtonType> delete = alert.showAndWait();
            if (delete.get() == ButtonType.YES) {
                itemSelected.forEach(allItems::remove);
                selectedItems.remove(index);
                displayTotal();
            }
        } else {
            showWarning("No item was selected");
        }
    }

    /**
     * This method will search for item(s) from the itemList with either the
     * item's ID or it's name, if an item(s) were found, it will return those items
     * @param main the location where the full arrayList of item are stored
     * @return an arrayList of found items
     */
    public static ArrayList<Item> searchItem(POSSystem main) {
        TextInputDialog input = new TextInputDialog();
        ArrayList<Item> foundItems = new ArrayList<>();
        input.setTitle("Input Dialog");
        input.setHeaderText(null);
        input.setContentText("Enter items ID or name");
        Optional<String> itemInfo = input.showAndWait();
        if (itemInfo.isPresent()) {
            for (Item item : main.itemList) {
                if (item.getItemName().contains(itemInfo.get())
                        || item.getItemId().contains(itemInfo.get())) {
                    foundItems.add(item);
                }
            }
        }
        return foundItems;
    }
    
    /**
     * If an item was found, this method will be called to ask which item the
     * user is looking for and will return that item
     * @param foundItems an arrayList of found items
     * @return the final selected item
     */
    public static Item foundItem(ArrayList<Item> foundItems) {
        Item item = null;
        if (!foundItems.isEmpty()) {
            Item[] items = new Item[foundItems.size()];
            for (int i = 0; i < items.length; i++) {
                items[i] = foundItems.get(i);
            }
            ChoiceDialog<Item> dialog = new ChoiceDialog<>(items[0], items);
            dialog.setTitle("Item(s) found");
            dialog.setHeaderText(null);
            dialog.setContentText("Choose an item");
            Optional<Item> result = dialog.showAndWait();
            if (result.isPresent()) {
                item = result.get();
            }
        } else {
            showWarning("There's no such an Item in the record.");
        }
        return item;
    }
    
    /**
     * This method will receive an item and change the combobox selection to the
     * item that was passed to this method
     * @param item item that will be selected in the combobox
     */
    private void changeSelection(Item item){
        if (item != null) {
            cbxItems.getSelectionModel().select(item.getItemName().substring(0, 42));
        }
    }

    /**
     * This method will change the price display with total price before tax
     */
    private void displayTotal() {
        double total = 0;
        for (Item selectedItem : selectedItems) {
            total += selectedItem.getPurchaseQty() * selectedItem.getItemPrice();
        }
        lblPrice.setText(String.format("$%8.2f", total));
    }

    /**
     * This method will display an error alert and abort the action
     *
     * @param contentText error message
     */
    public static void showWarning(String contentText) {
        Alert alert = new Alert(Alert.AlertType.WARNING, contentText);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    /**
     * This method will reset all the control elements in this pane
     */
    public static void clear() {
        table.getItems().clear();
        selectedItems.clear();
        lblPrice.setText(String.format("$%8.2f", 0.00));
        cbxItems.getSelectionModel().select(-1);
    }
}
