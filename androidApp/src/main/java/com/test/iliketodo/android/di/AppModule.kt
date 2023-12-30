package com.test.iliketodo.android.di

import android.app.Application
import app.cash.sqldelight.db.SqlDriver
import com.test.iliketodo.DriverFactory
import com.test.iliketodo.TodoUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDriverFactory(app: Application): DriverFactory {
        return DriverFactory(app)
    }

    @Provides
    @Singleton
    fun provideTodoUseCase(driverFactory: DriverFactory): TodoUseCase {
        return TodoUseCase(driverFactory)
    }

}