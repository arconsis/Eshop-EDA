FROM golang:alpine AS builder

ENV GO111MODULE=on \
    CGO_ENABLED=0 \
    GOOS=linux \
    GOARCH=amd64

WORKDIR /build

COPY go.mod .
COPY go.sum .

RUN go mod download

COPY . .

RUN go build -o bastion .

WORKDIR /dist

RUN cp /build/bastion .

# For go we could use scratch but need a shell so we use alpine instead
FROM alpine:3.14

COPY --from=builder /dist/bastion /usr/bin

ENTRYPOINT ["sleep", "infinity"]