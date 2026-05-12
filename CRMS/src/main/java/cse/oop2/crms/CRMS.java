package cse.oop2.crms;

//import cse.oop2.crms.ui.MainFrame;
import javax.swing.SwingUtilities;

public class CRMS {
    public static void main(String[] args) {
        // SFR-601: Java 21 기반 실행 확인
        System.out.println("강의실 예약 시스템을 시작합니다. (JDK 21)");
        
        SwingUtilities.invokeLater(() -> {
            // 팀원 D가 만들 UI 메인 프레임을 여기서 호출합니다.
            // new MainFrame().setVisible(true); 
        });
    }
}