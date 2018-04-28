package servermainui;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import controllers.ServerUIController;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import servermainlogic.ServerMain;

/**
 *
 * @author Java Champion
 */
public class Server extends Application {

    private String path = "/fxmlpkg/ServerUI.fxml";
    private String pathCSS = "/CSS/tabPane.css";
    private static Stage myStage;
    private static ServerMain server;

    public static ServerMain getServer() {
        return server;
    }

    public static Stage getMyStage() {
        return myStage;
    }

    /**
     *
     *
     * Javafx Start Method
     *
     * method to get Start Server
     */
    @Override
    public void start(Stage stage) {
        ServerUIController controller = null;
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader();
            controller = new ServerUIController();
            loader.setController(controller);
            root = loader.load(getClass().getResource(path));
            myStage = stage;
            Scene scene = new Scene(root);
            controller.setS(scene);
            PieChart PC = controller.getPc();
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(scene);
            scene.getStylesheets().add(getClass().getResource(pathCSS).toExternalForm());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        stage.show();
    }

    /**
     *
     *
     * public static MAIN
     *
     * @param array of string
     */
    public static void main(String[] args) {
        server=new ServerMain();
        launch(args);
    }

}
