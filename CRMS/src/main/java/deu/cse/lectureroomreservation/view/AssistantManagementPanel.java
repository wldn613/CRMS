package deu.cse.lectureroomreservation.view;

import deu.cse.lectureroomreservation.control.ReservationController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * SFR-203: 학생 예약 신청 승인 및 거부 처리 UI
 * SFR-404: 미입실(노쇼) 예약 조회 및 강제 취소 권한 UI
 */
public class AssistantManagementPanel extends javax.swing.JPanel {

    private final ReservationController controller;
    
    // UI 컴포넌트
    private JLabel lblTitle;
    private JTable tableReservations;
    private DefaultTableModel tableModel;
    private JButton btnApprove;
    private JButton btnReject;
    private JButton btnForceCancel;
    private JButton btnRefresh;

    public AssistantManagementPanel(ReservationController controller) {
        this.controller = controller;
        initComponents();
        refreshReservationTable(); // 초기 데이터 로드
    }

    private void initComponents() {
        // 전체 레이아웃 설정
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. 상단 타이틀 구역
        lblTitle = new JLabel("강의실 예약 제어 및 관리 (조교 전용)");
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        lblTitle.setHorizontalAlignment(JLabel.CENTER);
        this.add(lblTitle, BorderLayout.NORTH);

        // 2. 중앙 예약 현황 테이블 구역 (JTable)
        String[] columnNames = {"예약 ID", "신청자 ID", "강의실", "요일/시간", "신청 목적", "인원", "상태"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 테이블 셀 수정 금지
            }
        };
        tableReservations = new JTable(tableModel);
        tableReservations.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(tableReservations);
        this.add(scrollPane, BorderLayout.CENTER);

        // 3. 하단 기능 버튼 구역
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));

        btnApprove = new JButton("예약 승인");
        btnApprove.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        btnApprove.setBackground(new Color(46, 204, 113)); // 초록색 (승인)
        btnApprove.setForeground(Color.WHITE);
        btnApprove.addActionListener(e -> handleApprove());

        btnReject = new JButton("예약 거부");
        btnReject.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        btnReject.setBackground(new Color(231, 76, 60)); // 빨간색 (거부)
        btnReject.setForeground(Color.WHITE);
        btnReject.addActionListener(e -> handleReject());

        btnForceCancel = new JButton("미입실 강제 취소");
        btnForceCancel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        btnForceCancel.setBackground(new Color(230, 126, 34)); // 주황색 (강제 조치)
        btnForceCancel.setForeground(Color.WHITE);
        btnForceCancel.addActionListener(e -> handleForceCancel());

        btnRefresh = new JButton("🔄 새로고침");
        btnRefresh.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        btnRefresh.addActionListener(e -> refreshReservationTable());

        buttonPanel.add(btnApprove);
        buttonPanel.add(btnReject);
        buttonPanel.add(btnForceCancel);
        buttonPanel.add(btnRefresh);

        this.add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * 현재 시스템의 예약 데이터를 컨트롤러로부터 읽어와 테이블을 갱신하는 로직
     */
    private void refreshReservationTable() {
        tableModel.setRowCount(0); // 기존 테이블 행 초기화

        // TODO: 조원이 구현한 전체 예약 리스트를 가져오는 백엔드 메서드와 연동이 필요합니다.
        // 예시 가상 데이터 추가 (작동 구조 확인용)
        tableModel.addRow(new String[]{"RES-A123", "20261234", "912 일반강의실", "MONDAY / 2~3교시", "조별 학습", "4명", "대기 중"});
        tableModel.addRow(new String[]{"RES-B567", "PROF-01", "915 실습강의실", "TUESDAY / 1~3교시", "보강", "25명", "승인 완료"});
    }

    /**
     * SFR-203: 예약 승인 처리 버튼 이벤트
     */
    private void handleApprove() {
        int selectedRow = tableReservations.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "승인할 예약 건을 테이블에서 선택해 주세요.", "선택 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String resId = tableModel.getValueAt(selectedRow, 0).toString();
        String currentStatus = tableModel.getValueAt(selectedRow, 6).toString();

        if (!currentStatus.equals("대기 중")) {
            JOptionPane.showMessageDialog(this, "대기 중인 예약만 승인할 수 있습니다.", "처리 불가", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // TODO: controller.approveReservation(resId); 호출 연동
        JOptionPane.showMessageDialog(this, "예약 ID [" + resId + "] 건의 신청이 정상 승인(확정)되었습니다.", "승인 완료", JOptionPane.INFORMATION_MESSAGE);
        refreshReservationTable();
    }

    /**
     * SFR-203: 예약 거부 처리 버튼 이벤트
     */
    private void handleReject() {
        int selectedRow = tableReservations.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "거부할 예약 건을 테이블에서 선택해 주세요.", "선택 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String resId = tableModel.getValueAt(selectedRow, 0).toString();
        String currentStatus = tableModel.getValueAt(selectedRow, 6).toString();

        if (!currentStatus.equals("대기 중")) {
            JOptionPane.showMessageDialog(this, "대기 중인 예약만 거부할 수 있습니다.", "처리 불가", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 거부 사유 입력받기
        String rejectReason = JOptionPane.showInputDialog(this, "거부 사유를 입력하세요:", "예약 거부 사유 입력", JOptionPane.QUESTION_MESSAGE);
        
        if (rejectReason == null) return; // 취소 버튼을 누른 경우
        if (rejectReason.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "거부 사유는 공백일 수 없습니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // TODO: controller.rejectReservation(resId, rejectReason); 호출 연동
        JOptionPane.showMessageDialog(this, "예약 ID [" + resId + "] 건이 거부 처리되었습니다.\n사유: " + rejectReason, "거부 완료", JOptionPane.INFORMATION_MESSAGE);
        refreshReservationTable();
    }

    /**
     * SFR-404: 미입실(노쇼) 예약 강제 취소 버튼 이벤트
     */
    private void handleForceCancel() {
        int selectedRow = tableReservations.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "강제 취소할 미입실 예약 건을 선택해 주세요.", "선택 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String resId = tableModel.getValueAt(selectedRow, 0).toString();
        String currentStatus = tableModel.getValueAt(selectedRow, 6).toString();

        // 이미 완료되었거나 취소된 건은 제어 불가능하도록 방어
        if (currentStatus.equals("취소됨") || currentStatus.equals("거부됨")) {
            JOptionPane.showMessageDialog(this, "이미 취소되었거나 거부된 예약입니다.", "처리 불가", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "해당 예약자가 실제로 입실하지 않았음을 확인하셨습니까?\n확인 시 즉시 예약이 강제 취소됩니다.", 
            "미입실 강제 취소 의사 확인", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // TODO: controller.forceCancelNoShow(resId); 혹은 체크인 매니저 강제 언레지스터 연동
            JOptionPane.showMessageDialog(this, "예약 ID [" + resId + "] 건이 미입실(노쇼)로 인해 강제 취소 처리되었습니다.", "강제 조치 완료", JOptionPane.INFORMATION_MESSAGE);
            refreshReservationTable();
        }
    }
}