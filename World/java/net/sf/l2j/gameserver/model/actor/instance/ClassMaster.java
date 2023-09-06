/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver.model.actor.instance;

import java.util.List;

import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.data.xml.PlayerData;
import net.sf.l2j.gameserver.enums.FloodProtector;
import net.sf.l2j.gameserver.enums.actors.ClassId;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.holder.IntIntHolder;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.HennaInfo;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.TutorialCloseHtml;
import net.sf.l2j.gameserver.network.serverpackets.TutorialShowHtml;
import net.sf.l2j.gameserver.network.serverpackets.UserInfo;

/**
 * Custom class allowing you to choose your class.<br>
 * <br>
 * You can customize class rewards as needed items. Check npc.properties for more informations.<br>
 * This NPC type got 2 differents ways to level:
 * <ul>
 * <li>the normal one, where you have to be at least of the good level.<br>
 * NOTE : you have to take 1st class then 2nd, if you try to take 2nd directly it won't work.</li>
 * <li>the "allow_entire_tree" version, where you can take class depending of your current path.<br>
 * NOTE : you don't need to be of the good level.</li>
 * </ul>
 * Added to the "change class" function, this NPC can noblesse and give available skills (related to your current class and level).
 */
public final class ClassMaster extends Npc
{
	public ClassMaster(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void showChatWindow(Player player)
	{
		final ClassId currentClassId = player.getClassId();
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
		String filename = "data/html/classmaster/disabled.htm";
		
		if (Config.ALLOW_CLASS_MASTERS)
			filename = "data/html/classmaster/" + getNpcId() + ".htm";
		
		// Send a Server->Client NpcHtmlMessage containing the text of the L2Npc to the Player
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", getObjectId());
		html.replace("%name%", PlayerData.getInstance().getClassNameById(currentClassId.getId()));
		player.sendPacket(html);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (!Config.ALLOW_CLASS_MASTERS)
			return;
		
		if (command.startsWith("1stClass"))
			showHtmlMenu(player, getObjectId(), 1);
		else if (command.startsWith("2ndClass"))
			showHtmlMenu(player, getObjectId(), 2);
		else if (command.startsWith("3rdClass"))
			showHtmlMenu(player, getObjectId(), 3);
		else if (command.startsWith("change_class"))
		{
			int val = Integer.parseInt(command.substring(13));
			
			if (checkAndChangeClass(player, val))
			{
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile("data/html/classmaster/ok.htm");
				html.replace("%name%", PlayerData.getInstance().getClassNameById(val));
				player.sendPacket(html);
			}
		}
		else if (command.startsWith("become_noble"))
		{
			NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			
			if (!player.isNoble())
			{
				player.setNoble(true, true);
				player.sendPacket(new UserInfo(player));
				html.setFile("data/html/classmaster/nobleok.htm");
				player.sendPacket(html);
			}
			else
			{
				html.setFile("data/html/classmaster/alreadynoble.htm");
				player.sendPacket(html);
			}
		}
		else if (command.startsWith("learn_skills"))
		{
			player.rewardSkills();
			player.sendSkillList();
		}
		else
			super.onBypassFeedback(player, command);
	}
	
	public static final void onTutorialLink(Player player, String request)
	{
		if (!Config.ALTERNATE_CLASS_MASTER || request == null || !request.startsWith("CO"))
			return;
		
		if (!player.getClient().performAction(FloodProtector.SERVER_BYPASS))
			return;
		try
		{
			int val = Integer.parseInt(request.substring(2));
			checkAndChangeClass(player, val);
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to load Class Master Tutorial Flood data.", e);
		}
		
		player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
		OpenHtml(player);
		
	}
	
	public static final void onTutorialQuestionMark(Player player, int number)
	{
		if (!Config.ALTERNATE_CLASS_MASTER || number != 1001)
			return;
		
		showTutorialHtml(player);
	}
	
	public static final void showQuestionMark(Player player)
	{
		
		if (!Config.ALTERNATE_CLASS_MASTER)
			return;
		
		final ClassId classId = player.getClassId();
		if (getMinLevel(classId.getLevel()) > player.getStatus().getLevel())
			return;
		
		if (!Config.CLASS_MASTER_SETTINGS.isAllowed(classId.getLevel() + 1))
		{
			return;
		}
		
		showTutorialHtml(player);
		
	}
	
	private static final void showHtmlMenu(Player player, int objectId, int level)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(objectId);
		
		if (!Config.CLASS_MASTER_SETTINGS.isAllowed(level))
		{
			int jobLevel = player.getClassId().getLevel();
			final StringBuilder sb = new StringBuilder(100);
			sb.append("<html><body>");
			switch (jobLevel)
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
				html.setFile("data/html/classmaster/nomore.htm");
			else
			{
				final int minLevel = getMinLevel(currentClassId.getLevel());
				if (player.getStatus().getLevel() >= minLevel || Config.ALLOW_ENTIRE_TREE)
				{
					int row = 0;
					final StringBuilder menu = new StringBuilder(100);
					for (ClassId cid : ClassId.VALUES)
					{
						if (cid.getLevel() != level)
							continue;
						
						if (validateClassId(currentClassId, cid))
						{
							
							StringUtil.append(menu, ((row % 2) == 0 ? "<table width=\"280\" bgcolor=\"000000\"><tr>" : "<table width=\"280\"><tr>"));
							
							StringUtil.append(menu, "<td height=40 width=40><img src=\"", icon(), "\" width=32 height=32></td><td width=190>", PlayerData.getInstance().getClassNameById(cid.getId()), "<br1><font color=\"B09878\">", "Class Master", "</font></td><td><button action=\"bypass -h npc_%objectId%_change_class " + String.valueOf(cid.getId()), "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomout2\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>");
							
							StringUtil.append(menu, "</tr></table><img src=\"L2UI.SquareGray\" width=280 height=1>");
							
							row++;
						}
					}
					
					if (menu.length() > 0)
					{
						html.setFile("data/html/classmaster/template.htm");
						html.replace("%name%", PlayerData.getInstance().getClassNameById(currentClassId.getId()));
						html.replace("%menu%", menu.toString());
					}
					else
					{
						html.setFile("data/html/classmaster/comebacklater.htm");
						html.replace("%level%", getMinLevel(level - 1));
					}
				}
				else
				{
					if (minLevel < Integer.MAX_VALUE)
					{
						html.setFile("data/html/classmaster/comebacklater.htm");
						html.replace("%level%", minLevel);
					}
					else
						html.setFile("data/html/classmaster/nomore.htm");
				}
			}
		}
		
		html.replace("%objectId%", objectId);
		html.replace("%req_items%", getRequiredItems(level));
		player.sendPacket(html);
	}
	
