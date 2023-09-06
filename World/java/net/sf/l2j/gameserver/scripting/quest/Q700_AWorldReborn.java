package net.sf.l2j.gameserver.scripting.quest;

import java.util.logging.Logger;

import net.sf.l2j.commons.pool.ThreadPool;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.data.manager.SpawnManager;
import net.sf.l2j.gameserver.data.sql.PlayerVariables;
import net.sf.l2j.gameserver.enums.QuestStatus;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.spawn.ASpawn;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage.SMPOS;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.ShowMiniMap;
import net.sf.l2j.gameserver.scripting.Quest;
import net.sf.l2j.gameserver.scripting.QuestState;

/**
 * @author BAN {@Link} L2JDEV
 */
public class Q700_AWorldReborn extends Quest
{
	protected static final Logger _log = Logger.getLogger(Q700_AWorldReborn.class.getName());
	
	private static final String QUEST_NAME = "Q700_AWorldReborn";
	// NPC Start Quest
	private static final int INNOCENTIN = 1000;
	private static final int NPC_TALKING_DEATH_CREATURE = 1001;
	private static int _killCreature = 0;
	
	public Q700_AWorldReborn()
	{
		super(700, "A World Reborn Talking Island!");
		
	
		addQuestStart(INNOCENTIN, NPC_TALKING_DEATH_CREATURE);
		addTalkId(INNOCENTIN, NPC_TALKING_DEATH_CREATURE);
		
		// Monster List
		for (int i = 2000; i < 2003; i++)
		{
			addMyDying(i);
		}
		_killCreature = 0;
		_log.info("Special Quest A World Reborn.");
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		if (npc.getNpcId() == INNOCENTIN)
		{
			if (event.equalsIgnoreCase("1000-1.htm"))
			{
				

			}
			else if (event.equalsIgnoreCase("1000-2.htm"))
			{
				
			}
			else if (event.equalsIgnoreCase("1000-3.htm"))
			{
				player.sendPacket(new ExShowScreenMessage("Quest Started: A World Reborn", 3000, SMPOS.TOP_CENTER, true));
				st.setState(QuestStatus.STARTED);
				st.setCond(1);
				playSound(player, SOUND_ACCEPT);
				PlayerVariables.setVar(player, "Q700", 1, -1);
				final int npcId = NPC_TALKING_DEATH_CREATURE;
				
				ASpawn spawn = SpawnManager.getInstance().getSpawn(npcId);
				if (spawn != null)
				{
					ThreadPool.schedule(() -> player.getRadarList().addMarker(spawn.getSpawnLocation()), 500);
					player.sendPacket(ShowMiniMap.REGULAR_MAP);
					
				}
				else
					player.sendMessage("Warning: Can't show location of this NPC.");
				
			}
			else if (event.equalsIgnoreCase("1000-4.htm"))
			{
				st.setState(QuestStatus.CREATED);
				st.setCond(1);
				playSound(player, SOUND_FINISH);
				st.exitQuest(true);
				PlayerVariables.unsetVar(player, "Q700");
				_killCreature = 0;
			}
		}
		// Start Talking Two NPC
		if (npc.getNpcId() == NPC_TALKING_DEATH_CREATURE)
		{
			if (event.equalsIgnoreCase("1001-1.htm"))
			{
				
			}
			else if (event.equalsIgnoreCase("1001-2.htm"))
			{
				
			}
			else if (event.equalsIgnoreCase("1001-3.htm"))
			{
				if (st.getCond() == 0)
				htmltext = NPC_TALKING_DEATH_CREATURE + "-6.htm";
				
				st.setState(QuestStatus.STARTED);
				st.setCond(2);
				playSound(player, SOUND_ACCEPT);
				
				final int npcId = Rnd.get(2000, 2002);
				player.sendPacket(new ExShowScreenMessage("Quest Updated: A World Reborn", 3000, SMPOS.TOP_CENTER, true));
				PlayerVariables.setVar(player, "Q700", 1, -1);
				ASpawn spawn = SpawnManager.getInstance().getSpawn(npcId);
				if (spawn != null)
				{
					ThreadPool.schedule(() -> player.getRadarList().addMarker(spawn.getSpawnLocation()), 500);
					player.sendPacket(ShowMiniMap.REGULAR_MAP);
					
				}
				else
					player.sendMessage("Warning: Can't show location of this NPC.");
				
			}
			else if (event.equalsIgnoreCase("1001-4.htm"))
			{
				st.setState(QuestStatus.CREATED);
				st.setCond(1);
				playSound(player, SOUND_FINISH);
				st.exitQuest(true);
				PlayerVariables.unsetVar(player, "Q700");
				_killCreature = 0;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		String htmltext = getNoQuestMsg();
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case CREATED:
				if (npc.getNpcId() == INNOCENTIN)
				{
					htmltext = INNOCENTIN + ".htm";
				}
				if (npc.getNpcId() == NPC_TALKING_DEATH_CREATURE)
				{
					htmltext = NPC_TALKING_DEATH_CREATURE + ".htm";
				}
				break;
			
			case STARTED:
				int cond = st.getCond();
				
				if (npc.getNpcId() == INNOCENTIN)
				{
					if (cond == 1)
					htmltext = INNOCENTIN + "-5.htm";
				}
				if (npc.getNpcId() == NPC_TALKING_DEATH_CREATURE)
				{
					if (cond == 0)
						htmltext = NPC_TALKING_DEATH_CREATURE + "-6.htm";
					if (cond == 1)
						htmltext = NPC_TALKING_DEATH_CREATURE + ".htm";
					if (cond == 2)
						htmltext = NPC_TALKING_DEATH_CREATURE + "-5.htm";
					
					htmltext = NPC_TALKING_DEATH_CREATURE + "-6.htm";
					
				}
				break;
			case COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		Player player = killer.getActingPlayer();
		
		final QuestState st = checkPlayerState(player, npc, QuestStatus.STARTED);
		if (st == null)
			return;
		
		_killCreature++;
		PlayerVariables.setVar(player, "Q700", _killCreature, -1);
		if (PlayerVariables.getVarB(player, "Q700"))
		{

			if (PlayerVariables.getVarInt(player, "Q700") == 5) // retail 2000 Monster
			{
				player.sendPacket(new ExShowScreenMessage("Quest Complete: A World Reborn", 6000, SMPOS.TOP_CENTER, true));
				MagicSkillUse mgc = new MagicSkillUse(player, player, 2024, 1, 5, 0);
				player.sendPacket(mgc);
				player.broadcastPacket(mgc);
				playSound(player, SOUND_FINISH);
				
				_killCreature = 0;
				PlayerVariables.unsetVar(player, "Q700");
				
				rewardItems(player, 57, 5000);
				rewardExpAndSp(player, 5000, 0);
				st.setState(QuestStatus.COMPLETED);
				st.setCond(3);
				st.exitQuest(false);
			}
		}
		
	}
	
	public static void EnterWorld(Player player)
	{
		player.sendPacket(new ExShowScreenMessage("Quest Started: A World Reborn", 6500, SMPOS.TOP_CENTER, true));
		ASpawn spawn = SpawnManager.getInstance().getSpawn(NPC_TALKING_DEATH_CREATURE);
		ThreadPool.schedule(() -> player.getRadarList().addMarker(spawn.getSpawnLocation()), 500);
		player.sendPacket(ShowMiniMap.REGULAR_MAP);
	}
}
