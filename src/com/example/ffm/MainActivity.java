package com.example.ffm;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener  {

	GPS gps;
	Button btnShowLocation;
	public static int lock = 0;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;	
	private final float threshold = (float) 50.0;
	private boolean mInitialized = false;
	private float mLastX, mLastY, mLastZ;
	private float deltaX = (float) 0.0;
	private float deltaY = (float) 0.0;
	private float deltaZ = (float) 0.0;
	
	
	
	
	Double lat=0.0,lon=0.0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	
		/*Button addContact =  (Button) findViewById(R.id.button1);

        addContact.setOnClickListener(l)setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
            }
        });*/
		
		
		mSensorManager = (SensorManager) getSystemService (Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor (Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener (this, mAccelerometer, 
				SensorManager.SENSOR_DELAY_NORMAL);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		
		ImageView iv = (ImageView)findViewById(R.id.imageView1);
		iv.setVisibility(View.INVISIBLE
				
				);
		
		
		TextView home = (TextView)findViewById(R.id.textView1);
		
		home.setHeight(200);
		home.setTextSize(40);
		home.setText("\nWaiting 4 U 2 Be Hit By A Truck!! LOL");
		//TextView tv=(TextView)findViewById(R.id.textView1);
		//TextView tv2=(TextView)findViewById(R.id.textView2);
		//_getLocation(tv);
		//tv.setText("helloe");
		
	}
	
	
	protected void onResume ()
	{
		super.onResume ();
		mSensorManager.registerListener (this, mAccelerometer, 
				SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	protected void onPause ()
	{
		super.onPause ();
		mSensorManager.unregisterListener (this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onAccuracyChanged (Sensor arg0, int arg1) 
	{
		
	}
	
	
	@Override
	public void onSensorChanged (SensorEvent event) 
	{
		TextView tvX = (TextView) findViewById (R.id.x_axis);
		TextView tvY = (TextView) findViewById (R.id.y_axis);
		TextView tvZ = (TextView) findViewById (R.id.z_axis);
		
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		
		if (!mInitialized)
		{
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			
			tvX.setText ("0.0");
			tvY.setText ("0.0");
			tvZ.setText ("0.0");
			
			mInitialized = true;
		}
		else
		{
			deltaX = Math.abs (mLastX - x);
			deltaY = Math.abs (mLastY - y);
			deltaZ = Math.abs (mLastZ - z);
			
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			if(lock==0)
			{
			tvX.setText (Float.toString (deltaX));
			tvY.setText (Float.toString (deltaY));
			tvZ.setText (Float.toString (deltaZ));
			
			}
		}
		
		if (isAboveThreshold ())
		{
			lock++;
			GPS gps = new GPS (MainActivity.this);

			// check if GPS enabled 
			if(gps.canGetLocation()){

			double latitude = gps.latitude;
			double longitude = gps.longitude;
			String phoneNo[] = new String[5]; 
					phoneNo[0]="08790598796";
					phoneNo[1]="09503747599";
					phoneNo[2]="09501278080";
					phoneNo[3]="08987765820";
					phoneNo[4]="09566812573";
			
			String sms = "Help! I had an Accident!! \nLat: "+latitude+"\nLong:  "+longitude,sms2="";
			String geo="";
			try {
				geo = returnBossGeo(latitude, longitude);
				String arradd[] = new String[2];
				arradd=geo.split(";");
				sms+="\n"+arradd[0];
				arradd[1]=arradd[1].replace(" ", "+");
				sms2="\n https://www.google.co.in/maps/preview#!q="+arradd[1];
			}  catch (NullPointerException e1) {
				// TODO Auto-generated catch block
				Toast.makeText (getApplicationContext (), 
						"error"+e1.getMessage(), Toast.LENGTH_LONG).show ();
				e1.printStackTrace();
			}
			Toast.makeText (getApplicationContext (), 
					geo, Toast.LENGTH_LONG).show ();
			try {
			SmsManager smsManager = SmsManager.getDefault();
			
			if(lock==1)
			{
				
			//smsManager.sendTextMessage (phoneNo, null, sms, null, null);
			//smsManager.sendTextMessage (phoneNo, null, sms2, null, null);
			lock++;
			
			}
			}
			catch (Exception e) {
				// TODO: handle exception
				Toast.makeText (getApplicationContext (), "error"+e.getMessage(), Toast.LENGTH_LONG).show ();
			}
			
	        
			
			Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show(); 
			}else{
			
			gps.showSettingsAlert();
			}
			
			
			

		}
	}
	
	
	
	/*private void _getLocation(TextView tv1) {
	    // Get the location manager
	    LocationManager locationManager = (LocationManager) 
	            getSystemService(LOCATION_SERVICE);
	    Criteria criteria = new Criteria();
	    String bestProvider = locationManager.getBestProvider(criteria, false);
	    Location location = locationManager.getLastKnownLocation(bestProvider);
	    LocationListener loc_listener = new LocationListener() {

	        public void onLocationChanged(Location l) {
	        	
	        	
	        	
	        	
	        }

	        public void onProviderEnabled(String p) {}

	        public void onProviderDisabled(String p) {}

	        public void onStatusChanged(String p, int status, Bundle extras) {}
	    };
	    
	    locationManager
	            .requestLocationUpdates(bestProvider, 0, 0, loc_listener);
	    location = locationManager.getLastKnownLocation(bestProvider);
	    try {
	        lat = location.getLatitude();
	        lon = location.getLongitude();
	    } catch (NullPointerException e) {
	        lat = -1.0;
	        lon = -1.0;
	    }
	    tv1.setText(lat+" "+lon);
	    
	    
	}*/
	public boolean isAboveThreshold ()
	{
		
		
		
		  Double ampX = Math.pow(deltaX, 2);
		  Double ampY = Math.pow(deltaY, 2);
		  Double ampZ = Math.pow(deltaZ, 2);
		  Double sumAmp = ampX+ampY+ampZ;
		  Double aggAmp = Math.pow(sumAmp, 0.5);
		if (aggAmp >= threshold)
		{

			ImageView iv = (ImageView)findViewById(R.id.imageView1);
			iv.setVisibility(View.VISIBLE);
			/*new Handler().postDelayed(new Runnable()
			{
			    @Override
			    public void run()
			    {
			    	Button exit = new Button(getApplicationContext());
			    	exit.setOnClickListener(new View.OnClickListener() {
			             public void onClick(View v) {
			            	 
			                 // Perform action on click
			             }
			         });
			        //do your stuff here.
			    }
			}, 5000);*/
			
			
			Toast.makeText(getApplicationContext(), "aggAmp:"+aggAmp, Toast.LENGTH_LONG).show(); 
			TextView home = (TextView)findViewById(R.id.textView1);
			
			home.setHeight(200);
			home.setTextSize(40);
			home.setMarqueeRepeatLimit(10);
			home.setText("\nReporting Accident !!!");
			
			return true;
		}
		return false;
	}
	
	

	public String returnBossGeo(double lat, double lon) 
	{
		String request = "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20geo.placefinder%20where%20text='"+lat+","+lon+"'%20and%20gflags='R'";
		//String request = "http://google.com";
		HttpResponse data = null;
		
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet();
		try {
			get.setURI(new URI(request));
			
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Toast.makeText (getApplicationContext (), 
					"err1"+
			e1.getMessage(), Toast.LENGTH_LONG).show ();
		}
		
		try
		{
			
			  data = client.execute(get);
			Log.v("response code", data.getStatusLine().getStatusCode()+ "");
		}catch (Exception e) {

			Toast.makeText (getApplicationContext (), 
					"err"+e.getMessage(), Toast.LENGTH_LONG).show ();

			// TODO: handle exception
		}   
		String result="";
		if(data == null)
		{
			result = "null";

		}
		//String result = ""+response.get("Results");
		else
		{
			
			try {
				
				HttpEntity HE = data.getEntity();
				
				String gpsdata = EntityUtils.toString(HE);
				
				//Toast.makeText (getApplicationContext (), 
					//	"err"+gpsdata.indexOf(country, 0)+" "+gpsdata.indexOf(_country, 0), Toast.LENGTH_LONG).show ();
				result = getAddress(gpsdata);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText (getApplicationContext (), 
						"err"+e.getMessage(), Toast.LENGTH_LONG).show ();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText (getApplicationContext (), 
						"err2"+e.getMessage(), Toast.LENGTH_LONG).show ();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText (getApplicationContext (), 
						"err3"+e.getMessage(), Toast.LENGTH_LONG).show ();
			}

		}
		return result;

	


	}
	
	
	public String getAddress(String xml)
	{
		xml=xml.replace("\"", "");
		String address = "";
	String s="<street>";
	String c = "<city>";
	String p = "<pin>";
	String st = "<state>";
	String co = "<country>";
	  int city,notCity, street, notStreet, pin, notPin,  state, notState, country, notCountry;
	  
	street = xml.indexOf("<street>");
	notStreet = xml.indexOf("</street>");
	String _street = xml.substring(street+s.length(), notStreet);

	city = xml.indexOf("<city>");
	notCity = xml.indexOf("</city>");
	String _city = xml.substring(city+c.length(), notCity);

	

	state = xml.indexOf("<state>");
	notState = xml.indexOf("</state>");
	String _state = xml.substring(state+st.length(), notState);

	country = xml.indexOf("<country>");
	notCountry = xml.indexOf("</country>");
	String _country = xml.substring(country+co.length(), notCountry);

	
	pin = xml.indexOf("<pin>");
	notPin = xml.indexOf("</pin>");
	
	String _pin="";
	if(pin>=0 && notPin>=0)
	{
		
		_pin = xml.substring(pin+p.length(), notPin);
		address =  _street+"\n" + _city+"\n" + _pin+"\n" + _state+"\n" + _country+";"+_street+"+" + _city;
	}
	else
	{
		address =  _street+"\n" + _city+"\n" + _state+"\n" + _country +";"+_street+"+" + _city;
		
	}
	
	//address = (pin+p.length()-1)+" "+xml.length()+" "+notPin;
	Toast.makeText (getApplicationContext (), 
			"add"+address, Toast.LENGTH_LONG).show ();
	
	return address;
	
	
	}

	
	
	

}
