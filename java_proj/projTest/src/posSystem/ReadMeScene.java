/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package posSystem;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Scanner;
import javafx.event.Event;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Class name: ReadMeScene Author: Kendrick, Tsz-Kin Yeung
 * Date: 9 Apr 2019
 *
 * Description: This class will generate a Scene that display all the
 * information inside the README.txt whenever this class is instantiated
 */
public class ReadMeScene extends Stage {

    private Scene scene;
    private BorderPane pane;
    private TextArea txaReceipt;
    private Button btnReturn;

    /**
     * Default constructor
     */
    public ReadMeScene() {
        super();
        getComponents();
        scene = new Scene(pane);
        this.setScene(scene);
        this.setTitle("Readme");
        initModality(Modality.NONE);
    }

    /**
     * This method will get all the necessary components for this scene
     */
    private void getComponents() {
        pane = new BorderPane();
        pane.setPadding(new Insets(0, 10, 5, 10));
        pane.setPrefSize(500, 500);
        txaReceipt = new TextArea();
        txaReceipt.setEditable(false);
        txaReceipt.setStyle("-fx-font: 12px Monospace;");

        btnReturn = new Button("_Return");
        btnReturn.setMnemonicParsing(true);
        btnReturn.setOnAction(e -> eventCode(e));

        HBox pnlBtn = new HBox(10, btnReturn);
        pnlBtn.setAlignment(Pos.CENTER);
        try {
            writeReadMe();
        } catch (IOException ex) {
            ex.getMessage();
            txaReceipt.setText("README.TXT doesnt exist");
        }
        pane.setCenter(txaReceipt);
        pane.setBottom(pnlBtn);
    }

    private void eventCode(Event e) {
        if (e.getSource() == btnReturn) {
            this.close();
        }
    }

    /**
     * This method will read from the README.txt and write everything inside the
     * README.txt to the TextArea of the scene
     *
     * @throws FileNotFoundException If the README.txt was not found
     */
    private void writeReadMe() throws IOException {
        File file;
        try {
            file = new File(ItemList.getJarPath() + ".\\README.txt");
            Scanner sc = new Scanner(file);
            String output = "";
            while (sc.hasNext()) {
                output += sc.nextLine() + "\r\n";
            }
            sc.close();
            txaReceipt.setText(output);
        } catch (URISyntaxException ex) {
        }
    }
}
