package com.capella.zipit.activity;



import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.capella.zipit.R;
import com.capella.zipit.tools.Zipper;


/**
 * La classe Sms_WActivity activité permet d'envoyer SMS compressé:
 * permet d'envoyer un sms compressé c'est à dire qu'on va compresser la chaine
 * de caractere et l'envoyer le resultat de la compression, l'interlocuteur doit lui
 * aussi posseder l'application pr pouvoir decompressé le sms et pouvoir le lire
 */
public class Write_sms_activity extends ActionBarActivity {

	/*numero du destinataire*/
	private String numDestinataire ="";

	/*accuser d'envoi & reception*/
	private BroadcastReceiver accuseDEnvoi;
	private BroadcastReceiver accuseDeReception;

	/* liste des num des contacts*/
	public static ArrayList<String> numContact = new ArrayList<String>();
	/* liste des nom des contacts*/
	public static ArrayList<String> nomContact = new ArrayList<String>();

	/* liste des adapters*/
	private ArrayAdapter<String> adapter;

	/* Bouton envoyer*/
	ImageButton boutonEnvoyer;

	/*champ numero telephone*/
	AutoCompleteTextView numTel;
	/*champ message*/
	EditText bodySms;


	/**
	 * Fonction lancée lors de la création de l'activité: 
	 * on va dans cette methode recuperer les vues des boutons
	 * et on va les lier a des adapteurs
	 *
	 * @param savedInstanceState
	 * @Override
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms__w);
		
		/*on recupere les vues boutons*/
		boutonEnvoyer = (ImageButton) findViewById(R.id.buttonEnvoyer);
		/*on recupere les champs texte*/
		numTel = (AutoCompleteTextView) findViewById(R.id.editTextNumTel);
		bodySms = (EditText) findViewById(R.id.editTextSMS);
		
		/*Creation des adapters*/
		adapter = new ArrayAdapter<String>
				(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
		numTel.setThreshold(1);
        
        /*mettre en place adapter pr l'AutoComplete du TextView*/
		numTel.setAdapter(adapter);

        /*selection du num du contact*/
		numTel.setOnItemClickListener(selectNumListener);
		
		/*listener pour les bouttons envoyer et effacer */
		boutonEnvoyer.setOnClickListener(envoyerListener);
		
		/*lecture des contacts pr autocomplete*/
		readContactData();

		if(getIntent().hasExtra("desinataire")){
			numDestinataire = getIntent().getStringExtra("desinataire");
			numTel.setText(numDestinataire);
		}

	}

	/*listener sur bouton envoyer*/
	private OnClickListener envoyerListener = new OnClickListener() {

		/**
		 * Methode qui permet d'envoyer un sms:
		 * elle recupere les informations necessaire 
		 * numtel et message envoi le sms
		 * et reinitialise les champs textes
		 *
		 * @param vue
		 * @Override
		 */
		public void onClick(View v) {

			if(numDestinataire.isEmpty())
				numDestinataire = numTel.getText().toString();

			String Texto = bodySms.getText().toString();
			if(numDestinataire.isEmpty()){
				Toast.makeText(getBaseContext(), "Aucun destinataire selectionné",Toast.LENGTH_SHORT).show();
			}
			else if(Texto.equals(""))
				Toast.makeText(getBaseContext(), "Message vide",Toast.LENGTH_SHORT).show();
			else{

				sendSMS(numDestinataire,Texto);

				bodySms.setText("");
				numTel.setText("");
			}



		}
	};




	/*listener sur bouton effacer*/
	private OnClickListener effacerListener = new OnClickListener() {

		/**
		 * Methode qui permet d'effacer tous les champs:
		 * elle recupere les informations necessaire 
		 * numtel et message envoi le sms
		 * et reinitialise les champs textes
		 *
		 * @param vue
		 * @Override
		 */
		public void onClick(View v) {
			bodySms.setText("");

			numTel.setText("");

		}
	};



