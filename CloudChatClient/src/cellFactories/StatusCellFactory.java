package cellFactories;

import controllers.MainUIController;
import dtos.UserDto;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

/**
 *
 * @author Mina
 * @author Ausama
 *
 * This class is responsible for rendering status ComboBox cells
 */
public class StatusCellFactory extends ListCell<Color> {

    /**
     * override update method which is called each time a cell is rendered
     *
     * @param itemC
     * @param empty
     *
     */
    @Override
    protected void updateItem(Color itemC, boolean empty) {

        super.updateItem(itemC, empty);

        if (itemC == null || empty) {
            setGraphic(null);
        } else {
            HBox hbox = new HBox();
            Text text = new Text();

            if (itemC == Color.LIMEGREEN) {
                text.setText("AVAILABLE");
            } else if (itemC == Color.YELLOW) {
                text.setText("AWAY");
            } else if (itemC == Color.RED) {
                text.setText("BUSY");
            } else if (itemC == Color.GRAY) {
                text.setText("OFFLINE");
            }

            //HBox to add string and color circle
            hbox.spacingProperty().set(10);
            //status color circle
            Circle circle = new Circle(10);
            circle.setFill(itemC);
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(2);

            //adding a listener on selected cell to change user status
            hbox.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {

                    if (Platform.isFxApplicationThread()) {
                        MainUIController.getStatusCirclePane().getChildren().clear();
                        Circle newCircle = new Circle(10);
                        if (itemC == Color.LIMEGREEN) {
                            newCircle.setFill(Color.LIMEGREEN);
                            //setting user status
                            MainUIController.getMyUserDto().setAppearanceStatus(UserDto.AVAILABLE);
                        } else if (itemC == Color.YELLOW) {
                            newCircle.setFill(Color.YELLOW);
                            //setting user status
                            MainUIController.getMyUserDto().setAppearanceStatus(UserDto.AWAY);
                        } else if (itemC == Color.RED) {
                            newCircle.setFill(Color.RED);
                            //setting user status
                            MainUIController.getMyUserDto().setAppearanceStatus(UserDto.BUSY);
                        } else if (itemC == Color.GRAY) {
                            newCircle.setFill(Color.GRAY);
                            //setting user status
                            MainUIController.getMyUserDto().setAppearanceStatus(UserDto.OFFLINE);
                        }
                        newCircle.setLayoutX(10);
                        newCircle.setLayoutY(15);
                        newCircle.setStroke(Color.BLACK);
                        MainUIController.getStatusCirclePane().getChildren().add(newCircle);
                    } else {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                MainUIController.getStatusCirclePane().getChildren().clear();
                                Circle newCircle = new Circle(10);
                                if (itemC == Color.LIMEGREEN) {
                                    newCircle.setFill(Color.LIMEGREEN);
                                    //setting user status
                                    MainUIController.getMyUserDto().setAppearanceStatus(UserDto.AVAILABLE);
                                } else if (itemC == Color.YELLOW) {
                                    newCircle.setFill(Color.YELLOW);
                                    //setting user status
                                    MainUIController.getMyUserDto().setAppearanceStatus(UserDto.AWAY);
                                } else if (itemC == Color.RED) {
                                    newCircle.setFill(Color.RED);
                                    //setting user status
                                    MainUIController.getMyUserDto().setAppearanceStatus(UserDto.BUSY);
                                } else if (itemC == Color.GRAY) {
                                    newCircle.setFill(Color.GRAY);
                                    //setting user status
                                    MainUIController.getMyUserDto().setAppearanceStatus(UserDto.OFFLINE);
                                }
                                newCircle.setLayoutX(10);
                                newCircle.setLayoutY(15);
                                newCircle.setStroke(Color.BLACK);
                                MainUIController.getStatusCirclePane().getChildren().add(newCircle);
                            }
                        });
                    }
                }
            });
            try {
                MainUIController.getServerRef().updateInfo(MainUIController.getMyUserDto());
            } catch (SQLException ex) {
                Logger.getLogger(StatusCellFactory.class.getName()).log(Level.SEVERE, null, ex);
            } catch (RemoteException ex) {
                Logger.getLogger(StatusCellFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
            hbox.getChildren().addAll(circle, text);
            setGraphic(hbox);
        }
    }
}
