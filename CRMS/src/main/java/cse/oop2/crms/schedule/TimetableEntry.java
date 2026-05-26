/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cse.oop2.crms.schedule;

/**
 *
 * @author wooye
 */

import java.io.Serializable;

public class TimetableEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    private String academicYear; // 학년도 (예: "2026")
    private String semester;     // 학기 (예: "1학기")
    private String building;     // 건물명 (예: "정보관")
    private String roomNumber;   // 강의실 번호 (예: "912")
    private String roomType;     // 공간 구분 ("강의실" 또는 "실습실")
    private String dayOfWeek;    // 요일 (예: "월")
    private String timeSlot;     // 시간대 (예: "09:00-12:00")
    private String entryName;    // 과목명 또는 예약명 또는 보강/세미나명
    private String targetGrade;  // 대상 학년 또는 신청자 ID
    private String entryType;    // 데이터 분류 ("LECTURE": 강의현황, "RESERVATION": 예약현황, "WAITING": 예약대기)

    public TimetableEntry(String academicYear, String semester, String building, String roomNumber,
                          String roomType, String dayOfWeek, String timeSlot, String entryName, 
                          String targetGrade, String entryType) {
        this.academicYear = academicYear;
        this.semester = semester;
        this.building = building;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.dayOfWeek = dayOfWeek;
        this.timeSlot = timeSlot;
        this.entryName = entryName;
        this.targetGrade = targetGrade;
        this.entryType = entryType;
    }

    // Getters and Setters
    public String getAcademicYear() { return academicYear; }
    public String getSemester() { return semester; }
    public String getBuilding() { return building; }
    public String getRoomNumber() { return roomNumber; }
    public String getRoomType() { return roomType; }
    public String getDayOfWeek() { return dayOfWeek; }
    public String getTimeSlot() { return timeSlot; }
    public String getEntryName() { return entryName; }
    public void setEntryName(String entryName) { this.entryName = entryName; }
    public String getTargetGrade() { return targetGrade; }
    public String getEntryType() { return entryType; }
    public void setEntryType(String entryType) { this.entryType = entryType; }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s", 
                academicYear, semester, building, roomNumber, roomType, dayOfWeek, timeSlot, entryName, targetGrade, entryType);
    }
}