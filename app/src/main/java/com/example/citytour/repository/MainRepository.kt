package com.example.citytour.repository

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.example.ammf_core.AMMFSdk
import com.example.ammf_core.performance.MonitoringData
import com.example.ammf_core.performance.PerformanceMonitoringService
import com.example.citytour.model.EntriesRequestResponse
import com.example.citytour.remote.EntriesClient
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    @ApplicationContext private val context: Context
){

    private var service: PerformanceMonitoringService? = null
    private var isBound: Boolean = false

    val performanceState: StateFlow<MonitoringData>
        get() = AMMFSdk.getInstance().performanceManager.performanceState

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, serviceBinder: IBinder) {
            val binder = serviceBinder as PerformanceMonitoringService.LocalBinder
            service = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(className: ComponentName) {
            isBound = false
            service = null
        }
    }

    fun bindService() {
        val intent = Intent(context, PerformanceMonitoringService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService() {
        if (isBound) {
            context.unbindService(serviceConnection)
            isBound = false
        }
    }

    fun clearData() {
        AMMFSdk.getInstance().performanceManager.clearData()
    }

    fun startMonitoring() {
        registerListener()
        service?.startMonitoring()
    }

    fun stopMonitoring() {
        unregisterListener()
        service?.stopMonitoring()
    }

    private fun registerListener() {
        AMMFSdk.getInstance().performanceManager.register()
    }

    private fun unregisterListener() {
        AMMFSdk.getInstance().performanceManager.unregister()
    }

    suspend fun getEntries(): Response<EntriesRequestResponse> {
        val entriesClient = provideEntriesClient(provideRetrofit())
        return withContext(Dispatchers.IO) {
            entriesClient.getEntries()
        }
    }

    companion object {
        fun provideRetrofit(): Retrofit = Retrofit.Builder()
            .baseUrl("https://api.publicapis.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        fun provideEntriesClient(retrofit: Retrofit): EntriesClient {
            return retrofit.create(EntriesClient::class.java)
        }
    }
}