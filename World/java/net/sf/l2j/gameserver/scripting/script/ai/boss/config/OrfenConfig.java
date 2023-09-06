package net.sf.l2j.gameserver.scripting.script.ai.boss.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.sf.l2j.commons.config.ExProperties;

/**
 * @author BAN-L2JDEV
 */
public class OrfenConfig
{
	protected static final Logger _log = Logger.getLogger(LindviorConfig.class.getName());
	
	public static final String ORFEN_FILE = "./config/boss/Orfen.ini";
	
	public static String FWA_FIXTIMEPATTERNOFORFEN;
	
	public static void ini()
	{
		final ExProperties orfen = load(ORFEN_FILE);
		
		FWA_FIXTIMEPATTERNOFORFEN = orfen.getProperty("OrfenRespawnTimePattern", "");
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
