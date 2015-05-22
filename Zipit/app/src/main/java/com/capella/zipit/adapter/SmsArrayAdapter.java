package com.capella.zipit.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.capella.zipit.R;


/**
 * La classe SmsArrayAdapter adapter pour la liste des
 * activites msg (messages):
 * -nouveau sms
 * -sauvgarde des sms envoyés
 * -sauvgarde des sms reçus
 * @SuppressLint("ViewHolder")
 */

public class SmsArrayAdapter extends ArrayAdapter<Object> {
	
	/*context*/
	private final Context context;
	/*liste activités*/
	private final String[] item;
	
	/**
	 * constructeur SmsArrayAdapter permet de construire la
	 * liste menu des sms
	 * @param context
	 * */
	SmsArrayAdapter(Context context,String[] item)
	{
		super(context, R.layout.list_sms_layout, item);
		this.context= context;
		this.item=item;
	}



	/**
	 * methode getView gere la mise en place de la vue
	 *
	 * @param position
	 * @return vue
	 * @Override
	 * */
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 
			View rowView = inflater.inflate(R.layout.list_sms_layout, parent, false);
			ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView1);
			TextView textView = (TextView) rowView.findViewById(R.id.textView1);
			textView.setText(item[position]);
	 
			
			String s = item[position];
	 
			System.out.println(s);
			
			if (s.equals("Create Message")) {
				imageView.setImageResource(R.drawable.writemessage);
			} else if (s.equals("Inbox")) {
				imageView.setImageResource(R.drawable.inbox);
			} else if (s.equals("Send Item")) {
				imageView.setImageResource(R.drawable.send);
			} else {
				imageView.setImageResource(R.mipmap.ic_launcher);
			}
	 
			return rowView;
	}
	

}
