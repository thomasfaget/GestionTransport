package GestionTransport.MainFrame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import GestionTransport.DataBase.DataBase;
import GestionTransport.DataBase.Mode;
import GestionTransport.Outils.MailFile;
import GestionTransport.Tableau.TransportTableModel;
import GestionTransport.Transport.Statut;
import GestionTransport.Transport.Transport;

/** Interface permettant d'ajouter une ligne dans le tableau */

public class EditionFrame extends JFrame {
	
	
	private ArrayList<Object> fieldList = new ArrayList<Object>(); // Liste contenant les champs
	
	private int xField = 170; // largeur des champs standard
	private int yField = 20; // hauteur des champs standard

	private Box leftBox = Box.createVerticalBox(); 
	private Box rightBox = Box.createVerticalBox(); 
	
	private JLabel messages = new JLabel("");
	
	// Boutons
	private JButton valider = new JButton("Valider"); 
	private JButton annuler = new JButton("Annuler");
	
	private List<String> expe;
	private List<String> dest;	
	private List<Integer> expeIndex;
	private List<Integer> destIndex;
	
	private SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private boolean modeAjout; // Mode ajout modification -> true = ajout // false = modification
	private Mode mode; // indique quelle est le mode de la fenêtre (suivi ou actualite) qui a crée la fenêtre.
	
	
	//////////////////////////////////////
	/////////   FORMULAIRE     ///////////
	//////////////////////////////////////


