CREATE TABLE `spawnlist` (
  `npc_templateid` int(11) NOT NULL DEFAULT 0,
  `locx` int(11) NOT NULL DEFAULT 0,
  `locy` int(11) NOT NULL DEFAULT 0,
  `locz` int(11) NOT NULL DEFAULT 0,
  `heading` int(11) NOT NULL DEFAULT 0,
  `respawn_delay` bigint(20) NOT NULL DEFAULT 0,
  PRIMARY KEY (`npc_templateid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
