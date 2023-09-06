package net.sf.l2j.gameserver.scripting.script.ai.boss;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.l2j.commons.data.StatSet;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.data.SchedulingPattern;
import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.data.SkillTable.FrequentSkill;
import net.sf.l2j.gameserver.data.manager.GrandBossManager;
import net.sf.l2j.gameserver.data.manager.ZoneManager;
import net.sf.l2j.gameserver.enums.EventHandler;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.GrandBoss;
import net.sf.l2j.gameserver.model.actor.instance.Monster;
import net.sf.l2j.gameserver.model.zone.type.BossZone;
import net.sf.l2j.gameserver.network.serverpackets.CameraMode;
import net.sf.l2j.gameserver.network.serverpackets.NormalCamera;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.network.serverpackets.SpecialCamera;
import net.sf.l2j.gameserver.scripting.script.ai.AttackableAIScript;
import net.sf.l2j.gameserver.scripting.script.ai.boss.config.AntharasConfig;
import net.sf.l2j.gameserver.skills.L2Skill;

public class Antharas extends AttackableAIScript
{
	private static final BossZone ANTHARAS_LAIR = ZoneManager.getInstance().getZoneById(110001, BossZone.class);
	
	public static final byte DORMANT = 0; // Antharas is spawned and no one has entered yet. Entry is unlocked.
	public static final byte WAITING = 1; // Antharas is spawned and someone has entered, triggering a 30 minute window for additional people to enter. Entry is unlocked.
	public static final byte FIGHTING = 2; // Antharas is engaged in battle, annihilating his foes. Entry is locked.
	public static final byte DEAD = 3; // Antharas has been killed. Entry is locked.
	
	public static final int ANTHARAS = 29019;
	private final Set<Npc> _minions = ConcurrentHashMap.newKeySet();
	private long _timeTracker = 0; // Time tracker for last attack on ANTHARAS.
	private Player _actualVictim; // Actual target of ANTHARAS.
	private int _minionTimer;
	
	public Antharas()
	{
		super("ai/boss");
		AntharasConfig.init();
		final StatSet info = GrandBossManager.getInstance().getStatSet(ANTHARAS);
		
		switch (GrandBossManager.getInstance().getBossStatus(ANTHARAS))
		{
			case DEAD: // Launch the timer to set DORMANT, or set DORMANT directly if timer expired while offline.
				long temp = (info.getLong("respawn_time") - System.currentTimeMillis());
				if (temp > 0)
					startQuestTimer("ANTHARAS_unlock", null, null, temp);
				else
					GrandBossManager.getInstance().setBossStatus(ANTHARAS, DORMANT);
				break;
			
			case WAITING:
				startQuestTimer("beginning", null, null, AntharasConfig.WAIT_TIME_ANTHARAS);
				
				break;
			
			case FIGHTING:
				final int loc_x = info.getInteger("loc_x");
				final int loc_y = info.getInteger("loc_y");
				final int loc_z = info.getInteger("loc_z");
				final int heading = info.getInteger("heading");
				final int hp = info.getInteger("currentHP");
				final int mp = info.getInteger("currentMP");
				
				final Npc valakas = addSpawn(ANTHARAS, loc_x, loc_y, loc_z, heading, false, 0, false);
				GrandBossManager.getInstance().addBoss((GrandBoss) valakas);
				
				// Update Antharas informations.
				updateAntharas();
				valakas.getStatus().setHpMp(hp, mp);
				valakas.forceRunStance();
				
				// stores current time for inactivity task.
				_timeTracker = System.currentTimeMillis();
				
				// Start timers.
				startQuestTimerAtFixedRate("regen_task", valakas, null, 60000);
				startQuestTimerAtFixedRate("skill_task", valakas, null, 2000);
				break;
		}
	}
	
