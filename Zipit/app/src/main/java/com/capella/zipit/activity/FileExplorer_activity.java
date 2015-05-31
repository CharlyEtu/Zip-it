package com.capella.zipit.activity;

/**
 * Created by capella on 22/04/15.
 */


import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.MediaStore;
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
import com.capella.zipit.objet.Contact;
import com.capella.zipit.objet.FileExplorerList_Item;
import com.capella.zipit.objet.Sms;
import com.capella.zipit.tools.XmlFileCONTACTS;
import com.capella.zipit.tools.XmlFileSMS;
import com.capella.zipit.tools.Zipper;


public class FileExplorer_activity extends ActionBarActivity {



	private android.support.v7.widget.Toolbar toolbar;

	ArrayList<String> checked_items = new ArrayList<String>();

	//Fichier courant
	private File currentDir;

	//Choix fait sur l'activité du menu
	private String menuchoice;

	//Liste des items(Dossiers+Fichiers)
	private List<FileExplorerList_Item> myItems = new ArrayList<FileExplorerList_Item>();

	/**
	 * Fonction lancée lors de la création de l'activité
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		//On met dans l'activité le layout de l'exploreur de fichiers
		setContentView(R.layout.activity_file_explorer);


		toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
		setSupportActionBar(toolbar);

		//Récupère le textview qui affiche le chemin de l'exploreur
		TextView path = (TextView) findViewById(R.id.path);

		if(getIntent().hasExtra("menuchoice")) {
			menuchoice = getIntent().getStringExtra("menuchoice");

			//Initie la valeur du dossier courant avec le intent passé
			currentDir = new File(menuchoice);

			//Récupère le textview qui affiche la racine de l'explorateur
			TextView pathbegin = (TextView) findViewById(R.id.pathbegin);

			//Si le chemin est celui de la carte SD
			if (menuchoice.equalsIgnoreCase("/storage/extSdCard")) {

				//On met dans le pathbegin CARTE SD
				pathbegin.setText(R.string.pathsd);

				//Si c'est le chemin du stockage interne
			} else if (menuchoice.equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures")){

				pathbegin.setText(R.string.photos);
			} else if (menuchoice.equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Movies")){

				pathbegin.setText(R.string.videos);
			} else if (menuchoice.equalsIgnoreCase(Environment.getExternalStorageDirectory().
					getAbsolutePath())) {

				//On met dans le pathbegin Stockage Interne
				pathbegin.setText(R.string.pathlocal);
			}  else if(menuchoice.equalsIgnoreCase(getFilesDir().getPath() + "/repository")){

				pathbegin.setText(R.string.repository);
			}

		}

		//Si l'activité admet un intent du nom path
		if(getIntent().hasExtra("path")){

			//Le dossier courant est le dossier avec le chemin de cet intent
			currentDir = new File(getIntent().getStringExtra("path"));
		}

		if(menuchoice.equalsIgnoreCase( Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures")) {
			//Appel la fonction pour remplir notre liste d'items
			populate_imageexplorer_list(currentDir);
			if(isSDPresent()){
				populate_movieexplorer_list(new File("/storage/extSdCard"));
			}
		}else if(menuchoice.equalsIgnoreCase( Environment.getExternalStorageDirectory().getAbsolutePath()+"/Movies")){
			//Appel la fonction pour remplir notre liste d'items
			populate_movieexplorer_list(currentDir);
			if(isSDPresent()){
				populate_movieexplorer_list(new File("/storage/extSdCard"));
			}
		}
		else {
			//Appel la fonction pour remplir notre liste d'items
			populateFileExplorerList(currentDir);
		}



		//Appel la fonction pour remplir la listview avec les items
		populate_FileExplorer_Item_ListView();

		//Appel la fonction qui gère les click sur les items
		explorerFile_Click_on_Item();

		//Appel la fonction qui gère le long click sur les items
		explorerFile_Long_Click_on_Item();

		//Toast.makeText(getBaseContext(), currentDir.getAbsolutePath(), Toast.LENGTH_LONG).show();
	}

	public boolean isSDPresent(){
		return new File("/storage/extSdCard").canRead();
	}

	/**
	 * Fonction qui remplit une liste avec les dossiers et les fichiers
	 * du dossier qu'on lui passe comme paramètre
	 * @param dir
	 */
	private void populateFileExplorerList(File dir) {

		/*On récupère tous les dossiers et fichiers du dossier qu'on passe en paramètre et on met
		le résultat dans un tableau
		 */
		File[] dirs = dir.listFiles();

		//Récupère le textview qui affiche le chemin de l'exploreur
		TextView path = (TextView) findViewById(R.id.path);

		//Réecriture du chemin des fichiers
		if(currentDir.getAbsolutePath().equalsIgnoreCase
				(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures")){
			path.setText("");
		}
		else if(currentDir.getAbsolutePath().contains
				(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/")){
			path.setText(currentDir.getAbsolutePath().replace
					(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures/", ""));
		}


		else if(currentDir.getAbsolutePath().equalsIgnoreCase
				(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Movies")){
			path.setText("");
		} else if(currentDir.getAbsolutePath().contains
				(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Movies/")){
			path.setText(currentDir.getAbsolutePath().replace
					(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Movies/", ""));
		}

		else if(currentDir.getAbsolutePath().equalsIgnoreCase("/storage/extSdCard")){
			path.setText("");
		} else if(currentDir.getAbsolutePath().contains("/storage/extSdCard")){
			path.setText(currentDir.getAbsolutePath().replace("/storage/extSdCard/", ""));
		}

		else if(currentDir.getAbsolutePath().equalsIgnoreCase
				(Environment.getExternalStorageDirectory().getAbsolutePath())){
			path.setText("");
		}  else if(currentDir.getAbsolutePath().contains
				(Environment.getExternalStorageDirectory().getAbsolutePath())){
			path.setText(currentDir.getAbsolutePath().replace
					(Environment.getExternalStorageDirectory().getAbsolutePath()+"/", ""));
		}

		else if(currentDir.getAbsolutePath().equalsIgnoreCase
				(getFilesDir().getPath() + "/repository")){
			path.setText("");
		} else if(currentDir.getAbsolutePath().contains
				(getFilesDir().getPath() + "/repository")){
			path.setText(currentDir.getAbsolutePath().replace
					(getFilesDir().getPath() + "/repository/", ""));
		}

		//On vide la liste des items
		myItems.clear();

		//Pour chaque élément du tableau dirs (que je nomme item)
		for(File item:dirs){

			//On récupère la dernière date de modification de l'item
			Date lastModified = new Date(item.lastModified());

			//On instancie un Dateformat pour formater notre date suivant un format exact
			DateFormat formater = DateFormat.getDateTimeInstance();

			//Application du formater sur la date
			String date_modified = formater.format(lastModified);

			//Si l'item est un dossier
			if(item.isDirectory()){

				//On récupère la liste des sous-éléments de l'item
				File[] sub_dirs = item.listFiles();

				//On instancie une variable pour le nombre de sous-éléments de notre item
				int nb_sub_dirs = 0;

				//On déclare une chaîne de caractère qui va prendre la valeur du nb_sub_dirs
				String nb_sub_items;

				//Si la liste des sous-éléments est différente de null
				if(sub_dirs != null){

					//Récupérer la taille de la liste
					nb_sub_dirs = sub_dirs.length;
				}

				//Si la taille des sous éléments est égale à 0 ou à 1
				if(nb_sub_dirs == 0 || nb_sub_dirs == 1){

					//Mettre à côté du nombre élément
					nb_sub_items = nb_sub_dirs+" "+getString(R.string.element);
				}else{

					//Sinon mettre éléments
					nb_sub_items = nb_sub_dirs+" "+getString(R.string.elements);
				}

				/*
				On crée un item avec les informations qu'on a récupéré et on
				l'ajoute à notre liste d'item
				 */
				myItems.add(new FileExplorerList_Item(item.getName(), nb_sub_items, date_modified,
						item.getAbsolutePath(), 0, R.drawable.folder));

			}
			//Le cas d'un fichier
			else{

				/*
				On crée l'item de type fichier avec les informations propres du
				fichier et on l'ajoute à notre liste d'item
				 */
				myItems.add(new FileExplorerList_Item(item.getName(), item.length()+" Byte",
						date_modified, item.getAbsolutePath(), 1, R.drawable.document));
			}
		}

	}

	public boolean is_picture(File file){
		String filename = file.getName();
		String filenameArray[] = filename.split("\\.");
		String extension = filenameArray[filenameArray.length-1];
		if(extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg") ||extension.equalsIgnoreCase("png")){
			return true;
		}else{
			return false;
		}
	}

	public boolean is_movie(File file){
		String filename = file.getName();
		String filenameArray[] = filename.split("\\.");
		String extension = filenameArray[filenameArray.length-1];
		if(extension.equalsIgnoreCase("mp4") || extension.equalsIgnoreCase("avi")){
			return true;
		}else{
			return false;
		}
	}

	public void populate_movieexplorer_list(File currentDir){


		if(currentDir.isDirectory()){
			for(File child : currentDir.listFiles()){
				if(child.isDirectory()){
					populate_movieexplorer_list(child);
				}else{
					if(is_movie(child)){

						//On récupère la dernière date de modification de l'item
						Date lastModified = new Date(child.lastModified());

						//On instancie un Dateformat pour formater notre date suivant un format exact
						DateFormat formater = DateFormat.getDateTimeInstance();

						//Application du formater sur la date
						String date_modified = formater.format(lastModified);

						myItems.add(new FileExplorerList_Item(child.getName(), child.length()+" Byte",
								date_modified, child.getAbsolutePath(), 1, R.drawable.document));
					}
				}
			}
		}


	}

	public void populate_imageexplorer_list(File currentDir){


		if(currentDir.isDirectory()){
			for(File child : currentDir.listFiles()){
				if(child.isDirectory()){
					populate_imageexplorer_list(child);
				}else{
					if(is_picture(child)){

						//On récupère la dernière date de modification de l'item
						Date lastModified = new Date(child.lastModified());

						//On instancie un Dateformat pour formater notre date suivant un format exact
						DateFormat formater = DateFormat.getDateTimeInstance();

						//Application du formater sur la date
						String date_modified = formater.format(lastModified);

						myItems.add(new FileExplorerList_Item(child.getName(), child.length()+" Byte",
								date_modified, child.getAbsolutePath(), 1, R.drawable.document));
					}
				}
			}
		}


	}

	/**
	 * Fonction qui remplit la listview avec la liste des items
	 */
	private void populate_FileExplorer_Item_ListView() {

		//On crée un adaptateur pour notre liste
		ArrayAdapter<FileExplorerList_Item> adapter = new FileExplorer_Item_ListAdapter();

		//On récupère la listview
		ListView list = (ListView) findViewById(R.id.FileExplorer_Item_ListView);

		//On passe l'adaptateur à la liste
		list.setAdapter(adapter);
	}


	private void manage_file_icon(FileExplorerList_Item file, ImageView item_icon){
		String filename = file.getItem_name();
		String filenameArray[] = filename.split("\\.");
		String extension = filenameArray[filenameArray.length-1];

		if(extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("png")){
			item_icon.setImageResource(R.drawable.ic_pictures);
		} else if(extension.equalsIgnoreCase("mp4") || extension.equalsIgnoreCase("avi")){
			item_icon.setImageResource(R.drawable.ic_movie);
		} else if(extension.equalsIgnoreCase("doc") || extension.equalsIgnoreCase("docx") || extension.equalsIgnoreCase("odt")){
			item_icon.setImageResource(R.drawable.ic_word);
		} else if(extension.equalsIgnoreCase("apk")){
			item_icon.setImageResource(R.drawable.ic_apk);
		} else if(extension.equalsIgnoreCase("pdf")) {
			item_icon.setImageResource(R.drawable.ic_pdf);
		} else if(extension.equalsIgnoreCase("ppt") || extension.equalsIgnoreCase("pptx") || extension.equalsIgnoreCase("pptm")){
			item_icon.setImageResource(R.drawable.ic_ppt);
		} else if(extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase("xlsx") || extension.equalsIgnoreCase("xlsb")){
			item_icon.setImageResource(R.drawable.ic_excel);
		} else if(extension.equalsIgnoreCase("zip")){
			item_icon.setImageResource(R.drawable.ic_zipi);
		} else if(extension.equalsIgnoreCase("gzip")){
			item_icon.setImageResource(R.drawable.ic_gzip);
		} else if(extension.equalsIgnoreCase("rar")){
			item_icon.setImageResource(R.drawable.ic_rar);
		} else if(extension.equalsIgnoreCase("mp3") || extension.equalsIgnoreCase(".ogg") || extension.equalsIgnoreCase(".m4a")){
			item_icon.setImageResource(R.drawable.ic_music);
		} else if(extension.equalsIgnoreCase("html")){
			item_icon.setImageResource(R.drawable.ic_html);
		} else{
			item_icon.setImageResource(file.getItem_icon());
		}
	}

	/**
	 * Fonction qui gére les clicks sur les éléments de la liste
	 */
	private void explorerFile_Click_on_Item() {

		//On récupère la listview
		ListView list = (ListView) findViewById(R.id.FileExplorer_Item_ListView);

		//On met un listener sur les éléments de la listview
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			/**
			 * Fonction qui gère le click sur un élément de la liste
			 * @param parent
			 * @param view
			 * @param position
			 * @param id
			 * @Override
			 */
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				//On récupère l'élément sur lequel on a appuyé
				FileExplorerList_Item item_clicked = myItems.get(position);

				//Technique pour ne pas garder une trace des séléctions de l'activité précédente
				for (int i = 0; i < myItems.size(); i++) {
					myItems.get(i).setChecked(false);
				}

				//Vider la liste des éléments cochés
				checked_items.clear();

				//On repopule la liste des items
				//populateFileExplorerList(currentDir);

				//On recrée les vues de la listeView
				populate_FileExplorer_Item_ListView();

				//On définit une variable qui contient le chemin de l'élément séléctioné
				String extra = item_clicked.getItem_path();

				//Si l'item est un dossier
				if (item_clicked.getItem_type() == 0) {

					//On déclare un intent (un argument qu'on passe entre les activités)
					Intent intent;

					//On crée l'intent avec le context de l'application et l'activité cible
					intent = new Intent(getApplicationContext(), FileExplorer_activity.class);

					//On donne à l'intent le nom <path> et on lui passe la valeur de extra
					intent.putExtra("path", extra);

					intent.putExtra("menuchoice", menuchoice);

					//On relance l'activité
					startActivity(intent);

				} else if (item_clicked.getItem_name().contains(".zip")) {

					String path = currentDir.getAbsolutePath() + "/tmp";

					new File(path).mkdir();

					Zipper z = new Zipper();
					try {
						z.unzip(item_clicked.getItem_path(), path);
						//On déclare un intent (un argument qu'on passe entre les activités)
						Intent intent;

						//On crée l'intent avec le context de l'application et l'activité cible
						intent = new Intent(getApplicationContext(), FileExplorer_activity.class);

						//On donne à l'intent le nom <path> et on lui passe la valeur de extra
						intent.putExtra("path", path);

						intent.putExtra("menuchoice", menuchoice);

						//On relance l'activité
						startActivity(intent);

					} catch (IOException e) {
						Toast.makeText(getApplicationContext(), "Probleme d'aperçu de l'archive", Toast.LENGTH_SHORT).show();
					}
				} else if (is_picture(new File(item_clicked.getItem_path()))) {
					Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(item_clicked.getItem_path()).build();
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
				}

				//Le cas d'un fichier
				else {

					//On affiche un message comme quoi c'est un fichier et qu'on ne peut l'ouvrir
					Toast.makeText(getApplicationContext(), item_clicked.getItem_name() + " "
							+ getString(R.string.toast_message), Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	/**
	 * Fonction qui gère le click long sur un élément pour le séléctionner
	 */
	private void explorerFile_Long_Click_on_Item() {

		//On récupère la listview
		ListView list = (ListView) findViewById(R.id.FileExplorer_Item_ListView);

		list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

				//On récupère l'élément sur lequel on a appuyé
				FileExplorerList_Item item_clicked = myItems.get(position);

				/*Si notre liste d'éléments cochés contient l'élément sur lequel on a fait un long
				click*/
				if (checked_items.contains(item_clicked.getItem_path())) {

					//On supprime l'élément de la liste
					checked_items.remove(item_clicked.getItem_path());

					//Et on met sa valeur cochée à false
					item_clicked.setChecked(false);

					//Mise à jour des vues pour appliquer le background
					populate_FileExplorer_Item_ListView();

					//Message console
					Log.i("Check", checked_items.toString());
				} else {

					//Ajout de l'élément à la liste des éléments cochés
					checked_items.add(item_clicked.getItem_path());

					//On met sa valeur cochée à true
					item_clicked.setChecked(true);

					//Mise à jour des vues pour appliquer le background
					populate_FileExplorer_Item_ListView();

					//Message console
					Log.i("Check", checked_items.toString());
				}

				//On déclare un message pour l'utilisateur
				String selection_message;

				//Si on a 0 ou 1 élément dans notre liste
				if (checked_items.size() == 0 || checked_items.size() == 1) {

					//Un message sans les s
					selection_message = checked_items.size() + " élément séléctioné.";

					//Sinon
				} else {

					//Un message avec les s
					selection_message = checked_items.size() + " éléments séléctionés.";
				}

				//On affiche le message à l'utilisateur via un toast
				Toast.makeText(getApplicationContext(), selection_message, Toast.LENGTH_LONG).show();

				return true;
			}
		});
	}


	/**
	 * Fonction qui ajoute des éléments à la bare de notre activité
	 * @param menu
	 * @return boolean
	 * @Override
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		if(menuchoice.equalsIgnoreCase(getFilesDir().getPath() + "/repository")){
			getMenuInflater().inflate(R.menu.menu_repository_explorer, menu);
		}else{
			getMenuInflater().inflate(R.menu.menu_file_explorer, menu);
		}

		return true;
	}

	public void after_zip(){
		checked_items.clear();
		Toast.makeText(getBaseContext(), "Compression effectuée", Toast.LENGTH_SHORT).show();
		populateFileExplorerList(currentDir);
		populate_FileExplorer_Item_ListView();
	}

	public void after_unzip(){
		checked_items.clear();
		Toast.makeText(getBaseContext(), "Décompression effectuée", Toast.LENGTH_SHORT).show();
		populateFileExplorerList(currentDir);
		populate_FileExplorer_Item_ListView();
	}

	public void delete_checkeditems(){
		for (int i=0; i< checked_items.size(); i++)
		{
			File file = new File(checked_items.get(i));
			if(file.isFile())
				file.delete();
			else{
				//Supprimer d'abord les fichiers contenus dans le dossier
				File[] fichier = file.listFiles();
				for (File sous_file : fichier) {
					sous_file.delete();
				}
				//Ensuite supprimer ce dossier
				file.delete();
			}

		}
		checked_items.clear();
		Toast.makeText(getBaseContext(), "Suppression effectuée", Toast.LENGTH_SHORT).show();
		populateFileExplorerList(currentDir);
		populate_FileExplorer_Item_ListView();
	}

	/**
	 * Fonction qui gère l'appui sur des éléments du menu
	 * @param item
	 * @return boolean
	 * @Override
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_unzip) {
			Log.d("lieu", " "+currentDir.getName());
			switch(currentDir.getName()){

				case "sms":
					if(checked_items.size()==1){
						String file="";
        				
        				/*decompresser SMS*/
						Zipper z = new Zipper();
						try {
							file = z.unzip(checked_items.get(0));
						} catch (IOException e) {
							Toast.makeText(getApplicationContext(), "Probleme de decompression sms", Toast.LENGTH_SHORT).show();
						}
						//Log.d("nom fichier XML", file);
        				
        				/*liste sms a restaurer*/
						ArrayList<Sms> liste_sms_restauration = new ArrayList<Sms>();
						XmlFileSMS y = new XmlFileSMS(liste_sms_restauration, file);
						y.lire_sms_xml();
						//Log.d("XML", "Lecture du xml");

						//restauration des SMS
						if(checked_items.get(0).contains("RECU"))
							restaure_Sms(liste_sms_restauration, true);
						else
							restaure_Sms(liste_sms_restauration, true);

						//suppressione du fichier temporaire
						File f =new File(file);
						File[] fichier = f.listFiles();
						for (File sous_file : fichier) {
							sous_file.delete();
						}
						//Ensuite supprimer ce dossier
						f.delete();
                        
                        /*message a lutilisateur*/
						after_unzip();

					}
					else
						Toast.makeText(getApplicationContext(), "Un élément a la fois.", Toast.LENGTH_SHORT).show();
					break;

				case "contacts":

					if(checked_items.size()==1){
						String file="";
        				
        				/*decompresser CONTACTS*/
						Zipper z = new Zipper();
						try {
							file = z.unzip(checked_items.get(0));
						} catch (IOException e) {
							Toast.makeText(getApplicationContext(), "Probleme de decompression contacts", Toast.LENGTH_SHORT).show();
						}
						Log.d("nom fichier XML", file);
        				
        				/*liste Contacts a restaurer*/
						ArrayList<Contact> liste_contact_restauration = new ArrayList<Contact>();
						XmlFileCONTACTS y = new XmlFileCONTACTS(liste_contact_restauration, file);
						y.lire_contact_xml();
						Log.d("XML", "Lecture du xml");

						//restauration les contacts
						restaure_Contact(liste_contact_restauration);


						//suppressione du fichier temporaire
						File f =new File(file);
						File[] fichier = f.listFiles();
						for (File sous_file : fichier) {
							sous_file.delete();
						}
						//Ensuite supprimer ce dossier
						f.delete();
                        
                        /*message a lutilisateur*/
						after_unzip();

					}
					else
						Toast.makeText(getApplicationContext(), "Un élément a la fois.", Toast.LENGTH_SHORT).show();
					break;

				case "photos":

					Zipper zepi = new Zipper();
					if (checked_items.size() == 1){

						File file = new File(checked_items.get(0));
						try {
							zepi.unzip(file.getAbsolutePath(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/");

						} catch (IOException e) {
							e.printStackTrace();
						}
						after_unzip();
					}else if (checked_items.size() == 0){
						Toast.makeText(getBaseContext(), "Aucun élément séléctionné!", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(getApplicationContext(), "Un élément a la fois.", Toast.LENGTH_SHORT).show();
					}

					break;

				case "videos":

					zepi = new Zipper();
					if (checked_items.size() == 1){


						File file = new File(checked_items.get(0));
						try {
							zepi.unzip(file.getAbsolutePath(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/Movies/");
						} catch (IOException e) {
							e.printStackTrace();
						}
						after_unzip();
					}else if (checked_items.size() == 0){
						Toast.makeText(getBaseContext(), "Aucun élément séléctionné!", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(getApplicationContext(), "Un élément a la fois.", Toast.LENGTH_SHORT).show();
					}

					break;

				case "folders":

					zepi = new Zipper();
					if (checked_items.size() == 1){


						File file = new File(checked_items.get(0));
						try {
							zepi.unzip(file.getAbsolutePath());
						} catch (IOException e) {
							e.printStackTrace();
						}
						after_unzip();
					}else if (checked_items.size() == 0){
						Toast.makeText(getBaseContext(), "Aucun élément séléctionné!", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(getApplicationContext(), "Un élément a la fois.", Toast.LENGTH_SHORT).show();
					}

					break;

				default:
					Log.e("FileExplorer_activity.java", "erreur d'emplacement");
					break;
			}



			return true;
		}

		if(id == R.id.action_zip){

			switch (currentDir.getName()){

				case "Pictures":

					Zipper zepi = new Zipper();
					if(checked_items.size()>1){
						Log.d("Debug >1 :" , getFilesDir()+"/repository/photos/"+currentDir.getName()+".zip");
						try {
							zepi.zip(checked_items, getFilesDir()+"/repository/photos/"+currentDir.getName()+".zip");

						} catch (IOException e) {
							e.printStackTrace();
						}
						after_zip();
					}else if (checked_items.size() == 1){


						File file = new File(checked_items.get(0));
						String File_name = "";
						String tmp [] = file.getName().split("\\.");
						for(int i = 0 ; i < tmp.length-1 ; i++){
							File_name += tmp[i];
						}
						try {
							zepi.zip(checked_items, getFilesDir() + "/repository/photos/" + File_name+".zip");

						} catch (IOException e) {
							e.printStackTrace();
						}
						after_zip();

					}else if (checked_items.size() == 0){
						Toast.makeText(getBaseContext(), "Aucun élément séléctionné!", Toast.LENGTH_SHORT).show();
					}

					break;

				case "Movies":

					zepi = new Zipper();
					if(checked_items.size()>1){
						Log.d("Debug >1 :" , getFilesDir()+"/repository/videos/"+currentDir.getName()+".zip");
						try {
							zepi.zip(checked_items, getFilesDir()+"/repository/videos/"+currentDir.getName()+".zip");

						} catch (IOException e) {
							e.printStackTrace();
						}
						after_zip();
					}else if (checked_items.size() == 1){


						File file = new File(checked_items.get(0));
						String File_name = file.getName();
						try {
							zepi.zip(checked_items, getFilesDir() + "/repository/videos/" + File_name+".zip");

						} catch (IOException e) {
							e.printStackTrace();
						}
						after_zip();
					}else if (checked_items.size() == 0){
						Toast.makeText(getBaseContext(), "Aucun élément séléctionné!", Toast.LENGTH_SHORT).show();
					}

					break;

				default:

					zepi = new Zipper();
					if(checked_items.size()>1){
						Log.d("Debug >1 :" , getFilesDir()+"/repository/folders/"+currentDir.getName()+".zip");
						try {
							zepi.zip(checked_items, getFilesDir()+"/repository/folders/"+currentDir.getName()+".zip");

						} catch (IOException e) {
							e.printStackTrace();
						}
						after_zip();
					}else if (checked_items.size() == 1){


						File file = new File(checked_items.get(0));
						String File_name = file.getName();
						try {
							zepi.zip(checked_items, getFilesDir() + "/repository/folders/" + File_name+".zip");

						} catch (IOException e) {
							e.printStackTrace();
						}
						after_zip();
					}else if (checked_items.size() == 0){
						Toast.makeText(getBaseContext(), "Aucun élément séléctionné!", Toast.LENGTH_SHORT).show();
					}

					break;
			}

		}

		if(id == R.id.action_delete){
			delete_checkeditems();
		}
		 /*si user chosit de tout selectionné*/
		else if(id == R.id.action_select_all){

			for(int i = 0 ; i < myItems.size() ; i++ ){

				if (!checked_items.contains(myItems.get(i).getItem_path())){

					//Ajout de l'élément à la liste des éléments cochés
					checked_items.add(myItems.get(i).getItem_path());

					//On met sa valeur cochée à true
					myItems.get(i).setChecked(true);
				}
			}
			//Mise à jour des vues pour appliquer le background
			populate_FileExplorer_Item_ListView();

			return true;
		}
        /*si user chosit de tout déselectionné*/
		else if(id == R.id.action_unselect_all){

			for(int i = 0 ; i < myItems.size() ; i++ ){

				if (checked_items.contains(myItems.get(i).getItem_path())) {

					//On supprime l'élément de la liste
					checked_items.remove(myItems.get(i).getItem_path());

					//Et on met sa valeur cochée à false
					myItems.get(i).setChecked(false);

				}
			}
			//Mise à jour des vues pour appliquer le background
			populate_FileExplorer_Item_ListView();
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Classe interne
	 */
	private class FileExplorer_Item_ListAdapter extends ArrayAdapter<FileExplorerList_Item>{

		/**
		 * Constructeur de notre adapter
		 */
		public FileExplorer_Item_ListAdapter() {

			/**
			 * Appel du constructeur de la classe mère, on lui passe le context,
			 * le layout de chaque élément et la liste des éléments
			 */
			super(FileExplorer_activity.this, R.layout.fileexplorerlist_item, myItems);
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
			FileExplorerList_Item currentItem = myItems.get(position);

			//On remplit la vue personnalisée
			ImageView item_icon = (ImageView) itemView.findViewById(R.id.item_icon);
			manage_file_icon(currentItem, item_icon);

			TextView item_name = (TextView) itemView.findViewById(R.id.item_name);
			item_name.setText(currentItem.getItem_name());

			TextView item_extra = (TextView) itemView.findViewById(R.id.item_extra);
			item_extra.setText(currentItem.getItem_extra_information());

			TextView item_date = (TextView) itemView.findViewById(R.id.item_date);
			item_date.setText(currentItem.getItem_last_modified_date());

			if(currentItem.isChecked()){
				itemView.setBackgroundColor(Color.parseColor("#8027ae60"));
			}else{
				itemView.setBackgroundColor(Color.TRANSPARENT);
			}

			//On retourne la vue
			return itemView;
		}
	}




	void restaure_Sms(ArrayList<Sms> Sms_restauration, boolean reçu){
		ContentValues values = new ContentValues();

		for(int i = 0 ; i < Sms_restauration.size() ; i++){
			Log.d("SMS n° "+i+": ", "Msg : "+Sms_restauration.get(i).getMsg());
			values.put("address", Sms_restauration.get(i).getNum());
			values.put("person", Sms_restauration.get(i).getNom());
			values.put("date", Sms_restauration.get(i).getDate());
			values.put("body", Sms_restauration.get(i).getMsg());

			values.put("type", Sms_restauration.get(i).getHeure());

			if(reçu)
				getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
			else
				getContentResolver().insert(Uri.parse("content://sms/sent"), values);
		}

	}






	/**
	 * Methode restaure_Contact permet de restaurer les contacts
	 * a partir d'une liste de contacts
	 *
	 * @param Contacts_restauration
	 * */
	void restaure_Contact(ArrayList<Contact> Contacts_restauration){
		for(int i = 0 ; i < Contacts_restauration.size() ; i++){
			ajoutContact(Contacts_restauration.get(i).getNom(), Contacts_restauration.get(i).getNum(), Contacts_restauration.get(i).getMail());
		}


	}





	/**
	 * Methode ajoutContact cree un liste d'operation qu'elle execute
	 * qui consite a cree un contact "par etape" : nom , num, mail
	 * @param nom
	 * @param num
	 * @param mail
	 * */
	private void ajoutContact(String nom, String num, String mail) {
		ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
		operationList.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
				.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
				.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
				.build());

		// nom
		operationList.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)
				.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
				.withValue(StructuredName.FAMILY_NAME, nom)
				.build());

		//numero de telephone
		operationList.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)
				.withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
				.withValue(Phone.NUMBER, num)
				.withValue(Phone.TYPE, Phone.TYPE_HOME)
				.build());

		//mail si existant
		if(mail != null){
			operationList.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
					.withValueBackReference(Data.RAW_CONTACT_ID, 0)

					.withValue(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
					.withValue(Email.DATA, mail)
					.withValue(Email.TYPE, Email.TYPE_WORK)
					.build());
		}

		try{
			getContentResolver().applyBatch(ContactsContract.AUTHORITY, operationList);
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		if(currentDir.getName().equals("tmp")){
			Toast.makeText(getApplicationContext(), currentDir.getAbsolutePath(), Toast.LENGTH_LONG).show();
			supprimer_recursif(currentDir.getAbsolutePath());
		}
	}

	/**
	 * Methode supprimer_recursif
	 * permet de suppression d'un repertoire de façon recursif
	 *
	 * @param emplacement chemin vers le repertoire a supprimé
	 * */
	public void supprimer_recursif( String emplacement )
	{
		File path = new File( emplacement );
		if( path.isDirectory() )
			for( File file : path.listFiles())
				supprimer_recursif(file.getAbsolutePath());

		path.delete();
	}


}