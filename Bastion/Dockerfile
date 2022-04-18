FROM golang:alpine AS builder

# gcc needed for CGO
RUN apk add build-base

ENV GO111MODULE=on \
    CGO_ENABLED=1 \
    GOOS=linux \
    GOARCH=amd64

WORKDIR /build

COPY go.mod .
COPY go.sum .

RUN go mod download

COPY . .

# confluent-kafka-go needs musl when building on Alpine
RUN go build -tags musl -o main .

WORKDIR /dist

RUN cp /build/main .

# scratch cannot be used because of cgo
#FROM scratch
FROM alpine:latest

COPY --from=builder /dist/main /

ENTRYPOINT ["/main"]