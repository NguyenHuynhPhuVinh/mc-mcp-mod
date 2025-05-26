# Minecraft MCP API Mod

Mod này cung cấp các API RESTful để tương tác với Minecraft server, cho phép bạn truy cập thông tin về người chơi, thế giới, vật phẩm, công thức chế tạo và thực thi lệnh từ bên ngoài game.

## Cài đặt

1. Đảm bảo bạn đã cài đặt Minecraft 1.21.1 với Fabric Loader
2. Tải mod từ trang releases và đặt vào thư mục `mods` của Minecraft
3. Khởi động Minecraft server hoặc singleplayer world
4. API server sẽ tự động chạy trên cổng 8080

## Cấu trúc phản hồi API

Tất cả các API đều trả về cấu trúc phản hồi thống nhất:

```json
{
  "success": true/false,
  "message": "Thông báo về kết quả của yêu cầu",
  "data": {
    // Dữ liệu phản hồi, tùy thuộc vào API
  }
}
```

## Tên người chơi mặc định

Tất cả các API liên quan đến người chơi đều hỗ trợ việc sử dụng tên người chơi mặc định. Điều này có nghĩa là bạn không cần phải cung cấp tham số `playerName` trong mỗi request. Nếu bạn không cung cấp tên người chơi, hệ thống sẽ tự động sử dụng tên của chủ server hoặc người chơi đầu tiên trong danh sách người chơi online.

Ví dụ, thay vì gửi:
```json
{
  "playerName": "Steve"
}
```

Bạn có thể gửi một request rỗng:
```json
{}
```

Và hệ thống sẽ tự động sử dụng tên người chơi mặc định.

## Danh sách API

### 1. Kiểm tra trạng thái server

- **Endpoint**: `/api/health`
- **Phương thức**: GET
- **Mô tả**: Kiểm tra xem API server có hoạt động không
- **Phản hồi mẫu**:
```json
{
  "success": true,
  "message": "API server is running",
  "data": null
}
```

### 2. Thông tin người chơi

- **Endpoint**: `/api/player/info`
- **Phương thức**: POST
- **Body**:
```json
{
  "playerName": "Steve" // Tùy chọn, nếu không cung cấp sẽ sử dụng tên người chơi mặc định
}
```
- **Tham số**:
  - `playerName` (tùy chọn): Tên người chơi cần lấy thông tin, nếu không cung cấp sẽ sử dụng tên người chơi mặc định
- **Mô tả**: Lấy thông tin chi tiết về người chơi
- **Phản hồi mẫu**:
```json
{
  "success": true,
  "message": "Lấy thông tin người chơi thành công",
  "data": {
    "name": "Steve",
    "uuid": "c8c58c6a-fd71-4c31-9d70-9f654d8c5e73",
    "position": {
      "x": 100.5,
      "y": 65.0,
      "z": 200.5,
      "dimension": "minecraft:overworld"
    },
    "status": {
      "health": 20.0,
      "maxHealth": 20.0,
      "food": 20,
      "saturation": 5.0,
      "experienceLevel": 10,
      "experienceProgress": 0.5,
      "gamemode": "survival",
      "isCreative": false,
      "isFlying": false,
      "isSleeping": false,
      "isOnFire": false,
      "isInWater": false,
      "isInLava": false
    }
  }
}
```

### 3. Túi đồ người chơi

- **Endpoint**: `/api/player/inventory`
- **Phương thức**: POST
- **Body**:
```json
{
  "playerName": "Steve" // Tùy chọn, nếu không cung cấp sẽ sử dụng tên người chơi mặc định
}
```
- **Tham số**:
  - `playerName` (tùy chọn): Tên người chơi cần lấy thông tin túi đồ, nếu không cung cấp sẽ sử dụng tên người chơi mặc định
- **Mô tả**: Lấy thông tin túi đồ của người chơi
- **Phản hồi mẫu**:
```json
{
  "success": true,
  "message": "Lấy thông tin túi đồ thành công",
  "data": {
    "playerName": "Steve",
    "mainInventory": [
      {
        "slot": 0,
        "id": "minecraft:diamond_sword",
        "name": "Diamond Sword",
        "count": 1,
        "damage": 0,
        "enchantments": [
          {
            "id": "minecraft:sharpness",
            "level": 5
          }
        ]
      }
    ],
    "armorItems": [
      {
        "slot": 0,
        "id": "minecraft:diamond_helmet",
        "name": "Diamond Helmet",
        "count": 1,
        "damage": 0
      }
    ],
    "offHandItem": [
      {
        "slot": 0,
        "id": "minecraft:shield",
        "name": "Shield",
        "count": 1,
        "damage": 0
      }
    ]
  }
}
```

