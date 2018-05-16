package wb.com.cctm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wb.com.cctm.R;
import wb.com.cctm.base.BaseRecyclerViewAdapter;
import wb.com.cctm.base.BaseRecyclerViewHolder;
import wb.com.cctm.bean.AllReleaseBean;
import wb.com.cctm.bean.NoticeBean;
import wb.com.cctm.databinding.ItemAllReleaseBinding;

/**
 * Created by wb on 2018/4/22.
 */

public class AllReleaseAdapter extends BaseRecyclerViewAdapter<AllReleaseBean> {
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(viewGroup,R.layout.item_all_release);
    }
    private class ViewHolder extends BaseRecyclerViewHolder<AllReleaseBean,ItemAllReleaseBinding> {
        public ViewHolder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
        }
        @Override
        public void onBindViewHolder(AllReleaseBean object, int position) {
            binding.executePendingBindings();
            binding.tvBig.setText(object.getBIG_CURRENCY());
            binding.tvSmall.setText(object.getSMALL_CURRENCY());
            binding.tvJd.setText(object.getJD_CURRENCY());
            binding.tvTime.setText(object.getCREATE_TIME());
            binding.tvToday.setText(object.getCALCULATE_MONEY());
            binding.tvSmart.setText(object.getSTATIC_CURRENCY());

        }
    }
}
