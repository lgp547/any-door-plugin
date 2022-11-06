# AnyDoorPlugin 任意门插件
简单来说就是执行任意对象的任意方法

需要是Spring web项目，会在原有项目提供一个对外的路径(/any_door/run)，该路径可以调用到任意一个方法

## 适合场景
- xxlJob
- rpc
- mq入口
- 小改动的测试

## IDEA插件使用
本插件只需每个项目配置一次即可
### 添加插件
待补充

### 配置插件属性并进行导入
1. 配置插件属性
2. 点击Try按钮（成功or失败会有消息提示）

![img.png](dosc/image/插件配置说明.jpg)

### 启动项目
略

### 执行调用
1. 找到想要执行的方法，右键弹出选择（有对应的快捷键）

![img.png](dosc/image/打开方法选择.png)

2. 选择打开任意门

![img.png](dosc/image/打开任意门.png)

3. 填写要调用的参数，并执行启动！

![img.png](dosc/image/启动.png)

4. 你将会发现当前方法被执行了！（可进行断点查看）

## 插件原理
分两件事：导入jar包、调用项目接口
### 导入jar包

导入jar包，实际就是优先本地的AnyDoor的jar包导入到当前项目模块（若本地Maven配置目录没有，将会从中央仓库下载并放到本地仓库），

导入成功后可以在设置页面查看，当然删除也是在这里

![img.png](dosc/image/jar包导入.jpg)

![img.png](dosc/image/插入的maven路径.png)

### 调用项目接口
就是HTTP请求，任意门（AnyDoor）项目开放的路径

## 后续支持
- [x] 对于没有参数的直接进行调用

- [ ] 参数缓存
- [ ] **重要功能** 不依赖端口号进行调度到执行的项目
- [ ] 输入参数窗口，对于是对象的，直接生成json格式内容
- [ ] 增加快捷键直接调用当前方法（无须在具体方法名上）



