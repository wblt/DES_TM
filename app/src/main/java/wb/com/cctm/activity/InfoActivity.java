package wb.com.cctm.activity;

import android.content.Intent;
import android.icu.text.IDNA;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wb.com.cctm.R;
import wb.com.cctm.base.BaseActivity;

public class InfoActivity extends BaseActivity {
    @BindView(R.id.top_left)
    ImageButton top_left;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(this,R.layout.activity_info);
        appendTopBody(R.layout.activity_top_icon);
        ButterKnife.bind(this);
        iniview();
    }

    private void iniview() {
        top_left.setVisibility(View.INVISIBLE);
    }
    @OnClick({R.id.btn_login,R.id.btn_register})
    void viewClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_register:
                intent = new Intent(InfoActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_login:
                intent = new Intent(InfoActivity.this,LoginActivity.class);
                startActivity(intent);
                break;
        }
    }
}
