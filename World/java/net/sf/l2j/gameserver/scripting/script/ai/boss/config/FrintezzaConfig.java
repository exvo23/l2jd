package net.sf.l2j.gameserver.scripting.script.ai.boss.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.sf.l2j.commons.config.ExProperties;

/**
 * @author BAN-L2JDEV
 */
public class FrintezzaConfig
{
	protected static final Logger _log = Logger.getLogger(FrintezzaConfig.class.getName());
	public static final String FRINTEZZA_FILE = "./config/boss/Frintezza.ini";
	
	public static String FWA_FIXTIMEPATTERNOFFRINTEZZA;
	public static int PARTY_CHECK_MIN;
	public static int PARTY_CHECK_MAX;
	public static int MAX_PLAYER_QUEST_FRINTEZZA;
	public static int WAIT_TIME_FRINTEZZA;
	
	public static int FRINTEZZA_ITEM_REQUEST;
	public static int FRINTEZZA_ITEM_REQUEST_CONT;
	
	public static void init()
	{
		ExProperties frintezza = load(FRINTEZZA_FILE);
		FWA_FIXTIMEPATTERNOFFRINTEZZA = frintezza.getProperty("FrintezzaRespawnTimePattern", "");
		
		// Item Request Enter
		FRINTEZZA_ITEM_REQUEST = frintezza.getProperty("RequestEnterItemId", 10);
		FRINTEZZA_ITEM_REQUEST_CONT = frintezza.getProperty("RequestEnterItemIdCont", 10);
		
		WAIT_TIME_FRINTEZZA = frintezza.getProperty("FrintezzaWaitTime", 1) * 60000;
		PARTY_CHECK_MIN = frintezza.getProperty("CheckPartyMin", 8);
		PARTY_CHECK_MAX = frintezza.getProperty("CheckPartyMax", 8);
		MAX_PLAYER_QUEST_FRINTEZZA = frintezza.getProperty("MaxPlayerQuest", 8);
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
