package com.pipixing.sns.dao;

import com.pipixing.sns.model.Feed;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FeedDAO {
    String TABLE_NAME = "feed";
    String INSERT_FIELDS = "type, user_id, created_date, data";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into", TABLE_NAME, " ( ", INSERT_FIELDS,
            " ) values (#{type},#{userId},#{createdDate},#{data})"})
    int addFeed(Feed feed);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, "where id=#{feedId}"})
    Feed getFeedById(int feedId);

    //查找当前关注的所有用户产生的Feed
    List<Feed> getFeedsByFollow(int maxId, List<Integer> followeeIds, int count);
}
