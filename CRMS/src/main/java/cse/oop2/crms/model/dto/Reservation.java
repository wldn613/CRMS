package cse.oop2.crms.model.dto;

import java.time.LocalDateTime;

public class Reservation {
    private String resId;
    private String userId;
    private String roomId;
    private LocalDateTime start;
    private LocalDateTime end;
    private String status;     // "대기", "승인", "거절"
    private boolean isNoShow;

    public Reservation(String resId, String userId, String roomId, LocalDateTime start, LocalDateTime end) {
        this.resId = resId;
        this.userId = userId;
        this.roomId = roomId;
        this.start = start;
        this.end = end;
        this.status = "대기";
        this.isNoShow = false;
    }

    // 파일 저장을 위한 CSV 변환 로직
    public String toCsv() {
        return String.join("|", resId, userId, roomId, start.toString(), end.toString(), status, String.valueOf(isNoShow));
    }

    // CSV 한 줄을 객체로 복원하는 정적 메소드 (파일 읽기용)
    public static Reservation fromCsv(String csv) {
        String[] data = csv.split("\\|");
        Reservation res = new Reservation(data[0], data[1], data[2], LocalDateTime.parse(data[3]), LocalDateTime.parse(data[4]));
        res.setStatus(data[5]);
        res.setNoShow(Boolean.parseBoolean(data[6]));
        return res;
    }

    // Getter & Setter
    public String getResId() { return resId; }
    public String getRoomId() { return roomId; }
    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd() { return end; }
    public void setStatus(String status) { this.status = status; }
    public void setNoShow(boolean noShow) { isNoShow = noShow; }
}