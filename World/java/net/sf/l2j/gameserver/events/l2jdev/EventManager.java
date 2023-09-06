package net.sf.l2j.gameserver.events.l2jdev;

import java.util.Map;
import java.util.Random;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.gameserver.enums.MessageType;
import net.sf.l2j.gameserver.enums.SayType;
import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.enums.actors.ClassId;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.WorldObject.PolyType;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.olympiad.OlympiadManager;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ConfirmDlg;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import javolution.text.TextBuilder;
import javolution.util.FastList;
import javolution.util.FastMap;

public final class EventManager
{
	private EventConfig config;
	public FastMap<Integer, Event> events;
	public FastMap<Integer, Event> disabled;
	public FastList<Player> players;
	public FastMap<String, Integer> afkers;
	private Event current;
	protected FastMap<Player, Integer> colors;
	protected FastMap<Player, String> titles;
	protected FastMap<Player, int[]> positions;
	public FastMap<Player, Integer> votes;
	protected State status;
	protected int counter;
	public Countdown cdtask;
	protected ThreadPool tpm;
	private Scheduler task;
	protected Random rnd = new Random();
	protected FastList<Integer> eventIds;
	
	protected enum State
	{
		REGISTERING,
		VOTING,
		RUNNING,
		END
	}
	
	public class Countdown implements Runnable
	{
		public String getTime()
		{
			String mins = "" + counter / 60;
			String secs = (counter % 60 < 10 ? "0" + counter % 60 : "" + counter % 60);
			return mins + ":" + secs;
		}
		
		@Override
		public void run()
		{
			if (status == State.REGISTERING)
			{
				switch (counter)
				{
					case 300:
						announcetime("[" + getCurrentEvent().getString("eventName") + "] " + counter / 60 + " minutes remaining to join " + "[" + getCurrentEvent().getString("eventName") + "]");
						announcetime("[" + getCurrentEvent().getString("eventName") + "] " + "Change your participation status using");
						
						
						for (Player player : World.getInstance().getPlayers())
							AskJoinRegister(player);
						
					case 240:
					case 180:
					case 120:
					case 60:
						announcetime("[" + getCurrentEvent().getString("eventName") + "] " + counter / 60 + " minutes remaining to join " + "[" + getCurrentEvent().getString("eventName") + "]");
						announcetime("[" + getCurrentEvent().getString("eventName") + "] " + "Change your participation status using");
						
						for (Player player : World.getInstance().getPlayers())
							AskJoinRegister(player);
						break;
				}
			}
			
			if (status == State.VOTING && counter == getInt("showVotePopupAt") && getBoolean("votePopupEnabled"))
			{
				NpcHtmlMessage html = new NpcHtmlMessage(0);
				
				TextBuilder sb = new TextBuilder();
				@SuppressWarnings("unused")
				int count = 0;
				
				sb.append("<html><title>Event Engine - Vote for your favourite event</title><body><br1><img src=\"Icons.breaker\" width=\"260\" height=\"16\"><br1>");
				
				sb.append("<img src=L2UI.SquareWhite width=295 height=1>");
				sb.append("<center><table width=295 bgcolor=000000><tr><td width=100><font color=\"FFFF00\">Voting Phase</font></td><td width=100><center><font color=\"00FF00\">Remaining Time:</font> " + EventManager.getInstance().cdtask.getTime() + "</center></td></tr></table></center>");
				sb.append("<img src=L2UI.SquareWhite width=295 height=1><br>");
				
				for (Map.Entry<Integer, Event> event : EventManager.getInstance().events.entrySet())
				{
					count++;
					sb.append("<table align=center width=300 height=20 bgcolor=000000>");
					sb.append("<tr>");
					
					sb.append("<td align=\"left\" width=\"140\"><font color=\"FFFFFF\">" + event.getValue().getString("eventName") + "</font>" + "</td>");
					sb.append("<td align=\"center\" width=\"80\"><button value=\"\" action=\"" + "\"width=65 height=20 back=\"\" fore=\"\"></td>");
					sb.append("<td align=\"center\" width=\"80\"><button value=\"Vote\" action=\"bypass -h eventvote " + event.getKey() + "\"width=65 height=20 back=\"L2UI_ch3.smallbutton2_down\" fore=\"L2UI_ch3.smallbutton2\"></td>");
					
					sb.append("</tr>");
					sb.append("</table></center><img src=L2UI.SquareWhite width=295 height=1>");
					
				}
				
				sb.append("<br1></body></html>");
				
				html.setHtml(sb.toString());
				
				for (Player player : World.getInstance().getPlayers())
				{
					if (votes.containsKey(player) || player.getStatus().getLevel() < 40)
						continue;
					if (player.isInsideZone(ZoneId.BOSS))
						continue;
					
					player.sendPacket(html);
				}
			}
			
			if (counter == 0)
				schedule(1);
			else
			{
				counter--;
				ThreadPool.schedule(cdtask, 1000);
			}
		}
	}
	
