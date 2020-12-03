package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Optional;

public class ControlHandleFoodDetail {
    public static final String DB_NAME = "data.sqlite";
    public static final String CONNECTION_STRING = "jdbc:sqlite:db/" + DB_NAME;
    public static final String ALIMENTI = "ALIMENTI";
    public static final String MEALS_CONFIG = "MEALS_CONFIG";
    public static final String COLUMN_ID_ALIM = "ID_ALIM";
    public static final String COLUMN_NAME_FOOD = "ALIMENTO";
    public static final String COLUMN_FIBER = "FIB";
    public static final String COLUMN_TOTKCAL = "KC";
    public static final String COLUMN_CARBOHIDRATE = "CAR";
    public static final String COLUMN_PROTEINS = "PRO";
    public static final String COLUMN_FAT = "GRA";
    public static final String COLUMN_ID_FOOD = "ID_FOOD";
    private static boolean isNewFood;
    private static int rowModifyRecord;
    private int textValueFocusIn;
    PreparedStatement query;
    private static final Logger LOG = LoggerFactory.getLogger(ControlHandleFoodDetail.class);

    @FXML    private AnchorPane anchorPane;

    @FXML    private TextField nameFood;

    @FXML    private TextField carboFood;

    @FXML    private TextField protFood;

    @FXML    private TextField fatFood;

    @FXML    private TextField fiberFood;

    @FXML    private TextField totKcal;

    @FXML    private Button okButton;

    @FXML    private Button cancelButton;


    @FXML    private Button deleteButton;



