package net.sf.l2j.gameserver.communitybbs.manager.custom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.l2j.commons.data.CommandTokenizer;
import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.gameserver.communitybbs.manager.BaseBBSManager;
import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.data.sql.ClanTable;
import net.sf.l2j.gameserver.data.sql.PlayerVariables;
import net.sf.l2j.gameserver.data.xml.IconTable;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.model.item.kind.Weapon;
import net.sf.l2j.gameserver.model.pledge.Clan;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.CharInfo;
import net.sf.l2j.gameserver.network.serverpackets.UserInfo;


/**
 * @author BAN L2JDEV
 */
public class BypassCBManager extends BaseBBSManager
{
	private static final String ACTIVED = "<font color=00FF00>ON</font>";
	private static final String DESATIVED = "<font color=FF0000>OFF</font>";
	String hora = "MMM dd, HH:mm";
	private final static int PAGE_LIMIT = 14;
	private final Map<Integer, Long> _tracker = new ConcurrentHashMap<>();
	private static final String SELECT_DONATION = "SELECT transaction_id, payment_status, payment_amount FROM donation_paypal WHERE payer_email=? AND received_by=?";
	private static final String UPDATE_DONATION = "UPDATE donation_paypal SET received_by=? WHERE transaction_id=?";
	
	
	protected BypassCBManager()
	{
	}
	
