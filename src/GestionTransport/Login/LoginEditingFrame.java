package GestionTransport.Login;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import GestionTransport.DataBase.DataBase;
import GestionTransport.MainFrame.TransportFrame;

public class LoginEditingFrame extends JFrame {
	
	private ArrayList<Object> fieldList = new ArrayList<Object>(); // Liste contenant les champs
	
	private boolean modeAjout;
	
	private int xField = 170; // largeur des champs standard
	private int yField = 20; // hauteur des champs standard

	private Box leftBox = Box.createVerticalBox(); 
	private Box rightBox = Box.createVerticalBox(); 
	
	private JButton valider = new JButton("Enregistrer");
	private JButton annuler = new JButton("Annuler");
	
	public LoginEditingFrame(Boolean estAjout) {
		
		this.modeAjout=estAjout;
		
		// Ajout des lignes du formulaire
		this.addTextField( "Prénom :" );
		this.addTextField( "Nom :" );
		this.addTextField( "Identifiant :" );
		this.addTextField( "Mot de Passe :" );
		this.addComboBox("Rang :", Login.values() );
		this.addTextField( "Mail :" );
		
		// Ajout de l'auto completion de l'identifiant
		( (JTextField) fieldList.get(0) ).getDocument().addDocumentListener( new AutoCompletion() );
		( (JTextField) fieldList.get(1) ).getDocument().addDocumentListener( new AutoCompletion() );
		
		// Ajout des textes pour la modification ( SEULEMENT POUR LE MODE MODIFICATION )
		if (!this.modeAjout) {
			this.initModif();
		}
		
		// Concaténation des sections gauches et droites
		Box boxEntree = Box.createHorizontalBox();
		Box margeLeft = Box.createVerticalBox();
		margeLeft.add( Box.createHorizontalStrut(30));
		Box margeRight = Box.createVerticalBox();
		margeRight.add( Box.createHorizontalStrut(30));
		boxEntree.add(margeLeft);
		boxEntree.add(leftBox);
		boxEntree.add(rightBox);
		boxEntree.add(margeRight);
		
		// Section boutons
		Box boxBoutons = Box.createHorizontalBox();
		boxBoutons.add( Box.createVerticalStrut(1));			
		boxBoutons.add( valider );
		boxBoutons.add( Box.createVerticalStrut(1));	
		boxBoutons.add( annuler );
		boxBoutons.add( Box.createVerticalStrut(1));	
		boxBoutons.add( Box.createHorizontalStrut(5));
		
		// Ajout Actions boutons
		this.valider.addActionListener( new ActionValider() );
		this.annuler.addActionListener( new ActionAnnuler() );
		
		// Ajout des sections sur la fenêtre :
		Box panel = Box.createVerticalBox();
		panel.add( boxEntree );
		panel.add( boxBoutons );		
		
		// Paramétrage fenêtre :
		this.add(panel,BorderLayout.CENTER);
		if (estAjout) {
			this.setTitle("Création Login");
		} 
		else {
			this.setTitle("Edition Login");
		}
		this.setSize(400, 250);
		this.setIconImage(new ImageIcon("Images/icon.png").getImage());
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}
	
	// Action Valider
	public class ActionValider implements ActionListener {
		public void actionPerformed( ActionEvent e ) {
			User user = new User(
				( (JTextField) fieldList.get(0) ).getText(),
				( (JTextField) fieldList.get(1) ).getText(),
				( (JTextField) fieldList.get(2) ).getText(),
				( (JTextField) fieldList.get(3) ).getText(),
				(Login) ( (JComboBox) fieldList.get(4) ).getSelectedItem(),
				( (JTextField) fieldList.get(5) ).getText()
			);
			
			if (modeAjout) { // Mode ajout
				try {
					DataBase.getInstance().addUser(user);
					LoginFileFactory.addUser(user);
				} catch (SQLException sqle) {
					sqle.printStackTrace();
					JOptionPane.showMessageDialog(null, "L'insertion à échoué !" , "Erreur Login", JOptionPane.ERROR_MESSAGE);
	
				}
				TransportFrame.getInstance().getLoginTableModel().update();

			}
			else { // Mode modification
				int rowIndex = TransportFrame.getInstance().getLoginTable().getSelectedRow();
				User oldUser = TransportFrame.getInstance().getLoginTableModel().getUser( rowIndex );
				try {
					DataBase.getInstance().editUser(oldUser, user);
					LoginFileFactory.editUser(rowIndex, user);
				} catch (SQLException sqle) {
					sqle.printStackTrace();
					JOptionPane.showMessageDialog(null, "La modification à échoué !" , "Erreur Login", JOptionPane.ERROR_MESSAGE);
				}
				TransportFrame.getInstance().getLoginTableModel().update();
			}
			setVisible(false);
			dispose();
		}
	}
	
