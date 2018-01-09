package GestionTransport.MainFrame;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import GestionTransport.DataBase.DataBase;
import GestionTransport.DataBase.Mode;
import GestionTransport.Login.Login;
import GestionTransport.Login.LoginPanel;
import GestionTransport.Login.UserTableModel;
import GestionTransport.Tableau.TransportTable;
import GestionTransport.Tableau.TransportTableModel;
import GestionTransport.Login.User;

/** Affiche la fenetre de l'application 
 */
public class TransportFrame extends JFrame {
	
	
	private static TransportFrame INSTANCE = new TransportFrame();
	
	private JTabbedPane tabbedpane = new JTabbedPane();
	
	private ActualitePanel ongletActualite;
	private SuiviPanel ongletSuivi;
	private LoginPanel ongletLogin;
	
	public void init(User user) {
		Login login = user.getLogin();
		String name = "Gestion Transport - ";
		Mode mode = Mode.ACTUALITE_PG;
		switch (login) {
		case ADMIN:
			mode = Mode.ACTUALITE_ADMIN;
			name += "Admin";
			break;
		case EXPE:
			mode = Mode.ACTUALITE_EXPE;
			name += "Expédition";
			break;
		case PG:
			mode = Mode.ACTUALITE_PG;
			name += "Poste de garde";
			break;
		case VIP:
			mode = Mode.ACTUALITE_VIP;
			name += "VIP";
			break;
		
		}
		
		// Connexion data base 
		DataBase.getInstance().co( user );
		
		
		// Création des onglets
		this.ongletActualite = new ActualitePanel( mode );
		this.tabbedpane.add("Actualité" , this.ongletActualite );
		if ( login.equals(Login.ADMIN) || login.equals(Login.VIP) ) {
			// Ajout onglet suivi pour admin et vip
			this.ongletSuivi = new SuiviPanel();
			this.tabbedpane.add("Suivi" , this.ongletSuivi );
		}
		if ( login.equals(Login.ADMIN) ) {
			// Ajout onglet administration pour admin
			this.ongletLogin = new LoginPanel();
			this.tabbedpane.add("Administrateur", this.ongletLogin );
		}
		// Actualisation quand on clique sur l'onglet "suivi"
		this.tabbedpane.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent e ) {
				if ( tabbedpane.getSelectedIndex() == 1 ) {
					ongletSuivi.getTable().testVersion();
					
				}
			}
		});
		
		// Procédure de fermeture
		this.addWindowListener( new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				ongletActualite.stopTimer();
				DataBase.getInstance().deco();
				setVisible(false);
				dispose();
			}
		});
		
		// Image de couverture
		JPanel coverPanel  = new CoverPanel();
		coverPanel.setBackground( new Color(0,50,95) );
		coverPanel.setPreferredSize( new Dimension(JFrame.MAXIMIZED_HORIZ,210) );
				
		// configuration fenêtre et affichage :
		Box panel = Box.createVerticalBox();
		panel.add( coverPanel );
		panel.add( this.tabbedpane );
		this.add(  panel , BorderLayout.CENTER);
		
	
		this.getContentPane().setBackground( new Color(0,50,95) );
		this.setTitle(name);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setSize(720, 480);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setIconImage(new ImageIcon("Images/icon.png").getImage());
		this.setVisible(true);
		
		this.setGlassPane( new VeuillezPatienter() );
		this.getGlassPane().setVisible(false);
		
	}
	

	
	public static TransportFrame getInstance() {
		return INSTANCE;
	}
	
	public TransportTable getActualiteTable() {
		return this.ongletActualite.getTable();
	}
	
	public TransportTableModel getActualiteTableModel() {
		return this.ongletActualite.getTableModel();
	}

	public TransportTable getSuiviTable() {
		if (this.ongletSuivi != null) {
			return this.ongletSuivi.getTable();
		}
		else {
			return null;
		}
	}
	
	public TransportTableModel getSuiviTableModel() {
		if (this.ongletSuivi != null) {
			return this.ongletSuivi.getTableModel();
		}
		else {
			return null;
		}
	}
	
	public JTable getLoginTable() {
		return this.ongletLogin.getTable();
	}
	
	public UserTableModel getLoginTableModel() {
		return this.ongletLogin.getTableModel();
	}
	
	
	// Panel pour l'image de couverture
	public class CoverPanel extends JPanel {
		
		Image image = new ImageIcon("Images/cover.png").getImage(); 
		
		public CoverPanel() {
			super();
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			int x = ( this.getWidth() - image.getWidth(null) ) / 2;
			int y = ( this.getHeight() - image.getHeight(null) ) / 2;
			g.drawImage( image , x ,y , null);
		}
	}
	
	public class VeuillezPatienter extends JComponent {
		public VeuillezPatienter() {
			this.setBackground( Color.WHITE );
			this.setFont( new Font( "Default",Font.PLAIN, 12) );
		}
	
		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			Rectangle clip = g.getClipBounds();
			
			AlphaComposite alpha = AlphaComposite.SrcOver.derive(0.65f);
			Composite composite = g2d.getComposite();
			g2d.setComposite(alpha);
			
			g2d.setColor(this.getBackground());
			g2d.fillRect(clip.x, clip.y, clip.width, clip.height);
			
			int x = ( this.getWidth() - 200 ) /2 ;
			int y = ( this.getHeight() - 100 - g.getFontMetrics().getDescent() ) / 2;
			
			g2d.setComposite(composite);
			g2d.setColor( new Color(255,255,255) );
			g2d.fillRect(x, y, 200, 100);
			g2d.setColor( Color.BLACK);
			g2d.drawString("Mise à jour Serveur !", x+46, y+35);
			g2d.drawString("Veuillez Patientez...", x+50, y+65);
				
		}
	}


}
