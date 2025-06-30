# 🐟 二手交易平台

---

## 一、项目概述

本项目为一款二手物品交易平台，基于前后端分离架构，用户可发布商品、浏览商品、留言交流、收藏感兴趣商品，并支持基础交易功能，含后台管理模块（可选）。新增AI智能服务模块，用户可通过智能对话获取个性化商品推荐。系统设计简洁、功能完整，适合作为高校毕设、课程设计、个人实战项目及中小型商业原型开发。

---

## 二、核心技术栈

| 层次 | 技术选型                                                                                                                |
|----|---------------------------------------------------------------------------------------------------------------------|
| 后端 | Spring Boot 3.4.6、MyBatis-Plus、MySQL 8.x、Redis、Spring Security + JWT、Lombok、Knife4j（Swagger增强）、本地文件存储、DeepSeek AI接口 |
| 前端 | Vue 3.5.13、TypeScript、Vite 6.3.5、Pinia 3.0.3、Vue Router 4.5.1、Axios 1.10.0、Element Plus 2.10.2                      |                                                       |
| 其他 | Maven、Nginx、Postman                                                                                                 |

---

## 三、主要功能模块

### 1. 用户模块

- 用户注册、登录（JWT认证，支持令牌刷新机制，增强令牌验证安全性）

- 个人资料管理（昵称、头像、手机号等，含手机号格式验证）

- 我的发布、收藏、交易记录

- 头像上传

- 安全登出功能（支持令牌黑名单管理，确保退出后令牌立即失效）

- 密码安全策略（至少8位，包含字母和数字，增强密码加密存储）

- 用户信息缓存优化（Redis缓存减轻数据库压力，提升系统响应速度）

### 2. 商品模块

- 发布二手商品（主图上传、多图上传）

- 商品浏览、模糊搜索

- 商品详情查看

### 3. 交易模块

- 模拟订单下单、交易订单查看

### 4. 留言/收藏模块

- 买家留言 / 卖家回复
- 收藏/取消收藏商品

### 5. AI智能服务模块

- 用户在首页打开聊天窗口，与AI智能助手对话

- AI回复等待时的消息气泡动画和loading闪点效果，营造AI“思考”的视觉体验

- AI根据用户描述需求，智能推荐匹配商品

- AI对话接口支持多轮对话，提升交互体验

---

## 四、项目目录结构

### 后端（`secondhand_backend`）

```
secondhand_backend
│  .gitignore
│  pom.xml
└── src\
    └── main\
        ├── java/com/seateam/secondhand_system/
        │   ├── common  # 公共类
        │   │   ├── config  # 配置类(CorsConfig、SecurityConfig等)
        │   ├── ai  # AI服务相关类(DeepSeekApiClient等)
        │   ├── utils  # 工具类(FileUploadUtils等)
        │   │   └── exception  # 异常处理类
        │   ├── controller/  # REST API 控制器
        │   ├── entity/  # 实体类
        │   ├── mapper/  # MyBatis-Plus 映射接口
        │   ├── service/  # 业务逻辑接口及实现
        │   └── SecondhandSystemApplication.java # 启动类
        └── resources/
            ├── application.yml  # 配置文件
            ├── mapper/*.xml  # MyBatis XML文件
            └── static/  # 静态资源
```

### 前端（`secondhand_frontend`）

```
secondhand_frontend
│  .env
│  .gitignore
│  index.html
│  package-lock.json
│  package.json
├── public\
│  └── vite.svg
└── src\
    ├── App.vue
    ├── api\  # Axios封装与接口请求
    │   ├── ai.ts  # AI服务接口封装
    │   ├── user.ts
    │   ├── goods.ts
    │   └── ...
    ├── assets\  # 静态资源
    ├── components\  # 公共组件
    │   ├── AiChat.vue  # AI聊天对话框组件，支持消息展示、商品推荐卡片和Markdown渲染
    │   ├── ImageUpload.vue  # 图片上传组件，支持多图上传、预览和删除
    │   └── ...
    ├── main.ts  # 应用入口文件
    ├── pages\  # 路由页面组件
    ├── router\  # Vue Router 路由配置
    │   └── index.ts
    ├── store\  # Pinia 状态管理
    │   ├── ai.ts  # AI相关状态管理
    │   ├── user.ts
    │   ├── goods.ts
    │   └── ...
    ├── style.css
    ├── views\  # 视图组件
    └── vite-env.d.ts
```

