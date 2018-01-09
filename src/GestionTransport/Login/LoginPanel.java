package GestionTransport.Login;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;

import GestionTransport.DataBase.BackupFile;
import GestionTransport.DataBase.DataBase;
import GestionTransport.Login.User;


public class LoginPanel extends JPanel {
	
	Box panelBox;
	Box buttonBox;
	JTable table;
	
	private JButton creer = new JButton("Créer");
	private JButton modifier = new JButton("Modifier");
	private JButton supprimer = new JButton("Supprimer");
	private JButton actualiser = new JButton("Actualiser");
	
	private JTextField pathField = new JTextField();
	private JButton applyPath = new JButton("OK");

	public LoginPanel() {
		super();
		this.setLayout( new BorderLayout() );
		
		// Actions des boutons :
		this.creer.addActionListener( new ActionCreer() ); 
		this.modifier.addActionListener( new ActionModifier() );
		this.supprimer.addActionListener( new ActionSupprimer() );
		this.actualiser.addActionListener( new ActionActualiser() );
		this.applyPath.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					BackupFile.setBackupPath( pathField.getText() );
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Le changement du chemin à échoué !" , "Erreur !", JOptionPane.ERROR_MESSAGE);	
					e.printStackTrace();
				}	
			}	
		});
		
		
		// Réglage taille pathField
		this.pathField.setMinimumSize( new Dimension(350,20) );
		this.pathField.setPreferredSize( new Dimension(350,20) );
		this.pathField.setMaximumSize( new Dimension(350,20) );
		this.applyPath.setMinimumSize( new Dimension(60,20) );
		this.applyPath.setPreferredSize( new Dimension(60,20) );
		this.applyPath.setMaximumSize( new Dimension(60,20) );
		
		// Remplissage du pathField
		try {
			this.pathField.setText( BackupFile.getBackupPath() );
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Le chargement du fichier à échoué!" , "Erreur !", JOptionPane.ERROR_MESSAGE);	
			e.printStackTrace();
		}

		// Sections des zones boutons
		this.buttonBox = Box.createHorizontalBox();
		
			// Section des boutons d'editions
			Box editionBox = Box.createHorizontalBox();
			editionBox.add( Box.createVerticalStrut(1) );
			editionBox.add( creer );
			editionBox.add( Box.createVerticalStrut(1) );
			editionBox.add( modifier );
			editionBox.add( Box.createVerticalStrut(1) );
			editionBox.add( supprimer );
			editionBox.add( Box.createVerticalStrut(1) );
			editionBox.setBorder( new TitledBorder("Edition") );
			editionBox.setPreferredSize( new Dimension( 400,70 ));
			editionBox.setMaximumSize( new Dimension( 400,70 ));
			this.buttonBox.add( editionBox );
			
			// Section du bouton actualiser
			Box actualisationBox = Box.createHorizontalBox();
			actualisationBox.add( Box.createVerticalStrut(1) );
			actualisationBox.add( actualiser );
			actualisationBox.add( Box.createVerticalStrut(1) );
			actualisationBox.setBorder( new TitledBorder("Actualisation") );
			actualisationBox.setPreferredSize( new Dimension( 150,70 ));
			actualisationBox.setMaximumSize( new Dimension( 150,70 ));
			this.buttonBox.add( actualisationBox );
			
			// Section du chemin Backup
			Box backupBox = Box.createHorizontalBox();
			backupBox.add( Box.createVerticalStrut(1) );
			backupBox.add( pathField );
			backupBox.add( applyPath );
			backupBox.add( Box.createVerticalStrut(1) );
			backupBox.setBorder( new TitledBorder("Sauvegarde") );
			backupBox.setPreferredSize( new Dimension( 450,70 ));
			backupBox.setMaximumSize( new Dimension( 450,70 ));
			this.buttonBox.add( backupBox );
		this.buttonBox.add( Box.createVerticalStrut(1) );
		
		// Création section tableau :
		this.panelBox = Box.createVerticalBox();
		this.table = new JTable( new UserTableModel() );
		JScrollPane scrollPane =  new JScrollPane( this.table , JScrollPane.VERTICAL_SCROLLBAR_ALWAYS , JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
		this.panelBox.add( scrollPane , BorderLayout.CENTER );
		scrollPane.getViewport().setBackground( new Color(0,50,95) );
		
		// Configuration tableau
		this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	
		this.table.getTableHeader().setResizingAllowed(false);
		this.table.getTableHeader().setReorderingAllowed(false);
		
		// Centrage des colonnes du tableau
		this.table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				this.setHorizontalAlignment(JLabel.CENTER);
				return component;
			}
		});
		
		// Création fenêtre finale
		Box panel = Box.createVerticalBox();
		panel.add( this.buttonBox );
		panel.add( this.panelBox );
		this.add( panel , BorderLayout.CENTER );
	}
	
	// Action créer
	public class ActionCreer implements ActionListener {
		public void actionPerformed( ActionEvent e ) {
			new LoginEditingFrame(true);
		}
	}
	
	// Action modifier
	public class ActionModifier implements ActionListener {
		public void actionPerformed( ActionEvent e ) {
			if ( table.getSelectedRow() != -1 ) {
				new LoginEditingFrame(false);
			}
		}
	}
	
	// Action Supprimer
	public class ActionSupprimer implements ActionListener {
		public void actionPerformed( ActionEvent e ) {
			int indexRow = table.getSelectedRow();
			if ( indexRow != -1 ) {
				User user = getTableModel().getUser(indexRow);
				try {
					DataBase.getInstance().removeUser(user);
					LoginFileFactory.removeUser(indexRow);
					getTableModel().update();
				}
				catch (SQLException sqle){
					sqle.printStackTrace();
					JOptionPane.showMessageDialog(null, "La suppression a échouée!" , "Erreur base de donéées", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	
	// Action Actualiser
	public class ActionActualiser implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			getTableModel().update();
		}
	}
	
	public JTable getTable() {
		return this.table;
	}
	
	public UserTableModel getTableModel() {
		return (UserTableModel) this.table.getModel();
	}
		

}
