package cse.oop2.crms.model.repository;

import cse.oop2.crms.model.dto.Reservation;
import java.io.*;
import java.util.*;

public class ReservationRepository {
    private static ReservationRepository instance;
    
    // 1. 경로 수정: 현재 파일이 data/reservations.txt에 있다면 아래와 같이 설정합니다.
    // 프로젝트 루트 기준 상대 경로입니다.
    private final String FILE_PATH = "data/reservations.txt"; 
    
    private List<Reservation> memoryData = new ArrayList<>();

    private ReservationRepository() {
        load();
    }

    public static synchronized ReservationRepository getInstance() {
        // 2. 무한 루프 수정: ReservationRepository.getInstance()가 아니라 new로 생성해야 합니다.
        if (instance == null) {
            instance = new ReservationRepository(); 
        }
        return instance;
    }

    private void load() {
        File file = new File(FILE_PATH);
        
        // 파일이 들어있는 폴더(data)가 없으면 생성해주는 로직 (안정성 추가)
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) { // 빈 줄 무시
                    memoryData.add(Reservation.fromCsv(line));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Reservation res : memoryData) {
                pw.println(res.toCsv());
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public List<Reservation> findAll() { return new ArrayList<>(memoryData); }
    
    public void add(Reservation res) {
        memoryData.add(res);
        save();
    }
    
    public void remove(String resId) {

    memoryData.removeIf(
        r -> r.getResId().equals(resId)
    );

    save();
    }
}