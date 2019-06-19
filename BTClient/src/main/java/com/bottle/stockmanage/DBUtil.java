package com.bottle.stockmanage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;
import android.os.Message;
import android.util.Base64;

public class DBUtil {
	private ArrayList<String> arrayList = new ArrayList<String>();
	private ArrayList<String> brrayList = new ArrayList<String>();
	private ArrayList<String> crrayList = new ArrayList<String>();
	private HttpConnSoap Soap = new HttpConnSoap();
	private Handler mHandler;
	public static Connection getConnection() {
		Connection con = null;
		try {
			//Class.forName("org.gjt.mm.mysql.Driver");
			//con=DriverManager.getConnection("jdbc:mysql://192.168.0.106:3306/test?useUnicode=true&characterEncoding=UTF-8","root","initial");  		    
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return con;
	}

	/**
	 * 获取所有货物的信息
	 * 
	 * @return
	 */
	public List<HashMap<String, String>> getAllInfo(String userid) {
		
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		arrayList.clear();
		brrayList.clear();
		crrayList.clear();
		arrayList.add("userid");
		arrayList.add("userpwd");
		brrayList.add(userid);
		brrayList.add("zkytrs");

		crrayList = Soap.GetWebServre("selectSamples", arrayList, brrayList);

/*		HashMap<String, String> tempHash = new HashMap<String, String>();
		tempHash.put("ID", "ID");
		tempHash.put("xuhao", "序号");
		tempHash.put("samplename", "样品名");
		tempHash.put("containername", "容器名");
		tempHash.put("weight", "重量");
		list.add(tempHash);*/
		
		if(crrayList.size()>0)
		for (int j = 0; j < crrayList.size(); j += 5) {
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("ID", crrayList.get(j));
			hashMap.put("xuhao", Integer.toString(j/5+1));
			hashMap.put("containername", crrayList.get(j + 1));
			hashMap.put("samplename", crrayList.get(j + 2));
			hashMap.put("weight", crrayList.get(j + 3));
			hashMap.put("isfocus", crrayList.get(j + 4));
			list.add(hashMap);
		}

		return list;
	}
	/**
	 * 用户登录验证，验证用户名和密码
	 * */
	
	public String CheckUser(String userid, String password) {	
		
		arrayList.clear();
		brrayList.clear();
		crrayList.clear();
		
		arrayList.add("userid");
		arrayList.add("password");
		brrayList.add(userid);
		brrayList.add(password);

		String ID="0";
		try {
			crrayList = Soap.GetWebServre("CheckUser", arrayList, brrayList);
			if(crrayList.size()>0)
				ID=crrayList.get(0);
		}catch(Exception e){

		}
		return ID;
	}
	/**
	 * 增加一条货物信息
	 * 
	 * @return
	 */
	public void insertCargoInfo(String Cname, String Cnum) {

		arrayList.clear();
		brrayList.clear();
		
		arrayList.add("Cname");
		arrayList.add("Cnum");
		brrayList.add(Cname);
		brrayList.add(Cnum);
		new Thread()
		{
			public void run()
			{
				try{
					Soap.GetWebServre("insertCargoInfo", arrayList, brrayList);
				}
				catch(Exception e) {
				}
			}
			}.start(); 
			
			
			//Soap.GetWebServre("insertCargoInfo", arrayList, brrayList); 
	}
	/**
	 * 增加一条重量信息
	 * 
	 * @return
	 */
	public void insertSampleWeight(String userid, String id, String weight) {

		arrayList.clear();
		brrayList.clear();
		
		arrayList.add("userid");
		arrayList.add("userpwd");
		arrayList.add("id");
		arrayList.add("weight");
		brrayList.add(userid);
		brrayList.add("zkytrs");
		brrayList.add(id);
		brrayList.add(weight);
		new Thread()
		{
			public void run()
			{
				try{
					Soap.GetWebServre("insertSampleWeight", arrayList, brrayList);
				}
				catch(Exception e) {
				}
			}
		}.start(); 
			//Soap.GetWebServre("insertCargoInfo", arrayList, brrayList); 
	}
	/**
	 * 增加批次信息
	 * 
	 * @return
	 */
	public void insertBatchNew(String userid, String batchdes) {

		arrayList.clear();
		brrayList.clear();
		
		arrayList.add("userid");
		arrayList.add("userpwd");
		arrayList.add("batchdes");

		brrayList.add(userid);
		brrayList.add("zkytrs");
		brrayList.add(batchdes);
		new Thread()
		{
			public void run()
			{
				try{
					Soap.GetWebServre("batchnew", arrayList, brrayList);
				}
				catch(Exception e) {
				}
			}
		}.start(); 
			//Soap.GetWebServre("insertCargoInfo", arrayList, brrayList); 
	}
	/**
	 * 增加批次信息
	 * 
	 * @return
	 */
	public void BatchSelect(String userid, String batchname) {

		arrayList.clear();
		brrayList.clear();
		
		arrayList.add("userid");
		arrayList.add("userpwd");
		arrayList.add("batchname");

		brrayList.add(userid);
		brrayList.add("zkytrs");
		brrayList.add(batchname);
		new Thread()
		{
			public void run()
			{
				try{
					Soap.GetWebServre("batchselect", arrayList, brrayList);
				}
				catch(Exception e) {
				}
			}
		}.start(); 
			//Soap.GetWebServre("insertCargoInfo", arrayList, brrayList); 
	}
	/* 批次查询
	 * 
	 * @return
	 */
	public List<HashMap<String, String>> getAllBatch_byUserid(String userid) {
		
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		arrayList.clear();
		brrayList.clear();
		crrayList.clear();
		arrayList.add("userid");
		arrayList.add("userpwd");
		brrayList.add(userid);
		brrayList.add("zkytrs");

		crrayList = Soap.GetWebServre("batchlist", arrayList, brrayList);

	
		if(crrayList.size()>0)
		for (int j = 0; j < crrayList.size(); j += 3) {
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put("ID", crrayList.get(j));
			hashMap.put("xuhao", Integer.toString(j/3+1));
			hashMap.put("batchname", crrayList.get(j + 1));
			hashMap.put("desc", crrayList.get(j + 2));

			list.add(hashMap);
		}

		return list;
	}
	//样品增加
	public void sampleappend(String userid, String yps, 
			String rongqiqs, String yangpingqs, 
			String rongqiqz,String yangpingqz) {

		arrayList.clear();
		brrayList.clear();
		
		arrayList.add("userid");
		arrayList.add("yps");
		arrayList.add("rongqiqs");
		arrayList.add("yangpingqs");
		arrayList.add("rongqiqz");
		arrayList.add("yangpingqz");
		arrayList.add("userpwd");
		
		
		brrayList.add(userid);
		brrayList.add(yps);
		brrayList.add(rongqiqs);
		brrayList.add(yangpingqs);
		brrayList.add(rongqiqz);
		brrayList.add(yangpingqz);
		brrayList.add("zkytrs");

		new Thread()
		{
			public void run()
			{
				try{
					Soap.GetWebServre("sampleappend", arrayList, brrayList);
				}
				catch(Exception e) {
				}
			}
		}.start(); 
			//Soap.GetWebServre("insertCargoInfo", arrayList, brrayList); 
	}

	public void sampleinsert(String userid, String charu, 
			String containername, String samplename) {

		arrayList.clear();
		brrayList.clear();
		
		arrayList.add("userid");
		arrayList.add("charu");
		arrayList.add("containername");
		arrayList.add("samplename");
		arrayList.add("userpwd");
		
		
		brrayList.add(userid);
		brrayList.add(charu);
		brrayList.add(containername);
		brrayList.add(samplename);
		brrayList.add("zkytrs");

		new Thread()
		{
			public void run()
			{
				try{
					Soap.GetWebServre("sampleinsert", arrayList, brrayList);
				}
				catch(Exception e) {
				}
			}
		}.start(); 
			//Soap.GetWebServre("insertCargoInfo", arrayList, brrayList); 
	}
	
	/**
	 * 修改一条重量信息
	 * 
	 * @return
	 */
	public void alterSampleWeight(String userid, String id, String containername, String samplename, String weight) {

		arrayList.clear();
		brrayList.clear();
		
		arrayList.add("userid");
		arrayList.add("userpwd");
		arrayList.add("id");
		arrayList.add("containername");
		arrayList.add("samplename");
		arrayList.add("weight");
		brrayList.add(userid);
		brrayList.add("zkytrs");
		brrayList.add(id);
		brrayList.add(containername);
		brrayList.add(samplename);
		brrayList.add(weight);
		new Thread()
		{
			public void run()
			{
				try{
					Soap.GetWebServre("updateSampleWeight", arrayList, brrayList);
				}
				catch(Exception e) {
				}
			}
		}.start(); 
			//Soap.GetWebServre("insertCargoInfo", arrayList, brrayList); 
	}
	
	/**
	 * 删除一条重量信息
	 * 
	 * @return
	 */
	public void DeleteOneSample(String userid, String id) {

		arrayList.clear();
		brrayList.clear();
		
		arrayList.add("userid");
		arrayList.add("userpwd");
		arrayList.add("id");
		brrayList.add(userid);
		brrayList.add("zkytrs");
		brrayList.add(id);
		new Thread()
		{
			public void run()
			{
				try{
					Soap.GetWebServre("sampledel", arrayList, brrayList);
				}
				catch(Exception e) {
				}
			}
		}.start(); 
			//Soap.GetWebServre("insertCargoInfo", arrayList, brrayList); 
	}
	/**
	 * 增加一条重量信息
	 * 
	 * @return
	 */
	public void updateSampleIsWeight(String userid, String id, String isweight) {

		arrayList.clear();
		brrayList.clear();
		
		arrayList.add("userid");
		arrayList.add("userpwd");
		arrayList.add("id");
		arrayList.add("Isweight");
		brrayList.add(userid);
		brrayList.add("zkytrs");
		brrayList.add(id);
		brrayList.add(isweight);
		new Thread()
		{
			public void run()
			{
				try{
					Soap.GetWebServre("updateSampleIsWeight", arrayList, brrayList);
				}
				catch(Exception e) {
				}
			}
		}.start(); 
			//Soap.GetWebServre("insertCargoInfo", arrayList, brrayList); 
	}
	/**
	 * 删除一条货物信息
	 * 
	 * @return
	 */
	public void deleteCargoInfo(String Cno) {

		arrayList.clear();
		brrayList.clear();
		
		arrayList.add("Cno");
		brrayList.add(Cno);
		
		Soap.GetWebServre("deleteCargoInfo", arrayList, brrayList);
	}
	
	public void uploadTest(String filename)
    {
    	SimpleDateFormat sDateFormat =new SimpleDateFormat("yyyy-MM-dd_hhmmss");       
		String file1=sDateFormat.format(new java.util.Date())+filename.substring(filename.indexOf("."));
		try
		{
	      FileInputStream fis=new FileInputStream(filename);
	      //ByteArrayOutputStream baos=new ByteArrayOutputStream();
	      byte []buffer=new byte[100*1024];
	      int count=0;
	      int i=0;
	      while((count=fis.read(buffer))>=0){
    	  String uploadBuffer=new String(Base64.encode(buffer,0,count,Base64.DEFAULT));
    	  Soap.showServerice(uploadBuffer,file1,i);  //缁紶           
          for(int j=0;j<1000;j++);
          
          i++;
       }
      
       fis.close();
	   }catch(FileNotFoundException e){
	       e.printStackTrace();
	     }catch(IOException e){
	        e.printStackTrace();
	      }
    }
}
