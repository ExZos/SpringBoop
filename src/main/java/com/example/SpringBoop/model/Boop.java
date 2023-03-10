package com.example.SpringBoop.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Boop {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    private String description;
    private LocalDateTime lastModified;

    public Boop() { }
    public Boop(String description) {
        this.description = description;
    }

    public Boop(String description, LocalDateTime lastModified) {
        this.description = description;
        this.lastModified = lastModified;
    }

    public Integer getId() { return id; }
    public String getDescription() { return description; }
    public LocalDateTime getLastModified() { return lastModified; }

    public void setId(Integer id) { this.id = id; }
    public void setDescription(String description) { this.description = description; }
    public void setLastModified(LocalDateTime lastModified) { this.lastModified = lastModified; }

    public String toString() {
        return String.format("%s %s %s", id, description, lastModified);
    }
}
