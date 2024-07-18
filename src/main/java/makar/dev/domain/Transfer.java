package makar.dev.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import makar.dev.domain.Route;
import makar.dev.domain.Station;

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


//    @ManyToOne
//    @JoinColumn(name="route_id", nullable = false)
//    private Route route;

//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transfer")
//    @Column(nullable = false)
//    private List<Station> stationList;

}
