package GestionTransport.MainFrame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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
import GestionTransport.Tableau.CellRendererSuivi;
import GestionTransport.Tableau.TransportTable;
import GestionTransport.Tableau.TransportTableModel;
import GestionTransport.Transport.Transport;

/** Cr?er l'onglet "actualit?"
 */
public class SuiviPanel extends JPanel {
	
	
	private TransportTable table;
	
	private Box buttonBox;
	private Box panelBox;

	private JButton modifier = new JButton("Modifier");
	private JButton annuler = new JButton("Annuler");
	private JButton supprimer = new JButton("Supprimer");
	private JButton resetFiltre = new JButton("Reset Filtre");
	private JCheckBox selectionMode = new JCheckBox("Mode selection");
	private JButton extraction = new JButton("Extraction");
	private JButton changerSuivi = new JButton("R?duire");
	private JButton actualiser = new JButton("Actualiser");
	
	private JLabel labelDateBefore = new JLabel("Du :");
	private JLabel labelDateAfter = new JLabel("Au :");
	private JFormattedTextField fieldDateBefore = new JFormattedTextField();
	private JFormattedTextField fieldDateAfter = new JFormattedTextField();

	private SimpleDateFormat dateFormat =  new SimpleDateFormat("dd/MM/yyyy") ;
	
	private JTableFilter filter;
	private DateFilter dateFilter;
	TableRowSorter<TransportTableModel> sorter;
	
