package me.rejomy.murder.util.expansion;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.data.Arena;
import me.rejomy.murder.data.Match;
import me.rejomy.murder.data.PlayerData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Expansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "murder";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Rejomy";
    }

    @Override
    public @NotNull String getVersion() {
        return MurderAPI.INSTANCE.getPlugin().getDescription().getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        PlayerData data = MurderAPI.INSTANCE.getDataManager().get(player);

        return switch (params.toLowerCase()) {
            case "murder_chance" -> String.valueOf(data.chanceToMurder);
            case "detective_chance" -> String.valueOf(data.chanceToDetective);
            case "games" -> String.valueOf(data.games);
            case "wins" -> String.valueOf(data.wins);
            case "online" -> String.valueOf(MurderAPI.INSTANCE.getMatchManager().getTotalOnline());

            case "is_playing" -> {
                Match match = MurderAPI.INSTANCE.getMatchManager().get(player);

                if (match == null) {
                    yield "false";
                }

                yield "" + (match.getArena().status == Arena.Status.PLAYING);
            }

            case "is_waiting" -> {

                Match match = MurderAPI.INSTANCE.getMatchManager().get(player);

                if (match == null) {
                    yield "false";
                }

                yield "" + (match.getArena().status == Arena.Status.WAITING
                        || match.getArena().status == Arena.Status.ENDING
                        || match.getArena().status == Arena.Status.STARTING);
            }

            default -> "-1";
        };
    }
}
