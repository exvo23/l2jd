package net.sf.l2j.gameserver.data.manager;

import java.util.logging.Logger;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;

/**
 * @author BAN - JDEV
 */
public class PCCafePoint implements Runnable
{
	Logger _log = Logger.getLogger(PCCafePoint.class.getName());
	private static PCCafePoint _instance;
	
	public static PCCafePoint getInstance()
	
	{
		if (_instance == null)
		{
			_instance = new PCCafePoint();
		}
		
		return _instance;
	}
	
	private PCCafePoint()
	{
		_log.info("Special PC Cafe: point event started.");
	}
	
	@Override
	public void run()
	{
		
		int score = 0;
		for (Player activeChar : World.getInstance().getAllPlayers().values())
		{
			
			if (activeChar.getStatus().getLevel() > Config.PCB_MIN_LEVEL)
			{
				
					score = Rnd.get(Config.PCB_POINT_MIN, Config.PCB_POINT_MAX);
					
					if (Rnd.get(100) <= Config.PCB_CHANCE_DUAL_POINT)
					{
						score *= 2;
						
						activeChar.addPcBangScore(score);
						
						activeChar.sendMessage("Your PC Bang Point had doubled 1 hour.");
						activeChar.updatePcBangWnd(score, true, true);
					}
					else
					{
						activeChar.addPcBangScore(score);
						activeChar.sendMessage("You recevied PC Bang Point 1 hour.");
						activeChar.updatePcBangWnd(score, true, false);
					}
				}
			
			
			activeChar = null;
		}
	}
}