## 五、数据库设计

### 1. 用户表（`user`）

| 字段名         | 类型                                 | 允许为空 | 默认值               | 说明                                     |
|-------------|------------------------------------|------|-------------------|----------------------------------------|
| id          | BIGINT AUTO_INCREMENT              | 否    | —                 | 主键，自增                                  |
| username    | VARCHAR(50)                        | 否    | —                 | 用户名，唯一约束                               |
| password    | VARCHAR(255)                       | 否    | —                 | 密码（加密存储）                               |
| avatar      | VARCHAR(255)                       | 是    | NULL              | 头像URL地址                                |
| phone       | VARCHAR(20)                        | 是    | NULL              | 手机号                                    |
| role        | ENUM('user', 'admin', 'merchants') | 否    | 'user'            | 用户角色（user-普通用户，admin-管理员，merchants-商家） |
| create_time | DATETIME                           | 否    | CURRENT_TIMESTAMP | 注册时间                                   |

---

### 2. 商品表（`goods`）

| 字段名         | 类型                    | 允许为空 | 默认值               | 说明             |
|-------------|-----------------------|------|-------------------|----------------|
| id          | BIGINT AUTO_INCREMENT | 否    | —                 | 主键，自增          |
| user_id     | BIGINT                | 否    | —                 | 发布用户ID         |
| title       | VARCHAR(100)          | 否    | —                 | 商品标题           |
| description | TEXT                  | 是    | NULL              | 商品描述           |
| price       | DECIMAL(10,2)         | 否    | —                 | 商品价格           |
| category    | VARCHAR(50)           | 是    | NULL              | 商品分类           |
| status      | TINYINT               | 否    | 0                 | 商品状态：0-在售，1-已售 |
| create_time | DATETIME              | 否    | CURRENT_TIMESTAMP | 发布时间           |

---

### 3. 收藏表（`favorite`）

| 字段名      | 类型                    | 允许为空 | 默认值 | 说明     |
|----------|-----------------------|------|-----|--------|
| id       | BIGINT AUTO_INCREMENT | 否    | —   | 主键，自增  |
| user_id  | BIGINT                | 否    | —   | 收藏用户ID |
| goods_id | BIGINT                | 否    | —   | 商品ID   |

---

### 4. 留言表（`message`）

| 字段名           | 类型                    | 允许为空 | 默认值               | 说明         |
|---------------|-----------------------|------|-------------------|------------|
| id            | BIGINT AUTO_INCREMENT | 否    | —                 | 主键，自增      |
| goods_id      | BIGINT                | 否    | —                 | 关联商品ID     |
| user_id       | BIGINT                | 否    | —                 | 留言用户ID     |
| content       | TEXT                  | 否    | —                 | 留言内容       |
| reply_content | TEXT                  | 是    | NULL              | 卖家回复内容（可空） |
| reply_time    | DATETIME              | 是    | NULL              | 卖家回复时间（可空） |
| create_time   | DATETIME              | 否    | CURRENT_TIMESTAMP | 留言时间       |

---

### 5. 订单表（`order`）

| 字段名         | 类型                    | 允许为空 | 默认值               | 说明               |
|-------------|-----------------------|------|-------------------|------------------|
| id          | BIGINT AUTO_INCREMENT | 否    | —                 | 主键，自增            |
| buyer_id    | BIGINT                | 否    | —                 | 买家ID             |
| seller_id   | BIGINT                | 否    | —                 | 卖家ID             |
| goods_id    | BIGINT                | 否    | —                 | 商品ID             |
| price       | DECIMAL(10,2)         | 否    | —                 | 成交价格             |
| status      | TINYINT               | 否    | 0                 | 订单状态：0-未付款，1-已完成 |
| create_time | DATETIME              | 否    | CURRENT_TIMESTAMP | 下单时间             |

