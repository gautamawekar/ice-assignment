package com.ice.service;

import com.ice.entity.ArtistProfile;
import com.ice.exception.ArtistOfTheDayException;
import com.ice.exception.InvalidIdException;
import com.ice.model.ArtistProfileRequest;
import com.ice.model.ArtistProfileResponse;
import com.ice.model.UpdateArtistProfileRequest;
import com.ice.repository.ArtistProfileRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    @DisplayName("Reset Artists 'ArtistOfTheDay' marker when all artists have exhausted showing up")
    void resetArtistsUponExhaustingAllArtists() {
        ArtistProfileRepository profileRepository = mock(ArtistProfileRepository.class);
        ArtistProfileService service = new ArtistProfileService(profileRepository);

        ArtistProfile artist = new ArtistProfile("Today Artist");
        artist.setArtistId(25L);
        artist.setArtistOfTheDayMarker(false);
        when(profileRepository.findFirstByArtistOfTheDayMarker(false))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(artist));
        when(profileRepository.resetArtistOfTheDayMarker()).thenReturn(5);

        ArtistProfileResponse response = service.getArtistOfTheDay();

        assertThat(response.artistId()).isEqualTo(25L);
        assertThat(response.artistName()).isEqualTo("Today Artist");
        assertThat(artist.getArtistOfTheDayMarker()).isTrue();
        verify(profileRepository, times(1)).resetArtistOfTheDayMarker();
    }

    @Test
    @DisplayName("Upon failure to reset 'ArtistOfTheDay' marker throw error")
    void uponFailureToResetArtistOfTheDayThrowError() {
        ArtistProfileRepository profileRepository = mock(ArtistProfileRepository.class);
        ArtistProfileService service = new ArtistProfileService(profileRepository);
        when(profileRepository.findFirstByArtistOfTheDayMarker(false))
                .thenReturn(Optional.empty());
        when(profileRepository.resetArtistOfTheDayMarker()).thenReturn(0);

        //Then:
        var e = assertThrows(ArtistOfTheDayException.class, service::getArtistOfTheDay);

        assertEquals("4001", e.code());
        assertEquals("Unable to update AristOfTheDay marker", e.getMessage());

    }

    @Test
    @DisplayName("Upon unable to fetch ArtistOfTheDay throw error")
    void unableToFetchArtistOfTheDay() {
        ArtistProfileRepository profileRepository = mock(ArtistProfileRepository.class);
        ArtistProfileService service = new ArtistProfileService(profileRepository);
        when(profileRepository.findFirstByArtistOfTheDayMarker(false))
                .thenReturn(Optional.empty());
        when(profileRepository.resetArtistOfTheDayMarker()).thenReturn(1);

        //Then:
        var e = assertThrows(ArtistOfTheDayException.class, service::getArtistOfTheDay);

        assertEquals("4002", e.code());
        assertEquals("Unable to fetch ArtistOfTheDay", e.getMessage());

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