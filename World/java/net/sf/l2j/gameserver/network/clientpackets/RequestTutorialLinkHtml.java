package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.ai.Bot;
import net.sf.l2j.gameserver.model.actor.instance.ClassMaster;
import net.sf.l2j.gameserver.network.serverpackets.TutorialCloseHtml;
import net.sf.l2j.gameserver.scripting.specialtutorial.SpecialTutorialArmor;
import net.sf.l2j.gameserver.scripting.specialtutorial.SpecialTutorialAutoFarm;
import net.sf.l2j.gameserver.scripting.specialtutorial.SpecialTutorialGremlin;
import net.sf.l2j.gameserver.scripting.specialtutorial.SpecialTutorialTeleport;
import net.sf.l2j.gameserver.scripting.specialtutorial.SpecialTutorialWeapon;

public class RequestTutorialLinkHtml extends L2GameClientPacket
{
	private String _bypass;
	
	@Override
	protected void readImpl()
	{
		_bypass = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Bot bot = player.getBot();
		
//		final QuestState qs = player.getQuestList().getQuestState("Tutorial");
//		if (qs != null)
//			qs.getQuest().notifyEvent(_bypass, null, player);
		
		if (_bypass.equalsIgnoreCase("close"))
		{
			player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
			return;
		}
		
		ClassMaster.onTutorialLink(player, _bypass);
		
		if (_bypass.equals("farm_on"))
		{
			
			bot.start();
			showQuestionHtml(player);
		}
		
		else if (_bypass.equals("farm_off"))
		{
			bot.stop();
			showQuestionHtml(player);
		}
		
		else if (_bypass.equals("inc_lowlife"))
		{
			
			bot.setLowLifePercentage(bot.getLowLifePercentage() + 5);
			showQuestionHtml(player);
		}
		
		else if (_bypass.equals("dec_lowlife"))
		{
			showQuestionHtml(player);
			bot.setLowLifePercentage(bot.getLowLifePercentage() - 5);
		}
		
		else if (_bypass.equals("inc_chance"))
		{
			showQuestionHtml(player);
			bot.setChancePercentage(bot.getChancePercentage() + 5);
		}
		
		else if (_bypass.equals("dec_chance"))
		{
			showQuestionHtml(player);
			bot.setChancePercentage(bot.getChancePercentage() - 5);
		}
		
		else if (_bypass.equals("inc_hp_pot"))
		{
			showQuestionHtml(player);
			bot.setHpPotionPercentage(bot.getHpPotionPercentage() + 5);
		}
		
		else if (_bypass.equals("dec_hp_pot"))
		{
			showQuestionHtml(player);
			bot.setHpPotionPercentage(bot.getHpPotionPercentage() - 5);
		}
		
		else if (_bypass.equals("inc_mp_pot"))
		{
			showQuestionHtml(player);
			bot.setMpPotionPercentage(bot.getMpPotionPercentage() + 5);
		}
		
		else if (_bypass.equals("dec_mp_pot"))
		{
			showQuestionHtml(player);
			bot.setMpPotionPercentage(bot.getMpPotionPercentage() - 5);
		}
		
		else if (_bypass.equals("inc_radius"))
		{
			showQuestionHtml(player);
			bot.setRadius(bot.getRadius() + 100);
		}
		
		else if (_bypass.equals("dec_radius"))
		{
			showQuestionHtml(player);
			bot.setRadius(bot.getRadius() - 100);
		}
		
		else if (_bypass.equals("buff_protect"))
		{
			showQuestionHtml(player);
			bot.setNoBuffProtection(!bot.isNoBuffProtected());
			
		}
		
		SpecialTutorialGremlin.onTutorialBufferLink(player, _bypass);
		
		SpecialTutorialArmor.onTutorialArmorSetHeavy(player, _bypass);
		
		SpecialTutorialArmor.onTutorialArmorSetRobe(player, _bypass);
		
		SpecialTutorialArmor.onTutorialArmorSetLight(player, _bypass);
		
		SpecialTutorialWeapon.onTutorialWeaponBow(player, _bypass);
		
		SpecialTutorialWeapon.onTutorialWeaponSword(player, _bypass);
		SpecialTutorialWeapon.onTutorialWeaponDagger(player, _bypass);
		SpecialTutorialWeapon.onTutorialWeaponStaff(player, _bypass);
		
		SpecialTutorialTeleport.onTutoriaTeleportRa(player, _bypass);
		
		SpecialTutorialTeleport.onTutoriaTeleportCT(player, _bypass);
		SpecialTutorialTeleport.onTutoriaTeleportAL(player, _bypass);
		SpecialTutorialTeleport.onTutoriaTeleportHS(player, _bypass);

	}
	
	public static void showQuestionHtml(Player player)
	{
		
		SpecialTutorialAutoFarm.showTutorialHtml(player);
	}
}