package net.sf.l2j.gameserver.scripting.script.ai.boss;

import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.commons.data.StatSet;
import net.sf.l2j.commons.pool.ThreadPool;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.data.SchedulingPattern;
import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.data.manager.GrandBossManager;
import net.sf.l2j.gameserver.data.manager.ZoneManager;
import net.sf.l2j.gameserver.enums.EventHandler;
import net.sf.l2j.gameserver.geoengine.GeoEngine;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.GrandBoss;
import net.sf.l2j.gameserver.model.location.SpawnLocation;
import net.sf.l2j.gameserver.model.zone.type.BossZone;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage.SMPOS;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.SpecialCamera;
import net.sf.l2j.gameserver.network.serverpackets.StopMove;
import net.sf.l2j.gameserver.scripting.script.ai.AttackableAIScript;
import net.sf.l2j.gameserver.scripting.script.ai.boss.config.LindviorConfig;
import net.sf.l2j.gameserver.skills.L2Skill;

/**
 * @author BAN-L2JDEV
 */
public final class Lindvior extends AttackableAIScript
{
	private static final BossZone LINDVIOR_LAIR = ZoneManager.getInstance().getZoneById(110018, BossZone.class);
	private static Npc Plagueseeker = null;
	private long _lastAttack = 0L;
	private Creature _actualVictim; // Actual target of Lindvior.
	
	public static final int LINDVIOR = LindviorConfig.LIDVIOR_BOSS_ID; // Lindvior id used for status updates.
	
	public static final byte DORMANT = 0; // Lindvior is spawned and no one has entered yet. Entry is unlocked.
	public static final byte WAITING = 1; // Lindvior is spawned and someone has entered, triggering a 30 minute window for additional people to enter. Entry is unlocked.
	public static final byte FIGHTING = 2; // Lindvior is engaged in battle, annihilating his foes. Entry is locked.
	public static final byte DEAD = 3; // Lindvior has been killed. Entry is locked.
	
	private static final SpawnLocation[] CUBE_LOC =
	{
		new SpawnLocation(44094, -30684, -1408, 0),
		new SpawnLocation(46464, -30685, -1408, 0),
		new SpawnLocation(48796, -28348, -1408, 0),
		new SpawnLocation(48811, -25963, -1408, 0),
		new SpawnLocation(46479, -23513, -1408, 0),
		new SpawnLocation(44100, -23490, -1408, 0)
	};
	
	private static final int[] Lindvior_Normal_Attack =
	{
		54690,
		54691
	};
	
	private static final int[] LINDVIOR_SKILLS =
	{
		54683,
		54111,
		55092,
		54687,
		54688,
		54689
	};
	
	public Lindvior()
	{
		super("ai/boss");
		LindviorConfig.ini();
		final StatSet info = GrandBossManager.getInstance().getStatSet(LINDVIOR);
		
		switch (GrandBossManager.getInstance().getBossStatus(LINDVIOR))
		{
			case DEAD: // Launch the timer to set DORMANT, or set DORMANT directly if timer expired while offline.
				long temp = (info.getLong("respawn_time") - System.currentTimeMillis());
				if (temp > 0)
					startQuestTimer("lindvior_unlock", null, null, temp);
				else
					GrandBossManager.getInstance().setBossStatus(LINDVIOR, DORMANT);
				break;
			
			case WAITING:
				startQuestTimer("beginning", null, null, LindviorConfig.WAIT_TIME_LINDVIOR);
				break;
			
			case FIGHTING:
				final int loc_x = info.getInteger("loc_x");
				final int loc_y = info.getInteger("loc_y");
				final int loc_z = info.getInteger("loc_z");
				final int heading = info.getInteger("heading");
				final int hp = info.getInteger("currentHP");
				final int mp = info.getInteger("currentMP");
				
				final Npc lindvior = addSpawn(LINDVIOR, loc_x, loc_y, loc_z, heading, false, 0, false);
				GrandBossManager.getInstance().addBoss((GrandBoss) lindvior);
				
				lindvior.getStatus().setHpMp(hp, mp);
				lindvior.forceRunStance();
				
				// Start timers.
				startQuestTimerAtFixedRate("skill_task", lindvior, null, 2000);
				startQuestTimerAtFixedRate("action_task", lindvior, null, 2000);
				
				break;
		}
	}
	
