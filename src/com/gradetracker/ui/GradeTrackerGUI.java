package com.gradetracker.ui;

import com.gradetracker.model.Student;
import com.gradetracker.service.GradeTrackerService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Modern Swing Graphical User Interface for Student Grade Tracker.
 * Features smooth micro-animations for menu action buttons and dynamic feedback.
 */
public class GradeTrackerGUI extends JFrame {
    private final GradeTrackerService service;

    // Palette Colors
    public static final Color BG_MAIN = new Color(245, 247, 250);
    public static final Color BG_CARD = Color.WHITE;
    public static final Color TEXT_PRIMARY = new Color(30, 41, 59);
    public static final Color TEXT_MUTED = new Color(100, 116, 139);
    public static final Color PRIMARY_COLOR = new Color(79, 70, 229); // Indigo
    public static final Color PRIMARY_HOVER = new Color(67, 56, 202);
    public static final Color DARK_BTN_BG = new Color(15, 23, 42);      // Sleek Dark Charcoal / Slate (#0F172A)
    public static final Color DARK_BTN_HOVER = new Color(51, 65, 85);   // Dark Slate Hover (#334155)
    public static final Color DARK_BTN_TEXT = Color.WHITE;
    public static final Color SUCCESS_COLOR = new Color(16, 185, 129); // Emerald
    public static final Color DANGER_COLOR = new Color(239, 68, 68); // Red
    public static final Color WARNING_COLOR = new Color(245, 158, 11); // Amber
    public static final Color BORDER_COLOR = new Color(226, 232, 240);

    // Fonts
    private final Font fontTitle = new Font("Segoe UI", Font.BOLD, 22);
    private final Font fontSub = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font fontSection = new Font("Segoe UI", Font.BOLD, 15);
    private final Font fontBody = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font fontBold = new Font("Segoe UI", Font.BOLD, 13);
    private final Font fontStatVal = new Font("Segoe UI", Font.BOLD, 20);

    // Form inputs
    private JTextField txtName;
    private JTextField txtScore;
    private JTextField txtSearch;

    // KPI labels
    private JLabel lblTotalStudents;
    private JLabel lblAverageScore;
    private JLabel lblHighestScore;
    private JLabel lblLowestScore;
    private JLabel lblPassingRate;

    // Distribution Labels
    private JLabel lblDistA;
    private JLabel lblDistB;
    private JLabel lblDistC;
    private JLabel lblDistD;
    private JLabel lblDistF;

    // Table
    private JTable table;
    private DefaultTableModel tableModel;

    // Toast Notification Bar
    private JPanel toastPanel;
    private JLabel lblToast;
    private JLabel lblStatus;
    private Color toastBgColor = DARK_BTN_BG;
    private float toastOpacity = 0.0f;
    private Timer toastTimer;

    public GradeTrackerGUI(GradeTrackerService service) {
        this.service = service;
        initUI();
        refreshAllData();
    }

