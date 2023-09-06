package net.sf.l2j.gameserver.scripting.specialtutorial;

import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.commons.logging.CLogger;

import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.ai.Bot;
import net.sf.l2j.gameserver.network.serverpackets.TutorialShowHtml;

/**
 * @author juven
 */
public class SpecialTutorialAutoFarm
{
	protected static final CLogger LOGGER = new CLogger(SpecialTutorialAutoFarm.class.getName());
	
	public static void showQuestionHtml(Player player)
	{
		
		showTutorialHtml(player);
	}
	
	public static void showTutorialHtml(Player player)
	{
		
		Bot bot = player.getBot();
		String msg = HtmCache.getInstance().getHtm("data/html/farm-dashboard.htm");
		msg = msg.replaceAll("%name%", player.getName());
		
		msg = msg.replaceAll("%state%", bot.isActive() ? "<font color=00FF00>Active</font>" : "<font color=FF0000>Inactive</font>");
		
		msg = msg.replaceAll("%lowlife%", String.valueOf(bot.getLowLifePercentage()));
		msg = msg.replaceAll("%chance%", String.valueOf(bot.getChancePercentage()));
		msg = msg.replaceAll("%hpPotion%", String.valueOf(bot.getHpPotionPercentage() == 0 ? "<font color=FF0000>OFF" : "<font color=FA8072>" + bot.getHpPotionPercentage()));
		msg = msg.replaceAll("%mpPotion%", String.valueOf(bot.getMpPotionPercentage() == 0 ? "<font color=FF0000>OFF" : "<font color=3399CC>" + bot.getMpPotionPercentage()));
		msg = msg.replaceAll("%radius%", StringUtil.formatNumber(bot.getRadius()));
		
		msg = msg.replaceAll("%button%", (bot.isActive() ? "value=\"Stop\" action=\"link farm_off\"" : "value=\"Start\" action=\"link farm_on\""));
		msg = msg.replaceAll("%radius%", StringUtil.formatNumber(bot.getRadius()));
		
		player.sendPacket(new TutorialShowHtml(msg));
		
	}
	
}
