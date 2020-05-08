package com.zzj.mpchart_bluetooth;

import java.util.ArrayList;
import java.util.List;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder;
import com.github.mikephil.charting.charts.ScatterChart.ScatterShape;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;


import android.R.color;
import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FourActivity extends Activity{

	private TextView name;
	private TextView date;
	private TextView roaster;
	private TextView note;
	private CombinedChart chart;
	private RadarChart mChart;
	//private Typeface tf;
	private String curves=null;
	private String changepoint;
	private int id=0;
	private int x=0;
	  private float y=0;
	  private int sour=0;
	  private int flavor=0;
	  private int sweet=0;
	  private int body=0;
	  private int clean=0;
	  
	  ArrayList<Entry> yscatterValues=null;
	List<String> listdata=new ArrayList<String>();//记录点的y坐标的风门火力变化
	  ArrayAdapter<String> arrayAdapter;
	  LineDataSet set;
	  ArrayList<Entry> yValues ;
	  List<Float> list2=new ArrayList<Float>();//用於line線的y軸數值设置在外面，不然每次bluetoothcastreceiver接收到信息后，都会重建，设备列表被重置
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.four_activity);
		 yscatterValues = new ArrayList<Entry>();
		Intent intent =getIntent();
		id=intent.getIntExtra("id", 0);
		Log.i("id", id+"");
		findid();
		chart=(CombinedChart) findViewById(R.id.chart2);
		
		sqlread();
		
		if (!curves.isEmpty()) {
			getline();
			getchangepoint();
		}
			setmchart();
			CombinedData data = new CombinedData(setxdate());//xValues加进去才有x轴的数据
	        data.setData(generateLineData());
	        data.setData(generateScatterData());
	        //data.notifyDataChanged();