### 4. Khối xung quanh người chơi

- **Endpoint**: `/api/player/surroundings`
- **Phương thức**: POST
- **Body**:
```json
{
  "playerName": "Steve", // Tùy chọn, nếu không cung cấp sẽ sử dụng tên người chơi mặc định
  "radius": 15,
  "verticalRadius": 30,
  "includeCommonBlocks": false
}
```
- **Tham số**:
  - `playerName` (tùy chọn): Tên người chơi, nếu không cung cấp sẽ sử dụng tên người chơi mặc định
  - `radius` (tùy chọn): Bán kính quét ngang, mặc định là 15, tối đa 30
  - `verticalRadius` (tùy chọn): Bán kính quét dọc, mặc định là gấp đôi radius, tối đa 60
  - `includeCommonBlocks` (tùy chọn): Có bao gồm các khối phổ biến không, mặc định là false
- **Mô tả**: Lấy thông tin về các khối xung quanh người chơi
- **Phản hồi mẫu**:
```json
{
  "success": true,
  "message": "Lấy thông tin xung quanh người chơi thành công",
  "data": {
    "blockFrequency": {
      "minecraft:stone": 1200,
      "minecraft:dirt": 800,
      "minecraft:diamond_ore": 3
    },
    "notableBlocks": [
      {
        "id": "minecraft:diamond_ore",
        "name": "Diamond Ore",
        "position": { "x": 100, "y": 15, "z": 200 },
        "distance": 18.5,
        "relativePosition": "below"
      },
      {
        "id": "minecraft:chest",
        "name": "Chest",
        "position": { "x": 105, "y": 65, "z": 210 },
        "distance": 4.2,
        "relativePosition": "same_level"
      }
    ],
    "environment": {
      "biome": "minecraft:plains",
      "isDaytime": true,
      "isRaining": false,
      "isThundering": false,
      "skyLight": 15
    },
    "radius": 15,
    "totalBlocksScanned": 27000
  }
}
```

### 5. Entity gần người chơi

- **Endpoint**: `/api/player/entities`
- **Phương thức**: POST
- **Body**:
```json
{
  "playerName": "Steve", // Tùy chọn, nếu không cung cấp sẽ sử dụng tên người chơi mặc định
  "radius": 20,
  "entityType": "zombie",
  "includePassive": false,
  "includeHostile": true
}
```
- **Tham số**:
  - `playerName` (tùy chọn): Tên người chơi, nếu không cung cấp sẽ sử dụng tên người chơi mặc định
  - `radius` (tùy chọn): Bán kính quét, mặc định là 10, tối đa 50
  - `entityType` (tùy chọn): Loại entity cần lọc, để trống để lấy tất cả
  - `includePassive` (tùy chọn): Có bao gồm entity thụ động không, mặc định là true
  - `includeHostile` (tùy chọn): Có bao gồm entity thù địch không, mặc định là true
- **Mô tả**: Lấy danh sách các entity gần người chơi. Nếu không cung cấp playerName, sẽ sử dụng tên người chơi mặc định.
- **Phản hồi mẫu**:
```json
{
  "success": true,
  "message": "Lấy danh sách entity gần người chơi thành công",
  "data": {
    "entities": [
      {
        "id": "c8c58c6a-fd71-4c31-9d70-9f654d8c5e73",
        "type": "minecraft:zombie",
        "name": "Zombie",
        "isHostile": true,
        "isPassive": false,
        "position": { "x": 100, "y": 65, "z": 200 },
        "distance": 15.2,
        "status": {
          "health": 20.0,
          "maxHealth": 20.0,
          "isOnFire": false
        }
      }
    ],
    "radius": 20,
    "count": 1
  }
}
```

### 6. Thống kê người chơi

- **Endpoint**: `/api/player/statistics`
- **Phương thức**: POST
- **Body**:
```json
{
  "playerName": "Steve" // Tùy chọn, nếu không cung cấp sẽ sử dụng tên người chơi mặc định
}
```
- **Tham số**:
  - `playerName` (tùy chọn): Tên người chơi cần lấy thống kê, nếu không cung cấp sẽ sử dụng tên người chơi mặc định
- **Mô tả**: Lấy thống kê của người chơi
- **Phản hồi mẫu**:
```json
{
  "success": true,
  "message": "Lấy thống kê người chơi thành công",
  "data": {
    "general": {
      "playTime": 12000,
      "mobsKilled": 50,
      "playerKills": 5,
      "deaths": 10,
      "jumps": 1000,
      "damageDealt": 1000,
      "damageTaken": 500
    },
    "blocksMined": {
      "minecraft:stone": 1000,
      "minecraft:diamond_ore": 50
    }
  }
}
```

