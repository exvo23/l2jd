package net.sf.l2j.gameserver.events.l2jdev;

import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Logger;

import net.sf.l2j.commons.pool.ThreadPool;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.data.xml.DoorData;
import net.sf.l2j.gameserver.data.xml.NpcData;
import net.sf.l2j.gameserver.enums.LootRule;
import net.sf.l2j.gameserver.enums.MessageType;
import net.sf.l2j.gameserver.enums.SayType;
import net.sf.l2j.gameserver.enums.items.EtcItemType;
import net.sf.l2j.gameserver.enums.skills.SkillType;
import net.sf.l2j.gameserver.model.WorldObject;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.Summon;
import net.sf.l2j.gameserver.model.actor.instance.Door;
import net.sf.l2j.gameserver.model.actor.instance.Pet;
import net.sf.l2j.gameserver.model.actor.instance.RaidBoss;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.group.Party;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.spawn.Spawn;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage.SMPOS;
import net.sf.l2j.gameserver.skills.L2Skill;

import javolution.util.FastList;
import javolution.util.FastMap;

public abstract class Event
{
	protected static final Logger _log = Logger.getLogger(Event.class.getName());
	
	protected static final int[] ITEMS =
	{
		6393
	};
	protected int eventId;
	protected EventConfig config = EventConfig.getInstance();
	protected FastMap<Integer, EventTeam> teams;
	protected ThreadPool tpm;
	protected ResurrectorTask resurrectorTask;
	protected Clock clock;
	protected String scorebartext;
	protected int time;
	protected int winnerTeam;
	protected int loserTeam;
	private FastMap<Player, ArrayList<L2Skill>> summons;
	
	// TEAM-STATUS-SCORE
	protected FastMap<Player, int[]> players;
	
	protected void unequip()
	{
		for (Player player : players.keySet())
		{
			player.getInventory().unequipItemInSlot(7);
			player.getInventory().unequipItemInSlot(8);
		}
	}
	
	protected class Clock implements Runnable
	{
		private int totalTime;
		
		protected String getTime()
		{
			String mins = "" + time / 60;
			String secs = (time % 60 < 10 ? "0" + time % 60 : "" + time % 60);
			return mins + ":" + secs + "";
		}
		
		@Override
		public void run()
		{
			clockTick();
			
			if (time < totalTime)
			{
				scorebartext = getScorebar();
				if (scorebartext != "")
				{
					for (Player player : getPlayerList())
						player.sendPacket(new ExShowScreenMessage(scorebartext, 2000, SMPOS.BOTTOM_RIGHT, false));
					
				}
			}
			switch (time)
			{
				case 60:
				case 10:
				case 5:
				case 4:
				case 3:
				case 2:
				case 1:
				{
					String text = time + " second(s) till event finish.";
					for (Player player : players.keySet())
					{
						if (player == null)
						{
							continue;
						}
						player.sendMessage(text);
					}
					break;
				}
			}
			
			if (time <= 0)
			{
				schedule(1);
			}
			else
			{
				time--;
				ThreadPool.schedule(clock, 1000);
			}
		}
		
		protected void setTime(int t)
		{
			time = t;
		}
		
		protected void startClock(int mt)
		{
			totalTime = mt - 2;
			time = mt;
			ThreadPool.schedule(clock, 1);
		}
	}
	
	protected class ResurrectorTask implements Runnable
	{
		private Player player;
		
		public ResurrectorTask(Player p)
		{
			player = p;
			ThreadPool.schedule(this, 4000);
		}
		
		@Override
		public void run()
		{
			if (EventManager.getInstance().isRegistered(player))
			{
				player.doRevive();
				player.getStatus().setMaxCpHpMp();
				teleportToTeamPos(player);
			}
		}
	}
	
	public Event()
	{
		teams = new FastMap<>();
		clock = new Clock();
		players = new FastMap<>();
		summons = new FastMap<>();
		time = 0;
	}
	
	protected void clockTick()
	{
		
	}
	
	public void dropBomb(Player player)
	{
		
	}
	
	public void onHit(Player actor, Player target)
	{
		
	}
	
