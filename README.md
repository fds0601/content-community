# Content Community 轻量级内容社区系统

基于 Spring Boot 3 的后端练手项目，包含用户认证、RBAC 权限、文章管理、点赞评论、Redis 缓存等核心功能。

## 技术栈

| 分类 | 技术 |
|------|------|
| 框架 | Spring Boot 3.4 / Spring Security |
| 持久层 | MyBatis-Plus 3.5 / MySQL 8 |
| 缓存 | Redis (Lettuce) |
| 认证 | JWT (jjwt 0.12) |
| 接口文档 | Knife4j (OpenAPI3) |
| 构建 | Maven |
| JDK | 21+ |

## 功能模块

- **用户认证**：注册 / 登录，BCrypt 密码加密，JWT 无状态 Token
- **RBAC 权限**：USER / ADMIN 两种角色，接口级鉴权
- **文章管理**：发布、编辑、删除、分页列表、分类筛选
- **互动功能**：点赞（防重复）、评论
- **Redis 缓存**：文章详情缓存，10 分钟过期，更新/删除时主动失效
- **工程化**：全局异常处理、参数校验、统一返回体、Knife4j 接口文档

## 快速启动

### 1. 准备环境
- JDK 21+
- MySQL 8
- Redis

### 2. 初始化数据库
```bash
mysql -u root -p < sql/init.sql
```

### 3. 修改配置
编辑 `src/main/resources/application.yml`，改为你自己的 MySQL 密码和 Redis 地址。

### 4. 启动
```bash
mvn spring-boot:run
```

### 5. 访问接口文档
打开浏览器访问：http://localhost:8080/doc.html

## 接口示例

### 注册
```
POST /user/register
{
  "username": "test",
  "password": "123456",
  "nickname": "测试用户"
}
```

### 登录
```
POST /user/login
{
  "username": "test",
  "password": "123456"
}
```
返回 token，后续请求在 Header 中加：`Authorization: Bearer <token>`

### 发布文章
```
POST /article
Header: Authorization: Bearer <token>
{
  "title": "我的第一篇文章",
  "summary": "这是摘要",
  "content": "正文内容...",
  "category": "后端开发"
}
```

## 项目结构

```
src/main/java/com/example/community/
├── CommunityApplication.java    # 启动类
├── common/                      # 统一返回体、全局异常
├── config/                      # Security、MyBatisPlus、Knife4j、JWT过滤器
├── controller/                  # 接口层
├── service/                     # 业务层
├── mapper/                      # 数据访问层
├── entity/                      # 数据库实体
├── dto/                         # 请求参数
└── util/                        # JWT、用户上下文工具
```