	/*listener sur champs numtel*/
	private OnItemClickListener selectNumListener = new OnItemClickListener(){

		/**
		 * Methode qui permet selectionner un contact 
		 * dans une liste proposer par autocomplete
		 *
		 * @param vue adapter
		 * @param vue
		 * @param position
		 * @param identifiant
		 * @Override
		 */
		public void onItemClick(AdapterView<?> parent, View view, int position,
								long id) {
			
			 /* recupere position du contact dans la liste*/
			int i = nomContact.indexOf(""+parent.getItemAtPosition(position));
           
           /* si le nom existe*/
			if (i >= 0) {
                
               /* recupere le num de tel*/
				numDestinataire = numContact.get(i);

				InputMethodManager imm = (InputMethodManager) getSystemService(
						INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

			}

		}

	};






	/**
	 * Methode qui permet d'envoi de message
	 *
	 * @param message
	 * **/
	private void sendSMS(String phoneNumber, String message){ 
		/*message a l'utilisateur*/
		String SENT = "Message envoyé";
		String DELIVERED = "Message reçu";
		int limite = 65;

		ArrayList<PendingIntent> sentIntents = null;
		ArrayList<PendingIntent> deliveryIntents = null;

		PendingIntent sentIntent = null;
		PendingIntent deliveryIntent = null;
		Toast.makeText(getApplicationContext(), "taille Message avant zipper : "+message.length(), Toast.LENGTH_SHORT).show();
	    
	    /*compression*/
		String z = "";
		try {
			z = Zipper.compresser(message);
		}
		catch (IOException e) {
			Toast.makeText(getApplicationContext(), "Probleme de compression du message", Toast.LENGTH_SHORT).show();
			//sendSMS(numDestinataire,Texto);
		}

		Toast.makeText(getApplicationContext(), "taille Message apres zipper : "+z.length()+" => "+z, Toast.LENGTH_SHORT).show();
		SmsManager sms = SmsManager.getDefault();
		ArrayList<String> parts = sms.divideMessage(z);
		int numParts = parts.size();
		Toast.makeText(getApplicationContext(), "nbre de message decoupe : "+numParts, Toast.LENGTH_SHORT).show();


		if(z.length() > limite){
			sentIntents = new ArrayList<PendingIntent>();
			deliveryIntents = new ArrayList<PendingIntent>();
			Toast.makeText(getApplicationContext(), "multipart 1", Toast.LENGTH_SHORT).show();

			for (int i = 0; i < numParts; i++) {
				sentIntents.add(PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(SENT), 0));
				deliveryIntents.add(PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(DELIVERED), 0));
			}
		}else{
			Toast.makeText(getApplicationContext(), "simple sms 1", Toast.LENGTH_SHORT).show();
			sentIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(SENT), 0);
			deliveryIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(DELIVERED), 0);
		}
			
		
		



	    /*---Quand le sms a ete envoyer*/
		registerReceiver(new BroadcastReceiver(){
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode())
				{
					case Activity.RESULT_OK:
						Toast.makeText(getApplicationContext(), "SMS envoyer",
								Toast.LENGTH_SHORT).show();
						break;
					case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
						Toast.makeText(getApplicationContext(), "Generic failure",
								Toast.LENGTH_SHORT).show();
						break;
					case SmsManager.RESULT_ERROR_NO_SERVICE:
						Toast.makeText(getApplicationContext(), "No service",
								Toast.LENGTH_SHORT).show();
						break;
					case SmsManager.RESULT_ERROR_NULL_PDU:
						Toast.makeText(getApplicationContext(), "Null PDU",
								Toast.LENGTH_SHORT).show();
						break;
					case SmsManager.RESULT_ERROR_RADIO_OFF:
						Toast.makeText(getBaseContext(), "Radio off",
								Toast.LENGTH_SHORT).show();
						break;
				}
			}
		}, new IntentFilter(SENT));

		//--- When the SMS has been delivered. ---
		registerReceiver(new BroadcastReceiver(){
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode())
				{
					case Activity.RESULT_OK:
						Toast.makeText(getApplicationContext(), "SMS reçu",
								Toast.LENGTH_SHORT).show();
						break;
					case Activity.RESULT_CANCELED:
						Toast.makeText(getApplicationContext(), "SMS non reçu",
								Toast.LENGTH_SHORT).show();
						break;
				}
			}
		}, new IntentFilter(DELIVERED));

	    
	   
		
	    /*envoi du sms*/
		if(z.length() > limite){
			Toast.makeText(getApplicationContext(), "multipart 2", Toast.LENGTH_SHORT).show();
			sms.sendMultipartTextMessage(phoneNumber, null, parts, sentIntents, deliveryIntents);
		}

		else{
			Toast.makeText(getApplicationContext(), "simple sms 2", Toast.LENGTH_SHORT).show();
			sms.sendTextMessage(phoneNumber, null, z, sentIntent, deliveryIntent);
		}


	}



	/**
	 * Methode permet de lire les contacts et leurs 
	 * numero de telephone
	 * @Override
	 */
	private void readContactData() {

		try {

			/*********** lecture des Contacts Nom et Num **********/

			String phoneNumber = "";
			ContentResolver cr = getBaseContext()
					.getContentResolver();
             
            /*requete pr recupere les noms des contacts*/

			Cursor cur = cr
					.query(ContactsContract.Contacts.CONTENT_URI,
							null,
							null,
							null,
							null);
             
            /*si on a un resultat de la requete*/
			if (cur.getCount() > 0) {
                 
                /*nom selectionner*/
				String name = "";

				while (cur.moveToNext())
				{

					String id = cur
							.getString(cur
									.getColumnIndex(ContactsContract.Contacts._ID));
					name = cur
							.getString(cur
									.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                     
                    /*verifie les contact qui possede un num de tel*/
					if (Integer
							.parseInt(cur
									.getString(cur
											.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
					{
                             
                        /* requete pr recuperer les num tel  par id */
						Cursor pCur = cr
								.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
										null,
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID
												+ " = ?",
										new String[] { id },
										null);
						int j=0;

						while (pCur
								.moveToNext())
						{

							if(j==0)
							{
                                    /* recupere num tel*/
								phoneNumber =""+pCur.getString(pCur
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                     
                                    /* ajout du nom du contact a l'adapter */
								adapter.add(name);
                                     
                                    /* ajout du nom et num respectivement dans la liste numContact et nomContact*/
								numContact.add(phoneNumber.toString());
								nomContact.add(name.toString());

								j++;

							}
						}  // Fin While
						pCur.close();
					} // Fin if

				}  // Fin While

			} // Fin Cursor
			cur.close();


		} catch (Exception e) {
			Log.i("AutocompleteContacts","Exception : "+ e);
		}


	}


}