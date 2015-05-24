package com.capella.zipit.activity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.capella.zipit.R;
import com.capella.zipit.adapter.InboxArrayAdapter;
import com.capella.zipit.objet.FileExplorerList_Item;
import com.capella.zipit.objet.Sms;
import com.capella.zipit.tools.XmlFile;


/**
 * La classe ListeSMSActivity activité liste les SMS RECUS OU ENVOYES:
 * c'est a cette endroi que se trouve la liste des sms :
 * elles donnes possibilité de S.A.V les SMS (reçu ou envoyer). 
 */
public class SmsExplorer_activity extends ActionBarActivity {

	private android.support.v7.widget.Toolbar toolbar;

	/*choix entre SMS envoyes ou recus*/
	private String choix;
	/*liste sms selectionner*/
	private ArrayList<Sms> liste_sms = new ArrayList<Sms>();
	private ArrayList<Sms> sms_list = new ArrayList<Sms>();
	
	/*initialisation des buffers*/
	private String[] Inbox_name=new String[4000],
			Inbox_number=new String[4000],
			Inbox_date=new String[4000],
			Inbox_type=new String[4000],
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

		//On met dans l'activité le layout de l'exploreur de fichiers
		setContentView(R.layout.activity_sms_explorer);

		toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
		setSupportActionBar(toolbar);

		//Appel la fonction pour remplir notre liste de messages
		populateSmsExplorerList();

		//Appel la fonction pour remplir la listview avec les messages
		populate_SmsExplorer_Item_ListView();

		//Appel la fonction qui gère les click sur les items
		//explorerSms_Click_on_Item();

		//Appel la fonction qui gère le long click sur les items
		explorerSms_Long_Click_on_Item();

