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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MenuController {

    private static final Logger LOG = LoggerFactory.getLogger(MenuController.class);


    @FXML
    protected void handleMeals(ActionEvent event) {
        try {
            Stage newStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Meals.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1260, 500);

            JMetro jMetro = new JMetro(Style.LIGHT);
            jMetro.setScene(scene);

            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.setScene(scene);
            newStage.setTitle("Gestisci Dieta");
            newStage.showAndWait();
        } catch (IOException e) {
            LOG.error("", e);
        }
    }

    @FXML
    protected void handleFood(ActionEvent event) {
        try {
            Stage newStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HandleFood.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1040, 630);
            JMetro jMetro = new JMetro(Style.LIGHT);
            jMetro.setScene(scene);
            newStage.initModality(Modality.APPLICATION_MODAL);

            newStage.setScene(scene);
            newStage.setTitle("Gestisci Alimenti");
            newStage.showAndWait();
        } catch (IOException e) {
            LOG.error("", e);
        }


    }

    @FXML
    protected void handleQuit(ActionEvent event) {

        Platform.exit();
        System.exit(0);

    }

    @FXML
    protected void printDiet(ActionEvent event) throws Exception {
        PrintMeals printMeals = new PrintMeals("elenco_pasti.pdf");

        printMeals.manipulatePdf();

    }
    @FXML
    protected void handleSettings(ActionEvent event) {

        try {
            Stage newStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserData.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 260, 400);
            JMetro jMetro = new JMetro(Style.LIGHT);
            jMetro.setScene(scene);
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.setScene(scene);
            newStage.setTitle("Dati Utente");
            newStage.showAndWait();
        } catch (IOException e) {
            LOG.error("", e);
        }

    }
    @FXML
    protected void handleWebsite(ActionEvent event) {
        try {
            Desktop.getDesktop().browse(new URI("http://www.freni.it/stefano"));
        } catch (IOException | URISyntaxException e1) {
            e1.printStackTrace();
        }
    }

    @FXML

    protected void handleEmail(ActionEvent event) {

        String subject="Informazioni%20su%20JDietFX";

        try {
            Desktop.getDesktop().mail( new URI( "mailto:stefano@freni.it?subject="+subject) );
        }  catch (IOException | URISyntaxException e1) {
            e1.printStackTrace();
        }
    }


    }






