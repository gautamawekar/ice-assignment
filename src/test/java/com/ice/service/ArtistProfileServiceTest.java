package com.ice.service;

import com.ice.entity.ArtistProfile;
import com.ice.exception.InvalidIdException;
import com.ice.model.ArtistProfileRequest;
import com.ice.model.ArtistProfileResponse;
import com.ice.model.UpdateArtistProfileRequest;
import com.ice.repository.ArtistProfileRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ArtistProfileServiceTest {

    @Test
    void saveArtistProfileSavesNewArtist() {
        ArtistProfileRepository profileRepository = mock(ArtistProfileRepository.class);
        ArtistProfileService service = new ArtistProfileService(profileRepository);

        service.saveArtistProfile(new ArtistProfileRequest("New Artist"));

        ArgumentCaptor<ArtistProfile> captor = ArgumentCaptor.forClass(ArtistProfile.class);
        verify(profileRepository).save(captor.capture());
        assertThat(captor.getValue().getArtistName()).isEqualTo("New Artist");
    }

    @Test
    void updateArtistProfileUpdatesExistingArtist() {
        ArtistProfileRepository profileRepository = mock(ArtistProfileRepository.class);
        ArtistProfileService service = new ArtistProfileService(profileRepository);

        ArtistProfile artist = new ArtistProfile("Old Name");
        artist.setArtistId(10L);
        when(profileRepository.findById(10L)).thenReturn(Optional.of(artist));

        service.updateArtistProfile(new UpdateArtistProfileRequest(10L, "Updated Name"));

        assertThat(artist.getArtistName()).isEqualTo("Updated Name");
        verify(profileRepository).save(artist);
    }

    @Test
    void getArtistOfTheDayReturnsFirstAvailableArtist() {
        ArtistProfileRepository profileRepository = mock(ArtistProfileRepository.class);
        ArtistProfileService service = new ArtistProfileService(profileRepository);

        ArtistProfile artist = new ArtistProfile("Today Artist");
        artist.setArtistId(25L);
        artist.setArtistOfTheDayMarker(false);
        when(profileRepository.findFirstByArtistOfTheDayMarker(false)).thenReturn(Optional.of(artist));

        ArtistProfileResponse response = service.getArtistOfTheDay();

        assertThat(response.artistId()).isEqualTo(25L);
        assertThat(response.artistName()).isEqualTo("Today Artist");
        assertThat(artist.getArtistOfTheDayMarker()).isTrue();
    }

    @Test
    void updateArtistProfileThrowsForInvalidArtistId() {
        ArtistProfileRepository profileRepository = mock(ArtistProfileRepository.class);
        ArtistProfileService service = new ArtistProfileService(profileRepository);

        when(profileRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateArtistProfile(new UpdateArtistProfileRequest(99L, "New Name")))
                .isInstanceOf(InvalidIdException.class)
                .hasMessageContaining("Invalid Artist Id");
    }
}