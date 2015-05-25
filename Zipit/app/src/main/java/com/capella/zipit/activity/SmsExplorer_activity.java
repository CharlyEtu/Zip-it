package com.capella.zipit.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.capella.zipit.objet.Sms;
import com.capella.zipit.tools.XmlFileSMS;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;




/**
 * La classe ListeSMSActivity activité liste les SMS RECUS OU ENVOYES:
 * c'est a cette endroi que se trouve la liste des sms :
 * elles donnes possibilité de S.A.V les SMS (reçu ou envoyer). 
 */
public class SmsExplorer_activity extends ActionBarActivity {
	
	/*choix entre SMS envoyes ou recus*/
	private String choix;
	/*liste sms selectionner*/
	private ArrayList<Sms> liste_sms = new ArrayList<Sms>();
	/*pr la vue*/
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
	
	
	
	
	/**
	 * Methode explorerSms_Click_on_Item gere le clic simple
	 * sur un sms : ouvre un lecteur de sms
	 * 
	 * */
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
				
				//traitement de la date
				long l = Long.parseLong(sms.getDate());
				Date d = new Date(l);
				String date = DateFormat.getDateInstance(DateFormat.SHORT).format(d);
				//traitement de l'heure
				String heure = DateFormat.getTimeInstance().format(d).substring(0, 5);
				
