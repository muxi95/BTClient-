package com.test.BTClient;


import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.idulcimer.voicetest2.bean.VoiceBean;
import com.bottle.stockmanage.DBUtil;
import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.test.BTClient.db.DBHelperBatch;
import com.test.BTClient.db.DBHelperSample;
import com.test.BTClient.db.DBHelperUser;
import com.test.BTClient.db.SQLiteDataBaseTool;
import com.test.BTClient.model.Batch;
import com.test.BTClient.model.Sample;
import com.test.BTClient.model.UserInfo;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.litepal.util.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import dev.sqlite.LBSQLiteDatabase;
import dev.sqlite.entity.LBHashMap;
import jxl.Sheet;
import jxl.Workbook;

//import android.view.Menu;            //如使用菜单加入此三包
//import android.view.MenuInflater;
//import android.view.MenuItem;


public class BTClient extends Activity {

	private static final String TAG = "BTClient";
	Menu myMenu;
    final int MENU_LINK = Menu.FIRST;
    final int MENU_CREATE = Menu.FIRST+1;
    final int MENU_SELECT = Menu.FIRST+2;
	final int MENU_EXCLE = Menu.FIRST+3;
    final int MENU_OPEN = Menu.FIRST+4;
    final int MENU_SAVE = Menu.FIRST+5;
    final int MENU_UPDATE = Menu.FIRST+6;
    final int MENU_VRESION = Menu.FIRST+7;
    
	private final static int REQUEST_CONNECT_DEVICE = 1;    //宏定义查询设备句柄
	private final static int OPEN_EXCEL = 2;    //宏定义查询设备句柄

	private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";   //SPP服务UUID号
	
	private InputStream is;    //输入流，用来接收蓝牙数据
	//private TextView text0;    //提示栏解句柄
    private EditText edit0;    //发送数据输入句柄
    private TextView dis;       //接收数据显示句柄
    private ScrollView sv;      //翻页句柄
    private String smsg = "";    //显示用数据缓存
    private String fmsg = "";    //保存用数据缓存
	private StringBuffer mBuffer;
	private String finalResult="";
    /***************************************************/
    private DBUtil dbUtil;
    private ListView listView;
	private SimpleAdapter adapter;

	//private ArrayList<String> list = new ArrayList<String>();
	List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
	List<HashMap<String, String>> list_batch = new ArrayList<HashMap<String, String>>();
    private ArrayList<String> list1;

    private ArrayList<ArrayList<String>> lists_batch = new ArrayList<ArrayList<String>>();
	private String userid;
    private int scrolledY=0;
    private int position=0;
    
	private LayoutInflater inflater;
    private AlertDialog alertDialog;
    private boolean flag_link =false;
    private MenuItem mi1=null; 
    /*****************************************************/
    public String filename=""; //用来保存存储的文件名
    BluetoothDevice _device = null;     //蓝牙设备
    BluetoothSocket _socket = null;      //蓝牙通信socket
    boolean _discoveryFinished = false;    
    boolean bRun = true;
    boolean bThread = false;
    boolean flagFocus=true;
    private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();    //获取本地蓝牙适配器，即蓝牙设备
	
    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
        super.onCreate(savedInstanceState);
/*        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        setContentView(R.layout.main);   //设置画面为主画面 main.xml
		getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        //text0 = (TextView)findViewById(R.id.Text0);  //得到提示栏句柄
        edit0 = (EditText)findViewById(R.id.Edit0);   //得到输入框句柄
        sv = (ScrollView)findViewById(R.id.ScrollView01);  //得到翻页句柄
        dis = (TextView) findViewById(R.id.in);      //得到数据显示句柄

        /*************************************************************/
        dbUtil = new DBUtil();
        /************************************************************/
        
        /*************************************************************/

		UserInfo user3 = DBHelperUser.getUserInfo(BTClient.this,String.valueOf(1));
		if (user3 != null){
			userid = user3.getUserid();
		}
		else{
			//新页面接收数据
			Bundle bundle = this.getIntent().getExtras();
			//接收name值
			userid = bundle.getString("userid");
		}
		
		setListView(userid);
		
