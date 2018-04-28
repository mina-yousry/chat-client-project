/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messagepkg;

import java.io.Serializable;
import javafx.scene.Node;

/**
 *
 * @author Mina
 */
public class CloudNotification implements Serializable  {
    
    private String notificationBody;
    private byte[] notificationImage;

    public byte[] getNotificationImage() {
        return notificationImage;
    }

    public void setNotificationImage(byte[] notificationImage) {
        this.notificationImage = notificationImage;
    }

    public String getNotificationBody() {
        return notificationBody;
    }

    public void setNotificationBody(String notificationBody) {
        this.notificationBody = notificationBody;
    }

 
    
}
