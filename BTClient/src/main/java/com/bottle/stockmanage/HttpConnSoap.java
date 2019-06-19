package com.bottle.stockmanage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.os.Handler;
import android.util.Log;

public class HttpConnSoap {
	
	 /** Called when the activity is first created. */
	private final String NAMESCROPE="http://tempuri.org/";
	private final String METHOD_NAME="uploadResume";
	private final String URL="http://47.100.219.21:8080/Service1.asmx";
	private final String SOAP_ACTION="http://tempuri.org/uploadResume";
	Handler handler=null;    //杩涚▼涓皟鐢╲iew涓嶅畨鍏?
	
	public ArrayList<String> GetWebServre(String methodName, ArrayList<String> Parameters, ArrayList<String> ParValues) {
		ArrayList<String> Values = new ArrayList<String>();
		
		//ServerUrl是指webservice的url  
        //10.0.2.2是让android模拟器访问本地（PC）服务器，不能写成127.0.0.1  
        //11125是指端口号，即挂载到IIS上的时候开启的端口  
        //Service1.asmx是指提供服务的页面  
        String ServerUrl = "http://47.100.219.21:8080/Service1.asmx";
          
        //String soapAction="http://tempuri.org/LongUserId1";  
        String soapAction = "http://tempuri.org/" + methodName;  
        //String data = "";  
        String soap = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"  
                + "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"  
                + "<soap:Body />";  
        String tps, vps, ts;  
        String mreakString = "";  
  
        mreakString = "<" + methodName + " xmlns=\"http://tempuri.org/\">";  
        
        for (int i = 0; i < Parameters.size(); i++) {  
            tps = Parameters.get(i).toString();  
            //设置该方法的参数为.net webService中的参数名称  
            vps = ParValues.get(i).toString();  
            ts = "<" + tps + ">" + vps + "</" + tps + ">";  
            mreakString = mreakString + ts;  
        }  
        mreakString = mreakString + "</" + methodName + ">";  
        /* 
        +"<HelloWorld xmlns=\"http://tempuri.org/\">" 
        +"<x>string11661</x>" 
        +"<SF1>string111</SF1>" 
        + "</HelloWorld>" 
        */  
        String soap2 =  "</soap:Envelope>";  
        String requestData = soap + mreakString + soap2;  
        //System.out.println(requestData);  
  
        try {  
            URL url = new URL(ServerUrl);  
            HttpURLConnection con = (HttpURLConnection) url.openConnection();  
            byte[] bytes = requestData.getBytes("utf-8");  
            con.setDoInput(true);  
            con.setDoOutput(true);  
            con.setUseCaches(false);  
            con.setConnectTimeout(6000);// 设置超时时间  
            con.setRequestMethod("POST");  
            con.setRequestProperty("Content-Type", "text/xml;charset=utf-8");  
            con.setRequestProperty("SOAPAction", soapAction);  
            con.setRequestProperty("Content-Length", "" + bytes.length);  
            OutputStream outStream = con.getOutputStream();  
            outStream.write(bytes);  
            outStream.flush();  
            outStream.close();  
            InputStream inStream = con.getInputStream();  
  
            //data=parser(inStream);  
            //System.out.print("11");  
            Values = inputStreamtovaluelist(inStream, methodName);  
            //System.out.println(Values.size());  
            return Values;  
  
        } catch (Exception e) {  
            System.out.print("2221");  
            return null;  
        }  
    }  
  
    public ArrayList<String> inputStreamtovaluelist(InputStream in, String MonthsName) throws IOException {  
        StringBuffer out = new StringBuffer();  
        String s1 = "";  
        byte[] b = new byte[4096];  
        ArrayList<String> Values = new ArrayList<String>();  
        Values.clear();  
  
        for (int n; (n = in.read(b)) != -1;) {  
            s1 = new String(b, 0, n);  
            out.append(s1);  
        }  
        s1=out.toString();
        System.out.println(out);  
        String[] s13 = s1.split("><");  
        String ifString = MonthsName + "Result";  
        String TS = "";  
        String vs = "";  
  
        Boolean getValueBoolean = false;  
        for (int i = 0; i < s13.length; i++) {  
            TS = s13[i];  
            System.out.println(TS);  
            int j, k, l;  
            j = TS.indexOf(ifString);  
            k = TS.lastIndexOf(ifString);  
  
            if (j >= 0) {  
                System.out.println(j);  
                if (getValueBoolean == false) {  
                    getValueBoolean = true;  
                } else {  
  
                }  
  
                if ((j >= 0) && (k > j)) {  
                    System.out.println("FFF" + TS.lastIndexOf("/" + ifString));  
                    //System.out.println(TS);  
                    l = ifString.length() + 1;  
                    vs = TS.substring(j + l, k - 2);  
                    //System.out.println("fff"+vs);  
                    Values.add(vs);  
                    System.out.println("退出" + vs);  
                    getValueBoolean = false;  
                    return Values;  
                }  
  
            }  
            if (TS.lastIndexOf("/" + ifString) >= 0) {  
                getValueBoolean = false;  
                return Values;  
            }  
            if ((getValueBoolean) && (TS.lastIndexOf("/" + ifString) < 0) && (j < 0)) {  
                k = TS.length();  
                //System.out.println(TS);  
                vs = TS.substring(7, k - 8);  
                //System.out.println("f"+vs);  
                Values.add(vs);  
            }  
  
        }  
  
        return Values;  
    }  
    public void showServerice(String image,String file1,int tag)
    {
    	SoapObject request = new SoapObject(NAMESCROPE, METHOD_NAME);
    	SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER11);
    	
    	try {
			request.addProperty("filename", file1);
			request.addProperty("image",image);
			request.addProperty("tag",tag);
			request.addProperty("userid","admin");
			request.addProperty("userpwd","zkytrs");
			
			
			envelope.bodyOut=request;  
			envelope.dotNet=true;
			envelope.setOutputSoapObject(request);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			Log.e("Error","閿欒1");
		}
    	
    	HttpTransportSE ht=new HttpTransportSE(URL);
    	ht.debug=true;
    	try
    	{
    		ht.call(SOAP_ACTION, envelope);
    		SoapObject result = (SoapObject) envelope.bodyIn; 
    	
             //str1=result.getProperty(0).toString();
            
            //txt1.setText("uploadImage(filename,image)="+str1+" 鎴愬姛!"); 鍦ㄨ繘绋嬩腑涓嶅畨鍏紝瑕佸姞鍏unnable
            handler.post(runnableUi);
    		
    	}catch(Exception e){
    		Log.d("Error",e.getMessage());
    	}
    	
    }
    Runnable runnableUi=new  Runnable(){      //缁欐枃鏈璁惧??  
    	   @Override  
    	      public void run() {  
    	           //鏇存柊鐣岄潰   
    		   //txt1.setText("uploadImage(filename,image)="+str1+" 鎴愬姛!");
    	       }  
    	         
   };           
}  

