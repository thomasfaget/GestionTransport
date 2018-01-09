package GestionTransport.Outils;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import GestionTransport.Login.LoginFileFactory;
import GestionTransport.Transport.Transport;

public class MailFile {
	
	
	public static void prepareMail( Transport transport ) {
	    URI uriMailTo = null;
	

	    // Assembler la liste des mails en cc
	    List<String> mailList = LoginFileFactory.getAllMail();
	    String cc = "&cc=";
	    if ( mailList.size() != 0 ) {
		    for ( int i=0 ; i<mailList.size()-1 ; i++ ) {
		    	cc += mailList.get(i) + ";";
		    }
		    cc += mailList.get( mailList.size()-1 );
	    }

		  //Assembler l'url
	    String mail = "?subject=Demande de transport";
	    mail += cc;
	    mail += "&body=Bonjour,\n\n";
	    mail += "Merci de me transmettre votre meilleure cotation pour le transport suivant:\n\n";
	    mail += "\t\t - Véhicule : " + transport.getEntree(0) + "\n";
	    mail += "\t\t - Date de départ : " + transport.dateDepart() + "\n";
	    mail += "\t\t - Heure de départ : " + transport.heureDepart() + "\n";
	    mail += "\t\t - Expéditeur : " + transport.getEntree(2) + "\n";
	    mail += "\t\t - Adresse : " + transport.getEntree(3) + "\n"; 
	    mail += "\t\t - Ville : " + transport.getEntree(4) + "\n"; 
	    mail += "\t\t - Code postal : " + transport.getEntree(5) + "\n";
	    mail += "\t\t - Nombre de colis : " + transport.getEntree(6) + "\n";
	    mail += "\t\t - Dimensions : " + transport.getEntree(7) + "\n";
	    mail += "\t\t - Poids : " + transport.getEntree(8) + "\n";
	    mail += "\n";
	    mail += "\t\t - Date de livraison : " + transport.getEntree(9) + "\n";
	    mail += "\t\t - Destinataire : " + transport.getEntree(11) + "\n";
	    mail += "\t\t - Adresse de livraison : " + transport.getEntree(12) + "\n";
	    mail += "\t\t - Destination : " + transport.getEntree(10) + "\n";
	    mail += "\t\t - Code postal de livraison : " + transport.getEntree(13) + "\n";
	    mail += "\t\t - Contact : " + transport.getEntree(14) + "\n\n";
	    mail += "Cordialement,";

	    
	    
	    //Ouvrir le logiciel de messagerie par défaut
	    if (Desktop.isDesktopSupported()) {
	        if (Desktop.getDesktop().isSupported(Desktop.Action.MAIL)) {
	            try {
	                uriMailTo = new URI("mailto", mail, null);
	                Desktop.getDesktop().mail(uriMailTo);
	            } catch (IOException ex) {
	                Logger.getLogger(MailFile.class.getName()).log(Level.SEVERE, null, ex);
	            } catch (URISyntaxException ex) {
	                Logger.getLogger(MailFile.class.getName()).log(Level.SEVERE, null, ex);
	            }
	        }
	    }
    }
}
