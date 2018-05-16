package com.marichitech.ridestatistics;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;


public class ListAdapter extends BaseAdapter {

    Context context;
    private final String [] values;
    private final String [] numbers;
    private final int [] images;
    private  final float [] angle_values;
    private  final ILineDataSet [] datasets;

    public ListAdapter(Context context, String [] values, String [] numbers, float [] angle_values,ILineDataSet[] datasets, int [] images){
        //super(context, R.layout.single_list_app_item, utilsArrayList);
        this.context = context;
        this.values = values;
        this.numbers = numbers;
        this.images = images;
        this.angle_values = angle_values;
        this.datasets = datasets;
    }

    @Override
    public int getCount() {
        return values.length;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

         ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.aNametxt);
            viewHolder.txtVersion = (TextView) convertView.findViewById(R.id.aVersiontxt);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.appIconIV);

            viewHolder.chart2 = (LineChart) convertView.findViewById(R.id.chart1);
            viewHolder.chart2.getDescription().setEnabled(true);
            viewHolder.chart2.getDescription().setText("Real Time Data Plot");
            viewHolder.chart2.setTouchEnabled(false);
            viewHolder.chart2.setDragEnabled(false);
            viewHolder.chart2.setScaleEnabled(true);
            viewHolder.chart2.setPinchZoom(false);
            viewHolder.chart2.setBackgroundColor(Color.WHITE);
            LineData data = new LineData();
            data.setValueTextColor(Color.WHITE);
            viewHolder.chart2.setData(data);

            Legend l = viewHolder.chart2.getLegend();
            l.setForm(Legend.LegendForm.LINE);
            l.setTextColor(Color.WHITE);
            XAxis xl = viewHolder.chart2.getXAxis();
            xl.setDrawGridLines(true);
            xl.setTextColor(Color.WHITE);
            xl.setAvoidFirstLastClipping(true);
            xl.setEnabled(true);

            YAxis leftAxis = viewHolder.chart2.getAxisLeft();
            leftAxis.setTextColor(Color.WHITE);
            leftAxis.setDrawGridLines(false);
            leftAxis.setAxisMaximum(180f);
            leftAxis.setAxisMinimum(-180f);
            leftAxis.setDrawGridLines(true);

            YAxis rightAxis = viewHolder.chart2.getAxisRight();
            rightAxis.setEnabled(false);

            viewHolder.chart2.getAxisLeft().setDrawGridLines(false);
            viewHolder.chart2.getXAxis().setDrawGridLines(false);
            viewHolder.chart2.setDrawBorders(false);


            //startPlot();
            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)
        convertView.getTag();
        result=convertView;
    }

        viewHolder.txtName.setText(values[position]);
        viewHolder.txtVersion.setText("Version: "+numbers[position]);
        viewHolder.icon.setImageResource(images[position]);
        LineData data = viewHolder.chart2.getData();
        if(datasets != null){
            data.addDataSet(datasets[position]);
        }

        data.notifyDataChanged();
        viewHolder.chart2.notifyDataSetChanged();
        viewHolder.chart2.setMaxVisibleValueCount(150);
        viewHolder.chart2.moveViewToX(data.getEntryCount());
        return convertView;
    }

    private static class ViewHolder {

        TextView txtName;
        TextView txtVersion;
        ImageView icon;
        LineChart chart2;

    }

}
