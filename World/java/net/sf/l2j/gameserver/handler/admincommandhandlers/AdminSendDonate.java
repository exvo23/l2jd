package net.sf.l2j.gameserver.handler.admincommandhandlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.logging.Level;

import net.sf.l2j.commons.pool.ConnectionPool;

import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.enums.items.ItemLocation;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * @author BAN-L2JDEV
 */
public class AdminSendDonate implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_senddonate",
		"admin_givedonate"
	};
	
	@Override
	public void useAdminCommand(String command, Player activeChar)
	{
		if (command.equals("admin_senddonate"))
		{
			sendFile(activeChar, "sendDonate.htm");
		}
		else if (command.startsWith("admin_givedonate"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			
			String playername = "";
			Player player = null;
			
			if (st.countTokens() == 4)
			{
				playername = st.nextToken();
				player = World.getInstance().getPlayer(playername);
				String id = st.nextToken();
				int idval = Integer.parseInt(id);
				String num = st.nextToken();
				int numval = Integer.parseInt(num);
				String location = st.nextToken();
				
				// Can't use on yourself
				if (player != null && player.equals(activeChar))
				{
					activeChar.sendPacket(SystemMessageId.CANNOT_USE_ON_YOURSELF);
					return;
				}
				
				if (player != null)
					createItem(activeChar, player, idval, numval, getItemLocation(location));
				else
					giveItemToOfflinePlayer(activeChar, playername, idval, numval, getItemLocation(location));
				
			}
			else
				activeChar.sendMessage("Please fill in all the blanks before requesting a item creation.");
		}
	}
	
	private static void createItem(Player activeChar, Player player, int id, int count, ItemLocation location)
	{
		Item item = ItemData.getInstance().getTemplate(id);
		if (item == null)
		{
			activeChar.sendMessage("Unknown Item ID.");
			return;
		}
		
		if (count > 10 && !item.isStackable())
		{
			activeChar.sendMessage("You can't to create more than 10 non stackable items!");
			return;
		}
		
		if (location == ItemLocation.INVENTORY)
			player.getInventory().addItem("Admin", id, count, player, activeChar);
		else if (location == ItemLocation.WAREHOUSE)
			player.getWarehouse().addItem("Admin", id, count, player, activeChar);
		
		if (activeChar != player)
		{
			if (count > 1)
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_PICKED_UP_S2_S1).addItemName(id).addNumber(count));
			else
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_PICKED_UP_S1).addItemName(id));
		}
		
		activeChar.sendMessage("Spawned " + count + " " + item.getName() + " in " + player.getName() + " " + (location == ItemLocation.INVENTORY ? "inventory" : "warehouse") + ".");
	}
	
	@SuppressWarnings("resource")
	public static void giveItemToOfflinePlayer(Player activeChar, String playername, int id, int count, ItemLocation location)
	{
		Item item = ItemData.getInstance().getTemplate(id);
		int objectId = IdFactory.getInstance().getNextId();
		
		try (Connection con = ConnectionPool.getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT obj_Id FROM characters WHERE char_name=?");
			statement.setString(1, playername);
			ResultSet result = statement.executeQuery();
			int objId = 0;
			
			if (result.next())
			{
				objId = result.getInt(1);
			}
			
			result.close();
			statement.close();
			
			if (objId == 0)
			{
				activeChar.sendMessage("Char \"" + playername + "\" does not exists!");
				con.close();
				return;
			}
			
			if (item == null)
			{
				activeChar.sendMessage("Unknown Item ID.");
				return;
			}
			
			if (count > 1 && !item.isStackable())
			{
				activeChar.sendMessage("You can't to create more than 1 non stackable items!");
				return;
			}
			
			statement = con.prepareStatement("INSERT INTO items (owner_id,item_id,count,loc,loc_data,enchant_level,object_id,custom_type1,custom_type2,mana_left,time) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
			statement.setInt(1, objId);
			statement.setInt(2, item.getItemId());
			statement.setInt(3, count);
			statement.setString(4, location.name());
			statement.setInt(5, 0);
			statement.setInt(6, 0);
			statement.setInt(7, objectId);
			statement.setInt(8, 0);
			statement.setInt(9, 0);
			statement.setInt(10, -1);
			statement.setLong(11, 0);
			
			statement.executeUpdate();
			statement.close();
			
			activeChar.sendMessage("Created " + count + " " + item.getName() + " in " + playername + " " + (location == ItemLocation.INVENTORY ? "inventory" : "warehouse") + ".");
			LOGGER.info("Insert item: (" + objId + ", " + item.getName() + ", " + count + ", " + objectId + ")");
		}
		catch (SQLException e)
		{
			LOGGER.info(Level.SEVERE, "Could not insert item " + item.getName() + " into DB: Reason: " + e.getMessage(), e);
		}
	}
	
	private static ItemLocation getItemLocation(String name)
	{
		ItemLocation location = null;
		if (name.equalsIgnoreCase("inventory"))
			location = ItemLocation.INVENTORY;
		else if (name.equalsIgnoreCase("warehouse"))
			location = ItemLocation.WAREHOUSE;
		return location;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}