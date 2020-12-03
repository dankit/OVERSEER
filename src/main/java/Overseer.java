
import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;


public class Overseer {

	public static void main(String[] args) {
     databaseConfigurator database = new databaseConfigurator();
     System.out.println("Successfully " + database.toString());
     try {
    	 JDA jda = JDABuilder.createDefault("insert token here")
    	            .addEventListeners(new MyListener()).build();
		
	//Guild enigma = api.getGuildById("589685961508651038");
	//List<Member> memberList = enigma.getMembers();
		//database.updateAllUsers(memberList);
		//System.out.println("checking " + memberList.size() + " users");
		//add a check where it only checks members after the last updated member, use .getDateJoined or whatever
		
	
	} catch (LoginException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
     
}
}
