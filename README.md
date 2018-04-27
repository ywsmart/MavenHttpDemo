# MavenHttpDemo

接口自动化的实践项目。

## 主要功能模块

	1. 读写Excel解析（POI）
    2. 解析JSON参数（FastJSON）
	3. 多接口多方法请求（Excel）
	4. 验证返回结果（TestNG的Assert）
	5. 生成报告（ExtentReport/ReportNG）
	6. 运行结束提示（钉钉机器人）
	
## 技术栈

> 核心组件：Httpclient+jsonpath+TestNG+ExtentReport+log4j
> 调度组件：Maven+git+jenkins

## 运行
1. IDE工具直接执行testng.xml(即以testng形式运行)，IDE需安装好TestNG插件，运行结束会生成报告。
2. maven执行命令：clean test.

## 执行报告查看
1. testng.xml执行可视化报告：项目根目录/test-output/index.html
2. maven执行报告：项目根目录/target/test-output/index.html

## api-config.xml配置

> api请求根路径、请求头及初始化参数值可以在api-config上进行配置。

- rootUrl: 
必须的配置，api的根路径，在调用api时用于拼接，配置后，会在自动添加到用例中的url的前缀中。  
- headers: 
非必须配置，配置后在调用api时会将对应的name:value值设置到所有请求的请求头中header-name:header-value。
- params：
非必须配置，公共参数，通常放置初始化配置数据，所有用例执行前，会将params下所有的param配置进行读取并存储到公共参数池中，在用例执行时，使用特定的关键字(${param_name})可以获取。具体使用请参考下面的高级用法。
- project_name:
项目名称，会在html报告中使用

## 待优化

- 支持cookies
- 支持delete，put等方法
- 支持验证数据库
- 支持各种小工具函数（随机数等）
- 支持‘${param_name}’占位符
- 支持简单性能测试（部分Demo由刘凯迪提供）
- 支持持续集成
- 。。。