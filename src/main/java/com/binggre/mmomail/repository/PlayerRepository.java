package com.binggre.mmomail.repository;

import com.binggre.binggreapi.utils.file.FileManager;
import com.binggre.mmomail.objects.PlayerMail;
import com.binggre.mongolibraryplugin.base.MongoCachedRepository;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;

public class PlayerRepository extends MongoCachedRepository<UUID, PlayerMail> {

    public PlayerRepository(Plugin plugin, String database, String collection, Map<UUID, PlayerMail> cache) {
        super(plugin, database, collection, cache);
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
            saveAsync(playerMail);
        }
        return playerMail;
    }

    public void init() {
        cache.clear();

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            PlayerMail playerMail = findById(player.getUniqueId());
            playerMail = init(player, playerMail);
            putIn(playerMail);
        }
    }
}