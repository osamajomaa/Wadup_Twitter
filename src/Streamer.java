import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import twitter4j.HashtagEntity;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;


public class Streamer {

	public List<String> getAllMentions(UserMentionEntity[] mentionedUsers) {
		List<String> mentions = new ArrayList<String>();
		for(int i=0; i<mentionedUsers.length; i++)
			mentions.add(mentionedUsers[0].getScreenName());
		return mentions;
	}
	
	public List<String> getAllURLs(URLEntity[] EntityURLs) {
		List<String> urls = new ArrayList<String>();
		for(int i=0; i<EntityURLs.length; i++)
			urls.add(EntityURLs[0].getURL());
		return urls;
	}
	
	public Set<String> getAllHashTags(HashtagEntity[] EntityTags) {
		Set<String> hashtags = new HashSet<String>();
		for(int i=0; i<EntityTags.length; i++)
			hashtags.add(EntityTags[0].getText().toLowerCase());
		return hashtags;
	}
	
	public void addUserToDB(User user, List<String> mentions, database db) {
		db.addUser(user.getName(), user.getScreenName());
		for(String mentionee : mentions) 
			db.addMentionedBy(mentionee, user.getName());
	}
	
	public void addHashTagToDB(Set<String> hashTags, User user, String lang, database db) {
		for(String hashtag : hashTags) {
			db.addHashTag(hashtag);
			Set<String> coOccurTags = new HashSet<String>(hashTags);
			coOccurTags.remove(hashtag);
			db.addCoOccuringHashTag(hashtag, coOccurTags);
			db.adduserUsingHashTag(hashtag, user.getScreenName());
			db.IncrementLang(lang, hashtag);
		}
	}
	
	public void addLinkToDB(User user, List<String> links, database db) {
		for(String link : links) {
			db.addLink(link);
			db.addLinkUsedBy(user.getScreenName(), link);
		}
	}
	
	public void grabber(final database db) {
		TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
            	User user = status.getUser();
            	Set<String> hashTags = getAllHashTags(status.getHashtagEntities());
            	List<String> urls = getAllURLs(status.getURLEntities());
            	List<String> mentions = getAllMentions(status.getUserMentionEntities());
            	addUserToDB(user, mentions, db);
            	addHashTagToDB(hashTags, user, status.getLang(), db);
            	addLinkToDB(user, urls, db);
                //System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
        twitterStream.addListener(listener);
        twitterStream.sample();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
