package views;

import controllers.AuthController;
import models.Mesuesi;
import models.Nxenesi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;

public class SignUpView extends JFrame {
    private final AuthController authController = new AuthController();

    // Common fields
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JComboBox<String> cmbRole;

    // Teacher fields
    private JTextField txtTeacherEmri;
    private JTextField txtTeacherMbiemri;
    private JTextField txtTeacherEmail;
    private JTextField txtTeacherTelefon;

    // Student fields
    private JTextField txtStudentEmri;
    private JTextField txtStudentMbiemri;
    private JTextField txtStudentEmail;
    private JTextField txtStudentDataLindjes; // YYYY-MM-DD
    private JTextField txtStudentKlasa;

    private JPanel cardPanel;
    private CardLayout cardLayout;

    public SignUpView() {
        setTitle("School Management System - Sign Up");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header Panel (Navy Blue)
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(new Color(26, 35, 126));
        headerPanel.setPreferredSize(new Dimension(500, 70));
        JLabel lblTitle = new JLabel("Krijo Llogari të Re");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(15, 25, 15, 25));
        formPanel.setBackground(Color.WHITE);

        // Common Fields Panel
        JPanel commonPanel = new JPanel(new GridBagLayout());
        commonPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        // Username
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblUsername = new JLabel("Përdoruesi:");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 13));
        commonPanel.add(lblUsername, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        commonPanel.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblPassword = new JLabel("Fjalëkalimi:");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 13));
        commonPanel.add(lblPassword, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        commonPanel.add(txtPassword, gbc);

        // Confirm Password
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel lblConfirm = new JLabel("Konfirmo Fjalëkalimin:");
        lblConfirm.setFont(new Font("Segoe UI", Font.BOLD, 13));
        commonPanel.add(lblConfirm, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.7;
        txtConfirmPassword = new JPasswordField();
        txtConfirmPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        commonPanel.add(txtConfirmPassword, gbc);

        // Role
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        JLabel lblRole = new JLabel("Roli:");
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 13));
        commonPanel.add(lblRole, gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.7;
        cmbRole = new JComboBox<>(new String[]{"Mësues", "Nxënës"});
        cmbRole.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        commonPanel.add(cmbRole, gbc);

        formPanel.add(commonPanel);

        // Separator
        formPanel.add(Box.createVerticalStrut(10));
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(224, 224, 224));
        formPanel.add(separator);
        formPanel.add(Box.createVerticalStrut(10));

        // Card Panel for Role-Specific fields
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(Color.WHITE);

        // Teacher Card
        JPanel teacherCard = new JPanel(new GridBagLayout());
        teacherCard.setBackground(Color.WHITE);
        GridBagConstraints tGbc = new GridBagConstraints();
        tGbc.fill = GridBagConstraints.HORIZONTAL;
        tGbc.insets = new Insets(6, 6, 6, 6);

        tGbc.gridx = 0; tGbc.gridy = 0; tGbc.weightx = 0.3;
        JLabel lblTEmri = new JLabel("Emri:");
        lblTEmri.setFont(new Font("Segoe UI", Font.BOLD, 13));
        teacherCard.add(lblTEmri, tGbc);
        tGbc.gridx = 1; tGbc.gridy = 0; tGbc.weightx = 0.7;
        txtTeacherEmri = new JTextField();
        txtTeacherEmri.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        teacherCard.add(txtTeacherEmri, tGbc);

        tGbc.gridx = 0; tGbc.gridy = 1; tGbc.weightx = 0.3;
        JLabel lblTMbiemri = new JLabel("Mbiemri:");
        lblTMbiemri.setFont(new Font("Segoe UI", Font.BOLD, 13));
        teacherCard.add(lblTMbiemri, tGbc);
        tGbc.gridx = 1; tGbc.gridy = 1; tGbc.weightx = 0.7;
        txtTeacherMbiemri = new JTextField();
        txtTeacherMbiemri.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        teacherCard.add(txtTeacherMbiemri, tGbc);

        tGbc.gridx = 0; tGbc.gridy = 2; tGbc.weightx = 0.3;
        JLabel lblTEmail = new JLabel("Email:");
        lblTEmail.setFont(new Font("Segoe UI", Font.BOLD, 13));
        teacherCard.add(lblTEmail, tGbc);
        tGbc.gridx = 1; tGbc.gridy = 2; tGbc.weightx = 0.7;
        txtTeacherEmail = new JTextField();
        txtTeacherEmail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        teacherCard.add(txtTeacherEmail, tGbc);

        tGbc.gridx = 0; tGbc.gridy = 3; tGbc.weightx = 0.3;
        JLabel lblTTelefon = new JLabel("Telefon:");
        lblTTelefon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        teacherCard.add(lblTTelefon, tGbc);
        tGbc.gridx = 1; tGbc.gridy = 3; tGbc.weightx = 0.7;
        txtTeacherTelefon = new JTextField();
        txtTeacherTelefon.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        teacherCard.add(txtTeacherTelefon, tGbc);

        cardPanel.add(teacherCard, "Mësues");

        // Student Card
        JPanel studentCard = new JPanel(new GridBagLayout());
        studentCard.setBackground(Color.WHITE);
        GridBagConstraints sGbc = new GridBagConstraints();
        sGbc.fill = GridBagConstraints.HORIZONTAL;
        sGbc.insets = new Insets(6, 6, 6, 6);

        sGbc.gridx = 0; sGbc.gridy = 0; sGbc.weightx = 0.3;
        JLabel lblSEmri = new JLabel("Emri:");
        lblSEmri.setFont(new Font("Segoe UI", Font.BOLD, 13));
        studentCard.add(lblSEmri, sGbc);
        sGbc.gridx = 1; sGbc.gridy = 0; sGbc.weightx = 0.7;
        txtStudentEmri = new JTextField();
        txtStudentEmri.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentCard.add(txtStudentEmri, sGbc);

        sGbc.gridx = 0; sGbc.gridy = 1; sGbc.weightx = 0.3;
        JLabel lblSMbiemri = new JLabel("Mbiemri:");
        lblSMbiemri.setFont(new Font("Segoe UI", Font.BOLD, 13));
        studentCard.add(lblSMbiemri, sGbc);
        sGbc.gridx = 1; sGbc.gridy = 1; sGbc.weightx = 0.7;
        txtStudentMbiemri = new JTextField();
        txtStudentMbiemri.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentCard.add(txtStudentMbiemri, sGbc);

        sGbc.gridx = 0; sGbc.gridy = 2; sGbc.weightx = 0.3;
        JLabel lblSEmail = new JLabel("Email:");
        lblSEmail.setFont(new Font("Segoe UI", Font.BOLD, 13));
        studentCard.add(lblSEmail, sGbc);
        sGbc.gridx = 1; sGbc.gridy = 2; sGbc.weightx = 0.7;
        txtStudentEmail = new JTextField();
        txtStudentEmail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentCard.add(txtStudentEmail, sGbc);

        sGbc.gridx = 0; sGbc.gridy = 3; sGbc.weightx = 0.3;
        JLabel lblSDataLindjes = new JLabel("Data Lindjes (VVVV-MM-DD):");
        lblSDataLindjes.setFont(new Font("Segoe UI", Font.BOLD, 11));
        studentCard.add(lblSDataLindjes, sGbc);
        sGbc.gridx = 1; sGbc.gridy = 3; sGbc.weightx = 0.7;
        txtStudentDataLindjes = new JTextField("2008-01-01");
        txtStudentDataLindjes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentCard.add(txtStudentDataLindjes, sGbc);

        sGbc.gridx = 0; sGbc.gridy = 4; sGbc.weightx = 0.3;
        JLabel lblSKlasa = new JLabel("Klasa (p.sh. X-A):");
        lblSKlasa.setFont(new Font("Segoe UI", Font.BOLD, 13));
        studentCard.add(lblSKlasa, sGbc);
        sGbc.gridx = 1; sGbc.gridy = 4; sGbc.weightx = 0.7;
        txtStudentKlasa = new JTextField();
        txtStudentKlasa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentCard.add(txtStudentKlasa, sGbc);

        cardPanel.add(studentCard, "Nxënës");

        formPanel.add(cardPanel);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonsPanel.setBackground(Color.WHITE);

        JButton btnSubmit = new JButton("Regjistrohu");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSubmit.setBackground(new Color(26, 35, 126));
        btnSubmit.setForeground(new Color(26 , 35 ,126));
        btnSubmit.setPreferredSize(new Dimension(140, 35));
        btnSubmit.setFocusPainted(false);
        btnSubmit.setBorder(BorderFactory.createEmptyBorder());

        JButton btnBack = new JButton("Kthehu pas");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBack.setBackground(Color.WHITE);
        btnBack.setForeground(new Color(26, 35, 126));
        btnBack.setPreferredSize(new Dimension(140, 35));
        btnBack.setFocusPainted(false);
        btnBack.setBorder(BorderFactory.createLineBorder(new Color(26, 35, 126), 1));

        buttonsPanel.add(btnSubmit);
        buttonsPanel.add(btnBack);

        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(buttonsPanel);

        mainPanel.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        add(mainPanel);

        // Role select card swap logic
        cmbRole.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, (String) cmbRole.getSelectedItem());
            }
        });

        // Submit action
        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSubmit();
            }
        });

        // Back to log in action
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginView().setVisible(true);
            }
        });
    }

    private void handleSubmit() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String confirmPassword = new String(txtConfirmPassword.getPassword()).trim();
        String selectedRole = (String) cmbRole.getSelectedItem();

        // 1. Common Validation
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ju lutem mbushni fushat e përdoruesit dhe fjalëkalimit!", "Gabim Validimi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Fjalëkalimet nuk përputhen!", "Gabim Validimi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (authController.isUsernameTaken(username)) {
            JOptionPane.showMessageDialog(this, "Ky emër përdoruesi është i zënë. Ju lutem zgjidhni një tjetër!", "Gabim Validimi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = false;

        if ("Mësues".equals(selectedRole)) {
            String emri = txtTeacherEmri.getText().trim();
            String mbiemri = txtTeacherMbiemri.getText().trim();
            String email = txtTeacherEmail.getText().trim();
            String telefon = txtTeacherTelefon.getText().trim();

            if (emri.isEmpty() || mbiemri.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ju lutem mbushni emrin, mbiemrin dhe email-in e mësuesit!", "Gabim Validimi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Mesuesi mesuesi = new Mesuesi(0, emri, mbiemri, email, telefon);
            success = authController.signUpTeacher(username, password, mesuesi);

        } else if ("Nxënës".equals(selectedRole)) {
            String emri = txtStudentEmri.getText().trim();
            String mbiemri = txtStudentMbiemri.getText().trim();
            String email = txtStudentEmail.getText().trim();
            String dataLindjesStr = txtStudentDataLindjes.getText().trim();
            String klasa = txtStudentKlasa.getText().trim();

            if (emri.isEmpty() || mbiemri.isEmpty() || email.isEmpty() || klasa.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ju lutem mbushni të gjitha fushat e nxënësit!", "Gabim Validimi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Date dataLindjes;
            try {
                dataLindjes = Date.valueOf(dataLindjesStr);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "Formati i datës së lindjes duhet të jetë VVVV-MM-DD (p.sh. 2008-05-12)!", "Gabim Validimi", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Nxenesi nxenesi = new Nxenesi(0, emri, mbiemri, email, dataLindjes, klasa, new Date(System.currentTimeMillis()));
            success = authController.signUpStudent(username, password, nxenesi);
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Regjistrimi u krye me sukses!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new LoginView().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Ndodhi një gabim gjatë regjistrimit. Ju lutem provoni përsëri!", "Gabim", JOptionPane.ERROR_MESSAGE);
        }
    }
}
