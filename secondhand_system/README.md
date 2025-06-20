# 🐟 二手交易平台（仿“咸鱼”）

---

## 一、项目概述

本项目为一款仿“咸鱼”风格的二手物品交易平台，基于前后端分离架构，用户可发布商品、浏览商品、留言交流、收藏感兴趣商品，并支持基础交易功能，含后台管理模块（可选）。新增AI智能服务模块，用户可通过智能对话获取个性化商品推荐。系统设计简洁、功能完整，适合作为高校毕设、课程设计、个人实战项目及中小型商业原型开发。

---

## 二、核心技术栈

| 层次 | 技术选型                                                                                                             |
|----|------------------------------------------------------------------------------------------------------------------|
| 后端 | Spring Boot 3.4.6、MyBatis-Plus、MySQL 8.x、Redis、Spring Security + JWT、Lombok、Swagger 3、MinIO（OSS）、OpenAI GPT 模拟接口 |
| 前端 | Vue 3、TypeScript、Vite、Pinia、Vue Router、Axios、Element Plus                                                        |
| 其他 | Maven、Nginx、Postman、Docker（选用）                                                                                   |

---

## 三、主要功能模块

### 1. 用户模块

- 用户注册、登录（JWT认证，支持令牌刷新机制，增强令牌验证安全性）

- 个人资料管理（昵称、头像、手机号等，含手机号格式验证）

- 我的发布、收藏、交易记录

- 头像上传（OSS）

- 安全登出功能（支持令牌黑名单管理，确保退出后令牌立即失效）

- 密码安全策略（至少8位，包含字母和数字，增强密码加密存储）

- 用户信息缓存优化（Redis缓存减轻数据库压力，提升系统响应速度）

### 2. 商品模块

- 发布二手商品（多图上传）

- 商品浏览、分类筛选、模糊搜索

- 商品详情查看

- 收藏/取消收藏商品

### 3. 交易模块

- 买家留言 / 卖家回复

- 模拟订单下单、交易管理（未接入真实支付）

### 4. AI智能服务模块

- 用户在首页打开聊天窗口，与AI智能助手对话

- AI根据用户描述需求，智能推荐匹配商品

- AI对话接口支持多轮对话，提升交互体验

### 5. 后台管理模块

- 用户管理

- 商品管理（违规商品下架）

- 举报处理

---

## 四、项目目录结构（模板）

### 后端（`Secondhand_System`）

```
Secondhand_System
│  pom.xml
└───src/main
    ├── java/com/seateam/secondhand_system/
    |   |—— common  # 公共类
    |   |   └──── config  # # 安全配置（JWT/Security/CORS）、Swagger配置
    |   |   └──── exception  # 异常类
    |   |   └──── utils  # 工具类（JWT工具、OSS工具类）
    |   |   └──── sql    # sql脚本   
    │   ├── ai/                 # AI智能服务模块（模拟DeepSeek）
    │   ├── controller/         # REST API 控制器（含AI对话接口）
    │   ├── entity/             # 实体类（与数据库表映射）
    │   ├── mapper/             # MyBatis-Plus 映射接口
    │   ├── service/            # 业务逻辑接口
    │   ├── service/impl/       # 业务逻辑实现（含AI推荐实现）
    │   └── SecondhandSystemApplication.java # 启动类
    └───resources/
        ├── application.yml     # 配置文件
        ├── mapper/*.xml        # MyBatis XML文件
        └── static/             # 静态资源
```

### 前端（`Secondhand_Frontend`）

```
Secondhand_Frontend
│  package.json
└───src
    ├── api/                  # Axios封装与接口请求（含AI对话API）
    ├── assets/               # 静态资源（图片、样式等）
    ├── components/           # 公共组件（含AI聊天窗口组件）
    ├── pages/                # 路由页面组件
    │   ├── Home.vue          # 首页（含AI聊天入口）
    │   ├── GoodsDetail.vue   # 商品详情
    │   ├── Publish.vue       # 发布商品
    │   ├── Login.vue         # 登录注册
    │   └── UserCenter.vue    # 用户中心
    ├── router/               # Vue Router 路由配置
    ├── store/                # Pinia 状态管理（含AI聊天状态）
    └── App.vue
```

---

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

### 2. 商品模块

| 方法   | 路径                       | 描述      |
|------|--------------------------|---------|
| POST | `/api/goods/publish`     | 发布新商品   |
| GET  | `/api/goods/list`        | 商品分页列表  |
| GET  | `/api/goods/detail/{id}` | 获取商品详情  |
| GET  | `/api/goods/search`      | 关键词搜索商品 |

### 3. 收藏模块

| 方法     | 路径                     | 描述       |
|--------|------------------------|----------|
| POST   | `/api/favorite/add`    | 添加收藏     |
| DELETE | `/api/favorite/remove` | 取消收藏     |
| GET    | `/api/favorite/list`   | 获取我的收藏列表 |

### 4. 留言模块

| 方法   | 路径                            | 描述        |
|------|-------------------------------|-----------|
| POST | `/api/message/leave`          | 发表留言      |
| GET  | `/api/message/list/{goodsId}` | 获取某商品留言列表 |

### 5. 订单模块

| 方法   | 路径                  | 描述     |
|------|---------------------|--------|
| POST | `/api/order/create` | 生成订单   |
| GET  | `/api/order/my`     | 我的订单列表 |

### 6. AI智能服务模块

| 方法   | 路径             | 描述                 |
|------|----------------|--------------------|
| POST | `/api/ai/chat` | 发送用户对话，获取AI回复及推荐商品 |

请求示例：

```json
{
  "userId": 123,
  "sessionId": "uuid-xxxx-xxxx",
  "message": "我想买一台价格在1000元左右的二手手机"
}
```

响应示例：

```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "reply": "根据您的需求，我推荐以下几款手机...",
    "recommendedGoods": [
      {
        "id": 12,
        "title": "二手iPhone 11",
        "price": 980.00,
        "cover_img": "/images/iphone11.jpg"
      },
      {
        "id": 18,
        "title": "二手小米10",
        "price": 1050.00,
        "cover_img": "/images/xiaomi10.jpg"
      }
    ]
  }
}
```

---

## 七、运行与开发说明

### 后端启动

1. 配置 `application.yml`（数据库、Redis、OSS、AI服务模拟参数）

2. 导入 `doc/sql/second_hand_market.sql` 初始化数据库

3. 启动 Spring Boot 项目：

```bash
cd second-hand-backend
mvn clean package
java -jar target/second-hand-market-1.0.jar
```

### 前端启动

1. 配置 `src/api/request.ts` 指定后端API基础地址

2. 启动前端项目：

```bash
cd second-hand-frontend
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

## 九、项目预览效果（示意）

| 首页（含AI聊天入口） | AI智能聊天窗口示例 | 商品详情 | 用户中心 |
|-------------|------------|------|------|
|             |            |      |      |

## 作者信息

- Author: [River Xue]

- GitHub: [RiverXue · GitHub](https://github.com/RiverXue)

---
