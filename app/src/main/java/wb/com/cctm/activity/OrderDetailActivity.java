package wb.com.cctm.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.lmj.mypwdinputlibrary.InputPwdView;
import com.lmj.mypwdinputlibrary.MyInputPwdUtil;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.nanchen.compresshelper.CompressHelper;
import com.tencent.cos.COSClient;
import com.tencent.cos.COSClientConfig;
import com.tencent.cos.model.COSRequest;
import com.tencent.cos.model.COSResult;
import com.tencent.cos.model.PutObjectRequest;
import com.tencent.cos.model.PutObjectResult;
import com.tencent.cos.task.listener.IUploadTaskListener;

import org.xutils.http.RequestParams;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ch.ielse.view.imagewatcher.ImageWatcher;
import wb.com.cctm.R;
import wb.com.cctm.base.BaseActivity;
import wb.com.cctm.commons.utils.GlideImageLoader;
import wb.com.cctm.commons.utils.ImageLoader;
import wb.com.cctm.commons.utils.SPUtils;
import wb.com.cctm.commons.utils.Sign;
import wb.com.cctm.commons.utils.StringUtil;
import wb.com.cctm.commons.utils.ToastUtils;
import wb.com.cctm.commons.widget.ActionSheet;
import wb.com.cctm.net.CommonCallbackImp;
import wb.com.cctm.net.FlowAPI;
import wb.com.cctm.net.MXUtils;

