package makar.dev.service;

import lombok.RequiredArgsConstructor;
import makar.dev.manager.DataManager;
import makar.dev.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final DataManager dataManager;

    public void initDatabase(){
        // parse station information
//        dataManager.readAndSaveStationInfo();
//        dataManager.readUniqueStationNameAndSearchStation();
//        dataManager.addCleanStationNameAtDB();
//
        // parse linemap information
//        dataManager.readExcelFileAndSaveLineMap(10); // 1호선 신창행 노선도 parsing
//        dataManager.readExcelFileAndSaveLineMap(11); // 1호선 인천행 노선도 parsing
//        dataManager.readExcelFileAndSaveLineMap(20); // 2호선 까치산행 노선도 parsing
//        dataManager.readExcelFileAndSaveLineMap(21); // 2호선 성수외선행 노선도 parsing
//        dataManager.readExcelFileAndSaveLineMap(22); // 2호선 신설동행 노선도 parsing
//        dataManager.readExcelFileAndSaveLineMap(3); // 3호선 노선도 parsing
//        dataManager.readExcelFileAndSaveLineMap(4); // 4호선 노선도 parsing
//        dataManager.readExcelFileAndSaveLineMap(50); // 5호선 마천행 노선도 parsing
//        dataManager.readExcelFileAndSaveLineMap(51); // 5호선 하남단산행 노선도 parsing
//        dataManager.readExcelFileAndSaveLineMap(6); // 6호선 노선도 parsing
//        dataManager.readExcelFileAndSaveLineMap(7); // 7호선 노선도 parsing
//        dataManager.readExcelFileAndSaveLineMap(8); // 8호선 노선도 parsing
//        dataManager.readExcelFileAndSaveLineMap(9); // 9호선 노선도 parsing
//
         // parse transfer information
//        dataManager.readAndSaveTransferInfo();
//        dataManager.saveReverseTransferInfo();
    }


}
