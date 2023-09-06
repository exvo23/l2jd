package net.sf.l2j.gameserver.events.l2jdev;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.data.xml.DoorData;
import net.sf.l2j.gameserver.model.WorldObject;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.spawn.Spawn;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.skills.L2Skill;

import javolution.text.TextBuilder;
import javolution.util.FastList;

/**
 * @author juven
 */
public class Zombie extends Event
{
	protected EventState eventState;
	protected FastList<Spawn> zombie = new FastList<>();
	private Core task = new Core();
	private int counter;
	
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
						giveBows();
						setStatus(EventState.FIGHT);
						schedule(30000);
						break;
					
					case FIGHT:
						forceStandAll();
						sendMsg();
						transform(getRandomPlayer());
						
						DoorData.getInstance().getDoor(21170003).openMe();
						DoorData.getInstance().getDoor(21170004).openMe();
						DoorData.getInstance().getDoor(21170005).openMe();
						DoorData.getInstance().getDoor(21170006).openMe();
						setStatus(EventState.END);
						clock.startClock(getInt("matchTime"));
						break;
					
					case END:
						clock.setTime(0);
						setStatus(EventState.INACTIVE);
						removeMisc();
						
						DoorData.getInstance().getDoor(21170003).closeMe();
						DoorData.getInstance().getDoor(21170004).closeMe();
						DoorData.getInstance().getDoor(21170005).closeMe();
						DoorData.getInstance().getDoor(21170006).closeMe();
						Player winner = getWinner();

						if (getPlayersWithStatus(0).size() != 1)
						{
							EventManager.getInstance().end("The event ended in a tie! there are " + getPlayersWithStatus(0).size() + " humans still standing!");
				
							for (Player player : getPlayerList())
							{
								giveReward(player, getInt("rewardId"), getInt("rewardAmmount") / 2);
							}
						}
						else
						{
							giveReward(winner, getInt("rewardId"), getInt("rewardAmmount"));
							EventManager.getInstance().end("Congratulation! " + winner.getName() + " won the event!");
							
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
	
	@Override
	public void onLogout(Player player)
	{
		if (getStatus(player) == 1)
		{
			if (player.getPolymorphTemplate() != null)
				player.unpolymorph();
			
			player.stopSkillEffects(7029);
			super.onLogout(player);
			
			if (getPlayersWithStatus(1).size() == 0)
				transform(getRandomPlayer());
		}
		else
		{
			
			player.destroyItem("Zombies", player.getInventory().getItemByItemId(273), player, false);
			player.destroyItem("Zombies", player.getInventory().getItemByItemId(17), player, false);
			super.onLogout(player);
		}
		
		if (getPlayersWithStatus(0).size() == 1)
			schedule(1);
	}
	
	@Override
	protected void showHtml(Player player, int obj)
	{
		if (players.size() > 0)
		{
			NpcHtmlMessage html = new NpcHtmlMessage(obj);
			TextBuilder sb = new TextBuilder();
			sb.append("<html><body><table width=270><tr><td width=200>Event Engine </td></tr></table><br><center><table width=270 bgcolor=5A5A5A><tr><td width=70>Running</td><td width=130><center>" + getString("eventName") + "</td><td width=70>Time: " + clock.getTime() + "</td></tr></table><table width=270><tr><td><center>Players left: " + getPlayersWithStatus(0).size() + "</td></tr></table><br><table width=270>");
			
			sb.append("</table></body></html>");
			html.setHtml(sb.toString());
			player.sendPacket(html);
		}
	}
	
	protected void giveBows()
	{
		for (Player player : players.keySet())
		{
			try
			{
				player.addItem("", 273, 1, player, true);
				
				player.addItem("", 17, 1000, player, true);
				
				ItemInstance PhewPew1 = player.getInventory().getItemByItemId(273);
				player.useEquippableItem(PhewPew1, true);
				
			}
			catch (Exception e)
			{
				// player disconnected
			}
		}
	}
	
	protected void removeMisc()
	{
		for (Player player : getPlayersWithStatus(1))
		{
			
			if (player.getPolymorphTemplate() != null)
				player.unpolymorph();
			
			player.stopSkillEffects(7029);
		}
		for (Player player : getPlayersWithStatus(0))
		{
			try
			{
				
				player.destroyItem("Zombies", player.getInventory().getItemByItemId(273), player, false);
				player.destroyItem("Zombies", player.getInventory().getItemByItemId(17), player, false);
			}
			catch (Exception e)
			{
				
			}
		}
	}
	
	public Zombie()
	{
		super();
		eventId = 6;
		createNewTeam(1, "All", getColor("All"), getPosition("All", 1));
	}
	
	@Override
	protected void endEvent()
	{
		getLost();
		setStatus(EventState.END);
		clock.setTime(0);
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
	
	protected Player getWinner()
	{
		return getPlayersWithStatus(0).head().getNext().getValue();
	}
	
	protected Player getLost()
	{
		return players.head().getNext().getKey();
	}
	
	@Override
	public void onHit(Player actor, Player target)
	{
		if (eventState == EventState.END)
		{
			if ((getStatus(actor) == 1) && (getStatus(target) == 0))
			{
				transform(target);
				increasePlayersScore(actor);
				actor.addItem("Event", getInt("rewardId"), 1, actor, true);
				
				if (getPlayersWithStatus(0).size() == 1)
					schedule(1);
			}
			else if ((getStatus(actor) == 0) && (getStatus(target) == 1))
			{
				target.doDie(actor);
				increasePlayersScore(actor);
				addToResurrector(target);
			}
		}
	}
	
	@Override
	protected void start()
	{
		counter = 0;
		setStatus(EventState.START);
		schedule(1);
	}
	
	@Override
	public boolean onUseItem(Player player, ItemInstance item)
	{
		return false;
	}
	
	@Override
	public boolean canAttack(Player player, WorldObject target)
	{
		if (target instanceof Player)
			if (((getStatus(player) == 1) && (getStatus((Player) target) == 0)) || ((getStatus(player) == 0) && (getStatus((Player) target) == 1)))
				return true;
			
		return false;
	}
	
	@Override
	protected String getStartingMsg()
	{
		return "";
	}
	
	@Override
	protected String getScorebar()
	{
		if (counter == 0)
			return "Humans: " + getPlayersWithStatus(0).size() + "  Time: " + clock.getTime();
		
		counter--;
		return "";
	}
	
	protected void teleportToRandom()
	{
		for (Player player : players.keySet())
		{
			int[] loc = getPosition("All", 0);
			player.teleportTo(loc[0], loc[1], loc[2], 0);
		}
	}
	
	protected void transform(Player player)
	{
		if (player.getPolyId() == 25375)
			return;
		
		counter = 3;
		for (Player p : players.keySet())
			p.sendPacket(new ExShowScreenMessage(player.getName() + " has transformed into a zombie!", 6000));
		player.destroyItem("Zombies", player.getInventory().getItemByItemId(273), player, false);
		player.destroyItem("Zombies", player.getInventory().getItemByItemId(17), player, false);
		setStatus(player, 1);
		player.addSkill(SkillTable.getInstance().getInfo(7029, 1), false);
		player.getAppearance().setNameColor(255, 0, 0);
		player.broadcastUserInfo();
		player.polymorph(25375);
		
	}
}
