package main;

import views.LoginView;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set System Look and Feel for modern native OS rendering
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            views.UiTheme.install();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Launch LoginView
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
        });
    }
}
