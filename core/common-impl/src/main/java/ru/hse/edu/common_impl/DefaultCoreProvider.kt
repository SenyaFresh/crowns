package ru.hse.edu.common_impl

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import ru.hse.edu.common.CoreProvider
import ru.hse.edu.common.ErrorHandler
import ru.hse.edu.common.Logger
import ru.hse.edu.common.Resources
import ru.hse.edu.common.Toaster

class DefaultCoreProvider(
    private val appContext: Context,
    override val resources: Resources = AndroidResources(appContext),
    override val globalScope: CoroutineScope = createDefaultGlobalScope(),
    override val toaster: Toaster = AndroidToaster(appContext),
    override val logger: Logger = AndroidLogger(),
    override val errorHandler: ErrorHandler = DefaultErrorHandler(
        logger,
        resources,
        toaster
    )
) : CoreProvider