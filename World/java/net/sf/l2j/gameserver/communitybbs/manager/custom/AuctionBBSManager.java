package net.sf.l2j.gameserver.communitybbs.manager.custom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import net.sf.l2j.commons.data.Pagination;
import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.commons.util.CommandUtil;

import net.sf.l2j.gameserver.communitybbs.manager.BaseBBSManager;
import net.sf.l2j.gameserver.communitybbs.model.Auction;
import net.sf.l2j.gameserver.communitybbs.model.Function;
import net.sf.l2j.gameserver.data.xml.IconTable;
import net.sf.l2j.gameserver.enums.items.EtcItemType;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.EtcItem;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;

/**
 * @author BAN - L2JDEV
 */
public class AuctionBBSManager extends BaseBBSManager
{
	private static final String SELECT_AUCTION = "SELECT * FROM bbs_auction";
	
	private final Map<Integer, Auction> _auctions = new ConcurrentHashMap<>();
	private final Map<Integer, Function> _functions = new ConcurrentHashMap<>();

	public AuctionBBSManager()
	{
	}
	
	@Override
	public void parseCmd(String command, Player player)
	{
		final Function function = getFunction(player);
		final CommandUtil cu = new CommandUtil(command);
		
		
		if (cu.getCommand().equals("_cbauction"))
		{
			final int page = cu.nextInt(function.getLastPage());
			switch (cu.nextString())
			{
				case "src":
					final String search = cu.hasMoreTokens() ? command.substring(16 + String.valueOf(page).length()) : "";
					if (search.isBlank() || command.length() > (18 + String.valueOf(page).length()))
						function.setSearch(search);
					else
						player.sendPacket(SystemMessageId.INCORRECT_SYNTAX);
					break;
				
				case "clear":
					function.setSearch("");
					break;
				
				case "type":
					function.setItemType(cu.nextString("All"));
					break;
				
				case "grade":
					function.setItemGrade(cu.nextString("All"));
					break;
				
				case "currency":
					function.setCurrency(cu.nextString("All"));
					break;
				
				case "select":
					function.setViewId(cu.nextInt(0));
					break;
				
				case "purchase":
					final Auction auction = getAuction(function.getViewId());
					if (auction == null)
						player.sendMessage("Selected item no longer exist on auction house.");
					else if (auction.tryPurchase(player, cu.nextInt(0)))
					{
						function.setViewId(0);
						sendIndex(player, function.getLastPage(), function);
						return;
					}
					break;
				
				case "cancel":
					function.setViewId(0);
					break;
			}
			sendIndex(player, page, function);
		}
		else if (cu.getCommand().equals("_cbauction_mine"))
		{
			final String param = cu.nextString().toLowerCase();
			switch (param)
			{
				case "toselect":
					function.setItemOID(-1);
					player.sendPacket(new ItemList(player, true));
					break;
				
				case "unselect":
					function.setItemOID(0);
					break;
				
				case "edit":
					function.setEditId(cu.nextInt(0));
					break;
				
				case "update":
				case "remove":
					final Auction auction = getAuction(function.getEditId());
					if (auction == null)
						player.sendMessage("Selected item no longer exist on auction house.");
					else
					{
						if (param.equals("remove"))
						{
							auction.refund();
							function.setEditId(0);
						}
						else
							auction.updateDuration();
					}
					break;
				
				case "cancel":
					function.setEditId(0);
					break;
				
				case "sell":
					final ItemInstance item = player.getInventory().getItemByObjectId(function.getItemOID());
					if (item == null)
						player.sendMessage("You have not the selected item on your inventory.");
					else
						sellItem(player, item.getObjectId(), cu.nextInt(0), cu.nextString("Adena"), cu.nextInt(0));
					
					function.setItemOID(0);
					break;
			}
			sendIndexMine(player, function);
		}
	}
	
