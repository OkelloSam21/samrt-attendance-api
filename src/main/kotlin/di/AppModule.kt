package di

import dagger.Module
import dagger.Provides
import features.auth.services.JwtService
import features.auth.util.RoleAuthorization
import features.auth.util.RoleAuthorizationImpl
import javax.inject.Singleton

/**
 * Core application module
 */
@Module
object AppModule {
    
    @Provides
    @Singleton
    fun provideDatabaseFactory(): DatabaseFactory {
        return DatabaseFactory()
    }
    
    @Provides
    @Singleton
    fun provideJwtService(): JwtService {
        return JwtService()
    }
    
    @Provides
    @Singleton
    fun provideRoleAuthorization(): RoleAuthorization {
        return RoleAuthorizationImpl()
    }
}