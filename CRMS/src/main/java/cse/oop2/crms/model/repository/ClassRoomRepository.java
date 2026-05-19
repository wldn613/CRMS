package cse.oop2.crms.model.repository;

import cse.oop2.crms.model.dto.ClassRoom;
import java.io.*;
import java.util.*;

// [추가] SFR-106: 강의실 수용 인원 50% 제한 검증을 위해 ClassRoom.txt를 읽는 Repository
// Singleton Pattern 적용 (SFR-106 명세: Singleton Pattern / Sync)
public class ClassRoomRepository {

    private static ClassRoomRepository instance;

    private final String FILE_PATH = "CRMS/data/ClassRoom.txt";
    private List<ClassRoom> memoryData = new ArrayList<>();

    private ClassRoomRepository() {
        load();
    }

    // [SFR-106] Singleton + synchronized: 멀티스레드 환경에서 인스턴스 하나만 보장
    public static synchronized ClassRoomRepository getInstance() {
        if (instance == null) {
            instance = new ClassRoomRepository();
        }
        return instance;
    }

    private void load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    memoryData.add(ClassRoom.fromCsv(line));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ClassRoom> findAll() {
        return new ArrayList<>(memoryData);
    }

    // roomId로 강의실 찾기 (없으면 null 반환)
    public ClassRoom findByRoomId(String roomId) {
        return memoryData.stream()
                .filter(r -> r.getRoomId().equals(roomId))
                .findFirst()
                .orElse(null);
    }
}
