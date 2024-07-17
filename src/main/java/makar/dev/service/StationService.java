package makar.dev.service;

import lombok.RequiredArgsConstructor;
import makar.dev.converter.DataConverter;
import makar.dev.repository.StationRepository;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class StationService {
    private final StationRepository stationRepository;

    public void initDatabase(){
        // init database
        DataConverter databaseConverter = new DataConverter(stationRepository);
        databaseConverter.readExcelFileAndSave();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
                databaseConverter.readUniqueStationNameAndSearchStation();
//                databaseConverter.addCleanStationNameAtDB();
//                databaseConverter.validateOdsayStationsDataFromDB();
//                databaseConverter.modifyOdsayStationData();
//                databaseConverter.updateStationsCollection();
//                databaseConverter.readExcelFileAndSaveLineMap(2);
//                databaseConverter.copyField("1", "2", "1신창");
//                databaseConverter.copyFieldToAnotherDocument("1", "1", 5, "5하남검단산");
//                databaseConverter.saveNewLine(1, "1", "2", "1신창");
//                databaseConverter.saveReverseTransferInfo();
//                databaseConverter.validateTransferInfo();
//                databaseConverter.validateLineSequences2();
//                databaseConverter.validateLineSequences33("1신창");
//            }
//        }).start();
    }

}
