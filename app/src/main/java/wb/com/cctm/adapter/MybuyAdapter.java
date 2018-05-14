package wb.com.cctm.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import wb.com.cctm.R;
import wb.com.cctm.base.BaseRecyclerViewAdapter;
import wb.com.cctm.base.BaseRecyclerViewHolder;
import wb.com.cctm.bean.MybuyBean;
import wb.com.cctm.databinding.ItemMyBuyBinding;

/**
 * Created by wb on 2018/4/30.
 */

public class MybuyAdapter extends BaseRecyclerViewAdapter<MybuyBean> {

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(viewGroup, R.layout.item_my_buy);
    }
    private class ViewHolder extends BaseRecyclerViewHolder<MybuyBean,ItemMyBuyBinding> {

        public ViewHolder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
        }

        @Override
        public void onBindViewHolder(final MybuyBean object, final int position) {
            binding.executePendingBindings();
            binding.tvNameB.setText(object.getUSER_NAME());
            binding.tvPrice.setText(object.getBUSINESS_PRICE());
            binding.tvTtNumber.setText(object.getBUSINESS_COUNT());
            binding.tvTime.setText(object.getCREATE_TIME());
            binding.tvAll.setText(object.getTOTAL_MONEY());
            if (object.getSTATUS().equals("0")) {
                binding.tvStatus.setText("待审核");
                //binding.tvBtnStatus.setVisibility(View.VISIBLE);
                //binding.tvBtnStatus.setText("可取消");
            } else if (object.getSTATUS().equals("1")) {
                binding.tvStatus.setText("审核通过");
                //binding.tvBtnStatus.setVisibility(View.VISIBLE);
//                binding.tvBtnStatus.setText("可取消");
            } else if (object.getSTATUS().equals("2")) {
                binding.tvStatus.setText("部分成交");
//                binding.tvBtnStatus.setVisibility(View.VISIBLE);
//                binding.tvBtnStatus.setText("可取消");
            } else if (object.getSTATUS().equals("3")) {
                binding.tvStatus.setText("待付款");
//                binding.tvBtnStatus.setVisibility(View.VISIBLE);
//                binding.tvBtnStatus.setText("确认付款");
            } else if (object.getSTATUS().equals("4")) {
                binding.tvStatus.setText("已付款");
//                binding.tvBtnStatus.setVisibility(View.VISIBLE);
//                binding.tvBtnStatus.setText("确认付款");
            } else if (object.getSTATUS().equals("5")) {
                binding.tvStatus.setText("已成交");
//                binding.tvBtnStatus.setVisibility(View.INVISIBLE);
            } else if (object.getSTATUS().equals("6")) {
                binding.tvStatus.setText("已取消");
//                binding.tvBtnStatus.setVisibility(View.INVISIBLE);
            } else {
                binding.tvStatus.setText("未知状态");
//                binding.tvBtnStatus.setVisibility(View.INVISIBLE);
            }
            binding.tvBtnStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onClick(object,view,position);
                    }
                }
            });

        }
    }


}
