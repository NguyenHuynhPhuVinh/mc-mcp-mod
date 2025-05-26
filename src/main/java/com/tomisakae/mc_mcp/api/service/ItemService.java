package com.tomisakae.mc_mcp.api.service;

import com.tomisakae.mc_mcp.Mcmcpmod;
import com.tomisakae.mc_mcp.api.model.ItemInfo;
import com.tomisakae.mc_mcp.api.model.RecipeInfo;
import com.google.gson.Gson;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service xử lý logic liên quan đến vật phẩm và công thức chế tạo
 */
public class ItemService {

    /**
     * Lấy danh sách tất cả các vật phẩm trong game (chỉ trả về ID)
     * 
     * @param server MinecraftServer instance
     * @return Danh sách ID vật phẩm
     */
    public List<String> getAllItems(MinecraftServer server) {
        if (server == null) {
            throw new IllegalArgumentException("Server không được null");
        }

        List<String> itemIds = new ArrayList<>();

        // Lấy tất cả các vật phẩm đã đăng ký
        Registries.ITEM.forEach(item -> {
            String id = Registries.ITEM.getId(item).toString();
            itemIds.add(id);
        });

        return itemIds;
    }

    /**
     * Lấy thông tin công thức chế tạo của một vật phẩm cụ thể
     * 
     * @param server MinecraftServer instance
     * @param itemId ID của vật phẩm cần lấy công thức
     * @return Danh sách công thức chế tạo của vật phẩm
     */
    public List<RecipeInfo> getRecipesForItem(MinecraftServer server, String itemId) {
        if (server == null) {
            throw new IllegalArgumentException("Server không được null");
        }

        if (itemId == null || itemId.isEmpty()) {
            throw new IllegalArgumentException("ID vật phẩm không được để trống");
        }

        // Chuẩn hóa ID vật phẩm
        if (!itemId.contains(":")) {
            itemId = "minecraft:" + itemId;
        }

        final String finalItemId = itemId;
        RecipeManager recipeManager = server.getRecipeManager();
        List<RecipeInfo> recipes = new ArrayList<>();

        // Tìm tất cả công thức có kết quả là vật phẩm được chỉ định
        recipeManager.listAllOfType(RecipeType.CRAFTING).forEach(recipeEntry -> {
            ItemStack resultStack = recipeEntry.value().getResult(server.getRegistryManager());
            Item resultItem = resultStack.getItem();
            String resultItemId = Registries.ITEM.getId(resultItem).toString();
            
            if (resultItemId.equals(finalItemId)) {
                RecipeInfo recipeInfo = convertRecipeToRecipeInfo(server, recipeEntry);
                if (recipeInfo != null) {
                    recipes.add(recipeInfo);
                }
            }
        });

        return recipes;
    }

