package GestionTransport.DataBase;

import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import GestionTransport.Login.Login;
import GestionTransport.Login.User;
import GestionTransport.MainFrame.TransportFrame;
import GestionTransport.Transport.Statut;
import GestionTransport.Transport.Transport;


public class DataBase {

	
	private static DataBase INSTANCE = new DataBase();
	
	private User currentUser;
	
	private Connection connection;
	private String url;
	
	private Statement stat;
	private Statement statVersion;
	private Statement statBackup;
	
	
	/////////////////////////
	///// Constructeur //////
	/////////////////////////
		
	public DataBase() {
	
		// Test Driver existant :
		String nomDriver = "org.h2.Driver";
		try{
			Class.forName(nomDriver);
		} 
		catch (ClassNotFoundException cnfe) {
			JOptionPane.showMessageDialog(null, "Erreur de chargement du driver !" , "La classe "+nomDriver+" n'a pas été trouvé", JOptionPane.ERROR_MESSAGE);	
		}
		System.out.println("Driver H2 database chargé");
		
	}
	
	////////////////////////////////////
	///// Connexion - Déconnexion //////
	////////////////////////////////////
	
	/** Se connecte à la base de données
	 */
	public void co( User user ) {
		
		this.currentUser = user;

		try {
			// Connexion :
			System.out.println("Connection base de données ...");
			this.url = "jdbc:h2:./BD/BDH2;AUTO_RECONNECT=TRUE;AUTO_SERVER=TRUE";
			connection = DriverManager.getConnection(url,user.getUsername(), user.getPassword());
			System.out.println("Connection base de données ...");
			this.connection.setAutoCommit(true);
			stat = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE , ResultSet.CONCUR_UPDATABLE);
			statBackup = connection.createStatement();
			statVersion = connection.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE , ResultSet.CONCUR_UPDATABLE);
			System.out.println("Connection établie");

		}		
		catch (SQLException sqle){
			JOptionPane.showMessageDialog(null, "Connexion Impossible !\nVérifiez votre identifiant et mot de passe!\nVérifiez également que votre compte n'est pas déjà ouvert dans une autre fenêtre." , "Erreur Base de Données", JOptionPane.ERROR_MESSAGE);	
			sqle.printStackTrace();
		}
		
		// Mise à jour des statuts
		this.updateStatut();
				
		this.showData();
		
		this.createBackup();	
	}
	
	/** Se deconnecte de la base de données.
	 */
	public void deco() {
		try {
			this.createBackup();	
			this.stat.close();
			this.statVersion.close();
			this.statBackup.close();
			this.connection.close();
			System.out.println("Déconnexion BDD");
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/////////////////////////////////////////////
	///// Insertion et Modification ligne  //////
	/////////////////////////////////////////////
	
	
	/** Ajoute une ligne dans la base de données.
	 * l'iD de la ligne est défini automatiquement.
	 */
	
	public synchronized void insert( Transport transport ) {
		for ( int i=0 ; i<transport.getNbEntrees() ; i++ ) {
			transport.setEntree( transport.getEntree(i).replace("'", "''") , i);
		}
		String requete = "INSERT INTO TRANSPORT (" +
							" CATEGORIE," + 
							" DATE_HEURE_DEPART," +
							" EXPEDITEUR," + 
							" ADRESSE_EXPEDITEUR," + 
							" VILLE_EXPEDITEUR," + 
							" CODE_POSTAL_EXPEDITEUR," + 
							" NOMBRES_COLIS," + 
							" DIMENSIONS," + 
							" POIDS," +
							" DATE_LIVRAISON," +
							" VILLE_LIVRAISON," +
							" DESTINATAIRE," +
							" ADRESSE_LIVRAISON," +
							" CODE_POSTAL," +
							" CONTACT," +
							" DEMANDEUR," +
							" COMMENTAIRES," +
							" FLUX," +
							" NOMS_TRANSPORTEURS," +
							" IMMATRICULATION, " +
							" AFFRETEUR, " +
							" COUT," +
							" IMPUTATION," +
							" NUM_COMMANDE," +
							" NUM_POSTE_COMMANDE," +
							" MODE_TRANSPORT," +
							" DATE_DEMANDE," +
							" ZONE_LOGISTIQUE," +
							" STATUT," + 
							" VALIDATION," +
							" FACTURATION," +
							" COMMENTAIRES_BIS " +
						" ) VALUES (" +
							" '" + transport.getEntree(0) + "'," +
							" '" + transport.dateHeureDepart() + "'," +
							" '" + transport.getEntree(2) + "'," +
							" '" + transport.getEntree(3) + "'," +
							" '" + transport.getEntree(4) + "'," +
							" '" + transport.getEntree(5) + "'," +
							" '" + transport.getEntree(6) + "'," +
							" '" + transport.getEntree(7) + "'," +
							" '" + transport.getEntree(8) + "'," +
							" '" + transport.getEntree(9) + "'," +
							" '" + transport.getEntree(10)+ "'," +
							" '" + transport.getEntree(11)+ "'," +
							" '" + transport.getEntree(12)+ "'," +
							" '" + transport.getEntree(13)+ "'," +
							" '" + transport.getEntree(14)+ "'," +
							" '" + transport.getEntree(15)+ "'," +
							" '" + transport.getEntree(16)+ "'," +
							" '" + transport.getEntree(17)+ "'," +
							" '" + transport.getEntree(18)+ "'," +
							" '" + transport.getEntree(19)+ "'," +
							" '" + transport.getEntree(20)+ "'," +
							" '" + transport.getEntree(21)+ "'," +
							" '" + transport.getEntree(22)+ "'," +
							" '" + transport.getEntree(23)+ "'," +
							" '" + transport.getEntree(24)+ "'," +
							" '" + transport.getEntree(25)+ "'," +
							" '" + transport.getEntree(26)+ "'," +
							" '" + transport.getEntree(27)+ "'," +
							" '" + transport.getStatut().name() + "'," +
							" '" + transport.getValidation() + "'," +
							" '" + transport.getFacturation() + "'," +
							" '" + transport.getEntree(28) + "')";
		try {
			this.testConection();
			this.stat.executeUpdate(requete);
		}
		catch( SQLException sqle ) {
			sqle.printStackTrace();
			JOptionPane.showMessageDialog(null, "La requete : 'Insérer une ligne' à provoqué une erreur" , "Erreur base de données", JOptionPane.ERROR_MESSAGE);
		}				
	}
	
	/** Modifie une ligne dans le BDD
	 * Elle est identifié par son id, puis modifié.
	 */
	public synchronized void edit(Transport transport , int idTransport ) {
		for ( int i=0 ; i<transport.getNbEntrees() ; i++ ) {
			transport.setEntree( transport.getEntree(i).replace("'", "''") , i);
		}
		String requete = "UPDATE TRANSPORT SET" +
							" CATEGORIE = '" + transport.getEntree(0) + "'," +
							" DATE_HEURE_DEPART = '" + transport.dateHeureDepart() + "'," + 
							" EXPEDITEUR = '" + transport.getEntree(2) + "'," +
							" ADRESSE_EXPEDITEUR = '" + transport.getEntree(3) + "'," +
							" VILLE_EXPEDITEUR = '" + transport.getEntree(4) + "'," +
							" CODE_POSTAL_EXPEDITEUR = '" + transport.getEntree(5) + "'," +					
							" NOMBRES_COLIS = '" + transport.getEntree(6) + "'," +
							" DIMENSIONS = '" + transport.getEntree(7) + "'," +
							" POIDS = '" + transport.getEntree(8) + "'," +
							" DATE_LIVRAISON = '" + transport.getEntree(9) + "'," +
							" VILLE_LIVRAISON = '" + transport.getEntree(10) + "'," +
							" DESTINATAIRE = '" + transport.getEntree(11) + "'," +
							" ADRESSE_LIVRAISON = '" + transport.getEntree(12) + "'," +
							" CODE_POSTAL = '" + transport.getEntree(13) + "'," +
							" CONTACT = '" + transport.getEntree(14) + "'," +
							" DEMANDEUR = '" + transport.getEntree(15) + "'," +
							" COMMENTAIRES = '" + transport.getEntree(16) + "'," +
							" FLUX = '" + transport.getEntree(17) + "'," +
							" NOMS_TRANSPORTEURS = '" + transport.getEntree(18) + "'," +
							" IMMATRICULATION = '" + transport.getEntree(19) + "'," +
							" AFFRETEUR = '" + transport.getEntree(20) + "'," +
							" COUT = '" + transport.getEntree(21) + "'," +
							" IMPUTATION = '" + transport.getEntree(22) + "'," +
							" NUM_COMMANDE = '" + transport.getEntree(23) + "'," +
							" NUM_POSTE_COMMANDE = '" + transport.getEntree(24) + "'," +
							" MODE_TRANSPORT = '" + transport.getEntree(25) + "'," +
							" DATE_DEMANDE = '" + transport.getEntree(26) + "'," +
							" ZONE_LOGISTIQUE = '" + transport.getEntree(27) + "'," +
							" STATUT = '" + transport.getStatut().name() + "'," +
							" VALIDATION = '" + transport.getValidation() + "'," +
							" FACTURATION = '" + transport.getFacturation() + "'," +
							" COMMENTAIRES_BIS = '" + transport.getEntree(28) + "'" +
							"WHERE idTRANSPORT = " + idTransport;
		try {
			this.testConection();
			this.stat.executeUpdate(requete);
		}
		catch( SQLException sqle ) {
			sqle.printStackTrace();
			JOptionPane.showMessageDialog(null, "La requete : 'Modifier une ligne' à provoqué une erreur!" , "Erreur base de données", JOptionPane.ERROR_MESSAGE);
		}	
	}
	
	
	///////////////////////////////
	///// Récolte de données //////
	///////////////////////////////
	
	/** Affiche l'ensemble des données dans le terminal.
	 * Nécéssite l'éxécution du programme via un terminal pour voir les résultats
	 */
	public synchronized void showData() {
		ResultSetMetaData rsmd;
		ResultSet rs;


		
		try {
			System.out.println( "VERSION = " + this.getVersion() );
			
			rs = stat.executeQuery("select * from  TRANSPORT");
			rsmd = rs.getMetaData();
			
			System.out.println("\n**********************************");
			//On affiche le nom des colonnes
			for(int i = 1; i <=  rsmd.getColumnCount(); i++)
				System.out.print("\t" + rsmd.getColumnName(i).toUpperCase() + "\t *");
			
			System.out.println("\n**********************************");
			
			while(rs.next()){			
				for(int i = 1; i <=  rsmd.getColumnCount(); i++)
					System.out.print("\t" + rs.getObject(i).toString() + "\t |");
				
				System.out.println("\n---------------------------------");
	
			}	
		}
		catch( SQLException sqle ) {
			sqle.printStackTrace();
			JOptionPane.showMessageDialog(null, "La requete : 'Afficher données' à provoquée une erreur" , "Erreur base de donéées", JOptionPane.ERROR_MESSAGE);
		}
		
	}
	/** Récupère toutes les données pour l'onglet suivi.
	 * Triées par la date de départ (ordre croissants)
	 * Pas de filtre
	 */
	public synchronized ResultSet getAllData() {
		String requete = "SELECT" +
				" idTransport," +
				" CATEGORIE," + 
				" DATE_HEURE_DEPART," +
				" EXPEDITEUR," + 
				" ADRESSE_EXPEDITEUR," + 
				" VILLE_EXPEDITEUR," + 
				" CODE_POSTAL_EXPEDITEUR," + 
				" NOMBRES_COLIS," + 
				" DIMENSIONS," + 
				" POIDS," +
				" DATE_LIVRAISON," +
				" VILLE_LIVRAISON," +
				" DESTINATAIRE," +
				" ADRESSE_LIVRAISON," +
				" CODE_POSTAL," +
				" CONTACT," +
				" DEMANDEUR," +
				" COMMENTAIRES," +
				" FLUX," +
				" NOMS_TRANSPORTEURS," +
				" IMMATRICULATION, " +
				" AFFRETEUR, " +
				" COUT," +
				" IMPUTATION," +
				" NUM_COMMANDE," +
				" NUM_POSTE_COMMANDE," +
				" MODE_TRANSPORT," +
				" DATE_DEMANDE," +
				" ZONE_LOGISTIQUE," +
				" COMMENTAIRES_BIS, " + 
				" STATUT," + 
				" VALIDATION," +
				" FACTURATION "
					+ "FROM TRANSPORT WHERE "
					+ "(STATUT<>'TRANSPORT_SUPPRIME') "
					+ "ORDER BY DATE_HEURE_DEPART DESC";
		try {
			this.testConection();
			Statement statData = this.connection.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE , ResultSet.CONCUR_UPDATABLE );
			return statData.executeQuery(requete);
		}
		catch( SQLException sqle ) {
			sqle.printStackTrace();
			JOptionPane.showMessageDialog(null, "La requete : 'récupérer toutes les données' a provoquée une erreur" , "Erreur base de donéées", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		
	}
	
	
	/** Récupère les données en fonction d'un mode.
	 * Le mode indique quelles sont les colonnes et les lignes qu'il faut renvoyer.
	 */
	public synchronized ResultSet getData() {
		String date = new SimpleDateFormat("yyyy-MM-dd").format( new Date() );
		String requete = "SELECT" +
				" idTransport," +
				" CATEGORIE," + 
				" DATE_HEURE_DEPART," +
				" EXPEDITEUR," + 
				" ADRESSE_EXPEDITEUR," + 
				" VILLE_EXPEDITEUR," + 
				" CODE_POSTAL_EXPEDITEUR," + 
				" NOMBRES_COLIS," + 
				" DIMENSIONS," + 
				" POIDS," +
				" DATE_LIVRAISON," +
				" VILLE_LIVRAISON," +
				" DESTINATAIRE," +
				" ADRESSE_LIVRAISON," +
				" CODE_POSTAL," +
				" CONTACT," +
				" DEMANDEUR," +
				" COMMENTAIRES," +
				" FLUX," +
				" NOMS_TRANSPORTEURS," +
				" IMMATRICULATION, " +
				" AFFRETEUR, " +
				" COUT," +
				" IMPUTATION," +
				" NUM_COMMANDE," +
				" NUM_POSTE_COMMANDE," +
				" MODE_TRANSPORT," +
				" DATE_DEMANDE," +
				" ZONE_LOGISTIQUE," +
				" COMMENTAIRES_BIS, " + 
				" STATUT," + 
				" VALIDATION," +
				" FACTURATION " +
						" FROM TRANSPORT "
						   	+ "WHERE (DATE_HEURE_DEPART > '" + date + " 00:00:00') "
						   	+ "AND (STATUT <> 'TRANSPORT_SUPPRIME') "
						   	+ "AND (STATUT <> 'TRANSPORT_ANNULE') "
						   	+ "ORDER BY DATE_HEURE_DEPART ASC";

		try {
			this.testConection();
			Statement statData = this.connection.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE , ResultSet.CONCUR_UPDATABLE );
			return statData.executeQuery(requete);
		}
		catch ( SQLException sqle ) {
			sqle.printStackTrace();
			JOptionPane.showMessageDialog(null, "La requete : 'récupérer les données pour " + "' a provoquée une erreur" , "Erreur base de donéées", JOptionPane.ERROR_MESSAGE);
			return null;			
		}
	}
	
	
	
	////////////////////////////////////////////////////////////////////
	///// Modification du statut, de facturation et de validation //////
	////////////////////////////////////////////////////////////////////

	
	/** Met à jour le statut des transports dans la base de données en fonction de la date.
	 * En effet le statut peut changer en fonction de la date.
	 */
	public synchronized void updateStatut() {
		String date = new SimpleDateFormat("yyyy-MM-dd").format( new Date() );
		// les transports non validé futur passent Jour-j -> statut devient "en cours"
		String requete1 = "UPDATE TRANSPORT " +
						 		"SET STATUT = 'EN_COURS' " +
						 		"WHERE (DATE_HEURE_DEPART BETWEEN '" + date + " 00:00:00' AND '" + date + " 23:59:59') " +
						 		"AND (VALIDATION=false) " +
						 		"AND (STATUT<>'TRANSPORT_ANNULE') " +
						 		"AND (STATUT<>'TRANSPORT_SUPPRIME')";
		// les transports non validé Jour-j passent passé -> statut devient "Erreur Transport"
		String requete2 = "UPDATE TRANSPORT " +
			 					"SET STATUT = 'ERREUR_TRANSPORT' " +
			 					"WHERE (DATE_HEURE_DEPART < '" + date + " 00:00:00') " +
						 		"AND (VALIDATION=false) " +
						 		"AND (STATUT<>'TRANSPORT_ANNULE') " +
						 		"AND (STATUT<>'TRANSPORT_SUPPRIME')";
		try {
			this.testConection();
			this.stat.executeUpdate(requete1);
			this.stat.executeUpdate(requete2);
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}							
	}
	
	
	/** Valide (ou dévalide) un transport et actualise le statut du transport.
	 * Permet de modifier la ligne (comme edit), mais en ne modifiant que le statut et la valeur de validation.
	 */
	public synchronized void validate( Transport transport ) {
		String requeteStatut = "UPDATE TRANSPORT SET STATUT = '" + transport.getStatut().name() + "' WHERE idTransport = " + transport.getNum() ;
		String requeteCheck = "UPDATE TRANSPORT SET VALIDATION = '" + transport.getValidation() + "' WHERE idTransport = " + transport.getNum() ;
		
		try {
			this.testConection();
			this.statVersion.executeUpdate(requeteStatut);
			this.statVersion.executeUpdate(requeteCheck);
			this.nextVersion();
			//this.showData();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/** Facture (ou defacture un transport)
	 * N'altère pas le statut du transport
	 */
	public synchronized void charge( Transport transport ) {
		String requete = "UPDATE TRANSPORT SET FACTURATION = '" + transport.getFacturation() + "' WHERE idTransport= " + transport.getNum();
		try {
			this.testConection();
			this.stat.executeUpdate(requete);
			if ( transport.getStatut().equals(Statut.TRANSPORT_ANNULE) ) {
				this.stat.executeUpdate("UPDATE TRANSPORT SET COMMENTAIRES_BIS = '" + transport.getEntree( transport.getNbEntrees()-1 ) + "' WHERE idTransport= " + transport.getNum() );
			}
			this.nextVersion();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/** Modifie le statut d'un transport dans la base de données
	 */
	public synchronized void setStatut( Transport transport ) {
		String requete = "UPDATE TRANSPORT SET STATUT = '" + transport.getStatut().name() + "' WHERE idTransport = " + transport.getNum() ;
		try {
			this.testConection();
			this.stat.executeUpdate(requete);
			this.nextVersion();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/////////////////////////////////////
	///// Version Base de données  //////
	/////////////////////////////////////
	
	/** Incrémente le compteur de la version de la base de données.
	 * Dès que l'on modifie la base de données, on peut appeler cette fonction pour avertir les autres 
	 * utilisateurs que la base s'est mis à jour.
	 * La version varie en 0 et 49.
	 */
	public synchronized void nextVersion() {
		try {
			this.testConection();
			this.stat.executeUpdate("UPDATE VERSION SET v=(v+1)%50");
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	
	/** Récupère la version de la base de données.
	 */
	public synchronized int getVersion() {
		int v = 0;
		try {
			this.testConection();
			Statement statData = this.connection.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE , ResultSet.CONCUR_UPDATABLE );
			ResultSet rs = statData.executeQuery("SELECT v FROM VERSION;");
			rs.next();
			v = rs.getInt(1);
			rs.close();
			statData.close();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		return v;
	} 
	
	//////////////////////////////////
	///// Gestion utilisateurs  //////
	//////////////////////////////////
	
	/** AJoute un utilisateur dans la base de données
	 * @throws SQLException 
	 */
	public synchronized void addUser(User user) throws SQLException {
		String requete = "CREATE USER IF NOT EXISTS " + user.getUsername() + " PASSWORD '" + user.getPassword() + "'";
		if ( user.getLogin() == Login.ADMIN || user.getLogin() == Login.VIP ) {
			requete += " ADMIN";
		}
		this.testConection();
		this.stat.executeUpdate(requete);
		this.grant(user);
	}
	
	
	/** Modifie un utilisateur dans la base de données
	 * @throws SQLException 
	 */
	public synchronized void removeUser( User user ) throws SQLException {
		String requete = "DROP USER IF EXISTS " + user.getUsername();
		this.testConection();
		this.stat.executeUpdate(requete);
	}
	
	
	/** Met à jour les informations de l'ancien utilisateur par celles du nouveau.
	 * @throws SQLException 
	 */
	public synchronized void editUser( User oldUser , User newUser ) throws SQLException {
		List<String> requeteList = new ArrayList<String>();
		if ( !oldUser.getUsername().equals( newUser.getUsername() ) ) {
			requeteList.add( "ALTER USER " + oldUser.getUsername() + " RENAME TO " + newUser.getUsername() ) ;
		}
		if ( !oldUser.getPassword().equals( newUser.getPassword() ) ) {
			requeteList.add( "ALTER USER " + newUser.getUsername() + " SET PASSWORD '" + newUser.getPassword() + "'" ) ;
		}
		if ( !oldUser.getLogin().equals( newUser.getLogin() ) && oldUser.getLogin().equals( Login.ADMIN ) ) {
			requeteList.add( "ALTER USER " + newUser.getUsername() + " ADMIN FALSE" );
		}
		if ( !oldUser.getLogin().equals( newUser.getLogin() ) && newUser.getLogin().equals( Login.ADMIN ) ) {
			requeteList.add( "ALTER USER " + newUser.getUsername() + " ADMIN TRUE" );
		}		
		for ( String requete : requeteList ) {
			this.testConection();
			this.stat.executeUpdate(requete);
		}
		if ( !oldUser.getLogin().equals(newUser.getLogin() ) ) {
			this.grant(newUser);
		}
	}
	
	/** Donne les droits au utilisateurs pour accéder au différentes commandes sql (SELECT, UPDATE,....)
	 * @throws SQLException 
	 */
	
	public synchronized void grant(User user) throws SQLException {
		List<String> requeteList = new ArrayList<String>();

		switch ( user.getLogin() ) {
			case ADMIN:
				requeteList.add("ALTER USER " + user.getUsername() + " ADMIN TRUE");
				break;
			case VIP:
				requeteList.add("ALTER USER " + user.getUsername() + " ADMIN TRUE");
				break;
			case PG:
				requeteList.add("ALTER USER " + user.getUsername() + " ADMIN FALSE");
				requeteList.add("REVOKE SELECT,UPDATE,INSERT ON TRANSPORT FROM " + user.getUsername() );
				requeteList.add("GRANT SELECT,UPDATE ON TRANSPORT TO " + user.getUsername() );
				requeteList.add("GRANT SELECT ON VERSION TO " + user.getUsername() );
				break;
			case EXPE:
				requeteList.add("ALTER USER " + user.getUsername() + " ADMIN FALSE");
				requeteList.add("REVOKE SELECT,UPDATE,INSERT ON TRANSPORT FROM " + user.getUsername() );
				requeteList.add("GRANT SELECT,INSERT,UPDATE ON TRANSPORT TO " + user.getUsername() );
				requeteList.add("GRANT SELECT, UPDATE ON VERSION TO " + user.getUsername() );
				break;
		}
		for ( String requete : requeteList ) {
			this.testConection();
			this.stat.executeUpdate( requete );
		}
		
	}
	
	public void createBackup() {
		// Création de backup (pour admin et vip seulement)
		if (this.currentUser.getLogin().equals(Login.ADMIN) || this.currentUser.getLogin().equals(Login.VIP) ) {
			String date = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format( new Date() );
			String name = "backup" + date + ".zip";
			try {
				String backupPath = BackupFile.getBackupPath();
				// Création dans le dossier source
				Runtime.getRuntime().exec( backupPath + "/run.bat"); 	// Suppression des backup vieux de plus de 10 jours
				this.testConection();
				this.statBackup.executeUpdate("BACKUP TO '" + name + "'");
				
				// Attente de la création du Backup
				File file = null;
				file = new File(name);
				int timeout = 10;
				int time =0;
				while( time<timeout && !file.exists() ) {
					Thread.sleep(500);
					file = new File(name);
					time++;
				}
				
				// Copie vers le dossier choisi
				FileInputStream fileInputStream = new FileInputStream(name);
				FileChannel in = fileInputStream.getChannel();
				FileOutputStream fileOutputStream = new FileOutputStream( BackupFile.getBackupPath() + "/" + name );
				FileChannel out = fileOutputStream.getChannel();	
				in.transferTo(0, in.size(), out);
				fileInputStream.close();
				fileOutputStream.close();
				in.close();
				out.close();
			}
			catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "L'archivage à échoué!\n Vérifiez le chemin du fichier!" , "Erreur Archivage", JOptionPane.ERROR_MESSAGE);	
				e.printStackTrace();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "L'archivage à échoué!\n Vérifiez le chemin du fichier!" , "Erreur Archivage", JOptionPane.ERROR_MESSAGE);	
				e.printStackTrace();
			} catch (InterruptedException e) {
				JOptionPane.showMessageDialog(null, "L'archivage à échoué!\n Vérifiez le chemin du fichier!" , "Erreur Archivage", JOptionPane.ERROR_MESSAGE);	
				e.printStackTrace();
			}
				
			try {
				// Suppression dans le fichier source
				File file = new File(name);
				file.delete();
			}
			catch (Exception e) {
				JOptionPane.showMessageDialog(null, "L'archivage à échoué!\n Vérifiez le chemin du fichier!" , "Erreur Archivage", JOptionPane.ERROR_MESSAGE);	
				e.printStackTrace();
			}
		}
	}
	
	////////////////////////////////////////////
	////// Tests deconnexion hote server ///////
	////////////////////////////////////////////

	public void testConection() throws SQLException {
		if (this.connection.isClosed()) {
			TransportFrame.getInstance().setEnabled(false);
			TransportFrame.getInstance().getGlassPane().setVisible(true);
			try {
				int i = 0;
				int timeout = 100;
				while ( this.connection.isClosed() && i<timeout ) {
					Thread.sleep(100);
					i++;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			TransportFrame.getInstance().setEnabled(true);
			TransportFrame.getInstance().getGlassPane().setVisible(false);
		}
	}
	
	
	//////////////////////
	///// Instance  //////
	//////////////////////
	
	/** Renvoye l'instance de la base de données
	 */
	public static DataBase getInstance() {
		return INSTANCE;
	}

}