### 7. Thông tin thế giới

- **Endpoint**: `/api/world/info`
- **Phương thức**: GET
- **Mô tả**: Lấy thông tin về thế giới Minecraft
- **Phản hồi mẫu**:
```json
{
  "success": true,
  "message": "Lấy thông tin thế giới thành công",
  "data": {
    "name": "world",
    "time": 12000,
    "isDay": true,
    "isRaining": false,
    "isThundering": false,
    "difficulty": "normal",
    "gameRules": {
      "doDaylightCycle": true,
      "doMobSpawning": true,
      "keepInventory": false
    }
  }
}
```

### 8. Thực thi lệnh

- **Endpoint**: `/api/command`
- **Phương thức**: POST
- **Body**:
```json
{
  "command": "give @p diamond 64"
}
```
- **Mô tả**: Thực thi lệnh Minecraft
- **Phản hồi mẫu**:
```json
{
  "success": true,
  "message": "Lệnh đã được thực thi thành công",
  "data": {
    "command": "give @p diamond 64",
    "result": "Đã cho Steve 64 Diamond"
  }
}
```

### 9. Lấy danh sách vật phẩm trong game

- **Endpoint**: `/api/items`
- **Phương thức**: GET
- **Mô tả**: Lấy danh sách tất cả các vật phẩm trong game (chỉ trả về ID)
- **Phản hồi mẫu**:
```json
{
  "success": true,
  "message": "Lấy danh sách vật phẩm thành công",
  "data": [
    "minecraft:diamond",
    "minecraft:diamond_sword",
    "minecraft:stick",
    "minecraft:oak_planks"
  ]
}
```

### 10. Công thức chế tạo vật phẩm

- **Endpoint**: `/api/items/recipes`
- **Phương thức**: POST
- **Body**:
```json
{
  "itemId": "minecraft:diamond_sword"
}
```
- **Tham số**:
  - `itemId` (bắt buộc): ID của vật phẩm cần lấy công thức chế tạo
- **Mô tả**: Lấy công thức chế tạo của một vật phẩm cụ thể
- **Phản hồi mẫu**:
```json
{
  "success": true,
  "message": "Lấy công thức chế tạo thành công",
  "data": [
    {
      "id": "minecraft:diamond_sword",
      "type": "minecraft:crafting_shaped",
      "result": {
        "id": "minecraft:diamond_sword",
        "name": "diamond_sword",
        "translatedName": "Kiếm kim cương",
        "maxStackSize": 1,
        "hasRecipe": true
      },
      "resultCount": 1,
      "ingredients": [
        {
          "id": "minecraft:diamond",
          "name": "Kim cương",
          "count": 2
        },
        {
          "id": "minecraft:stick",
          "name": "Gậy",
          "count": 1
        }
      ],
      "pattern": {
        "grid": [
          [" ", "A", " "],
          ["A", " ", " "],
          [" ", "B", " "]
        ],
        "legend": {
          "A": {
            "id": "minecraft:diamond",
            "name": "Kim cương"
          },
          "B": {
            "id": "minecraft:stick",
            "name": "Gậy"
          }
        }
      }
    }
  ]
}
```

## Xử lý lỗi

Khi có lỗi xảy ra, API sẽ trả về phản hồi với `success` là `false` và `message` chứa thông tin về lỗi:

```json
{
  "success": false,
  "message": "Không tìm thấy người chơi: Steve",
  "data": null
}
```

## Ví dụ sử dụng với cURL

### Lấy thông tin người chơi:
```bash
curl -X POST http://localhost:8080/api/player/info \
  -H "Content-Type: application/json" \
  -d '{"playerName": "Steve"}'
```

### Thực thi lệnh:
```bash
curl -X POST http://localhost:8080/api/command \
  -H "Content-Type: application/json" \
  -d '{"command": "give @p diamond 64"}'
```

## Lưu ý

- API server chỉ hoạt động khi Minecraft server đang chạy
- Các API liên quan đến người chơi chỉ hoạt động khi người chơi đang online
- Bán kính quét càng lớn, thời gian phản hồi càng lâu
- Mod này chỉ hỗ trợ Minecraft 1.21.1 với Fabric Loader

## Đóng góp

Nếu bạn muốn đóng góp vào dự án, hãy tạo pull request hoặc báo cáo lỗi qua trang Issues.

## Giấy phép

Mod này được phát hành dưới giấy phép MIT.
