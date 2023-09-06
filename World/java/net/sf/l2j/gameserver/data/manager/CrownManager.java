package net.sf.l2j.gameserver.data.manager;

import java.util.logging.Logger;

import net.sf.l2j.gameserver.data.CrownTable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.pledge.Clan;
import net.sf.l2j.gameserver.model.pledge.ClanMember;

/**
 * @author BAN-L2JDEV
 */
public class CrownManager
{
	public static final Logger _log = Logger.getLogger(CrownManager.class.getName());
	private static CrownManager _instance;
	
	public static final CrownManager getInstance()
	{
		if (_instance == null)
		{
			_instance = new CrownManager();
		}
		return _instance;
	}
	
	public CrownManager()
	{
		_log.info("Crown Manager: initialized");
	}
	
	public void checkCrowns(final Clan clan)
	{
		if (clan == null)
			return;
		
		for (final ClanMember member : clan.getMembers())
		{
			if (member != null && member.isOnline() && member.getPlayerInstance() != null)
			{
				checkCrowns(member.getPlayerInstance());
			}
		}
	}
	
	public void checkCrowns(final Player activeChar)
	{
		if (activeChar == null)
			return;
		
		boolean isLeader = false;
		int crownId = -1;
		
		Clan activeCharClan = activeChar.getClan();
		ClanMember activeCharClanLeader;
		
		if (activeCharClan != null)
		{
			activeCharClanLeader = activeChar.getClan().getLeader();
		}
		else
		{
			activeCharClanLeader = null;
		}
		
		if (activeCharClan != null)
		{
			Castle activeCharCastle = CastleManager.getInstance().getCastleByOwner(activeCharClan);
			
			if (activeCharCastle != null)
			{
				crownId = CrownTable.getCrownId(activeCharCastle.getCastleId());
			}
			
			activeCharCastle = null;
			
			if (activeCharClanLeader != null && activeCharClanLeader.getObjectId() == activeChar.getObjectId())
			{
				isLeader = true;
			}
		}
		
		activeCharClan = null;
		activeCharClanLeader = null;
		
		if (crownId > 0)
		{
			if (isLeader && activeChar.getInventory().getItemByItemId(6841) == null)
			{
				activeChar.addItem("Crown", 6841, 1, activeChar, true);
				activeChar.getInventory().updateDatabase();
			}
			
			if (activeChar.getInventory().getItemByItemId(crownId) == null)
			{
				activeChar.addItem("Crown", crownId, 1, activeChar, true);
				activeChar.getInventory().updateDatabase();
			}
		}
		
		boolean alreadyFoundCirclet = false;
		boolean alreadyFoundCrown = false;
		
		for (final ItemInstance item : activeChar.getInventory().getItems())
		{
			if (CrownTable.getCrownList().contains(item.getItemId()))
			{
				if (crownId > 0)
				{
					if (item.getItemId() == crownId)
					{
						if (!alreadyFoundCirclet)
						{
							alreadyFoundCirclet = true;
							continue;
						}
					}
					else if (item.getItemId() == 6841 && isLeader)
					{
						if (!alreadyFoundCrown)
						{
							alreadyFoundCrown = true;
							continue;
						}
					}
				}
				
				activeChar.destroyItem("Removing Crown", item, activeChar, true);
				activeChar.getInventory().updateDatabase();
			}
		}
	}
}
