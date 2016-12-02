import opsCore

#module for Scene Opeations
class Scene:

	def enter(self):
		raw_input("This is a test.")
		exit(0)

class LoadGame:
	
	def enter(self):
		exists = 0
		while exists != 1:
			savedName = opsCore.clearPrintLine("What was your name again?\n")
			exists = opsCore.fileCheck(savedName, "sav")
		loadedPlayer = charaCore.PlayerCharacter(savedName)
		loadedPlayer.processStats()
		return loadedPlayer
		
	def resumeGame(player, sceneMap):
		opsCore.bitPrint("\nWelcome back, %s" % returning_name.getName())
		
		raw_input("\nYou recall where you were last...")
		
		#pass the player to the scene
		current_map = mapCore.GameMap(returningName.getLastScene(), "scn")
		currentMap.enterCurrentScene()
				
		raw_input("Press any key to restart...")

class NewGame:

	def enter():
		self.pName = opsCore.clearPrintFile("ng", "mnu")
		
		opsCore.bitPrint("	Thanks for stopping by %s,\n\tperhaps we will see you in the future!" % pName)
		opsCore.bitPrint("	For now, back to the menuLoop!\n")
		
		raw_input("Press any key to restart...")
		return 'start'
		
class Options:

	def enter(self):
		choice = opsCore.clearPrintFile("opts", "mnu")
		return choice

class SaveGame:

	def enter(self, save_name):
		raw_input("Saving the game is curently unavailable.\n")
		raw_input("Press any key to restart...")
		
class StartGame:

	def enter(self, sceneMap):
		choice = opsCore.clearPrintFile("start", "mnu")
		
		if choice == "E" or choice == "e":
			opsCore.bitPrint("Goodbye!\n")
			current = sceneMap.getNextScene('the end')
		elif choice == "L" or choice == "l":
			curentPlayer = self.loadGame()
			self.resumeGame(curentPlayer, sceneMap)
		elif choice == "N" or choice == "n":
			self.newGame(sceneMap)
		elif choice == "O" or choice == "o":
			self.currentScene = self.sceneMap.nextScene('options')
		else:
			opsCore.bitPrint ("I am not sure what you want to do,\nwhy not pick something else?\n")
			raw_input("Press any key to continue...")
		
		return current
		
		
class TheEnd:

	def enter(self):
		choice = opsCore.clearPrintFile("win", "mnu")
		return choice