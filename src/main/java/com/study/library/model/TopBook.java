package com.study.library.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TopBook {
    private final StringProperty name;
    private final IntegerProperty borrowCount;

    public TopBook(String name, int borrowCount) {
        this.name = new SimpleStringProperty(name);
        this.borrowCount = new SimpleIntegerProperty(borrowCount);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public int getBorrowCount() {
        return borrowCount.get();
    }

    public IntegerProperty borrowCountProperty() {
        return borrowCount;
    }
}