package GestionTransport.Transport;

public enum Statut {
	TRANSPORT_REALISE ("Transport r�alis�"),
	EN_COURS ("Transport en cours"), 
	COMMANDE_REALISE ("Commande r�alis�e"), 
	TRANSPORT_ANNULE ("Transport annul�"),
	TRANSPORT_SUPPRIME ("Transport supprim�"),
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
