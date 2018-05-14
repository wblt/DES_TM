package wb.com.cctm.base;

import android.app.Activity;
import android.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tu.loadingdialog.LoadingDialog;

import wb.com.cctm.App;
import wb.com.cctm.R;

public class BaseActivity extends AppCompatActivity {
    private LinearLayout base_main;
    private LinearLayout base_top;
    private LoadingDialog.Builder builder;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        initBaseView();
    }

    protected void initBaseView(){
        base_main = (LinearLayout) findViewById(R.id.base_main);
        base_top = (LinearLayout) findViewById(R.id.base_top);
    }

    /**
     * 设置页面主体内容
     */
    protected void appendMainBody(Object object,int pResID) {
        View view = LayoutInflater.from(this).inflate(pResID, null);
        RelativeLayout.LayoutParams _LayoutParams = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        base_main.addView(view,_LayoutParams);
    }


    /**
     * 设置页面头部内容
     */
    protected void appendTopBody(int pResID) {
        base_top.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(this).inflate(pResID,null);
        base_top.addView(view);
    }

    /**
     * 设置页面头部标题
     */
    protected void setTopBarTitle(String pText) {
        TextView tvTitle = (TextView) findViewById(R.id.top_center_text);
        if (tvTitle!=null) {
            tvTitle.setText(pText);
        }
    }

    /**
     * 设置返回事件
     */
    public void setTopLeftListener(final Activity activity){
        ImageButton top_left = (ImageButton) findViewById(R.id.top_left);
        top_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
    }

    /**
     *  默认监听
     */
    protected void setTopLeftDefultListener(){
        View leftView=findViewById(R.id.top_left);
        if(leftView!=null){
            leftView.setVisibility(View.VISIBLE);
            leftView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    /**
     * 加载菊花
     */
    public void showLoadding(String message) {
        if (builder == null) {
            builder = new LoadingDialog.Builder(this)
                    .setCancelable(false);
        }
        builder.setMessage(message);
        if (loadingDialog == null) {
            loadingDialog = builder.create();
        }
        loadingDialog.show();
    }

    /**
     * 消失菊花
     */
    public void dismissLoadding() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        super.onDestroy();
    }

}
