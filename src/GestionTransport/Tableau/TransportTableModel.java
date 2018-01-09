package GestionTransport.Tableau;


import javax.swing.table.AbstractTableModel;

import GestionTransport.DataBase.DataBase;
import GestionTransport.DataBase.Mode;
import GestionTransport.Transport.Statut;
import GestionTransport.Transport.Transport;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Crée un tableau contenant tous les transports avec toutes les infos.
 */


/** Crée un modèle pour le tableau des transports.
 * Il est utilisé par TrnaportTable pour créer la table.
 */
public class TransportTableModel extends AbstractTableModel {
	
	private List<Transport> transportList;
	
	private Mode mode;
	
	private List<Integer> indexColumn;
	private String titleName [] = { // le nom de toutes les colonnes
			"Véhicule (catégorie)", 		// 0
			"Date de départ", 				// 1
			"Heure de départ", 				// 2
			"Expéditeur", 					// 3
			"Adresse",						// 4
			"Ville",						// 5
			"Code Postal",					// 6
			"Nombres et type du colis", 	// 7
			"Dimensions", 					// 8 
			"Poids", 						// 9 
			"Date de livraison", 			// 10
			"Ville de livraison",			// 11
			"Destinataire", 				// 12
			"Adresse de livraison", 		// 13
			"Code Postal", 					// 14
			"Contact",						// 15
			"Demandeur",					// 16
			"Commentaires",					// 17
			"Flux", 						// 18
			"Nom du transporteur",			// 19
			"Immatriculation", 				// 20
			"Affréteur", 					// 21
			"Coût", 						// 22
			"Imputation", 					// 23
			"N° de commande", 				// 24
			"N° Poste de commande", 		// 25
			"Mode de transport", 			// 26
			"Date de demande", 				// 27
			"Zone Logistique (Tarbes)", 	// 28
			"Statut", 						// 29
			"Validation", 					// 30
			"Facturation", 					// 31
			"Commentaires 2" 				// 32
		};

	private ArrayList<String> title = new ArrayList<String>();

	
	public TransportTableModel(Mode mode) {
		this.transportList = new ArrayList<Transport>();
		this.mode = mode;
		this.initColumn();
		this.init();
	}
	
	public void addTransport( int row , Transport transport ) {
		this.transportList.add( row , transport );
	}
	
	public void removeTransport( int row ) {
		this.transportList.remove( row );
	}
	
	public Transport getTransport( int row ) {
		return transportList.get(row);
	}
	
	public void setTransport ( int row , Transport transport ) {
		this.transportList.set(row, transport);
	}
	
	
	@Override
	public int getColumnCount() {
		return this.title.size();
	}


	@Override
	public Class<?> getColumnClass( int col ) {
		if (this.getColumnName(col) == "Statut") {
			return Statut.class;
		}
		else if (this.getColumnName(col) == "Validation" || this.getColumnName(col) == "Facturation" ) {
			return Boolean.class;
		}
		else {
			return String.class;
		}
	}

	@Override
	public int getRowCount() {
		return this.transportList.size();
	}

	@Override
	public Object getValueAt( int rowIndex , int columnIndex ) {
		Transport transport = this.transportList.get(rowIndex);
		int index = this.indexColumn.get(columnIndex); // On récupère l'index réel dans le tableau
		switch (index) {
			case 0 : return transport.getEntree(0) ; 
			case 1 : return transport.dateDepart() ; 
			case 2 : return transport.heureDepart(); 
			case 3 : return transport.getEntree(2) ; 
			case 4 : return transport.getEntree(3) ; 
			case 5 : return transport.getEntree(4) ; 
			case 6 : return transport.getEntree(5) ; 
			case 7 : return transport.getEntree(6) ; 
			case 8 : return transport.getEntree(7) ; 
			case 9 : return transport.getEntree(8) ; 
			case 10: return transport.getEntree(9) ; 
			case 11: return transport.getEntree(10) ; 
			case 12: return transport.getEntree(11) ; 
			case 13: return transport.getEntree(12) ; 
			case 14: return transport.getEntree(13) ; 
			case 15: return transport.getEntree(14) ; 
			case 16: return transport.getEntree(15) ; 
			case 17: return transport.getEntree(16) ; 
			case 18: return transport.getEntree(17) ; 
			case 19: return transport.getEntree(18) ; 
			case 20: return transport.getEntree(19) ; 
			case 21: return transport.getEntree(20) ; 
			case 22: return transport.getEntree(21) ; 
			case 23: return transport.getEntree(22) ; 
			case 24: return transport.getEntree(23) ; 
			case 25: return transport.getEntree(24) ; 
			case 26: return transport.getEntree(25) ; 
			case 27: return transport.getEntree(26) ; 
			case 28: return transport.getEntree(27) ; 
			case 29: return transport.getStatut() ; 
			case 30: return transport.getValidation() ;
			case 31: return transport.getFacturation() ;
			case 32: return transport.getEntree(28) ; 
			default : return null;
		}
		
	}
	
	@Override
	public String getColumnName( int column ) {
		return this.title.get(column);
	}
	
	/** Renvoye le numéro du transport contenu à la ligne row.
	 */
	public int getNum( int row ){
		return this.transportList.get(row).getNum();
	}
	
	/** Vérifie si le transport de numéro "num" est dans la liste des transports affichés.
	 */
	public boolean containNum( int num ) {
		boolean isPresent = false;
		int row = 0;
		while ( !isPresent && row<this.transportList.size() ) {
			isPresent = this.transportList.get(row).isNum(num);
			row +=1;
		}
		return isPresent;
	}
	
