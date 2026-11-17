import bpy,math
from pathlib import Path
from mathutils import Vector,Matrix
out=Path('/Users/apple/Documents/ChatGPT/游戏 2/spatial-swords/app/src/main/assets/swords')
for key,path in [('crystal','/Users/apple/Downloads/crytal_sward_-_blaze_motion (1).glb'),('classic','/Users/apple/Downloads/3d__sward_model.glb')]:
 bpy.ops.wm.read_factory_settings(use_empty=True);bpy.ops.import_scene.gltf(filepath=path)
 objects=list(bpy.context.scene.objects);meshes=[o for o in objects if o.type=='MESH']
 pts=[o.matrix_world@Vector(v) for o in meshes for v in o.bound_box];lo=Vector([min(p[j] for p in pts) for j in range(3)]);hi=Vector([max(p[j] for p in pts) for j in range(3)])
 root=bpy.data.objects.new('SwordPresentation',None);bpy.context.collection.objects.link(root)
 for o in objects:
  if o.parent is None:o.parent=root
 extent=max(hi-lo);root.scale=Vector((1,1,1))/extent;root.location=-(hi+lo)*.5/extent
 bpy.context.view_layer.update()
 if key=="crystal":root.matrix_world=Matrix.Rotation(math.pi,4,"X")@root.matrix_world
 bpy.context.view_layer.update()
 bpy.ops.object.select_all(action='SELECT');bpy.ops.export_scene.gltf(filepath=str(out/f'{key}.glb'),export_format='GLB',use_selection=True)
 scene=bpy.context.scene;scene.render.engine='CYCLES';scene.cycles.samples=16;scene.render.resolution_x=256;scene.render.resolution_y=320;scene.render.resolution_percentage=100;scene.render.film_transparent=True
 bpy.ops.object.camera_add(location=(1,-2,.2));cam=bpy.context.object;cam.rotation_euler=(-cam.location).to_track_quat('-Z','Y').to_euler();cam.data.type='ORTHO';cam.data.ortho_scale=1.25;scene.camera=cam
 bpy.ops.object.light_add(type='AREA',location=(0,-3,3));light=bpy.context.object;light.data.energy=500;light.data.size=4;light.rotation_euler=(-light.location).to_track_quat('-Z','Y').to_euler()
 scene.world=bpy.data.worlds.new('World');scene.world.color=(.4,.4,.4);scene.render.filepath=str(out/f'{key}.png');bpy.ops.render.render(write_still=True)
