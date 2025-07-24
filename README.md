# MCCarnival-PPT 插件使用说明

## 功能描述
这个插件包含两个主要功能：
1. **PPT翻页功能**：可以修改ItemDisplay实体中指定物品的CustomModelData值，支持命令操作和物品交互两种方式。支持的物品类型可在配置文件中自定义，默认支持幻翼膜、纸、书和成书等物品。
2. **电梯传送功能**：玩家可以通过在特定方块上双击跳跃来实现垂直传送，支持向上和向下传送。

## PPT翻页命令使用

### /ppt next
- 功能：将附近支持的ItemDisplay实体的CustomModelData值+1
- 权限：仅OP可用
- 搜索范围：根据配置文件设置（默认10格）

### /ppt up
- 功能：将附近支持的ItemDisplay实体的CustomModelData值-1
- 权限：仅OP可用
- 搜索范围：根据配置文件设置（默认10格）

### /ppt pagepen [玩家名]
- 功能：给玩家发放PPT翻页笔物品
- 权限：仅OP可用
- 说明：获得一个CustomModelData为1的木棍，可用于交互式翻页
- 参数：
  - 不指定玩家名：给自己发放翻页笔
  - 指定玩家名：给指定玩家发放翻页笔
  - 支持Tab补全在线玩家名

### /ppt page <数字>
- 功能：直接跳转到指定页数
- 权限：仅OP可用
- 说明：将附近支持的ItemDisplay实体的CustomModelData直接设置为指定数字
- 参数：页数必须大于等于1
- 搜索范围：根据配置文件设置（默认10格）

### /ppt forbid
- 功能：切换翻页笔功能的启用/禁用状态
- 权限：仅OP可用
- 说明：禁用后翻页笔将无法使用，但命令翻页仍然可用，再次执行可重新启用

## PPT高级设置命令

### /post set <数字>
- 功能：强制设置页数（无最大值限制）
- 权限：仅OP可用
- 说明：可以设置超过配置文件中最大值的页数，用于特殊情况
- 参数：页数必须大于等于1
- 搜索范围：根据配置文件设置

## 电梯系统命令

### /elevator reload
- 功能：重载电梯配置文件
- 权限：仅OP可用
- 说明：当修改elevator.json配置文件后，使用此命令重载配置

### /elevator status
- 功能：查看电梯系统当前状态
- 权限：仅OP可用
- 说明：显示电梯功能是否启用、方块类型、搜索距离等信息

### /elevator info
- 功能：查看电梯系统使用说明
- 权限：仅OP可用
- 说明：显示如何使用电梯功能的详细说明

## 电梯传送功能

### 使用方法
1. **向上传送**：站在绿宝石块或钻石块上，单击跳跃键（空格键）
2. **向下传送**：站在红石块或钻石块上，按Shift键
3. **双向方块**：钻石块支持双向传送（跳跃向上，Shift向下）
4. 每种方块的传送距离可以独立配置

### 功能特点
- **精确传送**：按配置的距离进行精确的垂直传送
- **安全传送**：确保传送位置安全，避免卡在方块中或传送到危险位置
- **冷却机制**：防止频繁使用，默认3秒冷却时间
- **视觉效果**：传送时播放音效和粒子效果
- **权限兼容**：与现有PPT权限系统完美兼容

### 电梯配置文件 (elevator.json)

```json
{
  "elevator": {
    "enabled": true,
  "move-distance": 10,
  "up-block": "EMERALD_BLOCK",
  "down-block": "REDSTONE_BLOCK",
  "bidirectional-block": "DIAMOND_BLOCK",
  "up-block-distance": 10,
  "down-block-distance": 10,
  "bidirectional-block-up-distance": 10,
  "bidirectional-block-down-distance": 10,
  "sound-enabled": true,
    "sound-type": "ENTITY_ENDERMAN_TELEPORT",
    "particle-enabled": true,
    "particle-type": "PORTAL",
    "cooldown-seconds": 2
  }
}
```

