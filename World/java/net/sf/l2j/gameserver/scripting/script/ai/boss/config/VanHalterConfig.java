package net.sf.l2j.gameserver.scripting.script.ai.boss.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.sf.l2j.commons.config.ExProperties;

/**
 * @author BAN-L2JDEV
 */

public class VanHalterConfig
{
	protected static final Logger _log = Logger.getLogger(VanHalterConfig.class.getName());
	
	public static final String VANHALTER_FILE = "./config/boss/VanHalter.ini";
	
	// CreaturesMonster
	public static List<Integer> HING_PRIESTLIST_MONSTERS;
	public static String HING_PRIEST_CREATURE;
	public static int CREATURE_HINGPRINT_CONT;
	
	public static List<Integer> GUARD_ROYAL_LIST_MONSTERS;
	public static String GUARD_ROYAL_CREATURE;
	public static int GUARD_ROYAL_CONT;
	
	public static int HingPriestDropItemId;
	public static int HingPriestChanceDrop;
	public static int HingPriestAmountDrop;
	
	// Time Van Halter
	public static String FWA_FIXTIMEPATTERNOFVANHALTER;

	public static void init()
	{
		ExProperties Vanhalter = load(VANHALTER_FILE);
		
		// First Room 
		HING_PRIEST_CREATURE = Vanhalter.getProperty("HingPriestListMonsterId");
		HING_PRIESTLIST_MONSTERS = new ArrayList<>();
		for (String id : HING_PRIEST_CREATURE.split(","))
			HING_PRIESTLIST_MONSTERS.add(Integer.parseInt(id));
		
		CREATURE_HINGPRINT_CONT = Vanhalter.getProperty("HingPriestContDeathOpenDoor", 25);
		
		HingPriestDropItemId = Vanhalter.getProperty("HingPriestDropItemId", 8275);
		HingPriestChanceDrop = Vanhalter.getProperty("HingPriestChanceDrop", 33);
		HingPriestAmountDrop = Vanhalter.getProperty("HingPriestAmountDrop", 1);
		
		// Two Room
		GUARD_ROYAL_CREATURE = Vanhalter.getProperty("GuardRoyalListMonsterId");
		GUARD_ROYAL_LIST_MONSTERS = new ArrayList<>();
		for (String id : GUARD_ROYAL_CREATURE.split(","))
			GUARD_ROYAL_LIST_MONSTERS.add(Integer.parseInt(id));
		
		GUARD_ROYAL_CONT = Vanhalter.getProperty("GuardRoyalContDeathOpenDoor", 25);
		
		
		
		FWA_FIXTIMEPATTERNOFVANHALTER = Vanhalter.getProperty("VanHalterRespawnTimePattern", "");
		
	}
	
	public static ExProperties load(String filename)
	{
		return load(new File(filename));
	}
	
	public static ExProperties load(File file)
	{
		ExProperties result = new ExProperties();
		
		try
		{
			result.load(file);
		}
		catch (IOException e)
		{
			_log.warning("Error loading config : " + file.getName() + "!");
		}
		
		return result;
	}
}
