package com.pipixing.sns.dao;

import com.pipixing.sns.model.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface MessageDAO {
    String TABLE_NAME = "message";
    String INSERT_FIELDS = "from_id, to_id, content, created_date, has_read, conversation_id";
    String SELECT_FIELDS = "id" + INSERT_FIELDS;

    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{fromId},#{toId},#{content},#{createdDate},#{hasRead},#{conversationId})"})
    int addMessage(Message message);

    // 注意这里把count(id)的值设到了id字段
    @Select({"select ", INSERT_FIELDS, " , count(id) as id from ( select * from ", TABLE_NAME,
            " where from_id=#{userId} or to_id=#{userId} order by created_date desc) tt group by conversation_id order by created_date desc limit #{offset}, #{limit}"})
    List<Message> getMessageList(int userId,int offset,int limit);

    @Select({"select count(id) from ", TABLE_NAME, " where has_read=0 and to_id=#{userId} and conversation_id=#{conversationId}"})
    int getUnreadConversationCount(int userId,String conversationId);
}
