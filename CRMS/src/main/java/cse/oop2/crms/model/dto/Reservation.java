package cse.oop2.crms.model.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reservation {

    private String resId;
    private String userId;
    private String roomId;
    private LocalDateTime start;
    private LocalDateTime end;

    // 사용 목적
    private String purpose;

    private String status;     // "대기", "승인", "거절"
    private boolean isNoShow;

    // 참석 인원 수
    private int attendeeCount;

    // 동반 학생 목록
    private List<String> companionStudents;

    public Reservation(
            String resId,
            String userId,
            String roomId,
            LocalDateTime start,
            LocalDateTime end) {

        this.resId = resId;
        this.userId = userId;
        this.roomId = roomId;
        this.start = start;
        this.end = end;

        this.status = "대기";
        this.isNoShow = false;

        this.attendeeCount = 0;
        this.companionStudents = new ArrayList<>();
        this.purpose = "";
    }

    // 파일 저장을 위한 CSV 변환 로직
    public String toCsv() {

        String companions =
                String.join(",", companionStudents);

        return String.join(
                "|",
                resId,
                userId,
                roomId,
                start.toString(),
                end.toString(),
                status,
                String.valueOf(isNoShow),
                String.valueOf(attendeeCount),
                companions,
                purpose == null ? "" : purpose
        );
    }

    // CSV 한 줄을 객체로 복원하는 정적 메소드 (파일 읽기용)
    public static Reservation fromCsv(String csv) {

        String[] data = csv.split("\\|");

        Reservation res =
                new Reservation(
                        data[0],
                        data[1],
                        data[2],
                        LocalDateTime.parse(data[3]),
                        LocalDateTime.parse(data[4])
                );

        res.setStatus(data[5]);
        res.setNoShow(Boolean.parseBoolean(data[6]));

        // 참석 인원 수 복원
        if (data.length > 7) {
            res.setAttendeeCount(
                    Integer.parseInt(data[7])
            );
        }

        // 동반 학생 목록 복원
        if (data.length > 8 && !data[8].isBlank()) {

            res.setCompanionStudents(
                    Arrays.asList(
                            data[8].split(",")
                    )
            );
        }

        // 사용 목적 복원
        if (data.length > 9) {
            res.setPurpose(data[9]);
        }

        return res;
    }

    // Getter & Setter
    public String getUserId() {
        return userId;
    }

    public String getResId() {
        return resId;
    }

    public String getRoomId() {
        return roomId;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNoShow(boolean noShow) {
        isNoShow = noShow;
    }

    // 예약 상태 반환
    public String getStatus() {
        return status;
    }

    // 노쇼 여부 반환
    public boolean isNoShow() {
        return isNoShow;
    }

    // 참석 인원 반환
    public int getAttendeeCount() {
        return attendeeCount;
    }

    public void setAttendeeCount(int attendeeCount) {
        this.attendeeCount = attendeeCount;
    }

    // 동반 학생 목록 반환
    public List<String> getCompanionStudents() {
        return companionStudents;
    }

    public void setCompanionStudents(
            List<String> companionStudents) {

        this.companionStudents = companionStudents;
    }

    // 사용 목적 반환
    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}