import bpy,math,random
from pathlib import Path
bpy.ops.wm.read_factory_settings(use_empty=True)
random.seed(42)
mat=bpy.data.materials.new('FocusFlame');mat.use_nodes=True
p=mat.node_tree.nodes.get('Principled BSDF');p.inputs['Base Color'].default_value=(.1,.6,1,1);p.inputs['Emission Color'].default_value=(.08,.5,1,1);p.inputs['Emission Strength'].default_value=5
for i in range(12):
 a=i*math.tau/12;r=.10
 bpy.ops.mesh.primitive_cone_add(vertices=5,radius1=.018,radius2=0,depth=.14+random.random()*.18,location=(math.cos(a)*r,math.sin(a)*r,random.uniform(-.3,.3)))
 bpy.context.object.data.materials.append(mat)
bpy.ops.export_scene.gltf(filepath='/Users/apple/Documents/ChatGPT/游戏 2/spatial-swords/app/src/main/assets/swords/focus-flame.glb',export_format='GLB')
bpy.ops.wm.save_as_mainfile(filepath='/Users/apple/Documents/ChatGPT/游戏 2/spatial-swords/focus-flame.blend')
