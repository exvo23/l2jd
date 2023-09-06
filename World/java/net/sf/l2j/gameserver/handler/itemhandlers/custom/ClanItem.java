package net.sf.l2j.gameserver.handler.itemhandlers.custom;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;

public class ClanItem implements IItemHandler
{
	private static final int[] ITEM_IDS = new int[]
	{
		Config.CLAN_ITEMID
	};
	
	public static int reputation = Config.CLAN_REWARD_REPUTATION;
	public static int level = Config.CLAN_LEVEL;
	
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		Player activeChar = (Player) playable;
		
		if (activeChar.isClanLeader())
		{
			if (activeChar.getClan().getLevel() == 8)
			{
				activeChar.sendMessage("Your clan is already maximum level!");
				return;
			}
			
			activeChar.getClan().changeLevel(level);
			activeChar.getClan().addReputationScore(reputation);
			
			if (Config.CLAN_FULL_SKILL)
			{
				activeChar.getClan().addAllClanSkills();
				activeChar.sendMessage("You gave all available skills to " + activeChar.getClan().getName() + " clan.");
				
				activeChar.sendSkillList();
				activeChar.getClan().updateClanInDB();
			}
			activeChar.destroyItemByItemId("Consume", Config.CLAN_ITEMID, 1, activeChar, true);
			activeChar.broadcastUserInfo();
		}
		else
			activeChar.sendMessage("You are not the clan leader.");
		
		return;
	}
	
	public int[] getItemIds()
	{
		return ITEM_IDS;
	}
}
