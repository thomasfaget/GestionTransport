package GestionTransport.Login;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import GestionTransport.MainFrame.TransportFrame;
import GestionTransport.Login.User;

/** Affiche la fenêtre de Login.
 */
public class LoginFrame extends JFrame {
	

	private JLabel idLabel = new JLabel("Identifiant : ");
	private JTextField idField = new JTextField(10);
	
	private JLabel passwordLabel = new JLabel("Mot de passe : ");
	private JPasswordField passwordField = new JPasswordField(10);
	
	private JLabel confirmLabel = new JLabel(); // texte pour les erreurs d'identifications
	
	private JButton valider = new JButton("Valider"); 
	private JButton quitter = new JButton("Quitter");
	
	public LoginFrame(){
		
		
		// Ajouter actions des boutons
		this.valider.addActionListener( new ActionValider() );
		this.quitter.addActionListener( new ActionQuitter() );

		
		// Section fenetre identifiant
		this.idField.setMaximumSize( this.idField.getPreferredSize() );
		
		Box boxId = Box.createHorizontalBox();
		boxId.add( this.idLabel );
		boxId.add( Box.createHorizontalStrut(5));
		boxId.add( this.idField );
	
		
		// Section fenetre mot de passe
		this.passwordField.setMaximumSize( this.passwordField.getPreferredSize() );
		
		Box boxPassword = Box.createHorizontalBox();
		boxPassword.add( this.passwordLabel );
		boxPassword.add( Box.createHorizontalStrut(5));
		boxPassword.add( this.passwordField );
		
		
		// Section fenetre message confirmation 
		Box boxConfirm = Box.createHorizontalBox();
		boxConfirm.add( this.confirmLabel );

		
		//Section fenetre boutons
		Box boxBoutons = Box.createHorizontalBox();
		boxBoutons.add( Box.createVerticalStrut(1));			
		boxBoutons.add( valider );
		boxBoutons.add( Box.createVerticalStrut(1));	
		boxBoutons.add( quitter );
		boxBoutons.add( Box.createVerticalStrut(1));	
		boxBoutons.add( Box.createHorizontalStrut(5));
		
		
		// Concatenation sections (création fenêtre finale) : 
		Box panel = Box.createVerticalBox();
		panel.add( boxId );
		panel.add( boxPassword );
		panel.add( boxConfirm );
		panel.add( Box.createGlue() );
		panel.add( boxBoutons );
		
		this.add(panel,BorderLayout.CENTER);
		 
		// configuration finale fenêtre et affichage :
		this.setTitle("Gestion Transports - Login");
		this.setSize(400,200);
		this.setLocationRelativeTo(null);
		this.setIconImage(new ImageIcon("Images/icon.png").getImage());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);	
	}
		

	// Action valider
	public class ActionValider implements ActionListener {
		public void actionPerformed( ActionEvent e ) {
			
			User user = LoginFileFactory.getUser(idField.getText(), new String( passwordField.getPassword() ) );
			//User user = new User( "thomas","faget","thomasfaget","abc",Login.ADMIN ,"" );
			
			if (user == null) { // l'utilisateur est non reconnu -> échec de l'identification
				confirmLabel.setText("Le mot de passe ou l'identifiant est incorrect !");
			}
			else { // l'ulisateur est reconnu -> accès à l'application
				//new RootDB();
				//LoginFileFactory.createFile();
				dispose();
				TransportFrame.getInstance().init( user );
			}
		}
	}
	
	// Action quitter
	public class ActionQuitter implements ActionListener {
		public void actionPerformed( ActionEvent e ) {
			setVisible(false);
			dispose();
		}
	}	
	
}

