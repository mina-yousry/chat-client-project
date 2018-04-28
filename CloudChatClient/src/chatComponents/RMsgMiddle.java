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
 * this class is created to change message style while chatting
 * this class models users first middle sent messages 
 * 
 */
public class RMsgMiddle {
    
    /**
     * an HBox to contain a message
     *
     */
    private HBox msgBox;
    private Color myMsgColor;
    
    /**
     * public constructor to create message 
     *
     * @param msg
     *
     */
    public RMsgMiddle(Message s) {
        msgBox = new HBox();
        TextFlow msgTextFlow = new TextFlow();
        Text msgText = new Text(s.getMsg());
        //styling the message
        msgTextFlow.setStyle("-fx-background-color: #1daf8d ; "
                + "-fx-border-radius: 20 0 0 20; "
                + "-fx-background-radius: 20 0 0 20; "
                + "-fx-padding: 10 10 10 10;"
                + "-fx-margin: 10 10 10 10;"
                + "-fx-border-insets: 2px;"
                + "-fx-background-insets: 2px;");
        
        if (s.getMsgColor().equalsIgnoreCase("BLACK")) {
            myMsgColor = Color.BLACK;
        } else if (s.getMsgColor().equalsIgnoreCase("YELLOW")) {
            myMsgColor = Color.YELLOW;
        } else if (s.getMsgColor().equalsIgnoreCase("RED")) {
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
