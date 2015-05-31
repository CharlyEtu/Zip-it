package com.capella.zipit.activity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
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
import com.capella.zipit.tools.XmlFileCONTACTS;
import com.capella.zipit.tools.Zipper;

@SuppressLint("SimpleDateFormat")
public class ContactExplorer_activity extends ActionBarActivity {

	private android.support.v7.widget.Toolbar toolbar;

	/*liste Contact selectionner*/
	private ArrayList<Contact> liste_contact = new ArrayList<Contact>();
	/*pr la vue*/
	private ArrayList<Contact> contact_list = new ArrayList<Contact>();

	/*initialisation des buffers*/
	private String[] Contact_name=new String[4000],
			Contact_number=new String[4000],
			Contact_mail=new String[4000];

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


		//On met dans l'activité le layout de l'exploreur de fichiers
		setContentView(R.layout.activity_contact_explorer_activity);

		toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
		setSupportActionBar(toolbar);

		//Appel la fonction pour remplir notre liste de messages
		populateContactExplorerList();

		//Appel la fonction pour remplir la listview avec les messages
		populate_ContactExplorer_Item_ListView();


		//Appel la fonction qui gère le long click sur les items
		explorerContact_Long_Click_on_Item();

		//Appel la fonction qui gère les click sur les items
		//explorerContact_Click_on_Item();
	}






	/**
	 * Methode de poplation de notre vue 
	 * permet de recuperer les contacts du device
	 * */
	private void populateContactExplorerList() {
		String id = "";
		Log.d("populateContact :", "entre ");

		ContentResolver cr = getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {

				//on recupere ID du contact
				id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));

				//recupere le nom du contact
				Contact_name[pos] = cur.getString(cur.getColumnIndex(Phone.DISPLAY_NAME));
        		

        		/*traitement pour recuperer les nums du contact*/
				if(Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
				{
					Cursor pCur = cr.query(Phone.CONTENT_URI,null, Phone.CONTACT_ID +" = ?",new String[]{ id }, null);

					while (pCur.moveToNext())
					{
						//on recupere les num de tel du contact courant
						Contact_number[pos] = pCur.getString(pCur.getColumnIndex(Phone.NUMBER));
						break;
					}
					pCur.close();
				}
        		
        		
        		
        		/*traitement pour recuperer tous les adresses mail du contact*/
				Cursor mails = cr.query(Email.CONTENT_URI,null,Email.CONTACT_ID + " = " + id, null, null);
				while (mails.moveToNext())
				{
					//on recupere les adresses mails
					Contact_mail[pos] = mails.getString(mails.getColumnIndex(Email.DATA));
					break;
				}
				mails.close();


				//ajout a la liste de contact
				contact_list.add(new Contact(Contact_name[pos], Contact_number[pos], Contact_mail[pos]));

				pos++;


			}
		}

	}




	/**
	 * Fonction qui remplit la listview avec la liste des items
	 */
	private void populate_ContactExplorer_Item_ListView(){
		//On crée un adaptateur pour notre liste
		ArrayAdapter<Contact> adapter = new ContactExplorer_Item_ListAdapter();

		//On récupère la listview
		ListView list = (ListView) findViewById(R.id.SmsExplorer_Item_ListView);

		//On passe l'adaptateur à la liste
		list.setAdapter(adapter);
	}




	/**
	 * Methode explorerContact_Long_Click_on_Item permet de gerer 
	 * la selection d'un item ou plusieure items
	 * */
	private void explorerContact_Long_Click_on_Item() {
		//On récupère la listview
		ListView list = (ListView) findViewById(R.id.SmsExplorer_Item_ListView);

		list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

				//On récupère l'élément sur lequel on a appuyé
				Contact item_clicked = contact_list.get(position);

				/*Si notre liste d'éléments cochés contient l'élément sur lequel on a fait un long
				click*/
				if(liste_contact.contains(item_clicked)){

					//On supprime l'élément de la liste
					liste_contact.remove(item_clicked);

					//Et on met sa valeur cochée à false
					item_clicked.setChecked(false);

					//Mise à jour des vues pour appliquer le background
					populate_ContactExplorer_Item_ListView();

					//Message console
					Log.i("Check", liste_contact.toString());
				}else{

					//Ajout de l'élément à la liste des éléments cochés
					liste_contact.add(item_clicked);

					//On met sa valeur cochée à true
					item_clicked.setChecked(true);

					//Mise à jour des vues pour appliquer le background
					populate_ContactExplorer_Item_ListView();

					//Message console
					Log.i("Check", liste_contact.toString());
				}

				//On déclare un message pour l'utilisateur
				String selection_message;

				//Si on a 0 ou 1 élément dans notre liste
				if(liste_contact.size() == 0 || liste_contact.size() ==1){

					//Un message sans les s
					selection_message = liste_contact.size()+" élément séléctioné.";

					//Sinon
				}else{

					//Un message avec les s
					selection_message = liste_contact.size()+" éléments séléctionés.";
				}

				//On affiche le message à l'utilisateur via un toast
				Toast.makeText(getApplicationContext(), selection_message, Toast.LENGTH_LONG).show();

				return true;
			}
		});
	}



	/**
	 * Classe interne
	 */
	private class ContactExplorer_Item_ListAdapter extends ArrayAdapter<Contact>{

		/**
		 * Constructeur de notre adapter
		 */
		public ContactExplorer_Item_ListAdapter() {

			/**
			 * Appel du constructeur de la classe mère, on lui passe le context,
			 * le layout de chaque élément et la liste des éléments
			 */
			super(ContactExplorer_activity.this, R.layout.contactexplorerlist_item, contact_list);
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
				itemView = getLayoutInflater().inflate(R.layout.contactexplorerlist_item, parent,
						false);
			}

			//On récupère l'item à afficher
			Contact currentItem = contact_list.get(position);

			//On remplit la vue personnalisée
			ImageView item_icon = (ImageView) itemView.findViewById(R.id.contact_icon);
			item_icon.setImageResource(currentItem.getContact_icon());

			TextView item_name = (TextView) itemView.findViewById(R.id.contact_name);
			item_name.setText(currentItem.getNom());

			TextView item_extra = (TextView) itemView.findViewById(R.id.contact_tel);
			item_extra.setText(currentItem.getNum());



			if(currentItem.isChecked()){
				itemView.setBackgroundColor(Color.parseColor("#8027ae60"));
			}else{
				itemView.setBackgroundColor(Color.TRANSPARENT);
			}

			//On retourne la vue
			return itemView;
		}
	}










	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contact_explorer_activity, menu);
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
		String nomXml;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy_hh-mm"); //formateur de Date
        
        /*si user chosit de compressé la selection*/
		if (id == R.id.action_compresser) {

			nomXml = "CONTACTS_"+ sdf.format(new Date());
        		/*generer xml contenant sms selectionnés*/
			XmlFileCONTACTS x = new XmlFileCONTACTS(liste_contact, getFilesDir()+"/repository/contacts/"+nomXml+".xml", "CONTACTS");
			x.ecrire_contact_xml();
	            
	            /*COMPRESSION*/
			Zipper lol = new Zipper(9, false);
			try {
				lol.zip(getFilesDir()+"/repository/contacts/"+nomXml+".xml", getFilesDir()+"/repository/contacts/"+nomXml);
			} catch (IOException e) {

			}
	            
	            /*suppression xml temporaire*/
			File file = new File(getFilesDir()+"/repository/contacts/"+nomXml+".xml");
			if(file.isFile())
				file.delete();
				
				/*message a l'utilisateur*/
			after_zip();


			return true;
		}
        /*si user chosit de tout selectionné*/
		else if(id == R.id.action_select_all){

			for(int i = 0 ; i < contact_list.size() ; i++ ){

				if(!liste_contact.contains(contact_list.get(i))){
					//Ajout de l'élément à la liste des éléments cochés
					liste_contact.add(contact_list.get(i));

					//On met sa valeur cochée à true
					contact_list.get(i).setChecked(true);
				}

			}
			//Mise à jour des vues pour appliquer le background
			populate_ContactExplorer_Item_ListView();

			return true;
		}
        /*si user chosit de tout déselectionné*/
		else if(id == R.id.action_unselect_all){

			for(int i = 0 ; i < contact_list.size() ; i++ ){
				Log.d("contact DEBUG", "taille contact_list = "+contact_list.size()+" et  liste_contact = "+ liste_contact.size()+" i = "+i);

				if(liste_contact.contains(contact_list.get(i))){

					//On met sa valeur cochée à true
					contact_list.get(i).setChecked(false);

					//Ajout de l'élément à la liste des éléments cochés
					liste_contact.remove(liste_contact.indexOf(contact_list.get(i)));
				}


			}
			//Mise à jour des vues pour appliquer le background
			populate_ContactExplorer_Item_ListView();
		}

		return super.onOptionsItemSelected(item);
	}



