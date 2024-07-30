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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Route> recentRouteList; //최근 경로 리스트

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Route> favoriteRouteList; //즐겨찾는 경로 리스트

    public User(String userName){
        this.username = userName;
    }

    public void updateFavoriteHomeStation(Station station){this.favoriteHomeStation = station;}
    public void updateFavoriteSchoolStation(Station station){this.favoriteSchoolStation = station;}
    public boolean isFavoriteHomeStationExist(){return this.favoriteHomeStation != null;}
    public boolean isFavoriteSchoolStationExist(){return this.favoriteSchoolStation != null;}
    public void addNotiList(Noti noti){this.notiList.add(noti);}
    public void addFavoriteRoute(Route route){this.favoriteRouteList.add(favoriteRouteList.size(), route);}
    public void addRecentRouteList(Route route) {
        int maxOrder = -1;
        Route existingRoute = null;

        // 동일한 routeId를 가진 Route가 이미 최근 경로 리스트에 존재하는지 확인
        for (Route r : this.recentRouteList) {
            if (r.getRouteId().equals(route.getRouteId())) {
                existingRoute = r;
            }
            if (r.getRecentOrder() > maxOrder) {
                maxOrder = r.getRecentOrder();
            }
        }

        if (existingRoute != null) {
            // 이미 리스트에 존재하는 경우 recentOrder만 수정
            existingRoute.setRecentOrder(maxOrder + 1);
        } else {
            // 최근 경로 리스트 사이즈 최대 5개 유지
            if (this.recentRouteList.size() >= 5) {
                this.recentRouteList.remove(0);
            }

            // 새로운 Route일 경우 리스트의 마지막에 추가
            route.setRecentOrder(maxOrder + 1);
            this.recentRouteList.add(route);
        }
    }
    public void removeRecentRouteList(Route route) {
        this.recentRouteList.remove(route);
    }
    public void clearRecentRouteList() {
        this.recentRouteList.clear();
    }

    @Builder
    public User(String id, String password, String email, String username) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.username = username;
    }
}
