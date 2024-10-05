package goldcalculator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GoldCalculatorController {

    @FXML
    private TextField karatField;

    @FXML
    private TextField weightField;

    @FXML
    private Label resultLabel;

    @FXML
    private Button calculateButton;

    @FXML
    private Button clearButton;

    @FXML
    private Button sellButton;

    @FXML
    private ListView<String> salesListView;  // ListView to display sales

    @FXML
    private Button exportButton;

    @FXML
    private Label thankYouLabel;

    private float calculatedPrice = 0;

    @FXML
    void calculatePrice(ActionEvent event) {
        try {
            float karet = Float.parseFloat(karatField.getText());
            float weight = Float.parseFloat(weightField.getText());

            // Calculate price based on karat
            if (karet <= 18) {
                calculatedPrice = (weight / 11.664f) * 65000;
            } else if (karet <= 20) {
                calculatedPrice = (weight / 11.664f) * 70000;
            } else if (karet <= 21) {
                calculatedPrice = (weight / 11.664f) * 75000;
            } else if (karet <= 22) {
                calculatedPrice = (weight / 11.664f) * 80000;
            } else {
                resultLabel.setText("Invalid karat value.");
                return;
            }

            // Display result
            resultLabel.setText("Total Price: " + String.format("%.2f", calculatedPrice) + " TAKA");

        } catch (NumberFormatException e) {
            resultLabel.setText("Please enter valid numbers.");
        }
    }

    @FXML
    void sellGold(ActionEvent event) {
        float karat = Float.parseFloat(karatField.getText());
        float weight = Float.parseFloat(weightField.getText());

        if (calculatedPrice > 0) {
            // Add the sale details to the ListView
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            String currentTime = dtf.format(LocalDateTime.now());
            String saleDetails = "Date: " + currentTime + ", Karat: " + karat + ", Weight: " + weight + "g, Price: " + calculatedPrice + " TAKA";
            salesListView.getItems().add(saleDetails);
        } else {
            resultLabel.setText("Please calculate the price before selling.");
        }
    }

    @FXML
    void clearFields(ActionEvent event) {
        karatField.clear();
        weightField.clear();
        resultLabel.setText("");
        thankYouLabel.setText("");
        calculatedPrice = 0;
    }

    @FXML
    void exportSales(ActionEvent event) {
        // Open a file chooser dialog to save the file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Sales Data");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        Stage stage = (Stage) exportButton.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                for (String sale : salesListView.getItems()) {
                    writer.write(sale + "\n");
                }
                resultLabel.setText("Sales data exported successfully!");
            } catch (IOException e) {
                resultLabel.setText("Error exporting sales data.");
                e.printStackTrace();
            }
        }
    }
}
