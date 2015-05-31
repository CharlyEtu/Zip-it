package com.capella.zipit.tools;

// /!\ res-test vide qd création avec ce prog : n'apparait pas quand
// ouverture avec prog d'ubuntu mais bien recréé quand même lors de la
// décompression avec prog d'ubuntu.


// ===================================================================
// TODO :
//
//  o - Compression d'une chaîne de caractères
//
//  ~ - Méthode pour ajouter un élément à une archive
//
//  ~ - Faire la gestion d'éventuelles erreur :
//      - Faire liste des cas
//      - Si nécéssaire faire exceptions
//
// ===================================================================


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;




/**
 * La classe Zipper permet de compresser et décompresser des fichiers
 * au format ZIP. Elle agit également comme une interface entre un
 * programme et un ensemble d'archives au format ZIP (en proposant,
 * entre autres, une méthode pour lister le contenu d'une archive).
 */
public class Zipper {
    /* CONSTANTES DE CLASSE : NIVEAU DE COMPRESSION */

	/**  Meilleur taux de compression (Niveau 9). */
	public static int BEST_COMPRESSION = Deflater.BEST_COMPRESSION;
	/** Compression par défaut (Niveau 5). */
	public static int DEFAULT_COMPRESSION =
			Deflater.DEFAULT_COMPRESSION;
	/** Compression la plus rapide (Niveau 1). */
	public static int BEST_SPEED = Deflater.BEST_SPEED;
	/** Aucune compression (Niveau 0). */
	public static int NO_COMPRESSION = Deflater.NO_COMPRESSION;


    /* BUFFER INTERMÉDIAIRE (meilleur performances lecture/écritue) */

	/** Taille du buffer intermédiaire */
	private int BUFFSIZE = 2048;
	/** Buffer intermédiaire */
	private byte buffer[] = new byte[BUFFSIZE];


    /* ARCHIVES EN COURS DE TRAITEMENT */

    /* Écriture */

	/** Chemin vers l'entrée en cours de compression */
	private String currIn;
	/** Archive en cours de construction */
	private ZipOutputStream zOut;

    /* Lecture */

	/** Archive en cours de lecture */
	private ZipInputStream zIn;

    
    /* PARAMÈTRES DU ZIPPER */

	/** Niveau de compression */
	private int level;
	/** Horodatage (true : activé, false : désactivé) */
	private boolean timestamp;
    

    /* CONSTRUCTEURS */

	/**
	 * Créer un nouveau Zipper, avec les paramètres par défaut (niveau
	 * de compression à 5, sans horodatage).
	 */
	public Zipper() {
		level = DEFAULT_COMPRESSION;
		timestamp = false;
	}

	/**
	 * Créer un nouveau Zipper, avec les paramètres donnés.
	 * @param level Le niveau de compression souhaité.
	 * @param timestamp true: avec horodatage, false: sans horodatage.
	 */
	public Zipper(int level, boolean timestamp) {
		this.level = level;
		this.timestamp = timestamp;
	}


    /* MÉTHODES PRIVÉES */

	/**
	 * Traitement des caractères spéciaux d'une chaîne.
	 * (Pour l'instant cette méthode remplace les caractères accentués
	 * par des caractères non-accentués).
	 * @param s La chaîne de caractères à traiter.
	 * @return Une chaîne de caractères résultat du traitement.
	 */
	private String specialCharsTreatment(String s) {
		return Normalizer.normalize(s, Normalizer.Form.NFD)
				.replaceAll("[\u0300-\u036F]", "");
	}

	/**
	 * Supprimer le suffixe ".zip" d'une chaîne de caractères.
	 * @param s La chaîne de caractères.
	 * @return La chaîne sans ".zip", retourne la chaîne elle même
	 *         si elle ne contient pas ce suffixe.
	 */
	private String rmZipSuffix(String s) {
		if(s.endsWith(".zip")) {
			return s.substring(0, s.length() - 4);
		}
		else
			return s;
	}

	/**
	 * Supprimer un éventuel "/" à la fin d'une chaîne de caractères.
	 * @param s La chaîne de caractères à traiter.
	 * @return La chaîne nettoyée, elle même si elle ne se termine pas
	 *         par "/".
	 */
	private String rmSlashSuffix(String s) {
		if(s.endsWith("/")) {
			return s.substring(0, s.length() - 1);
		}
		else
			return s;
	}

