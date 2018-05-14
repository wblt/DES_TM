package wb.com.cctm.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wb.com.cctm.R;
import wb.com.cctm.base.BaseRecyclerViewAdapter;
import wb.com.cctm.base.BaseRecyclerViewHolder;
import wb.com.cctm.bean.StepBean;
import wb.com.cctm.commons.utils.ImageLoader;
import wb.com.cctm.commons.utils.SPUtils;
import wb.com.cctm.databinding.ItemStepBinding;

/**
 * Created by wb on 2018/4/24.
 */

public class StepAdapter extends BaseRecyclerViewAdapter<StepBean> {
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent,R.layout.item_step);
    }

    private class ViewHolder extends BaseRecyclerViewHolder<StepBean,ItemStepBinding>{

        public ViewHolder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
        }

        @Override
        public void onBindViewHolder(StepBean object, int position) {
            binding.executePendingBindings();
            binding.tvStepNumber.setText(object.getUSER_STEP());
            binding.tvUsername.setText(SPUtils.getString(SPUtils.nick_name));
            binding.tvTime.setText(object.getCREATE_TIME());
            ImageLoader.load(SPUtils.getString(SPUtils.headimgpath),binding.ivImg);
        }

    }

}
