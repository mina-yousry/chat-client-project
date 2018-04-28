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
import javafx.collections.ObservableList;
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
 *
 * @author Mina
 * @author Ausama
 *
 * this class is responsible for rendering the cells of the friend requests
 * represented in the friend request ListView
 */
public class FriendRequestsCellFactory extends ListCell<UserDto> {

    /**
     * VBox to carry the full friend request
     */
    private VBox requestVBox;
    /**
     * HBox to carry the text body of the request
     */
    private HBox bodyHBox;
    /**
     * HBox to carry two buttons (accept and decline)
     */
    private HBox buttonsHBox;
    /**
     * friend request sender name
     */
    private String senderName;
    /**
     * friend request text body
     */
    private Text bodyText;
    /**
     * Text node to carry fiend request sender name
     */
    private Text nameText;
    /**
     * Button to accept friend request
     */
    private Button accept;
    /**
     * Button to decline friend request
     */
    private Button decline;
    /**
     * reference to user's the user object
     */
    private UserDto myUser;
    /**
     * reference to the friend request sender object
     */
    private UserDto requestUser;
    /**
     * the circle holding the friend request sender profile pic
     */
    private Circle cir2;
    /**
     * observable list to display requests
     */
    ObservableList<UserDto> friendRequestsObservableList;

    /**
     * this method is called each time a friend request is rendered
     *
     * @param item
     * @param empty
     */
    @Override
    protected void updateItem(UserDto item, boolean empty) {

        super.updateItem(item, empty);

        myUser = SignInController.getMyBean();
        requestUser = item;
        if (item == null || empty) {
            setGraphic(null);
        } else {
            //preparing request body 
            senderName = requestUser.getFname() + " " + requestUser.getLname();
            bodyText = new Text(" wnats to be your friend");
            nameText = new Text(senderName);
            requestVBox = new VBox();
            bodyHBox = new HBox();
            buttonsHBox = new HBox();
            accept = new Button("Accept");
            accept.setStyle("-fx-background-color : #1daf8d ;"
                    + "-fx-text-fill :white;");
            decline = new Button("Decline");
            decline.setStyle("-fx-background-color : #1daf8d ;"
                    + "-fx-text-fill :white;");

            //loadig user Image 
            //case user has a profile pic 
            if (item.getProfilePic() != null) {
                try {
                    byte[] userImage = requestUser.getProfilePic();
                    BufferedImage buffImage = javax.imageio.ImageIO.read(new ByteArrayInputStream(userImage));
                    Image profileimage = SwingFXUtils.toFXImage(buffImage, null);
                    cir2 = new Circle(10);
                    cir2.setFill(new ImagePattern(profileimage));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                //loading template image if user doesn't have a prifile pic
            } else {
                try {
                    byte[] userImage = requestUser.getProfilePic();
                    BufferedImage buffImage = ImageIO.read(getClass().getResource("/Images/outlook-howto-icons-add.png"));
                    Image profileimage = SwingFXUtils.toFXImage(buffImage, null);
                    cir2 = new Circle(10);
                    cir2.setFill(new ImagePattern(profileimage));
                } catch (IOException ex) {
                    Logger.getLogger(FriendRequestsCellFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (Platform.isFxApplicationThread()) {
                bodyHBox.getChildren().add(cir2);
                bodyHBox.getChildren().addAll(nameText, bodyText);
                bodyHBox.setSpacing(10);
                buttonsHBox.getChildren().addAll(accept, decline);
                buttonsHBox.setSpacing(10);
                requestVBox.getChildren().addAll(bodyHBox, buttonsHBox);
                requestVBox.setStyle("-fx-padding: 10 10 10 10");
            } else {
                Platform.runLater(() -> {
                    bodyHBox.getChildren().add(cir2);
                    bodyHBox.getChildren().addAll(nameText, bodyText);
                    bodyHBox.setSpacing(10);
                    buttonsHBox.getChildren().addAll(accept, decline);
                    buttonsHBox.setSpacing(10);
                    requestVBox.getChildren().addAll(bodyHBox, buttonsHBox);
                    requestVBox.setStyle("-fx-padding: 10 10 10 10");
                });
            }

            //accept buton listener 
            accept.setOnAction((ev) -> {
                String tempName = null;
                VBox tempGrandParent = (VBox) accept.getParent().getParent();
                HBox tempParent = (HBox) tempGrandParent.getChildren().get(0);
                if (item.getProfilePic() != null) {
                    tempName = ((Text) tempParent.getChildren().get(1)).getText();
                } else {
                    tempName = ((Text) tempParent.getChildren().get(0)).getText();
                }
                friendRequestsObservableList = SignInController.getMainController().getFriendRequestsObservableList();
                for (UserDto u : friendRequestsObservableList) {
                    if (tempName.equalsIgnoreCase(u.getFname() + " " + u.getLname())) {
                        UserDto tempRequestUser = u;
                        try {
                            CloudChat.getServFunctionsRef().addFriend(myUser.getId(), tempRequestUser.getId());
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                        Platform.runLater(() -> {
                            SignInController.getMainController().getFriendRequestsObservableList().remove(tempRequestUser);
                            SignInController.getClientRemoteObj().getMyFriendsRequestList().remove(tempRequestUser);
                            SignInController.getMainController().getFriendReqList().getItems().clear();
                            SignInController.getMainController().showFriendRequest(SignInController.getClientRemoteObj().getMyFriendsRequestList());

                        });
                        break;
                    }
                }
            });

            //decline button listener
            decline.setOnAction((ev) -> {
                String tempName = null;
                VBox tempGrandParent = (VBox) accept.getParent().getParent();
                HBox tempParent = (HBox) tempGrandParent.getChildren().get(0);
                if (item.getProfilePic() != null) {
                    tempName = ((Text) tempParent.getChildren().get(1)).getText();
                } else {
                    tempName = ((Text) tempParent.getChildren().get(0)).getText();
                }
                friendRequestsObservableList = SignInController.getMainController().getFriendRequestsObservableList();
                for (UserDto u : friendRequestsObservableList) {
                    if (tempName.equalsIgnoreCase(u.getFname() + " " + u.getLname())) {
                        UserDto tempRequestUser = u;
                        try {
                            CloudChat.getServFunctionsRef().removeFriendRequest(myUser.getId(), tempRequestUser.getId());
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                        Platform.runLater(() -> {
                            SignInController.getMainController().getFriendRequestsObservableList().remove(tempRequestUser);
                            SignInController.getClientRemoteObj().getMyFriendsRequestList().remove(tempRequestUser);
                            SignInController.getMainController().getFriendReqList().getItems().clear();
                            SignInController.getMainController().showFriendRequest(SignInController.getClientRemoteObj().getMyFriendsRequestList());

                        });
                        break;
                    }
                }
            });

            setGraphic(requestVBox);
        }
    }
}
