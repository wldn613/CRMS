/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.lectureroomreservation.model;

import java.util.ArrayList;
import java.util.List;

public class LectureRoom {
    private final String id; // 강의실 호수 (예: IEB913)
    private final int maxCapacity; // 강의실 최대 수용 인원 (SFR-106 요구사항 반영)
    private final String[] lectureInfo = new String[10]; // 1~9교시 고정 수업
    private final List<String> reservationInfo = new ArrayList<>(); // 실시간 예약 내역

    // 생성자에 최대 수용 인원을 함께 입력받도록 수정
    public LectureRoom(String id, int maxCapacity) {
        this.id = id;
        this.maxCapacity = maxCapacity;
    }

    public String getId() {
        return id;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void addFixedLecture(int period, String lectureName) {
        if (period >= 1 && period <= 9) {
            this.lectureInfo[period] = lectureName;
        }
    }

    public String getLectureAt(int period) {
        if (period >= 1 && period <= 9) {
            return lectureInfo[period];
        }
        return null;
    }

    public List<String> getReservationInfo() {
        return reservationInfo;
    }

    // 예약을 추가할 때 사용자의 형태와 상세 정보까지 문자열 포맷으로 보관하도록 확장
    public void addReservation(String resDetails) {
        reservationInfo.add(resDetails);
    }
}