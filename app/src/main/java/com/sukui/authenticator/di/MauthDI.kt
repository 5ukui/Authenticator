package com.sukui.authenticator.di

import androidx.room.Room
import com.sukui.authenticator.core.auth.AuthManager
import com.sukui.authenticator.core.auth.DefaultAuthManager
import com.sukui.authenticator.core.otp.exporter.DefaultOtpExporter
import com.sukui.authenticator.core.otp.exporter.OtpExporter
import com.sukui.authenticator.core.otp.generator.DefaultOtpGenerator
import com.sukui.authenticator.core.otp.generator.OtpGenerator
import com.sukui.authenticator.core.otp.parser.DefaultOtpUriParser
import com.sukui.authenticator.core.otp.parser.OtpUriParser
import com.sukui.authenticator.core.otp.transformer.DefaultKeyTransformer
import com.sukui.authenticator.core.otp.transformer.KeyTransformer
import com.sukui.authenticator.core.settings.DefaultSettings
import com.sukui.authenticator.core.settings.Settings
import com.sukui.authenticator.db.AccountDatabase
import com.sukui.authenticator.domain.AuthRepository
import com.sukui.authenticator.domain.QrRepository
import com.sukui.authenticator.domain.SettingsRepository
import com.sukui.authenticator.domain.account.AccountRepository
import com.sukui.authenticator.domain.otp.OtpRepository
import com.sukui.authenticator.ui.screen.account.AccountViewModel
import com.sukui.authenticator.ui.screen.auth.AuthViewModel
import com.sukui.authenticator.ui.screen.export.ExportViewModel
import com.sukui.authenticator.ui.screen.home.HomeViewModel
import com.sukui.authenticator.ui.screen.pinremove.PinRemoveViewModel
import com.sukui.authenticator.ui.screen.pinsetup.PinSetupViewModel
import com.sukui.authenticator.ui.screen.qrscan.QrScanViewModel
import com.sukui.authenticator.ui.screen.settings.SettingsViewModel
import com.sukui.authenticator.ui.screen.theme.ThemeViewModel
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