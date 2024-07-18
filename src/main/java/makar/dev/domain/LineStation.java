package makar.dev.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Builder
@Entity
public class LineStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_station_id")
    private Long lineStationId;

    @Column(nullable = false)
    private int odsayStationId;

    @Column(nullable = false)
    private String stationName;
}
