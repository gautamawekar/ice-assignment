package com.ice.controller;

import com.ice.exception.InvalidInputException;
import com.ice.model.ArtistProfileRequest;
import com.ice.model.ArtistProfileResponse;
import com.ice.model.UpdateArtistProfileRequest;
import com.ice.service.ArtistProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/artists")
public class ArtistProfileController {

    private final ArtistProfileService service;

    public ArtistProfileController(ArtistProfileService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createArtist(@RequestBody ArtistProfileRequest request) {
        if (request.artistName() == null || request.artistName().trim().isEmpty()) {
            throw new InvalidInputException("1001", "Artist Name cannot be empty");
        }
        this.service.saveArtistProfile(request);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void updateArtistName(@RequestBody UpdateArtistProfileRequest request) {
        if (request.updatedName() == null || request.updatedName().trim().isEmpty()) {
            throw new InvalidInputException("1002", "Updated Artist Name cannot be empty");
        }
        this.service.updateArtistProfile(request);
    }

    @GetMapping("/of-the-day")
    public ArtistProfileResponse getArtistOfTheDay() {
        return this.service.getArtistOfTheDay();
    }


}
