/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package watertracker;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author N0Hel
 */
public class LoginSceneController implements Initializable {

    @FXML
    private TextField usernameField;
    
    
    @FXML
    private PasswordField passwordField;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void handleLogin(ActionEvent event) {
        System.out.println("OKKK");
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        // Print the details to the console
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        
        if (username.equals("AOE") && password.equals("1234")) {
            try {
                // Load the new FXML file
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("WaterTracker.fxml"));
                Parent root = fxmlLoader.load();

                // Get the current stage
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                // Hide the current scene
                stage.hide();

                // Create a new scene with the loaded FXML
                Scene scene = new Scene(root);

                // Set the new scene to the stage and show it
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            // loginErrorMsg.setText("Wrong Username or Password");
        }
    }
    
}
