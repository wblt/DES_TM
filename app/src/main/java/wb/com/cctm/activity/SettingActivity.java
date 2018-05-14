package wb.com.cctm.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wb.com.cctm.App;
import wb.com.cctm.R;
import wb.com.cctm.base.BaseActivity;
import wb.com.cctm.commons.utils.SPUtils;
import wb.com.cctm.commons.utils.ToastUtils;
import wb.com.cctm.commons.utils.VersionUtil;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.tv_version_name)
    TextView tv_version_name;
    @BindView(R.id.ll_safety_pwd)
    LinearLayout ll_safety_pwd;
    @BindView(R.id.ll_login_pwd)
    LinearLayout ll_login_pwd;
    @BindView(R.id.ll_phone_number)
    LinearLayout ll_phone_number;
    @BindView(R.id.ll_user_bind)
    LinearLayout ll_user_bind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(this,R.layout.activity_setting);
        appendTopBody(R.layout.activity_top_text);
        setTopBarTitle("设置");
        setTopLeftDefultListener();
        ButterKnife.bind(this);
        initview();
    }
    private void initview() {
        tv_version_name.setText(VersionUtil.getAppVersionName(SettingActivity.this));
    }

    @OnClick({R.id.btn_logout,R.id.ll_phone_number,R.id.ll_login_pwd,R.id.ll_safety_pwd,R.id.ll_user_bind})
    void viewClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_logout:
                SPUtils.clearUser();
                intent = new Intent(SettingActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                App.getInstance().closeActivitys();
                break;
            case R.id.ll_phone_number:
                intent = new Intent(SettingActivity.this,PhoneNumberActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_login_pwd:
                intent = new Intent(SettingActivity.this,LoginPwdActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_safety_pwd:
                intent = new Intent(SettingActivity.this,SafetyPwdActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_user_bind:
                intent = new Intent(SettingActivity.this,PayInfoActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