		/****************************************************************************/
       //如果打开本地蓝牙设备不成功，提示信息，结束程序
        if (_bluetooth == null){
        	Toast.makeText(this, "无法打开手机蓝牙，请确认手机是否有蓝牙功能！", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        // 设置设备可以被搜索  
       new Thread(){
    	   public void run(){
    		   if(_bluetooth.isEnabled()==false){
        		_bluetooth.enable();
    		   }
    	   }   	   
       }.start();   
       
   	//记录listView滚动到的位置的坐标，然后利用listView.scrollTo精确的进行恢复

		listView.setOnScrollListener(new OnScrollListener() {   
		  
		    /**  
		     * 滚动状态改变时调用  
		     */  
			 /** 
             *监听着ListView的滑动状态改变。官方的有三种状态SCROLL_STATE_TOUCH_SCROLL、SCROLL_STATE_FLING、SCROLL_STATE_IDLE： 
             * SCROLL_STATE_TOUCH_SCROLL：手指正拖着ListView滑动 
             * SCROLL_STATE_FLING：ListView正自由滑动 
             * SCROLL_STATE_IDLE：ListView滑动后静止 
             * */  
		    @Override  
		    public void onScrollStateChanged(AbsListView view, int scrollState) {   
		        // 不滚动时保存当前滚动到的位置   
		        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {   
		        	position = listView.getFirstVisiblePosition(); 
 
		        	if(list!=null)
		        	{
		        		 View v=listView.getChildAt(0);
		        		 scrolledY=(v==null)?0:v.getTop();
		        	}
		        }        
		                
		    }   
		    /**  
		     * 滚动时调用  
		     */ 
		    /** 
             * firstVisibleItem: 表示在屏幕中第一条显示的数据在adapter中的位置 
             * visibleItemCount：则表示屏幕中最后一条数据在adapter中的数据， 
             * totalItemCount则是在adapter中的总条数 
             * */  
		    @Override  
		    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {   
		    	if (firstVisibleItem == 0) {  
		            View firstVisibleItemView = listView.getChildAt(0);  
		            if (firstVisibleItemView != null && firstVisibleItemView.getTop() == 0) {  
		                //Log.d("ListView", "##### 滚动到顶部 #####"); 
		               /* Toast.makeText(getApplicationContext(), "已经滚动到顶部了！",
		                        Toast.LENGTH_SHORT).show();*/
		            }  
		        } else if ((firstVisibleItem + visibleItemCount) == totalItemCount) {  
		            View lastVisibleItemView = listView.getChildAt(listView.getChildCount() - 1);  
		            if (lastVisibleItemView != null && lastVisibleItemView.getBottom() == listView.getHeight()) {  
		                //Log.d("ListView", "##### 滚动到底部 ######");   
		                Toast.makeText(getApplicationContext(), "已经滚动到底部了！",
		                        Toast.LENGTH_SHORT).show();
		            }  
		        }  
		    }   
		});  
		/*//记录listView滚动到的位置的坐标，然后利用listView.scrollTo精确的进行恢复

		listView_batch.setOnScrollListener(new OnScrollListener() {   
		  
		    *//**  
		     * 滚动状态改变时调用  
		     *//*  
		    @Override  
		    public void onScrollStateChanged(AbsListView view, int scrollState) {   
		        // 不滚动时保存当前滚动到的位置   
			        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {   
			        	position = listView_batch.getFirstVisiblePosition(); 
	 
			        	if(list_batch!=null)
			        	{
			        		 View v=listView_batch.getChildAt(0);
			        		 scrolledY=(v==null)?0:v.getTop();
			        	}
			        }        
			                
			    }   
			    *//**  
		     * 滚动时调用  
		     *//*  
		    @Override  
		    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {   
	
		    }   
		});  
				*/
       listView.setOnItemClickListener(new OnItemClickListener(){
			 
           @Override
           public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                   long arg3) {
           	/** 
                * arg0 noteListView的指针，可以通过它获取listview所有信息 arg1 
                * 点击的item的view的指针，可以获取item的id arg2 item的位置 arg3 
                * item在listview中的第几行，通常与arg2相同 
                */  
           	 //HashMap<String, String> map = new HashMap<String, String>(); 
           	HashMap<String,String> map= (HashMap<String,String>)listView.getItemAtPosition(arg2);   
            String title=map.get("ID");  
            String xuhao=map.get("id");
            String rongqi=map.get("containername");
            String yangpin=map.get("samplename");
            String weight=map.get("weight");
            arg1.setBackgroundColor(Color.BLUE);
            setAddDialog(title,xuhao,rongqi,yangpin,weight);
           }
            
       });
       
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }
    //发送按键响应
    public void onSendButtonClicked(View v){
    	int i=0;
    	int n=0;
    	try{
    		OutputStream os = _socket.getOutputStream();   //蓝牙连接输出流
    		byte[] bos = edit0.getText().toString().getBytes();
    		for(i=0;i<bos.length;i++){
    			if(bos[i]==0x0a)n++;
    		}
    		byte[] bos_new = new byte[bos.length+n];
    		n=0;
    		for(i=0;i<bos.length;i++){ //手机中换行为0a,将其改为0d 0a后再发送
    			if(bos[i]==0x0a){
    				bos_new[n]=0x0d;
    				n++;
    				bos_new[n]=0x0a;
    			}else{
    				bos_new[n]=bos[i];
    			}
    			n++;
    		}
    		
    		os.write(bos_new);	
    	}catch(IOException e){  		
    	}  	
    }
    
