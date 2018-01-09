package GestionTransport.Login;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import GestionTransport.Login.User;


public class UserTableModel extends AbstractTableModel {
	
	private String title[] = {"Prénom","Nom","Identifiant","Mot de Passe","Rang","Adresse Mail"};
	
	private List<User> userList;
	
	public UserTableModel() {
		super();
		this.userList = LoginFileFactory.getAllUser();
	}
	
	public void addUser( int row , User user ) {
		this.userList.add( row , user );
	}
	
	public void removeUser( int row ) {
		this.userList.remove( row );
	}
	
	public User getUser( int row ) {
		return userList.get(row);
	}
	
	public void update() {
		this.userList.clear();
		this.userList = LoginFileFactory.getAllUser();
		this.fireTableDataChanged();
	}
	
	@Override
	public String getColumnName(int column) {
		return this.title[column];
	}

	@Override
	public int getColumnCount() {
		return this.title.length;
	}

	@Override
	public int getRowCount() {
		return this.userList.size();
	}

	@Override
	public Object getValueAt(int rowIndex , int columnIndex) {
		switch (columnIndex) {
			case 0 : return this.userList.get(rowIndex).getFirstname();
			case 1 : return this.userList.get(rowIndex).getLastname();
			case 2 : return this.userList.get(rowIndex).getUsername();
			case 3 : return this.userList.get(rowIndex).getPassword();
			case 4 : return this.userList.get(rowIndex).getLogin();
			case 5 : return this.userList.get(rowIndex).getMail();
			default: return null;
		}
	}

}
