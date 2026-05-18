/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.lectureroomreservation.view;

import cse.oop2.crms.model.dto.User;
import cse.oop2.crms.model.service.ReservationService;
import cse.oop2.crms.view.StudentReservationPanel;
import deu.cse.lectureroomreservation.control.ReservationController;
import deu.cse.lectureroomreservation.model.LectureRoom;
import deu.cse.lectureroomreservation.model.ReservationAgent;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * SFR-604: 시스템 동작 여부 실시간 모니터링 및 최상위 프레임워크 (CardLayout 반영)
 */
public class MainTestApp extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;
    
    private ReservationService crmsService;
    private ReservationController professorController;

    // 실시간 감시 스레드가 학생 패널의 로컬 예약 목록을 들여다볼 수 있도록 인스턴스 보관
    private StudentReservationPanel studentPanel;

    public MainTestApp() {
        setTitle("강의실 예약 시스템 - 런타임 검증 모니터 (SFR-604)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 650);
        setLocationRelativeTo(null);

        // 1. 인프라 데이터 및 컨트롤러/서비스 초기화
        initSubsystems();

        // 2. CardLayout 컨테이너 설정
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // 3. 각 역할별 패널 조립 및 카드 등록
        LoginPanel loginPanel = new LoginPanel(this);
        
        // 중요: 감시 스레드와 연동하기 위해 멤버 변수에 할당합니다.
        studentPanel = new StudentReservationPanel(crmsService);
        ProfessorReservationPanel professorPanel = new ProfessorReservationPanel(professorController);

        mainContainer.add(loginPanel, "LOGIN");
        mainContainer.add(studentPanel, "STUDENT");
        mainContainer.add(professorPanel, "PROFESSOR");

        add(mainContainer);

        // 4. SFR-403 / SFR-302 / SFR-404 백그라운드 타임아웃 감시 스레드 구동
        startTimeoutMonitoringWorker();
    }

    private void initSubsystems() {
        // 강의실 초기화 (정원 공통 30명 설정)
        List<LectureRoom> rooms = new ArrayList<>();
        rooms.add(new LectureRoom("912", 30));
        rooms.add(new LectureRoom("913", 30));
        rooms.add(new LectureRoom("914", 30));
        rooms.add(new LectureRoom("911", 30));
        rooms.add(new LectureRoom("915", 30));
        rooms.add(new LectureRoom("916", 30));
        rooms.add(new LectureRoom("918", 30));

        ReservationAgent agent = new ReservationAgent(rooms);
        professorController = new ReservationController(agent);
        crmsService = new ReservationService();
    }

    /**
     * SFR-403 & 302 & 404: 20초 내 미입실 타임아웃 체크 및 자동 취소 알림 백그라운드 워커
     */
    private void startTimeoutMonitoringWorker() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // 실시간으로 학생 패널의 가상 리스트를 순회하며 20초 타임아웃 여부를 감시합니다.
                boolean hasAutoCancelledEvent = checkAndProcessNoShowTimeout();

                if (hasAutoCancelledEvent) {
                    // 로그인 여부와 관계없이 공통으로 알림 메시지를 화면에 강제 송출합니다.
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(MainTestApp.this,
                                "[시스템 알림] 예약 후 20초 이내에 입실이 확인되지 않아\n해당 예약이 자동 취소(노쇼) 처리되었습니다.",
                                "미입실 타임아웃 안내 (SFR-403/404)", JOptionPane.WARNING_MESSAGE);
                    });
                }
            }
        }, 0, 1000); // 1초마다 주기적으로 백그라운드 탐색 실시
    }

    /**
     * 피드백 반영: 신청 시간 기준 20초 경과 시 자동 노쇼 변환 로직
     */
    private boolean checkAndProcessNoShowTimeout() {
        if (studentPanel == null) return false;

        // 학생 패널 내부의 가상 예약 목록을 가져옵니다.
        List<cse.oop2.crms.model.dto.Reservation> reservations = studentPanel.getLocalMyReservations();
        List<String> cancelledIds = studentPanel.getCancelledResIds();
        List<String> noShowIds = studentPanel.getNoShowResIds();

        long currentTimeMillis = System.currentTimeMillis();
        boolean anyCancelledNow = false;

        for (cse.oop2.crms.model.dto.Reservation res : reservations) {
            String resId = res.getResId();

            // 이미 사용자가 수동 취소했거나, 이미 노쇼 처리가 완료된 건은 감시에서 패스합니다.
            if (cancelledIds.contains(resId) || noShowIds.contains(resId)) {
                continue;
            }

            // [핵심 매커니즘]: 예약 객체의 생성 시점(startDateTime 변조값 등 설계 매핑 대신, 
            // 안전한 런타임 검증을 위해 학생 패널이 기록해 둔 '실제 신청 타임스탬프'와 대조합니다)
            long registerTime = studentPanel.getReservationRegisterTime(resId);
            
            // 신청한 지 20초(20000ms)가 지났는지 체크
            if (currentTimeMillis - registerTime > 20000) {
                // 20초가 지났는데 입실 확인이 안 되었으므로 시스템이 노쇼 리스트로 강제 등록 (SFR-403, 404)
                noShowIds.add(resId);
                anyCancelledNow = true;
                
                // 테이블 뷰에 실시간 반영하기 위해 UI 스레드에서 갱신 메서드 호출
                SwingUtilities.invokeLater(() -> {
                    studentPanel.refreshMyReservationsTable();
                });
            }
        }

        return anyCancelledNow; 
    }

    /**
     * 인증 성공 후 해당 사용자의 권한 카드 화면으로 전환하는 메소드
     */
    public void navigateToPanel(User authenticatedUser) {
        String role = authenticatedUser.getRole();
        if ("STUDENT".equals(role)) {
            cardLayout.show(mainContainer, "STUDENT");
        } else if ("PROFESSOR".equals(role)) {
            cardLayout.show(mainContainer, "PROFESSOR");
        } else if ("ASSISTANT".equals(role)) {
            cardLayout.show(mainContainer, "ASSISTANT");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainTestApp().setVisible(true);
        });
    }
}