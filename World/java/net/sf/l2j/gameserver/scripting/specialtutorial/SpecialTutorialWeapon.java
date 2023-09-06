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
import net.sf.l2j.gameserver.network.serverpackets.PlaySound;
import net.sf.l2j.gameserver.network.serverpackets.TutorialCloseHtml;
import net.sf.l2j.gameserver.network.serverpackets.TutorialShowHtml;

/**
 * @author juven
 */
public class SpecialTutorialWeapon
{
	protected static final CLogger LOGGER = new CLogger(SpecialTutorialWeapon.class.getName());
	
	public static final void showQuestionWeapon(Player player)
	{
		
		showTutorialHtmlWeapon(player);
	}
	
	private static final void showTutorialHtmlWeapon(Player player)
	{
		String msg = HtmCache.getInstance().getHtm("data/html/mods/SpecialTutorial/Weapon/WeaponStyle.htm");
		msg = msg.replaceAll("%name%", player.getName());
		
		final StringBuilder menu = new StringBuilder(100);
		
		// box 1
		StringUtil.append(menu, "<table width=\"280\" bgcolor=\"000000\"><tr>");
		
		StringUtil.append(menu, "<td height=40 width=40><img src=\"", Config.ICON_WEAPON_00, "\" width=32 height=32></td><td width=190>", Config.NAME_WEAPON_TUTORIAL_00, "<br1><font color=\"B09878\">", Config.DESC_WEAPON_TUTORIAL_00, "</font></td><td><button action=\"link BO", "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomout2\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>");
		
		StringUtil.append(menu, "</tr></table><img src=\"L2UI.SquareGray\" width=280 height=1><br>");
		
		// box 2
		StringUtil.append(menu, "<table width=\"280\" bgcolor=\"000000\"><tr>");
		
		StringUtil.append(menu, "<td height=40 width=40><img src=\"", Config.ICON_WEAPON_01, "\" width=32 height=32></td><td width=190>", Config.NAME_WEAPON_TUTORIAL_01, "<br1><font color=\"B09878\">", Config.DESC_WEAPON_TUTORIAL_01, "</font></td><td><button action=\"link SO", "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomout2\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>");
		
		StringUtil.append(menu, "</tr></table><img src=\"L2UI.SquareGray\" width=280 height=1>");
		
		// box 3
		StringUtil.append(menu, "<table width=\"280\" bgcolor=\"000000\"><tr>");
		
		StringUtil.append(menu, "<td height=40 width=40><img src=\"", Config.ICON_WEAPON_02, "\" width=32 height=32></td><td width=190>", Config.NAME_WEAPON_TUTORIAL_02, "<br1><font color=\"B09878\">", Config.DESC_WEAPON_TUTORIAL_02, "</font></td><td><button action=\"link DA", "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomout2\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>");
		
		StringUtil.append(menu, "</tr></table><img src=\"L2UI.SquareGray\" width=280 height=1><br>");
		
		// box 4
		StringUtil.append(menu, "<table width=\"280\" bgcolor=\"000000\"><tr>");
		
		StringUtil.append(menu, "<td height=40 width=40><img src=\"", Config.ICON_WEAPON_03, "\" width=32 height=32></td><td width=190>", Config.NAME_WEAPON_TUTORIAL_03, "<br1><font color=\"B09878\">", Config.DESC_WEAPON_TUTORIAL_03, "</font></td><td><button action=\"link SF", "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomout2\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>");
		
		StringUtil.append(menu, "</tr></table><img src=\"L2UI.SquareGray\" width=280 height=1><br>");
		
		msg = msg.replaceAll("%menu%", menu.toString());
		player.sendPacket(new TutorialShowHtml(msg));
		
	}
	