    /**
     * Chuyển đổi từ RecipeEntry của Minecraft sang RecipeInfo
     * 
     * @param server MinecraftServer instance
     * @param recipeEntry RecipeEntry cần chuyển đổi
     * @return RecipeInfo tương ứng
     */
    private RecipeInfo convertRecipeToRecipeInfo(MinecraftServer server, RecipeEntry<?> recipeEntry) {
        try {
            String recipeId = recipeEntry.id().toString();
            String recipeType = recipeEntry.value().getType().toString();
            
            ItemStack resultStack = recipeEntry.value().getResult(server.getRegistryManager());
            Item resultItem = resultStack.getItem();
            String resultItemId = Registries.ITEM.getId(resultItem).toString();
            String resultItemName = resultItemId.replace("minecraft:", "");
            String translatedName = resultItem.getName().getString();
            int resultCount = resultStack.getCount();
            
            ItemInfo resultItemInfo = new ItemInfo(
                resultItemId, 
                resultItemName, 
                translatedName, 
                resultItem.getMaxCount(),
                true
            );
            
            List<Map<String, Object>> ingredients = new ArrayList<>();
            String pattern = null;
            
            // Xử lý công thức theo loại
            if (recipeEntry.value() instanceof ShapedRecipe) {
                // Công thức có hình dạng
                ShapedRecipe shapedRecipe = (ShapedRecipe) recipeEntry.value();
                // Tạo một pattern mô tả công thức trong crafting table
                int width = shapedRecipe.getWidth();
                int height = shapedRecipe.getHeight();
                
                // Tạo ma trận đại diện cho công thức với các ký tự khác nhau cho các nguyên liệu khác nhau
                String[][] patternMatrix = new String[height][width];
                DefaultedList<Ingredient> recipeIngredients = shapedRecipe.getIngredients();
                
                // Tạo bản đồ các nguyên liệu để gán ký tự đại diện
                Map<String, Character> ingredientSymbols = new HashMap<>();
                char nextSymbol = 'A';
                
                // Điền ma trận với các ký tự đại diện cho nguyên liệu
                for (int row = 0; row < height; row++) {
                    for (int col = 0; col < width; col++) {
                        int index = row * width + col;
                        if (index < recipeIngredients.size()) {
                            Ingredient ingredient = recipeIngredients.get(index);
                            if (ingredient.isEmpty()) {
                                patternMatrix[row][col] = " ";
                            } else {
                                // Tìm ký tự đại diện cho nguyên liệu này
                                String itemId = "";
                                if (ingredient.getMatchingStacks().length > 0) {
                                    ItemStack stack = ingredient.getMatchingStacks()[0];
                                    itemId = Registries.ITEM.getId(stack.getItem()).toString();
                                }
                                
                                // Nếu chưa có ký tự cho nguyên liệu này, tạo một ký tự mới
                                if (!ingredientSymbols.containsKey(itemId) && !itemId.isEmpty()) {
                                    ingredientSymbols.put(itemId, nextSymbol++);
                                }
                                
                                // Sử dụng ký tự đại diện hoặc 'X' nếu không tìm thấy
                                patternMatrix[row][col] = ingredientSymbols.containsKey(itemId) ? 
                                        String.valueOf(ingredientSymbols.get(itemId)) : "X";
                            }
                        } else {
                            patternMatrix[row][col] = " ";
                        }
                    }
                }
                
                // Tạo một cấu trúc pattern dễ hiểu hơn
                Map<String, Object> patternInfo = new HashMap<>();
                
                // Thêm ma trận
                patternInfo.put("grid", patternMatrix);
                
                // Tạo bảng chú thích
                Map<String, Map<String, String>> legend = new HashMap<>();
                for (Map.Entry<String, Character> entry : ingredientSymbols.entrySet()) {
                    String itemId = entry.getKey();
                    char symbol = entry.getValue();
                    String itemName = "Unknown";
                    
                    // Tìm tên vật phẩm
                    for (Ingredient ingredient : recipeIngredients) {
                        if (ingredient.getMatchingStacks().length > 0) {
                            ItemStack stack = ingredient.getMatchingStacks()[0];
                            String id = Registries.ITEM.getId(stack.getItem()).toString();
                            if (id.equals(itemId)) {
                                itemName = stack.getItem().getName().getString();
                                break;
                            }
                        }
                    }
                    
                    Map<String, String> symbolInfo = new HashMap<>();
                    symbolInfo.put("id", itemId);
                    symbolInfo.put("name", itemName);
                    
                    legend.put(String.valueOf(symbol), symbolInfo);
                }
                
                patternInfo.put("legend", legend);
                
                // Chuyển cấu trúc thành JSON
                pattern = new Gson().toJson(patternInfo);
                for (Ingredient ingredient : recipeIngredients) {
                    if (ingredient.getMatchingStacks().length > 0) {
                        ItemStack stack = ingredient.getMatchingStacks()[0];
                        Map<String, Object> ingredientInfo = new HashMap<>();
                        ingredientInfo.put("id", Registries.ITEM.getId(stack.getItem()).toString());
                        ingredientInfo.put("name", stack.getItem().getName().getString());
                        ingredientInfo.put("count", stack.getCount());
                        ingredients.add(ingredientInfo);
                    }
                }
            } else if (recipeEntry.value() instanceof ShapelessRecipe) {
                // Công thức không có hình dạng
                ShapelessRecipe shapelessRecipe = (ShapelessRecipe) recipeEntry.value();
                DefaultedList<Ingredient> recipeIngredients = shapelessRecipe.getIngredients();
                
                for (Ingredient ingredient : recipeIngredients) {
                    if (ingredient.getMatchingStacks().length > 0) {
                        ItemStack stack = ingredient.getMatchingStacks()[0];
                        Map<String, Object> ingredientInfo = new HashMap<>();
                        ingredientInfo.put("id", Registries.ITEM.getId(stack.getItem()).toString());
                        ingredientInfo.put("name", stack.getItem().getName().getString());
                        ingredientInfo.put("count", stack.getCount());
                        ingredients.add(ingredientInfo);
                    }
                }
            } else {
                // Các loại công thức khác
                Mcmcpmod.LOGGER.info("Loại công thức không được hỗ trợ đầy đủ: " + recipeType);
            }
            
            return new RecipeInfo(recipeId, recipeType, resultItemInfo, resultCount, ingredients, pattern);
        } catch (Exception e) {
            Mcmcpmod.LOGGER.error("Lỗi khi chuyển đổi công thức: " + e.getMessage());
            return null;
        }
    }
}