				intent.putExtra("from_date", "Reçu le "+date+" à "+heure);
				SmsExplorer_activity.this.startActivity(intent);

			}
		});
	}
	
	
	
	
	
	
	
	/**
	 * Methode explorerSms_Long_Click_on_Item permet de gerer 
	 * la selection d'un item ou plusieure items
	 * */
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

	
	/**
	 * Methode de poplation de notre vue 
	 * permet de recuperer les sms du device
	 * */
	private void populateSmsExplorerList() {

		//sms_list.clear();
		if(choix.equals("reçu")){
			Inbox_Read();
		}else if(choix.equals("envoyer")){
			Send_item_Read();
		}
	}

	



	
	
	
	
	
	
	
	
	/**
	 * Methode qui permet de recupere les SMS reçu par le biai d'une
	 * requete sql
	 * */
	@SuppressWarnings("deprecation")
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
			

			while (cursor1.moveToNext()) {

				String number = cursor1.getString(cursor1.getColumnIndex(columns[0]));
				String name = cursor1.getString(cursor1.getColumnIndex(columns[1]));
				String date = cursor1.getString(cursor1.getColumnIndex(columns[2]));
				String msg = cursor1.getString(cursor1.getColumnIndex(columns[3]));
				String type = cursor1.getString(cursor1.getColumnIndex(columns[4]));

				/*traitement l'abs du nom contact*/
				if(name == null)
					Inbox_name[pos]= "Inconnu";
				else{
					Inbox_name[pos]= nom_contact(name);
				}
				Inbox_number[pos] = number; 
				


				Inbox_date[pos]=date;
				Inbox_type[pos]=type;


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
	@SuppressWarnings("deprecation")
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
			

			while (cursor1.moveToNext()) {

				String number = cursor1.getString(cursor1.getColumnIndex(columns[0]));
				String name = cursor1.getString(cursor1.getColumnIndex(columns[1]));
				String date = cursor1.getString(cursor1.getColumnIndex(columns[2]));
				String msg = cursor1.getString(cursor1.getColumnIndex(columns[3]));
				String type = cursor1.getString(cursor1.getColumnIndex(columns[4]));
				
				/*traitement l'abs du nom contact*/
				if(name == null)
					Inbox_name[pos]= "Inconnu";
				else
					Inbox_name[pos]= nom_contact(name);
				
				Inbox_number[pos] =number;

				Inbox_date[pos]=date;
				Inbox_type[pos]=type;

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
	            XmlFileSMS x = new XmlFileSMS(liste_sms, "/mnt/sdcard/donne_preso/SMS_RECU.xml", "sms_R");
	            x.ecrire_sms_xml();
	            
	            /*COMPRESSION*/
	            /*suppression xml*/
	            
        	}
        	/*Si SMS envoyés*/
        	else if(choix.equals("envoyer")){
        		/*generer xml contenant sms selectionnés*/
        		XmlFileSMS x = new XmlFileSMS(liste_sms, "/mnt/sdcard/donne_preso/SMS_ENVOYER.xml", "sms_W");
	            x.ecrire_sms_xml();
	            
	            /*COMPRESSION*/
	            /*suppression xml*/
        	}
        	return true;
        }
        /*si user chosit de decompressé et lire le sms reçu (selectionné)*/
        else if(id == R.id.action_lire){
        	
        	if(choix.equals("reçu")){
        		/*teeste inverse*/
        		
        		/*liste sms a restaurer*/
        		ArrayList<Sms> liste_sms_restauration = new ArrayList<Sms>();
        		XmlFileSMS y = new XmlFileSMS(liste_sms_restauration, "/mnt/sdcard/donne_preso/SMS_RECU.xml");
        		y.lire_sms_xml();
        		Log.d("XML", "Lecture du xml");
        		
        		restaure_Sms(liste_sms_restauration);


        		/*FIN teeste*/
        	}
        	else if(choix.equals("envoyer")){
        		// AFINIR
        	}
        	
        	return true;
        }

        return super.onOptionsItemSelected(item);
    }


    
    
    
    
	/**
	 * Classe interne
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
			item_name.setText(currentItem.getNom()+" ("+currentItem.getNum()+")");

			TextView item_extra = (TextView) itemView.findViewById(R.id.item_extra);
			
			
			//traitement de la date
			long l = Long.parseLong(currentItem.getDate());
			Date d = new Date(l);
			String date = DateFormat.getDateInstance(DateFormat.SHORT).format(d);
			//traitement de l'heure
			String heure = DateFormat.getTimeInstance().format(d).substring(0, 5);
			

			TextView item_date = (TextView) itemView.findViewById(R.id.item_date);
			
			//Si SMS reçu ajourd'hui
			if(date.equals(DateFormat.getDateInstance(DateFormat.SHORT).format(new Date()))){
				item_date.setText(heure);
				//Traitement de l'aperçu du message
				if(currentItem.getMsg().length() > 30)
					item_extra.setText(currentItem.getMsg().substring(0, 30)+"...");
				else
					item_extra.setText(currentItem.getMsg());

			}
			else{
				item_date.setText(date+" "+heure);
				//Traitement de l'aperçu du message
				if(currentItem.getMsg().length() > 19)
					item_extra.setText(currentItem.getMsg().substring(0, 19)+"...");
				else
					item_extra.setText(currentItem.getMsg());
				
			}
				
			if(currentItem.isChecked()){
				itemView.setBackgroundColor(Color.parseColor("#8027ae60"));
			}else{
				itemView.setBackgroundColor(Color.TRANSPARENT);
			}

			//On retourne la vue
			return itemView;
		}
	}
	
	
	
	
	
	
	
	
	void restaure_Sms(ArrayList<Sms> Sms_R_restauration){
		ContentValues values = new ContentValues();
		
		for(int i = 0 ; i < Sms_R_restauration.size() ; i++){
			Log.d("SMS n° "+i+": ", "Msg : "+Sms_R_restauration.get(i).getMsg());
			values.put("address", Sms_R_restauration.get(i).getNum());
			values.put("person", Sms_R_restauration.get(i).getNom());
			values.put("date", Sms_R_restauration.get(i).getDate());
			values.put("body", Sms_R_restauration.get(i).getMsg());
			
			values.put("type", Sms_R_restauration.get(i).getHeure());
			getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
		}
		
	}
	
	String nom_contact(String id){
		
		  String contactName = null;
		  
	        // querying contact data store
	        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts._ID + " = ?", new String[]{id}, null);
	 
	        if (cursor.moveToFirst()) {
	 
	            // DISPLAY_NAME = The display name for the contact.
	            // HAS_PHONE_NUMBER =   An indicator of whether this contact has at least one phone number.
	 
	            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
	        }
	 
	        cursor.close();
	 
	        Log.d("TAG", "Contact Name: " + contactName);
	        return contactName;
   }
	


}


