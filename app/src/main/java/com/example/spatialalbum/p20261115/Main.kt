package com.example.spatialalbum.p20261115
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.PicoTheme
import com.pico.spatial.ui.foundation.dsl.*
import com.pico.spatial.ui.platform.containers.*
import com.example.spatialalbum.p20261115.ui.swords.*
import kotlinx.coroutines.launch
fun mainApp(scope:SpatialAppScope)=with(scope){
 DefaultWindowContainer{PicoTheme{val nav=LocalSpatialNavigator.current;val job=rememberCoroutineScope();var opening by androidx.compose.runtime.saveable.rememberSaveable{mutableStateOf(true)};if(opening)com.example.spatialalbum.p20261115.ui.intro.SwordSplash{opening=false}else SwordScreen{job.launch{if(!ImmersionStatus.busy){ImmersionStatus.busy=true;ImmersionStatus.message="正在打开沉浸空间…"
 try{when(val result=nav.openStage("Gallery",StageStyle.Mixed)){
 OpenStageResult.Allowed->{nav.closeWindowContainer("SwordEEG","sword-eeg");nav.closeWindowContainer("SwordResults","sword-results");nav.openWindowContainer("SwordImmersionControls","sword-immersion-controls");ImmersionStatus.busy=false;ImmersionStatus.message="正在加载剑阵…"}
 OpenStageResult.NotAllowed->{ImmersionStatus.busy=false;ImmersionStatus.message="系统暂不允许进入沉浸模式，请退出其他沉浸应用后重试"}
 is OpenStageResult.Error->{ImmersionStatus.busy=false;ImmersionStatus.message="沉浸模式打开失败：${result.reason}"}
 }}catch(e:kotlinx.coroutines.CancellationException){ImmersionStatus.busy=false;throw e}catch(e:Exception){ImmersionStatus.busy=false;ImmersionStatus.message="打开失败：${e.message}"}
 }}}}}
 WindowContainer("SwordEEG",form=Form.Planar,defaultSize=WindowContainerSize(440.dp,640.dp),placement={containers.firstOrNull{it.id=="YourPlanarWindowContainer"}?.let{Placement.left(it,120.dp)}?:Placement.none()}){PicoTheme{com.example.spatialalbum.p20261115.ui.brain.BrainEeg()}}
 WindowContainer("SwordResults",form=Form.Planar,defaultSize=WindowContainerSize(440.dp,640.dp),placement={containers.firstOrNull{it.id=="YourPlanarWindowContainer"}?.let{Placement.right(it,120.dp)}?:Placement.none()}){PicoTheme{com.example.spatialalbum.p20261115.ui.brain.BrainResults()}}
 WindowContainer("SwordImmersionControls",form=Form.Planar,defaultSize=WindowContainerSize(420.dp,300.dp),placement={containers.firstOrNull{it.id=="YourPlanarWindowContainer"}?.let{Placement.bottom(it,80.dp)}?:Placement.none()}){PicoTheme{SwordImmersionControls()}}
 Stage(id="Gallery"){PicoTheme{SwordStage()}}
}
