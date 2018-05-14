package wb.com.cctm.bean;

/**
 * Created by wb on 2018/4/24.
 */

public class ReciverBean {
    String RECEIVE_MONEY;
    String W_ADDRESS;
    String ID;
    String CREATE_TIME;
    String CURRENCY_TYPE;

    public String getRECEIVE_MONEY() {
        return RECEIVE_MONEY;
    }

    public void setRECEIVE_MONEY(String RECEIVE_MONEY) {
        this.RECEIVE_MONEY = RECEIVE_MONEY;
    }

    public String getW_ADDRESS() {
        return W_ADDRESS;
    }

    public void setW_ADDRESS(String w_ADDRESS) {
        W_ADDRESS = w_ADDRESS;
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
