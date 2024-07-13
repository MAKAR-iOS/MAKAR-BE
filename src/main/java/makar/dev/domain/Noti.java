package makar.dev.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import makar.dev.common.enums.Notification;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Noti {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "noti_id")
    private Long notiId;

    @OneToOne(mappedBy = "noti", fetch = FetchType.LAZY)
    private Route route;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Notification notiType;

    @Column(nullable = false)
    private int noti_minute; //알림 시간

}
