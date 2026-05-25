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

    private StudentReservationPanel studentPanel;
    // 💡 [추가]: 조교 전용 패널을 보관할 멤버 변수 선언
    private AssistantManagementPanel assistantPanel;

    public MainTestApp() {
        setTitle("강의실 예약 시스템 - 런타임 검증 모니터 (SFR-604)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initSubsystems();

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        LoginPanel loginPanel = new LoginPanel(this);
        studentPanel = new StudentReservationPanel(crmsService);
        ProfessorReservationPanel professorPanel = new ProfessorReservationPanel(professorController);
        
        // 💡 [핵심 추가]: 조교 관리 패널 객체를 생성하고 professorController를 주입 연동
        assistantPanel = new AssistantManagementPanel(professorController);

        mainContainer.add(loginPanel, "LOGIN");
        mainContainer.add(studentPanel, "STUDENT");
        mainContainer.add(professorPanel, "PROFESSOR");
        // 💡 [핵심 추가]: 카드 레이아웃 컨테이너에 조교 패널을 "ASSISTANT" 키로 등록!
        mainContainer.add(assistantPanel, "ASSISTANT");

        add(mainContainer);

        startTimeoutMonitoringWorker();

        pack(); 
        setLocationRelativeTo(null); 
    }

    private void initSubsystems() {
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
                boolean hasAutoCancelledEvent = checkAndProcessNoShowTimeout();

                if (hasAutoCancelledEvent) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(MainTestApp.this,
                                "[시스템 알림] 예약 후 20초 이내에 입실이 확인되지 않아\n해당 예약이 자동 취소(노쇼) 처리되었습니다.",
                                "미입실 타임아웃 안내 (SFR-403/404)", JOptionPane.WARNING_MESSAGE);
                    });
                }
            }
        }, 0, 1000);
    }

    private boolean checkAndProcessNoShowTimeout() {
        if (studentPanel == null) return false;

        List<cse.oop2.crms.model.dto.Reservation> reservations = studentPanel.getLocalMyReservations();
        List<String> cancelledIds = studentPanel.getCancelledResIds();
        List<String> noShowIds = studentPanel.getNoShowResIds();
        List<String> checkedInIds = studentPanel.getCheckedInResIds(); 

        long currentTimeMillis = System.currentTimeMillis();
        boolean anyCancelledNow = false;

        for (cse.oop2.crms.model.dto.Reservation res : reservations) {
            String resId = res.getResId();

            if (cancelledIds.contains(resId) || noShowIds.contains(resId) || checkedInIds.contains(resId)) {
                continue;
            }

            long registerTime = studentPanel.getReservationRegisterTime(resId);
            
            if (currentTimeMillis - registerTime > 20000) {
                noShowIds.add(resId);
                anyCancelledNow = true;
                
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
            cardLayout.show(mainContainer, "ASSISTANT"); // 💡 이제 정상 동작합니다!
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainTestApp().setVisible(true);
        });
    }
}