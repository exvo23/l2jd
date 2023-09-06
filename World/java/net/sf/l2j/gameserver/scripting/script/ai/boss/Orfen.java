package net.sf.l2j.gameserver.scripting.script.ai.boss;


import net.sf.l2j.commons.data.StatSet;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.data.SchedulingPattern;
import net.sf.l2j.gameserver.data.manager.GrandBossManager;
import net.sf.l2j.gameserver.model.actor.Attackable;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.GrandBoss;
import net.sf.l2j.gameserver.network.NpcStringId;
import net.sf.l2j.gameserver.network.serverpackets.PlaySound;
import net.sf.l2j.gameserver.scripting.script.ai.AttackableAIScript;
import net.sf.l2j.gameserver.scripting.script.ai.boss.config.OrfenConfig;
import net.sf.l2j.gameserver.skills.L2Skill;

public class Orfen extends AttackableAIScript
{
	
	private static final NpcStringId[] ORFEN_CHAT =
	{
		NpcStringId.ID_1000028,
		NpcStringId.ID_1000029,
		NpcStringId.ID_1000030,
		NpcStringId.ID_1000031
	};
	
	// Grand boss
	private static final int ORFEN = 29014;
	
	// Monsters
	private static final int RAIKEL_LEOS = 29016;
	private static final int RIBA_IREN = 29018;
	
	private static final byte ALIVE = 0;
	private static final byte DEAD = 1;
	
	public Orfen()
	{
		super("ai/boss");
		
		final StatSet info = GrandBossManager.getInstance().getStatSet(ORFEN);
		
		switch (GrandBossManager.getInstance().getBossStatus(ORFEN))
		{
			case DEAD: // Launch the timer to set DORMANT, or set DORMANT directly if timer expired while offline.
				long temp = (info.getLong("respawn_time") - System.currentTimeMillis());
				if (temp > 0)
					startQuestTimer("orfen_unlock", null, null, temp);
				else
					GrandBossManager.getInstance().setBossStatus(ORFEN, DEAD);
				break;
			
			case ALIVE:
				spawnBoss();
				break;
		}
	
		
	}
	
	@Override
	protected void registerNpcs()
	{
		addAttacked(ORFEN, RIBA_IREN);
		addClanAttacked(RAIKEL_LEOS, RIBA_IREN);
		addCreated(ORFEN);
		addMyDying(ORFEN);
		addSeeSpell(ORFEN);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{

		
		if (name.equalsIgnoreCase("3001"))
		{
			if (npc.isScriptValue(1))
			{
				// HPs raised over 95%, instantly random teleport elsewhere. Teleport flag is set back to false.
				if (npc.getStatus().getHpRatio() > 0.95)
				{
					
					npc.setScriptValue(0);
				}
				
			}
		}
		
		else if (name.equalsIgnoreCase("orfen_unlock"))
		{
			spawnBoss();
		}
		
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			// Curses
			if (attacker.testCursesOnAttack(npc))
				return;
			
			if (npc.getNpcId() == ORFEN)
			{
				// Orfen didn't yet teleport, and reached 50% HP.
				if (npc.isScriptValue(0) && npc.getStatus().getHpRatio() < 0.5)
				{
					// Set teleport flag to true.
					npc.setScriptValue(1);
					
				}
				else if (attacker instanceof Player)
				{
					final double dist = npc.distance3D(attacker);
					if (dist > 300 && dist < 1000 && Rnd.get(100) < 10)
					{
						// Random chat.
						npc.broadcastNpcSay(Rnd.get(ORFEN_CHAT), attacker.getName());
						
						npc.getAI().tryToCast(attacker, 4063, 1);
					}
					else if (Rnd.get(100) < 20)
						npc.getAI().tryToCast(attacker, 4064, 1);
				}
			}
			// RIBA_IREN case, as it's the only other registered.
			else if (npc.getStatus().getHpRatio() < 0.5)
				npc.getAI().tryToCast(attacker, 4516, 1);
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Attackable caller, Attackable called, Creature attacker, int damage)
	{
		if (called.getNpcId() == RAIKEL_LEOS && Rnd.get(100) < 5)
			called.getAI().tryToCast(attacker, 4067, 4);
		else if (called.getNpcId() == RIBA_IREN && caller.getNpcId() != RIBA_IREN && (caller.getStatus().getHpRatio() < 0.5) && Rnd.get(100) < ((caller.getNpcId() == ORFEN) ? 90 : 10))
			called.getAI().tryToCast(caller, 4516, 1);
		
		super.onClanAttacked(caller, called, attacker, damage);
	}
	@Override
	public void onCreated(Npc npc)
	{
		// Broadcast spawn sound.
		npc.broadcastPacket(new PlaySound(1, "BS01_A", npc));
		
		// Fire a 10s task to check Orfen status.
		startQuestTimerAtFixedRate("3001", npc, null, 10000);
		
		super.onCreated(npc);
	}
	
	private void spawnBoss()
	{
		
		final GrandBoss Orfen;
		final StatSet info = GrandBossManager.getInstance().getStatSet(ORFEN);
		
		GrandBossManager.getInstance().setBossStatus(ORFEN, ALIVE);
		
		Orfen = (GrandBoss) addSpawn(ORFEN, info.getInteger("loc_x"), info.getInteger("loc_y"), info.getInteger("loc_z"), info.getInteger("heading"), false, 0, false);
		Orfen.getStatus().setHpMp(info.getInteger("currentHP"), info.getInteger("currentMP"));
		
		GrandBossManager.getInstance().addBoss(Orfen);
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		if (!OrfenConfig.FWA_FIXTIMEPATTERNOFORFEN.isEmpty())
		{
			if (npc.getNpcId() == ORFEN)
			{
				GrandBossManager.getInstance().setBossStatus(ORFEN, DEAD);
				
				cancelQuestTimers("3001", npc);
				
				npc.broadcastPacket(new PlaySound(1, "BS02_D", npc));
				
				startQuestTimer("orfen_unlock", null, null, getRespawnInterval());
				
				// also save the respawn time so that the info is maintained past reboots
				StatSet info = GrandBossManager.getInstance().getStatSet(ORFEN);
				info.set("respawn_time", System.currentTimeMillis() + getRespawnInterval());
				GrandBossManager.getInstance().setStatSet(ORFEN, info);
			}
		}
		super.onMyDying(npc, killer);
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		Creature originalCaster = isPet ? caster.getSummon() : caster;
		if (skill.getAggroPoints() > 0 && Rnd.get(100) < 20 && npc.isIn3DRadius(originalCaster, 1000))
		{
			// Random chat.
			npc.broadcastNpcSay(Rnd.get(ORFEN_CHAT), caster.getName());
			
			// Teleport caster near Orfen.
			// originalCaster.teleportTo(npc.getPosition(), 0);
			
			// Cast a skill.
			npc.getAI().tryToCast(originalCaster, 4064, 1);
		}
		super.onSeeSpell(npc, caster, skill, targets, isPet);
	}
	
	private static long getRespawnInterval()
	{
		SchedulingPattern timePattern = null;
		
		long now = System.currentTimeMillis();
		try
		{
			timePattern = new SchedulingPattern(OrfenConfig.FWA_FIXTIMEPATTERNOFORFEN);
			long delay = timePattern.next(now) - now;
			return Math.max(60000, delay);
		}
		catch (SchedulingPattern.InvalidPatternException ipe)
		{
			throw new RuntimeException("Invalid respawn data \"" + OrfenConfig.FWA_FIXTIMEPATTERNOFORFEN + "\" in " + Orfen.class.getSimpleName(), ipe);
		}
	}
}