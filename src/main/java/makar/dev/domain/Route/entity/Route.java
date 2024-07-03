package makar.dev.domain.Route.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import makar.dev.domain.Station.entity.Station;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private Long routeId;

    @ManyToOne
    private Station sourceStation;

    @ManyToOne
    private Station destinationStation;

    @Column(nullable = false)
    private int totalTime; //전체 소요시간

    @Column(nullable = false)
    private int transferCount; //환승 횟수

}
