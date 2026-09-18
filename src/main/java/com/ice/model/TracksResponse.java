package com.ice.model;

import java.util.List;

public record TracksResponse(List<Track> tracks, Pagination pagination) {
}