	public static void onTutorialWeaponBow(Player player, String request)
	{
		
		if (request == null || !request.startsWith("BO"))
			return;
		
		if (!player.getClient().performAction(FloodProtector.SERVER_BYPASS))
			return;
		try
		{
			
			player.addItem("", Config.ITEMID_WEAPON_TUTORIAL_00, 1, player, true);
			
			ItemInstance PhewPew1 = player.getInventory().getItemByItemId(Config.ITEMID_WEAPON_TUTORIAL_00);
			player.useEquippableItem(PhewPew1, true);
			PhewPew1.setEnchantLevel(Rnd.get(Config.MIN_ENCHANT_TUTORIAL, Config.MAX_ENCHANT_TUTORIAL));
			player.addItem("Mana Potion", Config.ITEMID_ETC_TUTORIAL_00, Config.CONT_ETC_TUTORIAL_00, player, false);
			player.addItem("Greater Healing Potion", Config.ITEMID_ETC_TUTORIAL_01, Config.CONT_ETC_TUTORIAL_01, player, false);
			player.addItem("Scroll of Scape", Config.ITEMID_ETC_TUTORIAL_02, Config.CONT_ETC_TUTORIAL_02, player, false);
			player.addItem("Arrow", Config.ITEMID_ETC_TUTORIAL_05, Config.CONT_ETC_TUTORIAL_05, player, false);
			
			player.addItem("SS", Config.ITEMID_ETC_TUTORIAL_03, Config.CONT_ETC_TUTORIAL_03, player, false);
			
			MagicSkillUse mgc = new MagicSkillUse(player, player, 2024, 1, 5, 0);
			player.sendPacket(mgc);
			player.broadcastPacket(mgc);
			player.broadcastUserInfo();
			player.setNewChar(false);
			player.getMemos().set("startEndTime", true);
			player.getInventory().reloadEquippedItems();
			
			player.sendPacket(new PlaySound(2, "tutorial_voice_013", player));
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to load ", player.getName() + "Tutorial Erro in Weapon Staget 3" + e);
		}
		
		player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
		SpecialTutorialTeleport.showQuestionTeleport(player);
	}
	
	public static void onTutorialWeaponSword(Player player, String request)
	{
		
		if (request == null || !request.startsWith("SO"))
			return;
		
		if (!player.getClient().performAction(FloodProtector.SERVER_BYPASS))
			return;
		try
		{
			
			player.addItem("", Config.ITEMID_WEAPON_TUTORIAL_01, 1, player, true);
			ItemInstance PhewPew1 = player.getInventory().getItemByItemId(Config.ITEMID_WEAPON_TUTORIAL_01);
			player.useEquippableItem(PhewPew1, true);

			
			PhewPew1.setEnchantLevel(Rnd.get(Config.MIN_ENCHANT_TUTORIAL, Config.MAX_ENCHANT_TUTORIAL));
			player.addItem("Mana Potion", Config.ITEMID_ETC_TUTORIAL_00, Config.CONT_ETC_TUTORIAL_00, player, false);
			player.addItem("Greater Healing Potion", Config.ITEMID_ETC_TUTORIAL_01, Config.CONT_ETC_TUTORIAL_01, player, false);
			player.addItem("Scroll of Scape", Config.ITEMID_ETC_TUTORIAL_02, Config.CONT_ETC_TUTORIAL_02, player, false);		
			player.addItem("SS", Config.ITEMID_ETC_TUTORIAL_03, Config.CONT_ETC_TUTORIAL_03, player, false);

			
			MagicSkillUse mgc = new MagicSkillUse(player, player, 2024, 1, 5, 0);
			player.sendPacket(mgc);
			player.broadcastPacket(mgc);
			player.broadcastUserInfo();
			player.setNewChar(false);
			player.getMemos().set("startEndTime", true);
			player.getInventory().reloadEquippedItems();
			player.sendPacket(new PlaySound(2, "tutorial_voice_013", player));
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to load ", player.getName() + "Tutorial Erro in Weapon Staget 2" + e);
		}
		
		player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
		SpecialTutorialTeleport.showQuestionTeleport(player);
	}
	
