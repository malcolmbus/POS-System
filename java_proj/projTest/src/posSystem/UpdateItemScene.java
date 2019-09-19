package posSystem;

import java.io.IOException;
import java.util.Optional;
import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import prog24178.utils.*;

/**
 * Class name: UpdateItemScene
 * Authors: Kendrick Tsz-Kin Yeung
 * Date: 9 Apr 2019
 *
 * Description:
 */
public class UpdateItemScene extends Stage {

    private Scene scene;
    private GridPane pane;
    private TextField txtID, txtName, txtPrice, txtAmt;
    private Button btnUpdate, btnPlus, btnMinus, btnDelete, btnReturn;
    POSSystem main = POSSystem.getInstance();

    /**
     * Default constructor
     * @param item the item that was selected
     */
    public UpdateItemScene(Item item) {
        super();
        getComponents();
        scene = new Scene(pane);
        txtID.setText(item.getItemId());
        txtName.setText(item.getItemName());
        txtPrice.setText(item.getItemPrice().toString());
        txtAmt.setText(item.getInventory().toString());
        this.setScene(scene);
        this.setTitle("Update an Item");
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
        txtID.setEditable(false);
        txtID.setMouseTransparent(true);
        txtName = new TextField();
        txtName.setPromptText("Name");
        txtName.setEditable(false);
        txtName.setMouseTransparent(true);
        txtPrice = new TextField();
        txtPrice.setPromptText("Price");
        txtAmt = new TextField("0");
        txtAmt.setEditable(false);
        txtAmt.setMouseTransparent(true);
        btnPlus = new Button("+");
        btnPlus.setOnAction(e -> eventCode(e));
        btnMinus = new Button("-");
        btnMinus.setOnAction(e -> eventCode(e));
        btnUpdate = new Button("_Update this Item");
        btnUpdate.setOnAction(e -> eventCode(e));
        btnDelete = new Button("_Delete");
        btnDelete.setOnAction(e -> eventCode(e));
        btnReturn = new Button("_Return");
        btnReturn.setOnAction(e -> eventCode(e));

        HBox pnlBtns = new HBox(10, btnUpdate, btnDelete, btnReturn);

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
        if (e.getSource() == btnUpdate) {
            Validator validator = new Validator();
            if (!validator.isValidDouble(txtPrice.getText())) {
                invalidInput("Price isn't valid");
            } else {
                UpdateItem();
            }
        }
        if (e.getSource() == btnDelete) {
            deleteItem();
            main.root.setCenter(new CheckOutPane());
            this.close();
        }
        if (e.getSource() == btnReturn) {
            main.root.setCenter(new CheckOutPane());
            this.close();
        }
    }

    /**
     * This method will update a specific item, then update the inventory
     */
    private void UpdateItem() {
        String ID = txtID.getText();
        double itemPrice = Double.parseDouble(txtPrice.getText());
        int inventory = Integer.parseInt(txtAmt.getText());
        main.itemList.searchID(ID).setItemPrice(itemPrice);
        main.itemList.searchID(ID).setInventory(inventory);
        updateInventory();
    }
    
    /**
     * This method will delete a specific item, then update the inventory
     */
    private void deleteItem(){
        String ID = txtID.getText();
        String display = "Are you sure to delete:\n" + ID + " " +
                txtName.getText().trim() + "?";
        Alert alert = new Alert(AlertType.CONFIRMATION, display
                ,ButtonType.YES, ButtonType.NO);
        alert.setTitle("Delete");
        alert.setHeaderText(null);
        Optional<ButtonType> delete = alert.showAndWait();
        if (delete.get() == ButtonType.YES) {
            main.itemList.remove(main.itemList.searchID(ID));
            updateInventory();
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
    
    /**
     * This method will update the items stored in the itemList, then refresh
     * the inventory
     */
    private void updateInventory(){
        try {
            main.itemList.update();   
            try {
                InventoryScene.updateInvenory(main);
            } catch (NullPointerException ex) {
            }
        } catch (IOException ex) {  
        }
    }
}