### 配置说明
- `enabled`: 是否启用电梯功能
- `move-distance`: 移动距离（当前未使用）
- `up-block`: 向上传送方块（默认：绿宝石块）
- `down-block`: 向下传送方块（默认：红石块）
- `bidirectional-block`: 双向传送方块（默认：钻石块）
- `up-block-distance`: 上行方块传送距离（默认：10格）
- `down-block-distance`: 下行方块传送距离（默认：10格）
- `bidirectional-block-up-distance`: 双向方块上行传送距离（默认：10格）
- `bidirectional-block-down-distance`: 双向方块下行传送距离（默认：10格）
- `sound-enabled`: 是否启用音效
- `sound-type`: 音效类型
- `particle-enabled`: 是否启用粒子效果
- `particle-type`: 粒子效果类型
- `cooldown-seconds`: 冷却时间（秒）

## PPT翻页笔使用

### 物品属性
- 材质：木棍(STICK)
- CustomModelData：1
- 显示名称：§6PPT翻页笔

### 使用权限
- 权限：mccarnival.ppt.penuse
- 说明：只有拥有此权限的玩家才能使用翻页笔进行翻页操作

### 交互方式
- **左键点击**：下一页（CustomModelData +1）
- **右键点击**：上一页（CustomModelData -1）
- 支持空气点击和方块点击
- 自动取消其他交互事件

## 使用步骤

### 命令使用
1. 确保你是服务器OP
2. 站在ItemDisplay实体附近（根据配置文件设置的范围）
3. 确保ItemDisplay实体显示的是配置文件中支持的物品类型
4. 使用命令：
   - `/ppt next` - 增加CustomModelData
   - `/ppt up` - 减少CustomModelData

### 翻页笔使用
1. 确保你有权限 `mccarnival.ppt.penuse`
2. 从OP处获取PPT翻页笔（使用 `/ppt pagepen` 命令）
3. 站在ItemDisplay实体附近
4. 左键点击翻到下一页，右键点击翻到上一页

## 配置文件

插件会在首次启动时生成 `config.yml` 配置文件，包含以下设置：

```yaml
# PPT页数设置
ppt:
  # PPT的最大页数，超过此数值只能通过 /post set 命令修改
  max-page: 100
  
# 支持的物品类型配置
supported-items:
  # 物品类型列表，支持多种不同的物品进行CustomModelData切换
  # 格式：- "MATERIAL_NAME"
  - "PHANTOM_MEMBRANE"
  - "PAPER"
  - "BOOK"
  - "WRITTEN_BOOK"
  
# 其他设置
settings:
  # 搜索范围（格数）
  search-range: 10
```

### 配置说明
- `ppt.max-page`: 设置PPT的最大页数，默认为100
- `supported-items`: 支持的物品类型列表，可以添加或删除物品类型
  - 默认支持：幻翼膜(PHANTOM_MEMBRANE)、纸(PAPER)、书(BOOK)、成书(WRITTEN_BOOK)
  - 可以添加任何有效的Minecraft物品类型名称
  - 物品名称必须使用Minecraft的英文名称（全大写，用下划线分隔）
- `settings.search-range`: 设置搜索ItemDisplay实体的范围，默认为10格

## 重要限制

### CustomModelData数值限制
- **最小值限制**：所有翻页操作都不能将CustomModelData设置为0或负数，最小值为1
- **最大值限制**：普通翻页操作（`/ppt next`、`/ppt up`、`/ppt page`、翻页笔）不能超过配置文件中设置的最大值
- **超过最大值的实体限制**：如果ItemDisplay实体的CustomModelData已经超过配置的最大值，则无法使用翻页笔或普通翻页命令对其进行任何操作
- **超过最大值时**：只能使用 `/post set` 命令进行设置或修改
- 尝试超过限制时会显示相应的错误提示

