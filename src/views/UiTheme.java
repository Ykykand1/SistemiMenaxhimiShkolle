package views;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public final class UiTheme {
    public static final Color PRIMARY = new Color(26, 35, 126);
    public static final Color PRIMARY_LIGHT = new Color(237, 240, 255);
    public static final Color BACKGROUND = Color.WHITE;
    public static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    public static final Color TEXT_SECONDARY = new Color(75, 75, 75);
    public static final Color DANGER = new Color(211, 47, 47);
    public static final Color GRID = new Color(224, 224, 224);
    public static final Color SELECTION = new Color(224, 242, 241);
    public static final Color TABLE_HEADER_BG = Color.BLACK;
    public static final Color TABLE_HEADER_FG = Color.WHITE;

    private UiTheme() {}

    public static void install() {
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("TabbedPane.background", BACKGROUND);
        UIManager.put("TabbedPane.contentAreaColor", BACKGROUND);
        UIManager.put("Viewport.background", BACKGROUND);
        UIManager.put("ScrollPane.background", BACKGROUND);
        UIManager.put("OptionPane.background", BACKGROUND);
        UIManager.put("TableHeader.background", TABLE_HEADER_BG);
        UIManager.put("TableHeader.foreground", TABLE_HEADER_FG);
    }

    public static void stylePanel(JComponent component) {
        component.setBackground(BACKGROUND);
        component.setOpaque(true);
    }

    public static void styleHeaderPanel(JPanel panel) {
        panel.setBackground(PRIMARY);
        panel.setOpaque(true);
    }

    public static void styleHeaderTitle(JLabel label) {
        label.setForeground(Color.WHITE);
    }

    public static void styleHeaderLogoutButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(Color.WHITE);
        button.setForeground(PRIMARY);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }

    public static void styleFormLabel(JLabel label) {
        label.setForeground(TEXT_PRIMARY);
    }

    public static void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }

    public static void stylePrimaryButton(JButton button, int topBottom, int leftRight) {
        stylePrimaryButton(button);
        button.setBorder(BorderFactory.createEmptyBorder(topBottom, leftRight, topBottom, leftRight));
    }

    public static void styleSecondaryButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(BACKGROUND);
        button.setForeground(PRIMARY);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setBorder(new LineBorder(PRIMARY, 1));
    }

    public static void styleDangerButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(DANGER);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }

    public static void styleTabbedPane(JTabbedPane tabbedPane) {
        tabbedPane.setBackground(BACKGROUND);
        tabbedPane.setForeground(TEXT_PRIMARY);
        tabbedPane.setOpaque(true);
    }

    public static void styleTable(JTable table) {
        JTableHeader header = table.getTableHeader();
        if (header != null) {
            Font headerFont = new Font("Segoe UI", Font.BOLD, 12);
            header.setFont(headerFont);
            header.setBackground(TABLE_HEADER_BG);
            header.setForeground(TABLE_HEADER_FG);
            header.setOpaque(true);
            header.setReorderingAllowed(false);
            header.setDefaultRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(
                        JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    JLabel label = (JLabel) super.getTableCellRendererComponent(
                            tbl, value, isSelected, hasFocus, row, column);
                    label.setOpaque(true);
                    label.setBackground(TABLE_HEADER_BG);
                    label.setForeground(TABLE_HEADER_FG);
                    label.setFont(headerFont);
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, GRID));
                    return label;
                }
            });
        }
        table.setBackground(BACKGROUND);
        table.setForeground(TEXT_PRIMARY);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setGridColor(GRID);
        table.setSelectionBackground(SELECTION);
        table.setSelectionForeground(TEXT_PRIMARY);
    }

    public static void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.getViewport().setBackground(BACKGROUND);
        scrollPane.setBackground(BACKGROUND);
        scrollPane.setBorder(BorderFactory.createLineBorder(GRID));
    }
}
