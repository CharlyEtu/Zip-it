package com.capella.zipit.tools;

import com.capella.zipit.objet.Contact;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlFileCONTACTS {

	/*liste de contact*/
	private ArrayList<com.capella.zipit.objet.Contact> liste;


	/*nom fichier xml*/
	private String nom_fichier_xml;

	/*nom de la racine du xml*/
	private String racine;








	/**
	 * Constructeur XmlFile creer un objet XmlFile
	 * pour les ecriture de Contact
	 *
	 * @param l
	 * @param file
	 * @param racine
	 *
	 * */
	public XmlFileCONTACTS(ArrayList<Contact> l, String file, String racine) {
		liste = l;
		nom_fichier_xml = file;
		this.racine = racine;
	}








	/**
	 * Constructeur XmlFile creer un objet XmlFile
	 * pour la lecture de SMS
	 *
	 * @param l
	 * @param file
	 *
	 * */
	public XmlFileCONTACTS(ArrayList<Contact> l, String file) {
		liste = l;
		nom_fichier_xml = file;
	}










	/**
	 * Methode ecrire_contact_xml permet de creer un fichier XML
	 *
	 * */
	public void ecrire_contact_xml(){

		Element mail = null;
		Element num = null;
		
		/*récupération d'une instance de la classe "DocumentBuilderFactory"*/
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
		    
			/*création d'un parseur*/

			final DocumentBuilder builder = factory.newDocumentBuilder();
		    		
		    /* création d'un Document*/
			final Document document= builder.newDocument();
						
		    /*  création de l'Element racine*/
			final Element racine = document.createElement(this.racine);
			document.appendChild(racine);


			for(int i = 0 ; i < liste.size() ; i++ ){
		    	
		    	/* création d'un Contact*/
				final Element contact = document.createElement("contact");
				racine.appendChild(contact);


				final Element nom = document.createElement("nom");
				nom.appendChild(document.createTextNode(liste.get(i).getNom()));
			    
			    
			    /*num de tel*/
				if(liste.get(i).getNum() != null){
					num = document.createElement("numero");
					num.appendChild(document.createTextNode(liste.get(i).getNum()));
				}

				if(liste.get(i).getMail() != null){
					mail = document.createElement("mail");
					mail.appendChild(document.createTextNode(liste.get(i).getMail()));
				}



				contact.appendChild(nom);
			    /*ajout des fils du noeud parent*/
				if(liste.get(i).getNum() != null)
					contact.appendChild(num);
				if(liste.get(i).getMail() != null)
					contact.appendChild(mail);

			}
		    
		    /*affichage fichier ou sorti standard*/
			final TransformerFactory transformerFactory = TransformerFactory.newInstance();
			final Transformer transformer = transformerFactory.newTransformer();
			final DOMSource source = new DOMSource(document);
			final StreamResult sortie = new StreamResult(new File(nom_fichier_xml));
		    
				
		    /*prologue*/
			transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
		    		
		    /*formatage*/
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				
		    /*sortie*/
			transformer.transform(source, sortie);
		}
		catch (final ParserConfigurationException e) {
			e.printStackTrace();
		}
		catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		catch (TransformerException e) {
			e.printStackTrace();
		}

	}



	/**
	 * Methode lire_contact_xml permet de lire un fichier XML
	 *
	 * */
	public void lire_contact_xml(){
		// AFINIR pr la restauration

		/*récupération d'une instance de la classe "DocumentBuilderFactory"*/
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			/* création d'un parseur*/
			final DocumentBuilder builder = factory.newDocumentBuilder();

			File tmp = new File(nom_fichier_xml);
			File[] fichier = tmp.listFiles();System.out.println(fichier[0].getName());
			/*création d'un Document */
			final Document document= builder.parse(new File(nom_fichier_xml+"/"+fichier[0].getName()));

			/* récupération de l'Element racine */
			final Element racine = document.getDocumentElement();

			//Affichage de l'élément racine
			//	System.out.println("\n*************RACINE************");
			//System.out.println(racine.getNodeName());

			/*récupération des contacts*/
			final NodeList racineNoeuds = racine.getChildNodes();
			final int nbRacineNoeuds = racineNoeuds.getLength();

			for (int i = 0; i<nbRacineNoeuds; i++) {
				if(racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
					final Element contact = (Element) racineNoeuds.item(i);

					//Affichage d'une personne
//					System.out.println("\n*************PERSONNE************");
//					System.out.println("sexe : " + personne.getAttribute("sexe"));

					
					/*récupération du nom et du prénom*/
					final Element nom = (Element) contact.getElementsByTagName("nom").item(0);
					final Element numero = (Element) contact.getElementsByTagName("numero").item(0);
					final Element mail = (Element) contact.getElementsByTagName("mail").item(0);


					//Ajout a la liste des sms a traité
					if(mail == null)
						liste.add(new Contact(nom.getTextContent(), numero.getTextContent()));
					else
						liste.add(new Contact(nom.getTextContent(), numero.getTextContent(), mail.getTextContent()));


				}
			}
		}
		catch (final ParserConfigurationException e) {
			e.printStackTrace();
		}
		catch (final SAXException e) {
			e.printStackTrace();
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}


}