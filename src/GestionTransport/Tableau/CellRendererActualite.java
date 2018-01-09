package GestionTransport.Tableau;

import java.awt.Color;
import java.awt.Component;


import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import GestionTransport.Transport.Statut;

public class CellRendererActualite extends DefaultTableCellRenderer {
	


	public Component getTableCellRendererComponent( JTable table , Object value , boolean isSelected , boolean hasFocus , int row , int column ) {
		Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);	
		
		TransportTableModel model = (TransportTableModel) table.getModel();
		
		
		// Centrer les cellules
		this.setHorizontalAlignment(JLabel.CENTER);
		
		// Réglage de la couleur de fond en fonction du statut
		
		Statut statut = model.getTransport( table.convertRowIndexToModel(row) ).getStatut();
		String flux = (String) model.getValueAt(table.convertRowIndexToModel(row), model.findColumn("Flux") );
		if (!isSelected) {
			if ( flux.equals("Import") && !statut.equals(Statut.ERREUR_DATE) ) {
				component.setBackground( new Color(255,255,0) ); // jaune 
			}
			else {
				switch (statut) {
					case EN_COURS: 
						component.setBackground( new Color(255,153,0) ); //Orange
						break;
					case ERREUR_DATE: 
						component.setBackground( new Color(235,78,78) ); //Rouge
						break;
					case TRANSPORT_REALISE: 
						component.setBackground( new Color(92,255,82)); //Vert
						break;
					default: 
						component.setBackground(Color.WHITE); //blanc 
						break;
				}
			}
		}
		// Réglage le la couleur de fond en fonction du statut quand la case est surligné
		else {
			if ( flux.equals("Import") && !statut.equals(Statut.ERREUR_DATE) ) {
				component.setBackground( new Color(230,230,10) ); // jaune surligné
			}
			else {
				switch (statut) {
				case EN_COURS: 
					component.setBackground( new Color(230,130,10)); // Orange surligné
					break;
				case ERREUR_DATE: 
					component.setBackground( new Color(210,50,50) ); // Rouge surligné
					break;
				case TRANSPORT_REALISE: 
					component.setBackground( new Color(70,230,50));	// Vert surligné
					break;
				default: 
					component.setBackground( new Color(230,230,230) ); // gris (blanc surligné)
					break;
				}
			}
		}		
	
		
		return component;
	}
}
