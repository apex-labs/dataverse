package org.apex.dataverse.port.core.redis;

import lombok.extern.slf4j.Slf4j;
import org.apex.dataverse.port.core.exception.NoPortNodeException;
import org.apex.dataverse.port.core.node.EngineNode;
import org.apex.dataverse.core.util.ObjectMapperUtil;
import org.apex.dataverse.port.core.node.PortNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Danny.Huo
 * @date 2023/6/1 19:03
 * @since 0.1.0
 */
@Slf4j
@Service
public class RedisRegistryService {

    /**
     * RedisTemplate
     * Autowired by Setter
     */
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Set key and value without expire time
     * @param key String , key
     * @param v Object , value
     */
    public void set(String key, Object v) {
        this.set(key, v, null);
    }

    /**
     * Set key and value with expire time
     * @param key String , key
     * @param v Object, value
     * @param expire long, expire time
     */
    public void set(String key, Object v, Long expire) {
        if (null == expire) {
            redisTemplate.opsForValue().set(key, v);
            return;
        }
        redisTemplate.opsForValue().set(key, v, expire, TimeUnit.MILLISECONDS);
    }

    /**
     * 设置过期时间
     *
     * @param key String ,key
     * @param expire long, expire time
     */
    public void setExpire(String key, Long expire) {
        redisTemplate.expire(key, expire, TimeUnit.MILLISECONDS);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

//    private final static String ENGINE_KEY = "engine:%s";
//
//    private final static String PORT_KEY = "odpc:%s";

    /**
     * Register engine
     *
     * @param engineNode EngineNode
     * @param expire Long, expire time
     * @throws JsonProcessingException JsonProcessingException
     */
    public void registryEngine(EngineNode engineNode, Long expire) throws JsonProcessingException {
        String engine = ObjectMapperUtil.toJson(engineNode);
        String key = String.format(engineNode.getRegistryCode(), engineNode.getEngineId());
        if (null == expire) {
            this.set(key, engine);
        }
        this.set(key, engine, expire);
    }

    /**
     * Register engine
     *
     * @param engineNode EngineNode
     * @throws JsonProcessingException JsonProcessingException
     */
    public void registryEngine(EngineNode engineNode) throws JsonProcessingException {
        this.registryEngine(engineNode, null);
    }

    /**
     * 注册Adaptor
     *
     * @param portNode AdaptorNode
     * @throws JsonProcessingException JsonProcessingException
     */
    public void registryPort(PortNode portNode) throws JsonProcessingException {
        this.registryPort(portNode, null);
    }

    /**
     * 注册Adaptor
     *
     * @param portNode AdaptorNode
     * @param expire Long, expire time
     * @throws JsonProcessingException JsonProcessingException
     */
    public void registryPort(PortNode portNode, Long expire) throws JsonProcessingException {
        String engine = ObjectMapperUtil.toJson(portNode);
        if (null == expire) {
            this.set(portNode.getRegistryCode(), engine);
        }
        this.set(portNode.getRegistryCode(), engine, expire);
    }

    /**
     * Port heartbeat
     * @param portNode PortNode
     * @param expire Long, expire time
     * @throws JsonProcessingException JsonProcessingException
     */
    public void portHeartbeat(PortNode portNode, Long expire) throws JsonProcessingException {
        String engine = ObjectMapperUtil.toJson(portNode);
        if (null == expire) {
            this.set(portNode.getRegistryCode(), engine);
        }
        this.set(portNode.getRegistryCode(), engine, expire);
    }

    /**
     * Get port node by port id
     * @param portNode AdaptorNode
     * @return AdaptorNode
     * @throws JsonProcessingException JsonProcessingException
     */
    public PortNode getPortNode(PortNode portNode) throws JsonProcessingException {
        Object o = this.redisTemplate.opsForValue().get(portNode.getRegistryCode());
        if (null != o) {
            return ObjectMapperUtil.toObject(o.toString(), PortNode.class);
        }
        return null;
    }

    /**
     * 查找port
     * @param key String , key
     * @return List<AdaptorNode>
     * @throws JsonProcessingException JsonProcessingException
     * @throws NoPortNodeException NoAdaptorException
     */
    public List<PortNode> findPort(String key)
            throws JsonProcessingException, NoPortNodeException {
        Set<String> keys = this.redisTemplate.keys(key);
        if (null == keys || keys.isEmpty()) {
            throw NoPortNodeException.newException();
        }

        List<Object> values = this.redisTemplate.opsForValue().multiGet(keys);
        if (null == values || values.isEmpty()) {
            throw NoPortNodeException.newException();
        }

        List<PortNode> nodes = new ArrayList<>(values.size());
        for (Object v : values) {
            PortNode odpcNode = ObjectMapperUtil.toObject(v.toString(), PortNode.class);
            nodes.add(odpcNode);
        }
        return nodes;
    }

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
