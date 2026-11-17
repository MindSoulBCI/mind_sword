package com.example.spatialalbum.p20261115.ui.intro
import androidx.compose.runtime.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.*
import com.example.spatialalbum.p20261115.R
import kotlinx.coroutines.delay
@Composable fun SwordSplash(onEnter:()->Unit){
 val enter by rememberUpdatedState(onEnter)
 val progress=remember{Animatable(0f)}
 LaunchedEffect(Unit){progress.animateTo(1f,tween(2400,easing=LinearEasing));delay(300);enter()}
 Box(Modifier.fillMaxSize().padding(24.dp),contentAlignment=Alignment.Center){
 Column(horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(20.dp)){
 Box(Modifier.size(240.dp),contentAlignment=Alignment.Center){
 Canvas(Modifier.fillMaxSize()){rotate(progress.value*300f){drawArc(Color(0xFF80CFFF).copy(alpha=.6f),0f,240f,false,style=Stroke(2.dp.toPx()));drawArc(Color(0xFFB9B2FF).copy(alpha=.5f),180f,100f,false,topLeft=androidx.compose.ui.geometry.Offset(16.dp.toPx(),16.dp.toPx()),size=androidx.compose.ui.geometry.Size(size.width-32.dp.toPx(),size.height-32.dp.toPx()),style=Stroke(1.dp.toPx()))}}
 Image(painterResource(R.drawable.ic_swords),"空间御剑",Modifier.size(172.dp).graphicsLayer{alpha=(progress.value*4).coerceAtMost(1f);scaleX=.85f+.15f*progress.value;scaleY=scaleX}.clip(CircleShape))
 }
 Text("空间御剑",style=PicoTheme.typography.headlineMedium)
 Text("从今天开始，成为武林中人...",modifier=Modifier.graphicsLayer{alpha=((progress.value-.2f)*3).coerceIn(0f,1f)})
 }
 Button(onClick={enter()},modifier=Modifier.align(Alignment.TopEnd)){Text("跳过")}
 }
}
