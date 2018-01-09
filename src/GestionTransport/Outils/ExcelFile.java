package GestionTransport.Outils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.filechooser.FileSystemView;

import jxl.CellView;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExcelFile {
	
	
	
	/** Cr�e un fichier excel avec les donn�es data choisis
	 */
	public static void createFile( String data[][] , String title[] ) {
		
		String path = FileSystemView.getFileSystemView().getDefaultDirectory().getAbsolutePath();
		String folderName = "/Gestion Transport - Export Excel";
		
		try {
			// Test dossier existant -> cr�ation si non existant:
			File dossier = new File(path + folderName);
			if (!dossier.exists() ) {
				dossier.mkdir();
			}
			
			
			String fileName = "export " + new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format( new Date() ) + ".xls";
			WritableWorkbook workbook = Workbook.createWorkbook( new File(path + folderName + "/" + fileName) );
			WritableSheet sheet = workbook.createSheet("Page 1", 0);
			
			// Ecriture titre
			for ( int i=0 ; i<title.length ; i++ ) {
				Label label = new Label(i,0, title[i]);
				sheet.addCell( label );
				
			}
			
			// Ecriture donn�es
			for ( int i=0 ; i<data.length ; i++) {
				for ( int j=0 ; j<data[0].length ; j++ ) {
					Label label = new Label(j,i+1, data[i][j] );
					sheet.addCell( label );
				}
			}
			
			// R�glage taille colonne
			for (int j=0 ; j<sheet.getColumns() ; j++ ) {
				CellView cell = sheet.getColumnView(j);
				cell.setAutosize(true);
				sheet.setColumnView(j, cell);
			}
			
			workbook.write();
			workbook.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (RowsExceededException e) {
			e.printStackTrace();
		}
		catch (WriteException e) {
			e.printStackTrace();
		}
	}

}
