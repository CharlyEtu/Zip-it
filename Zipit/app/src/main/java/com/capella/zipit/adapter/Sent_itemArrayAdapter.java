package com.capella.zipit.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.capella.zipit.R;


/**
 * La classe Sent_itemArrayAdapter adapter pour la liste des
 * items envoyés (messages)
 * 
 * @SuppressWarnings("rawtypes")
 * @SuppressLint("ViewHolder")
 */

public class Sent_itemArrayAdapter extends ArrayAdapter {
	
	private  Context context;
	private  String[] Send_item_name,Send_item_number,Send_item_date,Send_item_type,Send_item_msg;
	
	/**
	 * Constructeur Sent_itemArrayAdapter 
	 * @param context
	 * */
	Sent_itemArrayAdapter(Context context,String[] Send_item_name,String[] Send_item_number,String[] Send_item_date,String[] Send_item_type,String[] Send_item_msg)
	{
	super(context, R.layout.sent_item, Send_item_number);
		this.context= context;
		this.Send_item_name=Send_item_name;
		this.Send_item_number=Send_item_number;
		this.Send_item_date=Send_item_date;
		this.Send_item_type=Send_item_type;
		this.Send_item_msg=Send_item_msg;
		
	}
	
	/**
	 * methode getView gere la mise en place de la vue
	 *
	 * @return vue
	 * @Override
	 * */
	public View getView(int position, View convertView, ViewGroup parent) {
	
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 
			View rowView = inflater.inflate(R.layout.sent_item, parent, false);

			
			TextView name = (TextView) rowView.findViewById(R.id.tvName);
			TextView date = (TextView) rowView.findViewById(R.id.tvDate);
			TextView msg = (TextView) rowView.findViewById(R.id.tvSmallMsgView);
			TextView type = (TextView) rowView.findViewById(R.id.tvTime);

			name.setText(Send_item_number[position]);
			date.setText(Send_item_date[position]);
			msg.setText(Send_item_msg[position]);
			type.setText(Send_item_type[position]);

			return rowView;
	}
	

}