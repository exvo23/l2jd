package net.sf.l2j.gameserver.communitybbs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.l2j.commons.logging.CLogger;
import net.sf.l2j.commons.pool.ConnectionPool;

import net.sf.l2j.gameserver.communitybbs.manager.BaseBBSManager;
import net.sf.l2j.gameserver.communitybbs.manager.ClanBBSManager;
import net.sf.l2j.gameserver.communitybbs.manager.FavoriteBBSManager;
import net.sf.l2j.gameserver.communitybbs.manager.FriendsBBSManager;
import net.sf.l2j.gameserver.communitybbs.manager.MailBBSManager;
import net.sf.l2j.gameserver.communitybbs.manager.PostBBSManager;
import net.sf.l2j.gameserver.communitybbs.manager.RegionBBSManager;
import net.sf.l2j.gameserver.communitybbs.manager.TopicBBSManager;
import net.sf.l2j.gameserver.communitybbs.manager.custom.AccontsCBManager;
import net.sf.l2j.gameserver.communitybbs.manager.custom.AuctionBBSManager;
import net.sf.l2j.gameserver.communitybbs.manager.custom.BypassCBManager;
import net.sf.l2j.gameserver.communitybbs.manager.custom.GmShopCBManager;
import net.sf.l2j.gameserver.communitybbs.manager.custom.IndexCBManager;
import net.sf.l2j.gameserver.communitybbs.manager.custom.MissionBBSManager;
import net.sf.l2j.gameserver.communitybbs.manager.custom.RankingCBManager;
import net.sf.l2j.gameserver.communitybbs.manager.custom.SchemeCBManager;
import net.sf.l2j.gameserver.communitybbs.manager.custom.SearchBBSManager;
import net.sf.l2j.gameserver.communitybbs.manager.custom.TeleportCBManager;
import net.sf.l2j.gameserver.communitybbs.model.Forum;
import net.sf.l2j.gameserver.communitybbs.model.Post;
import net.sf.l2j.gameserver.communitybbs.model.Topic;
import net.sf.l2j.gameserver.enums.bbs.ForumAccess;
import net.sf.l2j.gameserver.enums.bbs.ForumType;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.GameClient;

public class CommunityBoard
{
	private static final CLogger LOGGER = new CLogger(CommunityBoard.class.getName());
	
	private static final String SELECT_FORUMS = "SELECT * FROM bbs_forum";
	private static final String SELECT_TOPICS = "SELECT * FROM bbs_topic ORDER BY id DESC";
	private static final String SELECT_POSTS = "SELECT * FROM bbs_post ORDER BY id ASC";
	
	private final Set<Forum> _forums = ConcurrentHashMap.newKeySet();
	
