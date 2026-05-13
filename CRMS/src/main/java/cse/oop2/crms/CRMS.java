package cse.oop2.crms;

import cse.oop2.crms.model.dto.Reservation;
import cse.oop2.crms.model.service.ReservationService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CRMS {
    public static void main(String[] args) {
        ReservationService service = new ReservationService();
        
        // 1. 테스트용 데이터 삽입 (이미 있다면 생략 가능)
        try {
            // 오늘 날짜 데이터
            service.makeReservation(new Reservation("R1", "user1", "A101", 
                    LocalDateTime.of(2026, 5, 14, 10, 0), LocalDateTime.of(2026, 5, 14, 12, 0)));
            // 내일 날짜 데이터
            service.makeReservation(new Reservation("R2", "user2", "A101", 
                    LocalDateTime.of(2026, 5, 15, 14, 0), LocalDateTime.of(2026, 5, 15, 16, 0)));
            // 다음 주 데이터
            service.makeReservation(new Reservation("R3", "user3", "B202", 
                    LocalDateTime.of(2026, 5, 22, 01, 0), LocalDateTime.of(2026, 5, 22, 03, 0)));
        } catch (Exception e) {
            System.out.println("참고: 중복 데이터는 저장되지 않습니다.");
        }

        System.out.println("===== [테스트 시작] =====");

        // 2. 일별 조회 테스트 (2026-05-14)
        System.out.println("\n[1. 일별 조회: 2026-05-14]");
        printList(service.getDailyReservations(LocalDate.of(2026, 5, 14)));

        // 3. 주별 조회 테스트 (오늘이 포함된 주)
        System.out.println("\n[2. 주별 조회: 5월 3째주]");
        printList(service.getWeeklyReservations(LocalDate.of(2026, 5, 14)));

        // 4. 월별 조회 테스트 (2026-05)
        System.out.println("\n[3. 월별 조회: 2026-05]");
        printList(service.getMonthlyReservations(2026, 5));
    }

    // 리스트 출력을 위한 헬퍼 메소드
    private static void printList(List<Reservation> list) {
        if (list.isEmpty()) {
            System.out.println("  조회된 데이터가 없습니다.");
        } else {
            for (Reservation res : list) {
                System.out.println("  - " + res.getStart() + " | " + res.getRoomId() + " (" + res.getUserId() + ")");
            }
        }
    }
}