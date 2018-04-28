/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import ServerInterfaces.LoginInterface;
import clientmain.CloudChat;
import dtos.UserDto;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Nouran
 */
public class SignUpController implements Initializable {

    @FXML
    private ImageView close;
    @FXML
    private ImageView minimize;
    @FXML
    private Button button;
    @FXML
    private AnchorPane anchorBase;
    @FXML
    private ScrollPane dataScrollPane;
    @FXML
    private Label labelMail;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField countryField;
    @FXML
    DatePicker birthDatePicker;
    @FXML
    RadioButton maleRadioButton;
    @FXML
    RadioButton femaleRadioButton;
    @FXML
    Label labelPassword;
    @FXML
    Label labelComplete;

    private static LoginInterface servLoginRef;
    private UserDto user;
    double xOffset;
    double yOffset;
    boolean flag;

    private static Stage primaryStage = CloudChat.getPrimaryStage();
    private URL signInPagePath = getClass().getResource("/fxmlpkg/SignIn.fxml");
    String regex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        servLoginRef = CloudChat.getServLoginRef();
        anchorBase.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = anchorBase.getScene().getWindow().getX() - event.getScreenX();
                yOffset = anchorBase.getScene().getWindow().getY() - event.getScreenY();
            }
        });

        anchorBase.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                anchorBase.getScene().getWindow().setX(event.getScreenX() + xOffset);
                anchorBase.getScene().getWindow().setY(event.getScreenY() + yOffset);
            }
        });

    }

    @FXML
    private void signUp() {
        flag = true;
        String fName = firstNameField.getText();
        String lName = lastNameField.getText();
        String mail = emailField.getText();
        String password = passwordField.getText();
        String passwordConfirm = confirmPasswordField.getText();
        String country = countryField.getText();
        LocalDate birthDate = birthDatePicker.getValue();
        String male = maleRadioButton.getText();
        String female = femaleRadioButton.getText();

        if (fName.equalsIgnoreCase("")
                || lName.equalsIgnoreCase("")
                || mail.equalsIgnoreCase("")
                || password.equalsIgnoreCase("")
                || passwordConfirm.equalsIgnoreCase("")
                || country.equalsIgnoreCase("") || birthDate == null) {

            labelComplete.setText("");
            labelPassword.setText("");
            labelMail.setText("");
            labelComplete.setText("Please complete your data");
            flag = false;
        } else {
            labelComplete.setText("");
            labelPassword.setText("");
            labelMail.setText("");
            
            if (!mail.matches(regex)) {
                labelMail.setText("Invalid e-mail");
                flag = false;
            }
            if (!password.equals(passwordConfirm)) {
                labelPassword.setText("Password doesn't match");
                flag = false;
                return;
            }
        }
        if (flag) {
            try {
                user = fillUser();
                if (servLoginRef.SignUp(user)) {
                    Parent root = FXMLLoader.load(signInPagePath);
                    primaryStage.setScene(new Scene(root));
                } else {
                    labelMail.setText("Email ALready\n Exist!!!!");
                }
            } catch (IOException ex) {
                Logger.getLogger(SignInController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                labelComplete.setText("Error in DB");
                Logger.getLogger(SignUpController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
            System.out.println("controllers.SignUpController.signUp()");
    }

    @FXML
    private void closeWindow() {
        System.exit(0);
    }

    @FXML
    private void minimizeWindow() {
        ((Stage) minimize.getScene().getWindow()).setIconified(true);
    }

    private UserDto fillUser() {
        user = new UserDto();
        user.setFname(firstNameField.getText());
        user.setLname(lastNameField.getText());
        user.setMail(emailField.getText());
        user.setPassword(passwordField.getText());
        user.setCountry(countryField.getText());
        Date date = Date.from(birthDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        user.setDob(sqlDate);
        user.setDor(new java.sql.Date(new java.util.Date().getTime()));
        user.setProfilePic(null);
        if (maleRadioButton.isSelected()) {
            user.setGender(UserDto.MALE);
        } else {
            user.setGender(UserDto.FEMALE);
        }
        user.setAppearanceStatus(UserDto.AWAY);
        user.setConnStatus(UserDto.OFFLINE);
        return user;
    }

}
