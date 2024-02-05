package com.astute_vision.nospoof.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.astute_vision.nospoof.R
import com.astute_vision.nospoof.navigation.NavigationGraphDestination

@Composable
fun MainView(vm: MainViewModel, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.main_background),
            contentDescription = "",
            contentScale = ContentScale.Crop
        )
        Image(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 72.dp),
            imageVector = ImageVector.vectorResource(id = R.drawable.label),
            contentDescription = ""
        )
        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            Button(
//                onClick = { /*TODO*/ }, modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 12.dp),
//                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007E58))
//            ) {
//                Text(
//                    text = "Выбрать файл",
//                    fontSize = 18.sp,
//                    color = Color.White,
//                    modifier = Modifier.padding(vertical = 4.dp)
//                )
//            }
//            Spacer(modifier = Modifier.padding(vertical = 8.dp))
            Button(
                onClick = {
                    navController.navigate(NavigationGraphDestination.Camera.name) {}
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007E58))
            ) {
                Text(
                    text = "Запустить трансляцию",
                    fontSize = 18.sp,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.padding(vertical = 16.dp))
        }
    }
}