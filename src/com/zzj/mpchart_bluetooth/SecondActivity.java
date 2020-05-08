package com.zzj.mpchart_bluetooth;

import com.zzj.mpchart_bluetooth.R.id;

import android.R.integer;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;

public class SecondActivity extends Activity{
	private Button buttonsave;
	private EditText nameEditText;
	private EditText dateEditText;
	private EditText roasterEditText;
	private EditText noteEditText;
	private SeekBar seekBarsour;
	private SeekBar seekBarsweet;
	private SeekBar seekBarflavor;
	private SeekBar seekBarclean;
	private SeekBar seekBarbody;
	private int sour=0;
	private int flavor=0;
	private int body=0;
	private int sweet=0;
	private int clean=0;
	private int id=0;
	
	
	
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.second_activity);
	Intent intent =getIntent();
	id=intent.getIntExtra("id", 0);
	Log.i("id", id+"");
	findid();
	saveonclicklinstener savelistener=new saveonclicklinstener();
	buttonsave.setOnClickListener(savelistener);
	
	
}


private void findid() {
	// TODO Auto-generated method stub
	buttonsave=(Button) findViewById(R.id.button1save);
	nameEditText=(EditText) findViewById(R.id.editText1);
	dateEditText=(EditText) findViewById(R.id.editText2);
	roasterEditText=(EditText) findViewById(R.id.editText3);
	noteEditText=(EditText) findViewById(R.id.editText4);
	seekBarsour=(SeekBar) findViewById(R.id.seekBarsour);
	seekBarsweet=(SeekBar) findViewById(R.id.seekBarsweet);
	seekBarflavor=(SeekBar) findViewById(R.id.seekBarflavor);
	seekBarclean=(SeekBar) findViewById(R.id.seekBarclean);
	seekBarbody=(SeekBar) findViewById(R.id.seekBarbody);
	seekBarbody.setMax(10);
	seekBarclean.setMax(10);
	seekBarflavor.setMax(10);
	seekBarsour.setMax(10);
	seekBarsweet.setMax(10);
	seekbarlistener seek=new seekbarlistener();
	seekBarbody.setOnSeekBarChangeListener(seek);
	seekBarclean.setOnSeekBarChangeListener(seek);
	seekBarflavor.setOnSeekBarChangeListener(seek);
	seekBarsweet.setOnSeekBarChangeListener(seek);
	seekBarsour.setOnSeekBarChangeListener(seek);
}
private class saveonclicklinstener implements OnClickListener{
	private String name;
	private String roaster;
	private String date;
	private String note; 

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		beanopenhelper helper=new beanopenhelper(SecondActivity.this, "bean.db");
		SQLiteDatabase sqLiteDatabase=helper.getWritableDatabase();
		name=nameEditText.getText().toString();
		roaster=roasterEditText.getText().toString();
		date=dateEditText.getText().toString();
		note=noteEditText.getText().toString();
		Log.i("save", name+":"+roaster+":"+date+":"+note);
		Log.i("info", sour+","+sweet+","+flavor+","+clean+","+body);
		ContentValues values=new ContentValues();
		//实例化内容值 ContentValues values = new ContentValues();   
		//在values中添加内容   
		values.put("name",name);
		values.put("note",note);
		values.put("date",date);
		values.put("roaster",roaster);
		values.put("sour",sour);
		values.put("flavor",flavor);
		values.put("clean",clean);
		values.put("body",body);
		values.put("sweet",sweet);
		//修改条件   
		String whereClause = "_id=?";   
		//修改添加参数   
		String str=Integer.toString(id);
		String[] whereArgs={str};   
		//修改   
		sqLiteDatabase.update("roast",values,whereClause,whereArgs);
		Cursor cursor=sqLiteDatabase.rawQuery("select*from roast", null);
		String[] columnString=cursor.getColumnNames();
		if (cursor!=null) {
			if (cursor.moveToLast()) {
				for (String string : columnString) {
					Log.i("info", string+":"+cursor.getString(cursor.getColumnIndex(string)));
				}
			}
		}
	cursor.close();
	Intent intent=new Intent();
	intent.setClass(SecondActivity.this, ThirdActivity.class);
	startActivity(intent);
	}
	
}

private class seekbarlistener implements OnSeekBarChangeListener{

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		switch (seekBar.getId()) {
		case R.id.seekBarbody:
			body=progress;
			break;
		case R.id.seekBarclean:
			clean=progress;
			break;
		case R.id.seekBarflavor:
			flavor=progress;
			break;
		case R.id.seekBarsour:
			sour=progress;
			break;
		case R.id.seekBarsweet:
			sweet=progress;
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
}
