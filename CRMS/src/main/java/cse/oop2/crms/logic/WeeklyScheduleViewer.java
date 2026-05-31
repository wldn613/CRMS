package cse.oop2.crms.logic;

import cse.oop2.crms.model.dto.Reservation;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

public class WeeklyScheduleViewer extends ScheduleViewer {
    private LocalDate startDate;
    private LocalDate endDate;

    public WeeklyScheduleViewer(LocalDate date) {
        // 해당 날짜가 속한 주의 월요일과 일요일을 계산
        this.startDate = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        this.endDate = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }

    @Override
    protected List<Reservation> filterByPeriod(List<Reservation> data) {
        return data.stream()
                .filter(res -> {
                    LocalDate resDate = res.getStart().toLocalDate();
                    // 시작일과 종료일 사이에 포함되는지 확인
                    return (!resDate.isBefore(startDate) && !resDate.isAfter(endDate));
                })
                .collect(Collectors.toList());
    }
}