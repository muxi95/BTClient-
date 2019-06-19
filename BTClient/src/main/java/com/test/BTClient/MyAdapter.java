package com.test.BTClient;

import java.util.ArrayList;

import com.bottle.stockmanage.DBUtil;

import android.content.Context;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MyAdapter extends BaseAdapter{
	private Context context;
	private LayoutInflater inflater;
	private ArrayList<ArrayList<String>> lists;
	private DBUtil dbUtil;

	
	public MyAdapter(Context context, ArrayList<ArrayList<String>> lists) {
		super();
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}

		this.context = context;
		this.lists = lists;
		inflater = LayoutInflater.from(context);
		dbUtil = new DBUtil();
		 //新页面接收数据

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lists.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int index, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		//ArrayList<String> list = lists.get(index);
		Holder holder;
		if(view == null){
			holder = new Holder();
			view = inflater.inflate(R.layout.adapter_batchitem, null);
			//view.setBackgroundColor(Color.WHITE);
			holder.id = (TextView) view.findViewById(R.id.txt_Cbatchid);
			holder.xuhao = (TextView) view.findViewById(R.id.txt_xuhao);
			holder.batchname = (TextView) view.findViewById(R.id.txt_Cbatchname);
			holder.batchdes = (TextView) view.findViewById(R.id.txt_Cbatchdes);
			holder.itembtn = (Button) view.findViewById(R.id.item_btn);
			view.setTag(holder);			
		}
		else{
            holder = (Holder) view.getTag();
        }
		
		/*textView1.setTextColor(Color.BLACK);
		textView2.setTextColor(Color.BLACK);*/

		holder.id.setText(lists.get(index).get(0));
		holder.xuhao.setText(lists.get(index).get(1));
		holder.batchname.setText(lists.get(index).get(2));
		holder.batchdes.setText(lists.get(index).get(3));
		
		holder.itembtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	dbUtil.BatchSelect(lists.get(index).get(4), lists.get(index).get(2));
            	Toast.makeText(context, "批次选择成功！", Toast.LENGTH_SHORT).show();
            }
        });
		
		return view;
	}
	protected class Holder{
        TextView id;
        TextView xuhao;
        TextView batchname;
        TextView batchdes;
        Button itembtn;
    }
	

}
