module myJavaFx {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires java.sql;
//    requires slf4j.api;
   requires org.jfxtras.styles.jmetro;
    opens sample;

}