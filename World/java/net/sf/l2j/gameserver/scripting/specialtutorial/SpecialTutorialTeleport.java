package net.sf.l2j.gameserver.scripting.specialtutorial;

import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.commons.logging.CLogger;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.enums.FloodProtector;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.TutorialCloseHtml;
import net.sf.l2j.gameserver.network.serverpackets.TutorialShowHtml;

/**
 * @author juven
 */
public class SpecialTutorialTeleport
{
	protected static final CLogger LOGGER = new CLogger(SpecialTutorialTeleport.class.getName());
	
	public static final void showQuestionTeleport(Player player)
	{
		
		showTutorialHtmlTeleport(player);
	}
	
	private static final void showTutorialHtmlTeleport(Player player)
	{
		if (player.getStatus().getLevel() == 77 && player.getStatus().getLevel() == 78 && player.getStatus().getLevel() == 79 && player.getStatus().getLevel() == 80)
			return;
		
		String msg = HtmCache.getInstance().getHtm("data/html/mods/SpecialTutorial/Teleport/Gatekeeper.htm");
		msg = msg.replaceAll("%name%", player.getName());
		
		final StringBuilder menu = new StringBuilder(100);
		
		// box 1
		StringUtil.append(menu, "<table width=\"280\" bgcolor=\"000000\"><tr>");
		
		StringUtil.append(menu, "<td height=40 width=40><img src=\"", Config.ICON_TELEPORT_GOTO_00, "\" width=32 height=32></td><td width=190>", Config.NAME_TELEPORT_TUTORIAL_00, "<br1><font color=\"B09878\">", Config.DESC_TELEPORT_TUTORIAL_00, "</font></td><td><button action=\"link RA", "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomout2\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>");
		
		StringUtil.append(menu, "</tr></table><img src=\"L2UI.SquareGray\" width=280 height=1>");
		
		// box 2
		StringUtil.append(menu, "<table width=\"280\"><tr>");
		
		StringUtil.append(menu, "<td height=40 width=40><img src=\"", Config.ICON_TELEPORT_GOTO_01, "\" width=32 height=32></td><td width=190>", Config.NAME_TELEPORT_TUTORIAL_01, "<br1><font color=\"B09878\">", Config.DESC_TELEPORT_TUTORIAL_01, "</font></td><td><button action=\"link CT", "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomout2\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>");
		
		StringUtil.append(menu, "</tr></table><img src=\"L2UI.SquareGray\" width=280 height=1>");
		
		// box 3
		StringUtil.append(menu, "<table width=\"280\" bgcolor=\"000000\"><tr>");
		
		StringUtil.append(menu, "<td height=40 width=40><img src=\"", Config.ICON_TELEPORT_GOTO_02, "\" width=32 height=32></td><td width=190>", Config.NAME_TELEPORT_TUTORIAL_02, "<br1><font color=\"B09878\">", Config.DESC_TELEPORT_TUTORIAL_02, "</font></td><td><button action=\"link TP", "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomout2\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>");
		
		StringUtil.append(menu, "</tr></table><img src=\"L2UI.SquareGray\" width=280 height=1>");
		
		// box 4
		StringUtil.append(menu, "<table width=\"280\"><tr>");
		
		StringUtil.append(menu, "<td height=40 width=40><img src=\"", Config.ICON_TELEPORT_GOTO_03, "\" width=32 height=32></td><td width=190>", Config.NAME_TELEPORT_TUTORIAL_03, "<br1><font color=\"B09878\">", Config.DESC_TELEPORT_TUTORIAL_03, "</font></td><td><button action=\"link HS", "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomout2\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>");
		
		StringUtil.append(menu, "</tr></table><img src=\"L2UI.SquareGray\" width=280 height=1>");
		
		msg = msg.replaceAll("%menu%", menu.toString());
		player.sendPacket(new TutorialShowHtml(msg));
		
	}
	
	public static void onTutoriaTeleportRa(Player player, String request)
	{
		
		if (request == null || !request.startsWith("RA"))
			return;
		
		if (!player.getClient().performAction(FloodProtector.SERVER_BYPASS))
			return;
		try
		{
			
			player.teleportTo(Config.SOE_TELEPORT_GOTO_00_LOCATION[0], Config.SOE_TELEPORT_GOTO_00_LOCATION[1], Config.SOE_TELEPORT_GOTO_00_LOCATION[2], 0);
			
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to load ", player.getName() + "Tutorial Erro in Teleport Staget 4" + e);
		}
		
		player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	}
	
	public static void onTutoriaTeleportCT(Player player, String request)
	{
		
		if (request == null || !request.startsWith("CT"))
			return;
		
		if (!player.getClient().performAction(FloodProtector.SERVER_BYPASS))
			return;
		try
		{
			
			player.teleportTo(Config.SOE_TELEPORT_GOTO_01_LOCATION[0], Config.SOE_TELEPORT_GOTO_01_LOCATION[1], Config.SOE_TELEPORT_GOTO_01_LOCATION[2], 0);
			
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to load ", player.getName() + "Tutorial Erro in Teleport Staget 4" + e);
		}
		
		player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	}
	
	public static void onTutoriaTeleportAL(Player player, String request)
	{
		
		if (request == null || !request.startsWith("TP"))
			return;
		
		if (!player.getClient().performAction(FloodProtector.SERVER_BYPASS))
			return;
		try
		{
			
			player.teleportTo(Config.SOE_TELEPORT_GOTO_02_LOCATION[0], Config.SOE_TELEPORT_GOTO_02_LOCATION[1], Config.SOE_TELEPORT_GOTO_02_LOCATION[2], 0);
			
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to load ", player.getName() + "Tutorial Erro in Teleport Staget 4" + e);
		}
		
		player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	}
	
	public static void onTutoriaTeleportHS(Player player, String request)
	{
		
		if (request == null || !request.startsWith("HS"))
			return;
		
		if (!player.getClient().performAction(FloodProtector.SERVER_BYPASS))
			return;
		try
		{
			
			player.teleportTo(Config.SOE_TELEPORT_GOTO_03_LOCATION[0], Config.SOE_TELEPORT_GOTO_03_LOCATION[1], Config.SOE_TELEPORT_GOTO_03_LOCATION[2], 0);
			
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to load ", player.getName() + "Tutorial Erro in Teleport Staget 4" + e);
		}
		
		player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	}
}