//	        data.setData(generateBarData());
//	        data.setData(generateBubbleData());
//	         data.setData(generateCandleData());
	        chart.setData(data);
	        //////////////////////////////////

	        mChart = (RadarChart) findViewById(R.id.chartradar);

	        //tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

	        mChart.setDescription("");

	        mChart.setWebLineWidth(1.5f);
	        mChart.setWebLineWidthInner(0.75f);
	        mChart.setWebAlpha(100);

	        // create a custom MarkerView (extend MarkerView) and specify the layout
	        // to use for it
	        //MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);

	        // set the marker to the chart
	        //mChart.setMarkerView(mv);

	       setData();

	        XAxis xAxis = mChart.getXAxis();
	        //xAxis.setTypeface(tf);
	        xAxis.setTextSize(9f);

	        YAxis yAxis = mChart.getYAxis();
	        //yAxis.setTypeface(tf);
	        yAxis.setLabelCount(5, false);
	        yAxis.setTextSize(9f);
	        yAxis.setStartAtZero(true);

	        Legend l = mChart.getLegend();
	        l.setPosition(LegendPosition.RIGHT_OF_CHART);
	        //l.setTypeface(tf);
	        l.setXEntrySpace(7f);
	        l.setYEntrySpace(5f);
		

	}
	 private String[] mParties = new String[] {
	            "sour", "sweet", "flavor", "clean", "body"
	    };
	private void setData() {
		// TODO Auto-generated method stub
		//float mult = 150;
        int cnt = 5;

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        //ArrayList<Entry> yVals2 = new ArrayList<Entry>();

        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.
        //for (int i = 0; i < cnt; i++) {
           // yVals1.add(new Entry((float) (Math.random() * mult) + mult / 2, i));
        //}
        yVals1.add(new Entry(sour, 1));
        yVals1.add(new Entry(sweet, 2));
        yVals1.add(new Entry(flavor, 3));
        yVals1.add(new Entry(clean, 4));
        yVals1.add(new Entry(body, 5));

        /*for (int i = 0; i < cnt; i++) {
            yVals2.add(new Entry((float) (Math.random() * mult) + mult / 2, i));
        }*/

        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < cnt; i++)
            xVals.add(mParties[i % mParties.length]);

        RadarDataSet set1 = new RadarDataSet(yVals1, "set_1");
        set1.setColor(ColorTemplate.VORDIPLOM_COLORS[4]);
        set1.setDrawFilled(true);
        set1.setLineWidth(2f);

        /*RadarDataSet set2 = new RadarDataSet(yVals2, "Set 2");
        set2.setColor(ColorTemplate.VORDIPLOM_COLORS[4]);
        set2.setDrawFilled(true);
        set2.setLineWidth(2f);*/

        ArrayList<RadarDataSet> sets = new ArrayList<RadarDataSet>();
        sets.add(set1);
        //sets.add(set2);

        RadarData data = new RadarData(xVals, sets);
        //data.setValueTypeface(tf);
        data.setValueTextSize(8f);
        data.setDrawValues(false);

        mChart.setData(data);

        mChart.invalidate();
    }
	

	private void getchangepoint() {
		// TODO Auto-generated method stub
		String[] values = changepoint.split("\\|");
		for (int i = 0; i < values.length; i++) {
			String[] values2 = values[i].split("\\,");
			y=new Float(values2[1]);
			x=new Integer(values2[0]);
			yscatterValues.add(new Entry(y, x));
		}
	}

	private void getline() {
		// TODO Auto-generated method stub
		String[] values = curves.split("\\|");
		for (int i = 0; i < values.length; i++) {
			list2.add(new Float(values[i]));
		}
	}

	private void sqlread() {
		// TODO Auto-generated method stub
		beanopenhelper helper=new beanopenhelper(FourActivity.this, "bean.db");
		SQLiteDatabase sqLiteDatabase=helper.getReadableDatabase();
		Cursor cursor=sqLiteDatabase.rawQuery("select*from roast", null);
		String[] columnString=cursor.getColumnNames();
		if (cursor!=null) {
			while (cursor.moveToNext()) {
				
				if (cursor.getInt(cursor.getColumnIndex(columnString[0]))==id) {
					name.setText(cursor.getString(cursor.getColumnIndex(columnString[1])));
					date.setText(cursor.getString(cursor.getColumnIndex(columnString[2])));
					roaster.setText(cursor.getString(cursor.getColumnIndex(columnString[5])));
					note.setText(cursor.getString(cursor.getColumnIndex(columnString[6])));
					curves=cursor.getString(cursor.getColumnIndex(columnString[3]));
					Log.i("curves", curves);
					changepoint=cursor.getString(cursor.getColumnIndex(columnString[4]));
					flavor=cursor.getInt(cursor.getColumnIndex(columnString[7]));
					body=cursor.getInt(cursor.getColumnIndex(columnString[8]));
					clean=cursor.getInt(cursor.getColumnIndex(columnString[9]));
					sour=cursor.getInt(cursor.getColumnIndex(columnString[10]));
					sweet=cursor.getInt(cursor.getColumnIndex(columnString[11]));
				}
				
					
				
			}
		}
	cursor.close();
	}

	private List<String> setxdate() {
		// TODO Auto-generated method stub
		ArrayList<String> xValues = new ArrayList<String>();
		int j = 0;
		int h = 0;
		for (int i = 0; i < 13 * 60; i++) {
			// xVals.add(aa[i]);
			j = i / 60;
			h = i % 60;
			if (j > 0) {
				if (h==0) {
					xValues.add(j + "分");
				}
				else {
					xValues.add(j + "分" + h + "秒");
				}
				
			} else {
				xValues.add(h + "秒");
			}
		}
        //String[] str = (String[])xValues.toArray(new String[xValues.size()]) ;
        return xValues;
	}

	private void setmchart() {
		// TODO Auto-generated method stub
		chart.setDescription("烘焙曲线");
        chart.setBackgroundColor(Color.argb(10, 00, 80, 255));
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setDragEnabled(true);
        //mChart.setScaleYEnabled(false);
        // draw bars behind lines,lines behind scatter
        chart.setDrawOrder(new DrawOrder[] {
                DrawOrder.BAR, DrawOrder.BUBBLE, DrawOrder.CANDLE, DrawOrder.LINE, DrawOrder.SCATTER
        });
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setStartAtZero(false);
		rightAxis.setDrawGridLines(false);
		rightAxis.setLabelCount(15, false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setStartAtZero(false);
		leftAxis.setDrawGridLines(true);
		leftAxis.setLabelCount(15, false);
		//leftAxis.setAxisMinValue(50);
        ///
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelsToSkip(59);
	}

	private void findid() {
		// TODO Auto-generated method stub
		name=(TextView) findViewById(R.id.name);
		date=(TextView) findViewById(R.id.date);
		roaster=(TextView) findViewById(R.id.roaster);
		note=(TextView) findViewById(R.id.note);
		
	}
	private ScatterData generateScatterData() {

        ScatterData d = new ScatterData();
        /*if (!list2.isEmpty()) {
        	//listy.add(list2.get(list2.size()-1));
        	//listx.add((float) (list2.size()-1));
        	y=list2.get(list2.size()-1);
        	x=list2.size()-1;
        	yscatterValues.add(new Entry(y,x));
		}else {
			yscatterValues.clear();
		}*/
        /*ArrayList<Entry> entries = new ArrayList<Entry>();
		
        for (int index = 0; index < itemcount; index++)
            entries.add(new Entry(getRandom(20, 15), index));
       
		for (int i = 0; i < bb.length; i++) {
			// yVals.add(new Entry(Float.parseFloat(bb[i]), i*60));
			// float value = (float) (Math.random() * range) + 3;
			yValues.add(new Entry(Float.parseFloat(bb[i]), i * 60));
		}*/
        Log.i("scatter", "in");
        ScatterDataSet set = new ScatterDataSet(yscatterValues, "变化点");
        //ScatterDataSet set = new ScatterDataSet(entries, "Scatter DataSet");
        set.setColor(Color.DKGRAY);
        set.setScatterShapeSize(8f);
        set.setValueTextSize(8f);
        set.setDrawValues(true);
        set.setScatterShape(ScatterShape.CROSS);
        set.setHighlightLineWidth(0.8f);
        
        d.addDataSet(set);
       // ArrayList<ScatterDataSet>scatterDataSets = new ArrayList<ScatterDataSet>();
		//scatterDataSets.add(set); // add the datasets
		// create a data object with the datasets
		//ScatterData scatterData = new ScatterData(xValues, scatterDataSets);
       // return scatterData;
        return d;
    }

    private LineData generateLineData() {
    	//String[] bb = { "140", "85", "90", "100", "112", "125", "130","142","154","162" };

        LineData d = new LineData();

       /* ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int index = 0; index < itemcount; index++)
            entries.add(new Entry(getRandom(15, 10), index));*/
         yValues = new ArrayList<Entry>();
		for (int i = 0; i < list2.size(); i++) {
			// yVals.add(new Entry(Float.parseFloat(bb[i]), i*60));
			// float value = (float) (Math.random() * range) + 3;
			yValues.add(new Entry(list2.get(i), i ));
			//set.addEntryOrdered(new Entry(list2.get(i), i ));
		}

		set = new LineDataSet(yValues, "temp");
        set.setColor(Color.rgb(104, 241, 175));
        set.setLineWidth(2f);
        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleSize(5f);
        set.setFillColor(Color.rgb(200, 200, 200));
        set.setDrawCubic(false);
        set.setCubicIntensity(0.15f);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));
        //
        set.setDrawCircles(false);
        set.setDrawValues(false);
        set.setDrawFilled(true);
        //set.setAxisDependency(YAxis.AxisDependency.LEFT);

       d.addDataSet(set);
        ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();
		lineDataSets.add(set); // add the datasets
		// create a data object with the datasets
		//LineData lineData = new LineData(xValues, lineDataSets);

        return d;
    }
}
