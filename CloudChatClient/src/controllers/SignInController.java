package controllers;

import ServerInterfaces.LoginInterface;
import clientInterfaceImpl.ClientImpl;
import clientmain.CloudChat;
import dtos.UserDto;
import exceptions.ResultNotFoundException;
import exceptions.WrongPasswordException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
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
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author AMUN
 */
public class SignInController implements Initializable {

    @FXML
    private Button button1;
    @FXML
    private TextField textFieldMail;
    @FXML
    private PasswordField passwordFieldPassword;
    @FXML
    private ImageView close;
    @FXML
    private ImageView minimize;
    @FXML
    private AnchorPane anchorBase;
    @FXML
    private Label userErrorLabel;
    @FXML
    private Label signUpLink;
    @FXML
    AnchorPane windowBar;
    double xOffset;
    double yOffset;

    private static Stage primaryStage;
    private static LoginInterface servLoginRef;
    private static UserDto myBean;
    private final URL mainPagePath = getClass().getResource("/fxmlpkg/MainInterface.fxml");
    private final URL signUpPagePath = getClass().getResource("/fxmlpkg/SignUp.fxml");
    String regex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    public static ClientImpl client;
    private static MainUIController mainController;

    public static MainUIController getMainController() {
        return mainController;
    }

    /**
     * Initializes the controller class.
     *
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        textFieldMail.setFocusTraversable(false);
        passwordFieldPassword.setFocusTraversable(false);
        primaryStage = CloudChat.getPrimaryStage();
        servLoginRef = CloudChat.getServLoginRef();
        windowBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = signUpLink.getScene().getWindow().getX() - event.getScreenX();
                yOffset = signUpLink.getScene().getWindow().getY() - event.getScreenY();
            }
        });

        windowBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                anchorBase.getScene().getWindow().setX(event.getScreenX() + xOffset);
                anchorBase.getScene().getWindow().setY(event.getScreenY() + yOffset);
            }
        });
        signUpLink.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                try {
                    Parent root = FXMLLoader.load(signUpPagePath);
                    primaryStage.setScene(new Scene(root));
                } catch (IOException ex) {
                    Logger.getLogger(SignInController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

    }

    @FXML
    private void signIn() {
        boolean flag = false;
        String mail = textFieldMail.getText();
        String password = passwordFieldPassword.getText();
        if (servLoginRef != null) {
            if (mail.toLowerCase().matches(regex)) {
                if (!password.equalsIgnoreCase("")) {
                    userErrorLabel.setText("");
                    try {
                        myBean = servLoginRef.login(mail, password);
                        if (myBean == null) {
                            userErrorLabel.setText("Account already Signed in");
                            return;
                        }
                        flag = true;
                    } catch (ResultNotFoundException ex) {
                        userErrorLabel.setText("User Doesn't Exist");
                    } catch (RemoteException ex) {
                        userErrorLabel.setText("Could't Connect to the Server");
                        CloudChat.refresh();
                        servLoginRef = CloudChat.getServLoginRef();
                    } catch (WrongPasswordException ex) {
                        userErrorLabel.setText("Wrong Password");
                    }
                    if (flag) {
                        try {
                            FXMLLoader loader = new FXMLLoader(mainPagePath);
                            mainController = new MainUIController();
                            loader.setController(mainController);
                            client = new ClientImpl(mainController);
                            Parent root = loader.load();
                            CloudChat.getServFunctionsRef().register(myBean.getId(), client);
                            primaryStage.setScene(new Scene(root));
                        } catch (IOException ex) {
                            Logger.getLogger(SignInController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                } else {

                    userErrorLabel.setText("Please Enter Your Password");
                }
            } else {
                userErrorLabel.setText("Wrong E-mail format");
            }
        } else {
            CloudChat.refresh();
            servLoginRef = CloudChat.getServLoginRef();
            userErrorLabel.setText("Could't Connect to the Server");
        }
    }

    @FXML
    private void closeWindow() {
        System.exit(0);
    }

    @FXML
    private void minimizeWindow() {
        ((Stage) minimize.getScene().getWindow()).setIconified(true);
    }

    public static UserDto getMyBean() {
        return myBean;
    }

    public static ClientImpl getClientRemoteObj() {
        return client;
    }
}
