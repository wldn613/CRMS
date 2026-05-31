/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package deu.cse.lectureroomreservation.view;

import cse.oop2.crms.model.dto.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import cse.oop2.crms.view.MainFrame;
import cse.oop2.crms.schedule.Notification;
import cse.oop2.crms.schedule.ScheduleAndFileManager;
import java.util.List;

public class LoginPanel extends JPanel {
    private JTextField txtId;
    private JPasswordField txtPassword;
    private JRadioButton rdoStudent;
    private JRadioButton rdoProfessor;
    private JRadioButton rdoAssistant;
    private JButton btnLogin;

    private MainFrame mainFrame;
    private Map<String, User> mockUserStorage;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("강의실 예약 시스템 로그인"));

        // 가상 계정 데이터 적재
        mockUserStorage = new HashMap<>();
        mockUserStorage.put("stud1", new User("stud1", "1234", "김학생", "STUDENT"));
        mockUserStorage.put("prof1", new User("prof1", "1234", "김교수", "PROFESSOR"));
        mockUserStorage.put("ast1", new User("ast1", "1234", "이조교", "ASSISTANT"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 역할 선택 레이블 및 라디오 버튼 그룹
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("역할 선택:"), gbc);

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rdoStudent = new JRadioButton("학생", true);
        rdoProfessor = new JRadioButton("교수");
        rdoAssistant = new JRadioButton("조교");
        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(rdoStudent);
        roleGroup.add(rdoProfessor);
        roleGroup.add(rdoAssistant);
        rolePanel.add(rdoStudent);
        rolePanel.add(rdoProfessor);
        rolePanel.add(rdoAssistant);

        gbc.gridx = 1;
        add(rolePanel, gbc);

        // ID 입력
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("아이디(학번/사번):"), gbc);
        txtId = new JTextField(15);
        gbc.gridx = 1;
        add(txtId, gbc);

        // PW 입력
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("비밀번호:"), gbc);
        txtPassword = new JPasswordField(15);
        gbc.gridx = 1;
        add(txtPassword, gbc);

        // 로그인 버튼
        btnLogin = new JButton("로그인");
        btnLogin.setBackground(new Color(52, 152, 219));
        btnLogin.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        add(btnLogin, gbc);

        btnLogin.addActionListener(this::handleLogin);
    }

    private void handleLogin(ActionEvent e) {
        String inputId = txtId.getText().trim();
        String inputPw = new String(txtPassword.getPassword()).trim();

        String selectedRole = "STUDENT";
        if (rdoProfessor.isSelected()) selectedRole = "PROFESSOR";
        if (rdoAssistant.isSelected()) selectedRole = "ASSISTANT";

        if (inputId.isEmpty() || inputPw.isEmpty()) {
            JOptionPane.showMessageDialog(this, "아이디와 비밀번호를 모두 입력해 주세요.", "입력 요망", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = mockUserStorage.get(inputId);

        // 입력 데이터 검증 검사
        if (user != null && user.getPassword().equals(inputPw) && user.getRole().equals(selectedRole)) {
            JOptionPane.showMessageDialog(this, user.getName() + "님 환영합니다.", "로그인 성공", JOptionPane.INFORMATION_MESSAGE);
            
            // 메인 프레임의 navigateToPanel이 이제 ASSISTANT 카드 화면도 정상적으로 스위칭 제어합니다.
            mainFrame.navigateToPanel(user);
            List<Notification> notifications =
                    ScheduleAndFileManager
                            .getInstance()
                            .getNotifications(inputId);
            for (Notification notification
                    : notifications) {
                JOptionPane.showMessageDialog(
                        this,
                        notification.getMessage(),
                        "예약 변경 알림",
                        JOptionPane.WARNING_MESSAGE
                );
            }

ScheduleAndFileManager
        .getInstance()
        .clearNotifications(inputId);
            clearLoginFields();
        } else {
            JOptionPane.showMessageDialog(this, "일치하는 회원 정보가 없거나 역할 선택이 잘못되었습니다.", "인증 실패", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearLoginFields() {
        txtId.setText("");
        txtPassword.setText("");
        rdoStudent.setSelected(true);
    }
}