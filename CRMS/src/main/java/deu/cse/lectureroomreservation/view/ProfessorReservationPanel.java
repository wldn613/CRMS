/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.lectureroomreservation.view;

import deu.cse.lectureroomreservation.control.ReservationController;
import deu.cse.lectureroomreservation.model.DaysOfWeek;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;

/**
 * SFR-103 & SFR-104: 교수용 빈 강의 시간 예약 입력 폼 UI 패널
 * SFR-501: 즐겨찾기 강의실 상단 고정 UI 구현 완료
 * 우선순위 취소 연동 기능 반영: 사용 목적 카테고리 고정화 (보강 / 세미나 / 학생 지도)
 */
public class ProfessorReservationPanel extends javax.swing.JPanel {

    private final ReservationController controller;
    private javax.swing.JButton btnToggleFavorite;
    private List<String> favoriteRooms;
    
    // 학생 패널과 일치시킨 6개 전체 강의실 목록
    private final String[] ALL_ROOMS = { 
        "911 실습강의실", "912 일반강의실", "913 일반강의실", 
        "915 실습강의실", "916 실습강의실", "918 실습강의실" 
    };

    public ProfessorReservationPanel(ReservationController controller) {
        this.controller = controller;
        this.favoriteRooms = new ArrayList<>();
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblTitle = new javax.swing.JLabel();
        lblId = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        lblRoom = new javax.swing.JLabel();
        comboRoom = new javax.swing.JComboBox<>();
        lblDay = new javax.swing.JLabel();
        comboDay = new javax.swing.JComboBox<>();
        lblPeriod = new javax.swing.JLabel();
        comboFromPeriod = new javax.swing.JComboBox<>();
        lblTo = new javax.swing.JLabel();
        comboToPeriod = new javax.swing.JComboBox<>();
        lblPurpose = new javax.swing.JLabel();
        
        // 주관식 입력 텍스트 필드를 명세서 기반 카테고리 콤보박스로 변경
        comboPurpose = new javax.swing.JComboBox<>(); 
        
        lblAttendees = new javax.swing.JLabel();
        spinnerAttendees = new javax.swing.JSpinner(new javax.swing.SpinnerNumberModel(1, 1, 50, 1));
        btnReserve = new javax.swing.JButton();

        lblTitle.setFont(new java.awt.Font("맑은 고딕", 1, 18)); 
        lblTitle.setText("강의실 예약 입력 폼 (교수 전용)");
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER); 
        this.add(lblTitle, BorderLayout.NORTH);

        JPanel formGridPanel = new JPanel(new GridLayout(6, 2, 8, 10));

        lblId.setText("   교수 사번 :");
        formGridPanel.add(lblId);
        formGridPanel.add(txtId);

        lblRoom.setText("   강의실 선택 :");
        formGridPanel.add(lblRoom);
        
        JPanel roomContainer = new JPanel(new BorderLayout(5, 0));
        comboRoom.setModel(new javax.swing.DefaultComboBoxModel<>(ALL_ROOMS));
        btnToggleFavorite = new JButton("고정");
        btnToggleFavorite.setFont(new Font("맑은 고딕", Font.BOLD, 11));
        btnToggleFavorite.setBackground(new Color(241, 196, 15));
        btnToggleFavorite.setForeground(Color.BLACK);
        btnToggleFavorite.addActionListener(e -> handleFavoriteToggle());
        roomContainer.add(comboRoom, BorderLayout.CENTER);
        roomContainer.add(btnToggleFavorite, BorderLayout.EAST);
        formGridPanel.add(roomContainer);

        lblDay.setText("   예약 요일 :");
        formGridPanel.add(lblDay);
        comboDay.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY" }));
        formGridPanel.add(comboDay);

        lblPeriod.setText("   예약 교시 :");
        formGridPanel.add(lblPeriod);
        
