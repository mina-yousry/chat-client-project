/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import clientInterface.ClientInterface;
import dbconnfactory.ConnectionFactory;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.imageio.ImageIO;
import messagepkg.CloudNotification;
import servermainlogic.ServerMain;
import servermainui.Server;

/**
 *
 * @author Java Champion
 */
public class ServerUIController implements Initializable {

    private double xOffset, yOffset;

    /**
     * Close Image
     */
    @FXML
    private ImageView close;
    /**
     * minimize Image
     */
    @FXML
    private ImageView minimize;
    /**
     * female/male pie chart
     */
    @FXML
    private PieChart pc;
    /**
     * chat address
     */
    @FXML
    private Label label;
    /**
     * top pane
     */
    @FXML
    private AnchorPane anchor;
    /**
     * main pane
     */
    @FXML
    private AnchorPane main;
    /**
     * online/offline pie chart
     */
    @FXML
    private PieChart pc1;
    /**
     * start/stop server label
     */
    @FXML
    private Label onoff;
    /**
     * table columns
     */
    @FXML
    private ObservableList<ObservableList> data;
    /**
     * table view
     */
    @FXML
    private TableView<ObservableList> table;
    /**
     * notification field
     */
    @FXML
    private TextArea textarea;

    /**
     * notification image
     */
    @FXML
    ImageView imageView;
    @FXML
    Tab mainTab;
    @FXML
    Tab statisticsTab;
    @FXML
    Tab userDataTab;
    private static Stage primaryStage;
    boolean working = true;
    Scene s;
    private byte[] rawImage;
    boolean firstTable = true;
    ServerMain server;

    /**
     * method to set the scene of the controller
     *
     * @param Scene
     */
    public void setS(Scene s) {
        this.s = s;
    }

    /**
     * method to get the statistics the controller
     *
     * @return pie chart
     */
    public PieChart getPc() {
        return pc;
    }

    /**
     * initialize method of the controller
     *
     * @param URL , ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        server = Server.getServer();
        mainTab.getStyleClass().add("tab");
        anchor.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                if (me.getButton() != MouseButton.MIDDLE) {
                    anchor.getScene().getWindow().setX(me.getScreenX() + xOffset);
                    anchor.getScene().getWindow().setY(me.getScreenY() + yOffset);
                }
            }
        });
        anchor.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = anchor.getScene().getWindow().getX() - event.getScreenX();
                yOffset = anchor.getScene().getWindow().getY() - event.getScreenY();
            }
        });

        primaryStage = servermainui.Server.getMyStage();
    }

    /**
     * method to show pie charts with statistics
     *
     * @param ActionEvent
     */
    @FXML
    public void pie(ActionEvent event) {
        Platform.runLater(() -> {
            int m, f, on, off;
            on = off = m = f = 0;

            try (Connection c = ConnectionFactory.getConnection();) {
                String SQL = "SELECT * from user where gender = 'M' ";
                String sql = "SELECT * from user where gender = 'F' ";
                String SQL1 = "SELECT * from user where connecting_status = 'ONLINE' ";
                String sql1 = "SELECT * from user where connecting_status = 'OFFLINE' ";
                ResultSet rs = c.createStatement().executeQuery(SQL);
                ResultSet rs1 = c.createStatement().executeQuery(sql);
                ResultSet rs2 = c.createStatement().executeQuery(SQL1);
                ResultSet rs3 = c.createStatement().executeQuery(sql1);
                while (rs.next()) {
                    m++;
                }
                while (rs1.next()) {
                    f++;
                }
                while (rs2.next()) {
                    on++;
                }
                while (rs3.next()) {
                    off++;
                }
                int tm = (m * 100) / (m + f);
                int tf = 100 - tm;
                int ton = (on * 100) / (on + off);
                int toff = 100 - ton;
                ObservableList<PieChart.Data> MFDATA
                        = FXCollections.observableArrayList(
                                new PieChart.Data("MALE", tm),
                                new PieChart.Data("FEMALE", tf)
                        );
                pc.setData(MFDATA);

                ObservableList<PieChart.Data> ONOFF
                        = FXCollections.observableArrayList(
                                new PieChart.Data("OFFLINE", toff),
                                new PieChart.Data("ONLINE", ton)
                        );
                pc1.setData(ONOFF);
            } catch (Exception e) {
                Platform.runLater(() -> {

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText("Cloud Chat ");
                    alert.setContentText("Cannot access database at this moment");
                    alert.showAndWait();
                });
            }

        });
    }

