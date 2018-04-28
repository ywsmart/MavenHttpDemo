# MavenHttpDemo

接口自动化的实践小项目。

## 主要功能模块

	1. 读写Excel解析（POI）
    2. 解析JSON参数（FastJSON）
	3. 多接口多方法请求（Excel）
	4. 验证返回结果（TestNG的Assert）
	5. 生成报告（ExtentReport/ReportNG）
	6. 运行结束提示（钉钉机器人），邮件（Jenkins功能）
	
## 技术栈

> 核心组件：Httpclient+jsonpath+TestNG+ExtentReport+log4j
> 调度组件：Maven+git+jenkins

## 运行方法(重要)

1. IDE工具直接执行testng.xml(即以testng形式运行)，IDE需安装好TestNG插件，运行结束会生成报告。
2. maven执行命令：clean test.

## 执行报告查看

1. testng.xml执行可视化报告：项目根目录/test-output/index.html
2. maven执行报告：项目根目录/target/test-output/index.html

## 待优化

- 支持cookies
- 支持delete，put等方法
- 支持验证数据库
- 支持各种小工具函数（随机数等）
- 支持‘${param_name}’占位符
- 支持简单性能测试（部分Demo由刘凯迪提供）
- 支持持续集成
- 代码解耦
- Java反射机制
- 完善log4j的使用
- 。。。