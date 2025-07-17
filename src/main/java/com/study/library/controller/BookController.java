package com.study.library.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class BookController implements Initializable {
    @FXML
    private AnchorPane headerPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HeaderController headerController = (HeaderController) headerPane.getUserData();
        headerController.setPageTitle("DANH SÁCH SÁCH");
        headerController.setIcon("fas-book");
        headerController.setIconColor("#3498db");
    }
}
