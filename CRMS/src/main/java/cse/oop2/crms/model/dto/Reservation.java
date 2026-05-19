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
    // [수정] SFR-203: cancelReason 필드 추가 저장 (기존 7컬럼 → 8컬럼)
    public String toCsv() {
        return String.join("|", resId, userId, roomId, start.toString(), end.toString(), status,
                String.valueOf(isNoShow),
                cancelReason != null ? cancelReason : "");
    }

    // CSV 한 줄을 객체로 복원하는 정적 메소드 (파일 읽기용)
    // [수정] SFR-203: cancelReason 복원 추가 / 기존 7컬럼 파일과의 호환성 유지
    public static Reservation fromCsv(String csv) {
        String[] data = csv.split("\\|", -1);  // -1: 끝 빈 문자열도 포함
        Reservation res = new Reservation(data[0], data[1], data[2],
                LocalDateTime.parse(data[3]), LocalDateTime.parse(data[4]));
        res.setStatus(data[5]);
        res.setNoShow(Boolean.parseBoolean(data[6]));
        // [추가] 8번째 컬럼(cancelReason) - 기존 파일(7컬럼)과 호환되도록 길이 체크
        if (data.length > 7 && !data[7].isEmpty()) {
            res.setCancelReason(data[7]);
        }
        return res;
    }

    // Getter & Setter
    public String getUserId()  { return userId; }
    public String getResId()   { return resId; }
    public String getRoomId()  { return roomId; }
    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd()   { return end; }
    public void setStatus(String status) { this.status = status; }
    public void setNoShow(boolean noShow) { isNoShow = noShow; }

    // [추가] SFR-301: status getter 추가 (승인/거부 처리에 필요)
    public String getStatus() { return status; }

    // [추가] SFR-203: 취소 사유 필드 및 getter/setter
    private String cancelReason;
    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }

    // [추가] SFR-107: 개인/조별 구분 및 예약 인원 필드 (Builder에서 설정)
    private String reservationType = "개인";  // "개인" 또는 "조별"
    private int numberOfPeople     = 1;       // 예약 인원 (SFR-106 50% 검증에 사용)

    public String getReservationType() { return reservationType; }
    public int getNumberOfPeople()     { return numberOfPeople; }

    // [추가] SFR-107: Builder Pattern - 기존 생성자는 그대로 유지하면서 추가 정보를 설정
    public static class Builder {

        // 필수 항목 (기존 Reservation 생성자와 동일)
        private final String resId;
        private final String userId;
        private final String roomId;
        private final LocalDateTime start;
        private final LocalDateTime end;

        // 선택 항목 (기본값)
        private String reservationType = "개인";
        private int numberOfPeople     = 1;

        public Builder(String resId, String userId, String roomId,
                       LocalDateTime start, LocalDateTime end) {
            this.resId  = resId;
            this.userId = userId;
            this.roomId = roomId;
            this.start  = start;
            this.end    = end;
        }

        // [SFR-107] 개인/조별 학습 유형 설정
        public Builder reservationType(String type) {
            this.reservationType = type;
            return this;
        }

        // [SFR-106] 예약 인원 설정 (50% 제한 검증에 사용됨)
        public Builder numberOfPeople(int count) {
            this.numberOfPeople = count;
            return this;
        }

        // 기존 Reservation 생성자를 내부에서 호출하여 객체 생성
        public Reservation build() {
            Reservation res = new Reservation(resId, userId, roomId, start, end);
            res.reservationType = this.reservationType;
            res.numberOfPeople  = this.numberOfPeople;
            return res;
        }
    }
}