    /**
     * method to close the server window
     *
     */
    @FXML
    public void closeWindow() {
        String msg = "Server Closed";
        CloudNotification notifi = new CloudNotification();
        notifi.setNotificationBody(msg);

        try {
            if (server.getOnlineUsersRefsMap().size() > 0) {
                for (ClientInterface client : server.getOnlineUsersRefsMap().values()) {
                    client.receiveNotification(notifi);
                }
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        if (working == true) {
            server.stop();
            working = false;
            Platform.runLater(() -> {
                onoff.setText("Server Closed");
            });
        }
        System.exit(0);
    }

    /**
     * method to minimize the server window
     *
     */
    @FXML
    public void minimizeWindow() {
        Platform.runLater(() -> {
            servermainui.Server.getMyStage().setIconified(true);
        });
    }

    /**
     * method to fill the table view by the database table
     *
     */
    public void fillTabel() {

        for (int i = 0; i < table.getItems().size(); i++) {
            table.getItems().clear();
            table.getColumns().clear();
        }

        String SQL = "SELECT * from user";
        data = FXCollections.observableArrayList();
        try (Connection conn = ConnectionFactory.getConnection();) {

            ResultSet rs = conn.createStatement().executeQuery(SQL);

            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                if (i == 8) {
                    continue;
                }
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });

                table.getColumns().addAll(col);
            }

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.add(rs.getString(i));
                }
                data.add(row);
            }
            firstTable = false;
            table.setItems(data);
        } catch (Exception e) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("Cloud Chat ");
                alert.setContentText("Cannot access database at this moment");
                alert.showAndWait();
            });
        }

    }

    @FXML
    public void refreshTable() {
        for (int i = 0; i < table.getItems().size(); i++) {
            table.getItems().clear();
            table.getColumns().clear();
        }
        fillTabel();
    }

    /*
    *Function to choose image and send it in a notification
    *
     */
    @FXML
    public void sendImage() {

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                imageView.setImage(image);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                javax.imageio.ImageIO.write(bufferedImage, "jpg", baos);
                rawImage = baos.toByteArray();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }

    /**
     * method to send notification to all Users
     *
     */
    public void notifyAllUsers() {

        if (!textarea.getText().trim().equals("")) {
            String msg = textarea.getText();
            imageView.setImage(null);
            textarea.setText("");
            CloudNotification adminMessage = new CloudNotification();
            adminMessage.setNotificationBody(msg);
            adminMessage.setNotificationImage(rawImage);
            System.out.println("messege sent");
            for (ClientInterface onlineClient : ServerMain.getOnlineUsersRefsMap().values()) {
                try {
                    onlineClient.receiveNotification(adminMessage);
                } catch (RemoteException ex) {
                    Logger.getLogger(ServerUIController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            rawImage = null;

        }

    }

    /**
     * method to close the server functionality
     *
     */
    @FXML
    public void stopServer() {
        if (working == true) {
            server.stop();
            working = false;
            Platform.runLater(() -> {
                onoff.setText("Server Closed");
            });
            String msg = "Server Stopped";
            CloudNotification notifi = new CloudNotification();
            notifi.setNotificationBody(msg);

            try {
                for (ClientInterface client : server.getOnlineUsersRefsMap().values()) {
                    client.receiveNotification(notifi);
                }
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }

    }

    /**
     * method to Resume the server functionality
     *
     */
    @FXML
    public void resumeServer() {
        if (working == false) {
            if (server == null) {
                server = new ServerMain();

            } else {
                server.resume();
                String msg = "Server Started";
                CloudNotification notifi = new CloudNotification();
                notifi.setNotificationBody(msg);

                try {
                    for (ClientInterface client : server.getOnlineUsersRefsMap().values()) {
                        client.receiveNotification(notifi);
                    }
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
            working = true;
            Platform.runLater(() -> {
                onoff.setText("Server Started");
            });
        }

    }

}
