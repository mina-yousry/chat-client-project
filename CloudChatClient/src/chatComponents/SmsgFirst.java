package chatComponents;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import messagepkg.Message;

/**
 *
 * @author Mina
 *
 * this class is created to change message style while chatting this class
 * models users first received messages
 */
public class SmsgFirst {

    /**
     * an VBox to contain a message and sender name
     *
     */
    private VBox totalmsgbox;

    /**
     * an HBox to contain a message
     *
     */
    private HBox msgBox;
    /**
     * color of the message
     */
    private Color myMsgColor;

    /**
     * public constructor to create message
     *
     * @param msg
     *
     */
    public SmsgFirst(Message msg) {
        msgBox = new HBox();
        TextFlow msgTextFlow = new TextFlow();
        Text msgText = new Text(msg.getMsg());
        //styling the message
        msgTextFlow.setStyle("-fx-background-color: #bbbec1 ; "
                + "-fx-border-radius: 20 20 20 0; "
                + "-fx-background-radius: 20 20 20 0; "
                + "-fx-padding: 10 10 10 10;"
                + "-fx-margin: 10 10 10 10;"
                + "-fx-border-insets: 2px;"
                + "-fx-background-insets: 2px;");

        if (msg.getMsgColor().equalsIgnoreCase("BLACK")) {
            myMsgColor = Color.BLACK;
        } else if (msg.getMsgColor().equalsIgnoreCase("YELLOW")) {
            myMsgColor = Color.YELLOW;
        } else if (msg.getMsgColor().equalsIgnoreCase("RED")) {
            myMsgColor = Color.RED;
        } else {
            myMsgColor = Color.GREEN;
        }
        msgText.setFill(myMsgColor);
        //user round image
        Circle cir2 = new Circle(7);
        cir2.setFill(new Color(0.968, 0.968, 0.890, 1));
        msgTextFlow.getChildren().add(msgText);
        msgBox.getChildren().addAll(cir2, msgTextFlow);
        msgBox.setAlignment(Pos.CENTER_LEFT);
        Label senderLabel = new Label(msg.getSenderName());
        totalmsgbox = new VBox();
        totalmsgbox.getChildren().add(senderLabel);
        totalmsgbox.getChildren().add(msgBox);
        totalmsgbox.setAlignment(Pos.CENTER_LEFT);
    }

/**
     * return VBox containing message to be shown on messages VBox
     *
     * @return msgBox
     *
     */
    public VBox getTotalmsgbox() {
        return totalmsgbox;
    }
}
