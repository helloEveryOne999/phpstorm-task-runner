# Task Action Runner Implementation Plan

**Goal:** 在 PhpStorm 2026.2.2 中，从 `app/tasks/*Task.php` 的 Action 方法启动对应 CLI 命令。

**Architecture:** 编辑器 Action 通过 PHP PSI 解析当前方法和类；解析器优先从 docblock 取命令的任务/动作部分，失败时采用命名推导。确认对话框产生最终命令，由终端执行器在项目根目录新建经典终端会话后运行。

**Tech stack:** Kotlin、Gradle、IntelliJ Platform Gradle Plugin、PhpStorm 2026.2.2。

## 实施清单

- [x] 确认 PhpStorm 版本为 2026.2.2 / 262.10315.130，并使用其内置 JBR 25。
- [x] 确认 PHP 和 Terminal 插件 ID，以及经典终端的 `createLocalShellWidget`、`executeCommand` API。
- [x] 建立 Gradle 插件工程，目标产品锁定 PhpStorm 2026.2.2。
- [x] 实现 Task Action 解析、默认 `php app/cli.php` 推导及 docblock 优先规则。
- [x] 实现确认对话框和每次新建终端标签的执行器。
- [x] 注册编辑器上下文菜单 Action，不预设快捷键。
- [x] 添加解析器单元测试。
- [x] 构建 ZIP，并通过 IntelliJ Plugin 项目配置校验检查兼容性。
