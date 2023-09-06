package net.sf.l2j.gameserver.scripting.script.ai.boss;

import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.data.manager.CastleManager;
import net.sf.l2j.gameserver.enums.EventHandler;
import net.sf.l2j.gameserver.enums.SayType;
import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.model.actor.Attackable;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.entity.Siege;
import net.sf.l2j.gameserver.model.location.SpawnLocation;
import net.sf.l2j.gameserver.network.NpcStringId;
import net.sf.l2j.gameserver.network.serverpackets.NpcSay;
import net.sf.l2j.gameserver.scripting.script.ai.AttackableAIScript;
import net.sf.l2j.gameserver.scripting.script.ai.boss.config.BenomConfig;
import net.sf.l2j.gameserver.skills.L2Skill;

/**
 * Benom is a specific Raid Boss, appearing in Rune Castle. He is aggressive towards anyone.<br>
 * <br>
 * The castle owning clan can defeat Benom. It can teleport to Benom's den using a specific gatekeeper, 24 hours before siege start. If the clan doesn't kill Benom before the siege start, Benom will appear during the siege if at least 2 life controls crystals have been broken.
 */
public class Benom extends AttackableAIScript
{
	private static final int BENOM = 29054;
	private static final int TELEPORT_CUBE = 29055;
	private static final int DUNGEON_KEEPER = 35506;
	
	// Important : the heading is used as offset.
	private static final SpawnLocation[] TARGET_TELEPORTS =
	{
		new SpawnLocation(12860, -49158, -976, 650),
		new SpawnLocation(14878, -51339, 1024, 100),
		new SpawnLocation(15674, -49970, 864, 100),
		new SpawnLocation(15696, -48326, 864, 100),
		new SpawnLocation(14873, -46956, 1024, 100),
		new SpawnLocation(12157, -49135, -1088, 650),
		new SpawnLocation(12875, -46392, -288, 200),
		new SpawnLocation(14087, -46706, -288, 200),
		new SpawnLocation(14086, -51593, -288, 200),
		new SpawnLocation(12864, -51898, -288, 200),
		new SpawnLocation(15538, -49153, -1056, 200),
		new SpawnLocation(17001, -49149, -1064, 650)
	};
	
	private static final int RUNE_CASTLE = 8;
	
	private Npc _benom;
	
	private boolean _isPrisonOpened;
	
	private final List<Player> _targets = new ArrayList<>();
	
	public Benom()
	{
		super("ai/boss");
		BenomConfig.init();
		addSiegeNotify(RUNE_CASTLE);
		
		addTalkId(DUNGEON_KEEPER, TELEPORT_CUBE);
	}
	
	@Override
	protected void registerNpcs()
	{
		addEventIds(BENOM, EventHandler.ATTACKED, EventHandler.MY_DYING, EventHandler.SEE_CREATURE, EventHandler.USE_SKILL_FINISHED);
	}
	
