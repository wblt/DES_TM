package wb.com.cctm.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import wb.com.cctm.R;
import wb.com.cctm.base.BaseFragment;


public class RecoderFragment extends BaseFragment {

    @BindView(R.id.top_left)
    ImageButton top_left;
    private Unbinder unbinder;
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

    private void initview(View view) {
        top_left.setVisibility(View.INVISIBLE);
        setTopBarTitle("记录");
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
