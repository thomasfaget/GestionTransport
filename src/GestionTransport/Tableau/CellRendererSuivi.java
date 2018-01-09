package GestionTransport.Tableau;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import GestionTransport.Transport.Statut;

public class CellRendererSuivi extends DefaultTableCellRenderer {
	
	public Component getTableCellRendererComponent( JTable table , Object value , boolean isSelected , boolean hasFocus , int row , int column ) {
		Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);	
		
		TransportTableModel model = (TransportTableModel) table.getModel();
		
		// Centrer les cellules
		this.setHorizontalAlignment(JLabel.CENTER);
	

		// Réglage des couleurs des cases
		Statut statut = model.getTransport(table.convertRowIndexToModel(row)).getStatut() ;
		boolean facturation = (Boolean) model.getTransport(table.convertRowIndexToModel(row)).getFacturation();
		if (!isSelected) {
			if ( statut.equals(Statut.TRANSPORT_ANNULE) || statut.equals(Statut.ERREUR_TRANSPORT) || statut.equals(Statut.ERREUR_DATE) ) {
				component.setBackground( new Color(235,78,78) ); //Rouge
			}
			else if ( facturation ==true ) {
				component.setBackground( new Color(116,208,241) ); // Bleu
			}
			else {
				component.setBackground( Color.WHITE ); // Blanc
			}
		}
		
		if (isSelected) {
			if ( statut.equals(Statut.TRANSPORT_ANNULE) || statut.equals(Statut.ERREUR_TRANSPORT) || statut.equals(Statut.ERREUR_DATE) ) {
				component.setBackground( new Color(210,50,50) ); // Rouge surligné	
			}
			else if ( facturation ) {
				component.setBackground( new Color(96,186,224) ); // Bleu surligné
			}
			else {
				component.setBackground( new Color(230,230,230) ); // gris (blanc surligné)	
			}
		}
		
		return component;
	}

}
