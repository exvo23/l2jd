package net.sf.l2j.gameserver.scripting.script.ai.boss.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.sf.l2j.commons.config.ExProperties;

/**
 * @author BAN-L2JDEV
 */
public class DrChaosConfig
{
	protected static final Logger _log = Logger.getLogger(BenomConfig.class.getName());
	
	public static final String DR_CHAOS_FILE = "./config/boss/DrChaos.ini";
	public static String FWA_FIXTIMEPATTERNOFCHAOS;
	
	public static void init()
	{
		ExProperties chaos = load(DR_CHAOS_FILE);
		
		FWA_FIXTIMEPATTERNOFCHAOS = chaos.getProperty("ChaosRespawnTimePattern", "");
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
