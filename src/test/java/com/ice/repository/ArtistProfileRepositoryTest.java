package com.ice.repository;

import com.ice.entity.ArtistProfile;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ArtistProfileRepositoryTest {

    @Autowired
    private ArtistProfileRepository artistProfileRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void saveAndLoadArtistProfile() {
        ArtistProfile profile = new ArtistProfile("Test Artist");
        ArtistProfile saved = artistProfileRepository.save(profile);

        assertThat(saved.getArtistId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getArtistOfTheDayMarker()).isFalse();

        var loaded = artistProfileRepository.findById(saved.getArtistId());
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getArtistName()).isEqualTo("Test Artist");
    }

    @Test
    void findFirstByArtistOfTheDayMarkerReturnsMatchingProfile() {
        ArtistProfile falseMarkerProfile = new ArtistProfile("Available Artist");
        falseMarkerProfile.setArtistOfTheDayMarker(false);
        artistProfileRepository.save(falseMarkerProfile);

        ArtistProfile trueMarkerProfile = new ArtistProfile("Selected Artist");
        trueMarkerProfile.setArtistOfTheDayMarker(true);
        artistProfileRepository.save(trueMarkerProfile);

        var result = artistProfileRepository.findFirstByArtistOfTheDayMarker(false);

        assertThat(result).isPresent();
        assertThat(result.get().getArtistName()).isEqualTo("Available Artist");
        assertThat(result.get().getArtistOfTheDayMarker()).isFalse();
    }

    @Test
    void resetArtistOfTheDayMarkerResetsAllProfiles() {
        ArtistProfile first = new ArtistProfile("Artist One");
        first.setArtistOfTheDayMarker(true);
        ArtistProfile second = new ArtistProfile("Artist Two");
        second.setArtistOfTheDayMarker(true);
        artistProfileRepository.save(first);
        artistProfileRepository.save(second);

        int updated = artistProfileRepository.resetArtistOfTheDayMarker();

        entityManager.clear();

        assertThat(updated).isGreaterThanOrEqualTo(2);
        assertThat(artistProfileRepository.findFirstByArtistOfTheDayMarker(false)).isPresent();
        assertThat(artistProfileRepository.findAll())
                .allSatisfy(profile -> assertThat(profile.getArtistOfTheDayMarker()).isFalse());
    }
}
