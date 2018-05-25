package wb.com.cctm.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;

import org.xutils.http.RequestParams;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wb.com.cctm.R;
import wb.com.cctm.base.BaseActivity;
import wb.com.cctm.commons.utils.RegExpValidator;
import wb.com.cctm.commons.utils.SPUtils;
import wb.com.cctm.commons.utils.StringUtil;
import wb.com.cctm.commons.utils.ToastUtils;
import wb.com.cctm.net.CommonCallbackImp;
import wb.com.cctm.net.FlowAPI;
import wb.com.cctm.net.MXUtils;


public class Register2Activity extends BaseActivity {

    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.et_re_password)
    EditText et_re_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(this,R.layout.activity_register2);
        appendTopBody(R.layout.activity_top_icon);
        setTopLeftDefultListener();
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_register})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                register();
                break;
        }
    }

    private void register() {
        final String username = getIntent().getStringExtra("username");
        final String password = et_password.getText().toString();
        final String userphone = getIntent().getStringExtra("userphone");;
        String re_password = et_re_password.getText().toString();
        String yaoqingcode = getIntent().getStringExtra("yaoqingcode");;
        String code = getIntent().getStringExtra("code");
        if (TextUtils.isEmpty(password)) {
            ToastUtils.toastutils("密码输入为空",Register2Activity.this);
            return;
        }
        if (!StringUtil.checkpwd(password)) {
            ToastUtils.toastutils("密码输入为空请输入6-15位字母、数字的密码",Register2Activity.this);
            return;
        }
        if (!password.equals(re_password)) {
            ToastUtils.toastutils("密码输入不一致",Register2Activity.this);
            return;
        }
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.register);
        requestParams.addParameter("USER_NAME", username);
        requestParams.addParameter("PASSWORD", password);
        requestParams.addParameter("ACCOUNT", userphone);
        requestParams.addParameter("SJYZM", code);
        requestParams.addParameter("YQ_CODE", yaoqingcode);
        MXUtils.httpPost(requestParams,new CommonCallbackImp("USER - 用户注册",requestParams,this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    JSONObject pd_obj = JSONObject.parseObject(pd);
                    SPUtils.putString(SPUtils.username,username);
                    SPUtils.putString(SPUtils.password,password);
                    SPUtils.putString(SPUtils.phone,userphone);
                    SPUtils.putString(SPUtils.isLogin,"1");
                    Intent intent = new Intent(Register2Activity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    ToastUtils.toastutils(message,Register2Activity.this);
                }

            }
        });

    }

}
