package com.study.library.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    private int id;
    private String title;
    private Author author;
    private Category category;
    private String publisher;
    private int yearPublished;
    private int totalQuantity;
    private int availableQuantity;
    private String description;
    private String location;
    private String createAt;

}
