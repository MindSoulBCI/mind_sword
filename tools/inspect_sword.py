import bpy
bpy.ops.wm.read_factory_settings(use_empty=True)
bpy.ops.import_scene.gltf(filepath='/Users/apple/Downloads/soward_link_in_description.glb')
for o in bpy.context.scene.objects:
 print(o.name,o.type,tuple(o.dimensions),tuple(o.rotation_euler))