    //接收活动结果，响应startActivityForResult()
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.out.println("RESULT_OKR111111");
		switch(requestCode){
			case OPEN_EXCEL:
				System.out.println("OPEN_EXCEL");
				if (data==null){
					break;
				}
				LogUtil.d(TAG, "选择的文件Uri = " + data.toString());
				//通过Uri获取真实路径
				final String excelPath = getRealFilePath(this, data.getData());
				LogUtil.d(TAG, "excelPath = " + excelPath);//    /storage/emulated/0/test.xls
				if (excelPath.contains(".xls") || excelPath.contains(".xlsx")) {
					LogUtil.d(TAG,"正在加载Excel中...");
					//载入excel
					 readExcel(excelPath);
				} else {
					LogUtil.d(TAG,"此文件不是excel格式");
				}
    	case REQUEST_CONNECT_DEVICE:     //连接结果，由DeviceListActivity设置返回
    		// 响应返回结果
            if (resultCode == Activity.RESULT_OK) {   //连接成功，由DeviceListActivity设置返回
                // MAC地址，由DeviceListActivity设置返回
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                // 得到蓝牙设备句柄      
                _device = _bluetooth.getRemoteDevice(address);
 
                // 用服务号得到socket
                try{
                	_socket = _device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                }catch(IOException e){
                	Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
                }
                //连接socket
            	Button btn = (Button) findViewById(R.id.Button_setting);
                try{
                	_socket.connect();
                	Toast.makeText(this, "连接"+_device.getName()+"成功！", Toast.LENGTH_SHORT).show();
                	btn.setText("断开");
                	mi1.setTitle("断开连接"); 
                	new Thread(new MyThread()).start(); 
                	
                }catch(IOException e){
                	try{
                		Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
                		_socket.close();
                		_socket = null;
                	}catch(IOException ee){
                		Toast.makeText(this, "连接失败！", Toast.LENGTH_SHORT).show();
                	}
                	
                	return;
                }
                
                //打开接收线程
                try{
            		is = _socket.getInputStream();   //得到蓝牙数据输入流
            		
            		//setListView("2005");
            		}catch(IOException e){
            			Toast.makeText(this, "接收数据失败！", Toast.LENGTH_SHORT).show();
            			return;
            		}
            		if(bThread==false){
            			ReadThread.start();
            			bThread=true;
            		}else{
            			bRun = true;
            		}  
            }
    		break;
    	default:break;
    	}
    }
    
    //接收数据线程
    Thread ReadThread=new Thread(){
    	
    	public void run(){
    		int num = 0;
    		byte[] buffer = new byte[1024];
    		byte[] buffer_new = new byte[1024];
    		int i = 0;
    		int n = 0;
    		bRun = true;
    		//接收线程
    		while(true){
    			try{
    				while(is.available()==0){
    					while(bRun == false){}
    				}
    				while(true){
    					fmsg="";
    					num = is.read(buffer);         //读入数据
    					n=0;
    					
    					String s0 = new String(buffer,0,num);
    					fmsg=s0;  
    					//fmsg+=s0;    //保存收到数据
    					for(i=0;i<num;i++){
    						if((buffer[i] == 0x0d)&&(buffer[i+1]==0x0a)){
    							buffer_new[n] = 0x0a;
    							i++;
    						}else{
    							buffer_new[n] = buffer[i];
    						}
    						n++;
    					}
    					String s = new String(buffer_new,0,n);
    					//smsg=s;
    					smsg+=s;   //写入接收缓存
    					if(is.available()==0)break;  //短时间没有数据才跳出进行显示
    				}
    				//发送显示消息，进行显示刷新  	
    				//
    	   			handler.sendMessage(handler.obtainMessage());  

    					/*try {
    	    				Thread.sleep(1000);
    					} catch (Exception e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
    					*/
    	    		}catch(IOException e){
    	    		}
 
    		}
    		
    		//handler.sendMessage(handler.obtainMessage());  
    		//handler.sendEmptyMessage(1);//发送消失到handler，通知主线程下载完成
    	}
    };
    

    //消息处理队列
	Handler handler= new Handler(){
    	public void handleMessage(Message msg){
    		super.handleMessage(msg);
    		//smsg为数据输出项
    		//smsg=smsg + "123";
    		//dis.setText(smsg);   //显示数据 
    		/********************************************************/
    		String read= smsg.trim();
			/*System.out.println("read:");
			System.out.println(read);*/
    		dis.setText(read);
 
    		/******************************************************************************/
    		//sv.scrollTo(0,dis.getMeasuredHeight()); //跳至数据最后一页
    	}
    };
    //关闭程序掉用处理部分
    public void onDestroy(){
    	super.onDestroy();
    	if(_socket!=null)  //关闭连接socket
    	try{
    		_socket.close();
    	}catch(IOException e){}
    //	_bluetooth.disable();  //关闭蓝牙服务
    }
    
    //菜单处理部分
  /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {//建立菜单
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }*/

  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) { //菜单响应函数
        switch (item.getItemId()) {
        case R.id.scan:
        	if(_bluetooth.isEnabled()==false){
        		Toast.makeText(this, "Open BT......", Toast.LENGTH_LONG).show();
        		return true;
        	}
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            return true;
        case R.id.quit:
            finish();
            return true;
        case R.id.clear:
        	smsg="";
        	ls.setText(smsg);
        	return true;
        case R.id.save:
        	Save();
        	return true;
        }
        return false;
    }*/
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		myMenu = menu;
		mi1 = myMenu.add(0, MENU_LINK, 0, "连接天平");	
		myMenu.add(0, MENU_CREATE, 0, "新建批次");	
		myMenu.add(0, MENU_SELECT, 0, "选择批次");
		myMenu.add(0, MENU_EXCLE, 0, "选择excle文件");
		myMenu.add(0, MENU_OPEN, 0, "打开网站");	
		myMenu.add(0, MENU_SAVE, 0, "保存数据");	
		myMenu.add(0, MENU_UPDATE, 0, "系统更新");
		myMenu.add(0, MENU_VRESION, 0, "系统版本");

		return super.onCreateOptionsMenu(myMenu);
	}	
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
        case MENU_LINK:
        	if(_bluetooth.isEnabled()==false){  //如果蓝牙服务不可用则提示
        		Toast.makeText(this, " 打开蓝牙中...", Toast.LENGTH_LONG).show();
        	}      	
            //如未连接设备则打开DeviceListActivity进行设备搜索
        	//Button btn = (Button) findViewById(R.id.Button_setting);
        	if(_socket==null){
        		Intent serverIntent = new Intent(this, DeviceListActivity.class); //跳转程序设置
        		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);  //设置返回宏定义
        	}
        	else{
        		 //关闭连接socket
        	    try{
        	    	
        	    	is.close();
        	    	_socket.close();
        	    	_socket = null;
        	    	bRun = false;
        	    	//btn.setText("连接");
        	    	mi1.setTitle("连接天平"); 
                    Toast.makeText(getApplicationContext(), "已经断开连接",
                            Toast.LENGTH_SHORT).show();

        	    }catch(IOException e){}   
        	}
        	//return;
        	//delay(3000);  //延时3秒
        /*	if(item.getTitle()=="连接天平")
                item.setTitle("断开天平");
        	else
                item.setTitle("连接天平");*/
        	break;
        case MENU_OPEN:
        	 Uri uri = Uri.parse("http://47.100.219.21");
        	 startActivity(new Intent(Intent.ACTION_VIEW,uri));  
        	break;
        case MENU_CREATE:
        	Batch_Create();
        	break;
        case MENU_SELECT:
        	BatchSelect();
        	break;
        case MENU_SAVE:
        	Save();
        	break;
        case MENU_UPDATE:
			UpdateManager manager = new UpdateManager(BTClient.this);
			manager.checkUpdate();
			break;
       	case MENU_VRESION:
       		default:  
       			version();
       			break;
	    case MENU_EXCLE:
				exlce();
				break;
        }
 
        return super.onOptionsItemSelected(item);
    }


	/**
	 * 打开excle文件
	 */
	private  void exlce(){

		System.out.println("excel");
		//导入格式为 .xls .xlsx
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");//设置类型
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try {
			startActivityForResult( Intent.createChooser(intent, "Select a File to Upload"),OPEN_EXCEL);
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
		}

	}
	//读取打开文件的路径
	public static String getRealFilePath(final Context context, final Uri uri) {
		if (null == uri) return null;
		final String scheme = uri.getScheme();
		String data = null;
		if (scheme == null)
			data = uri.getPath();
		else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
			data = uri.getPath();
		} else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
			Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
			if (null != cursor) {
				if (cursor.moveToFirst()) {
					int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
					if (index > -1) {
						data = cursor.getString(index);
					}
				}
				cursor.close();
			}
		}
		return data;
	}

	//读取Excel表
	private void readExcel(String excelPath) {
		try {
			InputStream input = new FileInputStream(new File(excelPath));
			POIFSFileSystem fs = new POIFSFileSystem(input);
			/*HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(0);*/
            Workbook book = Workbook.getWorkbook(new File(excelPath));
            Sheet sheet = book.getSheet(0);
            int Rows = sheet.getRows();
            int Cols = sheet.getColumns();
            System.out.println("当前工作表的名字:" + sheet.getName());
            System.out.println("总行数:" + Rows);
            System.out.println("总列数:" + Cols);
            for (int i = 0; i < Rows; ++i){
				ArrayList<LBHashMap<String>> maxSampleid = DBHelperSample.getMaxSampleid(BTClient.this);
				String id1= maxSampleid.get(0).get("MAX(id)");
				if(id1 == null){
					id1= "0";
				}
				int id = Integer.parseInt(id1);
				id=id+1;
				initDataInfo(id,sheet.getCell(0, i).getContents(), sheet.getCell(1, i).getContents(),
						sheet.getCell(2, i).getContents(), sheet.getCell(3, i).getContents());
            }

		} catch (Exception e) {
            System.out.println(e);
        }

		setListView(userid);
		setListView(userid);
		listView.setSelectionFromTop(position, scrolledY);

	}

	public void initDataInfo(int id,String userid, String containername,String samplename,String weight) {
		//LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(BTClient.this);
		Sample sa = new Sample();
		sa.setId(id);
		sa.setUserid(userid);
		sa.setContainername(containername);
		sa.setSamplename(samplename);
		sa.setWeight(weight);sa.setIsweight("1");
		//String where = "id = '" + id + "'";
		DBHelperSample.addSample(BTClient.this,sa);
	}





    /*
     * 延时函数
     */
    private void delay(int ms){  
        try {  
            Thread.currentThread();  
            Thread.sleep(ms);  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        }   
     }    
    private void version()
    {
    	final Dialog dialog = new Dialog(BTClient.this);
		dialog.setContentView(R.layout.version);
		dialog.setTitle("系统版本说明");
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp_version = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);
		dialogWindow.setAttributes(lp_version);
		Button btn_close =(Button) dialog.findViewById(R.id.btn_close);
		btn_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	dialog.dismiss();
            }
        });
		dialog.show();
    }
    /*
    //批次创建
     * *
     */
    private void Batch_Create()
    {
    	final Dialog dialog = new Dialog(BTClient.this);
		dialog.setContentView(R.layout.dialog_batchnew);
		dialog.setTitle("增加批次信息");
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp_batch_new = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);
		dialogWindow.setAttributes(lp_batch_new);

		final EditText cDesEditText = (EditText) dialog.findViewById(R.id.editbatchdes);       //批次描述
		final EditText cNameEditText = (EditText) dialog.findViewById(R.id.editbatchname);     //批次名称
		

		Button btnConfirm = (Button) dialog.findViewById(R.id.buttonok);
		Button btnCancel = (Button) dialog.findViewById(R.id.buttoncancel);

		btnConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					ArrayList<LBHashMap<String>> maxBatchid = DBHelperBatch.getMaxBatchid(BTClient.this);
					String id= maxBatchid.get(0).get("MAX(id)");
					if(id == null){
						id="0";
					}
					int i = Integer.parseInt(id);
					i=i+1;
					Batch batch = new Batch(i,userid,cNameEditText.getText().toString(),cDesEditText.getText().toString());
					DBHelperBatch.addBatch(BTClient.this,batch);
					//dbUtil.insertBatchNew(userid, cNameEditText.getText().toString());
					dialog.dismiss();
				//hideButton(false);
					Toast.makeText(BTClient.this, "成功增加批次", Toast.LENGTH_SHORT).show();
				}catch(Exception e){
					return;
				}	
				setListView(userid);
				listView.setSelectionFromTop(position, scrolledY);
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				//hideButton(false);
			}
		});

		dialog.show();
    }
    
	/**
	 * 选择批次,选择成功后，关闭对话框
	 * 
	 * @return
	 */
    private void BatchSelect()
    {
  	    	
		AlertDialog.Builder builder;
        //AlertDialog alertDialog;
        Context context = BTClient.this;
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.batch_main, (ViewGroup)findViewById(R.id.layout_myview));

		ListView listView_batch = (ListView) layout.findViewById(R.id.batch_list);
		Button btn_close =(Button) layout.findViewById(R.id.btn_close);
		
		//list_batch = dbUtil.getAllBatch_byUserid(userid);
		ArrayList<LBHashMap<String>> list_batch = DBHelperBatch.getAllBatch(BTClient.this,userid);

		LinearLayout la=(LinearLayout) layout.findViewById(R.id.layout_listview);
		LinearLayout.LayoutParams lp=(LinearLayout.LayoutParams)la.getLayoutParams();  
		if(list_batch.size()>6)
			lp.height=900;
		else
			lp.height=list_batch.size()*150;
		
		lists_batch.clear();

		for(int i=0;i<list_batch.size();i++)
		{
			HashMap<String,String> innerMap = list_batch.get(i);
			list1 = new ArrayList<String>();
			list1.add(innerMap.get("ID"));
			list1.add(innerMap.get("id"));
			list1.add(innerMap.get("batchname"));
			list1.add(innerMap.get("batchdes"));
			list1.add(userid);
			lists_batch.add(list1);
		}
        MyAdapter adapter = new MyAdapter(context, lists_batch);
		listView_batch.setAdapter(adapter);
		
		builder = new AlertDialog.Builder(context);
        builder.setView(layout);
        alertDialog = builder.create();
        alertDialog.show();
        
        btn_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	alertDialog.dismiss();
            }
        });
        
    }
    
    
    //连接按键响应函数
    public void onConnectButtonClicked(View v){ 
    	if(_bluetooth.isEnabled()==false){  //如果蓝牙服务不可用则提示
    		Toast.makeText(this, " 打开蓝牙中...", Toast.LENGTH_LONG).show();
    		return;
    	}
    	
    	
        //如未连接设备则打开DeviceListActivity进行设备搜索
    	Button btn = (Button) findViewById(R.id.Button03);
    	if(_socket==null){
    		Intent serverIntent = new Intent(this, DeviceListActivity.class); //跳转程序设置
    		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);  //设置返回宏定义
    	}
    	else{
    		 //关闭连接socket
    	    try{
    	    	
    	    	is.close();
    	    	_socket.close();
    	    	_socket = null;
    	    	bRun = false;
    	    	btn.setText("连接");
    	    }catch(IOException e){}   
    	}
    	return;
    }
    
    
    Handler myhandler = new Handler() {  
        public void handleMessage(Message msg) {  
            super.handleMessage(msg);  
            // 要做的事情  
        	
            if(dis.getText()!=null && !dis.getText().toString().equals("") && !dis.getText().toString().isEmpty())
        	{
            	try{
					ArrayList<LBHashMap<String>> maxSampleid1 = DBHelperSample.getMinSampleid(BTClient.this);
					String id1= maxSampleid1.get(0).getString("min(id)");
					int i = Integer.parseInt(id1);
					//i=i+1;
					Sample sample = new Sample(userid,i,
							dis.getText().toString(),"0");     //重量
					LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(BTClient.this);
					String where = "id = '" + i + "'";
					database.update(sample,where);
            		//dbUtil.insertSampleWeight(userid, "0", dis.getText().toString());
      
					Toast.makeText(BTClient.this, "成功添加数据", Toast.LENGTH_SHORT).show();
            		}catch(Exception e){
            			return;
            		}	
            	
            	 setListView(userid);
            	 setListView(userid);
 	    		 listView.setSelectionFromTop(position, scrolledY); 
        	}
        	dis.setText("");
        	smsg="";  
        }  
    };  
    public class MyThread implements Runnable {  
        @Override  
        public void run() {  
            // TODO Auto-generated method stub  
            while (true) {  
                try {  
                    Thread.sleep(2000);// 线程暂停1秒，单位毫秒  
                    Message message = new Message();  
                    message.what = 1;  
                    myhandler.sendMessage(message);// 发送消息  
                } catch (InterruptedException e) {  
                    // TODO Auto-generated catch block  
                    e.printStackTrace();  
                }  
            }  
        }  
    }  
    
    
    //保存按键响应函数
    public void onSaveButtonClicked(View v){
    	//Save();
    	setListView(userid);
    	listView.setSelectionFromTop(position, scrolledY); 
    	Toast.makeText(BTClient.this, "已经刷新！", Toast.LENGTH_SHORT).show();
    	/*
    	new Thread(new MyThread()).start(); 
    	Button btn = (Button) findViewById(R.id.Button04);
    	btn.setText("已经开始");
    	btn.setSelected(false);
		*/
    }
	RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
		//听写结果回调接口(返回Json格式结果，用户可参见附录13.1)；
		//一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
		//关于解析Json的代码可参见Demo中JsonParser类；
		//isLast等于true时会话结束。
		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			String result = results.getResultString();//语音听写的结果

			String resultString = processData(result);

			mBuffer.append(resultString);

			if (isLast) {
				//话已经说完了
				finalResult = mBuffer.toString();
				System.out.println("解析结果:" + finalResult);
				finalResult=finalResult.substring(0,finalResult.trim().length()-1);
				try{
					double num=Double.parseDouble(finalResult);
					VoiceAddWeight( String.valueOf(num));
				}catch(Exception e){
					Toast.makeText(BTClient.this, "请说数字！", Toast.LENGTH_SHORT).show();
				}
				//tv.setText(finalResult);
			}

		}

		@Override
		public void onError(SpeechError error) {

		}
	};

	//解析json
	protected String processData(String result) {
		Gson gson = new Gson();
		VoiceBean voiceBean = gson.fromJson(result, VoiceBean.class);

		StringBuffer sb = new StringBuffer();

		ArrayList<VoiceBean.WsBean> ws = voiceBean.ws;
		for (VoiceBean.WsBean wsBean : ws) {
			String word = wsBean.cw.get(0).w;
			sb.append(word);
		}

		return sb.toString();
	}
	public void VoiceAddWeight(String finalResult){
		String voiceWeight=finalResult;
		if(voiceWeight!=null && !voiceWeight.trim().equals("") && !voiceWeight.trim().isEmpty())
		{
			try{
				voiceWeight=voiceWeight + "g";//substring(0.voiceWeight.trim().length()-1);
                ArrayList<LBHashMap<String>> maxSampleid1 = DBHelperSample.getMinSampleid(BTClient.this);
                String id1= maxSampleid1.get(0).getString("min(id)");
                int i = Integer.parseInt(id1);
                //i=i+1;
                Sample sample = new Sample(userid,i,
						voiceWeight,"0");     //重量
                LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(BTClient.this);
                String where = "id = '" + i + "'";
                database.update(sample,where);
				//dbUtil.insertSampleWeight(userid, "0", voiceWeight);
				voiceWeight="";
				Toast.makeText(BTClient.this, "成功添加数据", Toast.LENGTH_SHORT).show();
			}catch(Exception e){
				return;
			}

			setListView(userid);
			setListView(userid);
			listView.setSelectionFromTop(position, scrolledY);
		}
	}

	//语音响应函数
	public void onVoiceButtonClicked(View v){
	//	Toast.makeText(BTClient.this, "成功添加数据", Toast.LENGTH_SHORT).show();
	//Intent intent=new Intent(BTClient.this, Voice.class);
	//	startActivity(intent);
	//finish();
		//1.创建RecognizerDialog对象
		RecognizerDialog mDialog = new RecognizerDialog(this, null);
		//2.设置accent、 language等参数
		mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
		//若要将UI控件用于语义理解，必须添加以下参数设置，设置之后onResult回调返回将是语义理解结果
		// mDialog.setParameter("asr_sch", "1");
		// mDialog.setParameter("nlp_version", "2.0");

		mBuffer = new StringBuffer();
		//3.设置回调接口
		mDialog.setListener(mRecognizerDialogListener);
		//4.显示dialog，接收语音输入
		mDialog.show();
		//Toast.makeText(BTClient.this, finalResult, Toast.LENGTH_SHORT).show();

	}
    
    //清除按键响应函数
    public void onClearButtonClicked(View v){
    	Save();
    	/*
    	smsg="";
    	fmsg="";
    	dis.setText(smsg);
    	return;*/
    }
    
    //退出按键响应函数
    public void onQuitButtonClicked(View v){
    	
    	//finish();
    	new AlertDialog.Builder(this).setTitle("确认退出吗？") 
    	.setIcon(android.R.drawable.ic_dialog_info) 
        .setPositiveButton("确定", new DialogInterface.OnClickListener() { 
     
            @Override 
            public void onClick(DialogInterface dialog, int which) { 
            // 点击“确认”后的操作
				LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(BTClient.this);
				String where = "userid = '" + userid + "'";
				database.delete(UserInfo.class,where);
            	BTClient.this.finish(); 
     
            } 
        }) 
        .setNegativeButton("返回", new DialogInterface.OnClickListener() { 
     
            @Override 
            public void onClick(DialogInterface dialog, int which) { 
            // 点击“返回”后的操作,这里不设置没有任何操作 
            } 
        }).show(); 
    }
    
  //追加按键响应函数
    public void onApendButtonClicked(View v){
    	final Dialog dialog = new Dialog(BTClient.this);
		dialog.setContentView(R.layout.dialog_append);
		dialog.setTitle("追加");
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp1 = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);
		dialogWindow.setAttributes(lp1);

		final EditText cNameEditText = (EditText) dialog.findViewById(R.id.editText1);//追加样品数量
		final EditText cNumEditText = (EditText) dialog.findViewById(R.id.editText2);//容器起始
		final EditText cgetEditText = (EditText) dialog.findViewById(R.id.editText3);//容器前缀
		final EditText csam1EditText = (EditText) dialog.findViewById(R.id.editText4);//样品起始
		final EditText csam2EditText = (EditText) dialog.findViewById(R.id.editText5);//样品前缀
		Button btnConfirm = (Button) dialog.findViewById(R.id.button1);
		Button btnCancel = (Button) dialog.findViewById(R.id.button2);
		

		btnConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(cNameEditText.getText().toString().trim().isEmpty()
						||cNumEditText.getText().toString().trim().isEmpty()
						||cgetEditText.getText().toString().trim().isEmpty()
						||csam1EditText.getText().toString().trim().isEmpty()
						||csam2EditText.getText().toString().trim().isEmpty())
				{
					Toast.makeText(BTClient.this, "所有内容不能为空！", Toast.LENGTH_SHORT).show();
					return;
				}
				try{
					/*ArrayList<LBHashMap<String>> maxSampleid1 = DBHelperSample.getMinSampleid(BTClient.this);
					String id1= maxSampleid1.get(0).getString("min(id)");
					int m = Integer.parseInt(id1);*/

					ArrayList<LBHashMap<String>> maxSampleid = DBHelperSample.getMaxSampleid(BTClient.this);
					String id= maxSampleid.get(0).get("MAX(id)");
					if(id == null){
						id = "0";
					}
					int i = Integer.parseInt(id);
					i=i+1;
					String a = cNameEditText.getText().toString(); //样品数量
					String b = cNumEditText.getText().toString(); //容器起始
					String c = cgetEditText.getText().toString();//容器前缀
					String d = csam1EditText.getText().toString();//样品起始
					String e =  csam2EditText.getText().toString();//样品前缀
					int a1 = Integer.parseInt(a);
					int b1 = Integer.parseInt(b);
					int d1 = Integer.parseInt(d);
					for(int x=i;x<i+a1;x++){
						String b2=Integer.toString(b1);
						b1++;
						String rq = c+b2;
						String d2 = Integer.toString(d1);
						d1++;
						String yp = e+d2;
						Sample sa = new Sample(x,userid,rq,yp,"准备","1");
						DBHelperSample.addSample(BTClient.this,sa);
					}
					/*dbUtil.sampleappend(userid,
							cNameEditText.getText().toString(), 
							cNumEditText.getText().toString(),
							csam1EditText.getText().toString(),
							cgetEditText.getText().toString(),
							csam2EditText.getText().toString());*/
					dialog.dismiss();
				//hideButton(false);
					Toast.makeText(BTClient.this, "样品追加成功！", Toast.LENGTH_SHORT).show();
				}catch(Exception e){
					return;
				}	
				setListView(userid);
				setListView(userid);
				listView.setSelectionFromTop(position, scrolledY);
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				//hideButton(false);
			}
		});
		
		dialog.show();
    }
    
    
  //插入按键响应函数
    public void onInsertButtonClicked(View v){
    	final Dialog dialog = new Dialog(BTClient.this);
		dialog.setContentView(R.layout.dialog_insert);
		dialog.setTitle("插入");
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp1 = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);
		dialogWindow.setAttributes(lp1);

		final EditText cNameEditText = (EditText) dialog.findViewById(R.id.editText1);//几号之后
		final EditText cNumEditText = (EditText) dialog.findViewById(R.id.editText2);//容器名
		final EditText cgetEditText = (EditText) dialog.findViewById(R.id.editText3);//样品名

		Button btnConfirm = (Button) dialog.findViewById(R.id.button1);
		Button btnCancel = (Button) dialog.findViewById(R.id.button2);
		

		btnConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(cNameEditText.getText().toString().isEmpty()
						||cNumEditText.getText().toString().isEmpty()
						||cgetEditText.getText().toString().isEmpty())
				{
					Toast.makeText(BTClient.this, "所有内容不能为空！", Toast.LENGTH_SHORT).show();
					return;
				}
				try{
					/*ArrayList<LBHashMap<String>> maxSampleid = DBHelperSample.getMaxSampleid(UARTLoopbackActivity.this);
                    String id= maxSampleid.get(0).get("MAX(id)");
                    int i = Integer.parseInt(id);
                    i=i+1;*/

					String a=cNameEditText.getText().toString();
					int i = Integer.parseInt(a); //插入到i号之后
					int j=i+1;
                    /*Sample sa1 = DBHelperSample.getSample(UARTLoopbackActivity.this,String.valueOf(i));
                    if(sa1 != null){
                        i++;
                    }
                    int x=i;

                    LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(UARTLoopbackActivity.this);
                   for(int y=i;y<=j+1;y--){
                       Sample sa = DBHelperSample.getSample(UARTLoopbackActivity.this,String.valueOf(x-1));
                       x=x-1;
                       String where = "id = '" + x + "'";
                       sa.setId(i);
                       i--;
                       database.update(sa,where);
                   }*/

					Sample sample = new Sample(j,userid,
							//cNameEditText.getText().toString(),     //几号之后
							cNumEditText.getText().toString(),      //容器名
							cgetEditText.getText().toString(),"准备","1");     //样品名
                    /*LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(UARTLoopbackActivity.this);
                    String where = "id = '" + j + "'";
                    database.update(sample,where);*/
					DBHelperSample.addSample(BTClient.this,sample);
					/*dbUtil.sampleinsert(userid,
							cNameEditText.getText().toString(), 
							cNumEditText.getText().toString(),
							cgetEditText.getText().toString());*/
					dialog.dismiss();
				//hideButton(false);
					Toast.makeText(BTClient.this, "样品插入成功！", Toast.LENGTH_SHORT).show();
				}catch(Exception e){
					return;
				}	
				setListView(userid);
				setListView(userid);
				listView.setSelectionFromTop(position, scrolledY);
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				//hideButton(false);
			}
		});
		
		dialog.show();
    }
    
  //设置按键响应函数
    public void onSettingButtonClicked(View v){
    	if(_bluetooth.isEnabled()==false){  //如果蓝牙服务不可用则提示
    		Toast.makeText(this, " 打开蓝牙中...", Toast.LENGTH_LONG).show();
    		return;
    	}
    	
    	
        //如未连接设备则打开DeviceListActivity进行设备搜索
    	Button btn = (Button) findViewById(R.id.Button_setting);
    	if(_socket==null){
    		Intent serverIntent = new Intent(this, DeviceListActivity.class); //跳转程序设置
    		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);  //设置返回宏定义
    	}
    	else{
    		 //关闭连接socket
    	    try{
    	    	
    	    	is.close();
    	    	_socket.close();
    	    	_socket = null;
    	    	bRun = false;
    	    	btn.setText("连接");
    	    }catch(IOException e){}   
    	}
    	return;
    }
    /** 
     * 设置listView 
     */ 
	private void setListView(String userid) {

		listView = (ListView) findViewById(R.id.list);
		//listView.setEmptyView(findViewById(R.id.noSmsData));
		//list = dbUtil.getAllInfo(userid);
		final ArrayList<LBHashMap<String>> list = DBHelperSample.getSampleX(BTClient.this,userid);
		int number=0;
		for(int i=0;i<list.size();i++)
		{
			HashMap<String,String> innerMap = list.get(i);
			if(innerMap.get("isweight").equals("1"))
			{
				number++;
				break;
			}

		}
		if(number==0)
		{
			Toast.makeText(BTClient.this, "称样结束，暂无样品等待称样！", Toast.LENGTH_SHORT).show();
		}
        //flagFocus=true;
		adapter = new SimpleAdapter(
				BTClient.this, 
				list, 
				R.layout.adapter_item, 
				new String[] { "ID", "id", "containername",  "samplename" ,"weight"},
				new int[] { R.id.txt_Cno, R.id.txt_xuhao, R.id.txt_Cname, R.id.txt_Cnum ,R.id.txt_Weight}){
				 @Override  
		         public View getView(int position, View convertView, ViewGroup parent) {  
					 if(null == convertView){  
					        Context context = BTClient.this;
				            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
				            convertView = inflater.inflate(R.layout.adapter_item,null);  
				        }  
					 
		             View view = super.getView(position, convertView, parent);

					 String isFocus = list.get(position).get("isweight");
		             if(isFocus.equals("1") ){  
		                 view.setBackgroundColor(Color.parseColor("#C0C0C0")); 
		                 //flagFocus=false;
		             }else{  
		                 view.setBackgroundColor(Color.WHITE);  
		             }
		             return view;  
		         }  

		     };  
		listView.setAdapter(adapter);
	}
	 /** 
     * 设置弹出添加对话框 
     */ 
	private void setAddDialog(String ID,final String xuhao,String rongqi,String yangpin,String weight) {

		final Dialog dialog = new Dialog(BTClient.this);
		dialog.setContentView(R.layout.dialog_add);
		dialog.setTitle("修改或设置样品信息");
		
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);
		dialogWindow.setAttributes(lp);

		final TextView cNameEditText = (TextView) dialog.findViewById(R.id.editText1);//序号
		
		final EditText cNumEditText = (EditText) dialog.findViewById(R.id.editText2);//重量
		final EditText cgetEditText = (EditText) dialog.findViewById(R.id.editText3);//容器名hide
		final EditText cgetYPEditText = (EditText) dialog.findViewById(R.id.editText4);//容器名
		final EditText cgetSamEditText = (EditText) dialog.findViewById(R.id.editText5);//样品名
		
		Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);
		Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
		Button btnDelay = (Button) dialog.findViewById(R.id.btn_delay);
		Button btnContinue = (Button) dialog.findViewById(R.id.btn_continue);
		Button btnDelete = (Button) dialog.findViewById(R.id.btn_delete);
		
		cNameEditText.setText(xuhao);
		cgetEditText.setText(ID);
		cgetYPEditText.setText(rongqi);
		cgetSamEditText.setText(yangpin);
		cNumEditText.setText(weight);
		btnConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(cgetYPEditText.getText().toString().isEmpty()
						||cgetSamEditText.getText().toString().isEmpty()
						||cNumEditText.getText().toString().isEmpty())
				{
					Toast.makeText(BTClient.this, "所有内容不能为空！", Toast.LENGTH_SHORT).show();
					return;
				}
				try{
					/* ArrayList<LBHashMap<String>> maxSampleid = DBHelperSample.getMaxSampleid(UARTLoopbackActivity.this);
                    String id= maxSampleid.get(0).get("MAX(id)");*/


					int i = Integer.parseInt(xuhao);
					//i=i+1;
					Sample sample = new Sample(i,userid,
							cgetEditText.getText().toString(),//容器名hide??? 这里是多的一个容器名，在这里用hide代替
							cgetYPEditText.getText().toString(),//容器名
							cgetSamEditText.getText().toString(),      //样品名
							cNumEditText.getText().toString(),"0");     //重量
					LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(BTClient.this);
					String where = "id = '" + i + "'";
					database.update(sample,where);
					//DBHelperSample.addSample(UARTLoopbackActivity.this,sample);
					/*dbUtil.alterSampleWeight(userid, cgetEditText.getText().toString(),
							cgetYPEditText.getText().toString(),
							cgetSamEditText.getText().toString(),
							cNumEditText.getText().toString());*/
					dialog.dismiss();
				//hideButton(false);
					Toast.makeText(BTClient.this, "成功修改数据", Toast.LENGTH_SHORT).show();
				}catch(Exception e){
					return;
				}	
				setListView(userid);
				setListView(userid);
				listView.setSelectionFromTop(position, scrolledY);
			}
		});

		btnDelay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try{
					 /*ArrayList<LBHashMap<String>> maxSampleid = DBHelperSample.getMaxSampleid(UARTLoopbackActivity.this);
                    String id= maxSampleid.get(0).get("MAX(id)");
                    int i = Integer.parseInt(id);
                    i=i+1;*/

					int i = Integer.parseInt(xuhao);
					Sample sample = new Sample(i,userid,cNumEditText.getText().toString());
					//DBHelperSample.addSample(UARTLoopbackActivity.this,sample);
					sample.setIsweight("1");
					String where = "id = '" + i + "'";
					LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(BTClient.this);
					database.update(sample,where);
					//DBHelperSample.updateSampleIsWeight0(UARTLoopbackActivity.this,sample);
					String a=cgetEditText.getText().toString();
					//dbUtil.updateSampleIsWeight(userid, cgetEditText.getText().toString(), "0");
					dialog.dismiss();
					//hideButton(false);
					Toast.makeText(BTClient.this, "暂不称重，修改成功", Toast.LENGTH_SHORT).show();
   	    		 
				}catch(Exception e){
					return;
				}	
				setListView(userid);
				setListView(userid);
				listView.setSelectionFromTop(position, scrolledY);
			}
		});
		btnContinue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try{
					/*ArrayList<LBHashMap<String>> maxSampleid = DBHelperSample.getMaxSampleid(UARTLoopbackActivity.this);
                    String id= maxSampleid.get(0).get("MAX(id)");
                    int i = Integer.parseInt(id);
                    i=i+1;*/

					int i = Integer.parseInt(xuhao);
					Sample sample = new Sample(i,userid,cNumEditText.getText().toString());
					sample.setIsweight("0");
					String where = "id = '" + i + "'";
					LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(BTClient.this);
					database.update(sample,where);
					//DBHelperSample.addSample(UARTLoopbackActivity.this,sample);
					//DBHelperSample.updateSampleIsWeight1(UARTLoopbackActivity.this,sample)dbUtil.updateSampleIsWeight(userid, cgetEditText.getText().toString(), "1");
					dialog.dismiss();
					//hideButton(false);
					Toast.makeText(BTClient.this, "继续称重，修改成功", Toast.LENGTH_SHORT).show();
				}catch(Exception e){
					return;
				}
				setListView(userid);
				setListView(userid);
				listView.setSelectionFromTop(position, scrolledY);
			}

		});
		
		btnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
		    	new AlertDialog.Builder(BTClient.this).setTitle("确认删除吗？") 
		    	.setIcon(android.R.drawable.ic_dialog_info) 
		        .setPositiveButton("确定", new DialogInterface.OnClickListener() { 
		     
		            @Override 
		            public void onClick(DialogInterface dialog, int which) { 
		            // 点击“确认”后的操作 
						try{
							int i = Integer.parseInt(xuhao);
							String where = "id = '" + i + "'";
							LBSQLiteDatabase database = SQLiteDataBaseTool.getSQLiteDatabase(BTClient.this);
							database.delete(Sample.class,where);
							//dbUtil.DeleteOneSample(userid, cgetEditText.getText().toString());
							dialog.dismiss();
							//hideButton(false);
							Toast.makeText(BTClient.this, "样品删除成功", Toast.LENGTH_SHORT).show();
						}catch(Exception e){
							return;
						}
						setListView(userid);
						setListView(userid);
						listView.setSelectionFromTop(position, scrolledY);
		     
		            } 
		        }) 
				.setNegativeButton("返回", new DialogInterface.OnClickListener() { 
     
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            // 点击“返回”后的操作,这里不设置没有任何操作
	            }
			}).show();
		    	dialog.dismiss();	
			}

		});
		
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setListView(userid);
				setListView(userid);
				dialog.dismiss();
				//hideButton(false);
			}
		});
		dialog.show();

	}
    //保存功能实现
	private void Save() {
	//显示对话框输入文件名
	LayoutInflater factory = LayoutInflater.from(BTClient.this);  //图层模板生成器句柄
	final View DialogView =  factory.inflate(R.layout.sname, null);  //用sname.xml模板生成视图模板
	new AlertDialog.Builder(BTClient.this)
			.setTitle("文件名")
			.setView(DialogView)   //设置视图模板
			.setPositiveButton("确定",
			new DialogInterface.OnClickListener() //确定按键响应函数
			{
				public void onClick(DialogInterface dialog, int whichButton){
					EditText text1 = (EditText)DialogView.findViewById(R.id.sname);  //得到文件名输入框句柄
					filename = text1.getText().toString();  //得到文件名
					
					try{
						if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){  //如果SD卡已准备好
							
							filename =filename+".txt";   //在文件名末尾加上.txt										
							File sdCardDir = Environment.getExternalStorageDirectory();  //得到SD卡根目录
							File BuildDir = new File(sdCardDir, "/data");   //打开data目录，如不存在则生成
							if(BuildDir.exists()==false)BuildDir.mkdirs();
							File saveFile =new File(BuildDir, filename);  //新建文件句柄，如已存在仍新建文档
							FileOutputStream stream = new FileOutputStream(saveFile);  //打开文件输入流
							
							/******************************************/
							String outtext="";
							ListView list=(ListView)findViewById(R.id.list);
							for(int i=0;i<adapter.getCount();i++)
							{
								LinearLayout layout=(LinearLayout)list.getAdapter().getView(i,null,null);
								TextView et1=(TextView)layout.findViewById(R.id.txt_Cname);
								TextView et2=(TextView)layout.findViewById(R.id.txt_Cnum);
								TextView et3=(TextView)layout.findViewById(R.id.txt_Weight);
								outtext=outtext + et1.getText() +"," + et2.getText() +"," +et3.getText() +"\n";
							}
							/********************************************/
							stream.write(outtext.getBytes());
							//stream.write(fmsg.getBytes());
							stream.close();
							Toast.makeText(BTClient.this, "存储成功！", Toast.LENGTH_SHORT).show();
						}else{
							Toast.makeText(BTClient.this, "没有存储卡！", Toast.LENGTH_LONG).show();
						}
					
					}catch(IOException e){
						return;
					}
													
				}
			})
			.setNegativeButton("取消",   //取消按键响应函数,直接退出对话框不做任何处理 
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) { 
				}
			}).show();  //显示对话框
	} 
}