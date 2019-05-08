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
        //对于关注user的操作，取的是被关注实体的follower
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        //对于关注user的操作，取的是粉丝的关注对象
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
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return getIdsfromSet(jedisAdapter.zrevrange(followeeKey, offset, offset + limit));
    }

    private List<Integer> getIdsfromSet(Set<String> followerIds) {
        List<Integer> Ids = new ArrayList<>();
        for (String followerId : followerIds) {
            Ids.add(Integer.parseInt(followerId));
        }
        return Ids;
    }

    //返回粉丝的数量
    public long getFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zcard(followerKey);
    }

    //返回该用户关注的实体的数量
    public long getFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return jedisAdapter.zcard(followeeKey);
    }

    //是不是查看用户的粉丝
    public boolean isFollower(int userId, int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        //sismember是用于set数据类型的饿，zscore是用于sort Set的
        return jedisAdapter.zscore(followerKey, String.valueOf(userId)) != null;
    }
}