	@Override
	public String onTalk(Npc npc, Player talker)
	{
		switch (npc.getNpcId())
		{
			case TELEPORT_CUBE:
				
				talker.teleportTo(BenomConfig.TELEPORT_EXIT[0], BenomConfig.TELEPORT_EXIT[1] + Rnd.get(-350, 350), BenomConfig.TELEPORT_EXIT[2], 0);
				
				break;
			
			case DUNGEON_KEEPER:
				if (_isPrisonOpened)
					talker.teleportTo(BenomConfig.TELEPORT_ENTRANCE[0], BenomConfig.TELEPORT_ENTRANCE[1] + Rnd.get(-350, 350), BenomConfig.TELEPORT_ENTRANCE[2], 0);
				else
					return HtmCache.getInstance().getHtm("data/html/doormen/35506-2.htm");
				break;
		}
		return super.onTalk(npc, talker);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("benom_spawn"))
		{
			_isPrisonOpened = true;
			
			_benom = addSpawn(BENOM, BenomConfig.SPAWN_BENOM_LOCATION[0], BenomConfig.SPAWN_BENOM_LOCATION[1], BenomConfig.SPAWN_BENOM_LOCATION[2], 0, false, 120000, false);
			_benom.broadcastNpcSay("Who dares to covet the throne of our castle! Leave immediately or you will pay the price of your audacity with your very own blood!");
		}
		else if (name.equalsIgnoreCase("tower_check"))
		{
			final Siege siege = CastleManager.getInstance().getCastleById(RUNE_CASTLE).getSiege();
			if (siege.getControlTowerCount() < 2)
			{
				npc.teleportTo(BenomConfig.TELEPORT_THORNE[0], BenomConfig.TELEPORT_THORNE[1], BenomConfig.TELEPORT_THORNE[2], 0);
				// TODO get Dungeon Keeper instance and use regular NpcSay constructor with instance
				siege.getCastle().getSiegeZone().broadcastPacket(new NpcSay(DUNGEON_KEEPER, SayType.ALL, "Oh no! The defenses have failed. It is too dangerous to remain inside the castle. Flee! Every man for himself!"));
				
				cancelQuestTimers("tower_check");
				startQuestTimerAtFixedRate("raid_check", npc, null, 10000);
			}
		}
		else if (name.equalsIgnoreCase("raid_check"))
		{
			if (!npc.isInsideZone(ZoneId.SIEGE) && !npc.isTeleporting())
				npc.teleportTo(BenomConfig.TELEPORT_THORNE[0], BenomConfig.TELEPORT_THORNE[1], BenomConfig.TELEPORT_THORNE[2], 0);
		}
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onSiegeEvent(Siege siege)
	{
		// Don't go further if the castle isn't owned.
		if (siege.getCastle().getOwnerId() <= 0)
			return;
		
		switch (siege.getStatus())
		{
			case IN_PROGRESS:
				_isPrisonOpened = false;
				if (_benom != null && !_benom.isDead())
					startQuestTimerAtFixedRate("tower_check", _benom, null, 30000);
				break;
			
			case REGISTRATION_OPENED:
				_isPrisonOpened = false;
				
				if (_benom != null)
				{
					cancelQuestTimers("tower_check");
					cancelQuestTimers("raid_check");
					
					_benom.deleteMe();
				}
				
				startQuestTimer("benom_spawn", null, null, siege.getSiegeDate().getTimeInMillis() - 86400000 - System.currentTimeMillis());
				break;
			
			case REGISTRATION_OVER:
				startQuestTimer("benom_spawn", null, null, 0);
				break;
		}
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			if (Rnd.get(100) < 25)
				npc.getAI().tryToCast(attacker, 4995, 1);
			else if (npc.getStatus().getHpRatio() < 0.33 && Rnd.get(500) < 1)
				npc.getAI().tryToCast(attacker, 4996, 1);
			else if (!npc.isIn3DRadius(attacker, 300) && Rnd.get(100) < 1)
				npc.getAI().tryToCast(attacker, 4993, 1);
			else if (Rnd.get(100) < 1)
				npc.getAI().tryToCast(attacker, 4994, 1);
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		npc.broadcastNpcSay(NpcStringId.ID_1010626);
		cancelQuestTimers("raid_check");
		
		addSpawn(TELEPORT_CUBE, BenomConfig.SPAWN_CUBIC_LOCATION[0], BenomConfig.SPAWN_CUBIC_LOCATION[1], BenomConfig.SPAWN_CUBIC_LOCATION[2], 0, false, 120000, false);
		
		super.onMyDying(npc, killer);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (creature instanceof Player)
		{
			final Player player = creature.getActingPlayer();
			if (_targets.size() < 10 && Rnd.get(3) < 1)
				_targets.add(player);
		}
		super.onSeeCreature(npc, creature);
	}
	
	@Override
	public void onUseSkillFinished(Npc npc, Player player, L2Skill skill)
	{
		switch (skill.getId())
		{
			case 4995:
				if (BenomConfig.DISABLE_SKILL_TELEPORT)
					teleportTarget(player);
				((Attackable) npc).getAggroList().stopHate(player);
				break;
			
			case 4996:
				if (BenomConfig.DISABLE_SKILL_TELEPORT)
					teleportTarget(player);
				((Attackable) npc).getAggroList().stopHate(player);
				
				if (!_targets.isEmpty())
				{
					for (Player target : _targets)
					{
						if (player.isIn3DRadius(target, 250))
						{
							if (BenomConfig.DISABLE_SKILL_TELEPORT)
								teleportTarget(target);
							((Attackable) npc).getAggroList().stopHate(target);
						}
					}
					_targets.clear();
				}
				break;
		}
		super.onUseSkillFinished(npc, player, skill);
	}
	
	/**
	 * Teleport the {@link Player} set as parameter using one random {@link SpawnLocation}.<br>
	 * <br>
	 * The heading from {@link SpawnLocation} is used as random offset, not as heading.
	 * @param player : The {@link Player} used as target.
	 */
	private static void teleportTarget(Player player)
	{
		if (player != null)
		{
			final SpawnLocation loc = Rnd.get(TARGET_TELEPORTS);
			if (BenomConfig.DISABLE_SKILL_TELEPORT)
				player.teleportTo(loc, loc.getHeading());
		}
	}
}