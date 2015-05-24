package com.capella.zipit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.capella.zipit.R;

/**
 * La classe SmsActivity activité liste les activités sur les SMS
 * c'est a cette endroi que se trouve la liste des activités qui touche au SMS:
 * elles donnes possibilité de S.A.V les SMS (reçu ou envoyer) et envoi de SMS
 * compressé (compression de chaine  de caracteres). 
 * 
 * */
public class Menu_sms_activity extends ActionBarActivity {

	/*declaration de tous les boutons de la vue*/
	private ImageButton btn_newSms = null;
	private Button btn_inbox = null;
	private Button btn_sendSms = null;

	private android.support.v7.widget.Toolbar toolbar;
	
	/**
	 * Fonction lancée lors de la création de l'activité: 
	 * on va dans cette methode recuperer tous les boutons
	 * du menu sms puis leurs attribué un listener (ecoute sur boutton
	 *  => interaction)
	 * 
	 * @param savedInstanceState
	 * @Override
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_sms);

		toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
		setSupportActionBar(toolbar);
		
		//On récupère tous les boutons du menu
		btn_newSms = (ImageButton) findViewById(R.id.btn_write_sms);
		btn_inbox = (Button) findViewById(R.id.btn_inbox);
		btn_sendSms = (Button) findViewById(R.id.btn_jesaispas);

		
		//on attribue un listener adapté aux vues
		btn_newSms.setOnClickListener(newSmsListener);
		btn_inbox.setOnClickListener(inboxListener);
		btn_sendSms.setOnClickListener(sendSmsListener);
	}


	/*listener sur bouton nouveau SMS*/
	private OnClickListener newSmsListener = new OnClickListener() {
		/**
		 * Methode qui permet de lancer l'activité nouveau message (Sms_WActivity.class)
		 * @param vue
		 * @Override
		 */
		
		public void onClick(View v) {

			/*instancition d'un intent (lien vers autre activité) => Sms_WActivity.class*/
			Intent intent = new Intent(Menu_sms_activity.this, Write_sms_activity.class);
			Menu_sms_activity.this.startActivity(intent);/*lance l'intent*/
		}
	};


	/*listener sur bouton SMS reçus*/
	private OnClickListener inboxListener= new OnClickListener() {
		/**
		 * Methode qui permet de lancer l'activité Explorateur des SMS reçus
		 * @param vue
		 * @Override
		 */
		public void onClick(View v) {

			/*instancition d'un intent (lien vers autre activité) => ListeSMSActivity.class*/
			Intent intent = new Intent(Menu_sms_activity.this, SmsExplorer_activity.class);
			intent.putExtra("mode", "reçu");/*parametre reçu => messages reçus*/
			Menu_sms_activity.this.startActivity(intent);/*lance l'intent*/
		}
	};



	/*listener sur bouton SMS envoyes*/
	private OnClickListener sendSmsListener = new OnClickListener() {
		/**
		 * Methode qui permet de lancer l'activité Explorateur des SMS envoyes
		 * @param vue
		 * @Override
		 */
		public void onClick(View v) {

			/*instancition d'un intent (lien vers autre activité) => ListeSMSActivity.class*/
			Intent intent = new Intent(Menu_sms_activity.this, SmsExplorer_activity.class);
			intent.putExtra("mode", "envoyer");/*parametre envoyer => messages envoyés*/
			Menu_sms_activity.this.startActivity(intent);/*lance l'intent*/
		}
	};

	
}
