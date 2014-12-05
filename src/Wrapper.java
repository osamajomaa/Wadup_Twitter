
public final class Wrapper {

	static String SERVER_NAME = "localhost";
	static String DATABASE_NAME = "TweetsDB";
	static int PORT = 27017;
	
	private Wrapper() {}	
	
	
	
	public static void main(String[] args) {
		Database db = new Database();
		
		db.createDB(SERVER_NAME, PORT, DATABASE_NAME);
		
		/*
		db.createCollection("User");
		db.createUniqueIndex("User", "ScreenName");
		
		db.createCollection("HashTag");
		db.createUniqueIndex("HashTag", "HashTagName");
		
		db.createCollection("Link");
		db.createUniqueIndex("Link", "LinkString");
		
		Streamer twitterStreamer = new Streamer();
		twitterStreamer.grabber(db);
		*/
		
		/*
		String dbFile = "DBFiles/MentionedBy.db";
		String newField = "MentionedByCount";
		String idName = "ScreenName";
		String hdfsFile = "HDFS/MentionedBy.hdfs";
		String countField = "MentionCount";
		String collName = "User";
		
		HDFS hdfs = new HDFS(db.db, idName, countField, newField, dbFile, hdfsFile, collName);
		hdfs.writeDataToFile();
		TweetFieldCount fieldCounter = new TweetFieldCount();
		
		String hadoopInput = "HDFS/input";
		String hadoopOutput = "HDFS/output";
		fieldCounter.run(hadoopOutput, hadoopInput);
		
		hdfs.writeBackToDB();
		*/
		
	}

}
