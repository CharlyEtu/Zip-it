package com.capella.zipit.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toolbar;

import com.capella.zipit.R;


/**
 * La classe Menu_activity activité principale de l'application
 * c'est a cette endroi que se trouve la liste complete des activités.
 * elle contient explorateur de fichier (local et sd), photos, video
 * et avec possibilité de S.A.V contects et SMS et enfin envoi de SMS
 * compressé (compression de chaine  de caracteres). 
 */
public class Menu_activity extends ActionBarActivity {

	private android.support.v7.widget.Toolbar toolbar;

	/*declaration de tous les boutons de la vue*/
	private Button btn_local = null;
	private Button btn_sd = null;
	private Button btn_sms = null;
	private Button btn_pictures = null;
	private Button btn_videos = null;
	private Button btn_contacts = null;

	
	
	/**
	 * Fonction lancée lors de la création de l'activité: 
	 * on va dans cette methode recuperer tous les boutons
	 * du menu puis leurs attribué un listener (interaction)
	 * 
	 * @param savedInstanceState
	 * @Override
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_activity);

		toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
		setSupportActionBar(toolbar);

		//On récupère tous les boutons du menu
		btn_local = (Button) findViewById(R.id.btn_internal);
		btn_sd = (Button) findViewById(R.id.btn_sd);
		btn_sms = (Button) findViewById(R.id.btn_sms);
		btn_pictures = (Button) findViewById(R.id.btn_pictures);
		btn_videos = (Button) findViewById(R.id.btn_videos);
		btn_contacts = (Button) findViewById(R.id.btn_contacts);

		//on attribue un listener adapté aux vues
		btn_local.setOnClickListener(localListener);
		btn_sd.setOnClickListener(sdListener);
		btn_sms.setOnClickListener(smsListener);
		btn_pictures.setOnClickListener(picturesListener);
		btn_videos.setOnClickListener(videosListener);
		btn_contacts.setOnClickListener(contactsListener);


	}
	
	
	
	
	
	/*listener sur bouton local*/
	private OnClickListener localListener = new OnClickListener() {
		/**
		 * Methode qui permet de lancer l'activité Explorateur de fichiers du device
		 * @Override
		 */
		public void onClick(View v) {
			Intent intent = new Intent(Menu_activity.this, FileExplorer_activity.class);/*instancition d'un intent (lien vers autre activité) => FileExplorer_activity.class*/
			intent.putExtra("menuchoice", Environment.getExternalStorageDirectory().getAbsolutePath());/*affectation d'un parametre a l'intent*/
			Menu_activity.this.startActivity(intent);/*lance l'intent*/
		}
	};
		
	
		
		
	
	/*listener sur bouton SD*/
	private OnClickListener sdListener = new View.OnClickListener() {
		/**
		 * Methode qui permet de lancer l'activité Explorateur de fichiers de la
		 * SD card du device
		 * @Override
		 */
		public void onClick(View v) {
			Intent intent = new Intent(Menu_activity.this, FileExplorer_activity.class);/*instancition d'un intent (lien vers autre activité) => FileExplorer_activity.class*/
			intent.putExtra("menuchoice", "/storage/extSdCard");/*affectation d'un parametre a l'intent*/
			Menu_activity.this.startActivity(intent);/*lance l'intent*/
		}
	};
	
	
	
	
	
	/*listener sur bouton SMS*/
	private OnClickListener smsListener = new View.OnClickListener() {
		/**
		 * Methode qui permet de lancer l'activité sms
		 * @Override
		 */
		public void onClick(View v) {
			Intent intent = new Intent(Menu_activity.this, Menu_sms_activity.class);/*instancition d'un intent (lien vers autre activité) => SmsActivity.class*/
			Menu_activity.this.startActivity(intent);/*lance l'intent*/
		}
	};
	
	
	
	
	
	
	/*listener sur bouton PHOTOS*/
	private OnClickListener picturesListener = new View.OnClickListener() {
		/**
		 * Methode qui permet de lancer l'activité Explorateur des images du device
		 * @Override
		 */
		public void onClick(View v) {
			Intent intent = new Intent(Menu_activity.this, FileExplorer_activity.class);/*instancition d'un intent (lien vers autre activité) => SmsActivity.class*/
			intent.putExtra("menuchoice", Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures");/*affectation d'un parametre a l'intent*/
			Menu_activity.this.startActivity(intent);/*lance l'intent*/
		}
	};
	
	
	
	
	
	/*listener sur bouton VIDEOS*/
	private OnClickListener videosListener = new View.OnClickListener() {
		/**
		 * Methode qui permet de lancer l'activité Explorateur des videos du device
		 * @Override
		 */
		public void onClick(View v) {
			Intent intent = new Intent(Menu_activity.this, FileExplorer_activity.class);/*instancition d'un intent (lien vers autre activité) => FileExplorer_activity.class*/
			intent.putExtra("menuchoice", Environment.getExternalStorageDirectory().getAbsolutePath()+"/Movies");/*affectation d'un parametre a l'intent*/
			Menu_activity.this.startActivity(intent);/*lance l'intent*/
		}
	};
	
	
	
	
	
	/*listener sur bouton CONTACTS*/
	private OnClickListener contactsListener = new View.OnClickListener() {
		/**
		 * Methode qui permet de lancer l'activité Explorateur des contacts du device
		 * @Override
		 */
		public void onClick(View v) {
			Intent intent = new Intent(Menu_activity.this, FileExplorer_activity.class);/*instancition d'un intent (lien vers autre activité) =>FileExplorer_activity.class*/
			//intent.putExtra("menuchoice", "/storage/extSdCard");/*affectation d'un parametre a l'intent*/
			Menu_activity.this.startActivity(intent);/*lance l'intent*/
		}
	};

	
	
	/**
	 * Methode onCreateOptionsMenu:
	 * Inflate le menu et ajout d'items du menu.
	 * @param menu
	 * @return boolean
	 * @Override
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.menu_menu_activity, menu);
		return true;
	}

	
	/**
	 * Methode onOptionsItemSelected:
	 * permet de gerer les clics sur les differents 
	 * items (options).
	 * @return boolean
	 * @Override
	 */
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

	
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
