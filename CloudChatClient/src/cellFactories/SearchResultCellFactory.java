package cellFactories;

import clientmain.CloudChat;
import controllers.SignInController;
import dtos.UserDto;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javax.imageio.ImageIO;

/**
 * class used to render search result ListView cells
 *
 * @author Mina
 */
public class SearchResultCellFactory extends ListCell<UserDto> {

    /**
     * VBox containing search result in ListView
     */
    private VBox resultVBox;
    /**
     * HBox to hold result body
     */
    private HBox bodyHBox;
    /**
     * HBox to hold buttons
     */
    private HBox buttonHBox;
    /**
     * the name of the found friend
     */
    private String resultUserName;
    /**
     * text node to hold the name on the HBox
     */
    private Text nameText;
    /**
     * button to send friend request
     */
    private Button sendFriendRequestButton;
    /**
     * reference of type user to carry user data
     */
    private UserDto myUser;
    /**
     * reference of type user to carry user data
     */
    private UserDto resultUser;
    /**
     *circle to carry result profile picture
     */
    private Circle profilePicCircle;
    /**
     * image to show if the user doesn't exist
     */
    private Image errorImage;
    /**
     * circle to hold error image 
     */
    private Circle errorCircle;

    /**
     * called each time search result item is rendered
     *
     * @param item
     * @param empty
     */
    @Override
    protected void updateItem(UserDto item, boolean empty) {

        super.updateItem(item, empty);

        myUser = SignInController.getMyBean();
        resultUser = item;
        
        if (item == null || empty) {
            setGraphic(null);
        } else {
            //case of user doesn't exist 
            if (resultUser.getFname().equals("error")) {
                nameText = new Text("Sorry this User is not found");
                resultVBox = new VBox();
                resultVBox.setSpacing(10);
                bodyHBox = new HBox();

                try {
                    BufferedImage bufferedImage = ImageIO.read(getClass().getResource("/Images/error-flat.png"));
                    errorImage = SwingFXUtils.toFXImage(bufferedImage, null);
                    errorCircle = new Circle(50);
                    errorCircle.setFill(new ImagePattern(errorImage));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (Platform.isFxApplicationThread()) {
                    bodyHBox.getChildren().addAll(errorCircle, nameText);
                    resultVBox.getChildren().addAll(bodyHBox);
                } else {
                    Platform.runLater(() -> {
                        bodyHBox.getChildren().addAll(errorCircle, nameText);
                        bodyHBox.setSpacing(10);
                        resultVBox.getChildren().addAll(bodyHBox);
                    });
                }
                setGraphic(resultVBox);
                //case user exist
            } else {
                resultUserName = resultUser.getFname() + " " + resultUser.getLname();
                nameText = new Text(resultUserName);
                resultVBox = new VBox();
                bodyHBox = new HBox();
                buttonHBox = new HBox();
                sendFriendRequestButton = new Button("Send Friend Request");
                sendFriendRequestButton.setStyle("-fx-background-color : #1daf8d ;"
                        + "-fx-text-fill :white;");

                //loading user profile pic 
                if (item.getProfilePic() != null) {
                    try {
                        byte[] userImage = resultUser.getProfilePic();
                        BufferedImage buffImage = javax.imageio.ImageIO.read(new ByteArrayInputStream(userImage));
                        Image profileimage = SwingFXUtils.toFXImage(buffImage, null);
                        profilePicCircle = new Circle(20);
                        profilePicCircle.setFill(new ImagePattern(profileimage));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                if (Platform.isFxApplicationThread()) {
                    if (item.getProfilePic() != null) {
                        bodyHBox.getChildren().add(profilePicCircle);
                    }
                    bodyHBox.getChildren().addAll(nameText);
                    buttonHBox.getChildren().addAll(sendFriendRequestButton);
                    resultVBox.getChildren().addAll(bodyHBox, buttonHBox);
                    resultVBox.setStyle("-fx-padding : 10 10 10 10;");
                    bodyHBox.setSpacing(10);
                    buttonHBox.setSpacing(10);
                } else {
                    Platform.runLater(() -> {
                        if (item.getProfilePic() != null) {
                            bodyHBox.getChildren().add(profilePicCircle);
                        }
                        bodyHBox.getChildren().addAll(nameText);
                        buttonHBox.getChildren().addAll(buttonHBox);
                        resultVBox.getChildren().addAll(bodyHBox);
                        resultVBox.setStyle("-fx-padding : 10 10 10 10;");
                        bodyHBox.setSpacing(10);
                        buttonHBox.setSpacing(10);
                    });
                }

                //action in send friend request button
                sendFriendRequestButton.setOnAction((ev) -> {
                    try {
                        CloudChat.getServFunctionsRef().sendFriendRequest(myUser.getId(), resultUser.getId());
                    } catch (RemoteException ex) {
                        Logger.getLogger(SearchResultCellFactory.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    SignInController.getMainController().getSearchResult().remove(resultUser);
                    SignInController.getMainController().getSearchFriendListView().getItems().clear();
                });
                setGraphic(resultVBox);
            }
        }
    }

}