	public void FortressReset(Player actor)
	{
		
	}
	
	public void useCapture(Player player, Npc base)
	{
		
	}
	
	protected void addToResurrector(Player player)
	{
		new ResurrectorTask(player);
	}
	
	protected void announce(Set<Player> list, String text)
	{
		for (Player player : list)
			player.sendMessage(text);
	}
	
	public boolean canAttack(Player player, WorldObject target)
	{
		return true;
	}
	
	protected int countOfPositiveStatus()
	{
		int count = 0;
		for (Player player : getPlayerList())
			if (getStatus(player) >= 0)
				count++;
			
		return count;
	}
	
	protected void createNewTeam(int id, String name, int[] color, int[] startPos)
	{
		teams.put(id, new EventTeam(id, name, color, startPos));
	}
	
	protected void createPartyOfTeam(int teamId)
	{
		
		int count = 0;
		Party pt = null;
		
		FastList<Player> list = new FastList<>();
		
		for (Player p : players.keySet())
			if (getTeam(p) == teamId)
				list.add(p);
			
		for (Player player : list)
		{
			if (count % 9 == 0 && list.size() - count != 1)
			{
				pt = new Party(player, player, LootRule.ITEM_LOOTER);
			}
			if (count % 9 < 9)
				player.setParty(pt);
			
			count++;
			
			
		}
	}
	
	protected void divideIntoTeams(int number)
	{
		int i = 0;
		
		while (EventManager.getInstance().players.size() != 0)
		{
			i++;
			Player player = EventManager.getInstance().players.get(Rnd.get(EventManager.getInstance().players.size()));
			
			// skip healers
			// if (player.getClassId().getId() == 16 || player.getClassId().getId() == 97)
			// continue;
			
			players.put(player, new int[]
			{
				i,
				0,
				0
			});
			EventManager.getInstance().players.remove(player);
			if (i == number)
				i = 0;
		}
		
		i = getPlayersOfTeam(1).size() > getPlayersOfTeam(2).size() ? 1 : 0;
		
		// healers here
		// while (EventManager.getInstance().players.size() != 0)
		// {
		// i++;
		// Player player = EventManager.getInstance().players.get(Rnd.get(EventManager.getInstance().players.size()));
		//
		// players.put(player, new int[] { i, 0, 0 });
		// EventManager.getInstance().players.remove(player);
		// if (i == number)
		// i = 0;
		// }
	}
	
	protected void forceSitAll()
	{
		for (Player player : players.keySet())
		{
			if (player == null)
				continue;
			
			player.abortAll(true);
			player.disableAllSkills();
			player.setTarget(null);
			player.setInvul(true);
			// player.setIsImmobilized(true);
			player.getAI().tryToIdle();
		}
	}
	
	protected void forceStandAll()
	{
		for (Player player : players.keySet())
		{
			if (player == null)
				continue;
			
			player.enableAllSkills();
			// player.setIsImmobilized(false);
			player.setInvul(false);
		}
	}
	
	protected void InvisAll()
	{
		for (Player player : players.keySet())
		{
			if (player == null)
				continue;
			
			player.getAttack().stop();
			player.getCast().stop();
			
		}
	}
	
	protected void unInvisAll()
	{
		for (Player player : players.keySet())
		{
			if (player == null)
				continue;
			player.getAttack().stop();
			player.getCast().stop();
		}
	}
	
	public boolean getBoolean(String propName)
	{
		return config.getBoolean(eventId, propName);
	}
	
	public int[] getColor(String owner)
	{
		return config.getColor(eventId, owner);
	}
	
	public int getInt(String propName)
	{
		return config.getInt(eventId, propName);
	}

	
	protected Set<Player> getPlayerList()
	{
		return players.keySet();
	}
	
	protected FastList<Player> getPlayersOfTeam(int team)
	{
		FastList<Player> list = new FastList<>();
		
		for (Player player : getPlayerList())
			if (getTeam(player) == team)
				list.add(player);
			
		return list;
	}
	
	protected EventTeam getPlayersTeam(Player player)
	{
		return teams.get(players.get(player)[0]);
	}
	
