package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.gameserver.model.actor.Player;

public class RequestTutorialClientEvent extends L2GameClientPacket
{
	private int _eventId;
	
	@Override
	protected void readImpl()
	{
		set_eventId(readD());
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		
		//	final QuestState qs = player.getQuestList().getQuestState("Tutorial");
		//	if (qs != null)
		//		qs.getQuest().notifyEvent("CE" + _eventId + "", null, player);
		
	}

	/**
	 * @return the _eventId
	 */
	public int get_eventId()
	{
		return _eventId;
	}

	/**
	 * @param _eventId the _eventId to set
	 */
	public void set_eventId(int _eventId)
	{
		this._eventId = _eventId;
	}
}