package com.tomisakae.mc_mcp.api.model;

import java.util.List;
import java.util.ArrayList;

/**
 * Lớp đại diện cho túi đồ của người chơi
 */
public class PlayerInventory {
    private String playerName;
    private List<ItemStack> mainInventory;
    private List<ItemStack> armorItems;
    private List<ItemStack> offHandItem;

    public PlayerInventory(String playerName) {
        this.playerName = playerName;
        this.mainInventory = new ArrayList<>();
        this.armorItems = new ArrayList<>();
        this.offHandItem = new ArrayList<>();
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public List<ItemStack> getMainInventory() {
        return mainInventory;
    }

    public void setMainInventory(List<ItemStack> mainInventory) {
        this.mainInventory = mainInventory;
    }

    public List<ItemStack> getArmorItems() {
        return armorItems;
    }

    public void setArmorItems(List<ItemStack> armorItems) {
        this.armorItems = armorItems;
    }

    public List<ItemStack> getOffHandItem() {
        return offHandItem;
    }

    public void setOffHandItem(List<ItemStack> offHandItem) {
        this.offHandItem = offHandItem;
    }
}
