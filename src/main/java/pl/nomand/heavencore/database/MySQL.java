package pl.nomand.heavencore.database;

public class MySQL {

	private final String host;
	private final String database;
	private final String username;
	private final String password;

	public MySQL(String host, String database, String username, String password) {
		this.host = host;
		this.database = database;
		this.username = username;
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public String getDatabase() {
		return database;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}
