package makar.dev.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import makar.dev.common.exception.GeneralException;
import makar.dev.common.status.ErrorStatus;
import makar.dev.domain.data.OdsayStation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

@Component
public class OdsayClient {
    @Value("${odsay.api-key}")
    private String apiKey;


    //대중교통 정류장 찾기 api호출
    public String searchStation(String stationName) throws IOException {
        System.out.println("apiKey : "+apiKey);
        if (apiKey == null || apiKey.isEmpty()) {
            throw new GeneralException(ErrorStatus.INVALID_API_KEY);
        }

        StringBuilder urlBuilder = new StringBuilder("https://api.odsay.com/v1/api/searchStation");

        urlBuilder.append("?lang=" + URLEncoder.encode("0", "UTF-8"));
        urlBuilder.append("&stationName=" + URLEncoder.encode(stationName, "UTF-8"));
        urlBuilder.append("&stationClass=" + URLEncoder.encode("2", "UTF-8")); //2:지하철
        urlBuilder.append("&apiKey=" + URLEncoder.encode(apiKey, "UTF-8"));

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

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
    }

    public List<OdsayStation.Station> parseStationDataResponse(String jsonResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            OdsayStation result = objectMapper.readValue(jsonResponse, OdsayStation.class);
            return result.getResult().getStation();
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

}
