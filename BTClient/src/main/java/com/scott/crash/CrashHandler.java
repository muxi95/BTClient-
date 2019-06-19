package com.scott.crash;

import java.io.File;  
import java.io.FileOutputStream;  
import java.io.PrintWriter;  
import java.io.StringWriter;  
import java.io.Writer;  
import java.lang.Thread.UncaughtExceptionHandler;  
import java.lang.reflect.Field;  
import java.text.DateFormat;  
import java.text.SimpleDateFormat;  
import java.util.Date;  
import java.util.HashMap;  
import java.util.Map;  

import com.bottle.stockmanage.DBUtil;
  
import android.content.Context;  
import android.content.pm.PackageInfo;  
import android.content.pm.PackageManager;  
import android.content.pm.PackageManager.NameNotFoundException;  
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;  
import android.os.Environment;  
import android.os.Handler;
import android.os.Looper;  
import android.util.Log;  
import android.widget.Toast;  
  
/** 
 * UncaughtException婢跺嫮鎮婄猾锟�,瑜版挾鈻兼惔蹇撳絺閻㈢兙ncaught瀵倸鐖堕惃鍕閸婏拷,閺堝顕氱猾缁樻降閹恒儳顓哥粙瀣碍,楠炴儼顔囪ぐ鏇炲絺闁線鏁婄拠顖涘Г閸涳拷. 
 *  
 * @author user 
 *  
 */  
public class CrashHandler implements UncaughtExceptionHandler {  
      
    public static final String TAG = "CrashHandler";  
      
  //绯荤粺榛樿鐨刄ncaughtException澶勭悊绫� 
    private Thread.UncaughtExceptionHandler mDefaultHandler;  
  //CrashHandler瀹炰緥 
    private static CrashHandler INSTANCE = new CrashHandler();  
    //绋嬪簭鐨凜ontext瀵硅薄  
    private Context mContext;  
  //鐢ㄦ潵瀛樺偍璁惧淇℃伅鍜屽紓甯镐俊鎭� 
    private Map<String, String> infos = new HashMap<String, String>();  
  
  //鐢ㄤ簬鏍煎紡鍖栨棩鏈�,浣滀负鏃ュ織鏂囦欢鍚嶇殑涓�閮ㄥ垎
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");  
    
    private DBUtil dbUtil;
    Handler handler=null;    
    /** 淇濊瘉鍙湁涓�涓狢rashHandler瀹炰緥 */
    private CrashHandler() {  
    }  
  
    /** 鑾峰彇CrashHandler瀹炰緥 ,鍗曚緥妯″紡 */ 
    public static CrashHandler getInstance() {  
        return INSTANCE;  
    }  
  
    /** 
     * 閸掓繂顫愰崠锟� 
     *  
     * @param context 
     */  
    public void init(Context context) {  
        mContext = context;  
      //鑾峰彇绯荤粺榛樿鐨刄ncaughtException澶勭悊鍣�
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();  
      //璁剧疆璇rashHandler涓虹▼搴忕殑榛樿澶勭悊鍣�   
        Thread.setDefaultUncaughtExceptionHandler(this);  
    }  
  
    /** 
     * 褰揢ncaughtException鍙戠敓鏃朵細杞叆璇ュ嚱鏁版潵澶勭悊 
     */  
    @Override  
    public void uncaughtException(Thread thread, Throwable ex) {  
    	if (!handleException(ex) && mDefaultHandler != null) {  
    		 //濡傛灉鐢ㄦ埛娌℃湁澶勭悊鍒欒绯荤粺榛樿鐨勫紓甯稿鐞嗗櫒鏉ュ鐞� 
            mDefaultHandler.uncaughtException(thread, ex);  
        } else {  
            try {  
                Thread.sleep(3000);  
            } catch (InterruptedException e) {  
                Log.e(TAG, "error : ", e);  
            }  
            //閫�鍑虹▼搴�
            android.os.Process.killProcess(android.os.Process.myPid());  
            System.exit(1);  
        }  
    }  
  
