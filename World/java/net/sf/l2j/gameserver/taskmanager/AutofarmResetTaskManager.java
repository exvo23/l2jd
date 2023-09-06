package net.sf.l2j.gameserver.taskmanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import net.sf.l2j.commons.logging.CLogger;
import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;

/**
 * @author BAN - L2JDEV
 *
 */
public final class AutofarmResetTaskManager implements Runnable
{	
	private static final String RESET_QUERY = "UPDATE character_memo SET val=? WHERE var=?";
	private final CLogger LOGGER = new CLogger(AutofarmResetTaskManager.class.getName());

	private AutofarmResetTaskManager()
	{
		ThreadPool.scheduleAtFixedRate(this, calculateNextReset(), TimeUnit.DAYS.toMillis(1));
	}
	
	private static long calculateNextReset()
	{
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		while (calendar.getTimeInMillis() <= System.currentTimeMillis())
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		
		return calendar.getTimeInMillis() - System.currentTimeMillis();
	}
	
	@Override
	public final void run()
	{
		for (final Player player : World.getInstance().getPlayers())
			player.getBot().resetTime();
		
		try (Connection con = ConnectionPool.getConnection();
				PreparedStatement ps = con.prepareStatement(RESET_QUERY))
		{			
			ps.setInt(1, 0);
			ps.setString(2, "usingBot");
			ps.execute();
		}
		catch (final Exception e)
		{
			LOGGER.error("Couldn't reset autofarm timers.", e);
		}
	}
	
	public static final AutofarmResetTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final AutofarmResetTaskManager INSTANCE = new AutofarmResetTaskManager();
	}
}
