package com.capella.zipit.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.capella.zipit.R;

import java.io.File;


public class Splash_activity extends Activity {

	//On initialise le temps d'affichage du splash screen à 3 secondes
	private static int SPLASH_TIME_OUT = 3000;

	/**
	 * Fonction lancée lors de la création de l'activité
	 * @param savedInstanceState
	 * @Override
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_activity);

		manageRepository();

		new Handler().postDelayed(new Runnable() {

			/**
			 * Cette méthode se lance une fois le timer terminé pour lancer
			 * notre activité principale qui affiche notre menu
			 * @Override
			 */
			public void run() {

				//On déclare un intent avec la classe de l'activité qu'on veut afficher
				Intent i = new Intent(Splash_activity.this, Menu_activity.class);

				//On lance l'activité
				startActivity(i);

				//On ferme cette activité
				finish();
			}
		}, SPLASH_TIME_OUT);

	}

	private void manageRepository(){
		File appdirectory = getFilesDir();
		File repository = new File(appdirectory.getPath()+"/repository");

		if(!repository.exists()){

			//Affichage d'un message d'initialisation de repository
			/*Toast.makeText(getApplicationContext(), repository.getAbsolutePath()
					, Toast.LENGTH_LONG).show();*/

			//Créer la racine du repository
			repository.mkdir();


			//Définir le chemin pour le sous-dossier des dossiers
			File repo_folder = new File(repository.getAbsolutePath()+"/folders");

			//S'il n'existe pas
			if(!repo_folder.exists()){

				//On le crée
				repo_folder.mkdir();
			}

			//Définir le chemin pour le sous-dossier des photos
			File repo_photos = new File(repository.getAbsolutePath()+"/photos");

			//S'il n'existe pas
			if(!repo_photos.exists()){

				//On le crée
				repo_photos.mkdir();
			}

			//Définir le chemin pour le sous-dossier des vidéos
			File repo_videos = new File(repository.getAbsolutePath()+"/videos");

			//S'il n'existe pas
			if(!repo_videos.exists()){

				//On le crée
				repo_videos.mkdir();
			}

			//Définir le chemin pour le sous-dossier des sms
			File repo_sms = new File(repository.getAbsolutePath()+"/sms");

			//S'il n'existe pas
			if(!repo_sms.exists()){

				//On le crée
				repo_sms.mkdir();
			}

			//Définir le chemin pour le sous-dossier des contacts
			File repo_contacts = new File(repository.getAbsolutePath()+"/contacts");

			//S'il n'existe pas
			if(!repo_contacts.exists()){

				//On le crée
				repo_contacts.mkdir();
			}

		}


	}

}