	@Override
	public void parseCmd(String command, Player player)
	{
		
		if (command.startsWith("_cbGmShop"))
			GmShop(player);
		else if (command.startsWith("_cbTeleport"))
			Teleport(player);
		else if (command.startsWith("_cbInfo"))
			Info(player);
		else if (command.startsWith("_cbEvent"))
			Evetns(player);
		else if (command.startsWith("_cbBuffer"))
			Buffer(player);
		else if (command.startsWith("_cbAIndex"))
			Action(player);
		else if (command.startsWith("_cbAcconts"))
			Acconts(player);
		else if (command.startsWith("_cbChangeName"))
			showChangeName(player);
		else if (command.startsWith("_cbSkins"))
			sendMainWindowskins(player);
		else if (command.startsWith("_cbremove_skin"))
		{
			if (player.isSkinDisable())
				player.setDisableSkin(false);
			else
				player.setDisableSkin(true);
			
			PlayerVariables.unsetVar(player, "skin");
			for (final Player player1 : player.getKnownType(Player.class))
			{
				player1.sendPacket(new CharInfo(player1));
				player1.sendPacket(new CharInfo(player));
				player.sendPacket(new CharInfo(player1));
				player.sendPacket(new CharInfo(player));
				
				player1.sendPacket(new UserInfo(player1));
				player1.sendPacket(new UserInfo(player));
				player.sendPacket(new UserInfo(player1));
				player.sendPacket(new UserInfo(player));
			}
			sendMainWindowskins(player);
		}
		else if (command.startsWith("_cbpaypal"))
		{
			final CommandTokenizer ct = new CommandTokenizer(command);
			
			final String email = ct.nextString();
			if (!StringUtil.isValidEmail(email))
				player.sendPacket(SystemMessageId.INCORRECT_SYNTAX);
			else if (_tracker.getOrDefault(player.getObjectId(), 0L) > System.currentTimeMillis())
				player.sendMessage("The server is still checking the previous request.");
			else
			{
				_tracker.put(player.getObjectId(), System.currentTimeMillis() + 30000L);
				player.sendMessage("The server is checking for donations of '%s'."+email);
				ThreadPool.schedule(() -> confirmDonation(player, email), 3000L);
			}
			//sendHTML(player, "PAYPAL", 1);
		}

		else if (command.startsWith("_cbdisable_helmet"))
		{
			if (player.isDisableHair())
				player.setDisableHair(false);
			else
				player.setDisableHair(true);
			player.broadcastUserInfo();
			sendMainWindowskins(player);
		}
		else if (command.startsWith("_cbChange_Race"))
			showChangeRace(player);
		
		else if (command.startsWith("_cbList_Ranking"))
			showRankingHtml(player);
		
		else if (command.equals("_cbRanking_Pk"))
		{
			final StringBuilder sb = new StringBuilder();
			
			try (Connection con = ConnectionPool.getConnection();
				PreparedStatement ps = con.prepareStatement("SELECT char_name,pkkills,online FROM characters WHERE pvpkills>0 AND accesslevel=0 order by pvpkills desc limit 15");
				ResultSet rs = ps.executeQuery())
			{
				int pos = 0;
				int index = 1;
				while (rs.next())
				{
					
					String pks = rs.getString("pkkills");
					String name = rs.getString("char_name");
					pos += 1;
					String statu = rs.getString("online");
					String status;
					
					if (statu.equals("1"))
						status = "<font color=00FF00>Online</font>";
					else
						status = "<font color=FF0000>Offline</font>";
					
					sb.append("<table width=\"100%\"><tr><td><center>Ranking Position</center></td><td><center>Character Status</center></td><td><center>Character Name</center></td><td><center>Character PKs</center></td></tr>");
					
					sb.append(((index % 2) == 0 ? "<table width=\"100%\" bgcolor=\"000000\"><tr>" : "<table width=\"100%\"><tr>"));
					StringUtil.append(sb, "<tr><td><center><font color =\"AAAAAA\">" + "[" + pos + "]" + "</center></td><td><center>" + status + "</center></td><td><center><font color=00FFFF>" + "[" + name + "]" + "</font></center></td><td><center><font color=00FF00>" + "[" + pks + "]" + "</font></center></td></tr>");
					
					StringUtil.append(sb, "</tr></table>");
					index++;
					
				}
			}
			catch (Exception e)
			{
				LOGGER.warn("Erro Pk Ranking  CB.", e);
			}
			
			String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/Ranking/PK.htm");
			
			content = content.replaceAll("%ServerName%", "L2JDev-Project");
			content = content.replaceAll("%info%", String.valueOf(sb.toString()));
			
			separateAndSend(content, player);
			
		}
		else if (command.equals("_cbRanking_Hero"))
		{
			final StringBuilder sb = new StringBuilder();
			
			try (Connection con = ConnectionPool.getConnection();
				PreparedStatement ps = con.prepareStatement("SELECT h.count, h.played, ch.char_name, ch.classid, ch.online, cl.clan_name, cl.ally_name FROM heroes h LEFT JOIN characters ch ON ch.obj_Id=h.char_id LEFT OUTER JOIN clan_data cl ON cl.clan_id=ch.clanid ORDER BY h.count DESC, ch.char_name ASC LIMIT 15");
				ResultSet rs = ps.executeQuery())
			{
				int pos = 0;
				
				int index = 1;
				while (rs.next())
				{
					int cont = rs.getInt("count");
					int played = rs.getInt("played");
					String name = rs.getString("char_name");
					pos = pos + 1;
					
					String clan_name = rs.getString("clan_name");
					String ally_name = rs.getString("ally_name");
					
					Clan playerClan = ClanTable.getInstance().getClanByName(clan_name);
					int classId = rs.getInt("classid");
					
					if (playerClan != null)
					{
						clan_name = playerClan.getName();
						if (playerClan.getAllyId() > 0)
							ally_name = playerClan.getAllyName();
						else
							ally_name = "No Ally";
						
					}
					else
						clan_name = "No Clan";
					
					String statu = rs.getString("online");
					String status;
					
					if (statu.equals("1"))
						status = "<font color=00FF00>Online</font>";
					else
						status = "<font color=FF0000>Offline</font>";
					
					sb.append("<table width=\"100%\"><tr><td><center>Ranking Position</center></td><td><center>Character Status</center></td><td><center>Character Name</center></td><td><center>Class Name</center></td><td><center>Score Wins</center></td><td><center>Character Ed</center></td><td><center>Clan Name</center></td><td><center>Ally Name</center></td></tr>");
					
					sb.append(((index % 2) == 0 ? "<table width=\"100%\" bgcolor=\"000000\"><tr>" : "<table width=\"100%\"><tr>"));
					StringUtil.append(sb, "<tr><td><center><font color =\"AAAAAA\">" + "[" + pos + "]" + "</center></td><td><center>" + status + "</center></td><td><center><font color=00FFFF>" + "[" + name + "]" + "</font></center></td><td><center><font color=00FF00>" + "[" + className(classId) + "]" + "</font></center></td>" + "<td><center>" + "[" + cont + "]" + "</center></td>" + "<td><center>" + "[" + played + "]" + "</center></td>" + "<td><center>" + "[" + clan_name + "]" + "</center></td>" + "<td><center>" + "[" + ally_name + "]" + "</center></td></tr>");
					
					StringUtil.append(sb, "</tr></table>");
					index++;
					
				}
			}
			catch (Exception e)
			{
				LOGGER.warn("Erro Hero Ranking  CB.", e);
			}
			
			String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/Ranking/HERO.htm");
			
			content = content.replaceAll("%ServerName%", "L2JDev-Project");
			content = content.replaceAll("%info%", String.valueOf(sb.toString()));
			
			separateAndSend(content, player);
			
		}

		else if (command.equals("_cbTop_Ench"))
		{
			String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/Ranking/topench.htm");
			
			final StringBuilder sb = new StringBuilder();
			try (Connection con = ConnectionPool.getConnection())
			{
				try (PreparedStatement ps = con.prepareStatement("SELECT cha.char_name, it.enchant_level, it.item_id FROM characters cha INNER JOIN items it ON cha.obj_Id = it.owner_id WHERE it.enchant_level > 1 AND it.item_id ORDER BY it.enchant_level DESC LIMIT " + PAGE_LIMIT);
					ResultSet rs = ps.executeQuery())
				{
					int index = 1;
					while (rs.next())
					{
						final Item item = ItemData.getInstance().getTemplate(rs.getInt("it.item_id"));
						if (item instanceof Weapon)
						{
							String itemName = item.getName();
							if (itemName.length() > 22)
								itemName = itemName.substring(0, 22) + "...";
							final Player databasePlayer = World.getInstance().getPlayer(player.getName());
							final String status = "L2UI_CH3.msnicon" + (databasePlayer != null && databasePlayer.isOnline() ? "1" : "4");
							
							sb.append(((index % 2) == 0 ? "<table width=\"100%\" bgcolor=\"000000\"><tr>" : "<table width=\"100%\"><tr>"));
							
							StringUtil.append(sb, "<td height=40 width=40><img src=\"", status, "\" width=16 height=16></td><td width=\"100%\">" + " - " + " " + rs.getString("cha.char_name") + " <font color=\"B09878\">", itemName, " +" + rs.getInt("it.enchant_level") + "</font></td><td><button action=\"", "\" width=32 height=32 back=\"" + IconTable.getIcon(item.getItemId()) + "\" fore=\"" + IconTable.getIcon(item.getItemId()) + "\"></td>");
							
							StringUtil.append(sb, "</tr></table><img src=L2UI.SquareGray width=296 height=1>");
							index++;
						}
					}
				}
			}
			catch (Exception e)
			{
				LOGGER.warn("Erro Rank Enchant Level.", e);
			}
			
			
			content = content.replaceAll("%info%", sb.toString());
			separateAndSend(content, player);
		}
		else if (command.equals("_cbRanking_Pvp"))
		{
			final StringBuilder sb = new StringBuilder();
			
			try (Connection con = ConnectionPool.getConnection();
				PreparedStatement ps = con.prepareStatement("SELECT char_name,pvpkills,online FROM characters WHERE pvpkills>0 AND accesslevel=0 order by pvpkills desc limit 15");
				ResultSet rs = ps.executeQuery())
			{
				int pos = 0;
				int index = 1;
				while (rs.next())
				{
					
					String pvps = rs.getString("pvpkills");
					String name = rs.getString("char_name");
					pos += 1;
					String statu = rs.getString("online");
					String status;
					
					if (statu.equals("1"))
						status = "<font color=00FF00>Online</font>";
					else
						status = "<font color=FF0000>Offline</font>";
					
					sb.append("<table width=\"100%\"><tr><td><center>Ranking Position</center></td><td><center>Character Status</center></td><td><center>Character Name</center></td><td><center>Character PvPs</center></td></tr>");
					
					sb.append(((index % 2) == 0 ? "<table width=\"100%\" bgcolor=\"000000\"><tr>" : "<table width=\"100%\"><tr>"));
					StringUtil.append(sb, "<tr><td><center><font color =\"AAAAAA\">" + "[" + pos + "]" + "</center></td><td><center>" + status + "</center></td><td><center><font color=00FFFF>" + "[" + name + "]" + "</font></center></td><td><center><font color=00FF00>" + "[" + pvps + "]" + "</font></center></td></tr>");
					
					StringUtil.append(sb, "</tr></table>");
					index++;
					
				}
			}
			catch (Exception e)
			{
				LOGGER.warn("Erro PVP Ranking CB.", e);
			}
			
			String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/Ranking/PVP.htm");
			
			content = content.replaceAll("%ServerName%", "L2JDev-Project");
			content = content.replaceAll("%info%", String.valueOf(sb.toString()));
			
			separateAndSend(content, player);
			
		}
		else if (command.equals("_cbRanking_Clan"))
		{
			final StringBuilder sb = new StringBuilder();
			
			try (Connection con = ConnectionPool.getConnection();
				PreparedStatement ps = con.prepareStatement("SELECT clan_name,clan_level,reputation_score FROM clan_data WHERE clan_level>0 order by reputation_score desc limit 15");
				ResultSet rs = ps.executeQuery())
			{
				int pos = 0;
				int index = 1;
				while (rs.next())
				{
					String clan_name = rs.getString("clan_name");
					String clan_level = rs.getString("clan_level");
					String clan_score = rs.getString("reputation_score");
					pos += 1;
					sb.append("<table width=\"100%\"><tr><td><center>Rank</center></td><td><center>Level</center></td><td><center>Clan Name</center></td><td><center>Reputation</center></td></tr>");
					
					sb.append(((index % 2) == 0 ? "<table width=\"100%\" bgcolor=\"000000\"><tr>" : "<table width=\"100%\"><tr>"));
					StringUtil.append(sb, "<tr><td><center><font color =\"AAAAAA\">" + pos + "</center></td><td><center>" + clan_level + "</center></td><td><center><font color=00FFFF>" + clan_name + "</font></center></td><td><center><font color=00FF00>" + clan_score + "</font></center></td></tr>");
					
					StringUtil.append(sb, "</tr></table>");
					index++;
					
				}
			}
			catch (Exception e)
			{
				LOGGER.warn("Erro Clan Retutaion Level.", e);
			}
			
			String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/Ranking/CLAN.htm");
			
			content = content.replaceAll("%ServerName%", "L2JDev-Project");
			content = content.replaceAll("%info%", String.valueOf(sb.toString()));
			
			separateAndSend(content, player);
			
		}
		
		else
			super.parseCmd(command, player);
	}
	
	
	public static void showChangeName(Player activeChar)
	{
		
		String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/NameChange.htm");
		
		content = content.replaceAll("%name%", activeChar.getName());
		content = content.replaceAll("%Accontname%", activeChar.getAccountName());
		content = content.replaceAll("%HwidIp%", "Hwid: " + activeChar.getIP());
		content = content.replaceAll("%ServerName%", "L2JDev-Project");
		separateAndSend(content, activeChar);
	}
	