	@Override
	protected void registerNpcs()
	{
		addEventIds(LINDVIOR, EventHandler.ATTACKED, EventHandler.CREATED, EventHandler.MY_DYING);
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
			_lastAttack = System.currentTimeMillis();
		}
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		Plagueseeker = npc;
		Plagueseeker.setInvul(true);
		paralyzeAll(true);
		startSpecialCamera(false);
		startQuestTimer("skill_task", npc, null, 2000);
		super.onCreated(npc);
		
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		// raid killed
		if (npc.getNpcId() == LINDVIOR)
		{
			if (!LindviorConfig.FWA_FIXTIMEPATTERNOFLINDVIOR.isEmpty())
			{
				if (npc.getNpcId() == LINDVIOR)
				{
					cancelQuestTimer("inactivity_task", npc, null);
					cancelQuestTimer("skill_task", npc, null);
					
					World.toAllOnlinePlayers(new ExShowScreenMessage("HONORABLE WARRIORS HAVE DRIVEN OFF LINDVIOR THE EVIL WIND DRAGON", 7500, SMPOS.TOP_CENTER, true));
					
					startQuestTimer("die_3", npc, null, 8000L); // 8000
					
					startQuestTimer("death_1", npc, null, 30); // 300
					startQuestTimer("death_2", npc, null, 60); // 300
					startQuestTimer("death_3", npc, null, 80); // 3200
					
					GrandBossManager.getInstance().setBossStatus(LINDVIOR, DEAD);
					// Start respawn timer.
					startQuestTimer("lindvior_unlock", null, null, getRespawnInterval());
					
					// Save the respawn time so that the info is maintained past reboots
					final StatSet info = GrandBossManager.getInstance().getStatSet(LINDVIOR);
					info.set("respawn_time", System.currentTimeMillis() + getRespawnInterval());
					GrandBossManager.getInstance().setStatSet(LINDVIOR, info);
				}
				
			}
			clear();
		}
		
