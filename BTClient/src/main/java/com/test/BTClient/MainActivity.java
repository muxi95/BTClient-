package com.test.BTClient;

import android.os.Bundle;
import android.app.Activity;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView; 
import java.util.ArrayList; 
import java.util.HashMap; 
import android.widget.AdapterView; 
import android.widget.AdapterView.OnItemClickListener; 
import android.widget.SimpleAdapter; 
import android.widget.Toast; 

public class MainActivity extends Activity {
	private Button btn1;
	private Button btn2;
	ListView myListView; 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        btn1 = (Button)findViewById(R.id.mainbtn);
       
        //setContentView(R.layout.main); 
        //生成ListView对象 
        myListView=(ListView)findViewById(R.id.list); 
        //创建ArrayList对象 并添加数据 
        ArrayList<HashMap<String,String>> myArrayList=new ArrayList<HashMap<String,String>>(); 
        for(int i=0;i<10;i++){ 
            HashMap<String, String> map = new HashMap<String, String>(); 
            map.put("itemTitle", "This Is Title "+i); 
            map.put("itemContent", "This Is Content "+i); 
            myArrayList.add(map); 
        } 
         
        //生成SimpleAdapter适配器对象 
        SimpleAdapter mySimpleAdapter=new SimpleAdapter(this, 
                myArrayList,//数据源 
                R.layout.adapter_item,//ListView内部数据展示形式的布局文件listitem.xml 
                new String[]{"itemTitle","itemContent"},//HashMap中的两个key值 itemTitle和itemContent 
                new int[]{R.id.itemTitle,R.id.itemContent});/*布局文件listitem.xml中组件的id   
                                                            布局文件的各组件分别映射到HashMap的各元素上，完成适配*/ 
         
        myListView.setAdapter(mySimpleAdapter); 
        myListView.setOnItemClickListener(new OnItemClickListener(){ 
            @Override 
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, 
                    long arg3) { 
                //获得选中项的HashMap对象 
                HashMap<String,String> map=(HashMap<String,String>)myListView.getItemAtPosition(arg2); 
                String title=map.get("itemTitle"); 
                String content=map.get("itemContent"); 
                Toast.makeText(getApplicationContext(),  
                        "你选择了第"+arg2+"个Item，itemTitle的值是："+title+"itemContent的值是:"+content, 
                        Toast.LENGTH_SHORT).show(); 
            } 
             
        }); 
        btn1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			
			}
		});
        
     
        
        		
    }
	





   
}
