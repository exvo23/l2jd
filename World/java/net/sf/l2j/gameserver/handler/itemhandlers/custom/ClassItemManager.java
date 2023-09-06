package net.sf.l2j.gameserver.handler.itemhandlers.custom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.TimeUnit;

import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author BAN {@Link} WWW.L2JDEV.COM.BR
 *
 */
public class ClassItemManager
{
	public static void ClassChangeCoin(Player player, String command)
	{
		String classes = player.getTemplate().getClassName();
		
		String type = command;
		switch (type)
		{
			
			case "---SELECT---":
			{
				NpcHtmlMessage html = new NpcHtmlMessage(0);
				html.setFile("data/html/mods/ItemClassChanger/Class.htm");
				player.sendPacket(html);
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			case "Duelist":
				if (player.getClassId().getId() == 88)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 88);
				break;
			case "Dreadnought":
				if (player.getClassId().getId() == 89)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 89);
				break;
			case "Phoenix_Knight":
				if (player.getClassId().getId() == 90)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 90);
				break;
			case "Hell_Knight":
				if (player.getClassId().getId() == 91)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 91);
				break;
			case "Saggitarius":
				if (player.getClassId().getId() == 92)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 92);
				break;
			case "Adventure":
				if (player.getClassId().getId() == 93)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 93);
				break;
			case "Archmage":
				if (player.getClassId().getId() == 94)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 94);
				break;
			case "Soultaker":
				if (player.getClassId().getId() == 95)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 95);
				break;
			case "Arcana_Lord":
				if (player.getClassId().getId() == 96)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 96);
				break;
			case "Cardial":
				if (player.getClassId().getId() == 97)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 97);
				break;
			case "Hierophant":
				if (player.getClassId().getId() == 98)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 98);
				break;
			case "Evas_Templar":
				if (player.getClassId().getId() == 99)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 99);
				break;
			case "Sword_Muse":
				if (player.getClassId().getId() == 100)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 100);
				break;
			case "Wind_Rider":
				if (player.getClassId().getId() == 101)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 101);
				break;
			case "Moonlight":
				if (player.getClassId().getId() == 102)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 102);
				break;
			case "Mystic_Muse":
				if (player.getClassId().getId() == 103)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 103);
				break;
			case "Elemental_Master":
				if (player.getClassId().getId() == 104)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 104);
				break;
			case "Evas_Saint":
				if (player.getClassId().getId() == 105)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 105);
				break;
			case "Shillie_Templar":
				if (player.getClassId().getId() == 106)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 106);
				break;
			case "Spectral_Dancer":
				if (player.getClassId().getId() == 107)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 107);
				break;
			case "Ghost_Hunter":
				if (player.getClassId().getId() == 108)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 108);
				break;
			case "Ghost_Sentinel":
				if (player.getClassId().getId() == 109)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 109);
				break;
			case "Storm_Screamer":
				if (player.getClassId().getId() == 110)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 110);
				break;
			case "Spectral_Master":
				if (player.getClassId().getId() == 111)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 111);
				break;
			case "Shillien_Saint":
				if (player.getClassId().getId() == 112)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 112);
				break;
			case "Titan":
				if (player.getClassId().getId() == 113)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 113);
				break;
			case "Grand_Khavatari":
				if (player.getClassId().getId() == 114)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 114);
				break;
			case "Dominator":
				if (player.getClassId().getId() == 115)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 115);
				break;
			case "Doomcryer":
				if (player.getClassId().getId() == 116)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 116);
				break;
			case "Fortune_Seeker":
				if (player.getClassId().getId() == 117)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 117);
				break;
			case "Maestro":
				if (player.getClassId().getId() == 118)
				{
					player.sendMessage(player.getName() + " your class is already " + classes + " choose another.");
					return;
				}
				
				getClassId(player, 118);
				break;
				
		}
		player.destroyItemByItemId("skin", Config.ITEM_CLASS_ID, 1, player.getTarget(), true);
		
	}
	
	public static void getClassId(Player player, int classId)
	{

		
		if (!player.isSubClassActive())
			player.setBaseClass(classId);
		
		player.setClassId(classId);
		player.store();
		player.broadcastUserInfo();
		player.sendSkillList();
		player.getAvailableSkills();
		player.disarmWeapon(true);
		player.disarmArmor(true);
		player.stopAllEffectsExceptThoseThatLastThroughDeath();
		player.sendPacket(new ExShowScreenMessage(player.getName() + " your new class is " + player.getTemplate().getClassName() + ".", (int) TimeUnit.SECONDS.toMillis(20)));
		
		try (Connection con = ConnectionPool.getConnection())
		{
			// Remove all henna info stored for this sub-class.
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_hennas WHERE char_obj_id=? AND class_index=?"))
			{
				ps.setInt(1, player.getObjectId());
				ps.setInt(2, 0);
				ps.execute();
			}
			
			// Remove all shortcuts info stored for this sub-class.
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_shortcuts WHERE char_obj_id=? AND class_index=?"))
			{
				ps.setInt(1, player.getObjectId());
				ps.setInt(2, 0);
				ps.execute();
			}
			
			// Remove all effects info stored for this sub-class.
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_skills_save WHERE char_obj_id=? AND class_index=?"))
			{
				ps.setInt(1, player.getObjectId());
				ps.setInt(2, 0);
				ps.execute();
			}
			
			// Remove all skill info stored for this sub-class.
			try (PreparedStatement ps = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id=? AND class_index=?"))
			{
				ps.setInt(1, player.getObjectId());
				ps.setInt(2, 0);
				ps.execute();
			}
			
			// remove hero
			try (PreparedStatement statement = con.prepareStatement("DELETE FROM heroes WHERE char_id=?"))
			{
				statement.setInt(1, player.getObjectId());
				statement.execute();
			}
		}
		catch (Exception e)
		{
			
		}

		ThreadPool.schedule(() -> player.logout(false), 1000);
	}
}
