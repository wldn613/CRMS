package deu.cse.lectureroomreservation.view;

import cse.oop2.crms.model.dto.Reservation;
import cse.oop2.crms.model.service.ReservationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AssistantManagementPanel extends JPanel {

    // ===== 공통 ReservationService 연결 =====
    private ReservationService reservationService;

    private JTable reservationTable;
    private DefaultTableModel tableModel;

    private JButton btnRefresh;
    private JButton btnApprove;
    private JButton btnReject;

    public AssistantManagementPanel(
            ReservationService reservationService
    ) {

        // ===== 공통 예약 서비스 연결 =====
        this.reservationService = reservationService;

        setLayout(new BorderLayout(10, 10));

        setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        10,
                        10,
                        10
                )
        );

        // ===== 예약 테이블 컬럼 =====
        String[] columns = {
                "예약 ID",
                "사용자 ID",
                "강의실",
                "시작 시간",
                "종료 시간",
                "상태"
        };

        tableModel =
                new DefaultTableModel(columns, 0) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        reservationTable =
                new JTable(tableModel);

        JScrollPane scrollPane =
                new JScrollPane(reservationTable);

        add(scrollPane, BorderLayout.CENTER);

        // ===== 버튼 패널 =====
        JPanel buttonPanel =
                new JPanel(
                        new GridLayout(1, 3, 10, 0)
                );

        btnRefresh =
                new JButton("새로고침");

        btnApprove =
                new JButton("예약 승인");

        btnReject =
                new JButton("예약 거절");

        // ===== 버튼 이벤트 연결 =====
        btnRefresh.addActionListener(
                this::handleRefresh
        );

        btnApprove.addActionListener(
                this::handleApprove
        );

        btnReject.addActionListener(
                this::handleReject
        );

        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnApprove);
        buttonPanel.add(btnReject);

        add(buttonPanel, BorderLayout.SOUTH);

        // ===== 초기 테이블 로딩 =====
        refreshReservationTable();
    }

    // ===== 예약 목록 새로고침 =====
    private void handleRefresh(ActionEvent e) {

        refreshReservationTable();
    }

    // ===== 예약 승인 처리 =====
    private void handleApprove(ActionEvent e) {

        int selectedRow =
                reservationTable.getSelectedRow();

        if(selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "승인할 예약을 선택해주세요."
            );

            return;
        }

        String reservationId =
                (String) tableModel.getValueAt(
                        selectedRow,
                        0
                );

        Reservation reservation =
                reservationService.findReservationById(
                        reservationId
                );

        if(reservation == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "예약 정보를 찾을 수 없습니다."
            );

            return;
        }

        // ===== 예약 승인 =====
        reservation.setStatus("APPROVED");

        JOptionPane.showMessageDialog(
                this,
                "예약이 승인되었습니다."
        );

        refreshReservationTable();
    }

    // ===== 예약 거절 처리 =====
    private void handleReject(ActionEvent e) {

        int selectedRow =
                reservationTable.getSelectedRow();

        if(selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "거절할 예약을 선택해주세요."
            );

            return;
        }

        String reservationId =
                (String) tableModel.getValueAt(
                        selectedRow,
                        0
                );

        Reservation reservation =
                reservationService.findReservationById(
                        reservationId
                );

        if(reservation == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "예약 정보를 찾을 수 없습니다."
            );

            return;
        }

        // ===== 예약 거절 =====
        reservationService.cancelReservation(reservationId);

        JOptionPane.showMessageDialog(
                this,
                "예약이 취소되었습니다."
        );

        refreshReservationTable();
    }

    // ===== 예약 테이블 갱신 =====
    private void refreshReservationTable() {

        tableModel.setRowCount(0);

        // ===== 공통 예약 데이터 조회 =====
        List<Reservation> reservations =
                reservationService.getAllReservations();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "yyyy-MM-dd HH:mm"
                );

        for(Reservation reservation : reservations) {

            tableModel.addRow(
                    new Object[]{

                            reservation.getResId(),

                            reservation.getUserId(),

                            reservation.getRoomId(),

                            reservation.getStart()
                                    .format(formatter),

                            reservation.getEnd()
                                    .format(formatter),

                            reservation.getStatus()
                    }
            );
        }
    }
}
