package com.ice.service;

import com.ice.entity.ArtistProfile;
import com.ice.exception.ArtistOfTheDayException;
import com.ice.exception.InvalidIdException;
import com.ice.model.ArtistProfileRequest;
import com.ice.model.ArtistProfileResponse;
import com.ice.model.UpdateArtistProfileRequest;
import com.ice.repository.ArtistProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ArtistProfileService {
    private final ArtistProfileRepository profileRepository;

    public ArtistProfileService(ArtistProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Transactional
    public void saveArtistProfile(ArtistProfileRequest profileRequest) {
        this.profileRepository.save(new ArtistProfile(profileRequest.artistName()));
    }

    @Transactional
    public void updateArtistProfile(UpdateArtistProfileRequest updateProfile) {
        ArtistProfile artistProfile = this.profileRepository.findById(updateProfile.artistId()).orElseThrow(() -> new InvalidIdException("1003", "Invalid Artist Id"));
        artistProfile.setArtistName(updateProfile.updatedName());
        this.profileRepository.save(artistProfile);
    }

    @Transactional
    public ArtistProfileResponse getArtistOfTheDay() {
        Optional<ArtistProfile> artist = this.profileRepository.findFirstByArtistOfTheDayMarker(false);
        if (artist.isPresent()) {
            var entity = artist.get();
            entity.setArtistOfTheDayMarker(true);
            return new ArtistProfileResponse(entity.getArtistId(), entity.getArtistName());
        }
        int updateCount = profileRepository.resetArtistOfTheDayMarker();
        if (updateCount == 0) {
            throw new ArtistOfTheDayException("4001", "Unable to update AristOfTheDay marker");
        }
        var entity = this.profileRepository.findFirstByArtistOfTheDayMarker(false)
                .orElseThrow(() -> new ArtistOfTheDayException("4002", "Unable to fetch ArtistOfTheDay"));
        entity.setArtistOfTheDayMarker(true);
        return new ArtistProfileResponse(entity.getArtistId(), entity.getArtistName());
    }
}
