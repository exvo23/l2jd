package net.sf.l2j.gameserver.events.l2jdev;


import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import javolution.text.TextBuilder;

public class TvT extends Event
{
	protected EventState eventState;
	private Core task = new Core();
	private enum EventState
	{
		START, FIGHT, END, TELEPORT, INACTIVE
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
						
						setStatus(EventState.INACTIVE);
						
						if (winnerTeam == 0)
						{
							for (Player player : getPlayerList())
							{
								giveReward(player, getInt("rewardId"), getInt("rewardAmmount") / 2);
							}
							EventManager.getInstance().end("The event ended in a tie! both teams had " + teams.get(1).getScore() + " kills!");
						}else
						{
							giveReward(getPlayersOfTeam(winnerTeam), getInt("rewardId"), getInt("rewardAmmount"));
							EventManager.getInstance().end("Congratulation! The " + teams.get(winnerTeam).getName() + " team won the event with " + teams.get(winnerTeam).getScore() + " kills!");
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
	
	public TvT()
	{
		super();
		eventId = 2;
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
		if (getPlayersTeam(killer) != getPlayersTeam((Player) victim))
		{
			getPlayersTeam(killer).increaseScore();
			increasePlayersScore(killer);
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
		sb.append("<html><body><table width=270><tr><td width=200>Event Engine </td><td><a action=\"bypass -h eventstats 1\">Statistics</a></td></tr></table><br><center><table width=270 bgcolor=000000><tr><td width=70>Running</td><td width=130><center>" + getString("eventName") + "</td><td width=70>Time: " + clock.getTime() + "</td></tr></table><center><table width=270><tr><td><center><font color=" + teams.get(1).getHexaColor() + ">" + teams.get(1).getScore() + "</font> - " + "<font color=" + teams.get(2).getHexaColor() + ">" + teams.get(2).getScore() + "</font></td></tr></table><br><table width=270>");
		
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
	
	@Override
	protected void start()
	{
		setStatus(EventState.START);
		schedule(1);
	}
	
	@Override
	protected String getStartingMsg()
	{
		return "Go and kill your enemies!";
	}
	
	@Override
	protected String getScorebar()
	{
		return teams.get(1).getName() + ": " + teams.get(1).getScore() + "  " + teams.get(2).getName() + ": " + teams.get(2).getScore() + "  Time: " + clock.getTime();
	}
}