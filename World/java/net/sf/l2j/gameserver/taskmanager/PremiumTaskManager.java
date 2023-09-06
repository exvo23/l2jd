package net.sf.l2j.gameserver.taskmanager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.gameserver.enums.SayType;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

/**
 * @author BAN L2JDEV
 */
public class PremiumTaskManager implements Runnable
{
	private final Map<Player, Long> _players = new ConcurrentHashMap<>();
	
	protected PremiumTaskManager()
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
			
			if (player.getMemos().getLong("vipEndTime") < System.currentTimeMillis())
			{
				DisablePremium(player, player);
				remove(player);
			}
		}
	}
	
	public static void ApllyPremium(Player target, Player player, int time)
	{
		target.broadcastPacket(new SocialAction(target, 3));
		target.setPremium(true);
		
		PremiumTaskManager.getInstance().add(target);
		long remainingTime = target.getMemos().getLong("vipEndTime", 0);
		if (remainingTime > 0)
		{
			target.getMemos().set("vipEndTime", remainingTime + TimeUnit.DAYS.toMillis(time));
			target.sendPacket(new CreatureSay(0, SayType.HERO_VOICE, "Premium Manager", "Dear " + player.getName() + ", your Premium status has been extended by " + time + " day(s)."));
		}
		else
		{
			target.getMemos().set("vipEndTime", System.currentTimeMillis() + TimeUnit.DAYS.toMillis(time));
			target.sendPacket(new CreatureSay(0, SayType.HERO_VOICE, "Premium Manager", "Dear " + player.getName() + ", you got Premium Status for " + time + " day(s)."));
			
			target.broadcastUserInfo();
		}
	}
	
	public static void DisablePremium(Player target, Player player)
	{
		PremiumTaskManager.getInstance().remove(target);
		target.getMemos().set("vipEndTime", 0);
		target.setPremium(false);
		
		target.sendPacket(new CreatureSay(0, SayType.HERO_VOICE, "Premium Manager", "Dear " + player.getName() + ", Your Premium period is over."));
		target.broadcastPacket(new SocialAction(target, 13));
		target.sendSkillList();
		target.broadcastUserInfo();
	}
	
	public static final PremiumTaskManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final PremiumTaskManager _instance = new PremiumTaskManager();
	}
}
