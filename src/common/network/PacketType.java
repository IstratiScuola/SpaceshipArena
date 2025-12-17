package common.network;

public enum PacketType {
    JOIN_REQUEST,
    JOIN_RESPONSE,
    INPUT,
    GAME_STATE,
    PLAYER_JOINED,
    PLAYER_LEFT,
    PLAYER_DIED,
    PLAYER_RESPAWNED,
    DISCONNECT,
    LOBBY_STATE,
    READY_TOGGLE,
    GAME_START,
    GAME_OVER
}