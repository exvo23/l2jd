package net.sf.l2j.gameserver.communitybbs.manager.custom;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.communitybbs.manager.BaseBBSManager;
import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.data.manager.SpawnManager;
import net.sf.l2j.gameserver.data.sql.SpawnTable;
import net.sf.l2j.gameserver.data.xml.IconTable;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.data.xml.MapRegionData;
import net.sf.l2j.gameserver.data.xml.NpcData;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.item.DropCategory;
import net.sf.l2j.gameserver.model.item.DropData;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.model.spawn.ASpawn;
import net.sf.l2j.gameserver.model.spawn.Spawn;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ShowMiniMap;

/**
 * @author BAN - L2JDEV
 */
public class SearchBBSManager extends BaseBBSManager
{
	
	public static SearchBBSManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	@Override
	public void parseCmd(String command, Player player)
	{
		if (command.equals("_cbsearch"))
		{
			final String content = HtmCache.getInstance().getHtm("data/html/CommunityBoard/search/main.htm");
			separateAndSend(content, player);
		}
		if (command.startsWith("_cbsearch"))
		{
			final StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			
			if (!st.hasMoreTokens())
				return;
			
			final String next = st.nextToken();
			
			if (next.equals("drop"))
			{
				final int page = Integer.parseInt(st.nextToken());
				
				if (!st.hasMoreTokens())
				{
					player.sendMessage("Invalid search");
					IndexCBManager.getInstance().parseCmd("_cbhome", player);
					return;
				}
				
				String search = st.nextToken();
				
				while (st.hasMoreTokens())
					search += " " + st.nextToken();
				
				if (search == null)
				{
					player.sendMessage("Invalid search");
					IndexCBManager.getInstance().parseCmd("_cbhome", player);
					return;
				}
				
				showItemsBySearch(player, page, search);
			}
			else if (next.equals("mobswithdrop"))
			{
				final int page = Integer.parseInt(st.nextToken());
				final int itemId = Integer.parseInt(st.nextToken());
				
				final int returnPage = Integer.parseInt(st.nextToken()); // in case there is no npc dropping itemId
				
				String search = st.nextToken();
				
				while (st.hasMoreTokens())
					search += " " + st.nextToken();
				
				showMobsThatDrop(player, page, itemId, returnPage, search);
			}
			else if (next.equals("npcdrops"))
			{
				final int page = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
				final int npcId = Integer.parseInt(st.nextToken());
				
				showNpcDrops(player, page, npcId);
			}
			else if (next.equals("spawns"))
			{
				final int page = Integer.parseInt(st.nextToken());
				final int npcId = Integer.parseInt(st.nextToken());
				
				showNpcSpawns(player, page, npcId);
			}
			else if (next.equals("radar"))
			{
				if (!st.hasMoreTokens())
					return;
				
				final int returnPage = Integer.parseInt(st.nextToken()); //
				final int npcId = Integer.parseInt(st.nextToken());
				
				ASpawn spawn = SpawnManager.getInstance().getSpawn(npcId);
				if (spawn != null)
				{
					ThreadPool.schedule(() -> player.getRadarList().addMarker(spawn.getSpawnLocation()), 500);
					player.sendPacket(ShowMiniMap.REGULAR_MAP);
					
				}
				else
					player.sendMessage("Warning: Can't show location of this NPC.");
				
				showNpcSpawns(player, returnPage, npcId);
			}
			else if (next.equals("npc"))
			{
				final int page = Integer.parseInt(st.nextToken());
				
				final String newName = st.nextToken();
				
				if (!StringUtil.isValidString(newName, "^[A-Za-z0-9]{1,16}$"))
				{
					IndexCBManager.getInstance().parseCmd("_cbhome", player);
					player.sendPacket(SystemMessageId.INCORRECT_NAME_TRY_AGAIN);
					return;
				}
				
				// Name is a npc name.
				if (NpcData.getInstance().getTemplateByName(newName) != null)
				{
					IndexCBManager.getInstance().parseCmd("_cbhome", player);
					player.sendPacket(SystemMessageId.INCORRECT_NAME_TRY_AGAIN);
					return;
					
				}
				
				showNpcsBySearch(player, page, newName);
			}
		}
		else
			separateAndSend("<html><body><br><br><center>Command : " + command + " is not implemented yet</center><br><br></body></html>", player);
	}
	
