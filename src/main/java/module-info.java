module myJavaFx {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires java.sql;
    requires org.jfxtras.styles.jmetro;
    requires kernel;
    requires layout;
    requires java.desktop;
    requires org.slf4j;
    requires io;

    opens sample to javafx.fxml;

    exports sample;
}