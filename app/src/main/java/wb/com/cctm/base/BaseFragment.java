package wb.com.cctm.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.tu.loadingdialog.LoadingDialog;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import wb.com.cctm.R;

/**
 * Created by wb on 2018/4/12.
 */

public class BaseFragment extends Fragment {
    private LinearLayout base_main;
    private LinearLayout base_top;
    protected View mainview;
    private LayoutInflater inflater;
    private ViewGroup container;
    private LoadingDialog.Builder builder;
    private LoadingDialog loadingDialog;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mainview =  inflater.inflate(R.layout.fragment_base, null);
        base_main = (LinearLayout)mainview.findViewById(R.id.base_main);
        base_top = (LinearLayout) mainview.findViewById(R.id.base_top);
        this.inflater = inflater;
        this.container = container;
        return mainview;
    }

    protected void appendMainBody(Object object,int pResID) {
        View view = inflater.inflate(pResID, null);
        RelativeLayout.LayoutParams _LayoutParams = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        base_main.addView(view,_LayoutParams);
    }

    /**
     * 包含返回和设置操作顶部栏
     */
    protected void appendTopBody(int pResID) {
        base_top.setVisibility(View.VISIBLE);
        View view = inflater.inflate(pResID,null);
        base_top.addView(view);
    }

    protected void setTopBarTitle(String pText) {
        TextView tvTitle = (TextView)mainview.findViewById(R.id.top_center_text);
        if (tvTitle!=null) {
            tvTitle.setText(pText);
        }
    }

    /**
     * 加载菊花
     */
    protected void showLoadding(String message) {
        if (builder == null) {
            builder = new LoadingDialog.Builder(this.getActivity())
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
    protected void dismissLoadding() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        super.onDestroy();
    }


}
