import zipfile,wave,io,math,struct,random
from pathlib import Path
out=Path('spatial-swords/app/src/main/assets/audio');out.mkdir(exist_ok=True)
z=zipfile.ZipFile('spatial-swords/audio-sources/swishes.zip');sounds=[]
for name in sorted(z.namelist()):
 if not name.startswith('swishes/') or not name.endswith('.wav'):continue
 w=wave.open(io.BytesIO(z.read(name)));raw=w.readframes(w.getnframes());width=w.getsampwidth();channels=w.getnchannels();rate=w.getframerate()
 samples=[int.from_bytes(raw[i:i+width],'little',signed=True)/(2**(8*width-1)) for i in range(0,len(raw),width)]
 mono=[sum(samples[i:i+channels])/channels for i in range(0,len(samples),channels)]
 sounds.append((mono,rate))
rate=44100;mix=[[0.,0.] for _ in range(rate*5)];random.seed(7)
for i in range(24):
 source,sr=sounds[i%len(sounds)];start=int((i*.13)*rate);pan=(i%7)/6;speed=.7+(i%5)*.1
 for n in range(int(len(source)*rate/sr/speed)):
  pos=int(n*sr/rate*speed)
  if start+n>=len(mix) or pos>=len(source):break
  v=source[pos]*.33
  mix[start+n][0]+=v*(1-pan*.7);mix[start+n][1]+=v*(.3+pan*.7)
with wave.open(str(out/'swords.wav'),'wb') as w:
 w.setparams((2,2,rate,0,'NONE','not compressed'));w.writeframes(b''.join(struct.pack('<hh',int(math.tanh(a)*26000),int(math.tanh(b)*26000)) for a,b in mix))
