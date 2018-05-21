# MavenHttpDemo

接口自动化的实践小项目（暂未取名）。

## 主要功能

	1. 读写Excel解析（POI）
    2. 解析JSON参数（FastJSON）
	3. 多接口多方法请求（Excel）
	4. 验证返回结果（TestNG的Assert）
	5. 生成报告（ExtentReport/ReportNG）
	6. 运行结束提示（钉钉机器人），邮件（需另借助Jenkins功能）
	
## 技术栈

- 核心组件：Httpclient+JsonPath+POI+TestNG+ExtentReport+Log4j
- 调度组件：Maven+Git+Jenkins

## 使用方法(重要)

1. 在Excel文件（项目根目录\src\main\resources\rest_infos.xlsx）里，准备在测试用例（填好表），目前request_data的sheet里只支持前五列，其他待开发。
2. IDE工具直接执行testng.xml(即以testng形式运行)，IDE需安装好TestNG插件；或者maven执行命令：clean test（也可通过Jenkins构建）.
3. 使用前请确定钉钉机器人的WEBHOOK_TOKEN准确性，查看地址src\main\com.yvan.util.MsgUtil.

## 结果查看

1. testng.xml执行可视化报告：项目根目录/test-output/index.html
2. maven执行报告：项目根目录/target/test-output/index.html
3. 请求结果可查看Excel回写内容：项目根目录\target\classes\rest_infos.xlsx（未执行前无此文件）

## 执行流程

开始→读取testng.xml→测试范围（suite）→测试前初始化（读取测试数据Excel）→测试执行（TestNG、Assert）→测试结束（数据回写Excel、提示、报告）→结束

## 待优化

- 利用Java反射机制
- 代码解耦
- 完善测试演示Demo
- 支持cookies
- 支持delete，put等方法
- 支持验证数据库
- 支持各种小工具函数（随机数等）
- 支持‘${param_name}’等占位符
- 支持简单性能测试（此Demo由思摸迪提供）
- 支持持续集成
- 完善log4j的使用
- 待加。。。