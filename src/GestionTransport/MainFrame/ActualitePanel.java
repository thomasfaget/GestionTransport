package GestionTransport.MainFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import javax.swing.text.MaskFormatter;

import org.oxbow.swingbits.table.filter.JTableFilter;
import org.oxbow.swingbits.table.filter.TableRowFilterSupport;

import GestionTransport.DataBase.DataBase;
import GestionTransport.DataBase.Mode;
import GestionTransport.Outils.ExcelFile;
import GestionTransport.Tableau.CellRendererActualite;
import GestionTransport.Tableau.TransportTable;
import GestionTransport.Tableau.TransportTableModel;
import GestionTransport.Transport.Transport;

/** Créer l'onglet "actualité"
 */
public class ActualitePanel extends JPanel {
	
	private TransportTable table;
	
	private Box buttonBox;
	private Box panelBox;
	
	private JButton creer = new JButton("Créer");
	private JButton modifier = new JButton("Modifier");
	private JButton annuler = new JButton("Annuler");
	private JButton supprimer = new JButton("Supprimer");
	private JButton resetFiltre = new JButton("Reset Filtre");
	private JCheckBox selectionMode = new JCheckBox("Mode selection");
	private JButton extraction = new JButton("Extraction");
	
	private Timer timer = new Timer();
	
	private JLabel labelDateBefore = new JLabel("Du :");
	private JLabel labelDateAfter = new JLabel("Au :");
	private JFormattedTextField fieldDateBefore = new JFormattedTextField();
	private JFormattedTextField fieldDateAfter = new JFormattedTextField();

	private SimpleDateFormat dateFormat =  new SimpleDateFormat("dd/MM/yyyy") ;
	
	private JTableFilter filter;
	private DateFilter dateFilter;
	
	private Mode mode;

	
	
