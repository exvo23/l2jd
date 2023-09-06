package net.sf.l2j.gameserver.communitybbs.manager.custom;

import java.util.List;
import java.util.StringTokenizer;

import net.sf.l2j.commons.lang.StringUtil;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.communitybbs.manager.BaseBBSManager;
import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.data.sql.PlayerInfoTable;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.data.xml.PlayerData;
import net.sf.l2j.gameserver.enums.actors.ClassId;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.holder.IntIntHolder;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.PlaySound;
import net.sf.l2j.gameserver.network.serverpackets.UserInfo;
import net.sf.l2j.gameserver.taskmanager.HeroTaskManager;
import net.sf.l2j.gameserver.taskmanager.PremiumTaskManager;

/**
 * @author BAN L2JDEV
 */
public class AccontsCBManager extends BaseBBSManager
{
	@Override
	public void parseCmd(String command, Player player)
	{
		if (!Config.ALLOW_CLASS_MASTERS)
			return;
		
		if (command.startsWith("_cb1stClass"))
			showHtmlMenu(player, player.getObjectId(), 1);
		else if (command.startsWith("_cb2ndClass"))
			showHtmlMenu(player, player.getObjectId(), 2);
		else if (command.startsWith("_cb3rdClass"))
			showHtmlMenu(player, player.getObjectId(), 3);
		
		else if (command.startsWith("_cbbecome_noble"))
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			
			if (!player.isNoble())
			{
				player.setNoble(true, true);
				player.sendPacket(new UserInfo(player));
				html.setFile("data/html/CommunityBoard/custom/VillaMaster/nobleok.htm");
				player.sendPacket(html);
			}
			else
			{
				html.setFile("data/html/CommunityBoard/custom/VillaMaster/alreadynoble.htm");
				player.sendPacket(html);
			}
		}
		else if (command.startsWith("_cblearn_skills"))
			player.rewardSkills();
		
		else if (command.startsWith("_cbNameChange"))
		{
			
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			
			try
			{
				ItemInstance itemInstance = player.getInventory().getItemByItemId(Config.ItemIdChangeName);
				final String name = st.nextToken();
				
				// Invalid pattern.
				if (!StringUtil.isValidString(name, "^[A-Za-z0-9]{1,16}$"))
				{
					player.sendPacket(SystemMessageId.INCORRECT_NAME_TRY_AGAIN);
					return;
				}
				// Name already exists.
				if (PlayerInfoTable.getInstance().getPlayerObjectId(name) > 0)
				{
					player.sendPacket(SystemMessageId.INCORRECT_NAME_TRY_AGAIN);
					return;
				}
				
				if (itemInstance == null || !itemInstance.isStackable() && player.getInventory().getItemCount(Config.ItemIdChangeName, -1) < (Config.PrinceChangeName))
				{
					player.sendMessage("You not enough " + Config.ConsumeNameChange + ".");
					player.sendMessage(player.getName() + " " + "Change Name Prince " + Config.PrinceChangeName + " " + Config.ConsumeNameChange + ".");
					return;
				}
				else if (itemInstance.isStackable())
				{
					if (!player.destroyItemByItemId("Adena", Config.ItemIdChangeName, Config.PrinceChangeName, player.getTarget(), true))
					{
						player.sendMessage("You not enough " + Config.ConsumeNameChange + ".");
						player.sendMessage(player.getName() + " " + "Change Name Prince " + Config.PrinceChangeName + " " + Config.ConsumeNameChange + ".");
						return;
					}
				}
				player.setName(name);
				PlayerInfoTable.getInstance().updatePlayerData(player, false);
				player.broadcastUserInfo();
				player.store();
				player.sendPacket(new ExShowScreenMessage("Your name has been changed." + name, 6000));
				player.sendPacket(new PlaySound("ItemSound.quest_finish"));
			}
			
			catch (Exception e2)
			{
				player.sendMessage("Fill out the field correctly.");
			}
			
		}
		
