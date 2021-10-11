import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { PrismaService } from './prisma.service';

@Module({
  imports: [ConfigModule.forRoot({
    ignoreEnvFile: process.env.NODE_ENV === 'production',
  })],
  providers: [PrismaService],
  exports: [PrismaService],
})
export class CommonModule {}
