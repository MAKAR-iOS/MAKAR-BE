package makar.dev.domain.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class OdsayStation {
    // Odsay 대중교통 정류장 검색 API 호출을 위한 Class

    @JsonProperty("result")
    private Result result;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        @JsonProperty("station")
        private List<Station> station;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class Station {

        @JsonProperty("stationName")
        private String stationName;

        @JsonProperty("stationID")
        private int stationID;

        @JsonProperty("x")
        private double x;

        @JsonProperty("y")
        private double y;

        @JsonProperty("CID")
        private int CID;

        @JsonProperty("arsID")
        private String arsID;

        @JsonProperty("do")
        private String doValue;

        @JsonProperty("gu")
        private String gu;

        @JsonProperty("dong")
        private String dong;

        @JsonProperty("type") //지하철 노선 번호
        private int type;

        @JsonProperty("laneName")
        private String laneName;

        @JsonProperty("laneCity")
        private String laneCity;

        @JsonProperty("ebid")
        private String ebid;
    }
}
