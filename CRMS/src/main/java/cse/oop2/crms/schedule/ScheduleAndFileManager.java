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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ScheduleAndFileManager
 *
 * SFR-201 : 강의실 시간표 관리
 * SFR-202 : 백업/복구
 * SFR-401 : 로그인 여부와 무관한 알림 저장
 * SFR-402 : 교수 우선예약 시 강제취소 알림
 */

public class ScheduleAndFileManager {

    private static ScheduleAndFileManager instance;

    // 시간표 저장소
    private List<TimetableEntry> timetableList =
            new ArrayList<>();

    // 시스템 알림 저장소
    private List<Notification> systemNotificationStorage =
            new ArrayList<>();

    // 시간표 파일
    private final String DATA_FILE_PATH =
            "data/timetable.txt";

    // 백업 파일
    private final String BACKUP_FILE_PATH =
            "data/backup_timetable.ser";

    // 알림 파일
    private final String NOTIFICATION_FILE_PATH =
            "data/notifications.txt";

    // 허용 강의실
    private final List<String> ALLOWED_LECTURE_ROOMS =
            Arrays.asList(
                    "908",
                    "912",
                    "913",
                    "914"
            );

    // 허용 실습실
    private final List<String> ALLOWED_PRACTICE_ROOMS =
            Arrays.asList(
                    "911",
                    "915",
                    "916",
                    "918"
            );

    private ScheduleAndFileManager() {

        loadDataFromFile();

        // 알림 복구
        loadNotifications();
    }

    public static synchronized ScheduleAndFileManager getInstance() {

        if(instance == null) {

            instance =
                    new ScheduleAndFileManager();
        }

        return instance;
    }

    /**
     * 강의실 유효성 검사
     */
    public boolean isValidRoom(
            String roomNumber,
            String roomType
    ) {

        if("강의실".equals(roomType)) {

            return ALLOWED_LECTURE_ROOMS
                    .contains(roomNumber);
        }

        if("실습실".equals(roomType)) {

            return ALLOWED_PRACTICE_ROOMS
                    .contains(roomNumber);
        }

        return false;
    }

    /**
     * SFR-201
     * 강의 일정 추가
     */
    public boolean addLectureSchedule(
            TimetableEntry entry
    ) {

        if(!isValidRoom(
                entry.getRoomNumber(),
                entry.getRoomType()
        )) {

            return false;
        }

        timetableList.add(entry);

        saveDataToFile();

        return true;
    }

    /**
     * 학년도/학기별 조회
     */
    public List<TimetableEntry> getSchedulesByTerm(
            String year,
            String semester
    ) {

        return timetableList.stream()

                .filter(e ->
                        e.getAcademicYear().equals(year)
                        &&
                        e.getSemester().equals(semester)
                )

                .collect(Collectors.toList());
    }

    /**
     * SFR-202
     * 백업
     */
    public boolean backupData() {

        try(
                ObjectOutputStream oos =
                        new ObjectOutputStream(
                                new FileOutputStream(
                                        BACKUP_FILE_PATH
                                )
                        )
        ) {

            oos.writeObject(timetableList);

            return true;

        } catch(Exception e) {

            e.printStackTrace();

            return false;
        }
    }

    /**
     * SFR-202
     * 복구
     */
    @SuppressWarnings("unchecked")
    public boolean restoreData() {

        File file =
                new File(BACKUP_FILE_PATH);

        if(!file.exists()) {

            return false;
        }

        try(
                ObjectInputStream ois =
                        new ObjectInputStream(
                                new FileInputStream(file)
                        )
        ) {

            timetableList =
                    (List<TimetableEntry>)
                            ois.readObject();

            saveDataToFile();

            return true;

        } catch(Exception e) {

            e.printStackTrace();

            return false;
        }
    }

    /**
     * SFR-401
     * 로그인 여부와 무관하게 알림 저장
     */
    public void sendDirectNotification(
            String userId,
            String message
    ) {

        Notification notification =
                new Notification(
                        userId,
                        message
                );

        systemNotificationStorage.add(
                notification
        );

        saveNotifications();

        System.out.println(
                "[알림 저장 완료] "
                        + userId
                        + " : "
                        + message
        );
    }

    /**
     * 특정 사용자 알림 조회
     */
    public List<Notification> getNotifications(
            String userId
    ) {

        List<Notification> result =
                new ArrayList<>();

        for(Notification notification
                : systemNotificationStorage) {

            if(notification.getUserId()
                    .equals(userId)) {

                result.add(notification);
            }
        }

        return result;
    }

    /**
     * 특정 사용자 알림 삭제
     */
    public void clearNotifications(
            String userId
    ) {

        systemNotificationStorage.removeIf(

                n ->
                        n.getUserId()
                                .equals(userId)
        );

        saveNotifications();
    }

