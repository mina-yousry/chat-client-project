package controllers;

import ServerInterfaces.ServerMainInterface;
import cellFactories.ButtonCellFactory;
import cellFactories.FriendRequestsCellFactory;
import cellFactories.FriendsCellFactory;
import cellFactories.SearchResultCellFactory;
import cellFactories.StatusCellFactory;
import chatComponents.Msg;
import chatComponents.Smsg;
import clientmain.CloudChat;
import dtos.UserDto;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import javax.imageio.ImageIO;
import messagepkg.CloudNotification;
import messagepkg.Message;
import org.controlsfx.control.Notifications;

/**
 *
 * @author AMUN
 *
 * This class controls the main interface FXML
 *
 */
public class MainUIController implements Initializable {

    /**
     * main AnchorePane
     */
    @FXML
    private AnchorPane mainPane;
    /**
     * pane to view profile picture
     */
    @FXML
    private AnchorPane profileImage;
    /**
     * pane to view status circle
     */
    @FXML
    private static AnchorPane statusCirclePane;
    /**
     * Label to view user name
     */
    @FXML
    private Label userName;
    /**
     * ComboBox to choose status
     */
    @FXML
    private ComboBox<Color> statusBox;
    /**
     * Friends ListView
     */
    @FXML
    private ListView friendsList;
    /**
     * an AnchorPane
     */
    @FXML
    private AnchorPane rightAnchorPane;
    @FXML
    private ListView friendReqList;
    @FXML
    private TextField searchFriendTextField;
    @FXML
    private ListView searchFriendListView;

    public ListView getSearchFriendListView() {
        return searchFriendListView;
    }

    public ListView getFriendReqList() {
        return friendReqList;
    }

    public ListView getFriendsList() {
        return friendsList;
    }

    /**
     * variables used to drag stages
     */
    private static double xDrag = 0;
    private static double yDrag = 0;
    /**
     * circle representing user status
     */
    private Circle statusCircle;
    /**
     * an AnchorPane to hold chat tabs
     */
    private static AnchorPane chatTabsAnchorPane;
    /**
     * chat TapPane
     */
    private static TabPane chatTabsPane;
    /**
     * user's appearance status
     */
    private String appearenceStatus;

    private byte[] rawImage;
    /**
     * reference to ComboBox cell factory
     */
    private StatusCellFactory stcf;
    /**
     * list of friends received from server
     */
    private ArrayList<UserDto> myFriends;
    /**
     * list of friends Requests received from server
     */
    private ArrayList<UserDto> myFriendRequests;
    /**
     * Observable List of friends to be added to friends list view
     */
    private ObservableList<UserDto> friendsObservableList;
    private static ObservableList<UserDto> friendRequestsObservableList;
    private static ObservableList<UserDto> searchResult;
    private static ArrayList<Integer> groupChatReceivers;

    public static ArrayList<Integer> getGroupChatReceivers() {
        return groupChatReceivers;
    }

    public static ObservableList<UserDto> getSearchResult() {
        return searchResult;
    }

    /**
     * reference of user DTO
     */
    private static UserDto myUserBean;
    /**
     * an HashMap to keep track of opened chat tabs
     */
    private static HashMap<Integer, Tab> chatIDTab;
    private static HashMap<String, Integer> UserNamechatID;
    private static HashMap<Integer, ChatTapController> tabsControllers;
    private static HashMap<Integer, String> chatIdGroupName;
    private static HashMap<Integer, Tab> groupChatIDTab;
    private static HashMap<Integer, ArrayList<Integer>> chatIdGroupChatReceivers;
    private static ServerMainInterface serverRef;
    private Tab foundTab = null;
    private ChatTapController foundTabController = null;

    /**
     * prepared to hold user messages
     */
    private Msg rMsg;
    /**
     * prepared to hold messages sent to user
     */
    private Smsg smsg;
    private Tab chatTab;
    private Tab groupChatTab;
    private boolean controllerFound;
    private UserDto resultUser;
    @FXML
    private Button openGroupChatTap;
    @FXML
    private Label warningGroupLabel;
    @FXML
    private TextField groupChatNameTextField;
    private boolean groupChatTabFound;
    private String groupChatName;

