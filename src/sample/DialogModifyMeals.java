package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.DateTimeStringConverter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DialogModifyMeals {

    static ArrayList<HBox> hboxList= new ArrayList<>();
    static int countButton; //Conta il numero delle righe ogni volta che viene cliccato (+)
    private int pressButton;
    private int textValueFocusIn;

    @FXML
    protected GridPane modifyMealsGrid;

    @FXML
    protected TextField hour;
    protected static String hourValue;
    @FXML
    private VBox vboxMeals;

    @FXML
    protected TextField nameMeals;
    protected static String nameMealsValue;
    @FXML
    public void initialize() {
        AddFields();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        try{
            hour.setTextFormatter(new TextFormatter<>(new DateTimeStringConverter(format), format.parse("12:00")));
            hourValue=hour.getText();

        }
            catch (ParseException parsexception){
        }

        nameMeals.focusedProperty().addListener((obs, oldVal, inFocus) -> {
        if(!inFocus){
            nameMealsValue=nameMeals.getText();

        }

        });

        hour.focusedProperty().addListener((obs, oldVal, inFocus) -> {
            if(!inFocus){
                hourValue=hour.getText();

            }

        });



    }

    @FXML
    protected void searchMeals(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("tableViews.fxml"));
        try {

            Stage newStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("tableViews.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 600, 700);

            newStage.setScene(scene);

            newStage.showAndWait();

        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }



      if(mealsTablesController.foodchoose!=null){
          setFoodValues(mealsTablesController.foodchoose,hboxList.get(pressButton));
      }



    }
    @FXML
    protected void AddFields() {

        int buttonPressed=countButton;
        if(mealsTablesController.foodchoose!=null || countButton==0) {

            HBox hbox1 = new HBox();
            hbox1.setSpacing(10);

            //modifyMealsGrid.add(hbox1, 0, countButton+1);
            vboxMeals.getChildren().add(hbox1);
            TextField textId = new TextField();
            textId.setVisible(false);
            textId.setMaxWidth(1);
            Label label1 = new Label();
            label1.setText("Alimento:");
            TextField textField1 = new TextField();
            textField1.setEditable(false);
            Button search1 = new Button();
            search1.setText("Cerca");

            search1.setOnAction(e -> {
                pressButton=buttonPressed;
                searchMeals(e);

            });

            Label label2 = new Label();
            label2.setText("Grammi:");
            TextField textField2 = new TextField();
            textField2.setMaxWidth(80);



            Label label3 = new Label();
             label3.setText("Kcal Totali:");
            TextField textField3 = new TextField();
            textField3.setMaxWidth(80);
            textField3.setEditable(false);
            Label label4 = new Label();
            label4.setText("Gr. Carboidrati:");
            TextField textField4 = new TextField();
            textField4.setMaxWidth(80);
            textField4.setEditable(false);
            Label label5 = new Label();
            label5.setText("Gr. Proteine:");
            TextField textField5 = new TextField();
            textField5.setMaxWidth(80);
            textField5.setEditable(false);
            Label label6 = new Label();
            label6.setText("Gr. Grassi:");
            TextField textField6 = new TextField();
            textField6.setMaxWidth(80);
            textField6.setEditable(false);
            textField2.focusedProperty().addListener((obs, oldVal, inFocus) -> {

                if (inFocus) {
                    textValueFocusIn=Integer.parseInt(textField2.getText());

                }else {

                    try {
                        int getVerifyValue = Integer.parseInt(textField2.getText());
                        if(getVerifyValue<=0){

                            textField2.setText(Integer.toString(textValueFocusIn));
                        }else{

                            textField4.setText(setTextValues(textField4, Integer.parseInt(textField2.getText()),textValueFocusIn));
                            textField5.setText(setTextValues(textField5, Integer.parseInt(textField2.getText()),textValueFocusIn));
                            textField6.setText(setTextValues(textField6, Integer.parseInt(textField2.getText()),textValueFocusIn));

                            double totKcalValue=(Double.parseDouble(textField4.getText())*4 + Double.parseDouble(textField5.getText())*4 + Double.parseDouble(textField6.getText())*9);

                            textField3.setText(Double.toString(totKcalValue));
                        }
                    } catch (NumberFormatException e) {
                        textField2.setText(Integer.toString(textValueFocusIn));

                    }


                }
            });

            Button addButton = new Button();
            addButton.setText("+");
            addButton.setOnAction(e ->{ AddFields();
                    modifyMealsGrid.getScene().getWindow().sizeToScene();

//                        for(HBox hboxElement : hboxList ){
//                            TextField txt = (TextField) hboxElement.getChildren().get(1);
//                            System.out.println(txt.getText());
//                        }

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
            if(countButton!=0){

                Button minusButton = new Button();
            minusButton.setText("-");
            minusButton.setOnAction(e -> {
                pressButton=buttonPressed;
                hbox1.getChildren().clear();
                //hboxList.remove(pressButton);

                mealsTablesController.foodchoose=new mealsTablesController.Food("0","0","0",
                        "0","0","0"); //creo un oggetto vuoto per far funzionare il pulsante "+" che fa un check sull'esistenza dell'oggetto.
            });

            hbox1.getChildren().add(minusButton);
        }

            hboxList.add(hbox1);

            mealsTablesController.foodchoose=null;
            //hbox1.getChildren().get(1);


            countButton++;
            System.out.println(hboxList.size());
        }

    }
    @FXML
    protected void setFoodValues(mealsTablesController.Food foodNameBeUsed, HBox hbox) {

        TextField foodIdValues =(TextField) hbox.getChildren().get(0);
        foodIdValues.setText(Integer.toString(foodNameBeUsed.getId()));
        TextField foodNameTextField =(TextField) hbox.getChildren().get(2);
        foodNameTextField.setText(foodNameBeUsed.getUnitFood());
        TextField foodGramsTextField =(TextField) hbox.getChildren().get(5);
        foodGramsTextField.setText(Integer.toString(100));
        TextField totalKcalTextField =(TextField) hbox.getChildren().get(7);
        totalKcalTextField.setText(Integer.toString(foodNameBeUsed.getTotKcal()));
        TextField grCarboTextField =(TextField) hbox.getChildren().get(9);
        grCarboTextField.setText(foodNameBeUsed.getGrCarboidrati());
        TextField grProsTextField =(TextField) hbox.getChildren().get(11);
        grProsTextField.setText(foodNameBeUsed.getGrProtein());
        TextField grGrasTextField =(TextField) hbox.getChildren().get(13);
        grGrasTextField.setText(foodNameBeUsed.getGrGrassi());


    }

    protected  String setTextValues(TextField textField, int  grams, int originValue) {
        double convertDouble=Double.parseDouble(textField.getText())/originValue*100;

        return 