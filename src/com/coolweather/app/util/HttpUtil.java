package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtil {
	public static void sendHttpRequset(final String address,final HttpCallbackListener listener){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
            HttpURLConnection connection=null;
            try {
				URL url=new URL(address);
				connection=(HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(8000);
				connection.setReadTimeout(8000);
				InputStream input=connection.getInputStream();
				BufferedReader buff=new BufferedReader(new InputStreamReader(input));
				StringBuilder builder=new StringBuilder();
				String line;
				while((line=buff.readLine())!=null){
					builder.append(line);
				}
				if(listener!=null){
					listener.onFinish(builder.toString());
				}
			} catch (Exception e) {
				if(listener!=null){
					listener.onError(e);
				}
			}finally{
				if(connection!=null){
					connection.disconnect();
				}
			}
			}
		}).start();;
	}
}
