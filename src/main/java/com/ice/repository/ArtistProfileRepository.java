package com.ice.repository;

import com.ice.entity.ArtistProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArtistProfileRepository extends JpaRepository<ArtistProfile, Long> {
    Optional<ArtistProfile> findFirstByArtistOfTheDayMarker(boolean value);

    @Modifying
    @Query("UPDATE ArtistProfile p SET p.artistOfTheDayMarker = false")
    int resetArtistOfTheDayMarker();
}