	/**
	 * Formater un chemin en chemin approprié à une archive :
	 * traitement des caractères spéciaux et suppression d'éventuels
	 * préfixe "../" ou "./".
	 * @param path Le chemin à formater.
	 * @return Le chemin formaté.
	 */
	private String toZPath(String path) {
		File f = new File(currIn);
		String inName = getNameInPath(currIn);

		// Traitement des caractères spéciaux
		path = specialCharsTreatment(path);

		// Suppression d'un éventuel préfixe "../" ou "./"
		// (source de problèmes lors de la construction de l'archive)
		if(path.startsWith("../"))
			path = path.substring(3);
		else if(path.startsWith("./"))
			path = path.substring(2);

		//if(path.startsWith(outName + "/"))
		//    path = path.substring(outName.length() + 1);

		if(f.isDirectory() && path.startsWith(currIn))
			path = path.replaceFirst(currIn + "/", "");
		else if(path.startsWith(currIn))
			path = path.substring(path.indexOf(inName));

		return path;
	}

	/**
	 * Extraire le nom d'un dossier ou d'un fichier dans un chemin.
	 * @param path Le chemin contenant le nom à extraire.
	 */
	private String getNameInPath(String path) {
		String elem[] = path.split("/");

		return elem[elem.length - 1];
	}

	/**
	 * Savoir si un tableau d'entrées de fichier zip contient une
	 * entrée donnée.
	 * @param tab Le tableau à parcourir.
	 * @param entry La chaîne à rechercher.
	 * @return true si tab contient s, false sinon.
	 */
	private boolean entriesContains(String tab[], String entry) {
		int i;

		for(i = 0; i < tab.length; i++) {
			if((entry.compareTo(tab[i]) == 0)
					|| (entry.compareTo(tab[i] + "/") == 0))
				return true;
		}

		return false;
	}

	/**
	 * Explorer un fichier ou un répertoire et ajouter son contenu à
	 * l'archive en cours de construction.
	 * @param f Le fichier ou répertoire à ajouter.
	 */
	private void exploreCompress(File f, boolean createDir)
			throws IOException {
		int i;
		String path;
		File currFile;
		File files[];

	/* Si, c'est bien un répertoire */
		if(f.isDirectory()) {
			if(createDir) {
				// Ajout de l'entrée (si demandé)
				path = toZPath(f.getPath() + "/");
				zOut.putNextEntry(new ZipEntry(path));
			}

			// Listing du contenu
			files = f.listFiles();
			// Exploration du contenu et compression
			for(i = 0; i < files.length; i++) {
				currFile = files[i];

				if(currFile.isDirectory())
					exploreCompress(currFile, true);
				else
					compress(currFile);
			}

			if(createDir) {
				// Fermeture de l'entrée
				zOut.closeEntry();
			}
		}
		else {
			System.out.println("ERREUR : exploreCompress(File) -> Mettre"
					+ " exception");
		}
	}

	/**
	 * Ajouter un fichier à l'archive en cours de construction.
	 * @param f Le fichier à ajouter.
	 */
	private void compress(File f)
			throws FileNotFoundException, IOException {
		int count;
		FileInputStream fIn  = new FileInputStream(f);
		BufferedInputStream buffIn =
				new BufferedInputStream(fIn, BUFFSIZE);
		ZipEntry zEntry = new ZipEntry(toZPath(f.getPath()));

	/* Ajout de l'entrée de l'archive */
		zOut.putNextEntry(zEntry);

	/* Lecture du fichier et écriture dans l'archive */
		System.out.println("Compression de " + f.getPath() + "...");
		count = buffIn.read(buffer, 0, BUFFSIZE);
		while(count != -1) {
			zOut.write(buffer, 0, count);
			count = buffIn.read(buffer, 0, BUFFSIZE);
		}
	
	/* Fermeture de l'entrée */
		zOut.closeEntry();
		buffIn.close();
		fIn.close();
	}

