package com.misnotasapp.ui;

import com.misnotasapp.database.DatabaseHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TodoController {
    @FXML
    private TextField textFieldTarea;
    @FXML
    private Button btnAddTarea, btnDeleteTarea, btnMarkComplete;
    @FXML
    private ListView<String> listViewTareas;

    private ObservableList<String> tareas;
    private DatabaseHandler databaseHandler;

    @FXML
    public void initialize() {
        tareas = FXCollections.observableArrayList();
        listViewTareas.setItems(tareas);

        databaseHandler = new DatabaseHandler();
        loadTasksInBackground();  

        btnAddTarea.setOnAction(event -> addTask());
        btnDeleteTarea.setOnAction(event -> deleteTask());
        btnMarkComplete.setOnAction(event -> markTaskAsDone());
    }

   
    private void loadTasksInBackground() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                loadTasks();
                return null;
            }
        };
        
        task.setOnSucceeded(event -> {
            
            listViewTareas.setItems(tareas);
        });

        new Thread(task).start(); 
    }

    
    private void loadTasks() {
        tareas.clear();
        try (Connection connection = databaseHandler.connect()) {
            String query = "SELECT task, done FROM tasks";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String taskText = rs.getString("task");
                boolean done = rs.getBoolean("done");
                if (done) {
                    taskText = "[Hecho] " + taskText;
                }
                tareas.add(taskText);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addTask() {
        String newTask = textFieldTarea.getText();
        if (!newTask.isEmpty()) {
            tareas.add(newTask);
            try (Connection connection = databaseHandler.connect()) {
                String query = "INSERT INTO tasks (task) VALUES (?)";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, newTask);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            textFieldTarea.clear();
        }
    }

    private void deleteTask() {
        String selectedTask = listViewTareas.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            tareas.remove(selectedTask);
            try (Connection connection = databaseHandler.connect()) {
                String query = "DELETE FROM tasks WHERE task = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, selectedTask.replace("[Hecho] ", ""));
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void markTaskAsDone() {
        String selectedTask = listViewTareas.getSelectionModel().getSelectedItem();
        if (selectedTask != null && !selectedTask.startsWith("[Hecho]")) {
            try (Connection connection = databaseHandler.connect()) {
                String query = "UPDATE tasks SET done = 1 WHERE task = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, selectedTask);
                stmt.executeUpdate();

                tareas.remove(selectedTask);
                tareas.add("[Hecho] " + selectedTask);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
