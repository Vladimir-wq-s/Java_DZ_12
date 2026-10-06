package vladimir;

import java.sql.*;

public class Task6 {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static final String USER = "system";
    private static final String PASS = "Passw0rdxxx";

    private static void checkAndCreateTables(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Создаем или обновляем таблицу клиентов кафе
            try {
                stmt.execute("CREATE TABLE cafe_customers (id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, customer_name VARCHAR2(100) NOT NULL, discount NUMBER(5,2) DEFAULT 0 NOT NULL, birth_date DATE, email VARCHAR2(100))");
            } catch (SQLException e) {
                try { stmt.execute("ALTER TABLE cafe_customers ADD discount NUMBER(5,2) DEFAULT 0 NOT NULL"); } catch (SQLException ignored) {}
            }

            // Автоматическое добавление тестовых клиентов, если таблица пуста
            String countSql = "SELECT COUNT(*) AS cnt FROM cafe_customers";
            try (ResultSet rs = stmt.executeQuery(countSql)) {
                if (rs.next() && rs.getInt("cnt") == 0) {
                    stmt.execute("INSERT INTO cafe_customers (customer_name, discount, birth_date, email) VALUES ('Alice', 5.0, TO_DATE('1995-04-12', 'YYYY-MM-DD'), 'alice@example.com')");
                    stmt.execute("INSERT INTO cafe_customers (customer_name, discount, birth_date, email) VALUES ('Bob', 15.0, TO_DATE('1988-11-23', 'YYYY-MM-DD'), 'bob@example.com')");
                    stmt.execute("INSERT INTO cafe_customers (customer_name, discount, birth_date, email) VALUES ('Charlie', 5.0, TO_DATE('2001-10-06', 'YYYY-MM-DD'), NULL)");
                    stmt.execute("INSERT INTO cafe_customers (customer_name, discount, birth_date, email) VALUES ('Diana', 12.5, TO_DATE('1992-07-19', 'YYYY-MM-DD'), 'diana@example.com')");
                    System.out.println("Test customers data generated automatically.");
                }
            }
        } catch (SQLException ignored) {}
    }

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            System.out.println("Connected to Oracle Database.");
            checkAndCreateTables(conn);

            // 1. Минимальная скидка
            String sql1 = "SELECT MIN(discount) AS min_disc FROM cafe_customers";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql1)) {
                if (rs.next()) System.out.println("Minimum customer discount: " + rs.getDouble("min_disc") + "%");
            }

            // 2. Максимальная скидка
            String sql2 = "SELECT MAX(discount) AS max_disc FROM cafe_customers";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql2)) {
                if (rs.next()) System.out.println("Maximum customer discount: " + rs.getDouble("max_disc") + "%");
            }

            // 3. Клиенты с минимальной скидкой и её величина
            String sql3 = "SELECT customer_name, discount FROM cafe_customers WHERE discount = (SELECT MIN(discount) FROM cafe_customers)";
            System.out.println("\nCustomers with minimum discount:");
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql3)) {
                while (rs.next()) {
                    System.out.println(" - " + rs.getString("customer_name") + " (Discount: " + rs.getDouble("discount") + "%)");
                }
            }

            // 4. Клиенты с максимальной скидкой и её величина
            String sql4 = "SELECT customer_name, discount FROM cafe_customers WHERE discount = (SELECT MAX(discount) FROM cafe_customers)";
            System.out.println("\nCustomers with maximum discount:");
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql4)) {
                while (rs.next()) {
                    System.out.println(" - " + rs.getString("customer_name") + " (Discount: " + rs.getDouble("discount") + "%)");
                }
            }

            // 5. Средняя величина скидки
            String sql5 = "SELECT AVG(discount) AS avg_disc FROM cafe_customers";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql5)) {
                if (rs.next()) System.out.println("\nAverage customer discount: " + Math.round(rs.getDouble("avg_disc") * 100.0) / 100.0 + "%");
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
