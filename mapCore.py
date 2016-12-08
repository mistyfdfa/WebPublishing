#mapCore
import sceneCore

class GameMap:
	menus = {
		'mnu bad': sceneCore.BadDestination(),
		'mnu load': sceneCore.LoadGame(),
		'mnu new': sceneCore.NewGame(),
		'mnu options': sceneCore.Options(),
		'mnu quit': sceneCore.Quit(),
		'mnu save': sceneCore.SaveGame(),
		'mnu start': sceneCore.StartGame(),
	}
	
	scenes = {
		'scn opening': sceneCore.Opening(),
		'scn death': sceneCore.Death(),
	}
	
	def __init__(self, start_scene):
		self.first_scene = start_scene
		self.prev_scene = start_scene
			
	#sets first scene to a new scene
	def reset(self, start_scene):
		self.first_scene = start_scene
	
	#run loadScene on first_scene
	def firstScene(self):
		return self.loadScene(self.first_scene)
	
	#gets a scene instance from the dictionary based on tag
	def loadScene(self,scene_name):
		if scene_name.startswith("mnu"):
			load_this = self.menus.get(scene_name.strip("\n"))
		elif scene_name.startswith("scn"):
			load_this = self.scenes.get(scene_name.strip("\n"))
		else:
			load_this = self.menus.get('mnu bad')
		
		return load_this