	private static void showMobsThatDrop(Player player, int page, int itemId, int returnPage, String search)
	{
		String content = HtmCache.getInstance().getHtm("data/html/CommunityBoard/search/npcs.htm");
		final int ITEMS_PER_LIST = 9;
		
		final List<Integer> npcIds = new ArrayList<>();
		
		int myPage = 1;
		int i = 0;
		int shown = 0;
		boolean hasMore = false;
		
		for (NpcTemplate template : NpcData.getInstance().getAllNpcs())
		{
			if (template == null)
				continue;
			
			if (!template.getType().equals("Monster") && !template.getType().equals("RaidBoss") && !template.getType().equals("GrandBoss"))
				continue;
			
			final List<DropCategory> list = new ArrayList<>();
			template.getDropData().forEach(data -> list.add(data));
			
			Collections.reverse(list);
			
			for (final DropCategory cat : list)
			{
				if (cat == null)
					continue;
				
				for (final DropData drop : cat.getAllDrops())
				{
					if (drop == null)
						continue;
					
					if (drop.getItemId() == itemId)
					{
						npcIds.add(template.getNpcId());
						break;
					}
				}
			}
		}
		
		final StringBuilder sb = new StringBuilder();
		for (int id : npcIds)
		{
			final NpcTemplate template = NpcData.getInstance().getTemplate(id);
			
			if (template == null)
				continue;
			
			if (shown == ITEMS_PER_LIST)
			{
				hasMore = true;
				break;
			}
			
			if (myPage != page)
			{
				i++;
				if (i == ITEMS_PER_LIST)
				{
					myPage++;
					i = 0;
				}
				continue;
			}
			
			if (shown == ITEMS_PER_LIST)
			{
				hasMore = true;
				break;
			}
			
			sb.append("<table><tr>");
			sb.append("<td FIXWIDTH=150>" + template.getName() + "</td>");
			sb.append("<td FIXWIDTH=100>" + template.getLevel() + "</td>");
			sb.append("<td FIXWIDTH=70 align=center>" + "<button value=\"Drops\" action=\"bypass _cbsearch npcdrops 1 " + id + "\" width=70 height=21 fore=\"L2UI.DefaultButton\" back=\"L2UI.DefaultButton_click\">" + "</td>");
			sb.append("<td FIXWIDTH=70 align=center>" + "<button value=\"Spawns\" action=\"bypass _cbsearch spawns 1 " + id + "\" width=70 height=21 fore=\"L2UI.DefaultButton\" back=\"L2UI.DefaultButton_click\">" + "</td>");
			sb.append("</tr></table>");
			sb.append("<img src=\"L2UI.Squaregray\" width=\"256\" height=\"1\">");
			shown++;
		}
		
		sb.append("<br1><table width=\"100%\" bgcolor=000000><tr>");
		// + item.getItemId() + " " + page + " " + search +
		if (page > 1)
			sb.append("<td align=left width=70><a action=\"bypass _cbsearch mobswithdrop " + (page - 1) + " " + itemId + " " + returnPage + " " + search + "\">Previous</a></td>");
		else
			sb.append("<td align=left width=70>Previous</td>");
		
		sb.append("<td align=center width=100> Page: " + page + "</td>");
		
		if (hasMore)
			sb.append("<td align=right width=70><a action=\"bypass _cbsearch mobswithdrop " + (page + 1) + " " + itemId + " " + returnPage + " " + search + "\">Next</a></td>");
		else
			sb.append("<td align=right width=70>Next</td>");
		
		sb.append("</tr></table><img src=\"L2UI.SquareGray\" width=256 height=1>");
		
		content = content.replaceAll("%showList%", sb.toString());
		separateAndSend(content, player);
	}
	
