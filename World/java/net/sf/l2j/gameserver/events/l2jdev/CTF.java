package net.sf.l2j.gameserver.events.l2jdev;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.enums.Paperdoll;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.model.spawn.Spawn;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

import javolution.text.TextBuilder;

public class CTF extends Event
{
	protected EventState eventState;
	protected Player playerWithRedFlag;
	protected Player playerWithBlueFlag;
	private Core task = new Core();
	private Spawn redFlagNpc;
	private Spawn blueFlagNpc;
	private Spawn redHolderNpc;
	private Spawn blueHolderNpc;
	private int redFlagStatus;
	private int blueFlagStatus;
	
	private enum EventState
	{
		START,
		FIGHT,
		END,
		TELEPORT,
		INACTIVE
	}
	
	protected class Core implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				switch (eventState)
				{
					case START:
						divideIntoTeams(2);
						preparePlayers();
						teleportToTeamPos();

						forceSitAll();
						spawnFlagsAndHolders();
						setStatus(EventState.FIGHT);
						schedule(20000);
						break;
					
					case FIGHT:
						forceStandAll();
						sendMsg();
						setStatus(EventState.END);
						clock.startClock(getInt("matchTime"));
						break;
					
					case END:
						clock.setTime(0);
						
						if (winnerTeam == 0)
							winnerTeam = getWinnerTeam();
				
						unspawnFlagsAndHolders();
						if (playerWithRedFlag != null)
							unequipFlag(playerWithRedFlag);
						if (playerWithBlueFlag != null)
							unequipFlag(playerWithBlueFlag);
						setStatus(EventState.INACTIVE);
						
						
						