	/**
	 * Décompresser un fichier de l'archive en cours de lecture, vers
	 * un emplacement donné (recréer la structure de l'archive).
	 * @param e L'entrée de l'archive à décompresser.
	 * @param out L'emplacement (répertoire) où placer le fichier
	 *            décompréssé (doit se terminer par "/").
	 */
	private void decompress(ZipEntry e, String out)
			throws FileNotFoundException, IOException {
		int count;
		FileOutputStream fOut =
				new FileOutputStream(out + e.getName());
		BufferedOutputStream buffOut =
				new BufferedOutputStream(fOut, BUFFSIZE);

	/* Lecture de l'archive et écriture dans un fichier */
		System.out.println("Décompression de " + e.getName() + "...");
		count = zIn.read(buffer, 0, BUFFSIZE);
		while(count != -1) {
			buffOut.write(buffer, 0, count);
			count = zIn.read(buffer, 0, BUFFSIZE);
		}

	/* Fermeture du buffer de sortie */
		buffOut.flush();
		buffOut.close();
	}

	/**
	 * Décompresser un fichier de l'archive en cours de lecture, vers
	 * un emplacement donné (ne recréer pas la structure de
	 * l'archive).
	 * @param e L'entrée de l'archive à décompresser.
	 * @param out L'emplacement (répertoire) où placer le fichier
	 *            décompréssé (doit se terminer par "/").
	 */
	private void decompressUnstructured(ZipEntry e, String out)
			throws FileNotFoundException, IOException {
		int count;
		String name;
		String tmp[];
		FileOutputStream fOut;
		BufferedOutputStream buffOut;

	/* Récupération du nom du fichier */
		tmp = e.getName().split("/");
		name = tmp[tmp.length - 1];

	/* Ouverture des buffers de sortie */
		fOut = new FileOutputStream(out + name);
		buffOut = new BufferedOutputStream(fOut, BUFFSIZE);

	/* Lecture de l'archive et écriture dans un fichier */
		System.out.println("Décompression de " + e.getName() + "...");
		count = zIn.read(buffer, 0, BUFFSIZE);
		while(count != -1) {
			buffOut.write(buffer, 0, count);
			count = zIn.read(buffer, 0, BUFFSIZE);
		}

	/* Fermeture du buffer de sortie */
		buffOut.flush();
		buffOut.close();
	}

	/**
	 * Ajouter la date et l'heure au nom d'un fichier zip.
	 * @param zipFile Le chemin vers le fichier zip concerné.
	 * @return Le nouveau nom du fichier si l'opération à réussie,
	 *         null sinon.
	 */
	private String putDate(String zipFile) throws IOException {
		boolean success;
		String date, name;
		File f = new File(zipFile);
		Date today = new Date();
		DateFormat shortDate =
				DateFormat.getDateTimeInstance(DateFormat.SHORT,
						DateFormat.MEDIUM);

		if(!zipFile.endsWith(".zip")) {
			System.out.println("ERREUR : putDate : " + zipFile
					+ "n'est pas un fichier .zip."
					+ "\n-> Mettre Exception");
		}

		name = zipFile.substring(0, zipFile.length() - 4);

		date = shortDate.format(today).replace(" ", "_");
		date = date.replace("/", "-");

	/* renameTo :
	 * Many aspects of the behavior of this method are inherently
	 * platform-dependent: The rename operation might not be able
	 * to move a file from one filesystem to another, it might not
	 * be atomic, and it might not succeed if a file with the
	 * destination abstract pathname already exists. The return
	 * value should always be checked to make sure that the rename
	 * operation was successful.
	 *
	 * Note that the Files class defines the move method to move
	 * or rename a file in a platform independent manner.
	 */
		name = name + "_" + date + ".zip";
		success = f.renameTo(new File(name));

	/* Avec la méthode "move" (importer "java.nio.file.Files") :
	 * Files.move(new File(zipFile).toPath(),
	 *	   new File(name + "_" + date + ".zip").toPath());
	 */

		if(success)
			return name;
		else
			return null;
	}


    /* MÉTHODES PUBLIQUES */

