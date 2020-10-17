package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class DialogController {


    @FXML
    private GridPane mealsGrid;

    public static final String DB_NAME = "data.sqlite";
    public static final String CONNECTION_STRING = "jdbc:sqlite:db/" + DB_NAME;
    public static final String MEALS = "MEALS";
    public static final String MEALS_CONFIG = "MEALS_CONFIG";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_ID_MEALS = "ID_MEALS";
    public static final String COLUMN_ID_FOOD = "ID_FOOD";
    public static final String COLUMN_GRAMS = "GRAMS";
    @FXML
    public void initialize() {

        Label label1 = new Label();
        label1.setText("esta");
        mealsGrid.add(label1,0,1);
    }

    @FXML
    protected void modifyMeals(ActionEvent event) {

        Dialog<ButtonType> dialog = new Dialog<>();

        //        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("modifyMeals.fxml"));

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

                savingData(DialogModifyMeals.hboxList);


        } else {
            System.out.println("Cancel pressed");


        }
        dialog = null;

    }

     private void savingData(ArrayList<HBox> hBoxArrayList) {

    try{
         Connection conn = DriverManager.getConnection(CONNECTION_STRING);

         Statement statement = conn.createStatement();

// insert the data
        String sql = "INSERT INTO MEALS (NAME_MEALS,HOUR) VALUES ('"+DialogModifyMeals.nameMealsValue+"','"+DialogModifyMeals.hourValue +"')";

         statement.execute(sql);

        Statement statementSearch = conn.createStatement();

        ResultSet results = statementSearch.executeQuery("SELECT * FROM "+ MEALS +" ORDER BY "+COLUMN_ID+" DESC LIMIT 1");

        int idField = results.getInt(COLUMN_ID);

        results.close();

        for(HBox hboxElement : hBoxArrayList ) {
             try {
                 TextField txtID = (TextField) hboxElement.getChildren().get(0);
                 System.out.println(txtID.getText());
                 TextField txtGrams = (TextField) hboxElement.getChildren().get(5);
                 String sqlInsert = "INSERT INTO "+MEALS_CONFIG+" ("+ COLUMN_ID_MEALS+","+ COLUMN_ID_FOOD+","+ COLUMN_GRAMS+") VALUES ("+idField+","+Integer.parseInt(txtID.getText())+","+Integer.parseInt(txtGrams.getText())+")";
                 statement.execute(sqlInsert);
             } catch (IndexOutOfBoundsException exception) {
                 System.out.println(exception);
             }
         }


        conn.close();
     }catch(SQLException e){
        System.out.