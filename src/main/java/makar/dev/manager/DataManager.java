package makar.dev.manager;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import makar.dev.common.exception.GeneralException;
import makar.dev.common.status.ErrorStatus;
import makar.dev.converter.LineConverter;
import makar.dev.converter.StationConverter;
import makar.dev.converter.TransferConverter;
import makar.dev.domain.LineMap;
import makar.dev.domain.LineStation;
import makar.dev.domain.Station;
import makar.dev.domain.Transfer;
import makar.dev.domain.data.OdsayStation;
import makar.dev.repository.LineMapRepository;
import makar.dev.repository.StationRepository;
import makar.dev.repository.TransferRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class DataManager {
    private final StationRepository stationRepository;
    private final LineMapRepository lineMapRepository;
    private final TransferRepository transferRepository;
    private final APIManager apiManager;

    // station information save in database
    public void readAndSaveStationInfo() {
        try {
            Sheet sheet = readExcelFile("assets/stations_info.xlsx");

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    String stationName = row.getCell(5).getStringCellValue();
                    String stationCode = row.getCell(4).getStringCellValue();
                    String lineNum = row.getCell(3).getStringCellValue();
                    String railOpr = row.getCell(0).getStringCellValue();

                    Station station = StationConverter.toStation(stationName, stationCode, lineNum, railOpr);
                    stationRepository.save(station);
                }
            }
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.FAILURE_STATION_DATA_INIT);
        }
    }

    // call odsay api and save odsay station information
    public void readUniqueStationNameAndSearchStation() {
        try {
            Sheet sheet = readExcelFile("assets/unique_clean_stations_name.xlsx");

            for (int i = 0; i <= 446; i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    String stationName = row.getCell(0).getStringCellValue();
                    List<OdsayStation.Station> stations = apiManager.searchStation(stationName);

                    if (stations.isEmpty()) {
                        continue;
                    }
                    updateOdsayStationData(stations);
                }
            }
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.FAILURE_STATION_DATA_INIT);
        }
    }


    private void updateOdsayStationData(List<OdsayStation.Station> stations) {
        for (OdsayStation.Station odsayStation : stations) {
            String stationName = odsayStation.getStationName();
            int stationType = odsayStation.getType();
            String lineNum = mapOdsayStationTypeToLineNum(stationType);

            if (lineNum == null) {
                continue;
            }

            Station dbStation = findStationByStationNameAndLineNum(stationName, lineNum);
            updateOdsayStationDataInEntity(dbStation, odsayStation);
            stationRepository.save(dbStation);
        }
    }

    private void updateOdsayStationDataInEntity(Station station, OdsayStation.Station odsayStation) {
        station.setOdsayStationID(odsayStation.getStationID());
        station.setX(odsayStation.getX());
        station.setY(odsayStation.getY());
        station.setOdsayLaneType(odsayStation.getType());
        System.out.println("station : " + station.toString());
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


    // parse line map information and save
    public void readExcelFileAndSaveLineMap(int code) {
        try {
            Sheet sheet = readExcelFile("assets/linemap.xlsx");

            // 노선도의 역 이름 리스트 추출
            String lineOrder = parseLineMapOrder(sheet, code);
            List<String> lineStationNamelist = extractStationNamesNot(lineOrder);
            System.out.println(lineStationNamelist);

            if (code >= 10)
                code = code / 10;

            // odsay 역 이름으로 mapping 후 LineStation entity 생성
            List<LineStation> upLineStationList = mapOdsayStationNameWithLine(lineStationNamelist, code);

            // 호선 정보 저장
            LineMap lineMap = LineConverter.toLineMap(code, upLineStationList.get(0).getStationName(), upLineStationList);
            lineMapRepository.save(lineMap);

        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.FAILURE_LINEMAP_DATA_INIT);
        }
    }

    private String parseLineMapOrder(Sheet sheet, int code) {
        int rowIndex =
                switch (code) {
                    case 10 -> 37; // 신창행
                    case 11 -> 38; // 인천행
                    case 20 -> 12; // 까치산행
                    case 21 -> 10; // 성수외선행
                    case 22 -> 11; // 신설동행
                    case 3 -> 39;
                    case 4 -> 40;
                    case 50 -> 34; // 마천행
                    case 51 -> 15; // 하남검단산행
                    case 6 -> 16;
                    case 7 -> 41;
                    case 8 -> 18;
                    case 9 -> 42;
                    default -> 0;
                };

        Row row = sheet.getRow(rowIndex);
        return row.getCell(4).getStringCellValue();
    }

    private List<String> extractStationNamesNot(String input) {
        List<String> stationNames = new ArrayList<>();

        // 쉼표(,)를 기준으로 문자열을 나눔
        String[] stationsArray = input.split(",");

        for (String station : stationsArray) {
            // "-역"을 제거하여 역이름만 추출
            String stationName = station.split("-")[1].replace("역", "");
            String cleanStationName = extractTextWithinParentheses(stationName);
            stationNames.add(cleanStationName);
        }
        return stationNames;
    }

    private List<LineStation> mapOdsayStationNameWithLine(List<String> list, int odsayLineType) {
        List<LineStation> orderedStations = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            // odsay 역 이름 리스트 검색
            String stationName = doubleCheckStationName(list.get(i));
            Station station = findStationByStationNameAndOdsayLaneType(stationName, odsayLineType);

            // TODO: get odsayStationName, put data in line station entity
            // ""호선의 ""역의 odsay 역 이름, odsayStationId 저장
            LineStation lineStation = LineConverter.toLineStation(station.getOdsayStationID(), station.getStationName());
            orderedStations.add(lineStation);
        }
        // 하행 리스트를 상행 리스트로 변경
        Collections.reverse(orderedStations);

        return orderedStations;
    }

    private String doubleCheckStationName(String stationName) {
        // '역'이 빠진 지하철 이름 수정
        if (stationName.equals("서울"))
            return "서울역";
        if (stationName.equals("곡"))
            return "역곡";
        if (stationName.equals("삼"))
            return "역삼";
        if (stationName.equals("동대문사문화공원"))
            return "동대문역사문화공원";
        if (stationName.equals("촌"))
            return "역촌";
        return stationName;
    }

    // parse transfer information and save
    public void readAndSaveTransferInfo() {
        try {
            Sheet sheet = readExcelFile("assets/transfer_info.xlsx");

            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                // parse transfer information
                String stationName = parseTransferStationName(row);
                int fromLineNum = (int) row.getCell(1).getNumericCellValue();
                int toLineNum = parseTransferToLineNum(row);
                int transferTime = parseTransferTime(row);

                if (toLineNum == 0)
                    continue;

                // get odsay Station Id
                System.out.println("StationName : "+stationName+fromLineNum+"->"+toLineNum);
                int fromStationId = getOdsayStationId(stationName, fromLineNum);
                int toStationId = getOdsayStationId(stationName, toLineNum);

                Transfer transfer = TransferConverter.toTransfer(stationName, fromLineNum, fromStationId, toLineNum, toStationId, transferTime);
                transferRepository.save(transfer);
                System.out.println(transfer.toString());
            }
        } catch(Exception e) {
            throw new GeneralException(ErrorStatus.FAILURE_TRANSFER_DATA_INIT);
        }
    }

    private String parseTransferStationName(Row row){
        String stationName = row.getCell(2).getStringCellValue();
        return stationName;
    }

    private int parseTransferToLineNum(Row row){
        String toLine = row.getCell(3).getStringCellValue();
        if (toLine.equals("경의중앙선"))
            return 104;
        if (toLine.equals("공항철도"))
            return 101;

        String[] parts = toLine.split("호선");
        try {
            return Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int parseTransferTime(Row row){
        String time = row.getCell(5).getStringCellValue();
        String[] parts = time.split("분");
        return Integer.parseInt(parts[0]) + 1;
    }

    private int getOdsayStationId(String stationName, int odsayLaneType){
        if (stationName.equals("이수") && odsayLaneType == 4)
            stationName = "총신대입구";
        if (stationName.equals("총신대입구") && odsayLaneType == 7)
            stationName = "이수";

        Station station = findStationByStationNameAndOdsayLaneType(stationName, odsayLaneType);
        return station.getOdsayStationID();
    }

    public void saveReverseTransferInfo() {
        List<Transfer> transferList = transferRepository.findAll();

        for (Transfer transfer : transferList) {
            int fromLineNum = transfer.getFromLineNum();
            int toLineNum = transfer.getToLineNum();
            String stationName = transfer.getOdsayStationName();

            // 반대 방향의 환승 정보가 존재하는지 확인
            if (transferRepository.existsByFromLineNumAndToLineNumAndOdsayStationName(toLineNum, fromLineNum, stationName))
                continue;

            // 존재하지 않다면 반대 방향 환승 정보 추가
            int fromStationId = transfer.getFromStationId();
            int toStationId = transfer.getToStationId();
            Transfer oppisiteTransfer = TransferConverter.toTransfer(stationName, toLineNum, toStationId, fromLineNum, fromStationId, transfer.getTransferTime());
            transferRepository.save(oppisiteTransfer);
        }
    }

    private Sheet readExcelFile(String path) {
        try {
            ClassPathResource classPathResource = new ClassPathResource(path);
            InputStream inputStream = classPathResource.getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            return workbook.getSheetAt(0);
        } catch (Exception e){
            throw new GeneralException(ErrorStatus.FAILURE_READ_EXCEL_FILE);
        }
    }

    private Station findStationByStationNameAndOdsayLaneType(String stationName, int odsayLaneType){
        return stationRepository.findByStationNameAndOdsayLaneType(stationName, odsayLaneType)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND_STATION));
    }

    private Station findStationByStationNameAndLineNum(String stationName, String lineNum){
        return stationRepository.findByStationNameAndLineNum(stationName, lineNum)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND_STATION));
    }
}
