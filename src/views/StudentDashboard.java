package views;

import controllers.NxenesiController;
import controllers.NotaController;
import controllers.MesuesiController;
import dao.LendaDAO;
import models.User;
import models.Nxenesi;
import models.Nota;
import models.Mesuesi;
import models.Lenda;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentDashboard extends JFrame {
    private final User currentUser;
    private Nxenesi currentStudent;

    // Controllers
    private final NxenesiController studentController = new NxenesiController();
    private final NotaController gradeController = new NotaController();
    private final MesuesiController teacherController = new MesuesiController();
    private final LendaDAO lendaDAO = new LendaDAO();

    // UI Components
    private JTable tblMyGrades;
    private DefaultTableModel modelMyGrades;
    private JLabel lblAverage;

    public StudentDashboard(User user) {
        this.currentUser = user;
        this.currentStudent = studentController.getStudentById(user.getRefId());

        if (currentStudent == null) {
            JOptionPane.showMessageDialog(null, "Të dhënat e nxënësit nuk u gjetën!", "Gabim", JOptionPane.ERROR_MESSAGE);
            currentStudent = new Nxenesi(user.getRefId(), "Nxënës", "I Panjohur", "", null, "N/A", null);
        }

        setTitle("School Management System - Student Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 550);
        setLocationRelativeTo(null);

        // Main Container
        JPanel mainContainer = new JPanel(new BorderLayout());
        UiTheme.stylePanel(mainContainer);

        // Header Panel (Navy Blue)
        JPanel headerPanel = new JPanel(new BorderLayout());
        UiTheme.styleHeaderPanel(headerPanel);
        headerPanel.setPreferredSize(new Dimension(800, 70));
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("School Management System - Nxënësi");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        UiTheme.styleHeaderTitle(lblTitle);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        headerRight.setOpaque(false);

        JLabel lblWelcome = new JLabel("Nxënësi: " + currentStudent.getEmri() + " " + currentStudent.getMbiemri());
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        UiTheme.styleHeaderTitle(lblWelcome);

        JButton btnLogout = new JButton("Çkyçu");
        UiTheme.styleHeaderLogoutButton(btnLogout);
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginView().setVisible(true);
        });

        headerRight.add(lblWelcome);
        headerRight.add(btnLogout);
        headerPanel.add(headerRight, BorderLayout.EAST);
        mainContainer.add(headerPanel, BorderLayout.NORTH);

        // JTabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        UiTheme.styleTabbedPane(tabbedPane);

        // Tab 1: Profili im
        tabbedPane.addTab("Profili Im", createProfilePanel());

        // Tab 2: Notat e mia
        tabbedPane.addTab("Notat e Mia", createMyGradesPanel());

        mainContainer.add(tabbedPane, BorderLayout.CENTER);
        add(mainContainer);

        // Load grades
        refreshMyGrades();
    }

    // ==========================================
    // TAB 1: PROFILE PANEL
    // ==========================================
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        UiTheme.stylePanel(panel);
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);

        // Profile icon card placeholder feel
        JPanel cardHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        cardHeader.setBackground(UiTheme.PRIMARY_LIGHT);
        cardHeader.setPreferredSize(new Dimension(300, 40));
        JLabel lblHeaderCard = new JLabel("Karta e Nxënësit");
        lblHeaderCard.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHeaderCard.setForeground(UiTheme.PRIMARY);
        cardHeader.add(lblHeaderCard);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(cardHeader, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Details
        addProfileRow(panel, "Emri:", currentStudent.getEmri(), 1, gbc);
        addProfileRow(panel, "Mbiemri:", currentStudent.getMbiemri(), 2, gbc);
        addProfileRow(panel, "Email:", currentStudent.getEmail(), 3, gbc);
        addProfileRow(panel, "Klasa:", currentStudent.getKlasa(), 4, gbc);
        addProfileRow(panel, "Data e Lindjes:", currentStudent.getDataLindjes() != null ? currentStudent.getDataLindjes().toString() : "N/A", 5, gbc);
        addProfileRow(panel, "Data e Regjistrimit:", currentStudent.getDataRegjistrimit() != null ? currentStudent.getDataRegjistrimit().toString() : "N/A", 6, gbc);

        return panel;
    }

    private void addProfileRow(JPanel panel, String label, String value, int row, GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.4;
        JLabel lblName = new JLabel(label);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblName.setForeground(UiTheme.TEXT_SECONDARY);
        panel.add(lblName, gbc);

        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.6;
        JLabel lblVal = new JLabel(value != null && !value.isEmpty() ? value : "Pa plotësuar");
        lblVal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblVal.setForeground(UiTheme.TEXT_PRIMARY);
        panel.add(lblVal, gbc);
    }

    // ==========================================
    // TAB 2: MY GRADES
    // ==========================================
    private JPanel createMyGradesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        UiTheme.stylePanel(panel);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("Pasqyra e notave të mia:");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(UiTheme.TEXT_PRIMARY);
        lblTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(lblTitle, BorderLayout.NORTH);

        String[] cols = {"Lënda", "Mësuesi", "Nota", "Lloji", "Data e Dhënies"};
        modelMyGrades = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblMyGrades = new JTable(modelMyGrades);
        UiTheme.styleTable(tblMyGrades);
        JScrollPane gradesScroll = new JScrollPane(tblMyGrades);
        UiTheme.styleScrollPane(gradesScroll);
        panel.add(gradesScroll, BorderLayout.CENTER);

        // Bottom GPA Display
        JPanel bottomPanel = new JPanel(new BorderLayout());
        UiTheme.stylePanel(bottomPanel);
        bottomPanel.setBorder(new EmptyBorder(10, 10, 0, 10));

        lblAverage = new JLabel("Nota Mesatare: 0.00");
        lblAverage.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblAverage.setForeground(UiTheme.PRIMARY);
        bottomPanel.add(lblAverage, BorderLayout.EAST);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshMyGrades() {
        modelMyGrades.setRowCount(0);
        List<Nota> grades = gradeController.getGradesByStudentId(currentStudent.getId());

        for (Nota n : grades) {
            String lendaName = n.getEmriLendes();
            if (lendaName == null) {
                Lenda l = lendaDAO.getLendaById(n.getLendaId());
                if (l != null) lendaName = l.getEmriLendes();
            }

            String teacherName = n.getTeacherFullName();
            if (teacherName.equals("Deleted Teacher") && n.getMesuesiId() != null) {
                Mesuesi m = teacherController.getTeacherById(n.getMesuesiId());
                if (m != null) teacherName = m.getEmri() + " " + m.getMbiemri();
            }

            modelMyGrades.addRow(new Object[]{
                lendaName, teacherName, n.getNota(), n.getLloji(), n.getDataDhenies()
            });
        }

        // Calculate average
        double avg = gradeController.getAverageGradeForStudent(currentStudent.getId());
        lblAverage.setText("Nota Mesatare (GPA): " + String.format("%.2f", avg));
    }
}
