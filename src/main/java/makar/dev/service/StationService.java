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
//        databaseConverter.readExcelFileAndSave();
//        databaseConverter.readUniqueStationNameAndSearchStation();
//        databaseConverter.addCleanStationNameAtDB();

        // parse linemap information
        databaseConverter.readExcelFileAndSaveLineMap(7); // 7호선 노선도 parsing
//        databaseConverter.readExcelFileAndSaveLineMap(2); // 2호선 노선도 parsing

//                databaseConverter.copyField("1", "2", "1신창");
//                databaseConverter.copyFieldToAnotherDocument("1", "1", 5, "5하남검단산");
//                databaseConverter.saveNewLine(1, "1", "2", "1신창");
//                databaseConverter.saveReverseTransferInfo();
//                databaseConverter.validateTransferInfo();
//                databaseConverter.validateLineSequences2();
//                databaseConverter.validateLineSequences33("1신창");
//
    }

}
