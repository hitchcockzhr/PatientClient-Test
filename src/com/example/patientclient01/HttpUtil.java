package com.example.patientclient01;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class HttpUtil {
    //MyApp myApp = MyApp.getInstance();

    private static HttpClient httpClient;
    private static final String TAG = "HttpUtil";

    /**
     * 获得线程安全的HttpClient对象，能够适应多线程环境 
     * @return
     */
    public static synchronized HttpClient getHttpClient() {
        if (null == httpClient) {
            HttpParams params = new BasicHttpParams();
            params.setParameter("charset", "UTF-8");
            // 设置一些基本参数  
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            //HttpProtocolParams.setUseExpectContinue(params, true);  

            // 超时设置  
            /* 从连接池中取连接的超时时间 */
            ConnManagerParams.setTimeout(params, 50000);  
            /* 连接超时 */
            HttpConnectionParams.setConnectionTimeout(params, 50000);  
            /* 请求超时 */
            HttpConnectionParams.setSoTimeout(params, 50000);

            // 设置HttpClient支持HTTP模式  
            SchemeRegistry schReg = new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));


            // 使用线程安全的连接管理来创建HttpClient  
            ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
                    params, schReg);
            httpClient = new DefaultHttpClient(conMgr, params);
        }
        return httpClient;
    }

    /**
     * 获得Post请求对象 
     * @param uri 请求地址，也可以带参数 
     * @param params 如果为null，则不添加由BasicNameValue封装的参数 
     * @return
     */
    public static HttpPost getPost(String uri, JSONObject params) {
        HttpPost post = new HttpPost(uri);
        if(params != null) {
            try{


            post.setEntity(new StringEntity(params.toString(),"UTF-8"));
            Log.v(TAG, "post done!");
            }catch (Exception e){

            }
        }
        return post;
    }

    public static HttpPost getJAPost(String uri, JSONArray params) {
        HttpPost post = new HttpPost(uri);
        if(params != null) {
            try{
            post.setEntity(new StringEntity(params.toString(),"UTF-8"));
            Log.v(TAG, "post done!");
            }catch (Exception e){

            }
        }
        return post;
    }


    public static HttpPut getPut(String uri, JSONObject params){
        HttpPut put = new HttpPut(uri);
        if(params != null) {
            try{
            put.setEntity(new StringEntity(params.toString(),"UTF-8"));
            Log.v(TAG, "Put done!");
        }catch (Exception e){

        }
        }
        return put;

    }
    public static HttpPost getPicPost(File file, String uri) throws FileNotFoundException, UnsupportedEncodingException {
        HttpPost post = new HttpPost(uri);
        if(file != null) {
            post.setEntity(new FileEntity(file,"image/jpg"));
        }
        return post;
    }

    /**
     * 用户使用的方法 
     * 功能：从服务器获得字符串 
     * @param post
     * @return
     */
    public static String getString(HttpPost post) {

        HttpClient httpClient = getHttpClient();
        HttpResponse response = null;
        try {
            response = httpClient.execute(post);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                post.abort();
                Log.v(TAG, "响应失败,请求终止.");
                return null;
            }
            Log.v(TAG, "响应成功.");
            String rev = EntityUtils.toString(response.getEntity());
            JSONObject joRev = new JSONObject(rev);
            return joRev.getString("result");
        } catch (JSONException je){
            je.printStackTrace();
            Log.e(TAG, je.getMessage());
            return null;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            return null;
        }finally{
            //finalize(response);
            post.abort();
        }
    }

    /**
     * 用户使用的方法 
     * 功能：请求服务器，返回字符串 
     * @param post post 请求对象 
     * @param requestLimit 请求失败限制次数 
     * @return
     */
    public static JSONObject getString(HttpPost post, int requestLimit) {

        if (requestLimit < 1) {
            return null;
        }
        HttpResponse response = null;
        int currCount = 0; // 当前请求次数  
        String result = null;

        while (currCount < requestLimit) {

            if(MyApp.getInstance().getHttpClient()!=null){
                httpClient = MyApp.getInstance().getHttpClient();
                Log.v(TAG, "instance");
            }else{
                httpClient = getHttpClient();
                Log.v(TAG, "getHttpClient");
            }
            currCount++;
            try {
                response = httpClient.execute(post);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    Log.v(TAG, "200");
                    String rev = EntityUtils.toString(response.getEntity());
                    Log.v(TAG, "rev:"+rev);
                    JSONObject joRev = new JSONObject(rev);
                    return joRev;
                } else if(response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED){
                    //post.abort();  
                    Log.v(TAG, "401");
                    String rev = EntityUtils.toString(response.getEntity());
                    JSONObject joRev = new JSONObject(rev);
                    return joRev;
                }else{
                    Log.e("TAG",String.valueOf(response.getStatusLine().getStatusCode()));
                    String rev = EntityUtils.toString(response.getEntity());
                    Log.v(TAG, "rev:"+rev);
                    JSONObject joRev = new JSONObject(rev);
                    String str = joRev.getString("resultMessage");
                    Log.e(TAG, str);
                    return joRev;
                }
            } catch (ClientProtocolException e) {
                Log.e(TAG, e.getMessage());
                if (currCount > requestLimit) {
                    result = "请求失败.";
                    break;
                }
                System.out.println("ClientProtocolException");
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                if (e instanceof ConnectTimeoutException) {
                    result = "连接超时.";
                } else {
                    result = "IO异常.";
                }
                if (currCount > requestLimit) {
                    break;
                }
                System.out.println("IOException");
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } finally {
                System.out.println("finally");
                //shutdown();
                //finalize(response);
                post.abort();
            }
        }
        return null;
    }

    /**
     * 用户Login的方法 
     * 功能：请求服务器，返回字符串 
     * @param post post 请求对象 
     * @param requestLimit 请求失败限制次数 
     * @return
     */
    public static JSONObject getLoginString(HttpPost post, int requestLimit) {

        if (requestLimit < 1) {
            return null;
        }
        HttpResponse response = null;
        int currCount = 0; // 当前请求次数  
        String result = null;

        while (currCount < requestLimit) {

            HttpClient httpClient = getHttpClient();
            currCount++;
            try {
                response = httpClient.execute(post);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    MyApp.getInstance().setHttpClient(httpClient);
                    Log.v(TAG, "200");
                    String rev = EntityUtils.toString(response.getEntity());
                    Log.v(TAG, "rev:"+rev);
                    JSONObject joRev = new JSONObject(rev);
                    return joRev;
                } else {
                    //post.abort();  

                    String rev = EntityUtils.toString(response.getEntity());
                    Log.v(TAG, "登录失败："+rev);
                    JSONObject joRev = new JSONObject(rev);
                    return joRev;
                }
            } catch (ClientProtocolException e) {
                Log.e(TAG, e.getMessage());
                if (currCount > requestLimit) {
                    result = "请求失败.";
                    break;
                }
                System.out.println("ClientProtocolException");
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                if (e instanceof ConnectTimeoutException) {
                    result = "连接超时.";
                } else {
                    result = "IO异常.";
                }
                if (currCount > requestLimit) {
                    break;
                }
                System.out.println("IOException");
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } finally {
                System.out.println("finally");
                //finalize(response);
                post.abort();
            }
        }
        return null;
    }
    /**
     * 用户使用的方法 
     * 功能：请求服务器，返回字符串 
     * @param uri 字符串形式的请求地址 
     * @param requestLimit 最多允许的请求失败次数 
     * @return
     */
    public static String getString(String uri, int requestLimit) {

        if (requestLimit < 1) {
            return null;
        }
        HttpResponse response = null;
        int currCount = 0; // 当前请求次数  
        String result = null;
        HttpPost post = getPost(uri, null);
        while (currCount < requestLimit) {

            HttpClient httpClient = getHttpClient();
            currCount++;
            try {
                response = httpClient.execute(post);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    Log.v(TAG, "200.");
                    return EntityUtils.toString(response.getEntity());
                } else if(response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED){
                    //post.abort();  
                    Log.v(TAG, "401");
                    return EntityUtils.toString(response.getEntity());
                }
            } catch (ClientProtocolException e) {
                Log.e(TAG, e.getMessage());
                if (currCount > requestLimit) {
                    result = "请求失败.";
                    break;
                }
                System.out.println("ClientProtocolException");
            } catch (IOException e) {
                //Log.e(TAG, e.getMessage());  
                if (e instanceof ConnectTimeoutException) {
                    result = "连接超时.";
                } else {
                    result = "IO异常.";
                }
                if (currCount > requestLimit) {
                    break;
                }
                //System.out.println("IOException");  
            } finally {
                System.out.println("finally");
                //finalize(response);
                post.abort();
            }
        }
        return result;
    }

    //获取图片地址
    public static String getPicAddress(File file, String uri, int requestLimit) throws FileNotFoundException {

        if (requestLimit < 1) {
            return null;
        }
        HttpResponse response = null;
        int currCount = 0; // 当前请求次数  
        String result = null;
        HttpPost post = null;
        try {
            post = getPicPost(file, uri);
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        post.setHeader("Content-Type", "image/jpg");

        while (currCount < requestLimit) {

            httpClient = getHttpClient();
            currCount++;
            try {
                response = httpClient.execute(post);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    Log.v(TAG, "200.");
                    return EntityUtils.toString(response.getEntity());
                } else if(response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED){
                    //post.abort();  
                    Log.v(TAG, "401");
                    return EntityUtils.toString(response.getEntity());
                }
            } catch (ClientProtocolException e) {
                Log.e(TAG, e.getMessage());
                if (currCount > requestLimit) {
                    result = "请求失败.";
                    break;
                }
                System.out.println("ClientProtocolException");
            } catch (IOException e) {
                //Log.e(TAG, e.getMessage());  
                if (e instanceof ConnectTimeoutException) {
                    result = "连接超时.";
                } else {
                    result = "IO异常.";
                }
                if (currCount > requestLimit) {
                    break;
                }
                //System.out.println("IOException");  
            } finally {
                System.out.println("finally");
                //finalize(response);
                post.abort();
            }
        }
        return result;
    }



    //doGet():将参数的键值对附加在url后面来传递
    public static JSONObject getResultForHttpGet(String uri) throws ClientProtocolException, IOException{
        //服务器  ：服务器项目  ：servlet名称
        //String path="http://192.168.5.21:8080/test/test";
        //String uri=path+"?name="+name+"&pwd="+pwd;
        //name:服务器端的用户名，pwd:服务器端的密码
        //注意字符串连接时不能带空格

        String result="";
        JSONObject joRev=null;
        HttpGet httpGet=new HttpGet(uri);

        if(MyApp.getInstance().getHttpClient()!=null){
            httpClient = MyApp.getInstance().getHttpClient();
            Log.v(TAG, "MyApp.getInstance().getHttpClient()");
        }else{
            httpClient = getHttpClient();
            Log.v(TAG, "getHttpClient()");
        }

        //httpClient = getHttpClient();
        //取得HTTP response
        HttpResponse response=httpClient.execute(httpGet);
        Log.v(TAG, "Get Response Code:"+ response.getStatusLine().getStatusCode());
        //若状态码为200
        //if(response.getStatusLine().getStatusCode()==200){
        Log.v(TAG, "get!!!");
        //取出应答字符串
        HttpEntity entity=response.getEntity();
        result=EntityUtils.toString(entity, HTTP.UTF_8);
        Log.v(TAG, "getResultForHttpGet string:"+result);
        try {
            joRev = new JSONObject(result);
            return joRev;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            //finalize(response);
            httpGet.abort();
        }
        //return EntityUtils.toString(response.getEntity());
        // }
        // return null;
        return joRev;
    }
    //接受文本 
    public static String getTextMessageForHttpGet(String uri) throws ParseException, IOException{
        HttpGet httpGet=new HttpGet(uri);
        if(MyApp.getInstance().getHttpClient()!=null){
            httpClient = MyApp.getInstance().getHttpClient();
        }else{httpClient = getHttpClient();}
        //取得HTTP response
        HttpResponse response=httpClient.execute(httpGet);
        Log.v(TAG, "Get Response Code:"+ response.getStatusLine().getStatusCode());
        //若状态码为200
        if(response.getStatusLine().getStatusCode()==200){
            Log.v(TAG, "get!!!");
            //取出应答字符串
            HttpEntity entity=response.getEntity();
            String result=EntityUtils.toString(entity, HTTP.UTF_8);
            //JSONObject joRev = new JSONObject(result);
            //shutdown();
            return result;
        }
        //finalize(response);
        httpGet.abort();
        return null;
    }
    //接收图片
    public static Bitmap getImageMessageForHttpGet(String uri) throws ParseException, IOException{
        HttpGet httpGet=new HttpGet(uri);
        if(MyApp.getInstance().getHttpClient()!=null){
            httpClient = MyApp.getInstance().getHttpClient();
        }else{httpClient = getHttpClient();}
        //取得HTTP response
        HttpResponse response=httpClient.execute(httpGet);
        Log.v(TAG, "Get Response Code:"+ response.getStatusLine().getStatusCode());
        //若状态码为200
        if(response.getStatusLine().getStatusCode()==200){
            Log.v(TAG, "get!!!");
            InputStream is = response.getEntity().getContent();
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();
            //finalize(response);
            return bitmap;
        }
        //finalize(response);
        httpGet.abort();
        return null;
    }

    public static JSONObject getString(HttpPut put) {

        HttpClient httpClient = getHttpClient();
        HttpResponse response = null;
        try {
            response = httpClient.execute(put);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                String rev = EntityUtils.toString(response.getEntity());
                JSONObject joRev = new JSONObject(rev);
                return joRev;
            }
            Log.v(TAG, "响应成功.");
            String rev = EntityUtils.toString(response.getEntity());
            JSONObject joRev = new JSONObject(rev);
            return joRev;
        } catch (JSONException je){
            je.printStackTrace();
            Log.e(TAG, je.getMessage());
            return null;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            return null;
        }finally{
            //shutdown();
            //finalize(response);
            put.abort();
        }
    }
    public static String getPutString(HttpPut put) {

        HttpClient httpClient = getHttpClient();
        HttpResponse response = null;
        try {
            response = httpClient.execute(put);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                String rev = EntityUtils.toString(response.getEntity());
                //JSONObject joRev = new JSONObject(rev);
                return rev;
            }
            Log.v(TAG, "响应成功.");
            String rev = EntityUtils.toString(response.getEntity());
            //JSONObject joRev = new JSONObject(rev);
            return rev;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            return null;
        } finally{
            //finalize(response);
            put.abort();
        }

    }
    //下载图片
    public Bitmap getHttpGetBitmap(String url)
            throws ClientProtocolException, IOException {
        Bitmap bitmap = null;
        // 新建一个默认的连接  
        if(MyApp.getInstance().getHttpClient()!=null){
            httpClient = MyApp.getInstance().getHttpClient();
        }else{httpClient = getHttpClient();}
        // 新建一个Get方法  
        HttpGet get = new HttpGet(url);
        // 得到网络的回应  
        HttpResponse response = httpClient.execute(get);

        // 如果服务器响应的是OK的话！  
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            InputStream is = response.getEntity().getContent();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        }
        //finalize(response);
        get.abort();
        return bitmap;
    }
    public static boolean saveBitmap2file(Bitmap bmp, String filename)
    {
        CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        try
        {
            stream = new FileOutputStream(Environment.getExternalStorageDirectory()
                    +"/" + filename);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }finally{
            //shutdown();


        }

        return bmp.compress(format, quality, stream);
    }
    //发送文本
    public static JSONObject postString(String text, String uri){
        if(MyApp.getInstance().getHttpClient()!=null){
            httpClient = MyApp.getInstance().getHttpClient();
        }else{httpClient = getHttpClient();}
        //取得HTTP response
        HttpPost post = new HttpPost(uri);
        HttpResponse response = null;
        JSONObject joRev = new JSONObject();
        if(text != null) {
            try{
            post.setEntity(new StringEntity(text,"UTF-8"));
            post.addHeader("Content-Type", "text/plain");
            post.addHeader("charset", HTTP.UTF_8);
            Log.v(TAG, "post done!");
        }catch (Exception e){

        }
        }
        try {
            response=httpClient.execute(post);
            Log.v(TAG, "200.");
            String result = EntityUtils.toString(response.getEntity());
            joRev = new JSONObject(result);
            /*
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {  
                Log.v(TAG, "200."); 
                String result = EntityUtils.toString(response.getEntity()); 
                JSONObject joRev = new JSONObject(result);
				return joRev;
            } else if(response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED){  
                //post.abort();  
                Log.v(TAG, "401");   
                String result = EntityUtils.toString(response.getEntity()); 
                JSONObject joRev = new JSONObject(result);
				return joRev;				  
            }   
            */
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally{
            //shutdown();
            //finalize(response);
            post.abort();
        }
        return joRev;
    }

    //发送图片message
    public static JSONObject postImage(File file, String uri){
        if(MyApp.getInstance().getHttpClient()!=null){
            httpClient = MyApp.getInstance().getHttpClient();
        }else{httpClient = getHttpClient();}
        //取得HTTP response
        HttpPost post = null;

        try {
            post = getPicPost(file, uri);
            post.setHeader("Content-Type", "image/jpg");
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        HttpResponse response = null;

        Log.v(TAG, "post done!");
        try {
            response=httpClient.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                Log.v(TAG, "200.");
                String result = EntityUtils.toString(response.getEntity());
                JSONObject joRev = new JSONObject(result);
                return joRev;
            } else if(response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED){
                //post.abort();  
                Log.v(TAG, "401");
                String result = EntityUtils.toString(response.getEntity());
                JSONObject joRev = new JSONObject(result);
                return joRev;
            }
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            //finalize(response);
            post.abort();
        }
        return null;

    }

    /**
     * 释放建立http请求占用的资源 
     */
    public static void shutdown() {
        // 释放建立http请求占用的资源  
        httpClient.getConnectionManager().shutdown();
        httpClient = null;
    }

    public static void finalize(final HttpResponse response) {
        if(response == null)
            return;

        try {

        //    EntityUtils.consume(response.getEntity());
            response.getEntity().consumeContent();
        } catch (IOException e) {
            System.out.println("释放http失败");
        }
    }
    //发送语音
    public static JSONObject postAudio(File file, String uri){
        if(MyApp.getInstance().getHttpClient()!=null){
            HttpClient httpClient = MyApp.getInstance().getHttpClient();
        }else{HttpClient httpClient = getHttpClient();}
        //取得HTTP response
        HttpPost post = null;

        try {
            post = getAudioPost(file, uri);
            post.setHeader("Content-Type", "audio/amr");
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        HttpResponse response;

        Log.v(TAG, "post done!");
        try {
            response=httpClient.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                Log.v(TAG, "200.");
                String result = EntityUtils.toString(response.getEntity());
                JSONObject joRev = new JSONObject(result);
                return joRev;
            } else if(response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED){
                //post.abort();  
                Log.v(TAG, "401");
                String result = EntityUtils.toString(response.getEntity());
                JSONObject joRev = new JSONObject(result);
                return joRev;
            }
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            //shutdown();
            post.abort();
        }
        return null;
    }
    //接受语音
    public static void getAudioMessageForHttpGet(String uri, String fileName) throws ClientProtocolException, IOException{
        HttpGet httpGet = new HttpGet(uri);
        if(MyApp.getInstance().getHttpClient()!=null){
            HttpClient httpClient = MyApp.getInstance().getHttpClient();
        }else{HttpClient httpClient = getHttpClient();}
        HttpResponse response=httpClient.execute(httpGet);
        Log.v(TAG, "Get Response Code:"+ response.getStatusLine().getStatusCode());
        if(response.getStatusLine().getStatusCode() == 200){
            Log.v(TAG, "get Audio!");
            InputStream is = response.getEntity().getContent();
            File file = new File(fileName);
            if(file.exists()){
                file.delete();
            }
            byte[] bs = new byte[1024];
            int len;
            OutputStream os = new FileOutputStream(fileName);
            while((len = is.read(bs))!= -1){
                os.write(bs,0,len);
            }
            os.close();
            is.close();
            httpGet.abort();
        }


    }
    public static HttpPost getAudioPost(File file, String uri) throws FileNotFoundException, UnsupportedEncodingException {
        HttpPost post = new HttpPost(uri);
        if(file != null) {
            post.setEntity(new FileEntity(file,"audio/amr"));
        }
        return post;
    }

}
