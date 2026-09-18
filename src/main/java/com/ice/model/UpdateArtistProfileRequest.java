package com.ice.model;

public record UpdateArtistProfileRequest(long artistId, String updatedName) {
}
