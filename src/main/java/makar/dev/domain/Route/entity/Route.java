package makar.dev.domain.Route.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import makar.dev.domain.Noti.entity.Noti;
import makar.dev.domain.Station.entity.Station;
import makar.dev.domain.Transfer.entity.Transfer;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private Long routeId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "noti_id")
    private Noti noti;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @ManyToOne
    private Station sourceStation;

    @ManyToOne
    private Station destinationStation;

    @Column(nullable = false)
    private int transferCount; //환승 횟수

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "route")
    @Column(nullable = false)
    private List<Transfer> transferList;

}
