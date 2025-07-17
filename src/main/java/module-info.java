module com.study.library {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires static lombok;

    opens com.study.library.controller to javafx.fxml;
    exports com.study.library;
    opens com.study.library.model to javafx.base;
}