package com.study.library.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class HeaderController implements Initializable {

    @FXML
    private Label lblCurrentTime;
    @FXML
    private Label lblPageTitle;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private FontIcon iconPage;

    private Timer timeTimer;

    /**
     * Bắt đầu đồng hồ thời gian thực
     */
    private void startClock() {
        timeTimer = new Timer();
        timeTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy - HH:mm:ss");
                    lblCurrentTime.setText(now.format(formatter));
                });
            }
        }, 0, 1000); // Cập nhật mỗi giây
    }

    public void setPageTitle(String title) {
        lblPageTitle.setText(title);
    }

    public void setIcon(String iconLiteral) {
        iconPage.setIconLiteral(iconLiteral);
    }

    public void setIconColor(String colorHex) {
        iconPage.setIconColor(Paint.valueOf(colorHex));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rootPane.setUserData(this);
        startClock();
    }
}
