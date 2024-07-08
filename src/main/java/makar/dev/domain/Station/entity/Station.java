package makar.dev.domain.Station.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import makar.dev.domain.Transfer.entity.Transfer;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "station_id")
    private Long stationId;

    @Column(nullable = false)
    private String stationName;

    @ManyToOne
    @JoinColumn(name="transfer_id", nullable = false)
    private Transfer transfer;
}
