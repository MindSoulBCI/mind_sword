package com.example.spatialalbum.p20261115.platform
import android.app.Activity
import android.os.Bundle
import android.content.Intent
import java.io.File
class ImportActivity:Activity(){
 override fun onCreate(s:Bundle?){super.onCreate(s);if(s==null)startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply{type="*/*";addCategory(Intent.CATEGORY_OPENABLE)},10)}
 override fun onActivityResult(r:Int,c:Int,d:Intent?){super.onActivityResult(r,c,d);val uri=d?.data;if(c!=RESULT_OK||uri==null){finish();return};Thread{
 val dir=File(filesDir,"swords").apply{mkdirs()};val tmp=File.createTempFile("sword-",".tmp",dir)
 try{contentResolver.openInputStream(uri)!!.use{input->tmp.outputStream().use{out->val b=ByteArray(8192);var total=0L;while(true){val n=input.read(b);if(n<0)break;total+=n;require(total<=50*1024*1024){"模型超过50MB"};out.write(b,0,n)}}};tmp.inputStream().use{require(it.read()==103&&it.read()==108&&it.read()==84&&it.read()==70){"请选择GLB模型"}};check(tmp.renameTo(File(dir,"御剑-${System.currentTimeMillis()}.glb")))}catch(e:Exception){runOnUiThread{android.widget.Toast.makeText(this,"导入失败：${e.message}",1).show()}}finally{tmp.delete()};runOnUiThread{finish()}
 }.start()}
}
