package GestionTransport.DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


import javax.swing.JOptionPane;


/** Crée la base de données
 * ATTENTION ! PEUT ECRASER LA BASE DE DONNEES ACTUELLE SI EXISTANTE ! 
 * A N'UTILISER QUE POUR INITIALISATION !
 */
public class RootDB {
	

	
	private Connection connection;
	private String url;
	private String login = "thomasfaget";
	private String password = "abc";
	
	private String requete;
	private Statement stmt;
	
	
	public RootDB() {
	
		// Test Driver existant :
		String nomDriver = "org.h2.Driver";
		try{
			Class.forName(nomDriver);
		} 
		catch (ClassNotFoundException cnfe) {
			JOptionPane.showMessageDialog(null, "Erreur de chargement du driver de base de données" , "La classe "+nomDriver+" n'a pas été trouvé", JOptionPane.ERROR_MESSAGE);	
		}
		
		// Connection et création base de données
		try {
			url = "jdbc:h2:./BD/BDH2";
			connection = DriverManager.getConnection(this.url,this.login,this.password);
			System.out.println("Connection base de données ...");
			this.stmt = connection.createStatement();
			
			// Création base de données
			
			System.out.println("Implémentation BD en cours ...");

			this.requete = "create table TRANSPORT (" +	
								" idTRANSPORT identity," +
								" CATEGORIE varchar(100)," + 
								" DATE_HEURE_DEPART TIMESTAMP," +
								" EXPEDITEUR varchar(100)," + 
								//" ADRESSE_EXPEDITEUR varchar(100)," + 
								//" VILLE_EXPEDITEUR varchar(100)," + 
								//" CODE_POSTAL_EXPEDITEUR varchar(100)," + 
								" NOMBRES_COLIS varchar(100)," + 
								" DIMENSIONS varchar(100)," + 
								" POIDS varchar(100)," +
								" DATE_LIVRAISON varchar(100)," +
								" VILLE_LIVRAISON varchar(100)," +
								" DESTINATAIRE varchar(100)," +
								" ADRESSE_LIVRAISON varchar(100)," +
								" CODE_POSTAL varchar(100)," +
								" CONTACT varchar(100)," +
								" DEMANDEUR varchar(100)," +
								" COMMENTAIRES varchar(100)," +
								" FLUX varchar(100)," +
								" NOMS_TRANSPORTEURS varchar(100)," +
								" IMMATRICULATION varchar (100)," +
								" AFFRETEUR varchar (100)," +
								" COUT varchar(100)," +
								" IMPUTATION varchar(100)," +
								" NUM_COMMANDE varchar(100)," +
								" NUM_POSTE_COMMANDE varchar(100)," +
								" MODE_TRANSPORT varchar(100)," +
								" DATE_DEMANDE varchar(100)," +
								" ZONE_LOGISTIQUE varchar(100), " +
								" STATUT varchar(50), " + 
								" VALIDATION boolean, " +
								" FACTURATION boolean, " +
								" COMMENTAIRES_BIS varchar(100)" +
								")";
			this.stmt.executeUpdate(this.requete);
			this.stmt.executeUpdate( "CREATE table VERSION ( v int )");
			this.stmt.executeUpdate( "INSERT into VERSION (v) values (0)");
			System.out.println("La base de données est implémentée avec succès !");
			
			//this.stmt.executeUpdate(" CREATE USER THOMASFAGET PASSWORD 'abc' ADMIN " );
			this.connection.close();


		}
		catch (SQLException sqle) {
			sqle.printStackTrace();
			System.out.println("Erreur !");
			JOptionPane.showMessageDialog(null, "Il y a eu une erreur lors de la création de la base de données" , "Erreur Base de données", JOptionPane.ERROR_MESSAGE);		
		}
		
	}



}
