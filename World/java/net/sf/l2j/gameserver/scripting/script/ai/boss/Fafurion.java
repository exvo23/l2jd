package net.sf.l2j.gameserver.scripting.script.ai.boss;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import net.sf.l2j.commons.data.StatSet;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.data.SchedulingPattern;
import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.data.manager.GrandBossManager;
import net.sf.l2j.gameserver.data.manager.ZoneManager;
import net.sf.l2j.gameserver.enums.EventHandler;
//import net.sf.l2j.gameserver.enums.skills.AbnormalEffect;
import net.sf.l2j.gameserver.geoengine.GeoEngine;
//import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.GrandBoss;
import net.sf.l2j.gameserver.model.actor.instance.Monster;
import net.sf.l2j.gameserver.model.location.SpawnLocation;
import net.sf.l2j.gameserver.model.zone.type.BossZone;
//import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage.SMPOS;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.PlaySound;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.network.serverpackets.SpecialCamera;
import net.sf.l2j.gameserver.network.serverpackets.StopMove;
//import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.scripting.script.ai.AttackableAIScript;
import net.sf.l2j.gameserver.scripting.script.ai.boss.config.FafurionConfig;
import net.sf.l2j.gameserver.skills.L2Skill;

/**
 * @author BAN-L2JDEV
 */
public final class Fafurion extends AttackableAIScript
{
	private static final BossZone FAFURION_LAIR = ZoneManager.getInstance().getZoneById(110019, BossZone.class);
	private long _lastAttack = 0L;
	private Creature _actualVictim; // Actual target of Fafurion.
	
	public static final int FAFURION = 37001; // Fafurion id used for status updates.
	public static final int FAFURION01 = 37002; // Fafurion id used for status updates.
	public static final int FAFURION02 = 37003; // Fafurion id used for status updates.
	
	public static Set<Player> _PlayersInside = ConcurrentHashMap.newKeySet();
	
	
	private static int _Fafurion1_x = 0;
	private static int _Fafurion1_y = 0;
	private static int _Fafurion1_z = 0;
	private static int _Fafurion1_h = 0;
	
	public static final byte DORMANT = 0; // Fafurion is spawned and no one has entered yet. Entry is unlocked.
	public static final byte WAITING = 1; // Fafurion is spawned and someone has entered, triggering a 30 minute window for additional people to enter. Entry is unlocked.
	public static final byte FIGHTING = 2; // Fafurion is engaged in battle, annihilating his foes. Entry is locked.
	public static final byte DEAD = 3; // Fafurion has been killed. Entry is locked.
	
	public static ScheduledFuture<?> timerTask = null;
	
	private final Set<Npc> _minions = ConcurrentHashMap.newKeySet();
	
	private static final SpawnLocation[] CUBE_LOC =
	{
		new SpawnLocation(180922, 213035, -14816, 0),
		new SpawnLocation(182338, 212793, -14816, 0)
	
	};
	private static final int[] Normal_Attack_RETAIL = {32705,32706,32707};
	private static final int[] FAFURION_SKILLS ={32708,32709,32710,32711};
	private static final int[] Normal_Attack_TRAMFORME ={40001,40002,40003};
	private static final int[] FAFURION_SKILLS_TRANSFORME_2 ={40004,40005,40006,40007,40008,40009,40010,};
	
	public Fafurion()
	{
		super("ai/boss");
		FafurionConfig.init();
		final StatSet info = GrandBossManager.getInstance().getStatSet(FAFURION);
		
		switch (GrandBossManager.getInstance().getBossStatus(FAFURION))
		{
			case DEAD: // Launch the timer to set DORMANT, or set DORMANT directly if timer expired while offline.
				long temp = (info.getLong("respawn_time") - System.currentTimeMillis());
				if (temp > 0)
					startQuestTimer("fafurion_unlock", null, null, temp);
				else
					GrandBossManager.getInstance().setBossStatus(FAFURION, DORMANT);
				break;
			
			case WAITING:
				startQuestTimer("beginning", null, null, FafurionConfig.WAIT_TIME_FAFURION);
				break;
			
			case FIGHTING:
				final int loc_x = info.getInteger("loc_x");
				final int loc_y = info.getInteger("loc_y");
				final int loc_z = info.getInteger("loc_z");
				final int heading = info.getInteger("heading");
				final int hp = info.getInteger("currentHP");
				final int mp = info.getInteger("currentMP");
				
				final Npc fafurion = addSpawn(FAFURION, loc_x, loc_y, loc_z, heading, false, 0, false);
				GrandBossManager.getInstance().addBoss((GrandBoss) fafurion);
				fafurion.getStatus().setHpMp(hp, mp);
				fafurion.forceRunStance();
				break;
		}
	}
	
