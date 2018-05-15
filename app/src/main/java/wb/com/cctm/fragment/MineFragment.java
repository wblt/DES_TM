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

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import wb.com.cctm.R;
import wb.com.cctm.activity.CompoundActivity;
import wb.com.cctm.activity.FinancialTransferActivity;
import wb.com.cctm.activity.InvitingFriendsActivity;
import wb.com.cctm.activity.MyorderActivity;
import wb.com.cctm.activity.ReciveCodeActivity;
import wb.com.cctm.activity.ReciverRecordActivity;
import wb.com.cctm.activity.SettingActivity;
import wb.com.cctm.activity.StepRecoderActivity;
import wb.com.cctm.activity.TransferRecoderActivity;
import wb.com.cctm.activity.UserInfoActivity;
import wb.com.cctm.activity.WalletConversionActivity;
import wb.com.cctm.base.BaseFragment;
import wb.com.cctm.commons.utils.ImageLoader;
import wb.com.cctm.commons.utils.SPUtils;
import wb.com.cctm.commons.utils.ToastUtils;
import wb.com.cctm.commons.zxing.android.CaptureActivity;
import wb.com.cctm.commons.zxing.bean.ZxingConfig;
import wb.com.cctm.commons.zxing.common.Constant;

import static android.app.Activity.RESULT_OK;

public class MineFragment extends BaseFragment {

    @BindView(R.id.top_right_icon)
    ImageButton top_right_icon;
    @BindView(R.id.top_left)
    ImageButton top_left;
    private Unbinder unbinder;
    @BindView(R.id.iv_head_img)
    ImageView iv_head_img;
    @BindView(R.id.tv_nick_name)
    TextView tv_nick_name;
    @BindView(R.id.tv_w_energy)
    TextView tv_w_energy;
    @BindView(R.id.tv_wallet_address)
    TextView tv_wallet_address;
    @BindView(R.id.tv_copy_address)
    TextView tv_copy_address;
    private int REQUEST_CODE_SCAN = 111;
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
        appendMainBody(this,R.layout.fragment_mine);
        appendTopBody(R.layout.activity_top_icon);
        setTopBarTitle("我的");
        unbinder = ButterKnife.bind(this,view);
        initview(view);
        initgradle(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        String headpath = SPUtils.getString(SPUtils.headimgpath);
        if (!TextUtils.isEmpty(headpath)) {
            ImageLoader.load(headpath,iv_head_img);
        }
        tv_nick_name.setText(SPUtils.getString(SPUtils.nick_name));
        tv_w_energy.setText(SPUtils.getString(SPUtils.w_energy));
        tv_wallet_address.setText(SPUtils.getString(SPUtils.wallet_address));
    }

    private void initview(View view) {
//        top_left.setImageResource(R.mipmap.scan);
//        top_left.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (ContextCompat.checkSelfPermission(getActivity(),
//                        Manifest.permission.CAMERA)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    //权限还没有授予，需要在这里写申请权限的代码
//                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_SCAN);
//                } else {
//                    Intent intent = new Intent(getActivity(), CaptureActivity.class);
//                                /*ZxingConfig是配置类  可以设置是否显示底部布局，闪光灯，相册，是否播放提示音  震动等动能
//                                * 也可以不传这个参数
//                                * 不传的话  默认都为默认不震动  其他都为true
//                                * */
//                    ZxingConfig config = new ZxingConfig();
//                    config.setPlayBeep(true);
//                    config.setShake(true);
//                    config.setShowbottomLayout(false);
//                    intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
//                    startActivityForResult(intent, REQUEST_CODE_SCAN);
//                }
//            }
//        });
        top_left.setVisibility(View.INVISIBLE);
        top_right_icon.setImageResource(R.mipmap.setting_icon);
        top_right_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    @OnClick({R.id.ll_user_info,R.id.tv_copy_address})
    void viewClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.ll_user_info:
                intent = new Intent(getActivity(), UserInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_copy_address:
                ClipboardManager clip_left = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                clip_left.setText(tv_wallet_address.getText());
                ToastUtils.toastutils("你已复制到粘贴板",getActivity());
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    private void initgradle(View view) {
        GridView gridView= (GridView) view.findViewById(R.id.gridview);//初始化
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                final String content = data.getStringExtra(Constant.CODED_CONTENT);
//                Toast.makeText(getActivity(),content, Toast.LENGTH_SHORT).show();
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
}
