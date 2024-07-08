package makar.dev.domain.Transfer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import makar.dev.domain.Route.entity.Route;
import makar.dev.domain.Station.entity.Station;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transfer_id")
    private Long transferId;

    @ManyToOne
    @JoinColumn(name="route_id", nullable = false)
    private Route route;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transfer")
    @Column(nullable = false)
    private List<Station> stationList;

}
