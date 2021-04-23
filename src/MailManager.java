import java.util.Arrays;
import java.util.Vector;

public class MailManager extends Thread {
    // initialization of queue size
    int MAX_MSG;
    public int current_iter;
    public boolean[] isCurrentIterDone;

    private Vector<Message> messages = new Vector();

    private Vector<Message> costMessages = new Vector();

    private Vector<Message> bestMessages = new Vector<>();

    MailManager(int MAX_MSG, int MAX_NODE)
    {
        this.MAX_MSG = MAX_MSG;
        this.current_iter = 0;
        this.isCurrentIterDone = new boolean[MAX_NODE];
        Arrays.fill(this.isCurrentIterDone, false);
    }

    @Override
    public void run()
    {
        try {
            while (true) {
                // producing a message to send to the consumer
//                putMessage();
                // producer goes to sleep when the queue is full
                sleep(1000);
            }
        }
        catch (InterruptedException e) {
        }
    }
    //    synchronized(OperateEndCount){
//        while(OperateEndCount.intValue()<this.totalAgentCount.intValue())
//            try {
//                OperateEndCount.wait();
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//    }
    public synchronized void makeTrue(int node)
    {
        this.isCurrentIterDone[node] = true;
    }
    public synchronized void makeFalse(int node)
    {
        this.isCurrentIterDone[node] = false;
    }
    public synchronized void checkAllTrue(int callingNode) throws InterruptedException {
        if(!isAllTrue(isCurrentIterDone))
        {
            wait();
        }

        notifyAll();
    }

    public synchronized boolean isAllTrue(boolean[] array)
    {
        for(boolean b : isCurrentIterDone)
        {

            if(!b)
            {
                return false;
            }
        }
        return true;
    }

    public synchronized boolean isAllFalse(boolean[] array)
    {
        for(boolean b : isCurrentIterDone)
        {
            if(b)
            {
                return false;
            }
        }
        return true;
    }

    public synchronized void startNewIter() throws InterruptedException {
//        if(!isAllTrue(isCurrentIterDone) && !isAllFalse(isCurrentIterDone))
//        {
//            wait();
//
//        }
        Arrays.fill(isCurrentIterDone, false);
//        notifyAll();
    }

    public synchronized void putMessage(Message msg)
            throws InterruptedException
    {

        // checks whether the queue is full or not
        while (messages.size() == MAX_MSG)
        {
//            System.out.println("Waiting on putmsg "+ msg.getSenderId() + " = "+ messages.size()+" " + MAX_MSG);
            // waits for the queue to get empty
            wait();
        }

        // then again adds element or messages

        messages.addElement(msg);
//        System.out.println(messages);
        notifyAll();
    }

    public synchronized Message getMessage()
            throws InterruptedException
    {

        while (messages.size() == 0)
        {
//            System.out.println("Waiting on rcvmsg");

            wait();
        }
//        System.out.println("getmsg " + messages.toString());
        Message message = (Message) messages.firstElement();

        // extracts the message from the queue
        messages.removeElement(message);
//        System.out.println("after getmsg " + messages.toString());

        notifyAll();

        return message;
    }

    public synchronized void putCostMessage(Message msg)
            throws InterruptedException
    {

        // checks whether the queue is full or not
        while (costMessages.size() == MAX_MSG)
        {
//            System.out.println("Waiting on cost putmsg "+ msg.getSenderId() + " = "+ costMessages.size()+" " + MAX_MSG);
            // waits for the queue to get empty
            wait();
        }

        // then again adds element or messages

        costMessages.addElement(msg);
//        System.out.println(messages);
        notifyAll();
    }

    public synchronized Message getCostMessage()
            throws InterruptedException
    {

        while (costMessages.size() == 0)
        {
//            System.out.println("Waiting on cost rcvmsg");
            wait();
        }
//        System.out.println("get costmsg " + costMessages.toString());
        Message costmessage = (Message) costMessages.firstElement();

        // extracts the message from the queue
        costMessages.removeElement(costmessage);
//        System.out.println("after getmsg " + costMessages.toString());

        notifyAll();

        return costmessage;
    }



    public synchronized void putBestMessage(Message msg)
            throws InterruptedException
    {

        // checks whether the queue is full or not
        while (bestMessages.size() == MAX_MSG)
        {
//            System.out.println("Waiting on best putmsg "+ msg.getSenderId() + " = "+ bestMessages.size()+" " + MAX_MSG);
            // waits for the queue to get empty
            wait();
        }

        // then again adds element or messages

        bestMessages.addElement(msg);
        notifyAll();
    }

    public synchronized Message getBestMessage()
            throws InterruptedException
    {

        while (bestMessages.size() == 0)
        {
            wait();
        }
        Message bestmessage = (Message) bestMessages.firstElement();

        // extracts the message from the queue
        bestMessages.removeElement(bestmessage);

        notifyAll();

        return bestmessage;
    }


}