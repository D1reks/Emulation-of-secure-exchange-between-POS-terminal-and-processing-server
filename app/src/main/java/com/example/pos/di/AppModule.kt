package com.example.pos.di

import android.content.Context
import com.example.pos.core.crypto.CryptoManager
import com.example.pos.core.crypto.DefaultCryptoManager
import com.example.pos.core.crypto.KeyGenerator
import com.example.pos.core.network.PosClient
import com.example.pos.core.network.TimeoutHandler
import com.example.pos.core.tlv.TlvEncoder
import com.example.pos.data.mapper.TransactionMapper
import com.example.pos.data.remote.RetrofitPosClient
import com.example.pos.data.remote.api.PosApiService
import com.example.pos.data.remote.interceptor.MockServerInterceptor
import com.example.pos.data.repository.TransactionRepositoryImpl
import com.example.pos.domain.interactor.TransactionInteractor
import com.example.pos.domain.repository.key.KeyRepository
import com.example.pos.domain.repository.key.KeyRepositoryImpl
import com.example.pos.domain.repository.transaction.TransactionRepository
import com.example.pos.domain.usecase.GetTransactionsUseCase
import com.example.pos.domain.usecase.SendTransactionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCryptoManager() : CryptoManager {
        return DefaultCryptoManager()
    }

    @Provides
    @Singleton
    fun provideTlvEncoder() : TlvEncoder {
        return TlvEncoder()
    }

    @Provides
    @Singleton
    fun provideKeyGenerator(): KeyGenerator {
        return KeyGenerator()
    }

    @Provides
    @Singleton
    fun provideKeyRepository(
        @ApplicationContext context: Context,
        cryptoManager: CryptoManager,
        keyGenerator: KeyGenerator
    ): KeyRepository {
        return KeyRepositoryImpl(context, cryptoManager, keyGenerator)
    }

    @Provides
    @Singleton
    fun provideTransactionMapper() : TransactionMapper {
        return TransactionMapper()
    }

    @Provides
    @Singleton
    fun provideTimeoutHandler() : TimeoutHandler {
        return TimeoutHandler()
    }


    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(MockServerInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://mock-server.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providePosApiService(retrofit: Retrofit): PosApiService {
        return retrofit.create(PosApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePosClient(
        apiService: PosApiService,
        cryptoManager: CryptoManager,
        keyRepositoryimpl: KeyRepositoryImpl,
        tlvEncoder: TlvEncoder
    ): PosClient {
        return RetrofitPosClient(apiService, cryptoManager,keyRepositoryimpl , tlvEncoder)
    }

    @Provides
    @Singleton
    fun provideTransactionRepository(
        retrofitPosClient: RetrofitPosClient,
        transactionMapper: TransactionMapper
    ): TransactionRepository {
        return TransactionRepositoryImpl(retrofitPosClient, transactionMapper)
    }

    @Provides
    fun provideTransactionInteractor(
        sendTransactionUseCase: SendTransactionUseCase,
        getTransactionsUseCase: GetTransactionsUseCase
    ): TransactionInteractor {
        return TransactionInteractor(sendTransactionUseCase, getTransactionsUseCase)
    }

    @Provides
    @Singleton
    fun provideSendTransactionUseCase(
        transactionRepository: TransactionRepository
    ): SendTransactionUseCase {
        return SendTransactionUseCase(transactionRepository)
    }

    @Provides
    @Singleton
    fun provideGetTransactionsUseCase(
        transactionRepository: TransactionRepository
    ): GetTransactionsUseCase {
        return GetTransactionsUseCase(transactionRepository)
    }
}