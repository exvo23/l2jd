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
import net.sf.l2j.gameserver.taskmanager.HeroTaskManager;

/**
 * @author BAN L2JDEV
 */
public class HeroHandler implements IItemHandler
{
	
	private static final int ITEM_IDS[] =
	{
		Config.HERO_ITEM_ID_7DAYS,
		Config.HERO_ITEM_ID_30DAYS,
		Config.HERO_ITEM_ID_30DAYS
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
		
		if (itemId == Config.HERO_ITEM_ID_7DAYS)
		{
			if (activeChar.isInOlympiadMode())
			{
				activeChar.sendMessage("This item cannot be used on Olympiad Games.");
				return;
			}
			else if (activeChar.isHero())
			{
				activeChar.sendMessage("You Are Hero!");
				return;
			}
			if (activeChar.destroyItemByItemId("Consume", Config.HERO_ITEM_ID_7DAYS, 1, activeChar, true))
			{
				
				HeroTaskManager.ApllyHero(activeChar, Config.HERO_7DAYS);
				activeChar.sendMessage("Congrats, you just became hero per " + Config.HERO_7DAYS + " day.");
				
				activeChar.broadcastUserInfo();
				activeChar.sendPacket(new EtcStatusUpdate(activeChar));
	
			}
		}
		
		if (itemId == Config.HERO_ITEM_ID_30DAYS)
		{
			if (activeChar.isInOlympiadMode())
			{
				activeChar.sendMessage("This item cannot be used on Olympiad Games.");
				return;
			}
			else if (activeChar.isHero())
			{
				activeChar.sendMessage("You Are Hero!");
				return;
			}
			if (activeChar.destroyItemByItemId("Consume", Config.HERO_ITEM_ID_30DAYS, 1, activeChar, true))
			{
				HeroTaskManager.ApllyHero(activeChar, Config.HERO_30DAYS);
				activeChar.sendMessage("Congrats, you just became hero per " + Config.HERO_30DAYS + " day.");
				
				activeChar.broadcastUserInfo();
				activeChar.sendPacket(new EtcStatusUpdate(activeChar));

			}
			
		}
		
		if (itemId == Config.HERO_ITEM_ID_90DAYS)
		{
			if (activeChar.isInOlympiadMode())
			{
				activeChar.sendMessage("This item cannot be used on Olympiad Games.");
				return;
			}
			else if (activeChar.isHero())
			{
				activeChar.sendMessage("You Are Hero!");
				return;
			}
			if (activeChar.destroyItemByItemId("Consume", Config.HERO_ITEM_ID_90DAYS, 1, activeChar, true))
			{
				
				HeroTaskManager.ApllyHero(activeChar, Config.HERO_90DAYS);
				activeChar.sendMessage("Congrats, you just became hero per " + Config.HERO_90DAYS + " day.");
				
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
