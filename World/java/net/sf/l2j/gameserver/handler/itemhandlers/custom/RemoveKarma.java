package net.sf.l2j.gameserver.handler.itemhandlers.custom;

import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.UserInfo;

/**
 * @author BAN - L2JDEV
 */
public class RemoveKarma implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		Player activeChar = (Player) playable;
		
		if (activeChar.isAllSkillsDisabled())
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (activeChar.isInOlympiadMode())
		{
			activeChar.sendMessage("This item cannot be used on Olympiad Games.");
		}
		
		if (activeChar.getKarma() == 0)
		{
			activeChar.sendMessage("You do not have Karma to be removed.");
		}
		else
		{
			playable.destroyItem("Consume", item.getItemId(), 1, null, false);
			
			if (activeChar.getKarma() > 0)
				activeChar.setKarma(0);
			
			activeChar.sendMessage("Your Karma have been removed.");
			activeChar.sendPacket(new UserInfo(activeChar));
		}
	}
}