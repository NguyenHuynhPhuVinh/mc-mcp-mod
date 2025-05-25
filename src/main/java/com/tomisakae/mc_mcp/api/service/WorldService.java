package com.tomisakae.mc_mcp.api.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.Collection;
import java.lang.Iterable;

/**
 * Service xử lý các thao tác liên quan đến thế giới
 */
public class WorldService {

    /**
     * Lấy thông tin chung về thế giới
     * 
     * @param server Server Minecraft
     * @return Thông tin thế giới dưới dạng JsonObject
     */
    public JsonObject getWorldInfo(MinecraftServer server) {
        JsonObject worldInfo = new JsonObject();
        
        // Thông tin chung về server
        JsonObject serverInfo = new JsonObject();
        serverInfo.addProperty("version", server.getVersion());
        serverInfo.addProperty("motd", server.getServerMotd());
        serverInfo.addProperty("maxPlayers", server.getMaxPlayerCount());
        serverInfo.addProperty("onlinePlayers", server.getCurrentPlayerCount());
        
        // Danh sách người chơi đang online
        JsonArray players = new JsonArray();
        server.getPlayerManager().getPlayerList().forEach(player -> {
            players.add(player.getName().getString());
        });
        serverInfo.add("players", players);
        
        worldInfo.add("server", serverInfo);
        
        // Thông tin về các thế giới
        JsonArray worlds = new JsonArray();
        // Sử dụng Iterable thay vì Collection vì getWorlds() trả về Iterable
        Iterable<ServerWorld> serverWorlds = server.getWorlds();
        
        for (ServerWorld world : serverWorlds) {
            JsonObject worldData = new JsonObject();
            
            // Thông tin cơ bản
            worldData.addProperty("name", world.toString());
            // Sử dụng getRegistryKey() thay vì getDimensionKey()
            worldData.addProperty("dimension", world.getRegistryKey().getValue().toString());
            worldData.addProperty("time", world.getTimeOfDay());
            worldData.addProperty("dayTime", world.getTime());
            worldData.addProperty("isDay", world.isDay());
            worldData.addProperty("isNight", !world.isDay());
            worldData.addProperty("isRaining", world.isRaining());
            worldData.addProperty("isThundering", world.isThundering());
            worldData.addProperty("moonPhase", world.getLunarTime() % 8);
            worldData.addProperty("difficulty", getDifficultyName(world.getDifficulty()));
            
            // Game rules
            JsonObject gameRules = new JsonObject();
            GameRules rules = world.getGameRules();
            
            // Thêm một số game rule phổ biến
            gameRules.addProperty("doDaylightCycle", rules.getBoolean(GameRules.DO_DAYLIGHT_CYCLE));
            gameRules.addProperty("doWeatherCycle", rules.getBoolean(GameRules.DO_WEATHER_CYCLE));
            gameRules.addProperty("doMobSpawning", rules.getBoolean(GameRules.DO_MOB_SPAWNING));
            gameRules.addProperty("doMobLoot", rules.getBoolean(GameRules.DO_MOB_LOOT));
            gameRules.addProperty("doTileDrops", rules.getBoolean(GameRules.DO_TILE_DROPS));
            gameRules.addProperty("keepInventory", rules.getBoolean(GameRules.KEEP_INVENTORY));
            gameRules.addProperty("doFireTick", rules.getBoolean(GameRules.DO_FIRE_TICK));
            gameRules.addProperty("mobGriefing", rules.getBoolean(GameRules.DO_MOB_GRIEFING));
            gameRules.addProperty("naturalRegeneration", rules.getBoolean(GameRules.NATURAL_REGENERATION));
            gameRules.addProperty("pvp", rules.getBoolean(GameRules.DO_IMMEDIATE_RESPAWN));
            
            worldData.add("gameRules", gameRules);
            
            worlds.add(worldData);
        }
        
        worldInfo.add("worlds", worlds);
        
        return worldInfo;
    }
    
    /**
     * Chuyển đổi Difficulty sang tên
     * 
     * @param difficulty Difficulty cần chuyển đổi
     * @return Tên của Difficulty
     */
    private String getDifficultyName(Difficulty difficulty) {
        switch (difficulty) {
            case PEACEFUL:
                return "peaceful";
            case EASY:
                return "easy";
            case NORMAL:
                return "normal";
            case HARD:
                return "hard";
            default:
                return "unknown";
        }
    }
}
