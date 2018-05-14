package wb.com.cctm.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wb.com.cctm.R;
import wb.com.cctm.base.BaseActivity;

public class TestActivity extends BaseActivity {

    @BindView(R.id.lineChart)
    LineChart lineChart;
    private List<String> mList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appendMainBody(this,R.layout.activity_test);
        appendTopBody(R.layout.activity_top_text);
        setTopLeftDefultListener();
        setTopBarTitle("测试");
        ButterKnife.bind(this);
        mList.add("04-22");
        mList.add("04-23");
        mList.add("04-24");
        mList.add("04-25");
        mList.add("04-26");
        mList.add("04-27");
        mList.add("04-28");
        initLineChart();
    }

    @OnClick({R.id.btn_refresh})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_refresh:
                //设置数据
                List<Entry> entries = new ArrayList<>();
                for (int i = 0; i < 7; i++) {
                    entries.add(new Entry(i, (float) (Math.random()) * 7));
                }
                notifyDataSetChanged(lineChart,entries);
                break;
        }
    }


    private void initLineChart() {
        //显示边界
        lineChart.setDrawBorders(true);
        // 轴
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(7, true);
        xAxis.setAxisMinimum(0f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mList.get((int) value); //mList为存有月份的集合
            }
        });
        // 左右y轴
        YAxis leftYAxis = lineChart.getAxisLeft();
        YAxis rightYAxis = lineChart.getAxisRight();
        leftYAxis.setAxisMinimum(0f);
        leftYAxis.setAxisMaximum(10f);
        rightYAxis.setAxisMinimum(0f);
        rightYAxis.setAxisMaximum(10f);
        rightYAxis.setTextColor(Color.WHITE); //文字颜色
        leftYAxis.setTextColor(Color.WHITE); //文字颜色

        // 描述
        Description description = new Description();
        description.setEnabled(false);
        lineChart.setDescription(description);

        // 手势
        lineChart.setDragEnabled(false);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setScaleEnabled(false);
        lineChart.setDragXEnabled(false);
        lineChart.setDragYEnabled(false);

        // 标签
        Legend legend = lineChart.getLegend();
        legend.setTextColor(Color.WHITE);

        //设置数据
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            entries.add(new Entry(i, (float) (Math.random()) * 7));
        }
        //一个LineDataSet就是一条线
        LineDataSet lineDataSet = new LineDataSet(entries, "价格走势图");
        LineData data = new LineData(lineDataSet);
        lineDataSet.setColor(Color.GREEN);
        lineDataSet.setValueTextColor(Color.GREEN);
        lineChart.setData(data);
    }

    /**
     * 更新图表
     *
     */
    public void notifyDataSetChanged(LineChart chart,List<Entry> values) {
        mList.clear();
        mList.add("03-22");
        mList.add("03-23");
        mList.add("03-24");
        mList.add("03-25");
        mList.add("03-26");
        mList.add("03-27");
        mList.add("03-28");
        chart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mList.get((int) value); //mList为存有月份的集合
            }
        });
        chart.invalidate();
        LineDataSet lineDataSet = (LineDataSet) chart.getData().getDataSetByIndex(0);
        lineDataSet.setValues(values);
        chart.getData().notifyDataChanged();
        chart.notifyDataSetChanged();
    }

}