//    /**
//     * Methode restaure_Contact permet de restaurer les contacts 
//     * a partir d'une liste de contacts
//     * 
//     * @param Contacts_restauration
//     * */
//    void restaure_Contact(ArrayList<Contact> Contacts_restauration){
//    	for(int i = 0 ; i < Contacts_restauration.size() ; i++){
//    		ajoutContact(Contacts_restauration.get(i).getNom(), Contacts_restauration.get(i).getNum(), Contacts_restauration.get(i).getMail());
//    	}
//    	
//    	
//    }
//    
//    /**
//     * Methode ajoutContact cree un liste d'operation qu'elle execute
//     * qui consite a cree un contact "par etape" : nom , num, mail
//     * @param nom
//     * @param num
//     * @param mail
//     * */
//    private void ajoutContact(String nom, String num, String mail) {
//        ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>(); 
//        operationList.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI) 
//                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null) 
//                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null) 
//                .build()); 
//
//        // nom 
//        operationList.add(ContentProviderOperation.newInsert(Data.CONTENT_URI) 
//                .withValueBackReference(Data.RAW_CONTACT_ID, 0) 
//                .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE) 
//                .withValue(StructuredName.FAMILY_NAME, nom) 
//                .build()); 
//       
//        //numero de telephone
//        operationList.add(ContentProviderOperation.newInsert(Data.CONTENT_URI) 
//                .withValueBackReference(Data.RAW_CONTACT_ID, 0) 
//                .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
//                .withValue(Phone.NUMBER, num)
//                .withValue(Phone.TYPE, Phone.TYPE_HOME)
//                .build());
//        
//        //mail si existant
//        if(mail != null){
//	        operationList.add(ContentProviderOperation.newInsert(Data.CONTENT_URI) 
//	                .withValueBackReference(Data.RAW_CONTACT_ID, 0)
//	
//	                .withValue(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
//	                .withValue(Email.DATA, mail)
//	                .withValue(Email.TYPE, Email.TYPE_WORK)
//	                .build());
//        }
//
//        try{ 
//            getContentResolver().applyBatch(ContactsContract.AUTHORITY, operationList); 
//        }catch(Exception e){ 
//            e.printStackTrace(); 
//        } 
//    }




	public void after_zip(){
		liste_contact.clear();
		Toast.makeText(getBaseContext(), "Compression effectuée", Toast.LENGTH_SHORT).show();
		populateContactExplorerList();
		populate_ContactExplorer_Item_ListView();
	}


}