package com.smartattendance.android.data.di

import androidx.lifecycle.SavedStateHandle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object AppModule {  // Name it appropriately, e.g., ViewModelModule

//    @Provides
//    @ViewModelScoped
//    fun provideSavedStateHandle(): SavedStateHandle {
        // This is a placeholder. You'll likely need to get the SavedStateHandle
        // from the ViewModel's creation context.  The exact way to do this
        // might depend on how you're creating your ViewModels within your
        // Composable functions. One common approach is to use `hiltViewModel()`
        // which handles this for you automatically, so you might not even
        // need to provide it explicitly. If you are not using hiltViewModel(),
        // you might need to rethink your approach to obtaining the SavedStateHandle.
//        throw IllegalStateException("SavedStateHandle should be provided by the navigation library or ViewModel creation context, not explicitly in a module.")
//    }
}