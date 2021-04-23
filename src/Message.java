import java.util.Arrays;

public class Message{

    public int getSenderId() {
        return senderId;
    }

    public int getRecieverId() {
        return recieverId;
    }

    public int getMESSAGE_TYPE() {
        return MESSAGE_TYPE;
    }

    public double[] getMsgContent() {
        return msgContent;
    }

    private int senderId, recieverId, MESSAGE_TYPE;
    private double[] msgContent;

    private String msgStrContent;

    public Message(int senderId, int recieverId, int MESSAGE_TYPE, double[] msg) {
        this.senderId = senderId;
        this.recieverId = recieverId;
        this.MESSAGE_TYPE = MESSAGE_TYPE;
        this.msgContent = msg;
    }
    public Message(int senderId, int recieverId, int MESSAGE_TYPE, String msg)
    {
        this.senderId = senderId;
        this.recieverId = recieverId;
        this.MESSAGE_TYPE = MESSAGE_TYPE;
        this.msgStrContent = msg;
    }

    @Override
    public String toString() {
        return "Message{" +
                "senderId=" + senderId +
                ", recieverId=" + recieverId +
                ", MESSAGE_TYPE=" + MESSAGE_TYPE +
                ", msgContent=" + Arrays.toString(msgContent) +
                '}';
    }


}