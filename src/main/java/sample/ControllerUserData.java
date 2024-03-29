package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class ControllerUserData {
    public static final String DB_NAME = "data.sqlite";
    public static final String CONNECTION_STRING = "jdbc:sqlite:db/" + DB_NAME;
    public static final String USER = "USER";
    public static final String COLUMN_NOME = "NOME";
    public static final String COLUMN_COGNOME = "COGNOME";
    public static final String COLUMN_ALTEZZA = "ALTEZZA";
    public static final String COLUMN_PESO = "PESO";
    public static final String COLUMN_ALTEZZA_PRINT = "StampaAltezza";
    public static final String COLUMN_PESO_PRINT = "stampaPeso";
    public static final String COLUMN_USER = "User";
    public static final String COLUMN_PASSWORD = "Password";


    private static final Logger LOG = LoggerFactory.getLogger(ControllerUserData.class);
    private int textValueFocusIn;
    PreparedStatement query;


    @FXML private Button cancelButton;

    @FXML private Button okButton;


    @FXML private TextField nome;
    @FXML private TextField cognome;
    @FXML private TextField altezza;
    @FXML private TextField peso;
    @FXML private TextField userLogin;
    @FXML private TextField password;


    @FXML private CheckBox altezzaPrint;
    @FXML private CheckBox pesoPrint;

    public void initialize() {
        String sql = "SELECT * FROM " + USER;
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             ResultSet results = conn.createStatement().executeQuery(sql)) {


            if (results.next()) {
                nome.setText(results.getString(COLUMN_NOME));
                cognome.setText(results.getString(COLUMN_COGNOME));
                altezza.setText(results.getString(COLUMN_ALTEZZA));
                peso.setText(results.getString(COLUMN_PESO));
                userLogin.setText(results.getString(COLUMN_USER));
                password.setText(results.getString(COLUMN_PASSWORD));

                if(results.getInt(COLUMN_ALTEZZA_PRINT)!=1){
                    altezzaPrint.setSelected(false);
                }
                if(results.getInt( COLUMN_PESO_PRINT)!=1){
                    pesoPrint.setSelected(false);
                }

            }
        } catch (SQLException e) {
            LOG.info("" , e);
        }



        altezza.focusedProperty().addListener((obs, oldVal, inFocus) -> verifyAndCalcucaleTotalKcal(inFocus,altezza));
        peso.focusedProperty().addListener((obs, oldVal, inFocus) -> verifyAndCalcucaleTotalKcal(inFocus,peso));


        okButton.setOnAction(e -> {



            try (Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
                final String parameter = " = ?, ";
                query = conn.prepareStatement(
                        "UPDATE " + USER + " SET " + COLUMN_NOME + parameter + COLUMN_COGNOME + " = ? " +
                                ", " + COLUMN_ALTEZZA + parameter + COLUMN_PESO + parameter + COLUMN_ALTEZZA_PRINT + parameter + COLUMN_PESO_PRINT + parameter
                         + COLUMN_USER + parameter + COLUMN_PASSWORD + " = ? " );

                query.setString(1, nome.getText());
                query.setString(2, cognome.getText());
                query.setString(3, altezza.getText());
                query.setString(4, peso.getText());

                if(!altezzaPrint.isSelected()){

                query.setInt(5, 0);
                }else{
                    query.setInt(5, 1);

                }
                if(!pesoPrint.isSelected()){

                    query.setInt(6, 0);
                }else{
                    query.setInt(6, 1);

                }

                query.setString(7, userLogin.getText());
                query.setString(8, password.getText());

                query.executeUpdate();
            } catch (SQLException ex) {
                LOG.info("" , ex);
            }finally {
                closeThisWindow();
            }
        });

        cancelButton.setOnAction(e -> closeThisWindow());
    }

    private void closeThisWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void verifyAndCalcucaleTotalKcal(boolean inFocus,TextField textField) {
        if (!inFocus && !textField.getText().equals("") ) {

            try {
                int getVerifyValue = Integer.parseInt(textField.getText());
                if (getVerifyValue < 0) {

                    textField.setText(Integer.toString(textValueFocusIn));
                }
            } catch (NumberFormatException e) {
                textField.setText(Integer.toString(textValueFocusIn));

            }

        }else if(inFocus){
            textValueFocusIn = Integer.parseInt(textField.getText());
        }
    }


}
