package com.test.task.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.test.task.app.mvp.PreferenceUtil
import com.test.task.providers.git.GitService
import com.test.task.providers.room.AppDatabase
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.androidCoreContextTranslators
import org.kodein.di.android.x.androidXContextTranslators
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Application : Application(), KodeinAware {

    override val kodein: Kodein = Kodein {
        import(androidXModule(this@Application))
        bind<Retrofit>() with singleton {
            Retrofit
                    .Builder()
                    .baseUrl("https://api.github.com")
                    .apply {
                        addConverterFactory(GsonConverterFactory.create())

                    }.build()
        }

        bind<GitService>() with provider {
            instance<Retrofit>()
                    .create(GitService::class.java)
        }

        bind<AppDatabase>() with provider {
           Room
                   .databaseBuilder(instance(), AppDatabase::class.java, "main")
                   .build()
        }

        bind<SharedPreferences>() with provider {
            instance<Context>().getSharedPreferences(
                PreferenceUtil.APP_GIT_PREFERENCE,
                Context.MODE_PRIVATE)
        }
    }

}