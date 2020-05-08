package com.zzj.mpchart_bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Entity;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder;
import com.github.mikephil.charting.charts.ScatterChart.ScatterShape;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;


public class MainActivity extends Activity {
	
	  private CombinedChart mChart;
	  private final int itemcount = 12;
	  private int id=0;
	  private int i=0; 
	  private int j=0; 
	  private int s=0; 
	  BluetoothAdapter adapter=BluetoothAdapter.getDefaultAdapter();
	  List<String> list=new ArrayList<String>();//用於dialog的listview设置在外面，不然每次bluetoothcastreceiver接收到信息后，都会重建，设备列表被重置
	  List<Float> list2=new ArrayList<Float>();//用於line線的y軸數值设置在外面，不然每次bluetoothcastreceiver接收到信息后，都会重建，设备列表被重置
	 // List<Float> listx=new ArrayList<Float>();//记录点的y坐标
	  //List<Float> listy=new ArrayList<Float>();//记录点的y坐标
	  private int x=0;
	  private float y=0;
	  
	  List<String> listdata=new ArrayList<String>();//记录点的y坐标的风门火力变化
	  ArrayAdapter<String> arrayAdapter;
	  LineDataSet set;
	  ArrayList<Entry> yValues ;
	  BluetoothSocket socket = null; 
	  BluetoothReceiver bluetoothReceiver;
	  AlertDialog.Builder builder;
	  LayoutInflater inflater;// 渲染器  
	  View customdialog2view;  
	  ListView listView1;
      CombinedData data;
      private Button buttonset;
      private Button buttonstart;
      private Button buttonsave;
      private Button buttonclear;
	  private TextView textView1;
	  private SeekBar seekBarfm;
	  private SeekBar seekBarfire;
	  ConnectedThread thread=null;
	  int READ=1;
	  AlertDialog opinionsDialog=null;
	  myhandle myhandle=new myhandle();
	  ArrayList<Entry> yscatterValues=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
	       getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	                WindowManager.LayoutParams.FLAG_FULLSCREEN);
	       setContentView(R.layout.activity_main);
	       AlertDialog.Builder builder = new AlertDialog.Builder(this);
	       LayoutInflater inflater = LayoutInflater.from(this);// 渲染器  
	 	  View customdialog2view = inflater.inflate(R.layout.customdialog2,  
	               null);  
	 	 
	 	 
	 	  listView1=(ListView) customdialog2view.findViewById(R.id.listView1);
	 	 arrayAdapter=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1, list);
	 	  listView1.setAdapter(arrayAdapter);
	       findidset();
	       ///////注册bluetoothreceiver//////////
	       IntentFilter intentFilter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
			bluetoothReceiver=new BluetoothReceiver();
			registerReceiver(bluetoothReceiver, intentFilter);
			////////////////////////////////////
			chooselistener listener=new chooselistener();
			 yscatterValues = new ArrayList<Entry>();
           listView1.setOnItemClickListener(listener);
           builder.setTitle("选择设备");  
           builder.setView(customdialog2view); 
           openbluetooth();
           adapter.startDiscovery();
           opinionsDialog = builder.create();  
           opinionsDialog.setCanceledOnTouchOutside(false);//使除了dialog以外的地方不能被点击  
           opinionsDialog.show();
	        mChart = (CombinedChart) findViewById(R.id.chart1);
	        setmchart();
	        // xAxis.setAvoidFirstLastClipping(true);
	         //xAxis.setSpaceBetweenLabels(1);
	        //xAxis.set
	        ////
	        data = new CombinedData(setxdate());//xValues加进去才有x轴的数据
	        data.setData(generateLineData());
	        data.setData(generateScatterData());
	        //data.notifyDataChanged();
//	        data.setData(generateBarData());
//	        data.setData(generateBubbleData());
//	         data.setData(generateCandleData());
	        mChart.setData(data);
	        
    }
private class seeklistener implements OnSeekBarChangeListener{

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		switch (seekBar.getId()) {
		case R.id.seekBar1:
			i=progress;
			break;
		case R.id.seekBar2:
			j=progress;
			break;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
	
	
}
/////////////////////////////////////////////////////////////////////
    private void findidset() {
		// TODO Auto-generated method stub
		textView1=(TextView) findViewById(R.id.listView1);
		seekBarfm=(SeekBar) findViewById(R.id.seekBar1);
		seekBarfire=(SeekBar) findViewById(R.id.seekBar2);
		seekBarfire.setMax(10);
		seekBarfm.setMax(10);
		buttonset=(Button) findViewById(R.id.buttonset);
		buttonstart=(Button) findViewById(R.id.buttonstart);
		buttonsave =(Button) findViewById(R.id.buttonsave);
		buttonclear=(Button) findViewById(R.id.buttonclear);
		seeklistener seeklistener=new seeklistener();
	    seekBarfm.setOnSeekBarChangeListener(seeklistener);
	    seekBarfire.setOnSeekBarChangeListener(seeklistener);
	    buttononclicklistener buttonlistener=new buttononclicklistener();
	    buttonstart.setOnClickListener(buttonlistener);
	    buttonset.setOnClickListener(buttonlistener);
	    buttonsave.setOnClickListener(buttonlistener);
	    buttonclear.setOnClickListener(buttonlistener);
	}
private class buttononclicklistener implements OnClickListener{

	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.buttonset:
			Log.i("info", "record");
			//list3.add(list2.get(list2.size()-1));
			//data.clearValues();
			StringBuilder builder =new StringBuilder();
			builder.append(x);
			builder.append(",");
			builder.append(y);
			builder.append(",");
			builder.append(i);
			builder.append(",");
			builder.append(j);
			String string=new String(builder);
			listdata.add(string);
			data.setData(generateScatterData());
			//data.setData(generateLineData());
	        //mChart.setData(data);
			//mChart.invalidate();
			mChart.setData(data);
			
