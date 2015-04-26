package com.capella.zipit;

/**
 * Created by capella on 22/04/15.
 */


public class FileExplorerList_Item {


    private String item_name;
    private String item_extra_information;
    private String item_last_modified_date;
    private String item_path;
    private int item_type;
    private int item_icon;
	private boolean item_checked;

	/**
	 * Constructeur de la classe
	 * @param item_name
	 * @param item_extra_information
	 * @param item_last_modified_date
	 * @param item_path
	 * @param item_type
	 * @param item_icon
	 */
    public FileExplorerList_Item(String item_name, String item_extra_information,
                                 String item_last_modified_date, String item_path,
                                 int item_type, int item_icon) {
        this.item_name = item_name;
        this.item_extra_information = item_extra_information;
        this.item_last_modified_date = item_last_modified_date;
        this.item_path = item_path;
        this.item_type = item_type;
        this.item_icon = item_icon;
		this.item_checked = false;
    }

	/**
	 * Getter du nom de l'élément
	 * @return String
	 */
    public String getItem_name() {
        return item_name;
    }

	/**
	 * Setter du nom de l'élément
	 * @param item_name
	 */
    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

	/**
	 * Getter des extras informations de l'élément
	 * @return String
	 */
    public String getItem_extra_information() {
        return item_extra_information;
    }

	/**
	 * Setter des extras informations
	 * @param item_extra_information
	 */
    public void setItem_extra_information(String item_extra_information) {
        this.item_extra_information = item_extra_information;
    }

	/**
	 * Getter de la dernière date de modification de l'élément
	 * @return String
	 */
    public String getItem_last_modified_date() {
        return item_last_modified_date;
    }

	/**
	 * Setter de la dernière date de modification de l'élément
	 * @param item_last_modified_date
	 */
    public void setItem_last_modified_date(String item_last_modified_date) {
        this.item_last_modified_date = item_last_modified_date;
    }

	/**
	 * Getter du chemin de l'élément
	 * @return String
	 */
    public String getItem_path() {
        return item_path;
    }

	/**
	 * Setter du chemin de l'élément
	 * @param item_path
	 */
    public void setItem_path(String item_path) {
        this.item_path = item_path;
    }

	/**
	 * Getter du type de l'élément
	 * @return String
	 */
    public int getItem_type() {
        return item_type;
    }

	/**
	 * Setter du type de l'élément
	 * @param item_type
	 */
    public void setItem_type(int item_type) {
        this.item_type = item_type;
    }

	/**
	 * Getter de l'identifiant de l'icône de l'élément
	 * @return int
	 */
    public int getItem_icon() {
        return item_icon;
    }

	/**
	 * Setter de l'identifiant de l'icône de l'élément
	 * @param item_icon
	 */
    public void setItem_icon(int item_icon) {
        this.item_icon = item_icon;
    }

	public boolean isChecked() {
		return item_checked;
	}

	public void setChecked(boolean checked) {
		this.item_checked = checked;
	}
}
