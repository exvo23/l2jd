package net.sf.l2j.gameserver.communitybbs.manager.custom;

import net.sf.l2j.gameserver.communitybbs.manager.BaseBBSManager;
import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.model.actor.Player;

/**
 * @author BAN L2JDEV
 */
public class RankingCBManager extends BaseBBSManager
{

	protected RankingCBManager()
	{
	}
	
	@Override
	public void parseCmd(String command, Player player)
	{
		if (command.startsWith("_cbRank"))
			showRankingHtml(player);
	}
	
	private static void showRankingHtml(Player activeChar)
	{
		String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/Ranking/Index.htm");	
		content = content.replaceAll("%ServerName%", "L2JDev-Project");
		content = content.replaceAll("%name%", activeChar.getName());
		separateAndSend(content, activeChar);
	}
	
	public static RankingCBManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final RankingCBManager INSTANCE = new RankingCBManager();
	}
}