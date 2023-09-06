package net.sf.l2j.gameserver.model.actor;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.commons.pool.ThreadPool;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.commons.util.ArraysUtil;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.SkillTable.FrequentSkill;
import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.data.manager.CastleManager;
import net.sf.l2j.gameserver.data.manager.DimensionalRiftManager;
import net.sf.l2j.gameserver.data.manager.LotteryManager;
import net.sf.l2j.gameserver.data.sql.ClanTable;
import net.sf.l2j.gameserver.data.xml.InstantTeleportData;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.data.xml.MultisellData;
import net.sf.l2j.gameserver.data.xml.NpcData;
import net.sf.l2j.gameserver.data.xml.ObserverGroupData;
import net.sf.l2j.gameserver.data.xml.ScriptData;
import net.sf.l2j.gameserver.data.xml.TeleportData;
import net.sf.l2j.gameserver.enums.EventHandler;
import net.sf.l2j.gameserver.enums.SayType;
import net.sf.l2j.gameserver.enums.TeleportType;
import net.sf.l2j.gameserver.enums.actors.MissionType;
import net.sf.l2j.gameserver.enums.actors.NpcAiType;
import net.sf.l2j.gameserver.enums.actors.NpcRace;
import net.sf.l2j.gameserver.enums.actors.NpcSkillType;
import net.sf.l2j.gameserver.enums.actors.NpcTalkCond;
import net.sf.l2j.gameserver.enums.items.ShotType;
import net.sf.l2j.gameserver.enums.skills.ElementType;
import net.sf.l2j.gameserver.events.l2jdev.EventManager;
import net.sf.l2j.gameserver.geoengine.GeoEngine;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.WorldObject;
import net.sf.l2j.gameserver.model.actor.ai.type.NpcAI;
import net.sf.l2j.gameserver.model.actor.instance.DungeonManagerNpc;
import net.sf.l2j.gameserver.model.actor.instance.Monster;
import net.sf.l2j.gameserver.model.actor.status.NpcStatus;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.clanhall.ClanHall;
import net.sf.l2j.gameserver.model.clanhall.SiegableHall;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.model.item.kind.Weapon;
import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.model.location.ObserverLocation;
import net.sf.l2j.gameserver.model.location.SpawnLocation;
import net.sf.l2j.gameserver.model.location.TeleportLocation;
import net.sf.l2j.gameserver.model.olympiad.OlympiadManager;
import net.sf.l2j.gameserver.model.pledge.Clan;
import net.sf.l2j.gameserver.model.spawn.ASpawn;
import net.sf.l2j.gameserver.model.spawn.MultiSpawn;
import net.sf.l2j.gameserver.model.spawn.Spawn;
import net.sf.l2j.gameserver.network.NpcStringId;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.AbstractNpcInfo.NpcInfo;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.ExShowVariationCancelWindow;
import net.sf.l2j.gameserver.network.serverpackets.ExShowVariationMakeWindow;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.NpcSay;
import net.sf.l2j.gameserver.network.serverpackets.ServerObjectInfo;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.scripting.Quest;
import net.sf.l2j.gameserver.scripting.QuestState;
import net.sf.l2j.gameserver.skills.L2Skill;
import net.sf.l2j.gameserver.taskmanager.DecayTaskManager;
import net.sf.l2j.gameserver.taskmanager.RandomAnimationTaskManager;

/**
 * An instance type extending {@link Creature}, which represents a Non Playable Character (or NPC) in the world.
 */
public class Npc extends Creature
{
	public static final int INTERACTION_DISTANCE = 197;
	private static final int SOCIAL_INTERVAL = 12000;
	public boolean _isFOS_Artifact = false;
	
	private ASpawn _spawn;
	private SpawnLocation _spawnLoc;
	private ScheduledFuture<?> _respawnTask;
	
	private Npc _master;
	private Set<Npc> _minions;
	
	private volatile boolean _isDecayed;
	
	private long _lastSocialBroadcast = 0;
	
	private int _leftHandItemId;
	private int _rightHandItemId;
	private int _enchantEffect;
	
	private double _currentCollisionHeight; // used for npc grow effect skills
	private double _currentCollisionRadius; // used for npc grow effect skills
	
	private int _currentSsCount = 0;
	private int _currentSpsCount = 0;
	private int _shotsMask = 0;
	
	private int _scriptValue = 0;
	
	private Castle _castle;
	private final ClanHall _clanHall;
	private final SiegableHall _siegableHall;
	
	private boolean _isCoreAiDisabled;
	
	private List<Integer> _observerGroups;
	
	private boolean _isReversePath;
	
	public Npc(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		for (final L2Skill skill : template.getSkills(NpcSkillType.PASSIVE))
			addStatFuncs(skill.getStatFuncs(this));
		
		getStatus().initializeValues();
		
		// initialize the "current" equipment
		_leftHandItemId = template.getLeftHand();
		_rightHandItemId = template.getRightHand();
		
		_enchantEffect = template.getEnchantEffect();
		
		// initialize the "current" collisions
		_currentCollisionHeight = template.getCollisionHeight();
		_currentCollisionRadius = template.getCollisionRadius();
		
		// Set the name of the Creature
		setName(template.getName());
		setTitle(template.getTitle());
		
		_castle = template.getCastle();
		_clanHall = template.getClanHall();
		_siegableHall = template.getSiegableHall();
	}
	
	@Override
	public NpcAI<? extends Npc> getAI()
	{
		return (NpcAI<?>) _ai;
	}
	
	@Override
	public void setAI()
	{
		_ai = new NpcAI<>(this);
	}
	
	@Override
	public NpcStatus<? extends Npc> getStatus()
	{
		return (NpcStatus<?>) _status;
	}
	
	@Override
	public void setStatus()
	{
		_status = new NpcStatus<>(this);
	}
	
	@Override
	public final NpcTemplate getTemplate()
	{
		return (NpcTemplate) super.getTemplate();
	}
	
	@Override
	public void setWalkOrRun(boolean value)
	{
		super.setWalkOrRun(value);
		
		for (final Player player : getKnownType(Player.class))
			sendInfo(player);
	}
	
	@Override
	public boolean isUndead()
	{
		return getTemplate().getRace() == NpcRace.UNDEAD;
	}
	
	@Override
	public void updateAbnormalEffect()
	{
		for (final Player player : getKnownType(Player.class))
			sendInfo(player);
	}
	
	@Override
	public final void setTitle(String value)
	{
		_title = (value == null) ? "" : value;
	}
	
	@Override
	public void onInteract(Player player)
	{
		if (hasRandomAnimation())
			onRandomAnimation(Rnd.get(8));
		
		player.getQuestList().setLastQuestNpcObjectId(getObjectId());
		
		List<Quest> scripts = getTemplate().getEventQuests(EventHandler.FIRST_TALK);
		if (scripts.size() == 1)
			scripts.get(0).notifyFirstTalk(this, player);
		else if (_observerGroups != null)
			showObserverWindow(player);
		else
			showChatWindow(player);
	}
	
	@Override
	public final void notifyQuestEventSkillFinished(L2Skill skill, WorldObject target)
	{
		final Player player = (target == null) ? null : target.getActingPlayer();
		
		for (Quest quest : getTemplate().getEventQuests(EventHandler.USE_SKILL_FINISHED))
			quest.onUseSkillFinished(this, player, skill);
	}
	
	@Override
	public boolean isMovementDisabled()
	{
		return super.isMovementDisabled() || !getTemplate().canMove() || getTemplate().getAiType() == NpcAiType.CORPSE;
	}
	
	@Override
	public void sendInfo(Player player)
	{
		player.sendPacket((getStatus().getMoveSpeed() == 0) ? new ServerObjectInfo(this, player) : new NpcInfo(this, player));
	}
	
