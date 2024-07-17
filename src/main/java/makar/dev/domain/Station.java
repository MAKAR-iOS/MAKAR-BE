package makar.dev.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Entity
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "station_id")
    private Long stationId;

    public Station(String stationName, String stationCode, String lineNum, String railOpr) {
        this.stationName = stationName;
        this.stationCode = stationCode;
        this.lineNum = lineNum;
        this.railOpr = railOpr;
    }

    @Column(nullable = false)
    private String stationName;

    @Column(nullable = false)
    private String stationCode;

    @Column(nullable = false)
    private String lineNum;

    @Column(nullable = false)
    private String railOpr;

    @Column(nullable = false)
    private int odsayStationID;

    @Column(nullable = false)
    private double x;

    @Column(nullable = false)
    private double y;

    @Column(nullable = false)
    private int odsayLaneType;

//    @ManyToOne
//    @JoinColumn(name="transfer_id", nullable = false)
//    private Transfer transfer;
}