	public void showFirstHtml(Player player, int obj)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(obj);
		TextBuilder sb = new TextBuilder();
		@SuppressWarnings("unused")
		int count = 0;
		
		sb.append("<html><title>Event Engine - Vote for your favourite event</title><body>");
		sb.append("<img src=L2UI.SquareWhite width=295 height=1>");
		sb.append("<table width=270><tr><td width=145><br1>Events Engine</td><td width=75>" + (getBoolean("eventBufferEnabled") ? "<button value=\"Buffer\" action=\"bypass -h eventbuffershow \"width=65 height=20 back=\"L2UI_ch3.smallbutton2_down\" fore=\"L2UI_ch3.smallbutton2\">" : "") + "</td><td width=50><button value=\"Statis\" action=\"bypass -h eventstats 1 \"width=65 height=20 back=\"L2UI_ch3.smallbutton2_down\" fore=\"L2UI_ch3.smallbutton2\"></td></tr></table>");
		
		sb.append("<img src=L2UI.SquareWhite width=295 height=1>");
		
		if (getStatus() == State.VOTING)
		{
			sb.append("<center><table width=256 bgcolor=000000><tr><td width=90><font color=\"FFFF00\">Voting Phase</font></td><td width=140><center><font color=\"00FF00\">Remaining Time:</font> " + cdtask.getTime() + "</center></td><td width=40><center><font color=\"FFFF00\">Votes</font></center></td></tr></table></center><br></center>");
			for (Map.Entry<Integer, Event> event : events.entrySet())
			{
				count++;
				
				sb.append("<table align=center width=300 height=20 bgcolor=000000>");
				sb.append("<tr>");
				
				sb.append("<td align=\"left\" width=\"140\"><font color=\"FFFFFF\">" + event.getValue().getString("eventName") + "</font>" + "</td>");
				sb.append("<td align=\"center\" width=\"80\"><button value=\"Info\" action=\"bypass -h eventinfo " + event.getKey() + "\"width=65 height=20 back=\"L2UI_ch3.smallbutton2_down\" fore=\"L2UI_ch3.smallbutton2\"></td>");
				sb.append("<td align=\"center\" width=\"80\"><button value=\"Vote " + getVoteCount(event.getKey()) + "\" action=\"bypass -h eventvote " + event.getKey() + "\"width=65 height=20 back=\"L2UI_ch3.smallbutton2_down\" fore=\"L2UI_ch3.smallbutton2\"></td>");
				
				sb.append("</tr>");
				sb.append("</table></center><img src=L2UI.SquareWhite width=295 height=1>");
				
			}
			
			if (player.isGM())
				sb.append("<center><br1><a action=\"bypass -h admin_events\" width=54 height=15><font color=\"00FF00\">Admin Config</font></a>");
			
			sb.append("</body></html>");
			html.setHtml(sb.toString());
			player.sendPacket(html);
		}
		else if (getStatus() == State.REGISTERING)
		{
			
			sb.append("<center><table width=256 bgcolor=000000><tr><td width=90><font color=\"FFFF00\">Voting Phase</font></td><td width=140><center><font color=\"00FF00\">Remaining Time:</font> " + EventManager.getInstance().cdtask.getTime() + "</center></td><td width=40><center><font color=\"FFFF00\">Votes</font></center></td></tr></table></center><br></center>");
			
			sb.append("<table align=center width=300 height=20 bgcolor=000000>");
			sb.append("<tr>");
			
			sb.append("<td align=\"left\" width=\"140\"><button value=\" " + getCurrentEvent().getString("eventName") + "\" action=\"bypass -h eventinfo " + getCurrentEvent().getInt("ids") + "\"width=75 height=20 back=\"\" fore=\"\"></td>");
			sb.append("<td align=\"center\" width=\"80\"><button value=\"Register\" action=\"bypass -h event_register\"width=65 height=20 back=\"L2UI_ch3.smallbutton2_down\" fore=\"L2UI_ch3.smallbutton2\"></td>");
			sb.append("<td align=\"center\" width=\"80\"><button value=\"Unregister\" action=\"bypass -h event_unregister\"width=65 height=20 back=\"L2UI_ch3.smallbutton2_down\" fore=\"L2UI_ch3.smallbutton2\"></td>");
			
			sb.append("</tr>");
			sb.append("</table></center><img src=L2UI.SquareWhite width=295 height=1><br>");
			
			sb.append("<center><table width=270 bgcolor=000000><tr><td width=120><font color=\"FFFF00\">Player Name</font></td><td width=40><center><font color=\"00FF00\">Lv</font></center></td><td width=110><center><font color=\"FFFF00\">Class</font></center></td></tr></table></center>");
			
			for (Player p : EventManager.getInstance().players)
			{
				count++;
				
				sb.append("<center><table width=270><tr><td width=120><font color=\"FFFF00\">" + p.getName() + "</font></td><td width=40><center><font color=\"00FF00\">" + p.getStatus().getLevel() + "</font></center></td><td width=110><center><font color=\"FFFF00\">" + p.getTemplate().getClassName() + "</font></center></td></tr></table></center>");
			}
			if (player.isGM())
				sb.append("<center><a action=\"bypass -h admin_events\" width=54 height=15><font color=\"00FF00\">Admin Config</font></a></center>");
			
			sb.append("</body></html>");
			html.setHtml(sb.toString());
			player.sendPacket(html);
		}
		else if (getStatus() == State.RUNNING)
			getCurrentEvent().showHtml(player, obj);
	}
	
	protected class Scheduler implements Runnable
	{
		@Override
		public void run()
		{
			switch (status)
			{
				case VOTING:
					if (votes.size() > 0)
						setCurrentEvent(getVoteWinner());
					else
						setCurrentEvent(eventIds.get(rnd.nextInt(eventIds.size())));
					
					announce("[" + getCurrentEvent().getString("eventName") + "] " + getInt("registerTime") / 60 + " minute(s) until registration is closed!");
					
					announce("[" + getCurrentEvent().getString("eventName") + "]" + " Adjust your participation with the.");
					
					for (Player player : World.getInstance().getPlayers())
						AskJoinRegister(player);
					setStatus(State.REGISTERING);
					counter = getInt("registerTime") - 1;
					ThreadPool.schedule(cdtask, 1);
					break;
				
				case REGISTERING:
					announce("[" + getCurrentEvent().getString("eventName") + "]" + " registration is closed!");
					if (players.size() < getCurrentEvent().getInt("minPlayers"))
					{
						announce("[" + getCurrentEvent().getString("eventName") + "]" + " Adjust your participation with the");
						for (Player player : World.getInstance().getPlayers())
							AskJoinRegister(player);
						getCurrentEvent().reset();
						setCurrentEvent(0);
						players.clear();
						colors.clear();
						positions.clear();
						titles.clear();
						setStatus(State.VOTING);
						counter = getInt("betweenEventsTime") - 1;
						
						ThreadPool.schedule(cdtask, 1);
					}
					else
					{
						setStatus(State.RUNNING);
						msgToAll("[" + getCurrentEvent().getString("eventName") + "]" + " You'll be teleported to the event in 10 seconds.");
						schedule(10000);
					}
					break;
				
				case RUNNING:
					getCurrentEvent().start();
					
					for (Player player : players)
					{
						EventStats.getInstance().tempTable.put(player.getObjectId(), new int[]
						{
							0,
							0,
							0,
							0
						});
					}
					break;
				
				case END:
					teleBackEveryone();
					if (getBoolean("statTrackingEnabled"))
					{
						EventStats.getInstance().applyChanges();
						EventStats.getInstance().tempTable.clear();
						EventStats.getInstance().updateSQL(getCurrentEvent().getPlayerList(), getCurrentEvent().eventId);
					}
					getCurrentEvent().reset();
					setCurrentEvent(0);
					players.clear();
					colors.clear();
					positions.clear();
					titles.clear();
					announce("Next event in " + getInt("betweenEventsTime") / 60 + " minutes!");
					setStatus(State.VOTING);
					counter = getInt("betweenEventsTime") - 1;
					ThreadPool.schedule(cdtask, 1);
					break;
			}
		}
	}
	
	private static class SingletonHolder
	{
		protected static final EventManager _instance = new EventManager();
	}
	
	public static EventManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	public EventManager()
	{
		config = EventConfig.getInstance();
		
		events = new FastMap<>();
		disabled = new FastMap<>();
		players = new FastList<>();
		afkers = new FastMap<>();
		votes = new FastMap<>();
		titles = new FastMap<>();
		colors = new FastMap<>();
		positions = new FastMap<>();
		eventIds = new FastList<>();
		status = State.VOTING;
		task = new Scheduler();
		cdtask = new Countdown();
		counter = 0;
		
		FastList<Integer> disabledEvents = getRestriction("disabledEvents");
		
		// Add the events to the list
		if (!disabledEvents.contains(1))
			events.put(1, new DM());
		else
			disabled.put(1, new DM());
		
		if (!disabledEvents.contains(2))
			events.put(2, new TvT());
		else
			disabled.put(2, new TvT());
		
		if (!disabledEvents.contains(3))
			events.put(3, new CTF());
		else
			disabled.put(3, new CTF());
		
		if (!disabledEvents.contains(4))
			events.put(4, new LMS());
		else
			disabled.put(4, new LMS());
		
		if (!disabledEvents.contains(5))
			events.put(5, new Lucky());
		else
			disabled.put(5, new Lucky());
		
		if (!disabledEvents.contains(6))
			events.put(6, new Zombie());
		else
			disabled.put(6, new Zombie());
		
		if (!disabledEvents.contains(7))
			events.put(7, new Fortress());
		else
			disabled.put(7, new Fortress());
		
		if (!disabledEvents.contains(8))
			events.put(8, new PvPEvent());
		else
			disabled.put(8, new PvPEvent());
		
		for (int eventId : events.keySet())
			eventIds.add(eventId);
		
		// Start the scheduler
		counter = getInt("firstAfterStartTime") - 1;
		ThreadPool.schedule(cdtask, 1);
		
		System.out.println("Event Engine Started");
	}
	
	public boolean addVote(Player player, int eventId)
	{
		if (getStatus() != State.VOTING)
		{
			player.sendMessage("You can't vote now!");
			return false;
		}
		if (votes.containsKey(player))
		{
			player.sendMessage("You have already voted for an event!");
			return false;
		}
		if (player.getStatus().getLevel() < 40)
		{
			player.sendMessage("Your level is too low to vote for events!");
			return false;
		}
		
		player.sendMessage("You have succesfully voted for the event");
		votes.put(player, eventId);
		return true;
	}
	
	protected static void announcetime(String text)
	{
		World.toAllOnlinePlayers(new CreatureSay(0, SayType.CRITICAL_ANNOUNCE, "", text));
	}
	
	protected static void announce(String text)
	{
		World.toAllOnlinePlayers(new CreatureSay(0, SayType.CRITICAL_ANNOUNCE, "", text));
	}
	
	private boolean canRegister(Player player)
	{
		if (players.contains(player))
		{
			player.sendMessage("You are already registered to the event!");
			return false;
		}
		if (player.isInJail())
		{
			player.sendMessage("You can't register from the jail.");
			return false;
		}
		if (player.isInOlympiadMode())
		{
			player.sendMessage("You can't register while you are in the olympiad.");
			return false;
		}
		if (player.getStatus().getLevel() > getCurrentEvent().getInt("maxLvl"))
		{
			player.sendMessage("You are greater than the maximum allowed lvl.");
			return false;
		}
		if (player.getStatus().getLevel() < getCurrentEvent().getInt("minLvl"))
		{
			player.sendMessage("You are lower than the minimum allowed lvl.");
			return false;
		}
		if (player.getKarma() > 0)
		{
			player.sendMessage("You can't register if you have karma.");
			return false;
		}
		if (player.isCursedWeaponEquipped())
		{
			player.sendMessage("You can't register with a cursed weapon.");
			return false;
		}
		if (player.isDead())
		{
			player.sendMessage("You can't register while you are dead.");
			return false;
		}
		if (afkers.containsKey(player.getAccountName()) && afkers.get(player.getAccountName()) == getInt("antiAfkDisallowAfter"))
		{
			player.sendMessage("You can't register because you were AFK inside events.");
			return false;
		}
		if (!getBoolean("dualboxAllowed"))
		{
			String ip = player.getClient().getConnection().getInetAddress().getHostAddress();
			for (Player p : players)
			{
				if (p.getClient().getConnection().getInetAddress().getHostAddress().equalsIgnoreCase(ip))
				{
					player.sendMessage("You have already joined the event with another character.");
					return false;
				}
			}
		}
		
		if (player.getClassId().getId() == 16 || player.getClassId().getId() == 97)
		{
			player.sendMessage("Healers is not allowed to register in events.");
			return false;
		}
		
		return true;
	}
	
	public boolean canTargetPlayer(Player target, Player self)
	{
		if (getStatus() == State.END)
		{
			
			for (Event event : EventManager.getInstance().events.values())
			{
				if (event.getInt("ids") == 7)
					return true;
				if (event.getInt("ids") == 8)
					return true;
			}
			
			if ((isRegistered(target) && isRegistered(self)) || (!isRegistered(target) && !isRegistered(self)))
				return true;
			
			return false;
		}
		
		return true;
	}
	
	protected void end(String text)
	{
		announce(text);
		status = State.END;
		schedule(1);
	}
	
	public boolean getBoolean(String propName)
	{
		return config.getBoolean(0, propName);
	}
	
	public Event getCurrentEvent()
	{
		return current;
	}
	
	public FastList<String> getEventNames()
	{
		FastList<String> map = new FastList<>();
		for (Event event : events.values())
			map.add(event.getString("eventName"));
		
		return map;
	}
	
	public FastMap<Integer, String> getEventMap()
	{
		FastMap<Integer, String> map = new FastMap<>();
		for (Event event : disabled.values())
			map.put(event.getInt("ids"), event.getString("eventName"));
		for (Event event : events.values())
			map.put(event.getInt("ids"), event.getString("eventName"));
		
		return map;
	}
	
	public Event getEvent(int id)
	{
		for (Event event : events.values())
		{
			if (event.getInt("ids") == id)
				return event;
		}
		for (Event event : disabled.values())
		{
			if (event.getInt("ids") == id)
				return event;
		}
		
		return null;
	}
	
	public void enableEvent(int id, int enable)
	{
		if (enable == 1)
		{
			if (disabled.containsKey(id))
				events.put(id, disabled.remove(id));
		}
		else
		{
			if (events.containsKey(id))
				disabled.put(id, events.remove(id));
		}
	}
	
	public boolean isEnabled(int id)
	{
		if (events.containsKey(id))
			return true;
		
		return false;
	}
	
	public int getInt(String propName)
	{
		return config.getInt(0, propName);
	}
	
	protected int[] getPosition(String owner, int num)
	{
		return config.getPosition(0, owner, num);
	}
	
	public FastList<Integer> getRestriction(String type)
	{
		return config.getRestriction(0, type);
	}
	
	public FastList<Integer> getDoor(String type)
	{
		return config.getDoor(0, type);
	}
	
	public int getInt(int eventId, String propName)
	{
		return config.getInt(eventId, propName);
	}
	
	public boolean getBoolean(int eventId, String propName)
	{
		return config.getBoolean(eventId, propName);
	}
	
	public String getString(int eventId, String propName)
	{
		return config.getString(eventId, propName);
	}
	
	private State getStatus()
	{
		return status;
	}
	
	public String getString(String propName)
	{
		return config.getString(0, propName);
	}
	
	public int getVoteCount(int event)
	{
		int count = 0;
		for (int e : votes.values())
			if (e == event)
				count++;
			
		return count;
	}
	
	protected int getVoteWinner()
	{
		int old = 0;
		FastMap<Integer, Integer> temp = new FastMap<>();
		
		for (int vote : votes.values())
		{
			if (!temp.containsKey(vote))
				temp.put(vote, 1);
			else
			{
				old = temp.get(vote);
				old++;
				temp.getEntry(vote).setValue(old);
			}
		}
		
		int max = temp.head().getNext().getValue();
		int result = temp.head().getNext().getKey();
		
		for (Map.Entry<Integer, Integer> entry : temp.entrySet())
		{
			if (entry.getValue() > max)
			{
				max = entry.getValue();
				result = entry.getKey();
			}
		}
		
		votes.clear();
		temp = null;
		return result;
	}
	
	public boolean isRegistered(Player player)
	{
		if (getCurrentEvent() != null)
			return getCurrentEvent().players.containsKey(player);
		
		return false;
	}
	
	public boolean isRegistered(Creature player)
	{
		if (getCurrentEvent() != null)
			return getCurrentEvent().players.containsKey(player);
		
		return false;
	}
	
	public boolean isRunning()
	{
		if (getStatus() == State.RUNNING)
			return true;
		
		return false;
	}
	
	protected void msgToAll(String text)
	{
		for (Player player : players)
			player.sendMessage(text);
	}
	
	public void onLogout(Player player)
	{
		if (votes.containsKey(player))
			votes.remove(player);
		if (players.contains(player))
		{
			players.remove(player);
			colors.remove(player);
			titles.remove(player);
			positions.remove(player);
		}
	}
	
	public boolean registerPlayer(Player player)
	{
		if (getStatus() != State.REGISTERING)
		{
			player.sendMessage("You can't register now!");
			return false;
		}
		
		if (getBoolean("eventBufferEnabled"))
		{
			if (!EventBuffer.getInstance().playerHaveTemplate(player))
			{
				player.sendMessage("You have to set a buff template first!");
				EventBuffer.getInstance().showHtml(player);
				return false;
			}
		}
		if (canRegister(player))
		{
			
			player.sendMessage("You have succesfully registered to the event!");
			players.add(player);
			titles.put(player, player.getTitle());
			colors.put(player, player.getAppearance().getNameColor());
			positions.put(player, new int[]
			{
				player.getX(),
				player.getY(),
				player.getZ()
			});
			return true;
		}
		
		player.sendMessage("You have failed registering to the event!");
		return false;
	}
	
	protected void schedule(int time)
	{
		ThreadPool.schedule(task, time);
	}
	
	protected void setCurrentEvent(int eventId)
	{
		current = eventId == 0 ? null : events.get(eventId);
	}
	
	protected void setStatus(State s)
	{
		status = s;
	}
	
	protected void teleBackEveryone()
	{
		for (Player player : getCurrentEvent().getPlayerList())
		{
			if (!(player.getPolyType() == PolyType.DEFAULT))
			{
				player.unpolymorph();
				player.decayMe();
				player.spawnMe(player.getX(), player.getY(), player.getZ());
			}
			
			if (player.isDead())
				player.doRevive();
			
			player.setEventKills(0);
			player.setEventRank(0);
			
			player.teleportTo(positions.get(player)[0], positions.get(player)[1], positions.get(player)[2], 0);
			player.getAppearance().setNameColor(colors.get(player));
			player.setTitle(titles.get(player));
			
			if (player.getParty() != null)
				player.getParty().removePartyMember(player, MessageType.EXPELLED);
			
			player.broadcastUserInfo();
		}
		
	}
	
	public boolean unregisterPlayer(Player player)
	{
		if (!players.contains(player))
		{
			player.sendMessage("You are not registered to the event!");
			return false;
		}
		else if (getStatus() != State.REGISTERING)
		{
			player.sendMessage("You can't unregister now!");
			return false;
		}
		
		player.sendMessage("You have succesfully unregistered from the event!");
		players.remove(player);
		colors.remove(player);
		positions.remove(player);
		return true;
	}
	
	public boolean areTeammates(Player player, Player target)
	{
		if (getCurrentEvent() == null)
			return false;
		
		if (getCurrentEvent().numberOfTeams() < 2)
			return false;
		
		if (getCurrentEvent().getTeam(player) == getCurrentEvent().getTeam(target))
			return true;
		
		return false;
	}
	
	public void manualStart(int eventId)
	{
		setCurrentEvent(eventId);
		
		announcetime("[" + getCurrentEvent().getString("eventName") + "] " + "Change your participation status using");
		
		for (Player player : World.getInstance().getPlayers())
			AskJoinRegister(player);
		setStatus(State.REGISTERING);
		counter = getInt("registerTime") - 1;
	}
	
	public void manualStop()
	{
		announce("The event has been aborted by a GM.");
		if (getStatus() == State.REGISTERING)
		{
			getCurrentEvent().reset();
			setCurrentEvent(0);
			players.clear();
			colors.clear();
			positions.clear();
			titles.clear();
			setStatus(State.VOTING);
			counter = getInt("betweenEventsTime") - 1;
		}
		else
			getCurrentEvent().endEvent();
	}
	
	public static boolean checkIfOkToCastSealOfRule(Player player)
	{
		
		if (player.getTarget() instanceof Npc && ((Npc) player.getTarget())._isFOS_Artifact && player.isInFunEvent())
			return true;
		
		return false;
	}
	
	public void AskJoinRegister(Player player)
	{
		if (EventManager.getInstance().isRegistered(player) || OlympiadManager.getInstance().isRegistered(player) || player.isAlikeDead() || player.isTeleporting() || player.isDead() || player.isInObserverMode() || player.isInStoreMode() || player.isInsideZone(ZoneId.BOSS) || player.isInsideZone(ZoneId.CASTLE) || player.isInsideZone(ZoneId.SIEGE) || player.getClassId() == ClassId.BISHOP || player.getClassId() == ClassId.CARDINAL || player.getClassId() == ClassId.SHILLIEN_TEMPLAR || player.getClassId() == ClassId.SHILLIEN_SAINT || player.getClassId() == ClassId.ELVEN_ELDER || player.getClassId() == ClassId.EVAS_SAINT || player.getClassId() == ClassId.PROPHET || player.getClassId() == ClassId.HIEROPHANT || player.isAutoFarm() || player.getStatus().getLevel() < getCurrentEvent().getInt("minLvl"))
			return;
		
		ConfirmDlg confirm = new ConfirmDlg(SystemMessageId.TVT.getId());
		confirm.addString("Do you wish to join " + player.getName() +" " + getCurrentEvent().getString("eventName") + "?");
		confirm.addTime(7000);
		confirm.addRequesterId(player.getObjectId());
		player.sendMessage(player.getName() + " was invited to your " + getCurrentEvent().getString("eventName") + ".");
		player.sendPacket(confirm);
	}
	
	public boolean _request = true;
	
	public boolean isSpecialEvent()
	{
		return getCurrentEvent() != null && (getCurrentEvent() instanceof DM);
	}
	
	public boolean isFortressManager()
	{
		return getCurrentEvent() != null && (getCurrentEvent() instanceof Fortress);
	}
	
}