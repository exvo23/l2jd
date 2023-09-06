package net.sf.l2j.gameserver.handler.itemhandlers.custom;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import net.sf.l2j.commons.mmocore.Maria;

import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.dungeon.DungeonManager;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;

/**
 * @author BAN - JDEV
 *
 */
public class DungeonReset implements IItemHandler
{
	Map<Integer, Long> commandUsages = new ConcurrentHashMap<>();
	long currentTime = System.currentTimeMillis();
	
	
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		final Player player = (Player) playable;

		
		if (commandUsages.getOrDefault(player.getObjectId(), 0L) > currentTime)
		{
			player.sendMessage("You cannot use it so often!");
			return;
		}
		
		player.destroyItem("dungeon reset", item.getItemId(), 1, null, true);
		DungeonManager.getInstance().getPlayerData().remove(player.getIP());
		Maria.set("DELETE FROM dungeon WHERE ipaddr=?", player.getIP());

		player.sendMessage("Your Dungeon status has been reset.");
		
		commandUsages.put(player.getObjectId(), currentTime + TimeUnit.MINUTES.toMillis(5L));
		
	}
}
