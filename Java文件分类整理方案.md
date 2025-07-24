# MCCarnival-PPT Java文件分类整理方案

## 当前文件结构问题
当前所有Java文件都放在同一个包下，缺乏模块化组织，不利于代码维护和扩展。

## 建议的分类结构

### 1. 核心模块 (core)
```
org.MCCarnival.mCCarnivalPPT.core/
├── MCCarnivalPPT.java          # 主插件类
```

### 2. PPT翻页模块 (ppt)
```
org.MCCarnival.mCCarnivalPPT.ppt/
├── PPTCommand.java             # PPT翻页命令处理
├── PPTItemListener.java        # PPT翻页笔监听器
├── PostCommand.java            # PPT高级设置命令
```

### 3. 电梯系统模块 (elevator)
```
org.MCCarnival.mCCarnivalPPT.elevator/
├── ElevatorCommand.java        # 电梯命令处理
├── ElevatorConfig.java         # 电梯配置管理
├── ElevatorListener.java       # 电梯事件监听器
```

### 4. 玩家位置管理模块 (position)
```
org.MCCarnival.mCCarnivalPPT.position/
├── PlayerPositionManager.java  # 玩家位置管理器
├── PositionCommand.java        # 位置管理命令
```

### 5. 演讲者模式模块 (speaker)
```
org.MCCarnival.mCCarnivalPPT.speaker/
├── SpeakerCommand.java         # 演讲者模式命令
```

## 重构后的完整目录结构

```
src/main/java/org/MCCarnival/mCCarnivalPPT/
├── core/
│   └── MCCarnivalPPT.java
├── ppt/
│   ├── PPTCommand.java
│   ├── PPTItemListener.java
│   └── PostCommand.java
├── elevator/
│   ├── ElevatorCommand.java
│   ├── ElevatorConfig.java
│   └── ElevatorListener.java
├── position/
│   ├── PlayerPositionManager.java
│   └── PositionCommand.java
└── speaker/
    └── SpeakerCommand.java
```

## 重构的好处

1. **模块化清晰**：每个功能模块独立，便于维护
2. **代码组织**：相关功能聚合在一起，提高可读性
3. **扩展性好**：新增功能时可以创建新的模块包
4. **团队协作**：不同开发者可以专注于不同模块
5. **测试友好**：可以针对每个模块进行单独测试

## 重构步骤

1. 创建新的包结构目录
2. 移动对应的Java文件到新包中
3. 更新所有文件的package声明
4. 更新import语句
5. 更新plugin.yml中的main类路径
6. 重新编译测试

## 注意事项

- 重构时需要同时更新所有相关的import语句
- plugin.yml中的main类路径需要更新为新的包路径
- 确保所有跨模块的类引用都正确更新
- 建议在重构前做好代码备份

## Speaker命令注册问题排查

根据检查，speaker命令的注册代码是正确的：
- 在MCCarnivalPPT.java中已正确注册
- 在plugin.yml中已正确定义
- 编译也没有错误

可能的问题：
1. 服务器需要重启才能加载新的命令
2. 插件可能需要重新加载
3. 检查服务器控制台是否有错误信息

建议：
1. 重启服务器或重新加载插件
2. 检查服务器日志中是否有相关错误
3. 使用 `/help speaker` 命令检查是否已注册