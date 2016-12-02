import charaCore
import sceneCore
import mapCore
import opsCore
	
class GameSession:
	def __init__(self):
		opsCore.setTxtSpd(0.02)
		self.scene_map = mapCore.GameMap('start')
		self.current_scene = self.sceneMap.firstScene()
		self.last_scene = self.sceneMap.nextScene('the end')
	
	#gameflow operations
	def startEngine(self):
		print self.current_scene
		print self.last_scene
		
		raw_input("Press any key...")
		
		#while self.current_scene != self.last_scene
		#	self.destination = self.current_scene.enter()
		#	current_scene = self.scene_map.nextScene(destination)