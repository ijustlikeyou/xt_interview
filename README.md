# 欣铁面试项目

已完成部门管理、用户部门关联、用户搜索与分页。

- 部门支持新增、编辑、查询和软删除。
- 用户可以选择或清空所属部门，列表显示部门名称。
- 用户列表支持用户名模糊搜索、状态和权限级别筛选。
- 分页使用原生 SQL，查询条件封装在 `AccountPageQuery` 中，默认第 0 页、每页 10 条。

## 运行

环境：Java 26、Maven 3.6+、Node.js、MySQL 8.0。

先创建 `xt_interview` 数据库，在 `src/main/resources/application-dev.properties` 中配置数据库连接，也可以通过 `SPRING_DATASOURCE_URL`、`SPRING_DATASOURCE_USERNAME` 和 `SPRING_DATASOURCE_PASSWORD` 环境变量配置。

在项目根目录启动后端：

```bash
mvn compile
mvn spring-boot:run
```

如果本机使用 JDK 21，可以执行：

```bash
mvn -Djava.version=21 -Dmaven.compiler.proc=none compile
mvn -Djava.version=21 -Dmaven.compiler.proc=none spring-boot:run
```

在 `frontend/web` 目录启动前端：

```bash
npm install
npm run dev
```

打开终端中显示的前端地址，使用 `admin / 123456` 登录。

后端启动时会自动执行 `db/migration` 下的 Flyway 脚本。`V2` 创建部门表并插入研发部、运维部；`V3` 为用户表添加部门外键。

功能和核心代码截图放在 `核心代码与要求功能截图` 目录。原始任务说明见 [INTERVIEW_TASKS.md](INTERVIEW_TASKS.md)。
