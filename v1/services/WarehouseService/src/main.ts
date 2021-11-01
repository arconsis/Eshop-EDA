import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { PrismaService } from './common/prisma.service';
import { MicroserviceOptions, Transport } from '@nestjs/microservices';
import { ConfigService } from '@nestjs/config';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);
  const configService = app.get(ConfigService);
  app.connectMicroservice<MicroserviceOptions>({
    transport: Transport.KAFKA,
    options: {
      client: {
        clientId: 'inventory-service',
        brokers: configService.get<string>('KAFKA_BROKER')?.split(',') ?? [],
        ssl: process.env.NODE_ENV !== 'production' ? false : true,
      },
      subscribe: {
        fromBeginning: true,
      },
      consumer: {
        groupId: 'inventory-consumer',
        readUncommitted: false
      },
      producer: {
        maxInFlightRequests: 1,
        idempotent: true,
        transactionalId: `inventory_producer`,
      }
    },
  });

  app.enableShutdownHooks();

  const prismaService: PrismaService = app.get(PrismaService);
  prismaService.enableShutdownHooks(app);

  await app.startAllMicroservices();

  await app.listen(3000);
}

bootstrap();
