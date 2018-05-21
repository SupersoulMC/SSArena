package hell.supersoul.arena.enums;

import org.bukkit.ChatColor;

public enum InvName {
	MyProfile(ChatColor.BLACK + "My Profile"), FriendList(ChatColor.BLACK + "Friends List"), PlayerStats(ChatColor.BLACK + "StatsBook"), PendingFriends(ChatColor.BLACK + "Friend Requests"), WaitingFriends(ChatColor.BLACK + "Sent Friend Requests"), FriendMenu(ChatColor.BLACK + "Friend Menu"), Invitation(ChatColor.BLACK + "Invitation"), TeamMenu(ChatColor.BLACK + "Team Menu");

	String name;

	private InvName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
