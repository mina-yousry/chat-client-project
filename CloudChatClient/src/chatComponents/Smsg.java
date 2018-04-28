package chatComponents;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javax.imageio.ImageIO;
import messagepkg.Message;

/**
 *
 * @author Mina
 * 
 *
 * this class is created to change message style while chatting this class
 * models users first only received messages
 *
 */
public class Smsg implements Serializable {

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
     * client profile image
     *
     */
    private Image clientImage;
    /**
     * client profile image as a byte array
     *
     */
    private byte[] rawImage;
    /**
     * client profile image as a buffered image
     *
     */
    private BufferedImage buffImage;
    /**
     * color of the message
     */
    private Color myMsgColor;
    /**
     * circle to carry profile picture
     */
    private Circle cir2;

    /**
     * public constructor to create message
     *
     * @param msg
     *
     */
    public Smsg(Message msg) {
        msgBox = new HBox();
        TextFlow msgTextFlow = new TextFlow();
        Text msgText = new Text(msg.getMsg());
        //styling the message
        msgTextFlow.setStyle("-fx-background-color: #bbbec1 ; "
                + "-fx-border-radius: 20 20 20 20; "
                + "-fx-background-radius: 20 20 20 20; "
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
        msgBox.setAlignment(Pos.CENTER_LEFT);

        if (msg.getSenderPicThumbnail() == null) {
            try {
                BufferedImage bufferedImage = ImageIO.read(getClass().getResource("/Images/outlook-howto-icons-add.png"));
                Image profileimage = SwingFXUtils.toFXImage(bufferedImage, null);
                cir2 = new Circle(7);
                cir2.setFill(new ImagePattern(profileimage));
            } catch (IOException ex1) {
                Logger.getLogger(Smsg.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } else {
            try {
                rawImage = msg.getSenderPicThumbnail();
                buffImage = javax.imageio.ImageIO.read(new ByteArrayInputStream(rawImage));
                clientImage = SwingFXUtils.toFXImage(buffImage, null);
                //user round image
                cir2 = new Circle(7);
                cir2.setFill(new ImagePattern(clientImage));
            } catch (IOException ex) {
                Logger.getLogger(Smsg.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        msgTextFlow.getChildren().add(msgText);
        msgBox.getChildren().addAll(cir2, msgTextFlow);
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
