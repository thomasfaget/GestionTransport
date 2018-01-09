package GestionTransport.Login;

import GestionTransport.Login.Login;

/** Définit un utilisateur
 */
public class User {
	
	private String username;
	private String password;
	private Login login;
	
	private String firstname;
	private String lastname;
	
	private String mail;
	
	
	
	public User(String a_firstname , String a_lastname, String a_username, String a_password, Login a_login, String mail) {
		this.firstname = a_firstname;
		this.lastname = a_lastname;
		this.username = a_username;
		this.password = a_password;
		this.login = a_login;
		this.mail = mail;
	}
	
	/** renvoie le prénom de l'utilisateur
	 */
	public String getFirstname(){
		return this.firstname;
	}
	
	/** Renvoie le nom de famille de l'utilisateur
	 */
	public String getLastname(){
		return this.lastname;
	}
	
	/** renvoie l'identifiant de l'utilisateur
	 */
	public String getUsername(){
		return this.username;
	}
	
	/** Renvoie le mot de passe de l'utilisateur
	 */
	public String getPassword(){
		return this.password;
	}
	
	/** Renvoie le grade de l'utilisateur
	 */
	public Login getLogin() {
		return this.login;
	}
	
	/** Renvoie le mail de l'utilisateur
	 */
	public String getMail() {
		return this.mail;
	}
	
	/** Supprime le mot de passe de l'utilisateur contenu en mémoire.
	 */
	public void clearPassword() {
		this.password = "";
	}
	
	/** Teste si le usersame et le password en argument correspond à cet user.
	 */
	public boolean testUser(String pusername , String ppassword) {
		return (pusername == this.username) && (ppassword == this.password);
	}
}
