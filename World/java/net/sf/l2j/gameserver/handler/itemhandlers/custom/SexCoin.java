package net.sf.l2j.gameserver.handler.itemhandlers.custom;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.gameserver.enums.actors.Sex;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * @author BAN - L2JDEV
 *
 */
public class SexCoin implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		final Player activeChar = (Player) playable;
		
		if (activeChar.destroyItemByItemId("Sex Change", item.getItemId(), 1, null, true))
		{
			Sex male = Sex.MALE;
			Sex female = Sex.FEMALE;
			
			if (activeChar.getAppearance().getSex() == male)
			{
				activeChar.getAppearance().setSex(female);
				activeChar.sendPacket(new ExShowScreenMessage("Congratulations. Your sex has been changed.", 6000));
				activeChar.broadcastUserInfo();
				activeChar.decayMe();
				activeChar.spawnMe();
			}
			else if (activeChar.getAppearance().getSex() == female)
			{
				activeChar.getAppearance().setSex(male);
				activeChar.sendPacket(new ExShowScreenMessage("Congratulations. Your sex has been changed.", 6000));
				activeChar.broadcastUserInfo();
				activeChar.decayMe();
				activeChar.spawnMe();
			}
			
			ThreadPool.schedule(new Runnable()
			{
				@Override
				public void run()
				{
					activeChar.logout(false);
				}
			}, 2500);
		}

	}
}
