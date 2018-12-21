package hell.supersoul.arena.waitroom;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import hell.supersoul.arena.recovery.RecoverManager;
import hell.supersoul.arena.utils.AUtils;

public class WREventListener implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		RecoverManager.get().loginCheck(event.getPlayer());
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player))
			return;
		ItemStack is = event.getCurrentItem();
		if (is == null)
			return;
		if (!is.hasItemMeta())
			return;
		if (!is.getItemMeta().hasLore())
			return;
		String code = is.getItemMeta().getLore().get(is.getItemMeta().getLore().size()-1);
		code = AUtils.convertToVisibleString(code);
		if (!code.startsWith("SSArena"))
			return;
		String codes[] = code.split(" ");
		Player player = (Player) event.getWhoClicked();
		if (codes.length == 3 && codes[1].equals("create")) {
			WaitRoom wr = WRManager.getManager().createWaitRoom(codes[2]);
			WRManager.getManager().addPlayer(wr.getRoomName(), player.getName());
			WRManager.getManager().setRoomOwner(null, player.getName());
		} else if (codes.length == 4 && codes[1].equals("open")) {
			WRManager.getManager().openWaitroomMenu(codes[2], player, Integer.parseInt(codes[3]));
		}
	}
	
}
