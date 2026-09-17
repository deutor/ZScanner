package eu.ldaldx.mobile.zscanner;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.*;

public class AppStackIdCommunicationTest {

    private Moshi moshi;
    private JsonAdapter<MainRequestData> requestAdapter;
    private JsonAdapter<MainResponseData> responseAdapter;

    @Before
    public void setUp() {
        moshi = new Moshi.Builder().build();
        requestAdapter = moshi.adapter(MainRequestData.class);
        responseAdapter = moshi.adapter(MainResponseData.class);
    }

    @Test
    public void testMainRequestDataSerialization_WithExpectedAppStackIdAndRequestId() {
        MainRequestData request = new MainRequestData();
        request.setRequest("go");
        request.setSessionID("sess-123");
        request.setUserID("5");
        request.setRequestId("req-abc-789");
        request.setExpectedAppStackId(42);

        HashMap<String, String> lov = new HashMap<>();
        lov.put("fieldA", "val1");
        request.setDataFromLov(lov);

        String json = requestAdapter.toJson(request);

        assertTrue(json.contains("\"requestId\":\"req-abc-789\""));
        assertTrue(json.contains("\"expectedAppStackId\":42"));
        assertTrue(json.contains("\"request\":\"go\""));
        assertTrue(json.contains("\"sessionID\":\"sess-123\""));
    }

    @Test
    public void testMainRequestDataSerialization_NullExpectedAppStackIdForPrepare() {
        MainRequestData request = new MainRequestData();
        request.setRequest("prepare");
        request.setRequestId("req-prep-1");
        request.setExpectedAppStackId(null);

        String json = requestAdapter.toJson(request);

        assertTrue(json.contains("\"requestId\":\"req-prep-1\""));
        assertFalse(json.contains("\"expectedAppStackId\":"));
    }

    @Test
    public void testMainResponseDataDeserialization_WorkflowStageSuccess() throws IOException {
        String json = "{\"requestId\":\"req-101\",\"appStackId\":105,\"stateIncluded\":true,\"staleState\":false,\"action\":[{\"name\":\"clear\",\"type\":\"action\",\"text\":\"Tytuł\"}]}";

        MainResponseData response = responseAdapter.fromJson(json);

        assertNotNull(response);
        assertEquals("req-101", response.getRequestId());
        assertEquals(Integer.valueOf(105), response.getAppStackId());
        assertEquals(Boolean.TRUE, response.getStateIncluded());
        assertEquals(Boolean.FALSE, response.getStaleState());
        assertNotNull(response.getAction());
        assertEquals(1, response.getAction().size());
    }

    @Test
    public void testMainResponseDataDeserialization_RootMenuWithNullAppStackId() throws IOException {
        String json = "{\"requestId\":\"req-menu\",\"appStackId\":null,\"stateIncluded\":true,\"staleState\":false,\"menu\":[{\"action\":\"move_hu\",\"label\":\"Przesunięcie HU\"}]}";

        MainResponseData response = responseAdapter.fromJson(json);

        assertNotNull(response);
        assertEquals("req-menu", response.getRequestId());
        assertNull(response.getAppStackId());
        assertEquals(Boolean.TRUE, response.getStateIncluded());
        assertEquals(Boolean.FALSE, response.getStaleState());
        assertNotNull(response.getMenu());
        assertEquals(1, response.getMenu().size());
    }

    @Test
    public void testMainResponseDataDeserialization_StaleStateResponse() throws IOException {
        String json = "{\"requestId\":\"req-retry\",\"appStackId\":106,\"stateIncluded\":true,\"staleState\":true,\"action\":[{\"name\":\"clear\",\"type\":\"action\",\"text\":\"Bieżący krok\"}]}";

        MainResponseData response = responseAdapter.fromJson(json);

        assertNotNull(response);
        assertEquals("req-retry", response.getRequestId());
        assertEquals(Integer.valueOf(106), response.getAppStackId());
        assertEquals(Boolean.TRUE, response.getStateIncluded());
        assertEquals(Boolean.TRUE, response.getStaleState());
    }

    @Test
    public void testMainResponseDataDeserialization_ErrorResponseStateNotIncluded() throws IOException {
        String json = "{\"requestId\":\"req-err\",\"stateIncluded\":false,\"action\":[{\"name\":\"error\",\"type\":\"action\",\"text\":\"Nieprawidłowa ilość (Kod błędu: ERR123)\"}]}";

        MainResponseData response = responseAdapter.fromJson(json);

        assertNotNull(response);
        assertEquals("req-err", response.getRequestId());
        assertNull(response.getAppStackId());
        assertEquals(Boolean.FALSE, response.getStateIncluded());
        assertNotNull(response.getAction());
        assertEquals(1, response.getAction().size());
        assertEquals("error", response.getAction().get(0).getName());
    }

    @Test
    public void testCurrentAppStackIdPreservationRule() {
        Integer currentAppStackId = 42;

        // Simulate validation error response
        MainResponseData errorResponse = new MainResponseData();
        errorResponse.setStateIncluded(false);
        errorResponse.setAppStackId(null);

        // Verification: currentAppStackId should NOT be updated when stateIncluded is false
        if (Boolean.TRUE.equals(errorResponse.getStateIncluded())) {
            currentAppStackId = errorResponse.getAppStackId();
        }
        assertEquals(Integer.valueOf(42), currentAppStackId);

        // Simulate success response to new stage
        MainResponseData successResponse = new MainResponseData();
        successResponse.setStateIncluded(true);
        successResponse.setAppStackId(43);

        if (Boolean.TRUE.equals(successResponse.getStateIncluded())) {
            currentAppStackId = successResponse.getAppStackId();
        }
        assertEquals(Integer.valueOf(43), currentAppStackId);

        // Simulate back to root menu
        MainResponseData menuResponse = new MainResponseData();
        menuResponse.setStateIncluded(true);
        menuResponse.setAppStackId(null);

        if (Boolean.TRUE.equals(menuResponse.getStateIncluded())) {
            currentAppStackId = menuResponse.getAppStackId();
        }
        assertNull(currentAppStackId);
    }
}
