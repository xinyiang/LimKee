package com.limkee.dashboard;

import android.graphics.Color;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;

public class Chart {
    private HorizontalBarChart chart;
    private BarDataSet customerAmount;
    private BarDataSet averageAmount;
    private ArrayList<String> month;

    public Chart(HorizontalBarChart chart){
        this.chart = chart;
        this.chart.setDoubleTapToZoomEnabled(false);
    }

    public void updateDataSet(String type, ArrayList<String> mth, ArrayList<Float> dataSet){
        if (type.equals("customer")){
            customerAmount = new BarDataSet(getDataSet(dataSet), "Total sales of current customer");
            customerAmount.setColors(Color.parseColor("#A0C25A"));
            customerAmount.setValueTextSize(15f);
            month = mth;
        }else{
            averageAmount = new BarDataSet(getDataSet(dataSet), "Total sales of all customers");
            averageAmount.setColors(Color.parseColor("#F78B5D"));
            averageAmount.setValueTextSize(15f);
            month = mth;
        }
    }

    public void showChart(boolean isChecked) {
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
                xAxis.setLabelRotationAngle(-15);

                chart.setData(data);
                if (!isChecked) {
                    chart.groupBars(-0.5f, 0.5f, 0f); // available since release v3.0.0
                }

                Description description = new Description();
                description.setText("");
                description.setTextSize(15);
                chart.setDescription(description);

                chart.getLegend().setEnabled(true);
                chart.getLegend().setTextSize(15f);
                chart.animateY(1000);
                chart.invalidate();

                //chart.setVisibleYRangeMaximum(300, YAxis.AxisDependency.LEFT);
                chart.setVisibleXRangeMaximum(5);
                chart.setVisibleXRangeMinimum(5);
                //chart.setViewPortOffsets(1f, 1f, 1f, 1f);
                chart.moveViewTo(month.size() - 1, 0, YAxis.AxisDependency.LEFT);

            }else {
                chart.setData(null);
                chart.invalidate();
            }
            //System.out.println("isChecked: "+isChecked);
            //System.out.println("avgAmount: "+ averageAmount.getLabel());
            //System.out.println("custAmount: "+ customerAmount.getLabel());
        } catch (Exception e){
            chart.setData(null);
            chart.invalidate();
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

}
