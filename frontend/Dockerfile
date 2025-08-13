FROM node:18

WORKDIR /app

# 패키지 파일만 먼저 복사
COPY package*.json ./

RUN npm install

# 소스 전체 복사
COPY . .

CMD ["npm", "run", "dev"]