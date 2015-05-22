package com.capella.zipit.objet;

/**

 * La classe Sms permet de materielliser un sms
 * avec tous les attribues qui vont avec 
 * num, nom, date, heure, message
 */
public class Sms {
	
	/*numero de telephone*/
	private String num;
	/*nom du destinataire*/
	private String nom;
	/*date du msg*/
	private String date;
	/*heure du msg*/
	private String heure;

	public int getSms_icon() {
		return sms_icon;
	}

	public void setSms_icon(int sms_icon) {
		this.sms_icon = sms_icon;
	}

	private int sms_icon;

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	/*texte du msg*/
	private String msg;

	private boolean checked;
	
	/**
	 * Constructeur d'un message 
	 * 
	 * @param numero telephone
	 * @param nom du destinataire
	 * @param date du message
	 * @param heure du message
	 * @param texte du message
	 * */
	public Sms(String num, String nom, String date, String heure, String msg, int sms_icon) {
		this.setNum(num);
		this.setNom(nom);
		this.setDate(date);
		this.setHeure(heure);
		this.setMsg(msg);
		this.checked=false;
		this.setSms_icon(sms_icon);
	}
	
	
	/**
	 * Getteur getNum recupere le numero de telephone
	 * @return numero de telephone
	 */
	public String getNum() {
		return num;
	}
	
	
	/**
	 * Setteur setNum met à jour le numero de telephone
	 * @param numero de telephone
	 */
	public void setNum(String num) {
		this.num = num;
	}

	
	/**
	 * Getteur getNom recupere le nom du destinataire
	 * @return nom du destinataire
	 */
	public String getNom() {
		return nom;
	}
	
	/**
	 * Setteur setNom met à jour le nom du destinataire
	 * @param nom du destinataire
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}

	
	/**
	 * Getteur getDate recupere la date du msg
	 * @return date du message
	 */
	public String getDate() {
		return date;
	}
	
	/**
	 * Setteur setDate met à jour la date du message
	 * @param date du message
	 */
	public void setDate(String date) {
		this.date = date;
	}

	
	/**
	 * Getteur getHeure recupere l'heure du message
	 * @return heure du message
	 */
	public String getHeure() {
		return heure;
	}
	
	/**
	 * Setteur setNum met à jour le numero de telephone
	 * @param numero de telephone
	 */
	public void setHeure(String heure) {
		this.heure = heure;
	}

	
	/**
	 * Getteur getMsg recupere le message
	 * @return message
	 */
	public String getMsg() {
		return msg;
	}
	
	/**
	 * Setteur setMsg met à jour le message
	 * @param message
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}


	public boolean isChecked() {
		return checked;
	}
}