    /**
     * 교수 강제 예약
     * 기존 예약자 강제취소
     */
    public NotificationIterator professorSpecialReservation(
            TimetableEntry profEntry
    ) {

        if(!isValidRoom(
                profEntry.getRoomNumber(),
                profEntry.getRoomType()
        )) {

            return null;
        }

        List<Notification> cancelNotifications =
                new ArrayList<>();

        List<TimetableEntry> targetsToExpel =

                timetableList.stream()

                        .filter(e ->

                                e.getAcademicYear()
                                        .equals(
                                                profEntry.getAcademicYear()
                                        )

                                        &&

                                        e.getSemester()
                                                .equals(
                                                        profEntry.getSemester()
                                                )

                                        &&

                                        e.getRoomNumber()
                                                .equals(
                                                        profEntry.getRoomNumber()
                                                )

                                        &&

                                        e.getDayOfWeek()
                                                .equals(
                                                        profEntry.getDayOfWeek()
                                                )

                                        &&

                                        e.getTimeSlot()
                                                .equals(
                                                        profEntry.getTimeSlot()
                                                )

                                        &&

                                        !e.getEntryType()
                                                .equals("LECTURE")
                        )

                        .collect(Collectors.toList());

        for(TimetableEntry expelled
                : targetsToExpel) {

            String targetUser =
                    expelled.getTargetGrade();

            String msg =
                    "[강제 취소] 교수 우선 예약으로 인해 "
                            + profEntry.getRoomNumber()
                            + "호 예약이 취소되었습니다.";

            Notification notification =
                    new Notification(
                            targetUser,
                            msg
                    );

            cancelNotifications.add(
                    notification
            );

            // 영구 저장
            sendDirectNotification(
                    targetUser,
                    msg
            );

            timetableList.remove(expelled);
        }

        profEntry.setEntryType("LECTURE");

        timetableList.add(profEntry);

        saveDataToFile();

        return new CombinedNotificationIterator(
                cancelNotifications
        );
    }

    /**
     * 알림 파일 저장
     */
    private void saveNotifications() {

        File file =
                new File(
                        NOTIFICATION_FILE_PATH
                );

        if(file.getParentFile() != null) {

            file.getParentFile().mkdirs();
        }

        try(
                BufferedWriter bw =
                        new BufferedWriter(
                                new OutputStreamWriter(
                                        new FileOutputStream(file),
                                        StandardCharsets.UTF_8
                                )
                        )
        ) {

            for(Notification n
                    : systemNotificationStorage) {

                bw.write(
                        n.getUserId()
                                + "|"
                                + n.getMessage()
                );

                bw.newLine();
            }

        } catch(Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * 알림 파일 로드
     */
    private void loadNotifications() {

        File file =
                new File(
                        NOTIFICATION_FILE_PATH
                );

        if(!file.exists()) {

            return;
        }

        try(
                BufferedReader br =
                        new BufferedReader(
                                new InputStreamReader(
                                        new FileInputStream(file),
                                        StandardCharsets.UTF_8
                                )
                        )
        ) {

            String line;

            while((line = br.readLine()) != null) {

                String[] parts =
                        line.split("\\|", 2);

                if(parts.length == 2) {

                    systemNotificationStorage.add(

                            new Notification(
                                    parts[0],
                                    parts[1]
                            )
                    );
                }
            }

        } catch(Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * 시간표 저장
     */
    private void saveDataToFile() {

        File file =
                new File(DATA_FILE_PATH);

        if(file.getParentFile() != null) {

            file.getParentFile().mkdirs();
        }

        try(
                BufferedWriter bw =
                        new BufferedWriter(
                                new OutputStreamWriter(
                                        new FileOutputStream(file),
                                        StandardCharsets.UTF_8
                                )
                        )
        ) {

            for(TimetableEntry e
                    : timetableList) {

                bw.write(e.toString());

                bw.newLine();
            }

        } catch(IOException e) {

            e.printStackTrace();
        }
    }

    /**
     * 시간표 로드
     */
    private void loadDataFromFile() {

        File file =
                new File(DATA_FILE_PATH);

        if(!file.exists()) {

            return;
        }

        try(
                BufferedReader br =
                        new BufferedReader(
                                new InputStreamReader(
                                        new FileInputStream(file),
                                        StandardCharsets.UTF_8
                                )
                        )
        ) {

            String line;

            while((line = br.readLine()) != null) {

                String[] t =
                        line.split(",");

                if(t.length == 10) {

                    timetableList.add(

                            new TimetableEntry(
                                    t[0],
                                    t[1],
                                    t[2],
                                    t[3],
                                    t[4],
                                    t[5],
                                    t[6],
                                    t[7],
                                    t[8],
                                    t[9]
                            )
                    );
                }
            }

        } catch(IOException e) {

            e.printStackTrace();
        }
    }
}