		//Appel la fonction qui gère les click sur les items
		explorerSms_Click_on_Item();
		
	}

	private void explorerSms_Click_on_Item() {
		//On récupère la listview
		ListView list = (ListView) findViewById(R.id.SmsExplorer_Item_ListView);

		//On met un listener sur les éléments de la listview
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			/**
			 * Fonction qui gère le click sur un élément de la liste
			 *
			 * @param parent
			 * @param view
			 * @param position
			 * @param id
			 * @Override
			 */
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Sms sms = sms_list.get(position);
				Intent intent = new Intent(SmsExplorer_activity.this, Read_sms_activity.class);
				if(sms.getNom()!=null){
					intent.putExtra("from_name", sms.getNom());
				}
				else{
					intent.putExtra("from_name", "Inconnu");
				}
				intent.putExtra("from_number", sms.getNum());
				intent.putExtra("from_message", sms.getMsg());
				intent.putExtra("from_date", "Reçu le "+sms.getDate()+" à "+sms.getHeure());
				SmsExplorer_activity.this.startActivity(intent);

			}
		});
	}

	private void explorerSms_Long_Click_on_Item() {
		//On récupère la listview
		ListView list = (ListView) findViewById(R.id.SmsExplorer_Item_ListView);

		list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

				//On récupère l'élément sur lequel on a appuyé
				Sms item_clicked = sms_list.get(position);

				/*Si notre liste d'éléments cochés contient l'élément sur lequel on a fait un long
				click*/
				if(liste_sms.contains(item_clicked)){

					//On supprime l'élément de la liste
					liste_sms.remove(item_clicked);

					//Et on met sa valeur cochée à false
					item_clicked.setChecked(false);

					//Mise à jour des vues pour appliquer le background
					populate_SmsExplorer_Item_ListView();

					//Message console
					Log.i("Check", liste_sms.toString());
				}else{

					//Ajout de l'élément à la liste des éléments cochés
					liste_sms.add(item_clicked);

					//On met sa valeur cochée à true
					item_clicked.setChecked(true);

					//Mise à jour des vues pour appliquer le background
					populate_SmsExplorer_Item_ListView();

					//Message console
					Log.i("Check", liste_sms.toString());
				}

				//On déclare un message pour l'utilisateur
				String selection_message;

				//Si on a 0 ou 1 élément dans notre liste
				if(liste_sms.size() == 0 || liste_sms.size() ==1){

					//Un message sans les s
					selection_message = liste_sms.size()+" élément séléctioné.";

					//Sinon
				}else{

					//Un message avec les s
					selection_message = liste_sms.size()+" éléments séléctionés.";
				}

				//On affiche le message à l'utilisateur via un toast
				Toast.makeText(getApplicationContext(), selection_message, Toast.LENGTH_LONG).show();

				return true;
			}
		});
	}

	/**
	 * Fonction qui remplit la listview avec la liste des items
	 */
	private void populate_SmsExplorer_Item_ListView(){
		//On crée un adaptateur pour notre liste
		ArrayAdapter<Sms> adapter = new SmsExplorer_Item_ListAdapter();

		//On récupère la listview
		ListView list = (ListView) findViewById(R.id.SmsExplorer_Item_ListView);

		//On passe l'adaptateur à la liste
		list.setAdapter(adapter);
	}

	private void populateSmsExplorerList() {

		//sms_list.clear();
		if(choix.equals("reçu")){
			Inbox_Read();
		}else if(choix.equals("envoyer")){
			Send_item_Read();
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
	/*
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		super.onListItemClick(l, v, position, id);

		View itemview = v;

		//SMS recus
		if(choix.equals("reçu")){
			Sms sms = new Sms(Inbox_number[position], Inbox_name[position], Inbox_date[position],
					Inbox_type[position], Inbox_msg[position], R.drawable.folder);
			//pb de suppression 
			//si le SMS a deja ete selectionner on le supprime
			if(liste_sms.contains(sms)){
				sms.setChecked(false);
				liste_sms.remove(sms);
				Toast.makeText(getBaseContext(), "Vous avez supprimer un sms nom ="+sms.getNom()+
						" num="+sms.getNum()+" date="+sms.getDate()+" msg="+sms.getMsg(),
						Toast.LENGTH_SHORT).show();
			}
			//dans le cas contraire en le rajout a notre liste
			else{
				sms.setChecked(true);
				v.setBackgroundColor(Color.parseColor("#8027ae60"));
				liste_sms.add(sms);
				Toast.makeText(getBaseContext(), "Vous avez ajouter un sms nom ="+sms.getNom()+
						" num="+sms.getNum()+" date="+sms.getDate()+" msg="+sms.getMsg(),
						Toast.LENGTH_SHORT).show();
			}
		}
		//SMS envoyer
		else if(choix.equals("envoyer")){
			Sms sms = new Sms(Inbox_number[position], Inbox_name[position], Inbox_date[position],
					Inbox_type[position], Inbox_msg[position], R.drawable.folder);
			//pb de suppression 
			//si le SMS a deja ete selectionner on le supprime
			if(liste_sms.contains(sms)){
				liste_sms.remove(sms);
				Toast.makeText(getBaseContext(), "Vous avez supprimer un sms nom ="+sms.getNom()+
						" num="+sms.getNum()+" date="+sms.getDate()+" msg="+sms.getMsg(),
						Toast.LENGTH_SHORT).show();
			}
			//dans le cas contraire en le rajout a notre liste
			else{
				liste_sms.add(sms);
				Toast.makeText(getBaseContext(), "Vous avez ajouter un sms nom ="+sms.getNom()+
						" num="+sms.getNum()+" date="+sms.getDate()+" msg="+sms.getMsg(),
						Toast.LENGTH_SHORT).show();
			}
		}

		
//bout de code a utiliser pr restaurer les sms et les contacts
		
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
	}*/




	
	
	
	
	
	
	
	
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

				sms_list.add(new Sms(Inbox_number[pos], Inbox_name[pos], Inbox_date[pos],
						Inbox_type[pos], Inbox_msg[pos], R.drawable.ic_read_sms));
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
				sms_list.add(new Sms(Inbox_number[pos], Inbox_name[pos], Inbox_date[pos],
						Inbox_type[pos], Inbox_msg[pos], R.drawable.ic_send_sms));
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


	/**
	 * Classe interne'if' statement has empty body more... (Ctrl+
	 */
	private class SmsExplorer_Item_ListAdapter extends ArrayAdapter<Sms>{

		/**
		 * Constructeur de notre adapter
		 */
		public SmsExplorer_Item_ListAdapter() {

			/**
			 * Appel du constructeur de la classe mère, on lui passe le context,
			 * le layout de chaque élément et la liste des éléments
			 */
			super(SmsExplorer_activity.this, R.layout.smsexplorerlist_item, sms_list);
		}

		/**
		 * Fonction qui récupère la vue à afficher une fois l'adapation des informations faites
		 * @param position
		 * @param convertView
		 * @param parent
		 * @return View
		 * @Override
		 */
		public View getView(int position, View convertView, ViewGroup parent) {

			//Être certain qu'on aura une vue sur laquelle on va bosser
			View itemView = convertView;

			//Si on a pas de vue
			if(itemView == null){

				//On récupère notre vue personnalisée
				itemView = getLayoutInflater().inflate(R.layout.fileexplorerlist_item, parent,
						false);
			}

			//On récupère l'item à afficher
			Sms currentItem = sms_list.get(position);

			//On remplit la vue personnalisée
			ImageView item_icon = (ImageView) itemView.findViewById(R.id.item_icon);
			item_icon.setImageResource(currentItem.getSms_icon());

			TextView item_name = (TextView) itemView.findViewById(R.id.item_name);
			item_name.setText(currentItem.getNum());

			TextView item_extra = (TextView) itemView.findViewById(R.id.item_extra);
			item_extra.setText(currentItem.getMsg());

			TextView item_date = (TextView) itemView.findViewById(R.id.item_date);
			item_date.setText(currentItem.getDate());

			if(currentItem.isChecked()){
				itemView.setBackgroundColor(Color.parseColor("#8027ae60"));
			}else{
				itemView.setBackgroundColor(Color.TRANSPARENT);
			}

			//On retourne la vue
			return itemView;
		}
	}


}


