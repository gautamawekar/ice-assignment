package com.ice.service;

import com.ice.entity.ArtistProfile;
import com.ice.entity.ArtistTrack;
import com.ice.exception.InvalidIdException;
import com.ice.model.Pagination;
import com.ice.model.Track;
import com.ice.model.TracksRequest;
import com.ice.model.TracksResponse;
import com.ice.repository.ArtistProfileRepository;
import com.ice.repository.ArtistTrackRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArtistTrackService {
    private final ArtistProfileRepository profileRepository;
    private final ArtistTrackRepository trackRepository;

    public ArtistTrackService(ArtistProfileRepository profileRepository, ArtistTrackRepository trackRepository) {
        this.profileRepository = profileRepository;
        this.trackRepository = trackRepository;
    }

    public int addTracks(long artistId, TracksRequest tracksRequest) {
        ArtistProfile artistProfile = this.profileRepository.findById(artistId).orElseThrow(() -> new InvalidIdException("2001", "Invalid Artist Id"));
        List<ArtistTrack> tracks = tracksRequest.tracks().stream()
                .map(track -> new ArtistTrack(artistProfile, track.title(), track.genre(), Integer.valueOf(track.length())))
                .toList();
        return this.trackRepository.saveAll(tracks).size();
    }

    public TracksResponse findTracksByArtistName(String artistName, int pageNumber, int size) {
        var pageDetails = PageRequest.of(pageNumber - 1, size, Sort.by("createdAt").descending());
        Page<ArtistTrack> page = this.trackRepository.findByArtist_ArtistName(artistName, pageDetails);
        List<Track> tracks = page.getContent().stream()
                .map(t -> new Track(t.getTitle(), t.getGenre(), t.getLength()))
                .toList();
        return new TracksResponse(tracks, new Pagination(pageNumber, size, page.getTotalElements(), page.getTotalPages()));
    }
}