	public void sendIndex(Player player, int page, Function function)
	{
		String content = getContent("index.htm");
		final StringBuilder sb = new StringBuilder("<img height=6><img src=L2UI.SquareWhite width=400 height=1>");
		
		final Auction auctionView = getAuction(function.getViewId());
		if (auctionView == null)
		{
			function.setLastPage(page);
			
			final Pagination<Auction> list = new Pagination<>(getAuctions().stream(), page, 8, a -> a.filter(function), Comparator.comparing(Auction::getItemName).thenComparing(Comparator.comparing(Auction::getPriceCount)));
			for (Auction auction : list)
			{
				final Item item = auction.getItem();
				StringUtil.append(sb, "<table width=400><tr><td width=36 height=40 align=center><img src=", IconTable.getIcon(item.getItemId()), " width=32 height=32></td>");
				StringUtil.append(sb, "<td width=20 align=center valign=top>", auction.getGradeIcon(), "</td>");
				StringUtil.append(sb, "<td width=274>", getName(auction), "<br1><font color=A3A3A3>Price", (item.isStackable() ? " Each" : ""), ":</font> ", StringUtil.formatNumber(auction.getPriceCount()), " ", auction.getPrice().getName(), "</font></td>");
				
				if (auction.getPlayerOID() == player.getObjectId())
					StringUtil.append(sb, "<td width=70 align=center><font color=5A5A5A>OWNER</font></td>");
				else
					StringUtil.append(sb, "<td width=70 align=center><img height=6><button value=\"Purchase\" action=\"bypass _cbauction ", page, " select ", auction.getId(), "\" width=65 height=19 back=L2UI_ch3.smallbutton2_down fore=L2UI_ch3.smallbutton2></td>");
				
				sb.append("</tr></table><img src=L2UI.SquareGray width=400 height=1>");
			}
			list.generateSpace(sb, "<img height=41>");
			sb.append("<img src=L2UI.SquareWhite width=400 height=1><img height=6><img src=L2UI.SquareWhite width=400 height=1>");
			list.generatePagesMedium(sb, "bypass _cbauction %page%");
			sb.append("<img src=L2UI.SquareWhite width=400 height=1>");
		}
		else
		{
			final Item item = auctionView.getItem();
			sb.append("<img height=20><img src=L2UI.SquareGray width=350 height=1>");
			sb.append("<table width=350 bgcolor=000000><tr><td width=350 align=center><font color=A3A3A3>Selected Item</font></td></tr></table>");
			sb.append("<img src=L2UI.SquareGray width=350 height=1>");
			StringUtil.append(sb, "<table width=350 bgcolor=000000><tr><td width=36 height=40 align=center valign=top><img src=", IconTable.getIcon(item.getItemId()), " width=32 height=32></td>");
			StringUtil.append(sb, "<td width=20 align=center valign=top>", auctionView.getGradeIcon(), "</td>");
			StringUtil.append(sb, "<td width=294>", getName(auctionView), "<br1><font color=A3A3A3>Price", (item.isStackable() ? " Each" : ""), ":</font> ", StringUtil.formatNumber(auctionView.getPriceCount()), " ", auctionView.getPrice().getName(), "</font><br1>");
			StringUtil.append(sb, "<font color=A3A3A3>Owner:</font> <font color=B09B79>", auctionView.getPlayerName(), "</font><br1>");
			StringUtil.append(sb, "<font color=A3A3A3>Auction Expire:</font> <font color=B09B79>", new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(auctionView.getDuration()), " GMT +2</font></td>");
			sb.append("</tr></table><img src=L2UI.SquareGray width=350 height=1>");
			
			if (item.isStackable())
			{
				sb.append("<img height=104><img src=L2UI.SquareGray width=350 height=1>");
				StringUtil.append(sb, "<table width=350 bgcolor=000000><tr><tr><td width=2></td><td width=350><font color=A3A3A3>Price Total:</font> ", StringUtil.formatNumber(auctionView.getItemCount() * auctionView.getPriceCount()), " ", auctionView.getPrice().getName(), "</font></td></tr></tr></table>");
				sb.append("<img src=L2UI.SquareGray width=350 height=1>");
				sb.append("<table width=350 height=28 bgcolor=000000><tr>");
				sb.append("<td width=2></td><td width=56><font color=A3A3A3>Quanity:</font></td>");
				sb.append("<td width=292><img height=2><edit var=quanity type=number width=270 height=12></td>");
				sb.append("</tr></table>");
				sb.append("<img src=L2UI.SquareGray width=350 height=1>");
				sb.append("<img height=20>");
				sb.append("<img src=L2UI.SquareGray width=350 height=1>");
				sb.append("<table width=350 bgcolor=000000><tr><td width=350 align=center><font color=B09878>Do you want to continue the purchase?</font></td></tr></table>");
				sb.append("<table width=350 bgcolor=000000><tr>");
				StringUtil.append(sb, "<td width=175 align=right><button value=\"Yes\" action=\"bypass _cbauction ", page, " purchase $quanity\" width=65 height=19 back=L2UI_ch3.smallbutton2_down fore=L2UI_ch3.smallbutton2></td>");
				StringUtil.append(sb, "<td width=175 align=left><button value=\"Cancel\" action=\"bypass _cbauction ", page, " cancel\" width=65 height=19 back=L2UI_ch3.smallbutton2_down fore=L2UI_ch3.smallbutton2></td>");
				sb.append("</tr></table><img src=L2UI.SquareGray width=350 height=1>");
			}
			else
			{
				sb.append("<img height=177>");
				sb.append("<img src=L2UI.SquareGray width=350 height=1>");
				sb.append("<table width=350 bgcolor=000000><tr><td width=350 align=center><font color=B09878>Do you want to continue the purchase?</font></td></tr></table>");
				sb.append("<table width=350 bgcolor=000000><tr>");
				StringUtil.append(sb, "<td width=175 align=right><button value=\"Yes\" action=\"bypass _cbauction ", page, " purchase 1\" width=65 height=19 back=L2UI_ch3.smallbutton2_down fore=L2UI_ch3.smallbutton2></td>");
				StringUtil.append(sb, "<td width=175 align=left><button value=\"Cancel\" action=\"bypass _cbauction ", page, " cancel\" width=65 height=19 back=L2UI_ch3.smallbutton2_down fore=L2UI_ch3.smallbutton2></td>");
				sb.append("</tr></table><img src=L2UI.SquareGray width=350 height=1>");
			}
			sb.append("<img height=16><img src=L2UI.SquareWhite width=400 height=1>");
		}
		
		content = content.replace("%template%", sb.toString());
		content = content.replace("%search%", getSearch(function));
		content = content.replace("%filters%", getFileters(function));
		separateAndSend(content, player);
	}
	
