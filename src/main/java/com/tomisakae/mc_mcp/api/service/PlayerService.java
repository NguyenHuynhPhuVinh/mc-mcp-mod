package com.tomisakae.mc_mcp.api.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tomisakae.mc_mcp.Mcmcpmod;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.registry.Registries;
import net.minecraft.world.GameMode;
import net.minecraft.advancement.AdvancementDisplay;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service xử lý các thao tác liên quan đến người chơi
 */
public class PlayerService {

    /**
     * Lấy thông tin chi tiết về người chơi
     * 
     * @param playerName Tên người chơi
     * @param server     Server Minecraft
     * @return Thông tin người chơi dưới dạng JsonObject
     */
    public JsonObject getPlayerInfo(String playerName, MinecraftServer server) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
        if (player == null) {
            return null;
        }

        JsonObject playerInfo = new JsonObject();
        
        // Thông tin cơ bản
        playerInfo.addProperty("name", player.getName().getString());
        playerInfo.addProperty("uuid", player.getUuidAsString());
        
        // Thông tin vị trí
        JsonObject position = new JsonObject();
        position.addProperty("x", player.getX());
        position.addProperty("y", player.getY());
        position.addProperty("z", player.getZ());
        position.addProperty("dimension", player.getWorld().getRegistryKey().getValue().toString());
        playerInfo.add("position", position);
        
        // Thông tin trạng thái
        JsonObject status = new JsonObject();
        status.addProperty("health", player.getHealth());
        status.addProperty("maxHealth", player.getMaxHealth());
        status.addProperty("food", player.getHungerManager().getFoodLevel());
        status.addProperty("saturation", player.getHungerManager().getSaturationLevel());
        status.addProperty("experienceLevel", player.experienceLevel);
        status.addProperty("experienceProgress", player.experienceProgress);
        status.addProperty("gamemode", getGameModeName(player.interactionManager.getGameMode()));
        status.addProperty("isCreative", player.getAbilities().creativeMode);
        status.addProperty("isFlying", player.getAbilities().flying);
        status.addProperty("isSleeping", player.isSleeping());
        status.addProperty("isOnFire", player.isOnFire());
        status.addProperty("isInWater", player.isSubmergedInWater());
        status.addProperty("isInLava", player.isInLava());
        playerInfo.add("status", status);

