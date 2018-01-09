package GestionTransport.Tableau;


import java.awt.FontMetrics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;

import GestionTransport.DataBase.DataBase;
import GestionTransport.DataBase.Mode;
import GestionTransport.MainFrame.TransportFrame;
import GestionTransport.Transport.Statut;
import GestionTransport.Transport.Transport;




/** Crée une table, suivant le modèle ModeleTransportTable
 */
public class TransportTable extends JTable {
	
	private FontMetrics fm;
	private int tableVersion;
	private Mode mode;
	
	public TransportTable(Mode a_mode) {
		super( new TransportTableModel(a_mode) );
		this.mode = a_mode;
		this.fm = this.getFontMetrics( this.getFont() );
		
		
		//Récupération de la version du tableau :
		this.tableVersion = DataBase.getInstance().getVersion();
		
		// Réglage généraux du tableau
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		this.getTableHeader().setResizingAllowed(false);
		this.getTableHeader().setReorderingAllowed(false); 
		
		// Règlage des largeurs des colonnes
		this.autoResize();
			
		// Rend les cases validation et facturation cochables
		this.addMouseListener( new MouseAdapter() {
			@Override
			public void mousePressed( MouseEvent event ) {
				TransportTableModel model = (TransportTableModel) getModel();
				int row = convertRowIndexToModel( rowAtPoint(event.getPoint()) );
				int col = columnAtPoint(event.getPoint());
				Transport transport = model.getTransport(row);
				
				// Case validation :
				if ( model.getColumnName(col) == "Validation" ) {
					if ( ( mode.equals(Mode.SUIVI) && !transport.getFacturation() ) || !transport.getValidation() ){ // On bloque la dé-validation dans actualité
						
						transport.valider(); // On valide dans le tableau
						DataBase.getInstance().validate( transport ); // On actualise la BDD
						tableVersion = DataBase.getInstance().getVersion(); // On actualise manuellement la version du tableau
						model.fireTableRowsUpdated(row, row);
						
						if ( mode.equals(Mode.SUIVI) ) {
							// On valide le transport du tableau actualité
							int num = transport.getNum();
							TransportFrame.getInstance().getActualiteTable().setVersion(tableVersion);
							TransportTableModel modelActualite = TransportFrame.getInstance().getActualiteTableModel();
							if ( modelActualite.containNum(num) ) {
								int rowActualite = modelActualite.indexOfNum(num);
								modelActualite.getTransport(rowActualite).valider(); // Actualiser la ligne dans suivi
								modelActualite.fireTableRowsUpdated(rowActualite, rowActualite);
							}
						}	
						else {
							if ( TransportFrame.getInstance().getSuiviTable() != null ) {
								// On valide le transport du tableau suivi
								int num = transport.getNum();
								TransportFrame.getInstance().getSuiviTable().setVersion(tableVersion);
								TransportTableModel modelSuivi = TransportFrame.getInstance().getSuiviTableModel();
								int rowSuivi = modelSuivi.indexOfNum(num);
								modelSuivi.getTransport(rowSuivi).valider(); // Actualiser la ligne dans actualité
								modelSuivi.fireTableRowsUpdated(rowSuivi, rowSuivi);
							}
						}
					}
				}
				
				// Case facturation
				else if ( model.getColumnName(col) == "Facturation" ) {
					boolean isCorrect = false; // indique si on le droit de modifier la facturation
					if ( transport.getStatut().equals(Statut.ERREUR_TRANSPORT) ) { // On bloque la facturation d'un transport non validé
						JOptionPane.showMessageDialog(TransportFrame.getInstance(), "Transport non validé !\nMerci de le VALIDER\nou de signifier son annulation.", "Erreur facturation !", JOptionPane.WARNING_MESSAGE);
					}
					else if ( transport.getStatut().equals(Statut.TRANSPORT_ANNULE) ) { // On demande une confirmation quand on facture un transport annulé
						int reponse = JOptionPane.showConfirmDialog(TransportFrame.getInstance(), "Transport annulé !\nY a t'il des frais engendrés ?", "Confirmation facturation",JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
						isCorrect = ( reponse == JOptionPane.YES_OPTION && !transport.getFacturation() ) 
								 || ( reponse == JOptionPane.NO_OPTION && transport.getFacturation() );
						// Actualisation de commentaires bis -> on change la valeur de facturation sur un transport annulé
						if (!transport.getFacturation()) {
							transport.setEntree("Coûts engendrés", transport.getNbEntrees()-1 );
						}
						else {
							transport.setEntree("Aucun coût engendré", transport.getNbEntrees()-1 );							
						}
						autoResize();
					}	
					else if ( !transport.getValidation() ) {
						isCorrect = false;
					}
					else { // On peur modifier la facturation sinon
						isCorrect = true;
					}
					if( isCorrect ) {
						model.getTransport(row).facturer(); // On facture dans le tableau
						DataBase.getInstance().charge(transport); // On facture dans la BDD
						int tableVersion = DataBase.getInstance().getVersion();
						TransportFrame.getInstance().getActualiteTable().setVersion(tableVersion); // maj version actualité
						TransportFrame.getInstance().getSuiviTable().setVersion(tableVersion); // maj version sui
						model.fireTableRowsUpdated(row, row);
					}
				}
			}
		});	
	}
	
	// Redimentionne les cellules par rapport au contenu des colonnes
	public void autoResize() {
		TableColumnModel columnModel = this.getColumnModel(); 
		TransportTableModel model = (TransportTableModel) this.getModel();
		int nbCol = columnModel.getColumnCount();
		int indexStatut = model.findColumn("Statut");
		int indexValidation = model.findColumn("Validation");
		int indexFacturation = model.findColumn("Facturation");
		
		for( int i=0 ; i<nbCol ; i++ ) {
			if ( i!= indexStatut && i != indexValidation && i !=indexFacturation ) {
				int taille =0;
				int tailleEntete = this.fm.stringWidth( model.getColumnName(i).toString() );
				if (tailleEntete > taille) {
					taille = tailleEntete;
				}
				int nbRow = model.getRowCount();
				for ( int j=0 ; j<nbRow ; j++ ) {
					int taille2 = this.fm.stringWidth( model.getValueAt(j, i).toString() );
					if (taille2 > taille) {
						taille = taille2;
					}
				}
				columnModel.getColumn(i).setMinWidth(taille+20);
				columnModel.getColumn(i).setMaxWidth(taille+20);
				columnModel.getColumn(i).setMinWidth(taille+20);
			}
		}
		if (indexStatut != -1) {
			columnModel.getColumn(indexStatut).setPreferredWidth(150);
		}
		model.fireTableDataChanged();
	}
	
	// Test la version du tableau par rapport à la version de la BDD
	// Si la version du tableau est obsolète -> mise à jour
	public void testVersion() {
		TransportTableModel model = (TransportTableModel) getModel();
		int databaseVersion = DataBase.getInstance().getVersion();
		if ( databaseVersion != this.tableVersion) {
			model.update();
			if (mode != Mode.SUIVI_LIGHT) {
				this.autoResize();
			}
			this.tableVersion = databaseVersion;
			System.out.println("Actualisation !");
		}
	}
	
	// Change manuelement la version du tableau
	public void setVersion( int version) {
		this.tableVersion = version;
	}
	
	// Actualisation des valeurs du tableau
	public void update() {
		TransportTableModel model = (TransportTableModel) getModel();
		model.update();
		this.tableVersion = DataBase.getInstance().getVersion();
		if (mode != Mode.SUIVI_LIGHT) {
			this.autoResize();
		}
	}
	
	public void setMode(Mode mode) {
		this.mode = mode;
	}
	

}
