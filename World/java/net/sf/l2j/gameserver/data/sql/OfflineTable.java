package net.sf.l2j.gameserver.data.sql;

/**
 * @author BAN - JDEV
 *
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import net.sf.l2j.commons.logging.CLogger;
import net.sf.l2j.commons.pool.ConnectionPool;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.enums.actors.OperateType;
import net.sf.l2j.gameserver.enums.skills.AbnormalEffect;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.craft.ManufactureItem;
import net.sf.l2j.gameserver.model.trade.TradeItem;
import net.sf.l2j.gameserver.network.GameClient;

public class OfflineTable
{
	private static final CLogger LOGGER = new CLogger(OfflineTable.class.getName());
	
	private static final String SAVE_OFFLINE_STATUS = "INSERT INTO character_offline_trade (`charId`,`time`,`type`,`title`) VALUES (?,?,?,?)";
	private static final String SAVE_ITEMS = "INSERT INTO character_offline_trade_items (`charId`,`item`,`count`,`price`,`enchant`) VALUES (?,?,?,?,?)";
	private static final String CLEAR_OFFLINE_TABLE = "DELETE FROM character_offline_trade";
	private static final String CLEAR_OFFLINE_TABLE_ITEMS = "DELETE FROM character_offline_trade_items";
	private static final String LOAD_OFFLINE_STATUS = "SELECT * FROM character_offline_trade";
	private static final String LOAD_OFFLINE_ITEMS = "SELECT * FROM character_offline_trade_items WHERE charId = ?";
	
	private static final String EFFECT = Config.OFFLINE_EFFECT;
	private static final int NAME_COLOR = Config.OFFLINE_NAME_COLOR;
	
	private static final List<String> ALLOWED_EFFECTS = Arrays.asList("sleep", "muted", "root", "stun");
	
	public void store()
	{
		if (!Config.OFFLINE_RESTORE || (!Config.OFFLINE_TRADE_ENABLE && !Config.OFFLINE_CRAFT_ENABLE))
			return;
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement offline = con.prepareStatement(SAVE_OFFLINE_STATUS);
			PreparedStatement item = con.prepareStatement(SAVE_ITEMS))
		{
			try (Statement stm = con.createStatement())
			{
				stm.execute(CLEAR_OFFLINE_TABLE);
				stm.execute(CLEAR_OFFLINE_TABLE_ITEMS);
			}
			for (Player player : World.getInstance().getPlayers())
			{
				try
				{
					if (player.getOperateType() != OperateType.NONE && (player.getClient() == null || player.getClient().isDetached()))
					{
						offline.setInt(1, player.getObjectId());
						offline.setLong(2, player.getOfflineStartTime());
						offline.setInt(3, player.getOperateType().getId());
						switch (player.getOperateType())
						{
							case BUY:
								if (!Config.OFFLINE_TRADE_ENABLE)
									continue;
								
								offline.setString(4, player.getBuyList().getTitle());
								for (TradeItem i : player.getBuyList())
								{
									item.setInt(1, player.getObjectId());
									item.setInt(2, i.getItem().getItemId());
									item.setLong(3, i.getCount());
									item.setLong(4, i.getPrice());
									item.setLong(5, i.getEnchant());
									item.addBatch();
								}
								break;
							case SELL:
							case PACKAGE_SELL:
								if (!Config.OFFLINE_TRADE_ENABLE)
									continue;
								
								offline.setString(4, player.getSellList().getTitle());
								player.getSellList().updateItems();
								for (TradeItem i : player.getSellList())
								{
									item.setInt(1, player.getObjectId());
									item.setInt(2, i.getObjectId());
									item.setLong(3, i.getCount());
									item.setLong(4, i.getPrice());
									item.setLong(5, i.getEnchant());
									item.addBatch();
								}
								break;
							case MANUFACTURE:
								if (!Config.OFFLINE_CRAFT_ENABLE)
									continue;
								
								offline.setString(4, player.getManufactureList().getStoreName());
								for (final ManufactureItem i : player.getManufactureList())
								{
									item.setInt(1, player.getObjectId());
									item.setInt(2, i.getId());
									item.setLong(3, 0L);
									item.setLong(4, i.getValue());
									item.setLong(5, 0L);
									item.addBatch();
								}
								break;
						}
						item.executeBatch();
						offline.execute();
					}
				}
				catch (Exception e)
				{
					LOGGER.warn("Error while saving offline: " + player.getObjectId() + " " + e, e);
				}
			}
			
			LOGGER.info("Offline stored.");
		}
		catch (Exception e)
		{
			LOGGER.warn("Error while saving offline: " + e, e);
		}
	}
	
	public void restore()
	{
		
		if (!Config.OFFLINE_RESTORE || (!Config.OFFLINE_TRADE_ENABLE && !Config.OFFLINE_CRAFT_ENABLE))
			return;
		
		LOGGER.info("Loading offline...");
		
		int count = 0;
		
		try (Connection con = ConnectionPool.getConnection();
			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(LOAD_OFFLINE_STATUS))
		{
			
			while (rs.next())
			{
				final long time = rs.getLong("time");
				if (Config.OFFLINE_MAX_DAYS > 0 && isExpired(time))
					continue;
				
				final OperateType type = getType(rs.getInt("type"));
				if (type == null || type == OperateType.NONE)
					continue;
				
				final Player player = Player.restore(rs.getInt("charId"));
				if (player == null)
					continue;
				
				final GameClient client = new GameClient(null);
				client.spawnOffline(player);
				player.setOfflineStartTime(time);
				player.sitDown();
				
				final String title = rs.getString("title");
				
				try (PreparedStatement ps = con.prepareStatement(LOAD_OFFLINE_ITEMS))
				{
					ps.setInt(1, player.getObjectId());
					try (ResultSet item = ps.executeQuery())
					{
						switch (type)
						{
							case BUY:
								while (item.next())
								{
									if (player.getBuyList().addItemByItemId(item.getInt(2), item.getInt(3), item.getInt(4), item.getInt(5)) == null)
										throw new NullPointerException("NPE at BUY of offline" + player.getName() + "(" + player.getObjectId() + ") " + item.getInt(2) + " " + item.getInt(3) + " " + item.getInt(4));
								}
								
								player.getBuyList().setTitle(title);
								break;
							case SELL:
							case PACKAGE_SELL:
								while (item.next())
									if (player.getSellList().addItem(item.getInt(2), item.getInt(3), item.getInt(4)) == null)
										throw new NullPointerException("NPE at SELL of offline " + player.getObjectId() + " " + item.getInt(2) + " " + item.getInt(3) + " " + item.getInt(4));
									
								player.getSellList().setTitle(title);
								player.getSellList().setPackaged(type == OperateType.PACKAGE_SELL);
								break;
							case MANUFACTURE:
								while (item.next())
									player.getManufactureList().add(new ManufactureItem(item.getInt(2), item.getInt(4)));
								
								player.getManufactureList().setStoreName(title);
								break;
						}
					}
					
					applyEffect(player);
					player.setOperateType(type);
					player.restoreEffects();
					player.broadcastUserInfo();
					player.broadcastTitleInfo();
					
					count++;
				}
				catch (Exception e)
				{
					
					LOGGER.warn("Error loading offline {}({}).", e, player.getName(), player.getObjectId());
					player.logout(true);
				}
			}
			
			LOGGER.info("Loaded " + count + " offline.");
			
			try (Statement stm2 = con.createStatement())
			{
				stm2.execute(CLEAR_OFFLINE_TABLE);
				stm2.execute(CLEAR_OFFLINE_TABLE_ITEMS);
			}
		}
		catch (Exception e)
		{
			LOGGER.warn("Error while loading offline: ", e);
		}
	}
	
	protected OperateType getType(int id)
	{
		for (final OperateType type : OperateType.values())
			if (type.getId() == id)
				return type;
			
		LOGGER.warn("Wrong OperateType id '{}' not found.", id);
		return null;
	}
	
	protected boolean isExpired(long time)
	{
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		cal.add(Calendar.DAY_OF_YEAR, Config.OFFLINE_MAX_DAYS);
		return (cal.getTimeInMillis() <= System.currentTimeMillis());
	}
	
	public boolean canBeOffline(Player player)
	{
		if (player.isInOlympiadMode() || player.isFestivalParticipant() || player.isInJail() || player.getBoat() != null)
			return false;
		
		if (Config.OFFLINE_IN_PEACE_ZONE && !player.isInsideZone(ZoneId.PEACE))
			return false;
		
		switch (player.getOperateType())
		{
			case SELL:
			case PACKAGE_SELL:
			case BUY:
				return Config.OFFLINE_TRADE_ENABLE;
			case MANUFACTURE:
				return Config.OFFLINE_CRAFT_ENABLE;
		}
		
		return false;
	}
	
	public void applyEffect(Player player)
	{
		if (EFFECT != null)
		{
			if (EFFECT.equalsIgnoreCase("none"))
				return;
			
			if (!ALLOWED_EFFECTS.contains(EFFECT))
				return;
			
			player.startAbnormalEffect(AbnormalEffect.getByName(EFFECT.toLowerCase()).getMask());
		}
		
		if (NAME_COLOR > 0)
		{
			player.getAppearance().setNameColor(NAME_COLOR);
			player.broadcastUserInfo();
		}
		
	}
	
	public static OfflineTable getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final OfflineTable _instance = new OfflineTable();
	}
	
}
