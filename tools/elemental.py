import bpy, math
from mathutils import Vector
from pathlib import Path
out=Path('/Users/apple/Documents/ChatGPT/游戏 2/spatial-swords/app/src/main/assets/swords')
bpy.ops.wm.read_factory_settings(use_empty=True)
bpy.ops.import_scene.gltf(filepath='/Users/apple/Downloads/elemental_swords.glb')
meshes=[o for o in bpy.context.scene.objects if o.type=='MESH']
print([(o.name,tuple(o.dimensions)) for o in meshes])
for i,o in enumerate(meshes):
 mat=o.matrix_world.copy();o.parent=None;o.matrix_world=mat
 bpy.ops.object.select_all(action='DESELECT');o.select_set(True);bpy.context.view_layer.objects.active=o
 bpy.ops.object.transform_apply(location=False,rotation=True,scale=True)
 o.rotation_euler.z=math.pi/2
 bpy.ops.object.transform_apply(location=False,rotation=True,scale=True)
 pts=[o.matrix_world@Vector(v) for v in o.bound_box];lo=Vector([min(p[j] for p in pts) for j in range(3)]);hi=Vector([max(p[j] for p in pts) for j in range(3)])
 o.location-=(lo+hi)*.5;o.location/=max(hi-lo);o.scale/=max(hi-lo)
 bpy.ops.export_scene.gltf(filepath=str(out/f'element-{i}.glb'),export_format='GLB',use_selection=True)
 # Render each actual textured sword on transparent background.
 for m in meshes:m.hide_render=m!=o
 scene=bpy.context.scene;scene.render.engine='CYCLES';scene.cycles.samples=16
 scene.render.resolution_x=256;scene.render.resolution_y=320;scene.render.resolution_percentage=100;scene.render.film_transparent=True
 bpy.ops.object.camera_add(location=(0,-2,0));cam=bpy.context.object;cam.rotation_euler=(Vector((0,0,0))-cam.location).to_track_quat('-Z','Y').to_euler();cam.data.type='ORTHO';cam.data.ortho_scale=1.2;scene.camera=cam
 bpy.ops.object.light_add(type='AREA',location=(0,-3,3));light=bpy.context.object;light.data.energy=500;light.data.shape='DISK';light.data.size=4;light.rotation_euler=(Vector((0,0,0))-light.location).to_track_quat('-Z','Y').to_euler()
 scene.world=scene.world or bpy.data.worlds.new('World');scene.world.color=(.4,.4,.4);scene.render.filepath=str(out/f'element-{i}.png');bpy.ops.render.render(write_still=True)
 bpy.data.objects.remove(cam,do_unlink=True);bpy.data.objects.remove(light,do_unlink=True)