---

### 6. AI对话表（`ai_chat`）

| 字段名         | 类型                    | 允许为空 | 默认值               | 说明                     |
|-------------|-----------------------|------|-------------------|------------------------|
| id          | BIGINT AUTO_INCREMENT | 否    | —                 | 主键，自增                  |
| user_id     | BIGINT                | 否    | —                 | 用户ID                   |
| session_id  | VARCHAR(100)          | 否    | —                 | 对话会话ID（多轮对话标识）         |
| message     | TEXT                  | 否    | —                 | 用户输入内容                 |
| response    | TEXT                  | 是    | NULL              | AI回复内容（SQL未指定NOT NULL） |
| create_time | DATETIME              | 否    | CURRENT_TIMESTAMP | 记录时间                   |

---

## 六、后端核心接口设计（RESTful规范）

### 1. 用户模块

| 方法   | 路径                        | 描述             |
|------|---------------------------|----------------|
| POST | `/api/user/register`      | 用户注册           |
| POST | `/api/user/login`         | 用户登录，返回JWT     |
| GET  | `/api/user/info`          | 获取当前用户信息（需JWT） |
| POST | `/api/user/update`        | 更新用户信息（昵称、头像等） |
| POST | `/api/user/refresh-token` | 刷新访问令牌         |
| POST | `/api/logout`             | 用户登出           |
| GET  | `/api/user/{userId}`      | 根据用户ID获取用户信息   |

### 2. 商品模块

| 方法   | 路径                        | 描述                                     |
|------|---------------------------|----------------------------------------|
| POST | `/api/goods/publish`      | 发布新商品（multipart/form-data格式，支持图片上传）    |
| POST | `/api/goods/publish`      | 发布新商品（application/json格式，纯数据提交）        |
| GET  | `/api/goods/list`         | 商品分页列表（支持pageNum、pageSize参数，默认第1页10条）  |
| GET  | `/api/goods/detail/{id}`  | 获取商品详情                                 |
| GET  | `/api/goods/search`       | 关键词搜索商品（支持pageNum、pageSize参数，默认第1页10条） |
| POST | `/api/goods/upload-image` | 上传商品图片，返回图片访问路径                        |

### 3. 收藏模块

| 方法     | 路径                     | 描述                                      |
|--------|------------------------|-----------------------------------------|
| POST   | `/api/favorite/add`    | 添加收藏                                    |
| DELETE | `/api/favorite/remove` | 取消收藏（需要goodsId参数）                       |
| GET    | `/api/favorite/list`   | 获取我的收藏列表（支持pageNum、pageSize参数，默认第1页10条） |

### 4. 留言模块

| 方法   | 路径                               | 描述        |
|------|----------------------------------|-----------|
| POST | `/api/message/leave`             | 发表留言      |
| POST | `/api/message/reply/{messageId}` | 回复留言      |
| GET  | `/api/message/list/{goodsId}`    | 获取某商品留言列表 |

### 5. 订单模块

| 方法   | 路径                  | 描述       |
|------|---------------------|----------|
| POST | `/api/order/create` | 生成订单     |
| GET  | `/api/order/my`     | 我的订单列表## |

### 6. AI智能服务模块

| 方法   | 路径             | 描述                 | 请求体类型           | 响应体类型            |
|------|----------------|--------------------|-----------------|------------------|
| POST | `/api/ai/chat` | 发送用户对话，获取AI回复及推荐商品 | `AiChatRequest` | `AiChatResponse` |

#### 请求参数（AiChatRequest）

| 参数名       | 类型     | 是否必须 | 描述                     |
|-----------|--------|------|------------------------|
| userId    | Long   | 是    | 用户ID                   |
| sessionId | String | 否    | 对话会话ID（多轮对话标识，首次调用可不传） |
| message   | String | 是    | 用户输入消息内容               |

请求示例：

```json
{
  "userId": 123,
  "sessionId": "uuid-xxxx-xxxx",
  "message": "我想买一台价格在1000元左右的二手手机"
}
```

