package com.capella.zipit;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;


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

}
