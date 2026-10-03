package vladimir;

import java.sql.*;
import java.util.Scanner;

public class Task5 {
    // Настройки подключения к локальной базе данных Oracle Express Edition (XE)
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static final String USER = "system";
    private static final String PASS = "Passw0rdxxx";

    private static void checkAndCreateTables(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            try { stmt.execute("CREATE TABLE coffee_types (id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, type_name VARCHAR2(100) NOT NULL)"); } catch (SQLException ignored) {}
            try { stmt.execute("CREATE TABLE coffee_orders (id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, coffee_name VARCHAR2(100) NOT NULL, price NUMBER(6,2) NOT NULL, waiter_name VARCHAR2(100), customer_name VARCHAR2(100))"); } catch (SQLException ignored) {}
            try { stmt.execute("CREATE TABLE dessert_orders (id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, dessert_name VARCHAR2(100) NOT NULL, price NUMBER(6,2) NOT NULL)"); } catch (SQLException ignored) {}
            try { stmt.execute("CREATE TABLE work_schedule (id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, work_date DATE NOT NULL, employee_name VARCHAR2(100) NOT NULL)"); } catch (SQLException ignored) {}
        } catch (SQLException ignored) {}
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        scanner.useLocale(java.util.Locale.US);

        // Установка соединения с базой данных Oracle с использованием логина и пароля
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            System.out.println("Connected to Oracle Database.");
            checkAndCreateTables(conn);

            while (true) {
                System.out.println("\n--- Data Management Menu ---");
                System.out.println("1. Add row (Insert)");
                System.out.println("2. Delete row (Delete)");
                System.out.println("3. Update row (Update)");
                System.out.println("4. Exit");
                System.out.print("Select an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                if (choice == 4) break;

                switch (choice) {
                    case 1:
                        System.out.print("Enter coffee type name to add: ");
                        String addName = scanner.nextLine();
                        String insertSql = "INSERT INTO coffee_types (type_name) VALUES (?)";
                        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                            ps.setString(1, addName);
                            ps.executeUpdate();
                            System.out.println("Row inserted.");
                        }
                        break;

                    case 2:
                        System.out.print("Enter coffee type ID to delete: ");
                        int deleteId = scanner.nextInt();
                        String deleteSql = "DELETE FROM coffee_types WHERE id = ?";
                        try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                            ps.setInt(1, deleteId);
                            ps.executeUpdate();
                            System.out.println("Row deleted.");
                        }
                        break;

                    case 3:
                        System.out.print("Enter coffee type ID to update: ");
                        int updateId = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Enter new coffee type name: ");
                        String updateName = scanner.nextLine();
                        String updateSql = "UPDATE coffee_types SET type_name = ? WHERE id = ?";
                        try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                            ps.setString(1, updateName);
                            ps.setInt(2, updateId);
                            ps.executeUpdate();
                            System.out.println("Row updated.");
                        }
                        break;

                    default:
                        System.out.println("Invalid option.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
