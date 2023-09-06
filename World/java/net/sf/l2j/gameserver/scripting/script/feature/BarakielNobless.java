package net.sf.l2j.gameserver.scripting.script.feature;

import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.group.Party;
import net.sf.l2j.gameserver.scripting.Quest;

/**
 * @author BAN - L2JDev
 */
public class BarakielNobless extends Quest
{
	public BarakielNobless()
	{
		super(-1, "feature");
		
		addMyDying(25325);
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		final Player player = killer.getActingPlayer();
		if (player != null)
		{
			final Party party = player.getParty();
			if (party != null)
			{
				for (Player members : player.getParty().getMembers())
				{
					if (!members.isIn3DRadius(npc, 2000))
					{
						members.sendMessage("You were too far away from Barakiel. You've missed the chance of becoming Noblesse!");
						continue;
					}
					if (!members.isNoble())
					{
						members.setNoble(true, true);
						members.getInventory().addItem("Noblesse Tiara", 7694, 1, members, null);
						members.sendMessage("Congratulations! All party members have obtained Noblesse Status");
					}
					members.sendMessage("You are already Noblesse!");
					members.broadcastUserInfo();
				}
			}
			else
			
			if (!player.isIn3DRadius(npc, 2000))
			{
				player.sendMessage("You were too far away from Barakiel. You've missed the chance of becoming Noblesse!");
			}
			if (!player.isNoble())
			{
				player.setNoble(true, true);
				player.getInventory().addItem("Noblesse Tiara", 7694, 1, player, null);
				player.sendMessage("Congratulations! All party members have obtained Noblesse Status");
			}
			player.sendMessage("You are already Noblesse!");
			player.broadcastUserInfo();
			
		}
		super.onMyDying(npc, killer);
	}
}