	private static void GmShop(Player activeChar)
	{
		String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/GmShop.htm");
		content = content.replaceAll("%ServerName%", "L2JDev-Project");
		separateAndSend(content, activeChar);
	}
	
	private static void Action(Player activeChar)
	{
		String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/Auction.htm");
		content = content.replaceAll("%ServerName%", "L2JDev-Project");
		separateAndSend(content, activeChar);
	}
	
	private static void Teleport(Player activeChar)
	{
		String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/Teleport.htm");
		content = content.replaceAll("%ServerName%", "L2JDev-Project");
		separateAndSend(content, activeChar);
	}
	
	private static void Info(Player activeChar)
	{
		String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/Info.htm");
		content = content.replaceAll("%ServerName%", "L2JDev-Project");
		separateAndSend(content, activeChar);
	}
	
	private static void Evetns(Player activeChar)
	{
		String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/EventTime.htm");
		content = content.replaceAll("%ServerName%", "L2JDev-Project");

		content = content.replaceAll("%online%", String.valueOf(World.getInstance().getPlayers().size()));
		separateAndSend(content, activeChar);
	}
	
	private static void Buffer(Player activeChar)
	{
		String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/buffer/home.htm");
		content = content.replaceAll("%ServerName%", "L2JDev-Project");
		separateAndSend(content, activeChar);
	}
	
