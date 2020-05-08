package com.zzj.mpchart_bluetooth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zzj.mpchart_bluetooth.R.id;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ThirdActivity extends Activity{

	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	private ListView listView;
	private Button button ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.third_activity);
		listView=(ListView) findViewById(R.id.listViewbean);
		button=(Button) findViewById(R.id.buttonadd);
		button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(ThirdActivity.this, MainActivity.class);
				startActivity(intent);
			}
			
		});
        SimpleAdapter adapter = new SimpleAdapter(this, getData(),
                R.layout.list, new String[] { "name", "note", "roaster" ,"date"},
                new int[] { R.id.textViewname, R.id.textViewroaster, R.id.textViewdate,R.id.textViewnote });
        listView.setAdapter(adapter);		
        listlistener listlistener=new listlistener();
        listView.setOnItemClickListener(listlistener);
        
	}

	private List<Map<String, Object>> getData() {
        
        Map<String, Object> map = new HashMap<String, Object>();
        beanopenhelper helper=new beanopenhelper(ThirdActivity.this, "bean.db");
		SQLiteDatabase sqLiteDatabase=helper.getWritableDatabase();
        
        String whereClause = "name=?";   
      //删除条件参数   
      String[] whereArgs = {"mybean"};   
      //执行删除   
      sqLiteDatabase.delete("roast",whereClause,whereArgs);   
      Cursor cursor=sqLiteDatabase.rawQuery("select*from roast", null);
		String[] columnString=cursor.getColumnNames();
		if (cursor!=null) {
			while (cursor.moveToNext()) {//从后往前
				map = new HashMap<String, Object>();
				map.put(columnString[1], cursor.getString(cursor.getColumnIndex(columnString[1])));
				map.put(columnString[0], cursor.getInt(cursor.getColumnIndex(columnString[0])));
					map.put(columnString[2], cursor.getString(cursor.getColumnIndex(columnString[2])));
					Log.i("info",columnString[2]+cursor.getString(cursor.getColumnIndex(columnString[2])));
					map.put(columnString[5], cursor.getString(cursor.getColumnIndex(columnString[5])));
					map.put(columnString[6], cursor.getString(cursor.getColumnIndex(columnString[6])));
				

				
				list.add(map);
			}
		}
	cursor.close();

        return list;
    }
	
private class listlistener implements OnItemClickListener{

	private int id;
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent intent =new Intent();
		Map<String, Object> map = list.get(arg2);
		id=(Integer) map.get("_id");
		intent.putExtra("id", id);
		intent.setClass(ThirdActivity.this, FourActivity.class);
		startActivity(intent);
	}
	
	
}	

}

