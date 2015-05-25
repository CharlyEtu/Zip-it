package com.capella.zipit.objet;




/**
 * La classe Contact permet de materielliser un contact
 * avec tous les attribues qui vont avec 
 * 
 */
public class Contact {

	/*nom du contact*/
	private String nom;
	
	/*numero du contact*/
	private String num;
	
	/*mail du contact*/
	private String mail;
	
	/*contact selectionné*/
	private boolean checked;
	
	/*icone du contact*/
	private int contact_icon;
	
	
	
	
	
	/**
	 * Constructeur d'un contact 
	 * 
	 * @param nom 
	 * @param num
	 * */
	public Contact(String nom, String num) {
		
		this.nom = nom;
		this.num = num;
		
	}
	
	
	
	
	
	
	
	/**
	 * Constructeur d'un contact 
	 * 
	 * @param nom 
	 * @param num
	 * @param mail
	 * */
	public Contact(String nom, String num, String mail) {
		
		this.nom = nom;
		this.num = num;
		this.mail = mail;
		
	}

	
	
	
	
	
	/**
	 * Getteur getNom recupere le nom du contact
	 * @return String
	 */
	public String getNom() {
		return nom;
	}

	
	
	
	
	
	
	/**
	 * Setteur setNom met à jour le nom du contact
	 * @param nom
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}

	
	
	
	
	
	/**
	 * Getteur getNum recupere le numero de telephone
	 * @return String
	 */
	public String getNum() {
		return num;
	}

	
	
	
	
	
	/**
	 * Setteur setNum met à jour le numero de telephone
	 * @param num
	 */
	public void setNum(String num) {
		this.num = num;
	}

	
	
	
	
	
	/**
	 * Getteur getMail recupere l'email du contact
	 * @return mail
	 */
	public String getMail() {
		return mail;
	}
	
	
	
	
	

	/**
	 * Setteur setMail met à jour l'email
	 * @param mail
	 */
	public void setMail(String mail) {
		this.mail = mail;
	}

	
	







	public boolean isChecked() {
		return checked;
	}






	public void setChecked(boolean checked) {
		this.checked = checked;
	}






	public int getContact_icon() {
		return contact_icon;
	}






	public void setContact_icon(int contact_icon) {
		this.contact_icon = contact_icon;
	}
	
	
}
