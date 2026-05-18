/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cse.oop2.crms.view;

import cse.oop2.crms.model.dto.Reservation;
import cse.oop2.crms.model.service.ReservationService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentReservationPanel extends JPanel {
    private JTabbedPane tabbedPane;

    // [탭 1] 예약 신청 관련 UI 컴포넌트
    private JTextField txtStudentId;
    private JTextField txtStudentName;
    private JComboBox<String> comboRoom;
    private JComboBox<String> comboDay;
    private JComboBox<Integer> comboStart;
    private JComboBox<Integer> comboEnd;
    private JTextField txtAttendees;
    private JTextArea txtPurpose;
    private JButton btnSubmit;

    // [탭 2] 예약 취소 관련 UI 컴포넌트
    private JTable tableMyReservations;
    private DefaultTableModel tableModel;
    private JButton btnCancelReservation;

    private ReservationService reservationService;
    
    // [R&R 격리 보장] 자체 가상 데이터 상태 관리 리스트 및 감시용 타임맵 세팅
    private List<Reservation> localMyReservations;
    private List<String> cancelledResIds; 
    private List<String> noShowResIds; // 백그라운드 스레드에 의해 변환될 노쇼 ID 추적셋
    private Map<String, Long> reservationTimeMap; // 예약 ID별 실제 신청 시간(밀리초) 기록 타임맵

    public StudentReservationPanel(ReservationService reservationService) {
        this.reservationService = reservationService;
        this.localMyReservations = new ArrayList<>();
        this.cancelledResIds = new ArrayList<>();
        this.noShowResIds = new ArrayList<>();
        this.reservationTimeMap = new HashMap<>();
        
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();

        // 1. 첫 번째 탭: 예약 신청 폼 조립
        JPanel makeReservationPanel = createMakeReservationPanel();
        tabbedPane.addTab("강의실 예약 신청", makeReservationPanel);

        // 2. 두 번째 탭: 본인 예약 취소 화면 조립
        JPanel cancelReservationPanel = createCancelReservationPanel();
        tabbedPane.addTab("내 예약 현황 및 취소", cancelReservationPanel);

        add(tabbedPane, BorderLayout.CENTER);
        
        // 탭이 전환될 때마다 내 가상 목록을 새로 바인딩
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) {
                refreshMyReservationsTable();
            }
        });
    }

    private JPanel createMakeReservationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        
        formPanel.add(new JLabel("  학번 (ID):"));
        txtStudentId = new JTextField();
        formPanel.add(txtStudentId);

        formPanel.add(new JLabel("  성명:"));
        txtStudentName = new JTextField();
        formPanel.add(txtStudentName);

        formPanel.add(new JLabel("  강의실 선택:"));
        comboRoom = new JComboBox<>(new String[]{
            "912 (일반강의실)", "913 (일반강의실)", "914 (일반강의실)",
            "911 (실습강의실)", "915 (실습강의실)", "916 (실습강의실)", "918 (실습강의실)"
        });
        formPanel.add(comboRoom);

        formPanel.add(new JLabel("  예약 요일 선택 (최소 하루 전 제약):"));
        comboDay = new JComboBox<>(new String[]{"월요일", "화요일", "수요일", "목요일", "금요일"});
        formPanel.add(comboDay);

        Integer[] periods = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        formPanel.add(new JLabel("  시작 교시:"));
        comboStart = new JComboBox<>(periods);
        formPanel.add(comboStart);

        formPanel.add(new JLabel("  종료 교시:"));
        comboEnd = new JComboBox<>(periods);
        formPanel.add(comboEnd);

        formPanel.add(new JLabel("  이용 인원수:"));
        txtAttendees = new JTextField();
        formPanel.add(txtAttendees);

        panel.add(formPanel, BorderLayout.NORTH);

        JPanel purposePanel = new JPanel(new BorderLayout());
        purposePanel.add(new JLabel(" 사용 목적 및 동반자 상세 정보:"), BorderLayout.NORTH);
        txtPurpose = new JTextArea(4, 20);
        purposePanel.add(new JScrollPane(txtPurpose), BorderLayout.CENTER);
        panel.add(purposePanel, BorderLayout.CENTER);

        btnSubmit = new JButton("예약 신청하기");
        btnSubmit.setBackground(new Color(41, 128, 185));
        btnSubmit.setForeground(Color.WHITE);
        panel.add(btnSubmit, BorderLayout.SOUTH);

        btnSubmit.addActionListener(this::handleReservation);

        return panel;
    }

    private JPanel createCancelReservationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnNames = {"예약 ID", "강의실", "시작 시간", "종료 시간", "상태"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        tableMyReservations = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableMyReservations);
        panel.add(scrollPane, BorderLayout.CENTER);

        btnCancelReservation = new JButton("선택한 예약 취소하기");
        btnCancelReservation.setBackground(new Color(192, 57, 43));
        btnCancelReservation.setForeground(Color.WHITE);
        panel.add(btnCancelReservation, BorderLayout.SOUTH);

        btnCancelReservation.addActionListener(this::handleCancelReservation);

        return panel;
    }

    private void handleReservation(ActionEvent e) {
        String studentId = txtStudentId.getText().trim();
        String studentName = txtStudentName.getText().trim();
        String selectedRoomRaw = (String) comboRoom.getSelectedItem();
        int startPeriod = (int) comboStart.getSelectedItem();
        int endPeriod = (int) comboEnd.getSelectedItem();
        String attendeesRaw = txtAttendees.getText().trim();
        String purpose = txtPurpose.getText().trim();

        if (studentId.isEmpty() || studentName.isEmpty() || attendeesRaw.isEmpty() || purpose.isEmpty()) {
            JOptionPane.showMessageDialog(this, "모든 필드를 빠짐없이 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (startPeriod > endPeriod) {
            JOptionPane.showMessageDialog(this, "시작 교시가 종료 교시보다 클 수 없습니다.", "시간 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Integer.parseInt(attendeesRaw);
            String roomId = selectedRoomRaw.substring(0, 3);
            String reservationType = selectedRoomRaw.contains("일반강의실") ? "일반 예약" : "사전 예약";

            LocalDate targetDate = LocalDate.now().plusDays(1); 
            LocalDateTime startDateTime = LocalDateTime.of(targetDate, LocalTime.of(startPeriod + 8, 0));
            LocalDateTime endDateTime = LocalDateTime.of(targetDate, LocalTime.of(endPeriod + 8, 50));
            String resId = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            Reservation newReservation = new Reservation(resId, studentId, roomId, startDateTime, endDateTime);
            
            // 팀원 서비스 레이어 메소드 호출
            reservationService.makeReservation(newReservation);

            // 질문자님 가상 보관함에 업데이트 및 실제 신청 시점 타임스탬프 기록
            localMyReservations.add(newReservation);
            reservationTimeMap.put(resId, System.currentTimeMillis());

            JOptionPane.showMessageDialog(this, 
                "학생 [" + reservationType + "]이 신청 완료되었습니다.\n[내 예약 현황] 탭에서 확인 및 취소가 가능합니다.", 
                "예약 완료", JOptionPane.INFORMATION_MESSAGE);
            
            clearFields();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "인원수는 숫자만 입력할 수 있습니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "예약 실패: " + ex.getMessage(), "시스템 오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCancelReservation(ActionEvent e) {
        int selectedRow = tableMyReservations.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "취소할 예약 건을 테이블에서 선택해 주세요.", "선택 요망", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String resId = (String) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 4);

        if ("취소됨".equals(currentStatus) || "노쇼취소".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, "이미 취소 완료 처리된 예약입니다.", "취소 불가", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "선택하신 예약(" + resId + ")을 정말 취소하시겠습니까?", 
            "예약 취소 확인", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            cancelledResIds.add(resId);
            JOptionPane.showMessageDialog(this, "예약이 정상적으로 취소되었습니다.", "취소 성공", JOptionPane.INFORMATION_MESSAGE);
            refreshMyReservationsTable(); 
        }
    }

    /**
     * 실시간 테이블 뷰 상태 매핑 (대기승인 / 취소됨 / 노쇼취소 3단 처리)
     */
    public void refreshMyReservationsTable() {
        tableModel.setRowCount(0); 
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Reservation res : localMyReservations) {
            String statusText = "대기/승인";
            
            if (cancelledResIds.contains(res.getResId())) {
                statusText = "취소됨";
            } else if (noShowResIds.contains(res.getResId())) {
                statusText = "노쇼취소";
            }

            tableModel.addRow(new Object[]{
                res.getResId(),
                res.getRoomId(),
                res.getStart().format(formatter),
                res.getEnd().format(formatter),
                statusText
            });
        }
    }

    private void clearFields() {
        txtStudentId.setText("");
        txtStudentName.setText("");
        txtAttendees.setText("");
        txtPurpose.setText("");
    }

    // --- MainTestApp 백그라운드 스레드와의 연동을 위한 전용 외부 오픈용 Getter API 메서드 구역 ---
    public List<Reservation> getLocalMyReservations() { return localMyReservations; }
    public List<String> getCancelledResIds() { return cancelledResIds; }
    public List<String> getNoShowResIds() { return noShowResIds; }
    public long getReservationRegisterTime(String resId) { return reservationTimeMap.getOrDefault(resId, 0L); }
}