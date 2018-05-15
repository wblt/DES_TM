package wb.com.cctm.activity;

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
import wb.com.cctm.commons.utils.ToastUtils;
import wb.com.cctm.net.CommonCallbackImp;
import wb.com.cctm.net.FlowAPI;
import wb.com.cctm.net.MXUtils;


public class SafetyPwd2Activity extends BaseActivity {

    @BindView(R.id.et_password)
    EditText et_password;
    @BindView(R.id.et_re_password)
    EditText et_re_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(this,R.layout.activity_safety_pwd2);
        appendTopBody(R.layout.activity_top_icon);
        setTopBarTitle("安全密码");
        setTopLeftDefultListener();
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_commit})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_commit:
                commit();
                break;
        }
    }

    private void commit() {
        final String phone = getIntent().getStringExtra("phone");
        final String username = getIntent().getStringExtra("username");
        String code = getIntent().getStringExtra("code");
        String password = et_password.getText().toString();
        String re_password = et_re_password.getText().toString();
        if (TextUtils.isEmpty(password)) {
            ToastUtils.toastutils("密码输入为空",SafetyPwd2Activity.this);
            return;
        }
        if (!re_password.equals(password)) {
            ToastUtils.toastutils("两次密码输入不一致",SafetyPwd2Activity.this);
            return;
        }
        if (password.length() != 6) {
            ToastUtils.toastutils("请输入6位数字密码",SafetyPwd2Activity.this);
            return;
        }
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.aqPassw);
        requestParams.addParameter("USER_NAME", username);
        requestParams.addParameter("SJYZM", code);
        requestParams.addParameter("NEW_PAS", password);
        MXUtils.httpPost(requestParams,new CommonCallbackImp("TOOL - 修改安全密码",requestParams,this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    SPUtils.putString(SPUtils.phone,phone);
                    ToastUtils.toastutils("修改成功",SafetyPwd2Activity.this);
                    SPUtils.putString(SPUtils.safety,"1");
                    finish();
                } else {
                    ToastUtils.toastutils(message,SafetyPwd2Activity.this);
                }
            }
        });

    }
}
