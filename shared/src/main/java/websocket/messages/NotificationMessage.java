package websocket.messages;

import model.GameData;

public class NotificationMessage extends ServerMessage{
    private final String note;

    public NotificationMessage(String note){
        super(ServerMessageType.NOTIFICATION);
        this.note = note;
    }
    public String getNotification(){
        return note;
    }
}
