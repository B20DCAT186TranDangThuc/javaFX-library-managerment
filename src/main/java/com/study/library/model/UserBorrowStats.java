package com.study.library.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class UserBorrowStats {
    private final SimpleStringProperty userName;
    private final SimpleIntegerProperty borrowCount;

    public UserBorrowStats(String userName, int borrowCount) {
        this.userName = new SimpleStringProperty(userName);
        this.borrowCount = new SimpleIntegerProperty(borrowCount);
    }

    public String getUserName() {
        return userName.get();
    }

    public Integer getBorrowCount() {
        return borrowCount.get();
    }
}