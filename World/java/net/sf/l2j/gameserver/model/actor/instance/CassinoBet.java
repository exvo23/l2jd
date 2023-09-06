package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.enums.SayType;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author BAN - L2JDEV
 */
public class CassinoBet extends Npc
{
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (command.startsWith("bet_50kk"))
		{
			if (player.getInventory().getItemCount(ADENA, 0) >= 50000000)
			{
				player.destroyItemByItemId("Consume", ADENA, 50000000, player, true);
				Bet50kk(player, this);
			}
			else
			{
				player.sendMessage("You do not have enough adena's.");
				return;
			}
		}
		else if (command.startsWith("bet_100kk"))
		{
			if (player.getInventory().getItemCount(ADENA, 0) >= 100000000)
			{
				player.destroyItemByItemId("Consume", ADENA, 100000000, player, true);
				Bet100kk(player, this);
			}
			else
			{
				player.sendMessage("You do not have enough adena's.");
				return;
			}
		}
		else if (command.startsWith("bet_500kk"))
		{
			if (player.getInventory().getItemCount(ADENA, 0) >= 500000000)
			{
				player.destroyItemByItemId("Consume", ADENA, 500000000, player, true);
				Bet500kk(player, this);
			}
			else
			{
				player.sendMessage("You do not have enough adena's.");
				return;
			}
		}
		else if (command.startsWith("bet_1tkt"))
		{
			if (player.getInventory().getItemCount(TICKET, 0) >= 1)
			{
				player.destroyItemByItemId("Consume", TICKET, 1, player, true);
				Bet1tkt(player, this);
			}
			else
			{
				player.sendMessage("You do not have enough ticket's.");
				return;
			}
		}
		else if (command.startsWith("bet_3tkt"))
		{
			if (player.getInventory().getItemCount(TICKET, 0) >= 3)
			{
				player.destroyItemByItemId("Consume", TICKET, 3, player, true);
				Bet3tkt(player, this);
			}
			else
			{
				player.sendMessage("You do not have enough ticket's.");
				return;
			}
		}
		else if (command.startsWith("bet_5tkt"))
		{
			if (player.getInventory().getItemCount(TICKET, 0) >= 5)
			{
				player.destroyItemByItemId("Consume", TICKET, 5, player, true);
				Bet5tkt(player, this);
			}
			else
			{
				player.sendMessage("You do not have enough ticket's.");
				return;
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
	
	private static final int ADENA = 57;
	private static final int TICKET = 9315;
	
	private static final int FRAGMENT_GOLD = 9315;
	
	private static final int[] BLESSED_ENCHANT =
	{
		6571,
		6569,
		6577,
		6578,
		6570,
		6578
	};
	
	private static final String[] BOX_MSG =
	{
		"I'm sorry $s1... hehehe!",
		"Fail... huehue!",
		"Better luck next time, you fool hihihi.",
		"I never get tired of doing this!",
		"Did you hear that sound? The sound of your defeat hAhAhA!"
	};
	
	public CassinoBet(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	private static final String ACTIVED = "<font color=FFFF00>ON</font>";
	private static final String DESATIVED = "<font color=FF0000>OFF</font>";
	
	@Override
	public void showChatWindow(Player player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile("data/html/mods/cassino/Main.htm");
		html.replace("%objectId%", String.valueOf(player.getTargetId()));
		html.replace("%count%", player.getBuffCount() + " / " + player.getMaxBuffCount());
		html.replace("%AugmentsRate%", "" + Config.AUGMENTATION_BASESTAT_CHANCE + "%");
		html.replace("%ServerName%", Config.SERVER_NAME);

		html.replace("%bloking%", player.isDisableHair() ? DESATIVED : ACTIVED);
		html.replace("%dresmedisable%", player.isSkinDisable() ? ACTIVED : DESATIVED);
		html.replace("%online%", World.getInstance().getPlayers().size());
		player.sendPacket(html);
	}
	
	// Lv.1 Reward's
	public void Bet50kk(Player activeChar, Npc npc)
	{
		switch (Rnd.get(23))
		{
			case 0:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 1:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 2:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 3:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 4:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 5:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", "Nice, You WON it..."));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 2024, 1, 1, 0));
				activeChar.addItem("Adena", ADENA, 60000000, activeChar, true);
				break;
			}
			case 6:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 7:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 8:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 9:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 10:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 11:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", "Nice, You WON it..."));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 2024, 1, 1, 0));
				activeChar.addItem("God", FRAGMENT_GOLD, 1, activeChar, true);
				break;
			}
			case 12:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 13:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 14:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 15:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 16:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 17:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", "Nice, You WON it..."));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 2024, 1, 1, 0));
				activeChar.addItem("Bless Enchant", BLESSED_ENCHANT[Rnd.get(6)], 1, activeChar, true);
				break;
			}
			case 18:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 19:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 20:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 21:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 22:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
		}
	}
	
