/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package cse.oop2.crms.schedule;

/**
 *
 * @author wooye
 */

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduleAndFileManager {
    private static ScheduleAndFileManager instance;
    
    private List<TimetableEntry> timetableList = new ArrayList<>();
    private List<Notification> systemNotificationStorage = new ArrayList<>(); // [SFR-401] 로그인 무관 알림 저장소
    
    private final String DATA_FILE_PATH = "data/timetable.txt";
    private final String BACKUP_FILE_PATH = "data/backup_timetable.ser";

    private final List<String> ALLOWED_LECTURE_ROOMS = Arrays.asList("908", "912", "913", "914");
    private final List<String> ALLOWED_PRACTICE_ROOMS = Arrays.asList("911", "915", "916", "918");

    private ScheduleAndFileManager() { loadDataFromFile(); }

    public static synchronized ScheduleAndFileManager getInstance() {
        if (instance == null) instance = new ScheduleAndFileManager();
        return instance;
    }

    public boolean isValidRoom(String roomNumber, String roomType) {
        if ("강의실".equals(roomType)) return ALLOWED_LECTURE_ROOMS.contains(roomNumber);
        if ("실습실".equals(roomType)) return ALLOWED_PRACTICE_ROOMS.contains(roomNumber);
        return false;
    }

    // ==========================================
    // [SFR-201] 특정 강의실의 강의 시간 학년도/학기별 관리 (조교)
    // ==========================================
    public boolean addLectureSchedule(TimetableEntry entry) {
        if (!isValidRoom(entry.getRoomNumber(), entry.getRoomType())) return false;
        timetableList.add(entry);
        saveDataToFile();
        return true;
    }

    public List<TimetableEntry> getSchedulesByTerm(String year, String semester) {
        return timetableList.stream()
                .filter(e -> e.getAcademicYear().equals(year) && e.getSemester().equals(semester))
                .collect(Collectors.toList());
    }

    // ==========================================
    // [SFR-202] 강의 현황 및 예약 현황 통합 백업/복구 (조교)
    // ==========================================
    public boolean backupData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BACKUP_FILE_PATH))) {
            oos.writeObject(timetableList); // 강의현황, 예약현황, 대기현황 전체 직렬화
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public boolean restoreData() {
        File file = new File(BACKUP_FILE_PATH);
        if (!file.exists()) return false;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            timetableList = (List<TimetableEntry>) ois.readObject();
            saveDataToFile();
            return true;
        } catch (IOException | ClassNotFoundException e) {
            return false;
        }
    }

    // ==========================================
    // [SFR-401] 일반 취소 발생 시 학생 알림 적재 (로그인 여부 무관)
    // ==========================================
    public void sendDirectNotification(String studentId, String message) {
        // 사용자의 세션/로그인 상태와 무관하게 시스템 데이터베이스(저장소)에 즉시 저장하여 영구 보존
        systemNotificationStorage.add(new Notification(studentId, message));
        System.out.println("[SFR-401 알림 적재완료] 학생ID: " + studentId + " | 내용: " + message);
    }

    // ==========================================
    // [SFR-402] 교수 보강/세미나 예약 시 모든 예약/대기 강제 취소 및 알림 생성
    // ==========================================
    public NotificationIterator professorSpecialReservation(TimetableEntry profEntry) {
        if (!isValidRoom(profEntry.getRoomNumber(), profEntry.getRoomType())) return null;

        List<Notification> cancelNotifications = new ArrayList<>();

        // 1. 해당 시간대(학년도, 학기, 호실, 요일, 시간)에 걸려있는 기존 '모든 예약(RESERVATION)' 및 '예약 대기(WAITING)' 추출
        List<TimetableEntry> targetsToExpel = timetableList.stream()
                .filter(e -> e.getAcademicYear().equals(profEntry.getAcademicYear()) &&
                             e.getSemester().equals(profEntry.getSemester()) &&
                             e.getRoomNumber().equals(profEntry.getRoomNumber()) &&
                             e.getDayOfWeek().equals(profEntry.getDayOfWeek()) &&
                             e.getTimeSlot().equals(profEntry.getTimeSlot()) &&
                             !e.getEntryType().equals("LECTURE")) // 기존 정규 강의는 제외
                .collect(Collectors.toList());

        // 2. 대상자들 정보 수집 후 강제 취소 알림 생성 및 기존 데이터 목록에서 폭파(삭제)
        for (TimetableEntry expelled : targetsToExpel) {
            String targetUser = expelled.getTargetGrade(); // 예약자 또는 대기자 ID
            String msg = String.format("[SFR-402 강제취소] 교수의 %s 목적 예약으로 인해, %s %s호 (%s)의 기존 예약/대기 건이 취소되었습니다.", 
                    profEntry.getEntryName(), profEntry.getBuilding(), profEntry.getRoomNumber(), profEntry.getTimeSlot());
            
            cancelNotifications.add(new Notification(targetUser, msg));
            systemNotificationStorage.add(new Notification(targetUser, msg)); // SFR-401 연동: 로그인 무관하게 수신함에 적재
            
            timetableList.remove(expelled); // 강제 삭제
        }

        // 3. 교수의 보강/세미나 일정을 시간표에 최종 강제 등록
        profEntry.setEntryType("LECTURE"); // 보강은 정규 강의 권한으로 등록
        timetableList.add(profEntry);
        saveDataToFile();

        // 4. 알림 처리를 위한 Iterator 반환
        return new CombinedNotificationIterator(cancelNotifications);
    }

    // 파일 입출력 생략 (UTF-8 기적용 스키마)
    private void saveDataToFile() {
        File file = new File(DATA_FILE_PATH);
        if (file.getParentFile() != null) file.getParentFile().mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), java.nio.charset.StandardCharsets.UTF_8))) {
            for (TimetableEntry e : timetableList) { bw.write(e.toString()); bw.newLine(); }
        } catch (IOException e) { System.err.println(e.getMessage()); }
    }

    private void loadDataFromFile() {
        File file = new File(DATA_FILE_PATH); if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), java.nio.charset.StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] t = line.split(",");
                if (t.length == 10) timetableList.add(new TimetableEntry(t[0], t[1], t[2], t[3], t[4], t[5], t[6], t[7], t[8], t[9]));
            }
        } catch (IOException e) { System.err.println(e.getMessage()); }
    }
}