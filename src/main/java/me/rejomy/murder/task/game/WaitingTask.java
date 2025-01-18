package me.rejomy.murder.task.game;

import lombok.Getter;
import lombok.Setter;
import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.data.Arena;
import me.rejomy.murder.data.Match;
import me.rejomy.murder.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class WaitingTask {

    @Getter
    @Setter
    private int taskId;
    private final Match match;
    Arena arena;
    private int waitingDelay = MurderAPI.INSTANCE.getFileManager().getConfig().getWaitingDelay();

    public WaitingTask(Match match) {
        this.match = match;
        this.arena = match.getArena();
    }

    public void run() {
        if (match.getPlayers().size() < arena.minPlayers) {
            Bukkit.getScheduler().cancelTask(getTaskId());

            for (Player player : match.getPlayers().keySet()) {
                MessageUtil.sendMessage(player, MurderAPI.INSTANCE.getFileManager().getMessage().getWaitingStop(),
                        "arena", arena.name, "player", player.getName(),
                        "max", arena.maxPlayers,
                        "min", arena.minPlayers,
                        "current", match.getPlayers().size());

                player.setLevel(0);
            }

            match.schedule = false;
            return;
        }

        // Here we calculate waiting delay if anyone is join to the game.
        waitingDelay = Math.min(waitingDelay, Math.max(MurderAPI.INSTANCE.getFileManager().getConfig().getMinWaitingDelay(),
                // Calculate time before start like: max players in arena
                (((match.getPlayers().size() * 100) / arena.maxPlayers) * MurderAPI.INSTANCE.getFileManager().getConfig().getWaitingDelay()) / 100
                ));

        String message = "";
        String title = "", subtitle = "";

        for(Map.Entry<Integer, HashMap<String, String>> entry : MurderAPI.INSTANCE.getFileManager().getMessage()
                .getWaitingSection().entrySet()) {
            if(entry.getKey() != waitingDelay) {
                continue;
            }

            for(Map.Entry<String , String>  actions : entry.getValue().entrySet()) {
                switch (actions.getKey().toLowerCase()) {
                    case "message":
                        message = actions.getValue();
                        break;
                    case "title":
                        title = actions.getValue();
                        break;
                    case "subtitle":
                        subtitle = actions.getValue();
                }
            }

            break;
        }

        for(Player player : match.getPlayers().keySet()) {
            player.setLevel(waitingDelay);

            MessageUtil.sendMessage(player, message);

            player.sendTitle(title, subtitle);
        }

        waitingDelay--;

        if(waitingDelay == 0) {
            MurderAPI.INSTANCE.getMatchManager().start(match);
            Bukkit.getScheduler().cancelTask(getTaskId());
        }
    }
}
