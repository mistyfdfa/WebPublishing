import charaCore
import sceneCore
import mapCore
import opsCore
	
class GameSession:
	def __init__(self):
		self.scene_map = mapCore.GameMap('mnu start')
		self.player = charaCore.PlayerCharacter()
		self.player.saveScene('mnu start')
		# raw_input("Engine Initialized")
		
	#gameflow operations
	def startEngine(self):
		# raw_input("Engine Started")
		choice = opsCore.clearPrintFile("start", "mnu", self.player.txtSpd()).lower()
		# choice = "n"
		
		if choice == "q":
			self.scene_map.reset('mnu quit')
		elif choice == "l":
			self.scene_map.reset('mnu load')
		elif choice == "n":
			self.scene_map.reset('mnu new')
		elif choice == "o":
			self.scene_map.reset('mnu options')
		else:
			self.scene_map.reset('mnu bad')
		
		#set up loop parameters
		self.current_scene = self.scene_map.firstScene()
		self.last_scene = self.scene_map.loadScene('scn the end')
				
		while self.current_scene != self.last_scene:
			self.destination_tag = self.current_scene.enter(self.player)
			self.player.saveScene(self.current_scene.tag())
			self.current_scene = self.scene_map.loadScene(self.destination_tag)
						
				
		#make sure to print last scene
		self.current_scene.enter(self.player)