package com.study.library.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class BookStat {
    private final SimpleStringProperty title;
    private final SimpleIntegerProperty borrowCount;

    public BookStat(String title, int borrowCount) {
        this.title = new SimpleStringProperty(title);
        this.borrowCount = new SimpleIntegerProperty(borrowCount);
    }

    public String getTitle() {
        return title.get();
    }

    public int getBorrowCount() {
        return borrowCount.get();
    }
}