package com.capella.zipit.activity;

/**
 * Created by capella on 22/04/15.
 */


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
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
import com.capella.zipit.objet.FileExplorerList_Item;
import com.capella.zipit.tools.Zipper;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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

		//Appel la fonction pour remplir notre liste d'items
        populateFileExplorerList(currentDir);

		//Appel la fonction pour remplir la listview avec les items
        populate_FileExplorer_Item_ListView();

		//Appel la fonction qui gère les click sur les items
		explorerFile_Click_on_Item();

		//Appel la fonction qui gère le long click sur les items
		explorerFile_Long_Click_on_Item();

		//Toast.makeText(getBaseContext(), currentDir.getAbsolutePath(), Toast.LENGTH_LONG).show();
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
        getMenuInflater().inflate(R.menu.menu_file_explorer, menu);
        return true;
    }

	public void after_zip(){
		checked_items.clear();
		Toast.makeText(getBaseContext(), "Compression effectuée", Toast.LENGTH_SHORT).show();
		populateFileExplorerList(currentDir);
		populate_FileExplorer_Item_ListView();
	}

	public void delete_checkeditems(){
		for (int i=0; i< checked_items.size(); i++)
		{
			File file = new File(checked_items.get(i));
			file.delete();
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
        if (id == R.id.action_settings) {
            return true;
        }

		if(id == R.id.action_zip){
			Zipper zepi = new Zipper();
			if(checked_items.size()>1){
				Log.d("Debug >1 :" , getFilesDir()+"/repository/folders/"+currentDir.getName()+".zip");
				try {
					zepi.zip(checked_items, getFilesDir()+"/repository/folders/"+currentDir.getName()+".zip");

				} catch (IOException e) {
					e.printStackTrace();
				}
				after_zip();
			}else if (checked_items.size() == 1){

				String name_zip = checked_items.get(0).replace(currentDir.getAbsolutePath()+"/", "");
				String final_name="";
				String [] tmp = name_zip.split("\\.");
				for(int i=0; i <tmp.length-1; i++){
					final_name = final_name+tmp[i];
				}
				Log.d("Debug ==1 :" , getFilesDir()+"/repository/folders/"+final_name+".zip" );
				try {
					zepi.zip(checked_items, getFilesDir() + "/repository/folders/" + final_name+".zip");

				} catch (IOException e) {
					e.printStackTrace();
				}
				after_zip();
			}else if (checked_items.size() == 0){
				Toast.makeText(getBaseContext(), "Aucun élément séléctionné!", Toast.LENGTH_SHORT).show();
			}
		}

		if(id == R.id.action_delete){
			delete_checkeditems();
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
            item_icon.setImageResource(currentItem.getItem_icon());

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
}
