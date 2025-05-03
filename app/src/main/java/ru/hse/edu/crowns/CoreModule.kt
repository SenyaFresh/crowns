package ru.hse.edu.crowns

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import ru.hse.edu.common.Core
import ru.hse.edu.common.CoreProvider
import ru.hse.edu.common.flow.DefaultLazyFlowLoaderFactory
import ru.hse.edu.common.flow.LazyFlowLoaderFactory
import ru.hse.edu.common_impl.DefaultCoreProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CoreModule {

    @Provides
    fun provideCoreProvider(
        @ApplicationContext context: Context
    ): CoreProvider {
        return DefaultCoreProvider(context)
    }

    @Provides
    fun provideCoroutineScope(): CoroutineScope {
        return Core.globalScope
    }

    @Provides
    @Singleton
    fun provideLazyFlowLoaderFactory(): LazyFlowLoaderFactory {
        return DefaultLazyFlowLoaderFactory(Dispatchers.IO)
    }

}