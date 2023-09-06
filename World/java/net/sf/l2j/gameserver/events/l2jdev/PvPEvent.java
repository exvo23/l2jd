package net.sf.l2j.gameserver.events.l2jdev;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import javolution.text.TextBuilder;

/**
 * @author BAN {Link} L2JDEV
 */
public class PvPEvent extends Event
{
	protected EventState eventState;
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
						setStatus(EventState.FIGHT);
						divideIntoTeams(1);
						teleportToTeamPos();
						World.announceToOnlinePlayers("Pvp Event: Top players will be rewarded with Donate Coins!", true);
						World.announceToOnlinePlayers("Pvp Event: " + (getInt("matchTime") / 60) + " minute(s) until the event is finished!", true);
						
						schedule(1);
						break;
					case FIGHT:
						
						for (Player plr : World.getInstance().getAllPlayers().values())
						{
							plr.setEventKills(0);
						}
						
						setStatus(EventState.END);
						clock.startClock(getInt("matchTime"));
						break;
					
					case END:
						clock.setTime(0);
						
						for (Player players : World.getInstance().getAllPlayers().values())
						{
							if (players.isInFunEvent())
							{
								players.sendRankHtml();
								giveReward(players, getInt("rewardId"), getInt("rewardAmmount") / 2);
								
							}
						}
						
						
						reward();
						setStatus(EventState.INACTIVE);
						EventManager.getInstance().end("Congratulation! " + "PvP Event End Thanks.");
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
	
	public PvPEvent()
	{
		super();
		eventId = 8;
		createNewTeam(1, "All", getColor("All"), getPosition("All", 1));
	}
	
	@Override
	protected void endEvent()
	{
		setStatus(EventState.END);
		clock.setTime(0);
	}
	
	protected void setStatus(EventState s)
	{
		eventState = s;
	}
	
	@Override
	public void onDie(Player victim, Creature killer)
	{
		super.onDie(victim, killer);
		addToResurrector(victim);
	}
	
	@Override
	public void onKill(Creature victim, Player killer)
	{
		super.onKill(victim, killer);
		
		increasePlayersScore(killer);
		
	}
	
	@Override
	protected String getStartingMsg()
	{
		return null;
	}
	
	@Override
	protected void start()
	{
		setStatus(EventState.START);
		schedule(1);
	}
	
	@Override
	protected void schedule(int time)
	{
		ThreadPool.schedule(task, time);
	}
	
	@Override
	protected void showHtml(Player player, int obj)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(obj);
		TextBuilder sb = new TextBuilder();
		sb.append("<html><body><table width=270><tr><td width=200>Event Engine </td><td><a action=\"bypass -h eventstats 1\">Statistics</a></td></tr></table><br><center><table width=270 bgcolor=5A5A5A><tr><td width=70>Running</td><td width=130><center>" + getString("eventName") + "</td><td width=70>Time: " + clock.getTime() + "</td></tr></table><table width=270><tr><td><center>Players Kills: " + player.getEventKills() + "</td></tr></table><br><table width=270>");
		int i = 1;
		
		for (Player p : getPlayersOfTeam(i))
			sb.append("<tr><td>" + p.getName() + "</td><td>lvl " + p.getStatus().getLevel() + "</td><td>" + p.getTemplate().getClassName() + "</td><td>" + p.getEventKills() + "</td></tr>");
		
		sb.append("</table></body></html>");
		html.setHtml(sb.toString());
		player.sendPacket(html);
	}
	
	@Override
	protected String getScorebar()
	{
		return null;
	}
	
	private static class RankComparator implements Comparator<Player>
	{
		@Override
		public int compare(Player o1, Player o2)
		{
			return Integer.compare(o1.getEventKills(), o2.getEventKills());
		}
	}
	
	@SuppressWarnings("null")
	protected void reward()
	{
		int i = 0;
		List<Player> temp = new ArrayList<>();
		for (Player plr : World.getInstance().getAllPlayers().values())
		{
			if (plr.isInFunEvent())
			{
				temp.add(plr);
			}
		}
		
		if (temp == null || temp.isEmpty())
		{
			return;
		}
		Collections.sort(temp, new RankComparator());
		Collections.reverse(temp);
		for (Player players : temp)
		{
			i++;
			
			if (i == 1)
			{
				int item = 9315;
				
				if (players.isPremium())
					players.addItem("Event", item, 3 * 2, players, true);
				else
					players.addItem("Event", item, 3, players, true);
				
			}
			else if (i == 2)
			{
				int item = 9315;
				if (players.isPremium())
					players.addItem("Event", item, 2 * 2, players, true);
				else
					players.addItem("Event", item, 2, players, true);
			}
			else if (i == 3)
			{
				int item = 9315;
				if (players.isPremium())
					players.addItem("Event", item, 1 * 2, players, true);
				else
					players.addItem("Event", item, 1, players, true);
			}
			if (i == 4)
				break;
			
		}
		
	}
	
	public List<Player> getTopRankForReward(int top)
	{
		List<Player> list = new ArrayList<>();
		for (Player player : World.getInstance().getAllPlayers().values())
		{
			if (player.getEventKills() > 0)
			{
				list.add(player);
			}
		}
		// System.out.println("List Size1: " + list.size());
		list.sort(Comparator.comparing(Player::getEventKills).reversed());
		if (list.size() > 3)
		{
			list = list.subList(0, 3);
		}
		System.out.println("List Size2: " + list.size());
		return list;
	}
}