### 翻页笔禁用功能
- 使用`/ppt forbid`可以全局禁用/启用翻页笔功能
- 禁用时翻页笔无法使用，但命令翻页仍然可用
- 状态会在插件重启后重置为启用

## 权限设置

### 命令权限
- 所有命令（`/ppt` 和 `/post`）：仅服务器OP可用
- 无需额外权限配置，只要是OP即可使用所有命令功能

### 翻页笔权限
- `mccarnival.ppt.penuse`: 使用PPT翻页笔的权限
- 默认值：false（需要手动给予权限）
- 说明：只有拥有此权限的玩家才能使用翻页笔进行翻页操作

## 注意事项

- 只有玩家可以使用这些命令
- 只会修改配置文件中指定的物品类型的ItemDisplay实体
- 支持的物品类型可在配置文件的 `supported-items` 中自定义
- 搜索范围可在配置文件中调整，默认为周围10x10x10的区域
- 如果找不到符合条件的实体，会显示提示信息
- 配置文件修改后需要重启服务器或重载插件才能生效
- 如果配置的物品类型无效，插件会在控制台显示警告并使用默认的幻翼膜

## 玩家位置固定系统

### 功能说明

玩家位置固定系统可以将服务器中的所有非OP玩家固定在指定的位置和朝向，按照特定的排列规则自动排列。

### 排列规则

- **第一个玩家**：作为基准点
- **主轴排列**：
  - **Z轴模式（默认）**：玩家按Z轴+1依次排列，每20个玩家换行（Y轴+1），每排30行后X轴延伸
  - **X轴模式**：玩家按X轴+1依次排列，每20个玩家换行（Y轴+1），每排30行后Z轴延伸
- **换行规则**：每20个玩家换行（Y轴+1）
- **换排规则**：每排最多30行，然后向另一轴向延伸

### 命令使用

#### 基本命令

```
/position enable          # 启用位置固定功能
/position disable         # 禁用位置固定功能
/position setbase         # 设置当前位置为基准点
/position status          # 查看系统状态
/position reposition      # 重新排列所有玩家位置
/position setyaw <角度>    # 设置玩家Yaw朝向（水平旋转角度）
/position setpitch <角度>  # 设置玩家Pitch朝向（垂直俯仰角度）
/position setaxis <x|z>   # 设置排列主轴（x轴或z轴）
/position help            # 显示帮助信息
```

#### 豁免管理

```
/position exempt add <玩家名>      # 添加玩家到豁免列表
/position exempt remove <玩家名>   # 从豁免列表移除玩家
```

### 使用步骤

1. **设置基准点**：站在想要作为第一个玩家位置的地方，执行 `/position setbase`
2. **启用功能**：执行 `/position enable`
3. **管理豁免**：如需要某些玩家不受位置固定影响，使用 `/position exempt add <玩家名>`

### 特性

- **自动排列**：玩家加入服务器时自动分配位置
- **位置锁定**：玩家移动超过0.5格会被自动传送回指定位置
- **OP豁免**：OP玩家自动豁免位置固定
- **动态调整**：玩家离开时自动重新排列剩余玩家
- **自定义朝向**：可通过命令设置玩家的yaw和pitch朝向
- **灵活排列**：支持以X轴或Z轴为主轴进行排列
- **豁免系统**：支持将特定玩家添加到豁免列表

### 权限

- `mccarnival.position.admin`：管理玩家位置固定系统（默认：OP）

## 安装方法

1. 将编译好的jar文件放入服务器的plugins文件夹
2. 重启服务器或使用插件管理器重载
3. 权限配置：
   - 命令使用：确保需要使用命令的玩家拥有OP权限
   - 翻页笔使用：给需要使用翻页笔的玩家分配权限 `mccarnival.ppt.penuse`
   - 位置固定管理：给需要管理位置固定功能的玩家分配权限 `mccarnival.position.admin`