	public static void Acconts(Player activeChar)
	{
		String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/Acconts.htm");
		content = content.replaceAll("%ServerName%", "L2JDev-Project");
		separateAndSend(content, activeChar);
	}
	
	public static void sendMainWindowskins(Player player)
	{
		String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/SkinsPanel.htm");
		content = content.replaceAll("%ServerName%", "L2JDev-Project");
		content = content.replaceAll("%bloking%", player.isDisableHair() ? DESATIVED : ACTIVED);
		content = content.replaceAll("%name%", player.getName());
		content = content.replaceAll("%online%", "" + World.getInstance().getPlayers().size());
		content = content.replace("%Premium%", player.isPremium() ? ACTIVED : DESATIVED);
		long delay = player.getMemos().getLong("vipEndTime", 0);
		content = content.replace("%PremiumEnd%", new SimpleDateFormat("dd-MM-yyyy HH:mm").format(delay) + "");
		
		separateAndSend(content, player);
		
		return;
	}
	
	private static void showChangeRace(Player activeChar)
	{
		
		String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/ChangeRace.htm");
		content = content.replaceAll("%name%", activeChar.getName());
		content = content.replaceAll("%Accontname%", activeChar.getAccountName());
		content = content.replaceAll("%HwidIp%", activeChar.getClient().toString());
		content = content.replace("%class%", activeChar.getTemplate().getClassName());
		separateAndSend(content, activeChar);
	}
	
