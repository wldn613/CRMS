package cse.oop2.crms.repository;

import cse.oop2.crms.model.*;
import java.util.ArrayList;
import java.util.List;

public class DataRepository {
    private static DataRepository instance;
    
    // 데이터를 임시로 담아둘 리스트 (나중에 파일 IO와 연결됨)
    private List<User> users = new ArrayList<>();
    private List<ClassRoom> rooms = new ArrayList<>();
    private List<Reservation> reservations = new ArrayList<>();

    private DataRepository() {
        // 초기 샘플 데이터를 여기서 몇 개 넣어두면 팀원들이 테스트하기 편합니다.
    }

    public static synchronized DataRepository getInstance() {
        if (instance == null) {
            instance = new DataRepository();
        }
        return instance;
    }

    // Getter들 (Setter는 필요 없음 - 리스트 자체를 관리하므로)
    public List<User> getUsers() { return users; }
    public List<ClassRoom> getRooms() { return rooms; }
    public List<Reservation> getReservations() { return reservations; }
}