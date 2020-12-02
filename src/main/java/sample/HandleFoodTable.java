package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class HandleFoodTable {

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
    private static final Logger LOG = LoggerFactory.getLogger(HandleFoodTable.class);
    public static final String STRING_DIALOG_ERROR = "Couldn't load the dialog";


    @FXML
    private TableView<Food> tbDietItems;

    @FXML
    private TableColumn<Food, Integer> id;

    @FXML
    private TableColumn<Food, String> unitFood;

    @FXML
    private TableColumn<Food, Integer> totKcal;

    @FXML
    private TableColumn<Food, Integer> grCarboidrati;

    @FXML
    private TableColumn<Food, Integer> grProtein;

    @FXML
    private TableColumn<Food, Integer> grGrassi;

    @FXML
    private TableColumn<Food, Integer> grFiber;

    @FXML
    private Button cancelButton;


    @FXML
    private  Button deleteButton;


    @FXML
    private  TextField filterField;

    @FXML

    public void initialize() {

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        unitFood.setCellValueFactory(new PropertyValueFactory<>("unitFood"));
        totKcal.setCellValueFactory(new PropertyValueFactory<>("totKcal"));
        grCarboidrati.setCellValueFactory(new PropertyValueFactory<>("grCarboidrati"));
        grProtein.setCellValueFactory(new PropertyValueFactory<>("grProtein"));
        grGrassi.setCellValueFactory(new PropertyValueFactory<>("grGrassi"));
        grFiber.setCellValueFactory(new PropertyValueFactory<>("grFiber"));

        loadTableItems();

        deleteButton.setOnAction(e -> {
                    boolean isEmptySelection = tbDietItems.getSelectionModel().getSelectedCells().isEmpty();
                    if (!isEmptySelection) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Eliminazione Pasto");
                        alert.setHeaderText("Stai per eliminare l'alimento visualizzato");
                        alert.setContentText("Sei sicuro?");
                        alert.getDialogPane().setPrefSize(350, 200);
                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {


                            TablePosition pos = tbDietItems.getSelectionModel().getSelectedCells().get(0);
                            int row = pos.getRow();
                            try {
                                ControlHandleFoodDetail.setRowModifyRecord(tbDietItems.getItems().get(row).getId());
                                ControlHandleFoodDetail.setIsNewFood(false);
                                ControlHandleFoodDetail.deleteData();
                            } catch (Exception ex) {
                                LOG.error(STRING_DIALOG_ERROR);
                                ex.printStackTrace();
                            }
                        }

                    } else {
                        Alert a = new Alert(Alert.AlertType.NONE);
                        a.setAlertType(Alert.AlertType.ERROR);
                        a.setContentText("Si prega di selezionare l'elemento desiderato prima di cliccare il pulsante Modifica");
                        a.show();

                    }
                    loadTableItems();

                }
        );
        cancelButton.setOnAction(e -> {
            Stage stage = (Stage) tbDietItems.getScene().getWindow();
            stage.close();

        });
    }

    private void loadTableItems() {
        ObservableList<Food> observableArrayList;
        observableArrayList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM " + ALIMENTI + " ORDER BY " + COLUMN_ALIMENTO + " COLLATE NOCASE ASC ";
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING);
             ResultSet results = conn.createStatement().executeQuery(sql)) {

            while (results.next()) {

                observableArrayList.add(new Food(results.getString(COLUMN_ID), results.getString(COLUMN_ALIMENTO), results.getString(COLUMN_TOTKCAL), results.getString(COLUMN_CARBOHIDRATE),
                        results.getString(COLUMN_PROTEINS), results.getString(COLUMN_FAT), results.getString(COLUMN_FIBER)));

            }
            tbDietItems.setItems(observableArrayList);

            tbDietItems.setOnMouseClicked(click -> {

                if (click.getClickCount() >= 2) {
                    modifyFood(new ActionEvent());
                }

            });


        } catch (
                SQLException e) {
            LOG.error("Something went wrong: ", e);
            e.printStackTrace();
        }

        FilteredList<Food> filteredData = new FilteredList<>(observableArrayList, p -> true);

        filterField.textProperty().addListener((observable, oldValue, newValue) -> filteredData.setPredicate(food -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }


                String lowerCaseFilter = newValue.toLowerCase();

                return food.getUnitFood().toLowerCase().contains(lowerCaseFilter);
            })
        );


        SortedList<Food> sortedData = new SortedList<>(filteredData);

        sortedData.comparatorProperty().bind(tbDietItems.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        tbDietItems.setItems(sortedData);
    }

    @FXML
    protected void newFood(ActionEvent event) {
        try {
            ControlHandleFoodDetail.setIsNewFood(true);
            Stage newStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HandleFoodDetail.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 410, 310);

            newStage.initModality(Modality.APPLICATION_MODAL);
            JMetro jMetro = new JMetro(Style.LIGHT);


            jMetro.setScene(scene);
            newStage.setTitle("Aggiungi Alimento");
            newStage.setScene(scene);
            newStage.showAndWait();

        } catch (IOException e) {
            LOG.error(STRING_DIALOG_ERROR);
            e.printStackTrace();

        }

        loadTableItems();

    }

    @FXML
    protected void modifyFood(ActionEvent event) {
        boolean isEmptySelection = tbDietItems.getSelectionModel().getSelectedCells().isEmpty();
        if (!isEmptySelection) {
            TablePosition pos = tbDietItems.getSelectionModel().getSelectedCells().get(0);
            int row = pos.getRow();
            try {
                ControlHandleFoodDetail.setRowModifyRecord(tbDietItems.getItems().get(row).getId());
                ControlHandleFoodDetail.setIsNewFood(false);
                Stage newStage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/HandleFoodDetail.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 410, 310);
                newStage.initModality(Modality.APPLICATION_MODAL);
                JMetro jMetro = new JMetro(Style.LIGHT);
                jMetro.setScene(scene);
                newStage.setTitle("Modifica Alimento");
                newStage.setScene(scene);
                newStage.showAndWait();
            } catch (IOException e) {
                LOG.error(STRING_DIALOG_ERROR);
                e.printStackTrace();
            }

            TableView.TableViewSelectionModel<Food> selectionModel = tbDietItems.getSelectionModel();
            selectionModel.select(row);

        } else {
            Alert a = new Alert(Alert.AlertType.NONE);
            a.setAlertType(Alert.AlertType.ERROR);
            a.setContentText("Si prega di selezionare l'elemento desiderato prima di cliccare il pulsante Modifica");
            a.show();

        }
        loadTableItems();


    }


    public static class Food {
        private final int id;
        private final String unitFood;
        private final int totKcal;
        private final int grCarboidrati;
        private final int grProtein;
        private final int grGrassi;
        private final int grFiber;

        public Food(String id, String unitFood, String totKcal, String grCarboidrati, String grProtein, String grGrassi, String grFiber) {
            this.id = Integer.parseInt(id);
            this.unitFood = unitFood;
            this.totKcal = Integer.parseInt(totKcal);
            this.grCarboidrati = Integer.parseInt(grCarboidrati);
            this.grProtein = Integer.parseInt(grProtein);
            this.grGrassi = Integer.parseInt(grGrassi);
            this.grFiber = Integer.parseInt(grFiber);
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

        public int getGrFiber() {
            return grFiber;
        }

    }
}