	public static void onTutorialWeaponDagger(Player player, String request)
	{
		
		if (request == null || !request.startsWith("DA"))
			return;
		
		if (!player.getClient().performAction(FloodProtector.SERVER_BYPASS))
			return;
		try
		{
			
			player.addItem("", Config.ITEMID_WEAPON_TUTORIAL_02, 1, player, true);
			
			ItemInstance PhewPew1 = player.getInventory().getItemByItemId(Config.ITEMID_WEAPON_TUTORIAL_02);
			player.useEquippableItem(PhewPew1, true);
			PhewPew1.setEnchantLevel(Rnd.get(Config.MIN_ENCHANT_TUTORIAL, Config.MAX_ENCHANT_TUTORIAL));
			
			player.addItem("Mana Potion", Config.ITEMID_ETC_TUTORIAL_00, Config.CONT_ETC_TUTORIAL_00, player, false);
			player.addItem("Greater Healing Potion", Config.ITEMID_ETC_TUTORIAL_01, Config.CONT_ETC_TUTORIAL_01, player, false);
			player.addItem("Scroll of Scape", Config.ITEMID_ETC_TUTORIAL_02, Config.CONT_ETC_TUTORIAL_02, player, false);
			player.addItem("SS", Config.ITEMID_ETC_TUTORIAL_03, Config.CONT_ETC_TUTORIAL_03, player, false);

			
			MagicSkillUse mgc = new MagicSkillUse(player, player, 2024, 1, 5, 0);
			player.sendPacket(mgc);
			player.broadcastPacket(mgc);
			player.broadcastUserInfo();
			player.setNewChar(false);
			player.getMemos().set("startEndTime", true);
			player.getInventory().reloadEquippedItems();
			player.sendPacket(new PlaySound(2, "tutorial_voice_013", player));
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to load ", player.getName() + "Tutorial Erro in Weapon Staget 3" + e);
		}
		
		player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
		SpecialTutorialTeleport.showQuestionTeleport(player);
	}
	
	public static void onTutorialWeaponStaff(Player player, String request)
	{
		
		if (request == null || !request.startsWith("SF"))
			return;
		
		if (!player.getClient().performAction(FloodProtector.SERVER_BYPASS))
			return;
		try
		{
			
			player.addItem("", Config.ITEMID_WEAPON_TUTORIAL_03, 1, player, true);
			
			ItemInstance PhewPew1 = player.getInventory().getItemByItemId(Config.ITEMID_WEAPON_TUTORIAL_03);
			player.useEquippableItem(PhewPew1, true);
			PhewPew1.setEnchantLevel(Rnd.get(Config.MIN_ENCHANT_TUTORIAL, Config.MAX_ENCHANT_TUTORIAL));
			player.addItem("Mana Potion", Config.ITEMID_ETC_TUTORIAL_00, Config.CONT_ETC_TUTORIAL_00, player, false);
			player.addItem("Greater Healing Potion", Config.ITEMID_ETC_TUTORIAL_01, Config.CONT_ETC_TUTORIAL_01, player, false);
			player.addItem("Scroll of Scape", Config.ITEMID_ETC_TUTORIAL_02, Config.CONT_ETC_TUTORIAL_02, player, false);

			player.addItem("SS", Config.ITEMID_ETC_TUTORIAL_04, Config.CONT_ETC_TUTORIAL_04, player, false);

			MagicSkillUse mgc = new MagicSkillUse(player, player, 2024, 1, 5, 0);
			player.sendPacket(mgc);
			player.broadcastPacket(mgc);
			player.broadcastUserInfo();
			player.setNewChar(false);
			player.getMemos().set("startEndTime", true);
			player.getInventory().reloadEquippedItems();
			player.sendPacket(new PlaySound(2, "tutorial_voice_013", player));
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to load ", player.getName() + "Tutorial Erro in Weapon Staget 3" + e);
		}
		
		player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
		SpecialTutorialTeleport.showQuestionTeleport(player);
	}

}
