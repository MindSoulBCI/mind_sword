package com.example.spatialalbum.p20261115.ui.audio
import android.media.MediaPlayer
import android.media.AudioAttributes
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
object AudioSettings{var muted by mutableStateOf(false)}
@Composable fun SwordAudio(asset:String?,loop:Boolean=false){
 val context=LocalContext.current;val lifecycle=LocalLifecycleOwner.current.lifecycle
 DisposableEffect(asset,AudioSettings.muted,lifecycle){
 var player:MediaPlayer?=null
 if(asset!=null&&!AudioSettings.muted){try{
 player=MediaPlayer().apply{
 setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
 context.assets.openFd("audio/$asset").use{setDataSource(it.fileDescriptor,it.startOffset,it.length)}
 isLooping=loop;setVolume(.35f,.35f);setOnPreparedListener{if(lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))it.start()};prepareAsync()
 }
 }catch(e:Exception){player?.release();player=null;android.util.Log.w("SwordAudio","Audio unavailable",e)}}
 val observer=LifecycleEventObserver{_,event->try{when(event){Lifecycle.Event.ON_PAUSE->player?.pause();Lifecycle.Event.ON_RESUME->player?.start();else->Unit}}catch(_:IllegalStateException){}}
 lifecycle.addObserver(observer)
 onDispose{lifecycle.removeObserver(observer);player?.release()}
 }
}
