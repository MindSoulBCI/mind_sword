import bpy, math
from mathutils import Vector
from pathlib import Path
out=Path('/Users/apple/Documents/ChatGPT/游戏 2/spatial-swords/app/src/main/assets/swords');out.mkdir(parents=True,exist_ok=True)
bpy.ops.wm.read_factory_settings(use_empty=True)
bpy.ops.import_scene.gltf(filepath='/Users/apple/Downloads/soward_link_in_description.glb')
groups=[['Object_4','Object_6','Object_8'],['Object_10','Object_12'],['Object_14','Object_16']]
for i,names in enumerate(groups):
 objs=[bpy.data.objects[n] for n in names]
 for o in objs:
  mat=o.matrix_world.copy();o.parent=None;o.matrix_world=mat
 # Keep the blade above the grip in Blender Z (glTF Y).
 blade=objs[0];grip=objs[1]
 blade_z=sum((blade.matrix_world@Vector(v)).z for v in blade.bound_box)/8
 grip_z=sum((grip.matrix_world@Vector(v)).z for v in grip.bound_box)/8
 if blade_z<grip_z:
  from mathutils import Matrix
  flip=Matrix.Rotation(math.pi,4,'X')
  for o in objs:o.matrix_world=flip@o.matrix_world
 pts=[o.matrix_world@Vector(v) for o in objs for v in o.bound_box]
 lo=Vector(tuple(min(p[j] for p in pts) for j in range(3)));hi=Vector(tuple(max(p[j] for p in pts) for j in range(3)));center=(lo+hi)*.5
 for o in objs:
  o.location-=center;o.location/= (hi.z-lo.z);o.scale/= (hi.z-lo.z)
 bpy.ops.object.select_all(action='DESELECT')
 for o in objs:o.select_set(True)
 bpy.ops.export_scene.gltf(filepath=str(out/f'sword-{i}.glb'),export_format='GLB',use_selection=True)
bpy.ops.wm.save_as_mainfile(filepath=str(out.parent.parent.parent.parent.parent/'swords.blend'))
