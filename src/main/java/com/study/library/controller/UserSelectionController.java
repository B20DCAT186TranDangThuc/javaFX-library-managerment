package com.study.library.controller;

import com.study.library.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import lombok.Setter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class UserSelectionController implements Initializable {
    @FXML
    private TableView<User> tableReaders;
    @FXML
    private TableColumn<User, Integer> colId;
    @FXML
    private TableColumn<User, String> colName;
    @FXML
    private TableColumn<User, String> colEmail;
    @FXML
    private TableColumn<User, Void> colAction;

    @Setter
    private Consumer<User> onUserSelected;

    @Setter
    private Stage dialogStage;

    public void setReaders(List<User> users) {
        ObservableList<User> observableUsers = FXCollections.observableArrayList(users);
        tableReaders.setItems(observableUsers);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btnSelect = new Button("Chọn");

            {
                btnSelect.getStyleClass().add("action-button");
                btnSelect.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleSelectUser(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnSelect);
                }
            }
        });
    }

    public void setUsers(List<User> users) {
        tableReaders.setItems(FXCollections.observableArrayList(users));
    }

    private void handleSelectUser(User user) {
        if (onUserSelected != null) {
            onUserSelected.accept(user); // truyền user về controller cha
        }
        if (dialogStage != null) {
            dialogStage.close(); // đóng dialog
        }
    }

}
