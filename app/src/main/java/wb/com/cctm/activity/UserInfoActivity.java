package wb.com.cctm.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.nanchen.compresshelper.CompressHelper;
import com.tencent.cos.COSClient;
import com.tencent.cos.COSClientConfig;
import com.tencent.cos.common.COSEndPoint;
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
import butterknife.internal.Utils;
import ch.ielse.view.imagewatcher.ImageWatcher;
import wb.com.cctm.R;
import wb.com.cctm.base.BaseActivity;
import wb.com.cctm.commons.utils.BBConfig;
import wb.com.cctm.commons.utils.BitmapUtils;
import wb.com.cctm.commons.utils.CommonUtils;
import wb.com.cctm.commons.utils.FileUtil;
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

public class UserInfoActivity extends BaseActivity {
    Dialog dialog;
    @BindView(R.id.v_image_watcher)
    ImageWatcher v_image_watcher;
    private String filepath;
    @BindView(R.id.iv_img_head)
    ImageView iv_img_head;
    private List<ImageView> groupList;
    private List<String> urlList;
    private List<ImageItem> images;
    public static final int REQUEST_CODE_SELECT = 100;
    private String sign;
    String bucket = "ala";
    COSClient cos;
    private String HEAD_URL = "";
    @BindView(R.id.et_nick_name)
    EditText et_nick_name;
    @BindView(R.id.top_right_text)
    TextView top_right_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(this,R.layout.activity_user_info);
        setTopLeftDefultListener();
        setTopBarTitle("个人信息");
        ButterKnife.bind(this);
        initView();
        initImagePicker();
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

    @Override
    public void onResume() {
        super.onResume();
        String headpath = SPUtils.getString(SPUtils.headimgpath);
        if (!TextUtils.isEmpty(headpath)) {
            ImageLoader.load(headpath,iv_img_head);
            String hint = SPUtils.getString(SPUtils.nick_name);
            SpannableString s = new SpannableString(hint);//这里输入自己想要的提示文字
            et_nick_name.setHint(s);
            HEAD_URL = SPUtils.getString(SPUtils.headimgpath);
        }
    }


    private void initView() {
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
                Intent intent = new Intent(UserInfoActivity.this, ImageGridActivity.class);
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
                Intent intent1 = new Intent(UserInfoActivity.this, ImageGridActivity.class);
                startActivityForResult(intent1, REQUEST_CODE_SELECT);
            }
        });

        // 配置error图标
        v_image_watcher.setErrorImageRes(R.mipmap.error_picture);
        filepath = SPUtils.getString(SPUtils.headimgpath);
        if (TextUtils.isEmpty(filepath)) {
            try {
                Bitmap bitmap = BitmapUtils.readBitMap(this,R.mipmap.head);
                filepath = BBConfig.YYW_FILE_PATH + System.currentTimeMillis() + "save.png";
                FileUtil.saveBitmap(bitmap,filepath);
                SPUtils.putString(SPUtils.headimgpath,filepath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        groupList = new ArrayList<>();
        urlList = new ArrayList<>();
        groupList.add(iv_img_head);
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(true);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(1);              //选中数量限制
        imagePicker.setMultiMode(false);
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    @OnClick({R.id.ll_nick,R.id.tv_head_edit,R.id.iv_img_head,R.id.btn_commit})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.ll_nick:
                break;
            case R.id.tv_head_edit:
                dialog.show();
                break;
            case R.id.iv_img_head:
                urlList.clear();
                urlList.add(SPUtils.getString(SPUtils.headimgpath));
                v_image_watcher.show(iv_img_head,groupList,urlList);
                break;
            case R.id.btn_commit:
                cgPersonMes();
                break;
            default:
                break;
        }
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
                    ImagePicker.getInstance().getImageLoader().displayImage(UserInfoActivity.this, imageItem.path, iv_img_head, 0, 0);
                    SPUtils.putString(SPUtils.headimgpath,imageItem.path);
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


    private void cgPersonMes() {
        String nick = "";
        if (TextUtils.isEmpty(et_nick_name.getText().toString())) {
            nick = et_nick_name.getHint().toString();
        } else {
            nick = et_nick_name.getText().toString();
        }
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.cgPersonMes);
        requestParams.addParameter("USER_NAME", SPUtils.getString(SPUtils.username));
        requestParams.addParameter("HEAD_URL", HEAD_URL);
        requestParams.addParameter("NICK_NAME",nick);
        MXUtils.httpPost(requestParams,new CommonCallbackImp("TOOL - 修改个人信息",requestParams,this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    ToastUtils.toastutils("修改成功",UserInfoActivity.this);
                    String hint = et_nick_name.getText().toString();
                    ImageLoader.load(HEAD_URL,iv_img_head);
                    SpannableString s = new SpannableString(hint);//这里输入自己想要的提示文字
                    et_nick_name.setHint(s);
                    // 更新头像地址
                    SPUtils.putString(SPUtils.headimgpath,HEAD_URL);
                    SPUtils.putString(SPUtils.nick_name,hint);
                    et_nick_name.getText().clear();
                } else {
                    ToastUtils.toastutils(message,UserInfoActivity.this);
                }
            }
        });

    }


}
