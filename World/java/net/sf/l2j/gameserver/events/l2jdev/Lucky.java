package net.sf.l2j.gameserver.events.l2jdev;


import net.sf.l2j.commons.pool.ThreadPool;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.enums.SayType;
import net.sf.l2j.gameserver.model.WorldObject;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.spawn.Spawn;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.skills.L2Skill;

import javolution.text.TextBuilder;
import javolution.util.FastList;

public class Lucky extends Event
{
	protected EventState eventState;
	protected FastList<Spawn> chests = new FastList<>();
	private Core task = new Core();
	
	private enum EventState
	{
		START,
		FIGHT,
		END,
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
						divideIntoTeams(1);
						preparePlayers();
						teleportToTeamPos();
						forceSitAll();
						unequip();
						setStatus(EventState.FIGHT);
						schedule(30000);
						break;
					
					case FIGHT:
						forceStandAll();
						sendMsg();
						int[] coor = getPosition("Chests", 1);
						for (int i = 0; i < getInt("numberOfChests"); i++)
							chests.add(spawnNPC(coor[0] + (Rnd.get(coor[3] * 2) - coor[3]), coor[1] + (Rnd.get(coor[3] * 2) - coor[3]), coor[2], getInt("chestNpcId")));
						setStatus(EventState.END);
						clock.startClock(getInt("matchTime"));
						break;
					
					case END:
						clock.setTime(0);
						Player winner = getPlayerWithMaxScore();
						giveReward(winner, getInt("rewardId"), getInt("rewardAmmount"));
						setStatus(EventState.INACTIVE);
						EventManager.getInstance().end("Congratulation! " + winner.getName() + " won the event with " + getScore(winner) + " opened chests!");
						unSpawnChests();
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
	
	public Lucky()
	{
		super();
		eventId = 5;
		createNewTeam(1, "All", getColor("All"), getPosition("All", 1));
	}
	
	@Override
	protected void endEvent()
	{
		setStatus(EventState.END);
		clock.setTime(0);
	}
	
	@Override
	public boolean onTalkNpc(Npc npc, Player player)
	{
		if (npc.getNpcId() != getInt("chestNpcId"))
			return false;
		
		if (Rnd.get(3) == 0)
		{
			player.sendPacket(new CreatureSay(npc.getObjectId(), SayType.SHOUT, "Chest", "Sorry! No reward."));
			player.stopAllEffects();
			player.reduceCurrentHp(player.getStatus().getMaxHp() + player.getStatus().getMaxCp() + 1, npc, null);
			EventStats.getInstance().tempTable.get(player.getObjectId())[2] = EventStats.getInstance().tempTable.get(player.getObjectId())[2] + 1;
			addToResurrector(player);
		}
		else
		{
			npc.doDie(player);
			increasePlayersScore(player);
			if (getBoolean("rewardOnOpen"))
			{
				// int rnd = Rnd.get(100);
				// if (rnd < ITEMS.length -1)
				// player.addItem("Lucky", ITEMS[rnd], 1, player, true);
			
				
				if(player.isPremium())
					player.addItem("Event", ITEMS[0], 1 * 2, player, true);
					else
						player.addItem("Event", ITEMS[0], 1, player, true);
				
			}
		}
		npc.deleteMe();
		//SpawnTable.getInstance().deleteSpawn(npc.getSpawn(), false);
		chests.remove(npc.getSpawn());
		
		if (chests.size() == 0)
			clock.setTime(0);
		
		return true;
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
	public boolean onUseMagic(L2Skill skill)
	{
		return false;
	}
	
	@Override
	protected void showHtml(Player player, int obj)
	{
		if (players.size() > 0)
		{
			NpcHtmlMessage html = new NpcHtmlMessage(obj);
			TextBuilder sb = new TextBuilder();
			sb.append("<html><body><table width=270><tr><td width=200>Event Engine </td><td><a action=\"bypass -h eventstats 1\">Statistics</a></td></tr></table><br><center><table width=270 bgcolor=000000><tr><td width=70>Running</td><td width=130><center>" + getString("eventName") + "</td><td width=70>Time: " + clock.getTime() + "</td></tr></table><table width=270><tr><td><center>" + getPlayerWithMaxScore().getName() + " - " + getScore(getPlayerWithMaxScore()) + "</td></tr></table><br><table width=270>");
			
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
	}
	
	@Override
	protected void start()
	{
		setStatus(EventState.START);
		schedule(1);
	}
	
	protected void unSpawnChests()
	{
		for (Spawn s : chests)
		{
			if (s == null)
			{
				chests.remove(s);
				continue;
			}
			
			s.getNpc().deleteMe();
			s.setRespawnState(false);
			chests.remove(s);
		}
	}
	
	@Override
	public boolean onUseItem(Player player, ItemInstance item)
	{
		return false;
	}
	
	@Override
	public boolean canAttack(Player player, WorldObject target)
	{
		return false;
	}
	
	@Override
	protected String getStartingMsg()
	{
		return "Open as much chests as possible!";
	}
	
	@Override
	protected String getScorebar()
	{
		return "Max: " + getScore(getPlayerWithMaxScore()) + "  Time: " + clock.getTime() + "";
	}
}