#!/bin/bash
npx prisma migrate deploy
node dist/main.js