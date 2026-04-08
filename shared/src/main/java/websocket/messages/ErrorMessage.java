package websocket.messages;

import model.GameData;

public class ErrorMessage extends ServerMessage{
    private String error;

    public ErrorMessage(String error){
        super(ServerMessageType.ERROR);
        this.error = "Error: " + error;
    }
    public String getError(){
        return error;
    }
}