	// Action Annuler
	public class ActionAnnuler implements ActionListener {
		public void actionPerformed( ActionEvent e ) {
			setVisible(false);
			dispose();
		}
	}
	
	
	private void addTextField( String titre ) {
		JLabel label = new JLabel(titre);
		JTextField field = new JTextField();
		
		label.setMinimumSize( new Dimension(this.xField,this.yField) );		
		label.setMaximumSize( new Dimension(this.xField,this.yField) );
		field.setMinimumSize( new Dimension(this.xField,this.yField) );		
		field.setMaximumSize( new Dimension(this.xField,this.yField) );
		
		this.leftBox.add( label );
		this.rightBox.add( field );
		
		this.fieldList.add( field ); 
	}
	
	private void addComboBox( String titre, Login [] choix ) {
		final Box fieldBox = Box.createHorizontalBox();
		final JLabel label = new JLabel(titre);
		final JComboBox field = new JComboBox();
		
		label.setMinimumSize( new Dimension(this.xField,this.yField) );
		label.setMaximumSize( new Dimension(this.xField,this.yField) );
		fieldBox.setMinimumSize( new Dimension(this.xField,this.yField) );
		fieldBox.setPreferredSize( new Dimension(this.xField,this.yField) );		
		fieldBox.setMaximumSize( new Dimension(this.xField,this.yField) );
		field.setMinimumSize( new Dimension(this.xField-1,this.yField) );
		field.setMaximumSize( new Dimension(this.xField-1,this.yField) );		
				
		for ( Login s : choix ) {
			field.addItem(s);
		}
		
		fieldBox.add( field );
		this.leftBox.add( label );
		this.rightBox.add( fieldBox );

		this.fieldList.add( field );
	}
	
	private void initModif() {
		int rowIndex = TransportFrame.getInstance().getLoginTable().getSelectedRow(); // le num de de la ligne
		User user= TransportFrame.getInstance().getLoginTableModel().getUser(rowIndex); // le transport avec les infos demandées
		( (JTextField) this.fieldList.get(0) ).setText(user.getFirstname() );
		( (JTextField) this.fieldList.get(1) ).setText(user.getLastname() );
		( (JTextField) this.fieldList.get(2) ).setText(user.getUsername() );
		( (JTextField) this.fieldList.get(3) ).setText(user.getPassword() );
		( (JComboBox) this.fieldList.get(4) ).setSelectedItem( user.getLogin() );
		( (JTextField) this.fieldList.get(5) ).setText(user.getMail() );
	}
	
	
	public class AutoCompletion implements DocumentListener {

		@Override
		public void changedUpdate(DocumentEvent arg0) {	run(); }
		@Override
		public void insertUpdate(DocumentEvent arg0) { run(); }
		@Override
		public void removeUpdate(DocumentEvent arg0) { run(); }
		
		public void run() {
			String textFirstName = ( (JTextField) fieldList.get(0) ).getText();
			String textLastName = ( (JTextField) fieldList.get(1) ).getText();
			if (textFirstName.length() >=1) {
				( (JTextField) fieldList.get(2) ).setText( textFirstName.substring(0,1).toLowerCase()+"_"+textLastName.toLowerCase() );
			}
			else {
				( (JTextField) fieldList.get(2) ).setText( textLastName.toLowerCase() );
			}
		}		


	}

}
