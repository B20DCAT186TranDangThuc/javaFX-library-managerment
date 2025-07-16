package com.study.library.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookLoan {
    private int id;
    private User user;
    private Book book;
    private String borrowDate; // Dùng String hoặc Date tùy nhu cầu
    private String returnDate;
    private boolean returned;
}
