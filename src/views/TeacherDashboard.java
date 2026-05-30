package views;

import controllers.MesuesiController;
import controllers.NotaController;
import controllers.NxenesiController;
import dao.LendaDAO;
import models.User;
import models.Mesuesi;
import models.Lenda;
import models.Nota;
import models.Nxenesi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class TeacherDashboard extends JFrame {
    private final User currentUser;
    private Mesuesi currentTeacher;

    // Controllers
    private final MesuesiController teacherController = new MesuesiController();
    private final NotaController gradeController = new NotaController();
    private final NxenesiController studentController = new NxenesiController();
    private final LendaDAO lendaDAO = new LendaDAO();

    // UI elements
    private JTable tblMySubjects;
    private DefaultTableModel modelMySubjects;

    private JComboBox<Lenda> cmbMySubjectsList;
    private JTable tblGrades;
    private DefaultTableModel modelGrades;

    public TeacherDashboard(User user) {
        this.currentUser = user;
        this.currentTeacher = teacherController.getTeacherById(user.getRefId());

        if (currentTeacher == null) {
            JOptionPane.showMessageDialog(null, "Të dhënat e mësuesit nuk u gjetën!", "Gabim", JOptionPane.ERROR_MESSAGE);
            currentTeacher = new Mesuesi(user.getRefId(), "Mësues", "I Panjohur", "", "");
        }

        setTitle("School Management System - Teacher Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 600);
        setLocationRelativeTo(null);

        // Main Panel
        JPanel mainContainer = new JPanel(new BorderLayout());
        UiTheme.stylePanel(mainContainer);

        // Header Panel (Navy Blue)
        JPanel headerPanel = new JPanel(new BorderLayout());
        UiTheme.styleHeaderPanel(headerPanel);
        headerPanel.setPreferredSize(new Dimension(850, 70));
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("School Management System - Mësuesi");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        UiTheme.styleHeaderTitle(lblTitle);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        headerRight.setOpaque(false);

        JLabel lblWelcome = new JLabel("Mësuesi: " + currentTeacher.getEmri() + " " + currentTeacher.getMbiemri());
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

        // Tab 1: Lëndët e mia
        tabbedPane.addTab("Lëndët e Mia", createMySubjectsPanel());

        // Tab 2: Menaxhimi i Notave
        tabbedPane.addTab("Menaxhimi i Notave", createGradesPanel());

        mainContainer.add(tabbedPane, BorderLayout.CENTER);
        add(mainContainer);

        // Load initial data
        refreshMySubjectsTable();
        loadMySubjectsCombo();
        refreshGradesTable();
    }

    // ==========================================
    // TAB 1: MY SUBJECTS
    // ==========================================
    private JPanel createMySubjectsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        UiTheme.stylePanel(panel);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("Lista e lëndëve që unë jap mësim:");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(UiTheme.TEXT_PRIMARY);
        lblTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(lblTitle, BorderLayout.NORTH);

        String[] cols = {"ID e Lëndës", "Emri i Lëndës", "Përshkrimi", "Kreditet"};
        modelMySubjects = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblMySubjects = new JTable(modelMySubjects);
        UiTheme.styleTable(tblMySubjects);
        JScrollPane subjectsScroll = new JScrollPane(tblMySubjects);
        UiTheme.styleScrollPane(subjectsScroll);
        panel.add(subjectsScroll, BorderLayout.CENTER);

        return panel;
    }

    private void refreshMySubjectsTable() {
        modelMySubjects.setRowCount(0);
        List<Lenda> list = lendaDAO.getLendetByTeacherId(currentTeacher.getId());
        for (Lenda l : list) {
            modelMySubjects.addRow(new Object[]{
                l.getId(), l.getEmriLendes(), l.getPershkrimi(), l.getKreditet()
            });
        }
    }

    // ==========================================
    // TAB 2: MANAGE GRADES
    // ==========================================
    private JPanel createGradesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        UiTheme.stylePanel(panel);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Filter by subject combo
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        UiTheme.stylePanel(filterPanel);
        JLabel lblSubject = new JLabel("Zgjidh Lëndën:");
        lblSubject.setForeground(UiTheme.TEXT_PRIMARY);
        filterPanel.add(lblSubject);

        cmbMySubjectsList = new JComboBox<>();
        cmbMySubjectsList.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbMySubjectsList.addActionListener(e -> refreshGradesTable());
        filterPanel.add(cmbMySubjectsList);
        panel.add(filterPanel, BorderLayout.NORTH);

        // Grades JTable
        String[] cols = {"ID", "Nxënësi", "Nota", "Lloji", "Data e Dhënies"};
        modelGrades = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblGrades = new JTable(modelGrades);
        UiTheme.styleTable(tblGrades);
        JScrollPane gradesScroll = new JScrollPane(tblGrades);
        UiTheme.styleScrollPane(gradesScroll);
        panel.add(gradesScroll, BorderLayout.CENTER);

        // Buttons (CRUD)
        JPanel crudPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        UiTheme.stylePanel(crudPanel);

        JButton btnAdd = new JButton("Shto Notë");
        JButton btnEdit = new JButton("Ndrysho");
        JButton btnDelete = new JButton("Fshi");

        UiTheme.stylePrimaryButton(btnAdd);
        UiTheme.stylePrimaryButton(btnEdit);
        UiTheme.styleDangerButton(btnDelete);

        crudPanel.add(btnAdd);
        crudPanel.add(btnEdit);
        crudPanel.add(btnDelete);
        panel.add(crudPanel, BorderLayout.SOUTH);

        // Action listeners
        btnAdd.addActionListener(e -> showAddGradeDialog());
        btnEdit.addActionListener(e -> showEditGradeDialog());
        btnDelete.addActionListener(e -> handleDeleteGrade());

        return panel;
    }

    private void loadMySubjectsCombo() {
        cmbMySubjectsList.removeAllItems();
        List<Lenda> list = lendaDAO.getLendetByTeacherId(currentTeacher.getId());
        for (Lenda l : list) {
            cmbMySubjectsList.addItem(l);
        }
    }

    private void refreshGradesTable() {
        modelGrades.setRowCount(0);
        Lenda selectedLenda = (Lenda) cmbMySubjectsList.getSelectedItem();
        if (selectedLenda != null) {
            List<Nota> grades = gradeController.getGradesByTeacherAndSubject(currentTeacher.getId(), selectedLenda.getId());
            for (Nota n : grades) {
                // Fetch student full name if helper is null
                String studentName = n.getStudentFullName();
                if (studentName.equals("N/A")) {
                    Nxenesi s = studentController.getStudentById(n.getNxenesiId());
                    if (s != null) studentName = s.getEmri() + " " + s.getMbiemri();
                }
                modelGrades.addRow(new Object[]{
                    n.getId(), studentName, n.getNota(), n.getLloji(), n.getDataDhenies()
                });
            }
        }
    }

    private void showAddGradeDialog() {
        Lenda selectedLenda = (Lenda) cmbMySubjectsList.getSelectedItem();
        if (selectedLenda == null) {
            JOptionPane.showMessageDialog(this, "Nuk keni asnjë lëndë të caktuar për të vendosur notë!", "Gabim", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Nxenesi> students = studentController.getAllStudents();
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nuk ka asnjë nxënës në sistem!", "Gabim", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dlg = new JDialog(this, "Shto Notë - " + selectedLenda.getEmriLendes(), true);
        dlg.setSize(380, 280);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new GridBagLayout());
        dlg.getContentPane().setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        JComboBox<Nxenesi> cmbStudent = new JComboBox<>();
        for (Nxenesi n : students) {
            cmbStudent.addItem(n);
        }

        JTextField txtNota = new JTextField("10.0", 10);
        JComboBox<String> cmbLloji = new JComboBox<>(new String[]{"Provim", "Test", "Detyre", "Projekt"});
        JTextField txtDate = new JTextField(new Date(System.currentTimeMillis()).toString(), 10);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        dlg.add(new JLabel("Nxënësi:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        dlg.add(cmbStudent, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        dlg.add(new JLabel("Nota (1.0 - 10.0):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        dlg.add(txtNota, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        dlg.add(new JLabel("Lloji:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.7;
        dlg.add(cmbLloji, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        dlg.add(new JLabel("Data (VVVV-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.7;
        dlg.add(txtDate, gbc);

        JButton btnSave = new JButton("Ruaj Notën");
        UiTheme.stylePrimaryButton(btnSave);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        dlg.add(btnSave, gbc);

        btnSave.addActionListener(e -> {
            Nxenesi selectedStudent = (Nxenesi) cmbStudent.getSelectedItem();
            if (selectedStudent == null) return;

            double notaVal;
            try {
                notaVal = Double.parseDouble(txtNota.getText().trim());
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

            Nota n = new Nota(0, selectedStudent.getId(), selectedLenda.getId(), currentTeacher.getId(), notaVal, (String) cmbLloji.getSelectedItem(), dateVal);
            if (gradeController.addGrade(n)) {
                JOptionPane.showMessageDialog(this, "Nota u regjistrua me sukses!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                refreshGradesTable();
            } else {
                JOptionPane.showMessageDialog(dlg, "Gabim gjatë regjistrimit në databazë!", "Gabim", JOptionPane.ERROR_MESSAGE);
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
        Lenda selectedLenda = (Lenda) cmbMySubjectsList.getSelectedItem();
        if (selectedLenda == null) return;

        // Retrieve existing Grade object
        List<Nota> grades = gradeController.getGradesByTeacherAndSubject(currentTeacher.getId(), selectedLenda.getId());
        Nota matchingNota = null;
        for (Nota g : grades) {
            if (g.getId() == gradeId) {
                matchingNota = g;
                break;
            }
        }
        if (matchingNota == null) return;

        final Nota notaToEdit = matchingNota;

        JDialog dlg = new JDialog(this, "Ndrysho Notën", true);
        dlg.setSize(380, 250);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new GridBagLayout());
        dlg.getContentPane().setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Student is read-only when editing a grade to maintain audit log integrity
        String studentName = (String) tblGrades.getValueAt(selectedRow, 1);
        JLabel lblStudentName = new JLabel(studentName);
        lblStudentName.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JTextField txtNota = new JTextField(String.valueOf(notaToEdit.getNota()), 10);
        JComboBox<String> cmbLloji = new JComboBox<>(new String[]{"Provim", "Test", "Detyre", "Projekt"});
        cmbLloji.setSelectedItem(notaToEdit.getLloji());
        JTextField txtDate = new JTextField(notaToEdit.getDataDhenies().toString(), 10);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        dlg.add(new JLabel("Nxënësi:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        dlg.add(lblStudentName, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        dlg.add(new JLabel("Nota (1.0 - 10.0):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        dlg.add(txtNota, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        dlg.add(new JLabel("Lloji:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.7;
        dlg.add(cmbLloji, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        dlg.add(new JLabel("Data (VVVV-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.7;
        dlg.add(txtDate, gbc);

        JButton btnSave = new JButton("Ruaj Ndryshimet");
        UiTheme.stylePrimaryButton(btnSave);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        dlg.add(btnSave, gbc);

        btnSave.addActionListener(e -> {
            double notaVal;
            try {
                notaVal = Double.parseDouble(txtNota.getText().trim());
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

            notaToEdit.setNota(notaVal);
            notaToEdit.setLloji((String) cmbLloji.getSelectedItem());
            notaToEdit.setDataDhenies(dateVal);

            if (gradeController.updateGrade(notaToEdit)) {
                JOptionPane.showMessageDialog(this, "Nota u përditësua me sukses!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                refreshGradesTable();
            } else {
                JOptionPane.showMessageDialog(dlg, "Gabim gjatë përditësimit në databazë!", "Gabim", JOptionPane.ERROR_MESSAGE);
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
