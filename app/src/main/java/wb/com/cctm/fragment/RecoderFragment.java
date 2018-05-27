package wb.com.cctm.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.xutils.http.RequestParams;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import wb.com.cctm.R;
import wb.com.cctm.activity.ReciverRecordActivity;
import wb.com.cctm.activity.TransferRecoderActivity;
import wb.com.cctm.adapter.ReciverAdapter;
import wb.com.cctm.adapter.TransferRecoderAdapter;
import wb.com.cctm.base.BaseActivity;
import wb.com.cctm.base.BaseFragment;
import wb.com.cctm.bean.ReciverBean;
import wb.com.cctm.bean.TransferRecoderBean;
import wb.com.cctm.commons.utils.SPUtils;
import wb.com.cctm.commons.utils.ToastUtils;
import wb.com.cctm.net.CommonCallbackImp;
import wb.com.cctm.net.FlowAPI;
import wb.com.cctm.net.MXUtils;


public class RecoderFragment extends BaseFragment {

    @BindView(R.id.top_left)
    ImageButton top_left;
    private Unbinder unbinder;
    @BindView(R.id.tv_jieshou_title)
    TextView tv_jieshou_title;
    @BindView(R.id.ll_jieshou_line)
    LinearLayout ll_jieshou_line;
    @BindView(R.id.tv_send_title)
    TextView tv_send_title;
    @BindView(R.id.ll_send_line)
    LinearLayout ll_send_line;
    @BindView(R.id.recyc_send_list)
    RecyclerView recyc_send_list;
    @BindView(R.id.recyc_jieshou_list)
    RecyclerView recyc_jieshou_list;
    private TransferRecoderAdapter transferRecoderAdapter;
    private  ReciverAdapter reciverAdapter;
    private String queryId = "0";
    @BindView(R.id.ll_no_data)
    LinearLayout ll_no_data;
    private String page_type = "发送";
    @BindView(R.id.sm_refreshLayout)
    SmartRefreshLayout sm_refreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater,container,savedInstanceState);
        appendMainBody(this,R.layout.fragment_recoder);
        appendTopBody(R.layout.activity_top_icon);
        unbinder = ButterKnife.bind(this,view);
        initview(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        queryId = "0";
        transferRecoderAdapter.clear();
        sendDetail("1");
    }

    private void initview(View view) {
        top_left.setVisibility(View.INVISIBLE);
        setTopBarTitle("记录");

        // 转账记录
        transferRecoderAdapter = new TransferRecoderAdapter();
        recyc_send_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyc_send_list.setAdapter(transferRecoderAdapter);

        // 接收记录
        reciverAdapter = new ReciverAdapter();
        recyc_jieshou_list.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyc_jieshou_list.setAdapter(reciverAdapter);

        sm_refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(1000);
                if (page_type.equals("发送")) {
                    transferRecoderAdapter.clear();
                    queryId = "0";
                    sendDetail("1");
                } else {
                    reciverAdapter.clear();
                    queryId = "0";
                    receiveDetail("1");
                }

            }
        });
        sm_refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
                if (page_type.equals("发送")) {
                    sendDetail("2");
                } else {
                    receiveDetail("2");
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    @OnClick({R.id.ll_jieshou,R.id.ll_send})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.ll_send:
                tv_jieshou_title.setTextColor(getResources().getColor(R.color.white));
                ll_jieshou_line.setBackgroundResource(R.color.white);
                tv_send_title.setTextColor(getResources().getColor(R.color.white));
                ll_send_line.setBackgroundResource(R.color.button_bb_gray);
                recyc_send_list.setVisibility(View.VISIBLE);
                recyc_jieshou_list.setVisibility(View.GONE);
                page_type = "发送";
                transferRecoderAdapter.clear();
                queryId = "0";
                sendDetail("1");
                break;
            case R.id.ll_jieshou:
                tv_send_title.setTextColor(getResources().getColor(R.color.white));
                ll_send_line.setBackgroundResource(R.color.white);
                tv_jieshou_title.setTextColor(getResources().getColor(R.color.white));
                ll_jieshou_line.setBackgroundResource(R.color.button_bb_gray);
                recyc_send_list.setVisibility(View.GONE);
                recyc_jieshou_list.setVisibility(View.VISIBLE);
                page_type = "接收";
                reciverAdapter.clear();
                queryId = "0";
                receiveDetail("1");
                break;
        }
    }

    private void sendDetail(String type) {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.sendDetail);
        requestParams.addParameter("USER_NAME", SPUtils.getString(SPUtils.username));
        requestParams.addParameter("QUERY_ID", queryId);
        requestParams.addParameter("TYPE", type);
        MXUtils.httpPost(requestParams,new CommonCallbackImp("MY - 转账记录",requestParams, (BaseActivity) getActivity()){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    List<TransferRecoderBean> beanList = JSONArray.parseArray(pd,TransferRecoderBean.class);
                    if (beanList.size()>0) {
                        queryId = beanList.get(beanList.size()-1).getID();
                    }
                    transferRecoderAdapter.addAll(beanList);
                    transferRecoderAdapter.notifyDataSetChanged();
                    if (transferRecoderAdapter.getData().size()>0) {
                        recyc_send_list.setVisibility(View.VISIBLE);
                        ll_no_data.setVisibility(View.GONE);
                    } else {
                        recyc_send_list.setVisibility(View.GONE);
                        ll_no_data.setVisibility(View.VISIBLE);
                    }
                } else {
                    ToastUtils.toastutils(message,getActivity());
                }

            }
        });

    }

    private void receiveDetail(String type) {
        RequestParams requestParams= FlowAPI.getRequestParams(FlowAPI.receiveDetail);
        requestParams.addParameter("USER_NAME", SPUtils.getString(SPUtils.username));
        requestParams.addParameter("QUERY_ID", queryId);
        requestParams.addParameter("TYPE", type);
        MXUtils.httpPost(requestParams,new CommonCallbackImp("MY - 接收记录",requestParams){
            @Override
            public void onSuccess(String data) {
                super.onSuccess(data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                String result = jsonObject.getString("code");
                String message = jsonObject.getString("message");
                if (result.equals(FlowAPI.SUCCEED)) {
                    String pd = jsonObject.getString("pd");
                    List<ReciverBean> beanList = JSONArray.parseArray(pd,ReciverBean.class);
                    if (beanList.size()>0) {
                        queryId = beanList.get(beanList.size()-1).getID();
                    }
                    reciverAdapter.addAll(beanList);
                    reciverAdapter.notifyDataSetChanged();
                    if (reciverAdapter.getData().size()>0) {
                        recyc_jieshou_list.setVisibility(View.VISIBLE);
                        ll_no_data.setVisibility(View.GONE);
                    } else {
                        recyc_jieshou_list.setVisibility(View.GONE);
                        ll_no_data.setVisibility(View.VISIBLE);
                    }
                } else {
                    ToastUtils.toastutils(message,getActivity());
                }

            }
        });

    }
}
