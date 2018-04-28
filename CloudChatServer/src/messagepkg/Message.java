/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messagepkg;

/**
 *
 * @author AMUN
 */

import java.io.Serializable;
import java.util.List;

public class Message implements Serializable {

    private int senderID;
    private String senderName;
    private String msgType;
    private String msg;
    private String msgColor;
    public static final String VOICE = "VOICE";
    public static final String TEXT = "TEXT";
    public static final String FILE = "FILE";   
    public static final String NOTIFICATION = "NOTIFICATION";
    private int chatID;
    private String groupChatName;
    private List<Integer> usersIdList;
    private byte[] senderPicThumbnail;
    private byte[] voiceMsg;
    private byte[] file;

    public String getMsgColor() {
        return msgColor;
    }
    
    public int getSenderID() {
        return senderID;
    }

    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        if (msgType.equalsIgnoreCase(TEXT) | msgType.equalsIgnoreCase(VOICE) | msgType.equalsIgnoreCase(FILE)) {
            this.msgType = msgType;
        }
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getChatID() {
        return chatID;
    }

    public void setMsgColor(String msgColor) {
        this.msgColor = msgColor;
    }

    public void setChatID(int chatID) {
        this.chatID = chatID;
    }

    public List<Integer> getUsersIdList() {
        return usersIdList;
    }

    public void setUsersIdList(List<Integer> usersIdList) {
        this.usersIdList = usersIdList;
    }

    public byte[] getSenderPicThumbnail() {
        return senderPicThumbnail;
    }

    public void setSenderPicThumbnail(byte[] senderPicThumbnail) {
        this.senderPicThumbnail = senderPicThumbnail;
    }

    public byte[] getVoiceMsg() {
        return voiceMsg;
    }

    public void setVoiceMsg(byte[] voiceMsg) {
        this.voiceMsg = voiceMsg;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
       public String getGroupChatName() {
        return groupChatName;
    }

    public void setGroupChatName(String groupChatName) {
        this.groupChatName = groupChatName;
    }

}
