package makar.dev.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private String id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String username;

    @Column(name = "refresh_token")
    private String refreshToken;

    @ManyToOne
    private Station favoriteHomeStation; //즐겨찾는 역_집

    @ManyToOne
    private Station favoriteSchoolStation; //즐겨찾는 역_학교

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Noti> notiList; //알림 설정

    @OneToMany(cascade = CascadeType.ALL)
    private List<Route> recentRouteList; //최근 경로 리스트

    @OneToMany(cascade = CascadeType.ALL)
    private List<Route> favoriteRouteList; //즐겨찾는 경로 리스트

    public User(String userName){
        this.username = userName;
    }

    public void updateFavoriteHomeStation(Station station){this.favoriteHomeStation = station;}
    public void updateFavoriteSchoolStation(Station station){this.favoriteSchoolStation = station;}
    public boolean isFavoriteHomeStationExist(){return this.favoriteHomeStation != null;}
    public boolean isFavoriteSchoolStationExist(){return this.favoriteSchoolStation != null;}
    public void addNotiList(Noti noti){this.notiList.add(noti);}


    @Builder
    public User(String id, String password, String email, String username) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.username = username;
    }
}
