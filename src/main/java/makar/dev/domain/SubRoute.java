package makar.dev.domain;

import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class SubRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_route_id")
    private Long subRouteId;

    @Column(nullable = false)
    private String fromStationName; // 출발역

    @Column(nullable = false)
    private String toStationName; // 도착역

    @Column(nullable = false)
    private int fromStationCode;

    @Column(nullable = false)
    private int toStationCode;

    @Column(nullable = false)
    private int lineNum;

    @Column(nullable = false)
    private int wayCode;

    @Column(nullable = false)
    private int sectionTime;

    private int transferTime; // 해당 루트 이후 환승시 소요 시간

    public void setTransferTime(int transferTime){
        this.transferTime = transferTime;
    }
}
