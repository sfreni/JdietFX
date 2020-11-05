package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class HandleFoodTable {

    public static final String DB_NAME = "data.sqlite";
    public static final String CONNECTION_STRING = "jdbc:sqlite:db/" + DB_NAME;
    public static final String TABLE_ALIMENTI = "tb_alim";
    public static final String COLUMN_ID = "id_Alim";
    public static final String COLUMN_ALIMENTO = "ALIMENTO";
    public static final String COLUMN_TOTKCAL = "KC";
    public static final String COLUMN_CARBOHIDRATE = "CAR";
    public static final String COLUMN_PROTEINS = "PRO";
    public static final String COLUMN_FAT = "GRA";
    public static final String COLUMN_FIBER = "FIB";

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
    public TableColumn<Food, Integer> grFiber;

    @FXML
    public AnchorPane anchorPane;

    @FXML
    public Button addButton;

    @FXML
    public Button cancelButton;

    @FXML
    protected Button modifyButton;

    @FXML
    protected Button deleteButton;


    @FXML
    protected Button     sampleButton;


    @FXML
    private TextField filterField;

    @FXML

    public void initialize() {
      //  anchorPane.getStyleClass().add(JMetroStyleClass.BACKGROUND);

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
                                ControlHandleFoodDetail.rowModifyRecord = tbDietItems.getItems().get(row).getId();
                                ControlHandleFoodDetail.isNewFood = false;
                                ControlHandleFoodDetail.deleteData();
                            } catch (Exception ex) {
                                System.out.println("Couldn't load the dialog");
                                ex.printStackTrace();
                            }
                        }
                        //    loadValues();

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
            stage=null;
        });
        sampleButton.setOnAction(e -> {
            try {
                ControlHandleFoodDetail.isNewFood = true;
                Stage newStage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("prova.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 410, 310);

                newStage.initModality(Modality.APPLICATION_MODAL);
                JMetro jMetro = new JMetro(Style.LIGHT);


                jMetro.setScene(scene);
                newStage.setTitle("Aggiungi Alimento");
                newStage.setScene(scene);
                newStage.showAndWait();
            }catch (IOException exc) {
                System.out.println("Couldn't load the dialog");
                exc.printStackTrace();
            }
        });




    }

    private void loadTableItems() {
        ObservableList<Food> observableArrayList;
        //  observableArrayList=null;
        observableArrayList = FXCollections.observableArrayList();

        try {
            Connection conn = DriverManager.getConnection(CONNECTION_STRING);
            Statement statement = conn.createStatement();

            ResultSet results = statement.executeQuery("SELECT * FROM " + TABLE_ALIMENTI + " ORDER BY " + COLUMN_ALIMENTO + " COLLATE NOCASE ASC ");

//            observableArrayList = tbDietItems.getItems();

            while (results.next()) {

                observableArrayList.add(new Food(results.getString(COLUMN_ID), results.getString(COLUMN_ALIMENTO), results.getString(COLUMN_TOTKCAL), results.getString(COLUMN_CARBOHIDRATE),
                        results.getString(COLUMN_PROTEINS), results.getString(COLUMN_FAT), results.getString(COLUMN_FIBER)));

            }
            tbDietItems.setItems(observableArrayList);
            results.close();
            conn.close();

            tbDietItems.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent click) {
                    if (click.getClickCount() >= 1) {
                        if (click.getClickCount() >= 2) {
                            modifyFood(new ActionEvent());
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
    }

    @FXML
    protected void newFood(ActionEvent event) {
        try {
            ControlHandleFoodDetail.isNewFood = true;
            Stage newStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("HandleFoodDetail.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 410, 310);

            newStage.initModality(Modality.APPLICATION_MODAL);
            JMetro jMetro = new JMetro(Style.LIGHT);


            jMetro.setScene(scene);
            newStage.setTitle("Aggiungi Alimento");
            newStage.setScene(scene);
            newStage.showAndWait();

        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
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
                ControlHandleFoodDetail.rowModifyRecord = tbDietItems.getItems().get(row).getId();
                ControlHandleFoodDetail.isNewFood = false;
                Stage newStage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("HandleFoodDetail.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 410, 310);
                newStage.initModality(Modality.APPLICATION_MODAL);
                JMetro jMetro = new JMetro(Style.LIGHT);
                jMetro.setScene(scene);
                newStage.setTitle("Modifica Alimento");
                newStage.setScene(scene);
                newStage.showAndWait();
            } catch (IOException e) {
                System.out.println("Couldn't load the dialog");
                e.printStackTrace();
            }

            //    loadValues();
            TableView.TableViewSelectionModel selectionModel = tbDietItems.getSelectionModel();
            selectionModel.select(row);

        } else {
            Alert a = new Alert(Alert.AlertType.NONE);
            a.setAlertType(Alert.AlertType.ERROR);
            a.setContentText("Si prega di selezionare l'elemento desiderato prima di cliccare il pulsante Modifica");
            // show the dialog
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