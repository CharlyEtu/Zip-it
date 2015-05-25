package com.capella.zipit.tools;


import com.capella.zipit.objet.Sms;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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


/**
 * La classe XmlFile permet de creer un fichier xml
 * et de lire un fichier xml
 * @param <E>
 */
public class XmlFileSMS {
	
	/*liste de sms*/
	private ArrayList<Sms> liste;
	
	
	/*nom fichier xml*/
	private String nom_fichier_xml;
	
	/*nom de la racine du xml*/
	private String racine;

	
	
	
	
	
	
	
	/**
	 * Constructeur XmlFile creer un objet XmlFile
	 * pour les ecriture de SMS
	 * 
	 * @param l
	 * @param file
	 * @param racine
	 * 
	 * */
	public XmlFileSMS(ArrayList<Sms> l, String file, String racine) {
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
	public XmlFileSMS(ArrayList<Sms> l, String file) {
		liste = l;
		nom_fichier_xml = file;
	}

	
	
	

	
	
	
	
	
	/**
	 * Methode ecrire_sms_xml permet de creer un fichier XML
	 * 
	 * */
	public void ecrire_sms_xml(){
		
		Element nom = null;
		
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
		    	
		    	/* création d'un sms*/
			    final Element sms = document.createElement("sms");
			    racine.appendChild(sms);
			    
			    if(liste.get(i).getNom() != null){
				    nom = document.createElement("nom");
				    nom.appendChild(document.createTextNode(liste.get(i).getNom()));
			    }
			    
			    /*num de tel*/
			    final Element num = document.createElement("numero");
			    num.appendChild(document.createTextNode(liste.get(i).getNum()));
			    
			    /*date msg*/
			    final Element date = document.createElement("date");
			    date.appendChild(document.createTextNode(liste.get(i).getDate()));
			    
			    /*heure msg*/
			    final Element heure = document.createElement("heure");
			    heure.appendChild(document.createTextNode(liste.get(i).getHeure()));
			    
			    /*msg*/
			    final Element msg = document.createElement("message");
			    msg.appendChild(document.createTextNode(liste.get(i).getMsg()));
			    
			    if(liste.get(i).getNom() != null)
			    	sms.appendChild(nom);
			    /*ajout des fils du noeud parent*/
		    	sms.appendChild(num);
		    	sms.appendChild(date);
		    	sms.appendChild(heure);
		    	sms.appendChild(msg);
		    	
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
	 * Methode lire_sms_xml permet de lire un fichier XML
	 * 
	 * */
	public void lire_sms_xml(){

		/*récupération d'une instance de la classe "DocumentBuilderFactory"*/
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			/* création d'un parseur*/
			final DocumentBuilder builder = factory.newDocumentBuilder();

			/*création d'un Document */
			final Document document= builder.parse(new File(nom_fichier_xml));

			/* récupération de l'Element racine */
			final Element racine = document.getDocumentElement();

			/*récupération des sms*/
			final NodeList listeNoeuds = racine.getChildNodes();
			final int nbNoeuds = listeNoeuds.getLength();

			for (int i = 0; i<nbNoeuds; i++) {
				if(listeNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE) {
					final Element sms = (Element) listeNoeuds.item(i);
			
					/*récupération du nom et du prénom*/
					final Element nom = (Element) sms.getElementsByTagName("nom").item(0);
					final Element numero = (Element) sms.getElementsByTagName("numero").item(0);
					final Element date = (Element) sms.getElementsByTagName("date").item(0);
					final Element heure = (Element) sms.getElementsByTagName("heure").item(0);
					final Element message = (Element) sms.getElementsByTagName("message").item(0);
					
					//Ajout a la liste des sms a traité
					liste.add(new Sms(numero.getTextContent(), nom.getTextContent(), date.getTextContent(), heure.getTextContent(), message.getTextContent()));
					

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
		
	
	
	
	