	public void sendIndexMine(Player player, Function function)
	{
		String content = getContent("index-mine.htm");
		final StringBuilder sb = new StringBuilder();
		
		final Auction auctionEdit = getAuction(function.getEditId());
		if (auctionEdit == null)
		{
			final Pagination<Auction> list = new Pagination<>(getAuctions().stream(), 1, 9, a -> a.getPlayerOID() == player.getObjectId(), Comparator.comparing(Auction::getDuration).reversed());
			for (Auction auction : list)
			{
				final Item item = auction.getItem();
				StringUtil.append(sb, "<table width=320", (auction.getDuration() < System.currentTimeMillis() ? " bgcolor=FF0000" : ""), "><tr><td width=36 height=40 align=center><img src=", IconTable.getIcon(item.getItemId()), " width=32 height=32></td>");
				StringUtil.append(sb, "<td width=20 align=center valign=top>", auction.getGradeIcon(), "</td>");
				StringUtil.append(sb, "<td width=244>", getName(auction), "<br1><font color=A3A3A3>Price", (item.isStackable() ? " Each" : ""), ":</font> ", StringUtil.formatNumber(auction.getPriceCount()), " ", auction.getPrice().getName(), "</font></td>");
				StringUtil.append(sb, "<td width=20 align=right valign=top><button action=\"bypass _cbauction_mine edit ", auction.getId(), "\" width=14 height=14 back=L2UI_CH3.ChatWnd.Chatting_Option2 fore=L2UI_CH3.ChatWnd.Chatting_Option1></td>");
				sb.append("</tr></table><img src=L2UI.SquareGray width=320 height=1>");
			}
			list.generateSpace(sb, "<img height=41>");
			content = content.replace("%info%", String.format("<font color=A3A3A3>Your listed auction items</font> <font color=B09B79>(%s/9)</font>", list.size()));
		}
		else
		{
			sb.append("<img height=6><img src=L2UI.SquareWhite width=320 height=1>");
			
			final Item item = auctionEdit.getItem();
			final String info = auctionEdit.getDuration() > System.currentTimeMillis() ? "<font color=00FF00>" + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(auctionEdit.getDuration()) + " GMT +2</font>" : "<font color=FF0000>Has Expire</font>";
			
			sb.append("<img height=20><img src=L2UI.SquareGray width=300 height=1>");
			sb.append("<table width=300 bgcolor=000000><tr><td width=300 align=center><font color=A3A3A3>Selected Item</font></td></tr></table>");
			sb.append("<img src=L2UI.SquareGray width=300 height=1>");
			StringUtil.append(sb, "<table width=300 bgcolor=000000><tr><td width=36 height=40 align=center valign=top><img src=", IconTable.getIcon(item.getItemId()), " width=32 height=32></td>");
			StringUtil.append(sb, "<td width=20 align=center valign=top>", auctionEdit.getGradeIcon(), "</td>");
			StringUtil.append(sb, "<td width=244>", getName(auctionEdit), "<br1><font color=A3A3A3>Price", (item.isStackable() ? " Each" : ""), ":</font> ", StringUtil.formatNumber(auctionEdit.getPriceCount()), " ", auctionEdit.getPrice().getName(), "</font></td>");
			sb.append("</tr></table><img src=L2UI.SquareGray width=300 height=1>");
			
			sb.append("<img height=146><img src=L2UI.SquareGray width=300 height=1>");
			sb.append("<table width=300 bgcolor=000000><tr>");
			sb.append("<td width=230><font color=B09878>Remove the item from auction house.</font></td>");
			sb.append("<td width=70 align=center><button value=\"Remove\" action=\"bypass _cbauction_mine remove\" width=65 height=19 back=L2UI_ch3.smallbutton2_down fore=L2UI_ch3.smallbutton2></td>");
			sb.append("</tr></table><img src=L2UI.SquareGray width=300 height=1>");
			sb.append("<table width=300 bgcolor=000000><tr>");
			sb.append("<td width=230><font color=B09878>Update auction expire time for next 7 days.</font><br1><font color=A3A3A3>Price:</font> <font color=00FFFF>15,000 Adena</font></td>");
			sb.append("<td width=70 align=center><img height=6><button value=\"Update\" action=\"bypass _cbauction_mine update\" width=65 height=19 back=L2UI_ch3.smallbutton2_down fore=L2UI_ch3.smallbutton2></td>");
			sb.append("</tr></table><img src=L2UI.SquareGray width=300 height=1>");
			sb.append("<img height=20><img src=L2UI.SquareGray width=300 height=1><table width=300 bgcolor=000000><tr>");
			sb.append("<td width=230><font color=B09878>Return back to your auction list.</font></td>");
			sb.append("<td width=70 align=center><button value=\"Back\" action=\"bypass _cbauction_mine cancel\" width=65 height=19 back=L2UI_ch3.smallbutton2_down fore=L2UI_ch3.smallbutton2></td>");
			sb.append("</tr></table><img src=L2UI.SquareGray width=300 height=1>");
			sb.append("<img height=16><img src=L2UI.SquareWhite width=320 height=1>");
			
			content = content.replace("%info%", "<font color=A3A3A3>Auction Duration:</font> " + info);
		}
		content = content.replace("%template%", sb.toString());
		content = content.replace("%inventory%", getInventory(player));
		separateAndSend(content, player);
	}
	
