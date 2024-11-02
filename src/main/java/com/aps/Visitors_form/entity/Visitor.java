package com.aps.Visitors_form.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Role is required")
    private String role;
    @NotBlank(message = "Purpose of visit is required")
    private String purpose;
    private LocalDateTime visitDateTime;
    @Lob // Use Large Object to store image data
    private byte[] photo;
    @Transient
    private String photoBase64;

    // Constructors
    public Visitor() {}

    public Visitor(String name, String role, String purpose ,LocalDateTime visitDateTime,byte[] photo) {
        this.name = name;
        this.role = role;
        this.purpose = purpose;
        this.visitDateTime = visitDateTime;
        this.photo = photo;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public LocalDateTime getVisitDateTime() {
        return visitDateTime;
    }

    public void setVisitDateTime(LocalDateTime visitDateTime) {
        this.visitDateTime = visitDateTime;
    }

    // Getters and setters for all fields including `photo`
    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getPhotoBase64() {
        return photoBase64;
    }

    public void setPhotoBase64(String photoBase64) {
        this.photoBase64 = photoBase64;
    }
}

