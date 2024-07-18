package makar.dev.service;

import lombok.RequiredArgsConstructor;
import makar.dev.converter.DataConverter;
import makar.dev.repository.StationRepository;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class StationService {
    private final StationRepository stationRepository;
    private final DataConverter databaseConverter;

    public void initDatabase(){
        // parse station information
//        databaseConverter.readAndSaveStationInfo();
//        databaseConverter.readUniqueStationNameAndSearchStation();
//        databaseConverter.addCleanStationNameAtDB();

        // parse linemap information
//        databaseConverter.readExcelFileAndSaveLineMap(10); // 1호선 신창행 노선도 parsing
//        databaseConverter.readExcelFileAndSaveLineMap(11); // 1호선 인천행 노선도 parsing
//        databaseConverter.readExcelFileAndSaveLineMap(20); // 2호선 까치산행 노선도 parsing
//        databaseConverter.readExcelFileAndSaveLineMap(21); // 2호선 성수외선행 노선도 parsing
//        databaseConverter.readExcelFileAndSaveLineMap(22); // 2호선 신설동행 노선도 parsing
//        databaseConverter.readExcelFileAndSaveLineMap(3); // 3호선 노선도 parsing
//        databaseConverter.readExcelFileAndSaveLineMap(4); // 4호선 노선도 parsing
//        databaseConverter.readExcelFileAndSaveLineMap(50); // 5호선 마천행 노선도 parsing
//        databaseConverter.readExcelFileAndSaveLineMap(51); // 5호선 하남단산행 노선도 parsing
//        databaseConverter.readExcelFileAndSaveLineMap(6); // 6호선 노선도 parsing
//        databaseConverter.readExcelFileAndSaveLineMap(7); // 7호선 노선도 parsing
//        databaseConverter.readExcelFileAndSaveLineMap(8); // 8호선 노선도 parsing
//        databaseConverter.readExcelFileAndSaveLineMap(9); // 9호선 노선도 parsing

        // parse transfer information
        databaseConverter.readAndSaveTransferInfo();
        databaseConverter.saveReverseTransferInfo();

    }

}
