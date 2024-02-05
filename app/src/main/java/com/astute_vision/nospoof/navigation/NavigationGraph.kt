package com.astute_vision.nospoof.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.astute_vision.nospoof.ui.view.CameraView
import com.astute_vision.nospoof.ui.view.CameraViewModel
import com.astute_vision.nospoof.ui.view.MainView
import com.astute_vision.nospoof.ui.view.MainViewModel

enum class NavigationGraphDestination {
    Main, Camera, ChooseFile
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController, startDestination = NavigationGraphDestination.Main.name) {

        composable(NavigationGraphDestination.Main.name) {
            val vm = hiltViewModel<MainViewModel>()
            MainView(vm = vm, navController)
        }

        composable(NavigationGraphDestination.Camera.name) {
            val vm = hiltViewModel<CameraViewModel>()
            CameraView(vm = vm, navController = navController)
        }

//        composable(AVNavigationGraphDestination.Navigator.name) {
//            val vm = hiltViewModel<NavigatorViewModel>()
//            NavigatorView(vm = vm, navController)
//        }
    }
}