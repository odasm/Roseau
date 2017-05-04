package org.alexdev.roseau.game.player;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.alexdev.roseau.Roseau;
import org.alexdev.roseau.game.GameVariables;

import com.google.common.collect.Lists;

public class PlayerManager {

	private ConcurrentHashMap<Integer, Player> players;
	private List<Permission> permissions;

	public PlayerManager() {
		this.players = new ConcurrentHashMap<Integer, Player>();
		this.permissions = Roseau.getDao().getPlayer().getPermissions();
	}

	public Player getByID(int userID) {

		try {
			return this.players.values().stream().filter(s -> s.getDetails().getID() == userID).findFirst().get();
		} catch (Exception e) {
			return null;
		}
	}

	public Player getByIDMainServer(int userID) {

		try {
			return this.players.values().stream().filter(s -> s.getDetails().getID() == userID && s.getNetwork().getServerPort() == Roseau.getServerPort()).findFirst().get();
		} catch (Exception e) {
			return null;
		}
	}

	public void syncPlayerTickets(int userID, int tickets) {

		try {

			List<Player> players =  this.players.values().stream().filter(player -> 
			player.getDetails().getID() == tickets)
					.collect(Collectors.toList());

			for (Player player : players) {
				player.getDetails().setTickets(tickets);
			}


		} catch (Exception e) {	}

	}

	public Player getPrivateRoomPlayer(int userID) {

		try {
			return this.players.values().stream().filter(s -> s.getDetails().getID() == userID && s.getNetwork().getServerPort() == Roseau.getPrivateServerPort()).findFirst().get();
		} catch (Exception e) {
			return null;
		}
	}

	public Player getPrivateRoomPlayer(String username) {

		try {
			return this.players.values().stream().filter(s -> s.getDetails().getName().equals(username) && s.getNetwork().getServerPort() == Roseau.getPrivateServerPort()).findFirst().get();
		} catch (Exception e) {
			return null;
		}
	}

	public Player getPlayerDifferentConnection(int userID, int connectionID) {

		try {
			return this.players.values().stream().filter(s -> s.getDetails().getID() == userID && s.getNetwork().getConnectionId() != connectionID).findFirst().get();
		} catch (Exception e) {
			return null;
		}
	}

	public Player getPlayerByPortDifferentConnection(int userID, int port, int connectionID) {

		try {
			return this.players.values().stream().filter(s -> s.getDetails().getID() == userID && s.getNetwork().getServerPort() == port && s.getNetwork().getConnectionId() != connectionID).findFirst().get();
		} catch (Exception e) {
			return null;
		}
	}


	public Player getByName(String name) {

		try {
			return this.players.values().stream().filter(s -> s.getDetails().getName().toLowerCase().equals(name.toLowerCase())).findFirst().get();
		} catch (Exception e) {
			return null;
		}
	}

	public PlayerDetails getPlayerData(int userID) {

		Player player = this.getByID(userID);

		if (player == null) {
			return Roseau.getDao().getPlayer().getDetails(userID);
		}

		return player.getDetails();
	}

	public boolean checkForDuplicates(Player player) {

		if (player.getNetwork().getConnectionId() == -1 || player.getDetails() == null) {
			return false;
		}

		for (Player session : this.players.values()) {

			if (session.getNetwork().getConnectionId() == -1 || session.getDetails() == null) {
				continue;
			}

			if (session.getDetails().getID() == player.getDetails().getID()) {
				if (session.getNetwork().getConnectionId() != player.getNetwork().getConnectionId()) { // user tries to login twice
					return true;
				}
			}
		}
		return false;
	}

	public boolean approveName(String name)
	{
		// FAILproof!
		if (name != null) {

			// Atleast 3 characters and not more than 20?
			if (name.length() >= 3 && name.length() <= 20) {
				// Does username start with MOD- ?
				if (name.indexOf("MOD-") != 0) {

					// We don't want m0d neither...
					if (name.indexOf("M0D-") != 0)
					{
						// Check for characters
						String allowed = GameVariables.USERNAME_CHARS;

						if (allowed.equals("*")) {

							// Any name can pass!
							return true;
						} else {

							// Check each character in the name
							char[] nameChars = name.toCharArray();

							for (int i = 0; i < nameChars.length; i++) {

								// Is this character allowed?
								if (allowed.indexOf(Character.toLowerCase(nameChars[i])) == -1) {
									// Not allowed
									return false;
								}
							}

							// Passed all checks!
							return true;
						}
					}
				}
			}
		}

		// Bad for whatever reason!
		return false;
	}

	public List<Player> getMainServerPlayers() {
		try {
			List<Player> players =  this.players.values().stream().filter(player -> 
			player.getNetwork().getServerPort() == Roseau.getServerPort())
					.collect(Collectors.toList());

			return players;
		} catch (Exception e) {
			e.printStackTrace();
			return Lists.newArrayList();
		}
	}

	public boolean hasPermission(int rank, String perm) {

		for (Permission permission  : this.permissions) {

			if (permission.getPermission().equals(perm)) {

				if (permission.isInheritable()) {
					if (rank >= permission.getRank()) {
						return true;
					}
				} else {
					if (rank == permission.getRank()) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public ConcurrentHashMap<Integer, Player> getPlayers() {
		return players;
	}


}
