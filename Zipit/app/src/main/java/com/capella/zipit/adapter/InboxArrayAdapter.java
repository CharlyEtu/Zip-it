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
 * La classe InboxArrayAdapter adapter pour la liste des
 * items reçus (messages)
 * 
 * @SuppressWarnings("rawtypes")
 */

public class InboxArrayAdapter extends ArrayAdapter {
	/*context*/
	private  Context context;
	/*les differents listes*/
	private  String[] Inbox_name,Inbox_number,Inbox_date,Inbox_type,Inbox_msg;
	
	
	
	
	/**
	 * Constructeur InboxArrayAdapter 
	 * @param context
	 * */
	@SuppressWarnings("unchecked")
	public InboxArrayAdapter(Context context,String[] Inbox_name,String[] Inbox_number,String[] Inbox_date,String[] Inbox_type,String[] Inbox_msg)
	{
		super(context, R.layout.inbox, Inbox_number);
		this.context= context;
		this.Inbox_name=Inbox_name;
		this.Inbox_number=Inbox_number;
		this.Inbox_date=Inbox_date;
		this.Inbox_type=Inbox_type;
		this.Inbox_msg=Inbox_msg;
		
	}
	
	
	
	/**
	 * methode getView gere la mise en place de la vue
	 * 
	 * @param position
	 * @param convertView
	 * @param parent
	 * @return vue
	 * @Override
	 * */
	public View getView(int position, View convertView, ViewGroup parent) {
	
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 
			View rowView = inflater.inflate(R.layout.inbox, parent, false);
			
			/*on recupere les champs texte*/
			TextView name = (TextView) rowView.findViewById(R.id.tvName);
			TextView date = (TextView) rowView.findViewById(R.id.tvDate);
			TextView msg = (TextView) rowView.findViewById(R.id.tvSmallMsgView);
			TextView type = (TextView) rowView.findViewById(R.id.tvTime);
			
			/*affectation texte a chaque champ texte */
			name.setText(Inbox_number[position]);
			date.setText(Inbox_date[position]);
			msg.setText(Inbox_msg[position]);
			type.setText(Inbox_type[position]);
	 
			return rowView;
	}
	

}