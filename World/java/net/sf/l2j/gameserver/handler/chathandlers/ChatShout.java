package net.sf.l2j.gameserver.handler.chathandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.enums.FloodProtector;
import net.sf.l2j.gameserver.enums.SayType;
import net.sf.l2j.gameserver.handler.IChatHandler;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;

public class ChatShout implements IChatHandler
{
	private static final SayType[] COMMAND_IDS =
	{
		SayType.SHOUT
	};
	
	@Override
	public void handleChat(SayType type, Player player, String target, String text)
	{
		if (!player.getClient().performAction(FloodProtector.GLOBAL_CHAT))
			return;
		
		if (Config.ALLOW_PVP_CHAT)
		{
			if ((player.getPvpKills() < Config.PVPS_TO_TALK_ON_SHOUT) && !player.isGM())
			{
				player.sendMessage("You must have at least " + Config.PVPS_TO_TALK_ON_SHOUT + " pvp kills in order to speak in global chat.");
				return;
			}
		}
		World.ChatShout(player, type, text);
	}
	
	@Override
	public SayType[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}