	@Override
	public boolean isChargedShot(ShotType type)
	{
		return (_shotsMask & type.getMask()) == type.getMask();
	}
	
	@Override
	public void setChargedShot(ShotType type, boolean charged)
	{
		if (charged)
			_shotsMask |= type.getMask();
		else
			_shotsMask &= ~type.getMask();
	}
	
	@Override
	public void rechargeShots(boolean physical, boolean magic)
	{
		if (physical)
		{
			// No more ss for this instance, already charged or the activation chance didn't trigger.
			if (_currentSsCount <= 0 || isChargedShot(ShotType.SOULSHOT) || Rnd.get(100) > getTemplate().getSsRate())
				return;
			
			// Reduce the amount of ss for this instance.
			_currentSsCount--;
			
			broadcastPacketInRadius(new MagicSkillUse(this, this, 2154, 1, 0, 0), 600);
			setChargedShot(ShotType.SOULSHOT, true);
		}
		
		if (magic)
		{
			// No more sps for this instance, already charged or the activation chance didn't trigger.
			if (_currentSpsCount <= 0 || isChargedShot(ShotType.SPIRITSHOT) || Rnd.get(100) > getTemplate().getSpsRate())
				return;
			
			// Reduce the amount of sps for this instance.
			_currentSpsCount--;
			
			broadcastPacketInRadius(new MagicSkillUse(this, this, 2061, 1, 0, 0), 600);
			setChargedShot(ShotType.SPIRITSHOT, true);
		}
	}
	
	@Override
	public int getSkillLevel(int skillId)
	{
		for (final List<L2Skill> list : getTemplate().getSkills().values())
		{
			for (final L2Skill skill : list)
				if (skill.getId() == skillId)
					return skill.getLevel();
		}
		return 0;
	}
	
	@Override
	public L2Skill getSkill(int skillId)
	{
		for (final List<L2Skill> list : getTemplate().getSkills().values())
		{
			for (final L2Skill skill : list)
				if (skill.getId() == skillId)
					return skill;
		}
		return null;
	}
	
	@Override
	public ItemInstance getActiveWeaponInstance()
	{
		return null;
	}
	
	@Override
	public Weapon getActiveWeaponItem()
	{
		final int weaponId = getTemplate().getRightHand();
		if (weaponId <= 0)
			return null;
		
		final Item item = ItemData.getInstance().getTemplate(weaponId);
		if (!(item instanceof Weapon))
			return null;
		
		return (Weapon) item;
	}
	
	@Override
	public ItemInstance getSecondaryWeaponInstance()
	{
		return null;
	}
	
	@Override
	public Item getSecondaryWeaponItem()
	{
		final int itemId = getTemplate().getLeftHand();
		if (itemId <= 0)
			return null;
		
		return ItemData.getInstance().getTemplate(itemId);
	}
	
	@Override
	public void reduceCurrentHp(double damage, Creature attacker, boolean awake, boolean isDOT, L2Skill skill)
	{
		// Test the ON_ATTACK ScriptEventType.
		if (attacker != null && !isDead())
		{
			for (Quest quest : getTemplate().getEventQuests(EventHandler.ATTACKED))
				quest.onAttacked(this, attacker, (int) damage, skill);
		}
		
		// Reduce the current HP of the Attackable and launch the doDie Task if necessary
		super.reduceCurrentHp(damage, attacker, awake, isDOT, skill);
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		if (!super.doDie(killer))
			return false;
		
		_leftHandItemId = getTemplate().getLeftHand();
		_rightHandItemId = getTemplate().getRightHand();
		
		_enchantEffect = getTemplate().getEnchantEffect();
		
		_currentCollisionHeight = getTemplate().getCollisionHeight();
		_currentCollisionRadius = getTemplate().getCollisionRadius();
		
		DecayTaskManager.getInstance().add(this, getTemplate().getCorpseTime());
		
		for (Quest quest : getTemplate().getEventQuests(EventHandler.MY_DYING))
			ThreadPool.schedule(() -> quest.onMyDying(this, killer), 3000);
		
		// Party aggro (minion/master).
		if (isMaster() || hasMaster())
		{
			// If we have a master, we call the event.
			final Npc master = getMaster();
			if (master != null)
			{
				// Retrieve scripts associated to called Attackable and notify the party call.
				for (Quest quest : getTemplate().getEventQuests(EventHandler.PARTY_DIED))
					quest.onPartyDied(this, master);
			}
			
			// For all minions except me, we call the event.
			for (Npc minion : getMinions())
			{
				if (minion == this)
					continue;
				
				// Retrieve scripts associated to called Attackable and notify the party call.
				for (Quest quest : getTemplate().getEventQuests(EventHandler.PARTY_DIED))
					quest.onPartyDied(this, minion);
			}
			
			if (isMaster())
				getMinions().forEach(n -> n.setMaster(null));
		}
		
		// Social aggro.
		final String[] actorClans = getTemplate().getClans();
		if (actorClans != null && getTemplate().getClanRange() > 0)
		{
			for (final Npc called : getKnownTypeInRadius(Npc.class, getTemplate().getClanRange()))
			{
				// Called is dead.
				if (called.isDead())
					continue;
				
				// Caller clan doesn't correspond to the called clan.
				if (!ArraysUtil.contains(actorClans, called.getTemplate().getClans()))
					continue;
				
				// Called ignores that type of caller id.
				if (ArraysUtil.contains(called.getTemplate().getIgnoredIds(), getNpcId()))
					continue;
				
				// Check if the Attackable is in the LoS of the caller.
				if (!GeoEngine.getInstance().canSeeTarget(this, called))
					continue;
				
				// Retrieve scripts associated to called Attackable and notify the clan call.
				for (Quest quest : called.getTemplate().getEventQuests(EventHandler.CLAN_DIED))
					quest.onClanDied(this, called);
			}
		}
		return true;
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		// Initialize ss/sps counts.
		_currentSsCount = getTemplate().getSsCount();
		_currentSpsCount = getTemplate().getSpsCount();
		
		for (Quest quest : getTemplate().getEventQuests(EventHandler.CREATED))
			quest.onCreated(this);
		
		if (_spawn != null)
			_spawn.onSpawn(this);
		
		// Process the walking route, if any.
		getAI().moveToNextPoint();
	}
	
	@Override
	public void onDecay()
	{
		if (isDecayed())
			return;
		
		setDecayed(true);
		
		for (Quest quest : getTemplate().getEventQuests(EventHandler.DECAYED))
			quest.onDecayed(this);
		
		// Remove the Npc from the world when the decay task is launched.
		super.onDecay();
		
		// Respawn it, if possible.
		if (_spawn != null)
			_spawn.onDecay(this);
	}
	
	@Override
	public void deleteMe()
	{
		// Decay
		onDecay();
		
		DecayTaskManager.getInstance().cancel(this);
		stopAllEffects();
		
		super.deleteMe();
	}
	
	@Override
	public double getCollisionHeight()
	{
		return _currentCollisionHeight;
	}
	
	@Override
	public double getCollisionRadius()
	{
		return _currentCollisionRadius;
	}
	
	@Override
	public String toString()
	{
		return StringUtil.trimAndDress(getName(), 20) + " [npcId=" + getNpcId() + " objId=" + getObjectId() + "]";
	}
	
	@Override
	public boolean isAttackingDisabled()
	{
		return super.isAttackingDisabled() || isCoreAiDisabled();
	}
	
	@Override
	public void forceDecay()
	{
		if (isDecayed())
			return;
		
		super.forceDecay();
	}
	
	public int getCurrentSsCount()
	{
		return _currentSsCount;
	}
	
	public int getCurrentSpsCount()
	{
		return _currentSpsCount;
	}
	
	/**
	 * @return the {@link ASpawn} associated to this {@link Npc}.
	 */
	public ASpawn getSpawn()
	{
		return _spawn;
	}
	
