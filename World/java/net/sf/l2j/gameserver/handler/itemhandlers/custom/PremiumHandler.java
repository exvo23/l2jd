package net.sf.l2j.gameserver.handler.itemhandlers.custom;

import java.util.concurrent.TimeUnit;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.enums.FloodProtector;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.taskmanager.PremiumTaskManager;

public class PremiumHandler implements IItemHandler
{
	private static final int ITEM_IDS[] =
	{
		Config.VIP_COIN_ID1,
		Config.VIP_COIN_ID2,
		Config.VIP_COIN_ID3
	};
	
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		Player activeChar = (Player) playable;
		
		if (!activeChar.getClient().performAction(FloodProtector.DRESSME))
		{
			activeChar.sendPacket(new ExShowScreenMessage(activeChar.getName() + " You must wait 5 seconds for the next use.", (int) TimeUnit.SECONDS.toMillis(2)));
			return;
		}
		int itemId = item.getItemId();
		
		if (itemId == Config.VIP_COIN_ID1)
		{
			if (activeChar.isInOlympiadMode())
			{
				activeChar.sendMessage("This item cannot be used on Olympiad Games.");
				return;
			}
			else if (activeChar.isPremium())
			{
				activeChar.sendMessage("You Are Premium!");
				return;
			}
			if (activeChar.destroyItemByItemId("Consume", Config.VIP_COIN_ID1, 1, activeChar, true))
			{
				
				PremiumTaskManager.ApllyPremium(activeChar, activeChar, Config.VIP_DAYS_ID1);
				activeChar.sendMessage("Congrats, you just became premium per " + Config.VIP_DAYS_ID1 + " day.");
				
				activeChar.broadcastUserInfo();
				activeChar.sendPacket(new EtcStatusUpdate(activeChar));
			}
		}
		
		if (itemId == Config.VIP_COIN_ID2)
		{
			if (activeChar.isInOlympiadMode())
			{
				activeChar.sendMessage("This item cannot be used on Olympiad Games.");
				return;
			}
			else if (activeChar.isPremium())
			{
				activeChar.sendMessage("You Are Premium!");
				return;
			}
			if (activeChar.destroyItemByItemId("Consume", Config.VIP_COIN_ID2, 1, activeChar, true))
			{
				PremiumTaskManager.ApllyPremium(activeChar, activeChar, Config.VIP_DAYS_ID2);
				activeChar.sendMessage("Congrats, you just became premium per " + Config.VIP_DAYS_ID2 + " day.");
				
				activeChar.broadcastUserInfo();
				activeChar.sendPacket(new EtcStatusUpdate(activeChar));
			}
		}
		
		if (itemId == Config.VIP_COIN_ID3)
		{
			if (activeChar.isInOlympiadMode())
			{
				activeChar.sendMessage("This item cannot be used on Olympiad Games.");
				return;
			}
			else if (activeChar.isPremium())
			{
				activeChar.sendMessage("You Are Premium!");
				return;
			}
			if (activeChar.destroyItemByItemId("Consume", Config.VIP_COIN_ID3, 1, activeChar, true))
			{
				
				PremiumTaskManager.ApllyPremium(activeChar, activeChar, Config.VIP_DAYS_ID3);
				activeChar.sendMessage("Congrats, you just became premium per " + Config.VIP_DAYS_ID3 + " day.");
				
				activeChar.broadcastUserInfo();
				activeChar.sendPacket(new EtcStatusUpdate(activeChar));
			}
		}
	}
	
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}