	/**
	 * Compresser un fichier ou un répertoire, avec le niveau de
	 * compression par défaut.
	 * @param in Le chemin vers le fichier ou le répertoire à
	 *           compresser.
	 * @return Le nom du fichier zip créé.
	 */
	public String zip(String in)
			throws FileNotFoundException, IOException {
		File f;
	/* Formatage des chaînes in et out */
		in = rmSlashSuffix(in);
		String nomZip = "";
	
	/*taitement extension du nom fichier zipper*/
		String [] tmp = getNameInPath(in).split(".");

		for(int i = 0 ; i < tmp.length-1 ; i++){
			nomZip = nomZip+tmp[i];
		}

		String newName, out = nomZip + ".zip";
	
	/* Préparation du fichier de sortie */
		FileOutputStream fOut = new FileOutputStream(out);
		// Utiliser un buffer améliore les performances d'écriture
		BufferedOutputStream buffOut = new BufferedOutputStream(fOut);
		zOut = new ZipOutputStream(buffOut);

		currIn = in;

	/* Initialisation des paramètres de compression */
		zOut.setMethod(ZipOutputStream.DEFLATED);
		zOut.setLevel(level);

	/* Exploration de l'entrée et compression */
		f = new File(in);
		if(f.isDirectory())
			exploreCompress(f, false);
		else
			compress(f);

	/* Fermeture du fichier de sortie */
		zOut.close();
		zOut = null;
		buffOut.flush();
		buffOut.close();
		fOut.close();

		if(timestamp) {
	    /* Horodatage */
			newName = putDate(out);

			if(newName != null)
				return newName;
			else {
				System.out.println("ERREUR : putDate : L'opération de"
						+ " renommage à échouée mais"
						+ " le fichier zip a tout de même"
						+ " été créé."
						+ "\n-> Mettre Exception");
				return out;
			}
		}
		else
			return out;
	}

	/**
	 * Compresser un fichier ou un répertoire, avec le niveau de
	 * compression par défaut.
	 * @param in Le chemin vers le fichier ou le répertoire à
	 *           compresser.
	 * @param out L'emplacement où doit être placé le résultat de la
	 *            de la compression (sans le nom de l'archive).
	 * @return Le chemin vers le fichier zip créé.
	 */
	public String zip(String in, String out)
			throws FileNotFoundException, IOException {
		File f;
		String newName;
		String nomZip = "";
	
	
	
	/* Préparation du fichier de sortie */
		out = out+".zip";
		FileOutputStream fOut = new FileOutputStream(out);
		// Utiliser un buffer améliore les performances d'écriture
		BufferedOutputStream buffOut = new BufferedOutputStream(fOut);
		zOut = new ZipOutputStream(buffOut);

		currIn = in;

	/* Formatage de la chaîne in */
		in = rmSlashSuffix(in);

	/* Initialisation des paramètres de compression */
		zOut.setMethod(ZipOutputStream.DEFLATED);
		zOut.setLevel(level);

	/* Exploration de l'entrée et compression */
		f = new File(in);
		if(f.isDirectory())
			exploreCompress(f, false);
		else
			compress(f);

	/* Fermeture du fichier de sortie */
		zOut.close();
		zOut = null;
		buffOut.flush();
		buffOut.close();
		fOut.close();

		if(timestamp) {
	    /* Horodatage */
			newName = putDate(out);

			if(newName != null)
				return newName;
			else {
				System.out.println("ERREUR : putDate : L'opération de"
						+ " renommage à échouée mais"
						+ " le fichier zip a tout de même"
						+ " été créé."
						+ "\n-> Mettre Exception");
				return out;
			}
		}
		else
			return out;
	}

