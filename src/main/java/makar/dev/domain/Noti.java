package makar.dev.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import makar.dev.common.enums.Notification;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Builder
@Entity
public class Noti implements Comparable<Noti>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "noti_id")
    private Long notiId;

    @ManyToOne
    private Route route;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Notification notiType;

    @Column(nullable = false)
    private int noti_minute; //알림 시간

    @Override
    public int compareTo(Noti noti) {
        if (noti.getNoti_minute() < noti_minute)
            return -1;
        else if (noti.getNoti_minute() > noti_minute)
            return 1;
        return 0;
    }
}
