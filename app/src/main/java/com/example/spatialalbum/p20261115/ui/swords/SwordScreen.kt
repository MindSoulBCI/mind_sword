package com.example.spatialalbum.p20261115.ui.swords
import androidx.compose.runtime.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import com.example.spatialalbum.p20261115.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import com.pico.spatial.ui.foundation.hover.spatialHoverEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.pico.spatial.ui.design.*
import com.pico.spatial.ui.platform.containers.LocalSpatialNavigator
import com.example.spatialalbum.p20261115.platform.ImportActivity
import kotlinx.coroutines.*
import java.io.File
import com.example.spatialalbum.p20261115.ui.audio.*
import com.example.spatialalbum.p20261115.ui.brain.*
internal object ImmersionStatus{var busy by mutableStateOf(false);var message by mutableStateOf("")}
internal data class Sword(val name:String,val path:String)
internal fun swords(context:android.content.Context)=listOf("炎血","细锋","曜阳","陨星","寒冰","星空","熔岩").mapIndexed{i,n->Sword(n,"asset://swords/element-$i.glb")}+listOf(Sword("烈焰水晶","asset://swords/crystal.glb"),Sword("玄锋","asset://swords/classic.glb"))+File(context.filesDir,"swords").listFiles().orEmpty().filter{it.extension=="glb"}.sortedBy{it.name}.map{Sword(it.nameWithoutExtension,android.net.Uri.fromFile(it).toString())}
@Composable fun SwordScreen(onImmerse:()->Unit){
 var guest by androidx.compose.runtime.saveable.rememberSaveable{mutableStateOf(false)}
 var account by remember{mutableStateOf(false)}
 if(!guest||account){com.example.spatialalbum.p20261115.ui.account.AuthPage(onEnter={guest=true;account=false},onGuest={guest=true;account=false});return}

 val context=LocalContext.current;var items by remember{mutableStateOf(swords(context))};var selected by remember{mutableStateOf(items.first())}
 LaunchedEffect(Unit){while(isActive){items=swords(context);delay(1000)}}
 SwordAudio(if(BrainState.running)if(BrainState.task=="专注")"focus.wav"else "emotion.wav"else null,true)
 val brainNav=LocalSpatialNavigator.current
 LaunchedEffect(BrainState.emotion,BrainState.running,BrainState.task){if(BrainState.running&&BrainState.task=="情绪")selected=items[BrainState.swords[BrainState.emotion]]}
 Column(Modifier.fillMaxSize().padding(horizontal=12.dp,vertical=20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){
 Row(Modifier.fillMaxWidth(),verticalAlignment=androidx.compose.ui.Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)){
 Image(painterResource(R.drawable.ic_swords),"意念御剑 Logo",Modifier.size(44.dp).clip(CircleShape))
 Text("意念御剑",style=PicoTheme.typography.titleLarge)
 Spacer(Modifier.weight(1f))
 Button(onClick={BrainState.binding=true},colors=ButtonDefaults.buttonColors(if(BrainState.running)Color(0xFF246A58)else Color(0xFF6750A4),Color.White)){Text(if(BrainState.running)"脑机接口意念模式 · 运行中"else "脑机接口意念模式")}
 }
 Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(12.dp)){
 Button(onClick={AudioSettings.muted=!AudioSettings.muted},colors=ButtonDefaults.buttonColors(if(AudioSettings.muted)Color(0xFFE6E6EA)else Color(0xFFD5EBE2),Color(0xFF183E32))){Text(if(AudioSettings.muted)"音效：关"else "音效：开")}
 Button(onClick={account=true}){Text("登录 / 注册")}
 Button(onClick={guest=false}){Text("退出游客")}
 Button(onClick={context.startActivity(android.content.Intent(context,ImportActivity::class.java))}){Text("导入剑 · GLB")}
 Button(onClick=onImmerse,enabled=!ImmersionStatus.busy,colors=ButtonDefaults.buttonColors(Color(0xFF285BA8),Color.White)){Text(if(ImmersionStatus.busy)"正在进入…"else "沉浸模式")}
 }

 if(ImmersionStatus.message.isNotEmpty())Text(ImmersionStatus.message)
 BrainControl{brainNav.openWindowContainer("SwordEEG","sword-eeg");brainNav.openWindowContainer("SwordResults","sword-results")}
 Row(Modifier.weight(1f),horizontalArrangement=Arrangement.spacedBy(12.dp)){
 Column(Modifier.width(220.dp).verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(12.dp)){items.forEach{s->Box(modifier=Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(if(s==selected)Color(0xFFD8E7FF)else Color(0xFFE8E8ED)).spatialHoverEffect{scale(if(it.isActive)1.025f else 1f);alpha(if(it.isActive)1f else .82f)}.clickable{selected=s}.padding(horizontal=12.dp,vertical=6.dp)){
 Row(verticalAlignment=androidx.compose.ui.Alignment.CenterVertically){
 val thumb=remember(s.path){runCatching{context.assets.open(s.path.removePrefix("asset://").replace(".glb",".png")).use{android.graphics.BitmapFactory.decodeStream(it)?.asImageBitmap()}}.getOrNull()}
 if(thumb!=null)Image(thumb,s.name,Modifier.size(64.dp,80.dp),contentScale=ContentScale.Fit)
 Text((if(s==selected)"✓ "else "")+s.name,color=if(s==selected)Color(0xFF173B70)else Color(0xFF303038))
 }
 }}}
 Column(Modifier.weight(1f)){var reset by remember{mutableIntStateOf(0)};Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){Text(selected.name+" · 3D 预览");Button(onClick={reset++}){Text("重置预览")}};key(selected.path,reset){SwordScene(listOf(selected),false,Modifier.weight(1f))};Text("拖动 / 旋转 / 双手缩放 · 长按 1.5 秒蓄电分身")}
 }
 }
}
@Composable fun SwordStage(){SwordAudio("swords.wav");val context=LocalContext.current;val nav=LocalSpatialNavigator.current;val job=rememberCoroutineScope();SwordScene(remember{swords(context)},true,onReady={ImmersionStatus.busy=false;ImmersionStatus.message="";nav.minimizeWindowContainer("YourPlanarWindowContainer")},onExit={job.launch{nav.restoreWindowContainer("YourPlanarWindowContainer");nav.closeStage();nav.closeWindowContainer("SwordImmersionControls","sword-immersion-controls")}})}

@Composable fun SwordImmersionControls(){
 val nav=LocalSpatialNavigator.current;val job=rememberCoroutineScope()
 Column(Modifier.fillMaxSize().padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
 Text("已进入沉浸御剑",style=PicoTheme.typography.titleLarge)
 Text("剑阵环绕中 · 当前半径 5.4 米")
 Text("默认剑高 1.5 米 · 宽厚等比例缩放")
 Button(onClick={job.launch{nav.restoreWindowContainer("YourPlanarWindowContainer");nav.closeStage();nav.closeWindowContainer("SwordImmersionControls","sword-immersion-controls");ImmersionStatus.busy=false}},modifier=Modifier.fillMaxWidth()){Text("退出沉浸 · 返回剑库")}
 }
}