	@Override
	protected void registerNpcs()
	{
		addEventIds(ANTHARAS, EventHandler.ATTACKED, EventHandler.CREATED, EventHandler.MY_DYING);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("beginning"))
		{
			updateAntharas();
			// Stores current time
			_timeTracker = System.currentTimeMillis();
			
			// Spawn ANTHARAS and set him invul.
			npc = addSpawn(ANTHARAS, 181323, 114850, -7623, 32542, false, 0, false);
			GrandBossManager.getInstance().addBoss((GrandBoss) npc);
			npc.setInvul(true);
			// Launch the cinematic, and tasks (regen + skill).
			startQuestTimer("spawn_1", npc, null, 1000 * 1);
			startQuestTimer("spawn_2", npc, null, 1000 * 3);
			startQuestTimer("spawn_3", npc, null, 1000 * 5);
			startQuestTimer("spawn_4", npc, null, 1000 * 5);
			startQuestTimer("spawn_5", npc, null, 1000 * 7);
			startQuestTimer("spawn_6", npc, null, 1000 * 10);
			
		}
		// Regeneration && inactivity task
		else if (name.equalsIgnoreCase("regen_task"))
		{
			// Inactivity task - 15min
			if (GrandBossManager.getInstance().getBossStatus(ANTHARAS) == FIGHTING)
			{
				if (_timeTracker + 900000 < System.currentTimeMillis())
				{
					// Set it dormant.
					GrandBossManager.getInstance().setBossStatus(ANTHARAS, DORMANT);
					
					// Drop all players from the zone.
					ANTHARAS_LAIR.oustAllPlayers();
					
					// Cancel skill_task and regen_task.
					cancelQuestTimers("regen_task", npc);
					cancelQuestTimers("skill_task", npc);
					// Delete current instance of ANTHARAS.
					npc.deleteMe();
					
					return null;
				}
			}
			
			// Regeneration buff.
			if (Rnd.get(30) == 0)
			{
				L2Skill skillRegen;
				final double hpRatio = npc.getStatus().getHpRatio();
				
				// Current HPs are inferior to 25% ; apply lvl 4 of regen skill.
				if (hpRatio < 0.25)
					skillRegen = SkillTable.getInstance().getInfo(4691, 4);
				// Current HPs are inferior to 50% ; apply lvl 3 of regen skill.
				else if (hpRatio < 0.5)
					skillRegen = SkillTable.getInstance().getInfo(4691, 3);
				// Current HPs are inferior to 75% ; apply lvl 2 of regen skill.
				else if (hpRatio < 0.75)
					skillRegen = SkillTable.getInstance().getInfo(4691, 2);
				else
					skillRegen = SkillTable.getInstance().getInfo(4691, 1);
				
				skillRegen.getEffects(npc, npc);
			}
		}
		
		else if (name.equalsIgnoreCase("spawn_cubes"))
		{
			addSpawn(12324, 177615, 114941, -7709, 0, false, 900000, false);
			int radius = 1600;
			for (int i = 0; i < 20; i++)
			{
				int x = (int) (radius * Math.cos(i * .331)); // .331~2pi/19
				int y = (int) (radius * Math.sin(i * .331));
				addSpawn(31859, 177615 + x, 114941 + y, -7709, 0, false, 900000, false);
			}
			cancelQuestTimer("antharas_despawn", npc, null);
			startQuestTimer("remove_players", npc, null, 900000);
		}
		