        JPanel periodContainer = new JPanel(new BorderLayout(5, 0));
        String[] periods = {"1교시", "2교시", "3교시", "4교시", "5교시", "6교시", "7교시", "8교시", "9교시"};
        comboFromPeriod.setModel(new javax.swing.DefaultComboBoxModel<>(periods));
        lblTo.setText("~");
        lblTo.setHorizontalAlignment(SwingConstants.CENTER);
        comboToPeriod.setModel(new javax.swing.DefaultComboBoxModel<>(periods));
        periodContainer.add(comboFromPeriod, BorderLayout.WEST);
        periodContainer.add(lblTo, BorderLayout.CENTER);
        periodContainer.add(comboToPeriod, BorderLayout.EAST);
        formGridPanel.add(periodContainer);

       
        lblPurpose.setText("   사용 목적 분류 :");
        formGridPanel.add(lblPurpose);
        comboPurpose.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "보강", "세미나", "학생 지도" }));
        formGridPanel.add(comboPurpose);

        lblAttendees.setText("   참석 인원 (명) :");
        formGridPanel.add(lblAttendees);
        formGridPanel.add(spinnerAttendees);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centerWrapper.add(formGridPanel, gbc);
        this.add(centerWrapper, BorderLayout.CENTER);

        btnReserve.setFont(new java.awt.Font("맑은 고딕", 1, 14)); 
        btnReserve.setText("예약 신청 및 즉시 반영");
        btnReserve.setBackground(new Color(41, 128, 185));
        btnReserve.setForeground(Color.WHITE);
        btnReserve.addActionListener(evt -> btnReserveActionPerformed(evt));
        this.add(btnReserve, BorderLayout.SOUTH);
    }

    private void btnReserveActionPerformed(java.awt.event.ActionEvent evt) {
        String profId = txtId.getText().trim();
        String selectedRoomRaw = comboRoom.getSelectedItem().toString();
        DaysOfWeek day = DaysOfWeek.valueOf(comboDay.getSelectedItem().toString());
        int fromPeriod = comboFromPeriod.getSelectedIndex() + 1;
        int toPeriod = comboToPeriod.getSelectedIndex() + 1;
        
        // 콤보박스에서 선택된 텍스트("보강", "세미나", "학생 지도")를 가져옴
        String purpose = comboPurpose.getSelectedItem().toString();
        int attendees = (Integer) spinnerAttendees.getValue();

        if (profId.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "교수 사번을 정확히 입력해 주세요.", "입력 오류", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (fromPeriod > toPeriod) {
            javax.swing.JOptionPane.showMessageDialog(this, "종료 교시는 시작 교시보다 빠를 수 없습니다.", "시간 오류", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        int requestedPeriods = toPeriod - fromPeriod + 1;
        if (requestedPeriods > 3) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "교수 권한으로 신청 가능한 최대 시간은 3시간(3개 교시) 이하입니다.\n현재 선택된 시간: " + requestedPeriods + "시간", 
                "시간 제한 초과 (SFR-103/104)", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (attendees > 50) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "강의실 최대 수용 인원은 50명입니다. 50명을 초과하여 예약할 수 없습니다.", 
                "인원 초과 오류", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        String roomId = selectedRoomRaw.replace("★ ", "").substring(0, 3);

        // 컨트롤러 호출: 이제 이 purpose 데이터("보강" 등)를 기반으로 조원이 학생 예약 취소 백엔드 로직을 연동할 수 있게 됩니다.
        boolean success = controller.makeProfessorReserve(roomId, day, fromPeriod, toPeriod, profId, purpose, attendees);

        if (success) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                selectedRoomRaw.replace("★ ", "") + " 호실 예약이 확정되었습니다.\n(교수 우선순위 정책에 따라 기존 학생 예약이 조정되었을 수 있습니다.)", 
                "예약 성공", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            clearFields();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "예약 실패!\n- 정규 수업 시간표와 겹치거나,\n- 다른 교수의 예약이 이미 해당 시간대에 존재합니다.", "예약 거부", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleFavoriteToggle() {
        String selectedRoomRaw = comboRoom.getSelectedItem().toString();
        if (selectedRoomRaw == null) return;

        String pureRoomName = selectedRoomRaw.replace("★ ", "");

        if (favoriteRooms.contains(pureRoomName)) {
            favoriteRooms.remove(pureRoomName);
            JOptionPane.showMessageDialog(this, "[" + pureRoomName + "] 즐겨찾기 상단 고정이 해제되었습니다.", "즐겨찾기 해제", JOptionPane.INFORMATION_MESSAGE);
        } else {
            favoriteRooms.add(pureRoomName);
            JOptionPane.showMessageDialog(this, "[" + pureRoomName + "] 이제 목록의 최상단에 우선 배치됩니다.", "즐겨찾기 고정 완료", JOptionPane.INFORMATION_MESSAGE);
        }

        refreshRoomComboBox(pureRoomName);
    }

    private void refreshRoomComboBox(String currentSelectedPure) {
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) comboRoom.getModel();
        model.removeAllElements();

        for (String room : ALL_ROOMS) {
            if (favoriteRooms.contains(room)) {
                model.addElement("★ " + room);
            }
        }
        for (String room : ALL_ROOMS) {
            if (!favoriteRooms.contains(room)) {
                model.addElement(room);
            }
        }
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i).replace("★ ", "").equals(currentSelectedPure)) {
                comboRoom.setSelectedIndex(i);
                break;
            }
        }
    }

    private void clearFields() {
        txtId.setText("");
        comboPurpose.setSelectedIndex(0); // 첫 번째 아이템(보강)으로 초기화
        spinnerAttendees.setValue(1);
        comboFromPeriod.setSelectedIndex(0);
        comboToPeriod.setSelectedIndex(0);
    }

    private javax.swing.JButton btnReserve;
    private javax.swing.JComboBox<String> comboDay;
    private javax.swing.JComboBox<String> comboFromPeriod;
    private javax.swing.JComboBox<String> comboToPeriod;
    private javax.swing.JComboBox<String> comboRoom;
    private javax.swing.JComboBox<String> comboPurpose;
    private javax.swing.JLabel lblAttendees;
    private javax.swing.JLabel lblDay;
    private javax.swing.JLabel lblId;
    private javax.swing.JLabel lblPeriod;
    private javax.swing.JLabel lblPurpose;
    private javax.swing.JLabel lblRoom;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTo;
    private javax.swing.JSpinner spinnerAttendees;
    private javax.swing.JTextField txtId;
}