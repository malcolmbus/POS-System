package posSystem;

import java.io.IOException;
import java.util.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import prog24178.utils.*;

/**
 * Class name: AddItemScene
 * Authors: Kendrick Tsz-Kin Yeung, Malcolm Busari, Manpreet Kaur
 * Date: 9 Apr 2019
 *
 * Description:
 */
public class AddItemScene extends Stage {

    private Scene scene;
    private GridPane pane;
    private TextField txtID, txtName, txtPrice, txtAmt;
    private Button btnAdd, btnPlus, btnMinus, btnReset, btnReturn;
    POSSystem main = POSSystem.getInstance();

    /**
     * Default constructor
     */
    public AddItemScene() {
        super();
        getComponents();
        scene = new Scene(pane);
        this.setScene(scene);
        this.setTitle("Add new Item");
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UNDECORATED);
    }

    /**
     * This method will create all the necessary components for the pane
     */
    private void getComponents() {
        pane = new GridPane();
        pane.setPadding(new Insets(20));
        txtID = new TextField();
        txtID.setPromptText("ID");
        txtID.setFocusTraversable(false);
        txtName = new TextField();
        txtName.setPromptText("Name");
        txtName.setFocusTraversable(false);
        txtPrice = new TextField();
        txtPrice.setPromptText("Price");
        txtPrice.setFocusTraversable(false);
        txtAmt = new TextField("0");
        txtAmt.setEditable(false);
        txtAmt.setMouseTransparent(true);
        txtAmt.setFocusTraversable(false);
        btnPlus = new Button("+");
        btnPlus.setOnAction(e -> eventCode(e));
        btnPlus.setFocusTraversable(false);
        btnMinus = new Button("-");
        btnMinus.setOnAction(e -> eventCode(e));
        btnMinus.setFocusTraversable(false);
        btnAdd = new Button("Add to Inventory");
        btnAdd.setOnAction(e -> eventCode(e));
        btnAdd.setFocusTraversable(false);
        btnReset = new Button("Reset");
        btnReset.setOnAction(e -> eventCode(e));
        btnReset.setFocusTraversable(false);
        btnReturn = new Button("Return");
        btnReturn.setOnAction(e -> eventCode(e));
        btnReturn.setFocusTraversable(false);

        HBox pnlBtns = new HBox(10, btnAdd, btnReset, btnReturn);

        pane.add(txtID, 0, 0, 3, 1);
        pane.add(txtName, 0, 1, 3, 1);
        pane.add(txtPrice, 0, 2, 3, 1);
        pane.add(txtAmt, 0, 3);
        pane.add(btnPlus, 1, 3);
        pane.add(btnMinus, 2, 3);
        pane.add(pnlBtns, 0, 4, 3, 1);
        pane.setAlignment(Pos.CENTER);
    }

    private void eventCode(Event e) {
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
        if (e.getSource() == btnAdd) {
            Validator validator = new Validator();
            if (!txtID.getText().matches("[0-9]{1,5}")) {
                invalidInput("ID isn't valid");
            } else if (!validator.isValidDouble(txtPrice.getText())) {
                invalidInput("Price isn't valid");
            } else {
                addToItemList();
            }
        }
        if (e.getSource() == btnReset) {
            txtID.clear();
            txtName.clear();
            txtPrice.clear();
            txtAmt.setText("0");
        }
        if (e.getSource() == btnReturn) {
            main.root.setCenter(new CheckOutPane());
            this.close();
        }
    }

    /**
     * This method will first check the validity of the entered data, and will 
     * add to the itemList if the item doesn't exist
     */
    private void addToItemList() {
        try {
            main.itemList.readFile();
            Item item = null;
            String[] names = main.itemList.getItemNames();
            String[] IDs = main.itemList.getItemIDs();
            String itemId = txtID.getText();
            if (itemId.length() < 5) {
                String zero = "";
                for (int i = 0; i < (5 - itemId.length()); i++) {
                    zero += "0";
                }
                itemId = zero + itemId;
            }
            double itemPrice = Double.parseDouble(txtPrice.getText());
            String itemName = txtName.getText();
            int inventory = Integer.parseInt(txtAmt.getText());
            for (int i = 0; i < names.length; i++) {
                if (IDs[i].equals(itemId)) {
                    item = main.itemList.get(i);
                    break;
                }
            }
            if (item != null) {
                    item = foundExistItem(item);
                if (item != null) {
                    UpdateItemScene update = new UpdateItemScene(item);
                    update.showAndWait();
                }
            } else {
                main.itemList.add(new Item(itemId, itemPrice, itemName, inventory));
            }
            try {
                main.itemList.update();
            } catch (IOException ex) {
            }
        } catch (IOException ex) {
        }
    }

    /**
     * The method will warn the user that the new item matches either the name or
     * ID already, and will ask if user wants to modify that item
     * @param i index of the existing item
     */
    private Item foundExistItem(Item item) {
        Item foundItem = null;
        Alert alert = new Alert(AlertType.WARNING, "Item Exist Already\n"
                + "Do you want to modify this item?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Alert");
        alert.setHeaderText(null);
        Optional<ButtonType> contSelect = alert.showAndWait();
        if (contSelect.get() == ButtonType.YES) {
            foundItem = item;
        }
        return foundItem;
    }

    /**
     * This method allows the user to modify an item's name, price and inventory
     * @param i index of the item that will be modify
     */
    private void modifyItem(int i) {
        Validator validator = new Validator();
        TextInputDialog input = new TextInputDialog();
        Optional<String> name, price, qty;
        input.setTitle("Input Dialog");
        input.setHeaderText(null);
        input.setContentText("Enter item ID: "
                + main.itemList.get(i).getItemId() + "'s new Name");
        name = input.showAndWait();
        if (name.isPresent()) {
            input.setContentText("Enter "
                    + main.itemList.get(i).getItemName().trim() + "'s new Price");
            price = input.showAndWait();
            while (price.isPresent() && !validator.isValidDouble(price.get())) {
                invalidInput("Price Isn't a valid double");
                price = input.showAndWait();
            }
            if (price.isPresent()) {
                input.setContentText("Enter "
                        + main.itemList.get(i).getItemName().trim() + "'s new Qty");
                qty = input.showAndWait();
                while (qty.isPresent() && !validator.isValidInteger(qty.get())) {
                    invalidInput("Qty Isn't a valid integer");
                    qty = input.showAndWait();
                }
                if (qty.isPresent()) {
                    main.itemList.get(i).setItemName(name.get());
                    main.itemList.get(i).setItemPrice(Double.parseDouble(price.get()));
                    main.itemList.get(i).setInventory(Integer.parseInt(qty.get()));
                }
            }
        }

    }

    /**
     * This method will display an error alert and abort the action
     * @param contentText error message
     */
    private void invalidInput(String contentText) {
        Alert alert = new Alert(AlertType.ERROR, contentText);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
