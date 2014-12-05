
public final class Wrapper {

	static String SERVER_NAME = "localhost";
	static String DATABASE_NAME = "TweetsDB";
	static int PORT = 27017;
	
	private Wrapper() {}	
	
	
	
	public static void main(String[] args) {
		Database db = new Database();
		
		db.createDB(SERVER_NAME, PORT, DATABASE_NAME);
		
		db.createCollection("User");
		db.createUniqueIndex("User", "ScreenName");
		
		db.createCollection("HashTag");
		db.createUniqueIndex("HashTag", "HashTagName");
		
		db.createCollection("Link");
		db.createUniqueIndex("Link", "LinkString");
		
		Streamer twitterStreamer = new Streamer();
		twitterStreamer.grabber(db);
		
		
		
		
		
		//db.getFlatFile("User", "ScreenName", "MentionedBy", "MentionedBy.txt");
		//db.writeBackToDB(args[0], args[1], args[2]);
		db.getAllSorted(args[0], args[1], args[2]);
		System.out.println("Done!");
		/*
		Database db = new Database();
		
		
		*/
	}

}
