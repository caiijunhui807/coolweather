package com.coolweather.app.activity;

import java.security.PublicKey;

import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;
import com.example.coolweather.R;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity{
	private LinearLayout weatherInfoLayout;
	private TextView cityNameText;
	private TextView publishText;
	private TextView weatherDespText;
	private TextView temp1Text;
	private TextView temp2Text;
	private TextView currentDateText;
	private Button switchCity;
	private Button refreshWeather;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		weatherInfoLayout=(LinearLayout) findViewById(R.id.weather_info_layoutt);
		cityNameText=(TextView) findViewById(R.id.city_name);
		publishText=(TextView) findViewById(R.id.publish_text);
		weatherDespText=(TextView) findViewById(R.id.weather_desp);
		temp1Text=(TextView) findViewById(R.id.temp1);
		temp2Text=(TextView) findViewById(R.id.temp2);
		currentDateText=(TextView) findViewById(R.id.current_data);
		switchCity=(Button) findViewById(R.id.switch_city);
		refreshWeather=(Button) findViewById(R.id.refresh_weather);
		String countyCode=getIntent().getStringExtra("county_code");
		if(!TextUtils.isEmpty(countyCode)){
			publishText.setText("同步中");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		}else{
			showWeather();
		}
	}
    public void onclick(View view){
    	switch(view.getId()){
    	case R.id.switch_city:
    		Intent intent=new Intent(this, ChooseAreaActivity.class);
    		intent.putExtra("from-weather_activity", true);
    		startActivity(intent);
    		finish();
    		break;
    	case R.id.refresh_weather:
    		publishText.setText("同步中");
    		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
    		String weatherCode=prefs.getString("weather-code", "");
    		if(!TextUtils.isEmpty(weatherCode)){
    			queryWeatherCode(weatherCode);
    		}
    		break;
    	default :
    		break;
    			
    	}
    }
    private void queryWeatherCode(String countyCode){
    	String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
    	queryFromServer(address,"countyCode");
    }
    private void queryWeatherInfo(String weatherCode){
    	String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
    	queryFromServer(address,"weatherCode");
    }
   private void queryFromServer(String address,final String type){
	   HttpUtil.sendHttpRequset(address, new HttpCallbackListener() {
		
		@Override
		public void onFinish(String response) {
			// TODO Auto-generated method stub
			if("countyCode".equals(type)){
				if(!TextUtils.isEmpty(response)){
					String[] array=response.split("\\|");
					if(array!=null&&array.length==2){
						String weatherCode=array[1];
						queryWeatherInfo(weatherCode);
					}
				}
			}else if("weatherCode".equals(type)){
				Utility.handleWeatherResponse(WeatherActivity.this, response);
				runOnUiThread(new Runnable() {
					public void run() {
						showWeather();
					}
				});
			}
		}
		
		@Override
		public void onError(Exception e) {
			// TODO Auto-generated method stub
			runOnUiThread(new Runnable(){
				public void run(){
					publishText.setText("同步失败");
				}
			});
		}
	});
   }
   private void showWeather(){
	   SharedPreferences prfs=PreferenceManager.getDefaultSharedPreferences(this);
	   cityNameText.setText(prfs.getString("city_name", ""));
	   temp1Text.setText(prfs.getString("temp1", ""));
	   temp2Text.setText(prfs.getString("temp2", ""));
	   weatherDespText.setText(prfs.getString("weather_desp", ""));
	   publishText.setText("今天"+prfs.getString("publish_time", "")+"发布");
	   currentDateText.setText(prfs.getString("current_date", ""));
	   weatherInfoLayout.setVisibility(View.VISIBLE);
   }
}
