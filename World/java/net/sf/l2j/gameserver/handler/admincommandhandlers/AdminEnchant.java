package net.sf.l2j.gameserver.handler.admincommandhandlers;

import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.data.xml.ArmorSetData;
import net.sf.l2j.gameserver.enums.Paperdoll;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.WorldObject;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.ArmorSet;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Armor;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.model.item.kind.Weapon;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.skills.L2Skill;

public class AdminEnchant implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_seteh", // 6
		"admin_setec", // 10
		"admin_seteg", // 9
		"admin_setel", // 11
		"admin_seteb", // 12
		"admin_setew", // 7
		"admin_setes", // 8
		"admin_setle", // 1
		"admin_setre", // 2
		"admin_setlf", // 4
		"admin_setrf", // 5
		"admin_seten", // 3
		"admin_setun", // 0
		"admin_setba", // 13
		"admin_enchant"
	};
	
	@Override
	public void useAdminCommand(String command, Player activeChar)
	{
		if (command.equals("admin_enchant"))
			showMainPage(activeChar);
		else
		{
			int armorType = -1;
			
			if (command.startsWith("admin_seteh"))
				armorType = Item.SLOT_HEAD;
			else if (command.startsWith("admin_setec"))
				armorType = Item.SLOT_CHEST;
			else if (command.startsWith("admin_seteg"))
				armorType = Item.SLOT_GLOVES;
			else if (command.startsWith("admin_seteb"))
				armorType = Item.SLOT_FEET;
			else if (command.startsWith("admin_setel"))
				armorType = Item.SLOT_LEGS;
			else if (command.startsWith("admin_setew"))
				armorType = Item.SLOT_R_HAND;
			else if (command.startsWith("admin_setes"))
				armorType = Item.SLOT_L_HAND;
			else if (command.startsWith("admin_setle"))
				armorType = Item.SLOT_L_EAR;
			else if (command.startsWith("admin_setre"))
				armorType = Item.SLOT_R_EAR;
			else if (command.startsWith("admin_setlf"))
				armorType = Item.SLOT_L_FINGER;
			else if (command.startsWith("admin_setrf"))
				armorType = Item.SLOT_R_FINGER;
			else if (command.startsWith("admin_seten"))
				armorType = Item.SLOT_NECK;
			else if (command.startsWith("admin_setun"))
				armorType = Item.SLOT_UNDERWEAR;
			else if (command.startsWith("admin_setba"))
				armorType = Item.SLOT_BACK;
			
			if (armorType != -1)
			{
				try
				{
					int ench = Integer.parseInt(command.substring(12));
					
					// check value
					if (ench < 0 || ench > 65535)
						activeChar.sendMessage("You must set the enchant level to be between 0-65535.");
					else
						setEnchant(activeChar, ench, armorType);
				}
				catch (Exception e)
				{
					activeChar.sendMessage("Please specify a new enchant value.");
				}
			}
			
			// show the enchant menu after an action
			showMainPage(activeChar);
		}
		
		return;
	}
	
	/**
	 * @param activeChar
	 * @param enchant
	 * @param armorType
	 */
	private static void setEnchant(Player activeChar, int enchant, int armorType)
	{
		WorldObject target = activeChar.getTarget();
		if (!(target instanceof Player))
			target = activeChar;
		
		final Player player = (Player) target;
		
		final ItemInstance item = player.getInventory().getItemFrom(armorType);
		if (item == null)
		{
			activeChar.sendMessage(player.getName() + " doesn't wear any item in " + armorType + " slot.");
			return;
		}
		
		final Item it = item.getItem();
		final int oldEnchant = item.getEnchantLevel();
		
		// Do nothing if both values are the same.
		if (oldEnchant == enchant)
		{
			activeChar.sendMessage(player.getName() + "'s " + it.getName() + " enchant is already set to " + enchant + ".");
			return;
		}
		
		item.setEnchantLevel(enchant);
		item.updateDatabase();
		
		// If item is equipped, verify the skill obtention/drop (+4 duals, +6 armorset).
		if (item.isEquipped())
		{
			final int currentEnchant = item.getEnchantLevel();
			
			// Skill bestowed by +4 duals.
			if (it instanceof Weapon)
			{
				// Old enchant was >= 4 and new is lower : we drop the skill.
				if (oldEnchant >= 4 && currentEnchant < 4)
				{
					final L2Skill enchant4Skill = ((Weapon) it).getEnchant4Skill();
					if (enchant4Skill != null)
					{
						player.removeSkill(enchant4Skill.getId(), false);
						player.sendSkillList();
					}
				}
				// Old enchant was < 4 and new is 4 or more : we add the skill.
				else if (oldEnchant < 4 && currentEnchant >= 4)
				{
					final L2Skill enchant4Skill = ((Weapon) it).getEnchant4Skill();
					if (enchant4Skill != null)
					{
						player.addSkill(enchant4Skill, false);
						player.sendSkillList();
					}
				}
			}
			// Add skill bestowed by +6 armorset.
			else if (it instanceof Armor)
			{
				// Old enchant was >= 6 and new is lower : we drop the skill.
				if (oldEnchant >= 6 && currentEnchant < 6)
				{
					// Check if player is wearing a chest item.
					final int itemId = player.getInventory().getItemIdFrom(Paperdoll.CHEST);
					if (itemId > 0)
					{
						final ArmorSet armorSet = ArmorSetData.getInstance().getSet(itemId);
						if (armorSet != null)
						{
							final int skillId = armorSet.getEnchant6skillId();
							if (skillId > 0)
							{
								player.removeSkill(skillId, false);
								player.sendSkillList();
							}
						}
					}
				}
				// Old enchant was < 6 and new is 6 or more : we add the skill.
				else if (oldEnchant < 6 && currentEnchant >= 6)
				{
					// Check if player is wearing a chest item.
					final int itemId = player.getInventory().getItemIdFrom(Paperdoll.CHEST);
					if (itemId > 0)
					{
						final ArmorSet armorSet = ArmorSetData.getInstance().getSet(itemId);
						if (armorSet != null && armorSet.isEnchanted6(player)) // has all parts of set enchanted to 6 or more
						{
							final int skillId = armorSet.getEnchant6skillId();
							if (skillId > 0)
							{
								final L2Skill skill = SkillTable.getInstance().getInfo(skillId, 1);
								if (skill != null)
								{
									player.addSkill(skill, false);
									player.sendSkillList();
								}
							}
						}
					}
				}
			}
		}
		
		final InventoryUpdate iu = new InventoryUpdate();
		iu.addModifiedItem(item);
		player.sendPacket(iu);
		
		player.broadcastUserInfo();
		
		activeChar.sendMessage(player.getName() + "'s " + it.getName() + " enchant was modified from " + oldEnchant + " to " + enchant + ".");
	}
	
	private void showMainPage(Player activeChar)
	{
		sendFile(activeChar, "enchant.htm");
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
