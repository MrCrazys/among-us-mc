package com.nktfh100.AmongUs.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nktfh100.AmongUs.info.Arena;
import com.nktfh100.AmongUs.info.BungeArena;
import com.nktfh100.AmongUs.info.PlayerInfo;
import com.nktfh100.AmongUs.inventory.CosmeticSelectorInv;
import com.nktfh100.AmongUs.main.Main;

public class PlayersCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			PlayerInfo pInfo = Main.getPlayersManager().getPlayerInfo(player);
			if (args == null || args.length == 0) {
				return false;
			}
			if (pInfo == null) {
				pInfo = Main.getPlayersManager()._addPlayer(player);
			}

			if (args[0].equalsIgnoreCase("join")) {
				if (!pInfo.getIsIngame()) {
					if (args.length > 1) {
						if (Main.getConfigManager().getBungeecord()) {
							if (Main.getBungeArenaManager().getArenaByServer(args[1]) != null) {
								Main.sendPlayerToArena(player, args[1]);
							} else {
								player.sendMessage(Main.getMessagesManager().getGameMsg("arenaNotFound", null, args[1]));
							}
						} else {
							if (Main.getArenaManager().getArenaByName(args[1]) != null) {
								Arena arena = Main.getArenaManager().getArenaByName(args[1]);
								arena.playerJoin(player);
							} else {
								player.sendMessage(Main.getMessagesManager().getGameMsg("arenaNotFound", null, args[1]));
							}
						}
					} else {
						// join arena with most players
						if (Main.getConfigManager().getBungeecord() && Main.getConfigManager().getBungeecordIsLobby()) {
							BungeArena arena_ = Main.getBungeArenaManager().getArenaWithMostPlayers();
							if (arena_ != null) {
								Main.sendPlayerToArena(player, arena_.getServer());
							} else {
								player.sendMessage(Main.getMessagesManager().getGameMsg("noArenasAvailable", null, null));
							}
						} else {
							Arena arena = Main.getArenaManager().getArenaWithMostPlayers();
							if (arena != null) {
								arena.playerJoin(player);
							} else {
								player.sendMessage(Main.getMessagesManager().getGameMsg("noArenasAvailable", null, null));
							}
						}
					}
				} else {
					player.sendMessage(Main.getMessagesManager().getGameMsg("alreadyInGame", null, pInfo.getArena().getDisplayName()));
				}
			} else if (args[0].equalsIgnoreCase("joinrandom")) {
				if (!pInfo.getIsIngame()) {
					// join random arena
					if (Main.getConfigManager().getBungeecord() && Main.getConfigManager().getBungeecordIsLobby()) {
						BungeArena arena_ = Main.getBungeArenaManager().getRandomArena();
						if (arena_ != null) {
							Main.sendPlayerToArena(player, arena_.getServer());
						} else {
							player.sendMessage(Main.getMessagesManager().getGameMsg("noArenasAvailable", null, null));
						}
					} else {
						Arena arena = Main.getArenaManager().getRandomArena();
						if (arena != null) {
							arena.playerJoin(player);
						} else {
							player.sendMessage(Main.getMessagesManager().getGameMsg("noArenasAvailable", null, null));
						}
					}
				} else {
					player.sendMessage(Main.getMessagesManager().getGameMsg("alreadyInGame", null, pInfo.getArena().getDisplayName()));
				}
			} else if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("leave") && pInfo.getIsIngame()) {
					pInfo.getArena().playerLeave(player, false, false, true);
				} else if (args[0].equalsIgnoreCase("arenas") && !pInfo.getIsIngame()) {
					if (Main.getConfigManager().getBungeecord() && Main.getConfigManager().getBungeecordIsLobby()) {
						Main.getBungeArenaManager().openArenaSelector(pInfo);
					} else if (!Main.getConfigManager().getBungeecord()) {
						Main.getArenaManager().openArenaSelector(pInfo);
					}
				} else if (args[0].equalsIgnoreCase("cosmetics")) {
					if(!pInfo.getIsIngame()) {
						if(Main.getIsPlaceHolderAPI()) {
							player.openInventory(new CosmeticSelectorInv(pInfo).getInventory());							
						}
					}
				}
			}
		}
		return true;
	}
}