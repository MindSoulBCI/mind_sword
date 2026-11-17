package com.example.spatialalbum.p20261115.ui.swords
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import com.pico.spatial.core.ecs.*
import com.pico.spatial.core.ecs.resource.*
import com.pico.spatial.core.math.*
import com.pico.spatial.ui.foundation.content.SpatialView
import com.pico.spatial.ui.foundation.gesture.*
import com.pico.spatial.ui.foundation.geometry.*
import com.pico.spatial.ui.design.Text
import com.pico.spatial.ui.design.Button
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext
import kotlin.math.*
import com.example.spatialalbum.p20261115.ui.brain.BrainState

private data class Actor(val anchor:Entity,val path:String,var manual:Boolean=false,val parent:Actor?=null)
// Normalize on a new parent: imported root scales and translations belong to the asset.
private fun fitSword(visual:Entity,anchor:Entity,targetLength:Float,byHeight:Boolean=false){
 val fitted=Entity();anchor.addChild(fitted);fitted.addChild(visual)
 val bounds=visual.getVisualBounds(fitted)
 val extent=if(byHeight)bounds.size.y else maxOf(bounds.size.x,bounds.size.y,bounds.size.z)
 require(extent.isFinite()&&extent>0.000001f){"剑模型尺寸无效"}
 val factor=targetLength/extent
 fitted.components[TransformComponent::class.java]!!.apply{
 setScaleVector(Vector3(factor,factor,factor))
 setPosition(bounds.center*(-factor))
 }
 android.util.Log.i("SwordSizing","source=$extent target=$targetLength factor=$factor")
}
@Composable internal fun SwordScene(items:List<Sword>,immersive:Boolean,modifier:Modifier=Modifier.fillMaxSize(),onExit:()->Unit={},onReady:()->Unit={}){
 val ready by rememberUpdatedState(onReady)
 val context=LocalContext.current
 val jobs=rememberCoroutineScope()
 var flame by remember{mutableStateOf<Entity?>(null)}
 LaunchedEffect(flame){while(isActive){val enabled=BrainState.running&&BrainState.task=="专注";flame?.enabled=enabled
 flame?.components?.get(TransformComponent::class.java)?.setScaleVector(Vector3.ONE*(.25f+BrainState.focus/100f*.9f));delay(50)}}
 var hold by remember{mutableStateOf<Job?>(null)}
 var charging by remember{mutableStateOf(0f)}
 var clone by remember{mutableStateOf<(suspend (Actor)->Unit)?>(null)}

 var constrain by remember{mutableStateOf<((Entity)->Unit)?>(null)}
 val actors=remember{mutableListOf<Actor>()};var root by remember{mutableStateOf<Entity?>(null)}
 var paused by remember{mutableStateOf(false)};var error by remember{mutableStateOf("")}
 var loader by remember{mutableStateOf<(suspend ()->Unit)?>(null)}
 fun target(entity:Entity?):Actor?{var e=entity;while(e!=null){actors.firstOrNull{it.anchor==e}?.let{return it};e=e.getParent()};return null}
 LaunchedEffect(loader){loader?.let{try{it()}catch(e:CancellationException){throw e}catch(e:Exception){error="模型加载失败：${e.message}";ImmersionStatus.busy=false;ImmersionStatus.message=error}}}
 LaunchedEffect(root,paused){if(root!=null&&!paused){var last=0L;var t=0f;while(isActive){withFrameNanos{now->if(last!=0L)t+=(now-last)/1e9f*.18f;last=now;actors.forEachIndexed{i,a->if(!a.manual&&(immersive||a.parent!=null)){val angle=t+i*2f*PI.toFloat()/actors.size.coerceAtLeast(1); val center=a.parent?.anchor?.components?.get(TransformComponent::class.java)?.position
 a.anchor.components[TransformComponent::class.java]?.setPosition(if(center!=null)center+Vector3(sin(angle)*.4f,0f,cos(angle)*.4f) else Vector3(sin(angle)*5.4f,1.7f,-cos(angle)*5.4f));if(immersive)a.anchor.components[TransformComponent::class.java]?.setQuaternion(TransformComponent(Vector3.ZERO,EulerAngles(0f,t*180f/kotlin.math.PI.toFloat()*2f+i*40f,0f),Vector3.ONE).quaternion);constrain?.invoke(a.anchor)}}}}}}
 DisposableEffect(Unit){onDispose{hold?.cancel();root?.destroy();actors.clear()}}
 var transform by remember{mutableStateOf<((SpatialTransformValue)->Unit)?>(null)}
 Box(modifier){SpatialView(modifier=Modifier.fillMaxSize().pointerInput(Unit){detectSpatialPointerEvent(context,targetedToEntity=TargetEntity.any()){events->
 val down=events.firstOrNull{it.isDownEvent()};val a=target(down?.targetedEntity)
 if(a!=null){hold?.cancel();hold=jobs.launch{try{while(isActive&&actors.size<24){for(step in 1..30){charging=step/30f;delay(50)};clone?.invoke(a);charging=0f}}finally{charging=0f}}}
 if(events.any{it.isUpEvent()}||events.count{it.pressed}>1){hold?.cancel();hold=null}
 false
 }}.pointerInput(Unit){detectSpatialTransformGesture(context,targetedToEntity=TargetEntity.any()){transform?.invoke(it)}},attachments={if(immersive)AttachmentPanel("controls"){Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){Button(onClick={paused=!paused}){Text(if(paused)"继续御剑"else"暂停飞行")};Button(onClick={actors.forEach{it.manual=false;it.anchor.components[TransformComponent::class.java]?.setScaleVector(Vector3.ONE)}}){Text("召回剑阵")};Button(onClick=onExit){Text("退出沉浸")}}}},initial={content,attachments->
 root?.destroy();actors.clear()
 val scene=Entity();root=scene;content.addEntity(scene)
 constrain={entity->if(!immersive){
 val corner=content.convertPosition(Vector3.ZERO,ViewCoordinateSpace.Local,content.localSpatialCoordinateSpace)
 val halfW=(abs(corner.x)*.88f).coerceAtLeast(.02f)
 val halfH=(abs(corner.y)*.88f).coerceAtLeast(.02f)
 val t=entity.components[TransformComponent::class.java]!!
 var box=entity.getVisualBounds(scene)
 val shrink=min(1f,min(halfW*2/box.size.x.coerceAtLeast(.001f),halfH*2/box.size.y.coerceAtLeast(.001f)))
 if(shrink<1f){t.setScaleVector(t.scaleVector*shrink);box=entity.getVisualBounds(scene)}
 val xRoom=(halfW-box.size.x/2).coerceAtLeast(0f)
 val yRoom=(halfH-box.size.y/2).coerceAtLeast(0f)
 val x=box.center.x.coerceIn(-xRoom,xRoom);val y=box.center.y.coerceIn(-yRoom,yRoom)
 t.setPosition(t.position+Vector3(x-box.center.x,y-box.center.y,-box.center.z))
 }}
 attachments.entity("controls")?.let{scene.addChild(it);it.components[TransformComponent::class.java]!!.setPosition(Vector3(0f,1f,-1.3f))}
 loader={items.forEachIndexed{i,s->
 val visual=Entity.loadSuspend(s.path);coroutineContext.ensureActive();val anchor=Entity();scene.addChild(anchor)
 fitSword(visual,anchor,if(immersive)1.5f else .55f,byHeight=immersive)
 val bounds=visual.getVisualBounds(anchor)
 anchor.components.set(CollisionComponent(collisionShape=listOf(ShapeResource.createBox(bounds.size+Vector3(.12f,.12f,.12f))),physicsMaterial=PhysicsMaterialResource()))
 anchor.components.set(InteractableComponent());anchor.components.set(HoverEffectComponent())
 if(immersive){val a=i*2f*PI.toFloat()/items.size;anchor.components[TransformComponent::class.java]!!.setPosition(Vector3(sin(a)*5.4f,1.7f,-cos(a)*5.4f))}
 if(!immersive){
 val corner=content.convertPosition(Vector3.ZERO,ViewCoordinateSpace.Local,content.localSpatialCoordinateSpace)
 val size=visual.getVisualBounds(anchor).size
 val fit=min(abs(corner.x)*1.5f/size.x.coerceAtLeast(.01f),abs(corner.y)*1.1f/size.y.coerceAtLeast(.01f))
 if(fit.isFinite()&&fit>0f)anchor.components[TransformComponent::class.java]!!.setScaleVector(Vector3(fit,fit,fit))
 }
 constrain?.invoke(anchor)
 actors.add(Actor(anchor,s.path))
 if(i==0&&!immersive){val fx=Entity.loadSuspend("asset://swords/focus-flame.glb");anchor.addChild(fx);fx.enabled=false;flame=fx}
 };ready() }
 clone={source->
 if(actors.size<24){val visual=Entity.loadSuspend(source.path);coroutineContext.ensureActive()
 val anchor=Entity();scene.addChild(anchor)
 fitSword(visual,anchor,if(immersive)1.5f else .55f,byHeight=immersive)
 val original=source.anchor.components[TransformComponent::class.java]!!
 anchor.components[TransformComponent::class.java]!!.apply{setPosition(original.position+Vector3(.25f,0f,0f));setScaleVector(original.scaleVector);setQuaternion(original.quaternion)}
 anchor.components.set(CollisionComponent(collisionShape=listOf(ShapeResource.createBox(visual.getVisualBounds(anchor).size+Vector3(.08f,.08f,.08f))),physicsMaterial=PhysicsMaterialResource()))
 anchor.components.set(InteractableComponent());anchor.components.set(HoverEffectComponent());constrain?.invoke(anchor);actors.add(Actor(anchor,source.path,parent=source))
 }
 }
 transform={v->hold?.cancel();hold=null;target(v.targetEntity)?.let{a->a.manual=true;val t=a.anchor.components[TransformComponent::class.java]!!
 val zero=content.convertPosition(Vector3.ZERO,ViewCoordinateSpace.Global,content.localSpatialCoordinateSpace)
 val point=content.convertPosition(Vector3(v.dragAmount.x,v.dragAmount.y,v.dragAmount.z),ViewCoordinateSpace.Global,content.localSpatialCoordinateSpace)
 t.setPosition(t.position+point-zero)
 val rotation=content.convertRotation(v.rotation.toQuaternion(),ViewCoordinateSpace.Global,content.localSpatialCoordinateSpace);t.setQuaternion(rotation*t.quaternion)
 ;val scale=(t.scaleVector.x*v.scaleValue).coerceIn(.25f,3f);t.setScaleVector(Vector3(scale,scale,scale));constrain?.invoke(a.anchor)
 }}
 });if(charging>0f)Text("蓄电 ${(charging*100).toInt()}% · 松手取消");if(error.isNotEmpty())Text(error)}
}