	public ActualitePanel(Mode mode) {
		super();
		this.setLayout( new BorderLayout() );
		this.mode = mode;
		dateFormat.setLenient(false);
		
		// Actions des boutons :
		if (mode != Mode.ACTUALITE_PG) {
			if (mode != Mode.ACTUALITE_EXPE) {
				this.creer.addActionListener( new ActionCreer() ); 
				this.modifier.addActionListener( new ActionModifier() );
				this.annuler.addActionListener( new ActionAnnuler() );
				this.supprimer.addActionListener( new ActionSupprimer() );
			}
			this.resetFiltre.addActionListener( new FiltreTout() );
			if (mode != Mode.ACTUALITE_EXPE) {
				this.selectionMode.addActionListener( new ActionSelectionMode() );
				this.extraction.addActionListener( new ActionExtraction() );
			}
		}
		
		// Formatter pour les textfield du filtre date
		try {
			MaskFormatter formatter = new MaskFormatter("##/##/####");
			this.fieldDateBefore = new JFormattedTextField(formatter);
			this.fieldDateAfter = new JFormattedTextField(formatter);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		// Récupération des dates pour le filtre
		this.fieldDateBefore.getDocument().addDocumentListener( new ActionDateBefore() );
		this.fieldDateAfter.getDocument().addDocumentListener( new ActionDateAfter() );
		
		// Réglage des textfield et des labels pour le filtre Date :
		this.labelDateBefore.setMinimumSize( new Dimension(30,20) );
		this.labelDateBefore.setPreferredSize( new Dimension(30,20) );
		this.labelDateBefore.setMaximumSize( new Dimension(30,20) );
		this.labelDateAfter.setMinimumSize( new Dimension(30,20) );
		this.labelDateAfter.setPreferredSize( new Dimension(30,20) );
		this.labelDateAfter.setMaximumSize( new Dimension(30,20) );
		this.fieldDateBefore.setMinimumSize( new Dimension(70,20) );
		this.fieldDateBefore.setPreferredSize( new Dimension(70,20) );
		this.fieldDateBefore.setMaximumSize( new Dimension(70,20) );
		this.fieldDateAfter.setMinimumSize( new Dimension(70,20) );
		this.fieldDateAfter.setPreferredSize( new Dimension(70,20) );
		this.fieldDateAfter.setMaximumSize( new Dimension(70,20) );

		
		// Sections des zones boutons
		this.buttonBox = Box.createHorizontalBox();
		this.buttonBox.setBackground( new Color(0,50,95) );
		
		if (mode != Mode.ACTUALITE_PG) {
			if (mode != Mode.ACTUALITE_EXPE) {
				// Section des boutons d'editions
				Box editionBox = Box.createHorizontalBox();
				editionBox.add( Box.createVerticalStrut(1) );
				editionBox.add( creer );
				editionBox.add( Box.createVerticalStrut(1) );
				editionBox.add( modifier );
				editionBox.add( Box.createVerticalStrut(1) );
				editionBox.add( annuler );
				editionBox.add( Box.createVerticalStrut(1) );
				editionBox.add( supprimer );
				editionBox.add( Box.createVerticalStrut(1) );
				editionBox.setBorder( new TitledBorder("Edition") );
				editionBox.setPreferredSize( new Dimension( 500,70 ));
				editionBox.setMaximumSize( new Dimension( 500,70 ));
				this.buttonBox.add( editionBox );
			}
			
			// Section filtre
			Box filtreBox = Box.createHorizontalBox();
			filtreBox.add( Box.createVerticalStrut(1) );
				Box labelDate = Box.createVerticalBox();
				labelDate.add( this.labelDateBefore );
				labelDate.add( this.labelDateAfter );
			filtreBox.add( labelDate );
				Box fieldDate = Box.createVerticalBox();
				fieldDate.add( this.fieldDateBefore );
				fieldDate.add( this.fieldDateAfter );
			filtreBox.add( fieldDate );
			filtreBox.add( Box.createVerticalStrut(1) );
			filtreBox.add( resetFiltre );
			filtreBox.add( Box.createVerticalStrut(1) );
			filtreBox.setBorder( new TitledBorder("Filtre") );
			filtreBox.setPreferredSize( new Dimension( 320,70 ));
			filtreBox.setMaximumSize( new Dimension( 320,70 ));
			this.buttonBox.add( filtreBox );
			
			if (mode != Mode.ACTUALITE_EXPE) {
				// Section excel
				Box excelBox = Box.createHorizontalBox();
				excelBox.add( Box.createVerticalStrut(1) );
				excelBox.add( selectionMode );
				excelBox.add( Box.createVerticalStrut(1) );
				excelBox.add( extraction );
				excelBox.add( Box.createVerticalStrut(1) );
				excelBox.setBorder( new TitledBorder("Extraction vers excel") );
				excelBox.setPreferredSize( new Dimension( 350,70 ));
				excelBox.setMaximumSize( new Dimension( 350,70 ));
				this.buttonBox.add( excelBox );
			}
			this.buttonBox.add( Box.createVerticalStrut(1) );
		}	
		
		// Création section tableau :
		this.panelBox = Box.createVerticalBox();
		this.table = new TransportTable(mode);
		JScrollPane scrollPane =  new JScrollPane( this.table , JScrollPane.VERTICAL_SCROLLBAR_ALWAYS , JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS );
		this.panelBox.add( scrollPane , BorderLayout.CENTER );
		scrollPane.getViewport().setBackground( new Color(0,50,95) );

		
		// Création fenêtre finale
		Box panel = Box.createVerticalBox();
		panel.add( this.buttonBox );
		panel.add( this.panelBox );
		this.add( panel , BorderLayout.CENTER );
		
		// Actualisation du tableau
		timer.schedule( new TimerTask() {
			public void run() {
				table.testVersion();
			}
		}, 1000, 1000);
		
		if ( !mode.equals(Mode.ACTUALITE_PG) ) {
			// Filtre
			this.filter = new JTableFilter(this.table);
			TableRowFilterSupport.forFilter(filter).searchable(true).useTableRenderers(true).apply();
			// Filtre spécial sur la date
			this.dateFilter = new DateFilter();
			TableRowSorter<TransportTableModel> sorter = new TableRowSorter<TransportTableModel>(getTableModel());
			sorter.setRowFilter( dateFilter );
			this.table.setRowSorter( sorter );
		}
		
		// Renderer
		this.table.setDefaultRenderer(Object.class, new CellRendererActualite() );
	}
	
	// Action d'édition : créer
	public class ActionCreer implements ActionListener {
		public void actionPerformed( ActionEvent e ) {
			new EditionFrame(true, mode);
		}
	}
	
	// Action d'édtion : Modifier
	public class ActionModifier implements ActionListener {
		public void actionPerformed( ActionEvent e ) {
			if ( table.getSelectedRow() != -1 ) {
				new EditionFrame(false, mode);
			}
		}
	}
	
	// Action d'édtion : Annuler
	public class ActionAnnuler implements ActionListener {
		public void actionPerformed( ActionEvent e ) {
			if ( table.getSelectedRow() != -1 ) {
				int row = getTable().convertRowIndexToModel( table.getSelectedRow() );
				Transport transport = getTableModel().getTransport(row);
				int num = transport.getNum();
				transport.annuler(); // On annule le transport 
				DataBase.getInstance().setStatut(transport); // On actualise la BDD
				DataBase.getInstance().nextVersion();
				int tableVersion = DataBase.getInstance().getVersion(); 
				table.setVersion(tableVersion); // On actualise manuelement la version de actualité
				getTableModel().removeTransport(row);
				getTableModel().fireTableRowsDeleted(row, row);
				if ( TransportFrame.getInstance().getSuiviTable() != null ) {
					// On actualise la ligne dans le tableau suivi :
					TransportFrame.getInstance().getSuiviTable().setVersion(tableVersion); // On actualise manuelement la version de suivi
					TransportTableModel modelSuivi = TransportFrame.getInstance().getSuiviTableModel();
					int rowActualite = modelSuivi.indexOfNum(num);
					modelSuivi.setTransport(rowActualite, transport);
					modelSuivi.fireTableRowsUpdated(rowActualite, rowActualite);
				}
			}
		}
	}
	
	// Action d'édtion : Supprimer
	public class ActionSupprimer implements ActionListener {
		public void actionPerformed( ActionEvent e ) {
			if ( table.getSelectedRow() != -1 ) {
				int reponse = JOptionPane.showConfirmDialog(null, "Etes-vous sûr?\nCette action est irreversible !", "Confirmation suppression",JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if ( reponse == JOptionPane.YES_OPTION ) {
					int row = getTable().convertRowIndexToModel( table.getSelectedRow() );
					Transport transport = getTableModel().getTransport(row);
					int num = transport.getNum();
					transport.supprimer(); // On annule le transport 
					DataBase.getInstance().setStatut(transport); // On actualise la BDD
					int tableVersion = DataBase.getInstance().getVersion(); 
					table.setVersion(tableVersion); // On actualise manuelement la version de actualité
					getTableModel().removeTransport(row);
					getTableModel().fireTableRowsDeleted(row, row);
					if ( TransportFrame.getInstance().getSuiviTable() != null ) {
						// On actualise la ligne dans le tableau suivi :
						TransportFrame.getInstance().getSuiviTable().setVersion(tableVersion); // On actualise manuelement la version de suivi
						TransportTableModel modelSuivi = TransportFrame.getInstance().getSuiviTableModel();
						int rowActualite = modelSuivi.indexOfNum(num);
						modelSuivi.removeTransport(rowActualite);
						modelSuivi.fireTableRowsDeleted(rowActualite, rowActualite);
					}
				}
			}
		}
	}
	
	// Action des filtres : Commande réalisé
	public class FiltreTout implements ActionListener {
		public void actionPerformed( ActionEvent e ) {
			filter.clear(); // Reset du flitre
			table.getRowSorter().modelStructureChanged(); // reset du sorter
			fieldDateBefore.setValue(null);
			fieldDateAfter.setValue(null);
			dateFilter.setDateAfter(null);
			dateFilter.setDateBefore(null);
			fieldDateBefore.setForeground( Color.black);
			fieldDateAfter.setForeground( Color.black);
		}
	}
	
	// Action excel : selection Mode
	public class ActionSelectionMode implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if ( ( (JCheckBox) e.getSource() ).isSelected() ) {
				modifier.setEnabled(false);
				annuler.setEnabled(false);
				supprimer.setEnabled(false);
				table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				table.setColumnSelectionAllowed(true);
			}
			else {
				modifier.setEnabled(true);
				annuler.setEnabled(true);
				supprimer.setEnabled(true);
				table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				table.setColumnSelectionAllowed(false);
			}
		}
	}
	