	private static void showNpcDrops(Player player, int page, int npcId)
	{
		String content = HtmCache.getInstance().getHtm("data/html/CommunityBoard/search/npcdrops.htm");
		final int ITEMS_PER_LIST = 7;
		final NpcTemplate npc = NpcData.getInstance().getTemplate(npcId);
		if (npc == null)
			return;
		
		if (npc.getDropData().isEmpty())
		{
			player.sendMessage("This target have not drop info.");
			return;
		}
		final List<DropCategory> list = new ArrayList<>();
		npc.getDropData().forEach(c -> list.add(c));
		Collections.reverse(list);
		
		int myPage = 1;
		int i = 0;
		int shown = 0;
		boolean hasMore = false;
		
		final StringBuilder sb = new StringBuilder();
		for (DropCategory cat : list)
		{
			if (shown == ITEMS_PER_LIST)
			{
				hasMore = true;
				break;
			}
			
			for (DropData drop : cat.getAllDrops())
			{
				double chance = (drop.getItemId() == 57 ? drop.getChance() * Config.RATE_DROP_CURRENCY : drop.getChance() * Config.RATE_DROP_ITEMS) / 10000;
				
				chance = chance > 100 ? 100 : chance;
				String percent = null;
				if (chance <= 0.001)
				{
					DecimalFormat df = new DecimalFormat("#.####");
					percent = df.format(chance);
				}
				else if (chance <= 0.01)
				{
					DecimalFormat df = new DecimalFormat("#.###");
					percent = df.format(chance);
				}
				else
				{
					DecimalFormat df = new DecimalFormat("##.##");
					percent = df.format(chance);
				}
				
				final Item item = ItemData.getInstance().getTemplate(drop.getItemId());
				
				String name = item.getName();
				
				if (name.startsWith("Recipe: "))
					name = "R: " + name.substring(8);
				
				if (name.length() >= 35)
					name = name.substring(0, 33) + "..";
				
				if (myPage != page)
				{
					i++;
					if (i == ITEMS_PER_LIST)
					{
						myPage++;
						i = 0;
					}
					continue;
				}
				
				if (shown == ITEMS_PER_LIST)
				{
					hasMore = true;
					break;
				}
				
				sb.append("<table><tr>");
				sb.append("<td FIXWIDTH=32 align=center> <img src=" + IconTable.getIcon(item.getItemId()) + " width=32 height=32>" + "</td>");
				sb.append("<td FIXWIDTH=104 align=left>" + name + "</td>");
				sb.append("<td FIXWIDTH=91 align=center>" + item.getGrade() + "</td>");
				sb.append("<td FIXWIDTH=91 align=center>" + percent + "%" + "</td>");
				sb.append("<td FIXWIDTH=91 align=center>" + drop.getMinDrop() + "</td>");
				sb.append("<td FIXWIDTH=91 align=center>" + drop.getMaxDrop() + "</td>");
				sb.append("</tr></table>");
				sb.append("<img src=\"L2UI.Squaregray\" width=\"256\" height=\"1\">");
				shown++;
			}
		}
		
		sb.append("<br1><table bgcolor=000000><tr>");
		
		if (page > 1)
			sb.append("<td align=left width=70><a action=\"bypass _cbsearch npcdrops " + (page - 1) + " " + npcId + "\">Previous</a></td>");
		else
			sb.append("<td align=left width=70>Previous</td>");
		
		sb.append("<td align=center width=100> Page: " + page + "</td>");
		
		if (hasMore)
			sb.append("<td align=right width=70><a action=\"bypass _cbsearch npcdrops " + (page + 1) + " " + npcId + "\">Next</a></td>");
		else
			sb.append("<td align=right width=70>Next</td>");
		
		sb.append("</tr></table><img src=\"L2UI.SquareGray\" width=256 height=1>");
		
		content = content.replaceAll("%showList%", sb.toString());
		separateAndSend(content, player);
	}
	
