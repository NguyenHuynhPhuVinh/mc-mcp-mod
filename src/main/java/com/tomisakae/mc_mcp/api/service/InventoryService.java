package com.tomisakae.mc_mcp.api.service;

import com.tomisakae.mc_mcp.api.model.PlayerInventory;
import com.tomisakae.mc_mcp.api.model.ItemStack;
import com.tomisakae.mc_mcp.api.util.JsonUtils;
import com.tomisakae.mc_mcp.Mcmcpmod;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service xử lý logic liên quan đến túi đồ của người chơi
 */
public class InventoryService {

    /**
     * Lấy thông tin túi đồ của người chơi theo tên
     * 
     * @param playerName Tên người chơi
     * @return Thông tin túi đồ của người chơi, hoặc null nếu không tìm thấy
     */
    public PlayerInventory getPlayerInventory(String playerName, MinecraftServer server) {
        if (server == null) {
            Mcmcpmod.LOGGER.error("Server is null when trying to get player inventory");
            return null;
        }

        // Tìm người chơi theo tên
        Optional<ServerPlayerEntity> optionalPlayer = server.getPlayerManager().getPlayerList()
                .stream()
                .filter(player -> player.getName().getString().equalsIgnoreCase(playerName))
                .findFirst();

        if (optionalPlayer.isEmpty()) {
            Mcmcpmod.LOGGER.warn("Player not found: " + playerName);
            return null;
        }

        ServerPlayerEntity player = optionalPlayer.get();
        PlayerInventory inventory = new PlayerInventory(playerName);

        // Lấy thông tin túi đồ chính
        List<ItemStack> mainInventoryItems = new ArrayList<>();
        for (int i = 0; i < player.getInventory().main.size(); i++) {
            net.minecraft.item.ItemStack mcItemStack = player.getInventory().main.get(i);
            if (!mcItemStack.isEmpty()) {
                ItemStack itemStack = ItemStack.fromMinecraftItemStack(mcItemStack, i);
                if (itemStack != null) {
                    mainInventoryItems.add(itemStack);
                }
            }
        }
        inventory.setMainInventory(mainInventoryItems);

        // Lấy thông tin giáp
        List<ItemStack> armorItems = new ArrayList<>();
        for (int i = 0; i < player.getInventory().armor.size(); i++) {
            net.minecraft.item.ItemStack mcItemStack = player.getInventory().armor.get(i);
            if (!mcItemStack.isEmpty()) {
                ItemStack itemStack = ItemStack.fromMinecraftItemStack(mcItemStack, i);
                if (itemStack != null) {
                    armorItems.add(itemStack);
                }
            }
        }
        inventory.setArmorItems(armorItems);

        // Lấy thông tin vật phẩm tay phụ
        List<ItemStack> offHandItems = new ArrayList<>();
        net.minecraft.item.ItemStack mcOffHandItem = player.getInventory().offHand.get(0);
        if (!mcOffHandItem.isEmpty()) {
            ItemStack itemStack = ItemStack.fromMinecraftItemStack(mcOffHandItem, 0);
            if (itemStack != null) {
                offHandItems.add(itemStack);
            }
        }
        inventory.setOffHandItem(offHandItems);

        return inventory;
    }
    
    /**
     * Lấy thông tin túi đồ của người chơi dưới dạng JsonObject
     * 
     * @param playerName Tên người chơi
     * @param server Instance của MinecraftServer
     * @return JsonObject chứa thông tin túi đồ của người chơi
     */
    public JsonObject getPlayerInventoryAsJson(String playerName, MinecraftServer server) {
        PlayerInventory inventory = getPlayerInventory(playerName, server);
        if (inventory == null) {
            return null;
        }
        
        // Sử dụng Gson để chuyển đổi PlayerInventory thành JsonObject
        String json = JsonUtils.toJson(inventory);
        return JsonUtils.fromJson(json, JsonObject.class);
    }
}