	protected CommunityBoard()
	{
	
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_FORUMS);
			PreparedStatement ps2 = con.prepareStatement(SELECT_TOPICS);
			PreparedStatement ps3 = con.prepareStatement(SELECT_POSTS))
		{
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
					addForum(new Forum(rs));
			}
			
			try (ResultSet rs2 = ps2.executeQuery())
			{
				while (rs2.next())
				{
					final Forum forum = getForumByID(rs2.getInt("forum_id"));
					if (forum == null)
						return;
					
					forum.addTopic(new Topic(rs2));
				}
			}
			
			try (ResultSet rs3 = ps3.executeQuery())
			{
				while (rs3.next())
				{
					final Forum forum = getForumByID(rs3.getInt("forum_id"));
					if (forum == null)
						return;
					
					final Topic topic = forum.getTopic(rs3.getInt("topic_id"));
					if (topic == null)
						return;
					
					topic.addPost(new Post(rs3));
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't load forums.", e);
		}
		LOGGER.info("Loaded {} forums.", _forums.size());
		
	}
	
	public void handleCommands(GameClient client, String command)
	{
		final Player player = client.getPlayer();
		if (player == null)
			return;
		
		if (command.startsWith("_cbhome"))
			IndexCBManager.getInstance().parseCmd(command, player);
		
		else if (command.startsWith("_cbList_Ranking"))
			BypassCBManager.getInstance().parseCmd(command, player);
		
		else if (command.startsWith("_cbPage_Shop"))
			TeleportCBManager.getInstance().parseCmd(command, player);
		
		else if (command.startsWith("_cbRanking_Clan"))
			BypassCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbRanking_Pvp"))
			BypassCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbRanking_Pk"))
			BypassCBManager.getInstance().parseCmd(command, player);
		
		else if (command.startsWith("_cbmission"))
			MissionBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbRanking_ClanPoint"))
			BypassCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbRanking_Hero"))
			BypassCBManager.getInstance().parseCmd(command, player);
			
		else if (command.startsWith("_cbBuy"))
			IndexCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbmultisell"))
			GmShopCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbSetRace"))
			GmShopCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbsearch"))
			SearchBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbauction"))
			AuctionBBSManager.getInstance().parseCmd(command, player);
		
		else if (command.startsWith("_cbremove_skin"))
			BypassCBManager.getInstance().parseCmd(command, player);
		
		
		else if (command.startsWith("_cbGmShop"))
			
			BypassCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbEvent"))
			BypassCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbRL"))
			BypassCBManager.getInstance().parseCmd(command, player);
		
		else if (command.startsWith("_cbTeleport"))
			BypassCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbInfo"))
			BypassCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbBuffer"))
			BypassCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbdoBuf"))
			BypassCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbChangeName"))
			BypassCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbAcconts"))
			BypassCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbSkins"))
			BypassCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbremove_skin"))
			BypassCBManager.getInstance().parseCmd(command, player);
		
		else if (command.startsWith("_cbRBord"))
			BypassCBManager.getInstance().parseCmd(command, player);
		
		else if (command.startsWith("_cbRDaly"))
			BypassCBManager.getInstance().parseCmd(command, player);
		
		else if (command.startsWith("_cbdisable_helmet"))
			BypassCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbChange_Race"))
			BypassCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbLraid"))
			BypassCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbWot"))
			BypassCBManager.getInstance().parseCmd(command, player);
		
		else if (command.startsWith("_cbGoto"))
			TeleportCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbLink"))
			TeleportCBManager.getInstance().parseCmd(command, player);
		
		else if (command.startsWith("_cbmenu"))
			SchemeCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbcleanup"))
			SchemeCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbheal"))
			SchemeCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbsupport"))
			SchemeCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbgivebuffs"))
			SchemeCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbeditschemes"))
			SchemeCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbskill"))
			SchemeCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbskillselect"))
			SchemeCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbnone"))
			SchemeCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbpet"))
			SchemeCBManager.getInstance().parseCmd(command, player);
		
		else if (command.startsWith("_cbskillunselect"))
			SchemeCBManager.getInstance().parseCmd(command, player);
		
		else if (command.startsWith("_cbcreatescheme"))
			SchemeCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbdeletescheme"))
			SchemeCBManager.getInstance().parseCmd(command, player);
		
		else if (command.startsWith("_cbAIndex"))
			BypassCBManager.getInstance().parseCmd(command, player);
		
		else if (command.startsWith("_cb1stClass"))
			AccontsCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cb2ndClass"))
			AccontsCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cb3rdClass"))
			AccontsCBManager.getInstance().parseCmd(command, player);
		
		else if (command.startsWith("_cbchange_class"))
			AccontsCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbbecome_noble"))
			AccontsCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cblearn_skills"))
			AccontsCBManager.getInstance().parseCmd(command, player);
		
		else if (command.startsWith("_cbNameChange"))
			AccontsCBManager.getInstance().parseCmd(command, player);
		
		else if (command.startsWith("_cbEpi"))
			AccontsCBManager.getInstance().parseCmd(command, player);
		
		else if (command.startsWith("_cbTop_Ench"))
			BypassCBManager.getInstance().parseCmd(command, player);
		
		else if (command.startsWith("_cbweapons"))
			RankingCBManager.getInstance().parseCmd(command, player);
		
		
		else if (command.startsWith("_bbsgetfav"))
			FavoriteBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_bbsloc"))
			RegionBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_bbsclan"))
			ClanBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_bbsmemo"))
			TopicBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_bbsmail") || command.equals("_maillist_0_1_0_"))
			MailBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_friend") || command.startsWith("_block"))
			FriendsBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_bbstopics"))
			TopicBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_bbsposts"))
			PostBBSManager.getInstance().parseCmd(command, player);
		
		else
			BaseBBSManager.separateAndSend("<html><body><br><br><center>The command: " + command + " isn't implemented.</center></body></html>", player);
	}
	
	public void handleWriteCommands(GameClient client, String url, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		final Player player = client.getPlayer();
		if (player == null)
			return;
		
		
		if (url.equals("Topic"))
			TopicBBSManager.getInstance().parseWrite(arg1, arg2, arg3, arg4, arg5, player);
		else if (url.equals("Post"))
			PostBBSManager.getInstance().parseWrite(arg1, arg2, arg3, arg4, arg5, player);
		else if (url.equals("_bbsloc"))
			RegionBBSManager.getInstance().parseWrite(arg1, arg2, arg3, arg4, arg5, player);
		else if (url.equals("_bbsclan"))
			ClanBBSManager.getInstance().parseWrite(arg1, arg2, arg3, arg4, arg5, player);
		else if (url.equals("Mail"))
			MailBBSManager.getInstance().parseWrite(arg1, arg2, arg3, arg4, arg5, player);
		else if (url.equals("_friend"))
			FriendsBBSManager.getInstance().parseWrite(arg1, arg2, arg3, arg4, arg5, player);
		else
			BaseBBSManager.separateAndSend("<html><body><br><br><center>The command: " + url + " isn't implemented.</center></body></html>", player);
	}
	
	public void addForum(Forum forum)
	{
		if (forum == null)
			return;
		
		_forums.add(forum);
	}
	
	public int getANewForumId()
	{
		return _forums.stream().mapToInt(Forum::getId).max().orElse(0) + 1;
	}
	
	public Forum getForum(ForumType type, int ownerId)
	{
		return _forums.stream().filter(f -> f.getType() == type && f.getOwnerId() == ownerId).findFirst().orElse(null);
	}
	
	public Forum getForumByID(int id)
	{
		return _forums.stream().filter(f -> f.getId() == id).findFirst().orElse(null);
	}
	
	public Forum getOrCreateForum(ForumType type, ForumAccess access, int ownerId)
	{
		// Try to retrieve the Forum based on ForumType and ownerId.
		Forum forum = getForum(type, ownerId);
		if (forum != null)
			return forum;
		
		// Forum isn't existing, create it.
		forum = new Forum(type, access, ownerId);
		forum.insertIntoDb();
		
		// Add it on memory.
		addForum(forum);
		
		return forum;
	}
	
	public static CommunityBoard getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final CommunityBoard INSTANCE = new CommunityBoard();
	}
}