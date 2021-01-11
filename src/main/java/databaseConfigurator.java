import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.sql.*;
//import java.time.Duration;
//import java.time.Instant;

public class databaseConfigurator {
	private String db_host;
	private String db_port;
	private String db_name;
	private String db_username;
	private String db_password;
	private static Connection db;
	private static Statement st;
	public static final String FORK = "fork(date,uid,message)";
	public static final String INFRACTION = "infractions(uid,name,reason,date,issuedby,infractionpoints)";
	public static final String ANALYTIC = "analytics(date,memCount,membersIn,membersOut,netGain,messagesSent,banCount,moderations,notes)";
	public static final String ACTIONLOG = "actionlog(date,uid,authorTag,action,uid2,uid2Tag)";
	private Scanner in = new Scanner(System.in);

	// private Instant startTime; for testing execution time
	// private Instant endTime;
	public databaseConfigurator() {

		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("setup.config"));
			// startTime = Instant.now();
			databaseConnect(br);

		}

		catch (FileNotFoundException ex) {

			try {
				FileWriter fileWriter = new FileWriter("setup.config", true); // true makes it so text is appended to
																				// the file contents
				boolean success = false;
				while (!success) {
					success = initializer(fileWriter);
				}
				// startTime = Instant.now();
				br = new BufferedReader(new FileReader("setup.config"));
				databaseConnect(br);

				// format jdbc:postgresql://<database_host>:<port>/<database_name>
				// Connection db = DriverManager.getConnection(url, username, password);

			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Problem creating/writing to the file...");
				System.out.println(
						"Can be caused if the file exists but is a directory rather than a regular file, or does not exist and cannot be created");
				System.out.println(
						"Or because it cannot be opened for any other reason (possible file access violated, lacking permissions)");
			}

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	private boolean initializer(FileWriter fileWriter) throws IOException {

		System.out.println("Enter database host: (or type localhost)");
		fileWriter.write("host: " + in.next() + "\n");
		System.out.println("Enter port, standard postgresql port is 5432:");
		fileWriter.write("port: " + in.next() + "\n");
		System.out.println("Enter Database name to connect to:");
		fileWriter.write("db_name: " + in.next() + "\n");
		System.out.println("Enter username");
		fileWriter.write("username: " + in.next() + "\n");
		fileWriter.close(); // When you write data to a stream, it is not written immediately, and it is
		return true; // buffered for better performance
						// So use flush() when you need to be sure that all your data from buffer is
						// written. close() automatically flushes the stream.
	}

	private void databaseConnect(BufferedReader br) throws IOException {
		db_host = br.readLine().substring(5).trim(); // format host: xx.xxx.xx.xxx, in setup.config
		db_port = br.readLine().substring(5).trim(); // port: xxxx  .trim() to prevent errors if user puts an extra space between/after the input
		db_name = br.readLine().substring(8).trim(); // db_name: xxxxx
		db_username = br.readLine().substring(9).trim(); // username: xxxx
		String credentials = "\nhost: " + db_host + "\nport: " + db_port
			+ "\ndb_name: " + db_name + "\ndb_username " + db_username + "\n";
		br.close();
		System.out.println("Enter database password");
		db_password = in.next(); // rather not store password in clear text
		// format jdbc:postgresql://<database_host>:<port>/<database_name> -> omit the <>
		String url = "jdbc:postgresql://" + db_host + ":" + db_port + "/" + db_name;
		try {
			db = DriverManager.getConnection(url, db_username, db_password);
			st = db.createStatement();
			in.close();
			
				st.execute("CREATE TABLE IF NOT EXISTS infractions("
						+ "uid bigint NOT NULL,"
						+ "name varchar(37) NOT NULL," //32 letter username + #xxxx
						+ "reason varchar(255) NOT NULL,"
						+ "date timestamptz NOT NULL,"
						+ "issuedBy varchar(37) NOT NULL,"
						+ "infractionpoints smallint NOT NULL);");

				st.execute("CREATE TABLE IF NOT EXISTS analytics("
						+ "date timestamp NOT NULL,"
						+ "memCount integer NOT NULL,"
						+ "membersIn integer NOT NULL,"
						+ "membersOut integer NOT NULL," 
						+ "netGain integer NOT NULL,"
						+ "messagesSent integer NOT NULL,"
						+ "banCount integer NOT NULL,"
						+ "moderations integer NOT NULL,"
						+ "notes varchar(255));");
				
				st.execute("CREATE TABLE IF NOT EXISTS fork("
						+ "date timestamptz NOT NULL,"
						+ "uid bigint NOT NULL," 
						+ "message text NOT NULL);");
				
				st.execute("CREATE TABLE IF NOT EXISTS actionlog("
						+ "date timestamptz NOT NULL,"
						+ "uid bigint NOT NULL,"
						+ "authorTag varchar(37) NOT NULL,"
						+ "action text NOT NULL,"
						+ "uid2 bigint,"
						+ "uid2Tag varchar(37));");
				
				//"SET client_encoding TO 'UTF8';" in postgres if emojis are breaking the database
				/*
			
			 https://jdbc.postgresql.org/documentation/80/query.html
			 * documentation, keep scrolling to see how to execute updates and what not
			 */
				
		} catch (SQLException e) {
			if (e.getLocalizedMessage().contains("authentication failed")) {
				System.out.print("Invalid password please try again, ");
				System.out.println(
						"also make sure to check the setup.config file to ensure other credentials are correct");
				System.out.println("You are using credentials: " + credentials);
				databaseConnect(new BufferedReader(new FileReader("setup.config")));

			} else {
				System.out.println(e.getLocalizedMessage());
				System.out.println(
						"Error! Make sure your credentials/database is configured correctly.. Credentials are read from the setup.config file");
				System.out.println("You are using credentials: " + credentials);
				System.out.println("Now terminating process..");
				try {
					Thread.sleep(5000);
					System.exit(0);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					System.exit(0);

				}

			}
		}

	}

	public String getDatabaseName() {
		return db_name;
	}

	public String getDatabaseHost() {
		return db_host;
	}

	public String getDatabasePort() {
		return db_port;
	}

	public String getDatabaseUsername() {
		return db_username;
	}

	public String toString() {
		return "connected to " + db_host + ":" + db_port + " on database " + db_name + " as user " + db_username;
	}

	public String updateAllUsers(List<Member> members) {
		return null; // possible update all username feature
	}

	public static ResultSet executeQuery(String query) throws SQLException {
		return st.executeQuery(query);
	}
	
	public static void executeUpdate(String query) throws SQLException { //for actions of INSERT,UPDATE,DELETE
		st.executeUpdate(query);
	}
	public static userObj getInfractions(long uid, MessageChannel channel) throws SQLException{
		ArrayList<String> reasons = new ArrayList<String>();
		short infractPts = 0; //points in the database are smallint which is a short in java
		int infractionCount = 0;
		String query = "SELECT * FROM infractions WHERE uid = " + uid +";";
		ResultSet rs = databaseConfigurator.executeQuery(query);
		int colCount = rs.getMetaData().getColumnCount();
		String[] columnName = new String[colCount];
		int i = 0;
		while (i < colCount) {
			columnName[i] = rs.getMetaData().getColumnName(i+1); //column 1 starts at index 1
			i++;
		}
		StringBuilder columns = new StringBuilder("***Past infractions:***\n>>> ");
		while (rs.next()) {
			infractionCount++;
			i = 0;
			while (i < colCount) {
				columns.append("***" + columnName[i] + "***: " + rs.getString(i+1) + "\n");
				i++;
				if(columns.length() > 1925) { //max discord character length = 2000
					reasons.add(columns.toString());
					columns.setLength(0); //reset buffer
					columns.append("\n>>> ");
				}
			}
			infractPts +=rs.getShort(("infractionPoints"));
			columns.append("------------------------------------------\n");
		}
		reasons.add(columns.toString());
		return new userObj(infractPts,infractionCount, reasons);
	}
	
	public static void insertInto(String tableInsert,List<String> values) throws SQLException {
		StringBuilder formatter = new StringBuilder();
		for(int i=0;i<values.size();i++) { //formats it so the list of arguments is surrounded by ' ' and separated by ,
			if(i<values.size()-1)
			formatter.append("'" + values.get(i) + "',");
			else{
				formatter.append("'" + values.get(i) + "'"); 
			}
		}

		databaseConfigurator.executeUpdate("INSERT INTO " + tableInsert + " VALUES(" + formatter.toString() + ");");
		
		
	}
	public static List<String> forkValues(Timestamp date, String uid, String msg) {
		ArrayList<String> values = new ArrayList<String>();
		values.add(date.toString());
		values.add(uid);
		values.add(msg);
		return values;
			
	}
	public static List<String> actionLogValues(Timestamp date, String uid, String authorTag, String action, String uid2, String uid2Tag){
		ArrayList<String> values = new ArrayList<String>();
		values.add(date.toString());
		values.add(uid);
		values.add(authorTag);
		values.add(action);
		values.add(uid2);
		values.add(uid2Tag);
		return values;
	}
	public static void forkQuery(String msgSplice,MessageChannel channel) throws SQLException {
		String query = "SELECT message FROM fork WHERE uid = " + msgSplice +";";
		System.out.println(query);
		ResultSet rs = databaseConfigurator.executeQuery(query);
		StringBuilder messageQueue = new StringBuilder(">>> "); 
		while(rs.next()) {
			messageQueue.append(rs.getString("message") + "\n");
		}
		channel.sendMessage(messageQueue.toString()).queue();
		messageQueue.setLength(0);
		rs.close();
	}
}