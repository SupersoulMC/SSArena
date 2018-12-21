package hell.supersoul.arena.waitroom;

import java.util.ArrayList;

public class MapData {
	public static ArrayList<MapData> maps = new ArrayList<>();
	
	String className, displayName, worldName, titleName;
	boolean luckyBlocks, allowLuckyBlocksSwitch, allowArmorAbilitySwitch, team, allowChooseTeam, allowChooseTeamSwitch, defaultMap;
	int difficulty, minPlayer = 1, maxPlayer = 30;
	int teams = 1;
	ArrayList<String> desc = new ArrayList<>();
	public MapData(String className, int difficulty, ArrayList<String> desc) {
		this.className = className;
		this.difficulty = difficulty;
		this.desc = desc;
		
		maps.add(this);
	}
	public String getClassName() {
		return className;
	}
	public boolean isLuckyBlocks() {
		return luckyBlocks;
	}
	public boolean isAllowLuckyBlocksSwitch() {
		return allowLuckyBlocksSwitch;
	}
	public boolean isAllowArmorAbilitySwitch() {
		return allowArmorAbilitySwitch;
	}
	public boolean isTeam() {
		return team;
	}
	public boolean isAllowChooseTeam() {
		return allowChooseTeam;
	}
	public int getDifficulty() {
		return difficulty;
	}
	public int getTeams() {
		return teams;
	}
	public ArrayList<String> getDesc() {
		return desc;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public void setLuckyBlocks(boolean luckyBlocks) {
		this.luckyBlocks = luckyBlocks;
	}
	public void setAllowLuckyBlocksSwitch(boolean allowLuckyBlocksSwitch) {
		this.allowLuckyBlocksSwitch = allowLuckyBlocksSwitch;
	}
	public void setAllowArmorAbilitySwitch(boolean allowArmorAbilitySwitch) {
		this.allowArmorAbilitySwitch = allowArmorAbilitySwitch;
	}
	public void setTeam(boolean team) {
		this.team = team;
	}
	public void setAllowChooseTeam(boolean allowChooseTeam) {
		this.allowChooseTeam = allowChooseTeam;
	}
	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}
	public void setTeams(int teams) {
		this.teams = teams;
	}
	public void setDesc(ArrayList<String> desc) {
		this.desc = desc;
	}
	public String getDisplayName() {
		return displayName;
	}
	public String getWorldName() {
		return worldName;
	}
	public boolean isAllowChooseTeamSwitch() {
		return allowChooseTeamSwitch;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}
	public void setAllowChooseTeamSwitch(boolean allowChooseTeamSwitch) {
		this.allowChooseTeamSwitch = allowChooseTeamSwitch;
	}
	public String getTitleName() {
		return titleName;
	}
	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}
	public boolean isDefaultMap() {
		return defaultMap;
	}
	public void setDefaultMap(boolean defaultMap) {
		this.defaultMap = defaultMap;
	}
	public int getMinPlayer() {
		return minPlayer;
	}
	public int getMaxPlayer() {
		return maxPlayer;
	}
	public void setMinPlayer(int minPlayer) {
		this.minPlayer = minPlayer;
	}
	public void setMaxPlayer(int maxPlayer) {
		this.maxPlayer = maxPlayer;
	}
}
