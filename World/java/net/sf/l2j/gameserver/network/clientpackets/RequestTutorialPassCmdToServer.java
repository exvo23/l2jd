package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.gameserver.model.actor.Player;

public class RequestTutorialPassCmdToServer extends L2GameClientPacket
{
	private String _bypass;
	
	@Override
	protected void readImpl()
	{
		set_bypass(readS());
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
	//	final QuestState qs = player.getQuestList().getQuestState("Tutorial");
	//	if (qs != null)
	//		qs.getQuest().notifyEvent(_bypass, null, player);
	}

	/**
	 * @return the _bypass
	 */
	public String get_bypass()
	{
		return _bypass;
	}

	/**
	 * @param _bypass the _bypass to set
	 */
	public void set_bypass(String _bypass)
	{
		this._bypass = _bypass;
	}
}