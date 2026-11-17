package com.example.spatialalbum.p20261115.ui.account
import androidx.compose.runtime.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.*
import com.example.spatialalbum.p20261115.R
import com.example.spatialalbum.p20261115.account.AuthFailure
import com.example.spatialalbum.p20261115.account.ForumAuthClient
import kotlinx.coroutines.launch

@Composable fun AuthPage(onEnter:()->Unit,onGuest:()->Unit){
 val context=LocalContext.current
 val client=remember{ForumAuthClient.from(context)}
 val scope=rememberCoroutineScope()
 var register by remember{mutableStateOf(false)}
 var passwordLogin by remember{mutableStateOf(false)}
 var email by remember{mutableStateOf("")}
 var code by remember{mutableStateOf("")}
 var password by remember{mutableStateOf("")}
 var confirm by remember{mutableStateOf("")}
 var agreed by remember{mutableStateOf(false)}
 var busy by remember{mutableStateOf(false)}
 var codeHint by remember{mutableStateOf("")}
 var message by remember{mutableStateOf("")}
 var policy by remember{mutableStateOf<String?>(null)}
 if(policy!=null)PolicyDialog(policy=="privacy"){policy=null}
 fun api():ForumAuthClient=client?:throw AuthFailure("账号服务尚未配置。")
 Box(Modifier.fillMaxSize().padding(32.dp),contentAlignment=Alignment.Center){
 Column(Modifier.widthIn(max=520.dp).fillMaxWidth().verticalScroll(rememberScrollState()),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(12.dp)){
 Image(painterResource(R.drawable.ic_swords),"意念御剑 Logo",Modifier.size(96.dp).clip(CircleShape),contentScale=ContentScale.Crop)
 Text("意念御剑",style=PicoTheme.typography.headlineMedium)
 Text("从今天开始，成为武林中人...")
 Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)){
  Text(if(register)"注册账号"else"邮箱登录",modifier=Modifier.weight(1f),style=PicoTheme.typography.titleMedium)
  if(!register)Button(enabled=!busy,onClick={passwordLogin=!passwordLogin;message="";password=""}){Text(if(passwordLogin)"使用邮箱验证码"else"使用密码登录")}
 }
 TextField(value=email,onValueChange={email=it;code="";codeHint="";message=""},placeholder={Text("账号 / 邮箱地址")},singleLine=true,modifier=Modifier.fillMaxWidth(),enabled=!busy)
 if(register||!passwordLogin){
  Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(12.dp)){
   TextField(value=code,onValueChange={code=it.filter(Char::isDigit).take(6);message=""},placeholder={Text("邮箱验证码")},singleLine=true,modifier=Modifier.weight(1f),enabled=!busy)
   Button(enabled=!busy,onClick={
    busy=true
    scope.launch{
     try{
      val mail=ForumAuthClient.email(email)
      if(!ForumAuthClient.validEmail(mail)){message="请输入有效邮箱地址";return@launch}
      api().sendCode(mail)
      codeHint="验证码已发送到邮箱，请查收。"
      message=""
     }catch(e:AuthFailure){message=e.message?:"发送失败"}
     catch(_:Exception){message="无法连接账号服务，请检查网络"}
     finally{busy=false}
    }
   }){Text("获取验证码")}
  }
  if(codeHint.isNotEmpty())Text(codeHint,style=PicoTheme.typography.bodySmall)
 }
 if(register||passwordLogin){
  TextField(value=password,onValueChange={password=it;message=""},placeholder={Text(if(register)"设置密码（6–20 位字母或数字）"else"密码")},visualTransformation=PasswordVisualTransformation(),singleLine=true,modifier=Modifier.fillMaxWidth(),enabled=!busy)
 }
 if(register){
  TextField(value=confirm,onValueChange={confirm=it;message=""},placeholder={Text("再次输入密码")},visualTransformation=PasswordVisualTransformation(),singleLine=true,modifier=Modifier.fillMaxWidth(),enabled=!busy)
 }
 Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.Center){
  Checkbox(checked=agreed,onCheckedChange={agreed=it},enabled=!busy)
  Text("我已阅读并同意")
  Link(onClick={policy="terms"}){Text("用户协议")}
  Text("及")
  Link(onClick={policy="privacy"}){Text("隐私政策")}
 }
 if(message.isNotEmpty())Text(message,style=PicoTheme.typography.bodySmall)
 Button(enabled=agreed&&!busy,onClick={
  if(!agreed){message="请先同意用户协议与隐私政策";return@Button}
  busy=true
  scope.launch{
   try{
    val mail=ForumAuthClient.email(email)
    if(!ForumAuthClient.validEmail(mail)){message="请输入有效邮箱地址";return@launch}
    if(register){
     if(!ForumAuthClient.validPassword(password)){message="密码需为 6–20 位字母或数字";return@launch}
     if(password!=confirm){message="两次输入的密码不一致";return@launch}
     if(!ForumAuthClient.validCode(code)){message="请输入 6 位邮箱验证码";return@launch}
     api().register(mail,password,code)
    }else if(passwordLogin){
     if(password.isEmpty()){message="请输入密码";return@launch}
     api().login(mail,password)
    }else{
     if(!ForumAuthClient.validCode(code)){message="请输入 6 位邮箱验证码";return@launch}
     api().loginByCode(mail,code)
    }
    password="";confirm="";code="";onEnter()
   }catch(e:AuthFailure){message=e.message?:"操作失败"}
   catch(_:Exception){message="无法连接账号服务，请检查网络"}
   finally{busy=false}
  }
 },modifier=Modifier.fillMaxWidth()){Text(if(busy)"处理中…"else if(register)"完成注册并进入"else"登录")}
 Row(horizontalArrangement=Arrangement.spacedBy(16.dp)){
  Link(onClick={if(!busy){register=!register;code="";password="";confirm="";codeHint="";message=""}}){Text(if(register)"已有账号，去登录"else"没有账号，去注册")}
  Link(onClick={if(!busy)onGuest()}){Text("游客体验账户")}
 }
 Text("验证码由服务端发送到邮箱",style=PicoTheme.typography.bodySmall)
 Text("成都脑云矩阵科技有限公司",style=PicoTheme.typography.bodySmall)
 }
 }
}
