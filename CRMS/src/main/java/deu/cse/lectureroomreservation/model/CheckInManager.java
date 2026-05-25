package deu.cse.lectureroomreservation.model;

import cse.oop2.crms.view.StudentReservationPanel;

public class CheckInManager {
    private static final CheckInManager instance = new CheckInManager();
    
    private volatile boolean isRunning = false;          
    private volatile boolean isCheckInCompleted = false;  
    private int secondsElapsed = 0;             
    private String currentMonitoringResId = "";  

    private CheckInManager() {}

    public static CheckInManager getInstance() {
        return instance;
    }

    public synchronized void startMonitoring(String resId, StudentReservationPanel targetPanel) {
        this.isRunning = false;
        try { Thread.sleep(50); } catch (InterruptedException e) {}
        
        this.isRunning = true;
        this.isCheckInCompleted = false;
        this.secondsElapsed = 0;
        this.currentMonitoringResId = resId;
        
        // 💡 디버깅용 로그: 현재 매니저의 해시코드 주소를 출력하여 동일 객체인지 확인합니다.
        System.out.println("[스레드 시작] 매니저 인스턴스 주소: " + this.hashCode() + " | 대상 ID: " + currentMonitoringResId);
        
        new Thread(() -> {
            try {
                while (secondsElapsed < 20) {
                    // 💡 [개선]: 루프가 시작될 때 플래그를 검사하여 즉시 자폭
                    if (!isRunning || isCheckInCompleted) {
                        System.out.println("[백그라운드 루프 탈출] 안전하게 종료되었습니다. (ID: " + currentMonitoringResId + ")");
                        return; 
                    }

                    Thread.sleep(1000); 
                    secondsElapsed++;
                    System.out.println("[감시 중] 예약ID: " + currentMonitoringResId + " -> " + secondsElapsed + "초 경과...");

                    // 💡 [개선]: sleep 직후에도 플래그를 정밀 검사하여 즉시 탈출
                    if (!isRunning || isCheckInCompleted) {
                        System.out.println("[백그라운드 루프 탈출] 안전하게 종료되었습니다. (ID: " + currentMonitoringResId + ")");
                        return; 
                    }
                }

                // 20초 만료 시 최종 상태 점검
                if (isRunning && !isCheckInCompleted) {
                    System.out.println("[🚨 노쇼 적발] 타임아웃 발생! 예약ID: " + currentMonitoringResId);
                    isRunning = false;
                    
                    if (targetPanel != null) {
                        targetPanel.getNoShowResIds().add(currentMonitoringResId);
                        targetPanel.getLocalMyReservations().removeIf(res -> res.getResId().equals(currentMonitoringResId));
                        java.awt.EventQueue.invokeLater(() -> targetPanel.refreshMyReservationsTable());
                    }
                    
                    java.awt.EventQueue.invokeLater(() -> {
                        javax.swing.JOptionPane.showMessageDialog(null, 
                            "[시스템 경고]\n예약 번호: " + currentMonitoringResId + "\n\n20초 동안 입실 확인이 되지 않아\n해당 예약이 자동 취소(NOSHOW) 처리되었습니다.", 
                            "노쇼 자동 취소 알림", 
                            javax.swing.JOptionPane.ERROR_MESSAGE);
                    });
                }

            } catch (InterruptedException e) {
                System.out.println("[스레드 오류] 강제 중단되었습니다.");
            }
        }).start();
    }

    // 💡 [핵심 조치]: 복잡한 조건 검사를 다 빼버리고, 이 메서드가 호출되면 무조건 플래그를 꺼서 스레드를 즉시 종료시킵니다.
    public synchronized void completeCheckIn(String resId) {
        System.out.println("[원격 종료 신호 수신] 매니저 인스턴스 주소: " + this.hashCode() + " | 요청된 ID: " + resId);
        this.isCheckInCompleted = true;
        this.isRunning = false; 
    }

    public synchronized boolean isMonitoring() {
        return this.isRunning;
    }
}