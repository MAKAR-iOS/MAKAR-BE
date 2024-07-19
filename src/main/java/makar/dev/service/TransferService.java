package makar.dev.service;

import lombok.RequiredArgsConstructor;
import makar.dev.converter.TransferConverter;
import makar.dev.domain.SubRoute;
import makar.dev.domain.Transfer;
import makar.dev.repository.TransferRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final TransferRepository transferRepository;
    private static final int DEFAULT_TRANSFER_TIME = 4; //기본 환승 소요시간

    public CompletableFuture<Transfer> searchTransferInfoAsync(SubRoute currentSubRoute, SubRoute nextSubRoute) {
        int fromStationID = currentSubRoute.getToStationCode();
        int toStationID = nextSubRoute.getFromStationCode();
        CompletableFuture<Transfer> future = new CompletableFuture<>();

        List<Transfer> transferList = transferRepository.findByFromStationIdAndToStationId(fromStationID, toStationID);
        Transfer transfer;

        if (transferList.isEmpty()){
            // 조회결과가 없을 경우 기본 환승소요시간으로 생성
            transfer = TransferConverter.toTransfer(currentSubRoute.getToStationName(), currentSubRoute.getLineNum(),
                    currentSubRoute.getToStationCode(), nextSubRoute.getLineNum(), nextSubRoute.getFromStationCode(), DEFAULT_TRANSFER_TIME);
        } else {
            transfer = transferList.get(0);
        }

        future.complete(transfer);
        return future;
    }

}
