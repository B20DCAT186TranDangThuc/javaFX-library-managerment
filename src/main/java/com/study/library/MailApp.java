package com.study.library;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MailApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Load giao diện chính (menu điều hướng)
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/study/library/fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1100, 800);
        // Load icon
        Image appIcon = new Image(getClass().getResourceAsStream("/com/study/library/images/library-icon.jpg"));
        stage.getIcons().add(appIcon);
        stage.setResizable(false);
        stage.setTitle("Library Management System");
        stage.setScene(scene);

        stage.setOnCloseRequest(event -> {
            event.consume();
            boolean confirm = showConfirmationDialog();
            if (confirm) {
                Platform.exit(); // Dừng chương trình
                System.exit(0); // Đảm bảo chương trình dừng hoàn toàn
            }
        });
        stage.show();
    }

    private boolean showConfirmationDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác Nhận");
        alert.setHeaderText("Bạn có chắc chắn muốn thoát không?");
        alert.setContentText("Nếu bạn thoát, tất cả dữ liệu chưa lưu sẽ bị mất.");

        ButtonType buttonTypeYes = new ButtonType("Có");
        ButtonType buttonTypeNo = new ButtonType("Không");
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        // Hiển thị hộp thoại và chờ phản hồi từ người dùng
        return alert.showAndWait().filter(response -> response == buttonTypeYes).isPresent();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
