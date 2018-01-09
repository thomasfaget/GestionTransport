package GestionTransport.Login;

import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Node;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



/** Classe permettant d'interragir avec le fichier contenant les logins
 */
public class LoginFileFactory {
	
	private static String path = "Login/login.xml";
	private static String key = "ze6f4e6z4fze";
	
	/** Crée le fichier contenant les utlisateurs (vide de base)
	 */
	public static void createFile() {
		
		try {
			// Création de la structure du document
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document document = (Document) builder.newDocument();
			final Element racine = document.createElement("repertoire");
			document.appendChild( racine );			
			
			// Exportation vers "login.xml"
			final TransformerFactory transformerFactory = TransformerFactory.newInstance();
			final Transformer transformer = transformerFactory.newTransformer();
			final DOMSource source = new DOMSource( document );
			final StreamResult sortie = new StreamResult( new File(LoginFileFactory.path) );
			transformer.transform( source, sortie);
			
			// Ajout d'un utilisateur par default
			LoginFileFactory.addUser( new User("Thomas","Faget","thomasfaget","abc",Login.ADMIN,"") );
		}
		catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}		
		catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		catch (TransformerException e) {
			e.printStackTrace();
		}
		
	}

	/** récupère la liste des utlisateurs dans le fichier
	 */
	public static List<User> getAllUser() {
		List<User> userList =  new ArrayList<User>();
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document document = (Document) builder.parse( new File(LoginFileFactory.path) );
			Element racine = document.getDocumentElement();
			
			NodeList racineNoeuds = racine.getChildNodes();
			int nbRacinesNoeuds = racineNoeuds.getLength();
			
			for ( int i=0 ; i<nbRacinesNoeuds ; i++ ) {
				if ( racineNoeuds.item(i).getNodeType() == Node.ELEMENT_NODE ) {
					Element user = (Element) racineNoeuds.item(i);
					userList.add( new User( user.getAttribute("firstname"),
											user.getAttribute("lastname"),
											user.getAttribute("username"),
											LoginFileFactory.decoding( user.getAttribute("password") ),
											Login.valueOf( user.getAttribute("login") ),
											user.getAttribute("mail")
											));
				}
			}
			return userList;

		} 
		catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		} 
		catch (SAXException e) {
			e.printStackTrace();
			return null;
		} 
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/** Ajoute un utilisateur dans le fichier
	 */
	public static void addUser( User user ) {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document document = (Document) builder.parse( new File(LoginFileFactory.path) );
			
			Element racine = document.getDocumentElement();
			Element newUser = document.createElement("personne");
			
			newUser.setAttribute("firstname", user.getFirstname() );
			newUser.setAttribute("lastname", user.getLastname() );
			newUser.setAttribute("username", user.getUsername() );
			newUser.setAttribute("password", LoginFileFactory.encoding( user.getPassword() ) );
			newUser.setAttribute("login", user.getLogin().toString() );
			newUser.setAttribute("mail", user.getMail() );
			
			racine.appendChild( newUser );
			
			// Sauvegarde
			final TransformerFactory transformerFactory = TransformerFactory.newInstance();
			final Transformer transformer = transformerFactory.newTransformer();
			final DOMSource source = new DOMSource( document );
			final StreamResult sortie = new StreamResult( new File(LoginFileFactory.path) );
			transformer.transform( source, sortie );
			
		} 
		catch (ParserConfigurationException e) {
			e.printStackTrace();
		} 
		catch (SAXException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}		
		catch (TransformerException e) {
			e.printStackTrace();
		}
	
		
	}
	
	/** Modifie un utilisateur d'indice i dans le fichier
	 */
	public static void editUser( int i , User user) {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document document = (Document) builder.parse( new File(LoginFileFactory.path) );
			
			Element racine = document.getDocumentElement();
			Element pUser = (Element) racine.getChildNodes().item(i);
			
			pUser.setAttribute("firstname", user.getFirstname() );
			pUser.setAttribute("lastname", user.getLastname() );
			pUser.setAttribute("username", user.getUsername() );
			pUser.setAttribute("password", LoginFileFactory.encoding( user.getPassword() ) );
			pUser.setAttribute("login", user.getLogin().toString() );
			pUser.setAttribute("mail", user.getMail() );
			
			// Sauvegarde
			final TransformerFactory transformerFactory = TransformerFactory.newInstance();
			final Transformer transformer = transformerFactory.newTransformer();
			final DOMSource source = new DOMSource( document );
			final StreamResult sortie = new StreamResult( new File(LoginFileFactory.path) );
			transformer.transform( source, sortie);
		} 
		catch (ParserConfigurationException e) {
			e.printStackTrace();
		} 
		catch (SAXException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}		
		catch (TransformerException e) {
			e.printStackTrace();
		}
		
	}
	
	/** Supprime un utilisateur d'indice i dans le fichier
	 */
	public static void removeUser( int i ) {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document document = (Document) builder.parse( new File(LoginFileFactory.path) );
			
			Element racine = document.getDocumentElement();
			Element pUser = (Element) racine.getChildNodes().item(i);
			
			racine.removeChild( pUser );

			// Sauvegarde
			final TransformerFactory transformerFactory = TransformerFactory.newInstance();
			final Transformer transformer = transformerFactory.newTransformer();
			final DOMSource source = new DOMSource( document );
			final StreamResult sortie = new StreamResult( new File(LoginFileFactory.path) );
			transformer.transform( source, sortie);
		} 
		catch (ParserConfigurationException e) {
			e.printStackTrace();
		} 
		catch (SAXException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}		
		catch (TransformerException e) {
			e.printStackTrace();
		}		
	}
	
	/** Trouve l'utilisateur dans le fichier (identifié par rapport à son identifiant et mot de passe)
	 * renvoie "null" si non trouvé
	 */
	public static User getUser( String username , String password ) {
		List<User> userList = LoginFileFactory.getAllUser();
		int i = 0;
		boolean isFound = false;
		while (i<userList.size() && !isFound) {
			if ( userList.get(i).getUsername().equals(username) && userList.get(i).getPassword().equals(password) ) {
				isFound = true;
			}
			else {
				i++;
			}
		}
		if (isFound) {
			return userList.get(i);
		}
		else {
			return null;
		}
	}
	
	/** Renvoie la liste de toutes les adresses mail contenu dans le fichier
	 */
	public static List<String> getAllMail(){
		List<String> mailList = new ArrayList<String>();
		List<User> userList = LoginFileFactory.getAllUser();
		for ( int i=0 ; i<userList.size() ;i++ ) {
			if ( !userList.get(i).getMail().equals("") ) {
				mailList.add( userList.get(i).getMail() );
			}
		}
		return mailList;
	}
	
	/** Encode un mot de passe.
	 */
	public static String encoding( String password ) {
		try {
			Key secretKey = new SecretKeySpec( key.getBytes("ISO-8859-2"), "RC2" );
			Cipher cipher = Cipher.getInstance("RC2");		
			cipher.init( Cipher.ENCRYPT_MODE, secretKey);
			return new String( Base64.encodeBase64String(cipher.doFinal( password.getBytes() ) ));
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
	
	/** décode un mot de passe encodé
	 */
	public static String decoding( String password ) {
		try {
			Key secretKey = new SecretKeySpec( key.getBytes("ISO-8859-2"), "RC2" );
			Cipher cipher = Cipher.getInstance("RC2");		
			cipher.init( Cipher.DECRYPT_MODE, secretKey);
			return new String( cipher.doFinal( Base64.decodeBase64( password.getBytes() ) ));
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}	

}