public class OrderDetailActivity extends BaseActivity {
    @BindView(R.id.tv_order_name)
    TextView tv_order_name;
    @BindView(R.id.tv_price)
    TextView tv_price;
    @BindView(R.id.tv_number)
    TextView tv_number;
    @BindView(R.id.tv_all)
    TextView tv_all;
    @BindView(R.id.tv_bank_name)
    TextView tv_bank_name;
    @BindView(R.id.tv_bank_number)
    TextView tv_bank_number;
    @BindView(R.id.tv_bank_user_name)
    TextView tv_bank_user_name;
    @BindView(R.id.tv_bank_addr_name)
    TextView tv_bank_addr_name;
    @BindView(R.id.tv_wechat)
    TextView tv_wechat;
    @BindView(R.id.tv_zhifu_bao)
    TextView tv_zhifu_bao;
    @BindView(R.id.btn_contain)
    Button btn_contain;
    @BindView(R.id.tv_status)
    TextView tv_status;
    @BindView(R.id.tv_time)
    TextView tv_time;
    private MyInputPwdUtil myInputPwdUtil;
    private String action = "";
    @BindView(R.id.ll_tool_bar)
    LinearLayout ll_tool_bar;
    @BindView(R.id.btn_cancel)
    Button btn_cancel;
    @BindView(R.id.iv_img)
    ImageView iv_img;
    @BindView(R.id.ll_add_img)
    LinearLayout ll_add_img;
    Dialog dialog;
    public static final int REQUEST_CODE_SELECT = 100;
    private List<ImageItem> images;
    @BindView(R.id.v_image_watcher)
    ImageWatcher v_image_watcher;
    private List<ImageView> groupList;
    private List<String> urlList;
    private String file_img_path;
    @BindView(R.id.ll_dk_pz)
    LinearLayout ll_dk_pz;
    private String sign;
    String bucket = "ala";
    COSClient cos;
    private String HEAD_URL = "";
    @BindView(R.id.tv_id)
    TextView tv_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(this,R.layout.activity_order_detail);
        setTopLeftDefultListener();
        if (getIntent().getStringExtra("action").equals("卖单")) {
            setTopBarTitle("卖单详情");
        } else {
            setTopBarTitle("买单详情");
        }
        ButterKnife.bind(this);
        initImagePicker();
        initview();
        orderDetail();
        initTX();
    }

    private void initTX(){
        Context context = getApplicationContext();
        //创建COSClientConfig对象，根据需要修改默认的配置参数
        COSClientConfig config = new COSClientConfig();
        //如设置园区
        config.setEndPoint("bj");
        cos = new COSClient(context,FlowAPI.TX_FILE_ID,config, FlowAPI.TX_PRES_ID);
    }

    /**
     * 获取多次签名
     * @return
     */
    public String getSign(){
        int appId = 1254340937;
        String bucket = "ala";
        String secretId = "AKIDjGchX7LVIp8ByUHlX4LNAj131jALkuJS";
        String secretKey = "2qOindHDvMXS6UcT4OSFmCMHwKJ2WkiP";
        long expired = System.currentTimeMillis() / 1000 + 60;
        return Sign.appSignature(appId,secretId,secretKey,expired,bucket);
    }

    private void upload(String filePath){
        String filename = filePath;
        File file = new File(filename);
        String prefix= filename.substring(filename.lastIndexOf(".")+1);
        String cosPath = SPUtils.getString("mobile")+ "_" + System.currentTimeMillis() + "_" + StringUtil.getRandom5()+"." + prefix;
        PutObjectRequest putObjectRequest = new PutObjectRequest();
        putObjectRequest.setBucket(bucket);
        putObjectRequest.setCosPath(cosPath);
        putObjectRequest.setInsertOnly("0");
        putObjectRequest.setSrcPath(CompressHelper.getDefault(getApplicationContext()).compressToFile(file).getAbsolutePath());
        putObjectRequest.setSign(sign);
        putObjectRequest.setListener(new  IUploadTaskListener(){
            @Override
            public void onSuccess(COSRequest cosRequest, COSResult cosResult) {
                PutObjectResult result = (PutObjectResult) cosResult;
                dismissLoadding();
                if(result != null){
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(" 上传结果： ret=" + result.code + "; msg =" +result.msg + "\n");
                    stringBuilder.append(" access_url= " + result.access_url == null ? "null" :result.access_url + "\n");
                    stringBuilder.append(" resource_path= " + result.resource_path == null ? "null" :result.resource_path + "\n");
                    stringBuilder.append(" url= " + result.url == null ? "null" :result.url);
                    Log.i("TEST",stringBuilder.toString());
                    HEAD_URL = result.access_url;
                }
            }
            @Override
            public void onFailed(COSRequest COSRequest, final COSResult cosResult) {
                dismissLoadding();
                Log.w("TEST","上传出错： ret =" +cosResult.code + "; msg =" + cosResult.msg);
            }
            @Override
            public void onProgress(COSRequest cosRequest, final long currentSize, final long totalSize) {
                float progress = (float)currentSize/totalSize;
                progress = progress *100;
                Log.w("TEST","进度：  " + (int)progress + "%");
            }
            @Override
            public void onCancel(COSRequest cosRequest, COSResult cosResult) {
                dismissLoadding();
            }
        });
        cos.putObject(putObjectRequest);
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(false);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(1);              //选中数量限制
        imagePicker.setMultiMode(false);
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    private void initview() {
        myInputPwdUtil = new MyInputPwdUtil(OrderDetailActivity.this);
        myInputPwdUtil.getMyInputDialogBuilder().setAnimStyle(R.style.dialog_anim);
        myInputPwdUtil.setListener(new InputPwdView.InputPwdListener() {
            @Override
            public void hide() {
                myInputPwdUtil.hide();
            }
            @Override
            public void forgetPwd() {
                ToastUtils.toastutils("忘记密码",OrderDetailActivity.this);
            }

            @Override
            public void finishPwd(String pwd) {
                myInputPwdUtil.hide();
                if (action.equals("可取消")) {
                    orderCancle(pwd);
                } else if (action.equals("确认付款")){
                    pay(pwd);
                } else if (action.equals("确认收款")) {
                    surePay(pwd);
                }
            }
        });
        dialog = ActionSheet.showSheet(this,R.layout.actionsheet_photo);
        TextView cancel = dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });
        TextView take_picture = dialog.findViewById(R.id.take_picture);
        take_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
                //打开选择,本次允许选择的数量
                ImagePicker.getInstance().setSelectLimit(1);
                Intent intent = new Intent(OrderDetailActivity.this, ImageGridActivity.class);
                intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                startActivityForResult(intent, REQUEST_CODE_SELECT);
            }
        });
        TextView choose_local = dialog.findViewById(R.id.choose_local);
        choose_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
                //打开选择,本次允许选择的数量
                ImagePicker.getInstance().setSelectLimit(1);
                Intent intent1 = new Intent(OrderDetailActivity.this, ImageGridActivity.class);
                startActivityForResult(intent1, REQUEST_CODE_SELECT);
            }
        });
        // 配置error图标
        v_image_watcher.setErrorImageRes(R.mipmap.error_picture);
        groupList = new ArrayList<>();
        urlList = new ArrayList<>();
        groupList.add(iv_img);

        if (getIntent().getStringExtra("action").equals("卖单")) {
            iv_img.setVisibility(View.VISIBLE);
            ll_add_img.setVisibility(View.GONE);
        } else {
            iv_img.setVisibility(View.GONE);
            ll_add_img.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.btn_contain,R.id.btn_cancel,R.id.ll_add_img,R.id.iv_img})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_contain:
                if (SPUtils.getString(SPUtils.safety).equals("0")) {
                    Intent intent = new Intent(OrderDetailActivity.this,SafetyPwdActivity.class);
                    startActivity(intent);
                } else {
                    if (getIntent().getStringExtra("action").equals("卖单")) {
                        action = "确认收款";
                        myInputPwdUtil.show();
                    } else {
                        action = "确认付款";
                        myInputPwdUtil.show();
                    }
                }
                break;
            case R.id.btn_cancel:
                if (SPUtils.getString(SPUtils.safety).equals("0")) {
                    Intent intent = new Intent(OrderDetailActivity.this,SafetyPwdActivity.class);
                    startActivity(intent);
                } else {
                    action = "可取消";
                    myInputPwdUtil.show();
                }
                break;
            case R.id.ll_add_img:
                dialog.show();
                break;
            case R.id.iv_img:
                if (file_img_path != null && groupList != null && groupList.size()>0) {
                    urlList.clear();
                    urlList.add(file_img_path);
                    v_image_watcher.show(iv_img,groupList,urlList);
                }
                break;
            default:
                break;
        }
    }

    private void orderDetail() {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.orderDetail);
        requestParams.addParameter("TRADE_ID", getIntent().getStringExtra("TRADE_ID"));
        MXUtils.httpPost(requestParams,new CommonCallbackImp("MARKET - 订单详情",requestParams,this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    JSONObject pd_obj = JSONObject.parseObject(pd);
                    tv_order_name.setText(pd_obj.getString("USER_NAME"));
                    tv_price.setText(pd_obj.getString("BUSINESS_PRICE"));
                    tv_number.setText(pd_obj.getString("BUSINESS_COUNT"));
                    tv_all.setText(pd_obj.getString("TOTAL_MONEY"));
                    tv_bank_name.setText(pd_obj.getString("BANK_NAME"));
                    tv_bank_number.setText(pd_obj.getString("BANK_NO"));
                    tv_bank_user_name.setText(pd_obj.getString("BANK_USERNAME"));
                    tv_bank_addr_name.setText(pd_obj.getString("BANK_ADDR"));
                    tv_wechat.setText(pd_obj.getString("WCHAT"));
                    tv_zhifu_bao.setText(pd_obj.getString("ALIPAY"));
                    tv_time.setText(pd_obj.getString("CREATE_TIME"));
                    String status = pd_obj.getString("STATUS");
                    tv_id.setText(pd_obj.getString("TRADE_ID"));
                    if (status.equals("0")) {
                        tv_status.setText("待审核");
                        if (getIntent().getStringExtra("action").equals("卖单")) {
                            ll_tool_bar.setVisibility(View.VISIBLE);
                            btn_cancel.setVisibility(View.VISIBLE);
                        } else {
                            ll_tool_bar.setVisibility(View.GONE);
                            btn_cancel.setVisibility(View.GONE);
                        }
                    } else if (status.equals("1")) {
                        tv_status.setText("审核通过");
                        if (getIntent().getStringExtra("action").equals("卖单")) {
                            ll_tool_bar.setVisibility(View.VISIBLE);
                            btn_cancel.setVisibility(View.VISIBLE);
                        } else {
                            ll_tool_bar.setVisibility(View.GONE);
                            btn_cancel.setVisibility(View.GONE);
                        }
                    } else if (status.equals("2")) {
                        tv_status.setText("部分成交");
                        if (getIntent().getStringExtra("action").equals("卖单")) {
                            ll_tool_bar.setVisibility(View.VISIBLE);
                            btn_cancel.setVisibility(View.VISIBLE);
                        } else {
                            ll_tool_bar.setVisibility(View.GONE);
                            btn_cancel.setVisibility(View.GONE);
                        }
                    } else if (status.equals("3")) {
                        tv_status.setText("待付款");
                        if (getIntent().getStringExtra("action").equals("卖单")) {
                            ll_tool_bar.setVisibility(View.GONE);
                            btn_cancel.setVisibility(View.GONE);
                            btn_contain.setVisibility(View.GONE);
                        } else {
                            ll_tool_bar.setVisibility(View.VISIBLE);
                            btn_cancel.setVisibility(View.VISIBLE);
                            btn_contain.setVisibility(View.VISIBLE);
                            btn_contain.setText("确认付款");
                            ll_dk_pz.setVisibility(View.VISIBLE);
                        }
                    } else if (status.equals("4")) {
                        tv_status.setText("已付款");
                        if (getIntent().getStringExtra("action").equals("卖单")) {
                            ll_tool_bar.setVisibility(View.VISIBLE);
                            btn_cancel.setVisibility(View.GONE);
                            btn_contain.setVisibility(View.VISIBLE);
                            btn_contain.setText("确认收款");
                            ll_dk_pz.setVisibility(View.VISIBLE);
                            ll_add_img.setVisibility(View.GONE);
                            iv_img.setVisibility(View.VISIBLE);
                            file_img_path = pd_obj.getString("IMAGE_NOTE");
                            ImageLoader.load(pd_obj.getString("IMAGE_NOTE"),iv_img);
                        } else {
                            ll_tool_bar.setVisibility(View.GONE);
                            btn_cancel.setVisibility(View.GONE);
                            btn_contain.setVisibility(View.GONE);
                            ll_dk_pz.setVisibility(View.VISIBLE);
                            ll_add_img.setVisibility(View.GONE);
                            iv_img.setVisibility(View.VISIBLE);
                            file_img_path = pd_obj.getString("IMAGE_NOTE");
                            ImageLoader.load(pd_obj.getString("IMAGE_NOTE"),iv_img);
                        }
                    } else if (status.equals("5")) {
                        tv_status.setText("已成交");
                        ll_dk_pz.setVisibility(View.VISIBLE);
                        ll_add_img.setVisibility(View.GONE);
                        iv_img.setVisibility(View.VISIBLE);
                        file_img_path = pd_obj.getString("IMAGE_NOTE");
                        ImageLoader.load(pd_obj.getString("IMAGE_NOTE"),iv_img);
                    } else if (status.equals("6")) {
                        tv_status.setText("已取消");
                    } else {
                        tv_status.setText("未知状态");
                    }
                } else {
                    ToastUtils.toastutils(message,OrderDetailActivity.this);
                }

            }
        });

    }

    private void orderCancle(String pwd) {
        String cancle_type = "";
        if (getIntent().getStringExtra("action").equals("卖单")) {
            cancle_type = "1";
        } else {
            cancle_type = "0";
        }
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.orderCancle);
        requestParams.addParameter("TRADE_ID", getIntent().getStringExtra("TRADE_ID"));
        requestParams.addParameter("TYPE", cancle_type);
        requestParams.addParameter("PASSW", pwd);
        MXUtils.httpPost(requestParams,new CommonCallbackImp("MARKET - 订单取消",requestParams, this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    JSONObject pd_obj = JSONObject.parseObject(pd);
                    ToastUtils.toastutils("取消成功",OrderDetailActivity.this);
                    finish();
                } else {
                    ToastUtils.toastutils(message,OrderDetailActivity.this);
                }
            }
        });
    }

    private void pay(String pwd) {
        if (TextUtils.isEmpty(HEAD_URL)) {
            ToastUtils.toastutils("请上传打款凭证",OrderDetailActivity.this);
            return;
        }
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.pay);
        requestParams.addParameter("TRADE_ID", getIntent().getStringExtra("TRADE_ID"));
        requestParams.addParameter("STATUS", "4");
        requestParams.addParameter("IMAGE_NOTE", HEAD_URL);
        requestParams.addParameter("PASSW", pwd);
        MXUtils.httpPost(requestParams,new CommonCallbackImp("MARKET - 订单确认收款",requestParams, this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    JSONObject pd_obj = JSONObject.parseObject(pd);
                    ToastUtils.toastutils("付款成功",OrderDetailActivity.this);
                    finish();
                } else {
                    ToastUtils.toastutils(message,OrderDetailActivity.this);
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null && images.size()>0) {
                    final ImageItem imageItem = images.get(0);
                    ImagePicker.getInstance().getImageLoader().displayImage(OrderDetailActivity.this, imageItem.path, iv_img, 0, 0);
                    file_img_path = imageItem.path;
                    iv_img.setVisibility(View.VISIBLE);
                    // 上传照片
                    showLoadding("请稍候...");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sign = getSign();
                            upload(imageItem.path);
                        }
                    },1000);
                }
            }
        }
    }

    private void surePay(String pwd) {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.surePay);
        requestParams.addParameter("TRADE_ID", getIntent().getStringExtra("TRADE_ID"));
        requestParams.addParameter("PASSW", pwd);
        MXUtils.httpPost(requestParams,new CommonCallbackImp("MARKET - 订单确认收款",requestParams, this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    JSONObject pd_obj = JSONObject.parseObject(pd);
                    ToastUtils.toastutils("收款成功",OrderDetailActivity.this);
                    finish();
                } else {
                    ToastUtils.toastutils(message,OrderDetailActivity.this);
                }

            }
        });
    }
}
