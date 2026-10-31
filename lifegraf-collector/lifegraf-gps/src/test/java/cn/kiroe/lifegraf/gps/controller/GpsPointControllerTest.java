package cn.kiroe.lifegraf.gps.controller;

import cn.kiroe.lifegraf.gps.dto.GpsPointInputDTO;
import cn.kiroe.lifegraf.gps.model.GpsPoint;
import cn.kiroe.lifegraf.gps.service.GpsDataService;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GpsPointControllerTest {

    private GpsDataService gpsDataService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        gpsDataService = mock(GpsDataService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new GpsPointController(gpsDataService)).build();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidUploads")
    void rejectsInvalidBatchWithoutSaving(String description, String json, String expectedMessage) throws Exception {
        mockMvc.perform(post("/gps/upload").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(expectedMessage));

        verifyNoInteractions(gpsDataService);
    }

    static Stream<Arguments> invalidUploads() {
        String nonEmptyArray = "GPS points must be a non-empty array";
        String latitude = "GPS point at index 0: latitude is required and must be between -90 and 90";
        String longitude = "GPS point at index 0: longitude is required and must be between -180 and 180";
        String invalidJson = "Invalid GPS upload JSON: expected an array of GPS points";
        return Stream.of(
                Arguments.of("JSON null", "null", nonEmptyArray),
                Arguments.of("empty body", "", nonEmptyArray),
                Arguments.of("empty array", "[]", nonEmptyArray),
                Arguments.of("null element", "[null]", "GPS point at index 0 must not be null"),
                Arguments.of("both coordinates missing", "[{}]", latitude),
                Arguments.of("latitude missing", "[{\"longitude\":0}]", latitude),
                Arguments.of("longitude missing", "[{\"latitude\":0}]", longitude),
                Arguments.of("latitude null", "[{\"latitude\":null,\"longitude\":0}]", latitude),
                Arguments.of("longitude null", "[{\"latitude\":0,\"longitude\":null}]", longitude),
                Arguments.of("latitude below range", "[{\"latitude\":-90.0001,\"longitude\":0}]", latitude),
                Arguments.of("latitude above range", "[{\"latitude\":90.0001,\"longitude\":0}]", latitude),
                Arguments.of("longitude below range", "[{\"latitude\":0,\"longitude\":-180.0001}]", longitude),
                Arguments.of("longitude above range", "[{\"latitude\":0,\"longitude\":180.0001}]", longitude),
                Arguments.of("NaN latitude", "[{\"latitude\":\"NaN\",\"longitude\":0}]", latitude),
                Arguments.of("infinite longitude", "[{\"latitude\":0,\"longitude\":\"Infinity\"}]", longitude),
                Arguments.of("overflowing latitude", "[{\"latitude\":1e400,\"longitude\":0}]", latitude),
                Arguments.of("valid point then invalid point",
                        "[{\"latitude\":1,\"longitude\":2},{\"latitude\":91,\"longitude\":2}]",
                        "GPS point at index 1: latitude is required and must be between -90 and 90"),
                Arguments.of("invalid point then valid point",
                        "[{\"latitude\":1,\"longitude\":181},{\"latitude\":1,\"longitude\":2}]", longitude),
                Arguments.of("valid point then null", "[{\"latitude\":1,\"longitude\":2},null]",
                        "GPS point at index 1 must not be null"),
                Arguments.of("valid point then missing longitude", "[{\"latitude\":1,\"longitude\":2},{\"latitude\":3}]",
                        "GPS point at index 1: longitude is required and must be between -180 and 180"),
                Arguments.of("object rather than array", "{\"latitude\":1,\"longitude\":2}", invalidJson),
                Arguments.of("malformed JSON", "[{", invalidJson),
                Arguments.of("unparseable coordinate is not echoed",
                        "[{\"latitude\":\"private-input-value\",\"longitude\":2}]", invalidJson));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("validUploads")
    void acceptsValidBatchAndPassesEveryPointUnchanged(String description, String json,
                                                       List<GpsPointInputDTO> expectedPoints) throws Exception {
        mockMvc.perform(post("/gps/upload").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("GPS data saved successfully"));

        ArgumentCaptor<List<GpsPointInputDTO>> points = ArgumentCaptor.captor();
        verify(gpsDataService).saveGpsPoints(points.capture());
        assertThat(points.getValue()).containsExactlyElementsOf(expectedPoints);
        verifyNoMoreInteractions(gpsDataService);
    }

    static Stream<Arguments> validUploads() {
        return Stream.of(
                Arguments.of("single point with optional fields omitted", "[{\"latitude\":0,\"longitude\":0}]",
                        List.of(new GpsPointInputDTO(null, 0.0, 0.0, null, null, null))),
                Arguments.of("multiple points including all boundary combinations",
                        """
                        [{"latitude":-90,"longitude":-180},
                         {"latitude":90,"longitude":180},
                         {"latitude":-90,"longitude":180},
                         {"latitude":90,"longitude":-180}]
                        """,
                        List.of(new GpsPointInputDTO(null, -90.0, -180.0, null, null, null),
                                new GpsPointInputDTO(null, 90.0, 180.0, null, null, null),
                                new GpsPointInputDTO(null, -90.0, 180.0, null, null, null),
                                new GpsPointInputDTO(null, 90.0, -180.0, null, null, null))),
                Arguments.of("optional values are preserved without new constraints",
                        """
                        [{"trackId":-1,"latitude":31.2,"longitude":121.5,"altitude":-5,"timestamp":-1,"speed":-2},
                         {"trackId":null,"latitude":12.3,"longitude":45.6,"altitude":null,"timestamp":null,"speed":null}]
                        """,
                        List.of(new GpsPointInputDTO(-1L, 31.2, 121.5, -5.0, -1L, -2.0),
                                new GpsPointInputDTO(null, 12.3, 45.6, null, null, null))));
    }

    @Test
    void keepsPersistenceFailuresAsServerErrors() throws Exception {
        doThrow(new IllegalStateException("storage unavailable")).when(gpsDataService).saveGpsPoints(anyList());

        mockMvc.perform(post("/gps/upload").contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"latitude\":1,\"longitude\":2}]"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error saving GPS data: storage unavailable"));

        verify(gpsDataService).saveGpsPoints(List.of(new GpsPointInputDTO(null, 1.0, 2.0, null, null, null)));
        verifyNoMoreInteractions(gpsDataService);
    }

    @Test
    void keepsTrackQueryCompatible() throws Exception {
        GpsPoint point = new GpsPoint();
        point.setTrackId(42L);
        point.setLatitude(31.2);
        point.setLongitude(121.5);
        when(gpsDataService.getTrackById(42L)).thenReturn(List.of(point));

        mockMvc.perform(get("/gps/track/42"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"trackId\":42,\"latitude\":31.2,\"longitude\":121.5}]"));

        verify(gpsDataService).getTrackById(42L);
        verifyNoMoreInteractions(gpsDataService);
    }
}
