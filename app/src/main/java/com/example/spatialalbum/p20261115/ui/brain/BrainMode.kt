package com.example.spatialalbum.p20261115.ui.brain
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.*
import com.pico.spatial.ui.design.windows.AlertDialog
import kotlinx.coroutines.delay
import kotlin.math.sin
object BrainState{
 var binding by mutableStateOf(false)
 var device by mutableStateOf("");var channels by mutableIntStateOf(3);var task by mutableStateOf("专注")
 var running by mutableStateOf(false);var elapsed by mutableIntStateOf(0);var focus by mutableFloatStateOf(50f)
 var emotion by mutableIntStateOf(0);var started by mutableStateOf("—")
 val emotions=listOf("快乐 · 黄","愤怒 · 红","悲伤 · 蓝","平静 · 绿")
 val swords=listOf(2,0,4,8)
}
@Composable fun BrainControl(onOpen:()->Unit){

 Column(verticalArrangement=Arrangement.spacedBy(8.dp)){
 Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){

 if(BrainState.device.isNotEmpty()){
 Button(onClick={BrainState.task="情绪"},enabled=!BrainState.running,colors=ButtonDefaults.buttonColors(if(BrainState.task=="情绪")Color(0xFF6750A4)else Color(0xFFE6E6EA),if(BrainState.task=="情绪")Color.White else Color(0xFF25252B))){Text("情绪")}
 Button(onClick={BrainState.task="专注"},enabled=!BrainState.running,colors=ButtonDefaults.buttonColors(if(BrainState.task=="专注")Color(0xFF246A58)else Color(0xFFE6E6EA),if(BrainState.task=="专注")Color.White else Color(0xFF25252B))){Text("专注")}
 Button(onClick={BrainState.running=!BrainState.running;if(BrainState.running){BrainState.elapsed=0;BrainState.started=java.text.SimpleDateFormat("HH:mm:ss",java.util.Locale.CHINA).format(java.util.Date())};onOpen()},colors=ButtonDefaults.buttonColors(if(BrainState.running)Color(0xFF9A402E)else Color(0xFF246A58),Color.White)){Text(if(BrainState.running)"停止意念任务"else "开始意念御剑")}
 }
 }
 if(BrainState.device.isNotEmpty())Text("${BrainState.task}任务 · 模拟 EEG / 手动结果预览，非真实识别",style=PicoTheme.typography.bodySmall)
 }
 LaunchedEffect(BrainState.running){while(BrainState.running){delay(1000);BrainState.elapsed++}}
 DisposableEffect(Unit){onDispose{BrainState.running=false}}
 if(BrainState.binding)AlertDialog(onDismissRequest={BrainState.binding=false},title={Text("绑定脑机接口")},content={Column(verticalArrangement=Arrangement.spacedBy(12.dp)){
 Text("请选择设备。佩戴并检查电极接触；当前 SDK 未接入，设备未连接。")
 listOf("BrainLink Pro","博瑞恩").forEach{d->Button(onClick={BrainState.device=d;BrainState.binding=false;onOpen()}){Text("选择 $d")}}
 Text("体验或商务合作：PMTalk123。3 / 32 通道为模拟显示配置，不代表设备通道规格。")
 }},buttons={Button(onClick={BrainState.binding=false}){Text("取消")}})
}
@Composable fun BrainEeg(){
 var page by remember{mutableIntStateOf(0)}
 val count=if(BrainState.channels==3)3 else 8
 val phase=BrainState.elapsed.toFloat();val color=PicoTheme.colorScheme.labelPrimary
 Column(Modifier.fillMaxSize().padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
 Text("EEG 采集看板",style=PicoTheme.typography.headlineSmall)
 Text("${BrainState.device} · 未连接")
 Text("模拟波形 · 非真实 EEG")
 Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Button(onClick={BrainState.channels=if(BrainState.channels==3)32 else 3;page=0},enabled=!BrainState.running){Text("${BrainState.channels} 通道")};Button(onClick={page=(page+1)%4},enabled=BrainState.channels==32){Text("${page+1}/4 页")}}
 Row(Modifier.weight(1f)){
 Column(Modifier.width(48.dp).fillMaxHeight(),verticalArrangement=Arrangement.SpaceAround){repeat(count){Text("CH${page*8+it+1}")}}
 Canvas(Modifier.weight(1f).fillMaxHeight()){repeat(count){c->val row=size.height/count;val path=Path();for(i in 0..180){val x=size.width*i/180;val y=row*(c+.5f)+if(BrainState.running)sin(i*.12f+phase*3+c)*row*.24f else 0f;if(i==0)path.moveTo(x,y)else path.lineTo(x,y)};drawPath(path,color,style=Stroke(1.dp.toPx()))}}
 }
 Text("任务时间：${BrainState.elapsed} 秒")
 Text("采集开始：${BrainState.started}（模拟）")
 Text("采集时长：${BrainState.elapsed} 秒（模拟）")
 Text("EEG 存储：0 B · 未保存\n真实采集：0 秒 · 0 B")
 }
}
@Composable fun BrainResults(){Column(Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(16.dp)){
 Text("意念任务结果",style=PicoTheme.typography.headlineSmall)
 Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){listOf("情绪","专注").forEach{t->Button(onClick={BrainState.task=t},enabled=!BrainState.running){Text((if(BrainState.task==t)"✓ "else "")+t)}}}
 Text("手动预览 · 非 EEG 推理结果")
 if(BrainState.task=="专注"){
 Text("专注力：${BrainState.focus.toInt()} / 100")
 LinearProgressIndicator(progress={BrainState.focus/100f},modifier=Modifier.fillMaxWidth())
 Slider(value=BrainState.focus,onValueChange={BrainState.focus=it},valueRange=0f..100f)
 Text("调节预览值；开始任务后作用于当前选中的剑。")
 }else{
 Text("情绪类型：${BrainState.emotions[BrainState.emotion]}")
 BrainState.emotions.forEachIndexed{i,e->Button(onClick={BrainState.emotion=i}){Text(e)}}
 Text("颜色为研究启发的视觉设计；群体关联存在文化差异，不代表诊断或个人情绪事实。")
 }
 Text(if(BrainState.running)"任务进行中"else "等待中间面板选择任务并开始")
}}
