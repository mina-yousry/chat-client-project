package cellFactories;

import clientmain.CloudChat;
import controllers.ChatTapController;
import controllers.MainUIController;
import static controllers.MainUIController.createChatTapPane;
import controllers.SignInController;
import dtos.UserDto;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javax.imageio.ImageIO;

/**
 *
 * @author Mina
 *
 * This class is responsible for rendering friends ListView Cells
 *
 */
public class FriendsCellFactory extends ListCell<UserDto> {

    /**
     * circle to show user status
     */
    private Circle statusCircle;
    /**
     * circle to show user profile pic
     */
    private Circle cirProfilePic;
    /**
     * parent root to load the FXML file in
     */
    private Parent root = null;
    /**
     * an HBox used in rendering friends ListView cell
     */
    private HBox friendHbox;
    /**
     * VBox to display messages
     */
    private static VBox chatmsgVbox;
    /**
     * an ID to distinguish between opened chat tabs
     */
    private static Integer chatID;
    /**
     * Tab pane that holds chat tabs
     */
    private TabPane chatTabsPane;
    /**
     * a tab that hold nodes related to single or group chat
     */
    private Tab chatTab;
    /**
     * reference to the friend Dto Object
     */
    private UserDto myFriend;
    /**
     * Array list of user's friends
     */
    private ArrayList<UserDto> renderedFriends;
    /**
     * hash map that stores chat tabs and its controllers
     */
    private static HashMap<Integer, ChatTapController> tabsControllersMap;
    /**
     * flag to decide if a certain chat tab found or not
     */
    private boolean chatTabFound = false;
    /**
     * a check box to add friends to a group chat
     */
    private CheckBox groupChatCheckBox;
    /**
     * hidden Text node to carry friend ID
     */
    private Text hiddenID;

    /**
     * override update method which is called each time a cell is rendered
     *
     * @param item
     * @param empty
     *
     */
    @Override
    protected void updateItem(UserDto item, boolean empty) {

        tabsControllersMap = MainUIController.getTabsControllers();

        myFriend = item;
        renderedFriends = new ArrayList<>();
        renderedFriends.add(myFriend);

        super.updateItem(item, empty);

        if (item == null || empty) {
            setGraphic(null);
        } else {

            friendHbox = new HBox();
            groupChatCheckBox = new CheckBox();
            hiddenID = new Text(Integer.toString(item.getId()));
            hiddenID.setVisible(false);
            AnchorPane friendImagePane = new AnchorPane();

            //loading user image if exist  
            Thread loadImageThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    //case user doesn't have a profile pic
                    if (item.getProfilePic() == null) {
                        try {
                            BufferedImage bufferedImage = ImageIO.read(getClass().getResource("/Images/outlook-howto-icons-add.png"));
                            Image profileimage = SwingFXUtils.toFXImage(bufferedImage, null);
                            cirProfilePic = new Circle(15);
                            cirProfilePic.setFill(new ImagePattern(profileimage));
                            cirProfilePic.setLayoutX(10);
                            cirProfilePic.setLayoutY(15);
                            cirProfilePic.setStroke(Color.DARKGRAY);

                        } catch (IOException ex) {
                            Logger.getLogger(FriendsCellFactory.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        //case user has a profile pic 
                    } else {
                        byte[] userImage = item.getProfilePic();
                        BufferedImage buffImage = null;
                        try {
                            buffImage = javax.imageio.ImageIO.read(new ByteArrayInputStream(userImage));
                        } catch (IOException ex) {
                            Logger.getLogger(FriendsCellFactory.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        Image profileimage = SwingFXUtils.toFXImage(buffImage, null);
                        cirProfilePic = new Circle(15);
                        cirProfilePic.setFill(new ImagePattern(profileimage));
                        cirProfilePic.setLayoutX(10);
                        cirProfilePic.setLayoutY(15);
                        cirProfilePic.setStroke(Color.DARKGRAY);
                    }
                    Platform.runLater(() -> {
                        friendImagePane.getChildren().addAll(cirProfilePic);
                    });

                }
            });
            loadImageThread.start();

            //deciding status circle color according to user status 
            statusCircle = new Circle(7);
            statusCircle.setStroke(Color.BLACK);
            statusCircle.setStyle("-fx-margin :5 5 5 5;");
            if (item.getAppearanceStatus().equalsIgnoreCase("AVAILABLE")) {
                statusCircle.setFill(Color.LIMEGREEN);
            } else if (item.getAppearanceStatus().equalsIgnoreCase("AWAY")) {
                statusCircle.setFill(Color.YELLOW);
            } else if (item.getAppearanceStatus().equalsIgnoreCase("BUSY")) {
                statusCircle.setFill(Color.RED);
            }

            if (item.getAppearanceStatus().equalsIgnoreCase(UserDto.OFFLINE) || item.getConnStatus().equalsIgnoreCase(UserDto.OFFLINE)) {
                statusCircle.setFill(Color.GRAY);
            }

            friendImagePane.getChildren().addAll(statusCircle);
            Text friendNameText = new Text(item.getFname() + " " + item.getLname());

            //listener on start chat button to open a tab to start chat 
            friendHbox.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    Text receiverNameNode = (Text) friendHbox.getChildren().get(2);
                    String receiverName = receiverNameNode.getText();
                    openChatTap(receiverName);
                }
            });

