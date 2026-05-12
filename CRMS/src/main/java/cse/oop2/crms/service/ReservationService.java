package cse.oop2.crms.service;

import cse.oop2.crms.model.Reservation;

public interface ReservationService {
    // SFR-101: 강의실 현황 조회 (Template Method Pattern의 기반)
    void getRoomStatus(String type); // 일별, 주별, 월별 조회

    // SFR-102~104: 예약 신청 (우선순위 제어 로직 포함될 곳)
    boolean requestReservation(Reservation request);
    
    // SFR-301: 조교의 승인/거절
    void approveReservation(String reservationId, boolean approved);
}