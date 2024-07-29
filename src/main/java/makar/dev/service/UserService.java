package makar.dev.service;

import lombok.RequiredArgsConstructor;
import makar.dev.common.exception.GeneralException;
import makar.dev.common.security.dto.TokenDto;
import makar.dev.common.status.ErrorStatus;
import makar.dev.converter.UserConverter;
import makar.dev.domain.Noti;
import makar.dev.domain.Route;
import makar.dev.domain.User;
import makar.dev.dto.response.UserResponse;
import makar.dev.manager.DataManager;
import makar.dev.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final DataManager dataManager;

    @Transactional
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

    public Optional<User> getOptionalUserById(String id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User createUser(String id, String password, String email, String username) {
        User user = UserConverter.toUser(id, password, email, username);
        user = userRepository.save(user);

        return user;
    }


    // 홈 화면 조회
    public UserResponse.HomeDto getHome(TokenDto tokenDto) {
        User user = findUserById(tokenDto.getUserId());
        boolean isRouteSet = isRouteSet(user);

        return UserConverter.toHomeDto(isRouteSet, user.getNotiList());
    }

    private User findUserById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOT_FOUND_USER));
    }

    private boolean isRouteSet(User user){
        List<Noti> notiList = user.getNotiList();
        return !notiList.isEmpty();
    }


}