	/** Crée une fenêtre d'édition de ligne.
	 * 
	 * @param estAjout indique le formulaire ajoute ou modifie une ligne.
	 * @param mode indique depuis quel type de fenêtre (suivi ou actualité), le formulaire a été crée
	 */
	public EditionFrame(Boolean estAjout , Mode a_mode) {
		
		this.modeAjout=estAjout;
		this.mode = a_mode;
		timestamp.setLenient(false);
		
		// les différents choix pour les menus déroulants
		String categorie[] = {
			"Semi",
			"Porteur",
			"Fourgon",
			"20m3 - Debachable",
			"Semi - Frigo (-18°C)",
			"Porteur - Frigo (-18°C)",
			"Fourgeon - Frigo (-18°C)",
			"20m3 - Frigo (-18°C)",
			"Semi",
			"surbaissé" };
		String modeTransport[] = {
			"Affret",
			"Taxi",
			"Dédié",
			"Navette",
			"Messagerie"};	
		String zone[] = {
			"Métallique",
			"Composite"	};
		String flux[] = {
			"Import",
			"Export",
			"Import/Export" };
		
		// Recolte des choix Expéditeur et Destinataire
		this.expe = new ArrayListCase();  
		this.expeIndex = new ArrayList<Integer>();  
		this.dest = new ArrayListCase();  
		this.destIndex = new ArrayList<Integer>();  
		TransportTableModel Suivimodel = TransportFrame.getInstance().getSuiviTableModel();
		int colExpe = 2;
		int colDest = 11;
		for (int row=0 ; row<Suivimodel.getRowCount() ; row++ ) {
			String valueExpe = Suivimodel.getTransport(row).getEntree(colExpe);
			if ( !expe.contains(valueExpe) && !valueExpe.equals("") && valueExpe!=null) {
				expe.add( Suivimodel.getTransport(row).getEntree(colExpe) );
				expeIndex.add( row );
			}
			String valueDest = Suivimodel.getTransport(row).getEntree(colDest);
			if ( !dest.contains( valueDest) && !valueDest.equals("") && valueDest!=null ) {
				dest.add( Suivimodel.getTransport(row).getEntree(colDest) );
				destIndex.add( row );
			};
		}

		
		// Ajout des lignes du formulaire
		this.addComboBox( "Véhicule (catégorie) :" , categorie );   								// 0
		this.addDateField( "Date de départ :" );													// 1
		this.addTimeField( "Heure de départ :" );													// 2
		this.addComboBox( "Expéditeur :" , expe.toArray( new String [expe.size()] ) );				// 3
		this.addTextField( "Adresse :" );															// 4
		this.addTextField( "Code Postal :" );														// 5
		this.addTextField( "Ville :" );																// 6
		this.addTextField( "Nombres et type du colis :" );											// 7
		this.addTextField( "Dimensions :" );														// 8
		this.addTextField( "Poids :" );																// 9
		this.addDateField( "Date de livraison :" );													// 10
		this.addComboBox( "Destinataire :" , (String []) dest.toArray( new String [expe.size()] ) );// 11
		this.addTextField( "Adresse de livraison :" );												// 12
		this.addTextField( "Code Postal :" );														// 13
		this.addTextField( "Ville de livraison :" );												// 14
		this.addTextField( "Contact :" );													 		// 15
		this.addTextField( "Demandeur :" );															// 16
		this.addTextField( "Commentaires :" );														// 17
		this.addComboBox( "Flux :", flux);															// 18
		this.addTextField( "Noms Transporteurs :" );												// 19
		this.addTextField( "Immatriculation :");													// 20
		this.addTextField( "Affréteur :"); 															// 21
		this.addTextField( "Coût :" );																// 22
		this.addTextField( "Imputation :" );														// 23
		this.addTextField( "N° de commande :" );													// 24
		this.addTextField( "N° Poste de commande :" );												// 25
		this.addComboBox( "Mode de transport :" , modeTransport );									// 26
		this.addDateField( "Date de demande :" );													// 27
		this.addComboBox( "Zone logistique (Tarbes) :" , zone );									// 28
		this.addTextField( "Commentaires 2:" );														// 29
		
		// Ajout des textes pour la modification ( SEULEMENT POUR LE MODE MODIFICATION )
		if (!this.modeAjout) {
			this.initModif();
		}
		
		// Selection du champ autre pour expéditeur et destinataire (Mode ajout seulement)
		if ( this.modeAjout ) {
			( (JComboBox) ( (Object[]) this.fieldList.get(3))[0] ).setSelectedItem("Autre");
			( (JComboBox) ( (Object[]) this.fieldList.get(11))[0] ).setSelectedItem("Autre");
		}
		
		// Ajout des listeners pour la completion auto de Expéditeur
		( (JComboBox) ( (Object[]) this.fieldList.get(3))[0] ).addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TransportTableModel Suivimodel = TransportFrame.getInstance().getSuiviTableModel();
				JComboBox boxExpe = (JComboBox) e.getSource();
				if ( ((JComboBox) e.getSource()).getSelectedItem().toString() != "Autre" ) {
					// Récupération des infos associé à l"expéditeur choisi
					String s = boxExpe.getSelectedItem().toString();
					int row = expeIndex.get( expe.indexOf(s) ); // On recupère la ligne dans le tableau
					// Complétion de la ville, code postal et adress associé à l'expéditeur
					( (JTextField) fieldList.get(4)).setText( Suivimodel.getTransport(row).getEntree(3) );
					( (JTextField) fieldList.get(6)).setText( Suivimodel.getTransport(row).getEntree(4) );
					( (JTextField) fieldList.get(5)).setText( Suivimodel.getTransport(row).getEntree(5) );
				}
				else {
					( (JTextField) fieldList.get(4)).setText("");
					( (JTextField) fieldList.get(5)).setText("");
					( (JTextField) fieldList.get(6)).setText("");
				}
			}
		});
		
		// Ajout des listeners pour la completion auto de destinataire
		( (JComboBox) ( (Object[]) this.fieldList.get(11))[0] ).addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TransportTableModel Suivimodel = TransportFrame.getInstance().getSuiviTableModel();
				JComboBox boxDest = (JComboBox) e.getSource();
				if ( ((JComboBox) e.getSource()).getSelectedItem().toString() != "Autre" ) {
					// Récupération des infos associé à l"expéditeur choisi
					String s = boxDest.getSelectedItem().toString();
					int row = destIndex.get( dest.indexOf(s) ); // On recupère la ligne dans le tableau
					// Complétion de la ville, code postal et adress associé à l'expéditeur
					( (JTextField) fieldList.get(14)).setText( Suivimodel.getTransport(row).getEntree(10) );
					( (JTextField) fieldList.get(12)).setText( Suivimodel.getTransport(row).getEntree(12) );
					( (JTextField) fieldList.get(13)).setText( Suivimodel.getTransport(row).getEntree(13) );
				}
				else {
					( (JTextField) fieldList.get(14)).setText("");
					( (JTextField) fieldList.get(12)).setText("");
					( (JTextField) fieldList.get(13)).setText("");
				}
			}
		});
		
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
		
		// Section message d'alertes
		Box boxMessages = Box.createHorizontalBox();
		boxMessages.add( messages );
		
		// Ajout des sections sur la fenêtre :
		Box panel = Box.createVerticalBox();
		panel.add( boxEntree );
		panel.add( boxMessages );
		panel.add( boxBoutons );
		
		
		// Paramétrage fenêtre :
		this.add(panel,BorderLayout.CENTER);
		if (estAjout) {
			this.setTitle("Création Transport");
		}
		else {
			this.setTitle("Modification Transport");
		}
		this.setIconImage(new ImageIcon("Images/icon.png").getImage());
		this.setSize(400, 720);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}
	
	

	//////////////////////////////////////
	/////////   ACTIONS     //////////////
	//////////////////////////////////////
	
	// Action valider
	public class ActionValider implements ActionListener {
		public void actionPerformed( ActionEvent e ) {
			
			Date dateHeureDepart = new Date();
			
			String entreeList [] = new String [fieldList.size()-1];
			
			Boolean isCorrect = true;

			// Création "date de départ" au format date
			// On vérifie que la date est bien correcte
			try {
				String dateDepartFormat = ( (JTextField []) fieldList.get(1))[2].getText() + "-" //yyyy
			                            + ( (JTextField []) fieldList.get(1))[1].getText() + "-" //MM 
			                            + ( (JTextField []) fieldList.get(1))[0].getText();      //dd 
			    String heureDepartFormat = ( (JTextField []) fieldList.get(2))[0].getText() + ":" //HH
			                             + ( (JTextField []) fieldList.get(2))[1].getText()       //mm 
									     + ":30";
			    if ( heureDepartFormat.equals("::30") ) {
			    	heureDepartFormat = "00:00:30";
			    }
				dateHeureDepart = timestamp.parse(dateDepartFormat + " " + heureDepartFormat); // conversion au format TIMESTAMP (le format date de la BBD)
			} 
			catch( ParseException exception) {
				isCorrect = false;		
				messages.setText("Attention ! La date ou l'heure de départ est incorrecte !");
			}
			
			// entrée standards (par les JTextField et JComboBox );
			entreeList[0] = EditionFrame.getText( fieldList.get(0) );
			entreeList[1] = timestamp.format(dateHeureDepart) ;
			entreeList[2] = EditionFrame.getText( fieldList.get(3) );	
			entreeList[3] = EditionFrame.getText( fieldList.get(4) );
			entreeList[4] = EditionFrame.getText( fieldList.get(6) );
			entreeList[5] = EditionFrame.getText( fieldList.get(5) );
			entreeList[6] = EditionFrame.getText( fieldList.get(7) );
			entreeList[7] = EditionFrame.getText( fieldList.get(8) );
			entreeList[8] = EditionFrame.getText( fieldList.get(9) );
			entreeList[9] = EditionFrame.getText( fieldList.get(10));
			entreeList[10]= EditionFrame.getText( fieldList.get(14));
			entreeList[11]= EditionFrame.getText( fieldList.get(11));
			entreeList[12]= EditionFrame.getText( fieldList.get(12));
			entreeList[13]= EditionFrame.getText( fieldList.get(13));
			entreeList[14]= EditionFrame.getText( fieldList.get(15));
			entreeList[15]= EditionFrame.getText( fieldList.get(16));
			entreeList[16]= EditionFrame.getText( fieldList.get(17));
			entreeList[17]= EditionFrame.getText( fieldList.get(18));
			entreeList[18]= EditionFrame.getText( fieldList.get(19));
			entreeList[19]= EditionFrame.getText( fieldList.get(20));
			entreeList[20]= EditionFrame.getText( fieldList.get(21));
			entreeList[21]= EditionFrame.getText( fieldList.get(22));
			entreeList[22]= EditionFrame.getText( fieldList.get(23));
			entreeList[23]= EditionFrame.getText( fieldList.get(24));
			entreeList[24]= EditionFrame.getText( fieldList.get(25));
			entreeList[25]= EditionFrame.getText( fieldList.get(26));
			entreeList[26]= EditionFrame.getText( fieldList.get(27));
			entreeList[27]= EditionFrame.getText( fieldList.get(28));
			entreeList[28]= EditionFrame.getText( fieldList.get(29));
			
			if (isCorrect) {
				// AJOUT DANS BASE DE DONNEES
				
				TransportFrame frame = TransportFrame.getInstance();
				DataBase database = DataBase.getInstance();
				
				// Créer le transport :
				Transport transport = new Transport( entreeList );
				
				if (modeAjout) { //MODE AJOUT LIGNE
					
					// modification du statut
					transport.updateStatut();
					// Ajout dans le BDD
					database.insert(transport);
					
					database.nextVersion();
					// Actualisation de suivi et actualité
					frame.getSuiviTable().update();
					frame.getActualiteTable().update();
					
					// préraration du mail :
					MailFile.prepareMail(transport);
					
					// Backup
					database.createBackup();
				}
				else { //MODE MODIFICATION LIGNE
					
					int num = 0;
					if ( mode.equals(Mode.SUIVI) || mode.equals(Mode.SUIVI_LIGHT) ) { // La ligne modifié vient du tableau SUIVI
						// Récupération du numéro du transport et de la ligne à modifier
						int rowIndex = frame.getSuiviTable().convertRowIndexToModel( TransportFrame.getInstance().getSuiviTable().getSelectedRow() );
						num = TransportFrame.getInstance().getSuiviTableModel().getNum( rowIndex );
						// Récupération de la validation, du statut et de facturation
						transport.setValidation( TransportFrame.getInstance().getSuiviTableModel().getTransport(rowIndex).getValidation() );
						transport.setStatut( TransportFrame.getInstance().getSuiviTableModel().getTransport(rowIndex).getStatut() );
						transport.setFacturation( TransportFrame.getInstance().getSuiviTableModel().getTransport(rowIndex).getFacturation() );
					}
					else { // La ligne modifié vient du tableau ACTUALITE
						
						// Récupération du numéro du transport et de la ligne à modifier
						int rowIndex = frame.getActualiteTable().convertRowIndexToModel( TransportFrame.getInstance().getActualiteTable().getSelectedRow() );
						num = TransportFrame.getInstance().getActualiteTableModel().getNum( rowIndex );
						// Récupération de la validation, du statut et de facturation
						transport.setValidation( TransportFrame.getInstance().getActualiteTableModel().getTransport(rowIndex).getValidation() );
						transport.setStatut( TransportFrame.getInstance().getActualiteTableModel().getTransport(rowIndex).getStatut() );
						transport.setFacturation( TransportFrame.getInstance().getActualiteTableModel().getTransport(rowIndex).getFacturation() );
					}
					// actualisation du statut (si la date à été modifiée)
					if ( transport.getStatut() != Statut.TRANSPORT_ANNULE && transport.getStatut() != Statut.TRANSPORT_SUPPRIME ) {
						transport.updateStatut();
					}
					// Modificaition dans le BDD
					database.edit(transport, num);
					database.nextVersion();
					
					
					// Actualisation de suivi et actualité
					frame.getActualiteTable().update();
					frame.getSuiviTable().update();
				}
				setVisible(false);
				dispose();
			}				
		}
	}
	
	// Action anuuler
	public class ActionAnnuler implements ActionListener {
		public void actionPerformed( ActionEvent e ) {
			setVisible(false);
			dispose();
		}
	}
	

	//////////////////////////////////////
	/////////   CREATION LIGNES     //////
	//////////////////////////////////////
	
	/** Ajoute un champ de saisie standard dans le formulaire
	 */
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
	
	
	/** Ajoute un champ de saisie pour la date dans le formulaire
	 */
	private void addDateField( String titre ) {
		JLabel marge = new JLabel();
		JLabel label = new JLabel(titre);
		JTextField fieldD = new JTextField(2); //Day
		JTextField fieldM = new JTextField(2); //Month
		JTextField fieldY = new JTextField(4); //Year
		
		fieldD.getDocument().addDocumentListener( new NextFocus(2,fieldD) );
		fieldM.getDocument().addDocumentListener( new NextFocus(2,fieldM) );
		fieldY.getDocument().addDocumentListener( new NextFocus(4,fieldY) );
		
		marge.setMinimumSize( new Dimension(77,20) );
		marge.setMaximumSize( new Dimension(77,20) );
		label.setMinimumSize( new Dimension(this.xField,this.yField) );		
		label.setMaximumSize( new Dimension(this.xField,this.yField) );
		fieldD.setMinimumSize( new Dimension(20,20) );
		fieldM.setMinimumSize( new Dimension(20,20) );
		fieldY.setMinimumSize( new Dimension(35,20) );
		fieldD.setMaximumSize( new Dimension(20,20) );
		fieldM.setMaximumSize( new Dimension(20,20) );
		fieldY.setMaximumSize( new Dimension(35,20) );
		
		Box dateBox = Box.createHorizontalBox();
		dateBox.add( marge );
		dateBox.add( fieldD );
		dateBox.add( new JLabel(" / "));
		dateBox.add( fieldM );
		dateBox.add( new JLabel(" / "));
		dateBox.add( fieldY );
		
		this.leftBox.add( label );
		this.rightBox.add( dateBox );
		
		JTextField  list[] = { fieldD , fieldM , fieldY };
		this.fieldList.add( list );
	}
	
	
	/** Ajoute un champ de saisie pour l'heure dans le formulaire 
	 */
	private void addTimeField( String titre ) {
		JLabel marge = new JLabel();
		JLabel label = new JLabel(titre);
		JTextField fieldH = new JTextField(2); //Hour
		JTextField fieldM = new JTextField(2); //Minutes
		
		fieldH.getDocument().addDocumentListener( new NextFocus(2,fieldH) );
		fieldM.getDocument().addDocumentListener( new NextFocus(2,fieldM) );
		
		marge.setMinimumSize( new Dimension(121,20) );
		marge.setMaximumSize( new Dimension(121,20) );
		label.setMinimumSize( new Dimension(this.xField,this.yField) );		
		label.setMaximumSize( new Dimension(this.xField,this.yField) );
		fieldH.setMinimumSize( new Dimension(20,20) );
		fieldM.setMinimumSize( new Dimension(20,20) );
		fieldH.setMaximumSize( new Dimension(20,20) );
		fieldM.setMaximumSize( new Dimension(20,20) );
		
		Box timeBox = Box.createHorizontalBox();
		timeBox.add( marge );
		timeBox.add( fieldH );
		timeBox.add( new JLabel(" : "));
		timeBox.add( fieldM );
		
		this.leftBox.add( label );
		this.rightBox.add( timeBox );
		
		JTextField  list[] = { fieldH , fieldM  };
		this.fieldList.add( list );
	}
	
	
	/** Ajout un menu déroulant dans le formulaire.
	 */
	private void addComboBox( String titre, String [] choix ) {
		final Box fieldBox = Box.createHorizontalBox();
		final JLabel label = new JLabel(titre);
		final JComboBox field = new JComboBox();
		final JTextField fieldAutre = new JTextField();
		final JButton boutonAutre = new JButton("...");
		
		label.setMinimumSize( new Dimension(this.xField,this.yField) );
		label.setMaximumSize( new Dimension(this.xField,this.yField) );
		fieldBox.setMinimumSize( new Dimension(this.xField,this.yField) );
		fieldBox.setPreferredSize( new Dimension(this.xField,this.yField) );		
		fieldBox.setMaximumSize( new Dimension(this.xField,this.yField) );
		field.setMinimumSize( new Dimension(this.xField-1,this.yField) );
		field.setMaximumSize( new Dimension(this.xField-1,this.yField) );
		fieldAutre.setMinimumSize( new Dimension(this.xField -29,this.yField) );		
		fieldAutre.setMaximumSize( new Dimension(this.xField -29,this.yField) );
		boutonAutre.setMinimumSize( new Dimension( 28, 20) );
		boutonAutre.setMaximumSize( new Dimension( 28, 20) );
				
		for ( String s : choix ) {
			field.addItem(s);
		}
		field.addItem("Autre");

		// Ré-afficher le menu déroulant quand on appuye sur le bouton de retour
		boutonAutre.addActionListener(new ActionListener(){
			public void actionPerformed( ActionEvent e ) {
				field.setSelectedIndex(0);
				fieldBox.remove(boutonAutre);
				fieldBox.remove(fieldAutre);
				fieldBox.add(field);
				fieldBox.revalidate();
				fieldBox.repaint();
			}
		});
		
		// Activer le champ de saisie quand on appuye sur "Autre"
		field.addActionListener(new ActionListener(){
			public void actionPerformed( ActionEvent e ) {
				if ( field.getSelectedItem().toString().equals("Autre") ) {
					fieldBox.remove(field);
					fieldBox.add(fieldAutre);
					fieldBox.add(boutonAutre);
					fieldAutre.requestFocus();
					fieldBox.revalidate();
					fieldBox.repaint();				
				}
			}
		});
		
		fieldBox.add( field );
		this.leftBox.add( label );
		this.rightBox.add( fieldBox );

		Object list[] = { field , fieldAutre , boutonAutre };
		this.fieldList.add( list );
	}
	

	//////////////////////////////////////
	/////////   RECOLTE DONNEES    ///////
	//////////////////////////////////////
	
	/** Permet d'obtenir le texte des différents champs à savoir : 
	 * les champs STANDARD (JTexField)
	 * les champs de DATE (3 JTextField)
	 * les champs de HEURE (2 JTextField)
	 * les CHOIX MULTIPLES (JComboBox ou JTexField si "Autre"
	 * 
	 * ATTENTION A NE PAS UTILISER POUR DateDepart !
	 */
	private static String getText( Object field ) {
		if (field instanceof JTextField) { //Standard
			return ( (JTextField) field ).getText();
		}
		else if (field instanceof JTextField[]) {
			if ( ((JTextField[]) field).length == 3 ) { // date
				return ( (JTextField[]) field )[0].getText() + "/" 
					 + ( (JTextField[]) field )[1].getText() + "/" 
					 + ( (JTextField[]) field )[2].getText() ;
			}
			else if ( ((JTextField[]) field).length == 2 ) { // Heure
				return ( (JTextField[]) field )[0].getText() + ":" 
					 + ( (JTextField[]) field )[1].getText() ;
			}
			else return ""; 
		}
		else if (field instanceof Object[]) { // Choix multiples
			if ( ( (JComboBox) ( (Object[]) field )[0] ).getSelectedItem().toString().equals("Autre") ) { // Cas on a choisi "Autre"
				return ( (JTextField) ( (Object[]) field )[1] ).getText();
			}
			else { // Cas : on a choisi dans la liste
				return ( (JComboBox) ( (Object[]) field )[0] ).getSelectedItem().toString(); 
			}
		}
		else return "";
	}
	
	//////////////////////////////////////
	/////////   PRE-SET DONNEES    ///////
	//////////////////////////////////////
		
	/** Pré-remplir le texte d'une ligne (champ standard Date et Time)
	 */
	private static void setText ( Object field , String text ) {
		if (field instanceof JTextField) {
			( (JTextField) field ).setText(text); // champ standard
		}
		else if (field instanceof JTextField[]) {
			if ( ((JTextField[]) field).length == 3 ) { // date
				String dateList[] = text.split("/",3);
				 ( (JTextField[]) field )[0].setText(dateList[0]);
				 ( (JTextField[]) field )[1].setText(dateList[1]);
				 ( (JTextField[]) field )[2].setText(dateList[2]);
			}
			else if ( ((JTextField[]) field).length == 2 ) { // time
				String timeList[] = text.split(":",2);				
				( (JTextField[]) field )[0].setText(timeList[0]);
				( (JTextField[]) field )[1].setText(timeList[1]);
			}
		}
		else if (field instanceof Object[]) { // Choix multiples
				JComboBox combobox = (JComboBox) ( (Object[]) field )[0] ;
				ArrayList<String> choixList = new ArrayList<String>();
				for ( int i=0 ; i<combobox.getItemCount() ; i++ ) {
					choixList.add( (String) combobox.getItemAt(i) );
				}			
			if ( !choixList.contains(text) )  { // Cas on a choisi "Autre"
				combobox.setSelectedItem( "Autre" );				
				( (JTextField) ( (Object[]) field )[1] ).setText(text);
			}
			else { // Cas : on a choisi dans la liste
				combobox.setSelectedItem( text );
			}
		}
	}
	
	/** Pré-remplit les champs avec les données de la ligne à modifier
	 */
	private void initModif () {
		Transport transport;
		if ( mode.equals(Mode.SUIVI) ){
			int rowIndex = TransportFrame.getInstance().getSuiviTable().convertRowIndexToModel( TransportFrame.getInstance().getSuiviTable().getSelectedRow() ); // le num de de la ligne
			transport = TransportFrame.getInstance().getSuiviTableModel().getTransport(rowIndex); // le transport avec les infos demandées
		}
		else {
			int rowIndex = TransportFrame.getInstance().getActualiteTable().convertRowIndexToModel( TransportFrame.getInstance().getActualiteTable().getSelectedRow() ); // le num de de la ligne
			transport = TransportFrame.getInstance().getActualiteTableModel().getTransport(rowIndex); // le transport avec les infos demandées
		}
		EditionFrame.setText( fieldList.get(0) , transport.getEntree(0) );
		EditionFrame.setText( fieldList.get(1) , transport.dateDepart() );		
		EditionFrame.setText( fieldList.get(2) , transport.heureDepart() );	
		EditionFrame.setText( fieldList.get(3) , transport.getEntree(2) );
		EditionFrame.setText( fieldList.get(4) , transport.getEntree(3) );
		EditionFrame.setText( fieldList.get(6) , transport.getEntree(4) );
		EditionFrame.setText( fieldList.get(5) , transport.getEntree(5) );
		EditionFrame.setText( fieldList.get(7) , transport.getEntree(6) );
		EditionFrame.setText( fieldList.get(8) , transport.getEntree(7) );
		EditionFrame.setText( fieldList.get(9) , transport.getEntree(8) );
		EditionFrame.setText( fieldList.get(10), transport.getEntree(9) );
		EditionFrame.setText( fieldList.get(14), transport.getEntree(10));
		EditionFrame.setText( fieldList.get(11), transport.getEntree(11));
		EditionFrame.setText( fieldList.get(12), transport.getEntree(12));
		EditionFrame.setText( fieldList.get(13), transport.getEntree(13));
		EditionFrame.setText( fieldList.get(15), transport.getEntree(14));
		EditionFrame.setText( fieldList.get(16), transport.getEntree(15));
		EditionFrame.setText( fieldList.get(17), transport.getEntree(16));
		EditionFrame.setText( fieldList.get(18), transport.getEntree(17));
		EditionFrame.setText( fieldList.get(19), transport.getEntree(18));
		EditionFrame.setText( fieldList.get(20), transport.getEntree(19));
		EditionFrame.setText( fieldList.get(21), transport.getEntree(20));
		EditionFrame.setText( fieldList.get(22), transport.getEntree(21));
		EditionFrame.setText( fieldList.get(23), transport.getEntree(22));
		EditionFrame.setText( fieldList.get(24), transport.getEntree(23));
		EditionFrame.setText( fieldList.get(25), transport.getEntree(24));		
		EditionFrame.setText( fieldList.get(26), transport.getEntree(25));
		EditionFrame.setText( fieldList.get(27), transport.getEntree(26));
		EditionFrame.setText( fieldList.get(28), transport.getEntree(27));		
		EditionFrame.setText( fieldList.get(29), transport.getEntree(28));	
	}
	
	
	/** Fermet de changer le focus pour les dates 
	 */
	public class NextFocus implements DocumentListener {
		
		private int taille = 0;
		JTextField field;
		
		public NextFocus( int taille , JTextField field ) {
			this.taille = taille;
			this.field = field;
		}
		
		@Override public void changedUpdate(DocumentEvent e) { run(e); }
		@Override public void insertUpdate(DocumentEvent e) { run(e); }
		@Override public void removeUpdate(DocumentEvent e) { run(e); }
		
		public void run(DocumentEvent e) {
			if ( e.getDocument().getLength() >= taille ) {
				this.field.transferFocus();
			}
		}
	}
	
	
	/** Obtenir le nombre d'entrées du formulaire.
	 */
	public int nbEntrees() {
		return this.fieldList.size();
	}
	
	// ArrayList sur les String pour comparer en ignorant les Majuscules
	public class ArrayListCase extends ArrayList<String> {
		 
		@Override
		public boolean contains(Object o) {
			String paramStr = (String) o;
			for (String s : this) {
				if ( paramStr.equalsIgnoreCase(s) ) return true;
			}
			return false;
		}
	}

}
