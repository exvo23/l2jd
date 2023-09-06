package net.sf.l2j.gameserver.taskmanager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;

/**
 * @author BAN L2JDEV
 */

public class AutoGoldBar implements Runnable
{
	@Override
	public final void run()
	{
		if (_players.isEmpty())
			return;
		
		for (Map.Entry<Player, Long> entry : _players.entrySet())
		{
			final Player player = entry.getKey();
			
			if (player.getMemos().getLong("AutoGoldBar") < System.currentTimeMillis())
			{
				
				if (player.getInventory().getItemCount(57, 0) >= Config.BANKING_SYSTEM_ADENA)
				{
					player.getInventory().reduceAdena("Goldbar", Config.BANKING_SYSTEM_ADENA, player, null);
					player.getInventory().addItem("Goldbar", 3470, Config.BANKING_SYSTEM_GOLDBARS, player, null);
					player.getInventory().updateDatabase();
					player.sendPacket(new ItemList(player, false));
					
				}
				remove(player);
			}
			if (player.getInventory().getItemCount(57, 0) >= Config.BANKING_SYSTEM_ADENA)
			{
				player.getInventory().reduceAdena("Goldbar", Config.BANKING_SYSTEM_ADENA, player, null);
				player.getInventory().addItem("Goldbar", 3470, Config.BANKING_SYSTEM_GOLDBARS, player, null);
				player.getInventory().updateDatabase();
				player.sendPacket(new ItemList(player, false));
				
			}
		}
	}
	

	private final Map<Player, Long> _players = new ConcurrentHashMap<>();
	
	protected AutoGoldBar()
	{
		// Run task each 10 second.
		ThreadPool.scheduleAtFixedRate(this, 1000, 1000);
	}
	
	public final void add(Player player)
	{
		_players.put(player, System.currentTimeMillis());
	}
	
	public final void remove(Creature player)
	{
		_players.remove(player);
	}
	
	public static final AutoGoldBar getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final AutoGoldBar _instance = new AutoGoldBar();
	}
}
