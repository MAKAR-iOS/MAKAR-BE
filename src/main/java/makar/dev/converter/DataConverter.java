package makar.dev.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import makar.dev.common.exception.GeneralException;
import makar.dev.common.status.ErrorStatus;
import makar.dev.domain.Station;
import makar.dev.domain.data.OdsayStation;
import makar.dev.repository.StationRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class DataConverter {
    @Value("${odsay.api.key}")
    private String apiKey;

    private final StationRepository stationRepository;

    // station information save in database
    public void readExcelFileAndSave() {
        try {
            ClassPathResource classPathResource = new ClassPathResource("assets/stations_info.xlsx");
            InputStream inputStream = classPathResource.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    String stationName = row.getCell(5).getStringCellValue();
                    String stationCode = row.getCell(4).getStringCellValue();
                    String lineNum = row.getCell(3).getStringCellValue();
                    String railOpr = row.getCell(0).getStringCellValue();

                    Station station = new Station(stationName, stationCode, lineNum, railOpr);
                    stationRepository.save(station);
                }
            }
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.FAILURE_DATA_INIT);
        }
    }

    // call odsay api and save odsay station information
    public void readUniqueStationNameAndSearchStation() {
        try {
            ClassPathResource classPathResource = new ClassPathResource("assets/unique_clean_stations_name.xlsx");
            InputStream inputStream = classPathResource.getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 0; i <= 50; i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    String stationName = row.getCell(0).getStringCellValue();
                    String result = searchStation(stationName);
                    List<OdsayStation.Station> stations = parseStationDataResponse(result);

                    if (stations.isEmpty()) {
                        continue;
                    }

                    updateOdsayStationData(stations);
                }
            }
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.FAILURE_DATA_INIT);
        }
    }


    //대중교통 정류장 찾기 api호출
    private String searchStation(String stationName) throws IOException {
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

    private List<OdsayStation.Station> parseStationDataResponse(String jsonResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            OdsayStation result = objectMapper.readValue(jsonResponse, OdsayStation.class);
            return result.getResult();
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    private void updateOdsayStationData(List<OdsayStation.Station> stations) {
        for (OdsayStation.Station odsayStation : stations) {
            String stationName = odsayStation.getStationName();
            int stationType = odsayStation.getType();
            String lineNum = mapOdsayStationTypeToLineNum(stationType);

            if (lineNum == null){
                continue;
            }

            List<Station> dbStations = stationRepository.findByStationNameAndLineNum(stationName, lineNum);

            if (dbStations.size() != 1) {
                continue;
            }

            for (Station dbStation : dbStations) {
                updateOdsayStationDataInEntity(dbStation, odsayStation);
                stationRepository.save(dbStation);
            }
        }
    }

    private void updateOdsayStationDataInEntity(Station dbStation, OdsayStation.Station odsayStation) {
        dbStation.setOdsayStationID(odsayStation.getStationID());
        dbStation.setX(odsayStation.getX());
        dbStation.setY(odsayStation.getY());
        dbStation.setOdsayLaneType(odsayStation.getType());
    }

    private String mapOdsayStationTypeToLineNum(int stationType) {
        if (stationType >= 1 && stationType <= 9) {
            return stationType + "호선";
        }
        if (stationType == 101) {
            return "공항철도";
        }
        if (stationType == 104) {
            return "경의중앙";
        }
        return null;
    }


    // station name 괄호 제거
    public void addCleanStationNameAtDB() {
        List<Station> stations = stationRepository.findAll();

        for (Station station : stations) {
            String stationName = station.getStationName();

            // 괄호 제거
            String cleanStationName = extractTextWithinParentheses(stationName);

            // cleanStationName 필드 업데이트
            station.setStationName(cleanStationName);
            stationRepository.save(station);
        }
    }

    private String extractTextWithinParentheses(String input) {
        Pattern pattern = Pattern.compile("(.+?)\\(.+?\\)");
        Matcher matcher = pattern.matcher(input);

        // 매칭된 경우 괄호 앞 부분 반환, 매칭이 안 되면 전체 문자열 반환
        return matcher.find() ? matcher.group(1) : input;
    }
}