	protected FastList<Player> getPlayersWithStatus(int status)
	{
		FastList<Player> list = new FastList<>();
		
		for (Player player : getPlayerList())
			if (getStatus(player) == status)
				list.add(player);
			
		return list;
	}
	
	protected Player getPlayerWithMaxScore()
	{
		Player max = players.head().getNext().getKey();
		
		for (Player player : players.keySet())
			if (players.get(player)[2] > players.get(max)[2])
				max = player;
			
		return max;
	}
	
	public int[] getPosition(String owner, int num)
	{
		return config.getPosition(eventId, owner, num);
	}
	
	protected Player getRandomPlayer()
	{
		FastList<Player> temp = new FastList<>();
		for (Player player : players.keySet())
			temp.add(player);
		
		return temp.get(Rnd.get(temp.size()));
	}
	
	protected Player getRandomPlayerFromTeam(int team)
	{
		FastList<Player> temp = new FastList<>();
		for (Player player : players.keySet())
			if (getTeam(player) == team)
				temp.add(player);
			
		return temp.get(Rnd.get(temp.size()));
	}
	
	protected FastList<Player> getPlayersFromTeamWithStatus(int team, int status)
	{
		FastList<Player> players = getPlayersWithStatus(status);
		FastList<Player> temp = new FastList<>();
		
		for (Player player : players)
			if (getTeam(player) == team)
				temp.add(player);
			
		return temp;
	}
	
	protected Player getRandomPlayerFromTeamWithStatus(int team, int status)
	{
		FastList<Player> temp = getPlayersFromTeamWithStatus(team, status);
		return temp.get(Rnd.get(temp.size()));
	}
	
	public FastList<Integer> getRestriction(String type)
	{
		return config.getRestriction(eventId, type);
	}
	
	public FastList<Integer> getDoor(String type)
	{
		return config.getDoor(eventId, type);
	}
	
	protected int getScore(Player player)
	{
		return players.get(player)[2];
	}
	
	protected int getStatus(Player player)
	{
		return players.get(player)[1];
	}
	
	public String getString(String propName)
	{
		return config.getString(eventId, propName);
	}
	
	public int getTeam(Player player)
	{
		return players.get(player)[0];
	}
	
	protected int getLoseTeam()
	{
		FastList<EventTeam> t = new FastList<>();
		
		for (EventTeam team : teams.values())
		{
			if (t.size() == 0)
			{
				t.add(team);
				continue;
			}
		}
		
		return t.getFirst().getId();
	}
	
	protected int getWinnerTeam()
	{
		FastList<EventTeam> t = new FastList<>();
		
		for (EventTeam team : teams.values())
		{
			if (t.size() == 0)
			{
				t.add(team);
				continue;
			}
			
			if (team.getScore() > t.getFirst().getScore())
			{
				t.clear();
				t.add(team);
				continue;
			}
			
			if (team.getScore() == t.getFirst().getScore())
				t.add(team);
		}
		
		if (t.size() > 1)
			return 0;
		
		return t.getFirst().getId();
	}
	
	protected void giveReward(FastList<Player> players, int id, int ammount)
	{
		for (Player player : players)
		{
			if (player == null)
				continue;
			
			if (player.isPremium())
				player.addItem("Event", id, ammount * 2, player, true);
			else
				player.addItem("Event", id, ammount, player, true);
			
			EventStats.getInstance().tempTable.get(player.getObjectId())[0] = 1;
		}
	}
	
	protected void giveReward(Player player, int id, int ammount)
	{
		EventStats.getInstance().tempTable.get(player.getObjectId())[0] = 1;
		if (player.isPremium())
			player.addItem("Event", id, ammount * 2, player, true);
		else
			player.addItem("Event", id, ammount, player, true);
	}
	
	protected void increasePlayersScore(Player player)
	{
		int old = getScore(player);
		setScore(player, old + 1);
		EventStats.getInstance().tempTable.get(player.getObjectId())[3] = EventStats.getInstance().tempTable.get(player.getObjectId())[3] + 1;
	}
	