	private static void showItemsBySearch(Player player, int page, String search)
	{
		if (search == null)
		{
			player.sendMessage("Invalid search.");
			IndexCBManager.getInstance().parseCmd("_cbhome", player);
			return;
		}
		
		String content = HtmCache.getInstance().getHtm("data/html/CommunityBoard/search/items.htm");
		final int ITEMS_PER_LIST = 7;
		
		final List<Item> items = new ArrayList<>();
		for (Item item : ItemData.getInstance().getAllItems())
		{
			if (item == null)
				continue;
			
			if (item.getName().toLowerCase().contains(search.toLowerCase()))
				items.add(item);
		}
		
		int myPage = 1;
		int i = 0;
		int shown = 0;
		boolean hasMore = false;
		
		if (items.isEmpty())
		{
			player.sendMessage("There are no items matching this search");
			IndexCBManager.getInstance().parseCmd("_cbhome", player);
			return;
		}
		
		final StringBuilder sb = new StringBuilder();
		for (final Item item : items)
		{
			if (item == null)
				continue;
			
			if (shown == ITEMS_PER_LIST)
			{
				hasMore = true;
				break;
			}
			
			if (myPage != page)
			{
				i++;
				if (i == ITEMS_PER_LIST)
				{
					myPage++;
					i = 0;
				}
				continue;
			}
			
			if (shown == ITEMS_PER_LIST)
			{
				hasMore = true;
				break;
			}
			
			sb.append("<table><tr>");
			sb.append("<td FIXWIDTH=32 align=center> <img src=" + IconTable.getIcon(item.getItemId()) + " width=32 height=32>" + "</td>");
			sb.append("<td FIXWIDTH=150 align=left>" + item.getName() + "</td>");
			sb.append("<td FIXWIDTH=100 align=center>" + item.getType() + "</td>");
			sb.append("<td FIXWIDTH=100 align=center>" + item.getGrade() + "</td>");
			sb.append("<td FIXWIDTH=50 align=center>" + "<button value=\"Npcs\" action=\"bypass _cbsearch mobswithdrop 1 " + item.getItemId() + " " + page + " " + search + "\" width=70 height=21 fore=\"L2UI.DefaultButton\" back=\"L2UI.DefaultButton_click\">" + "</td>");
			sb.append("</tr></table>");
			sb.append("<img src=\"L2UI.Squaregray\" width=\"256\" height=\"1\">");
			shown++;
		}
		
		sb.append("<br1><table bgcolor=000000><tr>");
		
		if (page > 1)
			sb.append("<td align=left width=70><a action=\"bypass _cbsearch drop " + (page - 1) + " " + search + "\">Previous</a></td>");
		else
			sb.append("<td align=left width=70>Previous</td>");
		
		sb.append("<td align=center width=100> Page: " + page + "</td>");
		
		if (hasMore)
			sb.append("<td align=right width=70><a action=\"bypass _cbsearch drop " + (page + 1) + " " + search + "\">Next</a></td>");
		else
			sb.append("<td align=right width=70>Next</td>");
		
		sb.append("</tr></table><img src=\"L2UI.SquareGray\" width=256 height=1>");
		
		content = content.replaceAll("%showList%", sb.toString());
		separateAndSend(content, player);
	}
	
	private static void showNpcsBySearch(Player player, int page, String search)
	{
		if (search == null)
		{
			player.sendMessage("Invalid search");
			IndexCBManager.getInstance().parseCmd("_cbhome", player);
			return;
		}
		
		String content = HtmCache.getInstance().getHtm("data/html/CommunityBoard/search/npcsearch.htm");
		final int ITEMS_PER_LIST = 9;
		
		final List<Integer> npcIds = new ArrayList<>();
		
		int myPage = 1;
		int i = 0;
		int shown = 0;
		boolean hasMore = false;
		
		for (NpcTemplate template : NpcData.getInstance().getAllNpcs().stream().filter(t -> t != null && t.getName().toLowerCase().contains(search)).collect(Collectors.toList()))
			npcIds.add(template.getNpcId());
		
		if (npcIds.isEmpty())
		{
			player.sendMessage("There are no npcs matching this search");
			IndexCBManager.getInstance().parseCmd("_cbhome", player);
			return;
		}
		
		final StringBuilder sb = new StringBuilder();
		for (int id : npcIds)
		{
			final NpcTemplate template = NpcData.getInstance().getTemplate(id);
			
			if (shown == ITEMS_PER_LIST)
			{
				hasMore = true;
				break;
			}
			
			if (myPage != page)
			{
				i++;
				if (i == ITEMS_PER_LIST)
				{
					myPage++;
					i = 0;
				}
				continue;
			}
			
			if (shown == ITEMS_PER_LIST)
			{
				hasMore = true;
				break;
			}
			
			sb.append("<table><tr>");
			sb.append("<td FIXWIDTH=280>" + template.getName() + "</td>");
			sb.append("<td FIXWIDTH=80>" + template.getLevel() + "</td>");
			sb.append("<td FIXWIDTH=70 align=center>" + "<button value=\"Drops\" action=\"bypass _cbsearch npcdrops 1 " + id + "\" width=70 height=21 fore=\"L2UI.DefaultButton\" back=\"L2UI.DefaultButton_click\">" + "</td>");
			sb.append("<td FIXWIDTH=70 align=center>" + "<button value=\"Spawns\" action=\"bypass _cbsearch spawns 1 " + id + "\" width=70 height=21 fore=\"L2UI.DefaultButton\" back=\"L2UI.DefaultButton_click\">" + "</td>");
			sb.append("</tr></table>");
			sb.append("<img src=\"L2UI.Squaregray\" width=\"256\" height=\"1\">");
			shown++;
		}
		
		sb.append("<br1><table bgcolor=000000><tr>");
		
		if (page > 1)
			sb.append("<td align=left width=70><a action=\"bypass _cbsearch npc " + (page - 1) + " " + search + "\">Previous</a></td>");
		else
			sb.append("<td align=left width=70>Previous</td>");
		
		sb.append("<td align=center width=100> Page: " + page + "</td>");
		
		if (hasMore)
			sb.append("<td align=right width=70><a action=\"bypass _cbsearch npc " + (page + 1) + " " + search + "\">Next</a></td>");
		else
			sb.append("<td align=right width=70>Next</td>");
		
		sb.append("</tr></table><img src=\"L2UI.SquareGray\" width=256 height=1>");
		
		content = content.replaceAll("%showList%", sb.toString());
		separateAndSend(content, player);
	}
	
