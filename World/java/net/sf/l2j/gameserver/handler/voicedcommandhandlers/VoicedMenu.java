package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.handler.bypasscommand.CommandAutoFarm;
import net.sf.l2j.gameserver.handler.bypasscommand.CommandRaidInfo;
import net.sf.l2j.gameserver.handler.itemhandlers.Books;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author BAN - JDEV
 */
public class VoicedMenu implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"menu",
		"events",
		"event",
		"autofarm",
		"raid",
		"raidboss",
		"dressme",
		"glamour",
		"ranking",
		"castle",
		"castles"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (command.equals("menu"))
			Books.showMenuHtml(activeChar);
		
		if (command.equals("events"))
			Books.showCPanel(activeChar);
		if (command.equals("event"))
			Books.showCPanel(activeChar);
		
		if (command.equals("autofarm"))
			CommandAutoFarm.dashboard(activeChar);
		
		if (command.equals("raidboss"))
			CommandRaidInfo.showChatWindow(activeChar, 1);
		if (command.equals("raid"))
			CommandRaidInfo.showChatWindow(activeChar, 1);
		
		if (command.equals("dressme"))
			showSkinHtml(activeChar);
		if (command.equals("glamour"))
			showSkinHtml(activeChar);
		
		if (command.equals("ranking"))
			showRanking(activeChar);
		
		if (command.equals("castle"))
			showcastle(activeChar);
		if (command.equals("castles"))
			showcastle(activeChar);
		
		return true;
	}
	
	private static final String ACTIVED = "<font color=00FF00>ON</font>";
	private static final String DESATIVED = "<font color=FF0000>OFF</font>";
	
	public static void showSkinHtml(Player activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/dressme/index.htm");
		html.replace("%bloking%", activeChar.isDisableHair() ? DESATIVED : ACTIVED);
		html.replace("%autogb%", activeChar.isAutoGb() ? ACTIVED : DESATIVED);
		html.replace("%dresmedisable%", activeChar.isSkinDisable() ? ACTIVED : DESATIVED);
		html.replace("%name%", activeChar.getName());
		html.replace("%online%", World.getInstance().getPlayers().size());
		activeChar.sendPacket(html);
	}
	
	public static void showcastle(Player activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/menu/Castle.htm");
		html.replace("%name%", activeChar.getName());
		html.replace("%online%", World.getInstance().getPlayers().size());
		activeChar.sendPacket(html);
	}
	
	public static void showRanking(Player activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/menu/ranking.htm");
		html.replace("%bloking%", activeChar.isDisableHair() ? DESATIVED : ACTIVED);
		html.replace("%autogb%", activeChar.isAutoGb() ? ACTIVED : DESATIVED);
		html.replace("%dresmedisable%", activeChar.isSkinDisable() ? ACTIVED : DESATIVED);
		html.replace("%name%", activeChar.getName());
		html.replace("%online%", World.getInstance().getPlayers().size());
		activeChar.sendPacket(html);
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