            //check box listener to add friends to group
            groupChatCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                    HBox tempParent = (HBox) groupChatCheckBox.getParent();
                    Text tempIDTExt = (Text) tempParent.getChildren().get(4);
                    Integer tempID = Integer.parseInt(tempIDTExt.getText());
                    if (newValue == true) {
                        SignInController.getMainController().getGroupChatReceivers().add(tempID);
                    } else if (newValue == false) {
                        SignInController.getMainController().getGroupChatReceivers().remove(tempID);
                    }
                    for (Integer I : SignInController.getMainController().getGroupChatReceivers()) {
                        System.out.println("users id when iam choosing them" + I);
                    }
                }
            });

            friendHbox.getChildren().addAll(friendImagePane, statusCircle, friendNameText, groupChatCheckBox, hiddenID);
            friendHbox.setSpacing(10);
            friendHbox.setStyle("-fx-padding: 5 5 5 5;");

            setGraphic(friendHbox);
        }
    }

    /**
     * open a new chat tab when clicking on the friend HBox or selects the tab
     * if it's already opened
     *
     * @param receiverName
     */
    public void openChatTap(String receiverName) {

        chatTabsPane = MainUIController.getChatTabsPane();
        Integer chatId = null;
        chatTabFound = false;
        //creating tab pane if this is the first tab to open
        if (chatTabsPane == null) {
            chatTabsPane = createChatTapPane();
            chatTabsPane.setLayoutY(40);
            Platform.runLater(() -> {
                SignInController.getMainController().getChatTabsAnchorPane().getChildren().add(chatTabsPane);
            });
        }

        if (receiverName.indexOf(" ") > 0) {
            receiverName = receiverName.substring(0, receiverName.indexOf(" "));
        }

        //searching the tab in opened chat tabs HashMap
        for (Entry e : SignInController.getMainController().getUserNamechatID().entrySet()) {
            System.out.println(e.getValue());
            if (((String) e.getKey()).equalsIgnoreCase(receiverName)) {
                chatId = (Integer) e.getValue();
                for (Map.Entry en : SignInController.getMainController().getChatTabID().entrySet()) {
                    if (chatId.equals(en.getKey())) {
                        chatTab = (Tab) en.getValue();
                        SignInController.getMainController().getChatTabsPane().getSelectionModel().select(chatTab);
                        chatTabFound = true;
                        Platform.runLater(() -> {
                            System.out.println("chat tap " + chatTab);
                            if (!chatTabsPane.getTabs().contains(chatTab)) {
                                chatTabsPane.getTabs().add(chatTab);
                            }
                            chatTabsPane.getSelectionModel().select(chatTab);
                            System.out.println("tabs array size" + chatTabsPane.getTabs().size());

                        });
                        return;
                    }
                }
                break;
            }
        }

        //in case tab not found open a new chat tab 
        if (chatTabFound == false) {
            try {
                System.out.println("User Not found");
                chatId = CloudChat.getServFunctionsRef().getChatID(MainUIController.getMyUserDto().getId(), myFriend.getId());
                FXMLLoader fx = null;
                AnchorPane root = null;
                fx = new FXMLLoader(getClass().getResource("/fxmlpkg/ChatTap.fxml"));
                ChatTapController controller = new ChatTapController();
                fx.setController(controller);
                root = fx.load();
                chatTab = new Tab(receiverName, root);
                chatTab.setClosable(true);

                Platform.runLater(() -> {
                    chatTabsPane.getTabs().add(chatTab);
                });

                SignInController.getMainController().getTabsControllers().put(chatId, controller);
                SignInController.getMainController().getChatTabID().put(chatId, chatTab);
                SignInController.getMainController().getUserNamechatID().put(receiverName, chatId);

                chatTab.setOnCloseRequest(new EventHandler<Event>() {
                    @Override
                    public void handle(Event e) {
                        Platform.runLater(() -> {
                            chatTabsPane.getTabs().remove(chatTab);
                        });
                    }
                });

            } catch (MalformedURLException ex1) {
                Logger.getLogger(MainUIController.class
                        .getName()).log(Level.SEVERE, null, ex1);

            } catch (IOException ex1) {
                Logger.getLogger(MainUIController.class
                        .getName()).log(Level.SEVERE, null, ex1);
            }
        }
        chatTabFound = false;
    }

}
