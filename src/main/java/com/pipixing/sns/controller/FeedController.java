package com.pipixing.sns.controller;

import com.pipixing.sns.model.EntityType;
import com.pipixing.sns.model.Feed;
import com.pipixing.sns.model.HostHolder;
import com.pipixing.sns.service.FeedService;
import com.pipixing.sns.service.FollowService;
import com.pipixing.sns.utils.JedisAdapter;
import com.pipixing.sns.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class FeedController {
    @Autowired
    HostHolder hostHolder;
    @Autowired
    FeedService feedService;
    @Autowired
    JedisAdapter jedisAdapter;
    @Autowired
    FollowService followService;

    @GetMapping(value = "/pushfeeds")
    public String getPushFeeds(Model model) {
        int localUserId = hostHolder.getUser() == null ? 0 : hostHolder.getUser().getId();
        String timelineKey = RedisKeyUtil.getTimelineKey(localUserId);
        List<String> feedIds = jedisAdapter.lrange(timelineKey, 0, 20);
        List<Feed> feeds = new ArrayList<>();
        for (String feedId : feedIds) {
            Feed feed = feedService.getFeedById(Integer.parseInt(feedId));
            if (feed != null)
                feeds.add(feed);
        }
        model.addAttribute("feeds", feeds);
        return "feeds";
    }

    @GetMapping(value = "/pullfeeds")
    public String getPullFeeds(Model model) {
        int localUserId = hostHolder.getUser() == null ? 0 : hostHolder.getUser().getId();
        List<Integer> followees = new ArrayList<>();
        if (localUserId != 0)
            followees = followService.getFolloweeList(localUserId, EntityType.ENTITY_USER, 0, 20);
        List<Feed> feeds = feedService.getFeedsByFollow(Integer.MAX_VALUE, followees, 10);
        model.addAttribute("feeds", feeds);
        return "feeds";
    }
}
