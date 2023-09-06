package net.sf.l2j.gameserver.communitybbs.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.commons.logging.CLogger;
import net.sf.l2j.commons.pool.ConnectionPool;

import net.sf.l2j.gameserver.communitybbs.manager.custom.AuctionBBSManager;
import net.sf.l2j.gameserver.data.sql.PlayerInfoTable;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.enums.items.CrystalType;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Armor;
import net.sf.l2j.gameserver.model.item.kind.EtcItem;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.model.item.kind.Weapon;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;

/**
 * @author BAN - L2JDEV
 */
public class Auction
{
	protected static final CLogger LOGGER = new CLogger(Auction.class.getName());
	
	private static final String INSERT_AUCTION = "INSERT INTO bbs_auction (id,player_oid,item_id,item_count,item_enchant,price_id,price_count,duration) VALUES (?,?,?,?,?,?,?,?)";
	private static final String DELETE_AUCTION = "DELETE FROM bbs_auction WHERE id=?";
	private static final String UPDATE_AUCTION_ITEMCOUNT = "UPDATE bbs_auction SET item_count=? WHERE id=?";
	private static final String UPDATE_AUCTION_DURATION = "UPDATE bbs_auction SET duration=? WHERE id=?";
	
	private static final String SELECT_INVENTORY = "SELECT count FROM items WHERE owner_id=? AND item_id=?";
	private static final String UPDATE_INVENTORY = "UPDATE items SET count=? WHERE owner_id=? AND item_id=?";
	private static final String INSERT_INVENTORY = "INSERT INTO items VALUES (?,?,?,?,?,?,?,?,?,?,?)";
	
	private final int _id;
	private final int _playerOID;
	private final int _itemId;
	private final int _itemEnchant;
	
	private final int _priceId;
	private final int _priceCount;
	
	private int _itemCount;
	private long _duration;
	
	public Auction(ResultSet rs) throws SQLException
	{
		_id = rs.getInt("id");
		_playerOID = rs.getInt("player_oid");
		_itemId = rs.getInt("item_id");
		_itemCount = rs.getInt("item_count");
		_itemEnchant = rs.getInt("item_enchant");
		
		_priceId = rs.getInt("price_id");
		_priceCount = rs.getInt("price_count");
		_duration = rs.getLong("duration");
	}
	
