
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant; //current time info, more versatile than offsetdatetime
import java.time.OffsetDateTime; //what JDA uses for time
import java.util.Date; //paired with instant to make a date
import java.util.HashMap;
import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Guild.Ban;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;

public class MyListener extends ListenerAdapter {
	String prefix = "!!";
	HashMap<Long, userObj> userMap = new HashMap<Long, userObj>();

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		Instant startTime = Instant.now();
		Timestamp ts = new Timestamp(startTime.toEpochMilli());
		MessageChannel channel = event.getChannel();
		Guild guild = event.getGuild();
		String msgContent = event.getMessage().getContentRaw().replaceAll("'", "''");
		String authorLong = event.getAuthor().getId();

		try {
			if (!event.getAuthor().isBot()) {
				databaseConfigurator.insertInto(databaseConfigurator.FORK,
						databaseConfigurator.forkValues(ts, authorLong, msgContent));
			}

			if (event.getAuthor().getId().equals("245111504863494145") && msgContent.startsWith(prefix)
					&& !event.getAuthor().isBot()) {
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
				
				// ---------------------------------------------------------banMatchCase-----------------------------------------------------------------------\\
				case "banMatchCase": {
					if (msgSplice.length > 2) {
						String username = msgFixed.replace("banMatchCase ", "");
						int counter = 0;
						boolean matchCase = Boolean.valueOf(msgSplice[1]);
						List<Member> bannable = guild.getMembersByName(username, matchCase);
						for (Member members : bannable) {
							guild.ban(members, 7).queue();
							counter++;
						}
						channel.sendMessage(
								counter + "/" + bannable.size() + " members have been banned with name case matching: '"
										+ username + "'\n" + queryTimer(startTime))
								.queue();
					} else {
						channel.sendMessage("Invalid argument list provided, format is '[prefix]banMatchCase <true/false> <string>'")
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
							List<Member> members = guild.getMembers();
							for (Member member : members) {
								if (member.getUser().getName().contains(username)) {
									guild.ban(member, 7).queue();
									counter++;
								}
							}

							channel.sendMessage(counter + " members have been banned where name contains '" + username
									+ "'\n" + queryTimer(startTime)).queue();
						} else if (username.length() < 5) {
							channel.sendMessage("Username must be greater than 5 characters to prevent accidents!")
									.queue();
							;
						}
					} else {
						channel.sendMessage("Invalid argument list provided, format is '[prefix]banContains <String>'")
								.queue();
					}
					break;
				}
				// -------------------------------------------------BAN---------------------------------------------------------------------------------\\
				case "ban":
					try {
						Member member = guild.getMemberById(msgSplice[1]);

						if (member != null) {

							guild.ban(member, 7).queue();
							channel.sendMessage(member.getUser().getAsTag() + " has been banned").queue();

						} else {
							channel.sendMessage("Member not found").queue();

						}
					} catch (InsufficientPermissionException | HierarchyException ex) {
						channel.sendMessage("You either do not have the permission to ban, or are lower in rank!")
								.queue();
					}

					break;

				// -------------------------------------------WHOIS---------------------------------------------------------------------\\
				case "whois":
					if (msgSplice.length > 1) {
						try {
							Member member = guild.getMemberById(msgSplice[1]);
							channel.sendMessage(">>> Tag: " + member.getUser().getAsTag() + " "
									+ member.getAsMention() + "\nServer Join Date: "
									+ convertToDate(member.getTimeJoined()) + " \nAccount creation: "
									+ convertToDate(member.getTimeCreated()) + "\n" + queryTimer(startTime)).queue();
						} catch (NullPointerException ex) {
							channel.sendMessage(
									"Error, user not found. User might not be in the server or ID is invalid").queue();
						}
					} else {
						channel.sendMessage("Invalid argument list provided, format '[prefix]whois <userid>'").queue();
					}
					break;
				// -----------------------------------------------------setPrefix-------------------------------------------------------------------------\\
				case "setPrefix":
					prefix = msgFixed.replace("setPrefix ", "");
					// store somewhere to get later

					break;
				// -----------------------------------------------------Contact-------------------------------------------------------------------------\\
				case "contact":
					channel.sendMessage("Add dank#0149 on discord for more information\n" + queryTimer(startTime))
							.queue();
					break;

				case "unbanAll":

					RestAction<List<Ban>> bans = guild.retrieveBanList();
					bans.queue((ban) -> ban.forEach(user -> {
						if (user != null && user.getReason() != null) {
							guild.unban(user.getUser()).queue();
							try {
								databaseConfigurator.insertInto(databaseConfigurator.ACTIONLOG,
										databaseConfigurator.actionLogValues(ts, authorLong,
												event.getAuthor().getAsTag(),
												"Mass unban " + ts.toLocalDateTime().toString(), user.getUser().getId(),
												user.getUser().getAsTag().replace("'", "")));
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}));

					break;
				// -----------------------------------------------------Infractions-------------------------------------------------------------------------\\
				case "getMessages":
					databaseConfigurator.forkQuery(msgSplice[1], channel);
					break;
				case "infract":
					userObj userData;
					if (msgSplice.length > 1 && (msgSplice[1].length() == 18 || msgSplice[1].length() == 17)) {// 17&18 = length of uid
																											
						long uid = Long.parseLong(msgSplice[1]); // sanitize input
						if (userMap.containsKey(uid)) {
							userData = userMap.get(uid);
							for (String reason : userData.getReasons()) {
								channel.sendMessage(reason);
							}
						} else {
							userData = databaseConfigurator.getInfractions(uid, channel); // only sets infraction
																							// points, infraction count,
																							// and reasons
							userData.setTag(guild.getMemberById(uid).getUser().getAsTag());
							userData.setDateJoined(convertToDate(guild.getMemberById(uid).getTimeJoined()));
							for (String reason : userData.getReasons()) {
								channel.sendMessage(reason).queue();
							}
							userMap.put(uid, userData);
							// databaseConfigurator.executeQuery("INSERT INTO
							// infractions(uid,name,reason,date,issuedBy,infractionPoints) VALUES"
							// + "("+uid+"," + )

						}

						// add member to db as well if not already in there
						// offer to infract user with reactions, need to be able to get msg just sent id
						// then add reacts corresponding to punishment

					} else {
						channel.sendMessage(
								"Invalid argument list provided, format '[prefix]infract <uid>(length=17/18)'").queue();
					}

				}

			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (NumberFormatException e) {
			channel.sendMessage("Invalid ID constructed").queue();
		} catch (ArrayIndexOutOfBoundsException ex) {
			channel.sendMessage("Invalid argument list provided, format '[prefix]ban <userid>'").queue();
		}

	}

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		if (event.getUser().getId().equals("omitted")) {
			event.getGuild().getTextChannelById("785300961396654120")
					.sendMessage("<@245111504863494145> watchlisted user has joined the guild").queue();

		}
	}
	

	public static Date convertToDate(OffsetDateTime time) {
		return new Date(time.toInstant().toEpochMilli());
	}

	public static String queryTimer(Instant startTime) {
		return String.format("Query executed in %d Milliseconds",
				Duration.between(startTime, Instant.now()).toMillis());
	}

}
