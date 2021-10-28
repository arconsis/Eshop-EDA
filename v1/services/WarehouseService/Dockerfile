FROM node:16-alpine AS builder

WORKDIR /usr/src/app

COPY package*.json ./
COPY tsconfig*.json ./
COPY ./prisma ./prisma

RUN npm install

COPY src src

RUN npm run build

FROM node:16-alpine
ENV NODE_ENV=production

WORKDIR /usr/src/app

COPY --chown=node:node startup.sh ./
COPY package*.json ./
COPY ./prisma ./prisma

RUN npm ci --only=production
RUN npm i -g prisma@3.1.1
RUN prisma generate

COPY --from=builder /usr/src/app/dist dist

USER node
EXPOSE 3000

CMD ["npm", "run", "start:prod"]