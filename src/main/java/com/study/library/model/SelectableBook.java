package com.study.library.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;

import java.time.LocalDate;

public class SelectableBook {
    @Getter
    private Book book;
    private BooleanProperty selected;
    private ObjectProperty<LocalDate> returnDate;

    public SelectableBook(Book book) {
        this.book = book;
        this.selected = new SimpleBooleanProperty(false);
        this.returnDate = new SimpleObjectProperty<>(LocalDate.now().plusDays(7)); // mặc định trả sau 7 ngày
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean value) {
        selected.set(value);
    }

    public ObjectProperty<LocalDate> returnDateProperty() {
        return returnDate;
    }

    public LocalDate getReturnDate() {
        return returnDate.get();
    }

    public void setReturnDate(LocalDate date) {
        returnDate.set(date);
    }
}