	public static final void showTutorialHtml(Player player)
	{
		final ClassId currentClassId = player.getClassId();
		if (getMinLevel(currentClassId.getLevel()) > player.getStatus().getLevel() && !Config.ALLOW_ENTIRE_TREE)
			return;
		
		String msg = HtmCache.getInstance().getHtm("data/html/classmaster/tutorialtemplate.htm");
		msg = msg.replaceAll("%name%", PlayerData.getInstance().getClassNameById(currentClassId.getId()));
		int row = 0;
		final StringBuilder menu = new StringBuilder(100);
		for (ClassId cid : ClassId.values())
		{
			if (validateClassId(currentClassId, cid))
			{
				
				StringUtil.append(menu, ((row % 2) == 0 ? "<table width=\"280\" bgcolor=\"000000\"><tr>" : "<table width=\"280\"><tr>"));
				
				StringUtil.append(menu, "<td height=40 width=40><img src=\"", icon(), "\" width=32 height=32></td><td width=190>", PlayerData.getInstance().getClassNameById(cid.getId()), "<br1><font color=\"B09878\">", "Class Master", "</font></td><td><button action=\"link CO" + String.valueOf(cid.getId()), "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomout2\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>");
				
				StringUtil.append(menu, "</tr></table><img src=\"L2UI.SquareGray\" width=280 height=1>");
				
				row++;
			}
			
		}
		
		msg = msg.replaceAll("%menu%", menu.toString());
		msg = msg.replace("%req_items%", getRequiredItems(currentClassId.getLevel() + 1));
		player.sendPacket(new TutorialShowHtml(msg));
		
	}
	
