package net.sf.l2j.gameserver.handler.admincommandhandlers;

import net.sf.l2j.gameserver.data.xml.IconTable;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author BAN {@Link} L2JDEV
 */
public class AdminInventory implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_show_inventory",
		"admin_delete_item"
	};
	
	@Override
	public void useAdminCommand(String command, Player activeChar)
	{
		if ((activeChar.getTarget() == null))
		{
			activeChar.sendMessage("Select a target");
			return;
		}
		
		if (!(activeChar.getTarget() instanceof Player))
		{
			activeChar.sendMessage("Target need to be player");
			return;
		}
		
		Player player = activeChar.getTarget().getActingPlayer();
		
		if (command.startsWith(ADMIN_COMMANDS[0]))
		{
			if (command.length() > ADMIN_COMMANDS[0].length())
			{
				String com = command.substring(ADMIN_COMMANDS[0].length() + 1);
				if (isDigit(com))
				{
					showItemsPage(activeChar, Integer.parseInt(com));
				}
			}
			
			else
			{
				showItemsPage(activeChar, 0);
			}
		}
		else if (command.contains(ADMIN_COMMANDS[1]))
		{
			String val = command.substring(ADMIN_COMMANDS[1].length() + 1);
			
			player.destroyItem("GM Destroy", Integer.parseInt(val), player.getInventory().getItemByObjectId(Integer.parseInt(val)).getCount(), null, true);
			showItemsPage(activeChar, 0);
		}
		
		return;
	}
	
	private static void showItemsPage(Player activeChar, int page)
	{
		
		final Player target = activeChar.getTarget().getActingPlayer();
		
		final ItemInstance[] items = target.getInventory().getAvailableItems(false, true, false);
		
		int maxItemsPerPage = 10;
		
		int maxPages = items.length / maxItemsPerPage;
		
		if (items.length > (maxItemsPerPage * maxPages))
		{
			maxPages++;
		}
		
		if (page > maxPages)
		{
			page = maxPages;
		}
		
		int itemsStart = maxItemsPerPage * page;
		
		int itemsEnd = items.length;
		
		if ((itemsEnd - itemsStart) > maxItemsPerPage)
		{
			itemsEnd = itemsStart + maxItemsPerPage;
		}
		
		NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
		adminReply.setFile("data/html/admin/inventory.htm");
		adminReply.replace("%PLAYER_NAME%", target.getName());
		StringBuilder sbPages = new StringBuilder();
		
		for (int x = 0; x < maxPages; x++)
		{
			int pagenr = x + 1;
			sbPages.append("<td><button value=\"" + String.valueOf(pagenr) + "\" action=\"bypass -h admin_show_inventory " + String.valueOf(x) + "\" width=16 height=16 back=\"sek.cbui67\" fore=\"sek.cbui67\"></td>");
		}
		adminReply.replace("%PAGES%", sbPages.toString());
		StringBuilder sbItems = new StringBuilder();
		for (int i = itemsStart; i < itemsEnd; i++)
		{
			
			sbItems.append("<tr><td><img src=\"" + IconTable.getIcon(items[i].getItemId()) + "\" width=32 height=32></td>");
			if(items[i].isEquipped())
			sbItems.append("<td width=60><font color=\"00FF00\">" + items[i].getName() + "</font></td>");
			else
				sbItems.append("<td width=60>" + items[i].getName() + "</td>");
			
			sbItems.append("<td><button action=\"bypass -h admin_delete_item " + String.valueOf(items[i].getObjectId()) + "\" width=14 height=14 back=\"Icons.Button_DF_Delete_Down\" fore=\"Icons.Button_DF_Delete_Over\">" + "</td></tr>");
		}
		adminReply.replace("%ITEMS%", sbItems.toString());
		activeChar.sendPacket(adminReply);
	}
	
	public static boolean isDigit(String text)
	{
		if (text == null)
			return false;
		
		return text.matches("[0-9]+");
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
