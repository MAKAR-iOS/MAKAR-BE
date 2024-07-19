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
public class LineMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_map_id")
    private Long lineMapId;

    @Column(nullable = false)
    private int lineNum;

    @Column(nullable = false)
    private String startStationName;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LineStation> upLineList;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LineStation> downLineList;

}

