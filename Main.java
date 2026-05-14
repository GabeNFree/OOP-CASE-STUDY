public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            DashboardWindow dashboardWindow = new DashboardWindow();
            dashboardWindow.setVisible(true);
        });
    }
}
