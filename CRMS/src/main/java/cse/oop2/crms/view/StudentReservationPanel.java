package cse.oop2.crms.view;

import cse.oop2.crms.model.dto.Reservation;
import cse.oop2.crms.model.service.ReservationService;
import deu.cse.lectureroomreservation.model.CheckInManager;

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

    private JTextField txtStudentId;
    private JTextField txtStudentName;
    private JComboBox<String> comboRoom;
    private JButton btnToggleFavorite; // 💡 SFR-501: 즐겨찾기 등록/해제 버튼
    private JComboBox<String> comboDay;
    private JComboBox<Integer> comboStart;
    private JComboBox<Integer> comboEnd;
    private JTextField txtAttendees;
    private JTextArea txtPurpose;
    private JButton btnSubmit;

    private JTable tableMyReservations;
    private DefaultTableModel tableModel;
    private JButton btnCancelReservation;
    private JButton btnCheckIn;

    private ReservationService reservationService;
    
    private List<Reservation> localMyReservations;
    private List<String> cancelledResIds; 
    private List<String> noShowResIds;     
    private List<String> checkedInResIds;  
    private Map<String, Long> reservationTimeMap; 

    // 💡 SFR-501: 즐겨찾기한 강의실 원본 이름을 저장하는 리스트 및 전체 강의실 고유 목록
    private List<String> favoriteRooms;
    private final String[] ALL_ROOMS = {
        "912 (일반강의실)", "913 (일반강의실)", "914 (일반강의실)",
        "911 (실습강의실)", "915 (실습강의실)", "916 (실습강의실)", "918 (실습강의실)"
    };

    public StudentReservationPanel(ReservationService reservationService) {
        this.reservationService = reservationService;
        this.localMyReservations = new ArrayList<>();
        this.cancelledResIds = new ArrayList<>();
        this.noShowResIds = new ArrayList<>();
        this.checkedInResIds = new ArrayList<>(); 
        this.reservationTimeMap = new HashMap<>();
        this.favoriteRooms = new ArrayList<>(); // 초기화
        
        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();

        JPanel makeReservationPanel = createMakeReservationPanel();
        tabbedPane.addTab("강의실 예약 신청", makeReservationPanel);

        JPanel cancelReservationPanel = createCancelReservationPanel();
        tabbedPane.addTab("내 예약 현황 및 관리", cancelReservationPanel);

        add(tabbedPane, BorderLayout.CENTER);
        
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
        
        formPanel.add(new JLabel("   학번 (ID):"));
        txtStudentId = new JTextField();
        formPanel.add(txtStudentId);

        formPanel.add(new JLabel("   성명:"));
        txtStudentName = new JTextField();
        formPanel.add(txtStudentName);

        // 💡 [SFR-501 수정을 위한 레이아웃 재배치]
        formPanel.add(new JLabel("   강의실 선택:"));
        JPanel roomContainerPanel = new JPanel(new BorderLayout(5, 0));
        comboRoom = new JComboBox<>(ALL_ROOMS);
        
        btnToggleFavorite = new JButton("고정");
        btnToggleFavorite.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        btnToggleFavorite.setBackground(new Color(241, 196, 15)); // 노란색 포인트 컬러
        btnToggleFavorite.setForeground(Color.BLACK);
        btnToggleFavorite.addActionListener(e -> handleFavoriteToggle());
        
        roomContainerPanel.add(comboRoom, BorderLayout.CENTER);
        roomContainerPanel.add(btnToggleFavorite, BorderLayout.EAST);
        formPanel.add(roomContainerPanel);

        formPanel.add(new JLabel("   예약 요일 선택 (최소 하루 전 제약):"));
        comboDay = new JComboBox<>(new String[]{"월요일", "화요일", "수요일", "목요일", "금요일"});
        formPanel.add(comboDay);

        Integer[] periods = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        formPanel.add(new JLabel("   시작 교시:"));
        comboStart = new JComboBox<>(periods);
        formPanel.add(comboStart);

        formPanel.add(new JLabel("   종료 교시:"));
        comboEnd = new JComboBox<>(periods);
        formPanel.add(comboEnd);

        formPanel.add(new JLabel("   이용 인원수:"));
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
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tableMyReservations = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableMyReservations);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonLayoutPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        btnCheckIn = new JButton("입실 확인 처리");
        btnCheckIn.setBackground(new Color(39, 174, 96)); 
        btnCheckIn.setForeground(Color.WHITE);
        btnCheckIn.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        btnCheckIn.addActionListener(this::handleCheckIn);
        buttonLayoutPanel.add(btnCheckIn);

        btnCancelReservation = new JButton("선택한 예약 취소하기");
        btnCancelReservation.setBackground(new Color(192, 57, 43));
        btnCancelReservation.setForeground(Color.WHITE);
        btnCancelReservation.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        btnCancelReservation.addActionListener(this::handleCancelReservation);
        buttonLayoutPanel.add(btnCancelReservation);

        panel.add(buttonLayoutPanel, BorderLayout.SOUTH);

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

        // 💡 [버그 2 해결]: 학생 예약 시간 제약 조건 검증 (최대 2시간 / 2개 교시 이하)
        // 예: 1교시~2교시 = 2 - 1 + 1 = 2개 교시 (통과) / 1교시~3교시 = 3 - 1 + 1 = 3개 교시 (차단)
        int requestedPeriods = endPeriod - startPeriod + 1;
        if (requestedPeriods > 2) {
            JOptionPane.showMessageDialog(this, 
                "학생은 공정한 사용을 위해 최대 2시간(2개 교시)까지만 예약할 수 있습니다.\n현재 신청 교시 수: " + requestedPeriods + "개 교시", 
                "시간 제한 초과 (STR-006)", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 💡 [버그 1 해결]: 강의실 최대 수용 인원 50명 제한 검증
            int attendees = Integer.parseInt(attendeesRaw);
            if (attendees > 50) {
                JOptionPane.showMessageDialog(this, 
                    "선택하신 강의실의 최대 수용 인원은 50명입니다.\n50명을 초과하여 예약할 수 없습니다.", 
                    "인원 초과 오류", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (attendees <= 0) {
                JOptionPane.showMessageDialog(this, "이용 인원수는 1명 이상이어야 합니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 💡 SFR-501 대응: 즐겨찾기되어 앞에 '★ '이 붙어있을 경우 순수 강의실 번호 3자리만 파싱하도록 예외 처리
            String cleanRoomName = selectedRoomRaw.replace("★ ", "");
            String roomId = cleanRoomName.substring(0, 3);
            String reservationType = cleanRoomName.contains("일반강의실") ? "일반 예약" : "사전 예약";

            localMyReservations.removeIf(res -> 
                res.getRoomId().equals(roomId) && 
                (cancelledResIds.contains(res.getResId()) || noShowResIds.contains(res.getResId()))
            );

            LocalDate targetDate = LocalDate.now().plusDays(1); 
            LocalDateTime startDateTime = LocalDateTime.of(targetDate, LocalTime.of(startPeriod + 8, 0));
            LocalDateTime endDateTime = LocalDateTime.of(targetDate, LocalTime.of(endPeriod + 8, 50));
            
            String resId = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Reservation newReservation = new Reservation(resId, studentId, roomId, startDateTime, endDateTime);
            
            // 조원 서비스 호출
            reservationService.makeReservation(newReservation);

            localMyReservations.add(newReservation);
            reservationTimeMap.put(resId, System.currentTimeMillis());

            CheckInManager.getInstance().startMonitoring(resId, this);

            JOptionPane.showMessageDialog(this, 
                "학생 [" + reservationType + "]이 신청 완료되었습니다.\n20초 내에 [내 예약 현황] 탭에서 입실 확인을 완료해 주세요.", 
                "예약 완료", JOptionPane.INFORMATION_MESSAGE);
            
            clearFields();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "인원수는 숫자만 입력할 수 있습니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "예약 실패: " + ex.getMessage(), "시스템 오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCheckIn(ActionEvent e) {
        int selectedRow = tableMyReservations.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "입실 확인을 처리할 예약 건을 테이블에서 선택해 주세요.", "선택 요망", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String resId = (String) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 4);

        if ("취소됨".equals(currentStatus) || "노쇼취소".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, "이미 취소되었거나 노쇼 처리된 건은 입실할 수 없습니다.", "입실 불가", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if ("입실완료".equals(currentStatus)) {
            JOptionPane.showMessageDialog(this, "이미 정상적으로 입실이 완료된 예약 건입니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        CheckInManager.getInstance().completeCheckIn(resId); 
        
        // 내 가상 뷰 상태 업데이트
        checkedInResIds.add(resId);                     
        
        JOptionPane.showMessageDialog(this, 
            "정상적으로 입실 확인되었습니다.\n백그라운드 노쇼 감시 스레드가 안전하게 종료됩니다.", 
            "입실 승인 완료", JOptionPane.INFORMATION_MESSAGE);
        
        refreshMyReservationsTable();
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
            localMyReservations.removeIf(res -> res.getResId().equals(resId));
            
            JOptionPane.showMessageDialog(this, "예약이 정상적으로 취소되었습니다.", "취소 성공", JOptionPane.INFORMATION_MESSAGE);
            refreshMyReservationsTable(); 
        }
    }

    public void refreshMyReservationsTable() {
        tableModel.setRowCount(0); 
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Reservation res : localMyReservations) {
            String statusText = "대기/승인";
            
            if (checkedInResIds.contains(res.getResId())) {
                statusText = "입실완료";
            } else if (cancelledResIds.contains(res.getResId())) {
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

    // 💡 SFR-501: 즐겨찾기 버튼 클릭 이벤트 처리 및 콤보박스 아이템 상단 배치 정렬 로직
    private void handleFavoriteToggle() {
        String selectedRoomRaw = (String) comboRoom.getSelectedItem();
        if (selectedRoomRaw == null) return;

        // 원본 강의실 이름 추출 (★ 기호 제거)
        String pureRoomName = selectedRoomRaw.replace("★ ", "");

        if (favoriteRooms.contains(pureRoomName)) {
            favoriteRooms.remove(pureRoomName);
            JOptionPane.showMessageDialog(this, "[" + pureRoomName + "] 즐겨찾기 상단 고정이 해제되었습니다.", "즐겨찾기 해제", JOptionPane.INFORMATION_MESSAGE);
        } else {
            favoriteRooms.add(pureRoomName);
            JOptionPane.showMessageDialog(this, "[" + pureRoomName + "] 자주 이용하는 강의실로 최상단에 고정됩니다.", "즐겨찾기 고정", JOptionPane.INFORMATION_MESSAGE);
        }

        // 콤보박스 목록 갱신 및 상단 재정렬 수행
        refreshRoomComboBox(pureRoomName);
    }

    // 💡 SFR-501: 즐겨찾기 리스트를 기준으로 콤보박스의 항목들을 재조립하는 헬퍼 메서드
    private void refreshRoomComboBox(String currentSelectedPure) {
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) comboRoom.getModel();
        model.removeAllElements();

        // 1단계: 즐겨찾기에 등록된 강의실들을 최상단에 "★ "을 붙여 우선 배치
        for (String room : ALL_ROOMS) {
            if (favoriteRooms.contains(room)) {
                model.addElement("★ " + room);
            }
        }

        // 2단계: 즐겨찾기 되지 않은 나머지 강의실들을 순서대로 배치
        for (String room : ALL_ROOMS) {
            if (!favoriteRooms.contains(room)) {
                model.addElement(room);
            }
        }

        // 3단계: 정렬이 바뀐 후에도 사용자가 이전에 선택했던 강의실 포커스가 풀리지 않도록 재지정
        for (int i = 0; i < model.getSize(); i++) {
            String item = model.getElementAt(i);
            if (item.replace("★ ", "").equals(currentSelectedPure)) {
                comboRoom.setSelectedIndex(i);
                break;
            }
        }
    }

    private void clearFields() {
        txtStudentId.setText("");
        txtStudentName.setText("");
        txtAttendees.setText("");
        txtPurpose.setText("");
    }

    public List<Reservation> getLocalMyReservations() { return localMyReservations; }
    public List<String> getCancelledResIds() { return cancelledResIds; }
    public List<String> getNoShowResIds() { return noShowResIds; }
    public List<String> getCheckedInResIds() { return checkedInResIds; } 
    public long getReservationRegisterTime(String resId) { return reservationTimeMap.getOrDefault(resId, 0L); }
}