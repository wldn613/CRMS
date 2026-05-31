package cse.oop2.crms.view;

import cse.oop2.crms.model.dto.User;
import cse.oop2.crms.model.service.ReservationService;

import deu.cse.lectureroomreservation.view.LoginPanel;
import deu.cse.lectureroomreservation.view.ProfessorReservationPanel;
import deu.cse.lectureroomreservation.view.AssistantManagementPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainContainer;

    private JPanel loginPage;
    private JPanel systemPage;

    private JTabbedPane tabbedPane;

    private JButton btnLogout;
    private JLabel lblUserInfo;

    private ReservationService reservationService;

    public MainFrame() {

        setTitle("강의실 예약 관리 시스템 (CRMS)");
        setSize(1000, 700);
        setLocationRelativeTo(null);

        reservationService = new ReservationService();

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        createLoginPage();
        createSystemPage();

        mainContainer.add(loginPage, "LOGIN");
        mainContainer.add(systemPage, "SYSTEM");

        add(mainContainer);

        cardLayout.show(mainContainer, "LOGIN");

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {

                int result =
                        JOptionPane.showConfirmDialog(
                                MainFrame.this,
                                "프로그램을 종료하시겠습니까?",
                                "종료 확인",
                                JOptionPane.YES_NO_OPTION
                        );

                if(result == JOptionPane.YES_OPTION) {

                    dispose();
                    System.exit(0);
                }
            }
        });

        setVisible(true);
    }

    private void createLoginPage() {

        loginPage = new JPanel(new BorderLayout());

        LoginPanel loginPanel =
                new LoginPanel(this);

        loginPage.add(
                loginPanel,
                BorderLayout.CENTER
        );
    }

    private void createSystemPage() {

        systemPage = new JPanel(new BorderLayout());

        tabbedPane = new JTabbedPane();

        JPanel topPanel =
                new JPanel(new BorderLayout());

        lblUserInfo =
                new JLabel("로그인 사용자");

        lblUserInfo.setBorder(
                BorderFactory.createEmptyBorder(
                        5,
                        10,
                        5,
                        10
                )
        );

        btnLogout =
                new JButton("로그아웃");

        btnLogout.addActionListener(
                e -> logout()
        );

        topPanel.add(
                lblUserInfo,
                BorderLayout.WEST
        );

        topPanel.add(
                btnLogout,
                BorderLayout.EAST
        );

        systemPage.add(
                topPanel,
                BorderLayout.NORTH
        );

        systemPage.add(
                tabbedPane,
                BorderLayout.CENTER
        );
    }

    /**
     * 로그인 성공 시 호출
     */
    public void navigateToPanel(User user) {

        tabbedPane.removeAll();

        lblUserInfo.setText(
                "현재 사용자 : "
                        + user.getName()
                        + " ("
                        + user.getRole()
                        + ")"
        );

        String role =
                user.getRole();

        if(role.equals("STUDENT")) {

            tabbedPane.addTab(
                    "학생 예약",
                    new StudentReservationPanel(
                            reservationService
                    )
            );
        }

        else if(role.equals("PROFESSOR")) {

            tabbedPane.addTab(
                    "교수 예약",
                    new ProfessorReservationPanel(
                            reservationService
                    )
            );
        }

        else if(role.equals("ASSISTANT")) {

            tabbedPane.addTab(
                    "조교 관리",
                    new AssistantManagementPanel(
                            reservationService
                    )
            );
        }

        cardLayout.show(
                mainContainer,
                "SYSTEM"
        );
    }

    /**
     * SFR-203 로그아웃
     */
    private void logout() {

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        "로그아웃 하시겠습니까?",
                        "로그아웃",
                        JOptionPane.YES_NO_OPTION
                );

        if(result == JOptionPane.YES_OPTION) {

            tabbedPane.removeAll();

            lblUserInfo.setText("");

            cardLayout.show(
                    mainContainer,
                    "LOGIN"
            );
        }
    }
}