package com.test.BTClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bottle.stockmanage.DBUtil;
import com.scott.crash.NetWorkHelper;
import com.test.BTClient.db.DBHelperUser;
import com.test.BTClient.model.UserInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dev.sqlite.entity.LBHashMap;

public class LoginActivity extends Activity implements OnClickListener{
	 /** Called when the activity is first created. */
		private EditText uname = null;
		private EditText upswd = null;
		private CheckBox auto = null;
		private Button login = null;
		SharedPreferences sp = null;
		private DBUtil dbUtil;
		@SuppressLint("NewApi") 
		@Override
	    public void onCreate(Bundle savedInstanceState) {
			
			if (android.os.Build.VERSION.SDK_INT > 9) {
			    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			    StrictMode.setThreadPolicy(policy);
			}
			
			dbUtil = new DBUtil();
			
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.login);
	        sp = this.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
	        
	        ImageView image = (ImageView) findViewById(R.id.logo);             //使用ImageView显示logo
	        image.setImageResource(R.drawable.icon);
	        
	        init();
	        
	        
	    }
	    public void init() {
			UserInfo user3 = DBHelperUser.getUserInfo(LoginActivity.this,String.valueOf(1));
			if (user3 != null) {
				//转到主题页面
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, BTClient.class);

				Bundle bundle = new Bundle();
				String name = user3.getUserid();
				bundle.putString("userid", name);
				intent.putExtras(bundle);

				LoginActivity.this.startActivity(intent);
				finish();
			}
			else{
				uname = (EditText) findViewById(R.id.login_edit_account);
				upswd = (EditText) findViewById(R.id.login_edit_pwd);
				auto = (CheckBox) findViewById(R.id.Login_Remember);
				login = (Button) findViewById(R.id.login_btn_login);
				if (sp.getBoolean("auto", false)) {
					uname.setText(sp.getString("uname", null));
					upswd.setText(sp.getString("upswd", null));
					auto.setChecked(true);

				}
				login.setOnClickListener(this);
			}
		}
		@Override
		public void onClick(View v) {


			if(NetWorkHelper.isMobileDataEnable(this) ||
					NetWorkHelper.isNetworkRoaming(this) ||
					NetWorkHelper.isWifiDataEnable(this)){
			}
			else
			{
				Toast.makeText(LoginActivity.this, "网络无连接！", Toast.LENGTH_SHORT).show();
				return;
			}
			if (v == login){
				
				String name = uname.getText().toString();
			 	String pswd = upswd.getText().toString();
			 	if(name.trim().equals("")){
	    			Toast.makeText(this, "请您输入用户名！", Toast.LENGTH_SHORT).show();
					return;
	    		}
	    		if(pswd.trim().equals("")){
	    			Toast.makeText(this, "请您输入密码！", Toast.LENGTH_SHORT).show();
					return;
	    		}

				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
				String date = df.format(new Date());
				ArrayList<LBHashMap<String>> maxUserid = DBHelperUser.getMaxUserInfoid(LoginActivity.this);
				String id = maxUserid.get(0).get("MAX(id)");
				if (id == null) {
					id = "0";
				}
				int i = Integer.parseInt(id);
				i = i + 1;
				UserInfo user = new UserInfo(i, uname.getText().toString(), upswd.getText().toString(), date);
				DBHelperUser.addUserInfo(LoginActivity.this, user);

				//boolean autoLogin = auto.isChecked();
				String ID="0";
				

					ID = dbUtil.CheckUser(name.trim(), pswd.trim());


				if (!ID.equals("0"))
				{
						Editor editor = sp.edit();
						editor.putString("uname", name);
						editor.putString("upswd", pswd);
						editor.putBoolean("auto", true);
						editor.commit();
						Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();      
						//转到主题页面    	
						Intent intent = new Intent();
						intent.setClass(LoginActivity.this, BTClient.class);
						
						Bundle bundle = new Bundle(); 
						bundle.putString("userid", name); 
						intent.putExtras(bundle);
						
						LoginActivity.this.startActivity(intent);
						finish();
						//结束当前的Activity
						 //如果没有上面的finish()，那么当跳转到MainActivity之后，SecondActivity只会onStop,不会ondestroy。即仍然还在栈中
						//需要注意的是，当它跳到MainActivity时，会去重新创建一个新的MainActivity，即执行MainActivity中的onCreate()方法;
						/**
						 * Intent intent = new Intent(); 
						intent.setClass(A.this, B.class); 
						 *Bundle bundle = new Bundle(); 
						bundle.putString("name", "xiaozhu"); 
						intent.putExtras(bundle); 
						Intent intent = this.getIntent(); 
						Bundle bundle = intent.getExtras(); 
						String name = bundle.getString("name");
						 * */		
				}

				else
				{  
					Editor editor = sp.edit();
					editor.putString("uname", null);
					editor.putString("upswd", null);
					editor.putBoolean("auto", false);
					editor.commit();
					Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();      
				}
				
			}}

			// TODO Auto-generated method stub
}
