package com.ice.controller;

import com.ice.exception.InvalidInputException;
import com.ice.model.TracksAddResponse;
import com.ice.model.TracksRequest;
import com.ice.model.TracksResponse;
import com.ice.service.ArtistTrackService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tracks")
public class ArtistTrackController {

    private final ArtistTrackService trackService;

    public ArtistTrackController(ArtistTrackService trackService) {
        this.trackService = trackService;
    }

    @PostMapping("/artists/{artist_id}")
    @ResponseStatus(HttpStatus.OK)
    public TracksAddResponse addTracks(@PathVariable("artist_id") long artistId,
                                       @RequestBody TracksRequest request) {
        return new TracksAddResponse(trackService.addTracks(artistId, request));
    }

    @GetMapping("/search")
    public TracksResponse findTracksByArtistName(
            @RequestParam("artistName") String artistName,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        if (artistName == null || artistName.trim().isEmpty()) {
            throw new InvalidInputException("2002", "Invalid Search. Artist Name missing.");
        }
        return trackService.findTracksByArtistName(artistName, page, size);
    }
}
