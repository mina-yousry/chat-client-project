/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientmain;

import ServerInterfaces.LoginInterface;
import ServerInterfaces.ServerMainInterface;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javax.imageio.ImageIO;
import org.controlsfx.control.Notifications;

/**
 *
 * @author AMUN
 */
public class CloudChat extends Application {

    private static Stage primaryStage;
    private static LoginInterface servLoginRef;
    private static ServerMainInterface servFunctionsRef;
    private static Registry reg;

    private static URL errorImage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        errorImage = getClass().getResource("/Images/error-flat.png");
        CloudChat.primaryStage = primaryStage;

        URL path = getClass().getResource("/fxmlpkg/SignIn.fxml");
        Parent root = FXMLLoader.load(path);
        Scene scene = new Scene(root);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static LoginInterface getServLoginRef() {
        return servLoginRef;
    }

    public static ServerMainInterface getServFunctionsRef() {
        return servFunctionsRef;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            reg = LocateRegistry.getRegistry("127.0.0.1", 9999);
            servLoginRef = (LoginInterface) reg.lookup("LoginService");
            servFunctionsRef = (ServerMainInterface) reg.lookup("FunctionService");

        } catch (RemoteException | NotBoundException ex) {
            servLoginRef = null;
        }
        launch(args);
    }

    public static void refresh() {
        try {
            servLoginRef = (LoginInterface) reg.lookup("LoginService");
            servFunctionsRef = (ServerMainInterface) reg.lookup("FunctionService");

        } catch (RemoteException | NotBoundException ex) {
            Platform.runLater(() -> {
                try {
                    Notifications popup = Notifications.create();
                    BufferedImage bufferedImage = ImageIO.read(errorImage);
                    Circle errorCircle = new Circle(50);
                    errorCircle.setFill(new ImagePattern(SwingFXUtils.toFXImage(bufferedImage, null)));
                    popup.graphic(errorCircle);
                    popup.title("Cloud Chat :)");
                    popup.text("Server is down try again later");
                    popup.show();
                    popup.hideAfter(Duration.seconds(15));
                } catch (IOException ex1) {
                    Logger.getLogger(CloudChat.class.getName()).log(Level.SEVERE, null, ex1);
                }

            }
            );
        }
    }
}
