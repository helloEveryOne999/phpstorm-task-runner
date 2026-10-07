# Task Action Runner

在 PhpStorm 中，将光标放在 OA 项目 `app/tasks/*Task.php` 的 `*Action` 方法内，右键选择“运行当前 Task Action”。插件会显示可编辑命令和附加参数的确认窗口；确认后在项目根目录的新终端标签执行命令。

## 命令规则

- 优先读取方法 docblock 中的 `php app/cli.php <task> <action>` 或 `php bin/cli <task> <action>`。
- docblock 中的示例参数不会自动带入。
- 缺少命令注释时，按类名去除 `Task` 后转 kebab-case，并按方法名去除 `Action` 推导。
- 推导命令的默认入口为 `php app/cli.php`。

## 构建

本机安装 PhpStorm 2026.2.2 后执行：

```bash
JAVA_HOME=/Applications/PhpStorm.app/Contents/jbr/Contents/Home ./gradlew buildPlugin
```

构建结果位于 `build/distributions/phpstorm-task-runner-0.1.0.zip`。在 PhpStorm 的插件设置中选择“从磁盘安装插件”导入该 ZIP，然后重启 IDE。

插件不预设快捷键。可在 Settings | Keymap 中搜索“运行当前 Task Action”并自行绑定。
