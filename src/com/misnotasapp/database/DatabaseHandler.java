package com.misnotasapp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class DatabaseHandler {
    private Connection connection;

    public DatabaseHandler() {
        connect(); 
        createTableIfNotExists(); 
        closeConnection(); 
    }

 
    private void logError(String message) {
        try (FileWriter fw = new FileWriter("log.txt", true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(new Date() + ": " + message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void createTableIfNotExists() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:C:/Users/Andrea/Documents/BETO/DAM/ASIGNATURAS/TFM/MisNotasApp/misnotas.db")) {
            String sql = "CREATE TABLE IF NOT EXISTS tasks ("
                       + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                       + "task TEXT NOT NULL, "
                       + "done BOOLEAN DEFAULT 0)";
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
            System.out.println("Tabla 'tasks' creada si no existía.");
        } catch (SQLException e) {
            logError("Error al crear la tabla: " + e.getMessage());
            System.out.println("Error al crear la tabla: " + e.getMessage());
        }
    }

    public Connection connect() {
        try {
            String url = "jdbc:sqlite:C:/Users/Andrea/Documents/BETO/DAM/ASIGNATURAS/TFM/MisNotasApp/misnotas.db";
            connection = DriverManager.getConnection(url);
            System.out.println("Conexión establecida con la base de datos.");
        } catch (SQLException e) {
            logError("Error en la conexión: " + e.getMessage());
            System.out.println("Error en la conexión: " + e.getMessage());
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            logError("Error al cerrar conexión: " + e.getMessage());
            System.out.println("Error al cerrar conexión: " + e.getMessage());
        }
    }
}


