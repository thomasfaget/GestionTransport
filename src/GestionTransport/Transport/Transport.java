package GestionTransport.Transport;

import java.text.SimpleDateFormat;
import java.util.Date;

/** Classe permetant d'implémenter un transport (= une ligne dans le tableau)
 */
public class Transport {
	
	private Statut statut;
	private boolean validation;
	private boolean facturation;

	private int num;

	private String [] entreeList; 
	// significations des entrées dans la liste :
	/*
	0 - Vehicule Catégorie
	1 - Date+Heure départ (format "yyyy-MM-dd HH:mm:00" );
	2 - Expéditeur 
	3 - Adresse Expéditeur
	4 - Ville Expéditeur
	5 - Code Postal Expéditeur
	6 - Nombres et type du colis 
	7 - Dimensions 
	8 - Poids
	9 - Date de livraison
	10- Ville de livraison 
	11- Destinataire 
	12- Adresse de livraison
	13- Code Postal 
	14- Contact 
	15- Demandeur 
	16- Commentaires 
	17- Flux
	18- Nom du transporteur
	19- Immatriculation
	20- Affréteur
	21- Coût 
	22- Imputation 
	23- N° de commande 
	24- N° Poste de Commande 
	25- Mode de transport
	26- Date de demande
	27- Zone Logistique (Tarbes)
	28- Commentaires 2
	*/
	
	
	/** Crée un objet contenant toutes les infos d'un transport.
	 * Les infos en arguments sont séparer par des "@"
	 * @param ligne contient toutes les infos standard (no Date, heure, ou choix multiple)
	 * @param dateHeureDepart date
	 */
	public Transport( String[] ligne ){
		
		this.entreeList = ligne;
		this.validation = false;
		this.facturation = false;
		this.statut = Statut.COMMANDE_REALISE;
	}
	
	
	/** renvoye la date et l'heure au format "yyyy-MM-dd HH:mm:00".
	 * @return la date et l'heure
	 */
	public String dateHeureDepart() {
		return this.getEntree(1);
	}
	
	/** renvoye l'heure au format HH:mm.
	 * @return l'heure
	 */
	public String heureDepart() { 
		String time = this.getEntree(1).split(" ")[1]; // heure au format hh:mm
		String timeList[] = time.split(":");
		return timeList[0] + ":" + timeList[1];
	}
	/** renvoye le nombre d'entrées (sans compter validation, statut et facturation)
	 * 
	 * @return le nombre d'entrées
	 */
	public int getNbEntrees() {
		return this.entreeList.length;
	}
	
	/** renvoye la liste des entrées
	 * 
	 * @return la liste d'entrées
	 */
	public String[] getEntreesList() {
		return this.entreeList;
	}
	
	/** renvoye la date au format dd/MM/yyyy.
	 * @return la date
	 */	
	public String dateDepart() {
		String date = this.getEntree(1).split(" ")[0]; // Date au format yyyy-MM-dd
		String dateList[] = date.split("-");
		return dateList[2] + "/" + dateList[1] + "/" + dateList[0];
	}
	
	/** Recupère l'entrée numéro i.
	 */
	public String getEntree( int i ) {
		return this.entreeList[i];	
	}
	
	/** Modifie l'entrée numéro i.		
	 */
	public void setEntree( String value , int i) {
		this.entreeList[i] = value;
	}
	
	/** Modifie le numéro du transport.
	 */
	public void setNum( int num ) {
		this.num = num;
	}
	
	/** Recupère le numéro du transport.
	 */
	public int getNum() {
		return this.num;
	}
	
	/** Vérifie si le numéro du transport est le bon.
	 */
	public boolean isNum( int num ) {
		return this.num == num;
	}
	
	/** valide ou dé-valide le transport
	 */
	public void valider() {
		this.validation = !this.validation;
		if ( this.statut != Statut.TRANSPORT_ANNULE && this.statut != Statut.TRANSPORT_SUPPRIME ) {
			this.updateStatut();
		}
	}
	/** On annule ou de-annule le transport.
	 */
	public void annuler() {
		if ( this.statut != Statut.TRANSPORT_ANNULE ) {
			this.statut = Statut.TRANSPORT_ANNULE ;
		}
		else {
			this.updateStatut();
		}
	}
	
	/** On facture un transport
	 */
	public void facturer() {
		this.facturation = !this.facturation;
	}
	
	/** On supprime le transport (irréverssible)
	 */
	public void supprimer() {
		this.statut = Statut.TRANSPORT_SUPPRIME;
	}
	
	/** Selectionne la validation du transport
	 */
	public void setValidation( boolean validation) {
		this.validation = validation;
	}

	/** Indique si le transport est validé
	 */
	public boolean getValidation() {
		return this.validation;
	}
	
	/** Selectionne la facturation du transport
	 */
	public void setFacturation( boolean facturation) {
		this.facturation = facturation;
	}
	
	/** Indique si le transport est facturé
	 */
	public boolean getFacturation() {
		return this.facturation;
	}	
	
	/** Selectionne le statut du transport
	 */
	public void setStatut( Statut statut ) {
		this.statut = statut;
	}
	
	/** recupère le statut du transport
	 */
	public Statut getStatut() {
		return this.statut;
	}
	
	/** Met à jour le statut, si la date de départ à changé
	 */
	public void updateStatut() {
		String date = new SimpleDateFormat("yyyy-MM-dd").format( new Date() );
		if (this.validation) { 
			if ( this.dateHeureDepart().contains(date) || this.dateHeureDepart().compareTo(date) <0  ) { // le transport est validé le jour -j ou passé
				this.statut = Statut.TRANSPORT_REALISE;
			}
			else { // Le transport est validé dans le futur
				this.statut = Statut.ERREUR_DATE;
			}
		}
		else { 
			if ( this.dateHeureDepart().contains(date) ) { // le transport est non validé le jour-j 
				this.statut = Statut.EN_COURS;
			}
			else if ( this.dateHeureDepart().compareTo(date)>0 ){ // le transport est non validé dans le futur
				this.statut = Statut.COMMANDE_REALISE;
				}
			else { // le transport est validé dans le passé
				this.statut = Statut.ERREUR_TRANSPORT;
			}
		}
	}


}
