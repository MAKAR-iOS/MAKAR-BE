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

    // 최근 경로 리스트 내 순서
    @Column(name = "recent_order")
    private int recentOrder;

    public void updateSchedule(Schedule schedule){
        this.schedule = schedule;
    }

    public void setRecentOrder(int recentOrder) {
        this.recentOrder = recentOrder;
    }
}
