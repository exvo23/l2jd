package net.sf.l2j.gameserver.data;

import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;

/**
 * @author BAN - L2JDEV
 */
public class AnnouncerHero
{
	public static void notifyHeroEnter(Player activeChar)
	{
		if (activeChar.isHero() && (!activeChar.isGM()))
			World.announceToOnlinePlayers("Hero " + activeChar.getName() + " is Now Online!", true);
	}
}
