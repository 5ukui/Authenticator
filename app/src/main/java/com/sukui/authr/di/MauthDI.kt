package com.sukui.authr.di

import androidx.room.Room
import com.sukui.authr.core.auth.AuthManager
import com.sukui.authr.core.auth.DefaultAuthManager
import com.sukui.authr.core.otp.exporter.DefaultOtpExporter
import com.sukui.authr.core.otp.exporter.OtpExporter
import com.sukui.authr.core.otp.generator.DefaultOtpGenerator
import com.sukui.authr.core.otp.generator.OtpGenerator
import com.sukui.authr.core.otp.parser.DefaultOtpUriParser
import com.sukui.authr.core.otp.parser.OtpUriParser
import com.sukui.authr.core.otp.transformer.DefaultKeyTransformer
import com.sukui.authr.core.otp.transformer.KeyTransformer
import com.sukui.authr.core.settings.DefaultSettings
import com.sukui.authr.core.settings.Settings
import com.sukui.authr.db.AccountDatabase
import com.sukui.authr.domain.AuthRepository
import com.sukui.authr.domain.QrRepository
import com.sukui.authr.domain.SettingsRepository
import com.sukui.authr.domain.account.AccountRepository
import com.sukui.authr.domain.otp.OtpRepository
import com.sukui.authr.ui.screen.account.AccountViewModel
import com.sukui.authr.ui.screen.auth.AuthViewModel
import com.sukui.authr.ui.screen.export.ExportViewModel
import com.sukui.authr.ui.screen.home.HomeViewModel
import com.sukui.authr.ui.screen.pinremove.PinRemoveViewModel
import com.sukui.authr.ui.screen.pinsetup.PinSetupViewModel
import com.sukui.authr.ui.screen.qrscan.QrScanViewModel
import com.sukui.authr.ui.screen.settings.SettingsViewModel
import com.sukui.authr.ui.screen.theme.ThemeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

object MauthDI {

    val CoreModule = module {
        singleOf(::DefaultOtpGenerator) bind OtpGenerator::class
        singleOf(::DefaultOtpUriParser) bind OtpUriParser::class
        singleOf(::DefaultKeyTransformer) bind KeyTransformer::class
        singleOf(::DefaultSettings) bind Settings::class
        singleOf(::DefaultAuthManager) bind AuthManager::class
        singleOf(::DefaultOtpExporter) bind OtpExporter::class
    }

    val DbModule = module {
        single {
            Room.databaseBuilder(androidContext(), AccountDatabase::class.java, "accounts")
                .addMigrations(AccountDatabase.Migrate3to4)
                .addMigrations(AccountDatabase.Migrate4To5)
                .build()
        }

        single {
            val db: AccountDatabase = get()
            db.accountsDao()
        }

        single {
            val db: AccountDatabase = get()
            db.rtdataDao()
        }
    }

    val DomainModule = module {
        singleOf(::AccountRepository)
        singleOf(::OtpRepository)
        singleOf(::QrRepository)
        singleOf(::SettingsRepository)
        singleOf(::AuthRepository)
    }

    val UiModule = module {
        viewModelOf(::AccountViewModel)
        viewModelOf(::SettingsViewModel)
        viewModelOf(::QrScanViewModel)
        viewModelOf(::PinSetupViewModel)
        viewModelOf(::PinRemoveViewModel)
        viewModelOf(::HomeViewModel)
        viewModelOf(::AuthViewModel)
        viewModelOf(::ThemeViewModel)
        viewModelOf(::ExportViewModel)
    }

    val all = listOf(CoreModule, DbModule, DomainModule, UiModule)

}