			break;
		case R.id.buttonstart:
			s=1;
			break;
		case R.id.buttonsave:
			Savedate();
			Intent intent=new Intent();
			intent.setClass(MainActivity.this, SecondActivity.class);
			intent.putExtra("id", id);
			startActivity(intent);
			break;

		case R.id.buttonclear:
			
			list2.clear();
			//data.clearValues();
			data.setData(generateScatterData());
			mChart.setData(data);
			//mChart.clearValues();
			mChart.invalidate();
			Log.i("msg", "clear");
			break;
		}
	}

	private void Savedate() {
		// TODO Auto-generated method stub
		
		StringBuilder builder =new StringBuilder();
		for (int i = 0; i < list2.size(); i++) {
			builder.append(list2.get(i));
			builder.append("|");	
		}
		String str=builder.toString();
		
		StringBuilder builderdata =new StringBuilder();
		for (int i = 0; i < listdata.size(); i++) {
			builderdata.append(listdata.get(i));
			builderdata.append("|");	
		}
		String strdata=builderdata.toString();
		Log.i("msg", str);
		Log.i("msg", strdata);
		beanopenhelper helper=new beanopenhelper(MainActivity.this, "bean.db");
		SQLiteDatabase sqLiteDatabase=helper.getWritableDatabase();
		ContentValues values=new ContentValues();
		values.put("curves", str);
		values.put("changedata", strdata);
		values.put("name", "mybean");
		sqLiteDatabase.insert("roast", null, values);
		values.clear();
		//查询
		
		Cursor cursor=sqLiteDatabase.rawQuery("select*from roast", null);
		//String[] columnString=cursor.getColumnNames();
		if (cursor!=null) {
			
				String column =cursor.getColumnName(3);
				String column2 =cursor.getColumnName(4);
				String column3 =cursor.getColumnName(0);
				//Log.i("info", column);
				//String ydate =cursor.getString(cursor.getColumnIndex(column));
				//String[] ydatelist = ydate.split("\\|");//分割字符和"\|"效果一样
			/*while (cursor.moveToNext()) {
				id=cursor.getInt(cursor.getColumnIndex(column3));
				Log.i("id", ""+id);
				Log.i("info", column+":"+cursor.getString(cursor.getColumnIndex(column)));
				Log.i("info", column2+":"+cursor.getString(cursor.getColumnIndex(column2)));
			}*/
				//cursor.moveToNext()是遍历行数据
			
				if (cursor.moveToLast()) {//直接跳到最后一个
					id=cursor.getInt(cursor.getColumnIndex(column3));
					Log.i("id", ""+id);
					Log.i("info", column+":"+cursor.getString(cursor.getColumnIndex(column)));
					Log.i("info", column2+":"+cursor.getString(cursor.getColumnIndex(column2)));
				}
			
		}
		cursor.close();
	}


}
	private void openbluetooth() {
		// TODO Auto-generated method stub
		if (adapter!=null) {
			Log.i("info", "你有蓝牙适配器");
			if (!adapter.enable()) {
				Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivity(intent);
			}
			//搜索已配对的设备
			
			//Set<BluetoothDevice> device=adapter.getBondedDevices();
			//ListView (device);
			  
		}else {
			Log.i("info", "你没有蓝牙适配器");
		}
	}
    private class BluetoothReceiver extends BroadcastReceiver{

    	@Override
    	public void onReceive(Context context, Intent intent) {
    		// TODO Auto-generated method stub
    		String action=intent.getAction();
    		if (BluetoothDevice.ACTION_FOUND.equals(action)) {
    			BluetoothDevice bluetoothDevice=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
    			
    				Log.i("info","名字:"+bluetoothDevice.getName()+"地址:"+bluetoothDevice.getAddress());
    				StringBuilder builder =new StringBuilder();
    				builder.append(bluetoothDevice.getName());
    				builder.append("|");
    				builder.append(bluetoothDevice.getAddress());
    				list.add(builder.toString());
    				arrayAdapter.notifyDataSetChanged();//更新适配器，listview会自动更新
    				//arrayAdapter=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1, list);
    				//listView1.setAdapter(arrayAdapter);
    		}
    	}
    }
	private void setmchart() {
		// TODO Auto-generated method stub
        mChart.setDescription("");
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawGridBackground(false);
        mChart.setDrawBarShadow(false);
        mChart.setDragEnabled(true);
        //mChart.setScaleYEnabled(false);
        // draw bars behind lines,lines behind scatter
        mChart.setDrawOrder(new DrawOrder[] {
                DrawOrder.BAR, DrawOrder.BUBBLE, DrawOrder.CANDLE, DrawOrder.LINE, DrawOrder.SCATTER
        });
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setStartAtZero(false);
		rightAxis.setDrawGridLines(false);
		rightAxis.setLabelCount(15, false);
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setStartAtZero(false);
		leftAxis.setDrawGridLines(true);
		leftAxis.setLabelCount(15, false);
		//leftAxis.setAxisMinValue(50);
        ///
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelsToSkip(59);
	}

	private ArrayList<String> setxdate() {//给combinedDate方法传x轴的数据
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

	private ScatterData generateScatterData() {

        ScatterData d = new ScatterData();
        if (!list2.isEmpty()) {
        	//listy.add(list2.get(list2.size()-1));
        	//listx.add((float) (list2.size()-1));
        	y=list2.get(list2.size()-1);
        	x=list2.size()-1;
        	yscatterValues.add(new Entry(y,x));
		}else {
			yscatterValues.clear();
		}
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
    private class chooselistener implements OnItemClickListener{

    	@Override
    	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    		// TODO Auto-generated method stub
    		String str = list.get(arg2);
    		String[] values = str.split("\\|");//分割字符和"\|"效果一样
    		String address=values[1];//address在后面，所以取values[1]
    		Log.e("address",values[1]);
    		opinionsDialog.cancel();
    		BluetoothDevice device = adapter.getRemoteDevice(address);	//根据mac地址	
    		Method m;			//建立连接
    		try {
    			m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
    			socket = (BluetoothSocket) m.invoke(device, Integer.valueOf(1));
    		} catch (SecurityException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		} catch (NoSuchMethodException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		} catch (IllegalArgumentException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IllegalAccessException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (InvocationTargetException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		try {
    			//socket = device.createRfcommSocketToServiceRecord(uuid); //建立连接（该方法不能用)
    			adapter.cancelDiscovery();  
    			//取消搜索蓝牙设备
    			socket.connect(); 
    			setTitle("连接成功");
    			Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
    			
    		} catch (IOException e) {
    			e.printStackTrace();
    			setTitle("连接失败");//目前连接若失败会导致程序出现ANR
    		}
    		thread = new ConnectedThread(socket);  //开启通信的线程
    		thread.start();
    	}
    	
    }
private class ConnectedThread extends Thread {
		
		private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        //构造函数
        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            Log.i("info", "进入connect线程");
            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream(); //获取输入流
                tmpOut = socket.getOutputStream();  //获取输出流
            } catch (IOException e) { }
     
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
     
        public void run() {
        	byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()   
            // Keep listening to the InputStream until an exception occurs
            while (true) {        	
                try {                	
                    // Read from the InputStream            
                	 bytes = mmInStream.read(buffer); //bytes数组返回值，为buffer数组的长度
                     // Send the obtained bytes to the UI activity
                	 String str = new String(buffer);
                	 Log.i("msglength", ""+bytes);
                	 //temp = byteToInt2(buffer);   //用一个函数实现类型转化，从byte到int
                     myhandle.obtainMessage(READ, bytes, -1, buffer)
                             .sendToTarget();     //压入消息队列
                     
                } catch (Exception e) {
                	System.out.print("read error");
                    break;
                    
                }
            }
        } 
        
        public void cancel(){     
            try {     
                mmSocket.close();     
            } catch (IOException e) { }     
        }  
    }
    private class myhandle extends Handler{

    	@Override
    	public void handleMessage(Message msg) {
    		// TODO Auto-generated method stub
    		if (msg.what==READ) {
    		//	String str = (String)msg.obj;	//类型转化

    			String str=new String((byte[])msg.obj,0,msg.arg1);
    			 float j=Float.parseFloat(str);
    			Log.i("infoint",""+j);
    			//Log.i("infostr", str);
    			//textView.append(" "+str);	  //显示在画布下方的TextView中
    			
    			//textView1.setText(str);
    			if (s==1) {
    				list2.add(j);
        			//data.notifyDataChanged();
    				//mChart.clear();
    				//mChart.invalidate();
        	        data.setData(generateLineData());
        	        //mChart.set
        	        mChart.setData(data);
        	        
        			mChart.invalidate();
				}

    			//arrayAdapter=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1, list2);
    			//listView1.setAdapter(arrayAdapter);
    		}
    		super.handleMessage(msg);
    	}
    	
    }
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    	//if (thread.isAlive()) {
    		//thread.cancel();
		//}
    	
    }
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	
    	super.onDestroy();
    }
    
    ///////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
