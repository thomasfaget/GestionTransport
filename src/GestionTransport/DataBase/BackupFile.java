package GestionTransport.DataBase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;

public class BackupFile {
	
	private static String filePath = "./BD/path_backup.txt";
	
	
	public static String getBackupPath() throws FileNotFoundException {
		String path = "";
		InputStream ips = new FileInputStream( new File(filePath) );
		InputStreamReader ipsr = new InputStreamReader(ips);
		BufferedReader br = new BufferedReader(ipsr);
		try {
			path = br.readLine();
			br.close();
			ipsr.close();
			ips.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;	
	}
	
	public static void setBackupPath(String backupPath) throws IOException {
		try {	
			String oldPath = BackupFile.getBackupPath();
			
			File file = new File(oldPath + "/run.bat");
			file.delete();
			
			FileInputStream fileInputStream = new FileInputStream("./BD/run.bat");
			FileChannel in = fileInputStream.getChannel();
			FileOutputStream fileOutputStream = new FileOutputStream( backupPath + "/run.bat" );
			FileChannel out = fileOutputStream.getChannel();	
			in.transferTo(0, in.size(), out);
			fileInputStream.close();
			fileOutputStream.close();
			in.close();
			out.close();
			
			FileWriter fw = new FileWriter( new File(filePath) );
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
			pw.println(backupPath);
			pw.close();
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