	// Lv.2 Reward's
	public void Bet100kk(Player activeChar, Npc npc)
	{
		switch (Rnd.get(23))
		{
			case 0:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 1:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 2:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 3:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 4:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 5:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", "Nice, You WON it..."));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 2024, 1, 1, 0));
				activeChar.addItem("Adena", ADENA, 110000000, activeChar, true);
				break;
			}
			case 6:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 7:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 8:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 9:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 10:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 11:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", "Nice, You WON it..."));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 2024, 1, 1, 0));
				activeChar.addItem("God", FRAGMENT_GOLD, 2, activeChar, true);
				break;
			}
			case 12:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 13:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 14:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 15:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 16:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 17:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", "Nice, You WON it..."));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 2024, 1, 1, 0));
				activeChar.addItem("Bless Enchant", BLESSED_ENCHANT[Rnd.get(6)], 1, activeChar, true);
				break;
			}
			case 18:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 19:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 20:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 21:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 22:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
		}
	}
	
	// Lv.3 Reward's
	public void Bet500kk(Player activeChar, Npc npc)
	{
		switch (Rnd.get(23))
		{
			case 0:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 1:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 2:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 3:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 4:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 5:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", "Nice, You WON it..."));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 2024, 1, 1, 0));
				activeChar.addItem("Adena", ADENA, 520000000, activeChar, true);
				break;
			}
			case 6:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 7:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 8:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 9:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 10:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 11:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", "Nice, You WON it..."));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 2024, 1, 1, 0));
				activeChar.addItem("God", FRAGMENT_GOLD, 3, activeChar, true);
				break;
			}
			case 12:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 13:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 14:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 15:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 16:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 17:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", "Nice, You WON it..."));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 2024, 1, 1, 0));
				activeChar.addItem("Bless Enchant", BLESSED_ENCHANT[Rnd.get(6)], 1, activeChar, true);
				break;
			}
			case 18:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 19:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 20:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 21:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 22:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
		}
	}
	
	// Lv.1 Reward's
	public void Bet1tkt(Player activeChar, Npc npc)
	{
		switch (Rnd.get(23))
		{
			case 0:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 1:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 2:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 3:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 4:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 5:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", "Nice, You WON it..."));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 2024, 1, 1, 0));
				activeChar.addItem("Adena", ADENA, 60000000, activeChar, true);
				break;
			}
			case 6:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 7:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 8:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 9:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 10:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 11:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", "Nice, You WON it..."));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 2024, 1, 1, 0));
				activeChar.addItem("Blood Coin", FRAGMENT_GOLD, 5, activeChar, true);
				break;
			}
			case 12:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 13:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 14:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 15:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 16:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 17:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", "Nice, You WON it..."));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 2024, 1, 1, 0));
				activeChar.addItem("Bless Enchant", BLESSED_ENCHANT[Rnd.get(6)], 1, activeChar, true);
				break;
			}
			case 18:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 19:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 20:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 21:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 22:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
		}
	}
	
	// Lv.2 Reward's
	public void Bet3tkt(Player activeChar, Npc npc)
	{
		switch (Rnd.get(23))
		{
			case 0:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 1:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 2:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 3:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 4:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 5:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", "Nice, You WON it..."));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 2024, 1, 1, 0));
				activeChar.addItem("Adena", ADENA, 110000000, activeChar, true);
				break;
			}
			case 6:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 7:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 8:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 9:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 10:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 11:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", "Nice, You WON it..."));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 2024, 1, 1, 0));
				activeChar.addItem("God", FRAGMENT_GOLD, 10, activeChar, true);
				break;
			}
			case 12:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 13:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 14:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 15:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 16:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 17:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", "Nice, You WON it..."));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 2024, 1, 1, 0));
				activeChar.addItem("Bless Enchant", BLESSED_ENCHANT[Rnd.get(6)], 1, activeChar, true);
				break;
			}
			case 18:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 19:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 20:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 21:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 22:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
		}
	}
	
	// Lv.3 Reward's
	public void Bet5tkt(Player activeChar, Npc npc)
	{
		switch (Rnd.get(23))
		{
			case 0:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 1:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 2:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 3:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 4:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 5:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", "Nice, You WON it..."));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 2024, 1, 1, 0));
				activeChar.addItem("Adena", ADENA, 520000000, activeChar, true);
				break;
			}
			case 6:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 7:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 8:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 9:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 10:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 11:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", "Nice, You WON it..."));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 2024, 1, 1, 0));
				activeChar.addItem("God", FRAGMENT_GOLD, 15, activeChar, true);
				break;
			}
			case 12:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 13:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 14:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 15:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 16:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 17:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", "Nice, You WON it..."));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 2024, 1, 1, 0));
				activeChar.addItem("Bless Enchant", BLESSED_ENCHANT[Rnd.get(6)], 1, activeChar, true);
				break;
			}
			case 18:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 19:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 20:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 21:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
			case 22:
			{
				activeChar.sendPacket(new CreatureSay(npc.getObjectId(), SayType.ALL, "Cassino", BOX_MSG[Rnd.get(5)].replace("$s1", activeChar.getName())));
				npc.broadcastPacket(new MagicSkillUse(this, npc, 347, 1, 1, 0));
				break;
			}
		}
	}
}
