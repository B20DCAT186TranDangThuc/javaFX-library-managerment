package com.study.library.model;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class OverdueBook {
    private final StringProperty user;
    private final StringProperty book;
    private final StringProperty dueDate;

    public OverdueBook(String user, String book, String dueDate) {
        this.user = new SimpleStringProperty(user);
        this.book = new SimpleStringProperty(book);
        this.dueDate = new SimpleStringProperty(dueDate);
    }

    public String getUser() {
        return user.get();
    }

    public StringProperty userProperty() {
        return user;
    }

    public String getBook() {
        return book.get();
    }

    public StringProperty bookProperty() {
        return book;
    }

    public String getDueDate() {
        return dueDate.get();
    }

    public StringProperty dueDateProperty() {
        return dueDate;
    }
}
