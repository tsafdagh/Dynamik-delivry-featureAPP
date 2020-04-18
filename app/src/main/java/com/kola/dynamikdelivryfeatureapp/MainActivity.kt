package com.kola.dynamikdelivryfeatureapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    lateinit var splitInstallManager: SplitInstallManager
    lateinit var request: SplitInstallRequest
    val DYNAMIC_FEATURE = "news_feature"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initDynamicModules()
        setClickListeners()
    }

    private fun initDynamicModules() {
        splitInstallManager = SplitInstallManagerFactory.create(this)
        request = SplitInstallRequest
            .newBuilder()
            .addModule(DYNAMIC_FEATURE)
            .build()
    }

    private fun setClickListeners() {
        buttonClick.setOnClickListener {
            if (!isDynamicFeatureDownloaded(DYNAMIC_FEATURE)) {
                downloadFeature()
            } else {
                buttonDeleteNewsModule.visibility = View.VISIBLE
                buttonOpenNewsModule.visibility = View.VISIBLE
            }
        }

        buttonOpenNewsModule.setOnClickListener {
            val intent =
                Intent().setClassName(this, "com.kola.news_feature.newsloader.NewsLoaderActivity")
            startActivity(intent)
        }

        buttonDeleteNewsModule.setOnClickListener {
            val list = ArrayList<String>()
            list.add(DYNAMIC_FEATURE)
            uninstallDynamicFeature(list)
        }
    }

    private fun isDynamicFeatureDownloaded(feature: String): Boolean =
        splitInstallManager.installedModules.contains(feature)

    private fun downloadFeature() {
        splitInstallManager.startInstall(request)
            .addOnFailureListener {
            }
            .addOnSuccessListener {
                buttonOpenNewsModule.visibility = View.VISIBLE
                buttonDeleteNewsModule.visibility = View.VISIBLE
            }
            .addOnCompleteListener {
            }
    }

    private fun uninstallDynamicFeature(list: List<String>) {
        splitInstallManager.deferredUninstall(list)
            .addOnSuccessListener {
                buttonDeleteNewsModule.visibility = View.GONE
                buttonOpenNewsModule.visibility = View.GONE
            }
    }

    /**
     * To get installed modules list
     * **/
    private fun getInstalledModuleLis() {
        val moduleList = splitInstallManager.installedModules
    }

    /**
     * We can also monitor the state of the request in the process when we request any dynamic-module,
     */
    var mySessionId = 0
    val listener = SplitInstallStateUpdatedListener {
        mySessionId = it.sessionId()
        when (it.status()) {
            SplitInstallSessionStatus.DOWNLOADING -> {
                val totalBytes = it.totalBytesToDownload()
                val progress = it.bytesDownloaded()
                // Update progress bar.
            }
            SplitInstallSessionStatus.INSTALLING -> Log.d("Status", "INSTALLING")
            SplitInstallSessionStatus.INSTALLED -> Log.d("Status", "INSTALLED")
            SplitInstallSessionStatus.FAILED -> Log.d("Status", "FAILED")
            SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> Log.d(
                "Status",
                "REQUIRES_USER_CONFIRMATION"
            )
        }
    }
}
