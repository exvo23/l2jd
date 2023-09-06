package net.sf.l2j.gameserver.handler.bypasscommand;

import net.sf.l2j.gameserver.data.xml.DressmeArmorData;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.DressmeArmor;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.clientpackets.RequestBypassToServer;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;

/**
 * @author BAN L2JDEV
 * 
 */
public class CommandDonateArmorPart
{
	public static void AddItem1(Player player)
	{
		DressmeArmor dress = DressmeArmorData.getInstance().getItemId(RequestBypassToServer.getItemId());
		
		final ItemInstance item = player.addItem("AuctionPurchase", dress.getHairId(), 1, player, true);
		item.setEnchantLevel(dress.getEnchantLevel());
		player.useEquippableItem(item, true);
		InventoryUpdate playerIU = new InventoryUpdate();
		playerIU.addItem(item);
		player.sendPacket(playerIU);
		
		final ItemInstance item1 = player.addItem("AuctionPurchase", dress.getChestId(), 1, player, true);
		item1.setEnchantLevel(dress.getEnchantLevel());
		player.useEquippableItem(item1, true);
		InventoryUpdate playerIU1 = new InventoryUpdate();
		playerIU1.addItem(item1);
		player.sendPacket(playerIU1);
		
		final ItemInstance item111 = player.addItem("AuctionPurchase", dress.getGlovesId(), 1, player, true);
		item111.setEnchantLevel(dress.getEnchantLevel());
		player.useEquippableItem(item111, true);
		InventoryUpdate playerIU111 = new InventoryUpdate();
		playerIU111.addItem(item111);
		player.sendPacket(playerIU111);
		
		final ItemInstance item1111 = player.addItem("AuctionPurchase", dress.getFeetId(), 1, player, true);
		item1111.setEnchantLevel(dress.getEnchantLevel());
		player.useEquippableItem(item1111, true);
		InventoryUpdate playerIU1111 = new InventoryUpdate();
		playerIU1111.addItem(item1111);
		player.sendPacket(playerIU1111);
	}
}
