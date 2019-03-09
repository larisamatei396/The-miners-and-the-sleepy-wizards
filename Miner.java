import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Class for a miner.
 */
public class Miner extends Thread {

    private Integer hashCount;
    private Set<Integer> solved;
    private CommunicationChannel channel;

    static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private static String encryptMultipleTimes(String input, Integer count) {
		String hashed = input;
		for (int i = 0; i < count; ++i) {
			hashed = encryptThisString(hashed);
		}

		return hashed;
	}

	private static String encryptThisString(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));

			// convert to string
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String hex = Integer.toHexString(0xff & messageDigest[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Creates a {@code Miner} object.
	 * 
	 * @param hashCount
	 *            number of times that a miner repeats the hash operation when
	 *            solving a puzzle.
	 * @param solved
	 *            set containing the IDs of the solved rooms
	 * @param channel
	 *            communication channel between the miners and the wizards
	 */
	public Miner(Integer hashCount, Set<Integer> solved, CommunicationChannel channel) {
        this.hashCount = hashCount;
        this.solved = solved;
        this.channel = channel;
	}

	@Override
	public void run() {
	    while (true) {
	        Message firstWizardMessage = channel.getMessageWizardChannel();
	        Message secondWizardMessage = channel.getMessageWizardChannel();
	        Message minerMessage;
	        int currentRoom, parentRoom;
	        String hashed;

	        if (!solved.contains(secondWizardMessage.getCurrentRoom())) {
	            hashed = encryptMultipleTimes(secondWizardMessage.getData(), hashCount);
	            currentRoom = secondWizardMessage.getCurrentRoom();
	            parentRoom = firstWizardMessage.getCurrentRoom();

	            synchronized (solved) {
                    solved.add(currentRoom);
                }

	            minerMessage = new Message(parentRoom, currentRoom, hashed);
	            channel.putMessageMinerChannel(minerMessage);
	        }
	    }
	}
}