	@Override
	protected void registerNpcs()
	{
		addEventIds(FAFURION, EventHandler.ATTACKED, EventHandler.CREATED, EventHandler.MY_DYING);
		addEventIds(FAFURION01, EventHandler.ATTACKED, EventHandler.CREATED, EventHandler.MY_DYING);
		addEventIds(FAFURION02, EventHandler.ATTACKED, EventHandler.CREATED, EventHandler.MY_DYING);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (npc.isInvul())
			return;
		updateHp(npc);
		
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
		npc.setInvul(true);
		paralyzeAll(true);
		startSpecialCamera(false, npc);
		super.onCreated(npc);
		
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		if (!FafurionConfig.FWA_FIXTIMEPATTERNOFFAFURION.isEmpty())
		{
			_PlayersInside.clear();
			cancelQuestTimer("inactivity_task", npc, null);
			cancelQuestTimer("skill_task", npc, null);
			
			ExShowScreenMessage msg = new ExShowScreenMessage("HONORABLE WARRIORS HAVE DRIVEN OFF FAFURION THE EVIL WIND DRAGON", 7500, SMPOS.TOP_CENTER, true);
			FAFURION_LAIR.broadcastPacket(msg);
			//FAFURION_LAIR.broadcastPacket(new PlaySound(1, "BS01_D"));
			
			startQuestTimer("die_3", npc, null, 8000L); // 8000
			// RedSky and Earthquake
			
			GrandBossManager.getInstance().setBossStatus(FAFURION, DEAD);
			// Start respawn timer.
			startQuestTimer("fafurion_unlock", null, null, getRespawnInterval());
			
			// Save the respawn time so that the info is maintained past reboots
			final StatSet info = GrandBossManager.getInstance().getStatSet(FAFURION);
			info.set("respawn_time", System.currentTimeMillis() + getRespawnInterval());
			GrandBossManager.getInstance().setStatSet(FAFURION, info);
			
			clear(npc);
		}
		super.onMyDying(npc, killer);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("beginning"))
		{
			_lastAttack = System.currentTimeMillis();
			npc = addSpawn(FAFURION, 180157, 208915, -14816, 0, false, 0, false);
			GrandBossManager.getInstance().addBoss((GrandBoss) npc);
			npc.setInvul(true);
			npc.broadcastPacket(new SocialAction(npc, 1));
			startQuestTimer("camera", npc, null, 16);
			startQuestTimer("skill_task", npc, null, 5000);
		}
		if (name.equalsIgnoreCase("camera"))
			FAFURION_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 2150, npc.getHeading(), 25, 2500, 2500, 0, 0, 0, 0));
		else if (name.equalsIgnoreCase("camera_2"))
		{
			FAFURION_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 2500, 190, 15, 2500, 2500, 0, 0, 0, 0));
			ExShowScreenMessage msg = new ExShowScreenMessage("FAFURION DRAGON ENTER WORLD", 7500, SMPOS.TOP_CENTER, true);
			FAFURION_LAIR.broadcastPacket(msg);
		}
		else if (name.equalsIgnoreCase("camera_3"))
			FAFURION_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 2500, 190, 15, 2500, 2500, 0, 0, 0, 0));
		else if (name.equalsIgnoreCase("camera_4"))
			FAFURION_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 2700, 190, 15, 2500, 2500, 0, 0, 0, 0));
		else if (name.equalsIgnoreCase("camera_5"))
		{
			FAFURION_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 3000, 190, 15, 5, 5, 0, 0, 1, 0));
			paralyzeAll(false);
			npc.setInvul(false);
			GrandBossManager.getInstance().setBossStatus(FAFURION, FIGHTING);
			startQuestTimer("inactivity_task", npc, null, 900000);
			startQuestTimer("skill_task", npc, null, 5000);
		}
		else if (name.equalsIgnoreCase("skill_task"))
		{
			// Pickup a target if no or dead victim. If Baium was hitting an angel, 50% luck he reconsiders his target. 10% luck he decides to reconsiders his target.
			if (_actualVictim == null || _actualVictim.isDead() || !npc.knows(_actualVictim))
				_actualVictim = getRandomTarget(npc);
			else
			{
				npc.abortAll(false);
				L2Skill skill = SkillTable.getInstance().getInfo(getRandomSkill(npc), 1);
				if(skill != null)
					npc.getAI().tryToCast(_actualVictim, skill);
			}
			startQuestTimer("skill_task", npc, null, 5000);
		}
		else if (name.equalsIgnoreCase("inactivity_task"))
		{
			if (_lastAttack + 900000 < System.currentTimeMillis())
			{
				FAFURION_LAIR.oustAllPlayers();
				clear(npc);
				npc.deleteMe();
				cancelQuestTimers("skill_task", npc);
			}
		}
		else if (name.equalsIgnoreCase("death_1"))
			FAFURION_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 4150, 200, 25, 2500, 2500, 0, 0, 0, 0));
		else if (name.equalsIgnoreCase("death_2"))
		{
			FAFURION_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 3500, 200, 15, 2500, 2500, 0, 0, 0, 0));
		}
		else if (name.equalsIgnoreCase("death_3"))
		{
			FAFURION_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 4150, 200, 10, 2500, 2500, 0, 0, 0, 0));
			npc.setInvul(false);
			paralyzeAll(false);
		}
		else if (name.equalsIgnoreCase("die_3"))
		{
			for (SpawnLocation loc : CUBE_LOC)
				addSpawn(31759, loc, false, 900000, false);
			startQuestTimer("remove_players", null, null, 900000);
		}
		if (name.equalsIgnoreCase("transform"))
		{
			int _NewFafurion = 0;
			double _OldHp = npc.getStatus().getHp();
			_Fafurion1_x = npc.getX();
			_Fafurion1_y = npc.getY();
			_Fafurion1_z = npc.getZ();
			_Fafurion1_h = npc.getHeading();
			switch(npc.getNpcId())
			{
				case FAFURION:
					_NewFafurion = FAFURION01;
					break;
				case FAFURION01:
					_NewFafurion = FAFURION02;
					break;
			}
			npc.abortAll(true);
			npc.deleteMe();
			npc = null;
			npc = addSpawn(_NewFafurion, _Fafurion1_x, _Fafurion1_y, _Fafurion1_z, _Fafurion1_h, false, 0, false);
			if(npc.getNpcId() == FAFURION01)
			{
				npc.getStatus().setHp(_OldHp);
				startQuestTimer("minions_spawn", npc, null, 12000);
			}
			FAFURION_LAIR.broadcastPacket(new SpecialCamera(npc.getObjectId(), 2150, 350, 25, 2500, 2500, 0, 0, 0, 0)); //SpecialCamera(objectId, dist, yaw, pitch, time, duration, turn, rise, widescreen, unknown)
			FAFURION_LAIR.broadcastPacket(new MagicSkillUse(npc, npc, 40010, 1, 4500, 0));
			startQuestTimer("skill_task", npc, null, 10000);
			startQuestTimer("StartPC", npc, null, 10000);
		}
		else if (name.equalsIgnoreCase("StopPC"))
		{
			for (Creature p : FAFURION_LAIR.getKnownTypeInside(Player.class))
			{
				p.abortAll(true);
				p.disableAllSkills();
				p.setIsParalyzed(true);
				p.setIsImmobilized(true);
				p.getAI().tryToIdle();
			}
		}
		else if (name.equalsIgnoreCase("StartPC"))
		{
			for (Creature p : FAFURION_LAIR.getKnownTypeInside(Player.class))
			{
				p.enableAllSkills();
				p.setIsParalyzed(false);
				p.setIsImmobilized(false);
				p.getAI().tryToIdle();
			}
		}
		else if (name.equalsIgnoreCase("minions_spawn"))
		{
			boolean isBehemoth = Rnd.get(100) < 60;
			int mobNumber = isBehemoth ? 3 : 4;
			
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
			}
			ExShowScreenMessage msg = new ExShowScreenMessage("MY MINIONS,IT IS TIME TO HUNT", 7500, SMPOS.TOP_CENTER, true);
			FAFURION_LAIR.broadcastPacket(msg);
			FAFURION_LAIR.broadcastPacket(new PlaySound(1, "ff_01"));
		}
		if (name.equalsIgnoreCase("fafurion_unlock"))
			GrandBossManager.getInstance().setBossStatus(FAFURION02, DORMANT);
		
		return super.onTimer(name, npc, player);
	}
	
	private static void killmobs()
	{
		for (Npc mob : FAFURION_LAIR.getKnownTypeInside(Npc.class))
		{
			if (mob.getNpcId() != FAFURION || mob.getNpcId() != FAFURION01 || mob.getNpcId() != FAFURION02 )
				mob.doDie(mob);
		}
	}
	
	private static void paralyzeAll(boolean val)
	{
		for (Creature creature : FAFURION_LAIR.getCharacters())
		{
			creature.setIsParalyzed(val);
			if (val)
				creature.broadcastPacket(new StopMove(creature));
		}
	}
	
	private void startSpecialCamera(boolean death, Npc npc)
	{
		if (death)
		{
			startQuestTimer("death_1", npc, null, 1000);
			startQuestTimer("death_2", npc, null, 3400);
			startQuestTimer("death_3", npc, null, 5800);
		}
		else
		{
			startQuestTimer("camera", npc, null, 1000);
			startQuestTimer("camera_2", npc, null, 3400);
			startQuestTimer("camera_3", npc, null, 5800);
			startQuestTimer("camera_4", npc, null, 8200);
			startQuestTimer("camera_5", npc, null, 9200);
		}
	}
	
	private static int getRandomSkill(Npc npc)
	{
		int Normal_Attack = 0;
		int Skills = 0;
		int _Skillid = 0;
		switch(npc.getNpcId())
		{
			case 37001:
				Normal_Attack = Normal_Attack_RETAIL[Rnd.get(0, Normal_Attack_RETAIL.length-1)];
				Skills = FAFURION_SKILLS[Rnd.get(0, FAFURION_SKILLS.length-1)];
				break;
			case 37002:
				Normal_Attack = Normal_Attack_TRAMFORME[Rnd.get(0, Normal_Attack_TRAMFORME.length-1)];
				Skills = FAFURION_SKILLS_TRANSFORME_2[Rnd.get(0, FAFURION_SKILLS_TRANSFORME_2.length-1)];
				break;
			case 37003:
				Normal_Attack = Normal_Attack_TRAMFORME[Rnd.get(0, Normal_Attack_TRAMFORME.length-1)];
				Skills = FAFURION_SKILLS_TRANSFORME_2[Rnd.get(0, FAFURION_SKILLS_TRANSFORME_2.length-1)];
				break;
		}
		if (Rnd.get(100) < 30) // CHANCE DE USAR  NORMAL ATTACK 70%
			_Skillid = Normal_Attack;
		else
			_Skillid = Skills;
		return _Skillid;
	}
	
	private static Creature getRandomTarget(Npc npc)
	{
		final List<Creature> result = new ArrayList<>();
		
		for (Creature obj : FAFURION_LAIR.getKnownTypeInside(Creature.class))
		{
			if (obj instanceof Player)
			{
				if (obj.isDead() || !(GeoEngine.getInstance().canSeeTarget(npc, obj)))
					continue;
				
				if (!((Player) obj).getAppearance().isVisible())
					continue;
				result.add(obj);
			}
		}
		
		return (result.isEmpty()) ? null : Rnd.get(result);
	}
	
	private void updateHp(Npc npc)
	{
		double hp = npc.getStatus().getHp() / npc.getStatus().getMaxHp();
		switch(npc.getNpcId())
		{
			case FAFURION:
				if (hp <= 0.50 && Rnd.get(100) < 50) // CHANCE DE 30% PARA TRANSFORMAR COM 50% DO HP
				{
					cancelQuestTimer("skill_task", npc, null);
					startQuestTimer("transform", npc, null, 5L);
					startQuestTimer("StopPC", npc, null, 2L);
				}
				break;
			case FAFURION01:
				if (hp <= 0.20 && Rnd.get(100) < 20)// CHANCE DE 30% PARA TRANSFORMAR COM 50% DO HP
				{
					killmobs();
					cancelQuestTimer("skill_task", npc, null);
					startQuestTimer("transform", npc, null, 5L);
					startQuestTimer("StopPC", npc, null, 2L);
				}
				break;
		}
	}
	private void clear(Npc npc)
	{
		cancelQuestTimer("action_task", npc, null);
		cancelQuestTimer("inactivity_task", npc, null);
		cancelQuestTimer("mass_kill_cin", npc, null);
		cancelQuestTimer("skill_task", npc, null);
		_lastAttack = 0L;
	}
	
	private static long getRespawnInterval()
	{
		SchedulingPattern timePattern = null;
		
		long now = System.currentTimeMillis();
		try
		{
			timePattern = new SchedulingPattern(FafurionConfig.FWA_FIXTIMEPATTERNOFFAFURION);
			long delay = timePattern.next(now) - now;
			return Math.max(60000, delay);
		}
		catch (SchedulingPattern.InvalidPatternException ipe)
		{
			throw new RuntimeException("Invalid respawn data \"" + FafurionConfig.FWA_FIXTIMEPATTERNOFFAFURION + "\" in " + Fafurion.class.getSimpleName(), ipe);
		}
	}
}