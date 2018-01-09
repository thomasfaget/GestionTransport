package GestionTransport.Transport;

import java.text.SimpleDateFormat;
import java.util.Date;

/** Classe permetant d'impl�menter un transport (= une ligne dans le tableau)
 */
public class Transport {
	
	private Statut statut;
	private boolean validation;
	private boolean facturation;

	private int num;

	private String [] entreeList; 
	// significations des entr�es dans la liste :
	/*
	0 - Vehicule Cat�gorie
	1 - Date+Heure d�part (format "yyyy-MM-dd HH:mm:00" );
	2 - Exp�diteur 
	3 - Adresse Exp�diteur
	4 - Ville Exp�diteur
	5 - Code Postal Exp�diteur
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
	20- Affr�teur
	21- Co�t 
	22- Imputation 
	23- N� de commande 
	24- N� Poste de Commande 
	25- Mode de transport
	26- Date de demande
	27- Zone Logistique (Tarbes)
	28- Commentaires 2
	*/
	
	
	/** Cr�e un objet contenant toutes les infos d'un transport.
	 * Les infos en arguments sont s�parer par des "@"
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
	/** renvoye le nombre d'entr�es (sans compter validation, statut et facturation)
	 * 
	 * @return le nombre d'entr�es
	 */
	public int getNbEntrees() {
		return this.entreeList.length;
	}
	
	/** renvoye la liste des entr�es
	 * 
	 * @return la liste d'entr�es
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
	
	/** Recup�re l'entr�e num�ro i.
	 */
	public String getEntree( int i ) {
		return this.entreeList[i];	
	}
	
	/** Modifie l'entr�e num�ro i.		
	 */
	public void setEntree( String value , int i) {
		this.entreeList[i] = value;
	}
	
	/** Modifie le num�ro du transport.
	 */
	public void setNum( int num ) {
		this.num = num;
	}
	
	/** Recup�re le num�ro du transport.
	 */
	public int getNum() {
		return this.num;
	}
	
	/** V�rifie si le num�ro du transport est le bon.
	 */
	public boolean isNum( int num ) {
		return this.num == num;
	}
	
	/** valide ou d�-valide le transport
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
	
	/** On supprime le transport (irr�verssible)
	 */
	public void supprimer() {
		this.statut = Statut.TRANSPORT_SUPPRIME;
	}
	
	/** Selectionne la validation du transport
	 */
	public void setValidation( boolean validation) {
		this.validation = validation;
	}

	/** Indique si le transport est valid�
	 */
	public boolean getValidation() {
		return this.validation;
	}
	
	/** Selectionne la facturation du transport
	 */
	public void setFacturation( boolean facturation) {
		this.facturation = facturation;
	}
	
	/** Indique si le transport est factur�
	 */
	public boolean getFacturation() {
		return this.facturation;
	}	
	
	/** Selectionne le statut du transport
	 */
	public void setStatut( Statut statut ) {
		this.statut = statut;
	}
	
	/** recup�re le statut du transport
	 */
	public Statut getStatut() {
		return this.statut;
	}
	
	/** Met � jour le statut, si la date de d�part � chang�
	 */
	public void updateStatut() {
		String date = new SimpleDateFormat("yyyy-MM-dd").format( new Date() );
		if (this.validation) { 
			if ( this.dateHeureDepart().contains(date) || this.dateHeureDepart().compareTo(date) <0  ) { // le transport est valid� le jour -j ou pass�
				this.statut = Statut.TRANSPORT_REALISE;
			}
			else { // Le transport est valid� dans le futur
				this.statut = Statut.ERREUR_DATE;
			}
		}
		else { 
			if ( this.dateHeureDepart().contains(date) ) { // le transport est non valid� le jour-j 
				this.statut = Statut.EN_COURS;
			}
			else if ( this.dateHeureDepart().compareTo(date)>0 ){ // le transport est non valid� dans le futur
				this.statut = Statut.COMMANDE_REALISE;
				}
			else { // le transport est valid� dans le pass�
				this.statut = Statut.ERREUR_TRANSPORT;
			}
		}
	}


}
