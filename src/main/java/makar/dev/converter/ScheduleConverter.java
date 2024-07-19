package makar.dev.converter;

import makar.dev.domain.Route;
import makar.dev.domain.Schedule;

public class ScheduleConverter {
    public static Schedule toSchedule(String sourceTime, String destinationTime, int totalTime) {
        return Schedule.builder()
                .sourceTime(sourceTime)
                .destinationTime(destinationTime)
                .totalTime(totalTime)
                .build();
    }
}
