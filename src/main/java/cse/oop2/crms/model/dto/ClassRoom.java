package cse.oop2.crms.model.dto;

// [추가] SFR-106: 수용 인원 50% 제한 검증을 위해 강의실 정보를 담는 DTO 클래스 추가
// ClassRoom.txt 파일 형식: 건물명,강의실호수,수용인원,타입
// 예) 정보관,912,50,강의실
public class ClassRoom {

    private String building;   // 건물명 (예: 정보관)
    private String roomId;     // 강의실 호수 (예: 912)
    private int capacity;      // 총 수용 인원 (예: 50)
    private String type;       // 강의실 타입 (예: 강의실)

    public ClassRoom(String building, String roomId, int capacity, String type) {
        this.building = building;
        this.roomId = roomId;
        this.capacity = capacity;
        this.type = type;
    }

    // ClassRoom.txt 한 줄 → ClassRoom 객체로 변환
    public static ClassRoom fromCsv(String csv) {
        String[] data = csv.split(",");
        return new ClassRoom(
            data[0].trim(),
            data[1].trim(),
            Integer.parseInt(data[2].trim()),
            data[3].trim()
        );
    }

    // [SFR-106] 수용 인원의 50%를 반환 (예약 가능 최대 인원)
    public int getAllowedCapacity() {
        return capacity / 2;
    }

    public String getBuilding()  { return building; }
    public String getRoomId()    { return roomId; }
    public int getCapacity()     { return capacity; }
    public String getType()      { return type; }

    @Override
    public String toString() {
        return building + " " + roomId + "호 (수용:" + capacity + "명, 예약가능:" + getAllowedCapacity() + "명)";
    }
}