	private static void showRankingHtml(Player activeChar)
	{
		
		String content = HtmCache.getInstance().getHtm(CB_PATH + "custom/Ranking/Index.htm");
		
		content = content.replaceAll("%ServerName%", "L2JDev-Project");
		content = content.replaceAll("%name%", activeChar.getName());
		content = content.replaceAll("%Accontname%", activeChar.getAccountName());
		content = content.replaceAll("%HwidIp%", activeChar.getClient().toString());
		content = content.replace("%class%", activeChar.getTemplate().getClassName());
		separateAndSend(content, activeChar);
	}
	
	
	
	public final static String className(int classId)
	{
		Map<Integer, String> classList;
		classList = new HashMap<>();
		classList.put(0, "Fighter");
		classList.put(1, "Warrior");
		classList.put(2, "Gladiator");
		classList.put(3, "Warlord");
		classList.put(4, "Knight");
		classList.put(5, "Paladin");
		classList.put(6, "Dark Avenger");
		classList.put(7, "Rogue");
		classList.put(8, "Treasure Hunter");
		classList.put(9, "Hawkeye");
		classList.put(10, "Mage");
		classList.put(11, "Wizard");
		classList.put(12, "Sorcerer");
		classList.put(13, "Necromancer");
		classList.put(14, "Warlock");
		classList.put(15, "Cleric");
		classList.put(16, "Bishop");
		classList.put(17, "Prophet");
		classList.put(18, "Elven Fighter");
		classList.put(19, "Elven Knight");
		classList.put(20, "Temple Knight");
		classList.put(21, "Swordsinger");
		classList.put(22, "Elven Scout");
		classList.put(23, "Plains Walker");
		classList.put(24, "Silver Ranger");
		classList.put(25, "Elven Mage");
		classList.put(26, "Elven Wizard");
		classList.put(27, "Spellsinger");
		classList.put(28, "Elemental Summoner");
		classList.put(29, "Oracle");
		classList.put(30, "Elder");
		classList.put(31, "Dark Fighter");
		classList.put(32, "Palus Knightr");
		classList.put(33, "Shillien Knight");
		classList.put(34, "Bladedancer");
		classList.put(35, "Assasin");
		classList.put(36, "Abyss Walker");
		classList.put(37, "Phantom Ranger");
		classList.put(38, "Dark Mage");
		classList.put(39, "Dark Wizard");
		classList.put(40, "Spellhowler");
		classList.put(41, "Phantom Summoner");
		classList.put(42, "Shillien Oracle");
		classList.put(43, "Shilien Elder");
		classList.put(44, "Orc Fighter");
		classList.put(45, "Orc Raider");
		classList.put(46, "Destroyer");
		classList.put(47, "Orc Monk");
		classList.put(48, "Tyrant");
		classList.put(49, "Orc Mage");
		classList.put(50, "Orc Shaman");
		classList.put(51, "Overlord");
		classList.put(52, "Warcryer");
		classList.put(53, "Dwarven Fighter");
		classList.put(54, "Scavenger");
		classList.put(55, "Bounty Hunter");
		classList.put(56, "Artisan");
		classList.put(57, "Warsmith");
		classList.put(88, "Duelist");
		classList.put(89, "Dreadnought");
		classList.put(90, "Phoenix Knight");
		classList.put(91, "Hell Knight");
		classList.put(92, "Sagittarius");
		classList.put(93, "Adventurer");
		classList.put(94, "Archmage");
		classList.put(95, "Soultaker");
		classList.put(96, "Arcana Lord");
		classList.put(97, "Cardinal");
		classList.put(98, "Hierophant");
		classList.put(99, "Evas Templar");
		classList.put(100, "Sword Muse");
		classList.put(101, "Wind Rider");
		classList.put(102, "Moonlight Sentinel");
		classList.put(103, "Mystic Muse");
		classList.put(104, "Elemental Master");
		classList.put(105, "Evas Saint");
		classList.put(106, "Shillien Templar");
		classList.put(107, "Spectral Dancer");
		classList.put(108, "Ghost Hunter");
		classList.put(109, "Ghost Sentinel");
		classList.put(110, "Storm Screamer");
		classList.put(111, "Spectral Master");
		classList.put(112, "Shillien Saint");
		classList.put(113, "Titan");
		classList.put(114, "Grand Khavatari");
		classList.put(115, "Dominator");
		classList.put(116, "Doomcryer");
		classList.put(117, "Fortune Seeker");
		classList.put(118, "Maestro");
		
		return classList.get(classId);
	}
	
