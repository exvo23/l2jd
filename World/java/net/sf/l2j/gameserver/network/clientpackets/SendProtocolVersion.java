package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.network.serverpackets.L2GameServerPacket;
import net.sf.l2j.gameserver.network.serverpackets.VersionCheck;

public final class SendProtocolVersion extends L2GameClientPacket
{
	private int _version;
	
	@Override
	protected void readImpl()
	{
		_version = readD();
	}
	
	@Override
	protected void runImpl()
	{
		if (_version == -2)
			getClient().close(new VersionCheck(getClient().enableCrypt()));
		else if (_version < Config.MIN_PROTOCOL_REVISION || _version > Config.MAX_PROTOCOL_REVISION)
		{
			LOGGER.info("Client: " + getClient().toString() + " -> Protocol Revision: " + _version + " is invalid. Minimum and maximum allowed are: " + Config.MIN_PROTOCOL_REVISION + " and " + Config.MAX_PROTOCOL_REVISION + ". Closing connection.");
			getClient().close((L2GameServerPacket) null);
		}

		switch (_version)
		{
			case 737:
			case 740:
			case 744:
			case 746:
				getClient().sendPacket(new VersionCheck(getClient().enableCrypt()));
				break;
			
			default:
				getClient().close((L2GameServerPacket) null);
				break;
		}
	}
}