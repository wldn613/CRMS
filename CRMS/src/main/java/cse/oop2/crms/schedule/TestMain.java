/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

// 빌드용 임시 파일 입니다. 합칠 때 삭제해주세요

package cse.oop2.crms.schedule;

public class TestMain {
    public static void main(String[] args) {
        System.out.println("====== [CRMS 기능 검증 테스트 시작] ======");
        
        ScheduleAndFileManager manager = ScheduleAndFileManager.getInstance();
        
        // 1. SFR-201: 강의 시간표 등록 테스트
        TimetableEntry lecture = new TimetableEntry(
            "2026", "1학기", "정보관", "912", "강의실", "월", "09:00-12:00", "소프트웨어공학", "3학년", "LECTURE"
        );
        boolean isAdded = manager.addLectureSchedule(lecture);
        System.out.println("SFR-201 정규 강의 등록 성공 여부: " + isAdded);
        
        // 2. 학생 예약 및 대기 가상 데이터 등록 (SFR-402 테스트용 준비)
        TimetableEntry studentRes = new TimetableEntry(
            "2026", "1학기", "정보관", "912", "강의실", "화", "13:00-15:00", "팀플회의", "20211234", "RESERVATION"
        );
        manager.addLectureSchedule(studentRes);
        System.out.println("테스트용 학생 예약 등록 완료.");

        // 3. SFR-402: 교수의 보강 예약 및 기존 학생 예약 폭파/취소 알림 테스트
        System.out.println("\n--- [교수가 화요일 13:00 보강 예약을 신청함] ---");
        TimetableEntry profRes = new TimetableEntry(
            "2026", "1학기", "정보관", "912", "강의실", "화", "13:00-15:00", "데이터베이스 보강", "PROF_KIM", "LECTURE"
        );
        
        NotificationIterator iterator = manager.professorSpecialReservation(profRes);
        
        System.out.println("--- [발송된 폭파/취소 알림 순회 (Iterator 패턴)] ---");
        if (iterator != null) {
            while (iterator.hasNext()) {
                Notification note = iterator.next();
                System.out.println("수신 유저: " + note.getUserId());
                System.out.println("알림 내용: " + note.getMessage());
            }
        }
        
        // 4. SFR-202: 백업 테스트
        boolean isBackedUp = manager.backupData();
        System.out.println("\nSFR-202 데이터 전체 백업 완료 여부: " + isBackedUp);
        
        System.out.println("====== [테스트 종료: 모든 기능 정상 작동 완료] ======");
    }
}