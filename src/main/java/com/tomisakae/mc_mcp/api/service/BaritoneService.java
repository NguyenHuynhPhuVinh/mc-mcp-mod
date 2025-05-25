package com.tomisakae.mc_mcp.api.service;

import com.tomisakae.mc_mcp.Mcmcpmod;
import com.tomisakae.mc_mcp.api.util.JsonUtils;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.List;

/**
 * Service xử lý logic liên quan đến Baritone
 */
public class BaritoneService {

    // Danh sách các lệnh Baritone phổ biến và mô tả
    private static final Map<String, String> BARITONE_COMMANDS = new HashMap<String, String>() {{
        put("help", "Hiển thị trợ giúp về các lệnh Baritone");
        put("goal", "Đặt mục tiêu đến tọa độ (x y z, x z, hoặc y)");
        put("goto", "Di chuyển đến tọa độ hoặc khối");
        put("mine", "Đào các loại khối cụ thể");
        put("path", "Tìm đường đến mục tiêu hiện tại");
        put("follow", "Theo dõi người chơi hoặc entity");
        put("wp", "Quản lý waypoint");
        put("build", "Xây dựng schematic");
        put("tunnel", "Đào đường hầm");
        put("farm", "Tự động thu hoạch và trồng cây");
        put("axis", "Di chuyển đến trục hoặc đường chéo");
        put("explore", "Khám phá thế giới từ tọa độ gốc");
        put("invert", "Đảo ngược mục tiêu hiện tại");
        put("come", "Di chuyển đến vị trí camera");
        put("blacklist", "Chặn Baritone đi đến khối gần nhất");
        put("eta", "Hiển thị thời gian ước tính đến mục tiêu");
        put("proc", "Hiển thị thông tin về quá trình hiện tại");
        put("repack", "Tải lại các chunk xung quanh");
        put("gc", "Gọi System.gc() để giải phóng bộ nhớ");
        put("render", "Sửa lỗi hiển thị chunk");
        put("find", "Tìm kiếm vị trí của khối trong cache");
        put("surface", "Di chuyển đến bề mặt gần nhất");
        put("version", "Hiển thị phiên bản Baritone");
        put("click", "Nhấp vào đích đến trên màn hình");
        put("cancel", "Dừng tất cả các hoạt động của Baritone");
        put("stop", "Dừng tất cả các hoạt động của Baritone");
    }};

    // Danh sách các cài đặt Baritone phổ biến
    private static final List<String> BARITONE_SETTINGS = Arrays.asList(
        "allowBreak", "allowSprint", "allowPlace", "allowParkour", "allowParkourPlace",
        "blockPlacementPenalty", "renderCachedChunks", "cachedChunksOpacity", "avoidance",
        "legitMine", "followRadius", "backfill", "buildInLayers", "buildRepeatDistance",
        "buildRepeatDirection", "worldExploringChunkOffset", "acceptableThrowawayItems",
        "blocksToAvoidBreaking", "mineScanDroppedItems", "allowDiagonalAscend"
    );

    /**
     * Thực thi lệnh Baritone cho người chơi
     * 
     * @param player Người chơi thực thi lệnh
     * @param command Lệnh Baritone cần thực thi (không bao gồm prefix #)
     * @return JsonObject chứa kết quả thực thi lệnh
     */
    public JsonObject executeBaritoneCommand(ServerPlayerEntity player, String command) {
        if (player == null) {
            throw new IllegalArgumentException("Người chơi không được null");
        }
        
        if (command == null || command.isEmpty()) {
            throw new IllegalArgumentException("Lệnh không được để trống");
        }
        
        // Thêm prefix # vào lệnh nếu chưa có
        String fullCommand = command.startsWith("#") ? command : "#" + command;
        
        // Gửi lệnh như thể người chơi đã gõ vào chat
        // Lưu ý: Trong môi trường server, cần sử dụng cơ chế khác để gửi lệnh đến client
        // Đây là cách mô phỏng, trong thực tế cần tích hợp với Baritone API
        player.sendMessage(Text.of(fullCommand), false);
        
        Mcmcpmod.LOGGER.info("Đã gửi lệnh Baritone cho người chơi " + player.getName().getString() + ": " + fullCommand);
        
        // Tạo kết quả
        JsonObject result = new JsonObject();
        result.addProperty("command", command);
        result.addProperty("playerName", player.getName().getString());
        result.addProperty("timestamp", System.currentTimeMillis());
        
        // Trong thực tế, bạn có thể muốn lấy phản hồi từ Baritone
        // Nhưng điều này đòi hỏi tích hợp sâu hơn với Baritone API
        result.addProperty("status", "sent");
        result.addProperty("note", "Lệnh đã được gửi đến client của người chơi. Kết quả thực tế phụ thuộc vào việc người chơi đã cài đặt Baritone chưa.");
        
        return result;
    }
    
    /**
     * Lấy danh sách các lệnh Baritone hỗ trợ
     * 
     * @return JsonObject chứa danh sách lệnh và mô tả
     */
    public JsonObject getBaritoneCommands() {
        JsonObject result = new JsonObject();
        
        // Thêm thông tin về prefix
        result.addProperty("prefix", "#");
        result.addProperty("description", "Baritone là một bot pathfinding tự động cho Minecraft. Sử dụng prefix # trước mỗi lệnh.");
        
        // Thêm danh sách lệnh
        JsonObject commands = new JsonObject();
        for (Map.Entry<String, String> entry : BARITONE_COMMANDS.entrySet()) {
            commands.addProperty(entry.getKey(), entry.getValue());
        }
        result.add("commands", commands);
        
        // Thêm danh sách cài đặt
        JsonArray settings = new JsonArray();
        for (String setting : BARITONE_SETTINGS) {
            settings.add(setting);
        }
        result.add("settings", settings);
        
        // Thêm ví dụ sử dụng
        JsonObject examples = new JsonObject();
        examples.addProperty("goto", "#goto 100 64 -200");
        examples.addProperty("mine", "#mine diamond_ore");
        examples.addProperty("path", "#path");
        examples.addProperty("follow", "#follow player Steve");
        examples.addProperty("stop", "#stop");
        result.add("examples", examples);
        
        return result;
    }
}
