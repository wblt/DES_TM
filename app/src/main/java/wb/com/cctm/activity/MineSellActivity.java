package wb.com.cctm.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lmj.mypwdinputlibrary.InputPwdView;
import com.lmj.mypwdinputlibrary.MyInputPwdUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.xutils.http.RequestParams;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import wb.com.cctm.R;
import wb.com.cctm.adapter.MybuyAdapter;
import wb.com.cctm.adapter.MychecAdapter;
import wb.com.cctm.base.BaseActivity;
import wb.com.cctm.base.OnItemClickListener;
import wb.com.cctm.bean.MycheckBean;
import wb.com.cctm.commons.utils.SPUtils;
import wb.com.cctm.commons.utils.ToastUtils;
import wb.com.cctm.net.CommonCallbackImp;
import wb.com.cctm.net.FlowAPI;
import wb.com.cctm.net.MXUtils;


public class MineSellActivity extends BaseActivity {

    private Unbinder unbinder;
    @BindView(R.id.recyc_list)
    RecyclerView recyc_list;
    @BindView(R.id.ll_no_data)
    LinearLayout ll_no_data;
    private String queryid = "0";
    private  MychecAdapter adpter;
    @BindView(R.id.sm_refreshLayout)
    SmartRefreshLayout sm_refreshLayout;
    private MyInputPwdUtil myInputPwdUtil;
    private String action = "";
    private String TRADE_ID = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(this,R.layout.activity_mine_sell);
        appendTopBody(R.layout.activity_top_icon);
        setTopLeftDefultListener();
        setTopBarTitle("我的卖单");
        ButterKnife.bind(this);
        initview();
        adpter.clear();
        queryid = "0";
        sellList("1");
    }
    private void initview() {
        adpter = new MychecAdapter();
        recyc_list.setLayoutManager(new LinearLayoutManager(MineSellActivity.this));
        recyc_list.setAdapter(adpter);
        adpter.setOnItemClickListener(new OnItemClickListener<MycheckBean>() {
            @Override
            public void onClick(MycheckBean mycheckBean, View view, int position) {
                Intent intent = new Intent(MineSellActivity.this, OrderDetailActivity.class);
                intent.putExtra("TRADE_ID",mycheckBean.getTRADE_ID());
                intent.putExtra("action","卖单");
                startActivity(intent);
            }
        });
        sm_refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(1000);
                queryid = "0";
                adpter.clear();
                sellList("1");
            }
        });
        sm_refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
                sellList("2");
            }
        });

        myInputPwdUtil = new MyInputPwdUtil(MineSellActivity.this);
        myInputPwdUtil.getMyInputDialogBuilder().setAnimStyle(R.style.dialog_anim);
        myInputPwdUtil.setListener(new InputPwdView.InputPwdListener() {
            @Override
            public void hide() {
                myInputPwdUtil.hide();
            }
            @Override
            public void forgetPwd() {
                ToastUtils.toastutils("忘记密码",MineSellActivity.this);
            }

            @Override
            public void finishPwd(String pwd) {
                myInputPwdUtil.hide();
                if (action.equals("可取消")) {
                    orderCancle(pwd);
                } else if (action.equals("确认收款")){
                    surePay(pwd);
                }
            }
        });
    }

    private void  sellList(String type) {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.sellList);
        requestParams.addParameter("QUERY_ID", queryid);
        requestParams.addParameter("USER_NAME", SPUtils.getString(SPUtils.username));
        requestParams.addParameter("TYPE", type);
        MXUtils.httpPost(requestParams,new CommonCallbackImp("MARKET - 卖单列表",requestParams, MineSellActivity.this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    List<MycheckBean> beanList = JSONArray.parseArray(pd,MycheckBean.class);
                    if (beanList.size()>0) {
                        queryid = beanList.get(beanList.size()-1).getTRADE_ID();
                    }
                    adpter.addAll(beanList);
                    adpter.notifyDataSetChanged();
                    if (adpter.getData().size()>0) {
                        recyc_list.setVisibility(View.VISIBLE);
                        ll_no_data.setVisibility(View.GONE);
                    } else {
                        recyc_list.setVisibility(View.GONE);
                        ll_no_data.setVisibility(View.VISIBLE);
                    }
                } else {
                    ToastUtils.toastutils(message,MineSellActivity.this);
                }

            }
        });

    }

    private void orderCancle(String pwd) {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.orderCancle);
        requestParams.addParameter("TRADE_ID", TRADE_ID);
        requestParams.addParameter("TYPE", "1");
        requestParams.addParameter("PASSW", pwd);
        MXUtils.httpPost(requestParams,new CommonCallbackImp("MARKET - 订单取消",requestParams, MineSellActivity.this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    JSONObject pd_obj = JSONObject.parseObject(pd);
                    ToastUtils.toastutils("取消成功",MineSellActivity.this);
                    // 调下刷新接口
                    queryid = "0";
                    adpter.clear();
                    sellList("1");
                } else {
                    ToastUtils.toastutils(message,MineSellActivity.this);
                }
            }
        });
    }

    private void surePay(String pwd) {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.surePay);
        requestParams.addParameter("TRADE_ID", TRADE_ID);
        requestParams.addParameter("PASSW", pwd);
        MXUtils.httpPost(requestParams,new CommonCallbackImp("MARKET - 订单确认收款",requestParams,MineSellActivity.this){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    JSONObject pd_obj = JSONObject.parseObject(pd);
                    ToastUtils.toastutils("收款成功",MineSellActivity.this);
                    // 调下刷新接口
                    queryid = "0";
                    adpter.clear();
                    sellList("1");
                } else {
                    ToastUtils.toastutils(message,MineSellActivity.this);
                }

            }
        });
    }
}
