package com.tomisakae.mc_mcp.api.model;

import java.util.List;
import java.util.Map;

/**
 * Lớp chứa thông tin về công thức chế tạo của một vật phẩm
 */
public class RecipeInfo {
    private String id;
    private String type;
    private ItemInfo result;
    private int resultCount;
    private List<Map<String, Object>> ingredients;
    private String pattern;
    
    public RecipeInfo() {
    }
    
    public RecipeInfo(String id, String type, ItemInfo result, int resultCount, 
                     List<Map<String, Object>> ingredients, String pattern) {
        this.id = id;
        this.type = type;
        this.result = result;
        this.resultCount = resultCount;
        this.ingredients = ingredients;
        this.pattern = pattern;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ItemInfo getResult() {
        return result;
    }

    public void setResult(ItemInfo result) {
        this.result = result;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public List<Map<String, Object>> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Map<String, Object>> ingredients) {
        this.ingredients = ingredients;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
