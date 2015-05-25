package com.capella.zipit.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;
import java.io.File;

import com.capella.zipit.R;
import com.capella.zipit.adapter.MyAdapter;


/**
 * La classe Menu_activity activité principale de l'application
 * c'est a cette endroi que se trouve la liste complete des activités.
 * elle contient explorateur de fichier (local et sd), photos, video
 * et avec possibilité de S.A.V contects et SMS et enfin envoi de SMS
 * compressé (compression de chaine  de caracteres). 
 */
public class Menu_activity extends ActionBarActivity {


	//On déclare des tableaux pour les éléments de notre burger menu et les icônes
	String TITLES[] = {"Repository","Settings","Noter", "Infos"};
	int ICONS[] = {R.drawable.ic_repo,R.drawable.ic_settings,R.drawable.ic_rate,
			R.drawable.ic_info};


	//On déclare notre RecyclerView
	RecyclerView mRecyclerView;

	//On déclare l'adapteur pour le Recycler View
	RecyclerView.Adapter mAdapter;

	//On déclare un Layout Manager
	RecyclerView.LayoutManager mLayoutManager;

	//On déclare un DrawerLayout
	DrawerLayout Drawer;

	//On déclare un ActionBarDrawerToggle
	ActionBarDrawerToggle mDrawerToggle;

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




		Boolean isSDPresent = new File("/storage/extSdCard").canRead();


		if(isSDPresent){
			setContentView(R.layout.activity_menu_activity);
			btn_sd = (Button) findViewById(R.id.btn_sd);
			btn_sd.setOnClickListener(sdListener);
		}else{
			setContentView(R.layout.activity_menu_activity_no_sd);
		}


		toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
		setSupportActionBar(toolbar);


		//On assigne notre RecyclerView du XML à notre variable mRecyclerView
		mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);

		//On informe le système que notre liste d'objets est fixe
		mRecyclerView.setHasFixedSize(true);

		//On crée un adapteur qui prend les titres et les icônes des éléments du burger menu
		mAdapter = new MyAdapter(TITLES,ICONS);

		//On passe l'adapteur au RecyclerView
		mRecyclerView.setAdapter(mAdapter);




		//On crée un LayoutManager comme étant linéaire
		mLayoutManager = new LinearLayoutManager(this);

		//On spécifie le LayoutManager du RecyclerView avec le le LayoutManager qu'on vient de créer
		mRecyclerView.setLayoutManager(mLayoutManager);

		//On récupère le DrawerLayout de notre fichier XML
		Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);

		//Gestion de l'ouverture et de la fermeture du Drawer
		mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar,R.string.openDrawer,
				R.string.closeDrawer){

			//Une fois le Drawer ouvert
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
			}

			//Une fois le Drawer fermé
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
			}

		};

		//On définit le listener du Drawer au Drawer toggle qu'on vient de créer
		Drawer.setDrawerListener(mDrawerToggle);

		//On définit le DrawerToggle à syncState
		mDrawerToggle.syncState();


		//On récupère tous les boutons du menu
		btn_local = (Button) findViewById(R.id.btn_internal);

		btn_sms = (Button) findViewById(R.id.btn_sms);
		btn_pictures = (Button) findViewById(R.id.btn_pictures);
		btn_videos = (Button) findViewById(R.id.btn_videos);
		btn_contacts = (Button) findViewById(R.id.btn_contacts);

		//on attribue un listener adapté aux vues
		btn_local.setOnClickListener(localListener);
		btn_sms.setOnClickListener(smsListener);
		btn_pictures.setOnClickListener(picturesListener);
		btn_videos.setOnClickListener(videosListener);
		btn_contacts.setOnClickListener(contactsListener);

		final GestureDetector mGestureDetector = new GestureDetector(Menu_activity.this, new GestureDetector.SimpleOnGestureListener() {

			@Override public boolean onSingleTapUp(MotionEvent e) {
				return true;
			}

		});


		mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
			@Override
			public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
				View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());


				if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {


					if (recyclerView.getChildPosition(child) == 1) {
						Intent intent = new Intent(Menu_activity.this, FileExplorer_activity.class);/*instancition d'un intent (lien vers autre activité) => FileExplorer_activity.class*/
						intent.putExtra("menuchoice", getFilesDir().getPath() + "/repository");/*affectation d'un parametre a l'intent*/
						Menu_activity.this.startActivity(intent);/*lance l'intent*/
						Drawer.closeDrawers();
					}
					if (recyclerView.getChildPosition(child) == 4) {
						Intent intent = new Intent(Menu_activity.this, Infos_activity.class);
						Menu_activity.this.startActivity(intent);

					}

					return true;

				}

				return false;
			}

			@Override
			public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

			}
		});

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