	/** On renvoye d'indice de la ligne du transport numéro "num".
	 * PRECOND : Le transport est présent !
	 */
	public int indexOfNum( int num ) {
		boolean isPresent = false;
		int row = 0;
		while ( !isPresent && row<this.transportList.size() ) {
			isPresent = this.transportList.get(row).isNum(num);
			row +=1;		
		}
		return (row -1);
	}

	/** Récupère toutes les données de la base de données.
	 * Initialise les données du tableau ainsi que les noms des colonnes.
	 */
	public void init() {
		try {
			ResultSet rs;
			if ( this.mode == Mode.SUIVI || this.mode == Mode.SUIVI_LIGHT ) {
				rs = DataBase.getInstance().getAllData();
			}
			else {
				rs = DataBase.getInstance().getData();
			}
			ResultSetMetaData rsmd = rs.getMetaData();
			// initialiser toutes les données
			while( rs.next() ) {
				String list[] = new String[rsmd.getColumnCount()-2]; 
				for ( int i=0 ; i<rsmd.getColumnCount()-2 ; i++ ) {
					list[i] = rs.getString(i+2);
				}
				System.out.println(list.length);
				Transport transport = new Transport(list);
				transport.setStatut( Statut.valueOf( rs.getString( rsmd.getColumnCount()-2 ) ) );
				transport.setValidation( rs.getBoolean( rsmd.getColumnCount()-1 ) );
				transport.setFacturation( (Boolean) rs.getBoolean( rsmd.getColumnCount() ) );
				transport.setNum( rs.getInt(1) );
				this.transportList.add(transport);
			}
			this.fireTableRowsInserted(0, transportList.size() -1);
		}
		catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}
	
	/** Met à jour tout le tableau.
	 * On récupère les infos de la BDD, puis on met à jour le tableau si elle sont différentes.
	 * Cette fonction permet d'effectuer les actions suivantes dans le tableau
	 * - Ajoute des lignes
	 * - Modifie les lignes
	 * - Supprime les lignes	
	 */
	public void update() {
		//DataBase.getInstance().showData();
		try {
			// Récolte des données
			ResultSet rs;
			if ( this.mode == Mode.SUIVI || this.mode == Mode.SUIVI_LIGHT ) {
				rs = DataBase.getInstance().getAllData();
			}
			else {
				rs = DataBase.getInstance().getData();
			}
			ResultSetMetaData rsmd = rs.getMetaData();
			int row = 0;
			
			while( rs.next() ) {
				int numBDD = Integer.parseInt( rs.getObject(1).toString() );
				
				// Construction du transport associé à la lignes
				String list[] = new String[rsmd.getColumnCount()-2]; 
				for ( int i=0 ; i<rsmd.getColumnCount()-2  ; i++ ) {
					list[i] = rs.getString(i+2);
				}
				System.out.println(list.length);
				Transport transport = new Transport(list);
				transport.setStatut( Statut.valueOf( rs.getString( rsmd.getColumnCount()-2 ) ) );
				transport.setValidation( rs.getBoolean( rsmd.getColumnCount()-1 ) );
				transport.setFacturation( (Boolean) rs.getBoolean( rsmd.getColumnCount() ) );
				transport.setNum( numBDD );

				if (!this.containNum(numBDD)) { // Le tableau ne contient pas cette ligne -> on ajoute
					this.addTransport(row, transport);	
					this.fireTableRowsInserted(row, row);
				}
				else { // Le tableau contient la ligne -> on la modifie (mise à jour)
					if ( numBDD != this.getNum(row) ) { // Le tableau contient la ligne, mais pas à la bonne place -> on change les places
						int i = this.indexOfNum(numBDD);
						this.transportList.set( i , this.transportList.set( row , this.transportList.get(i) ) );
					}
					// on met à jour la ligne
					this.setTransport(row, transport);
					this.fireTableRowsUpdated(row, row);
				}
				row += 1;
			}
			rs.getStatement().close();
			rs.close();
			// les données restantes sont supprimées du tableau
			int end = this.transportList.size();
			if ( row != end ) {
				for ( int i=row ; i<end; i++ ) {
					this.removeTransport(row);
				}
				this.fireTableRowsDeleted( row, end-1 );
			}
		}
		catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}
	
	/** Initialise la liste des colonnes à prendre en compte dans le tableau, à partir du mode de la fenêtre.
	 * ATTENTION ! NE PAS SUPPRIMER LE NUMERO 1, 2, 15 et 26 ( date de départ, heure de départ,flux et statut )
	 */
	private void initColumn() {
		// initialisation des colonnes à prendre
		switch (this.mode) {
		case ACTUALITE_ADMIN:
			this.indexColumn = new ArrayList<Integer>( Arrays.asList( 0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,32 ) );
			break;
		case ACTUALITE_EXPE:
			this.indexColumn = new ArrayList<Integer>( Arrays.asList( 0,1,2,3,7,8,9,10,11,12,13,14,15,16,17,18,19,20,28,29,30 ) );
			break;
		case ACTUALITE_PG:
			this.indexColumn = new ArrayList<Integer>( Arrays.asList( 0,1,2,3,10,11,12,13,14,16,18,19,20,28,29 ) );
			break;
		case ACTUALITE_VIP:
			this.indexColumn = new ArrayList<Integer>( Arrays.asList( 0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,32) );
			break;
		case SUIVI:
			this.indexColumn = new ArrayList<Integer>( Arrays.asList( 0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32 ) );
			break;
		case SUIVI_LIGHT:
			this.indexColumn = new ArrayList<Integer>( Arrays.asList( 1,21,22,12,3,0,23,16,18,30,31 ) );
		default:
			break;
		}
		// Création du titre du tableau
		for (int i : this.indexColumn ) {
			this.title.add( this.titleName[ i ] );
		}
		this.fireTableStructureChanged();
	}

}
  