	public String getName(Auction auction)
	{
		String name = auction.getItem().getName();
		if (name.length() >= 44)
			name = name.substring(0, 42) + "..";
		if (auction.getItem().isEquipable() && name.contains(" - "))
			name = auction.getItem().getName().replace(" - ", "</font> - <font color=LEVEL>") + "</font>";
		if (auction.getItem().isStackable())
			name += " (" + StringUtil.formatNumber(auction.getItemCount()) + ")";
		if (auction.getItemEnchant() > 0)
			name += " <font color=B09B79>+" + auction.getItemEnchant() + "</font>";
		
		return name;
	}
	
	public String getSearch(Function function)
	{
		if (function.getViewId() != 0)
			return "<td width=45 align=right></td><td width=265 align=left></td><td width=80 align=left></td>";
		
		if (!function.getSearch().isBlank())
			return "<td width=45 align=right><font color=B09878>Search:</font></td><td width=265 align=left><font color=LEVEL>" + function.getSearch() + "</font></td><td width=80 align=left><button value=\"Clear\" action=\"bypass _cbauction 1 clear\" width=75 height=21 back=L2UI_ch3.Btn1_normalOn fore=L2UI_ch3.Btn1_normal></td>";
		
		return "<td width=45 align=right><font color=B09878>Search:</font></td><td width=265 align=left><img height=3><edit var=param width=250 height=11 length=75></td><td width=80 align=left><button value=\"Search\" action=\"bypass _cbauction 1 src $param\" width=75 height=21 back=L2UI_ch3.Btn1_normalOn fore=L2UI_ch3.Btn1_normal></td>";
	}
	
