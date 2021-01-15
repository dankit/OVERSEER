import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
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
	@Override
	public void onTextChannelDelete(TextChannelDeleteEvent event) {
		event.getGuild().retrieveAuditLogs().type(ActionType.CHANNEL_DELETE).limit(1).queue(
				x -> {
					if (x.isEmpty()) return;
					AuditLogEntry entry = x.get(0);
					event.getGuild().getTextChannelById("785300961396654120").sendMessage(String.format("Channel %s deleted by user %s (%s)",event.getChannel().getName(), entry.getUser().getAsTag(),entry.getUser().getId())).queue();
				}
				);
	}
	@Override
	public void onTextChannelCreate(TextChannelCreateEvent event){
		event.getGuild().retrieveAuditLogs().type(ActionType.CHANNEL_CREATE).limit(1).queue(
				x -> {
					if (x.isEmpty()) return;
					AuditLogEntry entry = x.get(0);
					event.getGuild().getTextChannelById("785300961396654120").sendMessage(String.format("Channel %s created by user %s (%s)",event.getChannel().getName(), entry.getUser().getAsTag(),entry.getUser().getId())).queue();
				}
				);
	}
	@Override
	public void onRoleCreate(RoleCreateEvent event) {
		event.getGuild().retrieveAuditLogs().type(ActionType.ROLE_CREATE).limit(1).queue(
				x -> {
					if (x.isEmpty()) return;
					AuditLogEntry entry = x.get(0);
					event.getGuild().getTextChannelById("785300961396654120").sendMessage(String.format("Role %s created by user %s (%s)",event.getRole().getName(), entry.getUser().getAsTag(),entry.getUser().getId())).queue();
				}
				);
	}
	
	@Override
	public void onRoleDelete(RoleDeleteEvent event)  {
		event.getGuild().retrieveAuditLogs().type(ActionType.ROLE_DELETE).limit(1).queue(
				x -> {
					if (x.isEmpty()) return;
					AuditLogEntry entry = x.get(0);
					event.getGuild().getTextChannelById("785300961396654120").sendMessage(String.format("Role %s deleted by user %s (%s)",event.getRole().getName(), entry.getUser().getAsTag(),entry.getUser().getId())).queue();
				}
				);
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
