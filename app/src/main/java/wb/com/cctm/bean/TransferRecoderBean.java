package wb.com.cctm.bean;

/**
 * Created by wb on 2018/4/24.
 */

public class TransferRecoderBean {
    String W_ADDRESS;
    String SEND_MONEY;
    String ID;
    String CREATE_TIME;
    String CURRENCY_TYPE;

    public String getW_ADDRESS() {
        return W_ADDRESS;
    }

    public void setW_ADDRESS(String w_ADDRESS) {
        W_ADDRESS = w_ADDRESS;
    }

    public String getSEND_MONEY() {
        return SEND_MONEY;
    }

    public void setSEND_MONEY(String SEND_MONEY) {
        this.SEND_MONEY = SEND_MONEY;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCREATE_TIME() {
        return CREATE_TIME;
    }

    public void setCREATE_TIME(String CREATE_TIME) {
        this.CREATE_TIME = CREATE_TIME;
    }

    public String getCURRENCY_TYPE() {
        return CURRENCY_TYPE;
    }

    public void setCURRENCY_TYPE(String CURRENCY_TYPE) {
        this.CURRENCY_TYPE = CURRENCY_TYPE;
    }
}
