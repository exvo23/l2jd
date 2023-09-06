package net.sf.l2j.gameserver.communitybbs.manager.custom;

import java.util.StringTokenizer;

import net.sf.l2j.commons.data.Pagination;
import net.sf.l2j.commons.lang.StringUtil;

import net.sf.l2j.gameserver.communitybbs.manager.BaseBBSManager;
import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.data.xml.MissionData;
import net.sf.l2j.gameserver.enums.actors.MissionType;
import net.sf.l2j.gameserver.model.Mission;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.holder.IntIntHolder;

/**
 * @author BAN - L2JDEV
 */
public class MissionBBSManager extends BaseBBSManager
{
	public MissionBBSManager()
	{
	}
	
	@Override
	protected String getFolder()
	{
		return "mission/";
	}
	
	@Override
	public void parseCmd(String command, Player player)
	{
		
		if (command.startsWith("_cbmission;"))
		{
			
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			
			int page = Integer.parseInt(st.nextToken());
			
			String filename = "data/html/CommunityBoard/custom/Mission/Index.htm";
			String content = HtmCache.getInstance().getHtm(filename);
			separateAndSend(content, player);
			if (content != null)
			{
				content = content.replaceAll("%ServerName%", "L2JDev-Project");
				content = content.replaceAll("%list%", String.valueOf(getList(player, page)));
			}
			BaseBBSManager.separateAndSend(content, player);
		}
		else
			super.parseCmd(command, player);
	}
	
	protected String getList(Player player, int page)
	{
		final StringBuilder sb = new StringBuilder();
		final Pagination<MissionType> list = new Pagination<>(player.getMissions().getAvailableTypes().stream(), page, 8);
		for (MissionType type : list)
		{
			final IntIntHolder mission = player.getMissions().getMission(type);
			final Mission data = MissionData.getInstance().getMissionByLevel(type, mission.getId() + 1);
			if (data == null)
				continue;
			
			final boolean completed = data.getLevel() == mission.getId();
			sb.append("<table width=620 bgcolor=000000><tr><td width=610 align=center>" + generateBar(606, 4, completed ? data.getRequired() : mission.getValue(), data.getRequired()) + "</td></tr></table><table width=620 bgcolor=000000><tr>");
			sb.append("<td width=40 height=40 align=right><button width=32 height=32 back=" + data.getIcon() + " fore=" + data.getIcon() + "></td>");
			sb.append("<td width=580><font color=LEVEL>Lv " + data.getLevel() + "</font> " + data.getName() + " " + (completed ? "<font color=00FF00>Completed</font>" : "") + "<br1>");
			sb.append("<font color=B09878>" + (completed ? "[" + data.getName() + " achievement completed]" : data.getDescription().replaceAll("%remain%", StringUtil.formatNumber(data.getRequired() - mission.getValue()))) + "</font></td></tr></table><img src=L2UI.SquareGray width=620 height=1>");
		}
		
		list.generateSpace(sb, "<img height=47>");
		sb.append("<img src=L2UI.SquareWhite width=620 height=1><img height=12><img src=L2UI.SquareWhite width=620 height=1>");
		list.generatePagesCommunity(sb, "bypass _cbmission;%page%");
		return sb.toString();
	}
	
	public String generateBar(int width, int height, int current, int max)
	{
		final StringBuilder sb = new StringBuilder();
		current = current > max ? max : current;
		int bar = Math.max((width * (current * 100 / max) / 100), 0);
		sb.append("<table width=" + width + " cellspacing=0 cellpadding=0><tr><td width=" + bar + " align=center><img src=L2UI_CH3.BR_BAR1_CP width=" + bar + " height=" + height + "/></td>");
		sb.append("<td width=" + (width - bar) + " align=center><img src=L2UI_CH3.BR_BAR1_HP1 width=" + (width - bar) + " height=" + height + "/></td></tr></table>");
		return sb.toString();
	}
	
	public static MissionBBSManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final MissionBBSManager INSTANCE = new MissionBBSManager();
	}
}