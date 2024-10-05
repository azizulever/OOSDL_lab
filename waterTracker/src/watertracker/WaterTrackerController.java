package watertracker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class WaterTrackerController {

    @FXML
    private Label countLabel, goalLabel;
    
    @FXML
    private TextField goalInput, usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    
    @FXML
    private ListView<String> historyListView;
    @FXML
    private ProgressBar progressBar;

    private int waterCount = 0;
    private int waterGoal = 8;
    private ObservableList<String> waterHistory = FXCollections.observableArrayList();
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
     // Database connection parameters
    private final String DB_URL = "jdbc:mysql://localhost:3306/watertracker";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "";
    
   

    @FXML
    public void initialize() {
        historyListView.setItems(waterHistory);
        scheduler.scheduleAtFixedRate(this::sendReminder, 0, 1, TimeUnit.HOURS);
        
        loadHistoryFromDatabase();
    }
    
    private void loadHistoryFromDatabase() {
        String querySQL = "SELECT description FROM drinklog";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(querySQL);
             ResultSet rs = pstmt.executeQuery()) {

            // Iterate over the result set and add each entry to waterHistory
            while (rs.next()) {
                String log = rs.getString("description");
                waterHistory.add(log);
            }

            System.out.println("History loaded from database.");

        } catch (SQLException e) {
            System.out.println("Error loading history from database: " + e.getMessage());
        }
    }

    @FXML
    public void handleAddGlass() {
        waterCount++;
        updateCountLabel();
    }

    @FXML
    public void handleReset() {
        waterCount = 0;
        updateCountLabel();
    }

    @FXML
    public void handleSetGoal() {
        waterGoal = Integer.parseInt(goalInput.getText());
        updateGoalLabel();
    }

    @FXML
    public void handleEndDay() {
        addToHistory();
        waterCount = 0;
        updateCountLabel();
    }
    

    @FXML
    public void handleExportToCSV() {
       FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save History CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
         Window window = historyListView.getScene().getWindow();
       
         Stage stage = (Stage) window;
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                // Write the headers for CSV
                writer.write("Date,Water Intake\n");

                // Write each history item
                for (String item : historyListView.getItems()) {
                    writer.write(item + "\n");
                }

                System.out.println("Export successful: " + file.getAbsolutePath());
            } catch (IOException ex) {
                System.out.println("An error occurred while exporting: " + ex.getMessage());
            }
        }
    }

    private void addToHistory() {
        String historyEntry = "Day " + LocalDate.now() + ": " + waterCount + " glasses";
        waterHistory.add(historyEntry);
        
        saveToDatabase(historyEntry);
    }
    
    private void saveToDatabase(String historyEntry) {
        String insertSQL = "INSERT INTO drinklog (description) VALUES (?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            // Set the history entry in the prepared statement
            pstmt.setString(1, historyEntry);

            // Execute the insert operation
            pstmt.executeUpdate();

            System.out.println("History saved to database: " + historyEntry);

        } catch (SQLException e) {
            System.out.println("Error saving history to database: " + e.getMessage());
        }
    }

    private void updateCountLabel() {
        countLabel.setText("Glasses of Water Today: " + waterCount);
        updateProgressBar();
    }

    private void updateGoalLabel() {
        goalLabel.setText("Daily Goal: " + waterCount + "/" + waterGoal);
    }

    private void updateProgressBar() {
        double progress = (double) waterCount / waterGoal;
        progressBar.setProgress(progress);
    }

    private void sendReminder() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Water Reminder");
            alert.setHeaderText(null);
            alert.setContentText("Time to drink water!");
            alert.showAndWait();
        });
    }

    // Ensure proper shutdown of the scheduler
    public void stop() throws Exception {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
}