		else if (command.startsWith("_cbEpi"))
		{
			showIndex(player);
			
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			String action = st.nextToken();
			
			if (action.equals("vip30days"))
			{
				ItemInstance itemInstance = player.getInventory().getItemByItemId(Config.PremiumConsumeItemId);
				if (itemInstance == null || !itemInstance.isStackable() && player.getInventory().getItemCount(Config.PremiumConsumeItemId, -1) < (Config.PrinceBuyPremium30))
				{
					player.sendMessage("You not enough " + Config.ConsumeName + ".");
					player.sendMessage(player.getName() + " " + "30 Days Premium from " + Config.PrinceBuyPremium30 + " " + Config.ConsumeName + ".");
					return;
				}
				else if (itemInstance.isStackable())
				{
					if (!player.destroyItemByItemId("Adena", Config.PremiumConsumeItemId, Config.PrinceBuyPremium30, player.getTarget(), true))
					{
						player.sendMessage("You not enough " + Config.ConsumeName + ".");
						player.sendMessage(player.getName() + " " + "30 Days Premium from " + Config.PrinceBuyPremium30 + " " + Config.ConsumeName + ".");
						return;
					}
				}
				PremiumTaskManager.ApllyPremium(player, player, 30);
			}
			
			else if (action.equals("vip90days"))
			{
				ItemInstance itemInstance = player.getInventory().getItemByItemId(Config.PremiumConsumeItemId);
				if (itemInstance == null || !itemInstance.isStackable() && player.getInventory().getItemCount(Config.PremiumConsumeItemId, -1) < (Config.PrinceBuyPremium90))
				{
					player.sendMessage("You not enough " + Config.ConsumeName + ".");
					player.sendMessage(player.getName() + " " + "90 Days Premium from " + Config.PrinceBuyPremium90 + " " + Config.ConsumeName + ".");
					return;
				}
				else if (itemInstance.isStackable())
				{
					if (!player.destroyItemByItemId("Adena", Config.PremiumConsumeItemId, Config.PrinceBuyPremium90, player.getTarget(), true))
					{
						player.sendMessage("You not enough " + Config.ConsumeName + ".");
						player.sendMessage(player.getName() + " " + "90 Days Premium from " + Config.PrinceBuyPremium90 + " " + Config.ConsumeName + ".");
						return;
					}
				}
				PremiumTaskManager.ApllyPremium(player, player, 90);
				
			}
			if (action.equals("hero30days"))
			{
				ItemInstance itemInstance = player.getInventory().getItemByItemId(Config.PremiumConsumeItemId);
				if (itemInstance == null || !itemInstance.isStackable() && player.getInventory().getItemCount(Config.PremiumConsumeItemId, -1) < (Config.PrinceBuyHero30))
				{
					player.sendMessage("You not enough " + Config.ConsumeName + ".");
					player.sendMessage(player.getName() + " " + "30 Days Hero from " + Config.PrinceBuyHero30 + " " + Config.ConsumeName + ".");
					return;
				}
				else if (itemInstance.isStackable())
				{
					
					if (!player.destroyItemByItemId("Adena", Config.PremiumConsumeItemId, Config.PrinceBuyHero30, player.getTarget(), true))
					{
						player.sendMessage("You not enough " + Config.ConsumeName + ".");
						player.sendMessage(player.getName() + " " + "30 Days Hero from " + Config.PrinceBuyHero30 + " " + Config.ConsumeName + ".");
						return;
					}
				}
				HeroTaskManager.ApllyHero(player, 30);
			}
			if (action.equals("hero90days"))
			{
				ItemInstance itemInstance = player.getInventory().getItemByItemId(Config.PremiumConsumeItemId);
				if (itemInstance == null || !itemInstance.isStackable() && player.getInventory().getItemCount(Config.PremiumConsumeItemId, -1) < (Config.PrinceBuyHero90))
				{
					player.sendMessage("You not enough " + Config.ConsumeName + ".");
					player.sendMessage(player.getName() + " " + "90 Days Hero from " + Config.PrinceBuyHero90 + " " + Config.ConsumeName + ".");
					return;
				}
				else if (itemInstance.isStackable())
				{
					if (!player.destroyItemByItemId("Adena", Config.PremiumConsumeItemId, Config.PrinceBuyHero90, player.getTarget(), true))
					{
						player.sendMessage("You not enough " + Config.ConsumeName + ".");
						player.sendMessage(player.getName() + " " + "90 Days Hero from " + Config.PrinceBuyHero90 + " " + Config.ConsumeName + ".");
						return;
					}
				}
				HeroTaskManager.ApllyHero(player, 90);
			}
			
		}
		
	}
	
	/**
	 * @param level - current skillId level (0 - start, 1 - first, etc)
	 * @return minimum player level required for next class transfer
	 */
	private static final int getMinLevel(int level)
	{
		switch (level)
		{
			case 0:
				return 20;
			case 1:
				return 40;
			case 2:
				return 76;
			default:
				return Integer.MAX_VALUE;
		}
	}
	
	/**
	 * Returns true if class change is possible
	 * @param oldCID current player ClassId
	 * @param newCID new ClassId
	 * @return true if class change is possible
	 */
	private static final boolean validateClassId(ClassId oldCID, ClassId newCID)
	{
		if (newCID == null)
			return false;
		
		if (oldCID == newCID.getParent())
			return true;
		
		if (Config.ALLOW_ENTIRE_TREE && newCID.isChildOf(oldCID))
			return true;
		
		return false;
	}
	
	private static final void showHtmlMenu(Player player, int objectId, int level)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(objectId);
		
		if (!Config.CLASS_MASTER_SETTINGS.isAllowed(level))
		{
			final StringBuilder sb = new StringBuilder(100);
			sb.append("<html><body>");
			
			switch (player.getClassId().getLevel())
			{
				case 0:
					if (Config.CLASS_MASTER_SETTINGS.isAllowed(1))
						sb.append("Come back here when you reached level 20 to change your class.<br>");
					else if (Config.CLASS_MASTER_SETTINGS.isAllowed(2))
						sb.append("Come back after your first occupation change.<br>");
					else if (Config.CLASS_MASTER_SETTINGS.isAllowed(3))
						sb.append("Come back after your second occupation change.<br>");
					else
						sb.append("I can't change your occupation.<br>");
					break;
				
				case 1:
					if (Config.CLASS_MASTER_SETTINGS.isAllowed(2))
						sb.append("Come back here when you reached level 40 to change your class.<br>");
					else if (Config.CLASS_MASTER_SETTINGS.isAllowed(3))
						sb.append("Come back after your second occupation change.<br>");
					else
						sb.append("I can't change your occupation.<br>");
					break;
				
				case 2:
					if (Config.CLASS_MASTER_SETTINGS.isAllowed(3))
						sb.append("Come back here when you reached level 76 to change your class.<br>");
					else
						sb.append("I can't change your occupation.<br>");
					break;
				
				case 3:
					sb.append("There is no class change available for you anymore.<br>");
					break;
			}
			sb.append("</body></html>");
			html.setHtml(sb.toString());
		}
		else
		{
			final ClassId currentClassId = player.getClassId();
			if (currentClassId.getLevel() >= level)
				html.setFile("data/html/CommunityBoard/custom/VillaMaster/nomore.htm");
			else
			{
				final int minLevel = getMinLevel(currentClassId.getLevel());
				if (player.getStatus().getLevel() >= minLevel || Config.ALLOW_ENTIRE_TREE)
				{
					final StringBuilder menu = new StringBuilder(100);
					for (ClassId cid : ClassId.VALUES)
					{
						if (cid.getLevel() != level)
							continue;
						
						if (validateClassId(currentClassId, cid))
							StringUtil.append(menu, "<a action=\"bypass voiced_change_class ", cid.getId(), "\">", PlayerData.getInstance().getClassNameById(cid.getId()), "</a><br>");
					}
					
					if (menu.length() > 0)
					{
						html.setFile("data/html/CommunityBoard/custom/VillaMaster/template.htm");
						html.replace("%name%", PlayerData.getInstance().getClassNameById(currentClassId.getId()));
						html.replace("%menu%", menu.toString());
					}
					else
					{
						html.setFile("data/html/CommunityBoard/custom/VillaMaster/comebacklater.htm");
						html.replace("%level%", getMinLevel(level - 1));
					}
				}
				else
				{
					if (minLevel < Integer.MAX_VALUE)
					{
						html.setFile("data/html/CommunityBoard/custom/VillaMaster/comebacklater.htm");
						html.replace("%level%", minLevel);
					}
					else
						html.setFile("data/html/CommunityBoard/custom/VillaMaster/nomore.htm");
				}
			}
		}
		
		html.replace("%objectId%", objectId);
		html.replace("%req_items%", getRequiredItems(level));
		player.sendPacket(html);
	}
	
	private static String getRequiredItems(int level)
	{
		final List<IntIntHolder> neededItems = Config.CLASS_MASTER_SETTINGS.getRequiredItems(level);
		if (neededItems == null || neededItems.isEmpty())
			return "<tr><td>none</td></r>";
		
		final StringBuilder sb = new StringBuilder();
		for (IntIntHolder item : neededItems)
			StringUtil.append(sb, "<tr><td><font color=\"LEVEL\">", item.getValue(), "</font></td><td>", ItemData.getInstance().getTemplate(item.getId()).getName(), "</td></tr>");
		
		return sb.toString();
	}
	
	private static void showIndex(Player activeChar)
	{
		String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/Acconts.htm");
		
		separateAndSend(content, activeChar);
	}
	protected AccontsCBManager()
	{
	}
	
	public static AccontsCBManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final AccontsCBManager INSTANCE = new AccontsCBManager();
	}
}
