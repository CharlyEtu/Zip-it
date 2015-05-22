package com.capella.zipit.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.capella.zipit.R;

import java.io.File;


public class Splash_activity extends Activity {

	//Attributs de création des repo
	private boolean createdrepo = false;
	private boolean createdrepo_files = false;
	private boolean createdrepo_folder = false;
	private boolean createdrepo_photos = false;
	private boolean createdrepo_videos = false;
	private boolean createdrepo_sms = false;
	private boolean createdrepo_contacts = false;

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
			Toast.makeText(getApplicationContext(), repository.getAbsolutePath()
					, Toast.LENGTH_LONG).show();

			//Créer la racine du repository
			createdrepo = repository.mkdir();

			//Définir le chemin pour le sous-dossier des fichiers
			File repo_files = new File(repository.getAbsolutePath()+"/files");

			//S'il n'existe pas
			if(!repo_files.exists()){

				//On le crée
				createdrepo_files = repo_files.mkdir();
			}

			//Définir le chemin pour le sous-dossier des dossiers
			File repo_folder = new File(repository.getAbsolutePath()+"/folders");

			//S'il n'existe pas
			if(!repo_folder.exists()){

				//On le crée
				createdrepo_folder = repo_folder.mkdir();
			}

			//Définir le chemin pour le sous-dossier des photos
			File repo_photos = new File(repository.getAbsolutePath()+"/photos");

			//S'il n'existe pas
			if(!repo_photos.exists()){

				//On le crée
				createdrepo_photos = repo_photos.mkdir();
			}

			//Définir le chemin pour le sous-dossier des vidéos
			File repo_videos = new File(repository.getAbsolutePath()+"/videos");

			//S'il n'existe pas
			if(!repo_videos.exists()){

				//On le crée
				createdrepo_videos = repo_videos.mkdir();
			}

			//Définir le chemin pour le sous-dossier des sms
			File repo_sms = new File(repository.getAbsolutePath()+"/sms");

			//S'il n'existe pas
			if(!repo_sms.exists()){

				//On le crée
				createdrepo_sms = repo_sms.mkdir();
			}

			//Définir le chemin pour le sous-dossier des contacts
			File repo_contacts = new File(repository.getAbsolutePath()+"/contacts");

			//S'il n'existe pas
			if(!repo_contacts.exists()){

				//On le crée
				createdrepo_contacts = repo_contacts.mkdir();
			}

		}

		//Si le repository existe
		else{

			//Affichage d'un message comme quoi le repo est déjà crée
			Toast.makeText(getApplicationContext(), "Already created",
					Toast.LENGTH_LONG).show();
		}
	}

}
