package dtos;

import java.io.Serializable;

/**
 *
 * @author AMUN
 */
public class FriendsBean implements Serializable {

    private int idMe;
    private int idFriend;

    public FriendsBean() {

    }

    /**
     * Creates a new friend DTO
     *
     * @param idMe user
     *
     * @param idFriend friend
     */
    public FriendsBean(int idMe, int idFriend) {
        this.idMe = idMe;
        this.idFriend = idFriend;
    }

    /**
     * Used to user idMe
     *
     * @param id user idMe
     */
    public void setIdMe(int id) {
        this.idMe = id;
    }

    /**
     * Used to return idMe
     *
     * @return user idMe
     */
    public int getIdMe() {
        return idMe;
    }

    /**
     * Used to set friend's id
     *
     * @param idFriend friend's id
     */
    public void setIdFriend(int idFriend) {
        this.idFriend = idFriend;
    }

    /**
     * Used to return friend id
     *
     * @return user idMe
     */
    public int getIdFriend() {
        return idFriend;
    }

    /**
     * Used to print idMe ;
     *
     */
    @Override
    public String toString() {
        return Integer.toString(idMe);
    }

}
