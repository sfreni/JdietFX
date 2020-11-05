package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.IOException;

public class Controller {

    @FXML
    private Text actiontarget;

    @FXML
    private TextField userId;

    @FXML
    private GridPane opend;

    @FXML
    private BorderPane mainBorderPane;


    @FXML
    protected void handleSubmitButtonAction(ActionEvent event) {

        if (!userId.getText().equals("admin")) {
            actiontarget.setText("User/Password is Wrong!");
        } else {
            try{
                Stage newStage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("MyMenu.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 930, 500);
                JMetro jMetro = new JMetro(Style.LIGHT);
                jMetro.setScene(scene);
                newStage.setScene(scene);
                newStage.showAndWait();
            } catch (IOException e) {
                System.out.println("Couldn't load the dialog");
                e.printStackTrace();
            }


        }


    }


}

