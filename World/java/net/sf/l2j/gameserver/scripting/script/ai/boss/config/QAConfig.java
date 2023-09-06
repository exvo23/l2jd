package net.sf.l2j.gameserver.scripting.script.ai.boss.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.sf.l2j.commons.config.ExProperties;

/**
 * @author BAN-L2JDEV
 */
public class QAConfig
{
	protected static final Logger _log = Logger.getLogger(QAConfig.class.getName());
	
	public static final String QA_FILE = "./config/boss/QueenAnt.ini";
	public static String FWA_FIXTIMEPATTERNOFQA;
	
	public static void ini()
	{
		final ExProperties QA = load(QA_FILE);
		
		FWA_FIXTIMEPATTERNOFQA = QA.getProperty("QueenAntRespawnTimePattern", "");
		
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