    public static void launch(GradeTrackerService service) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            GradeTrackerGUI gui = new GradeTrackerGUI(service);
            gui.setVisible(true);
        });
    }

    private void initUI() {
        setTitle("Student Grade Tracker - Professional Edition");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 740);
        setMinimumSize(new Dimension(960, 620));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_MAIN);
        setLayout(new BorderLayout(0, 0));

        // Top Header and KPI Cards
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.setBackground(BG_MAIN);
        northPanel.setBorder(new EmptyBorder(16, 24, 12, 24));

        northPanel.add(createHeaderPanel());
        northPanel.add(Box.createVerticalStrut(14));
        northPanel.add(createKPICardsPanel());

        add(northPanel, BorderLayout.NORTH);

        // Center Content: Left Form & Right Table
        JPanel centerPanel = new JPanel(new BorderLayout(16, 0));
        centerPanel.setBackground(BG_MAIN);
        centerPanel.setBorder(new EmptyBorder(0, 24, 8, 24));

        centerPanel.add(createLeftFormPanel(), BorderLayout.WEST);
        centerPanel.add(createCenterTablePanel(), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Status & Toast Notification Bar
        add(createBottomStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_MAIN);

        JPanel titles = new JPanel();
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.setBackground(BG_MAIN);

        JLabel title = new JLabel("Student Grade Tracker");
        title.setFont(fontTitle);
        title.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Manage scores, calculate statistics, and view analytics with animated controls.");
        subtitle.setFont(fontSub);
        subtitle.setForeground(TEXT_MUTED);

        titles.add(title);
        titles.add(Box.createVerticalStrut(2));
        titles.add(subtitle);

        panel.add(titles, BorderLayout.WEST);

        // Top-right action button
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setBackground(BG_MAIN);

        AnimatedButton btnExport = new AnimatedButton(
                "📄", "Export Report",
                DARK_BTN_BG, DARK_BTN_TEXT, DARK_BTN_HOVER,
                "lift", true
        );
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnExport.setBorder(new EmptyBorder(12, 28, 12, 28));
        btnExport.setPreferredSize(new Dimension(210, 48));
        btnExport.setMinimumSize(new Dimension(210, 48));
        btnExport.addActionListener(e -> exportReportToFile());

        actions.add(btnExport);

        panel.add(actions, BorderLayout.EAST);
        return panel;
    }

    private JPanel createKPICardsPanel() {
        JPanel kpiContainer = new JPanel(new GridLayout(1, 5, 12, 0));
        kpiContainer.setBackground(BG_MAIN);

        lblTotalStudents = new JLabel("0");
        lblAverageScore = new JLabel("0.00");
        lblHighestScore = new JLabel("-");
        lblLowestScore = new JLabel("-");
        lblPassingRate = new JLabel("0.0%");

        kpiContainer.add(createCard("Total Students", lblTotalStudents, new Color(59, 130, 246)));
        kpiContainer.add(createCard("Class Average", lblAverageScore, PRIMARY_COLOR));
        kpiContainer.add(createCard("Highest Score", lblHighestScore, SUCCESS_COLOR));
        kpiContainer.add(createCard("Lowest Score", lblLowestScore, WARNING_COLOR));
        kpiContainer.add(createCard("Passing Rate", lblPassingRate, new Color(14, 165, 233)));

        return kpiContainer;
    }

    private JPanel createCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblTitle.setForeground(TEXT_MUTED);

        valueLabel.setFont(fontStatVal);
        valueLabel.setForeground(accent);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createLeftFormPanel() {
        JPanel left = new JPanel();
        left.setPreferredSize(new Dimension(360, 0));
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(BG_CARD);
        left.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JLabel heading = new JLabel("Manage Student");
        heading.setFont(fontSection);
        heading.setForeground(TEXT_PRIMARY);
        left.add(heading);
        left.add(Box.createVerticalStrut(14));

        // Student Name Field
        JLabel lblName = new JLabel("Student Full Name");
        lblName.setFont(fontBold);
        lblName.setForeground(TEXT_PRIMARY);
        left.add(lblName);
        left.add(Box.createVerticalStrut(4));

        txtName = new JTextField();
        styleTextField(txtName);
        // Enter key on name field submits the form
        txtName.addActionListener(e -> handleAddStudent());
        left.add(txtName);
        left.add(Box.createVerticalStrut(10));

        // Student Score Field
        JLabel lblScore = new JLabel("Grade / Score (0.0 - 100.0)");
        lblScore.setFont(fontBold);
        lblScore.setForeground(TEXT_PRIMARY);
        left.add(lblScore);
        left.add(Box.createVerticalStrut(4));

        txtScore = new JTextField();
        styleTextField(txtScore);
        // Enter key on score field submits the form
        txtScore.addActionListener(e -> handleAddStudent());
        left.add(txtScore);
        left.add(Box.createVerticalStrut(16));

        // ── ADD STUDENT  (full width, tallest) ──
        AnimatedButton btnAdd = new AnimatedButton(
                "➕", "Add Student",
                DARK_BTN_BG, DARK_BTN_TEXT, DARK_BTN_HOVER,
                "spin", true
        );
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnAdd.setBorder(new EmptyBorder(12, 18, 12, 18));
        btnAdd.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnAdd.setPreferredSize(new Dimension(Integer.MAX_VALUE, 48));
        btnAdd.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAdd.addActionListener(e -> handleAddStudent());
        left.add(btnAdd);
        left.add(Box.createVerticalStrut(10));

        // ── UPDATE  (full width) ──
        AnimatedButton btnUpdate = new AnimatedButton(
                "✏️", "Update Student",
                new Color(241, 245, 249), TEXT_PRIMARY, new Color(226, 232, 240),
                "wiggle", false
        );
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnUpdate.setBorder(new EmptyBorder(11, 18, 11, 18));
        btnUpdate.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnUpdate.setPreferredSize(new Dimension(Integer.MAX_VALUE, 44));
        btnUpdate.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnUpdate.addActionListener(e -> handleUpdateStudent());
        left.add(btnUpdate);
        left.add(Box.createVerticalStrut(8));

        // ── DELETE  (full width) ──
        AnimatedButton btnDelete = new AnimatedButton(
                "🗑️", "Delete Student",
                new Color(254, 242, 242), DANGER_COLOR, new Color(254, 226, 226),
                "shake", false
        );
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDelete.setBorder(new EmptyBorder(11, 18, 11, 18));
        btnDelete.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnDelete.setPreferredSize(new Dimension(Integer.MAX_VALUE, 44));
        btnDelete.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnDelete.addActionListener(e -> handleDeleteStudent());
        left.add(btnDelete);
        left.add(Box.createVerticalStrut(8));

        // ── CLEAR FORM  (full width, slightly shorter) ──
        AnimatedButton btnClear = new AnimatedButton(
                "🧹", "Clear Form",
                new Color(248, 250, 252), TEXT_MUTED, new Color(241, 245, 249),
                "sweep", false
        );
        btnClear.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnClear.setBorder(new EmptyBorder(9, 18, 9, 18));
        btnClear.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnClear.setPreferredSize(new Dimension(Integer.MAX_VALUE, 38));
        btnClear.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnClear.addActionListener(e -> {
            clearForm();
            table.clearSelection();
            showToast("Form selection and inputs cleared", TEXT_MUTED, "🧹");
        });
        left.add(btnClear);

        left.add(Box.createVerticalStrut(22));


        // Grade Distribution Widget
        JLabel lblDistTitle = new JLabel("Grade Distribution");
        lblDistTitle.setFont(fontSection);
        lblDistTitle.setForeground(TEXT_PRIMARY);
        left.add(lblDistTitle);
        left.add(Box.createVerticalStrut(8));

        lblDistA = new JLabel("Grade A (90-100): 0");
        lblDistB = new JLabel("Grade B (80-89):  0");
        lblDistC = new JLabel("Grade C (70-79):  0");
        lblDistD = new JLabel("Grade D (60-69):  0");
        lblDistF = new JLabel("Grade F (<60):    0");

        Font distFont = new Font("Consolas", Font.PLAIN, 12);
        for (JLabel l : new JLabel[]{lblDistA, lblDistB, lblDistC, lblDistD, lblDistF}) {
            l.setFont(distFont);
            l.setForeground(TEXT_PRIMARY);
            left.add(l);
            left.add(Box.createVerticalStrut(4));
        }

        left.add(Box.createVerticalGlue());
        return left;
    }

    private JPanel createCenterTablePanel() {
        JPanel right = new JPanel(new BorderLayout(0, 10));
        right.setBackground(BG_CARD);
        right.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        // Top Filter Bar
        JPanel filterBar = new JPanel(new BorderLayout(10, 0));
        filterBar.setBackground(BG_CARD);

        JLabel lblFilter = new JLabel("🔍 Search / Filter:");
        lblFilter.setFont(fontBold);
        lblFilter.setForeground(TEXT_PRIMARY);
        filterBar.add(lblFilter, BorderLayout.WEST);

        txtSearch = new JTextField();
        styleTextField(txtSearch);
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });
        filterBar.add(txtSearch, BorderLayout.CENTER);

        right.add(filterBar, BorderLayout.NORTH);

        // Student Table
        String[] columnNames = {"ID", "Student Name", "Score", "Grade", "Status", "Remarks"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(fontBody);
        table.setGridColor(BORDER_COLOR);
        table.setShowGrid(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setSelectionBackground(new Color(238, 242, 255));
        table.setSelectionForeground(PRIMARY_COLOR);

        // Header Style
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(TEXT_PRIMARY);
        header.setPreferredSize(new Dimension(0, 34));

        // Custom Cell Renderers
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Score
        table.getColumnModel().getColumn(3).setCellRenderer(new GradeBadgeRenderer()); // Grade
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeRenderer()); // Status

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(70);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(140);

        // On table row select -> populate form
        table.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                txtName.setText((String) table.getValueAt(selectedRow, 1));
                txtScore.setText(String.valueOf(table.getValueAt(selectedRow, 2)));
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        right.add(scrollPane, BorderLayout.CENTER);

        return right;
    }

    private JPanel createBottomStatusBar() {
        JPanel statusPanel = new JPanel(new BorderLayout(12, 0));
        statusPanel.setBackground(BG_MAIN);
        statusPanel.setBorder(new EmptyBorder(4, 24, 12, 24));

        lblStatus = new JLabel("System Ready | Use buttons to manage students");
        lblStatus.setFont(fontSub);
        lblStatus.setForeground(TEXT_MUTED);

        toastPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                if (toastOpacity > 0.01f) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, toastOpacity));
                    g2.setColor(toastBgColor);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                    g2.setColor(new Color(255, 255, 255, (int) (toastOpacity * 50)));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        toastPanel.setOpaque(false);
        toastPanel.setBorder(new EmptyBorder(3, 12, 3, 12));

        lblToast = new JLabel("");
        lblToast.setFont(fontBold);
        lblToast.setForeground(Color.WHITE);
        toastPanel.add(lblToast);
        toastPanel.setVisible(false);

        statusPanel.add(lblStatus, BorderLayout.WEST);
        statusPanel.add(toastPanel, BorderLayout.EAST);
        return statusPanel;
    }

    private void showToast(String message, Color bgColor, String icon) {
        lblToast.setText(icon + "  " + message);
        toastBgColor = bgColor;
        toastPanel.setVisible(true);

        if (toastTimer != null && toastTimer.isRunning()) {
            toastTimer.stop();
        }

        toastOpacity = 0.0f;
        toastTimer = new Timer(16, new ActionListener() {
            private int frame = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                frame++;
                if (frame <= 10) { // fade in over ~160ms
                    toastOpacity = frame / 10.0f;
                    toastPanel.repaint();
                } else if (frame > 160) { // after ~2.5s start fading out
                    toastOpacity = Math.max(0f, toastOpacity - 0.08f);
                    toastPanel.repaint();
                    if (toastOpacity <= 0.01f) {
                        toastPanel.setVisible(false);
                        ((Timer) e.getSource()).stop();
                    }
                }
            }
        });
        toastTimer.start();
        lblStatus.setText(icon + "  " + message);
    }

    private void selectStudentInTable(int id) {
        for (int i = 0; i < table.getRowCount(); i++) {
            if ((int) table.getValueAt(i, 0) == id) {
                table.setRowSelectionInterval(i, i);
                table.scrollRectToVisible(table.getCellRect(i, 0, true));
                break;
            }
        }
    }

    private void handleAddStudent() {
        String name = txtName.getText().trim();
        String scoreStr = txtScore.getText().trim();

        if (name.isEmpty()) {
            showError("Please enter student name.");
            txtName.requestFocus();
            return;
        }

        try {
            double score = Double.parseDouble(scoreStr);
            if (score < 0 || score > 100) {
                showError("Score must be between 0.0 and 100.0.");
                txtScore.requestFocus();
                return;
            }

            Student s = service.addStudent(name, score);
            clearForm();
            refreshAllData();
            selectStudentInTable(s.getId());
            showToast("Added " + name + " (Score: " + score + " | Grade: " + s.getLetterGrade() + ")", SUCCESS_COLOR, "➕");
        } catch (NumberFormatException e) {
            showError("Please enter a valid numeric score (e.g. 88.5).");
            txtScore.requestFocus();
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void handleUpdateStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            showError("Please select a student in the table to update.");
            return;
        }

        int id = (int) table.getValueAt(selectedRow, 0);
        String name = txtName.getText().trim();
        String scoreStr = txtScore.getText().trim();

        if (name.isEmpty()) {
            showError("Student name cannot be empty.");
            return;
        }

        try {
            double score = Double.parseDouble(scoreStr);
            if (score < 0 || score > 100) {
                showError("Score must be between 0.0 and 100.0.");
                return;
            }

            boolean ok = service.updateStudent(id, name, score);
            if (ok) {
                refreshAllData();
                selectStudentInTable(id);
                showToast("Updated student #" + id + " (" + name + " -> " + score + ")", PRIMARY_COLOR, "✏️");
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid numeric score.");
        }
    }

    private void handleDeleteStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            showError("Please select a student from the table to delete.");
            return;
        }

        int id = (int) table.getValueAt(selectedRow, 0);
        String name = (String) table.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete student: " + name + " (ID: " + id + ")?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            service.removeStudent(id);
            clearForm();
            refreshAllData();
            showToast("Deleted student record #" + id + " (" + name + ")", DANGER_COLOR, "🗑️");
        }
    }

    private void filterTable() {
        String query = txtSearch.getText().trim();
        List<Student> filtered = service.searchStudents(query);
        populateTable(filtered);
    }

    private void refreshAllData() {
        filterTable();

        // Update KPIs
        lblTotalStudents.setText(String.valueOf(service.getStudentCount()));
        lblAverageScore.setText(String.format("%.2f", service.calculateAverage()));

        if (service.getHighestScorer().isPresent()) {
            Student h = service.getHighestScorer().get();
            lblHighestScore.setText(String.format("%.1f (%s)", h.getScore(), h.getName()));
        } else {
            lblHighestScore.setText("-");
        }

        if (service.getLowestScorer().isPresent()) {
            Student l = service.getLowestScorer().get();
            lblLowestScore.setText(String.format("%.1f (%s)", l.getScore(), l.getName()));
        } else {
            lblLowestScore.setText("-");
        }

        lblPassingRate.setText(String.format("%.1f%%", service.getPassingPercentage()));

        // Update Grade Distribution
        Map<String, Integer> dist = service.getGradeDistribution();
        lblDistA.setText(String.format("Grade A (90-100) : %d", dist.getOrDefault("A", 0)));
        lblDistB.setText(String.format("Grade B (80-89)  : %d", dist.getOrDefault("B", 0)));
        lblDistC.setText(String.format("Grade C (70-79)  : %d", dist.getOrDefault("C", 0)));
        lblDistD.setText(String.format("Grade D (60-69)  : %d", dist.getOrDefault("D", 0)));
        lblDistF.setText(String.format("Grade F (<60)    : %d", dist.getOrDefault("F", 0)));
    }

    private void populateTable(List<Student> list) {
        tableModel.setRowCount(0);
        for (Student s : list) {
            tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getName(),
                    s.getScore(),
                    s.getLetterGrade(),
                    s.getStatus(),
                    s.getPerformanceRemarks()
            });
        }
    }

    private void exportReportToFile() {
        if (service.isEmpty()) {
            showError("No data available to export.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("student_grade_report.txt"));
        int userChoice = fileChooser.showSaveDialog(this);

        if (userChoice == JFileChooser.APPROVE_OPTION) {
            File targetFile = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(targetFile)) {
                writer.write(service.generateSummaryReport());
                showToast("Report exported successfully to: " + targetFile.getName(), SUCCESS_COLOR, "📄");
                JOptionPane.showMessageDialog(this,
                        "Report exported successfully to:\n" + targetFile.getAbsolutePath(),
                        "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                showError("Error writing file: " + ex.getMessage());
            }
        }
    }

    private void clearForm() {
        txtName.setText("");
        txtScore.setText("");
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Input Warning", JOptionPane.WARNING_MESSAGE);
    }

    private void styleTextField(JTextField field) {
        field.setFont(fontBody);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 32));
        field.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));
    }

    /**
     * Custom animated button with smooth hover fade, dynamic icon micro-animations,
     * glowing borders, tactile press depression, and click ripples.
     */
    public static class AnimatedButton extends JButton {
        private final Color baseBg;
        private final Color hoverBg;
        private final Color pressedBg;
        private final Color baseFg;
        private final String symbol;
        private final String labelText;
        private final String animationType; // "spin", "wiggle", "shake", "shimmer", "lift", "sweep"

        private float hoverProgress = 0f;
        private float animTick = 0f;
        private boolean isHovered = false;
        private boolean isPressed = false;

        private float rippleRadius = 0f;
        private float rippleAlpha = 0f;
        private Point rippleCenter = new Point(0, 0);

        private final Timer hoverTimer;
        private final Timer rippleTimer;

        public AnimatedButton(String symbol, String labelText, Color bg, Color fg, Color hoverBg, String animType, boolean bold) {
            super();
            this.symbol = symbol;
            this.labelText = labelText;
            this.baseBg = bg;
            this.baseFg = fg;
            this.hoverBg = hoverBg;
            this.pressedBg = hoverBg.darker();
            this.animationType = animType;

            setFont(bold ? new Font("Segoe UI", Font.BOLD, 13) : new Font("Segoe UI", Font.PLAIN, 13));
            setForeground(fg);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(8, 14, 8, 14));

            // Smooth hover animation timer (~60 fps)
            hoverTimer = new Timer(16, e -> {
                boolean repaintNeeded = false;
                if (isHovered) {
                    if (hoverProgress < 1.0f) {
                        hoverProgress = Math.min(1.0f, hoverProgress + 0.12f);
                        repaintNeeded = true;
                    }
                    animTick += 0.22f;
                    if (animTick > (float) (Math.PI * 20)) animTick = 0f;
                    repaintNeeded = true;
                } else {
                    if (hoverProgress > 0.0f) {
                        hoverProgress = Math.max(0.0f, hoverProgress - 0.12f);
                        repaintNeeded = true;
                    } else {
                        ((Timer) e.getSource()).stop();
                    }
                }
                if (repaintNeeded) repaint();
            });

            // Click ripple expansion timer
            rippleTimer = new Timer(16, e -> {
                rippleRadius += 4.5f;
                rippleAlpha = Math.max(0f, rippleAlpha - 0.05f);
                repaint();
                if (rippleAlpha <= 0.01f) {
                    ((Timer) e.getSource()).stop();
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    if (!hoverTimer.isRunning()) hoverTimer.start();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    if (!hoverTimer.isRunning()) hoverTimer.start();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    isPressed = true;
                    rippleCenter = e.getPoint();
                    rippleRadius = 4f;
                    rippleAlpha = 0.35f;
                    if (rippleTimer.isRunning()) rippleTimer.stop();
                    rippleTimer.start();
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    isPressed = false;
                    repaint();
                }
            });
        }

        private static Color blend(Color c1, Color c2, float ratio) {
            float iRatio = 1.0f - ratio;
            int r = Math.min(255, Math.max(0, (int) (c1.getRed() * iRatio + c2.getRed() * ratio)));
            int g = Math.min(255, Math.max(0, (int) (c1.getGreen() * iRatio + c2.getGreen() * ratio)));
            int b = Math.min(255, Math.max(0, (int) (c1.getBlue() * iRatio + c2.getBlue() * ratio)));
            return new Color(r, g, b);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // 1. Background fill with smooth color transition
            Color currentBg = blend(baseBg, hoverBg, hoverProgress);
            if (isPressed) {
                currentBg = pressedBg;
            }
            g2.setColor(currentBg);
            g2.fillRoundRect(0, 0, w, h, 10, 10);

            // 2. Glowing animated border on hover
            if (hoverProgress > 0.01f) {
                int alpha = (int) (hoverProgress * 180);
                Color glowColor = new Color(hoverBg.getRed(), hoverBg.getGreen(), hoverBg.getBlue(), alpha);
                g2.setColor(glowColor);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, w - 2, h - 2, 10, 10);
            } else if (baseBg.getRed() > 210 && baseBg.getGreen() > 210 && baseBg.getBlue() > 210) {
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 10, 10);
            }

            // 3. Expanding ripple on click
            if (rippleAlpha > 0.01f) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, rippleAlpha));
                g2.setColor(Color.WHITE);
                int r = (int) rippleRadius;
                g2.fillOval(rippleCenter.x - r, rippleCenter.y - r, r * 2, r * 2);
                g2.setComposite(AlphaComposite.SrcOver);
            }

            // 4. Content translation when pressed (tactile click)
            if (isPressed) {
                g2.translate(0, 1);
            }

            // 5. Measure symbol and text
            FontMetrics fm = g2.getFontMetrics(getFont());
            Font symbolFont = new Font("Segoe UI Emoji", Font.PLAIN, getFont().getSize() + 1);
            FontMetrics sfm = g2.getFontMetrics(symbolFont);

            int symbolW = sfm.stringWidth(symbol);
            int textW = fm.stringWidth(labelText);
            int gap = symbol.isEmpty() ? 0 : 7;
            int totalContentW = symbolW + gap + textW;

            int startX = (w - totalContentW) / 2;
            int textY = (h - fm.getHeight()) / 2 + fm.getAscent();

            // 6. Draw animated symbol
            if (!symbol.isEmpty()) {
                Graphics2D gSymbol = (Graphics2D) g2.create();
                gSymbol.setFont(symbolFont);
                gSymbol.setColor(baseFg);

                int symCenterX = startX + symbolW / 2;
                int symCenterY = textY - sfm.getAscent() / 2;

                gSymbol.translate(symCenterX, symCenterY);

                // Specific symbol animations based on hover progress and animTick
                if (hoverProgress > 0f) {
                    switch (animationType) {
                        case "spin" -> { // ➕ Add student: spins smoothly
                            double angle = (hoverProgress * Math.PI / 2.0) + (isHovered ? Math.sin(animTick) * 0.1 : 0);
                            gSymbol.rotate(angle);
                        }
                        case "wiggle" -> { // ✏️ Update: tilts back and forth
                            double wiggle = Math.sin(animTick * 1.5) * 0.3 * hoverProgress;
                            gSymbol.rotate(wiggle);
                        }
                        case "shake" -> { // 🗑️ Delete: warning vibration
                            double shakeX = Math.sin(animTick * 3.0) * 2.0 * hoverProgress;
                            gSymbol.translate(shakeX, 0);
                        }
                        case "shimmer" -> { // ✨ Demo data: pulses & shines
                            double scale = 1.0 + Math.sin(animTick * 2.0) * 0.22 * hoverProgress;
                            gSymbol.scale(scale, scale);
                        }
                        case "lift" -> { // 📄 Export: lifts up
                            double liftY = -3.0 * hoverProgress;
                            gSymbol.translate(0, liftY);
                        }
                        case "sweep" -> { // 🧹 Clear: sweeps left-right
                            double sweepAngle = Math.sin(animTick * 1.8) * 0.35 * hoverProgress;
                            gSymbol.rotate(sweepAngle);
                        }
                        default -> {}
                    }
                }

                gSymbol.drawString(symbol, -symbolW / 2, sfm.getAscent() / 2);
                gSymbol.dispose();
            }

            // 7. Draw text label
            g2.setFont(getFont());
            g2.setColor(baseFg);
            g2.drawString(labelText, startX + symbolW + gap, textY);

            g2.dispose();
        }
    }

    // Custom table cell renderer for grade letter badge
    private static class GradeBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int col) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (!isSelected && value != null) {
                String grade = value.toString();
                if ("A".equals(grade)) {
                    setForeground(new Color(16, 185, 129));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if ("B".equals(grade)) {
                    setForeground(new Color(59, 130, 246));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if ("F".equals(grade)) {
                    setForeground(new Color(239, 68, 68));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else {
                    setForeground(new Color(217, 119, 6));
                    setFont(getFont().deriveFont(Font.BOLD));
                }
            }
            return c;
        }
    }

    // Custom table cell renderer for Status (Pass / Fail)
    private static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int col) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (!isSelected && value != null) {
                if ("Pass".equals(value.toString())) {
                    setForeground(new Color(16, 185, 129));
                } else {
                    setForeground(new Color(239, 68, 68));
                }
                setFont(getFont().deriveFont(Font.BOLD));
            }
            return c;
        }
    }
}
