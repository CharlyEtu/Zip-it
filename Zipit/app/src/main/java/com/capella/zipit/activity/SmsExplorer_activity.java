package com.capella.zipit.activity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.ListActivity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.capella.zipit.R;
import com.capella.zipit.adapter.InboxArrayAdapter;
import com.capella.zipit.objet.Sms;
import com.capella.zipit.tools.XmlFile;


/**
 * La classe ListeSMSActivity activité liste les SMS RECUS OU ENVOYES:
 * c'est a cette endroi que se trouve la liste des sms :
 * elles donnes possibilité de S.A.V les SMS (reçu ou envoyer). 
 */
public class SmsExplorer_activity extends ListActivity{
	
	/*choix entre SMS envoyes ou recus*/
	private String choix;
	/*liste sms selectionner*/
	private ArrayList<Sms> liste_sms = new ArrayList<Sms>();
	
	/*initialisation des buffers*/
	private String[] Inbox_name=new String[100],
			Inbox_number=new String[100],
			Inbox_date=new String[100],
			Inbox_type=new String[100],
			Inbox_msg=new String[4000];

	int pos=0;
	
	/**
	 * Fonction lancée lors de la création de l'activité: 
	 * on va dans cette methode recuperer le choix si choix = reçu
	 * alors on liste les sms recus.
	 * si choix = envoyer on liste les sms envoyes
	 * 
	 * @param savedInstanceState
	 * @Override
	 */
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		choix = getIntent().getStringExtra("mode");
		
		if(choix.equals("reçu")){
			Inbox_Read();/*reque sql pr recuperer les sms reçus*/
			setListAdapter(new InboxArrayAdapter(this,Inbox_name,Inbox_number,Inbox_date,Inbox_type,Inbox_msg));/*on charge la liste des diff sms trouver*/
		}
		else if(choix.equals("envoyer")){
			Send_item_Read();/*reque sql pr recuperer les sms envoyés*/
			setListAdapter(new InboxArrayAdapter(this,Inbox_name,Inbox_number,Inbox_date,Inbox_type,Inbox_msg));/*on charge la liste des diff sms trouver*/
		}
		
	}

	
	
	
	
	
	
	

	/**
	 * Methode permet de definir l'action a faire en cas de clic sur un item (sms)
	 * de la liste des items(des SMS)
	 * 
	 * @param l vue
	 * @param v
	 * @param position
	 * @param id
	 * @Override
	 */
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		super.onListItemClick(l, v, position, id);
		
		/*SMS recus*/
		if(choix.equals("reçu")){
			Sms sms = new Sms(Inbox_number[position], Inbox_name[position], Inbox_date[position],
					Inbox_type[position], Inbox_msg[position]);
			//pb de suppression 
			/*si le SMS a deja ete selectionner on le supprime*/
			if(liste_sms.contains(sms)){
				liste_sms.remove(sms);
				Toast.makeText(getBaseContext(), "Vous avez supprimer un sms nom ="+sms.getNom()+
						" num="+sms.getNum()+" date="+sms.getDate()+" msg="+sms.getMsg(),
						Toast.LENGTH_SHORT).show();
			}
			/*dans le cas contraire en le rajout a notre liste*/
			else{
				liste_sms.add(sms);
				Toast.makeText(getBaseContext(), "Vous avez ajouter un sms nom ="+sms.getNom()+
						" num="+sms.getNum()+" date="+sms.getDate()+" msg="+sms.getMsg(),
						Toast.LENGTH_SHORT).show();
			}
		}
		/*SMS envoyer*/
		else if(choix.equals("envoyer")){
			Sms sms = new Sms(Inbox_number[position], Inbox_name[position], Inbox_date[position],
					Inbox_type[position], Inbox_msg[position]);
			//pb de suppression 
			/*si le SMS a deja ete selectionner on le supprime*/
			if(liste_sms.contains(sms)){
				liste_sms.remove(sms);
				Toast.makeText(getBaseContext(), "Vous avez supprimer un sms nom ="+sms.getNom()+
						" num="+sms.getNum()+" date="+sms.getDate()+" msg="+sms.getMsg(),
						Toast.LENGTH_SHORT).show();
			}
			/*dans le cas contraire en le rajout a notre liste*/
			else{
				liste_sms.add(sms);
				Toast.makeText(getBaseContext(), "Vous avez ajouter un sms nom ="+sms.getNom()+
						" num="+sms.getNum()+" date="+sms.getDate()+" msg="+sms.getMsg(),
						Toast.LENGTH_SHORT).show();
			}
		}
		
/**bout de code a utiliser pr restaurer les sms et les contacts */		
		
		//		Intent intent = new Intent(this,View.class);
