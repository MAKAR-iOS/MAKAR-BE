package makar.dev.domain.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter @Getter
public class SubwaySchedule {

    String stationName;
    int stationID;
    int type;
    String laneName;
    String laneCity;
    String upWay;
    String downWay;

    @JsonProperty("OrdList")
    OrdList OrdList;

    @JsonProperty("SatList")
    OrdList satList;

    @JsonProperty("SunList")
    OrdList sunList;

    @Override
    public String toString() {
        return "SubwayStation{" +
                "stationName='" + stationName + '\'' +
                ", stationID=" + stationID +
                ", type=" + type +
                ", laneName='" + laneName + '\'' +
                ", laneCity='" + laneCity + '\'' +
                ", upWay='" + upWay + '\'' +
                ", downWay='" + downWay + '\'' +
                ", OrdList=" + OrdList +
                ", satList=" + satList +
                ", sunList=" + sunList +
                '}';
    }

    @Getter @Setter
    public static class OrdList {

        TimeDirection up;
        TimeDirection down;

        @Override
        public String toString() {
            return "OrdList{" +
                    "up=" + up +
                    ", down=" + down +
                    '}';
        }

        @Setter @Getter
        public static class TimeDirection {
            List<TimeData> time;

            public int getSize() {
                return time.size();
            }

            @Override
            public String toString() {
                return "TimeDirection{" +
                        "time=" + time +
                        '}';
            }

            @Getter @Setter
            @NoArgsConstructor
            public static class TimeData {

                @JsonProperty("Idx")
                int idx;
                String list;
                String expList;
                String expSPList;
            }
        }
    }
}