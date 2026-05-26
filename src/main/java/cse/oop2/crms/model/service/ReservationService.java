package cse.oop2.crms.model.service;

import cse.oop2.crms.model.dto.ClassRoom;
import cse.oop2.crms.model.dto.Reservation;
import cse.oop2.crms.model.dto.User;
import cse.oop2.crms.model.repository.ClassRoomRepository;  // [추가] SFR-106: 강의실 수용 인원 조회용
import cse.oop2.crms.model.repository.ReservationRepository;
import cse.oop2.crms.logic.ScheduleViewer;
import cse.oop2.crms.logic.DailyScheduleViewer;   //일별
import cse.oop2.crms.logic.WeeklyScheduleViewer;  // 주별
import cse.oop2.crms.logic.MonthlyScheduleViewer; //월별
import java.time.LocalDate;
import java.time.Duration;
import java.util.List;

public class ReservationService {

    private final ReservationRepository repository = ReservationRepository.getInstance();

    // [추가] SFR-106: 강의실 수용 인원 50% 검증을 위해 ClassRoomRepository 추가
    private final ClassRoomRepository classRoomRepository = ClassRoomRepository.getInstance();

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

    //일별 조회
    public List<Reservation> getDailyReservations(LocalDate date) {
        ScheduleViewer viewer = new DailyScheduleViewer(date);
        return viewer.getSchedule(repository.findAll());    
    }
    
    //주별 조회
    public List<Reservation> getWeeklyReservations(LocalDate date) {
        ScheduleViewer viewer = new WeeklyScheduleViewer(date); // 주별 뷰어 사용
        return viewer.getSchedule(repository.findAll());        // 템플릿 메소드 실행
    }
    
    //월별 조회
    public List<Reservation> getMonthlyReservations(int year, int month) {
    ScheduleViewer viewer = new MonthlyScheduleViewer(year, month);
    // repository.findAll()을 통해 텍스트 파일에서 읽어온 전체 데이터를 전달합니다.
    return viewer.getSchedule(repository.findAll());
    }

    public void makeReservation(Reservation res) throws Exception {
        if (isConflict(res)) {
            throw new Exception("선택한 시간에 이미 예약이 존재합니다.");
        }
        repository.add(res);
    }

    // ─────────────────────────────────────────────────────────────────────
    // [추가] SFR-106: 수용 인원 50% 제한 검증 로직
    // ─────────────────────────────────────────────────────────────────────

    /**
     * SFR-106: 예약 인원이 강의실 수용 인원의 50%를 초과하는지 검사
     * 초과하면 true(예약 불가), 허용 범위 내면 false(예약 가능)
     */
    public boolean isOverCapacity(String roomId, int numberOfPeople) {
        ClassRoom room = classRoomRepository.findByRoomId(roomId);
        if (room == null) {
            // 강의실 정보가 없으면 일단 통과 (다른 팀원 코드와의 호환성 유지)
            return false;
        }
        // 수용 인원의 50% 초과 여부 확인
        return numberOfPeople > room.getAllowedCapacity();
    }

    /**
     * SFR-106 + SFR-107 통합 예약 신청 메서드
     * - 수용 인원 50% 초과 시 예약 불가
     * - 예약 시간 최대 2시간 제한
     * - 최소 하루 전 사전 신청
     * Reservation.Builder를 통해 생성된 객체를 받음 (SFR-107 Builder Pattern)
     */
    public void requestReservation(Reservation res) throws Exception {
        // [SFR-107] 사전 신청: 최소 하루 전에 신청해야 함
        LocalDate today    = LocalDate.now();
        LocalDate resDate  = res.getStart().toLocalDate();
        if (!resDate.isAfter(today)) {
            throw new Exception("사전 신청은 최소 하루 전에 이루어져야 합니다. (신청일: " + today + ", 예약일: " + resDate + ")");
        }

        // [SFR-107] 예약 시간 최대 2시간 제한
        long minutes = Duration.between(res.getStart(), res.getEnd()).toMinutes();
        if (minutes > 120) {
            throw new Exception("예약 시간은 최대 2시간 이하여야 합니다. (신청 시간: " + minutes + "분)");
        }
        if (minutes <= 0) {
            throw new Exception("종료 시간은 시작 시간 이후여야 합니다.");
        }

        // [SFR-106] 수용 인원 50% 초과 검사
        if (isOverCapacity(res.getRoomId(), res.getNumberOfPeople())) {
            ClassRoom room = classRoomRepository.findByRoomId(res.getRoomId());
            throw new Exception("수용 인원의 50%(" + room.getAllowedCapacity() + "명)를 초과하여 예약이 불가합니다.");
        }

        // 시간 충돌 검사 (기존 로직 재사용)
        if (isConflict(res)) {
            throw new Exception("선택한 시간에 이미 예약이 존재합니다.");
        }

        repository.add(res);
    }

