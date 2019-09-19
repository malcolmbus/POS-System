package posSystem;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Optional;
import javafx.application.Application;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Authors: Kendrick Tsz-Kin Yeung, Malcolm Busari, Manpreet Kaur Program: POS
 * system Date: 9 Apr 2019
 *
 * Description:
 */
public class POSSystem extends Application implements EventHandler<ActionEvent> {

    private Scene scene;
    static BorderPane root;
    private MenuBar menuBar;
    private Menu optionMenu, helpMenu;
    private MenuItem logoutItem, addToItem, exitItem, aboutItem;
    public ItemList itemList = new ItemList();
    public InventoryScene inventory;
    public AddItemScene addItem;
    public ReadMeScene readMe;
    private static POSSystem instance = null;
    
    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();
        root.setCenter(new LoginPane());

        scene = new Scene(root, 600, 500);

        primaryStage.setTitle("POS system");
        String css = this.getClass().getResource("/css/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * The main method that will launch the GUI
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void handle(ActionEvent e) {
        if (e.getSource() != exitItem) {
            if (e.getSource() == logoutItem) {
                logOut();
            }
            if (e.getSource() == addToItem) {
                String passcode = promptInput("Modify inventory", "Enter passcode to modify the inventory.");
                if (checkPrivilege(passcode)) {
                    addItem = new AddItemScene();
                    addItem.showAndWait();
                } else if (passcode != null){
                    CheckOutPane.showWarning("Access denied");
                }
            }
            if (e.getSource() == aboutItem) {
                readMe = new ReadMeScene();
                readMe.show();
            }
            updateMenuItems();
        } else {
            exitProgram();
        }
    }

    /**
     * This method will check the type of object inside the center of the root
     * and decide whether to show the menu bar or not. The menu bar will only be
     * displayed If the center of the root is a CheckOutPane object.
     */
    public void updateMenuItems() {
        if (root.getCenter() instanceof LoginPane) {
            root.setTop(null);
        } else if (root.getCenter() instanceof CheckOutPane) {
            getMenuBar();
            root.setTop(menuBar);
        }
    }

    /**
     * This method will create the menuBar for this pane
     */
    private void getMenuBar() {
        menuBar = new MenuBar();
        optionMenu = new Menu("_Option");
        logoutItem = new MenuItem("_Log out");
        logoutItem.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCodeCombination.CONTROL_DOWN));
        addToItem = new MenuItem("_Add new item to inventory");
        addToItem.setAccelerator(new KeyCodeCombination(KeyCode.I, KeyCodeCombination.CONTROL_DOWN));
        exitItem = new MenuItem("_Exit");
        exitItem.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCodeCombination.CONTROL_DOWN));
        optionMenu.getItems().addAll(logoutItem, addToItem, new SeparatorMenuItem(), exitItem);
        helpMenu = new Menu("_Help");
        aboutItem = new MenuItem("_About This system");
        aboutItem.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCodeCombination.CONTROL_DOWN));
        helpMenu.getItems().addAll(aboutItem);
        menuBar.getMenus().addAll(optionMenu, helpMenu);
        logoutItem.setOnAction(this);
        addToItem.setOnAction(this);
        exitItem.setOnAction(this);
        aboutItem.setOnAction(this);
    }

    /**
     * When the logout menuItem was triggered, this method will be called. This
     * method will show a confirmation alert that ask the user if he/she
     * confirms to logout, if the YES button in the Alert dialog was pressed,
     * the user will be logged out.
     */
    private void logOut() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Confirm to logout?",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("Log Out");
        alert.setHeaderText(null);
        Optional<ButtonType> selection = alert.showAndWait();
        if (selection.get() == ButtonType.YES) {
            root.setCenter(new LoginPane());
        }
    }
    
    /**
     * This method will prompt the user to confirm is the user really wants to\
     * exit
     */
    private void exitProgram(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Confirm to exit?",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("Exit?");
        alert.setHeaderText(null);
        Optional<ButtonType> selection = alert.showAndWait();
        if (selection.isPresent() && selection.get() == ButtonType.YES) {
            System.exit(0);
        }
    }

    /**
     * This method will prompt the user for input and return the entered value
     * @param title Title of the prompt window
     * @param contentText Content text of the prompt window
     * @return then
     */
    public String promptInput(String title, String contentText) {
        String input = null;
        TextInputDialog prompt = new TextInputDialog();
        prompt.setTitle(title);
        prompt.setHeaderText(null);
        prompt.setContentText(contentText);
        Optional<String> inputBox = prompt.showAndWait();
        if (inputBox.isPresent()) {
            input = inputBox.get();
        }
        return input;
    }
    
    /**
     * The method will check if the privilege passcode is correct or not
     * @param passcode the passcode
     * @return if the passcode matches record
     */
    public boolean checkPrivilege(String passcode){
        boolean manager = false;
        if (passcode != null && passcode.equals("f4E84zQ6I")) {
            manager = true;
        }
        return manager;
    }

    /**
     * Since the itemList declared in POSSystem will be used by multiple
     * classes, we have to ensure that every class is referencing the same
     * object so the itemList will be the same for every classes
     *
     * @return the reference of the mainPane object
     */
    public static POSSystem getInstance() {
        if (instance == null) {
            instance = new POSSystem();
        }
        return instance;
    }
}