	/**
	 * Compresser une liste de fichiers ou de répertoires, avec un
	 * niveau le compression par défaut.
	 * @param in La liste des chemins vers les fichiers ou les
	 *           répertoires à compresser.
	 * @param out L'emplacement où doit être placé le résultat de la
	 *            de la compression (doit se terminer par le nom de
	 *            l'archive à créer).
	 * @return Le chemin vers le fichier zip créé.
	 */
	public String zip(ArrayList<String> in, String out)
			throws FileNotFoundException, IOException {
		int i;
		File f;
		String newName;
		String inTab[] = in.toArray(new String[in.size()]);
		FileOutputStream fOut;
		BufferedOutputStream buffOut;

	/* Préparation du fichier de sortie */
		fOut = new FileOutputStream(out);
		// Utiliser un buffer améliore les performances d'écriture
		buffOut = new BufferedOutputStream(fOut);
		zOut = new ZipOutputStream(buffOut);

	/* Initialisation des paramètres de compression */
		zOut.setMethod(ZipOutputStream.DEFLATED);
		zOut.setLevel(level);

	/* Exploration des entrées et compression */
		for(i = 0; i < inTab.length; i++) {
			currIn = inTab[i];
			f = new File(rmSlashSuffix(inTab[i]));
			if(f.isDirectory())
				exploreCompress(f, false);
			else
				compress(f);
		}

	/* Fermeture du fichier de sortie */
		zOut.close();
		zOut = null;
		buffOut.flush();
		buffOut.close();
		fOut.close();

		if(timestamp) {
	    /* Horodatage */
			newName = putDate(out);

			if(newName != null) // Succés
				return newName;
			else { // Échec
				System.out.println("ERREUR : putDate : L'opération de"
						+ " renommage à échouée mais"
						+ " le fichier zip a tout de même"
						+ " été créé."
						+ "\n-> Mettre Exception");
				return out;
			}
		}
		else
			return out;
	}

	/**
	 * Décompresser une archive, dans le répertoire courant.
	 * @param in Le chemin vers l'archive à décompresser.
	 * @return Le nom du dossier ou fichier créé.
	 */
	public String unzip(String in)
			throws FileNotFoundException, IOException {
		ZipEntry entry;
		String out = rmZipSuffix(in);
	/* Ouverture du fichier d'entrée */
		FileInputStream fIn = new FileInputStream(rmSlashSuffix(in));
		BufferedInputStream buffIn = new BufferedInputStream(fIn);

		zIn = new ZipInputStream(buffIn);

	/* Création du répertoire de sortie */
		(new File(out)).mkdirs();
		out = out + "/";

	/* Traitement des entrées de l'archive */
		entry = zIn.getNextEntry();
		while(entry != null) {
			// Si c'est un répertoire
			if(entry.isDirectory()) {
				// Création du répertoire
				(new File(out + entry.getName())).mkdirs();
			}
			// Sinon c'est un fichier
			else
				// Décompression
				decompress(entry, out);

			entry = zIn.getNextEntry();
		}

	/* Fermeture du fichier d'entrée */
		zIn.close();
		zIn = null;
		buffIn.close();
		fIn.close();

		return out.substring(0, out.length() - 1);
	}

	/**
	 * Décompresser une archive.
	 * @param in Le chemin vers l'archive à décompresser.
	 * @param out L'emplacement où doit être placé le résultat de la
	 *            décompression.
	 * @return Le nom du dossier ou fichier créé.
	 */
	public String unzip(String in, String out)
			throws FileNotFoundException, IOException {
		ZipEntry entry;
	/* Ouverture du fichier d'entrée */
		FileInputStream fIn = new FileInputStream(rmSlashSuffix(in));
		BufferedInputStream buffIn = new BufferedInputStream(fIn);

		zIn = new ZipInputStream(buffIn);

	/* Création du répertoire de sortie */
		out = out + "/" + rmZipSuffix(getNameInPath(in));
		(new File(out)).mkdirs();
		out = out + "/";

	/* Traitement des entrées de l'archive */
		entry = zIn.getNextEntry();
		while(entry != null) {
			// Si c'est un répertoire
			if(entry.isDirectory()) {
				// Création du répertoire
				(new File(out + entry.getName())).mkdirs();
			}
			// Sinon c'est un fichier
			else
				// Décompression
				decompress(entry, out);

			entry = zIn.getNextEntry();
		}

	/* Fermeture du fichier d'entrée */
		zIn.close();
		zIn = null;
		buffIn.close();
		fIn.close();

		return out.substring(0, out.length() - 1);
	}