#### 响应参数（AiChatResponse）

| 参数名       | 类型      | 描述                 |
|-----------|---------|--------------------|
| reply     | String  | AI回复文本内容           |
| sessionId | String  | 当前对话会话ID           |
| list      | Goods[] | 推荐商品列表（为空时表示无匹配商品） |

响应示例：

```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "reply": "根据您的需求，我推荐以下几款手机...",
    "sessionId": "uuid-xxxx-xxxx",
    "list": [
      {
        "id": 12,
        "title": "二手iPhone 11",
        "price": 980.00,
        "image": "/uploads/iphone11.jpg"
      },
      {
        "id": 18,
        "title": "二手小米10",
        "price": 1050.00,
        "image": "/uploads/xiaomi10.jpg"
      }
    ]
  }
}
```

---

## 七、前端实现详情

### 特色功能：图片上传组件

商品发布页面(`Publish.vue`)实现了多图上传功能，支持以下特性：

- 最多上传5张商品图片
- 图片预览、删除、重试上传功能
- 上传进度显示
- 图片格式验证(仅允许jpg、png格式)

### 特色功能：AI推荐实现细节

AI推荐功能采用混合推荐策略

1. **数据库优先搜索**：接收用户查询后，首先通过关键词搜索商品数据库
2. **结果判断**：若搜索到匹配商品(如价格、类别匹配)，直接返回商品推荐
3. **AI调用降级**：仅当数据库搜索无结果时，才调用DeepSeek API获取AI生成的推荐内容

增强用户体验

- AI回复等待时的消息气泡动画和loading闪点效果，营造AI“思考”的视觉体验

#### AI聊天接口封装

前端通过`src/api/ai.ts`封装AI服务相关接口

**功能说明**：

- `sendAiChatMessage`：发送用户对话内容到后端AI服务接口，支持多轮对话（通过sessionId标识）

### UI框架与设计

项目采用Element Plus作为主要UI组件库，实现了统一的设计语言和响应式布局，包括：

- 简约主题配色方案，符合二手交易平台清爽的视觉定位
- 响应式布局设计，适配桌面端与移动端浏览
- 统一的卡片式组件设计，用于商品展示、用户信息等模块
- 交互动效优化，包括按钮悬停效果、页面过渡动画和加载状态反馈

### 1. 状态管理

项目使用 Pinia 进行状态管理，采用模块化设计，主要 store 文件及核心功能如下：

#### 核心 Store

- `store/user.ts`: 用户状态管理
    - 状态：`userInfo`（用户信息）、`accessToken`（访问令牌）、`isLogin`（登录状态）
    - 核心方法：`login()`、`logout()`、`fetchUserInfo()`、`refreshToken()`

- `store/goods.ts`: 商品状态管理
    - 状态：`goodsList`（商品列表）、`currentGoods`（当前商品详情）、`total`（商品总数）
    - 核心方法：`fetchGoodsList()`、`searchGoods()`、`fetchGoodsDetail()`

- `store/ai.ts`: AI服务状态管理
    - 核心方法：
        - `sendMessage(content: string)`: 发送消息并处理AI响应
        - `clearHistory()`: 清除聊天历史
        - `fetchHistory()`: 获取历史记录（本地模拟）

- `store/favorite.ts`: 收藏状态管理
- `store/message.ts`: 留言状态管理
- `store/order.ts`: 订单状态管理

#### 状态管理流程示例（AI对话）

1. 用户输入消息 → 调用 `aiStore.sendMessage()`
2. 存储用户消息到 `messages` 数组
3. 调用API获取AI响应 → 更新 `messages` 和 `recommendedGoods`
4. 组件通过 `useAiStore()` 获取状态并渲染界面

### 2. 核心页面组件

- `views/Home.vue`: 首页，展示商品列表及搜索功能
- `views/GoodsDetail.vue`: 商品详情页
- `views/Publish.vue`: 商品发布页
- `views/Login.vue`: 登录注册页
- `views/UserCenter.vue`: 用户中心，包含我的发布、收藏、订单等
- `views/Favorites.vue`: 我的收藏页面

