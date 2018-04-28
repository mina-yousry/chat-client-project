package controllers;

import cellFactories.MsgColorCellFactory;
import cellFactories.StatusCellFactory;
import chatComponents.Msg;
import chatComponents.RMsgFirst;
import chatComponents.RMsgLast;
import chatComponents.RMsgMiddle;
import chatComponents.Smsg;
import chatComponents.SmsgFirst;
import chatComponents.SmsgLast;
import chatComponents.SmsgLastNoPic;
import chatComponents.SmsgMiddle;
import chatComponents.SmsgNoPic;
import clientmain.CloudChat;
import dtos.UserDto;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import messagepkg.Message;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;

/**
 * FXML Controller class
 *
 * @author Mina
 */
public class ChatTapController implements Initializable {

    @FXML
    private TextArea sendTextArea;
    @FXML
    private VBox chatVBox;
    @FXML
    private ComboBox<Color> messageColor;

    private UserDto receiverUser;
    private UserDto myUser;
    private String msgColor;
    @FXML
    private VBox chatCurrentVBox;
    @FXML
    private ScrollPane chatScrollPane;
    /**
     * an Id to distinguish opened chat tabs
     */
    private static Integer chatId;
    /**
     * an HashMap to keep track of opened chat tabs
     */
    private HashMap<Integer, Tab> chatTabID;
    /**
     * flag to check if chat tab found in opened tabs
     */
    private boolean tabFound = false;
    /**
     * 2 vectors to store chat messages
     */

    /**
     * prepared to hold user messages
     */
    private Msg rMsg;
    /**
     * prepared to hold messages sent to user
     */
    private Smsg smsg;

    /**
     * Initializes the controller class.
     */
    private Integer msgID;
    private Tab foundTab;
    private Vector<Message> mymsgs = new Vector<>();
    private Vector<Message> receivedMsgs = new Vector<>();

    //////* xslt*///////
    private Document doc;
    private Element rootElement;
    private String path;
    private Element chatWith;
    private DocumentBuilderFactory docBF;
    private DocumentBuilder docBuild;
    private File file;
    private Element message;
    private Element messageBody;
    private Element direction;
    private Element from;
    private Element color;
    private boolean fTime = true;
    ///////* end*/////
    /**
     * index to add messages in VBox
     */
    private int VboxIndex = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        myUser = controllers.SignInController.getMyBean();

        sendTextArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getSource() == KeyCode.ENTER) {
                    sendMessage();
                }
            }
        });

        sendTextArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (messageColor.getSelectionModel().getSelectedItem().equals(Color.BLACK)) {
                    sendTextArea.setStyle("-fx-text-fill: black;");
                } else if (messageColor.getSelectionModel().getSelectedItem() == Color.YELLOW) {
                    sendTextArea.setStyle("-fx-text-fill: yellow;");
                } else if (messageColor.getSelectionModel().getSelectedItem() == Color.RED) {
                    sendTextArea.setStyle("-fx-text-fill: red;");
                } else {
                    sendTextArea.setStyle("-fx-text-fill: green;");
                }
            }
        });

        messageColor.getItems().addAll(Color.BLACK, Color.YELLOW, Color.RED, Color.GREEN);
        messageColor.setCellFactory(new Callback<ListView<Color>, ListCell<Color>>() {
            @Override
            public ListCell<Color> call(ListView<Color> param) {
                return new MsgColorCellFactory();
            }
        });
        sendTextArea.setWrapText(true);
        messageColor.setButtonCell(new StatusCellFactory());
        messageColor.getSelectionModel().select(Color.BLACK);

        messageColor.setButtonCell(new MsgColorCellFactory());

        //////////////////* xslt*//////////////
        try {
            docBF = DocumentBuilderFactory.newInstance();
            docBuild = docBF.newDocumentBuilder();
            doc = docBuild.newDocument();
            rootElement = doc.createElement("chat");
            chatWith = doc.createElement("chatWith");
            doc.appendChild(rootElement);
            doc.getFirstChild().appendChild(chatWith);

        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        }
        ///////////* xslt*///////////////////////
        chatScrollPane.vvalueProperty().bind(chatCurrentVBox.heightProperty());
    }

    @FXML
    private void sendMessage() {
        if (!sendTextArea.getText().trim().equalsIgnoreCase("")) {
            List<Integer> receiversIDS = Collections.synchronizedList(new ArrayList<>());
            HashMap<String, Integer> chatTabChatID = SignInController.getMainController().getUserNamechatID();
            HashMap<Integer, String> groupChatTabChatID = SignInController.getMainController().getChatIdGroupName();
            ArrayList<UserDto> myFirends;
            myFirends = SignInController.getClientRemoteObj().getMyFriendsList();

            Message sendingMsg = new Message();

            if (messageColor.getSelectionModel().getSelectedItem().equals(Color.BLACK)) {
                msgColor = new String("BLACK");
            } else if (messageColor.getSelectionModel().getSelectedItem() == Color.YELLOW) {
                msgColor = new String("YELLOW");
            } else if (messageColor.getSelectionModel().getSelectedItem() == Color.RED) {
                msgColor = new String("RED");
            } else {
                msgColor = new String("GREEN");
            }

            TabPane tabsPane = SignInController.getMainController().getChatTabsPane();
            Tab myTab = tabsPane.getSelectionModel().getSelectedItem();
            Integer ID = null;
            String tabText = myTab.getText();

            for (UserDto u : myFirends) {
                if (tabText.indexOf(" ") > 0) {
                    if (u.getFname().equalsIgnoreCase(tabText.substring(0, tabText.indexOf(" ")))) {
                        receiverUser = u;
                        System.out.println("recevier name : " + receiverUser.getFname());
                        break;
                    }
                } else if (u.getFname().equalsIgnoreCase(tabText)) {
                    receiverUser = u;
                    System.out.println("recevier name : " + receiverUser.getFname());
                    break;
                }
            }

            for (Entry e : chatTabChatID.entrySet()) {
                if (((String) e.getKey()).equalsIgnoreCase(tabText)) {
                    ID = chatTabChatID.get(e.getKey());
                }
            }

            if (ID == null) {

                for (Entry e : groupChatTabChatID.entrySet()) {
                    if (((String) e.getValue()).equalsIgnoreCase(tabText)) {
                        ID = (Integer) e.getKey();
                    }
                }

                for (Entry e : SignInController.getMainController().getChatIdGroupChatReceivers().entrySet()) {
                    System.out.println(ID);
                    if (ID.equals(e.getKey())) {
                        ArrayList<Integer> myGroupChatReceivers = (ArrayList<Integer>) e.getValue();
                        if (!myGroupChatReceivers.contains(myUser.getId())) {
                            myGroupChatReceivers.add(myUser.getId());
                        }
                        for (Integer I : myGroupChatReceivers) {
                            System.out.println("users id when i'am sending message" + I);
                        }
                        sendingMsg.setUsersIdList(myGroupChatReceivers);
                        sendingMsg.setGroupChatName(tabText);
                        sendingMsg.setSenderName(myUser.getFname());
                    }
                }

            } else {
                receiversIDS.add(receiverUser.getId());
                receiversIDS.add(myUser.getId());
                sendingMsg.setUsersIdList(receiversIDS);
            }

            sendingMsg.setChatID(ID);
            sendingMsg.setSenderName(myUser.getFname());
            sendingMsg.setMsg(sendTextArea.getText());
            System.out.println("text area text :" + sendTextArea.getText());
            System.out.println("sender name : " + myUser.getFname());
            sendingMsg.setMsgType("TEXT");
            sendingMsg.setSenderID(myUser.getId());
            sendingMsg.setMsgColor(msgColor);
            sendingMsg.setSenderPicThumbnail(controllers.SignInController.getMyBean().getProfilePic());
            try {

                CloudChat.getServFunctionsRef().sendToAll(sendingMsg);

            } catch (RemoteException ex) {
                ex.printStackTrace();
            }

            //////////////////////*xslt*////////////////////////////////
            if (fTime&&receiverUser !=null) {
                path = "\\savedChats\\" + SignInController.getMyBean().getFname() + " Messages To " + receiverUser.getFname() + ".xml";
                chatWith.setTextContent(sendingMsg.getSenderName());
                fTime = false;
            }else
            {
                 path = "\\savedChats\\" + SignInController.getMyBean().getFname() + " Messages To " + sendingMsg.getGroupChatName() + ".xml";
                chatWith.setTextContent(sendingMsg.getSenderName());
                fTime = false;
            }
            message = doc.createElement("message");
            messageBody = doc.createElement("messageBody");
            direction = doc.createElement("direction");
            from = doc.createElement("from");
            color = doc.createElement("color");
            messageBody.setTextContent(sendingMsg.getMsg());
            direction.setTextContent("right");
            from.setTextContent("me");
            color.setTextContent(msgColor);
            message.appendChild(messageBody);
            message.appendChild(direction);
            message.appendChild(from);
            message.appendChild(color);
            message.appendChild(direction);
            doc.getFirstChild().appendChild(message);
            ///////////////////////////////////////////////////////////////
            sendTextArea.setText("");

        }
    }

    public static Integer getChatId() {
        return chatId;
    }

    /**
     * method to make chatId = zero
     *
     */
    public static void makeChatIdZero() {
        chatId = 0;
    }

    public TextArea getSendTextArea() {
        return sendTextArea;
    }

    public VBox getChatVBox() {
        return chatVBox;
    }

    public ComboBox<Color> getMessageColor() {
        return messageColor;
    }

    public void showRMsg(Message msg) {
        //////////////////////*xslt*///////////////
        if (fTime) {
            path = "\\savedChats\\" + msg.getSenderName() + " Messages To " + SignInController.getMyBean().getFname() + ".xml";
            chatWith.setTextContent(msg.getSenderName());
            fTime = false;
        }
        message = doc.createElement("message");
        messageBody = doc.createElement("messageBody");
        direction = doc.createElement("direction");
        from = doc.createElement("from");
        color = doc.createElement("color");
        messageBody.setTextContent(msg.getMsg());
        message.appendChild(direction);
        direction.setTextContent("left");
        from.setTextContent("other");
        color.setTextContent(msgColor);
        message.appendChild(messageBody);
        message.appendChild(direction);
        message.appendChild(from);
        message.appendChild(color);
        doc.getFirstChild().appendChild(message);
        /////////////*xslt*////////////////////////

        mymsgs.clear();
        if (receivedMsgs.size() > 0) {
            if (!receivedMsgs.get((receivedMsgs.size() - 1)).getSenderName().equalsIgnoreCase(msg.getSenderName())) {
                int noOfmsgsSent = receivedMsgs.size();

                switch (noOfmsgsSent) {

                    case 1:
                        if (Platform.isFxApplicationThread()) {
                            chatCurrentVBox.getChildren().remove(VboxIndex - 1);
                            VboxIndex--;
                        } else {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    chatCurrentVBox.getChildren().remove(VboxIndex - 1);
                                    VboxIndex--;
                                }

                            });

                        }

                        System.out.println(receivedMsgs.get(receivedMsgs.size() - 1).getMsg());

                        SmsgNoPic smsgNoPic = new SmsgNoPic(receivedMsgs.get(receivedMsgs.size() - 1));

                        if (Platform.isFxApplicationThread()) {
                            chatCurrentVBox.getChildren().add(VboxIndex, smsgNoPic.getTotalmsgbox());
                            VboxIndex++;
                        } else {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {

                                    chatCurrentVBox.getChildren().add(VboxIndex, smsgNoPic.getTotalmsgbox());
                                    VboxIndex++;
                                }

                            });

                        }
                        break;

                    default:
                        if (Platform.isFxApplicationThread()) {
                            chatCurrentVBox.getChildren().remove(VboxIndex - 1);
                            VboxIndex--;
                        } else {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    chatCurrentVBox.getChildren().remove(VboxIndex - 1);
                                    VboxIndex--;
                                }

                            });

                        }

                        SmsgLastNoPic laststmsg = new SmsgLastNoPic(receivedMsgs.get(receivedMsgs.size() - 1));

                        if (Platform.isFxApplicationThread()) {
                            chatCurrentVBox.getChildren().add(VboxIndex, laststmsg.getMsgBox());
                            VboxIndex++;
                        } else {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    chatCurrentVBox.getChildren().add(VboxIndex, laststmsg.getMsgBox());
                                    VboxIndex++;
                                }

                            });
                        }
                        break;
                }
                receivedMsgs.clear();
            }
        }
        receivedMsgs.add(msg);
        int noOfmsgsSent = receivedMsgs.size();

        switch (noOfmsgsSent) {

            case 1:

                Smsg smsgNoPic = new Smsg(msg);

                if (Platform.isFxApplicationThread()) {
                    chatCurrentVBox.getChildren().add(VboxIndex, smsgNoPic.getTotalmsgbox());
                    VboxIndex++;
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {

                            chatCurrentVBox.getChildren().add(VboxIndex, smsgNoPic.getTotalmsgbox());
                            VboxIndex++;
                        }

                    });

                }
                break;

            case 2:

                if (Platform.isFxApplicationThread()) {
                    chatCurrentVBox.getChildren().remove(VboxIndex - 1);
                    VboxIndex--;
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            chatCurrentVBox.getChildren().remove(VboxIndex - 1);
                            VboxIndex--;
                        }

                    });

                }

                SmsgFirst firstmsg = new SmsgFirst(receivedMsgs.get(receivedMsgs.size() - 2));

                if (Platform.isFxApplicationThread()) {
                    chatCurrentVBox.getChildren().add(VboxIndex, firstmsg.getTotalmsgbox());
                    VboxIndex++;
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            chatCurrentVBox.getChildren().add(VboxIndex, firstmsg.getTotalmsgbox());
                            VboxIndex++;
                        }

                    });
                }

                SmsgLast laststmsg = new SmsgLast(receivedMsgs.get(receivedMsgs.size() - 1));

                if (Platform.isFxApplicationThread()) {
                    chatCurrentVBox.getChildren().add(VboxIndex, laststmsg.getTotalmsgbox());
                    VboxIndex++;
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            chatCurrentVBox.getChildren().add(VboxIndex, laststmsg.getTotalmsgbox());
                            VboxIndex++;
                        }

                    });
                }
                break;

            default:

                if (Platform.isFxApplicationThread()) {
                    chatCurrentVBox.getChildren().remove(VboxIndex - 1);
                    VboxIndex--;
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            chatCurrentVBox.getChildren().remove(VboxIndex - 1);
                            VboxIndex--;
                        }

                    });

                }

                SmsgMiddle middleMsg = new SmsgMiddle(receivedMsgs.get(receivedMsgs.size() - 2));

                if (Platform.isFxApplicationThread()) {
                    chatCurrentVBox.getChildren().add(VboxIndex, middleMsg.getTotalmsgbox());
                    VboxIndex++;
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            chatCurrentVBox.getChildren().add(VboxIndex, middleMsg.getTotalmsgbox());
                            VboxIndex++;
                        }

                    });
                }

                SmsgLast laststMsg = new SmsgLast(receivedMsgs.get(receivedMsgs.size() - 1));

                if (Platform.isFxApplicationThread()) {
                    chatCurrentVBox.getChildren().add(VboxIndex, laststMsg.getTotalmsgbox());
                    VboxIndex++;
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            chatCurrentVBox.getChildren().add(VboxIndex, laststMsg.getTotalmsgbox());
                            VboxIndex++;
                        }

                    });
                }
                break;
        }
    }

    public void showMyMsg(Message msg) {

        if (receivedMsgs.size() > 0) {
            int noOfmsgsSent = receivedMsgs.size();

            switch (noOfmsgsSent) {

                case 1:
                    if (Platform.isFxApplicationThread()) {
                        chatCurrentVBox.getChildren().remove(VboxIndex - 1);
                        VboxIndex--;
                    } else {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                chatCurrentVBox.getChildren().remove(VboxIndex - 1);
                                VboxIndex--;
                            }

                        });

                    }

                    System.out.println(receivedMsgs.get(receivedMsgs.size() - 1).getMsg());

                    SmsgNoPic smsgNoPic = new SmsgNoPic(receivedMsgs.get(receivedMsgs.size() - 1));

                    if (Platform.isFxApplicationThread()) {
                        chatCurrentVBox.getChildren().add(VboxIndex, smsgNoPic.getTotalmsgbox());
                        VboxIndex++;
                    } else {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {

                                chatCurrentVBox.getChildren().add(VboxIndex, smsgNoPic.getTotalmsgbox());
                                VboxIndex++;
                            }

                        });

                    }
                    break;

                default:
                    if (Platform.isFxApplicationThread()) {
                        chatCurrentVBox.getChildren().remove(VboxIndex - 1);
                        VboxIndex--;
                    } else {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                chatCurrentVBox.getChildren().remove(VboxIndex - 1);
                                VboxIndex--;
                            }

                        });

                    }

                    SmsgLastNoPic laststmsg = new SmsgLastNoPic(receivedMsgs.get(receivedMsgs.size() - 1));

                    if (Platform.isFxApplicationThread()) {
                        chatCurrentVBox.getChildren().add(VboxIndex, laststmsg.getMsgBox());
                        VboxIndex++;
                    } else {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                chatCurrentVBox.getChildren().add(VboxIndex, laststmsg.getMsgBox());
                                VboxIndex++;
                            }

                        });
                    }
                    break;
            }
        }

        receivedMsgs.clear();

        mymsgs.add(msg);

        int noOfmsgs = mymsgs.size();

        switch (noOfmsgs) {

            case 1:
                Msg smsg = new Msg(msg);

                if (Platform.isFxApplicationThread()) {
                    chatCurrentVBox.getChildren().add(VboxIndex, smsg.getMsgBox());
                    VboxIndex++;
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            chatCurrentVBox.getChildren().add(VboxIndex, smsg.getMsgBox());
                            VboxIndex++;
                        }

                    });

                }
                break;

            case 2:

                if (Platform.isFxApplicationThread()) {
                    chatCurrentVBox.getChildren().remove(VboxIndex - 1);
                    VboxIndex--;
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            chatCurrentVBox.getChildren().remove(VboxIndex - 1);
                            VboxIndex--;
                        }

                    });

                }

                RMsgFirst firstmsg = new RMsgFirst(mymsgs.get(mymsgs.size() - 2));

                if (Platform.isFxApplicationThread()) {
                    chatCurrentVBox.getChildren().add(VboxIndex, firstmsg.getMsgBox());
                    VboxIndex++;
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            chatCurrentVBox.getChildren().add(VboxIndex, firstmsg.getMsgBox());
                            VboxIndex++;
                        }

                    });
                }

                RMsgLast laststmsg = new RMsgLast(mymsgs.get(mymsgs.size() - 1));

                if (Platform.isFxApplicationThread()) {
                    chatCurrentVBox.getChildren().add(VboxIndex, laststmsg.getMsgBox());
                    VboxIndex++;
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            chatCurrentVBox.getChildren().add(VboxIndex, laststmsg.getMsgBox());
                            VboxIndex++;
                        }

                    });
                }
                break;

            default:

                if (Platform.isFxApplicationThread()) {
                    chatCurrentVBox.getChildren().remove(VboxIndex - 1);
                    VboxIndex--;
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            chatCurrentVBox.getChildren().remove(VboxIndex - 1);
                            VboxIndex--;
                        }

                    });

                }

                RMsgMiddle middleMsg = new RMsgMiddle(mymsgs.get(mymsgs.size() - 2));

                if (Platform.isFxApplicationThread()) {
                    chatCurrentVBox.getChildren().add(VboxIndex, middleMsg.getMsgBox());
                    VboxIndex++;
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            chatCurrentVBox.getChildren().add(VboxIndex, middleMsg.getMsgBox());
                            VboxIndex++;
                        }

                    });
                }

                RMsgLast laststMsg = new RMsgLast(mymsgs.get(mymsgs.size() - 1));

                if (Platform.isFxApplicationThread()) {
                    chatCurrentVBox.getChildren().add(VboxIndex, laststMsg.getMsgBox());
                    VboxIndex++;
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            chatCurrentVBox.getChildren().add(VboxIndex, laststMsg.getMsgBox());
                            VboxIndex++;
                        }

                    });
                }
                break;
        }

    }

    @FXML
    private void saveChat() {
        try {
            file = new File(path);
            file.createNewFile();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            Element root = doc.getDocumentElement();
            ProcessingInstruction instruction = doc.createProcessingInstruction("xml-stylesheet", "href=\"styleSheet.xsl\" type=\"text/xsl\" ");
            doc.insertBefore(instruction, root);
            StreamResult result = new StreamResult(file);
            transformer.setOutputProperty(OutputKeys.INDENT, "Yes");
            transformer.transform(source, result);
            docBF = DocumentBuilderFactory.newInstance();
            docBuild = docBF.newDocumentBuilder();
            doc = docBuild.newDocument();
            rootElement = doc.createElement("chat");
            chatWith = doc.createElement("chatWith");
            doc.appendChild(rootElement);
            doc.getFirstChild().appendChild(chatWith);
            fTime = true;
        } catch (TransformerException ex) {
            Logger.getLogger(ChatTapController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ChatTapController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ChatTapController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
