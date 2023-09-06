package net.sf.l2j.gameserver.taskmanager;

import java.util.Vector;

import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.skills.L2Skill;

/**
 * @author BAN - L2JDev
 */
public class CustomCancelTaskManager implements Runnable
{
	private Creature player = null;
	private Vector<L2Skill> buffsCanceled = null;
	
	public CustomCancelTaskManager(Creature p, Vector<L2Skill> skill)
	{
		player = p;
		buffsCanceled = skill;
	}
	
	@Override
	public void run()
	{
		if (player == null)
			return;
		
		for (L2Skill skill : buffsCanceled)
		{
			if (skill == null)
				continue;
			skill.getEffects(player, player);
			
		}
		
	}
	
}