	private static void showNpcSpawns(Player player, int page, int npcId)
	{
		String content = HtmCache.getInstance().getHtm("data/html/CommunityBoard/search/spawns.htm");
		final int ITEMS_PER_LIST = 9;
		
		final List<Spawn> spawns = new ArrayList<>();
		
		int myPage = 1;
		int i = 0;
		int shown = 0;
		boolean hasMore = false;
		
		for (Spawn s : SpawnTable.getInstance().getSpawns())
			spawns.add(s);
		
		if (spawns.isEmpty())
		{
			player.sendMessage("This npc has no spawns yet");
			return;
		}
		
		final StringBuilder sb = new StringBuilder();
		for (Spawn temp : spawns)
		{
			if (temp == null || temp.getNpc() == null)
				continue;
			
			if (NpcData.getInstance().getTemplate(temp.getNpcId()).isType("Guard"))
				continue;
			if (NpcData.getInstance().getTemplate(temp.getNpcId()).isType("Merchant"))
				continue;
			if (NpcData.getInstance().getTemplate(temp.getNpcId()).isType("Door"))
				continue;
			final String closestTown = MapRegionData.getInstance().getClosestTownName(temp.getLocX(), temp.getLocY());
			
			if (closestTown == null)
				continue;
			
			if (shown == ITEMS_PER_LIST)
			{
				hasMore = true;
				break;
			}
			
			if (myPage != page)
			{
				i++;
				if (i == ITEMS_PER_LIST)
				{
					myPage++;
					i = 0;
				}
				continue;
			}
			
			if (shown == ITEMS_PER_LIST)
			{
				hasMore = true;
				break;
			}
			
			int current = (myPage - 1) * ITEMS_PER_LIST + shown + 1;
			sb.append("<table><tr>");
			sb.append("<td FIXWIDTH=50 align=center>" + current + "</td>");
			sb.append("<td FIXWIDTH=150 align=center>" + (temp.getNpc().isDead() ? "Dead" : "Alive") + "</td>");
			sb.append("<td FIXWIDTH=150 align=center>" + closestTown + "</td>");
			sb.append("<td FIXWIDTH=100 align=center>" + "<button value=\"Mark\" action=\"bypass _cbsearch radar " + page + " " + npcId + "\" width=70 height=21 fore=\"L2UI.DefaultButton\" back=\"L2UI.DefaultButton_click\">" + "</td>");
			
			sb.append("</tr></table>");
			sb.append("<img src=\"L2UI.Squaregray\" width=\"256\" height=\"1\">");
			shown++;
		}
		
		sb.append("<br1><table bgcolor=000000><tr>");
		
		if (page > 1)
			sb.append("<td align=left width=70><a action=\"bypass _cbsearch spawns " + (page - 1) + " " + npcId + "\">Previous</a></td>");
		else
			sb.append("<td align=left width=70>Previous</td>");
		
		sb.append("<td align=center width=100> Page: " + page + "</td>");
		
		if (hasMore)
			sb.append("<td align=right width=70><a action=\"bypass _cbsearch spawns " + (page + 1) + " " + npcId + "\">Next</a></td>");
		else
			sb.append("<td align=right width=70>Next</td>");
		
		sb.append("</tr></table><img src=\"L2UI.SquareGray\" width=256 height=1>");
		
		content = content.replaceAll("%showList%", sb.toString());
		separateAndSend(content, player);
	}
	
	@Override
	public void parseWrite(String ar1, String ar2, String ar3, String ar4, String ar5, Player player)
	{
	}
	
	private static class SingletonHolder
	{
		private static final SearchBBSManager INSTANCE = new SearchBBSManager();
	}
}