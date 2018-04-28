package chatComponents;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javax.imageio.ImageIO;
import messagepkg.Message;

/**
 *
 * @author Mina
 *
 * this class is created to change message style while chatting this class
 * models users last received messages
 *
 */
public class SmsgLast {

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
     * color of the message
     */
    private Color myMsgColor;
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
     * circle to carry user image
     */
    private Circle cir2;

    /**
     * public constructor to create message
     *
     * @param msg
     *
     */
    public SmsgLast(Message s) {
        msgBox = new HBox();
        Label msgLabel = new Label(s.getMsg());
        //styling the message
        msgLabel.setStyle("-fx-background-color: #bbbec1 ; "
                + "-fx-border-radius: 0 20 20 20; "
                + "-fx-background-radius: 0 20 20 20; "
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
        msgLabel.setTextFill(myMsgColor);
        rawImage = s.getSenderPicThumbnail();

        if (s.getSenderPicThumbnail() == null) {
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
                rawImage = s.getSenderPicThumbnail();
                buffImage = javax.imageio.ImageIO.read(new ByteArrayInputStream(rawImage));
                clientImage = SwingFXUtils.toFXImage(buffImage, null);
                //user round image
                cir2 = new Circle(7);
                cir2.setFill(new ImagePattern(clientImage));
            } catch (IOException ex) {
                Logger.getLogger(Smsg.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        msgBox.getChildren().addAll(cir2, msgLabel);

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
