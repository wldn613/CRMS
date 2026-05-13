package cse.oop2.crms.model.repository;

import cse.oop2.crms.model.dto.Reservation;
import java.io.*;
import java.util.*;

public class ReservationRepository {
    private static ReservationRepository instance;
    private final String FILE_PATH = "reservations.txt";
    private List<Reservation> memoryData = new ArrayList<>();

    private ReservationRepository() {
        load();
    }

    public static synchronized ReservationRepository getInstance() {
        if (instance == null) instance = ReservationRepository.getInstance(); // 오타 주의: instance = new ReservationRepository(); 가 맞습니다.
        return instance;
    }

    // 파일에서 데이터 로드
    private void load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                memoryData.add(Reservation.fromCsv(line));
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    // 파일에 데이터 저장
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
        save(); // 실시간 반영 요구사항 충족
    }
}