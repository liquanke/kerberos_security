# IOT security design

#### 介绍
基于Kerberos的简易局域网网盘系统

#### 软件架构
软件架构说明


#### 安装教程

1.  首先导入project项目（所有电脑都导入本项目），在三台电脑上分别运行Socket中的AS.java,TGS.java,V.java
2.  然后其余电脑（充当服务器的三台电脑也可）运行application中的Main.java即可
3. 

#### 使用说明

1.  Socket包中AS.java文件第36行（private static String ID_TGS= "192.168.43.244";），需要修改ID_TGS为TGS服务器的IP地址。
2.  Socket包中V.java文件第37行( private String FILE_PATH = "D:\\test\\file_disk\\";),需要修改FILE_PATh为服务器存储文件的路径，即用户上传成功后文件存储的路径。
3.  Test包中Client_Funtcion.java中第31,32,33行需要修改AS_IP，TGS_IP，V_IP为正确的IP地址。
4.  Test包中只有Client_Funtcion.java文件有用，其他皆为无用测试代码文件

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request
