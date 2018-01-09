package GestionTransport.Transport;

public enum Statut {
	TRANSPORT_REALISE ("Transport réalisé"),
	EN_COURS ("Transport en cours"), 
	COMMANDE_REALISE ("Commande réalisée"), 
	TRANSPORT_ANNULE ("Transport annulé"),
	TRANSPORT_SUPPRIME ("Transport supprimé"),
	ERREUR_DATE ("Erreur sur la date"),
	ERREUR_TRANSPORT ("ANOMALIE !");
	
	private String name = "";
	
	Statut(String name) {
		this.name = name;
	}
	
	public String toString () {
		return name;
	}
	
}
