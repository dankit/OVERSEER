
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant; //current time info, more versatile than offsetdatetime
import java.time.OffsetDateTime; //what JDA uses for time
import java.util.Date;  //paired with instant to make a date
import java.util.HashMap;
import java.util.List;
import net.dv8tion.jda.api.JDA;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Guild.Ban;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;

public class MyListener extends ListenerAdapter {
	String prefix = "!!";
	HashMap<Long,userObj> userMap = new HashMap<Long,userObj>();
/*
 * 
 * Please note the code is currently in the process of being refactored to better 
 * deal with each specific use case, to allow for more modularity and cleaner code
 * Todo: surround the code as a whole with try/catch, each exception is not unique to a certain command
 *       e.g. numberformatexception is invalid id, arrayoutofbounds is because of invalid arguments
 */
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		Instant startTime = Instant.now();
		Timestamp ts = new Timestamp(startTime.toEpochMilli());
		MessageChannel channel = event.getChannel();
		Guild guild = event.getGuild();
		JDA jda = guild.getJDA();
		String msgContent = event.getMessage().getContentRaw().replaceAll("'", "''");
		String authorLong = event.getAuthor().getId();
		
		try {
			if(!event.getAuthor().isBot()) {
			databaseConfigurator.insertInto(databaseConfigurator.FORKINSERT,databaseConfigurator.forkValues(ts, authorLong, msgContent));}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		if (event.getAuthor().getId().equals("245111504863494145") && msgContent.startsWith(prefix) && !event.getAuthor().isBot()) { // !event.getAuthor().isBot()
			// &&
			// We don't want to respond to other bot accounts, including ourself
			if (prefix.length() > 1) {
				msgContent = msgContent.substring(prefix.length());
			} else {
				// prefix of length 1
				msgContent = msgContent.substring(1);
			}
			String msgFixed = msgContent;
			String[] msgSplice = msgContent.split(" ");
			switch (msgSplice[0]) {
			case "test":
				channel.sendMessage("Bot is up and running!").queue();
				break;
			case "selfDelete":
					
				break;
			// -------------------------------------------------------BANS-------------------------------------------------------------------------\\
			// -----------------------------------------------------banIgnoreCase--------------------------------------------------------------------------\\
			case "banIgnoreCase": {
				int counter = 0;
				if (msgSplice.length > 1) { // checking if the command has an argument is quicker than manipulating the
											// string
					String username = msgFixed.replace("banIgnoreCase ", "");
					List<User> bannable = jda.getUsersByName(username, true);
					for (User users : bannable) {
						guild.ban(users, 7).queue();
						counter++;
					}
					channel.sendMessage(counter + "/" + bannable.size()
							+ " members have been banned with case ignored name '" + username + "'\n" + queryTimer(startTime)).queue();
				} else {
					channel.sendMessage("Invalid argument list provided, format is '[prefix]banIgnoreCase <String>'")       
							.queue();
				}
				break;
			}
			// ---------------------------------------------------------banMatchCase-----------------------------------------------------------------------\\
			case "banMatchCase": {
				if (msgSplice.length > 1) {
					String username = msgFixed.replace("banMatchCase ", "");
					int counter = 0;
					List<User> bannable = jda.getUsersByName(username, false);
					for (User users : bannable) {
						guild.ban(users, 7).queue();
						counter++;
					}
					channel.sendMessage(counter + "/" + bannable.size()
							+ " members have been banned with name case matching: '" + username + "'\n" + queryTimer(startTime)).queue();
				} else {
					channel.sendMessage("Invalid argument list provided, format is '[prefix]banMatchCase <String>'")
							.queue();
				}
				break;
			}
			// -----------------------------------------------------banContains-----------------------------------------------------------------------------\\
			case "banContains": {
				if (msgSplice.length > 1) {
					int counter = 0;
					String username = msgFixed.replace("banContains ", "");
					if (username.length() >= 5) {
						List<User> members = jda.getUsers();
						for (User member : members) {
							if (member.getName().contains(username)) {
								guild.ban(member, 7).queue();
								counter++;
							}
						}

						channel.sendMessage(
								counter + " members have been banned where name contains '" + username + "'\n" + queryTimer(startTime)).queue();
					} else if (username.length() < 5) {
						channel.sendMessage("Username must be greater than 5 characters to prevent accidents!").queue();
						;
					}
				} else {
					channel.sendMessage("Invalid argument list provided, format is '[prefix]banContains <String>'")
							.queue();
				}
				break;
			}
			// -------------------------------------------------BAN---------------------------------------------------------------------------------\\
			case "ban": {
				try {
					User user = jda.getUserById(msgSplice[1]);

					if (user != null) {
						try {
							guild.ban(user, 7).queue();
							channel.sendMessage(user.getAsTag() + " has been banned").queue();
						} catch (InsufficientPermissionException | HierarchyException ex) {
							channel.sendMessage("You either do not have the permission to ban, or are lower in rank!")
									.queue();

						}

					} else {
						channel.sendMessage("User not found").queue();

					}
				} catch (NumberFormatException ex) {
					channel.sendMessage("Invalid user id, make sure you copy user id, NOT the tag or message id!")
							.queue();
				} catch (ArrayIndexOutOfBoundsException ex) {
					channel.sendMessage("Invalid argument list provided, format '[prefix]ban <userid>'").queue();
				}
				break;
			}

			// -------------------------------------------WHOIS---------------------------------------------------------------------\\
			case "whois":
				if (msgSplice.length > 1) {
					try {

						Member member = guild.getMemberById(msgSplice[1]);
						channel.sendMessage(">>> Tag: " + jda.getUserById(msgSplice[1]).getAsTag() + "\nStatus: "
								+ member.getOnlineStatus() + "\nServer Join Date: "
								+ convertToDate(member.getTimeJoined()) + " \nAccount creation: "
								+ convertToDate(member.getTimeCreated()) + "\n" + queryTimer(startTime)).queue();
					} catch (NullPointerException ex) {
						channel.sendMessage("Error, user not found. User might not be in the server or ID is invalid")
								.queue();
					} catch (NumberFormatException ex) {
						channel.sendMessage(
								"ID value of type Long expected, please make sure you're inputting a valid user ID!")
								.queue();
					}
				} else {
					channel.sendMessage("Invalid argument list provided, format '[prefix]whois <userid>'").queue();
				}
				break;
			// -----------------------------------------------------setPrefix-------------------------------------------------------------------------\\
			case "setPrefix":
				prefix = msgFixed.replace("setPrefix ", "");
				channel.sendMessage("Prefix successfully changed to " + prefix +"\n"+queryTimer(startTime)).queue();
				// store somewhere to get later

				break;
			// -----------------------------------------------------Contact-------------------------------------------------------------------------\\
			case "contact":
				channel.sendMessage("Add dank#0149 on discord for more information\n" +queryTimer(startTime) ).queue();
				break;
				
			case "unbanAll":
				unbanAll(guild.retrieveBanList());
				
				
				break;
			// -----------------------------------------------------Infractions-------------------------------------------------------------------------\\
			case "getMessages":
				try {
					databaseConfigurator.forkQuery(msgSplice[1], channel);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;
			case "infract":
				userObj userData;
				if (msgSplice.length > 1 && (msgSplice[1].length() == 18 || msgSplice[1].length() == 17)) {//17&18 = length of user id
					try {
						long uid = Long.parseLong(msgSplice[1]); // sanitize input
						if(userMap.containsKey(uid)) {
							userData = userMap.get(uid);
							for(String reason:userData.getReasons()) {
								channel.sendMessage(reason);
							}
						}
						else {
							userData = databaseConfigurator.getInfractions(uid, channel); // only sets infraction points, infraction count, and reasons
							userData.setTag(jda.getUserById(uid).getAsTag());
							userData.setDateJoined(convertToDate(guild.getMemberById(uid).getTimeJoined()));
							for(String reason:userData.getReasons()) {
								channel.sendMessage(reason).queue();
							}
							userMap.put(uid, userData);
						//	databaseConfigurator.executeQuery("INSERT INTO infractions(uid,name,reason,date,issuedBy,infractionPoints) VALUES"
								//	+ "("+uid+"," + )
							
						}
						
						//add member to db as well if not already in there	
						//offer to infract user with reactions, need to be able to get msg just sent id then add reacts corresponding to punishment
						
								
					} catch (SQLException e) {
						
						e.printStackTrace();
					} catch (NumberFormatException e) {
						channel.sendMessage("Invaid ID constructed").queue();
					}

				}
				else {
					channel.sendMessage("Invalid argument list provided, format '[prefix]infract <uid>(length=18)'").queue();
				}

			}

		} 	

	}
	

public static Date convertToDate(OffsetDateTime time){
	return new Date(time.toInstant().toEpochMilli());
}

public static String queryTimer(Instant startTime) {
	return String.format("Query executed in %d Milliseconds", Duration.between(startTime, Instant.now()).toMillis());
}

public void unbanAll(RestAction<List<Ban>> bannedUsers) {
	/*bannedUsers.queue(bans -> 
	{for (Guild.Ban a : bans) { 
		if(!a.getReason().contains(arg0))});
	}*/
}


}

