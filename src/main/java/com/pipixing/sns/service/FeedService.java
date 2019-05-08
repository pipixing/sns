package com.pipixing.sns.service;

import com.pipixing.sns.dao.FeedDAO;
import com.pipixing.sns.model.Feed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedService {
    @Autowired
    FeedDAO feedDAO;

    public int addFeed(Feed feed) {
        return feedDAO.addFeed(feed);
    }

    public Feed getFeedById(int feedId) {
        return  feedDAO.getFeedById(feedId);
    }

    public List<Feed> getFeedsByFollow(int maxId, List<Integer> followees, int count) {
        return feedDAO.getFeedsByFollow(maxId,followees,count);
    }
}
