package makar.dev.domain.Route.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class SelectedRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "selected_route_id")
    private Long selectedRouteId;

    @OneToOne
    private Route route;

    @Column(nullable = false)
    private String sourceStationName;

    @Column(nullable = false)
    private String destinationStationName;

    @Column(nullable = false)
    private int makarNotiTime; //막차 알림 시간

    @Column(nullable = false)
    private String getOffNotiTime; //하차 알림 시간

    @Column(nullable = false)
    private Date makarTime; //막차 시간

    @Column(nullable = false)
    private Date getOffTime; //하차 시간
}
