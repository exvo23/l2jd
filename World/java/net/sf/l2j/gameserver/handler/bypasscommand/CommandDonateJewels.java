package net.sf.l2j.gameserver.handler.bypasscommand;

import net.sf.l2j.gameserver.data.xml.DressmeArmorJewels;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.DressmeJewels;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.clientpackets.RequestBypassToServer;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;

/**
 * @author BAN L2JDEV
 * 
 */
public class CommandDonateJewels
{
	public static void AddJewels(Player player)
	{
		DressmeJewels dress = DressmeArmorJewels.getInstance().getItemId(RequestBypassToServer.getItemId());
		
		final ItemInstance item = player.addItem("AuctionPurchase", dress.getJewelsId(), 1, player, true);
		item.setEnchantLevel(dress.getEnchantLevel());
		player.useEquippableItem(item, true);
		InventoryUpdate playerIU = new InventoryUpdate();
		playerIU.addItem(item);
		player.sendPacket(playerIU);
	}
}
