package cse.oop2.crms.model.service;

import cse.oop2.crms.model.dto.Reservation;
import cse.oop2.crms.model.repository.ReservationRepository;

import cse.oop2.crms.logic.ScheduleViewer;
import cse.oop2.crms.logic.DailyScheduleViewer;
import cse.oop2.crms.logic.WeeklyScheduleViewer;
import cse.oop2.crms.logic.MonthlyScheduleViewer;

import cse.oop2.crms.schedule.ScheduleAndFileManager;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class ReservationService {

    private final ReservationRepository repository =
            ReservationRepository.getInstance();

    // 예약 충돌 검사
    public boolean isConflict(Reservation newRes) {

        List<Reservation> existingList =
                repository.findAll();

        for (Reservation exist : existingList) {

            if (exist.getRoomId().equals(newRes.getRoomId())) {

                if (newRes.getStart().isBefore(exist.getEnd())
                        &&
                        newRes.getEnd().isAfter(exist.getStart())) {

                    return true;
                }
            }
        }

        return false;
    }

    // 일반 예약 등록
    public void makeReservation(Reservation res)
            throws Exception {

        if (isConflict(res)) {

            throw new Exception(
                    "선택한 시간에 이미 예약이 존재합니다."
            );
        }

        repository.add(res);
    }

    // 교수 우선 예약
    // 학생 예약 강제 취소 + 알림
    public void forceProfessorReservation(
            Reservation professorRes
    ) {
        if(hasProfessorReservation(
                professorRes.getUserId()))
        {
            throw new RuntimeException(
                "이미 예약된 교수 예약이 존재합니다."
        );
    }

        List<Reservation> reservations =
                repository.findAll();

        List<Reservation> targets =
                new ArrayList<>();

        for (Reservation res : reservations) {

            boolean sameRoom =
                    res.getRoomId()
                            .equals(
                                    professorRes.getRoomId()
                            );

            boolean overlap =
                    professorRes.getStart()
                            .isBefore(res.getEnd())
                    &&
                    professorRes.getEnd()
                            .isAfter(res.getStart());

            // 학생 예약만 제거
            if (sameRoom
                    && overlap
                    && !res.getResId().startsWith("PROF-")) {

                targets.add(res);
            }
        }

        for (Reservation target : targets) {

            repository.remove(
                    target.getResId()
            );

            ScheduleAndFileManager
                    .getInstance()
                    .sendDirectNotification(
                            target.getUserId(),
                            "[강제 취소 알림]\n"
                                    + "교수 우선 예약으로 인해\n"
                                    + target.getRoomId()
                                    + " 강의실 예약이 취소되었습니다."
                    );

            System.out.println(
                    "[교수 우선 예약] 학생 예약 제거 : "
                            + target.getResId()
            );
        }

        repository.add(professorRes);

        System.out.println(
                "[교수 예약 완료] "
                        + professorRes.getResId()
        );
    }

    // 전체 예약 조회
    public List<Reservation> getAllReservations() {

        return repository.findAll();
    }

    // 예약 ID 조회
    public Reservation findReservationById(
            String reservationId
    ) {

        for (Reservation reservation
                : repository.findAll()) {

            if (reservation.getResId()
                    .equals(reservationId)) {

                return reservation;
            }
        }

        return null;
    }

    // 예약 취소
    public void cancelReservation(String resId) {

        repository.remove(resId);
    }

    // 일별 조회
    public List<Reservation> getDailyReservations(
            LocalDate date
    ) {

        ScheduleViewer viewer =
                new DailyScheduleViewer(date);

        return viewer.getSchedule(
                repository.findAll()
        );
    }

    // 주별 조회
    public List<Reservation> getWeeklyReservations(
            LocalDate date
    ) {

        ScheduleViewer viewer =
                new WeeklyScheduleViewer(date);

        return viewer.getSchedule(
                repository.findAll()
        );
    }

    // 월별 조회
    public List<Reservation> getMonthlyReservations(
            int year,
            int month
    ) {

        ScheduleViewer viewer =
                new MonthlyScheduleViewer(
                        year,
                        month
                );

        return viewer.getSchedule(
                repository.findAll()
        );
    }
    // 교수 예약 조회
    public List<Reservation> getReservationsByUser(String userId) {
        return repository.findAll()
                .stream()
                .filter(r -> r.getUserId().equals(userId))
                .toList();
    }
    
    public boolean hasProfessorReservation(String professorId) {
        return repository.findAll()
                .stream()
                .anyMatch(r ->
                        r.getUserId().equals(professorId)
                        &&
                        r.getResId().startsWith("PROF-")
                );
    }
    
}