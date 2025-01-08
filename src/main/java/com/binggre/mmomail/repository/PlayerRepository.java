package com.binggre.mmomail.repository;

import com.binggre.binggreapi.utils.file.FileManager;
import com.binggre.mmomail.objects.PlayerMail;
import com.binggre.mongolibraryplugin.base.MongoCachedRepository;
import com.binggre.velocitysocketclient.VelocityClient;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import redis.clients.jedis.Jedis;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerRepository extends MongoCachedRepository<UUID, PlayerMail> {

    private final String PREFIX = "PlayerMail:";

    public PlayerRepository(Plugin plugin, String database, String collection, Map<UUID, PlayerMail> cache) {
        super(plugin, database, collection, cache);
    }

    private Jedis resource() {
        return VelocityClient.getInstance().getResource();
    }

    @Override
    public PlayerMail get(UUID uuid) {
        try (Jedis jedis = resource()) {
            String json = jedis.get(PREFIX + uuid.toString());
            return json == null ? null : FileManager.toObject(json, PlayerMail.class);
        }
    }

    @Override
    public PlayerMail remove(UUID uuid) {
        try (Jedis jedis = resource()) {
            String json = jedis.get(PREFIX + uuid.toString());
            jedis.del(PREFIX + uuid);
            return json == null ? null : FileManager.toObject(json, PlayerMail.class);
        }
    }

    @Override
    public void putIn(PlayerMail entity) {
        try (Jedis jedis = resource()) {
            String json = FileManager.toJson(entity);
            jedis.set(PREFIX + entity.getId(), json);
        }
    }

    @Override
    public Map<UUID, PlayerMail> getCache() {
        Map<UUID, PlayerMail> cache = new HashMap<>();
        for (PlayerMail value : values()) {
            cache.put(value.getId(), value);
        }
        return cache;
    }

    @Override
    public Collection<PlayerMail> values() {
        try (Jedis jedis = resource()) {
            return jedis.keys(PREFIX + "*").stream()
                    .map(key -> FileManager.toObject(jedis.get(key), PlayerMail.class))
                    .collect(Collectors.toList());
        }
    }

    public void saveAll() {
        for (PlayerMail playerMail : values()) {
            save(playerMail);
        }
    }

    @Override
    public Document toDocument(PlayerMail playerMail) {
        String json = FileManager.toJson(playerMail);
        Document parse = Document.parse(json);
        parse.put(ID_FILED, playerMail.getId().toString());
        return parse;
    }

    @Override
    public PlayerMail toEntity(Document document) {
        return FileManager.toObject(document.toJson(), PlayerMail.class);
    }

    public PlayerMail init(Player player, PlayerMail playerMail) {
        if (playerMail == null) {
            playerMail = new PlayerMail(player);
            save(playerMail);
        }
        return playerMail;
    }

    public void init() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            UUID uniqueId = player.getUniqueId();
            remove(uniqueId);

            PlayerMail playerMail = findById(uniqueId);
            playerMail = init(player, playerMail);
            putIn(playerMail);
        }
    }
}