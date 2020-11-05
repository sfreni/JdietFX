package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.DateTimeStringConverter;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Optional;

public class DialogModifyMeals {
    public static final String DB_NAME = "data.sqlite";
    public static final String CONNECTION_STRING = "jdbc:sqlite:db/" + DB_NAME;
    public static final String MEALS = "MEALS";
    public static final String MEALS_CONFIG = "MEALS_CONFIG";
    public static final String TB_ALIM = "tb_alim";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_ID_MEALS = "ID_MEALS";
    public static final String COLUMN_NAME_MEALS = "NAME_MEALS";
    public static final String COLUMN_HOUR_MEALS = "HOUR";
    public static final String COLUMN_ID_FOOD = "ID_FOOD";
    public static final String COLUMN_ID_ALIM = "ID_ALIM";
    public static final String COLUMN_GRAMS = "GRAMS";
    public static final String COLUMN_ALIMENTO = "ALIMENTO";
    public static final String COLUMN_TOTKCAL = "KC";
    public static final String COLUMN_CARBOHIDRATE = "CAR";
    public static final String COLUMN_PROTEINS = "PRO";
    public static final String COLUMN_FAT = "GRA";
    static ArrayList<HBox> hboxList = new ArrayList<>();
    static int countButton; //Conta il numero delle righe ogni volta che viene cliccato (+)
    static int isNewMeal=0; //Conta il numero delle righe ogni volta che viene cliccato (+)
    private int pressButton;
    private int textValueFocusIn;



    @FXML
    protected BorderPane borderPane;

    @FXML
    protected TextField hour;
    protected static String hourValue;

    @FXML
    private VBox vboxMeals;

    @FXML
    protected TextField nameMeals;
    protected static String nameMealsValue;

    @FXML
    protected TextField totKcalOverall;

    @FXML
    protected Button okButton;

    @FXML
    protected Button cancelButton;


    @FXML
    public Button deleteButton;


