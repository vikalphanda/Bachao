package com.example.ffm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class contact {
	/*
	 protected Context context;

	    public contact(Context context){
	        this.context = context;
	}*/
	EditText contactName ;
	Button addContact;
	Map<String, String> map = new HashMap<String, String>();
	File myFile = new File("contacts.txt");
	FileOutputStream fOut = null;
	FileInputStream fIn = null;
	
	public contact()
	{
		initMap();
	}
	
	public void createContactFile(View v)
	{
		
        try {
			myFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		try {
			fOut = new FileOutputStream(myFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        OutputStreamWriter myOutWriter = new OutputStreamWriter(
                fOut);
        
        String keyVal = "";
        
		String name = contactName.getText().toString();
		
		String phoneNum = getPhoneNumber(name, v.getContext() );
		for (Map.Entry<String,String> entry : map.entrySet()) {
		    //System.out.printf("%s -> %s%n", entry.getKey(), entry.getValue());
			keyVal = entry.getKey()+ entry.getValue();
			try {
				myOutWriter.append(keyVal);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			myOutWriter.close();
			fOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	
	public void initMap()
	{
		// Read data from file and initialize map
		File myFile = new File("contacts.txt");
		 
        FileInputStream fIn = null;
		try {
			fIn = new FileInputStream(myFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        BufferedReader myReader = new BufferedReader(
                new InputStreamReader(fIn));			
        String aDataRow = "";
        
        String key  = "";
        String value = "";
        int pos = 0;

        try {
			while ((aDataRow = myReader.readLine()) != null) {
			   // Fill map
				pos = aDataRow.indexOf("-");
				key = aDataRow.substring(0, pos-1);
				value = aDataRow.substring(pos+1,aDataRow.length()-1);
				map.put(key, value);

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public String getPhoneNumber(String name, Context context) {
		String ret = null;
		String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" like'%" + name +"%'";
		String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER};
		Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
		        projection, selection, null, null);
		if (c.moveToFirst()) {
		    ret = c.getString(0);
		}
		c.close();
		if(ret==null)
		    ret = "Unsaved";
		return ret;
		}
	
}