	private boolean isLight = false; // Indique si Suivi est en mode r?duit ou non
	
	
	public SuiviPanel() {
		super();
		this.setLayout( new BorderLayout() );
		dateFormat.setLenient(false);
		
		// Actions des boutons :
		this.modifier.addActionListener( new ActionModifier() );
		this.annuler.addActionListener( new ActionAnnuler() );
		this.supprimer.addActionListener( new ActionSupprimer() );
		this.resetFiltre.addActionListener( new FiltreTout() );
		this.selectionMode.addActionListener( new ActionSelectionMode() );
		this.extraction.addActionListener( new ActionExtraction() );
		this.changerSuivi.addActionListener( new ChangerSuivi() );
		this.actualiser.addActionListener( new ActionActualiser () );
		
		// Formatter pour les textfield du filtre date
		try {
			MaskFormatter formatter = new MaskFormatter("##/##/####");
			this.fieldDateBefore = new JFormattedTextField(formatter);
			this.fieldDateAfter = new JFormattedTextField(formatter);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		// R?cup?ration des dates pour le filtre
		this.fieldDateBefore.getDocument().addDocumentListener( new ActionDateBefore() );
		this.fieldDateAfter.getDocument().addDocumentListener( new ActionDateAfter() );
		
		// R?glage des textfield et des labels pour le filtre Date :
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
		
			// Section des boutons d'editions
			Box editionBox = Box.createHorizontalBox();
			editionBox.add( Box.createVerticalStrut(1) );
			editionBox.add( modifier );
			editionBox.add( Box.createVerticalStrut(1) );
			editionBox.add( annuler );
			editionBox.add( Box.createVerticalStrut(1) );
			editionBox.add( supprimer );
			editionBox.add( Box.createVerticalStrut(1) );
			editionBox.setBorder( new TitledBorder("Edition") );
			editionBox.setPreferredSize( new Dimension( 410,70 ));
			editionBox.setMaximumSize( new Dimension( 410,70 ));
			this.buttonBox.add( editionBox );
			
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
			filtreBox.setPreferredSize( new Dimension( 300,70 ));
			filtreBox.setMaximumSize( new Dimension( 300,70 ));
			this.buttonBox.add( filtreBox );
			
			// Section excel
			Box excelBox = Box.createHorizontalBox();
			excelBox.add( Box.createVerticalStrut(1) );
			excelBox.add( selectionMode );
			excelBox.add( Box.createVerticalStrut(1) );
			excelBox.add( extraction );
			excelBox.add( Box.createVerticalStrut(1) );
			excelBox.setBorder( new TitledBorder("Extraction vers excel") );
			excelBox.setPreferredSize( new Dimension( 320,70 ));
			excelBox.setMaximumSize( new Dimension( 320,70 ));
			this.buttonBox.add( excelBox );
			
			// Section du boutons actualiser
			Box actualisationBox = Box.createHorizontalBox();
			actualisationBox.add( Box.createVerticalStrut(1) );
			actualisationBox.add( actualiser );
			actualisationBox.add( Box.createVerticalStrut(1) );
			actualisationBox.add( changerSuivi );
			actualisationBox.add( Box.createVerticalStrut(1) );
			actualisationBox.setBorder( new TitledBorder("Affichage") );
			actualisationBox.setPreferredSize( new Dimension( 250,70 ));
			actualisationBox.setMaximumSize( new Dimension( 250,70 ));
			this.buttonBox.add( actualisationBox );
		this.buttonBox.add( Box.createVerticalStrut(1) );
		
		// Cr?ation fen?tre finale
		this.panelBox = Box.createVerticalBox();
		Box panel = Box.createVerticalBox();
		panel.add( this.buttonBox );
		panel.add( this.panelBox );
		this.add( panel , BorderLayout.CENTER );
		
		// Cr?ation tableau
		this.table = new TransportTable(Mode.SUIVI);
		JScrollPane scrollPane =  new JScrollPane( this.table , JScrollPane.VERTICAL_SCROLLBAR_ALWAYS , JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS );
		this.panelBox.add( scrollPane , BorderLayout.CENTER );
		scrollPane.getViewport().setBackground( new Color(0,50,95) );
		
		// Filtre
		this.resetFiltre();
		
		// Renderer
		this.table.setDefaultRenderer(Object.class, new CellRendererSuivi() );	
	}
	
	public void resetFiltre() { 
		// Filtre
		this.filter = new JTableFilter(this.table);
		TableRowFilterSupport.forFilter(filter).searchable(true).useTableRenderers(true).apply();
		
		// Filtre sp?cial sur la date
		this.dateFilter = new DateFilter();
		this.sorter = new TableRowSorter<TransportTableModel>(getTableModel());
		this.sorter.setRowFilter( dateFilter );
		this.table.setRowSorter( sorter );
	}
	
	// Action modifier
	public class ActionModifier implements ActionListener {
		public void actionPerformed( ActionEvent e ) {
			if ( table.getSelectedRow() != -1 ) {
				new EditionFrame(false, Mode.SUIVI);
			}
		}
	}
	
	// Action d'?dtion : Annuler
	public class ActionAnnuler implements ActionListener {
		public void actionPerformed( ActionEvent e ) {
			if ( table.getSelectedRow() != -1 ) {
				int row = getTable().convertRowIndexToModel( table.getSelectedRow() );
				Transport transport = getTableModel().getTransport(row);
				transport.annuler(); // On annule dans le tableau
				DataBase.getInstance().setStatut(transport); // On annule dans le BDD
				int tableVersion = DataBase.getInstance().getVersion();
				table.setVersion(tableVersion); // Mise ? jour de suivi 
				getTableModel().fireTableRowsUpdated(row, row);
				TransportFrame.getInstance().getActualiteTable().update(); // Mise ? jour de actualit?
			}
		}
	}
	
	// Action d'?dtion : Supprimer
	public class ActionSupprimer implements ActionListener {
		public void actionPerformed( ActionEvent e ) {
				if ( table.getSelectedRow() != -1 ) {
				int reponse = JOptionPane.showConfirmDialog(null, "Etes-vous s?r?\nCette action est irreversible !", "Confirmation suppression",JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					if ( reponse == JOptionPane.YES_OPTION ) {
					int row = getTable().convertRowIndexToModel( table.getSelectedRow() );
					Transport transport = getTableModel().getTransport(row);
					transport.supprimer();
					getTableModel().removeTransport(row);
					getTableModel().fireTableRowsDeleted(row, row);
					DataBase.getInstance().setStatut(transport); // On supprime dans le BDD
					int tableVersion = DataBase.getInstance().getVersion();
					table.setVersion(tableVersion); // Mise ? jour de suivi 
					TransportFrame.getInstance().getActualiteTable().update(); // Mise ? jour de actualit?
				}
			}
		}
	}
	
	// Action des filtres : Commande r?alis?
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
				// R?colte des donn?es : Noms colonnes 
				String title[] = new String[columnList.length];
				for ( int col=0 ; col<columnList.length ; col++ ) {
					title[col] = table.getColumnName(col);
				}
				// R?colte des donn?es : Contenu de la table
				String data[][] = new String[rowList.length][columnList.length];
				for ( int row=0 ; row<rowList.length ; row++) {
					for ( int column=0 ; column<columnList.length ; column++ ) {
						data[row][column] = model.getValueAt(table.convertRowIndexToModel(rowList[row]), table.convertColumnIndexToModel(columnList[column]) ).toString();
					}
				}
				// Cr?ation du fichier et remplissage ? l'aide des donn?es
				ExcelFile.createFile(data, title);
			}
			else {
				// R?colte des donn?es : Noms colonnes 
				String title[] = new String[table.getColumnCount()];
				for ( int col=0 ; col<table.getColumnCount() ; col++ ) {
					title[col] = table.getColumnName(col);
				}
				// R?colte des donn?es : Contenu de la table
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
	
	// Action Actualiser
	public class ActionActualiser implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			getTable().testVersion();
		}
	}
	
	// Action ChangerSuivi
	public class ChangerSuivi implements ActionListener {
		public void actionPerformed( ActionEvent e ) {
			if (!isLight) {
				// R?duction
				table.setModel( new TransportTableModel(Mode.SUIVI_LIGHT) );
				table.setMode( Mode.SUIVI_LIGHT );
				table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
				table.getTableHeader().setResizingAllowed(true);
				changerSuivi.setText("R?tablir");
				isLight = true;
				resetFiltre();
			}
			else {
				// R?tablissement
				table.setModel( new TransportTableModel( Mode.SUIVI) );
				table.setMode( Mode.SUIVI );
				table.autoResize();
				table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				table.getTableHeader().setResizingAllowed(false);
				changerSuivi.setText("R?duire");
				isLight = false;
				resetFiltre();
			}
		}
	}
	
	public class DateFilter extends RowFilter<Object,Object>{		

		int indexDateDepart = getTableModel().findColumn("Date de d?part");
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

}
