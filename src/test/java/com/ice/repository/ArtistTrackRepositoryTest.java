package com.ice.repository;

import com.ice.entity.ArtistProfile;
import com.ice.entity.ArtistTrack;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ArtistTrackRepositoryTest {

    @Autowired
    private ArtistProfileRepository artistProfileRepository;

    @Autowired
    private ArtistTrackRepository artistTrackRepository;

    @Test
    void saveTrackWithArtist() {
        ArtistProfile profile = new ArtistProfile("Track Artist");
        ArtistProfile savedProfile = artistProfileRepository.save(profile);

        ArtistTrack track = new ArtistTrack(savedProfile, "Title 1", "Rock", 210);
        ArtistTrack savedTrack = artistTrackRepository.save(track);

        assertThat(savedTrack.getTrackId()).isNotNull();
        assertThat(savedTrack.getCreatedAt()).isNotNull();
        assertThat(savedTrack.getArtist().getArtistId()).isEqualTo(savedProfile.getArtistId());
    }

    @Test
    void findByArtist_ArtistNameReturnsMatchingTracksPage() {
        ArtistProfile profile = new ArtistProfile("Artist Search");
        ArtistProfile savedProfile = artistProfileRepository.save(profile);

        artistTrackRepository.save(new ArtistTrack(savedProfile, "First Song", "Rock", 180));
        artistTrackRepository.save(new ArtistTrack(savedProfile, "Second Song", "Pop", 220));

        Page<ArtistTrack> result = artistTrackRepository.findByArtist_ArtistName("Artist Search", PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting(ArtistTrack::getTitle)
                .containsExactlyInAnyOrder("First Song", "Second Song");
    }
}
