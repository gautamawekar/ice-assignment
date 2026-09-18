package com.ice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ice.exception.ArtistOfTheDayException;
import com.ice.model.ArtistProfileRequest;
import com.ice.model.ArtistProfileResponse;
import com.ice.model.UpdateArtistProfileRequest;
import com.ice.service.ArtistProfileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ArtistProfileController.class, ErrorHandler.class})
class ArtistProfileControllerTest {
    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    ArtistProfileService artistProfileService;

    @Test
    @DisplayName("Create Artist Profile")
    void createArtistDirectControllerCall() throws Exception {
        ArtistProfileRequest request = new ArtistProfileRequest("someName");
        mockMvc.perform(
                post("/artists").contentType(MediaType.APPLICATION_JSON)                 // 2. Set request header
                        .content(OBJECT_MAPPER.writeValueAsString(request)
                        )).andExpect(status().isCreated());
        verify(artistProfileService, times(1)).saveArtistProfile(any(ArtistProfileRequest.class));
    }

    @ParameterizedTest(name = "Invalid Artist Name during creation. Invalid value: {0} ")
    @ValueSource(strings = {"", "   "})
    @NullSource
    void missingArtistName(String artistName) throws Exception {
        String expectedError = """
                {
                  "errors": [
                    {
                      "code": "1001",
                      "message": "Artist Name cannot be empty"
                    }
                  ]
                }
                """;
        ArtistProfileRequest request = new ArtistProfileRequest(artistName);
        mockMvc.perform(
                        post("/artists").contentType(MediaType.APPLICATION_JSON)                 // 2. Set request header
                                .content(OBJECT_MAPPER.writeValueAsString(request)
                                )).andExpect(status().isBadRequest())
                .andExpect(content().json(expectedError));
        verify(artistProfileService, never()).saveArtistProfile(any(ArtistProfileRequest.class));
    }

    @Test
    @DisplayName("Update Artist Name")
    void updateArtistDirectControllerCall() throws Exception {
        UpdateArtistProfileRequest request = new UpdateArtistProfileRequest(1, "someName");
        mockMvc.perform(
                put("/artists").contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(request)
                        )).andExpect(status().is2xxSuccessful());
        verify(artistProfileService, times(1)).updateArtistProfile(any(UpdateArtistProfileRequest.class));
    }

    @ParameterizedTest(name = "Invalid Artist Name during creation. Invalid value: {0} ")
    @ValueSource(strings = {"", "   "})
    @NullSource
    void missingArtistNameDuringUpdate(String artistName) throws Exception {
        String expectedError = """
                {
                  "errors": [
                    {
                      "code": "1002",
                      "message": "Updated Artist Name cannot be empty"
                    }
                  ]
                }
                """;
        UpdateArtistProfileRequest request = new UpdateArtistProfileRequest(1, artistName);
        mockMvc.perform(
                        put("/artists").contentType(MediaType.APPLICATION_JSON)
                                .content(OBJECT_MAPPER.writeValueAsString(request)
                                )).andExpect(status().isBadRequest())
                .andExpect(content().json(expectedError));
        verify(artistProfileService, never()).updateArtistProfile(any(UpdateArtistProfileRequest.class));
    }

    @Test
    @DisplayName("Find Artist of the day")
    void findArtistOfTheDay() throws Exception {
        String expectedResponse = """
                {
                    "artistId" : 1,
                    "artistName" : "gautam"
                }
                """;
        when(artistProfileService.getArtistOfTheDay()).thenReturn(new ArtistProfileResponse(1L, "gautam"));
        mockMvc.perform(get("/artists/of-the-day"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

    }

    @Test
    @DisplayName("Find Artist of the day not found")
    void findArtistOfTheDayNotFound() throws Exception {
        String expectedResponse = """
                {
                   "errors": [
                     {
                       "code": "4001",
                       "message": "Unable to update AristOfTheDay marker"
                     }
                   ]
                 }
                """;
        when(artistProfileService.getArtistOfTheDay())
                .thenThrow(new ArtistOfTheDayException("4001", "Unable to update AristOfTheDay marker"));
        mockMvc.perform(get("/artists/of-the-day"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(expectedResponse));

    }
}
