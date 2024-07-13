package makar.dev.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    @OneToOne(mappedBy = "schedule", fetch = FetchType.LAZY)
    private Route route;

    @Column(nullable = false)
    private String sourceTime; //출발 시간, 막차 시간

    @Column(nullable = false)
    private String destinationTime; //도착 시간, 하차 시간

    @Column(nullable = false)
    private int totalTime; //전체 소요시간
}
