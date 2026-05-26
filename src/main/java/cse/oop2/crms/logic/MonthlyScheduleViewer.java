package cse.oop2.crms.logic;

import cse.oop2.crms.model.dto.Reservation;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class MonthlyScheduleViewer extends ScheduleViewer {
    private int year;
    private int month;

    public MonthlyScheduleViewer(int year, int month) {
        this.year = year;
        this.month = month;
    }

    @Override
    protected List<Reservation> filterByPeriod(List<Reservation> data) {
        return data.stream()
                .filter(res -> {
                    LocalDate resDate = res.getStart().toLocalDate();
                    // 연도와 월이 모두 일치하는지 확인
                    return resDate.getYear() == year && resDate.getMonthValue() == month;
                })
                .collect(Collectors.toList());
    }
}