> <font size="5" >【目录】</font>  
> <font size="4">[1.前言](#前言)</font>  
> <font size="4">[2.初现端倪](#初现端倪)</font>  
> <font size="4">[3.款款深入](#款款深入)</font>  
> <font size="4">[4.责任细分](#责任细分])</font>  
> <font size="4">[5.功能层级图](#功能层级图)</font>  
> <font size="4">[6.项目结构](#项目结构)</font>  
> <font size="4">[7.关键类设计](#关键类设计)</font>  
> <font size="4">[8.一些设计想法](#一些设计想法)</font>  
> <font size="4">[9.待优化](#待优化)</font>  
> <font size="4">[10.一点心得](#一点心得)</font>  
> <font size="4">[11.效果演示](#效果演示)</font>  
> <font size="4">[12.项目导入及运行](#项目导入及运行)</font>  
> <font size="4">[13.版本变化](#版本变化)</font>  

## 前言
远程桌面控制的产品已经有很多很多，我做此项目的初衷并不是要开发出一个商用的产品，只是出于兴趣爱好，做一个开源的项目，之前也没有阅读过任何远程桌面控制的项目源码，只是根据自己已有的经验设计开发，肯定有许多不足，有兴趣的朋友欢迎留言讨论。

## 初现端倪
一般需要远程控制的场景发生在公司和家之间，由于公司和家里的电脑一般都在局域网内，所以不能直接相连，需要第三方中转，所以至少有三方,如下图。   
![](https://upload-images.jianshu.io/upload_images/6752673-bdfc1646a00bd3d8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

负责中转的第三方是服务器，控制端和傀儡端(被控制端)相对于服务器来说都是客户端，都和服务器直接相连，也就是说控制端不和傀儡端相连。

## 款款深入

> **约定:**
> - 控制端M(Master)
> - 服务器S(Server)
> - 傀儡端P(Puppet)

> **为了叙述方便,以下如不做特别说明,M表示控制端,S表示服务端,P表示傀儡端。**

如果要达到控制傀儡的目的，应该怎么做呢？三方之间至少要发生什么交互呢？
![三方会谈](https://upload-images.jianshu.io/upload_images/6752673-601784990497dcd4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

> 控制端、傀儡端的接收器和服务器中的转发器都是一个，为便于流程的清晰，分开画了。

## 责任细分
![责任细分](https://upload-images.jianshu.io/upload_images/6752673-434fe6b4600d463f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

可以看出三者交互主要通过命令形式(命令可以带数据也可以不带数据)，发送、转发、接收命令，然后做出相应的动作。
从上图中看到，服务端不仅需要转数据，还需要记录存活的傀儡以及维护控制端和傀儡之间的关系，其实还得处理一些异常情况，比如远程过程中，傀儡断开，过一会又连接上，傀儡是否需要继续给控制端发送屏幕截图。

## 功能层级图

粗粒度分一下，可以分为三层：Desktop层负责UI处理，CommandHandler层负责命令处理(接收和发送),Netty网络层负责数据的网络传输。

![功能层级图](https://upload-images.jianshu.io/upload_images/6752673-f8658fede3bab7ed.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

具体来看一下commandHandler层：
![commandhandler](https://upload-images.jianshu.io/upload_images/6752673-55b5f559f4823e05.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

CommandHandlerLoader工具类会根据Netty或Desktop层传入的Command到配置文件commandhandlers中查找对应的处理类，动态加载，然后进行逻辑处理，这样对于后期命令添加是非常方便的，命令与命令之间，以及命令与Netty/Deskto之间解耦。

## 项目结构
![总体顶目结构](https://upload-images.jianshu.io/upload_images/6752673-822b7d4301573cd8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

这个项目一共有四个子模块:
- **server**:  服务端
- **puppet**: 傀儡端
- **master**  控制端
- **common**: 前面三者共用的一些类或接口。
各个子模块的包结构类似，我们看其中的一个子模块puppet即可。
![puppet](https://upload-images.jianshu.io/upload_images/6752673-a4762e940be18b9a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

| 包名  |描述  |
|-- | -- |
|commandhandler|命令处理器|
|constants|常量类，包括配置参数常量、异常消息常量、和消息常量|
| exception|自定义的一些业务异常类 |
|netty|Netty网络通信的相关类|
|ui|界面操作的相关类|
|PuppetStarter|启动器类|
|Resources/commandhandlers|命令对应的处理器配置文件|

## 关键类设计
下面来看一下关键几个类的设计:
### 请求/响应类 Invocation
```java
public class Invocation implements Serializable {
    /**
     * ID(客户端标识(控制端为'M',傀儡端为'P')+MAC地址+序列号)
     */
    private String id;

    /**
     * 傀儡名
     */
    private String puppetName;

    /**
     * 命令
     */
    private Enum<Commands> command;

    /**
     * 值
     */
    private Object value;

    //省略getter、setter方法

    @Override
    public String toString() {
        return "Response{" +
                "requestId='" + requestId + '\'' +
                ", puppetName='" + puppetName + '\'' +
                ", command=" + command +
                ", value=" + value +
                '}';
    }
}
```
其中id的作用有两点：
1. 用于标识是来自M的请求，还是P的请求。 
2. 用于标识一次请求或响应，可以将M和P串联起来，用于请求追踪。  

Invocation类是一个基类，请求类(Request)和响应类(Response)在此基础之上扩展。
Invocation类中有一个成员变量是命令command，我们来看一下:

### 命令类 Commands
```java
/**
 * @author cool-coding
 * 2018/7/27
 * 命令
 */
public enum Commands{
    /**
     * 控制端或傀儡端连接服务器时的命令
     */
    CONNECT,

    /**
     * 控制命令
     * 1.主人向服务器发送控制请求
     * 2.服务器将控制命令发给傀儡
     * 3.傀儡收到控制命令，将向服务器发送截屏
     */
    CONTROL,

    /**
     * 傀儡发送心跳给服务器
     */
    HEARTBEAT,

    /**
     * 傀儡发送屏幕截图命令
     */
    SCREEN,

    /**
     * 控制端发送键盘事件
     */
    KEYBOARD,

    /**
     * 控制端发送鼠标事件
     */
    MOUSE,

    /**
     * 断开控制傀儡
     */
    TERMINATE,

    /**
     * 清晰度
     */
    QUALITY
}
```
目前一共有8个命令，有的命令是M和P共用，有的是一方单用。

### 命令处理接口 ICommandHandler
```java
public interface ICommandHandler<T> {
    /**
     * 
     * @param ctx           当前channel处理器上下文
     * @param inbound       channel输入对象
     * @throws Exception    异常
     */
    void handle(ChannelHandlerContext ctx,T inbound) throws Exception;
}
```
ICommandHandler接口是所有命令处理类的父接口，Netty ChannelHandler在处理请求时，根据不同的命令，寻找对应的处理类。

## 一些设计想法
### 心跳与屏幕截图
心跳和屏幕截图都是定时向服务器发送，所以在设计时这两者同时只有一个活动即可。即发送心跳时不发送屏幕截图，发送屏幕截图时不发送心跳，控制结束后，继续发送心跳。这两者之间的控制由Puppet模块中 ***ConnectCommandHandler*** 类中的 ***HeartBeatAndScreenSnapShotTaskManagement*** 内部类控制。

### 命令分层
通过对用例和流程的分析，发现命令出现的频率比较高，于是考虑将命令处理单独独立出来，采取动态加载的方式，使其与ChannelHandler解耦，使用后期扩展，而且当命令很多时，不需要一次都加载，只是在使用时按需加载，减少JVM加载类的字节码量，此处参考了SPI思想。而添加命令，势必会修改界面，我使用模板模式，预留出菜单，界面体，界面属性设置等，修改时只需继续相关类并修改，然后在spring配置文件进行配置即可。

### 序列号和Puppet名称生成器
请求和响应类中都有ID属性，其中一部分是通过序列号生成器生成的，所以提供了 *SequenceGenerate* 接口和一个简单的实现类SimpleSequenceGenerator。同理还有当傀儡连接服务器时，服务器生成唯一的傀儡名，也提供了一个简单的实现类SimplePuppetNameGenerator。

### 图像处理
图像的数据相对于纯命令来说大了许多，所以需要想办法减少图像传输的数据，大致有两种方式：
- **选择合适的图片格式，并进行压缩**：我这里选择了jpg格式，并使用Google Thumbnailator工具进行等宽高压缩，因为jpg具有较高的压缩比,但是代价是压缩后图像的质量不是太理想。
- 只传输变化的图像：很多时候图像变化的部分并不太多，可以只传输变化的区域，传输到控制端后，控制端只绘制变化的区域。  
    (1). **像素级别**: 我的思路是在傀儡端保持前一次传输时的截屏，和本次截屏图像进行像素级的比较，将不同的像素保存到一个对象数组中，记录像素的位置和像素值，传输到控制端后，根据像素位置和要替换的像素进行绘制    
    (2). **区域级别**：只记录变化图像的开始点(左上角)和结束点(右下角)，然后绘制以这两个点框定的矩形式区域。   
我尝试了这两种方式，没有达到很好的效果，由于时间有限，没有更深入研究，最终采取了压缩图像的方式。若有更好的方式，可以通过继承Puppet模块中抽象类*AbstractRobotReplay*，实现屏幕截屏方法*byte[] getScreenSnapshot()*,然后继承Master模块中抽像类*AbstractDisplayPuppet*实现其中的paint方法(也可以继承现有的实现类 *PuppetScreen* ，覆盖相应的方法)，然后将自定义的类在spring配置文件中配置，替换掉现在的实现类即可。

## 待优化
- 快速按键的情况、双击时响应的比较慢。传输命令需要时间，所以快速按键时命令产生滞后现象，而傀儡端图像传输到控制端后，Swing是单线程处理AWT事件(鼠标、键盘、绘图等)，若此时仍在按键，则会阻塞，等到按键结束之后，再进行图像的绘制。进行了如下尝试。
   > 1. 将命令发送采用异步方式，将命令存放在队列中，开启一个线程依次处理，这样可以减轻awt工作负担。
   > 2. 鼠标移动时，在移动过程中不发送命令，等待移动结束发送：实现方式是移动事件响应方式中添加一个计数器，再采用一个延迟线程，判断计数器值是否变化，如果延迟时间到时仍没有变化，则发送“移动命令”，但当移动后单击，会先发送单击命令，再发送鼠标移动命令，也不可行。
   > 3. 傀儡端在发送屏幕截图时，与上一次进行比较，如果没有变化，则不发送，减少发送数据量，也减少awt负担。

## 一点心得

1. 需求分析很重要，分析需求中各对象的属性和行为，以及对象之间的关系，这是后面功能、领域模型、静态/动态模型分析的基础。
2. 设计静态模型时，需要根据SOLID原则进行设计，例如远程控制中命令较多，就抽像出一层，为每个命令单独写处理逻辑(当然多个命令也可以共用同一处理逻辑)，既符合单一职责原则，又符合开闭原则，将影响降到最低，具体很大的灵活性。又如Master模块中的 ***IDisplayPuppet*** 接口，此接口是控制端显示傀儡屏幕的接口，供控制端主窗口 ***MasterDesktop*** 和 ***Listener*** 调用。
```java
/**
 * @author Cool-Coding
 *         2018/8/2
 * 傀儡控制屏幕接口
 */
public interface IDisplayPuppet {
    /**
     * 启动窗口显示傀儡桌面
     */
    void launch();
    /**
     * 刷新桌面
     * @param bytes
     */
    void refresh(byte[] bytes);
    /**
     *
     * @return 傀儡名称
     */
    String getPuppetName();
}
```
接口中这三个方法前两个方法launch和refresh，都是主窗口启动傀儡控制窗口和刷新屏幕必须的方法，第三个方法是由于发送命令时，需要知道傀儡名称，而实体之间是面向接口设计的，所以需要提供获取傀儡自身名称的方法。

3.日志、异常处理   

   日志和异常处理是相当重要的，好的日志记录方式和好的异常处理方式能够使项目结构更加清晰，怎么样才算好呢，人者见仁，智者见智。

  **日志**   
  
    1. 记录程序关键步骤的上下文信息，例如记录请求或响应的数据以及附加的消息，记录此处建议使用trace/debug级别。
    2. 记录业务流程的日志，使用info/error级别，这一部分日志主要是应用日志，例如控制端发起控制，成功或失败消息。
    3. 日志最好通过统一的口径记录，便于结构清晰和日志管理

  **异常**   
  
    1. 一定不要catch异常不处理，而且不要catch Throwable，因为Throwable包括了Error和Exception,Error一般都是不可恢复的错误，无法在程序中手工处理，不应该catch住。
    2. 一般下层在记录异常日志，并向上抛出后，上层不需要处理，直接继续向上抛出即可，如果为了让异常具体业务含义，便于异常问题查找，可以封装一些关键的业务异常。
    3. 异常最好集中处理,如springmvc:将异常集中在一个异常处理类中处理。

有两篇文章，我觉得不错，推荐给大家，我也从中参考了一些方法。

[Java 日志管理最佳实践](https://blog.csdn.net/f525921307/article/details/50519443)

[Java异常处理的10个最佳实践](http://www.importnew.com/20139.html)

## 效果演示

> - **Centos6.5**：傀儡端
> - **Windows**： 控制端、服务器
1. 启动服务器、傀儡、控制端   
2. 复制傀儡名   
![傀儡名](https://upload-images.jianshu.io/upload_images/6752673-802a8f5903d2aea6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  
也可以通过日志获取:
 ![](https://upload-images.jianshu.io/upload_images/6752673-573e31c59731cbd7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

3. 将名称输入控制端  
![](https://upload-images.jianshu.io/upload_images/6752673-8ae57ded64d33d94.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

4. 控制端打开一个远程屏幕   
![](https://upload-images.jianshu.io/upload_images/6752673-327308e1b21731d2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

5. 可以进行鼠标(单击，双击，右键，拖动等)或键盘(单键或组合键等)操作，并可调整屏幕清晰度。
![](https://github.com/Cool-Coding/photos/blob/master/remote-desktop-control/remote.gif)

## 项目导入及运行
1. IntelliJ IDEA
   File->New->Project from version control->Github/Git  
   输入Url:https://github.com/Cool-Coding/remote-desktop-control.git   
   ![](https://github.com/Cool-Coding/photos/blob/master/remote-desktop-control/import.png)
2. 导入后项目结构如下图，Maven会自动加载依赖的Jar包   
   ![](https://github.com/Cool-Coding/photos/blob/master/remote-desktop-control/import_result.png)
3. 调试运行
   - 配置子项目server/master/puppet resources文件夹下对应的配置文件server-config.txt/master-config.txt/puppet-config.txt
     主要是配置服务器IP与端口号，其它一般保持不变即可。
   - 运行子项目server/master/puppet 类ServerStarter/MasterStarter/PuppetStart
     配置好IP和端口后，分别运行Server/Master/Puppet端，Master和Puppet运行后会自动连接服务端，如果服务器不可用，
     Puppet会不断连接，而Master会报出错误消息，以后需要手工点击菜单连接。
4. 发布运行
   - File->Project Structure   
   ![](https://github.com/Cool-Coding/photos/blob/master/remote-desktop-control/deploy01.png)
   - 创建Artifacts
     - From Modules with dependencies   
     ![](https://github.com/Cool-Coding/photos/blob/master/remote-desktop-control/deploy02.png)
     - 选择Module和Main Class
       > Moudle应总是选择desktop-control-parent  
       > Main Class根据服务器、控制端、傀儡端需要选择对应的启动类   
       ![](https://github.com/Cool-Coding/photos/blob/master/remote-desktop-control/deploy03.png)
     - 打包common包和对应子项对应的源文件和依赖的jar包   
     > 截图中只有common包的操作方法，其它子项目也是相同操作方法,不再截图,如打包Master，则需要打包common和master;打包puppet，则需要打包
     common和puppet;打包server,则需要打包common和server。
         
     (1) 修改name名称   
     (2) 选中common包下的'common' compile output,右键选择Put into Output Root   
     (3) 选择common包下所有依赖的jar包，右键选择Extract Into Output Root     
     ![](https://github.com/Cool-Coding/photos/blob/master/remote-desktop-control/deploy04.png)  
     ![](https://github.com/Cool-Coding/photos/blob/master/remote-desktop-control/deploy05.png)
     - Build->Build Artifacts->build对应的artifact   
     ![](https://github.com/Cool-Coding/photos/blob/master/remote-desktop-control/deploy06.png)
   - 运行打好的jar包
       > java -jar xxxx.jar
      
## 版本变化

1. V0.1.0 
> 20180802
- 实现控制端Master通过鼠标键盘远程控制傀儡端Puppet   
2. V0.1.1
> 20180916
- master: 命令使用队列方式，单线程消费，减轻awt压力，加快响应屏幕刷新
- puppet获取屏幕截图时，若前后两次获取的屏幕截图无变化，则不发送。
- bugs fixed
  - puppet重连时之前发送心跳的任务仍在运行  
3. V0.1.2
> 20181221
- 当puppet重连服务端时，保持ID不变
- master的命令保存到阻塞队列中，再由单线程取出发给服务器
   多个mouse moved命令，只发送最后一个mouse moved命令，减少
   无效命令和占用的带宽流量
- bugs fixed
   - puppet两次截图不变化，则不再发送截图，如果master断开，则无法检测到，修改为屏幕截图无变化时，发送心跳数据包
   - master由于某些原因在没有向puppet发送TERMINATE命令时断开，则Master再次请求被控制Puppet时，
   若puppet中本次截图与上次截图数据一样，则不发送，则Master控制端会不显示puppet屏幕截图
- 优化体验   
   - master向puppet发送命令失败，如果鼠标移动，则会一有移动就发送，导致反复出现相同消息
      
## 讨论
**bug反馈及建议**：https://github.com/Cool-Coding/remote-desktop-control/issues
