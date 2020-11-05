package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.IOException;

public class MenuController {

    @FXML
    protected void handleMeals(ActionEvent event) {
        try {
            Stage newStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Meals.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1110, 500);

            JMetro jMetro = new JMetro(Style.LIGHT);
            jMetro.setScene(scene);

            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.setScene(scene);
            newStage.setTitle("Gestisci Dieta");
            newStage.showAndWait();
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
        }
    }
        @FXML
        protected void handleFood(ActionEvent event) {
            try{
                Stage newStage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("HandleFood.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 1040, 630);
                JMetro jMetro = new JMetro(Style.LIGHT);
                jMetro.setScene(scene);
                newStage.initModality(Modality.APPLICATION_MODAL);
                newStage.setScene(scene);
                newStage.setTitle("Gestisci Alimenti");
                newStage.showAndWait();
            } catch (IOException e) {
                System.out.println("Couldn't load the dialog");
                e.printStackTrace();
            }



    }

    @FXML
    protected void handleQuit(ActionEvent event){

                Platform.exit();
                System.exit(0);

        }

    @FXML
    protected void printDiet(ActionEvent event){
        String pdfFilename = "Invoice_Ex.pdf";



}





}
