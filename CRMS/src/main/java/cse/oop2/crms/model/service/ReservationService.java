package cse.oop2.crms.model.service;

import cse.oop2.crms.model.dto.Reservation;
import cse.oop2.crms.model.repository.ReservationRepository;
import java.util.List;

public class ReservationService {
    private final ReservationRepository repository = ReservationRepository.getInstance();

    // SFR-102: 교수 예약 시 우선순위 제어 및 충돌 판정
    public boolean isConflict(Reservation newRes) {
        List<Reservation> existingList = repository.findAll();

        for (Reservation exist : existingList) {
            // 같은 강의실인 경우만 체크
            if (exist.getRoomId().equals(newRes.getRoomId())) {
                // 시간 겹침 로직: (새 시작 < 기존 종료) && (새 종료 > 기존 시작)
                if (newRes.getStart().isBefore(exist.getEnd()) && newRes.getEnd().isAfter(exist.getStart())) {
                    return true; // 충돌 발생
                }
            }
        }
        return false;
    }

    public void makeReservation(Reservation res) throws Exception {
        if (isConflict(res)) {
            throw new Exception("선택한 시간에 이미 예약이 존재합니다.");
        }
        repository.add(res);
    }
}