    /**
     * this method is implemented from initializable interface to do some jobs
     * while starting MainUi FXML (specially getting user's data)
     *
     * @param url
     * @param rb
     *
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        controllerFound = false;
        groupChatTabFound = false;
        warningGroupLabel.setVisible(false);

        tabsControllers = new HashMap<>();
        groupChatReceivers = new ArrayList<>();
        chatIdGroupChatReceivers = new HashMap<>();
        chatIdGroupName = new HashMap<>();

        //get user bean
        myUserBean = controllers.SignInController.getMyBean();
        serverRef = CloudChat.getServFunctionsRef();
        //code to move stage by dragging mouse
        mainPane.setOnMousePressed((MouseEvent event) -> {
            xDrag = ((Stage) mainPane.getScene().getWindow()).getX() - event.getScreenX();
            yDrag = ((Stage) mainPane.getScene().getWindow()).getY() - event.getScreenY();
        });

        mainPane.setOnMouseDragged((MouseEvent event) -> {
            clientmain.CloudChat.getPrimaryStage().setX(event.getScreenX() + xDrag);
            clientmain.CloudChat.getPrimaryStage().setY(event.getScreenY() + yDrag);
        });

        chatIDTab = new HashMap<>();
        UserNamechatID = new HashMap<>();

        //initializing **AnchorePane** to hold chat tabs and chat tabs pane
        chatTabsAnchorPane = new AnchorPane();
        chatTabsAnchorPane.setLayoutX(18);
        chatTabsAnchorPane.setLayoutY(20);
        rightAnchorPane.getChildren().add(chatTabsAnchorPane);

        try {
            //getting friends list
            myFriends = serverRef.getFriendList(myUserBean.getId());
            SignInController.getClientRemoteObj().setMyFriendsList(myFriends);
            //getting friend Requests list 
            myFriendRequests = serverRef.getFriendRequestList(myUserBean.getId());
            SignInController.getClientRemoteObj().setMyFriendsRequestList(myFriendRequests);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        showFriends(myFriends);
        showFriendRequest(myFriendRequests);

        userName.setText(myUserBean.getFname());
        appearenceStatus = myUserBean.getAppearanceStatus();

        //initializing combobox according to user appearance status
        statusBox.getItems().addAll(Color.LIMEGREEN, Color.YELLOW, Color.RED, Color.GRAY);
        statusBox.setCellFactory(new Callback<ListView<Color>, ListCell<Color>>() {
            @Override
            public ListCell<Color> call(ListView<Color> param) {
                stcf = new StatusCellFactory();
                return stcf;
            }
        });
        //set combo box button render 
        statusBox.setButtonCell(new ButtonCellFactory());

        if (appearenceStatus.equalsIgnoreCase(UserDto.AVAILABLE)) {
            statusBox.getSelectionModel().select(Color.LIMEGREEN);
        } else if (appearenceStatus.equalsIgnoreCase(UserDto.AWAY)) {
            statusBox.getSelectionModel().select(Color.YELLOW);
        } else if (appearenceStatus.equalsIgnoreCase(UserDto.BUSY)) {
            statusBox.getSelectionModel().select(Color.RED);
        } else {
            statusBox.getSelectionModel().select(Color.GRAY);
        }
        //AnchorePane to hold status circle
        statusCirclePane = new AnchorPane();
        statusCirclePane.setLayoutX(80);
        statusCirclePane.setLayoutY(75);
        statusCircle = new Circle(10);
        statusCircle.setLayoutX(10);
        statusCircle.setLayoutY(15);
        statusCircle.setStroke(Color.BLACK);
        //creating status circle according to user status 
        if (myUserBean.getAppearanceStatus().equalsIgnoreCase("AVAILABLE")) {
            statusCircle.setFill(Color.LIMEGREEN);
        } else if (myUserBean.getAppearanceStatus().equalsIgnoreCase("AWAY")) {;
            statusCircle.setFill(Color.YELLOW);
        } else if (myUserBean.getAppearanceStatus().equalsIgnoreCase("BUSY")) {
            statusCircle.setFill(Color.RED);
        } else {
            statusCircle.setFill(Color.GRAY);
        }
        statusCirclePane.getChildren().clear();
        statusCirclePane.getChildren().addAll(statusCircle);

        //loading user image
        if (myUserBean.getProfilePic() != null) {

            try {
                byte[] userImage = myUserBean.getProfilePic();
                BufferedImage buffImage = javax.imageio.ImageIO.read(new ByteArrayInputStream(userImage));
                Image profileimage = SwingFXUtils.toFXImage(buffImage, null);
                Circle cir2 = new Circle(50);
                cir2.setFill(new ImagePattern(profileimage));
                cir2.setLayoutX(50);
                cir2.setLayoutY(55);
                cir2.setStroke(Color.LIGHTGRAY);
                cir2.setStrokeWidth(2);
                profileImage.getChildren().clear();
                profileImage.getChildren().addAll(cir2, statusCirclePane);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } else {
            try {
                BufferedImage bufferedImage = ImageIO.read(getClass().getResource("/Images/outlook-howto-icons-add.png"));
                Image profileimage = SwingFXUtils.toFXImage(bufferedImage, null);
                Circle cir2 = new Circle(50);
                cir2.setFill(new ImagePattern(profileimage));
                cir2.setLayoutX(50);
                cir2.setLayoutY(55);
                cir2.setStroke(Color.LIGHTGRAY);
                cir2.setStrokeWidth(2);
                statusCircle = new Circle(10);
                if (myUserBean.getAppearanceStatus().equals(UserDto.AVAILABLE)) {
                    statusCircle.setFill(Color.LIMEGREEN);
                } else if (myUserBean.getAppearanceStatus().equals(UserDto.AWAY)) {
                    statusCircle.setFill(Color.YELLOW);
                } else {
                    statusCircle.setFill(Color.RED);
                }
                statusCircle.setLayoutX(10);
                statusCircle.setLayoutY(15);
                statusCircle.setStroke(Color.BLACK);
                if (Platform.isFxApplicationThread()) {
                    statusCirclePane.getChildren().clear();
                    statusCirclePane.getChildren().addAll(statusCircle);
                    profileImage.getChildren().clear();
                    profileImage.getChildren().addAll(cir2, statusCirclePane);
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            statusCirclePane.getChildren().clear();
                            statusCirclePane.getChildren().addAll(statusCircle);
                            profileImage.getChildren().clear();
                            profileImage.getChildren().addAll(cir2, statusCirclePane);
                        }
                    });
                }
            } catch (IOException ex) {
                Logger.getLogger(MainUIController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * a method to allow the user to change his profile picture
     *
     */
    @FXML
    public void changePicture() {

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        BufferedImage bufferedImage = ImageIO.read(file);
                        Image profileimage = SwingFXUtils.toFXImage(bufferedImage, null);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        javax.imageio.ImageIO.write(bufferedImage, "jpg", baos);
                        rawImage = baos.toByteArray();
                        Circle cir2 = new Circle(50);
                        cir2.setFill(new ImagePattern(profileimage));
                        cir2.setLayoutX(50);
                        cir2.setLayoutY(55);
                        cir2.setStroke(Color.LIGHTGRAY);
                        cir2.setStrokeWidth(2);
                        statusCircle = new Circle(10);
                        if (myUserBean.getAppearanceStatus().equals(UserDto.AVAILABLE)) {
                            statusCircle.setFill(Color.LIMEGREEN);
                        } else if (myUserBean.getAppearanceStatus().equals(UserDto.AWAY)) {
                            statusCircle.setFill(Color.YELLOW);
                        } else {
                            statusCircle.setFill(Color.RED);
                        }
                        statusCircle.setLayoutX(10);
                        statusCircle.setLayoutY(15);
                        statusCircle.setStroke(Color.BLACK);
                        if (Platform.isFxApplicationThread()) {
                            statusCirclePane.getChildren().clear();
                            statusCirclePane.getChildren().addAll(statusCircle);
                            profileImage.getChildren().clear();
                            profileImage.getChildren().addAll(cir2, statusCirclePane);
                        } else {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    statusCirclePane.getChildren().clear();
                                    statusCirclePane.getChildren().addAll(statusCircle);
                                    profileImage.getChildren().clear();
                                    profileImage.getChildren().addAll(cir2, statusCirclePane);
                                }
                            });
                        }
                        myUserBean.setProfilePic(rawImage);
                        try {
                            serverRef.updateInfo(myUserBean);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

            });
            th.start();
        }
    }

    /**
     * method to respond to exit button and close scene
     */
    @FXML
    private void close() {
        try {
            CloudChat.getServFunctionsRef().logOut(myUserBean);
            System.exit(0);
        } catch (SQLException ex) {
            Logger.getLogger(MainUIController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(MainUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * method to respond to minimize button and minimize the scene
     */
    @FXML
    private void minimize() {
        clientmain.CloudChat.getPrimaryStage().setIconified(true);
    }

    /**
     * method to get getStatusCirclePane
     *
     * @return statusCirclePane
     */
    public static AnchorPane getStatusCirclePane() {
        return statusCirclePane;
    }

    /**
     * method to get userBean
     *
     * @return userBean
     */
    public static UserDto getMyUserDto() {
        return myUserBean;
    }

    /**
     * method to get ChatTabsAnchorPane
     *
     * public static AnchorPane getChatTab
     *
     * @return chatTabsAnchorPane
     */
    public static AnchorPane getChatTabsAnchorPane() {
        return chatTabsAnchorPane;
    }

    /**
     * method to get getChatTabsPane
     *
     * @return chatTabsPane
     */
    public static TabPane getChatTabsPane() {
        return chatTabsPane;
    }

    /**
     * method to display friends on friends list view
     *
     * @param friends
     *
     */
    public void showFriends(final ArrayList<UserDto> friends) {
        friendsObservableList = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

        if (friends != null) {
            Platform.runLater(() -> {
                friendsObservableList.addAll(friends);
                friendsList.getItems().clear();
                friendsList.getItems().addAll(friendsObservableList);
                friendsList.setCellFactory(new Callback<ListView<UserDto>, ListCell<UserDto>>() {
                    @Override
                    public ListCell<UserDto> call(ListView<UserDto> param) {
                        return new FriendsCellFactory();
                    }
                });
            });

        }
    }

    /**
     * method to display update a friend in list view
     *
     * @param friend
     *
     */
    public void updateMyFriend(UserDto friend) {
        if (friend != null) {
            Platform.runLater(() -> {
                friendsObservableList.remove(friend);
                friendsList.getItems().clear();
                friendsObservableList.add(friend);
                friendsList.getItems().addAll(friendsObservableList);
                friendsList.setCellFactory(new Callback<ListView<UserDto>, ListCell<UserDto>>() {
                    @Override
                    public ListCell<UserDto> call(ListView<UserDto> param) {
                        return new FriendsCellFactory();
                    }
                });
            });

        }
    }

    public static TabPane createChatTapPane() {
        chatTabsPane = new TabPane();
        chatTabsPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
        return chatTabsPane;
    }

    public static HashMap<Integer, Tab> getChatTabID() {
        if (chatIDTab == null) {
            System.out.println("return chatIDTab null");
        }
        return chatIDTab;
    }

    public static HashMap<String, Integer> getUserNamechatID() {
        return UserNamechatID;
    }

    public static ServerMainInterface getServerRef() {
        return serverRef;
    }

    public ArrayList<UserDto> getMyFriends() {
        return myFriends;
    }

    public static HashMap<Integer, ChatTapController> getTabsControllers() {
        return tabsControllers;
    }

    public void showMessage(Message msg) {

        Integer chatId = new Integer(msg.getChatID());
        String senderName = msg.getSenderName();
        String receiverName = null;

        if (chatTabsPane == null) {
            chatTabsPane = createChatTapPane();
            chatTabsPane.setLayoutY(40);
            Platform.runLater(() -> {

                chatTabsAnchorPane.getChildren().add(chatTabsPane);
            });
            System.out.println("Chat Tab Pane Created Successfully");
        }

        if (senderName.equalsIgnoreCase(myUserBean.getFname())) {

            if (!tabsControllers.containsKey(chatId)) {

                try {
                    FXMLLoader fx = null;
                    AnchorPane root = null;
                    fx = new FXMLLoader(getClass().getResource("/fxmlpkg/ChatTap.fxml"));
                    ChatTapController controller = new ChatTapController();
                    fx.setController(controller);
                    root = fx.load();

                    //get the sender name 
                    for (UserDto user : SignInController.client.getMyFriendsList()) {
                        if (user.getId() == msg.getUsersIdList().indexOf(0)) {
                            receiverName = user.getFname();
                            break;
                        }
                    }

                    final String temp = receiverName;

                    //create new tab
                    chatTab = new Tab(receiverName, root);
                    chatTab.setClosable(true);
                    chatTab.setOnCloseRequest(new EventHandler<Event>() {
                        @Override
                        public void handle(Event e) {
                            Platform.runLater(() -> {
                                chatTabsPane.getTabs().remove(chatTab);
                                System.out.println("after closing" + chatTabsPane.getTabs().size());
                            });
                        }
                    });
                    if (Platform.isFxApplicationThread()) {
                        chatTabsPane.getTabs().add(chatTab);
                    } else {
                        Platform.runLater(() -> {
                            chatTabsPane.getTabs().add(chatTab);
                        });
                    }
                    tabsControllers.put(chatId, controller);
                    chatIDTab.put(chatId, chatTab);
                    UserNamechatID.put(senderName, chatId);
                    foundTabController.showRMsg(msg);
                    return;

                } catch (MalformedURLException ex1) {
                    Logger.getLogger(MainUIController.class
                            .getName()).log(Level.SEVERE, null, ex1);

                } catch (IOException ex1) {
                    Logger.getLogger(MainUIController.class
                            .getName()).log(Level.SEVERE, null, ex1);
                }
            }
            for (Entry e : tabsControllers.entrySet()) {
                if (chatId.equals(e.getKey())) {
                    foundTabController = (ChatTapController) e.getValue();
                    foundTabController.showMyMsg(msg);
                    Platform.runLater(() -> {
                        if (!chatTabsPane.getTabs().contains(chatIDTab.get(chatId))) {
                            chatTabsPane.getTabs().add(chatIDTab.get(chatId));

                            System.out.println("tabs array size" + chatTabsPane.getTabs().size());
                        }
                    });
                    break;
                }
            }
            System.out.println("tabsControllers Size : " + tabsControllers.size());
            System.out.println("foundTabController : " + foundTabController);
        } else {
            for (Map.Entry e : tabsControllers.entrySet()) {
                if (chatId.equals(e.getKey())) {
                    foundTabController = (ChatTapController) e.getValue();
                    foundTabController.showRMsg(msg);
                    Platform.runLater(() -> {
                        if (!chatTabsPane.getTabs().contains(chatIDTab.get(chatId))) {
                            chatTabsPane.getTabs().add(chatIDTab.get(chatId));
                            System.out.println("tabs array size" + chatTabsPane.getTabs().size());
                        }
                    });
                    controllerFound = true;
                    return;
                }
            }
            if (controllerFound == false) {
                try {
                    FXMLLoader fx = null;
                    AnchorPane root = null;
                    fx = new FXMLLoader(getClass().getResource("/fxmlpkg/ChatTap.fxml"));
                    ChatTapController controller = new ChatTapController();
                    fx.setController(controller);
                    root = fx.load();
                    chatTab = new Tab(senderName, root);
                    chatTab.setOnCloseRequest(new EventHandler<Event>() {
                        @Override
                        public void handle(Event e) {
                            Platform.runLater(() -> {
                                chatTabsPane.getTabs().remove(chatTab);
                                System.out.println("after closing" + chatTabsPane.getTabs().size());
                            });
                        }
                    });
                    if (Platform.isFxApplicationThread()) {
                        if (!chatTabsPane.getTabs().contains(chatTab)) {
                            chatTabsPane.getTabs().add(chatTab);

                            System.out.println("tabs array size" + chatTabsPane.getTabs().size());
                        }
                    } else {
                        Platform.runLater(() -> {
                            if (!chatTabsPane.getTabs().contains(chatTab)) {
                                chatTabsPane.getTabs().add(chatTab);

                                System.out.println("tabs array size" + chatTabsPane.getTabs().size());
                            }
                        });
                    }

                    tabsControllers.put(chatId, controller);
                    chatIDTab.put(chatId, chatTab);
                    UserNamechatID.put(senderName, chatId);
                    chatIdGroupChatReceivers.put(chatId, groupChatReceivers);
                    foundTabController = controller;
                    foundTabController.showRMsg(msg);

                } catch (MalformedURLException ex) {
                    Logger.getLogger(MainUIController.class
                            .getName()).log(Level.SEVERE, null, ex);

                } catch (IOException ex) {
                    Logger.getLogger(MainUIController.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
            controllerFound = false;
        }
    }

    public void showGroupMessage(Message msg) {

        Integer chatId = new Integer(msg.getChatID());
        String groupName = msg.getGroupChatName();
        String senderName = msg.getSenderName();

        if (chatTabsPane == null) {
            chatTabsPane = createChatTapPane();
            chatTabsPane.setLayoutY(40);
            Platform.runLater(() -> {

                chatTabsAnchorPane.getChildren().add(chatTabsPane);
            });
            System.out.println("Chat Tab Pane Created Successfully");
        }

        if (senderName.equalsIgnoreCase(myUserBean.getFname())) {

            if (!tabsControllers.containsKey(chatId)) {
                try {
                    FXMLLoader fx = null;
                    AnchorPane root = null;
                    fx = new FXMLLoader(getClass().getResource("/fxmlpkg/ChatTap.fxml"));
                    ChatTapController controller = new ChatTapController();
                    fx.setController(controller);
                    root = fx.load();
                    //create new tab
                    chatTab = new Tab(groupName, root);
                    chatTab.setClosable(true);
                    chatTab.setOnCloseRequest(new EventHandler<Event>() {
                        @Override
                        public void handle(Event e) {
                            Platform.runLater(() -> {
                                chatTabsPane.getTabs().remove(chatTab);
                            });
                        }
                    });
                    if (Platform.isFxApplicationThread()) {
                        chatTabsPane.getTabs().add(chatTab);
                    } else {
                        Platform.runLater(() -> {
                            chatTabsPane.getTabs().add(chatTab);
                        });
                    }

                    tabsControllers.put(chatId, controller);
                    chatIDTab.put(chatId, chatTab);
                    chatIdGroupName.put(chatId, groupName);
                    controller.showRMsg(msg);
                    return;

                } catch (MalformedURLException ex1) {
                    Logger.getLogger(MainUIController.class
                            .getName()).log(Level.SEVERE, null, ex1);

                } catch (IOException ex1) {
                    Logger.getLogger(MainUIController.class
                            .getName()).log(Level.SEVERE, null, ex1);
                }
            }
            for (Entry e : tabsControllers.entrySet()) {
                if (chatId.equals(e.getKey())) {
                    foundTabController = (ChatTapController) e.getValue();
                    controllerFound = true;
                    foundTabController.showMyMsg(msg);
                    Platform.runLater(() -> {
                        if (!chatTabsPane.getTabs().contains(chatIDTab.get(chatId))) {
                            chatTabsPane.getTabs().add(chatIDTab.get(chatId));
                        }
                    });
                    break;
                }
            }
        } else {
            for (Map.Entry e : tabsControllers.entrySet()) {
                if (chatId.equals(e.getKey())) {
                    foundTabController = (ChatTapController) e.getValue();
                    controllerFound = true;
                    foundTabController.showRMsg(msg);
                    Platform.runLater(() -> {
                        if (!chatTabsPane.getTabs().contains(chatIDTab.get(chatId))) {
                            chatTabsPane.getTabs().add(chatIDTab.get(chatId));
                            System.out.println("tabs array size" + chatTabsPane.getTabs().size());
                        }
                    });

                    return;
                }
            }
            if (controllerFound == false) {
                try {
                    FXMLLoader fx = null;
                    AnchorPane root = null;
                    fx = new FXMLLoader(getClass().getResource("/fxmlpkg/ChatTap.fxml"));
                    ChatTapController controller = new ChatTapController();
                    fx.setController(controller);
                    root = fx.load();
                    chatTab = new Tab(groupName, root);
                    chatTab.setOnCloseRequest(new EventHandler<Event>() {
                        @Override
                        public void handle(Event e) {
                            Platform.runLater(() -> {
                                chatTabsPane.getTabs().remove(chatTab);
                            });
                        }
                    });
                    if (Platform.isFxApplicationThread()) {
                        if (!chatTabsPane.getTabs().contains(chatTab)) {
                            chatTabsPane.getTabs().add(chatTab);

                            System.out.println("tabs array size" + chatTabsPane.getTabs().size());
                        }
                    } else {
                        Platform.runLater(() -> {
                            if (!chatTabsPane.getTabs().contains(chatTab)) {
                                chatTabsPane.getTabs().add(chatTab);
                            }
                        });
                    }
                    tabsControllers.put(chatId, controller);
                    chatIDTab.put(chatId, chatTab);
                    chatIdGroupName.put(chatId, groupName);
                    foundTabController = controller;
                    groupChatReceivers.addAll(msg.getUsersIdList());
                    ArrayList<Integer> tempGroupChatReceivers = new ArrayList<>(groupChatReceivers);
                    chatIdGroupChatReceivers.put(chatId, tempGroupChatReceivers);
                    groupChatReceivers.clear();
                    foundTabController.showRMsg(msg);

                } catch (MalformedURLException ex) {
                    Logger.getLogger(MainUIController.class
                            .getName()).log(Level.SEVERE, null, ex);

                } catch (IOException ex) {
                    Logger.getLogger(MainUIController.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
            controllerFound = false;
        }
    }

    public void showFriendRequest(final ArrayList<UserDto> friendRequests) {
        if (friendRequests != null && friendRequests.size() > 0) {
            Platform.runLater(() -> {

                friendRequestsObservableList = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
                friendRequestsObservableList.addAll(friendRequests);
                friendReqList.getItems().clear();
                friendReqList.getItems().addAll(friendRequestsObservableList);
                friendReqList.setCellFactory(new Callback<ListView<UserDto>, ListCell<UserDto>>() {
                    @Override
                    public ListCell<UserDto> call(ListView<UserDto> param) {
                        return new FriendRequestsCellFactory();
                    }
                });
            });
        }
    }

    public static ObservableList<UserDto> getFriendRequestsObservableList() {
        return friendRequestsObservableList;
    }

    public ObservableList<UserDto> getFriendsObservableList() {
        return friendsObservableList;
    }

    @FXML
    private void showSearchResult() {

        try {
            resultUser = CloudChat.getServFunctionsRef().searchByMail(searchFriendTextField.getText());

            if (resultUser != null) {
                Platform.runLater(() -> {

                    searchResult = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
                    searchResult.addAll(resultUser);
                    searchFriendListView.getItems().clear();
                    searchFriendListView.getItems().addAll(searchResult);
                    searchFriendListView.setCellFactory(new Callback<ListView<UserDto>, ListCell<UserDto>>() {
                        @Override
                        public ListCell<UserDto> call(ListView<UserDto> param) {
                            return new SearchResultCellFactory();
                        }
                    });
                });
            } else {

                UserDto errorResult = new UserDto();
                errorResult.setFname("error");
                searchResult = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
                searchResult.addAll(errorResult);
                searchFriendListView.getItems().clear();
                searchFriendListView.getItems().addAll(searchResult);
                searchFriendListView.setCellFactory(new Callback<ListView<UserDto>, ListCell<UserDto>>() {
                    @Override
                    public ListCell<UserDto> call(ListView<UserDto> param) {
                        return new SearchResultCellFactory();
                    }
                });

            }

        } catch (SQLException ex) {
            ex.printStackTrace();

        } catch (RemoteException ex) {
            Logger.getLogger(MainUIController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static HashMap<Integer, String> getChatIdGroupName() {
        return chatIdGroupName;
    }

    @FXML
    private void openGroupChatTap() {

        if (groupChatReceivers.size() > 0) {
            if (groupChatNameTextField.getText().trim().equals("")) {
                warningGroupLabel.setVisible(true);
            } else {
                warningGroupLabel.setVisible(false);
                Integer chatId = null;
                groupChatTabFound = false;
                groupChatName = groupChatNameTextField.getText();

                if (chatTabsPane == null) {
                    chatTabsPane = createChatTapPane();
                    chatTabsPane.setLayoutY(40);
                    Platform.runLater(() -> {
                        chatTabsAnchorPane.getChildren().add(chatTabsPane);
                    });
                    System.out.println("Chat Tab Pane Created Successfully");
                }

                for (Entry e : chatIdGroupName.entrySet()) {

                    if (groupChatName.equalsIgnoreCase((String) e.getValue())) {
                        chatId = (Integer) e.getKey();
                        for (Entry en : groupChatIDTab.entrySet()) {
                            if (chatId.equals(en.getKey())) {
                                groupChatTab = (Tab) en.getValue();
                                chatTabsPane.getSelectionModel().select(chatTab);
                                groupChatTabFound = true;
                                Platform.runLater(() -> {
                                    System.out.println("chat tap " + groupChatName);

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

                if (groupChatTabFound == false) {
                    try {
                        System.out.println("User Not found");
                        chatId = CloudChat.getServFunctionsRef().getGroupChatID(groupChatName);
                        FXMLLoader fx = null;
                        AnchorPane root = null;
                        fx = new FXMLLoader(getClass().getResource("/fxmlpkg/ChatTap.fxml"));
                        ChatTapController controller = new ChatTapController();
                        fx.setController(controller);
                        root = fx.load();
                        chatTab = new Tab(groupChatName, root);
                        chatTab.setClosable(true);
                        System.out.println("the new receriver name is " + groupChatName);
                        Platform.runLater(() -> {

                            chatTabsPane.getTabs().add(chatTab);

                        });
                        tabsControllers.put(chatId, controller);
                        chatIDTab.put(chatId, chatTab);
                        chatIdGroupName.put(chatId, groupChatName);
                        ArrayList<Integer> tempGroupChatReceivers = new ArrayList<>(groupChatReceivers);
                        chatIdGroupChatReceivers.put(chatId, tempGroupChatReceivers);
                        for (Integer i : tempGroupChatReceivers) {
                            System.out.println("users when i start group " + i);
                        }
                        groupChatReceivers.clear();
                        final String temp = groupChatName;
                        chatTab.setOnCloseRequest(new EventHandler<Event>() {
                            @Override
                            public void handle(Event e) {
                                Platform.runLater(() -> {
                                    chatTabsPane.getTabs().remove(chatTab);
                                    System.out.println("after closing" + chatTabsPane.getTabs().size());
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
                groupChatTabFound = false;
            }
        }
    }

    public void showNotification(CloudNotification notification) {
        Platform.runLater(() -> {
            Notifications popup = Notifications.create();
            byte[] userImage = notification.getNotificationImage();
            if (userImage != null) {
                BufferedImage buffImage;
                try {
                    buffImage = javax.imageio.ImageIO.read(new ByteArrayInputStream(userImage));
                    Image profileimage = SwingFXUtils.toFXImage(buffImage, null);
                    Circle cir = new Circle(20);
                    cir.setFill(new ImagePattern(profileimage));
                    popup.graphic(cir);
                } catch (IOException ex) {
                    Logger.getLogger(MainUIController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            popup.title("Cloud Chat :)");
            popup.text(notification.getNotificationBody());
            popup.show();
            popup.hideAfter(Duration.seconds(15));

        }
        );
    }

    public static HashMap<Integer, ArrayList<Integer>> getChatIdGroupChatReceivers() {
        return chatIdGroupChatReceivers;
    }

}
