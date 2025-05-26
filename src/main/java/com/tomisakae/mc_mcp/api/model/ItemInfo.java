package com.tomisakae.mc_mcp.api.model;

/**
 * Lớp chứa thông tin cơ bản về một vật phẩm trong game
 */
public class ItemInfo {
    private String id;
    private String name;
    private String translatedName;
    private int maxStackSize;
    private boolean hasRecipe;
    
    public ItemInfo() {
    }
    
    public ItemInfo(String id, String name, String translatedName, int maxStackSize, boolean hasRecipe) {
        this.id = id;
        this.name = name;
        this.translatedName = translatedName;
        this.maxStackSize = maxStackSize;
        this.hasRecipe = hasRecipe;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTranslatedName() {
        return translatedName;
    }

    public void setTranslatedName(String translatedName) {
        this.translatedName = translatedName;
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    public void setMaxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;
    }
    
    public boolean isHasRecipe() {
        return hasRecipe;
    }
    
    public void setHasRecipe(boolean hasRecipe) {
        this.hasRecipe = hasRecipe;
    }
}
