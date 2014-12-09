import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class Wrapper {
	
	private Wrapper() {}	
	
	public static void plotTopUsers(Database db, Charting charting) {
		Map<String, Integer> users = db.getTopDocs("User", "MentionCount", "ScreenName", 10);
		charting.plotBarChart(users, "Most Popular User on Twitter", "User Screen Name", "User Mention Count", "images/tops/top_users.png");
	}
	
	public static Map<String, Integer> plotTopHashTags(Database db, Charting charting) {
		Map<String, Integer> hashtags = db.getTopDocs("HashTag", "HashTagCount", "HashTagName", 11);
		charting.plotBarChart(hashtags, "Trending Hashtags on Twitter", "HashTag Name", "HashTag Count", "images/tops/top_hashtags.png");
		for(String ht : hashtags.keySet()) {
			plotLangsDistro(db, charting, ht, true);
			plotLangsDistro(db, charting, ht, false);
			plotHashtagTweetersDistro(db, charting, ht);
			plotTopHashTagUsers(db, charting, ht, 10);
			plotLocsOnMap(db, charting, ht);
		}
		return hashtags;
	}
	
	private static String getHighestLang(Map<String, Integer> Languages) {
		String lang = "";
		int count = Integer.MIN_VALUE;
		for(Entry<String, Integer> entry : Languages.entrySet()) {
			if (entry.getValue() > count) {
				lang = entry.getKey();
				count = entry.getValue();
			}
		}
		return lang;
	}
	
	public static void plotLangsDistro(Database db, Charting charting, String hashtag, boolean lang) {
		Map<String,Integer> langs;
		String chartName;
		if (lang) {
			langs = db.getLangDistro(hashtag, 4);
			chartName = "images/lang_distro/"+hashtag+"_top_4_langs_.png";
		}
		else {
			langs = db.getLangDistro(hashtag, db.LANGS_COUNT);
			langs.remove(getHighestLang(langs));
			chartName = "images/lang_distro/"+hashtag+"all_langs_but_top.png";
		}
		charting.plotPieChart(langs, "Distribution of Languages for #"+hashtag, chartName);
	}
	
	public static void plotLocsOnMap(Database db, Charting charting, String hashtag) {
		List<String> locs = db.getArray("HashTag", "HashTagName", hashtag, "Locations");
		charting.openGMaps(locs);
	}
	
	public static void plotHashtagTweetersDistro(Database db, Charting charting, String hashtag) {
		Map<String,Integer> tweeters = db.getAllCountDocs("HashTag", "HashTagName", hashtag, "HashTagUsersCount");
		Map<Integer,Integer> freqs = new HashMap<Integer,Integer>();
		for(Entry<String, Integer> entry : tweeters.entrySet()) {
			if (!freqs.containsKey(entry.getValue()))
				freqs.put(entry.getValue(), 1);
			else
				freqs.put(entry.getValue(), freqs.get(entry.getValue())+1);
		}
		charting.plotLineChart(freqs, "Numer of Tweets with #"+hashtag+" by Screen Name", "Screen Name Frequency", 
								"Number of Tweets with #"+hashtag, "images/freqs/"+hashtag+"_tweeter_freqs.png");
	}
	
	public static void plotTopHashTagUsers(Database db, Charting charting, String hashtag, int count) {
		Map<String, Integer> users = db.getTopArrayDocs("HashTag", "HashTagName", hashtag, "HashTagUsersCount", "Name", count);
		charting.plotBarChart(users, "Top "+count+" #"+hashtag+" tweeters", "User Screen Name", "Tweeting Count", "images/tops/top_"+hashtag+"_tweeters"+".png");
	}
	
	public static void main(String[] args) {
		Database db = new Database();
		
		db.createDB(args[0], Integer.parseInt(args[1]), args[2]);
		
		//Create Database for the first time
		/*
			db.createCollection("User");
			db.createUniqueIndex("User", "ScreenName");
			
			db.createCollection("HashTag");
			db.createUniqueIndex("HashTag", "HashTagName");
			
			db.createCollection("Link");
			db.createUniqueIndex("Link", "LinkString");
		*/
		
		//Call Twitter streaming client and store the metadata in the database 
		/*
		 	Streamer twitterStreamer = new Streamer();
			twitterStreamer.grabber(db);
		 */
		
		//Run Apache Hadoop to do calculation on the database
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
		
		//Finally, plot the data
		/*
		   Charting charting = new Charting();
			plotTopUsers(db, charting);
			plotTopHashTags(db, charting);
		 */
	}

}
