package makar.dev.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transfer_id")
    private Long transferId;

    @Column(nullable = false)
    private String odsayStationName;

    @Column(nullable = false)
    private int fromLineNum;

    @Column(nullable = false)
    private int fromStationId;

    @Column(nullable = false)
    private int toLineNum;

    @Column(nullable = false)
    private int toStationId;

    @Column(nullable = false)
    private int transferTime;


    @Override
    public String toString() {
        return "Transfer{" +
                "stationName='" + odsayStationName + '\'' +
                ", toLineNum='" + toLineNum + '\'' +
                ", toStationId='" + toStationId + '\'' +
                ", fromLineNum='" + fromLineNum + '\'' +
                ", fromStationId=" + fromStationId +
                ", transferTime=" + transferTime +
                '}';
    }

}
