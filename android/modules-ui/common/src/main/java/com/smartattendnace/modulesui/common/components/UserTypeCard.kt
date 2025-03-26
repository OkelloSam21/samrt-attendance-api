package com.smartattendnace.modulesui.common.components

import androidx.annotation.DrawableRes
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp


//@Composable
//fun UserTypeCard(
//    @DrawableRes image: Int,
//    title: String,
//    userRole: UserRole,
//    onClick: (UserRole) -> Unit
//) {
//    Card (
//        modifier =
//        Modifier.width(170.dp)
//            .height(180.dp)
//            .padding(horizontal = 8.dp, vertical = 8.dp)
//            .shadow(elevation = 12.dp, spotColor = Color(0x26BFBFD9), ambientColor = Color(0x26BFBFD9))
//            .shadow(elevation = 4.dp, spotColor = Color(0x1ABFBFD9), ambientColor = Color(0x1ABFBFD9))
//            .border(width = 0.5.dp, color = MaterialTheme.colorScheme.onSurfaceVariant, shape = RoundedCornerShape(size = 16.dp))
//    ){
//        Column (
//            modifier = Modifier.fillMaxWidth().clickable(onClick = { onClick(userRole) }),
//            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ){
//            Image(
//                painter = painterResource(id = image),
//                contentDescription = title,
//                contentScale = ContentScale.Inside,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(150.dp)
//                )
//            Spacer(modifier = Modifier.height(16.dp))
//            Text(
//                text = title,
//                style = MaterialTheme.typography.labelSmall,
//                modifier = Modifier.padding(start = 16.dp)
//            )
//        }
//    }
//}