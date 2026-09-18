package com.ice.repository;

import com.ice.entity.ArtistTrack;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistTrackRepository extends JpaRepository<ArtistTrack, Long> {
    Page<ArtistTrack> findByArtist_ArtistName(String artistName, Pageable pageable);
}
