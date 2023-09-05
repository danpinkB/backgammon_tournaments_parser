package backgammon.tournaments.parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DbConnection {
    private Connection conn;
    private Statement smt;
    public DbConnection() throws IOException, SQLException, ClassNotFoundException {
        Properties properties = new Properties();
        Class.forName("org.postgresql.Driver");
        FileInputStream fileInputStream = new FileInputStream(System.getProperty("user.dir")+"/src/main/resources/config.properties");
        properties.load(fileInputStream);
        fileInputStream.close();
        String dbUrl = properties.getProperty("DB_URL");
        String dbUsername = properties.getProperty("DB_USERNAME");
        String dbPassword = properties.getProperty("DB_PASSWORD");
        this.conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        this.smt = this.conn.createStatement();

        smt.executeUpdate("" +
                "CREATE TABLE IF NOT EXISTS PLAYERS_RESULTS(" +
                    "id SERIAL PRIMARY KEY," +
                    "tour_name VARCHAR(500)," +
                    "tour_location VARCHAR(500)," +
                    "tour_dates VARCHAR(500)," +
                    "sub_tour_name VARCHAR(500)," +
                    "name VARCHAR(500)," +
                    "place VARCHAR(500)," +
                    "country VARCHAR(500)" +
                ")");
    }

    public void insertResult(PlayerResult result, String tour_name, String dates, String location) throws SQLException {
        String query = "INSERT INTO PLAYERS_RESULTS (tour_name, tour_location, tour_dates, sub_tour_name, name, place,country) VALUES(?,?,?,?,?,?,?)";
        PreparedStatement prepare = conn.prepareStatement(query);
        prepare.setString(1, tour_name);
        prepare.setString(2, location);
        prepare.setString(3, dates);
        prepare.setString(4, result.getSubTour());
        prepare.setString(5, result.getName());
        prepare.setString(6, result.getPlace());
        prepare.setString(7, result.getCountry());
//        System.out.println(prepare.toString());
        prepare.executeUpdate();
    }
}
