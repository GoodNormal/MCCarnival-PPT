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

## 重构状态

**状态**: ✅ 已完成
**完成时间**: 重构已成功完成

## 重构结果

### 📁 新的包结构
```
src/main/java/org/MCCarnival/mCCarnivalPPT/
├── core/                    # 核心模块
│   └── MCCarnivalPPT.java  # 主插件类
├── ppt/                     # PPT翻页模块
│   ├── PPTCommand.java     # PPT命令处理
│   ├── PPTItemListener.java # PPT物品监听器
│   └── PostCommand.java    # 发放翻页笔命令
├── elevator/                # 电梯系统模块
│   ├── ElevatorCommand.java # 电梯命令处理
│   ├── ElevatorConfig.java  # 电梯配置管理
│   └── ElevatorListener.java # 电梯事件监听器
├── position/                # 玩家位置管理模块
│   ├── PlayerPositionManager.java # 位置管理器
│   └── PositionCommand.java # 位置命令处理
└── speaker/                 # 演讲者模式模块
    └── SpeakerCommand.java  # 演讲者命令处理
```

### 🔧 已完成的修改

1. **文件移动**: 所有Java文件已移动到对应的模块目录
2. **包声明更新**: 所有文件的package声明已更新为新的包路径
3. **导入语句修正**: 添加了必要的import语句以正确引用其他模块
4. **plugin.yml更新**: 主类路径已更新为`org.MCCarnival.mCCarnivalPPT.core.MCCarnivalPPT`
5. **编译验证**: 项目已成功编译，无错误

## 注意事项

1. **✅ 编译成功**: 所有代码已成功编译
2. **⚠️ 部署提醒**: 需要重新部署jar文件到服务器
3. **🔄 服务器重启**: 建议重启服务器以确保所有更改生效

## speaker命令未注册问题排查

经过检查和重构，`speaker`命令的注册代码和`plugin.yml`定义都是正确的：

1. ✅ `plugin.yml`中已正确定义`speaker`命令
2. ✅ `MCCarnivalPPT.java`的`onEnable`方法中已注册命令执行器
3. ✅ 代码编译无错误
4. ✅ 包结构已重构并正确引用

**解决方案**:
1. ✅ 重新编译插件: `gradle build` (已完成)
2. 🔄 将生成的jar文件复制到服务器plugins目录
3. 🔄 重启服务器或使用`/reload`命令重载插件
4. 🔍 检查服务器控制台是否有错误日志