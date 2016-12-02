#mapCore
import sceneCore

class GameMap:
	scenes = {
		'load': sceneCore.LoadGame(),
		'options': sceneCore.Options(),
		'save': sceneCore.SaveGame(),
		'start': sceneCore.StartGame(),
		'the end': sceneCore.TheEnd(),
	}
	
	def __init__(self, startScene):
		self.start_scene = start_scene
		self.prev_scene
	
	def firstScene(self):
		return self.nextScene(self.startScene)
	
	def nextScene(self,scene_name):
		scene = self.scenes.get(scene_name)
		return scene

	def savePrevScene(self, scene_name):
		self.prev_scene = scene_name
		
	def prevScene(self):
		return self.prev_scene