	/**
	 * Set the {@link ASpawn} of this {@link Npc}.
	 * @param spawn : The ASpawn to set.
	 */
	public void setSpawn(ASpawn spawn)
	{
		_spawn = spawn;
	}
	
	/**
	 * Sets {@link SpawnLocation} of this {@link Npc}. Used mostly for raid bosses teleporting, so return home mechanism works.
	 * @param loc : new spawn location.
	 */
	public final void setSpawnLocation(SpawnLocation loc)
	{
		_spawnLoc = loc;
	}
	
	/**
	 * @return The {@link SpawnLocation} of this {@link Npc}, regardless the type of spawn (e.g. null, {@link Spawn}, {@link MultiSpawn}, etc).
	 */
	public final SpawnLocation getSpawnLocation()
	{
		return _spawnLoc;
	}
	
	public Npc getMaster()
	{
		return _master;
	}
	
	public void setMaster(Npc npc)
	{
		_master = npc;
	}
	
	public boolean isMaster()
	{
		return _minions != null;
	}
	
	public boolean hasMaster()
	{
		return _master != null;
	}
	
	public Set<Npc> getMinions()
	{
		if (_master == null)
		{
			if (_minions == null)
				_minions = ConcurrentHashMap.newKeySet();
			
			return _minions;
		}
		return _master.getMinions();
	}
	
	/**
	 * Teleport this {@link Npc} to its {@link Npc} master.
	 */
	public void teleportToMaster()
	{
		final Npc master = getMaster();
		if (master == null)
			return;
		
		teleportTo(getSpawn().getSpawnLocation(), 0);
	}
	
	/**
	 * @return True, when this {@link Npc} is in its area of free movement/territory.
	 */
	public boolean isInMyTerritory()
	{
		final Npc master = getMaster();
		if (master != null)
			return master.isInMyTerritory();
		
		return _spawn.isInMyTerritory(this);
	}
	
	public void scheduleRespawn(long delay)
	{
		_respawnTask = ThreadPool.schedule(() ->
		{
			if (_spawn != null)
				_spawn.doRespawn(this);
		}, delay);
	}
	
	public void cancelRespawn()
	{
		if (_respawnTask != null)
		{
			_respawnTask.cancel(false);
			_respawnTask = null;
		}
	}
	
	public void scheduleDespawn(long delay)
	{
		ThreadPool.schedule(() ->
		{
			if (!isDecayed())
				deleteMe();
		}, delay);
	}
	
	public boolean isDecayed()
	{
		return _isDecayed;
	}
	
	public void setDecayed(boolean decayed)
	{
		_isDecayed = decayed;
	}
	
	/**
	 * Broadcast a {@link SocialAction} packet with a specific id. It refreshs the timer.
	 * @param id : The animation id to broadcast.
	 */
	public void onRandomAnimation(int id)
	{
		final long now = System.currentTimeMillis();
		if (now - _lastSocialBroadcast > SOCIAL_INTERVAL)
		{
			_lastSocialBroadcast = now;
			broadcastPacket(new SocialAction(this, id));
		}
	}
	
	/**
	 * Add this {@link Npc} on {@link RandomAnimationTaskManager}. The task will be fired after a calculated delay.
	 */
	public void startRandomAnimationTimer()
	{
		if (!hasRandomAnimation())
			return;
		
		RandomAnimationTaskManager.getInstance().add(this, calculateRandomAnimationTimer());
	}
	
	public int calculateRandomAnimationTimer()
	{
		return Rnd.get(Config.MIN_NPC_ANIMATION, Config.MAX_NPC_ANIMATION);
	}
	
	/**
	 * @return True if this {@link Npc} allows Random Animation, false if not or if the {@link NpcAiType} is a corpse.
	 */
	public boolean hasRandomAnimation()
	{
		return Config.MAX_NPC_ANIMATION > 0 && getTemplate().getAiType() != NpcAiType.CORPSE;
	}
	
	/**
	 * @return The id of this {@link Npc} contained in its {@link NpcTemplate}.
	 */
	public int getNpcId()
	{
		return getTemplate().getNpcId();
	}
	
	/**
	 * @return True if this {@link Npc} is agressive.
	 */
	public boolean isAggressive()
	{
		return false;
	}
	
	/**
	 * @return The id of the item in the left hand of this {@link Npc}.
	 */
	public int getLeftHandItemId()
	{
		return _leftHandItemId;
	}
	
	/**
	 * Set the id of the item in the left hand of this {@link Npc}.
	 * @param itemId : The itemId to set.
	 */
	public void setLeftHandItemId(int itemId)
	{
		_leftHandItemId = itemId;
	}
	
	/**
	 * @return The id of the item in the right hand of this {@link Npc}.
	 */
	public int getRightHandItemId()
	{
		return _rightHandItemId;
	}
	
	/**
	 * Set the id of the item in the right hand of this {@link Npc}.
	 * @param itemId : The itemId to set.
	 */
	public void setRightHandItemId(int itemId)
	{
		_rightHandItemId = itemId;
	}
	
	public int getEnchantEffect()
	{
		return _enchantEffect;
	}
	
	public void setEnchantEffect(int enchant)
	{
		_enchantEffect = enchant;
	}
	
	public void setCollisionHeight(double height)
	{
		_currentCollisionHeight = height;
	}
	
	public void setCollisionRadius(double radius)
	{
		_currentCollisionRadius = radius;
	}
	
	public int getScriptValue()
	{
		return _scriptValue;
	}
	
	public void setScriptValue(int val)
	{
		_scriptValue = val;
	}
	
	public boolean isScriptValue(int val)
	{
		return _scriptValue == val;
	}
	
	/**
	 * @return True if this {@link Npc} can be a warehouse manager, false otherwise.
	 */
	public boolean isWarehouse()
	{
		return false;
	}
	
	/**
	 * @return The {@link Castle} this {@link Npc} belongs to.
	 */
	public final Castle getCastle()
	{
		return _castle;
	}
	
	public void setCastle(Castle castle)
	{
		_castle = castle;
	}
	
	/**
	 * @return The {@link ClanHall} this {@link Npc} belongs to.
	 */
	public final ClanHall getClanHall()
	{
		return _clanHall;
	}
	
	/**
	 * @return The {@link SiegableHall} this {@link Npc} belongs to.
	 */
	public final SiegableHall getSiegableHall()
	{
		return _siegableHall;
	}
	
	/**
	 * @param player : The {@link Player} used as reference.
	 * @return True if the {@link Player} set as parameter is the clan leader owning this {@link Npc} (being a {@link Castle}, {@link ClanHall} or {@link SiegableHall}).
	 */
	public boolean isLordOwner(Player player)
	{
		// The player isn't a Clan leader, return.
		if (!player.isClanLeader())
			return false;
		
		// Test Castle ownership.
		if (_castle != null && _castle.getOwnerId() == player.getClanId())
			return true;
		
		// Test ClanHall / SiegableHall ownership.
		if (_clanHall != null && _clanHall.getOwnerId() == player.getClanId())
			return true;
		
		return false;
	}
	
	/**
	 * @return True if this {@link Npc} got its regular AI behavior disabled.
	 */
	public boolean isCoreAiDisabled()
	{
		return _isCoreAiDisabled || getTemplate().getAiType() == NpcAiType.CORPSE;
	}
	
	/**
	 * Toggle on/off the regular AI behavior of this {@link Npc}.
	 * @param value : The value to set.
	 */
	public void disableCoreAi(boolean value)
	{
		_isCoreAiDisabled = value;
	}
	
	public List<Integer> getObserverGroups()
	{
		return _observerGroups;
	}
	
	public void setObserverGroups(List<Integer> groups)
	{
		_observerGroups = groups;
	}
	
