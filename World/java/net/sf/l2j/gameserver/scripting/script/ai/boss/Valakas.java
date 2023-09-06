package net.sf.l2j.gameserver.scripting.script.ai.boss;

import java.util.concurrent.TimeUnit;

import net.sf.l2j.commons.data.StatSet;
import net.sf.l2j.commons.logging.CLogger;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.data.SchedulingPattern;
import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.data.manager.GrandBossManager;
import net.sf.l2j.gameserver.data.manager.ZoneManager;
import net.sf.l2j.gameserver.enums.EventHandler;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.GrandBoss;
import net.sf.l2j.gameserver.model.location.SpawnLocation;
import net.sf.l2j.gameserver.model.zone.type.BossZone;
import net.sf.l2j.gameserver.network.serverpackets.CameraMode;
import net.sf.l2j.gameserver.network.serverpackets.NormalCamera;
import net.sf.l2j.gameserver.network.serverpackets.PlaySound;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.network.serverpackets.SpecialCamera;
import net.sf.l2j.gameserver.scripting.script.ai.AttackableAIScript;
import net.sf.l2j.gameserver.scripting.script.ai.boss.config.ValakasConfig;
import net.sf.l2j.gameserver.skills.L2Skill;

public class Valakas extends AttackableAIScript
{
	private static final BossZone VALAKAS_LAIR = ZoneManager.getInstance().getZoneById(110010, BossZone.class);
	private static final CLogger _log = new CLogger(Valakas.class.getName());
	
	public static final byte DORMANT = 0; // Valakas is spawned and no one has entered yet. Entry is unlocked.
	public static final byte WAITING = 1; // Valakas is spawned and someone has entered, triggering a 30 minute window for additional people to enter. Entry is unlocked.
	public static final byte FIGHTING = 2; // Valakas is engaged in battle, annihilating his foes. Entry is locked.
	public static final byte DEAD = 3; // Valakas has been killed. Entry is locked.
	private Npc _cubic;
	
	private static final int[] FRONT_SKILLS =
	{
		4681,
		4682,
		4683,
		4684,
		4689
	};
	
	private static final int[] BEHIND_SKILLS =
	{
		4685,
		4686,
		4688
	};
	
	private static final int LAVA_SKIN = 4680;
	private static final int METEOR_SWARM = 4690;
	
	private static final SpawnLocation[] CUBE_LOC =
	{
		new SpawnLocation(214880, -116144, -1644, 0),
		new SpawnLocation(213696, -116592, -1644, 0),
		new SpawnLocation(212112, -116688, -1644, 0),
		new SpawnLocation(211184, -115472, -1664, 0),
		new SpawnLocation(210336, -114592, -1644, 0),
		new SpawnLocation(211360, -113904, -1644, 0),
		new SpawnLocation(213152, -112352, -1644, 0),
		new SpawnLocation(214032, -113232, -1644, 0),
		new SpawnLocation(214752, -114592, -1644, 0),
		new SpawnLocation(209824, -115568, -1421, 0),
		new SpawnLocation(210528, -112192, -1403, 0),
		new SpawnLocation(213120, -111136, -1408, 0),
		new SpawnLocation(215184, -111504, -1392, 0),
		new SpawnLocation(215456, -117328, -1392, 0),
		new SpawnLocation(213200, -118160, -1424, 0)
	};
	
	public static final int VALAKAS = 29028;
	
	private long _timeTracker = 0; // Time tracker for last attack on Valakas.
	private Player _actualVictim; // Actual target of Valakas.
	
