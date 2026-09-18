package com.ice.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "artist_profiles")
public class ArtistProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_id")
    private Long artistId;

    @Column(name = "artist_name", nullable = false)
    private String artistName;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "artist_of_the_day_marker", nullable = false)
    private Boolean artistOfTheDayMarker = false;

    public ArtistProfile() {
    }

    public ArtistProfile(String artistName) {
        this.artistName = artistName;
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.artistOfTheDayMarker == null) {
            this.artistOfTheDayMarker = false;
        }
    }

    public Long getArtistId() {
        return artistId;
    }

    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getArtistOfTheDayMarker() {
        return artistOfTheDayMarker;
    }

    public void setArtistOfTheDayMarker(Boolean artistOfTheDayMarker) {
        this.artistOfTheDayMarker = artistOfTheDayMarker;
    }
}
