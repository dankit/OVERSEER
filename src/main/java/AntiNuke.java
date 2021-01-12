import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdateSlowmodeEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateRegionEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateVerificationLevelEvent;
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateNameEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdatePermissionsEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class AntiNuke extends ListenerAdapter {
	/*
	 * https://ci.dv8tion.net/job/JDA/javadoc/net/dv8tion/jda/api/events/GenericEvent.html
	 *read about each specific one here
	 * 
	 * onTextChannelCreate​(TextChannelCreateEvent event)
	 */
	
	
	//idea -> make !banMessageSent <String>
	//pulls uids for all members who have sent a message of <String> in the past 5-10 minutes (using date functions)
	//then ban all the members
	public void onTextChannelDelete​(TextChannelDeleteEvent event) {

	}

	public void onTextChannelCreate​(TextChannelCreateEvent event) {

	}

	public void onRoleCreate​(RoleCreateEvent event) {

	}

	public void onRoleDelete​(RoleDeleteEvent event) {
//event.getGuild().retrieveAuditLogs().type(ActionType.BAN).queue
	}

	public void onRoleUpdatePermissions​(RoleUpdatePermissionsEvent event) {

	}

	public void onGuildBan​(GuildBanEvent event) {

	}

	public void onGuildUnban​(GuildUnbanEvent event) {
		// make sure to account for mass unban feature
	}


	public void onGuildMemberRemove​(GuildMemberRemoveEvent event) { //works for kicks,bans,and leaves

	}

	public void onGuildMessageDelete​(GuildMessageDeleteEvent event) {

	}

	public void onGuildUpdateName​(GuildUpdateNameEvent event) {

	}

	public void GenericPermissionOverrideEvent​(PermissionOverride event) {

	}

	public void onTextChannelUpdateSlowmode​(TextChannelUpdateSlowmodeEvent event) {

	}

	public void onRoleUpdateName​(RoleUpdateNameEvent event) {

	}

	public void onMessageUpdate​(MessageUpdateEvent event) {

	}

	public void onMessageBulkDelete​(MessageBulkDeleteEvent event) {

	}

	public void onGuildUpdateRegion​(GuildUpdateRegionEvent event) {

	}

	public void onGuildUpdateVerificationLevel​(GuildUpdateVerificationLevelEvent event) {

	}

}