//		intent.putExtra("name", Inbox_name[position]);
//		intent.putExtra("no",   Inbox_number[position]);
//		intent.putExtra("date", Inbox_date[position]);
//		intent.putExtra("time", Inbox_type[position]);
//		
//		intent.putExtra("msg",  Inbox_msg[position]);
//		startActivity(intent);
//		ContentValues values1 = new ContentValues();
//		values1.put("address", "1239");
//		values1.put("body", "message sent");
//		getContentResolver().insert(Uri.parse("content://sms/sent"), values1);
//		
//		ContentValues values = new ContentValues();
//		values.put("address", "12389");
//		values.put("body", "message inbox");
//		getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
	}


	
	
	
	
	
	
	
	
	/**
	 * Methode qui permet de recupere les SMS reçu par le biai d'une
	 * requete sql
	 * */
	void Inbox_Read()
	{
		Uri mSmsinboxQueryUri = Uri.parse("content://sms/inbox");
		Cursor cursor1 = getContentResolver().query(
				mSmsinboxQueryUri,
				new String[] { "_id", "thread_id", "address", "person", "date",
						"body", "type" }, null, null, null);
		startManagingCursor(cursor1);
		String[] columns = new String[] { "address", "person", "date", "body",
		"type" };
		if (cursor1.getCount() > 0) {
			String count = Integer.toString(cursor1.getCount());

			while (cursor1.moveToNext()) {

				String number = cursor1.getString(cursor1.getColumnIndex(columns[0]));
				String name = cursor1.getString(cursor1.getColumnIndex(columns[1]));
				String date = cursor1.getString(cursor1.getColumnIndex(columns[2]));
				String msg = cursor1.getString(cursor1.getColumnIndex(columns[3]));
				String type = cursor1.getString(cursor1.getColumnIndex(columns[4]));

				Inbox_name[pos]= name;  
				Inbox_number[pos] =number; 

				if(date!=null)
				{
					long l = Long.parseLong(date);
					Date d = new Date(l);
					Inbox_date[pos]=DateFormat.getDateInstance(DateFormat.LONG).format(d);
					Inbox_type[pos]=DateFormat.getTimeInstance().format(d);
				}
				else
				{
					Inbox_date[pos]=date;
					Inbox_type[pos]=type;
				}

				Inbox_msg[pos]=msg;
				pos +=1;

			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Methode qui permet de recupere les SMS envoyés par le biai d'une
	 * requete sql
	 * */
	void Send_item_Read()
	{
		Uri mSmsSend_itemQueryUri = Uri.parse("content://sms/sent");
		Cursor cursor1 = getContentResolver().query(
				mSmsSend_itemQueryUri,
				new String[] { "_id", "thread_id", "address", "person", "date",
						"body", "type" }, null, null, null);
		startManagingCursor(cursor1);
		String[] columns = new String[] { "address", "person", "date", "body",
		"type" };
		if (cursor1.getCount() > 0) {
			String count = Integer.toString(cursor1.getCount());

			while (cursor1.moveToNext()) {

				String number = cursor1.getString(cursor1.getColumnIndex(columns[0]));
				String name = cursor1.getString(cursor1.getColumnIndex(columns[1]));
				String date = cursor1.getString(cursor1.getColumnIndex(columns[2]));
				String msg = cursor1.getString(cursor1.getColumnIndex(columns[3]));
				String type = cursor1.getString(cursor1.getColumnIndex(columns[4]));

				Inbox_name[pos]= name;  
				Inbox_number[pos] =number;

				if(date!=null)
				{
					long l = Long.parseLong(date);
					Date d = new Date(l);
					Inbox_date[pos]=DateFormat.getDateInstance(DateFormat.LONG).format(d);
					Inbox_type[pos]=DateFormat.getTimeInstance().format(d);
				}
				else
				{
					Inbox_date[pos]=date;
					Inbox_type[pos]=type;
				}

				Inbox_msg[pos]=msg;
				pos +=1;

			}
		}
	}
	
	/**
	 * Fonction qui ajoute des éléments à la bare de notre activité
	 * @param menu
	 * @return boolean
	 * @Override
	 */
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_smsexplorer_activity, menu);
        return true;
    }
    

	/**
	 * Fonction qui gère l'appui sur des éléments du menu
	 * @param item
	 * @return boolean
	 * @Override
	 */
    public boolean onOptionsItemSelected(MenuItem item) {
        
        int id = item.getItemId();
        
        /*si user chosit de compressé la selection*/
        if (id == R.id.action_compresser) {
        	/*si SMS reçu*/
        	if(choix.equals("reçu")){
        		/*generer xml contenant sms selectionnés*/
	            XmlFile x = new XmlFile(liste_sms, "/mnt/sdcard/donne_preso/SMS_RECU.xml", "sms_R");
	            x.creer_ficher_xml();
        	}
        	/*Si SMS envoyés*/
        	else if(choix.equals("envoyer")){
        		/*generer xml contenant sms selectionnés*/
        		XmlFile x = new XmlFile(liste_sms, "/mnt/sdcard/donne_preso/SMS_ENVOYER.xml", "sms_W");
	            x.creer_ficher_xml();
        	}
        	return true;
        }
        /*si user chosit de decompressé et lire le sms reçu (selectionné)*/
        else if(id == R.id.action_lire){
        	
        	if(choix.equals("reçu")){
	            // AFINIR
        	}
        	else if(choix.equals("envoyer")){
        		// AFINIR
        	}
        	
        	return true;
        }

        return super.onOptionsItemSelected(item);
    }


}