	public boolean isReversePath()
	{
		return _isReversePath;
	}
	
	public void setReversePath(boolean isReversePath)
	{
		_isReversePath = isReversePath;
	}
	
	/**
	 * @return The Exp reward of this {@link Npc} based on its {@link NpcTemplate} and modified by {@link Config#RATE_XP}.
	 */
	public int getExpReward()
	{
		return (int) (getTemplate().getRewardExp() * Config.RATE_XP);
	}
	
	/**
	 * @return The SP reward of this {@link Npc} based on its {@link NpcTemplate} and modified by {@link Config#RATE_SP}.
	 */
	public int getSpReward()
	{
		return (int) (getTemplate().getRewardSp() * Config.RATE_SP);
	}
	
	/**
	 * Open a quest or chat window for a {@link Player} with the text of this {@link Npc} based of the {@link String} set as parameter.
	 * @param player : The {@link Player} to test.
	 * @param command : The {@link String} used as command bypass received from client.
	 */
	public void onBypassFeedback(Player player, String command)
	{
		if (command.equalsIgnoreCase("TerritoryStatus"))
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			
			if (getCastle().getOwnerId() > 0)
			{
				html.setFile("data/html/territorystatus.htm");
				final Clan clan = ClanTable.getInstance().getClan(getCastle().getOwnerId());
				html.replace("%clanname%", clan.getName());
				html.replace("%clanleadername%", clan.getLeaderName());
				html.replace("%ServerName%", Config.SERVER_NAME);
				html.replace("%bloking%", player.isDisableHair() ? DESATIVED : ACTIVED);
				html.replace("%count%", player.getBuffCount() + " / " + player.getMaxBuffCount());
				html.replace("%online%", World.getInstance().getPlayers().size());
				html.replace("%autogb%", player.isAutoGb() ? ACTIVED : DESATIVED);

			}
			else
				html.setFile("data/html/territorynoclan.htm");
			
			html.replace("%castlename%", getCastle().getName());
			html.replace("%taxpercent%", getCastle().getTaxPercent());
			html.replace("%objectId%", getObjectId());
			html.replace("%ServerName%", Config.SERVER_NAME);
			html.replace("%bloking%", player.isDisableHair() ? DESATIVED : ACTIVED);
			html.replace("%count%", player.getBuffCount() + " / " + player.getMaxBuffCount());
			html.replace("%online%", World.getInstance().getPlayers().size());
			html.replace("%autogb%", player.isAutoGb() ? ACTIVED : DESATIVED);

			if (getCastle().getCastleId() > 6)
				html.replace("%territory%", "The Kingdom of Elmore");
			else
				html.replace("%territory%", "The Kingdom of Aden");
			
			player.sendPacket(html);
		}
		else if (command.startsWith("Quest"))
		{
			String quest = "";
			try
			{
				quest = command.substring(5).trim();
			}
			catch (final IndexOutOfBoundsException ioobe)
			{
			}
			
			if (quest.isEmpty())
				showQuestWindowGeneral(player, this);
			else
				showQuestWindowSingle(player, this, ScriptData.getInstance().getQuest(quest));
		}
		else if (command.startsWith("Chat"))
		{
			int val = 0;
			try
			{
				val = Integer.parseInt(command.substring(5));
			}
			catch (final IndexOutOfBoundsException ioobe)
			{
			}
			catch (final NumberFormatException nfe)
			{
			}
			
			showChatWindow(player, val);
		}
		else if (command.startsWith("Link"))
		{
			final String path = command.substring(5).trim();
			if (path.indexOf("..") != -1)
				return;
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile("data/html/" + path);
			html.replace("%objectId%", getObjectId());
			html.replace("%ServerName%", Config.SERVER_NAME);
			html.replace("%bloking%", player.isDisableHair() ? DESATIVED : ACTIVED);
			html.replace("%count%", player.getBuffCount() + " / " + player.getMaxBuffCount());
			html.replace("%online%", World.getInstance().getPlayers().size());
			html.replace("%dresmedisable%", player.isSkinDisable() ? ACTIVED : DESATIVED);
			html.replace("%autogb%", player.isAutoGb() ? ACTIVED : DESATIVED);
			html.replace("%dungstat1%", DungeonManagerNpc.getPlayerStatus(player, 1));
			html.replace("%dungstat2%", DungeonManagerNpc.getPlayerStatus(player, 2));
			player.sendPacket(html);
		}
		else if (command.startsWith("Loto"))
		{
			int val = 0;
			try
			{
				val = Integer.parseInt(command.substring(5));
			}
			catch (final IndexOutOfBoundsException ioobe)
			{
			}
			catch (final NumberFormatException nfe)
			{
			}
			
			if (val == 0)
			{
				// new loto ticket
				for (int i = 0; i < 5; i++)
					player.setLoto(i, 0);
			}
			showLotoWindow(player, val);
		}
		else if (command.startsWith("CPRecovery"))
		{
			if (getNpcId() != 31225 && getNpcId() != 31226)
				return;
			
			if (player.isCursedWeaponEquipped())
			{
				player.sendMessage("Go away, you're not welcome here.");
				return;
			}
			
			// Consume 100 adenas
			if (player.reduceAdena("RestoreCP", 100, player.getCurrentFolk(), true))
			{
				setTarget(player);
				getAI().tryToCast(player, FrequentSkill.ARENA_CP_RECOVERY.getSkill());
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CP_WILL_BE_RESTORED).addCharName(player));
			}
		}
		else if (command.startsWith("observe_group"))
		{
			final StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			
			final List<ObserverLocation> locs = ObserverGroupData.getInstance().getObserverLocations(Integer.parseInt(st.nextToken()));
			if (locs == null)
				return;
			
			final StringBuilder sb = new StringBuilder();
			sb.append("<html><body>&$650;<br><br>");
			
			for (ObserverLocation loc : locs)
			{
				StringUtil.append(sb, "<a action=\"bypass -h npc_", getObjectId(), "_observe ", loc.getLocId(), "\">&$", loc.getLocId(), ";");
				
				if (loc.getCost() > 0)
					StringUtil.append(sb, " - ", loc.getCost(), " &#57;");
				
				StringUtil.append(sb, "</a><br1>");
			}
			sb.append("</body></html>");
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setHtml(sb.toString());
			
			player.sendPacket(html);
		}
		else if (command.startsWith("observe"))
		{
			final StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			
			final ObserverLocation loc = ObserverGroupData.getInstance().getObserverLocation(Integer.parseInt(st.nextToken()));
			if (loc == null)
				return;
			
			final boolean hasSummon = player.getSummon() != null;
			
			if (loc.getCastleId() > 0)
			{
				// Summon check. Siege observe type got an appropriate message.
				if (hasSummon)
				{
					player.sendPacket(SystemMessageId.NO_OBSERVE_WITH_PET);
					return;
				}
				
				// Active siege must exist.
				final Castle castle = CastleManager.getInstance().getCastleById(loc.getCastleId());
				if (castle == null || !castle.getSiege().isInProgress())
				{
					player.sendPacket(SystemMessageId.ONLY_VIEW_SIEGE);
					return;
				}
			}
			// Summon check for regular observe. No message on retail.
			else if (hasSummon)
				return;
			
			// Can't observe if under attack stance.
			if (player.isInCombat())
			{
				player.sendPacket(SystemMessageId.CANNOT_OBSERVE_IN_COMBAT);
				return;
			}
			
			// Olympiad registration check. No message on retail.
			if (OlympiadManager.getInstance().isRegisteredInComp(player))
				return;
			
			player.enterObserverMode(loc);
		}
		else if (command.startsWith("multisell"))
		{
			MultisellData.getInstance().separateAndSend(command.substring(9).trim(), player, this, false);
		}
		else if (command.startsWith("exc_multisell"))
		{
			MultisellData.getInstance().separateAndSend(command.substring(13).trim(), player, this, true);
		}
		else if (command.startsWith("Augment"))
		{
			final int cmdChoice = Integer.parseInt(command.substring(8, 9).trim());
			switch (cmdChoice)
			{
				case 1:
					player.sendPacket(SystemMessageId.SELECT_THE_ITEM_TO_BE_AUGMENTED);
					player.sendPacket(ExShowVariationMakeWindow.STATIC_PACKET);
					break;
				case 2:
					player.sendPacket(SystemMessageId.SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION);
					player.sendPacket(ExShowVariationCancelWindow.STATIC_PACKET);
					break;
			}
		}
		else if (command.startsWith("EnterRift"))
		{
			try
			{
				final Byte b1 = Byte.parseByte(command.substring(10)); // Selected Area: Recruit, Soldier etc
				DimensionalRiftManager.getInstance().start(player, b1, this);
			}
			catch (final Exception e)
			{
			}
		}
		else if (command.equals("teleport_request"))
		{
			showTeleportWindow(player, TeleportType.STANDARD);
		}
		else if (command.startsWith("teleport"))
		{
			try
			{
				final StringTokenizer st = new StringTokenizer(command, " ");
				st.nextToken();
				
				teleport(player, Integer.parseInt(st.nextToken()));
			}
			catch (final Exception e)
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
			}
		}
		else if (command.startsWith("instant_teleport"))
		{
			try
			{
				final StringTokenizer st = new StringTokenizer(command, " ");
				st.nextToken();
				
				instantTeleport(player, Integer.parseInt(st.nextToken()));
			}
			catch (final Exception e)
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
			}
		}
	}
	
	/**
	 * @param player : The {@link Player} to test.
	 * @return True if the teleport is possible, false otherwise.
	 */
	protected boolean isTeleportAllowed(Player player)
	{
		return true;
	}
	
	/**
	 * Teleport the {@link Player} into the {@link Npc}'s instant teleports {@link List} index.<br>
	 * <br>
	 * The only check is {@link #isTeleportAllowed(Player)}.
	 * @param player : The {@link Player} to test.
	 * @param index : The {@link Location} index information to retrieve from this {@link Npc}'s instant teleports {@link List}.
	 */
	protected void instantTeleport(Player player, int index)
	{
		if (!isTeleportAllowed(player))
			return;
		
		final List<Location> teleports = InstantTeleportData.getInstance().getTeleports(getNpcId());
		if (teleports == null || index > teleports.size())
			return;
		
		final Location teleport = teleports.get(index);
		if (teleport == null)
			return;
		
		player.teleportTo(teleport, 20);
	}
	
	/**
	 * Teleport the {@link Player} into the {@link Npc}'s {@link TeleportLocation}s {@link List} index.<br>
	 * <br>
	 * Following checks are done : {@link #isTeleportAllowed(Player)}, castle siege, price.
	 * @param player : The {@link Player} to test.
	 * @param index : The {@link TeleportLocation} index information to retrieve from this {@link Npc}'s instant teleports {@link List}.
	 */
	protected void teleport(Player player, int index)
	{
		if (!isTeleportAllowed(player))
			return;
		
		final List<TeleportLocation> teleports = TeleportData.getInstance().getTeleports(getNpcId());
		if (teleports == null || index > teleports.size())
			return;
		
		final TeleportLocation teleport = teleports.get(index);
		if (teleport == null)
			return;
		
		if (teleport.getCastleId() > 0)
		{
			final Castle castle = CastleManager.getInstance().getCastleById(teleport.getCastleId());
			if (castle != null && castle.getSiege().isInProgress())
			{
				player.sendPacket(SystemMessageId.CANNOT_PORT_VILLAGE_IN_SIEGE);
				return;
			}
		}
		
		if (Config.FREE_TELEPORT || teleport.getPriceCount() == 0 || player.destroyItemByItemId("InstantTeleport", teleport.getPriceId(), teleport.getPriceCount(), this, true))
			player.teleportTo(teleport, 20);
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	protected NpcTalkCond getNpcTalkCond(Player player)
	{
		return NpcTalkCond.OWNER;
	}
	
	/**
	 * Collect quests in progress and possible quests and show proper quest window to a {@link Player}.
	 * @param player : The player that talk with the Npc.
	 * @param npc : The Npc instance.
	 */
	public static void showQuestWindowGeneral(Player player, Npc npc)
	{
		final List<Quest> quests = new ArrayList<>();
		
		for (Quest quest : npc.getTemplate().getEventQuests(EventHandler.TALKED))
		{
			if (quest == null || !quest.isRealQuest() || quests.contains(quest))
				continue;
			
			final QuestState qs = player.getQuestList().getQuestState(quest.getName());
			if (qs == null || qs.isCreated())
				continue;
			
			quests.add(quest);
		}
		
		for (Quest quest : npc.getTemplate().getEventQuests(EventHandler.QUEST_START))
		{
			if (quest == null || !quest.isRealQuest() || quests.contains(quest))
				continue;
			
			quests.add(quest);
		}
		
		if (quests.isEmpty())
			showQuestWindowSingle(player, npc, null);
		else if (quests.size() == 1)
			showQuestWindowSingle(player, npc, quests.get(0));
		else
			showQuestWindowChoose(player, npc, quests);
	}
	
	/**
	 * Open a quest window on client with the text of this {@link Npc}. Create the {@link QuestState} if not existing.
	 * @param player : The Player that talk with the Npc.
	 * @param npc : The Npc instance.
	 * @param quest : The Quest to check.
	 */
	private static void showQuestWindowSingle(Player player, Npc npc, Quest quest)
	{
		if (quest == null)
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
			html.setHtml(Quest.getNoQuestMsg());
			player.sendPacket(html);
			
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (quest.isRealQuest())
		{
			// Check player being overweight.
			if (player.getWeightPenalty().ordinal() > 2 || player.getStatus().isOverburden())
			{
				player.sendPacket(SystemMessageId.INVENTORY_LESS_THAN_80_PERCENT);
				return;
			}
			
			// Check player has the quest started.
			if (player.getQuestList().getQuestState(quest.getName()) == null)
			{
				// Check available quest slot.
				if (player.getQuestList().getAllQuests(false).size() >= 25)
				{
					final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
					html.setHtml(Quest.getTooMuchQuestsMsg());
					player.sendPacket(html);
					
					player.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				
				// Create new state.
				if (npc.getTemplate().getEventQuests(EventHandler.QUEST_START).contains(quest))
					quest.newQuestState(player);
			}
		}
		
		player.getQuestList().setLastQuestNpcObjectId(npc.getObjectId());
		quest.notifyTalk(npc, player);
	}
	
	/**
	 * Shows the list of available {@link Quest}s for this {@link Npc}.
	 * @param player : The player that talk with the Npc.
	 * @param npc : The Npc instance.
	 * @param quests : The list containing quests of the Npc.
	 */
	private static void showQuestWindowChoose(Player player, Npc npc, List<Quest> quests)
	{
		final StringBuilder sb = new StringBuilder("<html><body>");
		
		for (final Quest q : quests)
		{
			StringUtil.append(sb, "<a action=\"bypass -h npc_%objectId%_Quest ", q.getName(), "\">[", q.getDescr());
			
			final QuestState qs = player.getQuestList().getQuestState(q.getName());
			if (qs != null && qs.isStarted())
				sb.append(" (In Progress)]</a><br>");
			else if (qs != null && qs.isCompleted())
				sb.append(" (Done)]</a><br>");
			else
				sb.append("]</a><br>");
		}
		
		sb.append("</body></html>");
		
		final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		html.setHtml(sb.toString());
		html.replace("%objectId%", npc.getObjectId());
		html.replace("%ServerName%", Config.SERVER_NAME);
		html.replace("%bloking%", player.isDisableHair() ? DESATIVED : ACTIVED);
		html.replace("%count%", player.getBuffCount() + " / " + player.getMaxBuffCount());
		html.replace("%online%", World.getInstance().getPlayers().size());
		html.replace("%dresmedisable%", player.isSkinDisable() ? ACTIVED : DESATIVED);
		html.replace("%autogb%", player.isAutoGb() ? ACTIVED : DESATIVED);

		player.sendPacket(html);
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	/**
	 * Generate the complete path to retrieve a htm, based on npcId.
	 * <ul>
	 * <li>if the file exists on the server (page number = 0) : <B>data/html/default/12006.htm</B> (npcId-page number)</li>
	 * <li>if the file exists on the server (page number > 0) : <B>data/html/default/12006-1.htm</B> (npcId-page number)</li>
	 * <li>if the file doesn't exist on the server : <B>data/html/npcdefault.htm</B> (message : "I have nothing to say to you")</li>
	 * </ul>
	 * @param npcId : The id of the Npc whose text must be displayed.
	 * @param val : The number of the page to display.
	 * @return the pathfile of the selected HTML file in function of the npcId and of the page number.
	 */
	public String getHtmlPath(int npcId, int val)
	{
		String filename;
		
		if (val == 0)
			filename = "data/html/default/" + npcId + ".htm";
		else
			filename = "data/html/default/" + npcId + "-" + val + ".htm";
		
		if (HtmCache.getInstance().isLoadable(filename))
			return filename;
		
		return "data/html/npcdefault.htm";
	}
	
	/**
	 * Broadcast a {@link String} to the knownlist of this {@link Npc}.
	 * @param message : The {@link String} message to send.
	 */
	public void broadcastNpcSay(String message)
	{
		broadcastPacket(new NpcSay(this, SayType.ALL, message));
	}
	
	/**
	 * Broadcast a {@link NpcStringId} to the knownlist of this {@link Npc}.
	 * @param npcStringId : The {@link NpcStringId} to send.
	 */
	public void broadcastNpcSay(NpcStringId npcStringId)
	{
		broadcastNpcSay(npcStringId.getMessage());
	}
	
	/**
	 * Broadcast a {@link NpcStringId} to the knownlist of this {@link Npc}.
	 * @param npcStringId : The {@link NpcStringId} to send.
	 * @param params : Additional parameters for {@link NpcStringId} construction.
	 */
	public void broadcastNpcSay(NpcStringId npcStringId, Object... params)
	{
		broadcastNpcSay(npcStringId.getMessage(params));
	}
	
	/**
	 * Broadcast a {@link String} to the knownlist of this {@link Npc}.
	 * @param message : The {@link String} message to send.
	 */
	public void broadcastNpcShout(String message)
	{
		broadcastPacket(new NpcSay(this, SayType.SHOUT, message));
	}
	
	/**
	 * Broadcast a {@link NpcStringId} to the knownlist of this {@link Npc}.
	 * @param npcStringId : The {@link NpcStringId} to send.
	 */
	public void broadcastNpcShout(NpcStringId npcStringId)
	{
		broadcastNpcShout(npcStringId.getMessage());
	}
	
	/**
	 * Broadcast a {@link NpcStringId} to the knownlist of this {@link Npc}.
	 * @param npcStringId : The {@link NpcStringId} to send.
	 * @param params : Additional parameters for {@link NpcStringId} construction.
	 */
	public void broadcastNpcShout(NpcStringId npcStringId, Object... params)
	{
		broadcastNpcShout(npcStringId.getMessage(params));
	}
	
	/**
	 * Broadcast a {@link String} on screen to the knownlist of this {@link Npc}.
	 * @param time : The time to show the message on screen.
	 * @param message : The {@link String} to send.
	 */
	public void broadcastOnScreen(int time, String message)
	{
		broadcastPacket(new ExShowScreenMessage(message, time));
	}
	
	/**
	 * Broadcast a {@link NpcStringId} on screen to the knownlist of this {@link Npc}.
	 * @param time : The time to show the message on screen.
	 * @param npcStringId : The {@link NpcStringId} to send.
	 */
	public void broadcastOnScreen(int time, NpcStringId npcStringId)
	{
		broadcastOnScreen(time, npcStringId.getMessage());
	}
	
	/**
	 * Broadcast a {@link NpcStringId} on screen to the knownlist of this {@link Npc}.
	 * @param time : The time to show the message on screen.
	 * @param npcStringId : The {@link NpcStringId} to send.
	 * @param params : Additional parameters for {@link NpcStringId} construction.
	 */
	public void broadcastOnScreen(int time, NpcStringId npcStringId, Object... params)
	{
		broadcastOnScreen(time, npcStringId.getMessage(params));
	}
	
	/**
	 * Open a Loto window for the {@link Player} set as parameter.
	 * <ul>
	 * <li>0 - first buy lottery ticket window</li>
	 * <li>1-20 - buttons</li>
	 * <li>21 - second buy lottery ticket window</li>
	 * <li>22 - selected ticket with 5 numbers</li>
	 * <li>23 - current lottery jackpot</li>
	 * <li>24 - Previous winning numbers/Prize claim</li>
	 * <li>>24 - check lottery ticket by item object id</li>
	 * </ul>
	 * @param player : The player that talk with this Npc.
	 * @param val : The number of the page to display.
	 */
	public void showLotoWindow(Player player, int val)
	{
		final int npcId = getTemplate().getNpcId();
		
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		
		if (val == 0) // 0 - first buy lottery ticket window
		{
			html.setFile(getHtmlPath(npcId, 1));
		}
		else if (val >= 1 && val <= 21) // 1-20 - buttons, 21 - second buy lottery ticket window
		{
			if (!LotteryManager.getInstance().isStarted())
			{
				// tickets can't be sold
				player.sendPacket(SystemMessageId.NO_LOTTERY_TICKETS_CURRENT_SOLD);
				return;
			}
			if (!LotteryManager.getInstance().isSellableTickets())
			{
				// tickets can't be sold
				player.sendPacket(SystemMessageId.NO_LOTTERY_TICKETS_AVAILABLE);
				return;
			}
			
			html.setFile(getHtmlPath(npcId, 5));
			
			int count = 0;
			int found = 0;
			// counting buttons and unsetting button if found
			for (int i = 0; i < 5; i++)
			{
				if (player.getLoto(i) == val)
				{
					// unsetting button
					player.setLoto(i, 0);
					found = 1;
				}
				else if (player.getLoto(i) > 0)
				{
					count++;
				}
			}
			
			// if not rearched limit 5 and not unseted value
			if (count < 5 && found == 0 && val <= 20)
				for (int i = 0; i < 5; i++)
					if (player.getLoto(i) == 0)
					{
						player.setLoto(i, val);
						break;
					}
				
			// setting pusshed buttons
			count = 0;
			for (int i = 0; i < 5; i++)
				if (player.getLoto(i) > 0)
				{
					count++;
					String button = String.valueOf(player.getLoto(i));
					if (player.getLoto(i) < 10)
						button = "0" + button;
					
					final String search = "fore=\"L2UI.lottoNum" + button + "\" back=\"L2UI.lottoNum" + button + "a_check\"";
					final String replace = "fore=\"L2UI.lottoNum" + button + "a_check\" back=\"L2UI.lottoNum" + button + "\"";
					html.replace(search, replace);
				}
			
			if (count == 5)
			{
				final String search = "0\">Return";
				final String replace = "22\">The winner selected the numbers above.";
				html.replace(search, replace);
			}
		}
		else if (val == 22) // 22 - selected ticket with 5 numbers
		{
			if (!LotteryManager.getInstance().isStarted())
			{
				// tickets can't be sold
				player.sendPacket(SystemMessageId.NO_LOTTERY_TICKETS_CURRENT_SOLD);
				return;
			}
			if (!LotteryManager.getInstance().isSellableTickets())
			{
				// tickets can't be sold
				player.sendPacket(SystemMessageId.NO_LOTTERY_TICKETS_AVAILABLE);
				return;
			}
			
			final int lotonumber = LotteryManager.getInstance().getId();
			
			int enchant = 0;
			int type2 = 0;
			
			for (int i = 0; i < 5; i++)
			{
				if (player.getLoto(i) == 0)
					return;
				
				if (player.getLoto(i) < 17)
					enchant += Math.pow(2, player.getLoto(i) - 1);
				else
					type2 += Math.pow(2, player.getLoto(i) - 17);
			}
			
			if (!player.reduceAdena("Loto", Config.LOTTERY_TICKET_PRICE, this, true))
				return;
			
			LotteryManager.getInstance().increasePrize(Config.LOTTERY_TICKET_PRICE);
			
			final ItemInstance item = new ItemInstance(IdFactory.getInstance().getNextId(), 4442);
			item.setCount(1);
			item.setCustomType1(lotonumber);
			item.setEnchantLevel(enchant);
			item.setCustomType2(type2);
			
			player.addItem("Loto", item, player, false);
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1).addItemName(4442));
			
			html.setFile(getHtmlPath(npcId, 3));
		}
		else if (val == 23) // 23 - current lottery jackpot
		{
			html.setFile(getHtmlPath(npcId, 3));
		}
		else if (val == 24) // 24 - Previous winning numbers/Prize claim
		{
			final int lotoNumber = LotteryManager.getInstance().getId();
			
			final StringBuilder sb = new StringBuilder();
			for (final ItemInstance item : player.getInventory().getItems())
			{
				if (item == null)
					continue;
				
				if (item.getItemId() == 4442 && item.getCustomType1() < lotoNumber)
				{
					StringUtil.append(sb, "<a action=\"bypass -h npc_%objectId%_Loto ", item.getObjectId(), "\">", item.getCustomType1(), " Event Number ");
					
					final int[] numbers = LotteryManager.decodeNumbers(item.getEnchantLevel(), item.getCustomType2());
					for (int i = 0; i < 5; i++)
						StringUtil.append(sb, numbers[i], " ");
					
					final int[] check = LotteryManager.checkTicket(item);
					if (check[0] > 0)
					{
						switch (check[0])
						{
							case 1:
								sb.append("- 1st Prize");
								break;
							case 2:
								sb.append("- 2nd Prize");
								break;
							case 3:
								sb.append("- 3th Prize");
								break;
							case 4:
								sb.append("- 4th Prize");
								break;
						}
						StringUtil.append(sb, " ", check[1], "a.");
					}
					sb.append("</a><br>");
				}
			}
			
			if (sb.length() == 0)
				sb.append("There is no winning lottery ticket...<br>");
			
			html.setFile(getHtmlPath(npcId, 4));
			html.replace("%result%", sb.toString());
		}
		else if (val == 25) // 25 - lottery instructions
		{
			html.setFile(getHtmlPath(npcId, 2));
			html.replace("%prize5%", Config.LOTTERY_5_NUMBER_RATE * 100);
			html.replace("%prize4%", Config.LOTTERY_4_NUMBER_RATE * 100);
			html.replace("%prize3%", Config.LOTTERY_3_NUMBER_RATE * 100);
			html.replace("%prize2%", Config.LOTTERY_2_AND_1_NUMBER_PRIZE);
		}
		else if (val > 25) // >25 - check lottery ticket by item object id
		{
			final ItemInstance item = player.getInventory().getItemByObjectId(val);
			if (item == null || item.getItemId() != 4442 || item.getCustomType1() >= LotteryManager.getInstance().getId())
				return;
			
			if (player.destroyItem("Loto", item, this, true))
			{
				final int adena = LotteryManager.checkTicket(item)[1];
				if (adena > 0)
					player.addAdena("Loto", adena, this, true);
				player.getMissions().update(MissionType.LOTTERY_WIN);
			}
			return;
		}
		html.replace("%objectId%", getObjectId());
		html.replace("%race%", LotteryManager.getInstance().getId());
		html.replace("%adena%", LotteryManager.getInstance().getPrize());
		html.replace("%ticket_price%", Config.LOTTERY_TICKET_PRICE);
		html.replace("%enddate%", DateFormat.getDateInstance().format(LotteryManager.getInstance().getEndDate()));
		html.replace("%ServerName%", Config.SERVER_NAME);
		html.replace("%bloking%", player.isDisableHair() ? DESATIVED : ACTIVED);
		html.replace("%count%", player.getBuffCount() + " / " + player.getMaxBuffCount());
		html.replace("%online%", World.getInstance().getPlayers().size());
		html.replace("%dresmedisable%", player.isSkinDisable() ? ACTIVED : DESATIVED);
		html.replace("%autogb%", player.isAutoGb() ? ACTIVED : DESATIVED);

		player.sendPacket(html);
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	private static final String ACTIVED = "<font color=FFFF00>ON</font>";
	private static final String DESATIVED = "<font color=FF0000>OFF</font>";
	
	/**
	 * Research the pk chat window HTM related to this {@link Npc}, based on a {@link String} folder.<br>
	 * Send the content to the {@link Player} passed as parameter.
	 * @param player : The {@link Player} to send the HTM.
	 * @param type : The folder to search on.
	 * @return True if such HTM exists, false otherwise.
	 */
	protected boolean showPkDenyChatWindow(Player player, String type)
	{
		final String content = HtmCache.getInstance().getHtm("data/html/" + type + "/" + getNpcId() + "-pk.htm");
		if (content != null)
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setHtml(content);
			html.replace("%dresmedisable%", player.isSkinDisable() ? ACTIVED : DESATIVED);
			html.replace("%autogb%", player.isAutoGb() ? ACTIVED : DESATIVED);

			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return true;
		}
		return false;
	}
	
	/**
	 * Build and send an HTM to a {@link Player}, based on {@link Npc}'s observer groups.
	 * @param player : The {@link Player} to test.
	 */
	public void showObserverWindow(Player player)
	{
		if (_observerGroups == null)
			return;
		
		final StringBuilder sb = new StringBuilder();
		sb.append("<html><body>&$650;<br><br>");
		
		for (int groupId : _observerGroups)
			StringUtil.append(sb, "<a action=\"bypass -h npc_", getObjectId(), "_observe_group ", groupId, "\">&$", groupId, ";</a><br1>");
		
		sb.append("</body></html>");
		
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setHtml(sb.toString());
		
		player.sendPacket(html);
	}
	
	/**
	 * Build and send an HTM to a {@link Player}, based on {@link Npc}'s {@link TeleportLocation}s and {@link TeleportType}.
	 * @param player : The {@link Player} to test.
	 * @param type : The {@link TeleportType} to filter.
	 */
	public void showTeleportWindow(Player player, TeleportType type)
	{
		final List<TeleportLocation> teleports = TeleportData.getInstance().getTeleports(getNpcId());
		if (teleports == null)
			return;
		
		final StringBuilder sb = new StringBuilder();
		sb.append("<html><body>&$556;<br><br>");
		
		for (int index = 0; index < teleports.size(); index++)
		{
			final TeleportLocation teleport = teleports.get(index);
			if (teleport == null || type != teleport.getType())
				continue;
			
			StringUtil.append(sb, "<a action=\"bypass -h npc_", getObjectId(), "_teleport ", index, "\" msg=\"811;", teleport.getDesc(), "\">", teleport.getDesc());
			
			if (!Config.FREE_TELEPORT)
			{
				final int priceCount = teleport.getCalculatedPriceCount(player);
				if (priceCount > 0)
					StringUtil.append(sb, " - ", priceCount, " &#", teleport.getPriceId(), ";");
			}
			
			sb.append("</a><br1>");
		}
		sb.append("</body></html>");
		
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setHtml(sb.toString());
		
		player.sendPacket(html);
	}
	
	/**
	 * Open a chat window on client with the text of the Npc.<br>
	 * Send the content to the {@link Player} passed as parameter.
	 * @param player : The player that talk with the Npc.
	 */
	public void showChatWindow(Player player)
	{
		showChatWindow(player, 0);
	}
	
	/**
	 * Open a chat window on client with the text specified by {@link #getHtmlPath} and val parameter.<br>
	 * Send the content to the {@link Player} passed as parameter.
	 * @param player : The player that talk with the Npc.
	 * @param val : The current htm page to show.
	 */
	public void showChatWindow(Player player, int val)
	{
		showChatWindow(player, getHtmlPath(getNpcId(), val));
	}
	
	/**
	 * Open a chat window on client with the text specified by the given file name and path.<br>
	 * Send the content to the {@link Player} passed as parameter.
	 * @param player : The player that talk with the Npc.
	 * @param filename : The filename that contains the text to send.
	 */
	public final void showChatWindow(Player player, String filename)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		NpcTemplate npc = NpcData.getInstance().getTemplateByName(getName());
		html.replace("%npcname%", npc.getName());
		html.replace("%objectId%", getObjectId());
		html.replace("%count%", player.getBuffCount() + " / " + player.getMaxBuffCount());
		html.replace("%AugmentsRate%", "" + Config.AUGMENTATION_BASESTAT_CHANCE + "%");
		html.replace("%ServerName%", Config.SERVER_NAME);

		html.replace("%bloking%", player.isDisableHair() ? DESATIVED : ACTIVED);
		html.replace("%dresmedisable%", player.isSkinDisable() ? ACTIVED : DESATIVED);
		html.replace("%online%", World.getInstance().getPlayers().size());
		html.replace("%autogb%", player.isAutoGb() ? ACTIVED : DESATIVED);
		html.replace("%dungstat1%", DungeonManagerNpc.getPlayerStatus(player, 1));
		html.replace("%dungstat2%", DungeonManagerNpc.getPlayerStatus(player, 2));

		final int npcId = getNpcId();
		if (npcId == EventManager.getInstance().getInt("managerNpcId"))
		{
			EventManager.getInstance().showFirstHtml(player, getObjectId());
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (EventManager.getInstance().isRunning() && EventManager.getInstance().isRegistered(player) && EventManager.getInstance().getCurrentEvent().onTalkNpc(this, player))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		player.sendPacket(html);
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	/**
	 * Move this {@link Npc} from its initial {@link Spawn} location using a defined random offset. The {@link Npc} will circle around the initial location.
	 * @param offset : The random offset used.
	 */
	public void moveFromSpawnPointUsingRandomOffset(int offset)
	{
		// No spawn point or offset isn't noticeable ; return instantly.
		if (_spawn == null || offset < 10)
			return;
		
		// Generate a new Location and calculate the destination.
		final Location loc = _spawn.getRandomWalkLocation(this, offset);
		if (loc != null)
		{
			// Try to move to the position.
			getAI().tryToMoveTo(loc, null);
		}
	}
	
	/**
	 * Force this {@link Attackable} to attack a given {@link Creature}.
	 * @param creature : The {@link Creature} to attack.
	 * @param hate : The amount of hate to set.
	 */
	public void forceAttack(Creature creature, int hate)
	{
		forceRunStance();
		getAI().tryToAttack(creature);
	}
	
	@Override
	public void onActiveRegion()
	{
		startRandomAnimationTimer();
	}
	
	/**
	 * Enforce the call of {@link EventHandler#SEE_ITEM}.
	 * @param radius : The radius.
	 * @param quantity : The quantity of items to check.
	 * @param ids : The ids of {@link ItemInstance}s.
	 */
	public void lookItem(int radius, int quantity, int... ids)
	{
		final List<ItemInstance> items = getKnownTypeInRadius(ItemInstance.class, radius, i -> ArraysUtil.contains(ids, i.getItem().getItemId()));
		if (!items.isEmpty())
		{
			for (Quest quest : getTemplate().getEventQuests(EventHandler.SEE_ITEM))
				quest.onSeeItem(this, quantity, items);
		}
	}
	
	/**
	 * Enforce the call of {@link EventHandler#SEE_CREATURE}.
	 * @param radius : The radius.
	 */
	public void lookNeighbor(int radius)
	{
		for (Creature creature : getKnownTypeInRadius(Creature.class, radius))
		{
			for (Quest quest : getTemplate().getEventQuests(EventHandler.SEE_CREATURE))
				quest.onSeeCreature(this, creature);
		}
	}
	
	@Override
	public void onActionShift(Player player)
	{
		if (!(this instanceof Monster))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		final WorldObject targetWorldObject = player.getTarget();
		if (targetWorldObject instanceof Npc)
		{
			final Npc targetNpc = (Npc) targetWorldObject;
			
			html.setFile("data/html/mods/ShiftClick/npcinfo.htm");
			html.replace("%id%", getTemplate().getNpcId());
			html.replace("%lvl%", getTemplate().getLevel());
			html.replace("%name%", getTemplate().getName());
			html.replace("%loc%", getX() + " " + getY() + " " + getZ());
			
			html.replace("%hp%", (int) targetNpc.getStatus().getHp());
			html.replace("%hpmax%", targetNpc.getStatus().getMaxHp());
			html.replace("%mp%", (int) targetNpc.getStatus().getMp());
			html.replace("%mpmax%", targetNpc.getStatus().getMaxMp());
			html.replace("%patk%", targetNpc.getStatus().getPAtk(null));
			html.replace("%matk%", targetNpc.getStatus().getMAtk(null, null));
			html.replace("%pdef%", targetNpc.getStatus().getPDef(null));
			html.replace("%mdef%", targetNpc.getStatus().getMDef(null, null));
			html.replace("%accu%", targetNpc.getStatus().getAccuracy());
			html.replace("%evas%", targetNpc.getStatus().getEvasionRate(null));
			html.replace("%crit%", targetNpc.getStatus().getCriticalHit(null, null));
			html.replace("%rspd%", (int) targetNpc.getStatus().getMoveSpeed());
			html.replace("%aspd%", targetNpc.getStatus().getPAtkSpd());
			html.replace("%cspd%", targetNpc.getStatus().getMAtkSpd());
			html.replace("%str%", targetNpc.getStatus().getSTR());
			html.replace("%dex%", targetNpc.getStatus().getDEX());
			html.replace("%con%", targetNpc.getStatus().getCON());
			html.replace("%int%", targetNpc.getStatus().getINT());
			html.replace("%wit%", targetNpc.getStatus().getWIT());
			html.replace("%men%", targetNpc.getStatus().getMEN());
			html.replace("%ele_fire%", targetNpc.getStatus().getDefenseElementValue(ElementType.FIRE));
			html.replace("%ele_water%", targetNpc.getStatus().getDefenseElementValue(ElementType.WATER));
			html.replace("%ele_wind%", targetNpc.getStatus().getDefenseElementValue(ElementType.WIND));
			html.replace("%ele_earth%", targetNpc.getStatus().getDefenseElementValue(ElementType.EARTH));
			html.replace("%ele_holy%", targetNpc.getStatus().getDefenseElementValue(ElementType.HOLY));
			html.replace("%ele_dark%", targetNpc.getStatus().getDefenseElementValue(ElementType.DARK));
			
			player.sendPacket(html);
		}
		if (player.getTarget() != this)
			player.setTarget(this);
		else
			player.sendPacket(ActionFailed.STATIC_PACKET);
	}
}