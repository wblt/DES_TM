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
import wb.com.cctm.activity.TibijlActivity;
import wb.com.cctm.activity.TibishActivity;
import wb.com.cctm.activity.TransferRecoderActivity;
import wb.com.cctm.activity.UserInfoActivity;
import wb.com.cctm.activity.WalletConversionActivity;
import wb.com.cctm.activity.WalletRecordActivity;
import wb.com.cctm.base.BaseActivity;
import wb.com.cctm.base.BaseFragment;
import wb.com.cctm.commons.utils.CommonUtils;
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
    private int REQUEST_CODE_SCAN = 111;
    @BindView(R.id.top_right_icon)
    ImageButton top_right_icon;
    @BindView(R.id.top_left)
    ImageButton top_left;
    @BindView(R.id.gridview)
    GridView gridView;
    @BindView(R.id.tv_username)
    TextView tv_username;
    @BindView(R.id.tv_ip)
    TextView tv_ip;
    @BindView(R.id.iv_head_img)
    ImageView iv_head_img;
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
            R.mipmap.yaoqing_icon,
            R.mipmap.tibi_shengqing,
            R.mipmap.tibi_jilu,
            R.mipmap.setting_icon};
    //定义图标下方的名称数组
    private String[] name = {
            "算力钱包",
            "区块DEC",
            "零钱包",
            "能量钱包",
            "收款地址",
            "邀请好友",
            "提币申请",
            "提币记录",
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
        initgradle();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        tv_username.setText(SPUtils.getString(SPUtils.nick_name));
        String ip = CommonUtils.getIPAddress(getActivity());
        if (ip == null) {
            tv_ip.setText("登录IP  "+ "0.0.0.0");
        } else {
            tv_ip.setText("登录IP  "+ ip);
        }
        ImageLoader.loadCircle(SPUtils.getString(SPUtils.headimgpath),iv_head_img);
        homepage();
    }

    private void initview(View view) {
        top_left.setVisibility(View.INVISIBLE);
        setTopBarTitle("DEC");
        top_right_icon.setImageResource(R.mipmap.scan);
        top_right_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    //权限还没有授予，需要在这里写申请权限的代码
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_SCAN);
                } else {
                    Intent intent = new Intent(getActivity(), CaptureActivity.class);
                                /*ZxingConfig是配置类  可以设置是否显示底部布局，闪光灯，相册，是否播放提示音  震动等动能
                                * 也可以不传这个参数
                                * 不传的话  默认都为默认不震动  其他都为true
                                * */
                    ZxingConfig config = new ZxingConfig();
                    config.setPlayBeep(true);
                    config.setShake(true);
                    config.setShowbottomLayout(false);
                    intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                    startActivityForResult(intent, REQUEST_CODE_SCAN);
                }
            }
        });
        iv_head_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),UserInfoActivity.class);
                startActivity(intent);
            }
        });
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
                    SPUtils.putString("D_CURRENCY",D_CURRENCY);
                    SPUtils.putString("A_CURRENCY",A_CURRENCY);
                    SPUtils.putString("QK_CURRENCY",QK_CURRENCY);
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
                        break;
                    case "零钱包":
                        break;
                    case "能量钱包":
                        break;
                    case "收款地址":
                        intent = new Intent(getActivity(),ReciveCodeActivity.class);
                        startActivity(intent);
                        break;
                    case "算力":
                        intent = new Intent(getActivity(),WalletRecordActivity.class);
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
                    case "提币申请":
//                        Toast.makeText(getActivity(),"待开放",Toast.LENGTH_SHORT).show();
                        intent = new Intent(getActivity(),TibishActivity.class);
                        startActivity(intent);
                        break;
                    case "提币记录":
//                        Toast.makeText(getActivity(),"待开放",Toast.LENGTH_SHORT).show();
                        intent = new Intent(getActivity(),TibijlActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_SCAN) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted准许
                Toast.makeText(getActivity(),"已获得授权！",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), CaptureActivity.class);
                                /*ZxingConfig是配置类  可以设置是否显示底部布局，闪光灯，相册，是否播放提示音  震动等动能
                                * 也可以不传这个参数
                                * 不传的话  默认都为默认不震动  其他都为true
                                * */
                ZxingConfig config = new ZxingConfig();
                config.setPlayBeep(true);
                config.setShake(true);
                config.setShowbottomLayout(false);
                intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
            } else {
                // Permission Denied拒绝
                Toast.makeText(getActivity(),"未获得授权！",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                final String content = data.getStringExtra(Constant.CODED_CONTENT);
                // 传递进去
                showLoadding("请稍候...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadding();
                        Intent intent = new Intent(getActivity(),FinancialTransferActivity.class);
                        intent.putExtra("address",content);
                        startActivity(intent);
                    }
                },1000);

            }
        }
    }


}
