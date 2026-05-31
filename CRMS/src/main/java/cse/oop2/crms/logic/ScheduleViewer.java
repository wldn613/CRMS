package cse.oop2.crms.logic;

import cse.oop2.crms.model.dto.Reservation;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ScheduleViewer {
    
    // 템플릿 메소드: 전체적인 조회 흐름을 정의합니다. (final로 선언하여 구조 변경 방지)
    public final List<Reservation> getSchedule(List<Reservation> allData) {
        // 1. 특정 기준에 따라 필터링 (일별/주별/월별 - 자식이 구현)
        List<Reservation> filtered = filterByPeriod(allData);
        
        // 2. 시간순 정렬 (공통 로직)
        return filtered.stream()
                .sorted((r1, r2) -> r1.getStart().compareTo(r2.getStart()))
                .collect(Collectors.toList());
    }

    // 세부 구현은 자식 클래스에게 맡깁니다.
    protected abstract List<Reservation> filterByPeriod(List<Reservation> data);
}