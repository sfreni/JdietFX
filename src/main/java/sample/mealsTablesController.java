package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.*;

public class mealsTablesController {

    public static final String DB_NAME = "data.sqlite";
    public static final String CONNECTION_STRING = "jdbc:sqlite:db/" + DB_NAME;

    public static final String TABLE_ALIMENTI = "tb_alim";
    public static final String COLUMN_ID = "id_Alim";
    public static final String COLUMN_ALIMENTO = "ALIMENTO";
    public static final String COLUMN_TOTKCAL = "KC";
    public static final String COLUMN_CARBOHIDRATE = "CAR";
    public static final String COLUMN_PROTEINS = "PRO";
    public static final String COLUMN_FAT = "GRA";
    public static Food foodchoose; //E' un oggetto transitorio che consente di trasferire l'oggetto nell'altra mascherina
    // public static int cancelPressed;
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


        ObservableList<Food> observableArrayList = FXCollections.observableArrayList();
        try {
            Connection conn = DriverManager.getConnection(CONNECTION_STRING);
            Statement statement = conn.createStatement();

            ResultSet results = statement.executeQuery("SELECT * FROM " + TABLE_ALIMENTI + " ORDER BY "+ COLUMN_ALIMENTO+" COLLATE NOCASE ASC ");

            observableArrayList = tbDietItems.getItems();

            while (results.next()) {

                observableArrayList.add(new Food(results.getString(COLUMN_ID), results.getString(COLUMN_ALIMENTO), results.getString(COLUMN_TOTKCAL), results.getString(COLUMN_CARBOHIDRATE),
                        results.getString(COLUMN_PROTEINS), results.getString(COLUMN_FAT)));

            }
            tbDietItems.setItems(observableArrayList);
            results.close();
            conn.close();

            tbDietItems.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent click) {
                    if (click.getClickCount() >= 1) {
                        if (click.getClickCount() >= 2) {
                            okButton.fire();
                }

                    }
                }
            });


            results.close();

            statement.close();
            conn.close();

        } catch (
                SQLException e) {
            System.out.println("Something went wrong: " + e.getMessage());
            e.printStackTrace();
        }

        FilteredList<Food> filteredData = new FilteredList<>(observableArrayList, p -> true);

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(food -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (food.getUnitFood().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true; // Filter matches first name.
                }

                return false; // Does not match.
            });
        });

        // 3. Wrap the FilteredList in a SortedList.
        SortedList<Food> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        // 	  Otherwise, sorting the TableView would have no effect.
        sortedData.comparatorProperty().bind(tbDietItems.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        tbDietItems.setItems(sortedData);


        okButton.setOnAction(e -> {

            boolean isEmptySelection = tbDietItems.getSelectionModel().getSelectedCells().isEmpty();
            if(!isEmptySelection) {
                TablePosition pos = tbDietItems.getSelectionModel().getSelectedCells().get(0);
                int row = pos.getRow();
                foodchoose = tbDietItems.getItems().get(row);
                Stage stage = (Stage) tbDietItems.getScene().getWindow();
                // do what you have to do
                stage.close();
            }else{
                Alert a = new Alert(Alert.AlertType.NONE);
                a.setAlertType(Alert.AlertType.ERROR);
                a.setContentText("NON HAI SELEZIONATO ALCUNA CELLA");
                // show the dialog
                a.show();
            }
        });

        cancelButton.setOnAction(e -> {
            foodchoose=null;
            Stage stage = (Stage) tbDietItems.getScene().getWindow();
            stage.close();

        });

    }


    public static class Food {
        private final int id;
        private final String unitFood;
        private final int totKcal;
        private final int grCarboidrati;
        private final int grProtein;
        private final int grGrassi;


        public Food(String id, String unitFood, String totKcal, String grCarboidrati, String grProtein, String grGrassi) {
            this.id = Integer.parseInt(id);
            this.unitFood = unitFood;
            this.totKcal = Integer.parseInt(totKcal);
            this.grCarboidrati = Integer.parseInt(grCarboidrati);
            this.grProtein = Integer.parseInt(grProtein);
            this.grGrassi = Integer.parseInt(grGrassi);
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
    }
}