						if (winnerTeam == 0)
						{
							EventManager.getInstance().end("The event ended in a tie! both teams had " + teams.get(1).getScore() + " flags taken!");
							for (Player player : getPlayerList())
							{
								giveReward(player, getInt("rewardId"), getInt("rewardAmmount") / 2);
							}
						}
						else
						{
							giveReward(getPlayersOfTeam(winnerTeam), getInt("rewardId"), getInt("rewardAmmount"));
							EventManager.getInstance().end("Congratulation! The " + teams.get(winnerTeam).getName() + " team won the event with " + teams.get(winnerTeam).getScore() + " flags taken!");
						}
						break;
				}
			}
			catch (Throwable e)
			{
				e.printStackTrace();
				EventManager.getInstance().end("Error! Event ended.");
			}
		}
	}
	
	public CTF()
	{
		super();
		eventId = 3;
		createNewTeam(1, "Blue", getColor("Blue"), getPosition("Blue", 1));
		createNewTeam(2, "Red", getColor("Red"), getPosition("Red", 1));
	}
	
	@Override
	protected void endEvent()
	{
		winnerTeam = players.head().getNext().getValue()[0];
		
		setStatus(EventState.END);
		clock.setTime(0);
	}
	
	private void equipFlag(Player player, int flag)
	{
		// un-equip - destroy flag
		if (player.getInventory().unequipItemInBodySlotAndRecord(Item.SLOT_LR_HAND) != null)
		{
			if (player.getInventory().hasItemIn(Paperdoll.RHAND))
				player.getInventory().unequipItemInBodySlotAndRecord(Item.SLOT_R_HAND);
		}
		else
		{
			// player.getInventory().unequipItemInBodySlotAndRecord(Item.SLOT_LR_HAND);
			if (player.getInventory().hasItemIn(Paperdoll.LHAND))
				player.getInventory().unequipItemInBodySlotAndRecord(Item.SLOT_L_HAND);
		}
		
		player.broadcastPacket(new SocialAction(player, 16));
		
		player.getInventory().reloadEquippedItems();
		
		ItemInstance item = player.getInventory().addItem("", 6718, 1, player, null);
		player.getInventory().equipItem(ItemData.getInstance().createDummyItem(6718));
		player.getInventory().equipItemAndRecord(item);
		
		player.broadcastCharInfo();
		
		// player.getInventory().equipItem(ItemData.getInstance().createDummyItem(6718));
		
		switch (flag)
		{
			case 1:
				playerWithBlueFlag = player;
				announce(getPlayerList(), player.getName() + " took the Blue flag!");
				unspawnNPC(blueFlagNpc);
				break;
			case 2:
				playerWithRedFlag = player;
				announce(getPlayerList(), player.getName() + " took the Red flag!");
				unspawnNPC(redFlagNpc);
				break;
		}
		
		player.broadcastUserInfo();
		
		player.sendMessage("You got it! Run back!");

	}
	
	@Override
	public void onDie(Player victim, Creature killer)
	{
		super.onDie(victim, killer);
		
		if (playerWithRedFlag == victim)
		{
			announce(getPlayerList(), victim.getName() + " dropped the Red flag!");
			redFlagStatus = 2;
			unequipFlag(victim);
			redFlagNpc = spawnNPC(victim.getX(), victim.getY(), victim.getZ(), getInt("redFlagId"));
		}
		
		if (playerWithBlueFlag == victim)
		{
			announce(getPlayerList(), victim.getName() + " dropped the Blue flag!");
			blueFlagStatus = 2;
			unequipFlag(victim);
			blueFlagNpc = spawnNPC(victim.getX(), victim.getY(), victim.getZ(), getInt("blueFlagId"));
		}
		
		addToResurrector(victim);
	}
	
	@Override
	public void onLogout(Player player)
	{
		super.onLogout(player);
		
		if (playerWithRedFlag == player)
		{
			announce(getPlayerList(), player.getName() + " dropped the Red flag!");
			redFlagStatus = 2;
			unequipFlag(player);
			redFlagNpc = spawnNPC(player.getX(), player.getY(), player.getZ(), getInt("redFlagId"));
		}
		
		if (playerWithBlueFlag == player)
		{
			announce(getPlayerList(), player.getName() + " dropped the Blue flag!");
			blueFlagStatus = 2;
			unequipFlag(player);
			blueFlagNpc = spawnNPC(player.getX(), player.getY(), player.getZ(), getInt("blueFlagId"));
		}
	}
	
	@Override
	public boolean onTalkNpc(Npc npc, Player player)
	{
		if (npc.getNpcId() != getInt("blueFlagId") && npc.getNpcId() != getInt("blueFlagHolderId") && npc.getNpcId() != getInt("redFlagId") && npc.getNpcId() != getInt("redFlagHolderId"))
			return false;
		
		// Blue holder
		if (npc.getNpcId() == getInt("blueFlagHolderId"))
		{
			if (player == playerWithRedFlag)
			{
				if (blueFlagStatus == 0)
				{
					announce(getPlayerList(), "The Blue team scored!");
					teams.get(getTeam(player)).increaseScore();
					increasePlayersScore(player);
					returnFlag(2);
				}
				else
					player.sendMessage("Your team must kill enemy flag owner and return the flag in order to score!");
			}
		}
		// Red holder
		else if (npc.getNpcId() == getInt("redFlagHolderId"))
		{
			if (player == playerWithBlueFlag)
			{
				if (redFlagStatus == 0)
				{
					announce(getPlayerList(), "The Red team scored!");
					teams.get(getTeam(player)).increaseScore();
					increasePlayersScore(player);
					returnFlag(1);
				}
				else
					player.sendMessage("Your team must kill enemy flag owner and return the flag in order to score!");
			}
		}
		// Blue flag
		else if (npc.getNpcId() == getInt("blueFlagId"))
		{
			if (blueFlagStatus == 2)
			{
				// blue player
				if (getTeam(player) == 1)
					returnFlag(1);
				
				// red player
				if (getTeam(player) == 2)
					equipFlag(player, 1);
			}
			if (blueFlagStatus == 0)
			{
				if (getTeam(player) == 2)
				{
					equipFlag(player, 1);
					unspawnNPC(blueFlagNpc);
					blueFlagStatus = 1;
				}
			}
		}
		// Red flag
		else
		{
			if (redFlagStatus == 2)
			{
				// red player
				if (getTeam(player) == 2)
					returnFlag(2);
				
				// blue player
				if (getTeam(player) == 1)
					equipFlag(player, 2);
			}
			if (redFlagStatus == 0)
			{
				if (getTeam(player) == 1)
				{
					equipFlag(player, 2);
					unspawnNPC(redFlagNpc);
					redFlagStatus = 1;
				}
			}
		}
		
		return true;
	}
	
	@Override
	public boolean onUseItem(Player player, ItemInstance item)
	{
		if (playerWithRedFlag == player || playerWithBlueFlag == player)
			return false;
		
		return super.onUseItem(player, item);
	}
	
	private void returnFlag(int flag)
	{
		int[] pos;
		
		switch (flag)
		{
			case 1:
				if (playerWithBlueFlag != null)
					unequipFlag(playerWithBlueFlag);
				if (blueFlagStatus == 2)
					unspawnNPC(blueFlagNpc);
				
				pos = getPosition("BlueFlag", 1);
				blueFlagNpc = spawnNPC(pos[0], pos[1], pos[2], getInt("blueFlagId"));
				blueFlagStatus = 0;
				announce(getPlayerList(), "The Blue flag returned!");
				break;
			
			case 2:
				if (playerWithRedFlag != null)
					unequipFlag(playerWithRedFlag);
				if (redFlagStatus == 2)
					unspawnNPC(redFlagNpc);
				
				pos = getPosition("RedFlag", 1);
				redFlagNpc = spawnNPC(pos[0], pos[1], pos[2], getInt("redFlagId"));
				redFlagStatus = 0;
				announce(getPlayerList(), "The Red flag returned!");
				break;
		}
	}
	
	@Override
	protected void schedule(int time)
	{
		ThreadPool.schedule(task, time);
	}
	
	protected void setStatus(EventState s)
	{
		eventState = s;
	}
	
	@Override
	protected void showHtml(Player player, int obj)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(obj);
		TextBuilder sb = new TextBuilder();
		sb.append("<html><body><table width=270><tr><td width=200>Event Engine </td><td><a action=\"bypass -h eventstats 1\">Statistics</a></td></tr></table><br><center><table width=270 bgcolor=000000><tr><td width=70>Running</td><td width=130><center>" + getString("eventName") + "</td><td width=70>Time: " + clock.getTime() + "</td></tr></table><center><table width=270><tr><td><center><font color=" + teams.get(1).getHexaColor() + ">" + teams.get(1).getScore() + "</font> - <font color=" + teams.get(2).getHexaColor() + ">" + teams.get(2).getScore() + "</font></td></tr></table><br><table width=270>");
		
		int i = 0;
		for (EventTeam team : teams.values())
		{
			i++;
			sb.append("<tr><td><font color=" + team.getHexaColor() + ">" + team.getName() + "</font> team</td><td></td><td></td><td></td></tr>");
			for (Player p : getPlayersOfTeam(i))
				sb.append("<tr><td>" + p.getName() + "</td><td>lvl " + p.getStatus().getLevel() + "</td><td>" + p.getTemplate().getClassName() + "</td><td>" + getScore(p) + "</td></tr>");
		}
		
		sb.append("</table></body></html>");
		html.setHtml(sb.toString());
		player.sendPacket(html);
	}
	
	protected void spawnFlagsAndHolders()
	{
		int[] pos = getPosition("BlueFlag", 1);
		blueFlagNpc = spawnNPC(pos[0], pos[1], pos[2], getInt("blueFlagId"));
		blueHolderNpc = spawnNPC(pos[0] + 50, pos[1], pos[2], getInt("blueFlagHolderId"));
		
		pos = getPosition("RedFlag", 1);
		redFlagNpc = spawnNPC(pos[0], pos[1], pos[2], getInt("redFlagId"));
		redHolderNpc = spawnNPC(pos[0] + 50, pos[1], pos[2], getInt("redFlagHolderId"));
	}
	
	@Override
	protected void start()
	{
		setStatus(EventState.START);
		schedule(1);
	}
	
	@Override
	protected String getStartingMsg()
	{
		return "Steal the enemy flag while keeping yours safe!";
	}
	
	@Override
	protected String getScorebar()
	{
		return teams.get(1).getName() + ": " + teams.get(1).getScore() + "  " + teams.get(2).getName() + ": " + teams.get(2).getScore() + "  Time: " + clock.getTime();
	}
	
	protected void unequipFlag(Player player)
	{
		ItemInstance wpn = player.getInventory().getItemFrom(Paperdoll.RHAND);
		if (wpn != null)
		{
			ItemInstance[] unequiped = player.getInventory().unequipItemInBodySlotAndRecord(wpn.getItem().getBodyPart());
			player.getInventory().destroyItemByItemId("", 6718, 1, player, null);
			
			InventoryUpdate iu = new InventoryUpdate();
			for (ItemInstance element : unequiped)
				iu.addModifiedItem(element);
			player.sendPacket(iu);
			player.sendPacket(new ItemList(player, true));
			player.broadcastUserInfo();
			player.broadcastCharInfo();
		}
		
		if (player == playerWithRedFlag)
			playerWithRedFlag = null;
		if (player == playerWithBlueFlag)
			playerWithBlueFlag = null;
	}
	
	protected void unspawnFlagsAndHolders()
	{
		unspawnNPC(blueFlagNpc);
		unspawnNPC(blueHolderNpc);
		unspawnNPC(redFlagNpc);
		unspawnNPC(redHolderNpc);
	}
}