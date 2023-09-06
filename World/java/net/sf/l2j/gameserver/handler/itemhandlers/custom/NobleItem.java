package net.sf.l2j.gameserver.handler.itemhandlers.custom;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

public class NobleItem implements IItemHandler
{
	private static final int[] ITEM_IDS = new int[]
	{
		Config.NOBLES_ITEMID
	};
	
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		
		if (!(playable instanceof Player))
			return;
		
		Player activeChar = (Player) playable;
		if (activeChar.isNoble())
		{
			activeChar.sendMessage("You Are Already A Noblesse!");
			return;
		}
		
		activeChar.broadcastPacket(new SocialAction(activeChar, 16));
		activeChar.setNoble(true, true);
		activeChar.addItem("Quest", Config.NOBLES_REWARD_NOBLESS_TIARA, 1, activeChar, true);
		activeChar.sendMessage("You Are Now a Noble! Check your skills.");
		activeChar.broadcastUserInfo();
		activeChar.destroyItemByItemId("Consume", Config.NOBLES_ITEMID, 1, activeChar, true);
	}
	
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}
