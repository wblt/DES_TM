package wb.com.cctm.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.zxing.WriterException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wb.com.cctm.R;
import wb.com.cctm.base.BaseActivity;
import wb.com.cctm.commons.utils.BitmapUtils;
import wb.com.cctm.commons.utils.ImageLoader;
import wb.com.cctm.commons.utils.SPUtils;
import wb.com.cctm.commons.utils.ToastUtils;
import wb.com.cctm.commons.zxing.encode.CodeCreator;

import static org.xutils.common.util.IOUtil.copy;

public class ReciveCodeActivity extends BaseActivity {

    @BindView(R.id.contentIv)
    ImageView contentIv;
    @BindView(R.id.iv_head_img)
    ImageView iv_head_img;
    @BindView(R.id.tv_address)
    TextView tv_address;
    @BindView(R.id.tv_copy)
    TextView tv_copy;
    @BindView(R.id.tv_nick_name)
    TextView tv_nick_name;
    @BindView(R.id.ll_code)
    LinearLayout ll_code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(this,R.layout.activity_recive_code);
        appendTopBody(R.layout.activity_top_text);
        setTopLeftDefultListener();
        setTopBarTitle("收付款");
        ButterKnife.bind(this);
        initView();
    }


    @OnClick({R.id.ll_transfer,R.id.tv_copy,R.id.ll_transfer_in,R.id.ll_transfer_out})
    void viewClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.tv_copy:
                ClipboardManager clip_left = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                clip_left.setText(tv_address.getText());
                ToastUtils.toastutils("你已复制到粘贴板",ReciveCodeActivity.this);
                break;
            case R.id.ll_transfer_in:
                intent = new Intent(ReciveCodeActivity.this,ReciverRecordActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_transfer_out:
                intent = new Intent(ReciveCodeActivity.this,TransferRecoderActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_transfer:
                intent = new Intent(ReciveCodeActivity.this,FinancialTransferActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void initView() {
        showLoadding("请稍候...");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissLoadding();
                try {
                    String headpath = SPUtils.getString(SPUtils.headimgpath);
                    if (!TextUtils.isEmpty(headpath)) {
                        ImageLoader.load(headpath,iv_head_img);
                    }
                    tv_nick_name.setText(SPUtils.getString(SPUtils.nick_name));
                    String address = SPUtils.getString(SPUtils.wallet_address);
                    tv_address.setText(address);
                    Bitmap bitmap = CodeCreator.createQRCode(address, 1000, 1000, null);
                    if (bitmap != null) {
                        ll_code.setVisibility(View.VISIBLE);
                        contentIv.setImageBitmap(bitmap);
                    }
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        },1000);
    }

}
