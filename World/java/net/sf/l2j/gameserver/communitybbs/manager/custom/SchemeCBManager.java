package net.sf.l2j.gameserver.communitybbs.manager.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.commons.math.MathUtil;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.communitybbs.manager.BaseBBSManager;
import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.data.manager.BufferManager;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.Summon;

/**
 * @author BAN L2JDEV
 */
public class SchemeCBManager extends BaseBBSManager
{
	private static final int PAGE_LIMIT = 6;
	
	protected SchemeCBManager()
	{
	}
	
	@Override
	public void parseCmd(String command, Player player)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String currentCommand = st.nextToken();
		
		if (currentCommand.startsWith("_cbmenu"))
		{
			String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/Buffer/home.htm");
			
			content = content.replaceAll("%ServerName%", "L2JDev-Project");
			separateAndSend(content, player);
		}
		
		else if (currentCommand.startsWith("_cbcleanup"))
		{
			player.stopAllEffectsExceptThoseThatLastThroughDeath();
			
			final Summon summon = player.getSummon();
			if (summon != null)
				summon.stopAllEffectsExceptThoseThatLastThroughDeath();
			
			String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/Buffer/home.htm");
			
			content = content.replaceAll("%ServerName%", "L2JDev-Project");
			separateAndSend(content, player);
		}
		else if (currentCommand.startsWith("_cbheal"))
		{
			if (!checkAllowed(player))
				return;
			
			player.getStatus().setMaxCpHpMp();
			
			final Summon summon = player.getSummon();
			if (summon != null)
				summon.getStatus().setMaxHpMp();
			
			String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/Buffer/home.htm");
			
			content = content.replaceAll("%ServerName%", "L2JDev-Project");
			separateAndSend(content, player);
		}
		
		else if (currentCommand.startsWith("_cbsupport"))
		{
			showGiveBuffsWindow(player);
		}
		else if (currentCommand.startsWith("_cbgivebuffs"))
		{
			final String schemeName = st.nextToken();
			final int cost = Integer.parseInt(st.nextToken());
			
			Creature target = null;
			if (st.hasMoreTokens())
			{
				final String targetType = st.nextToken();
				if (targetType != null && targetType.equalsIgnoreCase("_cbpet"))
					target = player.getSummon();
			}
			else
				target = player;
			
			if (target == null)
				player.sendMessage("You don't have a pet.");
			else if (cost == 0 || player.reduceAdena("NPC Buffer", cost, null, true))
				BufferManager.getInstance().applySchemeEffects(null, target, player.getObjectId(), schemeName);
			
			showGiveBuffsWindow(player);
		}
		else if (currentCommand.startsWith("_cbeditschemes"))
		{
			showEditSchemeWindow(player, st.nextToken(), st.nextToken(), Integer.parseInt(st.nextToken()));
		}
		else if (currentCommand.startsWith("_cbskill"))
		{
			final String groupType = st.nextToken();
			final String schemeName = st.nextToken();
			
			final int skillId = Integer.parseInt(st.nextToken());
			final int page = Integer.parseInt(st.nextToken());
			
			final List<Integer> skills = BufferManager.getInstance().getScheme(player.getObjectId(), schemeName);
			
			if (currentCommand.startsWith("_cbskillselect") && !schemeName.equalsIgnoreCase("_cbnone"))
			{
				if (skills.size() < player.getMaxBuffCount())
					skills.add(skillId);
				else
					player.sendMessage("This scheme has reached the maximum amount of buffs.");
			}
			else if (currentCommand.startsWith("_cbskillunselect"))
				skills.remove(Integer.valueOf(skillId));
			
			showEditSchemeWindow(player, groupType, schemeName, page);
		}
		else if (currentCommand.startsWith("_cbcreatescheme"))
		{
			try
			{
				final String schemeName = st.nextToken();
				if (schemeName.length() > 14)
				{
					player.sendMessage("Scheme's name must contain up to 14 chars. Spaces are trimmed.");
					return;
				}
				
				final Map<String, ArrayList<Integer>> schemes = BufferManager.getInstance().getPlayerSchemes(player.getObjectId());
				if (schemes != null)
				{
					if (schemes.size() == Config.BUFFER_MAX_SCHEMES)
					{
						player.sendMessage("Maximum schemes amount is already reached.");
						return;
					}
					
					if (schemes.containsKey(schemeName))
					{
						player.sendMessage("The scheme name already exists.");
						return;
					}
				}
				
				BufferManager.getInstance().setScheme(player.getObjectId(), schemeName.trim(), new ArrayList<>());
				showGiveBuffsWindow(player);
				// Save schemes.
				BufferManager.getInstance().saveSchemes();
			}
			catch (Exception e)
			{
				player.sendMessage("Scheme's name must contain up to 14 chars. Spaces are trimmed.");
			}
		}
		
