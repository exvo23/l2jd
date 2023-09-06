package net.sf.l2j.gameserver.handler.bypasscommand;

import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;

/**
 * @author BAN - JDEV
 *
 */
public class CommandCastleAnnouncer
{
	public static void notifyCastleOwner(Player activeChar)
	{
		
		if (activeChar.isCastleLord(1) && (!activeChar.isGM()))
			World.announceToOnlinePlayers("Lord " + activeChar.getName() + " Ruler Of Gludio Castle is Now Online!", true);
		
		else if (activeChar.isCastleLord(2) && (!activeChar.isGM()))
			World.announceToOnlinePlayers("Lord " + activeChar.getName() + " Ruler Of Dion Castle is Now Online!", true);
		
		else if (activeChar.isCastleLord(3) && (!activeChar.isGM()))
			World.announceToOnlinePlayers("Lord " + activeChar.getName() + " Ruler Of Giran Castle is Now Online!", true);
		
		else if (activeChar.isCastleLord(4) && (!activeChar.isGM()))
			World.announceToOnlinePlayers("Lord " + activeChar.getName() + " Ruler Of Oren Castle is Now Online!", true);
		
		else if (activeChar.isCastleLord(5) && (!activeChar.isGM()))
			World.announceToOnlinePlayers("Lord " + activeChar.getName() + " Ruler Of Aden Castle is Now Online!", true);
		
		else if (activeChar.isCastleLord(6) && (!activeChar.isGM()))
			World.announceToOnlinePlayers("Lord " + activeChar.getName() + " Ruler Of Innadril Castle is Now Online!", true);
		
		else if (activeChar.isCastleLord(7) && (!activeChar.isGM()))
			World.announceToOnlinePlayers("Lord " + activeChar.getName() + " Ruler Of Goddard Castle is Now Online!", true);
		
		else if (activeChar.isCastleLord(8) && (!activeChar.isGM()))
			World.announceToOnlinePlayers("Lord " + activeChar.getName() + " Ruler Of Rune Castle is Now Online!", true);
		
		else if (activeChar.isCastleLord(9) && (!activeChar.isGM()))
			World.announceToOnlinePlayers("Lord " + activeChar.getName() + " Ruler Of Schuttgart Castle is Now Online!", true);
	}
}
