package com.ice.service;

import com.ice.entity.ArtistProfile;
import com.ice.entity.ArtistTrack;
import com.ice.model.Track;
import com.ice.model.TracksRequest;
import com.ice.model.TracksResponse;
import com.ice.repository.ArtistProfileRepository;
import com.ice.repository.ArtistTrackRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ArtistTrackServiceTest {

    @Test
    void addTracksPersistsNewTracksForArtist() {
        //Given:
        ArtistTrackRepository trackRepo = mock(ArtistTrackRepository.class);
        ArtistProfileRepository profileRepo = mock(ArtistProfileRepository.class);
        ArtistTrackService service = new ArtistTrackService(profileRepo, trackRepo);

        ArtistProfile profile = new ArtistProfile();
        profile.setArtistId(1L);
        profile.setArtistName("Finder Artist");

        TracksRequest request = new TracksRequest(List.of(
                new Track("Song One", "Pop", 180),
                new Track("Song Two", "Rock", 210)
        ));

        when(profileRepo.findById(1L)).thenReturn(Optional.of(profile));
        when(trackRepo.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        //when:
        int savedTrackCount = service.addTracks(1L, request);

        //Then:
        ArgumentCaptor<List<ArtistTrack>> captor = ArgumentCaptor.forClass(List.class);
        verify(trackRepo).saveAll(captor.capture());

        assertThat(savedTrackCount).isEqualTo(2);
        assertThat(captor.getValue())
                .extracting(ArtistTrack::getArtist)
                .allSatisfy(artist -> assertThat(artist.getArtistId()).isEqualTo(1L));
        assertThat(captor.getValue())
                .extracting(ArtistTrack::getTitle)
                .containsExactly("Song One", "Song Two");
    }

    @Test
    void findTracksByArtistNameDelegatesToRepository() {
        //Given:
        ArtistTrackRepository trackRepo = mock(ArtistTrackRepository.class);
        ArtistProfileRepository profileRepo = mock(ArtistProfileRepository.class);
        ArtistTrackService service = new ArtistTrackService(profileRepo, trackRepo);

        ArtistProfile profile = new ArtistProfile();
        profile.setArtistId(1L);
        profile.setArtistName("Finder Artist");

        ArtistTrack track = new ArtistTrack();
        track.setTrackId(100L);
        track.setArtist(profile);
        track.setTitle("Found Song");
        track.setGenre("Pop");
        track.setLength(180);
        track.setCreatedAt(LocalDateTime.now());

        when(trackRepo.findByArtist_ArtistName(eq("Finder Artist"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(track)));

        //When:
        TracksResponse result = service.findTracksByArtistName("Finder Artist", 1, 5);

        //Then:
        assertThat(result.tracks()).hasSize(1);
        assertThat(result.tracks().getFirst().title()).isEqualTo("Found Song");
        assertThat(result.pagination().totalElements()).isEqualTo(1);

    }
}