		else if (currentCommand.startsWith("_cbdeletescheme"))
		{
			try
			{
				final String schemeName = st.nextToken();
				final Map<String, ArrayList<Integer>> schemes = BufferManager.getInstance().getPlayerSchemes(player.getObjectId());
				
				if (schemes != null && schemes.containsKey(schemeName))
					schemes.remove(schemeName);
			}
			catch (Exception e)
			{
				player.sendMessage("This scheme name is invalid.");
			}
			showGiveBuffsWindow(player);
		}
		// Save schemes.
		BufferManager.getInstance().saveSchemes();
		
	}
	
	private static void showGiveBuffsWindow(Player player)
	{
		final StringBuilder sb = new StringBuilder(200);
		
		final Map<String, ArrayList<Integer>> schemes = BufferManager.getInstance().getPlayerSchemes(player.getObjectId());
		if (schemes == null || schemes.isEmpty())
			sb.append("<font color=\"LEVEL\">You haven't defined any scheme.</font>");
		else
		{
			for (Map.Entry<String, ArrayList<Integer>> scheme : schemes.entrySet())
			{
				final int cost = getFee(scheme.getValue());
				
				StringUtil.append(sb, "<table width=100%><tr>");
				StringUtil.append(sb, "<td><center><img src=\"Icons.Minimap_DF_ICN_TerritoryWar_Giran\" width=29 height=28><font color=\"LEVEL\">", scheme.getKey(), "</font></center></td>");
				
				StringUtil.append(sb, "<td><center><img src=\"Icons.MACRO_ICON37\" width=32 height=32><button value=\"" + "Use on Me" + "\" action=\"bypass _cbgivebuffs ", scheme.getKey(), " ", cost + "\" width=\"75\" height=\"21\" back=\"anim70.anim_over" + "\" fore=\"anim70.anim" + "\"> </center></td>");
				StringUtil.append(sb, "<td><center><img src=\"Icons.MACRO_ICON90\" width=32 height=32><button value=\"" + "Use on Pet" + "\" action=\"bypass _cbgivebuffs " + scheme.getKey() + " " + cost + " _cbpet" + "\" width=\"75\" height=\"21\" back=\"anim70.anim_over" + "\" fore=\"anim70.anim" + "\"> </center></td>");
				
				StringUtil.append(sb, "<td><center><img src=\"Icons.MACRO_ICON45\" width=32 height=32><button value=\"" + "Edit" + "\" action=\"bypass _cbeditschemes Buffs ", scheme.getKey(), " 1" + "\" width=\"75\" height=\"21\" back=\"anim70.anim_over" + "\" fore=\"anim70.anim" + "\"></center></td>");
				StringUtil.append(sb, "<td><center><img src=\"Icons.skill0475\" width=32 height=32><button value=\"" + "Delete" + "\" action=\"bypass _cbdeletescheme ", scheme.getKey(), "\" width=\"75\" height=\"21\" back=\"anim70.anim_over" + "\" fore=\"anim70.anim" + "\"></center></td>");
				
				StringUtil.append(sb, "<tr></table><br>");
			}
		}
		
		String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/Buffer/create.htm");
		content = content.replaceAll("%ServerName%", "L2JDev-Project");
		content = content.replaceAll("%schemes%", sb.toString());
		content = content.replaceAll("%max_schemes%", String.valueOf(Config.BUFFER_MAX_SCHEMES));
		
		separateAndSend(content, player);
		
	}
	
	private static void showEditSchemeWindow(Player player, String groupType, String schemeName, int page)
	{
		
		final List<Integer> schemeSkills = BufferManager.getInstance().getScheme(player.getObjectId(), schemeName);
		
		String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/Buffer/schemer.htm");
		
		content = content.replaceAll("%ServerName%", "L2JDev-Project");
		content = content.replaceAll("%schemename%", String.valueOf(schemeName));
		content = content.replaceAll("%count%", String.valueOf(schemeSkills.size() + " / " + player.getMaxBuffCount()));
		content = content.replaceAll("%typesframe%", String.valueOf(getTypesFrame(groupType, schemeName)));
		content = content.replaceAll("%skilllistframe%", String.valueOf(getGroupSkillList(player, groupType, schemeName, page)));
		
		separateAndSend(content, player);
		
	}
	
	private static String getGroupSkillList(Player player, String groupType, String schemeName, int page)
	{
		// Retrieve the entire skills list based on group type.
		List<Integer> skills = BufferManager.getInstance().getSkillsIdsByType(groupType);
		if (skills.isEmpty())
			return "That group doesn't contain any skills.";
		
		// Calculate page number.
		final int max = MathUtil.countPagesNumber(skills.size(), PAGE_LIMIT);
		if (page > max)
			page = max;
		
		// Cut skills list up to page number.
		skills = skills.subList((page - 1) * PAGE_LIMIT, Math.min(page * PAGE_LIMIT, skills.size()));
		
		final List<Integer> schemeSkills = BufferManager.getInstance().getScheme(player.getObjectId(), schemeName);
		final StringBuilder sb = new StringBuilder(skills.size() * 150);
		
		int row = 0;
		for (int skillId : skills)
		{
			final String icon = (skillId < 100) ? "icon.skill00" + skillId : (skillId < 1000) ? "icon.skill0" + skillId : "icon.skill" + skillId;
			
			sb.append(((row % 2) == 0 ? "<table width=\"100%\" bgcolor=\"000000\"><tr>" : "<table width=\"100%\"><tr>"));
			
			if (schemeSkills.contains(skillId))
				StringUtil.append(sb, "<td height=40 width=40><img src=\"", icon, "\" width=32 height=32></td><td width=190>", SkillTable.getInstance().getInfo(skillId, 1).getName(), "<br1><font color=\"B09878\">", BufferManager.getInstance().getAvailableBuff(skillId).getDescription(), "</font></td><td><button action=\"bypass _cbskillunselect ", groupType, " ", schemeName, " ", skillId, " ", page, "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomout2\" fore=\"L2UI_CH3.mapbutton_zoomout1\"></td>");
			else
				StringUtil.append(sb, "<td height=40 width=40><img src=\"", icon, "\" width=32 height=32></td><td width=190>", SkillTable.getInstance().getInfo(skillId, 1).getName(), "<br1><font color=\"B09878\">", BufferManager.getInstance().getAvailableBuff(skillId).getDescription(), "</font></td><td><button action=\"bypass _cbskillselect ", groupType, " ", schemeName, " ", skillId, " ", page, "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomin2\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>");
			
			sb.append("</tr></table><img src=\"L2UI.SquareGray\" width=280 height=1>");
			row++;
		}
		
		for (int i = PAGE_LIMIT; i > row; i--)
			StringUtil.append(sb, "<img height=41>");
		
		// Build page footer.
		sb.append("<br><img src=\"L2UI.SquareGray\" width=280 height=1><table width=\"100%\" bgcolor=000000><tr>");
		
		if (page > 1)
			StringUtil.append(sb, "<td align=left width=70><a action=\"bypass " + "_cbeditschemes ", groupType, " ", schemeName, " ", page - 1, "\">Previous</a></td>");
		else
			StringUtil.append(sb, "<td align=left width=70>Previous</td>");
		
		StringUtil.append(sb, "<td align=center width=100>Page ", page, "</td>");
		
		if (page < max)
			StringUtil.append(sb, "<td align=right width=70><a action=\"bypass " + "_cbeditschemes ", groupType, " ", schemeName, " ", page + 1, "\">Next</a></td>");
		else
			StringUtil.append(sb, "<td align=right width=70>Next</td>");
		
		sb.append("</tr></table><img src=\"L2UI.SquareGray\" width=280 height=1>");
		
		return sb.toString();
	}
	
	private static String getTypesFrame(String groupType, String schemeName)
	{
		final StringBuilder sb = new StringBuilder(500);
		sb.append("<table>");
		
		int count = 0;
		for (String type : BufferManager.getInstance().getSkillTypes())
		{
			if (count == 0)
				sb.append("<tr>");
			
			if (groupType.equalsIgnoreCase(type))
				StringUtil.append(sb, "<td width=65>", type, "</td>");
			else
				StringUtil.append(sb, "<td width=65><a action=\"bypass _cbeditschemes ", type, " ", schemeName, " 1\">", type, "</a></td>");
			
			count++;
			if (count == 4)
			{
				sb.append("</tr>");
				count = 0;
			}
		}
		
		if (!sb.toString().endsWith("</tr>"))
			sb.append("</tr>");
		
		sb.append("</table>");
		
		return sb.toString();
	}
	
	private static int getFee(ArrayList<Integer> list)
	{
		if (Config.BUFFER_STATIC_BUFF_COST > 0)
			return list.size() * Config.BUFFER_STATIC_BUFF_COST;
		
		int fee = 0;
		for (int sk : list)
			fee += BufferManager.getInstance().getAvailableBuff(sk).getPrice();
		
		return fee;
	}
	
	public String getHtmlPath(int val)
	{
		String filename = "";
		if (val == 0)
			filename = "";
		else
			filename = "-" + val;
		
		String content = HtmCache.getInstance().getHtm(CB_PATH + filename + ".htm");
		
		separateAndSend(content, null);
		
		return content;
	}
	
	public boolean checkAllowed(Player activeChar)
	{
		String msg = null;
		if (activeChar.isSitting())
			msg = "You can't use Community Community Buffer when you sit!";
		else if (activeChar.isInCombat())
			msg = "You can't use Community Community Buffer when you Combat!";
		
		else if (activeChar.getPvpFlag() != 0)
			msg = "You can't use Community Community Buffer when you Flag!";
		
		else if (activeChar.getPvpFlag() != 1)
			msg = "You can't use Community Community Buffer when you Flag!";
		
		else if (activeChar.getPvpFlag() != 2)
			msg = "You can't use Community Community Buffer when you Combat Flag!";
		
		else if (activeChar.getPvpFlag() > 0)
			msg = "You can't use Community Community Buffer when you Combat Flag!";
		if (msg != null)
		{
			activeChar.sendMessage(msg);
		}
		return msg == null;
	}
	
	public static SchemeCBManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SchemeCBManager INSTANCE = new SchemeCBManager();
	}
}
