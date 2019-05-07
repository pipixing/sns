package com.pipixing.sns.service;

import com.pipixing.sns.model.EntityType;
import com.pipixing.sns.utils.JedisAdapter;
import com.pipixing.sns.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class FollowService {
    @Autowired
    JedisAdapter jedisAdapter;

    //关注
    public boolean follow(int userId, int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
        //创建一个事务来保证操作的原子性
        Transaction tx = jedisAdapter.multi(jedis);
        //在实体被关注的列表加上用户名
        tx.zadd(followerKey, date.getTime(), String.valueOf(userId));
        //在用户关注的某一类实体里加上这种实体的id
        tx.zadd(followeeKey, date.getTime(), String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(tx, jedis);
        return ret.size() == 2 && (long) ret.get(0) > 0 && (long) ret.get(1) > 0;
    }

    //取消关注
    public boolean unfollow(int userId, int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
        //创建一个事务来保证操作的原子性
        Transaction tx = jedisAdapter.multi(jedis);
        //在实体被关注的列表删除用户名
        tx.zrem(followerKey, String.valueOf(userId));
        //在用户关注的某一类实体里删除这种实体的id
        tx.zrem(followeeKey, String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(tx, jedis);
        return ret.size() == 2 && (long) ret.get(0) > 0 && (long) ret.get(1) > 0;
    }

    //返回该实体前count个的粉丝
    public List<Integer> getFollowerList(int entityType, int entityId, int count) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdsfromSet(jedisAdapter.zrevrange(followerKey, 0, count));
    }

    //返回该实体按照每页的形式返回粉丝数
    public List<Integer> getFollowerList(int entityType, int entityId, int offset, int limit) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdsfromSet(jedisAdapter.zrevrange(followerKey, offset, offset + limit));
    }

    //返回该用户关注的一类实体的count个
    public List<Integer> getFolloweeList(int userId, int entityType, int count) {
        String followerKey = RedisKeyUtil.getFollowerKey(userId, entityType);
        return getIdsfromSet(jedisAdapter.zrevrange(followerKey, 0, count));
    }

    //返回该用户关注的一类实体按照每页的形式
    public List<Integer> getFolloweeList(int userId, int entityType, int offset, int limit) {
        String followerKey = RedisKeyUtil.getFollowerKey(userId, entityType);
        return getIdsfromSet(jedisAdapter.zrevrange(followerKey, offset, offset + limit));
    }

    private List<Integer> getIdsfromSet(Set<String> followerIds) {
        List<Integer> Ids = new ArrayList<>();
        for (String followerId : followerIds) {
            Ids.add(Integer.parseInt(followerId));
        }
        return Ids;
    }
}