	protected void msgToAll(String text)
	{
		for (Player player : players.keySet())
			player.sendMessage(text);
	}
	
	public void onDie(Player victim, Creature killer)
	{
		EventStats.getInstance().tempTable.get(victim.getObjectId())[2] = EventStats.getInstance().tempTable.get(victim.getObjectId())[2] + 1;
	}
	
	public void onKill(Creature victim, Player killer)
	{
		EventStats.getInstance().tempTable.get(killer.getObjectId())[1] = EventStats.getInstance().tempTable.get(killer.getObjectId())[1] + 1;
	}
	
	public void onLogout(Player player)
	{
		if (players.containsKey(player))
			removePlayer(player);
		
		player.setXYZ(EventManager.getInstance().positions.get(player)[0], EventManager.getInstance().positions.get(player)[1], EventManager.getInstance().positions.get(player)[2]);
		player.setTitle(EventManager.getInstance().titles.get(player));
		
		if (teams.size() == 1)
		{
			if (getPlayerList().size() == 1)
				endEvent();
		}
		else
		{
			int t = players.head().getNext().getValue()[0];
			for (Player p : getPlayerList())
				if (getTeam(p) != t)
					return;
				
			endEvent();
		}
	}
	
	public boolean onSay(SayType tell, Player player, String text)
	{
		return true;
	}
	
	public boolean onTalkNpc(Npc npc, Player player)
	{
		return false;
	}
	
	public boolean onUseItem(Player player, ItemInstance item)
	{
		if (EventManager.getInstance().getRestriction("item").contains(item.getItemId()) || getRestriction("item").contains(item.getItemId()))
			return false;
		
		if (item.getItemType() == EtcItemType.POTION && !getBoolean("allowPotions"))
			return false;
		
		if (item.getItemType() == EtcItemType.SCROLL)
			return false;
		
		if (item.getItemType() == EtcItemType.PET_COLLAR)
			return false;
		
		return true;
	}
	
	public boolean isAttakDoord(int id, Creature attacker)
	{
		if (EventManager.getInstance().getDoor("Door").contains(id) || getDoor("Door").contains(id))
			return false;
		
		Door doorInstance = DoorData.getInstance().getDoor(id);
		
		if (doorInstance != null)
		{
			if (attacker instanceof Player && ((Player) attacker).isInFunEvent())
				return true;
			else if (attacker instanceof Summon && ((Summon) attacker).getOwner().isInFunEvent())
				return true;
			else if (attacker instanceof Pet && ((Pet) attacker).getOwner().isInFunEvent())
				return true;
			
		}
		
		return false;
		
	}
	
	public boolean onUseMagic(L2Skill skill)
	{
		if (EventManager.getInstance().getRestriction("skill").contains(skill.getId()) || getRestriction("skill").contains(skill.getId()))
			return false;
		if (skill.getSkillType() == SkillType.HEAL || skill.getSkillType() == SkillType.BALANCE_LIFE || skill.getSkillType() == SkillType.HEAL_PERCENT)
			return false;
		if ((skill.getSkillType() == SkillType.RESURRECT) && !(null instanceof RaidBoss))
			return false;
		if (skill.getSkillType() == SkillType.SUMMON_FRIEND)
			return false;
		if (skill.getSkillType() == SkillType.RECALL)
			return false;
		if (skill.getSkillType() == SkillType.FAKE_DEATH)
			return false;
		if ((skill.getSkillType() == SkillType.HEAL) && (this instanceof DM))
			return false;
		return true;
	}
	
