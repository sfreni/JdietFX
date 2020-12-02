package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class MealsTablesController {

    public static final String DB_NAME = "data.sqlite";
    public static final String CONNECTION_STRING = "jdbc:sqlite:db/" + DB_NAME;
    public static final String ALIMENTI = "ALIMENTI";
    public static final String COLUMN_ID = "id_Alim";
    public static final String COLUMN_ALIMENTO = "ALIMENTO";
    public static final String COLUMN_TOTKCAL = "KC";
    public static final String COLUMN_CARBOHIDRATE = "CAR";
    public static final String COLUMN_PROTEINS = "PRO";
    public static final String COLUMN_FAT = "GRA";
    public static final String COLUMN_FIBER = "FIB";
    private static final Logger LOG = LoggerFactory.getLogger(MealsTablesController.class);
    private static Food foodchoose;


    @FXML
    private TableView<Food> tbDietItems;

    @FXML
    public TableColumn<Food, Integer> id;

    @FXML
    public TableColumn<Food, String> unitFood;

    @FXML
    public TableColumn<Food, Integer> totKcal;

    @FXML
    public TableColumn<Food, Integer> grCarboidrati;

    @FXML
    public TableColumn<Food, Integer> grProtein;

    @FXML
    public TableColumn<Food, Integer> grGrassi;

    @FXML
    public TableColumn<Food, Integer> grFibre;

    @FXML
    public Button okButton;

    @FXML
    public Button cancelButton;


    @FXML
    private TextField filterField;



    @FXML
    public void initialize() {

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        unitFood.setCellValueFactory(new PropertyValueFactory<>("unitFood"));
        totKcal.setCellValueFactory(new PropertyValueFactory<>("totKcal"));
        grCarboidrati.setCellValueFactory(new PropertyValueFactory<>("grCarboidrati"));
        grProtein.setCellValueFactory(new PropertyValueFactory<>("grProtein"));
        grGrassi.setCellValueFactory(new PropertyValueFactory<>("grGrassi"));
        grFibre.setCellValueFactory(new PropertyValueFactory<>("grFibre"));

        ObservableList<Food> observableArrayList = FXCollections.observableArrayList();

        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
            Statement statement = conn.createStatement();){

            ResultSet results = statement.executeQuery("SELECT * FROM " + ALIMENTI + " ORDER BY "+ COLUMN_ALIMENTO+" COLLATE NOCASE ASC ");

            observableArrayList = tbDietItems.getItems();

            while (results.next()) {

                observableArrayList.add(new Food(results.getString(COLUMN_ID), results.getString(COLUMN_ALIMENTO), results.getString(COLUMN_TOTKCAL), results.getString(COLUMN_CARBOHIDRATE),
                        results.getString(COLUMN_PROTEINS), results.getString(COLUMN_FAT),results.getString(COLUMN_FIBER)));

            }
            tbDietItems.setItems(observableArrayList);

            tbDietItems.setOnMouseClicked(click -> {

                    if (click.getClickCount() >= 2) {
                        okButton.fire();


                }
            });


        } catch (
                SQLException e) {
            LOG.error("Something went wrong: " , e);
            e.printStackTrace();
        }

        FilteredList<Food> filteredData = new FilteredList<>(observableArrayList, p -> true);

        filterField.textProperty().addListener((observable, oldValue, newValue) ->
            filteredData.setPredicate(food -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (food.getUnitFood().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                return false;
            })
        );

        SortedList<Food> sortedData = new SortedList<>(filteredData);

          sortedData.comparatorProperty().bind(tbDietItems.comparatorProperty());

        tbDietItems.setItems(sortedData);


        okButton.setOnAction(e -> {

            boolean isEmptySelection = tbDietItems.getSelectionModel().getSelectedCells().isEmpty();
            if(!isEmptySelection) {
                TablePosition pos = tbDietItems.getSelectionModel().getSelectedCells().get(0);
                int row = pos.getRow();
                setFoodchoose(tbDietItems.getItems().get(row));
                Stage stage = (Stage) tbDietItems.getScene().getWindow();
                stage.close();
            }else{
                Alert a = new Alert(Alert.AlertType.NONE);
                a.setAlertType(Alert.AlertType.ERROR);
                a.setContentText("NON HAI SELEZIONATO ALCUNA CELLA");
                a.show();
            }
        });

        cancelButton.setOnAction(e -> {
            setFoodchoose(null);
            Stage stage = (Stage) tbDietItems.getScene().getWindow();
            stage.close();

        });



    }
    public static Food getFoodchoose() {
        return foodchoose;
    }

    public static void setFoodchoose(Food foodchoose) {
        MealsTablesController.foodchoose = foodchoose;
    }

    public static class Food {
        private final int id;
        private final String unitFood;
        private final int totKcal;
        private final int grCarboidrati;
        private final int grProtein;
        private final int grGrassi;
        private final int grFibre;


        public Food(String id, String unitFood, String totKcal, String grCarboidrati, String grProtein, String grGrassi, String grFibre) {
            this.id = Integer.parseInt(id);
            this.unitFood = unitFood;
            this.totKcal = Integer.parseInt(totKcal);
            this.grCarboidrati = Integer.parseInt(grCarboidrati);
            this.grProtein = Integer.parseInt(grProtein);
            this.grGrassi = Integer.parseInt(grGrassi);
            this.grFibre= Integer.parseInt(grFibre);

        }

        public int getId() {
            return id;
        }

        public String getUnitFood() {
            return unitFood;
        }

        public int getTotKcal() {
            return totKcal;
        }

        public int getGrCarboidrati() {
            return grCarboidrati;
        }

        public int getGrProtein() {
            return grProtein;
        }

        public int getGrGrassi() {
            return grGrassi;
        }
        public int getGrFibre() {
            return grFibre;
        }

    }
}