        return playerInfo;
    }

    /**
     * Lấy thông tin về các block xung quanh người chơi
     * 
     * @param playerName         Tên người chơi
     * @param radius             Bán kính quét ngang
     * @param server             Server Minecraft
     * @param verticalRadius     Bán kính quét dọc
     * @param includeCommonBlocks Có bao gồm các block phổ biến không
     * @return Thông tin tổng hợp về các block xung quanh dưới dạng JsonObject
     */
    public JsonObject getPlayerSurroundings(String playerName, int radius, MinecraftServer server, int verticalRadius, boolean includeCommonBlocks) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
        if (player == null) {
            return null;
        }

        JsonObject surroundings = new JsonObject();
        ServerWorld world = player.getServerWorld();
        BlockPos playerPos = player.getBlockPos();
        
        // Tạo một bản đồ tần suất của các loại block
        JsonObject blockFrequency = new JsonObject();
        // Lưu trữ block đáng chú ý (không phải không khí, đất, đá)
        JsonArray notableBlocks = new JsonArray();
        
        // Danh sách các block phổ biến không cần liệt kê chi tiết
        java.util.Set<String> commonBlocks = new java.util.HashSet<>();
        
        // Chỉ thêm các block phổ biến vào danh sách nếu không bao gồm chúng
        if (!includeCommonBlocks) {
            commonBlocks.add("minecraft:air");
            commonBlocks.add("minecraft:stone");
            commonBlocks.add("minecraft:dirt");
            commonBlocks.add("minecraft:grass_block");
            commonBlocks.add("minecraft:water");
            commonBlocks.add("minecraft:sand");
            commonBlocks.add("minecraft:gravel");
        }
        
        int totalBlocksScanned = 0;
        
        // Sử dụng bán kính ngang và dọc được cung cấp
        int horizontalRadius = radius;
        // Nếu không có bán kính dọc, sử dụng gấp đôi bán kính ngang
        if (verticalRadius <= 0) {
            verticalRadius = radius * 2;
        }
        
        // Tạo các biến để lưu trữ thông tin về các tầng
        JsonObject layerInfo = new JsonObject();
        int currentLayer = 0;
        String currentLayerType = "unknown";
        int skyLayerStart = -1;
        int groundLayerStart = -1;
        int undergroundLayerStart = -1;
        int bedrockLayerStart = -1;
        
        // Xác định vị trí hiện tại của người chơi
        int playerY = playerPos.getY();
        
        for (int x = -horizontalRadius; x <= horizontalRadius; x++) {
            for (int y = -verticalRadius; y <= verticalRadius; y++) {
                for (int z = -horizontalRadius; z <= horizontalRadius; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    BlockState blockState = world.getBlockState(pos);
                    Block block = blockState.getBlock();
                    String blockId = Registries.BLOCK.getId(block).toString();
                    
                    // Xác định loại tầng dựa vào độ cao
                    int layerY = pos.getY();
                    if (layerY > playerY + 10 && skyLayerStart == -1) {
                        skyLayerStart = layerY;
                    } else if (layerY < playerY - 10 && layerY > playerY - 30 && groundLayerStart == -1) {
                        groundLayerStart = layerY;
                    } else if (layerY < playerY - 30 && undergroundLayerStart == -1) {
                        undergroundLayerStart = layerY;
                    } else if (layerY < 5 && bedrockLayerStart == -1) {
                        bedrockLayerStart = layerY;
                    }
                    
                    // Tăng tần suất của block này
                    if (blockFrequency.has(blockId)) {
                        int currentCount = blockFrequency.get(blockId).getAsInt();
                        blockFrequency.addProperty(blockId, currentCount + 1);
                    } else {
                        blockFrequency.addProperty(blockId, 1);
                    }
                    
                    // Tính khoảng cách 3D từ người chơi
                    double distance = Math.sqrt(
                        Math.pow(playerPos.getX() - pos.getX(), 2) +
                        Math.pow(playerPos.getY() - pos.getY(), 2) +
                        Math.pow(playerPos.getZ() - pos.getZ(), 2)
                    );
                    
                    // Nếu là block đáng chú ý (không phải block phổ biến) và gần người chơi
                    // Hoặc nếu là block đáng chú ý ở xa nhưng có giá trị cao (như kim cương, emerald...)
                    boolean isValuableBlock = blockId.contains("diamond") || blockId.contains("emerald") || 
                                           blockId.contains("gold") || blockId.contains("ancient_debris") ||
                                           blockId.contains("chest") || blockId.contains("spawner");
                    
                    if ((!commonBlocks.contains(blockId) && distance <= 5) || (isValuableBlock && distance <= 20)) {
                        JsonObject notableBlock = new JsonObject();
                        notableBlock.addProperty("id", blockId);
                        notableBlock.addProperty("name", block.getName().getString());
                        
                        JsonObject position = new JsonObject();
                        position.addProperty("x", pos.getX());
                        position.addProperty("y", pos.getY());
                        position.addProperty("z", pos.getZ());
                        notableBlock.add("position", position);
                        notableBlock.addProperty("distance", distance);
                        
                        // Xác định vị trí tương đối so với người chơi (trên, dưới, ngang hàng)
                        if (pos.getY() > playerPos.getY() + 3) {
                            notableBlock.addProperty("relativePosition", "above");
                        } else if (pos.getY() < playerPos.getY() - 3) {
                            notableBlock.addProperty("relativePosition", "below");
                        } else {
                            notableBlock.addProperty("relativePosition", "same_level");
                        }
                        
                        notableBlocks.add(notableBlock);
                    }
                    
                    totalBlocksScanned++;
                }
            }
        }
        
        // Thêm thông tin tổng hợp
        surroundings.add("blockFrequency", blockFrequency);
        surroundings.add("notableBlocks", notableBlocks);
        surroundings.addProperty("radius", radius);
        surroundings.addProperty("totalBlocksScanned", totalBlocksScanned);
        
        // Thêm thông tin về môi trường xung quanh
        JsonObject environment = new JsonObject();
        environment.addProperty("biome", world.getBiome(playerPos).toString());
        environment.addProperty("isDaytime", world.isDay());
        environment.addProperty("isRaining", world.isRaining());
        environment.addProperty("isThundering", world.isThundering());
        environment.addProperty("skyLight", world.getLightLevel(playerPos));
        surroundings.add("environment", environment);
        
        return surroundings;
    }

    /**
     * Lấy danh sách entity gần người chơi
     * 
     * @param playerName     Tên người chơi
     * @param radius         Bán kính quét
     * @param server         Server Minecraft
     * @param entityType     Loại entity cần lọc (null để lấy tất cả)
     * @param includePassive Có bao gồm entity thụ động không
     * @param includeHostile Có bao gồm entity thù địch không
     * @return Thông tin các entity gần đó dưới dạng JsonObject
     */
    public JsonObject getNearbyEntities(String playerName, int radius, MinecraftServer server, String entityType, boolean includePassive, boolean includeHostile) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
        if (player == null) {
            return null;
        }

        JsonObject result = new JsonObject();
        JsonArray entities = new JsonArray();
        ServerWorld world = player.getServerWorld();
        
        // Tạo box xung quanh người chơi với bán kính đã cho
        Box box = new Box(
            player.getX() - radius, player.getY() - radius, player.getZ() - radius,
            player.getX() + radius, player.getY() + radius, player.getZ() + radius
        );
        
        // Lấy danh sách entity trong box - không sử dụng predicate để tránh lỗi
        List<Entity> nearbyEntities = world.getOtherEntities(player, box);
        
        // Thêm thông tin về từng entity
        for (Entity entity : nearbyEntities) {
            String entityTypeId = Registries.ENTITY_TYPE.getId(entity.getType()).toString();
            
            // Lọc theo loại entity nếu có
            if (entityType != null && !entityType.isEmpty() && !entityTypeId.contains(entityType)) {
                continue;
            }
            
            // Xác định loại entity (thụ động hay thù địch)
            boolean isHostile = false;
            boolean isPassive = false;
            
            // Danh sách các entity thù địch
            if (entityTypeId.contains("zombie") || entityTypeId.contains("skeleton") || 
                entityTypeId.contains("creeper") || entityTypeId.contains("spider") || 
                entityTypeId.contains("enderman") || entityTypeId.contains("witch") || 
                entityTypeId.contains("slime") || entityTypeId.contains("phantom") || 
                entityTypeId.contains("drowned") || entityTypeId.contains("illager")) {
                isHostile = true;
            } 
            // Danh sách các entity thụ động
            else if (entityTypeId.contains("cow") || entityTypeId.contains("sheep") || 
                     entityTypeId.contains("pig") || entityTypeId.contains("chicken") || 
                     entityTypeId.contains("horse") || entityTypeId.contains("rabbit") || 
                     entityTypeId.contains("villager") || entityTypeId.contains("wolf") || 
                     entityTypeId.contains("cat") || entityTypeId.contains("fox")) {
                isPassive = true;
            }
            
            // Lọc theo loại (thụ động/thù địch)
            if ((isHostile && !includeHostile) || (isPassive && !includePassive)) {
                continue;
            }
            
            JsonObject entityInfo = new JsonObject();
            entityInfo.addProperty("id", entity.getUuidAsString());
            entityInfo.addProperty("type", entityTypeId);
            entityInfo.addProperty("name", entity.getName().getString());
            entityInfo.addProperty("isHostile", isHostile);
            entityInfo.addProperty("isPassive", isPassive);
            
            JsonObject position = new JsonObject();
            position.addProperty("x", entity.getX());
            position.addProperty("y", entity.getY());
            position.addProperty("z", entity.getZ());
            entityInfo.add("position", position);
            
            // Thêm thông tin về khoảng cách từ người chơi
            double distance = entity.distanceTo(player);
            entityInfo.addProperty("distance", distance);
            
            // Thêm thông tin về trạng thái của entity (nếu là LivingEntity)
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                JsonObject status = new JsonObject();
                status.addProperty("health", livingEntity.getHealth());
                status.addProperty("maxHealth", livingEntity.getMaxHealth());
                status.addProperty("isOnFire", livingEntity.isOnFire());
                entityInfo.add("status", status);
            }
            
            entities.add(entityInfo);
        }
        
        result.add("entities", entities);
        result.addProperty("radius", radius);
        result.addProperty("count", entities.size());
        
        return result;
    }

    // Phương thức getPlayerAdvancements đã bị loại bỏ

    /**
     * Lấy thống kê (statistics) của người chơi
     * 
     * @param playerName Tên người chơi
     * @param server     Server Minecraft
     * @return Thông tin thống kê dưới dạng JsonObject
     */
    public JsonObject getPlayerStatistics(String playerName, MinecraftServer server) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
        if (player == null) {
            return null;
        }

        JsonObject result = new JsonObject();
        
        // Thống kê cơ bản
        JsonObject generalStats = new JsonObject();
        generalStats.addProperty("playTime", player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.PLAY_TIME)));
        generalStats.addProperty("mobsKilled", player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.MOB_KILLS)));
        generalStats.addProperty("playerKills", player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.PLAYER_KILLS)));
        generalStats.addProperty("deaths", player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.DEATHS)));
        generalStats.addProperty("jumps", player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.JUMP)));
        generalStats.addProperty("damageDealt", player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.DAMAGE_DEALT)));
        generalStats.addProperty("damageTaken", player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.DAMAGE_TAKEN)));
        result.add("general", generalStats);
        
        // Thống kê về block đã đào
        JsonObject miningStats = new JsonObject();
        for (Block block : Registries.BLOCK) {
            Stat<Block> minedStat = Stats.MINED.getOrCreateStat(block);
            int count = player.getStatHandler().getStat(minedStat);
            if (count > 0) {
                miningStats.addProperty(Registries.BLOCK.getId(block).toString(), count);
            }
        }
        result.add("blocksMined", miningStats);
        
        return result;
    }

    /**
     * Chuyển đổi GameMode sang tên
     * 
     * @param gameMode GameMode cần chuyển đổi
     * @return Tên của GameMode
     */
    private String getGameModeName(GameMode gameMode) {
        switch (gameMode) {
            case SURVIVAL:
                return "survival";
            case CREATIVE:
                return "creative";
            case ADVENTURE:
                return "adventure";
            case SPECTATOR:
                return "spectator";
            default:
                return "unknown";
        }
    }
}
