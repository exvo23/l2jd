package net.sf.l2j.gameserver.scripting.specialtutorial;

import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.commons.logging.CLogger;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.enums.FloodProtector;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.network.serverpackets.TutorialCloseHtml;
import net.sf.l2j.gameserver.network.serverpackets.TutorialShowHtml;

/**
 * @author BAN - L2JDEV
 */
public class SpecialTutorialArmor
{
	protected static final CLogger LOGGER = new CLogger(SpecialTutorialArmor.class.getName());
	
	public static void showQuestionArmor(Player player)
	{
		if (player.getMemos().getBool("startEndTimeArmor"))
		{
			OpenHtml(player);
			return;
		}

		showTutorialHtmlArmor(player);
	}
	
	public static void showTutorialHtmlArmor(Player player)
	{

		player.sendPacket(new SocialAction(player, 11));
		
		String msg = HtmCache.getInstance().getHtm("data/html/mods/SpecialTutorial/Armor/ArmorSet.htm");
		msg = msg.replaceAll("%name%", player.getName());
		
		final StringBuilder menu = new StringBuilder(100);
		// box 1
		StringUtil.append(menu, "<table width=\"280\" bgcolor=\"000000\"><tr>");
		
		StringUtil.append(menu, "<td height=40 width=40><img src=\"", Config.ICON_ARMOR_LIGHT_00, "\" width=32 height=32></td><td width=190>", Config.NAME_ARMOR_TUTORIAL_00, "<br1><font color=\"B09878\">", Config.DESC_ARMOR_TUTORIAL_00, "</font></td><td><button action=\"link LH", "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomout2\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>");
		
		StringUtil.append(menu, "</tr></table><img src=\"L2UI.SquareGray\" width=280 height=1><br>");
		
		// box 2
		StringUtil.append(menu, "<table width=\"280\"><tr>");
		
		StringUtil.append(menu, "<td height=40 width=40><img src=\"", Config.ICON_ARMOR_HEAVY_01, "\" width=32 height=32></td><td width=190>", Config.NAME_ARMOR_TUTORIAL_01, "<br1><font color=\"B09878\">", Config.DESC_ARMOR_TUTORIAL_01, "</font></td><td><button action=\"link HE", "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomout2\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>");
		
		StringUtil.append(menu, "</tr></table><img src=\"L2UI.SquareGray\" width=280 height=1>");
		
		// box 3
		StringUtil.append(menu, "<table width=\"280\" bgcolor=\"000000\"><tr>");
		
		StringUtil.append(menu, "<td height=40 width=40><img src=\"", Config.ICON_ARMOR_ROBE_02, "\" width=32 height=32></td><td width=190>", Config.NAME_ARMOR_TUTORIAL_02, "<br1><font color=\"B09878\">", Config.DESC_ARMOR_TUTORIAL_02, "</font></td><td><button action=\"link RO", "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomout2\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>");
		
		StringUtil.append(menu, "</tr></table><img src=\"L2UI.SquareGray\" width=280 height=1><br>");
		
		msg = msg.replaceAll("%menu%", menu.toString());
		player.sendPacket(new TutorialShowHtml(msg));
		
	}
	
	
	public static void onTutorialArmorSetLight(Player player, String request)
	{
		if (request == null || !request.startsWith("LH"))
			return;
		
		if (!player.getClient().performAction(FloodProtector.SERVER_BYPASS))
			return;
		try
		{
			
			for (int[] item : Config.SET_LIGHT_ITEMS)
			{
				player.addItem("", item[0], item[1], player, true);
				
				ItemInstance PhewPew1 = player.getInventory().getItemByItemId(item[0]);
				player.useEquippableItem(PhewPew1, true);
				PhewPew1.setEnchantLevel(Rnd.get(Config.MIN_ENCHANT_TUTORIAL, Config.MAX_ENCHANT_TUTORIAL));
			}
			
			MagicSkillUse mgc = new MagicSkillUse(player, player, 2024, 1, 5, 0);
			player.sendPacket(mgc);
			player.broadcastPacket(mgc);
			player.broadcastUserInfo();

			player.getMemos().set("startEndTime", true);
			player.getInventory().reloadEquippedItems();
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to load ", player.getName() + "Tutorial Erro in Armor Staget 2" + e);
		}
		
		player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
		OpenHtml(player);
	}
	
	
	public static void onTutorialArmorSetRobe(Player player, String request)
	{
		
		if (request == null || !request.startsWith("RO"))
			return;
		
		if (!player.getClient().performAction(FloodProtector.SERVER_BYPASS))
			return;
		try
		{
			
			for (int[] item : Config.SET_ROBE_ITEMS)
			{
				player.addItem("", item[0], item[1], player, true);
				
				ItemInstance PhewPew1 = player.getInventory().getItemByItemId(item[0]);
				player.useEquippableItem(PhewPew1, true);
				PhewPew1.setEnchantLevel(Rnd.get(Config.MIN_ENCHANT_TUTORIAL, Config.MAX_ENCHANT_TUTORIAL));
			}
			
			MagicSkillUse mgc = new MagicSkillUse(player, player, 2024, 1, 5, 0);
			player.sendPacket(mgc);
			player.broadcastPacket(mgc);
			player.broadcastUserInfo();
			player.getMemos().set("startEndTimeArmor", true);
			player.getInventory().reloadEquippedItems();
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to load ", player.getName() + "Tutorial Erro in Armor Staget 2" + e);
		}
		
		player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
		OpenHtml(player);
	}
	
	
	public static void onTutorialArmorSetHeavy(Player player, String request)
	{
		
		if (request == null || !request.startsWith("HE"))
			return;
		
		if (!player.getClient().performAction(FloodProtector.SERVER_BYPASS))
			return;
		try
		{
			
			for (int[] item : Config.SET_HEAVY_ITEMS)
			{
				player.addItem("", item[0], item[1], player, true);
				
				ItemInstance PhewPew1 = player.getInventory().getItemByItemId(item[0]);
				player.useEquippableItem(PhewPew1, true);
				PhewPew1.setEnchantLevel(Rnd.get(Config.MIN_ENCHANT_TUTORIAL, Config.MAX_ENCHANT_TUTORIAL));
			}
			
			MagicSkillUse mgc = new MagicSkillUse(player, player, 2024, 1, 5, 0);
			player.sendPacket(mgc);
			player.broadcastPacket(mgc);
			player.broadcastUserInfo();
			player.getMemos().set("startEndTimeArmor", true);
			player.getInventory().reloadEquippedItems();
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to load ", player.getName() + "Tutorial Erro in Armor Staget 2" + e);
		}
		
		player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
		OpenHtml(player);
	}
	
	public static void OpenHtml(Player player)
	{
		SpecialTutorialWeapon.showQuestionWeapon(player);
	}
}
