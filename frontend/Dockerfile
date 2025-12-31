# 建议使用 alpine 版本，下载更快
FROM registry.cn-hangzhou.aliyuncs.com/libraries/node:18-alpine AS build
WORKDIR /app

# 只要 package-lock.json 没变，这层缓存就不会失效
COPY package.json package-lock.json ./

# 配置淘宝/阿里云 npm 源（可选，为了加速下载）
RUN npm config set registry https://registry.npmmirror.com

# 使用 ci 严格安装依赖
RUN npm ci

# 复制源码
COPY . .

# 接收构建参数
ARG VITE_API_BASE=/api
ENV VITE_API_BASE=$VITE_API_BASE

# 构建
RUN npm run build

# --- 第二阶段 ---
FROM nginx:1.25-alpine

# 确保你本地有这个 nginx.conf 且配置了 try_files
COPY nginx.conf /etc/nginx/conf.d/default.conf

COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

# 默认 CMD 已经包含在 nginx 镜像里了，不需要额外写