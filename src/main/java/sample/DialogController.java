package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class DialogController {

    public static final String DB_NAME = "data.sqlite";
    public static final String CONNECTION_STRING = "jdbc:sqlite:db/" + DB_NAME;
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NAME_MEALS  = "NAME_MEALS";
    public static final String COLUMN_MEALS_HOUR  = "HOUR";
    public static final String COLUMN_TOTKCAL = "TKCAL";
    public static final String COLUMN_CARBOHIDRATE = "GRCARB";
    public static final String COLUMN_PROTEINS = "GRPRO";
    public static final String COLUMN_FAT = "GRGRA";
    public static final String MEALS = "MEALS";
    public static final String MEALS_CONFIG = "MEALS_CONFIG";
    public static final String COLUMN_ID_MEALS = "ID_MEALS";

    public static Meal mealChoose; //E' un oggetto transitorio che consente di gestire la modifica nel DialogModifyMeals

    @FXML
    protected TableView<Meal> tbMealsItems;
    @FXML
    protected TableColumn<Meal, Integer> id;
    @FXML
    protected TableColumn<Meal, String> meals;
    @FXML
    protected TableColumn<Meal, Integer> mealsHour;
    @FXML
    protected TableColumn<Meal, Integer> totKCal;
    @FXML
    protected TableColumn<Meal, Integer> grCarboidrati;
    @FXML
    protected TableColumn<Meal, Integer> grProtein;
    @FXML
    protected TableColumn<Meal, Integer> grGrassi;
    @FXML
    protected TextField TKcalOverall;


    @FXML
    public void initialize() {

        id.setCellValueFactory(new PropertyValueFactory<>("idMeal"));
        meals.setCellValueFactory(new PropertyValueFactory<>("nameMeal"));
        mealsHour.setCellValueFactory(new PropertyValueFactory<>("hourMeal"));
        totKCal.setCellValueFactory(new PropertyValueFactory<>("totKCal"));
        grCarboidrati.setCellValueFactory(new PropertyValueFactory<>("grCarbo"));
        grProtein.setCellValueFactory(new PropertyValueFactory<>("grPro"));
        grGrassi.setCellValueFactory(new PropertyValueFactory<>("grFat"));
        //public Meal(String idMeal, String nameMeal, String hourMeal, String totKCal, String grCarbo, String grPro, String grFat) {
        loadValues();



    }

    @FXML
    protected void newMeals(ActionEvent event) {
        try {
            DialogModifyMeals.isNewMeal=0;
            Stage newStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("modifymeals.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1250, 400);
            newStage.initModality(Modality.APPLICATION_MODAL);
            JMetro jMetro = new JMetro(Style.LIGHT);
            jMetro.setScene(scene);
            newStage.setTitle("Inserisci Pasto");
            newStage.setScene(scene);

            newStage.showAndWait();
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();

        }

        loadValues();
        TableView.TableViewSelectionModel selectionModel = tbMealsItems.getSelectionModel();
        selectionModel.select(0);



    }

    @FXML
    protected void modifyMeals(ActionEvent event) {
        boolean isEmptySelection = tbMealsItems.getSelectionModel().getSelectedCells().isEmpty();
        if(!isEmptySelection) {
            TablePosition pos = tbMealsItems.getSelectionModel().getSelectedCells().get(0);
            int row = pos.getRow();
            mealChoose = tbMealsItems.getItems().get(row);
        try {
            DialogModifyMeals.isNewMeal=1;
            Stage newStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("modifymeals.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1250, 400);
            JMetro jMetro = new JMetro(Style.LIGHT);
            jMetro.setScene(scene);
            newStage.setTitle("Modifica Pasto");
            newStage.setScene(scene);
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.showAndWait();
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
        }

        loadValues();
            TableView.TableViewSelectionModel selectionModel = tbMealsItems.getSelectionModel();
            selectionModel.select(row);

        }else{
            Alert a = new Alert(Alert.AlertType.NONE);
            a.setAlertType(Alert.AlertType.ERROR);
            a.setContentText("Si prega di selezionare l'elemento desiderato prima di cliccare il pulsante Modifica");
            // show the dialog
            a.show();

        }



    }










        @FXML
    protected void closeThisScreen(ActionEvent event) {
        Stage stage = (Stage) tbMealsItems.getScene().getWindow();
        stage.close();
        stage=null;
    }

    protected void loadValues(){
            try(Connection conn = DriverManager.getConnection(CONNECTION_STRING)) {

            Statement statement = conn.createStatement();
            //ResultSet results = statement.executeQuery("SELECT * FROM MEALS  ORDER BY "+ COLUMN_ID +" ASC ");
            ResultSet results = statement.executeQuery("SELECT MEALS.ID, MEALS.NAME_MEALS, MEALS.HOUR, SUM([GRAMS]*[CAR])/100 as GRCARB, \n" +
                    "sum([GRAMS]*[PRO])/100 as GRPRO, sum([GRAMS]*[GRA])/100 AS GRGRA, SUM([GRAMS]*[KC])/100 AS TKCAL\n" +
                    " FROM MEALS INNER JOIN MEALS_CONFIG ON MEALS.ID = MEALS_CONFIG.ID_MEALS AND MEALS.ID = MEALS_CONFIG.ID_MEALS AND \n" +
                    "MEALS.ID = MEALS_CONFIG.ID_MEALS AND MEALS.ID = MEALS_CONFIG.ID_MEALS AND MEALS.ID = MEALS_CONFIG.ID_MEALS \n" +
                    "INNER JOIN tb_alim ON MEALS_CONFIG.ID_FOOD = tb_alim.ID_Alim GROUP BY MEALS.ID, MEALS.NAME_MEALS, MEALS.HOUR ORDER BY MEALS.HOUR");

            ObservableList<Meal> observableArrayList= FXCollections.observableArrayList();
            //observableArrayList = tbMealsItems.getItems();

            while (results.next()) {

                observableArrayList.add(new Meal(results.getString(COLUMN_ID), results.getString(COLUMN_NAME_MEALS), results.getString(COLUMN_MEALS_HOUR), (results.getString(COLUMN_TOTKCAL)),
                        results.getString(COLUMN_CARBOHIDRATE), results.getString(COLUMN_PROTEINS), results.getString(COLUMN_FAT)));
            }
            tbMealsItems.setItems(observableArrayList);
            results.close();
            int sumTotKcal=0;
            for(Meal meal: observableArrayList){
                sumTotKcal+=Integer.parseInt(meal.getTotKCal());
            }
            TKcalOverall.setText(Integer.toString(sumTotKcal));

            tbMealsItems.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent click) {
                    if (click.getClickCount() >= 1) {
                        if (click.getClickCount() >= 2) {
                            modifyMeals(new ActionEvent());//                    okButton.fire();
                        }

                    }
                }
            });


        } catch (
                SQLException e) {
            System.out.println("Something went wrong: " + e.getMessage());
            e.printStackTrace();
        }

    }


    @FXML
    protected void deleteMeals(ActionEvent event) {
        boolean isEmptySelection = tbMealsItems.getSelectionModel().getSelectedCells().isEmpty();
        if(!isEmptySelection) {

            ///

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Eliminazione Pasto");
            alert.setHeaderText("Stai per eliminare il pasto visualizzato");
            alert.setContentText("Sei sicuro?");
            alert.getDialogPane().setPrefSize(350, 200);
            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK) {



            ///
            TablePosition pos = tbMealsItems.getSelectionModel().getSelectedCells().get(0);
            int row = pos.getRow();
            mealChoose = tbMealsItems.getItems().get(row);

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

            loadValues();
            TableView.TableViewSelectionModel selectionModel = tbMealsItems.getSelectionModel();
            selectionModel.select(row);
            }
        }else{
            Alert a = new Alert(Alert.AlertType.NONE);
            a.setAlertType(Alert.AlertType.ERROR);
            a.setContentText("Si prega di selezionare l'elemento desiderato prima di cliccare il pulsante Elimina");
            // show the dialog
            a.show();

        }



    }






    public class Meal{
        private String idMeal;
        private String nameMeal;
        private String hourMeal;
        private String totKCal;
        private String grCarbo;
        private String grPro;
        private String grFat;

        public Meal(String idMeal, String nameMeal, String hourMeal, String totKCal, String grCarbo, String grPro, String grFat) {
            this.idMeal = idMeal;
            this.nameMeal = nameMeal;
            this.hourMeal = hourMeal;

            this.totKCal = totKCal;
           // Convertdouble = Math.round(Double.parseDouble(grCarbo)); //rimuovo i senza senso decimali
            this.grCarbo =grCarbo;
         //   Convertdouble = Math.round(Double.parseDouble(grPro)); //rimuovo i senza senso decimali
            this.grPro = grPro;
       //     Convertdouble = Math.round(Double.parseDouble(grFat)); //rimuovo i senza senso decimali
            this.grFat = grFat;
        }

        public String getIdMeal() {
            return idMeal;
        }

        public String getNameMeal() {
            return nameMeal;
        }

        public String getHourMeal() {
            return hourMeal;
        }

        public String getTotKCal() {
            return totKCal;
        }

        public String getGrCarbo() {
            return grCarbo;
        }

        public String getGrPro() {
            return grPro;
        }

        public String getGrFat() {
            return grFat;
        }
    }

}
