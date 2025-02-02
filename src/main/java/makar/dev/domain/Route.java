package makar.dev.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Builder
@Entity
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private Long routeId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @ManyToOne
    private Station sourceStation;

    @ManyToOne
    private Station destinationStation;

    @Column(nullable = false)
    private int transferCount; //환승 횟수

    @OneToMany(cascade = CascadeType.ALL)
    @Column(nullable = false)
    private List<SubRoute> subRouteList;

    public void updateSchedule(Schedule schedule){
        this.schedule = schedule;
    }
}