	// Action excel : extraction
	public class ActionExtraction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			TransportTableModel model = getTableModel();
			if ( selectionMode.isSelected() ) {
				int rowList[] = table.getSelectedRows();
				int columnList[] = table.getSelectedColumns();
				// Récolte des données : Noms colonnes 
				String title[] = new String[columnList.length];
				for ( int col=0 ; col<columnList.length ; col++ ) {
					title[col] = table.getColumnName(col);
				}
				// Récolte des données : Contenu de la table
				String data[][] = new String[rowList.length][columnList.length];
				for ( int row=0 ; row<rowList.length ; row++) {
					for ( int column=0 ; column<columnList.length ; column++ ) {
						data[row][column] = model.getValueAt(table.convertRowIndexToModel(rowList[row]), table.convertColumnIndexToModel(columnList[column]) ).toString();
					}
				}
				// Création du fichier et remplissage à l'aide des données
				ExcelFile.createFile(data, title);
			}
			else {
				// Récolte des données : Noms colonnes 
				String title[] = new String[table.getColumnCount()];
				for ( int col=0 ; col<table.getColumnCount() ; col++ ) {
					title[col] = table.getColumnName(col);
				}
				// Récolte des données : Contenu de la table
				String data[][] = new String[table.getRowCount()][table.getColumnCount()];	
				for ( int row=0 ; row<table.getRowCount() ; row++) {
					for ( int column=0 ; column<table.getColumnCount() ; column++ ) {
						data[row][column] = model.getValueAt(table.convertRowIndexToModel(row), table.convertColumnIndexToModel(column) ).toString();
					}
				}
				ExcelFile.createFile(data, title);
			}
		}
	}
	
	public class ActionDateBefore implements DocumentListener {

		@Override public void changedUpdate(DocumentEvent arg0) { run(); }
		@Override public void insertUpdate(DocumentEvent arg0) { run(); }
		@Override public void removeUpdate(DocumentEvent arg0) {run() ;}	
		
		private void run() {
			String date = fieldDateBefore.getText();
			try {
				Date dateBefore = dateFormat.parse(date);
				fieldDateBefore.setForeground( Color.black);
				dateFilter.setDateBefore(dateBefore);
			} 
			catch (ParseException e1) {
				fieldDateBefore.setForeground( Color.red);
			}		
		}	
	}
	
	public class ActionDateAfter implements DocumentListener {

		@Override public void changedUpdate(DocumentEvent arg0) { run(); }
		@Override public void insertUpdate(DocumentEvent arg0) { run(); }
		@Override public void removeUpdate(DocumentEvent arg0) { run(); }	
		
		private void run() {
			String date = fieldDateAfter.getText();
			try {
				Date dateAfter = dateFormat.parse(date);
				dateFilter.setDateAfter(dateAfter);
				fieldDateAfter.setForeground( Color.black);
			} 
			catch (ParseException e1) {
				fieldDateAfter.setForeground( Color.red);
			}		
		}
	}
	
	public class DateFilter extends RowFilter<Object,Object>{		

		int indexDateDepart = getTableModel().findColumn("Date de départ");
		private Date dateBefore;
		private Date dateAfter;
		
		@Override
		public boolean include( Entry<? extends Object, ? extends Object> entry) {
			try {
				Date date = dateFormat.parse( entry.getStringValue(indexDateDepart) );
				return ( dateAfter == null && dateBefore == null)
					|| ( dateAfter == null && dateBefore != null && dateBefore.equals(date) )
					|| ( dateBefore != null && dateAfter != null && !date.before(dateBefore) && !date.after(dateAfter) );
		
			} catch (ParseException e) {
				return true;
			}
			catch (Exception e) {
				return true;
			}
			
		}
	
		public void setDateAfter( Date date ) {
			this.dateAfter = date;
			table.getRowSorter().modelStructureChanged();
		}
		public void setDateBefore( Date date ) {
			this.dateBefore = date;
			table.getRowSorter().modelStructureChanged();
		}
	}
	
	public TransportTable getTable() {
		return this.table;
	}
	
	public TransportTableModel getTableModel() {
		return (TransportTableModel) this.table.getModel();
	}
	
	public void stopTimer() {
		this.timer.cancel();
	}

}
