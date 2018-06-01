package wb.com.cctm.bean;

/**
 * Created by wb on 2018/6/1.
 */

public class TibiBean {
    private String STATUS;
    private String ID;
    private String USER_NAME;
    private String S_CURRENCY;
    private String CREATE_TIME;
    private String W_ADDRESS;

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUSER_NAME() {
        return USER_NAME;
    }

    public void setUSER_NAME(String USER_NAME) {
        this.USER_NAME = USER_NAME;
    }

    public String getS_CURRENCY() {
        return S_CURRENCY;
    }

    public void setS_CURRENCY(String s_CURRENCY) {
        S_CURRENCY = s_CURRENCY;
    }

    public String getCREATE_TIME() {
        return CREATE_TIME;
    }

    public void setCREATE_TIME(String CREATE_TIME) {
        this.CREATE_TIME = CREATE_TIME;
    }

    public String getW_ADDRESS() {
        return W_ADDRESS;
    }

    public void setW_ADDRESS(String w_ADDRESS) {
        W_ADDRESS = w_ADDRESS;
    }
}
