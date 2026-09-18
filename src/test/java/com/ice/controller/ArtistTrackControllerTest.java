package com.ice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ice.model.Pagination;
import com.ice.model.Track;
import com.ice.model.TracksRequest;
import com.ice.model.TracksResponse;
import com.ice.service.ArtistTrackService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ArtistTrackController.class, ErrorHandler.class})
class ArtistTrackControllerTest {
    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    ArtistTrackService trackService;

    @Test
    @DisplayName("Add tracks to an artist")
    void addTracksToArtist() throws Exception {
        TracksRequest request = new TracksRequest(List.of(
                new Track("A", "POP", 100),
                new Track("B", "Rock", 100),
                new Track("C", "Hip-Hop", 100),
                new Track("D", "Classical", 100)
        ));
        mockMvc.perform(
                post("/tracks/artists/{artist_id}", "1")
                        .contentType(MediaType.APPLICATION_JSON)                 // 2. Set request header
                        .content(OBJECT_MAPPER.writeValueAsString(request)
                        )).andExpect(status().isOk());
        verify(trackService, times(1)).addTracks(eq(1L), any(TracksRequest.class));
    }

    @Test
    @DisplayName("Successful search")
    void successfulSearch() throws Exception {
        when(trackService.findTracksByArtistName(eq("gautam"), eq(1), eq(5))).thenReturn(
                new TracksResponse(List.of(
                        new Track("A", "POP", 100),
                        new Track("B", "Rock", 100),
                        new Track("C", "Hip-Hop", 100)
                ), new Pagination(1, 5, 4, 1))
        );
        String expectedResponse = """
                {
                    "tracks": [
                      {
                        "title": "A",
                        "genre": "POP",
                        "length": 100
                      },
                      {
                        "title": "B",
                        "genre": "Rock",
                        "length": 100
                      },
                      {
                        "title": "C",
                        "genre": "Hip-Hop",
                        "length": 100
                      }
                    ],
                    "pagination": {
                      "page": 1,
                      "size": 5,
                      "totalElements": 4,
                      "totalPages": 1
                    }
                  }
                """;

        mockMvc.perform(get("/tracks/search")
                        .queryParam("artistName", "gautam"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    @DisplayName("Error missing artist name")
    void missingArtistName() throws Exception {
        String expectedError = """
                {
                  "errors": [
                    {
                      "code": "2002",
                      "message": "Invalid Search. Artist Name missing."
                    }
                  ]
                }
                """;
        mockMvc.perform(get("/tracks/search")
                        .queryParam("artistName", ""))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expectedError));
        verify(trackService, times(0)).findTracksByArtistName(anyString(), anyInt(), anyInt());

    }


}