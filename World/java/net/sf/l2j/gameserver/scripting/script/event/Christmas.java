package net.sf.l2j.gameserver.scripting.script.event;

import java.util.logging.Logger;

import net.sf.l2j.commons.pool.ThreadPool;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.data.xml.NpcData;
import net.sf.l2j.gameserver.enums.EventHandler;
import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.model.actor.Attackable;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.Monster;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.holder.IntIntHolder;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.scripting.ScheduledQuest;
import net.sf.l2j.gameserver.skills.L2Skill;

public class Christmas extends ScheduledQuest
{
	protected static final Logger _log = Logger.getLogger(Christmas.class.getName());
	
	private final int SANTA_TRAINEE_1 = 31863;
	private final int SANTA_TRAINEE_2 = 31864;
	
	private final int STAR = EventDropConfig.STAR_CHANCEID;
	private final int BEAD = EventDropConfig.BEAD_CHANCEID;
	private final int FIR = EventDropConfig.FIR_CHANCEID;
	private final int FLOWER = EventDropConfig.FLOWER_CHANCEID;
	
	private final int STAR_CHANCE = EventDropConfig.STAR_CHANCE;
	private final int BEAD_CHANCE = EventDropConfig.BEAD_CHANCE;
	private final int FIR_CHANCE = EventDropConfig.FIR_CHANCE;
	private final int FLOWER_CHANCE = EventDropConfig.FLOWER_CHANCE;
	
	private final int STAR_COUNT = EventDropConfig.STAR_COUNT;
	private final int BEAD_COUNT = EventDropConfig.BEAD_COUNT;
	private final int FIR_COUNT = EventDropConfig.FIR_COUNT;
	private final int FLOWER_COUNT = EventDropConfig.FLOWER_COUNT;
	
	public Christmas()
	{
		super(-1, "event");
		
		addFirstTalkId(SANTA_TRAINEE_1, SANTA_TRAINEE_2);
		addQuestStart(SANTA_TRAINEE_1, SANTA_TRAINEE_2);
		addTalkId(SANTA_TRAINEE_1, SANTA_TRAINEE_2);
		addCreated(13007);
		onStart();
		_log.info("Christmas Event Start.");
		
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		addTask(npc);
		super.onCreated(npc);
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return npc.getNpcId() + ".htm";
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		
		if (event.equalsIgnoreCase("0"))
		{
			if (player.getInventory().getItemCount(5556) >= 4 && player.getInventory().getItemCount(5557) >= 4 && player.getInventory().getItemCount(5558) >= 10 && player.getInventory().getItemCount(5559) >= 1)
			{
				takeItems(player, 5556, 4);
				takeItems(player, 5557, 4);
				takeItems(player, 5558, 10);
				takeItems(player, 5559, 1);
				giveItems(player, 5560, 1);
				return null;
			}
			return "no.htm";
		}
		else if (event.equalsIgnoreCase("1"))
		{
			if (player.getInventory().getItemCount(5560) >= 10)
			{
				takeItems(player, 5560, 10);
				giveItems(player, 5561, 1);
				return null;
			}
			return "no.htm";
		}
		else if (event.equalsIgnoreCase("2"))
		{
			if (player.getInventory().getItemCount(5561) >= 10)
			{
				takeItems(player, 5560, 10);
				giveItems(player, 7836, 1);
				return null;
			}
			player.sendMessage("Need 10 Christmas Tree Need 10 small Christmas trees.");
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NOT_ENOUGH_REQUIRED_ITEMS));
			return null;
		}
		else if (event.equalsIgnoreCase("3"))
		{
			if (player.getInventory().getItemCount(5561) >= 20)
			{
				takeItems(player, 5560, 10);
				giveItems(player, 8936, 1);
				return null;
			}
			player.sendMessage("Need 20 Special Christmas Tree Need 10 big trees.");
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NOT_ENOUGH_REQUIRED_ITEMS));
			return null;
		}
		
		return htmltext;
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		Player player = killer.getActingPlayer();

		if (npc instanceof Monster)
		{
			final Monster mob = (Monster) npc;
			if (mob.isRaidBoss())
				return;
			
			if (player.getStatus().getLevel() - mob.getStatus().getLevel() > 8)
				return;
			
			if (Rnd.get(100) < STAR_CHANCE)
				mob.dropOrAutoLootItem(player, new IntIntHolder(STAR, STAR_COUNT));
			
			if (Rnd.get(100) < BEAD_CHANCE)
				mob.dropOrAutoLootItem(player, new IntIntHolder(BEAD, BEAD_COUNT));
			
			if (Rnd.get(100) < FIR_CHANCE)
				mob.dropOrAutoLootItem(player, new IntIntHolder(FIR, FIR_COUNT));
			
			if (Rnd.get(100) < FLOWER_CHANCE)
				mob.dropOrAutoLootItem(player, new IntIntHolder(FLOWER, FLOWER_COUNT));
		}
		return;
	}
	
	private void addTask(Npc npc)
	{
		final L2Skill skill = SkillTable.getInstance().getInfo(2139, 1);
		ThreadPool.schedule(new ChristmasTreeAI(npc, skill), 1000);
	}
	
	protected class ChristmasTreeAI implements Runnable
	{
		private final Npc _npc;
		private final L2Skill _skill;
		
		protected ChristmasTreeAI(Npc npc, L2Skill skill)
		{
			_npc = npc;
			_skill = skill;
		}
		
		@Override
		public void run()
		{
			if (!_npc.isInsideZone(ZoneId.PEACE))
			{
				if (_npc.getSummon() == null)
				{
					ThreadPool.schedule(this, 1000);
					return;
				}
				
				final Player player = _npc.getSummon().getActingPlayer();
				if (!player.isInParty())
				{
					if (player.isIn3DRadius(_npc, 1000))
						_skill.getEffects(_npc, player);
				}
				else
				{
					for (Player member : player.getParty().getMembers())
					{
						if (member != null && member.isIn3DRadius(_npc, 1000))
							_skill.getEffects(_npc, member);
					}
				}
			}
			ThreadPool.schedule(this, 1000);
		}
	}
	
	@Override
	protected void onStart()
	{
		for (final NpcTemplate template : NpcData.getInstance().getAllNpcs())
		{
			if (!template.isType("Monster"))
				continue;	
			try
			{
				if (Attackable.class.isAssignableFrom(Class.forName("net.sf.l2j.gameserver.model.actor.instance." + template.getType())))
					addEventId(template.getNpcId(), EventHandler.MY_DYING);
			}
			catch (ClassNotFoundException e)
			{
				LOGGER.error("An unknown template type {} has been found on {}.", e, template.getType(), toString());
			}
		}
	}
	
	@Override
	protected void onEnd()
	{
	}
}