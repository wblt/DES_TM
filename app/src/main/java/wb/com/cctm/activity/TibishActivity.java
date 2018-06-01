package wb.com.cctm.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;
import com.lmj.mypwdinputlibrary.InputPwdView;
import com.lmj.mypwdinputlibrary.MyInputPwdUtil;

import org.xutils.http.RequestParams;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wb.com.cctm.R;
import wb.com.cctm.base.BaseActivity;
import wb.com.cctm.commons.utils.SPUtils;
import wb.com.cctm.commons.utils.ToastUtils;
import wb.com.cctm.net.CommonCallbackImp;
import wb.com.cctm.net.FlowAPI;
import wb.com.cctm.net.MXUtils;

public class TibishActivity extends BaseActivity {
    private MyInputPwdUtil myInputPwdUtil;
    @BindView(R.id.et_wallet_address)
    EditText et_wallet_address;
    @BindView(R.id.et_number)
    EditText et_number;
    @BindView(R.id.btn_commit)
    Button btn_commit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(this,R.layout.activity_tibish);
        appendTopBody(R.layout.activity_top_text);
        setTopLeftDefultListener();
        setTopBarTitle("提币申请");
        ButterKnife.bind(this);
        initview();
    }

    private void initview() {
        myInputPwdUtil = new MyInputPwdUtil(this);
        myInputPwdUtil.getMyInputDialogBuilder().setAnimStyle(R.style.dialog_anim);
        myInputPwdUtil.setListener(new InputPwdView.InputPwdListener() {
            @Override
            public void hide() {
                myInputPwdUtil.hide();
            }
            @Override
            public void forgetPwd() {
                ToastUtils.toastutils("忘记密码",TibishActivity.this);
            }

            @Override
            public void finishPwd(String pwd) {
                myInputPwdUtil.hide();
                tequila(pwd);
            }
        });
    }

    @OnClick({R.id.btn_commit})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_commit:
                if (TextUtils.isEmpty(et_number.getText().toString())) {
                    ToastUtils.toastutils("请输入提取数量",TibishActivity.this);
                    return;
                }
                if (TextUtils.isEmpty(et_wallet_address.getText().toString())) {
                    ToastUtils.toastutils("请输入提取的钱包地址",TibishActivity.this);
                    return;
                }
                if (SPUtils.getString(SPUtils.safety).equals("0")) {
                    Intent intent = new Intent(TibishActivity.this,SafetyPwdActivity.class);
                    startActivity(intent);
                } else {
                    myInputPwdUtil.show();
                }
                break;
        }
    }

    private void tequila(String pwd) {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.tequila);
        requestParams.addParameter("USER_NAME", SPUtils.getString(SPUtils.username));
        requestParams.addParameter("W_ADDRESS", et_wallet_address.getText().toString());
        requestParams.addParameter("S_MONEY", et_number.getText().toString());
        requestParams.addParameter("PASSW", pwd);
        MXUtils.httpPost(requestParams,new CommonCallbackImp("MY - 提币申请",requestParams,this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    JSONObject pd_obj = JSONObject.parseObject(pd);
                    ToastUtils.toastutils("提交成功",TibishActivity.this);
                } else {
                    ToastUtils.toastutils(message,TibishActivity.this);
                }

            }
        });
    }
}