    @FXML
    public void initialize() {
        hboxList=new ArrayList<>();
        countButton=0;
        if(isNewMeal==0){
        addTextFields();}       else
            {

                modifyTextFields();
                deleteButton.setVisible(true);
        }
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        try {
            hour.setTextFormatter(new TextFormatter<>(new DateTimeStringConverter(format), format.parse("12:00")));
            hourValue = hour.getText();

        } catch (ParseException parsexception) {
            System.out.println(parsexception);

        }
        nameMeals.setPromptText("Inserire il nome del pasto");

        nameMeals.focusedProperty().addListener((obs, oldVal, inFocus) -> {
            if (!inFocus) {
                nameMealsValue = nameMeals.getText();
                nameMeals.setText(nameMealsValue.toUpperCase());
                nameMealsValue = nameMeals.getText();

            }

        });

        hour.focusedProperty().addListener((obs, oldVal, inFocus) -> {
            if (!inFocus) {

                hourValue = hour.getText();

            }

        });

        totKcalOverall.setText("0");


        okButton.setOnAction(e -> {

            if(hourValue.equals("") || nameMealsValue.equals("")){
                dialogError("L'ORARIO ED IL NOME DEL PASTO DEVE ESSERE COMPILATO!");
            }else {
                if(!verifyNameMeal()) {
                    if (isNewMeal != 0) {
                        deleteData(hboxList);
                    }
                    savingData(hboxList);
                    hboxList = new ArrayList<>();
                    countButton = 0;
                    Stage stage = (Stage) borderPane.getScene().getWindow();
                    stage.close();
                    stage = null;
                }else{
                    dialogError("NOME PASTO GIA' ESISTENTE, CAMBIARE IL NOME!");
                }
            }
        });


        deleteButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Eliminazione Pasto");
            alert.setHeaderText("Stai per eliminare il pasto visualizzato");
            alert.setContentText("Sei sicuro?");
            alert.getDialogPane().setPrefSize(350, 200);
            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK) {


                    deleteData(hboxList);
                    hboxList = new ArrayList<>();
                    countButton = 0;

                    Stage stage = (Stage) borderPane.getScene().getWindow();
                    stage.close();
                    stage = null;
            }
            }
        );



        cancelButton.setOnAction(e -> {
            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.close();
            stage=null;

        });




        if(isNewMeal!=0){

            calculateTotKcal(hboxList);
            loadHeaders();
        }

    }

    private void dialogError(String declaration) {
        Alert a = new Alert(Alert.AlertType.NONE);
        a.setAlertType(Alert.AlertType.ERROR);
        a.setContentText(declaration);

        // show the dialog
        a.show();
    }


    @FXML
    protected void searchMeals(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("tableViews.fxml"));
        try {

            Stage newStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("tableViews.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 930, 630);
            JMetro jMetro = new JMetro(Style.LIGHT);
            jMetro.setScene(scene);
            newStage.setTitle("Selezionare l'alimento desiderato");
            newStage.setScene(scene);

            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.showAndWait();
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog" + e );
            return;
        }

        if (mealsTablesController.foodchoose != null ) {
            setFoodValues(mealsTablesController.foodchoose, hboxList.get(pressButton));
            calculateTotKcal(hboxList);
            okButton.setDisable(false);
        }


    }

    @FXML
    protected void addTextFields() {

        int buttonPressed = countButton;

        if (mealsTablesController.foodchoose != null || countButton == 0) {

            HBox hbox1 = new HBox();
            hbox1.setSpacing(10);

            vboxMeals.setPadding(new Insets(15, 12, 15, 12));
            vboxMeals.getChildren().add(hbox1);


            TextField textId = new TextField();
            textId.setVisible(false);
            textId.setMaxWidth(1);
            Label label1 = new Label();
            label1.setText("Alimento:");
            TextField textField1 = new TextField();
            //disable="true" style="-fx-text-fill: #000000;-fx-background-color: #FFFFFF;  "

            textField1.setDisable(true);
            textField1.setStyle("-fx-text-fill: #000000;-fx-background-color: #FFFFFF; ");
            Button search1 = new Button();
            search1.setText("Cerca");

            search1.setOnAction(e -> {
                pressButton = buttonPressed;
                searchMeals(e);

            });

            Label label2 = new Label();
            label2.setText("Grammi:");
            TextField textField2 = new TextField();

            textField2.setMaxWidth(80);
            textField2.setStyle("-fx-text-fill: blue;");
            textField2.setText("0");
            Label label3 = new Label();
            label3.setText("Kcal Totali:");
            TextField textField3 = new TextField();
            textField3.setMaxWidth(80);
            textField3.setDisable(true);
            textField3.setStyle("-fx-text-fill: #000000;-fx-background-color: #FFFFFF; ");

            textField3.setText("0");
            Label label4 = new Label();
            label4.setText("Gr. Carboidrati:");
            TextField textField4 = new TextField();
            textField4.setMaxWidth(80);
            textField4.setDisable(true);
            textField4.setStyle("-fx-text-fill: #000000;-fx-background-color: #FFFFFF; ");

            Label label5 = new Label();
            label5.setText("Gr. Proteine:");
            TextField textField5 = new TextField();
            textField5.setMaxWidth(80);
            textField5.setDisable(true);
            textField5.setStyle("-fx-text-fill: #000000;-fx-background-color: #FFFFFF; ");

            Label label6 = new Label();
            label6.setText("Gr. Grassi:");
            TextField textField6 = new TextField();
            textField6.setMaxWidth(80);
            textField6.setDisable(true);
            textField6.setStyle("-fx-text-fill: #000000;-fx-background-color: #FFFFFF; ");

            textField2.focusedProperty().addListener((obs, oldVal, inFocus) -> {

                if (inFocus) {
                    textValueFocusIn = Integer.parseInt(textField2.getText());

                } else {

                    updateKcalories(textField2, textField3, textField4, textField5, textField6,textValueFocusIn);
                    calculateTotKcal(hboxList);

                }
            });

            Button addButton = new Button();
            addButton.setText("+");
            addButton.setOnAction(e -> {
                        addTextFields();
//
                if(countButton>=10){
                    borderPane.getScene().getWindow().setHeight(borderPane.getScene().getWindow().getHeight()+50);
                }
                //                borderPane.getScene().getWindow().sizeToScene();
                                       }
            );
            hbox1.getChildren().add(textId);
            hbox1.getChildren().add(label1);
            hbox1.getChildren().add(textField1);
            hbox1.getChildren().add(search1);
            hbox1.getChildren().add(label2);
            hbox1.getChildren().add(textField2);
            hbox1.getChildren().add(label3);
            hbox1.getChildren().add(textField3);
            hbox1.getChildren().add(label4);
            hbox1.getChildren().add(textField4);
            hbox1.getChildren().add(label5);
            hbox1.getChildren().add(textField5);
            hbox1.getChildren().add(label6);
            hbox1.getChildren().add(textField6);
            hbox1.getChildren().add(addButton);
            if (countButton != 0) {

                Button minusButton = new Button();
                minusButton.setText("-");
                minusButton.setOnAction(e -> {
                    pressButton = buttonPressed;
                    hbox1.getChildren().clear();

                    mealsTablesController.foodchoose = new mealsTablesController.Food("0", "0", "0",
                            "0", "0", "0"); //creo un oggetto vuoto per far funzionare il pulsante "+" che fa un check sull'esistenza dell'oggetto.

                        calculateTotKcal(hboxList);
                });

                hbox1.getChildren().add(minusButton);

            }

            hboxList.add(hbox1);

            mealsTablesController.foodchoose = null;


            countButton++;

        }

    }

    private void updateKcalories(TextField textField2, TextField textField3, TextField textField4, TextField textField5, TextField textField6,int textValueFocusIn) {

 //       System.out.println(textField2.getText()+" "+textField3.getText()+" "+textField4.getText()+" "+textField5.getText()+" "+textField6.getText());
        try {
            int getVerifyValue = Integer.parseInt(textField2.getText());
            if (getVerifyValue <= 0) {

                textField2.setText(Integer.toString(textValueFocusIn));
            } else {

                textField4.setText(setTextValues(textField4, Integer.parseInt(textField2.getText()), textValueFocusIn));
                textField5.setText(setTextValues(textField5, Integer.parseInt(textField2.getText()), textValueFocusIn));
                textField6.setText(setTextValues(textField6, Integer.parseInt(textField2.getText()), textValueFocusIn));

                int totKcalValue = (int) (Double.parseDouble(textField4.getText()) * 4 + Double.parseDouble(textField5.getText()) * 4 + Double.parseDouble(textField6.getText()) * 9);

                textField3.setText(Integer.toString(totKcalValue));


            }
        } catch (NumberFormatException e) {
            textField2.setText(Integer.toString(textValueFocusIn));

        }
    }

    @FXML
    protected void setFoodValues(mealsTablesController.Food foodNameBeUsed, HBox hbox) {

        TextField foodIdValues = (TextField) hbox.getChildren().get(0);
        foodIdValues.setText(Integer.toString(foodNameBeUsed.getId()));
        TextField foodNameTextField = (TextField) hbox.getChildren().get(2);
        foodNameTextField.setText(foodNameBeUsed.getUnitFood());
        TextField foodGramsTextField = (TextField) hbox.getChildren().get(5);
        foodGramsTextField.setText(Integer.toString(100));
        TextField totalKcalTextField = (TextField) hbox.getChildren().get(7);

        totalKcalTextField.setText(Double.toString(foodNameBeUsed.getTotKcal()));

        TextField grCarboTextField = (TextField) hbox.getChildren().get(9);
        grCarboTextField.setText(Integer.toString(foodNameBeUsed.getGrCarboidrati()));
        TextField grProsTextField = (TextField) hbox.getChildren().get(11);
        grProsTextField.setText(Integer.toString(foodNameBeUsed.getGrProtein()));
        TextField grGrasTextField = (TextField) hbox.getChildren().get(13);
        grGrasTextField.setText(Integer.toString(foodNameBeUsed.getGrGrassi()));


    }

    protected String setTextValues(TextField textField, int grams, int originValue) {
        double convertDouble = Double.parseDouble(textField.getText()) / originValue * 100;

        convertDouble= Math.round(convertDouble*100)/100;

        return Double.toString(convertDouble* grams /100); // Arrotonda il numero convertito in relazione ai grammi dell'alimento e poi ritorna String per essere inserito nel TextField/
    }

    protected void calculateTotKcal(ArrayList<HBox> hbList){
        double sumKcal=0;
        try {
            for (HBox hbox : hbList) {
                TextField calcTotKcal = (TextField) hbox.getChildren().get(7);
                sumKcal += Double.parseDouble(calcTotKcal.getText());
                
            }
        }catch (IndexOutOfBoundsException e){

        }
        int convertedValue= (int) sumKcal; // ho convertito il valore in questo modo per troncare il decimale senza fare arrotondamento.
        totKcalOverall.setText(Integer.toString(convertedValue));

    }

    private void savingData(ArrayList<HBox> hBoxArrayList) {

        try(Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {

            Statement statement = conn.createStatement();
            String sql = "INSERT INTO MEALS (NAME_MEALS,HOUR) VALUES ('" + DialogModifyMeals.nameMealsValue + "','" + DialogModifyMeals.hourValue + "')";
            statement.execute(sql);
            Statement statementSearch = conn.createStatement();
            ResultSet results = statementSearch.executeQuery("SELECT * FROM " + MEALS + " ORDER BY " + COLUMN_ID + " DESC");
            int idField = results.getInt(COLUMN_ID);
            results.close();
            for (HBox hboxElement : hBoxArrayList) {
                try {

                    TextField txtID = (TextField) hboxElement.getChildren().get(0);
                    TextField txtGrams = (TextField) hboxElement.getChildren().get(5);
                    if(Double.parseDouble(txtGrams.getText())!=0.00){
                    String sqlInsert = "INSERT INTO " + MEALS_CONFIG + " (" + COLUMN_ID_MEALS + "," + COLUMN_ID_FOOD + "," + COLUMN_GRAMS + ") VALUES (" + idField + "," + Integer.parseInt(txtID.getText()) + "," + Integer.parseInt(txtGrams.getText()) + ")";
                    statement.execute(sqlInsert);
                    }

                } catch (IndexOutOfBoundsException exception) {
                    System.out.println(exception);
                }
            }

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void modifyTextFields() {
        int countTextField=0;
        try(Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            Statement statementSearchMeal = conn.createStatement();
            String sqlMeal= "SELECT * FROM " + MEALS_CONFIG + " WHERE "+ COLUMN_ID_MEALS +" = "+ DialogController.mealChoose.getIdMeal() +"  ORDER BY " + COLUMN_ID + " ASC" ;
            ResultSet rsMeal= statementSearchMeal.executeQuery(sqlMeal);

            while (rsMeal.next())
            {

                try(Connection connFood = DriverManager.getConnection(CONNECTION_STRING)) {
                    addTextFields();
                    String sqlFood = "SELECT * FROM " + TB_ALIM + " WHERE " + COLUMN_ID_ALIM + " = " + rsMeal.getInt(COLUMN_ID_FOOD);
                    Statement statementSearchFood = connFood.createStatement();
                    ResultSet rsFood = statementSearchFood.executeQuery(sqlFood);
                    //if(rsFood.next()) System.out.println(rsFood.getString(COLUMN_ID));
                    mealsTablesController.foodchoose = new mealsTablesController.Food(rsFood.getString(COLUMN_ID_ALIM), rsFood.getString(COLUMN_ALIMENTO),
                    rsFood.getString(COLUMN_TOTKCAL), rsFood.getString(COLUMN_CARBOHIDRATE),
                    rsFood.getString(COLUMN_PROTEINS), rsFood.getString(COLUMN_FAT));
                    setFoodValues(mealsTablesController.foodchoose, hboxList.get(countTextField));

                    HBox hboxGramsTextField = hboxList.get(countTextField);
                    //procedo ad aggiornare il campo Grammi con quelli memorizzati
                    TextField gramsTextField = (TextField) hboxGramsTextField.getChildren().get(5);
                    TextField textField3 = (TextField) hboxGramsTextField.getChildren().get(7);
                    TextField textField4 = (TextField) hboxGramsTextField.getChildren().get(9);
                    TextField textField5 = (TextField) hboxGramsTextField.getChildren().get(11);
                    TextField textField6 = (TextField) hboxGramsTextField.getChildren().get(13);
                    gramsTextField.setText(rsMeal.getString(COLUMN_GRAMS));
                    updateKcalories(gramsTextField, textField3, textField4, textField5, textField6,100);
                      countTextField++;
                    statementSearchFood.close();
                    rsFood.close();
                }catch(SQLException e){
                    System.out.println(e);
                }
                okButton.setDisable(false);
                            }

            rsMeal.close();

        } catch (SQLException e) {
            System.out.println(e);
        }

    }


    private void loadHeaders() {
        try(Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            Statement statementSearchMeal = conn.createStatement();
            String sqlMeal= "SELECT * FROM " + MEALS + " WHERE "+ COLUMN_ID +" = "+ DialogController.mealChoose.getIdMeal();
            ResultSet rsMeal= statementSearchMeal.executeQuery(sqlMeal);
            nameMeals.setText(rsMeal.getString(COLUMN_NAME_MEALS));
            nameMealsValue=rsMeal.getString(COLUMN_NAME_MEALS);
            hour.setText(rsMeal.getString(COLUMN_HOUR_MEALS));
            hourValue =rsMeal.getString(COLUMN_HOUR_MEALS);


            rsMeal.close();
        }catch(SQLException e){
                    System.out.println(e);
               }
    }




    private void deleteData(ArrayList<HBox> hBoxArrayList) {

        try(Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            Statement statement = conn.createStatement();
            String sqlDeleteMealConfig = "DELETE FROM " + MEALS_CONFIG +"  WHERE "+ COLUMN_ID_MEALS +" = " + DialogController.mealChoose.getIdMeal();
            statement.execute(sqlDeleteMealConfig);
            statement.close();
            statement = conn.createStatement();
            String sqlDeleteMeal = "DELETE FROM " + MEALS +"  WHERE "+ COLUMN_ID +" = " + DialogController.mealChoose.getIdMeal();
            statement.execute(sqlDeleteMeal);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private boolean verifyNameMeal(){
        boolean foundName=false;
        String sql= "SELECT "+ COLUMN_NAME_MEALS+" FROM " + MEALS + " Where " + COLUMN_NAME_MEALS+" = '"+nameMeals.getText()
                +"'";

        try(Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {
            Statement statementSearch = conn.createStatement();
        if(isNewMeal!=0) {
            sql += " AND " + COLUMN_ID + " != " + DialogController.mealChoose.getIdMeal();
        }
        ResultSet results = statementSearch.executeQuery(sql);
        if(results.next()) {
            foundName=true;
        }
        results.close();
    }catch (SQLException e){
        System.out.println(e);
    }

    return foundName;
    }

}