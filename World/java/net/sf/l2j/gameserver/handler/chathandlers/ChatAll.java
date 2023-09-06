package net.sf.l2j.gameserver.handler.chathandlers;

import java.util.StringTokenizer;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.enums.FloodProtector;
import net.sf.l2j.gameserver.enums.SayType;
import net.sf.l2j.gameserver.handler.IChatHandler;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.handler.VoicedCommandHandler;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

public class ChatAll implements IChatHandler
{
	private static final SayType[] COMMAND_IDS =
	{
		SayType.ALL
	};
	
	@Override
	public void handleChat(SayType type, Player player, String target, String text)
	{
		if (!player.getClient().performAction(FloodProtector.GLOBAL_CHAT))
			return;
		
		boolean vcd_used = false;
		if (text.startsWith("."))
		{
			StringTokenizer st = new StringTokenizer(text);
			IVoicedCommandHandler vch;
			String command = "";
			if (st.countTokens() > 1)
			{
				command = st.nextToken().substring(1);
				target = text.substring(command.length() + 2);
				vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler(command);
			}
			else
			{
				command = text.substring(1);
				vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler(command);
			}
			
			if (vch != null)
			{
				vch.useVoicedCommand(command, player, text);
				vcd_used = true;
				
			}
		}
		
		if (!vcd_used)
		{
			World.ChatAll(player, type, text);
			
			if (Config.EMOTION_CHAT_SYSTEM && Config.EMOTION_CHAT_LIST.get(text) != null && !player.isAlikeDead() && !player.getAttack().isAttackingNow() && !player.getCast().isCastingNow())
				player.broadcastPacket(new SocialAction(player, Config.EMOTION_CHAT_LIST.get(text)));
		}
	}
	
	@Override
	public SayType[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}