		// Spawn cinematic, regen_task and choose of skill.
		else if (name.equalsIgnoreCase("spawn_1"))
		{
			
			for (Creature pc : ANTHARAS_LAIR.getCharacters())
			{
				if ((pc instanceof Player))
				{
					
					pc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1600, 13, 10, 0, 20000, 0, 0, 0, 0));
				}
			}
		}
		else if (name.equalsIgnoreCase("spawn_2"))
		{
			npc.broadcastPacket(new SocialAction(npc, 1));
			
			for (Creature pc : ANTHARAS_LAIR.getCharacters())
			{
				if ((pc instanceof Player))
				{
					
					pc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1600, 13, 0, 6000, 20000, 0, 0, 0, 0));
				}
			}
			
		}
		else if (name.equalsIgnoreCase("spawn_3"))
		{
			for (Creature pc : ANTHARAS_LAIR.getCharacters())
			{
				if ((pc instanceof Player))
				{
					
					pc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1850, 0, 0, 0, 10000, 0, 0, 0, 0));
				}
			}
		}
		else if (name.equalsIgnoreCase("spawn_4"))
		{
			npc.broadcastPacket(new SocialAction(npc, 2));
			for (Creature pc : ANTHARAS_LAIR.getCharacters())
			{
				if ((pc instanceof Player))
				{
					
					pc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1850, 0, 0, 22000, 30000, 0, 0, 0, 0));
				}
			}
		}
		else if (name.equalsIgnoreCase("spawn_5"))
		{
			
			for (Creature pc : ANTHARAS_LAIR.getCharacters())
			{
				if ((pc instanceof Player))
				{
					
					pc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1850, 0, 0, 300, 7000, 0, 0, 0, 0));
				}
			}
			
		}
		else if (name.equalsIgnoreCase("spawn_6"))
		{
			
			GrandBossManager.getInstance().setBossStatus(ANTHARAS, FIGHTING);
			npc.setInvul(false);
			
			for (Creature pc : ANTHARAS_LAIR.getCharacters())
			{
				if ((pc instanceof Player))
				{
					
					pc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1200, 20, 10, 0, 13000, 0, 0, 0, 0));
					pc.sendPacket(new CameraMode(0));
					pc.sendPacket(NormalCamera.STATIC_PACKET);
				}
			}
			
			startQuestTimerAtFixedRate("regen_task", npc, null, 60000);
			startQuestTimerAtFixedRate("skill_task", npc, null, 2000);
			
			if (AntharasConfig.SPAWN_MINIONS)
				startQuestTimerAtFixedRate("minions_spawn", npc, null, _minionTimer);
			
		}
		
		else if (name.equalsIgnoreCase("minions_spawn"))
		{
			boolean isBehemoth = Rnd.get(100) < 60;
			int mobNumber = isBehemoth ? 2 : 3;
			
			// Set spawn.
			for (int i = 0; i < mobNumber; i++)
			{
				if (_minions.size() > 9)
					break;
				
				final int npcId = isBehemoth ? 29069 : Rnd.get(29070, 29076);
				final Npc dragon = addSpawn(npcId, npc.getX() + Rnd.get(-200, 200), npc.getY() + Rnd.get(-200, 200), npc.getZ(), 0, false, 0, true);
				((Monster) dragon).setRaidRelated();
				
				_minions.add(dragon);
				
				final Player victim = getRandomPlayer(dragon);
				if (victim != null)
					dragon.forceAttack(victim, 200);
				
				if (!isBehemoth)
					startQuestTimer("self_destruct", dragon, null, (_minionTimer / 3));
			}
		}
		else if (name.equalsIgnoreCase("skill_task"))
			callSkillAI(npc);
		else if (name.equalsIgnoreCase("ANTHARAS_unlock"))
			GrandBossManager.getInstance().setBossStatus(ANTHARAS, DORMANT);
		else if (name.equalsIgnoreCase("remove_players"))
			ANTHARAS_LAIR.oustAllPlayers();
		
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (npc.isInvul())
			return;
		
		if (attacker instanceof Playable)
		{
			// Curses
			if (attacker.testCursesOnAttack(npc))
				return;
			
			// Refresh timer on every hit.
			_timeTracker = System.currentTimeMillis();
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		npc.disableCoreAi(true);
		
		super.onCreated(npc);
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		if (!AntharasConfig.FWA_FIXTIMEPATTERNOFANTHARAS.isEmpty())
		{
			// Cancel skill_task and regen_task.
			cancelQuestTimers("regen_task", npc);
			cancelQuestTimers("skill_task", npc);
			cancelQuestTimers("minions_spawn", npc);
			
			GrandBossManager.getInstance().setBossStatus(ANTHARAS, DEAD);
			startQuestTimer("spawn_cubes", null, null, 8000);
			
			startQuestTimer("ANTHARAS_unlock", null, null, getRespawnInterval());
			
			// also save the respawn time so that the info is maintained past reboots
			StatSet info = GrandBossManager.getInstance().getStatSet(ANTHARAS);
			info.set("respawn_time", System.currentTimeMillis() + getRespawnInterval());
			GrandBossManager.getInstance().setStatSet(ANTHARAS, info);
		}
		else
		{
			// Cancel skill_task and regen_task.
			cancelQuestTimers("regen_task", npc);
			cancelQuestTimers("skill_task", npc);
			cancelQuestTimers("minions_spawn", npc);
			
			GrandBossManager.getInstance().setBossStatus(ANTHARAS, DEAD);
			
			startQuestTimer("ANTHARAS_unlock", null, null, getRespawnInterval());
			// also save the respawn time so that the info is maintained past reboots
			StatSet info = GrandBossManager.getInstance().getStatSet(ANTHARAS);
			info.set("respawn_time", System.currentTimeMillis() + getRespawnInterval());
			GrandBossManager.getInstance().setStatSet(ANTHARAS, info);
		}
		super.onMyDying(npc, killer);
	}
	
	private void callSkillAI(Npc npc)
	{
		if (npc.isInvul())
			return;
		
		// Pickup a target if no or dead victim. 10% luck he decides to reconsiders his target.
		if (_actualVictim == null || _actualVictim.isDead() || !npc.knows(_actualVictim) || Rnd.get(10) == 0)
			_actualVictim = getRandomPlayer(npc);
		
		// If result is still null, ANTHARAS will roam. Don't go deeper in skill AI.
		if (_actualVictim == null)
		{
			if (Rnd.get(10) == 0)
				npc.moveUsingRandomOffset(1400);
			
			return;
		}
		
		npc.getAI().tryToCast(_actualVictim, getRandomSkill(npc), false, false, 0);
	}
	
	/**
	 * Pick a random skill.<br>
	 * ANTHARAS will mostly use utility skills. If ANTHARAS feels surrounded, he will use AoE skills.<br>
	 * Lower than 50% HPs, he will begin to use Meteor skill.
	 * @param npc ANTHARAS
	 * @return a usable skillId
	 */
	private static L2Skill getRandomSkill(Npc npc)
	{
		final double hpRatio = npc.getStatus().getHpRatio();
		
		// Find enemies surrounding Antharas.
		final int[] playersAround = getPlayersCountInPositions(1100, npc);
		
		if (hpRatio < 0.25)
		{
			if (Rnd.get(100) < 30)
				return FrequentSkill.ANTHARAS_MOUTH.getSkill();
			
			if (playersAround[1] >= 10 && Rnd.get(100) < 80)
				return FrequentSkill.ANTHARAS_TAIL.getSkill();
			
			if (playersAround[0] >= 10)
			{
				if (Rnd.get(100) < 40)
					return FrequentSkill.ANTHARAS_DEBUFF.getSkill();
				
				if (Rnd.get(100) < 10)
					return FrequentSkill.ANTHARAS_JUMP.getSkill();
			}
			
			if (Rnd.get(100) < 10)
				return FrequentSkill.ANTHARAS_METEOR.getSkill();
		}
		else if (hpRatio < 0.5)
		{
			if (playersAround[1] >= 10 && Rnd.get(100) < 80)
				return FrequentSkill.ANTHARAS_TAIL.getSkill();
			
			if (playersAround[0] >= 10)
			{
				if (Rnd.get(100) < 40)
					return FrequentSkill.ANTHARAS_DEBUFF.getSkill();
				
				if (Rnd.get(100) < 10)
					return FrequentSkill.ANTHARAS_JUMP.getSkill();
			}
			
			if (Rnd.get(100) < 7)
				return FrequentSkill.ANTHARAS_METEOR.getSkill();
		}
		else if (hpRatio < 0.75)
		{
			if (playersAround[1] >= 10 && Rnd.get(100) < 80)
				return FrequentSkill.ANTHARAS_TAIL.getSkill();
			
			if (playersAround[0] >= 10 && Rnd.get(100) < 10)
				return FrequentSkill.ANTHARAS_JUMP.getSkill();
			
			if (Rnd.get(100) < 5)
				return FrequentSkill.ANTHARAS_METEOR.getSkill();
		}
		else
		{
			if (playersAround[1] >= 10 && Rnd.get(100) < 80)
				return FrequentSkill.ANTHARAS_TAIL.getSkill();
			
			if (Rnd.get(100) < 3)
				return FrequentSkill.ANTHARAS_METEOR.getSkill();
		}
		
		if (Rnd.get(100) < 6)
			return FrequentSkill.ANTHARAS_BREATH.getSkill();
		
		if (Rnd.get(100) < 50)
			return FrequentSkill.ANTHARAS_NORMAL_ATTACK.getSkill();
		
		if (Rnd.get(100) < 5)
		{
			if (Rnd.get(100) < 50)
				return FrequentSkill.ANTHARAS_FEAR.getSkill();
			
			return FrequentSkill.ANTHARAS_SHORT_FEAR.getSkill();
		}
		
		return FrequentSkill.ANTHARAS_NORMAL_ATTACK_EX.getSkill();
	}
	
	private void updateAntharas()
	{
		final int playersNumber = ANTHARAS_LAIR.getAllowedPlayers().size();
		if (playersNumber < 45)
		{
			_minionTimer = 180000;
		}
		else if (playersNumber < 63)
		{
			_minionTimer = 150000;
		}
		else
		{
			_minionTimer = 120000;
		}
	}
	
	private static long getRespawnInterval()
	{
		SchedulingPattern timePattern = null;
		
		long now = System.currentTimeMillis();
		try
		{
			timePattern = new SchedulingPattern(AntharasConfig.FWA_FIXTIMEPATTERNOFANTHARAS);
			long delay = timePattern.next(now) - now;
			return Math.max(60000, delay);
		}
		catch (SchedulingPattern.InvalidPatternException ipe)
		{
			throw new RuntimeException("Invalid respawn data \"" + AntharasConfig.FWA_FIXTIMEPATTERNOFANTHARAS + "\" in " + Antharas.class.getSimpleName(), ipe);
		}
	}
	
}