	public static String icon()
	{
		String msg = "icon.NOIMAGE";
		
		switch (Rnd.get(5))
		{
			case 0:
				
				msg = "Icons.class_changer";
				break;
			case 1:
				msg = "Icons.class_changer";
				break;
			case 2:
				msg = "Icons.class_changer";
				break;
			case 3:
				msg = "Icons.class_changer";
				break;
			case 4:
				msg = "Icons.class_changer";
				break;
			case 5:
				msg = "Icons.class_changer";
				break;
		}
		return msg;
		
	}
	
	private static final boolean checkAndChangeClass(Player player, int val)
	{
		final ClassId currentClassId = player.getClassId();
		if (getMinLevel(currentClassId.getLevel()) > player.getStatus().getLevel() && !Config.ALLOW_ENTIRE_TREE)
			return false;
		
		if (!validateClassId(currentClassId, val))
			return false;
		
		int newJobLevel = currentClassId.getLevel() + 1;
		
		// Weight/Inventory check
		if (!Config.CLASS_MASTER_SETTINGS.getRewardItems(newJobLevel).isEmpty())
		{
			if (player.getWeightPenalty().ordinal() > 2)
			{
				player.sendPacket(SystemMessageId.INVENTORY_LESS_THAN_80_PERCENT);
				return false;
			}
		}
		
		final List<IntIntHolder> neededItems = Config.CLASS_MASTER_SETTINGS.getRequiredItems(newJobLevel);
		
		// check if player have all required items for class transfer
		for (IntIntHolder item : neededItems)
		{
			if (player.getInventory().getItemCount(item.getId(), -1) < item.getValue())
			{
				player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
				return false;
			}
		}
		
		// get all required items for class transfer
		for (IntIntHolder item : neededItems)
		{
			if (!player.destroyItemByItemId("ClassMaster", item.getId(), item.getValue(), player, true))
				return false;
		}
		
		// reward player with items
		for (IntIntHolder item : Config.CLASS_MASTER_SETTINGS.getRewardItems(newJobLevel))
			player.addItem("ClassMaster", item.getId(), item.getValue(), player, true);
		
		switch (player.getClassId())
		{
			case KNIGHT:
			case ELVEN_KNIGHT:
			case PALUS_KNIGHT:
			case ARTISAN:
				player.addItem("Shield", 4222, 1, player, true);
		}
		player.setClassId(val);
		
		if (player.isSubClassActive())
			player.getSubClasses().get(player.getClassIndex()).setClassId(player.getActiveClass());
		else
			player.setBaseClass(player.getActiveClass());
		
		player.sendPacket(new HennaInfo(player));
		player.broadcastUserInfo();
		if (Config.CLASS_MASTER_SETTINGS.isAllowed(player.getClassId().getLevel() + 1) && Config.ALTERNATE_CLASS_MASTER && (((player.getClassId().getLevel() == 1) && (player.getStatus().getLevel() >= 40)) || ((player.getClassId().getLevel() == 2) && (player.getStatus().getLevel() >= 76))))
		{
			showQuestionMark(player);
		}
		return true;
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
	 * @param val new class index
	 * @return
	 */
	private static final boolean validateClassId(ClassId oldCID, int val)
	{
		try
		{
			return validateClassId(oldCID, ClassId.values()[val]);
		}
		catch (Exception e)
		{
			// possible ArrayOutOfBoundsException
		}
		return false;
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
	
	private static String getRequiredItems(int level)
	{
		final List<IntIntHolder> neededItems = Config.CLASS_MASTER_SETTINGS.getRequiredItems(level);
		if (neededItems == null || neededItems.isEmpty())
			return "<tr><td>none</td></r>";
		
		StringBuilder sb = new StringBuilder();
		for (IntIntHolder item : neededItems)
			sb.append("<tr><td><font color=\"LEVEL\">" + item.getValue() + "</font></td><td>" + ItemData.getInstance().getTemplate(item.getId()).getName() + "</td></tr>");
		
		return sb.toString();
	}
	
	public static void OpenHtml(Player player)
	{
		ClassMaster.showTutorialHtml(player);
	}
}