### 3. API 请求

所有 API 请求通过 `src/api/request.ts` 封装的 Axios 实例实现，包含请求/响应拦截器统一处理：

#### 请求拦截器

自动添加 Authorization 请求头（Bearer Token），实现 JWT 身份验证

#### 响应拦截器

统一错误处理，包含 401/403 权限错误处理和自动登出逻辑。

#### 接口模块划分

所有 API 请求封装在 `src/api/` 目录下，按功能模块划分：

- `api/user.ts`: 用户相关接口
- `api/goods.ts`: 商品相关接口
- `api/favorite.ts`: 收藏相关接口
- `api/message.ts`: 留言相关接口
- `api/order.ts`: 订单相关接口

### 4. 应用初始化配置

应用入口文件 `src/main.ts` 负责初始化 Vue 应用并集成核心依赖

### 5. 路由配置

路由配置在 `src/router/index.ts` 文件中，采用 Vue Router 进行路由管理，实现了页面间的导航和权限控制。主要路由定义如下：

| 路径                  | 组件                      | 说明     | 权限要求 |
|---------------------|-------------------------|--------|------|
| `/`                 | `pages/Home.vue`        | 首页     | 无需登录 |
| `/login`            | `pages/Login.vue`       | 登录页    | 无需登录 |
| `/register`         | `pages/Register.vue`    | 注册页    | 无需登录 |
| `/user-center`      | `pages/UserCenter.vue`  | 用户中心主页 | 需要登录 |
| `/goods/detail/:id` | `pages/GoodsDetail.vue` | 商品详情页  | 需要登录 |
| `/publish`          | `pages/Publish.vue`     | 商品发布页  | 需要登录 |
| `/user/profile`     | `views/UserProfile.vue` | 用户资料页  | 需要登录 |
| `/favorites`        | `views/Favorites.vue`   | 我的收藏页  | 需要登录 |
| `/orders`           | `views/OrderList.vue`   | 我的订单页  | 需要登录 |

**路由守卫实现**：系统通过全局路由守卫控制页面访问权限，未登录用户访问需要授权的页面时会自动重定向到登录页。核心逻辑如下：

```typescript
// 路由守卫实现
router.beforeEach((to, _from, next) => {
    const token = localStorage.getItem('accessToken')
    // 不需要登录的页面
    const publicPages = ['/login', '/register']
    const requiresAuth = !publicPages.includes(to.path)

    if (requiresAuth && !token) {
        next('/login')
    } else {
        next()
    }
})
```

## 八、运行与开发说明

### 系统配置补充

#### 后端配置(`application.yml`)

```yaml
# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/secondhand?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
  # Redis配置
  data:
    redis:
      port: 6379
      password: 123456
      host: localhost
  # AI服务配置
  ai:
    deepseek:
      api-key: 填入获取到的Key
      endpoint: "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation"

# 文件上传配置
upload:
  path: "d:/SecondhandSystem/uploads/"

# JWT配置
jwt:
  secret: secondhand_system_jwt_secret_key_2025
  expiration: 86400000
```

### 后端启动

1. 配置 `application.yml`（数据库、Redis、OSS、AI服务模拟参数）

2. 导入数据库初始化脚本（`secondhand_backend/src/main/resources/sql/init.sql`）初始化数据库

3. 启动 Spring Boot 项目：

```bash
cd secondhand_backend
mvn clean package
java -jar target/second-hand-market-1.0.jar
```

### 前端启动

1. 配置环境变量（位于前端根目录 `.env` 文件）：
   ```
   VITE_APP_API_URL=http://localhost:7272/api  # 后端API基础地址
   VITE_APP_BASE_URL=http://localhost:7272     # 后端基础地址
   ```
   根据实际后端服务地址修改上述值

2. 启动前端项目：

```bash
cd secondhand_frontend
npm install
npm run dev
```

---

## 八、API文档（Openapi）

> 访问地址：

```
http://localhost:7272/doc.html
```

---

## 作者信息

- Author: [River Xue]

- GitHub: [RiverXue · GitHub](https://github.com/RiverXue)

---