	public Auction(int playerOID, int itemId, int itemCount, int itemEnchant, int priceId, int priceCount)
	{
		_id = AuctionBBSManager.getInstance().nextId();
		_playerOID = playerOID;
		_itemId = itemId;
		_itemCount = itemCount;
		_itemEnchant = itemEnchant;
		
		_priceId = priceId;
		_priceCount = priceCount;
		_duration = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7);
	}
	
	public final int getId()
	{
		return _id;
	}
	
	public final int getPlayerOID()
	{
		return _playerOID;
	}
	
	public final String getPlayerName()
	{
		return PlayerInfoTable.getInstance().getPlayerName(_playerOID);
	}
	
	public final int getItemId()
	{
		return _itemId;
	}
	
	public final String getItemName()
	{
		return ItemData.getInstance().getTemplate(_itemId).getName();
	}
	
	public final Item getItem()
	{
		return ItemData.getInstance().getTemplate(_itemId);
	}
	
	public final int getItemCount()
	{
		return _itemCount;
	}
	
	public final int getItemEnchant()
	{
		return _itemEnchant;
	}
	
	public final int getPriceId()
	{
		return _priceId;
	}
	
	public final Item getPrice()
	{
		return ItemData.getInstance().getTemplate(_priceId);
	}
	
	public final int getPriceCount()
	{
		return _priceCount;
	}
	
	public final long getDuration()
	{
		return _duration;
	}
	
	public final boolean hasExpire()
	{
		return (_duration - System.currentTimeMillis()) <= 0;
	}
	
	public final String getGradeIcon()
	{
		switch (getItem().getCrystalType())
		{
			case S:
				return "<img src=symbol.grade_s width=14 height=14>";
			case A:
				return "<img src=symbol.grade_a width=14 height=14>";
			case B:
				return "<img src=symbol.grade_b width=14 height=14>";
			case C:
				return "<img src=symbol.grade_c width=14 height=14>";
			case D:
				return "<img src=symbol.grade_d width=14 height=14>";
		}
		return "<img height=14>";
	}
	
	public final boolean filter(Function function)
	{
		if (_duration < System.currentTimeMillis())
			return false;
		
		final Item item = getItem();
		if (function.isItemType("Weapon") && !(item instanceof Weapon))
			return false;
		
		if (function.isItemType("Armor") && (!(item instanceof Armor) || item.isJewel()))
			return false;
		
		if (function.isItemType("Other") && (!(item instanceof EtcItem) || item.isShot() || item.isEnchantScroll()))
			return false;
		
		if (function.isItemType("Jewel") && !item.isJewel())
			return false;
		
		if (function.isItemType("Soulshot/Spiritshot") && !item.isShot())
			return false;
		
		if (function.isItemType("Enchant") && !item.isEnchantScroll())
			return false;
		
		if (!function.isItemGrade("All") && CrystalType.valueOf(function.getItemGrade()) != item.getCrystalType())
			return false;
		
		if (!function.getSearch().isBlank() && !StringUtil.matches(getItemName(), function.getSearch()))
			return false;
		
		if (!isCurrencry(function.getCurrency()))
			return false;
		
		return true;
	}
	
	public final boolean isCurrencry(String currency)
	{
		switch (currency.toUpperCase().replaceAll(" ", ""))
		{
			case "TickDonator":
				return _priceId == 9315;
			case "Tournament":
				return _priceId == 9314;
			case "GoldBar":
				return _priceId == 3470;
			case "Adena":
				return _priceId == 57;
		}
		return true;
	}
	
	public void updateDuration()
	{
		final Player player = World.getInstance().getPlayer(_playerOID);
		if (player == null)
			return;
		
		if ((_duration - System.currentTimeMillis()) > TimeUnit.DAYS.toDays(1))
		{
			player.sendMessage("This auction duration time has more than 24 hours left.");
			return;
		}
		
		if (!player.destroyItemByItemId("AuctionDuration", 57, 15000, player, true))
			return;
		
		_duration = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7);
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_AUCTION_DURATION))
		{
			ps.setLong(1, _duration);
			ps.setInt(2, _id);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.warn("Couldn't update auction house duration '{}'.", e, _id);
		}
		
		player.sendMessage(String.format("You have update duration util %s.", new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(_duration)));
	}
	
	public boolean tryPurchase(Player player, int count)
	{
		if (count == 0)
		{
			player.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
			return false;
		}
		
		if (count > _itemCount)
		{
			player.sendMessage(String.format("%s have not the item quantinty you asked.", getPlayerName()));
			return false;
		}
		
		final int totalCost = (count * _priceCount);
		if (!player.destroyItemByItemId("AuctionPurchase", _priceId, totalCost, player, true))
			return false;
		
		_itemCount -= count;
		
		player.sendMessage("You have successfully purchase auction item.");
		// player.addItem("AuctionPurchase", _itemId, count, _itemEnchant, player, true);
		final ItemInstance item = player.addItem("AuctionPurchase", _itemId, count, player, true);
		item.setEnchantLevel(_itemEnchant);
		InventoryUpdate playerIU = new InventoryUpdate();
		playerIU.addItem(item);
		player.sendPacket(playerIU);
		
		try (Connection con = ConnectionPool.getConnection())
		{
			final Player owner = World.getInstance().getPlayer(_playerOID);
			if (owner == null)
			{
				try (PreparedStatement ps = con.prepareStatement(SELECT_INVENTORY))
				{
					ps.setInt(1, _playerOID);
					ps.setInt(2, _priceId);
					try (ResultSet rs = ps.executeQuery())
					{
						if (rs.next() && getItem().isStackable())
						{
							try (PreparedStatement update = con.prepareStatement(UPDATE_INVENTORY))
							{
								update.setInt(1, rs.getInt("count") + totalCost);
								update.setInt(2, _playerOID);
								update.setInt(3, _priceId);
								update.execute();
							}
						}
						else
						{
							try (PreparedStatement insert = con.prepareStatement(INSERT_INVENTORY))
							{
								insert.setInt(1, _playerOID);
								insert.setInt(2, IdFactory.getInstance().getNextId());
								insert.setInt(3, _priceId);
								insert.setInt(4, totalCost);
								insert.setInt(5, 0);
								insert.setString(6, "INVENTORY");
								insert.setInt(7, 0);
								insert.setInt(8, 0);
								insert.setInt(9, 0);
								insert.setInt(10, -60);
								insert.setLong(11, System.currentTimeMillis());
								insert.execute();
								
							}
						}
					}
				}
			}
			else
			{
				owner.sendMessage("You have successfully sold auction item.");
				owner.addItem("AuctionPurchase", _priceId, totalCost, owner, true);
				
			}
			
			if (_itemCount > 0)
			{
				try (PreparedStatement ps = con.prepareStatement(UPDATE_AUCTION_ITEMCOUNT))
				{
					ps.setInt(1, _itemCount);
					ps.setInt(2, _id);
					ps.execute();
				}
			}
			else
			{
				try (PreparedStatement ps = con.prepareStatement(DELETE_AUCTION))
				{
					ps.setInt(1, _id);
					ps.execute();
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.warn("{} couldn't purchase auction house item '{}' (count: {}).", e, player.getName(), _id, count);
		}
		return true;
	}
	
	public void refund()
	{
		final Player player = World.getInstance().getPlayer(_playerOID);
		if (player == null)
			return;
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_AUCTION))
		{
			ps.setInt(1, _id);
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.warn("Couldn't refound auction house item '{}'.", e, _id);
			return;
		}
		
		player.sendMessage("You have removed auction house item.");
		// player.addItem("AuctionRefund", _itemId, _itemCount, _itemEnchant, player, true);
		final ItemInstance item = player.addItem("AuctionPurchase", _itemId, _itemCount, player, true);
		item.setEnchantLevel(_itemEnchant);
		InventoryUpdate playerIU = new InventoryUpdate();
		playerIU.addItem(item);
		player.sendPacket(playerIU);
		
		_itemCount = 0;
	}
	
	public void store()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(INSERT_AUCTION))
		{
			ps.setInt(1, _id);
			ps.setInt(2, _playerOID);
			ps.setInt(3, _itemId);
			ps.setInt(4, _itemCount);
			ps.setInt(5, _itemEnchant);
			ps.setInt(6, _priceId);
			ps.setInt(7, _priceCount);
			ps.setLong(8, _duration);
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.warn("Couldn't store auction house item '{}'.", e, _id);
		}
	}
}