	/**
	 * Liste le contenu d'une archive ZIP.
	 * @param in Le chemin vers l'archive à traiter.
	 * @return La liste des chemin des fichiers et répertoires
	 * contenus dans in (le chemin d'un répertoir se termine par "/").
	 */
	public String[] listContent(String in)
			throws IOException {
		ZipEntry e;
		ZipFile f = new ZipFile(in);
		Enumeration entries = f.entries();
		ArrayList<String> list = new ArrayList<String>();

		while(entries.hasMoreElements()) {
			e = (ZipEntry) entries.nextElement();
			list.add(e.getName());
		}

		f.close();
		return list.toArray(new String[list.size()]);
	}

	/**
	 * Liste les entrées d'une archive ZIP.
	 * @param in Le chemin vers l'archive à traiter.
	 * @return La liste des entrées contenus dans in.
	 */
	public ZipEntry[] listEntries(String in)
			throws IOException {
		ZipEntry e;
		ZipFile f = new ZipFile(in);
		Enumeration entries = f.entries();
		ArrayList<ZipEntry> list = new ArrayList<ZipEntry>();

		while(entries.hasMoreElements()) {
			e = (ZipEntry) entries.nextElement();
			list.add(e);
		}

		f.close();
		return list.toArray(new ZipEntry[list.size()]);
	}

	/**
	 * Extraire une entrée donnée d'une archive, dans le répertoire
	 * courant.
	 * @param zipFile Le chemin vers l'archive.
	 * @param entryPath L'entrée à extraire.
	 */
	public void extract(String zipFile, String entryPath)
			throws FileNotFoundException, IOException {
		ZipEntry e;
		String dirPath;
	/* Ouverture du fichier de l'archive */
		FileInputStream fIn =
				new FileInputStream(rmSlashSuffix(zipFile));
		BufferedInputStream buffIn = new BufferedInputStream(fIn);

		zIn = new ZipInputStream(buffIn);

	/* Recherche de l'entrée demandée */
		e = zIn.getNextEntry();
		while((e != null)
				&& (e.getName().compareTo(entryPath) != 0)
				&& (e.getName().compareTo(entryPath + "/") != 0)) {
			e = zIn.getNextEntry();
		}

	/* Décompression de l'entrée */
		if(e.isDirectory()) {
			dirPath = e.getName();
			e = zIn.getNextEntry();
			while(e != null) {
				if(e.getName().startsWith(dirPath)) {
					new File("./" + dirPath).mkdirs();
					decompressUnstructured(e, "./" + dirPath);
				}

				e = zIn.getNextEntry();
			}
		}
		else
			decompressUnstructured(e, "./");
	
	/* Fermeture de l'archive */
		zIn.close();
		zIn = null;
		buffIn.close();
		fIn.close();
	}

	/**
	 * Extraire une entrée donnée d'une archive, dans un répertoire
	 * donné (s'il n'existe pas, il est créé).
	 * @param zipFile Le chemin vers l'archive.
	 * @param entryPath L'entrée à extraire.
	 * @param out Le répertoire où placer le résultat.
	 */
	public void extract(String zipFile, String entryPath, String out)
			throws FileNotFoundException, IOException {
		ZipEntry e;
		String dirPath;
	/* Ouverture du fichier de l'archive */
		FileInputStream fIn =
				new FileInputStream(rmSlashSuffix(zipFile));
		BufferedInputStream buffIn = new BufferedInputStream(fIn);

		zIn = new ZipInputStream(buffIn);

	/* Recherche de l'entrée demandée */
		e = zIn.getNextEntry();
		while((e != null)
				&& (e.getName().compareTo(entryPath) != 0)
				&& (e.getName().compareTo(entryPath + "/") != 0)) {
			e = zIn.getNextEntry();
		}

	/* Décompression de l'entrée */
		new File(out).mkdirs();
		if(e.isDirectory()) {
			dirPath = e.getName();
			e = zIn.getNextEntry();
			while(e != null) {
				if(e.getName().startsWith(dirPath)) {
					new File(out + "/" + dirPath).mkdirs();
					decompressUnstructured(e, out + "/" + dirPath);
				}

				e = zIn.getNextEntry();
			}
		}
		else
			decompressUnstructured(e, out + "/");
	
	/* Fermeture de l'archive */
		zIn.close();
		zIn = null;
		buffIn.close();
		fIn.close();
	}

