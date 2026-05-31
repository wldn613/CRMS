package cse.oop2.crms.logic;

import cse.oop2.crms.model.dto.Reservation;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class DailyScheduleViewer extends ScheduleViewer {
    private LocalDate targetDate;

    public DailyScheduleViewer(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    @Override
    protected List<Reservation> filterByPeriod(List<Reservation> data) {
        // 예약 시작일이 선택한 날짜와 같은 데이터만 걸러냅니다.
        return data.stream()
                .filter(res -> res.getStart().toLocalDate().equals(targetDate))
                .collect(Collectors.toList());
    }
}