package DB;

import java.sql.*;

public class DbHelper {
    private Connection connection = null;

    static {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        String dbUrl = "jdbc:oracle:thin:@localhost:1521:orcl";
        try {
            connection = DriverManager.getConnection(dbUrl, "c##tcs", "tcs");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void doTask(String query) {
        ResultSet result = null;
        Statement statement = null;
        try {
            statement = connection.createStatement();
            result = statement.executeQuery(query);
            while (result.next()) {
                System.out.println(result.getString(1));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void finish() {
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

//    public static void getVal() {
//        ResultSet result = null;
//        Connection connection = null;
//        Statement statement = null;
//
//        try {
//            String dbUrl = "jdbc:oracle:thin:@localhost:1521:orcl";
//            connection = DriverManager.getConnection(dbUrl, "c##tcs", "tcs");
//            statement = connection.createStatement();
//
//            result = statement.executeQuery("SELECT * FROM CARD_DATA");
//
//            while (result.next()) {
//                System.out.println(result.getString(1));
//            }
//        } catch (SQLException e) {
//
//        } finally {
//
//            try {
//                result.close();
//                statement.close();
//                connection.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }

}
