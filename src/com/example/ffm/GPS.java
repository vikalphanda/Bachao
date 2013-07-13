package com.example.ffm;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.gsm.SmsManager;
import android.telephony.gsm.SmsMessage;
import android.util.Log;
import android.webkit.WebView.FindListener;
import android.widget.TextView;
import android.widget.Toast;

public class GPS extends Service implements LocationListener{

	private final Context mContext;

	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	boolean canGetLocation = false;

	Location location; // location
	double latitude; // latitude
	double longitude; // longitude

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute 1000 * 60 * 1 

	// Declaring a Location Manager
	protected LocationManager locationManager;

	public GPS(Context context) {
		this.mContext = context;
		getLocation();
	}


	public Location getLocation() {
		try {
			locationManager = (LocationManager) mContext
					.getSystemService(LOCATION_SERVICE);

			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
			} else {
				this.canGetLocation = true;
				// First get location from Network Provider
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);
					Log.d("Network", "Network");
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);
						Log.d("GPS Enabled", "GPS Enabled");
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
							/* else
                            {
                            	latitude = 1;
                                longitude =1;


                            }*/
						}
					}
					else
					{
						latitude = location.getLatitude();
						longitude = location.getLongitude();


					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return location;
	}

	public boolean canGetLocation() {
		return this.canGetLocation;
	}

	/**
	 * Function to show settings alert dialog
	 * On pressing Settings button will lauch Settings Options
	 * */
	public void showSettingsAlert(){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

		// Setting Dialog Title
		alertDialog.setTitle("GPS is settings");

		// Setting Dialog Message
		alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				mContext.startActivity(intent);
			}
		});

		// on pressing cancel button
		
		
		
		
		
		
		
		
		
		
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}


	@Override
	public void onLocationChanged(Location location) {

		//    	double lat = this.getLatitude();
		//    	double lon = this.getLongitude();
		//    	 Toast.makeText(getApplicationContext(), "Your CHnaged Location is - \nLat: " + lat + "\nLong: " + lon, Toast.LENGTH_LONG).show();    

	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}



	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}



	public double getLatitude(){
		/*if(location != null){
            latitude = location.getLatitude();
        }
		 */
		// return latitude
		return latitude;
	}
	public double getLongitude(){
		/*if(location != null){
            longitude = location.getLongitude();
        }
		 */ 
		// return longitude
		return longitude;
	}
	public void stopUsingGPS(){
		if(locationManager != null){
			locationManager.removeUpdates(GPS.this);
		}       
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	public String returnBossGeo(double lat, double lon) 
	{
		String request = "http://query.yahooapis.com/v1/public/yql?" +
				"q=select%20*%20from%20geo.placefinder%20where%20text='"+lat+","+lon+"'";

		Toast.makeText (getApplicationContext (), 
				"i", Toast.LENGTH_LONG).show ();
		HttpResponse data = null;
		try
		{
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet();
			get.setURI(new URI(request));
			data =  client.execute(get);
		}catch (Exception e) {

			Toast.makeText (getApplicationContext (), 
					"error2", Toast.LENGTH_LONG).show ();

			// TODO: handle exception
		}   
		String result;
		if(data == null)
		{
			result = "null";

		}
		//String result = ""+response.get("Results");
		else
		{
			result = "no data";
			//result = getXmlData(EntityUtils.toString((HttpEntity) data));

		}
		return result;

		/*int statusCode = client.executeMethod(method);
        if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + method.getStatusLine());
        } else {
                InputStream rstream = null;
                rstream = method.getResponseBodyAsStream();
                // Process response
                Document response = DocumentBuilderFactory.newInstance()
                                .newDocumentBuilder().parse(rstream);

                XPathFactory factory = XPathFactory.newInstance();
                XPath xPath = factory.newXPath();
                // Get all search Result nodes
                NodeList nodes = (NodeList) xPath.evaluate("query/results/Result",
                                response, XPathConstants.NODESET);
                int nodeCount = nodes.getLength();
                // iterate over search Result nodes
                for (int i = 0; i < nodeCount; i++) {
                        // Get each xpath expression as a string
                        String title = (String) xPath.evaluate("Title", nodes.item(i),
                                        XPathConstants.STRING);
                        String summary = (String) xPath.evaluate("Address", nodes
                                        .item(i), XPathConstants.STRING);
                        String url = (String) xPath.evaluate("Url", nodes.item(i),
                                        XPathConstants.STRING);
                        // print out the Title, Summary, and URL for each search result
                        System.out.println("Title: " + title);
                        System.out.println("Address: " + summary);
                        System.out.println("URL: " + url);
                        System.out.println("-----------");

                }
        }
		 */



	}
	public String getXmlData(String file) throws Exception
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(file));
		String data = "";
		try{

			Document doc = db.parse(file);
			Element docEle = doc.getDocumentElement();
			NodeList dataList = docEle.getElementsByTagName("results");
			if (dataList != null && dataList.getLength() > 0) {
				for (int i = 0; i < dataList.getLength(); i++) {

					Node node = dataList.item(i);

					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element e = (Element) node;
						NodeList nodeList = e.getElementsByTagName("street");
						data += nodeList.item(0).getChildNodes().item(0)
								.getNodeValue();
						nodeList = e.getElementsByTagName("city");
						data += nodeList.item(0).getChildNodes().item(0)
								.getNodeValue();
						nodeList = e.getElementsByTagName("county");
						data += nodeList.item(0).getChildNodes().item(0)
								.getNodeValue();
						nodeList = e.getElementsByTagName("state");
						data += nodeList.item(0).getChildNodes().item(0)
								.getNodeValue();
						nodeList = e.getElementsByTagName("country");
						data += nodeList.item(0).getChildNodes().item(0)
								.getNodeValue();
					}
				}


				// Data contains complete address
			}


		}
		catch(Exception e)
		{
			System.out.println(e);
		}

		return data;

	}



}
