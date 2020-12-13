package com.example.bookfinder.model.login;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "subscribed_authors")
public class BookDTO {
    @Id
    private String bookId;
    @ElementCollection(targetClass = String.class)
    private List<String> authors;

    public BookDTO(String bookId, List<String> authors) {
        this.bookId = bookId;
        this.authors = authors;
    }

    public BookDTO() {

    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }


    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }
}
