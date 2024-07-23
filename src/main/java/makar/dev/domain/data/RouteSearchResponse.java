package makar.dev.domain.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RouteSearchResponse {

    @JsonProperty("result")
    private Result result;

    public Result getResult() {
        return result;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        @JsonProperty("subwayCount")
        private int totalRouteCount;

        @JsonProperty("path")
        private List<Path> path;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Path {

        @JsonProperty("info")
        private Info info;

        @JsonProperty("subPath")
        private List<SubPath> subPath;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Info {
        @JsonProperty("totalTime")
        private int totalTime;

        @JsonProperty("mapObj")
        private String mabObj;

        @JsonProperty("subwayTransitCount")
        private int subwayTransitCount;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SubPath {
        @JsonProperty("trafficType")
        private int trafficType;  //1:지하철 //2:버스

        @JsonProperty("sectionTime")
        private int sectionTime; //구간 소요시간

        @JsonProperty("lane")
        private List<Lane> lane;

        @JsonProperty("startName")
        private String startStationName;

        @JsonProperty("endName")
        private String endStationName;

        @JsonProperty("wayCode")
        private int wayCode; //1: 상행, 2: 하행

        @JsonProperty("startID")
        private int startID;

        @JsonProperty("endID")
        private int endID;

        @JsonProperty("passStopList")
        private PassStopList passStopList;

        public boolean isWalkType() {
            return trafficType == 3;
        }
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Lane {
        @JsonProperty("name")
        private String name;

        @JsonProperty("subwayCode")
        private int lineNum;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PassStopList {
        @JsonProperty("stations")
        private List<Station> stations;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Station {
        @JsonProperty("stationName")
        private String stationName;
    }


}
