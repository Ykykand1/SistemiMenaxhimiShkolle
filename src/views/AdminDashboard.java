package views;

import controllers.NxenesiController;
import controllers.MesuesiController;
import controllers.NotaController;
import dao.LendaDAO;
import models.User;
import models.Nxenesi;
import models.Mesuesi;
import models.Lenda;
import models.Nota;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboard extends JFrame {
    private final User currentUser;

    // Controllers & DAOs
    private final NxenesiController studentController = new NxenesiController();
    private final MesuesiController teacherController = new MesuesiController();
    private final NotaController gradeController = new NotaController();
    private final LendaDAO lendaDAO = new LendaDAO();

    // Tables & Models
    private JTable tblStudents;
    private DefaultTableModel modelStudents;

    private JTable tblTeachers;
    private DefaultTableModel modelTeachers;
    private JPanel panelTeacherSubjects; // Checklist panel for teacher subjects
    private List<JCheckBox> chkSubjectsList = new ArrayList<>();
    private int selectedTeacherIdForAssignment = -1;

    private JTable tblSubjects;
    private DefaultTableModel modelSubjects;

    private JTable tblGrades;
    private DefaultTableModel modelGrades;
    
    // Grade filter components
    private JComboBox<String> cmbFilterStudent;
    private JComboBox<String> cmbFilterSubject;
    private JComboBox<String> cmbFilterType;

    public AdminDashboard(User user) {
        this.currentUser = user;
        setTitle("School Management System - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Main Container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.WHITE);

        // Header Panel (Navy Blue)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(26, 35, 126));
        headerPanel.setPreferredSize(new Dimension(1000, 70));
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("School Management System - Admin Dashboard");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        headerRight.setOpaque(false);
        
        JLabel lblWelcome = new JLabel("Mirëseerdhe, " + user.getUsername() + "!");
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblWelcome.setForeground(Color.WHITE);
        
        JButton btnLogout = new JButton("Çkyçu");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(Color.WHITE);
        btnLogout.setForeground(new Color(26, 35, 126));
        btnLogout.setFocusPainted(false);
        btnLogout.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
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

        // Tab 1 – Nxënësit
        tabbedPane.addTab("Nxënësit", createStudentsPanel());

        // Tab 2 – Mësuesit
        tabbedPane.addTab("Mësuesit", createTeachersPanel());

        // Tab 3 – Lëndët
        tabbedPane.addTab("Lëndët", createSubjectsPanel());

        // Tab 4 – Notat
        tabbedPane.addTab("Notat", createGradesPanel());

        mainContainer.add(tabbedPane, BorderLayout.CENTER);
        add(mainContainer);

        // Load data on startup
        refreshStudentsTable();
        refreshTeachersTable();
        refreshSubjectsTable();
        loadGradeFilters();
        refreshGradesTable();
    }

    private void styleTable(JTable table) {
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(26, 35, 126));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setGridColor(new Color(224, 224, 224));
        table.setSelectionBackground(new Color(224, 242, 241));
        table.setSelectionForeground(Color.BLACK);
    }

    // ==========================================
    // TAB 1: STUDENTS PANEL
    // ==========================================
    private JPanel createStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Top Search Bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchBar.setBackground(Color.WHITE);
        
        JLabel lblSearch = new JLabel("Kërko sipas Emrit/Klasës:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JTextField txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JButton btnSearch = new JButton("Kërko");
        btnSearch.setBackground(new Color(26, 35, 126));
        btnSearch.setForeground(Color.BLACK);
        btnSearch.setFocusPainted(false);
        
        JButton btnClear = new JButton("Pastro");
        btnClear.setBackground(Color.WHITE);
        btnClear.setForeground(new Color(26, 35, 126));
        btnClear.setBorder(BorderFactory.createLineBorder(new Color(26, 35, 126), 1));
        btnClear.setFocusPainted(false);

        searchBar.add(lblSearch);
        searchBar.add(txtSearch);
        searchBar.add(btnSearch);
        searchBar.add(btnClear);
        panel.add(searchBar, BorderLayout.NORTH);

        // JTable
        String[] cols = {"ID", "Emri", "Mbiemri", "Email", "Klasa", "Data e Lindjes", "Data e Regjistrimit"};
        modelStudents = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblStudents = new JTable(modelStudents);
        styleTable(tblStudents);
        JScrollPane scroll = new JScrollPane(tblStudents);
        panel.add(scroll, BorderLayout.CENTER);

        // Buttons Panel (CRUD)
        JPanel crudPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        crudPanel.setBackground(Color.WHITE);
        
        JButton btnAdd = new JButton("Shto Nxënës");
        JButton btnEdit = new JButton("Ndrysho");
        JButton btnDelete = new JButton("Fshi");

        for (JButton b : new JButton[]{btnAdd, btnEdit, btnDelete}) {
            b.setFont(new Font("Segoe UI", Font.BOLD, 12));
            b.setFocusPainted(false);
        }
        btnAdd.setBackground(new Color(26, 35, 126));
        btnAdd.setForeground(Color.BLACK);
        btnEdit.setBackground(new Color(26, 35, 126));
        btnEdit.setForeground(Color.BLACK);
        btnDelete.setBackground(new Color(211, 47, 47)); // Red
        btnDelete.setForeground(Color.BLACK);

        crudPanel.add(btnAdd);
        crudPanel.add(btnEdit);
        crudPanel.add(btnDelete);
        panel.add(crudPanel, BorderLayout.SOUTH);

        // Action Listeners
        btnSearch.addActionListener(e -> {
            String query = txtSearch.getText().trim();
            if (!query.isEmpty()) {
                List<Nxenesi> result = studentController.searchStudents(query);
                populateStudentsTable(result);
            } else {
                refreshStudentsTable();
            }
        });

        btnClear.addActionListener(e -> {
            txtSearch.setText("");
            refreshStudentsTable();
        });

        btnAdd.addActionListener(e -> showAddStudentDialog());
        btnEdit.addActionListener(e -> showEditStudentDialog());
        btnDelete.addActionListener(e -> handleDeleteStudent());

        return panel;
    }

    private void refreshStudentsTable() {
        populateStudentsTable(studentController.getAllStudents());
    }

    private void populateStudentsTable(List<Nxenesi> list) {
        modelStudents.setRowCount(0);
        for (Nxenesi n : list) {
            modelStudents.addRow(new Object[]{
                n.getId(), n.getEmri(), n.getMbiemri(), n.getEmail(),
                n.getKlasa(), n.getDataLindjes(), n.getDataRegjistrimit()
            });
        }
    }

    private void showAddStudentDialog() {
        JDialog dlg = new JDialog(this, "Shto Nxënës të Ri", true);
        dlg.setSize(380, 350);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new GridBagLayout());
        dlg.getContentPane().setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        JTextField txtEmri = new JTextField(15);
        JTextField txtMbiemri = new JTextField(15);
        JTextField txtEmail = new JTextField(15);
        JTextField txtKlasa = new JTextField(15);
        JTextField txtDataLindjes = new JTextField(15); // YYYY-MM-DD
        txtDataLindjes.setText("2008-01-01");

        addFieldToDialog(dlg, "Emri:", txtEmri, 0, gbc);
        addFieldToDialog(dlg, "Mbiemri:", txtMbiemri, 1, gbc);
        addFieldToDialog(dlg, "Email:", txtEmail, 2, gbc);
        addFieldToDialog(dlg, "Klasa:", txtKlasa, 3, gbc);
        addFieldToDialog(dlg, "Data Lindjes (VVVV-MM-DD):", txtDataLindjes, 4, gbc);

        JButton btnSave = new JButton("Ruaj");
        btnSave.setBackground(new Color(26, 35, 126));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        dlg.add(btnSave, gbc);

        btnSave.addActionListener(e -> {
            String emri = txtEmri.getText().trim();
            String mbiemri = txtMbiemri.getText().trim();
            String email = txtEmail.getText().trim();
            String klasa = txtKlasa.getText().trim();
            String dataLindjesStr = txtDataLindjes.getText().trim();

            if (emri.isEmpty() || mbiemri.isEmpty() || email.isEmpty() || klasa.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Të gjitha fushat duhet të plotësohen!", "Gabim", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Date dataLindjes;
            try {
                dataLindjes = Date.valueOf(dataLindjesStr);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dlg, "Formati i datës së lindjes është i gabuar (VVVV-MM-DD)!", "Gabim", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Nxenesi n = new Nxenesi(0, emri, mbiemri, email, dataLindjes, klasa, new Date(System.currentTimeMillis()));
            
            // Also need to register a user credential for the student. Since this is admin adding a student,
            // we will default their username to 'student_' + email (before @) or simply their name, and password to 'nxenesi123'.
            // Let's create user login info: Username = lowercase name, Password = student123.
            String defUsername = (emri.substring(0, Math.min(3, emri.length())) + mbiemri.substring(0, Math.min(3, mbiemri.length()))).toLowerCase() + (int)(Math.random() * 100);
            String defPassword = "student123";

            controllers.AuthController authController = new controllers.AuthController();
            if (authController.signUpStudent(defUsername, defPassword, n)) {
                JOptionPane.showMessageDialog(this, "Nxënësi u shtua me sukses!\nUsername: " + defUsername + "\nPassword: " + defPassword, "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                refreshStudentsTable();
                loadGradeFilters();
            } else {
                JOptionPane.showMessageDialog(dlg, "Ndodhi një gabim gjatë shtimit. Sigurohuni që email është unik!", "Gabim", JOptionPane.ERROR_MESSAGE);
            }
        });

        dlg.setVisible(true);
    }

    private void showEditStudentDialog() {
        int selectedRow = tblStudents.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Ju lutem zgjidhni një nxënës për të ndryshuar!", "Gabim", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tblStudents.getValueAt(selectedRow, 0);
        Nxenesi n = studentController.getStudentById(id);
        if (n == null) return;

        JDialog dlg = new JDialog(this, "Ndrysho Nxënës", true);
        dlg.setSize(380, 320);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new GridBagLayout());
        dlg.getContentPane().setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        JTextField txtEmri = new JTextField(n.getEmri(), 15);
        JTextField txtMbiemri = new JTextField(n.getMbiemri(), 15);
        JTextField txtEmail = new JTextField(n.getEmail(), 15);
        JTextField txtKlasa = new JTextField(n.getKlasa(), 15);
        JTextField txtDataLindjes = new JTextField(n.getDataLindjes().toString(), 15);

        addFieldToDialog(dlg, "Emri:", txtEmri, 0, gbc);
        addFieldToDialog(dlg, "Mbiemri:", txtMbiemri, 1, gbc);
        addFieldToDialog(dlg, "Email:", txtEmail, 2, gbc);
        addFieldToDialog(dlg, "Klasa:", txtKlasa, 3, gbc);
        addFieldToDialog(dlg, "Data Lindjes (VVVV-MM-DD):", txtDataLindjes, 4, gbc);

        JButton btnSave = new JButton("Ruaj Ndryshimet");
        btnSave.setBackground(new Color(26, 35, 126));
        btnSave.setForeground(Color.BLACK);
        btnSave.setFocusPainted(false);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        dlg.add(btnSave, gbc);

        btnSave.addActionListener(e -> {
            String emri = txtEmri.getText().trim();
            String mbiemri = txtMbiemri.getText().trim();
            String email = txtEmail.getText().trim();
            String klasa = txtKlasa.getText().trim();
            String dataLindjesStr = txtDataLindjes.getText().trim();

            if (emri.isEmpty() || mbiemri.isEmpty() || email.isEmpty() || klasa.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Të gjitha fushat duhet të plotësohen!", "Gabim", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Date dataLindjes;
            try {
                dataLindjes = Date.valueOf(dataLindjesStr);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dlg, "Formati i datës së lindjes është i gabuar (VVVV-MM-DD)!", "Gabim", JOptionPane.WARNING_MESSAGE);
                return;
            }

            n.setEmri(emri);
            n.setMbiemri(mbiemri);
            n.setEmail(email);
            n.setKlasa(klasa);
            n.setDataLindjes(dataLindjes);

            if (studentController.updateStudent(n)) {
                JOptionPane.showMessageDialog(this, "Të dhënat u ndryshuan me sukses!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                refreshStudentsTable();
                loadGradeFilters();
            } else {
                JOptionPane.showMessageDialog(dlg, "Ndodhi një gabim gjatë ndryshimit!", "Gabim", JOptionPane.ERROR_MESSAGE);
            }
        });

        dlg.setVisible(true);
    }

    private void handleDeleteStudent() {
        int selectedRow = tblStudents.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Ju lutem zgjidhni një nxënës për të fshirë!", "Gabim", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tblStudents.getValueAt(selectedRow, 0);
        String emri = (String) tblStudents.getValueAt(selectedRow, 1);
        String mbiemri = (String) tblStudents.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "A jeni të sigurtë që dëshironi të fshini nxënësin " + emri + " " + mbiemri + "?\n" +
            "Kjo do të fshijë llogarinë e tij dhe të gjitha notat e tij!",
            "Konfirmo Fshirjen",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (studentController.deleteStudent(id)) {
                JOptionPane.showMessageDialog(this, "Nxënësi u fshi me sukses!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                refreshStudentsTable();
                loadGradeFilters();
                refreshGradesTable();
            } else {
                JOptionPane.showMessageDialog(this, "Fshirja dështoi!", "Gabim", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addFieldToDialog(JDialog dlg, String labelText, JTextField textField, int row, GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dlg.add(lbl, gbc);

        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.7;
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dlg.add(textField, gbc);
    }

    // ==========================================
    // TAB 2: TEACHERS PANEL
    // ==========================================
    private JPanel createTeachersPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // JTable left
        String[] cols = {"ID", "Emri", "Mbiemri", "Email", "Telefon"};
        modelTeachers = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblTeachers = new JTable(modelTeachers);
        styleTable(tblTeachers);
        JScrollPane scrollTeachers = new JScrollPane(tblTeachers);

        // Right side panel for subject assignments (using scroll panel with checkboxes)
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setPreferredSize(new Dimension(300, 0));
        rightPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(26, 35, 126), 1),
            "Lëndët e Mësuesit",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 13),
            new Color(26, 35, 126)
        ));

        panelTeacherSubjects = new JPanel();
        panelTeacherSubjects.setLayout(new BoxLayout(panelTeacherSubjects, BoxLayout.Y_AXIS));
        panelTeacherSubjects.setBackground(Color.WHITE);
        JScrollPane scrollSubjects = new JScrollPane(panelTeacherSubjects);
        rightPanel.add(scrollSubjects, BorderLayout.CENTER);

        JButton btnSaveAssignment = new JButton("Ruaj Lëndët");
        btnSaveAssignment.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSaveAssignment.setBackground(new Color(26, 35, 126));
        btnSaveAssignment.setForeground(Color.WHITE);
        btnSaveAssignment.setFocusPainted(false);
        btnSaveAssignment.setEnabled(false);
        rightPanel.add(btnSaveAssignment, BorderLayout.SOUTH);

        // Split pane to divide teachers list and assignment panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollTeachers, rightPanel);
        splitPane.setDividerLocation(650);
        splitPane.setBackground(Color.WHITE);
        panel.add(splitPane, BorderLayout.CENTER);

        // Buttons Panel (CRUD)
        JPanel crudPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        crudPanel.setBackground(Color.WHITE);
        
        JButton btnAdd = new JButton("Shto Mësues");
        JButton btnEdit = new JButton("Ndrysho");
        JButton btnDelete = new JButton("Fshi");

        for (JButton b : new JButton[]{btnAdd, btnEdit, btnDelete}) {
            b.setFont(new Font("Segoe UI", Font.BOLD, 12));
            b.setFocusPainted(false);
        }
        btnAdd.setBackground(new Color(26, 35, 126));
        btnAdd.setForeground(Color.BLACK);
        btnEdit.setBackground(new Color(26, 35, 126));
        btnEdit.setForeground(Color.BLACK);
        btnDelete.setBackground(new Color(211, 47, 47));
        btnDelete.setForeground(Color.BLACK);

        crudPanel.add(btnAdd);
        crudPanel.add(btnEdit);
        crudPanel.add(btnDelete);
        panel.add(crudPanel, BorderLayout.SOUTH);

        // Selection Listener for teachers table
        tblTeachers.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblTeachers.getSelectedRow();
                if (row != -1) {
                    int teacherId = (int) tblTeachers.getValueAt(row, 0);
                    selectedTeacherIdForAssignment = teacherId;
                    loadTeacherSubjectAssignments(teacherId);
                    btnSaveAssignment.setEnabled(true);
                } else {
                    selectedTeacherIdForAssignment = -1;
                    clearTeacherSubjectSelection();
                    btnSaveAssignment.setEnabled(false);
                }
            }
        });

        // Save Assignment Action
        btnSaveAssignment.addActionListener(e -> {
            if (selectedTeacherIdForAssignment != -1) {
                List<Integer> list = new ArrayList<>();
                for (JCheckBox chk : chkSubjectsList) {
                    if (chk.isSelected()) {
                        int subId = (int) chk.getClientProperty("subjectId");
                        list.add(subId);
                    }
                }
                if (teacherController.assignSubjectsToTeacher(selectedTeacherIdForAssignment, list)) {
                    JOptionPane.showMessageDialog(this, "Lëndët u caktuan me sukses!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    refreshSubjectsTable(); // Update teacher counts on Tab 3
                } else {
                    JOptionPane.showMessageDialog(this, "Ndodhi një gabim!", "Gabim", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnAdd.addActionListener(e -> showAddTeacherDialog());
        btnEdit.addActionListener(e -> showEditTeacherDialog());
        btnDelete.addActionListener(e -> handleDeleteTeacher());

        return panel;
    }

    private void refreshTeachersTable() {
        modelTeachers.setRowCount(0);
        List<Mesuesi> list = teacherController.getAllTeachers();
        for (Mesuesi m : list) {
            modelTeachers.addRow(new Object[]{
                m.getId(), m.getEmri(), m.getMbiemri(), m.getEmail(), m.getTelefon()
            });
        }
    }

    private void loadTeacherSubjectAssignments(int teacherId) {
        panelTeacherSubjects.removeAll();
        chkSubjectsList.clear();

        List<Lenda> allLendet = lendaDAO.getAllLendet();
        List<Integer> assignedIds = teacherController.getAssignedSubjectIds(teacherId);

        for (Lenda l : allLendet) {
            JCheckBox chk = new JCheckBox(l.getEmriLendes() + " (" + l.getKreditet() + " Kr)");
            chk.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            chk.setBackground(Color.WHITE);
            chk.putClientProperty("subjectId", l.getId());
            if (assignedIds.contains(l.getId())) {
                chk.setSelected(true);
            }
            chkSubjectsList.add(chk);
            panelTeacherSubjects.add(chk);
        }

        panelTeacherSubjects.revalidate();
        panelTeacherSubjects.repaint();
    }

    private void clearTeacherSubjectSelection() {
        panelTeacherSubjects.removeAll();
        chkSubjectsList.clear();
        panelTeacherSubjects.add(new JLabel("Zgjidhni një mësues për të caktuar lëndët."));
        panelTeacherSubjects.revalidate();
        panelTeacherSubjects.repaint();
    }

    private void showAddTeacherDialog() {
        JDialog dlg = new JDialog(this, "Shto Mësues të Ri", true);
        dlg.setSize(380, 280);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new GridBagLayout());
        dlg.getContentPane().setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        JTextField txtEmri = new JTextField(15);
        JTextField txtMbiemri = new JTextField(15);
        JTextField txtEmail = new JTextField(15);
        JTextField txtTelefon = new JTextField(15);

        addFieldToDialog(dlg, "Emri:", txtEmri, 0, gbc);
        addFieldToDialog(dlg, "Mbiemri:", txtMbiemri, 1, gbc);
        addFieldToDialog(dlg, "Email:", txtEmail, 2, gbc);
        addFieldToDialog(dlg, "Telefon:", txtTelefon, 3, gbc);

        JButton btnSave = new JButton("Ruaj");
        btnSave.setBackground(new Color(26, 35, 126));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        dlg.add(btnSave, gbc);

        btnSave.addActionListener(e -> {
            String emri = txtEmri.getText().trim();
            String mbiemri = txtMbiemri.getText().trim();
            String email = txtEmail.getText().trim();
            String telefon = txtTelefon.getText().trim();

            if (emri.isEmpty() || mbiemri.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Fushat Emri, Mbiemri dhe Email janë të detyrueshme!", "Gabim", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Mesuesi m = new Mesuesi(0, emri, mbiemri, email, telefon);
            
            // Add matching credentials
            String defUsername = (emri.substring(0, Math.min(3, emri.length())) + mbiemri.substring(0, Math.min(3, mbiemri.length()))).toLowerCase() + (int)(Math.random() * 100);
            String defPassword = "teacher123";

            controllers.AuthController authController = new controllers.AuthController();
            if (authController.signUpTeacher(defUsername, defPassword, m)) {
                JOptionPane.showMessageDialog(this, "Mësuesi u shtua me sukses!\nUsername: " + defUsername + "\nPassword: " + defPassword, "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                refreshTeachersTable();
                loadGradeFilters();
            } else {
                JOptionPane.showMessageDialog(dlg, "Gabim gjatë regjistrimit. Sigurohuni që email është unik!", "Gabim", JOptionPane.ERROR_MESSAGE);
            }
        });

        dlg.setVisible(true);
    }

    private void showEditTeacherDialog() {
        int selectedRow = tblTeachers.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Ju lutem zgjidhni një mësues për të ndryshuar!", "Gabim", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tblTeachers.getValueAt(selectedRow, 0);
        Mesuesi m = teacherController.getTeacherById(id);
        if (m == null) return;

        JDialog dlg = new JDialog(this, "Ndrysho Mësues", true);
        dlg.setSize(380, 280);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new GridBagLayout());
        dlg.getContentPane().setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        JTextField txtEmri = new JTextField(m.getEmri(), 15);
        JTextField txtMbiemri = new JTextField(m.getMbiemri(), 15);
        JTextField txtEmail = new JTextField(m.getEmail(), 15);
        JTextField txtTelefon = new JTextField(m.getTelefon(), 15);

        addFieldToDialog(dlg, "Emri:", txtEmri, 0, gbc);
        addFieldToDialog(dlg, "Mbiemri:", txtMbiemri, 1, gbc);
        addFieldToDialog(dlg, "Email:", txtEmail, 2, gbc);
        addFieldToDialog(dlg, "Telefon:", txtTelefon, 3, gbc);

        JButton btnSave = new JButton("Ruaj Ndryshimet");
        btnSave.setBackground(new Color(26, 35, 126));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        dlg.add(btnSave, gbc);

        btnSave.addActionListener(e -> {
            String emri = txtEmri.getText().trim();
            String mbiemri = txtMbiemri.getText().trim();
            String email = txtEmail.getText().trim();
            String telefon = txtTelefon.getText().trim();

            if (emri.isEmpty() || mbiemri.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Fushat Emri, Mbiemri dhe Email janë të detyrueshme!", "Gabim", JOptionPane.WARNING_MESSAGE);
                return;
            }

            m.setEmri(emri);
            m.setMbiemri(mbiemri);
            m.setEmail(email);
            m.setTelefon(telefon);

            if (teacherController.updateTeacher(m)) {
                JOptionPane.showMessageDialog(this, "Mësuesi u ndryshua me sukses!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                refreshTeachersTable();
                loadGradeFilters();
            } else {
                JOptionPane.showMessageDialog(dlg, "Ndryshimi dështoi!", "Gabim", JOptionPane.ERROR_MESSAGE);
            }
        });

        dlg.setVisible(true);
    }

    private void handleDeleteTeacher() {
        int selectedRow = tblTeachers.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Ju lutem zgjidhni një mësues për të fshirë!", "Gabim", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tblTeachers.getValueAt(selectedRow, 0);
        String emri = (String) tblTeachers.getValueAt(selectedRow, 1);
        String mbiemri = (String) tblTeachers.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "A jeni të sigurtë që dëshironi të fshini mësuesin " + emri + " " + mbiemri + "?\n" +
            "Kjo do të fshijë llogarinë e tij dhe do të lërë notat e dhëna prej tij pa mësues (NULL).",
            "Konfirmo Fshirjen",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (teacherController.deleteTeacher(id)) {
                JOptionPane.showMessageDialog(this, "Mësuesi u fshi me sukses!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                refreshTeachersTable();
                loadGradeFilters();
                refreshGradesTable();
                clearTeacherSubjectSelection();
            } else {
                JOptionPane.showMessageDialog(this, "Fshirja dështoi!", "Gabim", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==========================================
    // TAB 3: SUBJECTS PANEL
    // ==========================================
    private JPanel createSubjectsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // JTable
        String[] cols = {"ID", "Lënda", "Përshkrimi", "Kreditet", "Numri i Mësuesve"};
        modelSubjects = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblSubjects = new JTable(modelSubjects);
        styleTable(tblSubjects);
        JScrollPane scroll = new JScrollPane(tblSubjects);
        panel.add(scroll, BorderLayout.CENTER);

        // Buttons Panel (CRUD)
        JPanel crudPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        crudPanel.setBackground(Color.WHITE);
        
        JButton btnAdd = new JButton("Shto Lëndë");
        JButton btnEdit = new JButton("Ndrysho");
        JButton btnDelete = new JButton("Fshi");

        for (JButton b : new JButton[]{btnAdd, btnEdit, btnDelete}) {
            b.setFont(new Font("Segoe UI", Font.BOLD, 12));
            b.setFocusPainted(false);
        }
        btnAdd.setBackground(new Color(26, 35, 126));
        btnAdd.setForeground(Color.BLACK);
        btnEdit.setBackground(new Color(26, 35, 126));
        btnEdit.setForeground(Color.BLACK);
        btnDelete.setBackground(new Color(211, 47, 47));
        btnDelete.setForeground(Color.BLACK);

        crudPanel.add(btnAdd);
        crudPanel.add(btnEdit);
        crudPanel.add(btnDelete);
        panel.add(crudPanel, BorderLayout.SOUTH);

        // Listeners
        btnAdd.addActionListener(e -> showAddSubjectDialog());
        btnEdit.addActionListener(e -> showEditSubjectDialog());
        btnDelete.addActionListener(e -> handleDeleteSubject());

        return panel;
    }

    private void refreshSubjectsTable() {
        modelSubjects.setRowCount(0);
        List<Object[]> list = lendaDAO.getLendetWithTeacherCount();
        for (Object[] row : list) {
            Lenda l = (Lenda) row[0];
            int teacherCount = (int) row[1];
            modelSubjects.addRow(new Object[]{
                l.getId(), l.getEmriLendes(), l.getPershkrimi(), l.getKreditet(), teacherCount
            });
        }
    }

    private void showAddSubjectDialog() {
        JDialog dlg = new JDialog(this, "Shto Lëndë të Re", true);
        dlg.setSize(380, 250);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new GridBagLayout());
        dlg.getContentPane().setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        JTextField txtEmri = new JTextField(15);
        JTextField txtPershkrimi = new JTextField(15);
        JTextField txtKreditet = new JTextField("1", 15);

        addFieldToDialog(dlg, "Emri i Lëndës:", txtEmri, 0, gbc);
        addFieldToDialog(dlg, "Përshkrimi:", txtPershkrimi, 1, gbc);
        addFieldToDialog(dlg, "Kreditet:", txtKreditet, 2, gbc);

        JButton btnSave = new JButton("Ruaj");
        btnSave.setBackground(new Color(26, 35, 126));
        btnSave.setForeground(Color.BLACK);
        btnSave.setFocusPainted(false);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        dlg.add(btnSave, gbc);

        btnSave.addActionListener(e -> {
            String emri = txtEmri.getText().trim();
            String pershkrimi = txtPershkrimi.getText().trim();
            String kreditetStr = txtKreditet.getText().trim();

            if (emri.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Emri i lëndës duhet plotësuar!", "Gabim", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int kreditet;
            try {
                kreditet = Integer.parseInt(kreditetStr);
                if (kreditet < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg, "Kreditet duhet të jenë një numër i plotë pozitiv!", "Gabim", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Lenda l = new Lenda(0, emri, pershkrimi, kreditet);
            if (lendaDAO.addLenda(l) != -1) {
                JOptionPane.showMessageDialog(this, "Lënda u shtua me sukses!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                refreshSubjectsTable();
                loadGradeFilters();
            } else {
                JOptionPane.showMessageDialog(dlg, "Shtimi i lëndës dështoi!", "Gabim", JOptionPane.ERROR_MESSAGE);
            }
        });

        dlg.setVisible(true);
    }

    private void showEditSubjectDialog() {
        int selectedRow = tblSubjects.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Ju lutem zgjidhni një lëndë për të ndryshuar!", "Gabim", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tblSubjects.getValueAt(selectedRow, 0);
        Lenda l = lendaDAO.getLendaById(id);
        if (l == null) return;

        JDialog dlg = new JDialog(this, "Ndrysho Lëndë", true);
        dlg.setSize(380, 250);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new GridBagLayout());
        dlg.getContentPane().setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        JTextField txtEmri = new JTextField(l.getEmriLendes(), 15);
        JTextField txtPershkrimi = new JTextField(l.getPershkrimi(), 15);
        JTextField txtKreditet = new JTextField(String.valueOf(l.getKreditet()), 15);

        addFieldToDialog(dlg, "Emri i Lëndës:", txtEmri, 0, gbc);
        addFieldToDialog(dlg, "Përshkrimi:", txtPershkrimi, 1, gbc);
        addFieldToDialog(dlg, "Kreditet:", txtKreditet, 2, gbc);

        JButton btnSave = new JButton("Ruaj Ndryshimet");
        btnSave.setBackground(new Color(26, 35, 126));
        btnSave.setForeground(Color.BLACK);
        btnSave.setFocusPainted(false);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        dlg.add(btnSave, gbc);

        btnSave.addActionListener(e -> {
            String emri = txtEmri.getText().trim();
            String pershkrimi = txtPershkrimi.getText().trim();
            String kreditetStr = txtKreditet.getText().trim();

            if (emri.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Emri i lëndës duhet plotësuar!", "Gabim", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int kreditet;
            try {
                kreditet = Integer.parseInt(kreditetStr);
                if (kreditet < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg, "Kreditet duhet të jenë një numër i plotë pozitiv!", "Gabim", JOptionPane.WARNING_MESSAGE);
                return;
            }

            l.setEmriLendes(emri);
            l.setPershkrimi(pershkrimi);
            l.setKreditet(kreditet);

            if (lendaDAO.updateLenda(l)) {
                JOptionPane.showMessageDialog(this, "Lënda u ndryshua me sukses!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                refreshSubjectsTable();
                loadGradeFilters();
            } else {
                JOptionPane.showMessageDialog(dlg, "Ndryshimi i lëndës dështoi!", "Gabim", JOptionPane.ERROR_MESSAGE);
            }
        });

        dlg.setVisible(true);
    }

    private void handleDeleteSubject() {
        int selectedRow = tblSubjects.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Ju lutem zgjidhni një lëndë për të fshirë!", "Gabim", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tblSubjects.getValueAt(selectedRow, 0);
        String emri = (String) tblSubjects.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "A jeni të sigurtë që dëshironi të fshini lëndën " + emri + "?\n" +
            "Kjo do të fshijë automatikisht të gjitha notat e vendosura në këtë lëndë!",
            "Konfirmo Fshirjen",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (lendaDAO.deleteLenda(id)) {
                JOptionPane.showMessageDialog(this, "Lënda u fshi me sukses!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                refreshSubjectsTable();
                loadGradeFilters();
                refreshGradesTable();
            } else {
                JOptionPane.showMessageDialog(this, "Fshirja dështoi!", "Gabim", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==========================================
    // TAB 4: GRADES PANEL
    // ==========================================
    private JPanel createGradesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Filters Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        filterPanel.setBackground(Color.WHITE);

        cmbFilterStudent = new JComboBox<>();
        cmbFilterSubject = new JComboBox<>();
        cmbFilterType = new JComboBox<>(new String[]{"Te gjitha", "Provim", "Test", "Detyre", "Projekt"});

        filterPanel.add(new JLabel("Nxënësi:"));
        filterPanel.add(cmbFilterStudent);
        filterPanel.add(new JLabel("Lënda:"));
        filterPanel.add(cmbFilterSubject);
        filterPanel.add(new JLabel("Lloji:"));
        filterPanel.add(cmbFilterType);

        JButton btnFilter = new JButton("Filtro");
        btnFilter.setBackground(new Color(26, 35, 126));
        btnFilter.setForeground(Color.BLACK);
        btnFilter.setFocusPainted(false);
        filterPanel.add(btnFilter);

        JButton btnResetFilter = new JButton("Pastro");
        btnResetFilter.setBackground(Color.WHITE);
        btnResetFilter.setForeground(new Color(26, 35, 126));
        btnResetFilter.setBorder(BorderFactory.createLineBorder(new Color(26, 35, 126), 1));
        btnResetFilter.setFocusPainted(false);
        filterPanel.add(btnResetFilter);

        panel.add(filterPanel, BorderLayout.NORTH);

        // JTable
        String[] cols = {"ID", "Nxënësi", "Lënda", "Mësuesi", "Nota", "Lloji", "Data e Dhënies"};
        modelGrades = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblGrades = new JTable(modelGrades);
        styleTable(tblGrades);
        JScrollPane scroll = new JScrollPane(tblGrades);
        panel.add(scroll, BorderLayout.CENTER);

        // CRUD buttons
        JPanel crudPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        crudPanel.setBackground(Color.WHITE);

        JButton btnAdd = new JButton("Shto Notë");
        JButton btnEdit = new JButton("Ndrysho Notë");
        JButton btnDelete = new JButton("Fshi Notë");

        for (JButton b : new JButton[]{btnAdd, btnEdit, btnDelete}) {
            b.setFont(new Font("Segoe UI", Font.BOLD, 12));
            b.setFocusPainted(false);
        }
        btnAdd.setBackground(new Color(26, 35, 126));
        btnAdd.setForeground(Color.BLACK);
        btnEdit.setBackground(new Color(26, 35, 126));
        btnEdit.setForeground(Color.BLACK);
        btnDelete.setBackground(new Color(211, 47, 47));
        btnDelete.setForeground(Color.BLACK);

        crudPanel.add(btnAdd);
        crudPanel.add(btnEdit);
        crudPanel.add(btnDelete);
        panel.add(crudPanel, BorderLayout.SOUTH);

        // Action listeners
        btnFilter.addActionListener(e -> applyGradesFilter());
        btnResetFilter.addActionListener(e -> {
            cmbFilterStudent.setSelectedIndex(0);
            cmbFilterSubject.setSelectedIndex(0);
            cmbFilterType.setSelectedIndex(0);
            refreshGradesTable();
        });

        btnAdd.addActionListener(e -> showAddGradeDialog());
        btnEdit.addActionListener(e -> showEditGradeDialog());
        btnDelete.addActionListener(e -> handleDeleteGrade());

        return panel;
    }

    private void loadGradeFilters() {
        // Load Students filter combo box
        cmbFilterStudent.removeAllItems();
        cmbFilterStudent.addItem("Të gjithë");
        for (Nxenesi n : studentController.getAllStudents()) {
            cmbFilterStudent.addItem(n.getId() + " - " + n.getEmri() + " " + n.getMbiemri());
        }

        // Load Subjects filter combo box
        cmbFilterSubject.removeAllItems();
        cmbFilterSubject.addItem("Të gjitha");
        for (Lenda l : lendaDAO.getAllLendet()) {
            cmbFilterSubject.addItem(l.getId() + " - " + l.getEmriLendes());
        }
    }

    private void refreshGradesTable() {
        populateGradesTable(gradeController.getAllGrades());
    }

    private void populateGradesTable(List<Nota> list) {
        modelGrades.setRowCount(0);
        for (Nota n : list) {
            String studentName = n.getStudentFullName();
            if (studentName.equals("N/A")) {
                Nxenesi s = studentController.getStudentById(n.getNxenesiId());
                if (s != null) studentName = s.getEmri() + " " + s.getMbiemri();
            }

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

            modelGrades.addRow(new Object[]{
                n.getId(), studentName, lendaName, teacherName, n.getNota(), n.getLloji(), n.getDataDhenies()
            });
        }
    }

    private void applyGradesFilter() {
        Integer sId = null;
        if (cmbFilterStudent.getSelectedIndex() > 0) {
            String item = (String) cmbFilterStudent.getSelectedItem();
            sId = Integer.parseInt(item.split(" - ")[0]);
        }

        Integer lId = null;
        if (cmbFilterSubject.getSelectedIndex() > 0) {
            String item = (String) cmbFilterSubject.getSelectedItem();
            lId = Integer.parseInt(item.split(" - ")[0]);
        }

        String type = (String) cmbFilterType.getSelectedItem();
        
        List<Nota> list = gradeController.getGradesWithFilters(sId, lId, type);
        populateGradesTable(list);
    }

    private void showAddGradeDialog() {
        JDialog dlg = new JDialog(this, "Shto Notë të Re", true);
        dlg.setSize(400, 350);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new GridBagLayout());
        dlg.getContentPane().setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        JComboBox<String> cmbStudent = new JComboBox<>();
        for (Nxenesi n : studentController.getAllStudents()) {
            cmbStudent.addItem(n.getId() + " - " + n.getEmri() + " " + n.getMbiemri());
        }

        JComboBox<String> cmbSubject = new JComboBox<>();
        for (Lenda l : lendaDAO.getAllLendet()) {
            cmbSubject.addItem(l.getId() + " - " + l.getEmriLendes());
        }

        JComboBox<String> cmbTeacher = new JComboBox<>();
        cmbTeacher.addItem("Pa mësues");
        for (Mesuesi m : teacherController.getAllTeachers()) {
            cmbTeacher.addItem(m.getId() + " - " + m.getEmri() + " " + m.getMbiemri());
        }

        JTextField txtNotaVal = new JTextField("10.0", 10);
        JComboBox<String> cmbLloji = new JComboBox<>(new String[]{"Provim", "Test", "Detyre", "Projekt"});
        JTextField txtDate = new JTextField(new Date(System.currentTimeMillis()).toString(), 10);

        // Add fields
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        dlg.add(new JLabel("Nxënësi:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        dlg.add(cmbStudent, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        dlg.add(new JLabel("Lënda:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        dlg.add(cmbSubject, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        dlg.add(new JLabel("Mësuesi:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.7;
        dlg.add(cmbTeacher, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        dlg.add(new JLabel("Nota (1.0 - 10.0):"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.7;
        dlg.add(txtNotaVal, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        dlg.add(new JLabel("Lloji:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 0.7;
        dlg.add(cmbLloji, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.3;
        dlg.add(new JLabel("Data (VVVV-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; gbc.weightx = 0.7;
        dlg.add(txtDate, gbc);

        JButton btnSave = new JButton("Ruaj Notën");
        btnSave.setBackground(new Color(26, 35, 126));
        btnSave.setForeground(Color.BLACK);
        btnSave.setFocusPainted(false);
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        dlg.add(btnSave, gbc);

        btnSave.addActionListener(e -> {
            if (cmbStudent.getSelectedItem() == null || cmbSubject.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(dlg, "Ju lutem regjistroni nxënës ose lëndë më parë!", "Gabim", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int stId = Integer.parseInt(((String) cmbStudent.getSelectedItem()).split(" - ")[0]);
            int suId = Integer.parseInt(((String) cmbSubject.getSelectedItem()).split(" - ")[0]);
            
            Integer teId = null;
            if (cmbTeacher.getSelectedIndex() > 0) {
                teId = Integer.parseInt(((String) cmbTeacher.getSelectedItem()).split(" - ")[0]);
            }

            double notaVal;
            try {
                notaVal = Double.parseDouble(txtNotaVal.getText().trim());
                if (notaVal < 1.0 || notaVal > 10.0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg, "Nota duhet të jetë një numër dhjetor midis 1.0 dhe 10.0!", "Gabim", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Date dateVal;
            try {
                dateVal = Date.valueOf(txtDate.getText().trim());
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dlg, "Formati i datës është i gabuar (VVVV-MM-DD)!", "Gabim", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Nota n = new Nota(0, stId, suId, teId, notaVal, (String) cmbLloji.getSelectedItem(), dateVal);
            if (gradeController.addGrade(n)) {
                JOptionPane.showMessageDialog(this, "Nota u vendos me sukses!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                refreshGradesTable();
            } else {
                JOptionPane.showMessageDialog(dlg, "Veprimi dështoi gjatë shtimit në databazë!", "Gabim", JOptionPane.ERROR_MESSAGE);
            }
        });

        dlg.setVisible(true);
    }

    private void showEditGradeDialog() {
        int selectedRow = tblGrades.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Ju lutem zgjidhni një notë për të ndryshuar!", "Gabim", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int gradeId = (int) tblGrades.getValueAt(selectedRow, 0);
        List<Nota> grades = gradeController.getAllGrades();
        Nota n = null;
        for (Nota grade : grades) {
            if (grade.getId() == gradeId) {
                n = grade;
                break;
            }
        }
        if (n == null) return;

        final Nota notaToEdit = n;

        JDialog dlg = new JDialog(this, "Ndrysho Notën", true);
        dlg.setSize(400, 350);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new GridBagLayout());
        dlg.getContentPane().setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Combos
        JComboBox<String> cmbStudent = new JComboBox<>();
        int selectStudentIdx = 0;
        int idx = 0;
        for (Nxenesi st : studentController.getAllStudents()) {
            cmbStudent.addItem(st.getId() + " - " + st.getEmri() + " " + st.getMbiemri());
            if (st.getId() == notaToEdit.getNxenesiId()) selectStudentIdx = idx;
            idx++;
        }
        cmbStudent.setSelectedIndex(selectStudentIdx);

        JComboBox<String> cmbSubject = new JComboBox<>();
        int selectSubjectIdx = 0;
        idx = 0;
        for (Lenda l : lendaDAO.getAllLendet()) {
            cmbSubject.addItem(l.getId() + " - " + l.getEmriLendes());
            if (l.getId() == notaToEdit.getLendaId()) selectSubjectIdx = idx;
            idx++;
        }
        cmbSubject.setSelectedIndex(selectSubjectIdx);

        JComboBox<String> cmbTeacher = new JComboBox<>();
        cmbTeacher.addItem("Pa mësues");
        int selectTeacherIdx = 0;
        idx = 1;
        for (Mesuesi t : teacherController.getAllTeachers()) {
            cmbTeacher.addItem(t.getId() + " - " + t.getEmri() + " " + t.getMbiemri());
            if (notaToEdit.getMesuesiId() != null && t.getId() == notaToEdit.getMesuesiId()) {
                selectTeacherIdx = idx;
            }
            idx++;
        }
        cmbTeacher.setSelectedIndex(selectTeacherIdx);

        JTextField txtNotaVal = new JTextField(String.valueOf(notaToEdit.getNota()), 10);
        JComboBox<String> cmbLloji = new JComboBox<>(new String[]{"Provim", "Test", "Detyre", "Projekt"});
        cmbLloji.setSelectedItem(notaToEdit.getLloji());
        JTextField txtDate = new JTextField(notaToEdit.getDataDhenies().toString(), 10);

        // Layout
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        dlg.add(new JLabel("Nxënësi:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        dlg.add(cmbStudent, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        dlg.add(new JLabel("Lënda:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        dlg.add(cmbSubject, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        dlg.add(new JLabel("Mësuesi:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.7;
        dlg.add(cmbTeacher, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        dlg.add(new JLabel("Nota (1.0 - 10.0):"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.7;
        dlg.add(txtNotaVal, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        dlg.add(new JLabel("Lloji:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 0.7;
        dlg.add(cmbLloji, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.3;
        dlg.add(new JLabel("Data (VVVV-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; gbc.weightx = 0.7;
        dlg.add(txtDate, gbc);

        JButton btnSave = new JButton("Ruaj Ndryshimet");
        btnSave.setBackground(new Color(26, 35, 126));
        btnSave.setForeground(Color.BLACK);
        btnSave.setFocusPainted(false);
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        dlg.add(btnSave, gbc);

        btnSave.addActionListener(e -> {
            int stId = Integer.parseInt(((String) cmbStudent.getSelectedItem()).split(" - ")[0]);
            int suId = Integer.parseInt(((String) cmbSubject.getSelectedItem()).split(" - ")[0]);
            
            Integer teId = null;
            if (cmbTeacher.getSelectedIndex() > 0) {
                teId = Integer.parseInt(((String) cmbTeacher.getSelectedItem()).split(" - ")[0]);
            }

            double notaVal;
            try {
                notaVal = Double.parseDouble(txtNotaVal.getText().trim());
                if (notaVal < 1.0 || notaVal > 10.0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg, "Nota duhet të jetë një numër dhjetor midis 1.0 dhe 10.0!", "Gabim", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Date dateVal;
            try {
                dateVal = Date.valueOf(txtDate.getText().trim());
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dlg, "Formati i datës është i gabuar (VVVV-MM-DD)!", "Gabim", JOptionPane.WARNING_MESSAGE);
                return;
            }

            notaToEdit.setNxenesiId(stId);
            notaToEdit.setLendaId(suId);
            notaToEdit.setMesuesiId(teId);
            notaToEdit.setNota(notaVal);
            notaToEdit.setLloji((String) cmbLloji.getSelectedItem());
            notaToEdit.setDataDhenies(dateVal);

            if (gradeController.updateGrade(notaToEdit)) {
                JOptionPane.showMessageDialog(this, "Nota u përditësua me sukses!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                refreshGradesTable();
            } else {
                JOptionPane.showMessageDialog(dlg, "Përditësimi dështoi!", "Gabim", JOptionPane.ERROR_MESSAGE);
            }
        });

        dlg.setVisible(true);
    }

    private void handleDeleteGrade() {
        int selectedRow = tblGrades.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Ju lutem zgjidhni një notë për të fshirë!", "Gabim", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int gradeId = (int) tblGrades.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "A jeni të sigurtë që dëshironi të fshini këtë notë?",
            "Konfirmo Fshirjen",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (gradeController.deleteGrade(gradeId)) {
                JOptionPane.showMessageDialog(this, "Nota u fshi me sukses!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                refreshGradesTable();
            } else {
                JOptionPane.showMessageDialog(this, "Fshirja dështoi!", "Gabim", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
