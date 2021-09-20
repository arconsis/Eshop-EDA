FROM node:14-alpine as builder
ARG NPM_TOKEN
RUN mkdir -p /build

COPY ./package.json ./package-lock.json /build/
WORKDIR /build
RUN npm install && \
    rm -f .npmrc

COPY . /build

FROM node:14-alpine
ENV user node
USER $user

# Create app directory
RUN mkdir -p /home/$user/server/src
WORKDIR /home/$user/server/src

COPY --from=builder /build ./

EXPOSE 7777

ENV NODE_ENV development

CMD ["npm", "start"]
