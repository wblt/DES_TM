package wb.com.cctm.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import wb.com.cctm.R;
import wb.com.cctm.base.BaseRecyclerViewAdapter;
import wb.com.cctm.base.BaseRecyclerViewHolder;
import wb.com.cctm.bean.MycheckBean;
import wb.com.cctm.databinding.ItemMyCheckBinding;

/**
 * Created by wb on 2018/5/1.
 */

public class ShengHeAdapter extends BaseRecyclerViewAdapter<MycheckBean> {

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(viewGroup, R.layout.item_my_check);
    }

    private class ViewHolder extends BaseRecyclerViewHolder<MycheckBean,ItemMyCheckBinding> {

        public ViewHolder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
        }
        @Override
        public void onBindViewHolder(final MycheckBean object, final int position) {
            binding.executePendingBindings();
            binding.tvNumber.setText(object.getBUSINESS_COUNT());
            binding.tvPrice.setText(object.getBUSINESS_PRICE());
            binding.tvAll.setText(object.getTOTAL_MONEY());
            binding.tvNameB.setText(object.getUSER_NAME_B());
            binding.tvTime.setText(object.getCREATE_TIME());
            if (object.getSTATUS().equals("0")) {
                binding.tvStatus.setText("待审核");
//                binding.tvStatus.setText("审核");
            } else if (object.getSTATUS().equals("1")) {
                binding.tvStatus.setText("审核通过");
//                binding.tvStatus.setText("可取消");
            } else if (object.getSTATUS().equals("2")) {
                binding.tvStatus.setText("部分成交");
//                binding.tvStatus.setText("可取消");
            } else if (object.getSTATUS().equals("3")) {
                binding.tvStatus.setText("待付款");
//                binding.tvStatus.setText("确认付款");
            } else if (object.getSTATUS().equals("4")) {
                binding.tvStatus.setText("已付款");
//                binding.tvStatus.setText("确认收款");
            } else if (object.getSTATUS().equals("5")) {
                binding.tvStatus.setText("已成交");
            } else if (object.getSTATUS().equals("6")) {
                binding.tvStatus.setText("已取消");
            } else {
                binding.tvStatus.setText("未知状态");
            }
            binding.llItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener!=null) {
                        listener.onClick(object,view,position);
                    }
                }
            });
        }
    }
}
