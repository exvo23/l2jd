package net.sf.l2j.gameserver.handler.itemhandlers.custom;

import java.util.concurrent.TimeUnit;

import net.sf.l2j.gameserver.enums.FloodProtector;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author BAN - JDEV
 *
 */
public class ClassItem implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		final Player activeChar = (Player) playable;

		if (!activeChar.getClient().performAction(FloodProtector.DRESSME))
		{
			activeChar.sendPacket(new ExShowScreenMessage(activeChar.getName() + " You must wait 5 seconds for the next use.", (int) TimeUnit.SECONDS.toMillis(2)));
			return;
		}

		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/ItemClassChanger/Warning.htm");
		activeChar.sendPacket(html);
		activeChar.sendPacket(ActionFailed.STATIC_PACKET);
	}
}
