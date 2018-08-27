# understand-plugin-framework

分析[DroidPlugin][1]，深入理解插件化框架，内容如下：

- [Hook机制之动态代理][2]
- [Hook机制之Binder Hook][3]
- [Hook机制之AMS&PMS][4]
- [Activity生命周期管理][5]
- [插件加载机制][6]
- [广播的处理方式][7]
- [Service的管理][8]
- [ContentProvider的管理][9]
- DroidPlugin插件通信机制
- 插件机制之资源管理
- 不同插件框架方案对比
- 插件化的未来

2018/8/27 更新：

从我写下 [Android插件化原理解析](http://weishu.me/2016/01/28/understand-plugin-framework-overview/) 系列第一篇文章至今，已经过去了两年时间。这期间，插件化技术也得到了长足的发展；与此同时，React Native，PWA，App Bundle，以及最近的Flutter也如火如荼。由于实现插件化需要太多的黑科技，它给项目的维护成本和稳定性增加了诸多不确定性；我个人认为，2017年手淘Atlas插件化项目的开源标志着插件化的落幕，2018年Android 9.0上私有API的限制几乎称得上是盖棺定论了——曾经波澜壮阔的插件化进程必将要退出历史主流。如今的插件化技术朝两个方向发展：其一，插件化的工程特性：模块化/解耦被抽离，逐渐演进为稳定、务实的的组件化方案；其二，插件化的黑科技特性被进一步发掘，inline hook/method hook大行其道，走向双开，虚拟环境等等。

虽然插件化终将落幕，但是它背后的技术原理包罗万象，值得每一个希望深入Android的小伙伴们学习。

很遗憾曾经的系列文章没有写完，现在已经没机会甚至可以说不可能去把它完结了；不过幸运的是，我的良师益友包老师（我习惯称呼他为包哥）写了一本关于插件化的书——《Android插件化开发指南》，书中讲述了过去数年浩浩荡荡的插件化历程以及插件技术的方方面面；有兴趣的小伙伴可以买一本看看。

[![点击购买](http://7xp3xc.com1.z0.glb.clouddn.com/201605/1535348090511.png)][10]

[1]: https://github.com/DroidPluginTeam/DroidPlugin
[2]: http://weishu.me/2016/01/28/understand-plugin-framework-proxy-hook/
[3]: http://weishu.me/2016/02/16/understand-plugin-framework-binder-hook/
[4]: http://weishu.me/2016/03/07/understand-plugin-framework-ams-pms-hook/
[5]: http://weishu.me/2016/03/21/understand-plugin-framework-activity-management/
[6]: http://weishu.me/2016/04/05/understand-plugin-framework-classloader/
[7]: http://weishu.me/2016/04/12/understand-plugin-framework-receiver/
[8]: http://weishu.me/2016/05/11/understand-plugin-framework-service/
[9]: http://weishu.me/2016/07/12/understand-plugin-framework-content-provider/
[10]: https://item.m.jd.com/product/31188356430.html?utm_source=iosapp&utm_medium=appshare&utm_campaign=t_335139774&utm_term=Wxfriends
