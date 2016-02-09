package me.streib.janis.dbaufzug;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

public class DatabaseConnection {
    private Connection connection;
    private static JodelWebConfiguration config;
    private HashMap<String, PreparedStatement> statements = new HashMap<String, PreparedStatement>();
    private long lastAction = System.currentTimeMillis();
    private Statement adHoc;
    private static final int CONNECTION_TIMEOUT = 24 * 60 * 60;


    public DatabaseConnection() {
        try {
            Class.forName(config.getJDBCDriver());
        } catch (ClassNotFoundException e) {
            System.err.println("Error loading database driver!");
            e.printStackTrace();
        }
        tryConnect();
    }

    public static void init(JodelWebConfiguration conf) {
        config = conf;
    }

    public Connection getConnection() {
        return connection;
    }

    public static DatabaseConnection getInstance() {
        return instances.get();
    }

    static ThreadLocal<DatabaseConnection> instances = new ThreadLocal<DatabaseConnection>() {

        @Override
        protected DatabaseConnection initialValue() {
            return new DatabaseConnection();
        }
    };

    public PreparedStatement prepare(String query) throws SQLException {
        ensureOpen();
        PreparedStatement statement = statements.get(query);
        if (statement == null) {
            statement = connection.prepareStatement(query,
                    Statement.RETURN_GENERATED_KEYS);
            statements.put(query, statement);
        }
        return statement;
    }

    private void tryConnect() {
    	
        try {
            connection = DriverManager.getConnection(config.getDB()
                    + "?zeroDateTimeBehavior=convertToNull",
                    config.getDBUser(), config.getDBPW());
            PreparedStatement ps = connection
                    .prepareStatement("SET SESSION wait_timeout=?;");
            ps.setInt(1, CONNECTION_TIMEOUT);
            ps.execute();
            ps.close();
            adHoc = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void ensureOpen() {
        if (System.currentTimeMillis() - lastAction > CONNECTION_TIMEOUT * 1000L) {
            try {
                ResultSet rs = adHoc.executeQuery("SELECT 1");
                rs.close();
                lastAction = System.currentTimeMillis();
                return;
            } catch (SQLException e) {
            }
            statements.clear();
            tryConnect();
        }
        lastAction = System.currentTimeMillis();
    }

    public static int lastInsertId(PreparedStatement query) throws SQLException {
        ResultSet rs = query.getGeneratedKeys();
        rs.next();
        int id = rs.getInt(1);
        rs.close();
        return id;
    }
}
