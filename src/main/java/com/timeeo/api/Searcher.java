package com.timeeo.api;

import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramTagFeedRequest;
import org.brunocvcunha.instagram4j.requests.payload.InstagramFeedItem;
import org.brunocvcunha.instagram4j.requests.payload.InstagramFeedResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Searcher {

    private Instagram4j instagram;

    public Searcher(String username, String password) throws IOException {
        instagram = Instagram4j.builder().username(username).password(password).build();
        instagram.setup();
        instagram.login();
    }

    public Map<String, Map<String, Integer>> searchTags(String hashtag) throws IOException {
        Map<String, Map<String, Integer>> locationTagMap = new HashMap<>();

        InstagramFeedResult feed = instagram.sendRequest(new InstagramTagFeedRequest(hashtag));

        // Searches the feed
        for (InstagramFeedItem item : feed.getItems()) {

            // Handle if the text is in caption or first preview comment..
            String text = "";

            if (item.getCaption() == null){
                if (item.getComment_count() >= 1)
                    text = item.getPreview_comments().get(0).getText();
                else System.out.println("ERROR... THE FUCK IS GOING ON HERE PLZ");
            } else
                text =  item.getCaption().getText();

            // Get all tags from each item
            Pattern pattern = Pattern.compile("/#([A-Z])*\\w+/g");
            Matcher matcher = pattern.matcher(text);

            while (matcher.find()) {
                String tag = matcher.group();
                System.out.println("TAG = " + tag);

                // Skip the searched tag as we are looking for related ones
                if (tag.equals(hashtag)) continue;

                String location = "";

                if (item.getLocation() != null)
                    location = item.getLocation().getName();

                // If the tag was already found
                if (locationTagMap.containsKey(tag)) {
                    Map<String, Integer> submap = locationTagMap.get(tag);

                    // If the location was already found for this tag
                    if (submap.containsKey(location))
                        submap.put(location, submap.get(location) + 1);
                    else
                        submap.put(location, 1);
                }

                // If the tag wasnt found
                else {
                    // Add new entry with the location
                    Map<String, Integer> submap = new HashMap<>();
                    submap.put(location, 1);
                    locationTagMap.put(tag, submap);
                }
            }
        }

        return locationTagMap;
    }
}
