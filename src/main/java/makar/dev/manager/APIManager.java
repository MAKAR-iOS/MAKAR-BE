package makar.dev.manager;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import makar.dev.common.exception.GeneralException;
import makar.dev.common.status.ErrorStatus;
import makar.dev.domain.data.OdsayStation;
import makar.dev.domain.data.RouteSearchResponse;
import makar.dev.domain.data.SubwaySchedule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class APIManager {
    @Value("${odsay.api-key}")
    private String apiKey;
    private final ObjectMapper objectMapper;

    //대중교통 정류장 찾기 api호출
    public List<OdsayStation.Station> searchStation(String stationName) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new GeneralException(ErrorStatus.INVALID_API_KEY);
        }
        String endpoint = "https://api.odsay.com/v1/api/searchStation";
        Map<String, String> params = new HashMap<>();
        params.put("lang", String.valueOf(0));
        params.put("stationName", String.valueOf(stationName));
        params.put("stationClass", String.valueOf(2));

        try {
            String stationDataResponse = makeApiRequest(endpoint, params);
            return parseStationDataResponse(stationDataResponse);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.FAILURE_API_REQUEST);
        }
    }

    //대중교통 길찾기 api 호출
    public RouteSearchResponse requestRoute(double sourceX, double sourceY, double destinationX, double destinationY) {
        String endpoint = "https://api.odsay.com/v1/api/searchPubTransPathT";
        Map<String, String> params = new HashMap<>();
        params.put("SX", String.valueOf(sourceX));
        params.put("SY", String.valueOf(sourceY));
        params.put("EX", String.valueOf(destinationX));
        params.put("EY", String.valueOf(destinationY));
        params.put("SearchPathType", "1");

        try {
            String routeSearchResponse = makeApiRequest(endpoint, params);
            return parseRouteSearchResponse(routeSearchResponse);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.FAILURE_API_REQUEST);
        }
    }

    //지하철 시간표 api호출
    public SubwaySchedule requestSubwaySchedule(int stationID, int wayCode) {
        String endpoint = "https://api.odsay.com/v1/api/subwayTimeTable";
        Map<String, String> params = new HashMap<>();
        params.put("stationID", String.valueOf(stationID));
        params.put("wayCode", String.valueOf(wayCode));
        params.put("showExpressTime", "1");
        params.put("sepExpressTime", "1");

        try {
            String subwayScheduleResponse = makeApiRequest(endpoint, params);
            return parseSubwayScheduleResponse(subwayScheduleResponse);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.INVALID_REQUEST);
        }
    }

    private String makeApiRequest(String endpoint, Map<String, String> params) {
        try {
            StringBuilder urlBuilder = new StringBuilder(endpoint);
            urlBuilder.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlBuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                        .append("&");
            }
            urlBuilder.append("apiKey=").append(URLEncoder.encode(apiKey, "UTF-8"));

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.FAILURE_API_REQUEST);
        }
    }

    private List<OdsayStation.Station> parseStationDataResponse(String jsonResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            OdsayStation result = objectMapper.readValue(jsonResponse, OdsayStation.class);
            return result.getResult().getStation();
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    private RouteSearchResponse parseRouteSearchResponse(String jsonResponse) throws IOException {
        return objectMapper.readValue(jsonResponse, RouteSearchResponse.class);
    }

    private SubwaySchedule parseSubwayScheduleResponse(String jsonResponse) throws IOException {
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode resultNode = rootNode.path("result");
        return objectMapper.treeToValue(resultNode, SubwaySchedule.class);
    }

}
