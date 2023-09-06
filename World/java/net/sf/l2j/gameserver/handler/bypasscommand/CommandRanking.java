package net.sf.l2j.gameserver.handler.bypasscommand;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.commons.pool.ConnectionPool;

import net.sf.l2j.gameserver.data.xml.IconTable;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.model.item.kind.Weapon;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author BAN L2JDEV
 */
public class CommandRanking
{
	public static void RankingPvP(Player activeChar, int rank)
	{
	
		switch (rank)
		{
			case 0:
				NpcHtmlMessage htm = new NpcHtmlMessage(0);
				
				StringBuilder tb = new StringBuilder("<html><head><title>Ranking PvP</title></head><body>");
				
				tb.append("<center><button value=\"\" action=\"\" width=256 height=50 back=\"symbol.credit_L2\" fore=\"symbol.credit_L2\"></center>");

				tb.append("<br1><table width=290><tr><td><center>Rank</center></td><td><center>Character</center></td><td><center>Pvp's</center></td><td><center>Status</center></td></tr>");
				
				try (Connection con = ConnectionPool.getConnection();
					PreparedStatement ps = con.prepareStatement("SELECT char_name,pvpkills,online FROM characters WHERE pvpkills>0 AND accesslevel=0 order by pvpkills desc limit 15");
					ResultSet rs = ps.executeQuery())
				{
					int pos = 0;
					
					while (rs.next())
					{
						String pvps = rs.getString("pvpkills");
						String name = rs.getString("char_name");
						pos += 1;
						
						Player databasePlayer2 = World.getInstance().getPlayer(rs.getString("char_name"));
						String status2 = "L2UI_CH3.msnicon" + (databasePlayer2 != null && databasePlayer2.isOnline() ? "1" : "4");
						
						tb.append("<tr><td><center><font color =\"AAAAAA\">" + pos + "</td><td><center><font color=00FFFF>" + name + "</font></center></td><td><center>" + pvps + "</center></td><td><center><img src=\"" + status2 + "\"width=16 height=16></center></td></tr>");
					}
				}
				catch (Exception e)
				{
					
				}
				tb.append("</body></html>");
				
				htm.setHtml(tb.toString());
				activeChar.sendPacket(htm);
				break;
			
			case 1:
				NpcHtmlMessage htm1 = new NpcHtmlMessage(0);
				StringBuilder tb1 = new StringBuilder("<html><head><title>Ranking PK</title></head><body>");
					
				tb1.append("<center><button value=\"\" action=\"\" width=256 height=50 back=\"symbol.credit_L2\" fore=\"symbol.credit_L2\"></center>");

				tb1.append("<br1><table width=290><tr><td><center>Rank</center></td><td><center>Character</center></td><td><center>Pk's</center></td><td><center>Status</center></td></tr>");
				
			
				
				try (Connection con = ConnectionPool.getConnection();
					PreparedStatement ps = con.prepareStatement("SELECT char_name,pkkills,online FROM characters WHERE pvpkills>0 AND accesslevel=0 order by pkkills desc limit 15");
					ResultSet rs = ps.executeQuery())
				{
					int pos = 0;
					
					while (rs.next())
					{
						
						String pks = rs.getString("pkkills");
						String name = rs.getString("char_name");
						pos += 1;
						Player databasePlayer1 = World.getInstance().getPlayer(rs.getString("char_name"));
						String status1 = "L2UI_CH3.msnicon" + (databasePlayer1 != null && databasePlayer1.isOnline() ? "1" : "4");
						
						tb1.append("<tr><td><center><font color =\"AAAAAA\">" + pos + "</td><td><center><font color=00FFFF>" + name + "</font></center></td><td><center>" + pks + "</center></td><td><center><img src=\"" + status1 + "\"width=16 height=16></center></td></tr>");
					}
				}
				catch (Exception e)
				{
					
				}
				tb1.append("</body></html>");
				
				htm1.setHtml(tb1.toString());
				activeChar.sendPacket(htm1);
				break;
			case 2:
				NpcHtmlMessage htm11 = new NpcHtmlMessage(0);
				StringBuilder tb11 = new StringBuilder("<html><head><title>Clan Ranking</title></head><body>");
				
				tb11.append("<center><button value=\"\" action=\"\" width=256 height=50 back=\"symbol.credit_L2\" fore=\"symbol.credit_L2\"></center>");
				
				tb11.append("<br1><table width=290><tr><td><center>Rank</center></td><td><center>Level</center></td><td><center>Clan Name</center></td><td><center>Reputation</center></td></tr>");
				
				tb11.append("</body></html>");
				
				try (Connection con = ConnectionPool.getConnection();
					PreparedStatement ps = con.prepareStatement("SELECT clan_name,clan_level,reputation_score FROM clan_data WHERE clan_level>0 AND accesslevel=0 order by reputation_score desc limit 15");
					ResultSet rs = ps.executeQuery())
				{
					int pos = 0;
					
					while (rs.next())
					{
						String clan_name = rs.getString("clan_name");
						String clan_level = rs.getString("clan_level");
						String clan_score = rs.getString("reputation_score");
						pos += 1;
						
						tb11.append("<tr><td><center><font color =\"AAAAAA\">" + pos + "</center></td><td><center>" + clan_level + "</center></td><td><center><font color=00FFFF>" + clan_name + "</font></center></td><td><center><font color=00FF00>" + clan_score + "</font></center></td></tr>");
					}
				}
				catch (Exception e)
				{
					
				}
				tb11.append("</body></html>");
				
				htm11.setHtml(tb11.toString());
				activeChar.sendPacket(htm11);
				break;
				
			case 3:

				NpcHtmlMessage content = new NpcHtmlMessage(0);
				
				final StringBuilder sb = new StringBuilder();
				
				sb.append("<html><head><title>Top Ranking Enchant </title></head><body><center><br><center><button value=\"\" action=\"\" width=256 height=50 back=\"symbol.credit_L2\" fore=\"symbol.credit_L2\"></center>");
				try (Connection con = ConnectionPool.getConnection())
				{
					try (PreparedStatement ps = con.prepareStatement("SELECT cha.char_name, it.enchant_level, it.item_id FROM characters cha INNER JOIN items it ON cha.obj_Id = it.owner_id WHERE it.enchant_level > 1 AND it.item_id AND accesslevel=0 ORDER BY it.enchant_level DESC LIMIT " + 9);
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
								final Player databasePlayer = World.getInstance().getPlayer(activeChar.getName());
								final String status = "L2UI_CH3.msnicon" + (databasePlayer != null && databasePlayer.isOnline() ? "1" : "4");
								
								sb.append(((index % 2) == 0 ? "<table width=\"300\" bgcolor=\"000000\"><tr>" : "<table width=\"300\"><tr>"));
								
								StringUtil.append(sb, "<td height=10 width=10><img src=\"", status, "\" width=16 height=16></td><td width=\"220\">" + " - " + " " + rs.getString("cha.char_name") + " <font color=\"B09878\">", itemName, " +" + rs.getInt("it.enchant_level") + "</font></td><td><button action=\"", "\" width=32 height=32 back=\"" + IconTable.getIcon(item.getItemId()) + "\" fore=\"" + IconTable.getIcon(item.getItemId()) + "\"></td>");
								
								StringUtil.append(sb, "</tr></table><br1>");
								index++;
							}
						}
					}
				}
				catch (Exception e)
				{

				}
				sb.append("</body></html>");

				content.setHtml(sb.toString());
				activeChar.sendPacket(content);

				break;
		}
	}

}
