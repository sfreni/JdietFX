package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Controller {

    @FXML
    private TextField userId;
    @FXML
    private TextField passwordField;
    public static final String DB_NAME = "data.sqlite";
    public static final String CONNECTION_STRING = "jdbc:sqlite:db/" + DB_NAME;
    public static final String USER = "USER";
    public static final String COLUMN_USER = "User";
    public static final String COLUMN_PASSWORD = "Password";
    private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

    @FXML
    protected void handleSubmitButtonAction(ActionEvent event) {
        String user=userId.getText();
        String password=passwordField.getText();
        String sql = "SELECT USER,PASSWORD FROM " + USER;
        boolean startSoftware=false;
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             ResultSet results = conn.createStatement().executeQuery(sql)) {

            if (results.next() && results.getString(COLUMN_USER).equals(user) && results.getString(COLUMN_PASSWORD).equals(password)) {

                    startSoftware=true;



            }
        } catch (SQLException e) {
            LOG.info("" + e);
        }

        if (startSoftware) {
            try {
                Stage newStage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/MyMenu.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 500, 400);
                JMetro jMetro = new JMetro(Style.LIGHT);
                jMetro.setScene(scene);
                newStage.initModality(Modality.APPLICATION_MODAL);
                newStage.setResizable(false);

                newStage.setTitle("JDietFX - Sviluppato da Stefano Freni");
                newStage.setScene(scene);
                newStage.setOnCloseRequest(e-> {
                    Platform.exit();
                    System.exit(0);
                });
                newStage.showAndWait();
            } catch (IOException e) {
               LOG.error("Couldn't load the dialog");
                e.printStackTrace();
            }
        }else{

            Alert a = new Alert(Alert.AlertType.NONE);
            a.setAlertType(Alert.AlertType.ERROR);
            a.setContentText("Attenzione User o Password Errati");
            
            a.show();
        }
    }


}