		super.onMyDying(npc, killer);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("beginning"))
		{
			_lastAttack = System.currentTimeMillis();
			
			// Spawn Lidvior and set him invul.
			npc = addSpawn(LINDVIOR, 44145, -25322, -1400, 0, false, 0, false);
			GrandBossManager.getInstance().addBoss((GrandBoss) npc);
			npc.setInvul(true);
			startQuestTimer("camera", npc, null, 16);
			startQuestTimer("skill_task", npc, null, 2000);
		}
		else if (name.contains("camera"))
		{
			broadcastCameraEnter(name);
			startQuestTimer("skill_task", npc, null, 2000);
		}
		
		else if (name.equalsIgnoreCase("skill_task"))
		{
			
			// Pickup a target if no or dead victim. If Baium was hitting an angel, 50% luck he reconsiders his target. 10% luck he decides to reconsiders his target.
			if (_actualVictim == null || _actualVictim.isDead() || !npc.knows(_actualVictim))
				_actualVictim = getRandomTarget(npc);
			
			final L2Skill skill = SkillTable.getInstance().getInfo(getRandomSkill(npc), 1);
			
			npc.getAI().tryToCast(_actualVictim, skill);
			
		}
		else if (name.equalsIgnoreCase("inactivity_task"))
		{
			if (_lastAttack + 900000 < System.currentTimeMillis())
			{
				LINDVIOR_LAIR.oustAllPlayers();
				clear();
				Plagueseeker.deleteMe();
				cancelQuestTimers("skill_task", npc);
			}
		}
		else if (name.equalsIgnoreCase("death_1"))
			LINDVIOR_LAIR.broadcastPacket(new SpecialCamera(Plagueseeker.getObjectId(), 4150, 200, 25, 2500, 2500, 0, 0, 0, 0));
		else if (name.equalsIgnoreCase("death_2"))
		{
			LINDVIOR_LAIR.broadcastPacket(new MagicSkillUse(Plagueseeker, Plagueseeker, 54683, 1, 3500, 0));
			
			LINDVIOR_LAIR.broadcastPacket(new SpecialCamera(Plagueseeker.getObjectId(), 3500, 200, 15, 2500, 2500, 0, 0, 0, 0));
		}
		else if (name.equalsIgnoreCase("death_3"))
		{
			LINDVIOR_LAIR.broadcastPacket(new SpecialCamera(Plagueseeker.getObjectId(), 4150, 200, 10, 2500, 2500, 0, 0, 0, 0));
			Plagueseeker.setInvul(false);
			paralyzeAll(false);
		}
		else if (name.equalsIgnoreCase("die_3"))
		{
			for (SpawnLocation loc : CUBE_LOC)
				addSpawn(31759, loc, false, 900000, false);
			startQuestTimer("remove_players", null, null, 900000);
		}
		else if (name.equalsIgnoreCase("lindvior_unlock"))
			GrandBossManager.getInstance().setBossStatus(LINDVIOR, DORMANT);
		return super.onTimer(name, npc, player);
	}
	
	private void runTasks()
	{
		paralyzeAll(false);
		Plagueseeker.setInvul(false);
		
		GrandBossManager.getInstance().setBossStatus(LINDVIOR, FIGHTING);
		startQuestTimer("skill_task", Plagueseeker, null, 2000);
		startQuestTimer("inactivity_task", Plagueseeker, null, 900000);
	}
	
	private static void paralyzeAll(boolean val)
	{
		for (Creature creature : LINDVIOR_LAIR.getCharacters())
		{
			creature.setIsParalyzed(val);
			if (val)
				creature.broadcastPacket(new StopMove(creature));
		}
		
	}
	
	private void startSpecialCamera(boolean death)
	{
		if (death)
		{
			startQuestTimer("death_1", Plagueseeker, null, 1000);
			startQuestTimer("death_2", Plagueseeker, null, 3400);
			startQuestTimer("death_3", Plagueseeker, null, 5800);
			
		}
		else
		{
			startQuestTimer("camera_1", Plagueseeker, null, 1000);
			startQuestTimer("camera_2", Plagueseeker, null, 3400);
			startQuestTimer("camera_3", Plagueseeker, null, 5800);
			startQuestTimer("camera_4", Plagueseeker, null, 8200);
			startQuestTimer("camera_5", Plagueseeker, null, 9200);
		}
		
	}
	
	private void broadcastCameraEnter(String event)
	{
		if (event.equalsIgnoreCase("camera_1"))
			LINDVIOR_LAIR.broadcastPacket(new SpecialCamera(Plagueseeker.getObjectId(), 3150, 200, 25, 2500, 2500, 0, 0, 0, 0));
		else if (event.equalsIgnoreCase("camera_2"))
		{
			LINDVIOR_LAIR.broadcastPacket(new SpecialCamera(Plagueseeker.getObjectId(), 1500, 200, 15, 2500, 2500, 0, 0, 0, 0));
			World.toAllOnlinePlayers(new ExShowScreenMessage("LINDVIOR DRAGON ENTER WORLD", 7500, SMPOS.TOP_CENTER, true));
			LINDVIOR_LAIR.broadcastPacket(new MagicSkillUse(Plagueseeker, Plagueseeker, 54683, 1, 3500, 0));
		}
		else if (event.equalsIgnoreCase("camera_3"))
			LINDVIOR_LAIR.broadcastPacket(new SpecialCamera(Plagueseeker.getObjectId(), 3150, 200, 10, 2500, 2500, 0, 0, 0, 0));
		else if (event.equalsIgnoreCase("camera_4"))
			LINDVIOR_LAIR.broadcastPacket(new SpecialCamera(Plagueseeker.getObjectId(), 3150, 200, 20, 2500, 2500, 0, 0, 0, 0));
		else if (event.equalsIgnoreCase("camera_5"))
		{
			LINDVIOR_LAIR.broadcastPacket(new MagicSkillUse(Plagueseeker, Plagueseeker, 55092, 1, 3500, 0));
			LINDVIOR_LAIR.broadcastPacket(new SpecialCamera(Plagueseeker.getObjectId(), 3150, 200, 20, 1700, 1700, 0, 0, 1, 0));
			ThreadPool.schedule(() -> runTasks(), 1700L);
		}
		
	}
	
	private static int getRandomSkill(Npc npc)
	{
		
		final double hpRatio = npc.getStatus().getHp() / npc.getStatus().getMaxHp();
		
		final int _Skillid = LINDVIOR_SKILLS[Rnd.get(LINDVIOR_SKILLS.length)];
		final int[] playersAround = getPlayersCountInPositions(1100, npc);
		
		if (Rnd.get(100) < 25 && _Skillid != LINDVIOR_SKILLS[4])
			return _Skillid;
		if (Rnd.get(100) < 25 && _Skillid == LINDVIOR_SKILLS[4] && hpRatio < 50.0 || playersAround[1] >= 10 && Rnd.get(100) < 25)
			return _Skillid;
		
		return Lindvior_Normal_Attack[Rnd.get(Lindvior_Normal_Attack.length)];
	}
	
	private static Creature getRandomTarget(Npc npc)
	{
		final List<Creature> result = new ArrayList<>();
		
		for (Creature obj : LINDVIOR_LAIR.getKnownTypeInside(Creature.class))
		{
			if (obj instanceof Player)
			{
				if (obj.isDead() || !(GeoEngine.getInstance().canSeeTarget(npc, obj)))
					continue;
				
				if (((Player) obj).isGM() && !((Player) obj).getAppearance().isVisible())
					continue;
				result.add(obj);
			}
		}
		
		return (result.isEmpty()) ? null : Rnd.get(result);
	}
	
	private void clear()
	{
		cancelQuestTimer("action_task", Plagueseeker, null);
		cancelQuestTimer("inactivity_task", Plagueseeker, null);
		cancelQuestTimer("mass_kill_cin", Plagueseeker, null);
		cancelQuestTimer("skill_task", Plagueseeker, null);
		_lastAttack = 0L;
		
	}
	
	private static long getRespawnInterval()
	{
		SchedulingPattern timePattern = null;
		
		long now = System.currentTimeMillis();
		try
		{
			timePattern = new SchedulingPattern(LindviorConfig.FWA_FIXTIMEPATTERNOFLINDVIOR);
			long delay = timePattern.next(now) - now;
			return Math.max(60000, delay);
		}
		catch (SchedulingPattern.InvalidPatternException ipe)
		{
			throw new RuntimeException("Invalid respawn data \"" + LindviorConfig.FWA_FIXTIMEPATTERNOFLINDVIOR + "\" in " + Lindvior.class.getSimpleName(), ipe);
		}
	}
}