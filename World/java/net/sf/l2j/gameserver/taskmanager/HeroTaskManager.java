package net.sf.l2j.gameserver.taskmanager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.gameserver.enums.SayType;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

/**
 * @author BAN L2JDEV
 */
public class HeroTaskManager implements Runnable
{
	private final Map<Player, Long> _players = new ConcurrentHashMap<>();
	
	protected HeroTaskManager()
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
	
	@Override
	public final void run()
	{
		if (_players.isEmpty())
			return;
		
		for (Map.Entry<Player, Long> entry : _players.entrySet())
		{
			final Player player = entry.getKey();
			
			if (player.getMemos().getLong("heroEndTime") < System.currentTimeMillis())
			{
				DisableHero(player);
				remove(player);
			}
		}
	}
	
	public static void ApllyHero(Player target, int time)
	{
		target.broadcastPacket(new SocialAction(target, 3));
		target.setHero(true);
		
		HeroTaskManager.getInstance().add(target);
		long remainingTime = target.getMemos().getLong("vipEndTime", 0);
		if (remainingTime > 0)
		{
			target.getMemos().set("heroEndTime", remainingTime + TimeUnit.DAYS.toMillis(time));
			target.sendPacket(new CreatureSay(0, SayType.HERO_VOICE, "Hero Manager", "Dear " + target.getName() + ", your Hero status has been extended by " + time + " day(s)."));
		}
		else
		{
			target.getMemos().set("heroEndTime", System.currentTimeMillis() + TimeUnit.DAYS.toMillis(time));
			target.sendPacket(new CreatureSay(0, SayType.HERO_VOICE, "Hero Manager", "Dear " + target.getName() + ", you got Hero Status for " + time + " day(s)."));
			
			target.broadcastUserInfo();
		}
	}
	
	public static void DisableHero(Player target)
	{
		
		HeroTaskManager.getInstance().remove(target);
		target.getMemos().set("heroEndTime", 0);
		target.setHero(false);
		
		target.sendPacket(new CreatureSay(0, SayType.HERO_VOICE, "Hero Manager", "Dear " + target.getName() + ", Your Hero period is over."));
		target.broadcastPacket(new SocialAction(target, 13));
		target.sendSkillList();
		
		// Unequip Hero items, if found.
		for (ItemInstance item : target.getInventory().getPaperdollItems())
		{
			if (item.isHeroItem() && (item.getItemId() >= 6611 && item.getItemId() <= 6621) && item.getItemId() == 6842)
				target.useEquippableItem(item, true);
			
		
		}
		
		// Check inventory and delete Hero items.
		for (ItemInstance item : target.getInventory().getAvailableItems(false, true, false))
		{
			if (item.isHeroItem() && (item.getItemId() >= 6611 && item.getItemId() <= 6621) && item.getItemId() == 6842)
				continue;
			
			target.destroyItem("Weapons", item, null, true);
			
		}
		
		target.broadcastUserInfo();
		
	}
	
	public static final HeroTaskManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final HeroTaskManager _instance = new HeroTaskManager();
	}
}
