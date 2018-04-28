package chatComponents;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import messagepkg.Message;

/**
 *
 * @author Mina
 *
 * this class is created to change message style while chatting this class
 * models users first sent messages
 *
 */
public class RMsgFirst {

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
    public RMsgFirst(Message msg) {
        msgBox = new HBox();
        TextFlow msgTextFlow = new TextFlow();
        Text msgText = new Text(msg.getMsg());
        //styling the message
        msgTextFlow.setStyle("-fx-background-color: #1daf8d ; "
                + "-fx-border-radius: 20 20 0 20; "
                + "-fx-background-radius: 20 20 0 20; "
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
        msgTextFlow.getChildren().add(msgText);
        msgBox.getChildren().add(msgTextFlow);
        msgBox.setAlignment(Pos.CENTER_RIGHT);
    }

    /**
     * return HBox containing message to be shown on messages VBox
     *
     * @return msgBox
     *
     */
    public HBox getMsgBox() {
        return msgBox;
    }

}
