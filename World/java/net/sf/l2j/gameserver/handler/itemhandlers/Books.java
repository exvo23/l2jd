package net.sf.l2j.gameserver.handler.itemhandlers;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.sf.l2j.gameserver.enums.FloodProtector;
import net.sf.l2j.gameserver.events.l2jdev.Event;
import net.sf.l2j.gameserver.events.l2jdev.EventManager;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.DungeonManagerNpc;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import javolution.text.TextBuilder;

public class Books implements IItemHandler
{
	private static final String ACTIVED = "<font color=00FF00>ON</font>";
	private static final String DESATIVED = "<font color=FF0000>OFF</font>";
	
	
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		final Player player = (Player) playable;
		
		if (!player.getClient().performAction(FloodProtector.DRESSME))
		{
			player.sendPacket(new ExShowScreenMessage(player.getName() + " You must wait 5 seconds for the next use.", (int) TimeUnit.SECONDS.toMillis(2)));
			return;
		}
		showMenuHtml(player);
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	public static void showCPanel(Player activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		
		TextBuilder sb = new TextBuilder();

		sb.append("<html><title>Event Engine - Vote for your favourite</title><body>");
		
		sb.append("<img src=L2UI.SquareWhite width=295 height=1><img src=L2UI.SquareWhite width=295 height=1><img src=L2UI.SquareWhite width=295 height=1>");
		sb.append("<table width=270><tr><td width=145><br1>Events Engine</td><td width=75>" + (EventManager.getInstance().getBoolean("eventBufferEnabled") ? "<button value=\"Buffer\" action=\"bypass -h eventbuffershow \"width=65 height=20 back=\"L2UI_ch3.smallbutton2_down\" fore=\"L2UI_ch3.smallbutton2\">" : "") + "</td><td width=50><button value=\"Statis\" action=\"bypass -h eventstats 1 \"width=65 height=20 back=\"L2UI_ch3.smallbutton2_down\" fore=\"L2UI_ch3.smallbutton2\"></td></tr></table>");
		
		sb.append("<img src=L2UI.SquareWhite width=295 height=1><img src=L2UI.SquareWhite width=295 height=1><img src=L2UI.SquareWhite width=295 height=1>");
		
		sb.append("<center><table width=256 bgcolor=000000><tr><td width=90><font color=\"FFFF00\">Voting Phase</font></td><td width=140><center><font color=\"00FF00\">Remaining Time:</font> " + EventManager.getInstance().cdtask.getTime() + "</center></td><td width=40><center><font color=\"FFFF00\">Votes</font></center></td></tr></table></center><br></center>");
	
		for (Map.Entry<Integer, Event> event : EventManager.getInstance().events.entrySet())
		{
			sb.append("<table align=center width=300 height=20 bgcolor=000000>");
			sb.append("<tr>");
			
			sb.append("<td align=\"left\" width=\"140\"><font color=\"FFFFFF\">" + event.getValue().getString("eventName") + "</font>" + "</td>");
			sb.append("<td align=\"center\" width=\"80\"><button value=\"Info\" action=\"bypass -h eventinfo " + event.getKey() + "\"width=65 height=20 back=\"L2UI_ch3.smallbutton2_down\" fore=\"L2UI_ch3.smallbutton2\"></td>");
			sb.append("<td align=\"center\" width=\"80\"><button value=\"Vote " + EventManager.getInstance().getVoteCount(event.getKey()) + "\" action=\"bypass -h eventvote " + event.getKey() + "\"width=65 height=20 back=\"L2UI_ch3.smallbutton2_down\" fore=\"L2UI_ch3.smallbutton2\"></td>");
			
			sb.append("</tr>");
			sb.append("</table></center><img src=L2UI.SquareWhite width=295 height=1><img src=L2UI.SquareWhite width=295 height=1><img src=L2UI.SquareWhite width=295 height=1>");
			
		}

		sb.append("<br1></body></html>");
		
		html.setHtml(sb.toString());
		
		activeChar.sendPacket(html);
		
	}
	
	public static void showMenuHtml(Player activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/menu/Menu.htm");
		html.replace("%bloking%", activeChar.isDisableHair() ? DESATIVED : ACTIVED);
		html.replace("%dresmedisable%", activeChar.isSkinDisable() ? ACTIVED : DESATIVED);
		html.replace("%name%", activeChar.getName());
		html.replace("%dungstat1%", DungeonManagerNpc.getPlayerStatus(activeChar, 1));
		html.replace("%dungstat2%", DungeonManagerNpc.getPlayerStatus(activeChar, 2));
		html.replace("%online%", World.getInstance().getPlayers().size());
		html.replace("%autogb%", activeChar.isAutoGb() ? ACTIVED : DESATIVED);
		activeChar.sendPacket(html);
	}
}