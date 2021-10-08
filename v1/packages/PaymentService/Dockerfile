FROM node:14-alpine as builder
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
RUN mkdir -p /home/$user/paymentservice/src
WORKDIR /home/$user/paymentservice/src

COPY --from=builder /build ./

EXPOSE 3000

ENV NODE_ENV production

CMD ["npm", "start"]
