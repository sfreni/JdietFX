package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

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

            Dialog<ButtonType> dialog = new Dialog<>();
            //        dialog.initOwner(mainBorderPane.getScene().getWindow());
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("MyMenu.fxml"));
            try {
                dialog.getDialogPane().setContent(fxmlLoader.load());

            } catch (IOException e) {
                System.out.println("Couldn't load the dialog");
                e.printStackTrace();
                return;
            }

            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
            dialog.showAndWait();
        }


    }


}

