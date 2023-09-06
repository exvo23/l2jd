package net.sf.l2j.gameserver.events.l2jdev;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.data.manager.SpawnManager;
import net.sf.l2j.gameserver.data.xml.DoorData;
import net.sf.l2j.gameserver.data.xml.NpcData;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.Door;
import net.sf.l2j.gameserver.model.actor.instance.HolyThing;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.spawn.Spawn;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.skills.L2Skill;

import javolution.text.TextBuilder;

/**
 * @author BAN {Link} JDev
 */
public class Fortress extends Event
{
	protected EventState eventState;
	private Core task = new Core();
	
	private static List<HolyThing> npc = new CopyOnWriteArrayList<>();
	
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
						
						SpawnArtifact();
		
						
						setStatus(EventState.FIGHT);
						schedule(10000);
						break;
					
					case FIGHT:
						sendMsg();
						GiveSkill();
						setStatus(EventState.END);
						clock.startClock(getInt("matchTime"));
						break;
					
					case END:
						clock.setTime(0);
						if (winnerTeam == 0)
							winnerTeam = getWinnerTeam();
						 RemoveSkills();
						unspawnFlagsAndHolders();
						setStatus(EventState.INACTIVE);
						if (winnerTeam == 0)
						{
							EventManager.getInstance().end("The event ended in a tie! both teams had " + teams.get(1).getScore() + " Point!");
							for (Player player : getPlayerList())
							{
								giveReward(player, getInt("rewardId"), getInt("rewardAmmount") / 2);
							}
						}
						else
						{
							giveReward(getPlayersOfTeam(winnerTeam), getInt("rewardId"), getInt("rewardAmmount"));
							EventManager.getInstance().end("Congratulation! The " + teams.get(winnerTeam).getName() + " team won the event with " + teams.get(winnerTeam).getScore() + " Point!");
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
	
	public Fortress()
	{
		super();
		eventId = 7;
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
		return "";
	}
	
	@Override
	protected String getScorebar()
	{
		return teams.get(1).getName() + ": " + teams.get(1).getScore() + "  " + teams.get(2).getName() + ": " + teams.get(2).getScore() + "  Time: " + clock.getTime();
	}
	
	@Override
	public void FortressReset(Player actor)
	{
		if (eventState == EventState.END)
		{
			
			teams.get(getTeam(actor)).increaseScore();
			
			increasePlayersScore(actor);
			
			World.announceToOnlinePlayers("Fortress Event: " + actor.getName() + " scored " + teams.get(getTeam(actor)).getScore() + " for the " + teams.get(getTeam(actor)).getName() + " team!", true);
			
			teleportToTeamPos();
			healDoors(getInt("DoorId_00"));
			healDoors(getInt("DoorId_01"));
			healDoors(getInt("DoorId_02"));
			healDoors(getInt("DoorId_03"));
			healDoors(getInt("DoorId_04"));
			healDoors(getInt("DoorId_05"));
			
		}
	}
	
	public void healDoors(int id)
	{
		Door doorInstance = DoorData.getInstance().getDoor(id);
		
		if (doorInstance.isDead())
			doorInstance.doRevive();
		
		doorInstance.getStatus().setMaxHpMp();
		
	}
	
	@Override
	public boolean onTalkNpc(Npc npc, Player player)
	{
		if (npc.getNpcId() == getInt("ArtifactNpcId"))
		{
			L2Skill skill = SkillTable.getInstance().getInfo(EventManager.getInstance().getCurrentEvent().getInt("SealFortressSkill"), EventManager.getInstance().getCurrentEvent().getInt("SealFortressSkillLevel"));
			player.broadcastPacketInRadius(new MagicSkillUse(player, player, skill.getId(), skill.getLevel(), skill.getHitTime(), skill.getReuseDelay()), 3000);
			
			player.getAI().tryToCast((Creature) player.getTarget(), skill);
		}
		return true;
	}
	
	public void GiveSkill()
	{
		for (Player player : players.keySet())
		{
			try
			{
				L2Skill sealOfRuler = SkillTable.getInstance().getInfo(EventManager.getInstance().getCurrentEvent().getInt("SealFortressSkill"), EventManager.getInstance().getCurrentEvent().getInt("SealFortressSkillLevel"));
				
				if (player.returnSkills().containsValue(sealOfRuler))
					player.addSkill(sealOfRuler, false);
				else
					player._FOSRulerSkills = true;
				
				player.sendSkillList();
				player.sendMessage("You have been given the Seal Of Ruler skill for this event.");
				
			}
			catch (Exception e)
			{
				// player disconnected
			}
		}
	}
	public void RemoveSkills()
	{
		for (Player player : players.keySet())
		{
			try
			{
				L2Skill sealOfRuler = SkillTable.getInstance().getInfo(EventManager.getInstance().getCurrentEvent().getInt("SealFortressSkill"), EventManager.getInstance().getCurrentEvent().getInt("SealFortressSkillLevel"));
			if (player.returnSkills().containsValue(sealOfRuler) && player._FOSRulerSkills)
				{
					player.removeSkill(EventManager.getInstance().getCurrentEvent().getInt("SealFortressSkill"), false);
					player.sendSkillList();
				}
				else
					player._FOSRulerSkills = false;
				
			}
			catch (Exception e)
			{
				// player disconnected
			}
		}
	}
	public void SpawnArtifact()
	{
		NpcTemplate template = NpcData.getInstance().getTemplate(EventManager.getInstance().getCurrentEvent().getInt("ArtifactNpcId"));
		try
		{
			
			Spawn spawn = new Spawn(template);
			int[] pos = getPosition("ArtifactLocation", 1);
			spawn.setLoc(pos[0], pos[1], pos[2], 0);
			
			spawn.setRespawnDelay(1);
			spawn.setRespawnState(false);
			spawn.doSpawn(false);
			spawn.getNpc()._isFOS_Artifact = true;
			
			// Add it to memory
			npc.add((HolyThing) spawn.getNpc());
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "SpawnArtifact: exception: " + e.getMessage(), e);
			e.printStackTrace();
		}
		
	}
	
	protected void unspawnFlagsAndHolders()
	{
		
		for (HolyThing mob : npc)
			deleteMob(mob);
	}
	
	private static void deleteMob(HolyThing mob)
	{
		if (!npc.contains(mob))
			return;
		
		npc.remove(mob);
		
		final Spawn spawn = (Spawn) mob.getSpawn();
		spawn.doDelete();
		SpawnManager.getInstance().deleteSpawn(spawn);
		mob.deleteMe();
		npc.clear();
	}
	
	@Override
	public void onLogout(Player player)
	{
		super.onLogout(player);
	}
	
}