	public Valakas()
	{
		super("ai/boss");
		
		final StatSet info = GrandBossManager.getInstance().getStatSet(VALAKAS);
		
		switch (GrandBossManager.getInstance().getBossStatus(VALAKAS))
		{
			case DEAD: // Launch the timer to set DORMANT, or set DORMANT directly if timer expired while offline.
				long temp = (info.getLong("respawn_time") - System.currentTimeMillis());
				if (temp > 0)
					startQuestTimer("valakas_unlock", null, null, temp);
				else
					GrandBossManager.getInstance().setBossStatus(VALAKAS, DORMANT);
				break;
			
			case WAITING:
				startQuestTimer("beginning", null, null, ValakasConfig.WAIT_TIME_VALAKAS);
				break;
			
			case FIGHTING:
				final int loc_x = info.getInteger("loc_x");
				final int loc_y = info.getInteger("loc_y");
				final int loc_z = info.getInteger("loc_z");
				final int heading = info.getInteger("heading");
				final int hp = info.getInteger("currentHP");
				final int mp = info.getInteger("currentMP");
				
				final Npc valakas = addSpawn(VALAKAS, loc_x, loc_y, loc_z, heading, false, 0, false);
				GrandBossManager.getInstance().addBoss((GrandBoss) valakas);
				
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
		addEventIds(VALAKAS, EventHandler.ATTACKED, EventHandler.CREATED, EventHandler.MY_DYING);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("beginning"))
		{
			// Stores current time
			_timeTracker = System.currentTimeMillis();
			
			// Spawn Valakas and set him invul.
			npc = addSpawn(VALAKAS, 212852, -114842, -1632, 0, false, 0, false);
			GrandBossManager.getInstance().addBoss((GrandBoss) npc);
			npc.setInvul(true);
			
			// Sound + socialAction.
			for (Player plyr : VALAKAS_LAIR.getKnownTypeInside(Player.class))
			{
				plyr.sendPacket(new PlaySound(1, "B03_A", npc));
				plyr.sendPacket(new SocialAction(npc, 3));
			}
			
			// Launch the cinematic, and tasks (regen + skill).
			startQuestTimer("spawn_1", npc, null, 1000 * 1);
			startQuestTimer("spawn_2", npc, null, 1000 * 3);
			startQuestTimer("spawn_3", npc, null, 1000 * 5);
			startQuestTimer("spawn_4", npc, null, 1000 * 5);
			startQuestTimer("spawn_5", npc, null, 1000 * 7);
			startQuestTimer("spawn_6", npc, null, 1000 * 10);
			startQuestTimer("spawn_7", npc, null, 1000 * 12);
			startQuestTimer("spawn_8", npc, null, 1000 * 13);
			startQuestTimer("spawn_9", npc, null, 1000 * 14);
			startQuestTimer("spawn_10", npc, null, 1000 * 15);
		}
		// Regeneration && inactivity task
		else if (name.equalsIgnoreCase("regen_task"))
		{
			// Inactivity task - 15min
			if (GrandBossManager.getInstance().getBossStatus(VALAKAS) == FIGHTING)
			{
				if (_timeTracker + 900000 < System.currentTimeMillis())
				{
					// Set it dormant.
					GrandBossManager.getInstance().setBossStatus(VALAKAS, DORMANT);
					
					// Drop all players from the zone.
					VALAKAS_LAIR.oustAllPlayers();
					
					// Cancel skill_task and regen_task.
					cancelQuestTimers("regen_task", npc);
					cancelQuestTimers("skill_task", npc);
					
					// Delete current instance of Valakas.
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
		// Spawn cinematic, regen_task and choose of skill.
		else if (name.equalsIgnoreCase("spawn_1"))
			VALAKAS_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1800, 180, -1, 1500, 15000, 0, 0, 1, 0));
		else if (name.equalsIgnoreCase("spawn_2"))
			VALAKAS_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1300, 180, -5, 3000, 15000, 0, -5, 1, 0));
		else if (name.equalsIgnoreCase("spawn_3"))
			VALAKAS_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 500, 180, -8, 15000, 0, 0, 60, 1, 0));
		else if (name.equalsIgnoreCase("spawn_4"))
			VALAKAS_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 800, 180, -8, 2700, 15000, 0, 30, 1, 0));
		else if (name.equalsIgnoreCase("spawn_5"))
			VALAKAS_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 200, 250, 70, 0, 15000, 30, 80, 1, 0));
		else if (name.equalsIgnoreCase("spawn_6"))
			VALAKAS_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1100, 250, 70, 2500, 15000, 30, 80, 1, 0));
		else if (name.equalsIgnoreCase("spawn_7"))
			VALAKAS_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 700, 150, 30, 0, 15000, -10, 60, 1, 0));
		else if (name.equalsIgnoreCase("spawn_8"))
			VALAKAS_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1200, 150, 20, 2900, 15000, -10, 30, 1, 0));
		else if (name.equalsIgnoreCase("spawn_9"))
			VALAKAS_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 750, 170, -10, 3400, 15000, 10, -15, 1, 0));
		else if (name.equalsIgnoreCase("spawn_10"))
		{
			GrandBossManager.getInstance().setBossStatus(VALAKAS, FIGHTING);
			npc.setInvul(false);
			
			startQuestTimerAtFixedRate("regen_task", npc, null, 60000);
			startQuestTimerAtFixedRate("skill_task", npc, null, 2000);
			
			
			
			for (Creature pc : VALAKAS_LAIR.getCharacters())
			{
				if ((pc instanceof Player))
				{
					pc.sendPacket(new CameraMode(0));
					pc.sendPacket(NormalCamera.STATIC_PACKET);
				}
			}

		}
		// Death cinematic, spawn of Teleport Cubes.
		else if (name.equalsIgnoreCase("die_1"))
			VALAKAS_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 2000, 130, -1, 0, 10000, 0, 0, 1, 1));
		else if (name.equalsIgnoreCase("die_2"))
			VALAKAS_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1100, 210, -5, 3000, 10000, -13, 0, 1, 1));
		else if (name.equalsIgnoreCase("die_3"))
			VALAKAS_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1300, 200, -8, 3000, 10000, 0, 15, 1, 1));
		else if (name.equalsIgnoreCase("die_4"))
			VALAKAS_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1000, 190, 0, 500, 10000, 0, 10, 1, 1));
		else if (name.equalsIgnoreCase("die_5"))
			VALAKAS_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1700, 120, 0, 2500, 10000, 12, 40, 1, 1));
		else if (name.equalsIgnoreCase("die_6"))
			VALAKAS_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1700, 20, 0, 700, 10000, 10, 10, 1, 1));
		else if (name.equalsIgnoreCase("die_7"))
			VALAKAS_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1700, 10, 0, 1000, 10000, 20, 70, 1, 1));
		else if (name.equalsIgnoreCase("die_8"))
		{
			
			for (SpawnLocation loc : CUBE_LOC)
				addSpawn(31759, loc, false, 900000, false);
			
			for (Creature pc : VALAKAS_LAIR.getCharacters())
			{
				if ((pc instanceof Player))
				{
					pc.sendPacket(new CameraMode(0));
					pc.sendPacket(NormalCamera.STATIC_PACKET);
				}
			}			
			startQuestTimer("remove_players", null, null, 900000);
		}
		else if (name.equalsIgnoreCase("skill_task"))
			callSkillAI(npc);
		else if (name.equalsIgnoreCase("valakas_unlock"))
			GrandBossManager.getInstance().setBossStatus(VALAKAS, DORMANT);
		else if (name.equalsIgnoreCase("remove_players"))
			VALAKAS_LAIR.oustAllPlayers();
		
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
		if (!ValakasConfig.FWA_FIXTIMEPATTERNOFVALAKAS.isEmpty())
		{
			// Cancel skill_task and regen_task.
			cancelQuestTimers("regen_task", npc);
			cancelQuestTimers("skill_task", npc);
			
			startQuestTimer("die_8", _cubic, null, 16500); // 2500
			
			GrandBossManager.getInstance().setBossStatus(VALAKAS, DEAD);
			
			startQuestTimer("valakas_unlock", null, null, getRespawnInterval());
			_log.info("ValakasManager: Next spawn date of Valakas is " + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() + getRespawnInterval()) + ".");
			
			// also save the respawn time so that the info is maintained past reboots
			StatSet info = GrandBossManager.getInstance().getStatSet(VALAKAS);
			info.set("respawn_time", System.currentTimeMillis() + getRespawnInterval());
			GrandBossManager.getInstance().setStatSet(VALAKAS, info);
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
		
		// If result is still null, Valakas will roam. Don't go deeper in skill AI.
		if (_actualVictim == null)
		{
			if (Rnd.get(10) == 0)
				npc.moveUsingRandomOffset(1400);
			
			return;
		}
		
		npc.getAI().tryToCast(_actualVictim, getRandomSkill(npc), 1);
	}
	
	private static int getRandomSkill(Npc npc)
	{
		final double hpRatio = npc.getStatus().getHpRatio();
		
		// Valakas Lava Skin is prioritary.
		if (hpRatio < 0.25 && Rnd.get(1500) == 0 && npc.getFirstEffect(4680) == null)
			return LAVA_SKIN;
		
		if (hpRatio < 0.5 && Rnd.get(60) == 0)
			return METEOR_SWARM;
		
		// Find enemies surrounding Valakas.
		final int[] playersAround = getPlayersCountInPositions(1200, npc);
		
		// Behind position got more ppl than front position, use behind aura skill.
		if (playersAround[1] > playersAround[0])
			return Rnd.get(BEHIND_SKILLS);
		
		// Use front aura skill.
		return Rnd.get(FRONT_SKILLS);
	}
	
	private static long getRespawnInterval()
	{
		SchedulingPattern timePattern = null;
		
		long now = System.currentTimeMillis();
		try
		{
			timePattern = new SchedulingPattern(ValakasConfig.FWA_FIXTIMEPATTERNOFVALAKAS);
			long delay = timePattern.next(now) - now;
			return Math.max(60000, delay);
		}
		catch (SchedulingPattern.InvalidPatternException ipe)
		{
			throw new RuntimeException("Invalid respawn data \"" + ValakasConfig.FWA_FIXTIMEPATTERNOFVALAKAS + "\" in " + Valakas.class.getSimpleName(), ipe);
		}
	}
}