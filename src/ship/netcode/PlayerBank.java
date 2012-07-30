package ship.netcode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PlayerBank implements Iterable<PlayerData> {

    private Map<Integer, PlayerData> players;

    private int maxPings;

    public PlayerBank(int maxPings) {
        players = new HashMap<>();

        this.maxPings = maxPings;
    }

    public void addPlayer(int playerId) {
        players.put(playerId, new PlayerData(playerId));
    }
    public void remPlayer(int playerId) {
        players.remove(playerId);
    }

    public void setPlayerName(int playerId, String name) {
        players.get(playerId).playerName = name;
    }
    public String getPlayerName(int playerId) {
        return players.get(playerId).playerName;
    }

    public void incrementPingCount(int playerId) {
        players.get(playerId).pingCount++;
    }
    public int getPingCount(int playerId) {
        return players.get(playerId).pingCount;
    }

    public boolean pingReady() {
        for (PlayerData data : this) {
            return data.playerId == 0 || data.pingCount < maxPings;
        }

        return true;
    }
    public void resetAllPings() {
        for (PlayerData data : this) {
            data.pingCount = 0;
        }
    }

    public String createPingStatusMessage() {
        String message = "Syncing, please wait";

        for (PlayerData data : this) {
            if (data.playerId != 0)
                message += "\n" +data.playerName+ " (" +data.pingCount+ "/" +maxPings+ ")";
        }

        return message;
    }

    @Override
    public Iterator<PlayerData> iterator() {
        return new PlayerBankIterator(players, players.keySet());
    }

}


class PlayerData {

    int    playerId;
    String playerName;
    int    pingCount;

    PlayerData(int playerId) {
        this.playerId = playerId;

        pingCount = 0;
    }
}

class PlayerBankIterator implements Iterator<PlayerData> {

    private Iterator<Integer> playerIds;
    private Map<Integer, PlayerData> bank;

    PlayerBankIterator(Map<Integer, PlayerData> bank, Set<Integer> playerIds) {
        this.playerIds = playerIds.iterator();
        this.bank      = bank;
    }

    @Override
    public boolean hasNext() {
        return playerIds.hasNext();
    }

    @Override
    public PlayerData next() {
        return bank.get(playerIds.next());
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}