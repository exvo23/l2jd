package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.gameserver.data.sql.PlayerVariables;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author BAN - L2JDev
 */
public class ColorManager extends Folk
{
	public ColorManager(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player activeChar, String command)
	{
		if (command.startsWith("premiumchangenamecolor"))
		{
			
			String[] args = command.substring("premiumchangenamecolor".length() + 1).split(" ");
			
			int color = Integer.decode("0x" + args[0]);
			
			PlayerVariables.setVar(activeChar, "lastColorName", color, -1);
			activeChar.getAppearance().setNameColor(color);
			activeChar.setRecomHave(0);
			activeChar.store();
			activeChar.broadcastUserInfo();
			
			activeChar.sendMessage("Name Color Changed");
			showChatWindow(activeChar, 0);
		}
		else if (command.startsWith("premiumchangetitlecolor"))
		{
			
			String[] args = command.substring("premiumchangetitlecolor".length() + 1).split(" ");
			
			int color = Integer.decode("0x" + args[0]);
			
			PlayerVariables.setVar(activeChar, "lastColorTitle", color, -1);
			activeChar.getAppearance().setTitleColor(color);
			activeChar.store();
			activeChar.broadcastUserInfo();
			activeChar.sendMessage("Title Color Changed");
			showChatWindow(activeChar, 0);
		}
		else
			super.onBypassFeedback(activeChar, command);
	}
	
	@Override
	public void showChatWindow(Player activeChar, int val)
	{
		NpcHtmlMessage htm = new NpcHtmlMessage(getObjectId());
		htm.setFile("data/html/mods/ColorManager/" + getNpcId() + (val == 0 ? "" : "-" + val) + ".htm");
		htm.replace("%name%", activeChar.getName());
		
		if (activeChar.getTitle() != null)
		{
			htm.replace("%title%", activeChar.getTitle());
			
		}
		else
		{
			activeChar.setTitle("Title Color");
			htm.replace("%title%", activeChar.getTitle());
			
		}
		htm.replace("%objectId%", getObjectId());
		activeChar.sendPacket(htm);
	}
	
	public static void EnterWorld(Player player)
	{
		if (PlayerVariables.getVarB(player, "lastColorName"))
			player.getAppearance().setNameColor(PlayerVariables.getVarInt(player, "lastColorName"));
		
		if (PlayerVariables.getVarB(player, "lastColorTitle"))
			player.getAppearance().setTitleColor(PlayerVariables.getVarInt(player, "lastColorTitle"));
		
	}
}
