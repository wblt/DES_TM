package wb.com.cctm.fragment;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import org.xutils.http.RequestParams;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import wb.com.cctm.R;
import wb.com.cctm.activity.AllReleaseActivity;
import wb.com.cctm.activity.ChargeActivity;
import wb.com.cctm.activity.CompoundActivity;
import wb.com.cctm.activity.FinancialTransferActivity;
import wb.com.cctm.activity.InvitingFriendsActivity;
import wb.com.cctm.activity.InvitingInfoActivity;
import wb.com.cctm.activity.MoveWalletActivity;
import wb.com.cctm.activity.MyorderActivity;
import wb.com.cctm.activity.ReciveCodeActivity;
import wb.com.cctm.activity.ReciverRecordActivity;
import wb.com.cctm.activity.SettingActivity;
import wb.com.cctm.activity.StepRecoderActivity;
import wb.com.cctm.activity.TransferRecoderActivity;
import wb.com.cctm.activity.UserInfoActivity;
import wb.com.cctm.activity.WalletConversionActivity;
import wb.com.cctm.activity.WalletRecordActivity;
import wb.com.cctm.base.BaseActivity;
import wb.com.cctm.base.BaseFragment;
import wb.com.cctm.commons.utils.ImageLoader;
import wb.com.cctm.commons.utils.SPUtils;
import wb.com.cctm.commons.utils.ToastUtils;
import wb.com.cctm.commons.zxing.android.CaptureActivity;
import wb.com.cctm.commons.zxing.bean.ZxingConfig;
import wb.com.cctm.commons.zxing.common.Constant;
import wb.com.cctm.net.CommonCallbackImp;
import wb.com.cctm.net.FlowAPI;
import wb.com.cctm.net.MXUtils;

import static android.app.Activity.RESULT_OK;

public class MineFragment extends BaseFragment {

    @BindView(R.id.top_right_icon)
    ImageButton top_right_icon;
    @BindView(R.id.top_left)
    ImageButton top_left;
    @BindView(R.id.gridview)
    GridView gridView;
    @BindView(R.id.tv_username)
    TextView tv_username;
    private Unbinder unbinder;
    private String A_CURRENCY = "";
    private String QK_CURRENCY = "";
    private String W_ENERGY = "";
    private String D_CURRENCY = "";
    private String S_CURRENCY = "";
    //定义图标数组
    private int[] imageRes = {
            R.mipmap.suanli_icon,
            R.mipmap.qukuaidec_icon,
            R.mipmap.lingqian_icon,
            R.mipmap.nengliang_icon,
            R.mipmap.shoukuan_icon,
            R.mipmap.fuli_icon,
            R.mipmap.yaoqing_icon,
            R.mipmap.dingdan_icon,
            R.mipmap.setting_icon};
    //定义图标下方的名称数组
    private String[] name = {
            "算力钱包",
            "区块DEC",
            "零钱包",
            "能量钱包",
            "收款地址",
            "复利设置",
            "邀请好友",
            "我的订单",
            "设置"
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        appendMainBody(this,R.layout.fragment_mine);
        appendTopBody(R.layout.activity_top_icon);
        unbinder = ButterKnife.bind(this,view);
        initview(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        tv_username.setText(SPUtils.getString(SPUtils.nick_name));
        homepage();
    }

    private void initview(View view) {
        top_left.setVisibility(View.INVISIBLE);
        setTopBarTitle("DEC");
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
                    D_CURRENCY = pd_obj.getString("D_CURRENCY");
                    A_CURRENCY = pd_obj.getString("A_CURRENCY");
                    QK_CURRENCY = pd_obj.getString("QK_CURRENCY");
                    W_ENERGY = pd_obj.getString("W_ENERGY");
                    S_CURRENCY = pd_obj.getString("S_CURRENCY");
                    initgradle();
                } else {
                    ToastUtils.toastutils(message,getActivity());
                }
            }
        });
    }

    private void initgradle() {
        //生成动态数组，并且转入数据
        ArrayList<HashMap<String ,Object>> listItemArrayList=new ArrayList<HashMap<String,Object>>();
        for(int i=0; i<imageRes.length; i++){
            HashMap<String, Object> map=new HashMap<String,Object>();
            map.put("itemImage", imageRes[i]);
            map.put("itemText", name[i]);
            listItemArrayList.add(map);
        }
        HashMap<String,Object> map0 = listItemArrayList.get(0);
        map0.put("itemText",S_CURRENCY);
        HashMap<String,Object> map1 = listItemArrayList.get(1);
        map1.put("itemText",QK_CURRENCY);
        HashMap<String,Object> map2 = listItemArrayList.get(2);
        map2.put("itemText",D_CURRENCY);
        HashMap<String,Object> map3 = listItemArrayList.get(3);
        map3.put("itemText",W_ENERGY);
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
                    case "算力钱包":
                        break;
                    case "区块DEC":
                        intent = new Intent(getActivity(), MoveWalletActivity.class);
                        intent.putExtra("D_CURRENCY",D_CURRENCY);
                        intent.putExtra("QK_CURRENCY",QK_CURRENCY);
                        intent.putExtra("A_CURRENCY",A_CURRENCY);
                        startActivity(intent);
                        break;
                    case "零钱包":
                        
                        break;
                    case "能量钱包":
                        break;
                    case "收款地址":
                        intent = new Intent(getActivity(),ReciveCodeActivity.class);
                        startActivity(intent);
                        break;
                    case "复利设置":
                        intent = new Intent(getActivity(),CompoundActivity.class);
                        startActivity(intent);
                        break;
                    case "邀请好友":
                        intent = new Intent(getActivity(),InvitingInfoActivity.class);
                        startActivity(intent);
                        break;
                    case "我的订单":
                        intent = new Intent(getActivity(),MyorderActivity.class);
                        startActivity(intent);
                        break;
                    case "设置":
                        intent = new Intent(getActivity(),SettingActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });
    }


}
