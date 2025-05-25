package com.tomisakae.mc_mcp.api.model;

/**
 * Lớp đại diện cho một stack vật phẩm trong túi đồ
 */
public class ItemStack {
    private String itemId;
    private String displayName;
    private int count;
    private int slot;
    private int damage;
    private boolean enchanted;
    private String[] enchantments;

    public ItemStack(String itemId, String displayName, int count, int slot, int damage, boolean enchanted, String[] enchantments) {
        this.itemId = itemId;
        this.displayName = displayName;
        this.count = count;
        this.slot = slot;
        this.damage = damage;
        this.enchanted = enchanted;
        this.enchantments = enchantments;
    }

    // Khởi tạo từ ItemStack của Minecraft
    public static ItemStack fromMinecraftItemStack(net.minecraft.item.ItemStack itemStack, int slot) {
        if (itemStack == null || itemStack.isEmpty()) {
            return null;
        }

        String itemId = itemStack.getItem().toString();
        String displayName = itemStack.getName().getString();
        int count = itemStack.getCount();
        int damage = itemStack.getDamage();
        boolean enchanted = itemStack.hasEnchantments();
        
        // Xử lý enchantments nếu có
        String[] enchantments = new String[0];
        if (enchanted) {
            // Trong một triển khai thực tế, bạn sẽ cần trích xuất thông tin enchantment
            // Đây chỉ là mã giả
        }

        return new ItemStack(itemId, displayName, count, slot, damage, enchanted, enchantments);
    }

    // Getters and Setters
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public boolean isEnchanted() {
        return enchanted;
    }

    public void setEnchanted(boolean enchanted) {
        this.enchanted = enchanted;
    }

    public String[] getEnchantments() {
        return enchantments;
    }

    public void setEnchantments(String[] enchantments) {
        this.enchantments = enchantments;
    }
}
