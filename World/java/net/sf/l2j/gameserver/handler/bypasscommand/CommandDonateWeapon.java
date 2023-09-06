package net.sf.l2j.gameserver.handler.bypasscommand;

import net.sf.l2j.gameserver.data.xml.DressmeWeaponData;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.DressmeWeapon;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.clientpackets.RequestBypassToServer;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;

/**
 * @author BAN L2JDEV
 * 
 */
public class CommandDonateWeapon
{
	public static void AddItem(Player player)
	{
		DressmeWeapon dress = DressmeWeaponData.getInstance().getItemId(RequestBypassToServer.getItemId());
		
		final ItemInstance item = player.addItem("AuctionPurchase", dress.getChestId(), 1, player, true);
		item.setEnchantLevel(dress.getEnchantLevel());
		player.useEquippableItem(item, true);
		InventoryUpdate playerIU = new InventoryUpdate();
		playerIU.addItem(item);
		player.sendPacket(playerIU);
	
	}
}
