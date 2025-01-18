package me.rejomy.murder.util;

import lombok.experimental.UtilityClass;
import me.rejomy.murder.MurderAPI;
import me.rejomy.murder.data.Arena;
import me.rejomy.murder.data.Match;
import me.rejomy.murder.data.PlayerData;
import me.rejomy.murder.data.PlayerRole;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@UtilityClass
public class MurderUtil {

    public void setDetectiveAndMurders(Match match) {
        HashMap<Player, PlayerData> players = new HashMap<>(match.getPlayers());

        int murderCount = 1, detectiveCount = 1,
            playerDependency = MurderAPI.INSTANCE.getFileManager().getConfig().getSettingPlayerDependency(),
                // Remove from size player dependency, because else we will get 2 murders and detectives with dependency equals 4
                size = players.size() - playerDependency;
        boolean randomizeDependency = MurderAPI.INSTANCE.getFileManager().getConfig().isSettingPlayerDependencyRandomize();

        /*
         * If players a lot on the game, we should set randomize amount of murders and detectives.
         */
        if(size > playerDependency) {
            int playersPerEntity = size / playerDependency;

            // add + 1 to random because else if value will equals 1, we receive only zero with random.
            murderCount += randomizeDependency? RandomUtil.RANDOM.nextInt(playersPerEntity + 1) : playersPerEntity;
            detectiveCount += randomizeDependency? RandomUtil.RANDOM.nextInt(playersPerEntity + 1) : playersPerEntity;
        }

        // Sort this value as first element is highest chance of get this role.
        List<Map.Entry<Player, PlayerData>> murderers = new HashMap<>(players).entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> -entry.getValue().chanceToMurder))
                .collect(Collectors.toList());

        List<Map.Entry<Player, PlayerData>> detectives = new HashMap<>(players).entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> -entry.getValue().chanceToDetective))
                .collect(Collectors.toList());


        // Select random detectives based on their chances
        selectRole(match, detectives, detectiveCount, PlayerRole.DETECTIVE,
                (playerData) -> playerData.chanceToDetective,
                (playerData) -> {
                    playerData.chanceToDetective = 0;
                    playerData.gamesWithoutDetectiveRole = 1;
                });

        recalculatePlayerChances(match);
        murderers.removeIf(entry -> entry.getValue().role == PlayerRole.DETECTIVE);

        // Select random murderers based on their chances, excluding detectives
        selectRole(match, murderers, murderCount, PlayerRole.MURDER,
                (playerData) -> playerData.chanceToMurder,
                (playerData) -> {
                    playerData.chanceToMurder = 0;
                    playerData.gamesWithoutMurderRole = 1;
                });
    }

    private void selectRole(Match match, List<Map.Entry<Player, PlayerData>> candidates, int roleCount,
                            PlayerRole role, Function<PlayerData, Integer> chanceFunction, Consumer<PlayerData> resetChances) {

        for (int i = 0; i < roleCount; i++) {
            Player selectedPlayer = getRandomPlayerByChance(candidates, chanceFunction);
            PlayerData data = match.getPlayers().get(selectedPlayer);
            data.role = role;

            // Set chance to 0 to avoid selecting the same player for another role
            resetChances.accept(data);

            // Remove selected player from the candidate list
            candidates.removeIf(entry -> entry.getKey().equals(selectedPlayer));
        }
    }

    private Player getRandomPlayerByChance(List<Map.Entry<Player, PlayerData>> candidates, Function<PlayerData, Integer> chanceFunction) {
        int totalChance = candidates.stream().mapToInt(entry -> chanceFunction.apply(entry.getValue())).sum();
        int randomValue = RandomUtil.RANDOM.nextInt(totalChance);
        int cumulativeChance = 0;

        for (Map.Entry<Player, PlayerData> entry : candidates) {
            cumulativeChance += chanceFunction.apply(entry.getValue());
            if (randomValue < cumulativeChance) {
                return entry.getKey();
            }
        }

        // Fallback in case something goes wrong (shouldn't normally happen)
        return candidates.get(0).getKey();
    }

    public void saveStats(PlayerData data) {
        if (data.spectator) {
            data.games++;

            data.gamesWithoutDetectiveRole++;
            data.gamesWithoutMurderRole++;
        } else if (data.currentMatch != null && data.currentMatch.getArena().status == Arena.Status.ENDING) {
            data.games++;

            data.gamesWithoutDetectiveRole++;
            data.gamesWithoutMurderRole++;

            data.wins++;
        }
    }

    /**
     * We should recalculate player chances for display correct chance for detective or murder.
     *  Because games without murder role is not equals chance.
     * @param match - match where we should do this.
     */
    public void recalculatePlayerChances(Match match) {
        Set<Map.Entry<Player, PlayerData>> entrySet = new HashMap<>(match.getPlayers()).entrySet();

        // Get total games without role
        int totalGamesWithoutMurder = 0;
        int totalGamesWithoutDetective = 0;

        for (Map.Entry<Player, PlayerData> entry : entrySet) {
            PlayerData data = entry.getValue();

            // We need to run this in select murders method, and for prevent add player with role, we should continue.
            if (data.role != PlayerRole.INNOCENT) continue;

            totalGamesWithoutMurder += data.gamesWithoutMurderRole;
            totalGamesWithoutDetective += data.gamesWithoutDetectiveRole;
        }

        // Calculate chances and save these chances to player data.
        for (Map.Entry<Player, PlayerData> entry : entrySet) {
            PlayerData data = entry.getValue();

            // We need to run this in select murders method, and for prevent add player with role, we should continue.
            if (data.role != PlayerRole.INNOCENT) {
                data.chanceToMurder = 0;
                data.chanceToDetective = 0;
                continue;
            }

            int murderChance = ((data.gamesWithoutMurderRole) * 100) / totalGamesWithoutMurder;
            int detectiveChance = ((data.gamesWithoutDetectiveRole) * 100) / totalGamesWithoutDetective;

            data.chanceToMurder = murderChance;
            data.chanceToDetective = detectiveChance;
        }
    }
}