	protected void prepare(Player player)
	{
		if (player.isDead())
			player.doRevive();
		
		if (player.getCast().isCastingNow())
			player.getCast().stop();
		
		// player.getAppearance().setVisible(false);
		
		if (player.getSummon() != null && player.getSummon() instanceof Pet)
			player.getSummon().unSummon(player);
		
		if (player.isMounted())
			player.dismount();
		
		if (getBoolean("removeBuffs"))
		{
			player.stopAllEffects();
			if (player.getSummon() != null)
				player.getSummon().unSummon(player);
		}
		else
		{
			if (player.getSummon() != null)
			{
				ArrayList<L2Skill> summonBuffs = new ArrayList<>();
				summons.put(player, summonBuffs);
			}
		}
		
		ItemInstance wpn = player.getActiveWeaponInstance();
		if (wpn != null && wpn.isHeroItem())
			player.getInventory().unequipItemInBodySlotAndRecord(wpn.getItemId());
		
		if (player.getParty() != null)
		{
			Party party = player.getParty();
			party.removePartyMember(player, MessageType.EXPELLED);
		}
		
		int[] nameColor = getPlayersTeam(player).getTeamColor();
		player.getAppearance().setNameColor(nameColor[0], nameColor[1], nameColor[2]);
		player.setTitle("Score 0");
		
		if (EventManager.getInstance().getBoolean("eventBufferEnabled"))
			EventBuffer.getInstance().buffPlayer(player);
		
		player.getStatus().setMaxCpHpMp();
		player.broadcastUserInfo();
	}
	
	public ArrayList<L2Skill> getSummonBuffs(Player player)
	{
		return summons.get(player);
	}
	
	protected void preparePlayers()
	{
		for (Player player : players.keySet())
			prepare(player);
	}
	
	protected void removePlayer(Player player)
	{
		players.remove(player);
	}
	
	public void reset()
	{
		players.clear();
		summons.clear();
		winnerTeam = 0;
		
		for (EventTeam team : teams.values())
			team.setScore(0);
	}
	
	protected void selectPlayers(int teamId, int playerCount)
	{
		for (int i = 0; i < playerCount; i++)
		{
			Player player = EventManager.getInstance().players.get(Rnd.get(EventManager.getInstance().players.size()));
			players.put(player, new int[]
			{
				teamId,
				0,
				0
			});
			EventManager.getInstance().players.remove(player);
		}
	}
	
	protected void setScore(Player player, int score)
	{
		players.get(player)[2] = score;
		player.setTitle("Score " + score);
		player.broadcastUserInfo();
	}
	
	protected void setStatus(Player player, int status)
	{
		if (players.containsKey(player))
			players.get(player)[1] = status;
	}
	
	protected void setTeam(Player player, int team)
	{
		players.get(player)[0] = team;
	}
	
	protected Spawn spawnNPC(int xPos, int yPos, int zPos, int npcId)
	{
		final NpcTemplate template = NpcData.getInstance().getTemplate(npcId);
		
		try
		{
			final Spawn spawn = new Spawn(template);
			spawn.setLoc(xPos, yPos, zPos, 0);
			spawn.setRespawnDelay(1);
			spawn.doSpawn(false);
			// spawn.init();
			return spawn;
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	protected void teleportPlayer(Player player, int[] coordinates)
	{
		player.teleportTo(coordinates[0] + (Rnd.get(coordinates[3] * 2) - coordinates[3]), coordinates[1] + (Rnd.get(coordinates[3] * 2) - coordinates[3]), coordinates[2], 0);
	}
	
	protected void teleportToTeamPos()
	{
		for (Player player : players.keySet())
			teleportToTeamPos(player);
	}
	
	protected void teleportToTeamPos(Player player)
	{
		int[] pos = getPosition(teams.get(getTeam(player)).getName(), 0);
		teleportPlayer(player, pos);
	}
	
	protected void unspawnNPC(Spawn npcSpawn)
	{
		if (npcSpawn == null)
			return;
		
		npcSpawn.getNpc().deleteMe();
		npcSpawn.setRespawnState(false);
		npcSpawn.doSpawn(false);
	
	}
	
	public int numberOfTeams()
	{
		return teams.size();
	}
	public void askJoinTeam()
	{
		
	}
	protected void sendMsg()
	{
		for (Player player : getPlayerList())
			player.sendPacket(new ExShowScreenMessage(getStartingMsg(), 3000, SMPOS.BOTTOM_RIGHT, false));
	}
	
	protected abstract void endEvent();
	
	protected abstract String getStartingMsg();
	
	protected abstract String getScorebar();
	
	protected abstract void start();
	
	protected abstract void showHtml(Player player, int obj);
	
	protected abstract void schedule(int time);
	
}