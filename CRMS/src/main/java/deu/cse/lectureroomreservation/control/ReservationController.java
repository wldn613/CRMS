/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.lectureroomreservation.control;

import deu.cse.lectureroomreservation.model.ReservationAgent;
import deu.cse.lectureroomreservation.model.DaysOfWeek;

/**
 * 사용자 UI 화면들과 비즈니스 판정 에이전트 사이를 중계하는 컨트롤러
 * (본인의 담당 요구사항인 SFR-103, 104, 105 처리를 총괄함)
 */
public class ReservationController {
    private final ReservationAgent agent;

    public ReservationController(ReservationAgent agent) {
        this.agent = agent;
    }

    /**
     * 화면에서 입력된 예약 요청이 규칙(시간 중복, 3시간 제한, 50% 인원 제한 등)에 맞는지 선제 체크
     */
    public boolean canReserveRoom(String roomId, DaysOfWeek day, int fromPeriod, int toPeriod, String role, int userCount) {
        return agent.canReserveRoom(roomId, day, fromPeriod, toPeriod, role, userCount);
    }

    /**
     * SFR-103, 104: 교수용 화면으로부터 데이터를 받아 예약을 즉시 반영하고 확정함
     */
    public boolean makeProfessorReserve(String roomId, DaysOfWeek day, int fromPeriod, int toPeriod, String profId, String purpose, int attendees) {
        return agent.makeProfessorReservation(roomId, day, fromPeriod, toPeriod, profId, purpose, attendees);
    }

    /**
     * SFR-105, 106: 학생용 화면으로부터 동반자 정보 및 학번을 받아 예약을 확정함
     */
    public boolean makeStudentReserve(String roomId, DaysOfWeek day, int fromPeriod, int toPeriod, String studentId, String studentName, int companionCount, String companionDetails) {
        return agent.makeStudentReservation(roomId, day, fromPeriod, toPeriod, studentId, studentName, companionCount, companionDetails);
    }
}