package views;

import controllers.AuthController;
import models.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {
    private final AuthController authController = new AuthController();

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnSignUp;

    public LoginView() {
        setTitle("School Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main Panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        UiTheme.stylePanel(mainPanel);

        // Header Panel (Navy Blue)
        JPanel headerPanel = new JPanel();
        UiTheme.styleHeaderPanel(headerPanel);
        headerPanel.setPreferredSize(new Dimension(450, 80));
        headerPanel.setLayout(new GridBagLayout());
        
        JLabel lblTitle = new JLabel("School Management System");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        UiTheme.styleHeaderTitle(lblTitle);
        headerPanel.add(lblTitle);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form Panel (White background, GridBagLayout for alignment)
        JPanel formPanel = new JPanel(new GridBagLayout());
        UiTheme.stylePanel(formPanel);
        formPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Username
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 14));
        UiTheme.styleFormLabel(lblUsername);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(lblUsername, gbc);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        formPanel.add(txtUsername, gbc);

        // Password
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 14));
        UiTheme.styleFormLabel(lblPassword);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(lblPassword, gbc);

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        formPanel.add(txtPassword, gbc);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        UiTheme.stylePanel(buttonsPanel);

        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        UiTheme.stylePrimaryButton(btnLogin, 8, 20);
        btnLogin.setPreferredSize(new Dimension(110, 35));

        btnSignUp = new JButton("Sign Up");
        btnSignUp.setFont(new Font("Segoe UI", Font.BOLD, 14));
        UiTheme.styleSecondaryButton(btnSignUp);
        btnSignUp.setPreferredSize(new Dimension(110, 35));

        buttonsPanel.add(btnLogin);
        buttonsPanel.add(btnSignUp);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(20, 10, 10, 10);
        formPanel.add(buttonsPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);

        // Action Listeners
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        btnSignUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new SignUpView().setVisible(true);
            }
        });

        // Press Enter to Login
        getRootPane().setDefaultButton(btnLogin);
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ju lutem plotesoni te gjitha fushat!", "Gabim", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = authController.login(username, password);

        if (user != null) {
            JOptionPane.showMessageDialog(this, "Kyçja u krye me sukses!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Close login window

            // Redirect based on role
            SwingUtilities.invokeLater(() -> {
                switch (user.getRole()) {
                    case "admin" -> new AdminDashboard(user).setVisible(true);
                    case "teacher" -> new TeacherDashboard(user).setVisible(true);
                    case "student" -> new StudentDashboard(user).setVisible(true);
                    default -> JOptionPane.showMessageDialog(null, "Rol i panjohur!", "Gabim", JOptionPane.ERROR_MESSAGE);
                }
            });
        } else {
            JOptionPane.showMessageDialog(this, "Emri i perdoruesit ose fjalekalimi eshte i gabuar!", "Gabim Kyçjeje", JOptionPane.ERROR_MESSAGE);
        }
    }
}
