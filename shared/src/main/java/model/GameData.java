package model;

import chess.ChessGame;

import java.util.Objects;

public class GameData {
    private final int gameId;
    private final String whiteUsername;
    private final String blackUsername;
    private final String gameName;
    private final ChessGame game;
    GameData(int gameId, String whiteUsername, String blackUsername, String gameName, ChessGame game){
        this.gameId = gameId;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game = game;
    }
    public int getGameId(){
        return gameId;
    }
    public String getWhiteUsername(){
        return whiteUsername;
    }
    public String getBlackUsername() {
        return blackUsername;
    }
    public String getGameName() {
        return gameName;
    }
    public ChessGame getGame(){
        return game;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GameData gameData)) {
            return false;
        }
        return getGameId() == gameData.getGameId() && Objects.equals(getWhiteUsername(), gameData.getWhiteUsername()) && Objects.equals(getBlackUsername(), gameData.getBlackUsername()) && Objects.equals(getGameName(), gameData.getGameName()) && Objects.equals(getGame(), gameData.getGame());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGameId(), getWhiteUsername(), getBlackUsername(), getGameName(), getGame());
    }

    @Override
    public String toString() {
        return "GameData{" +
                "gameId=" + gameId +
                ", whiteUsername='" + whiteUsername + '\'' +
                ", blackUsername='" + blackUsername + '\'' +
                ", gameName='" + gameName + '\'' +
                ", game=" + game +
                '}';
    }
}

