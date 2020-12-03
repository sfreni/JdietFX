package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public static final String COLUMN_FIB = "GRFIB";
    public static final String TABLE_MEALS = "MEALS";
    public static final String MEALS_CONFIG = "MEALS_CONFIG";
    public static final String COLUMN_ID_MEALS = "ID_MEALS";
    private static final Logger LOG = LoggerFactory.getLogger(DialogController.class);
    private static Meal mealChoose; //E' un oggetto transitorio che consente di gestire la modifica nel DialogModifyMeals



    @FXML
    private TableView<Meal> tbMealsItems;

    @FXML
    private TableColumn<Meal, Integer> id;
    @FXML
    private TableColumn<Meal, String> meals;
    @FXML
    private TableColumn<Meal, Integer> mealsHour;
    @FXML
    private TableColumn<Meal, Integer> totKCal;
    @FXML
    private TableColumn<Meal, Integer> grCarboidrati;
    @FXML
    private TableColumn<Meal, Integer> grProtein;
    @FXML
    private TableColumn<Meal, Integer> grGrassi;
    @FXML
    private TableColumn<Meal, Integer> grFibre;
    @FXML
    private TextField tkcalOverall;


    @FXML
    public void initialize() {

        id.setCellValueFactory(new PropertyValueFactory<>("idMeal"));
        meals.setCellValueFactory(new PropertyValueFactory<>("nameMeal"));
        mealsHour.setCellValueFactory(new PropertyValueFactory<>("hourMeal"));
        totKCal.setCellValueFactory(new PropertyValueFactory<>("totKCal"));
        grCarboidrati.setCellValueFactory(new PropertyValueFactory<>("grCarbo"));
        grProtein.setCellValueFactory(new PropertyValueFactory<>("grPro"));
        grGrassi.setCellValueFactory(new PropertyValueFactory<>("grFat"));
        grFibre.setCellValueFactory(new PropertyValueFactory<>("grFib"));
        loadValues();



    }

    @FXML
    protected void newMeals() {
        try {
            DialogModifyMeals.setIsNewMeal(true);
            Stage newStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifyMeals.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1370, 400);
            newStage.initModality(Modality.APPLICATION_MODAL);
            JMetro jMetro = new JMetro(Style.LIGHT);
            jMetro.setScene(scene);
            newStage.setTitle("Inserisci Pasto");
            newStage.setScene(scene);

            newStage.showAndWait();
        } catch (IOException e) {
            LOG.error("Couldn't load the dialog");
            e.printStackTrace();

        }

        loadValues();
        TableView.TableViewSelectionModel<Meal> selectionModel = tbMealsItems.getSelectionModel();
        selectionModel.select(0);



    }

    @FXML
    protected void modifyMeals() {
        boolean isEmptySelection = tbMealsItems.getSelectionModel().getSelectedCells().isEmpty();
        if(!isEmptySelection) {
            TablePosition pos = tbMealsItems.getSelectionModel().getSelectedCells().get(0);
            int row = pos.getRow();
            setMealChoose(tbMealsItems.getItems().get(row));
        try {
            DialogModifyMeals.setIsNewMeal(false);
            Stage newStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifyMeals.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1370, 400);
            JMetro jMetro = new JMetro(Style.LIGHT);
            jMetro.setScene(scene);

            newStage.setTitle("Modifica Pasto");
            newStage.setScene(scene);

            newStage.showAndWait();
        } catch (IOException e) {
            LOG.error("Couldn't load the dialog");
            e.printStackTrace();
        }

        loadValues();
            TableView.TableViewSelectionModel<Meal> selectionModel = tbMealsItems.getSelectionModel();
            selectionModel.select(row);

        }else{
            Alert a = new Alert(Alert.AlertType.NONE);
            a.setAlertType(Alert.AlertType.ERROR);
            a.setContentText("Si prega di selezionare l'elemento desiderato prima di cliccare il pulsante Modifica");
            a.show();

        }



    }

        @FXML
    protected void closeThisScreen() {
        Stage stage = (Stage) tbMealsItems.getScene().getWindow();
        stage.close();

    }

    protected void loadValues(){
        String sql="SELECT MEALS.ID, MEALS.NAME_MEALS, MEALS.HOUR, SUM([GRAMS]*[CAR])/100 as GRCARB, \n" +
                "sum([GRAMS]*[PRO])/100 as GRPRO, sum([GRAMS]*[GRA])/100 AS GRGRA, sum([GRAMS]*[FIB])/100 AS GRFIB, SUM([GRAMS]*[KC])/100 AS TKCAL\n" +
                " FROM MEALS INNER JOIN MEALS_CONFIG ON MEALS.ID = MEALS_CONFIG.ID_MEALS AND MEALS.ID = MEALS_CONFIG.ID_MEALS AND \n" +
                "MEALS.ID = MEALS_CONFIG.ID_MEALS AND MEALS.ID = MEALS_CONFIG.ID_MEALS AND MEALS.ID = MEALS_CONFIG.ID_MEALS \n" +
                "INNER JOIN ALIMENTI ON MEALS_CONFIG.ID_FOOD = ALIMENTI.ID_Alim GROUP BY MEALS.ID, MEALS.NAME_MEALS, MEALS.HOUR ORDER BY MEALS.HOUR";

try(Connection conn = DriverManager.getConnection(CONNECTION_STRING);
                ResultSet results = conn.createStatement().executeQuery(sql)) {
            ObservableList<Meal> observableArrayList= FXCollections.observableArrayList();

            while (results.next()) {

                observableArrayList.add(new Meal(results.getString(COLUMN_ID), results.getString(COLUMN_NAME_MEALS), results.getString(COLUMN_MEALS_HOUR), (results.getString(COLUMN_TOTKCAL)),
                        results.getString(COLUMN_CARBOHIDRATE), results.getString(COLUMN_PROTEINS), results.getString(COLUMN_FAT), results.getString(COLUMN_FIB) ));
            }
            tbMealsItems.setItems(observableArrayList);

            int sumTotKcal=0;
            for(Meal meal: observableArrayList){
                sumTotKcal+=Integer.parseInt(meal.getTotKCal());
            }
            tkcalOverall.setText(Integer.toString(sumTotKcal));

            tbMealsItems.setOnMouseClicked(click -> {

                    if (click.getClickCount() >= 2) {
                        modifyMeals();
                    }


            });


        } catch (
                SQLException e) {
            LOG.error(("Something went wrong: " + e.getMessage()));
            e.printStackTrace();
        }

    }


    @FXML
    protected void deleteMeals() {
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

            TablePosition pos = tbMealsItems.getSelectionModel().getSelectedCells().get(0);
            int row = pos.getRow();
            setMealChoose(tbMealsItems.getItems().get(row));
                String sqlDeleteMealConfig = "DELETE FROM " + MEALS_CONFIG +"  WHERE "+ COLUMN_ID_MEALS +" = " + DialogController.mealChoose.getIdMeal();
                String sqlDeleteMeal = "DELETE FROM " + TABLE_MEALS +"  WHERE "+ COLUMN_ID +" = " + DialogController.mealChoose.getIdMeal();
            try(Connection conn = DriverManager.getConnection(CONNECTION_STRING);
                Statement statementConfig = conn.createStatement();
                Statement statementMeal = conn.createStatement()) {
                statementConfig.execute(sqlDeleteMealConfig);
                statementMeal.execute(sqlDeleteMeal);
            } catch (SQLException e) {
                LOG.error(e.getMessage());
            }

            loadValues();
            TableView.TableViewSelectionModel<Meal> selectionModel = tbMealsItems.getSelectionModel();
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
    public static Meal getMealChoose() {
        return mealChoose;
    }

    public static void setMealChoose(Meal mealChoose) {
        DialogController.mealChoose = mealChoose;
    }


}