	/**
	 * Extraire une liste d'entrées données d'une archive, dans le
	 * répertoire courant.
	 * @param zipFile Le chemin vers l'archive.
	 * @param entryPath L'entrée à extraire.
	 */
	public void extract(String zipFile, ArrayList<String> entries)
			throws FileNotFoundException, IOException {
		ZipEntry e;
		String dirPath;
		String entriesTab[] =
				entries.toArray(new String[entries.size()]);
	/* Ouverture du fichier de l'archive */
		FileInputStream fIn =
				new FileInputStream(rmSlashSuffix(zipFile));
		BufferedInputStream buffIn = new BufferedInputStream(fIn);

		zIn = new ZipInputStream(buffIn);

	/* Recherche de l'entrée demandée */
		e = zIn.getNextEntry();
		while(e != null) {
			System.out.println("--- " + e.getName());
			if(entriesContains(entriesTab, e.getName())) {
		/* Décompression de l'entrée */
				if(e.isDirectory()) {
					dirPath = e.getName();
					e = zIn.getNextEntry();
					while(e.getName().startsWith(dirPath)) {
						new File("./" + dirPath).mkdirs();
						decompressUnstructured(e,  "./" + dirPath);

						e = zIn.getNextEntry();
					}
				}
				else
					decompressUnstructured(e, "./");
			}

			e = zIn.getNextEntry();
		}
	
	/* Fermeture de l'archive */
		zIn.close();
		zIn = null;
		buffIn.close();
		fIn.close();
	}

	/**
	 * Extraire une liste d'entrées données d'une archive, dans un
	 * répertoire donné (s'il n'existe pas, il est créé).
	 * @param zipFile Le chemin vers l'archive.
	 * @param entryPath L'entrée à extraire.
	 * @param out Le répertoire où placer le résultat.
	 */
	public void extract(String zipFile, ArrayList<String> entries,
						String out)
			throws FileNotFoundException, IOException {
		ZipEntry e;
		String dirPath;
		String entriesTab[] =
				entries.toArray(new String[entries.size()]);
	/* Ouverture du fichier de l'archive */
		FileInputStream fIn =
				new FileInputStream(rmSlashSuffix(zipFile));
		BufferedInputStream buffIn = new BufferedInputStream(fIn);

		zIn = new ZipInputStream(buffIn);

	/* Recherche de l'entrée demandée */
		new File(out).mkdirs();
		e = zIn.getNextEntry();
		while(e != null) {
			if(entriesContains(entriesTab, e.getName()))
		/* Décompression de l'entrée */
				if(e.isDirectory()) {
					dirPath = e.getName();
					e = zIn.getNextEntry();
					while(e.getName().startsWith(dirPath)) {
						new File(out + "/" + dirPath).mkdirs();
						decompressUnstructured(e, out + "/"
								+ dirPath);

						e = zIn.getNextEntry();
					}
				}
				else
					decompressUnstructured(e, out + "/");

			e = zIn.getNextEntry();
		}
	
	/* Fermeture de l'archive */
		zIn.close();
		zIn = null;
		buffIn.close();
		fIn.close();
	}








	public static String compresser(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return str;
		}
		System.out.println("String length : " + str.length());
		ByteArrayOutputStream out = new ByteArrayOutputStream(str.length());
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(str.getBytes(Charset.forName("ISO-8859-1")));

		gzip.close();

		System.out.println("Output en byte : " + out.size());
		String outStr = new String( out.toByteArray(), Charset.forName("ISO-8859-1"));
		out.close();

		return outStr;
	}



	public static String DeCompresserString(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return str;
		}
		System.out.println("Input String length : " + str.length());
		GZIPInputStream gis = new GZIPInputStream(new   ByteArrayInputStream(str.getBytes(Charset.forName("ISO-8859-1"))));
		BufferedReader bf = new BufferedReader(new InputStreamReader(gis, Charset.forName("ISO-8859-1")));
		String outStr = "";
		String line;
		while ((line=bf.readLine())!=null) {
			outStr += line;
		}
		System.out.println("Output String lenght : " + outStr.length());

		gis.close();
		bf.close();

		return outStr;
	}






}