    /** 
     * 鑷畾涔夐敊璇鐞�,鏀堕泦閿欒淇℃伅 鍙戦�侀敊璇姤鍛婄瓑鎿嶄綔鍧囧湪姝ゅ畬鎴�. 
     *  
     * @param ex 
     * @return true:濡傛灉澶勭悊浜嗚寮傚父淇℃伅;鍚﹀垯杩斿洖false. 
     */  
    private boolean handleException(Throwable ex) {  
        if (ex == null) {  
            return true;  
        }  
        //娴ｈ法鏁oast閺夈儲妯夌粈鍝勭磽鐢晲淇婇幁锟�  
        new Thread() {  
            @Override  
            public void run() {  
                Looper.prepare();  
                Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出!", Toast.LENGTH_LONG).show();  
                Looper.loop();  
            }  
        }.start();  
        //閺�鍫曟肠鐠佹儳顦崣鍌涙殶娣団剝浼�   
        collectDeviceInfo(mContext);  
        //娣囨繂鐡ㄩ弮銉ョ箶閺傚洣娆�   
        String filename="";
        dbUtil=new DBUtil();
        filename=saveCrashInfo2File(ex);  
        final String filepath= "/sdcard/crash/" + filename; 
        handler=new Handler();
        //@SuppressWarnings("unused")
        new Thread(){
			 @Override
			public void run(){  
				 dbUtil.uploadTest(filepath);  
			 }
		}.start();
        return true;  
    }  
      
    /** 
     * 閺�鍫曟肠鐠佹儳顦崣鍌涙殶娣団剝浼� 
     * @param ctx 
     */  
    public void collectDeviceInfo(Context ctx) {  
        try {  
            PackageManager pm = ctx.getPackageManager();  
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);  
            if (pi != null) {  
                String versionName = pi.versionName == null ? "null" : pi.versionName;  
                String versionCode = pi.versionCode + "";  
                infos.put("versionName", versionName);  
                infos.put("versionCode", versionCode);  
            }  
        } catch (NameNotFoundException e) {  
            Log.e(TAG, "an error occured when collect package info", e);  
        }  
        Field[] fields = Build.class.getDeclaredFields();  
        for (Field field : fields) {  
            try {  
                field.setAccessible(true);  
                infos.put(field.getName(), field.get(null).toString());  
                Log.d(TAG, field.getName() + " : " + field.get(null));  
            } catch (Exception e) {  
                Log.e(TAG, "an error occured when collect crash info", e);  
            }  
        }  
    }  
  
   
    
    /** 
     * 娣囨繂鐡ㄩ柨娆掝嚖娣団剝浼呴崚鐗堟瀮娴犳湹鑵� 
     *  
     * @param ex 
     * @return  鏉╂柨娲栭弬鍥︽閸氬秶袨,娓氬じ绨亸鍡樻瀮娴犳湹绱堕柅浣稿煂閺堝秴濮熼崳锟� 
     */  
    private String saveCrashInfo2File(Throwable ex) {  
          
        StringBuffer sb = new StringBuffer();  
        for (Map.Entry<String, String> entry : infos.entrySet()) {  
            String key = entry.getKey();  
            String value = entry.getValue();  
            sb.append(key + "=" + value + "\n");  
        }  
          
        Writer writer = new StringWriter();  
        PrintWriter printWriter = new PrintWriter(writer);  
        ex.printStackTrace(printWriter);  
        Throwable cause = ex.getCause();  
        while (cause != null) {  
            cause.printStackTrace(printWriter);  
            cause = cause.getCause();  
        }  
        printWriter.close();  
        String result = writer.toString();  
        sb.append(result);  
        try {  
            long timestamp = System.currentTimeMillis();  
            String time = formatter.format(new Date());  
            String fileName = "crash-" + time + "-" + timestamp + ".log";  
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  
                String path = "/sdcard/crash/";  
                File dir = new File(path);  
                if (!dir.exists()) {  
                    dir.mkdirs();  
                }  
                FileOutputStream fos = new FileOutputStream(path + fileName);  
                fos.write(sb.toString().getBytes());  
                fos.close();  
            }  
            return fileName;  
        } catch (Exception e) {  
            Log.e(TAG, "an error occured while writing file...", e);  
        }  
        return null;  
    }  
}  