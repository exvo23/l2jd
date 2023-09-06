package net.sf.l2j.gameserver.handler.itemhandlers.custom;

import java.util.concurrent.TimeUnit;

import net.sf.l2j.gameserver.data.sql.PlayerVariables;
import net.sf.l2j.gameserver.data.xml.DressmeArmorData;
import net.sf.l2j.gameserver.enums.FloodProtector;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.DressmeArmor;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;

/**
 * @author BAN L2JDEV
 */
public class ItemSkinFree implements IItemHandler
{

	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		final Player player = (Player) playable;
		
		if (!(playable instanceof Player))
			return;

		if (!player.getClient().performAction(FloodProtector.DRESSME))
		{
			player.sendPacket(new ExShowScreenMessage(player.getName() + " You must wait 5 seconds for the next use.", (int) TimeUnit.SECONDS.toMillis(2)));
			return;
		}
		if (player.isEquipe())
		{
			DressmeArmor dress = DressmeArmorData.getInstance().getItemId(0);
			
			player.setSkinArmor(dress);
			player.broadcastUserInfo();
			player.setEquipe(false);
		}
		else
		{
			DressmeArmor dress = DressmeArmorData.getInstance().getItemId(item.getItemId());
			
			player.setSkinArmor(dress);
			player.broadcastUserInfo();
			PlayerVariables.setVar(player, "skin", item.getItemId(), -1);
			player.setEquipe(true);
		}
		return;
		
	}
}