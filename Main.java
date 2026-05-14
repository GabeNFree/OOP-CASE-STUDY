public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            WorldDatabase worldDatabase = new WorldDatabase();
            worldDatabase.loadData();
            DashboardWindow dashboardWindow = new DashboardWindow(worldDatabase);
            dashboardWindow.setVisible(true);
        });
    }
}
