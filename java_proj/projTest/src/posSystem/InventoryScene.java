package posSystem;

import javafx.collections.*;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Class name: InventoryScene Authors: Kendrick Tsz-Kin Yeung, Malcolm Busari
 * Date: 9 Apr 2019
 *
 * Description:
 */
public class InventoryScene extends Stage {

    private Scene scene;
    private BorderPane pane;
    private MenuBar menuBar;
    private Menu optionMenu;
    private MenuItem updateItem;
    static TableView table;
    private TableColumn itemId, itemName, itemPrice, itemInventory;
    POSSystem main = POSSystem.getInstance();
    private UpdateItemScene update;

    /**
     * Default constructor
     */
    public InventoryScene() {
        super();
        getComponents();
        scene = new Scene(pane);
        String css = this.getClass().getResource("/css/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        this.setScene(scene);
        this.setTitle("Inventory");
        initModality(Modality.APPLICATION_MODAL);
    }

    /**
     * This method will create all the necessary components for the pane
     */
    private void getComponents() {
        pane = new BorderPane();
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
        itemInventory = new TableColumn("Inventory");
        itemInventory.setCellValueFactory(new PropertyValueFactory<>("inventory"));
        itemInventory.getStyleClass().add("table-view-header");
        table.getColumns().addAll(itemId, itemName, itemPrice, itemInventory);
        ObservableList<Item> data = FXCollections.observableArrayList(main.itemList);
        table.setItems(data);
        pane.setTop(getMenuBar());
        pane.setCenter(table);
    }

    /**
     * This method will create the menuBar for this Scene
     */
    private MenuBar getMenuBar() {
        updateItem = new MenuItem("_Update an Item");
        updateItem.setOnAction(e -> eventCode(e));
        updateItem.setAccelerator(new KeyCodeCombination(KeyCode.I, KeyCodeCombination.CONTROL_DOWN));
        optionMenu = new Menu("_Option");
        optionMenu.getItems().add(updateItem);
        menuBar = new MenuBar(optionMenu);
        return menuBar;
    }

    private void eventCode(Event e) {
        if (e.getSource() == updateItem) {
            if (main.itemList.size() > 0) {
                String passcode = main.promptInput("Modify inventory", "Enter passcode to modify the inventory.");
                if (main.checkPrivilege(passcode)) {
                    searchEditItem();
                } else if (passcode != null) {
                    CheckOutPane.showWarning("Access denied");
                }
            } else {
                CheckOutPane.showWarning("There are no any record");
            }
        }
    }

    /**
     * This method will search whether the item info entered by user exists if
     * item exist, a choice dialog will be used to ask which item does the user
     * wanna to modify
     */
    private void searchEditItem() {
        if (main.itemList.size() > 0) {
            Item item = CheckOutPane.foundItem(CheckOutPane.searchItem(main));
            if (item != null) {
                update = new UpdateItemScene(item);
                update.showAndWait();
            }
        }
    }
    
    public static void updateInvenory(POSSystem main) {
        InventoryScene.table.getItems().clear();
        ObservableList<Item> data = FXCollections.observableArrayList(main.itemList);
        InventoryScene.table.setItems(data);
    }
}
