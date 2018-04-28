package dtos;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author AMUN
 */
public class UserDto implements Serializable {

    private int id;
    private String fName;
    private String lName;
    private String mail;
    private String pass;
    private String connStatus;
    public static String OFFLINE = "OFFLINE";
    public static String ONLINE = "ONLINE";
    private String gender;
    public static String MALE = "M";
    public static String FEMALE = "F";
    private Date dob;
    private Date dor;
    private String appearanceStatus;
    public static String AVAILABLE = "AVAILABLE";
    public static String AWAY = "AWAY";
    public static String BUSY = "BUSY";
    private String country;
    private byte[] profilePic;

    public UserDto() {

    }

    /**
     * Creates a new User DTO
     *
     * @param fName user first name
     * @param lName user last name
     * @param mail user Mail address
     * @param pass user password
     * @param gender user Gender
     * @param dob user date of birth
     * @param country user Country
     * @param profilePic user Pic
     * @param connStatus user connection status
     * @param appearanceStatus user appearance status
     * @param dor user date of registration
     */
    public UserDto(String fName, String lName, String mail, String pass, String gender, Date dob,
            String country, byte[] profilePic, String connStatus, String appearanceStatus, Date dor) {

        this.fName = new String(fName);
        this.lName = new String(lName);
        this.mail = new String(mail);
        this.pass = new String(pass);
        this.gender = new String(gender);
        this.dob = dob;
        this.country = new String(country);
        this.profilePic = profilePic;
        this.connStatus = new String(connStatus);
        this.appearanceStatus = new String(appearanceStatus);
        this.dor = dor;
    }

    /**
     * Used if we changed user id to premium range
     *
     * @param id user id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Used to return user id
     *
     * @return user id
     */
    public int getId() {
        return id;
    }

    /**
     * Used to set user first name
     *
     * @param fName
     */
    public void setFname(String fName) {
        this.fName = fName;
    }

    public String getFname() {
        return fName;
    }

    /**
     * Used to set user last name
     *
     * @param lName
     */
    public void setLname(String lName) {
        this.lName = lName;

    }

    /**
     * Used to get user connection status
     *
     * @return String
     */
    public String getLname() {
        return lName;
    }

    /**
     * Used to set user Email Add
     *
     * @param mail
     */
    public void setMail(String mail) {
        this.mail = mail;

    }

    /**
     * Used to get user Email Address
     *
     * @return String
     */
    public String getMail() {
        return mail;
    }

    /**
     * Used to set user's password
     *
     * @param pass
     */
    public void setPassword(String pass) {
        this.pass = pass;

    }

    /**
     * Used to get user Password
     *
     * @return String
     */
    public String getPassword() {
        return pass;
    }

    /**
     * Used to set user appearance Status
     *
     * @param appearanceStatus
     */
    public void setAppearanceStatus(String appearanceStatus) {

        this.appearanceStatus = appearanceStatus;

    }

    /**
     * Used to get user appearance status
     *
     * @return String
     */
    public String getAppearanceStatus() {
        return appearanceStatus;
    }

    /**
     * Used to set user home country
     *
     * @param country
     *
     */
    public void setCountry(String country) {

        this.country = country;
    }

    /**
     * Used to get user Country name
     *
     * @return String
     */
    public String getCountry() {
        return country;
    }

    /**
     * Used to set user date of birth
     *
     * @param dob
     */
    public void setDob(Date dob) {
        this.dob = dob;
    }

    /**
     * Used to get user date of birth
     *
     * @return String
     */
    public Date getDob() {
        return dob;
    }

    /**
     * Used to set user Date of registeration
     *
     * @param dor
     */
    public void setDor(Date dor) {
        this.dor = dor;
    }

    /**
     * Used to get user date of registeration
     *
     * @return String
     */
    public Date getDor() {
        return dor;
    }

    /**
     * Used to set user connection status
     *
     * @param connStatus ONLINE || OFFLINE
     */
    public void setConnStatus(String connStatus) {
        if (connStatus.equalsIgnoreCase(ONLINE) || connStatus.equalsIgnoreCase(OFFLINE)) {
            this.connStatus = connStatus;
        }

    }

    /**
     * Used to get user connection status
     *
     * @return String
     */
    public String getConnStatus() {
        return connStatus;
    }

    /**
     * Used to set user gender
     *
     * @param gender
     */
    public void setGender(String gender) {
        if (gender.equalsIgnoreCase(MALE) || gender.equalsIgnoreCase(FEMALE)) {
            this.gender = gender;
        }

    }

    /**
     * Used to get user gender
     *
     *
     * @return gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Used to get user profile pic
     *
     * @return byte[]
     */
    public byte[] getProfilePic() {
        return profilePic;
    }

    /**
     * Used to set user profile pic
     *
     * @param profilePic
     */
    public void setProfilePic(byte[] profilePic) {
        this.profilePic = profilePic;
    }

    /**
     * Used to print user fName + lName + connStatus
     *
     */
    @Override
    public String toString() {
        return fName + "   " + lName + "   " + connStatus;
    }
 /**
     * Used to compare users 
     *
     */
  
    @Override
    public boolean equals(Object obj) {
        if(obj==null) return false;
        return this.id==((UserDto)obj).id;
    }
    

}