    @FXML     public void initialize() {
        nameFood.setPromptText("Inserire il nome dell' alimento");
        nameFood.focusedProperty().addListener((obs, oldVal, inFocus) -> {
            if (!inFocus && !nameFood.getText().equals("")) {
                String nameMealsText = nameFood.getText();
                nameMealsText = nameMealsText.substring(0, 1).toUpperCase() + nameMealsText.substring(1).toLowerCase();
                nameFood.setText(nameMealsText);
                okButton.setDisable(false);
            }
        });


        carboFood.setText("0");
        protFood.setText("0");
        fatFood.setText("0");
        fiberFood.setText("0");
        carboFood.focusedProperty().addListener((obs, oldVal, inFocus) -> verifyAndCalcucaleTotalKcal(inFocus, carboFood));
        protFood.focusedProperty().addListener((obs, oldVal, inFocus) -> verifyAndCalcucaleTotalKcal(inFocus, protFood));
        fatFood.focusedProperty().addListener((obs, oldVal, inFocus) -> verifyAndCalcucaleTotalKcal(inFocus, fatFood));
        fiberFood.focusedProperty().addListener((obs, oldVal, inFocus) -> verifyAndCalcucaleTotalKcal(inFocus, fiberFood));
        okButton.setOnAction(e -> {
            if (nameFood.getText().equals("") ||
                    carboFood.getText().equals("") ||
                    protFood.getText().equals("") ||
                    fatFood.getText().equals("") ||
                    fiberFood.getText().equals("")
            ) {
                dialogError("Tutti i campi devono essere compilati!");
            } else {
                savingData();
                Stage stage = (Stage) anchorPane.getScene().getWindow();
                stage.close();

            }
        });

        deleteButton.setOnAction(e -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Eliminazione Pasto");
                    alert.setHeaderText("Stai per eliminare l'alimento visualizzato");
                    alert.setContentText("Sei sicuro?");
                    alert.getDialogPane().setPrefSize(350, 200);
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        deleteData();
                        Stage stage = (Stage) anchorPane.getScene().getWindow();
                        stage.close();
                    }


                }
        );


        cancelButton.setOnAction(e -> {
            Stage stage = (Stage) anchorPane.getScene().getWindow();
            stage.close();
        });

        if (!isNewFood) {
            loadValues();
            deleteButton.setVisible(true);
        }
        totKcal.setDisable(true);
        totKcal.setStyle("-fx-text-fill: #000000;-fx-background-color: #FFFFFF; ");
    }



    private void verifyAndCalcucaleTotalKcal(boolean inFocus, TextField textField) {
        if (!inFocus && !textField.getText().equals("")) {

            try {
                int getVerifyValue = Integer.parseInt(textField.getText());
                if (getVerifyValue < 0) {
                    textField.setText(Integer.toString(textValueFocusIn));
                } else {
                    int totkcalTextField = Integer.parseInt(carboFood.getText()) * 4 + Integer.parseInt(protFood.getText()) * 4 + Integer.parseInt(fatFood.getText()) * 9;
                    totKcal.setText(Integer.toString(totkcalTextField));
                }
            } catch (NumberFormatException e) {
                textField.setText(Integer.toString(textValueFocusIn));
            }
        } else if (inFocus) {
            textValueFocusIn = Integer.parseInt(carboFood.getText());
        }
    }

    private void dialogError(String declaration) {
        Alert a = new Alert(Alert.AlertType.NONE);
        a.setAlertType(Alert.AlertType.ERROR);
        a.setContentText(declaration);
        a.show();
    }


    private void savingData() {
        if (isNewFood) {
            try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
                 Statement statement = conn.createStatement()) {
                int totkcal = Integer.parseInt(protFood.getText()) * 4 + Integer.parseInt(carboFood.getText()) * 4 + Integer.parseInt(fatFood.getText()) * 9;
                String sql = "INSERT INTO " + ALIMENTI + " (ALIMENTO,PRO,CAR,GRA,FIB,KC) VALUES ('" + nameFood.getText() + "',"
                        + protFood.getText() + " , " + carboFood.getText() + ", " + fatFood.getText() + ", " + fiberFood.getText() + "," + totkcal + ")";
                statement.execute(sql);
            } catch (SQLException e) {
                LOG.error("", e);
            }
        } else {
            try (Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
                int totkcal = Integer.parseInt(protFood.getText()) * 4 + Integer.parseInt(carboFood.getText()) * 4 + Integer.parseInt(fatFood.getText()) * 9;
                query = conn.prepareStatement(
                        "UPDATE " + ALIMENTI + " SET ALIMENTO = ?, PRO = ? , CAR = ?, GRA = ?, FIB = ?, KC = ? "
                                + " WHERE " + COLUMN_ID_ALIM + " = ?");
                query.setString(1, nameFood.getText());//automatically sanitizes and adds quotes
                query.setInt(2, Integer.parseInt(protFood.getText()));
                query.setInt(3, Integer.parseInt(carboFood.getText()));
                query.setInt(4, Integer.parseInt(fatFood.getText()));
                query.setInt(5, Integer.parseInt(fiberFood.getText()));
                query.setInt(6, totkcal);
                query.setInt(7, rowModifyRecord);
                query.executeUpdate();
            } catch (SQLException e) {
                LOG.error("e", e);
            }
        }
    }


    public static void deleteData() {
        boolean isNotFound=false;
        String sqlSelectFood = "SELECT * FROM " + MEALS_CONFIG + "  WHERE " + COLUMN_ID_FOOD + " = " + rowModifyRecord;
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             ResultSet results = conn.createStatement().executeQuery(sqlSelectFood)) {
            if (!results.next()) {
                isNotFound=true;
            } else {
                Alert a = new Alert(Alert.AlertType.NONE);
                a.setAlertType(Alert.AlertType.ERROR);
                a.setContentText("Attenzione non è possibile cancellare questo elemento" +
                        " in quanto è stato utilizzato per la composizione dei pasti.");
                a.show();
            }
        } catch (SQLException e) {
            LOG.error("",e);
        }

        if(isNotFound) {
            try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
                Statement statement = conn.createStatement()) {
                String sqlDeleteFood = "DELETE FROM " + ALIMENTI + "  WHERE " + COLUMN_ID_ALIM + " = " + rowModifyRecord;
                statement.execute(sqlDeleteFood);
            } catch (SQLException e) {
                LOG.error("", e);
            }
        }


    }

    private void loadValues() {
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             ResultSet results = conn.createStatement().executeQuery("SELECT * from " + ALIMENTI + " where " + COLUMN_ID_ALIM +
                                                                         " = " + rowModifyRecord)) {
            if (results.next()) {
                nameFood.setText(results.getString(COLUMN_NAME_FOOD));
                carboFood.setText(results.getString(COLUMN_CARBOHIDRATE));
                protFood.setText(results.getString(COLUMN_PROTEINS));
                fatFood.setText(results.getString(COLUMN_FAT));
                fiberFood.setText(results.getString(COLUMN_FIBER));
                totKcal.setText(results.getString(COLUMN_TOTKCAL));
            }
        } catch (SQLException e) {
            LOG.error("Something goes wrong: ", e);
            e.printStackTrace();
        }

        okButton.setDisable(false);
    }
    protected static void setIsNewFood(boolean isNewFood) {
        ControlHandleFoodDetail.isNewFood = isNewFood;
    }
    public static void setRowModifyRecord(int rowModifyRecord) {
        ControlHandleFoodDetail.rowModifyRecord = rowModifyRecord;
    }

}