	// RANKING
	class TourRankRecord
	{
		int pos;
		String playerName;
		String recordVal;
		
		public TourRankRecord(int pos, String playerName, String recordVal)
		{
			this.pos = pos + 1;
			this.playerName = playerName;
			this.recordVal = recordVal;
		}
	}

	protected void confirmDonation(Player player, String email)
	{
		double val = 0;
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_DONATION))
		{
			ps.setString(1, email);
			ps.setString(2, "@");
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					if (rs.getString("payment_status").equalsIgnoreCase("Completed"))
					{
						val += Double.valueOf(rs.getString("payment_amount")) * 10;
						try (PreparedStatement ps2 = con.prepareStatement(UPDATE_DONATION))
						{
							ps2.setString(1, player.getName());
							ps2.setString(2, rs.getString("transaction_id"));
							ps2.execute();
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.warn("Couldn't confirm donation info for player '{}' with email '{}':", e, player.getName(), email);
			return;
		}
		
		_tracker.remove(player.getObjectId());
		
		final int quantity = (int) val;
		if (quantity < 1)
		{
			player.sendMessage("Your donation its not listed yet, try again in a while.");
			return;
		}
		
		player.addItem("DonationCurrency", 9315, quantity, player, true);
		player.sendMessage("Thanks you for supporting our server.");
		LOGGER.info("[Donation] Player '{}' received {} Donate Coin(s).", player.getName(), quantity);
	}
	public static BypassCBManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final BypassCBManager INSTANCE = new BypassCBManager();
	}
}
