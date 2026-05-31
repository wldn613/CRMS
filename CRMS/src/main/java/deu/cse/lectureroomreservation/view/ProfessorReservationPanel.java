/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package deu.cse.lectureroomreservation.view;

import cse.oop2.crms.model.dto.Reservation;
import cse.oop2.crms.model.service.ReservationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class ProfessorReservationPanel extends JPanel {

    // ===== 공통 Service =====
    private ReservationService reservationService;

    // ===== UI =====
    private JTextField txtProfessorId;
    private JTextField txtProfessorName;

    private JComboBox<String> comboRoom;
    private JComboBox<Integer> comboStart;
    private JComboBox<Integer> comboEnd;

    private JTextField txtAttendees;
    private JTextArea txtPurpose;

    private JButton btnReserve;
    private JButton btnCancel;
    private JTabbedPane tabbedPane;


    // ===== 교수 예약 테이블 =====
    private JTable reservationTable;
    private DefaultTableModel tableModel;

    public ProfessorReservationPanel(ReservationService reservationService) {
        this.reservationService = reservationService;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        tabbedPane = new JTabbedPane();
        JPanel reservationPanel = createReservationPanel();
        JPanel myReservationPanel = createMyReservationPanel();
        
        tabbedPane.addTab(
                "교수 예약 신청",
                reservationPanel
        );
        
        tabbedPane.addTab(
                "교수 예약 현황",
                myReservationPanel
        );
        
        add(tabbedPane, BorderLayout.CENTER);
        
        tabbedPane.addChangeListener(e -> {
            if(tabbedPane.getSelectedIndex() == 1) {
                refreshMyReservations();
            }
        });

        // =========================
        // 이벤트
        // =========================
        btnReserve.addActionListener(this::handleReservation);

        // 초기 로딩
        refreshMyReservations();
    }
    
    private JPanel createReservationPanel() {
        
        JPanel panel = new JPanel(new BorderLayout(10,10));
        
        // 입력 폼
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));

        formPanel.add(new JLabel("교수 ID"));
        txtProfessorId = new JTextField();
        formPanel.add(txtProfessorId);

        formPanel.add(new JLabel("교수 성명"));
        txtProfessorName = new JTextField();
        formPanel.add(txtProfessorName);

        formPanel.add(new JLabel("강의실"));
        comboRoom = new JComboBox<>(new String[]{
                "911", "912", "913", "914", "915", "916", "918"
        });
        formPanel.add(comboRoom);

        Integer[] periods = {1,2,3,4,5,6,7,8,9};

        formPanel.add(new JLabel("시작 교시"));
        comboStart = new JComboBox<>(periods);
        formPanel.add(comboStart);

        formPanel.add(new JLabel("종료 교시"));
        comboEnd = new JComboBox<>(periods);
        formPanel.add(comboEnd);

        formPanel.add(new JLabel("참여 인원"));
        txtAttendees = new JTextField();
        formPanel.add(txtAttendees);

        panel.add(formPanel, BorderLayout.NORTH);

        // 목적 입력
        JPanel purposePanel = new JPanel(new BorderLayout());

        purposePanel.add(new JLabel("사용 목적"), BorderLayout.NORTH);

        txtPurpose = new JTextArea(4, 20);
        purposePanel.add(new JScrollPane(txtPurpose), BorderLayout.CENTER);

        panel.add(purposePanel, BorderLayout.CENTER);

        // 예약 버튼
        btnReserve = new JButton("교수 예약 신청");
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 1));
        buttonPanel.add(btnReserve);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createMyReservationPanel() {
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        String[] columns = {
            "예약ID",
            "강의실",
            "시작",
            "종료",
            "상태"
        };
        
        tableModel = new DefaultTableModel(columns,0) {
            @Override
            public boolean isCellEditable(
                    int raw,
                    int col
            ){
                return false;
            }
        };
        
        reservationTable = new JTable(tableModel);
        
        panel.add(new JScrollPane(reservationTable), BorderLayout.CENTER);
        
        btnCancel = new JButton("예약 취소");

        btnCancel.addActionListener(this::handleCancel);
        
        panel.add(btnCancel, BorderLayout.SOUTH);
        
        return panel;
    }

    // =========================
    // 예약 생성
    // =========================
    private void handleReservation(ActionEvent e) {

        try {
            String professorId = txtProfessorId.getText().trim();
            String professorName = txtProfessorName.getText().trim();

            String roomId = (String) comboRoom.getSelectedItem();

            int startPeriod = (int) comboStart.getSelectedItem();
            int endPeriod = (int) comboEnd.getSelectedItem();

            int attendees = Integer.parseInt(txtAttendees.getText().trim());
            String purpose = txtPurpose.getText().trim();

            if (startPeriod > endPeriod) {
                JOptionPane.showMessageDialog(this, "시간 오류");
                return;
            }

            LocalDate date = LocalDate.now().plusDays(1);

            LocalDateTime start =
                    LocalDateTime.of(date, LocalTime.of(startPeriod + 8, 0));

            LocalDateTime end =
                    LocalDateTime.of(date, LocalTime.of(endPeriod + 8, 50));

            Duration d = Duration.between(start, end);

            if (d.toHours() > 3) {
                JOptionPane.showMessageDialog(this, "최대 3시간 제한");
                return;
            }

            String resId =
                    "PROF-" + UUID.randomUUID()
                            .toString()
                            .substring(0, 8)
                            .toUpperCase();

            Reservation res = new Reservation(
                    resId,
                    professorId,
                    roomId,
                    start,
                    end
            );
            
            res.setAttendeeCount(attendees);
            res.setPurpose(purpose);

            // ⭐ 핵심: 강제예약 포함 서비스 사용
            reservationService.forceProfessorReservation(res);

            JOptionPane.showMessageDialog(this, "예약 완료");

            refreshMyReservations();
            clearFields();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // =========================
    // 예약 취소
    // =========================
    private void handleCancel(ActionEvent e) {

        int row = reservationTable.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this, "선택하세요");
            return;
        }

        String resId = (String) tableModel.getValueAt(row, 0);

        reservationService.cancelReservation(resId);

        refreshMyReservations();
    }

    // =========================
    // 내 예약 조회 (DB 기반)
    // =========================
    private void refreshMyReservations() {

        tableModel.setRowCount(0);

        List<Reservation> list = reservationService.getAllReservations();

        DateTimeFormatter f =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Reservation r : list) {

            String status = "예약";
            
            if(!r.getResId().startsWith("PROF-")) {
                continue;
            }

            tableModel.addRow(new Object[]{
                    r.getResId(),
                    r.getRoomId(),
                    r.getStart().format(f),
                    r.getEnd().format(f),
                    status
            });
        }
    }

    // =========================
    // 초기화
    // =========================
    private void clearFields() {
        // 교수 정보는 유지
        txtAttendees.setText("");
        txtPurpose.setText("");
    }
}