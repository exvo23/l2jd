package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.gameserver.model.actor.Player;

public class RequestTutorialQuestionMark extends L2GameClientPacket
{
	private int _number;
	
	@Override
	protected void readImpl()
	{
		set_number(readD());
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		// final QuestState qs = player.getQuestList().getQuestState("Tutorial");
		// if (qs != null)
		// qs.getQuest().notifyEvent("QM" + _number + "", null, player);
		
	}

	/**
	 * @return the _number
	 */
	public int get_number()
	{
		return _number;
	}

	/**
	 * @param _number the _number to set
	 */
	public void set_number(int _number)
	{
		this._number = _number;
	}
}