package com.limkee.dashboard;

import android.graphics.Color;
import android.graphics.Paint;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.limkee.R;

import java.util.ArrayList;

import static com.github.mikephil.charting.components.Legend.LegendPosition.BELOW_CHART_CENTER;
import static com.github.mikephil.charting.components.Legend.LegendPosition.BELOW_CHART_LEFT;
import static java.lang.Boolean.TRUE;

public class Chart {
    private HorizontalBarChart chart;
    private BarDataSet customerAmount;
    private BarDataSet averageAmount;
    private ArrayList<String> month;
    private ArrayList<String> chineseMth;

    public Chart(HorizontalBarChart chart){
        this.chart = chart;
        this.chart.setDoubleTapToZoomEnabled(false);
    }

    public void updateDataSet(String type, ArrayList<String> mth, ArrayList<Float> dataSet, String language){

        if (type.equals("customer")) {
            if (language.equals("Yes")) {
                customerAmount = new BarDataSet(getDataSet(dataSet), "My spendings");
                month = mth;
            } else {
                customerAmount = new BarDataSet(getDataSet(dataSet), "我的花费");
                chineseMth = new ArrayList<>();
                for (String engMth : mth) {
                    String chineseMonth = getChineseMonth(engMth);
                    chineseMth.add(chineseMonth);
                }
                month = chineseMth;
            }
            customerAmount.setColors(Color.parseColor("#A0C25A"));
            month = mth;
        } else {
            if (language.equals(("Yes"))) {
                averageAmount = new BarDataSet(getDataSet(dataSet), "Average spendings");
                month = mth;
            } else {
                averageAmount = new BarDataSet(getDataSet(dataSet), "平均花费");
                chineseMth = new ArrayList<>();
                for (String engMth : mth) {
                    String chineseMonth = getChineseMonth(engMth);
                    chineseMth.add(chineseMonth);
                }
                month = chineseMth;
            }

            averageAmount.setColors(Color.parseColor("#F78B5D"));
            averageAmount.setValueTextSize(15f);
        }
    }

    public void showChart(boolean isChecked, String language) {
        System.out.println("customer " + customerAmount);
        try {
            if (customerAmount != null && averageAmount != null) {
                IAxisValueFormatter axisFormatter = new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        return "" + ((int) value);
                    }
                };
                BarData data = null;
                if (!isChecked) {
                    data = new BarData(customerAmount, averageAmount);
                } else {
                    data = new BarData(customerAmount);
                }

                data.setValueTextSize(10f);
                data.setValueFormatter(new IValueFormatter(){
                    @Override
                    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                        return String.format("%.2f", (double)(Math.round(value*100)/100.0));
                    }
                });
                data.setBarWidth(0.25f);

                YAxis left = chart.getAxisLeft();
                left.setValueFormatter(axisFormatter);
                left.setGranularity(1f);
                left.setTextSize(15f);
                left.setAxisMinimum(0f);

                YAxis right = chart.getAxisRight();
                right.setDrawLabels(false);
                right.setDrawGridLines(false);

                // X-axis labels
                String[] values = month.toArray(new String[month.size()]);
                System.out.println("Items are " + values[0]);
                XAxis xAxis = chart.getXAxis();
                xAxis.setValueFormatter(new MyXAxisValueFormatter(values));
                xAxis.setGranularity(1f);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(false);
                xAxis.setTextSize(15f);
                xAxis.setAxisMaximum(month.size() - 0.5f);
                xAxis.setAxisMinimum(-0.5f);
                //xAxis.setLabelRotationAngle(-15);

                chart.setData(data);
                if (!isChecked) {
                    chart.groupBars(-0.5f, 0.5f, 0f); // available since release v3.0.0
                }

                Description description = new Description();
                description.setText("");
                chart.setDescription(description);

                Legend l = chart.getLegend();
                l.setFormSize(9f);
                l.setForm(Legend.LegendForm.CIRCLE);
                l.setTextSize(9f);
                l.setStackSpace(0.5F);
                l.setTextColor(Color.BLACK);
                l.setWordWrapEnabled(true);

                chart.animateY(1000);
                chart.invalidate();

                //chart.setVisibleYRangeMaximum(300, YAxis.AxisDependency.LEFT);
                chart.setVisibleXRangeMaximum(5);
                chart.setVisibleXRangeMinimum(5);

                chart.moveViewTo(month.size() - 1, 0, YAxis.AxisDependency.LEFT);
            } else {
                chart.setData(null);
                chart.invalidate();
                chart.setNoDataTextColor(R.color.colorAccent);
                Paint p = chart.getPaint(com.github.mikephil.charting.charts.Chart.PAINT_INFO);
                p.setTextSize(60);
                if (language.equals("Yes")) {
                    chart.setNoDataText("No data");

                } else {
                    chart.setNoDataText("没有数据");
                }
            }
        } catch (Exception e){
            chart.setData(null);
            chart.invalidate();
            chart.setNoDataTextColor(R.color.colorAccent);
            Paint p = chart.getPaint(com.github.mikephil.charting.charts.Chart.PAINT_INFO);
            p.setTextSize(60);
            if (language.equals("Yes")) {
                chart.setNoDataText("No data");
            } else {
                chart.setNoDataText("没有数据");
            }
            e.printStackTrace();
        }
    }

    private ArrayList<BarEntry> getDataSet(ArrayList<Float> floats) {
        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        for (int i = 0; i < floats.size(); i++) {
            BarEntry v1e1 = new BarEntry(i, floats.get(i));
            valueSet1.add(v1e1);
        }
        return valueSet1;
    }

    public void hide(String language){
        chart.setData(null);
        chart.invalidate();
        chart.setNoDataTextColor(R.color.colorAccent);
        Paint p = chart.getPaint(com.github.mikephil.charting.charts.Chart.PAINT_INFO);
        p.setTextSize(60);
        if (language.equals("Yes")) {
            chart.setNoDataText("No data");
        } else {
            chart.setNoDataText("没有数据");
        }
    }

    public void loading(){
        chart.setData(null);
        chart.invalidate();
        chart.setNoDataText("");
    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter {
        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            if (mValues.length == 0) {
                return "";
            } else {
                if ((int) value < mValues.length) {
                    return mValues[(int) value];
                }
                return "";
            }
        }
    }

    private String getChineseMonth(String engMonth) {

        String chineseMth = "";

        if (engMonth.equals("Jan")) {
            chineseMth = "一月";
        } else if (engMonth.equals("Feb")) {
            chineseMth = "二月";
        } else if (engMonth.equals("Mar")) {
            chineseMth = "三月";
        } else if (engMonth.equals("Apr")) {
            chineseMth = "四月";
        } else if (engMonth.equals("May")) {
            chineseMth = "五月";
        } else if (engMonth.equals("Jun")) {
            chineseMth = "六月";
        } else if (engMonth.equals("Jul")) {
            chineseMth = "七月";
        } else if (engMonth.equals("Aug")) {
            chineseMth = "八月";
        } else if (engMonth.equals("Sep")) {
            chineseMth = "九月";
        } else if (engMonth.equals("Oct")) {
            chineseMth = "十月";
        } else if (engMonth.equals("Nov")) {
            chineseMth = "十一月";
        } else if (engMonth.equals("Dec")) {
            chineseMth = "十二月";
        } else {
            //nothing
        }
        return chineseMth;
    }
}
