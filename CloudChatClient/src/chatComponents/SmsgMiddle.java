package chatComponents;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
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
 * models users middle received messages
 *
 */
public class SmsgMiddle {

    /**
     * an HBox to contain a message
     *
     */
    private HBox msgBox;
    /**
     * color of the message
     */
    private Color myMsgColor;

    public SmsgMiddle(Message s) {
        msgBox = new HBox();
        TextFlow msgTextFlow = new TextFlow();
        Text msgText = new Text(s.getMsg());

        msgTextFlow.setStyle("-fx-background-color: #bbbec1 ; "
                + "-fx-border-radius: 0 20 20 0; "
                + "-fx-background-radius: 0 20 20 0; "
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
        //user round image
        Circle cir2 = new Circle(7);
        cir2.setFill(new Color(0.968, 0.968, 0.890, 1));
        msgTextFlow.getChildren().add(msgText);
        msgBox.getChildren().addAll(cir2, msgTextFlow);
        msgBox.setAlignment(Pos.CENTER_LEFT);
    }

    /**
     * return HBox containing message to be shown on messages VBox
     *
     * @return msgBox
     *
     */
    public HBox getTotalmsgbox() {
        return msgBox;
    }

}