    // ─────────────────────────────────────────────────────────────────────
    // [추가] SFR-301: 조교의 예약 승인/거부 프로세스
    // ─────────────────────────────────────────────────────────────────────

    /**
     * SFR-301: 조교가 예약을 승인
     * - 조교 권한 확인 후 status를 "승인"으로 변경
     */
    public void approveReservation(String resId, User assistant) throws Exception {
        // 조교 권한 확인
        if (!assistant.isAssistant()) {
            throw new Exception("조교 권한이 없습니다. 예약 승인은 조교만 가능합니다.");
        }

        Reservation target = findReservationById(resId);
        if (target == null) {
            throw new Exception("해당 예약을 찾을 수 없습니다. ID: " + resId);
        }
        if (!"대기".equals(target.getStatus())) {
            throw new Exception("대기 상태인 예약만 승인할 수 있습니다. 현재 상태: " + target.getStatus());
        }

        target.setStatus("승인");
        repository.save(); // 변경사항 파일에 저장
    }

    /**
     * SFR-301: 조교가 예약을 거부
     * - 조교 권한 확인 후 status를 "거절"로 변경
     */
    public void rejectReservation(String resId, User assistant) throws Exception {
        // 조교 권한 확인
        if (!assistant.isAssistant()) {
            throw new Exception("조교 권한이 없습니다. 예약 거부는 조교만 가능합니다.");
        }

        Reservation target = findReservationById(resId);
        if (target == null) {
            throw new Exception("해당 예약을 찾을 수 없습니다. ID: " + resId);
        }
        if (!"대기".equals(target.getStatus())) {
            throw new Exception("대기 상태인 예약만 거부할 수 있습니다. 현재 상태: " + target.getStatus());
        }

        target.setStatus("거절");
        repository.save(); // 변경사항 파일에 저장
    }

    // ─────────────────────────────────────────────────────────────────────
    // [추가] SFR-203: 조교 전용 예약 취소 및 취소 사유 등록
    // ─────────────────────────────────────────────────────────────────────

    /**
     * SFR-203: 조교가 단일 예약을 취소 (취소 사유 필수)
     * - 조교 권한 확인
     * - 취소 사유를 Reservation에 등록 후 status를 "취소"로 변경
     */
    public void cancelReservation(String resId, User assistant, String cancelReason) throws Exception {
        // 조교 권한 확인
        if (!assistant.isAssistant()) {
            throw new Exception("조교 권한이 없습니다. 예약 취소는 조교만 가능합니다.");
        }
        if (cancelReason == null || cancelReason.trim().isEmpty()) {
            throw new Exception("취소 사유를 입력해야 합니다.");
        }

        Reservation target = findReservationById(resId);
        if (target == null) {
            throw new Exception("해당 예약을 찾을 수 없습니다. ID: " + resId);
        }
        if ("취소".equals(target.getStatus())) {
            throw new Exception("이미 취소된 예약입니다. ID: " + resId);
        }

        target.setStatus("취소");
        target.setCancelReason(cancelReason); // [SFR-203] 취소 사유 등록
        repository.save();
    }

    /**
     * SFR-203: 조교가 복수 개의 예약을 한번에 취소 (취소 사유 필수)
     * - resIds 리스트에 있는 모든 예약을 취소
     */
    public void cancelReservations(List<String> resIds, User assistant, String cancelReason) throws Exception {
        // 조교 권한 확인
        if (!assistant.isAssistant()) {
            throw new Exception("조교 권한이 없습니다. 예약 취소는 조교만 가능합니다.");
        }
        if (cancelReason == null || cancelReason.trim().isEmpty()) {
            throw new Exception("취소 사유를 입력해야 합니다.");
        }

        // 모든 ID에 대해 취소 처리
        for (String resId : resIds) {
            Reservation target = findReservationById(resId);
            if (target == null) {
                throw new Exception("해당 예약을 찾을 수 없습니다. ID: " + resId);
            }
            if ("취소".equals(target.getStatus())) {
                throw new Exception("이미 취소된 예약입니다. ID: " + resId);
            }
            target.setStatus("취소");
            target.setCancelReason(cancelReason); // [SFR-203] 취소 사유 등록
        }

        repository.save(); // 모두 변경 후 한 번에 저장
    }

    // ─────────────────────────────────────────────────────────────────────
    // [추가] 내부 헬퍼: resId로 예약 객체 찾기
    // findAll()은 복사본 반환 → repository.findById()로 memoryData 직접 참조
    // ─────────────────────────────────────────────────────────────────────
    private Reservation findReservationById(String resId) {
        return repository.findById(resId);
    }

}
