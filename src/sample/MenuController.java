package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.io.IOException;
import java.util.Optional;

public class MenuController {

    @FXML
    protected void handleMeals(ActionEvent event) {

        Dialog<ButtonType> dialog = new Dialog<>();
        //        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("Meals.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());

        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }


        //dialog.getDialogPane().getChildren().add(label1);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        //dialog.getDialogPane().getChildren().addAll(myMenu);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            System.out.println("OK pressed");
        } else {
            System.out.println("Cancel pressed");
        }





    }
}
