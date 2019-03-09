import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class that implements the channel used by wizards and miners to communicate.
 */
public class CommunicationChannel {
	/**
	 * Creates a {@code CommunicationChannel} object.
	 */
	public CommunicationChannel() {
	}

    static LinkedBlockingQueue<Message> minerMessage = new LinkedBlockingQueue();
    static LinkedBlockingQueue<Message> wizardMessage = new LinkedBlockingQueue();
    static ReentrantLock firstLock = new ReentrantLock();
    static ReentrantLock secondLock = new ReentrantLock();

    /**
	 * Puts a message on the miner channel (i.e., where miners write to and wizards
	 * read from).
	 * 
	 * @param message
	 *            message to be put on the channel
	 */
	public void putMessageMinerChannel(Message message) {
        try {
            minerMessage.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

	/**
	 * Gets a message from the miner channel (i.e., where miners write to and
	 * wizards read from).
	 * 
	 * @return message from the miner channel
	 */
	public Message getMessageMinerChannel() {
        Message message = null;
        try {
            message = minerMessage.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return message;
	}

	/**
	 * Puts a message on the wizard channel (i.e., where wizards write to and miners
	 * read from).
	 * 
	 * @param message
	 *            message to be put on the channel
	 */
	public void putMessageWizardChannel(Message message) {
        if (message.getData() != "END" && message.getData() != "EXIT") {
            firstLock.lock();
            try {
                wizardMessage.put(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (firstLock.getHoldCount() == 2) {
                    firstLock.unlock();
                    firstLock.unlock();
            }
        }
    }

	/**
	 * Gets a message from the wizard channel (i.e., where wizards write to and
	 * miners read from).
	 * 
	 * @return message from the miner channel
	 */
	public Message getMessageWizardChannel() {
        Message message = null;
        secondLock.lock();
        try {
            message = wizardMessage.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (secondLock.getHoldCount() == 2) {
            secondLock.unlock();
            secondLock.unlock();
        }
        return message;
	}
}
