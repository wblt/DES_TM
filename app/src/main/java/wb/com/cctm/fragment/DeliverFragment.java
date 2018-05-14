package wb.com.cctm.fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.lzy.imagepicker.ImagePicker;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.xutils.http.RequestParams;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import wb.com.cctm.R;
import wb.com.cctm.activity.CompoundActivity;
import wb.com.cctm.activity.FinancialTransferActivity;
import wb.com.cctm.activity.FrendsActivity;
import wb.com.cctm.activity.InvitingFriendsActivity;
import wb.com.cctm.activity.MoveWalletActivity;
import wb.com.cctm.activity.MyorderActivity;
import wb.com.cctm.activity.NewsActivity;
import wb.com.cctm.activity.ReciveCodeActivity;
import wb.com.cctm.activity.ReciverRecordActivity;
import wb.com.cctm.activity.ShengHeActivity;
import wb.com.cctm.activity.StepRecoderActivity;
import wb.com.cctm.activity.TestActivity;
import wb.com.cctm.activity.TransferRecoderActivity;
import wb.com.cctm.activity.UserInfoActivity;
import wb.com.cctm.activity.WalletConversionActivity;
import wb.com.cctm.activity.WalletRecordActivity;
import wb.com.cctm.base.BaseActivity;
import wb.com.cctm.base.BaseFragment;
import wb.com.cctm.commons.step.UpdateUiCallBack;
import wb.com.cctm.commons.step.service.StepService;
import wb.com.cctm.commons.step.utils.SharedPreferencesUtils;
import wb.com.cctm.commons.step.view.StepArcView;
import wb.com.cctm.commons.utils.CommonUtils;
import wb.com.cctm.commons.utils.ImageLoader;
import wb.com.cctm.commons.utils.SPUtils;
import wb.com.cctm.commons.utils.ToastUtils;
import wb.com.cctm.commons.widget.MyGridView;
import wb.com.cctm.net.CommonCallbackImp;
import wb.com.cctm.net.FlowAPI;
import wb.com.cctm.net.MXUtils;

public class DeliverFragment extends BaseFragment {
    Unbinder unbinder;
    @BindView(R.id.top_left)
    ImageButton top_left;
    @BindView(R.id.top_right_icon)
    ImageButton top_right_icon;
    //定义图标数组
    private int[] imageRes = {
            R.mipmap.chang,
            R.mipmap.chang,
            R.mipmap.chang,
            R.mipmap.chang,
            R.mipmap.chang,
            R.mipmap.chang,
            R.mipmap.chang,
            R.mipmap.chang,
            R.mipmap.chang};
    //定义图标下方的名称数组
    private String[] name = {
            "我的订单",
            "复利设置",
            "财务转账",
            "转账记录",
            "接收记录",
            "能量兑换",
            "运动记录",
            "收款地址",
            "更多"
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        appendMainBody(this,R.layout.fragment_deliver);
        appendTopBody(R.layout.activity_top_icon);
        setTopBarTitle("首页");
        unbinder = ButterKnife.bind(this,view);
        initview(view);
        initgradle(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        homepage();
    }

    private void initview(View view) {
        top_left.setVisibility(View.INVISIBLE);
        top_right_icon.setImageResource(R.mipmap.scan);
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    private void homepage() {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.homePage);
        requestParams.addParameter("USER_NAME", SPUtils.getString(SPUtils.username));
        MXUtils.httpPost(requestParams,new CommonCallbackImp("INDEX - 首页",requestParams, (BaseActivity) getActivity()){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    JSONObject pd_obj = JSONObject.parseObject(pd);
                    SPUtils.putString(SPUtils.wallet_address,pd_obj.getString("W_ADDRESS"));
                    SPUtils.putString(SPUtils.headimgpath,pd_obj.getString("HEAD_URL"));
                    SPUtils.putString(SPUtils.nick_name,pd_obj.getString("NICK_NAME"));
                    SPUtils.putString(SPUtils.safety,pd_obj.getString("IFPAS"));
                    SPUtils.putString(SPUtils.w_energy,pd_obj.getString("W_ENERGY"));
                } else {
                    ToastUtils.toastutils(message,getActivity());
                }
            }
        });
    }


    private void initgradle(View view) {
        MyGridView gridView= (MyGridView) view.findViewById(R.id.gridview);//初始化
        gridView.setNestedScrollingEnabled(false);
        gridView.setFocusable(false);
        //生成动态数组，并且转入数据
        ArrayList<HashMap<String ,Object>> listItemArrayList=new ArrayList<HashMap<String,Object>>();
        for(int i=0; i<imageRes.length; i++){
            HashMap<String, Object> map=new HashMap<String,Object>();
            map.put("itemImage", imageRes[i]);
            map.put("itemText", name[i]);
            listItemArrayList.add(map);
        }
        //生成适配器的ImageItem 与动态数组的元素相对应
        SimpleAdapter saImageItems = new SimpleAdapter(getActivity(),
                listItemArrayList,//数据来源
                R.layout.item_mine,//item的XML
                //动态数组与ImageItem对应的子项
                new String[]{"itemImage","itemText"},
                //ImageItem的XML文件里面的一个ImageView,TextView ID
                new int[]{R.id.img_shoukuan,R.id.tv_tt_title });
        //添加并且显示
        gridView.setAdapter(saImageItems);
        //添加消息处理
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = name[position];
                Intent intent;
                switch (title) {
                    case "我的订单":
                        intent = new Intent(getActivity(), MyorderActivity.class);
                        startActivity(intent);
                        break;
                    case "复利设置":
                        intent = new Intent(getActivity(), CompoundActivity.class);
                        startActivity(intent);
                        break;
                    case "财务转账":
                        intent = new Intent(getActivity(), FinancialTransferActivity.class);
                        startActivity(intent);
                        break;
                    case "转账记录":
                        intent = new Intent(getActivity(), TransferRecoderActivity.class);
                        startActivity(intent);
                        break;
                    case "接收记录":
                        intent = new Intent(getActivity(), ReciverRecordActivity.class);
                        startActivity(intent);
                        break;
                    case "能量兑换":
                        intent = new Intent(getActivity(), WalletConversionActivity.class);
                        startActivity(intent);
                        break;
                    case "运动记录":
                        intent = new Intent(getActivity(), StepRecoderActivity.class);
                        startActivity(intent);
                        break;
                    case "收款地址":
                        intent = new Intent(getActivity(), ReciveCodeActivity.class);
                        startActivity(intent);
                        break;
                    case "更多":
                        ToastUtils.toastutils("开发中",getActivity());
                        break;
                    default:
                        break;
                }
            }
        });
    }
}
