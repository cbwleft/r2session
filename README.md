# r2session
## What
r2session 的全称是 **Reactive** Redis proxy server to session，通俗来说是一个高并发、高性能的 Session 微服务，并集成了 SpringSession。

## Why
写这个项目的起因是因为有同事在项目中需要用到 `HttpSession`，
又因为公司运维的 Redis 屏蔽了某些命令导致无法直接使用 `spring-session-data-redis` 作为分布式 Session 实现，希望能够自己实现一套 Redis Session。
而 Leader 指出不应该让应用直接集成 Redis，而是应该由一个统一的 Session 微服务来实现这类需求，并且保证响应在 10ms 以内。
这一点我比较赞同，简单思考了下画出了如下的架构图。

![image](http://assets.processon.com/chart_image/5f638e975653bb28eb450279.png)

为了高性能，那么就要求服务必须是 **Reactive** 的，由少量线程来支持高并发，保证服务本身不成为瓶颈。

那么把 Session 服务化，除了增加额外的网络延迟以外，有什么好处呢？我觉得有以下几点：
- 应用无需自己申请 Redis，并且由 AppId 来确定多个服务之间是 Session 隔离，还是共享 Session
- 由于 Session 服务化了，可以很容易对 Session 进行打点，统计应用的 UV、在线人数以及趋势
- 如果上层和公司 CAS + SpringSecurity 进行集成，那么可以关联上 OneId，打通用户在数百个内部应用中的访问足迹
- 如果再集成权限中台，基本上通过一个 starter 就完成了大部分应用的骨架，减轻研发负担

## How
项目分为以下几个模块：
- spring-session：与 SpringSession 集成的模块，将 `setAttribute` 适配成普通的 JSON 字符串持久化操作
- api：r2session SDK，抽象 Session 操作，只需要将任意存储介质适配为类似于 `Map<String, Map<String, String>>` 的接口即可
- server：r2session Server
- test：在 Web 场景下的集成测试模块，并提供了开箱即用的 Mock 测试，和需要依赖外部 Redis 的集成测试

在参考了 `spring-session-data-mongodb` 和 `spring-session-data-redis` 后发现，
前者始终会拿最新 Session 覆盖，而后者只会在请求结束后批量修改所有变更的 key。前者有性能问题，后者过于复杂。
这个项目的实现方式是在 Session 发生变更时立刻进行持久化，并且抛弃了 `changeSessionId` 这类 99.9% 不会用到接口实现。
也因为 Redis 默认不开启 key event，抛弃了基本上用不着的 `SessionListener` 实现，整个代码实现上非常轻量，单元测试覆盖率高，以保证代码质量。

具体在 Web 项目中集成可以参考 test 模块的代码。

参考文档：http://note.youdao.com/noteshare?id=840ec9344311eeaf901fa401ad8881bd&sub=BBC4EB8DC1BC403F9D2286A63542AFFC