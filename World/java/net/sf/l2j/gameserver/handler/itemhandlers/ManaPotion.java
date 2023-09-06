package net.sf.l2j.gameserver.handler.itemhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.Servitor;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.CharInfo;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.UserInfo;

/**
 * @author BAN-L2JDEV
 */
public class ManaPotion implements IItemHandler
{
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
	
	private static final int[] ITEM_IDS =
	{
		728
	};
	
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (playable instanceof Servitor)
			return;
		Player activeChar = (Player) playable;
		
		long MaxMp = activeChar.getStatus().getMaxMp();
		long CurrentMp = (int) activeChar.getStatus().getMp();
		
		if (MaxMp == CurrentMp)
		{
			activeChar.sendMessage("Your MP is full.");
			return;
		}
		
		if (activeChar.isAfraid() || activeChar.isAlikeDead() || activeChar.isInOlympiadMode())
			return;
		
		int itemId = item.getItemId();
		if (itemId == 728)
		{
			if (!Config.INFINITY_MANAPOT)
			{
				activeChar.getInventory().destroyItemByItemId("ManaPot", itemId, 1, activeChar, null);
				activeChar.getInventory().updateDatabase();
				activeChar.sendPacket(new ItemList(activeChar, false));
			}
			
			activeChar.broadcastPacketInRadius(new MagicSkillUse(activeChar, activeChar, 2005, 1, 500, 1000), 1350);
			
			if (MaxMp - CurrentMp > Config.MAX_MP)
			{
				activeChar.getStatus().setMp(CurrentMp + Config.MAX_MP);
				activeChar.getStatus().broadcastStatusUpdate();
				CharInfo info1 = new CharInfo(activeChar);
				activeChar.broadcastPacket(info1);
				UserInfo info2 = new UserInfo(activeChar);
				activeChar.sendPacket(info2);
				activeChar.sendMessage("MP recovered by " + Config.MAX_MP + ".");
			}
			else
			{
				activeChar.getStatus().setMp(MaxMp);
				activeChar.getStatus().broadcastStatusUpdate();
				CharInfo info1 = new CharInfo(activeChar);
				activeChar.broadcastPacket(info1);
				UserInfo info2 = new UserInfo(activeChar);
				activeChar.sendPacket(info2);
				activeChar.sendMessage("MP recovered by " + (MaxMp - CurrentMp) + ".");
			}
		}
		
	}
}
