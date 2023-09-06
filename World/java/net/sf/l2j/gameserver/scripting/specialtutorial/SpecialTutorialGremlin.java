package net.sf.l2j.gameserver.scripting.specialtutorial;

import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.commons.logging.CLogger;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.enums.FloodProtector;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.CameraMode;
import net.sf.l2j.gameserver.network.serverpackets.NormalCamera;
import net.sf.l2j.gameserver.network.serverpackets.TutorialCloseHtml;
import net.sf.l2j.gameserver.network.serverpackets.TutorialShowHtml;
import net.sf.l2j.gameserver.skills.L2Skill;

/**
 * @author juven
 */
public class SpecialTutorialGremlin
{
	protected static final CLogger LOGGER = new CLogger(SpecialTutorialGremlin.class.getName());
	
	private static final String _fighterSet = Config.TUTORIAL_BUFFER_FIGHTER_SET;
	private static final String _mageSet = Config.TUTORIAL_BUFFER_MAGE_SET;
	
	public static final void showQuestionBuffer(Player player)
	{
		
		showTutorialHtmlBuffer(player);
	}
	
	private static final void showTutorialHtmlBuffer(Player player)
	{

		if (player.getMemos().getBool("startEndTime"))
		return;
		
		
		player.sendPacket(new CameraMode(0));
		player.sendPacket(NormalCamera.STATIC_PACKET);
		
		String msg = HtmCache.getInstance().getHtm("data/html/mods/SpecialTutorial/Buffer/ByfferStyle.htm");
		msg = msg.replaceAll("%name%", player.getName());
		
		final StringBuilder menu = new StringBuilder(100);
		
		StringUtil.append(menu, "<table width=\"280\" bgcolor=\"000000\"><tr>");
		
		StringUtil.append(menu, "<td height=40 width=40><img src=\"", Config.ICON_BUFFER_FIGHT_00, "\" width=32 height=32></td><td width=190>", Config.NAME_BUFFER_TUTORIAL_00, "<br1><font color=\"B09878\">", Config.DESC_BUFFER_TUTORIAL_00, "</font></td><td><button action=\"link BU", "1" + "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomout2\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>");
		
		StringUtil.append(menu, "</tr></table><img src=\"L2UI.SquareGray\" width=280 height=1><br>");
		
		StringUtil.append(menu, "<table width=\"280\" bgcolor=\"000000\"><tr>");
		
		StringUtil.append(menu, "<td height=40 width=40><img src=\"", Config.ICON_BUFFER_MAGIC_01, "\" width=32 height=32></td><td width=190>", Config.NAME_BUFFER_TUTORIAL_01, "<br1><font color=\"B09878\">", Config.DESC_BUFFER_TUTORIAL_01, "</font></td><td><button action=\"link BU", "2" + "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomout2\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>");
		
		StringUtil.append(menu, "</tr></table><img src=\"L2UI.SquareGray\" width=280 height=1>");
		
		msg = msg.replaceAll("%menu%", menu.toString());
		player.sendPacket(new TutorialShowHtml(msg));
		
	}
	
	public static final void onTutorialBufferLink(Player player, String request)
	{
		
		if (request == null || !request.startsWith("BU"))
			return;
		
		if (!player.getClient().performAction(FloodProtector.SERVER_BYPASS))
			return;
		try
		{
			int val = Integer.parseInt(request.substring(2));
			String[] set_splitted = val == 1 ? _fighterSet.split(";") : _mageSet.split(";");
			for (String s : set_splitted)
			{
				int id = Integer.parseInt(s);
				int lvl = SkillTable.getInstance().getMaxLevel(id);
				
				L2Skill sk = SkillTable.getInstance().getInfo(id, lvl);
				if (sk == null)
				{
					System.out.println("Error on buffer bypass: Wrong skill id " + id + ".");
					continue;
				}
				
				if (player.getBuff() == 0)
					sk.getEffects(player, player);
				else
				{
					if (player.getSummon() != null)
						sk.getEffects(player.getSummon(), player.getSummon());
				}
				
				
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to load schemes data.", e);
		}

		player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
		OpenHtml(player);
	
	}
	
	public static void OpenHtml(Player player)
	{
		SpecialTutorialArmor.showQuestionArmor(player);
	}
}