	public String getFileters(Function function)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("<img height=6><img src=L2UI.SquareWhite width=180 height=1><table width=180><tr><td width=180 align=center><font color=A3A3A3>Item Type</font></td></tr></table>");
		StringUtil.append(sb, "<img src=L2UI.SquareWhite width=180 height=1>", getFilters("type", function.getItemType(), "All,Weapon,Armor,Jewel,Other,Soulshot/Spiritshot,Enchant"), "<img src=L2UI.SquareWhite width=180 height=1>");
		sb.append("<table width=180><tr><td width=180 align=center><font color=A3A3A3>Item Grade</font></td></tr></table>");
		StringUtil.append(sb, "<img src=L2UI.SquareWhite width=180 height=1>", getFilters("grade", function.getItemGrade(), "All,NONE,D,C,B,A,S"), "<img src=L2UI.SquareWhite width=180 height=1>");
		sb.append("<table width=180><tr><td width=180 align=center><font color=A3A3A3>Currency</font></td></tr></table>");
		StringUtil.append(sb, "<img src=L2UI.SquareWhite width=180 height=1>", getFilters("currency", function.getCurrency(), "All,TicktDonate,Tournament,GoldBar,Adena"), "<img src=L2UI.SquareWhite width=180 height=1><img height=10>");
		return sb.toString();
	}
	
	public String getFilters(String bypass, String function, String type)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("<table width=180>");
		for (String split : type.split(","))
		{
			final String typeAll = function.toUpperCase().equals(split.replaceAll(" ", "").toUpperCase()) ? "width=12 height=12 back=L2UI.CheckBox_checked fore=L2UI.CheckBox_checked" : "action=\"bypass _cbauction 1 " + bypass + " " + split.replaceAll(" ", "") + "\" width=12 height=12 back=L2UI.CheckBox fore=L2UI.CheckBox";
			if (split.equals("NONE") || split.equals("D") || split.equals("C") || split.equals("B") || split.equals("A") || split.equals("S"))
				split = ((split.equals("NONE") ? "No-Grade" : split + "-Grade"));
			
			if (split.equals("Enchant"))
				split = "Enchant Scroll";
			
			StringUtil.append(sb, "<tr><td width=20 align=right><button ", typeAll, "></td><td width=160 align=left>", split, "</td></tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}
	
	public String getInventory(Player player)
	{
		final int itemOID = getFunction(player).getItemOID();
		final StringBuilder sb = new StringBuilder();
		final ItemInstance item = player.getInventory().getItemByObjectId(itemOID);
		if (item != null)
		{
			String name = item.getName();
			if (name.length() >= 44)
				name = name.substring(0, 42) + "..";
			if (item.isEquipable() && name.contains(" - "))
				name = item.getName().replace(" - ", "</font> - <font color=LEVEL>") + "</font>";
			
			sb.append("<table width=270><tr><td width=270 align=right valign=top><button action=\"bypass _cbauction_mine unselect\" width=14 height=14 back=L2UI_CH3.FrameCloseOnBtn fore=L2UI_CH3.FrameCloseBtn></td></tr></table>");
			sb.append("<img height=4><img src=L2UI.SquareGray width=270 height=1><table width=270 bgcolor=000000><tr><td width=36 height=40 align=center valign=top><img src=" + IconTable.getIcon(item.getItemId()) + " width=32 height=32></td>");
			sb.append("<td width=234>" + name + "<br1>");
			sb.append((item.getItem() instanceof EtcItem && ((EtcItem) item.getItem()).getItemType() == EtcItemType.PET_COLLAR) ? "<font color=A3A3A3>Level:</font> <font color=B09B79>" + item.getEnchantLevel() + "</font></td>" : item.isStackable() ? "<font color=A3A3A3>Quantity:</font> " + StringUtil.formatNumber(item.getCount()) + "</font></td>" : "<font color=A3A3A3>Enchant Level:</font> <font color=B09B79>+" + item.getEnchantLevel() + "</font></td>");
			sb.append("</tr></table><img src=L2UI.SquareGray width=270 height=1>");
			
			sb.append("<img height=160><img src=L2UI.SquareGray width=270 height=1><table width=270 height=24 bgcolor=000000><tr>");
			sb.append("<td width=80><font color=A3A3A3>Auction Fee:</font></td>");
			sb.append("<td width=190 align=left><font color=00FFFF>15,000 Adena</font></td>");
			sb.append("</tr></table><img src=L2UI.SquareGray width=270 height=1>");
			
			sb.append("<table width=270 height=24 bgcolor=000000><tr>");
			sb.append("<td width=80><font color=A3A3A3>Auction Expire:</font></td><td width=190 align=left><font color=B09B79>" + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7)) + " GMT +2</font></td>");
			sb.append("</tr></table><img src=L2UI.SquareGray width=270 height=1>");
			
			sb.append("<table width=270 height=24 bgcolor=000000><tr>");
			sb.append("<td width=80><font color=A3A3A3>Sell Quantity:</font></td><td width=190 align=left>" + (item.isStackable() ? "<edit var=quantity type=number width=180 height=12>" : "<font color=B09B79>1</font>") + "</td>");
			sb.append("</tr></table><img src=L2UI.SquareGray width=270 height=1>");
			
			sb.append("<table width=270 height=24 bgcolor=000000><tr>");
			sb.append("<td width=80><font color=A3A3A3>Currency:</font></td><td width=190 align=left><combobox width=180 height=21 var=\"currency\" list=TicktDonate;GoldBar;Tournament;Adena;></td>");
			sb.append("</tr></table><img src=L2UI.SquareGray width=270 height=1>");
			
			sb.append("<table width=270 height=24 bgcolor=000000><tr>");
			sb.append("<td width=80><font color=A3A3A3>Price" + (item.isStackable() ? " Each" : "") + ":</font></td><td width=190 align=left><edit var=price type=number width=180 height=12></td>");
			sb.append("</tr></table><img src=L2UI.SquareGray width=270 height=1>");
			
			sb.append("<img height=6><table width=270><tr>");
			sb.append("<td width=270 align=right><button value=\"Apply\" action=\"bypass _cbauction_mine sell " + (item.isStackable() ? "$quantity" : "1") + " $currency $price\" width=65 height=19 back=L2UI_ch3.smallbutton2_down fore=L2UI_ch3.smallbutton2></td>");
			sb.append("</tr></table>");
		}
		else
		{
			if (itemOID == -1)
			{
				sb.append("<table width=270><tr><td width=270 align=right valign=top><button action=\"bypass _cbauction_mine unselect\" width=14 height=14 back=L2UI_CH3.FrameCloseOnBtn fore=L2UI_CH3.FrameCloseBtn></td></tr></table>");
				sb.append("<img height=4><img src=L2UI.SquareGray width=270 height=1><table width=270 bgcolor=000000><tr><td width=36 height=40 align=center valign=top><img src=L2UI.SquareGray width=32 height=32></td>");
				sb.append("<td width=234 valign=top><font color=3A3A3A>N/A</font><br1><font color=LEVEL>Ctrl and Double Click an item on your inventory</font></td>");
				sb.append("</tr></table><img src=L2UI.SquareGray width=270 height=1>");
			}
			else
			{
				sb.append("<table width=270><tr><td width=270 align=right valign=top><button action=\"bypass _cbauction_mine toselect\" width=14 height=14 back=L2UI_CH3.ChatWnd.Chatting_Option2 fore=L2UI_CH3.ChatWnd.Chatting_Option1></td></tr></table>");
				sb.append("<img height=4><img src=L2UI.SquareGray width=270 height=1><table width=270 bgcolor=000000><tr><td width=36 height=40 align=center valign=top><img src=L2UI.SquareGray width=32 height=32></td>");
				sb.append("<td width=234 valign=top><font color=3A3A3A>N/A</font></td>");
				sb.append("</tr></table><img src=L2UI.SquareGray width=270 height=1>");
			}
		}
		
		return sb.toString();
	}
	
	public boolean selectItem(Player player, ItemInstance item)
	{
		final Function function = getFunction(player);
		if (function.getItemOID() != -1)
			return false;
		
		if (_auctions.values().stream().filter(a -> a.getPlayerOID() == player.getObjectId()).count() >= 9)
		{
			player.sendMessage("You have reach the limit of 9 items listed.");
			return true;
		}
		
		if (item.getItemId() == 57 || item.isQuestItem() || item.isHeroItem())
		{
			player.sendMessage(String.format("%s is not allowed on Auction House.", item.getName()));
			return true;
		}
		
		function.setItemOID(item.getObjectId());
		sendIndexMine(player, function);
		
		return true;
	}
	
	public void sellItem(Player player, int itemOID, int quantity, String currency, int price)
	{
		final ItemInstance item = player.getInventory().getItemByObjectId(itemOID);
		if (item == null)
		{
			player.sendMessage("This item is not longer on your inventory.");
			return;
		}
		
		if (quantity == 0 || item.getCount() < quantity)
		{
			player.sendMessage("Incorrect item quantity.");
			return;
		}
		
		if (price == 0 || (Integer.MAX_VALUE / item.getCount()) < price || (long) (price * item.getCount()) > Integer.MAX_VALUE)
		{
			player.sendMessage("Incorrect item price.");
			return;
		}
		
		int costId = 0;
		if (currency.equals("TicktDonate"))
		{
			costId = 9315;
		}
		if (currency.equals("Tournament"))
		{
			costId = 9314;
		}
		if (currency.equals("GoldBar"))
		{
			costId = 3470;
		}
		if (currency.equals("Adena"))
		{
			costId = 57;
		}
		
		switch (currency.toUpperCase())
		{
			case "TicktDonate":
				costId = 9315;
				break;
			
			case "Tournament":
				costId = 9314;
				break;
			
			case "GoldBar":
				costId = 3470;
				break;
			case "Adena":
				costId = 57;
				break;
		}
		if (!player.destroyItemByItemId("AuctionFee", 57, 15000, player, false))
		{
			player.sendMessage("You have not 15,000 adena to pay auction fee.");
			return;
		}
		
		if (!player.destroyItem("AuctionItem", itemOID, quantity, player, false))
		{
			player.sendMessage("This item is not longer on your inventory.");
			return;
		}
		
		final Auction auction = new Auction(player.getObjectId(), item.getItemId(), quantity, item.getEnchantLevel(), costId, price);
		_auctions.put(auction.getId(), auction);
		auction.store();
		
		player.sendMessage(String.format("You have successfully listed %s.", item.getName()));
		player.sendPacket(new ItemList(player, false));
	}
	
	@Override
	protected String getFolder()
	{
		return "auction/";
	}
	
	public Auction getAuction(int id)
	{
		return _auctions.get(id);
	}
	
	public Collection<Auction> getAuctions()
	{
		_auctions.values().removeIf(a -> a.getItemCount() == 0);
		return _auctions.values();
	}
	
	public void addAuction(Auction auction)
	{
		_auctions.put(auction.getId(), auction);
	}
	
	public int nextId()
	{
		return _auctions.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
	}
	
	public void load()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_AUCTION);
			ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
				addAuction(new Auction(rs));
		}
		catch (Exception e)
		{
			LOGGER.warn("Couldn't load bbs_auction items.", e);
		}
		LOGGER.info("Loaded {} auction house items.", _auctions.size());
	}
	
	public Function getFunction(Player player)
	{
		_functions.putIfAbsent(player.getObjectId(), new Function());
		return _functions.get(player.getObjectId());
	}
	
	public static AuctionBBSManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final AuctionBBSManager INSTANCE = new AuctionBBSManager();
	}
}