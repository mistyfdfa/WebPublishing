import charaCore
import sceneCore
import opsCore
	
class GameSession:
	def __init__(self):
		opsCore.setTxtSpd(0.02)
	
	#gameflow operations
	def startGame(self):			
		choice = opsCore.clearPrintFile("start", "mnu")
		
		if choice == "E" or choice == "e":
			opsCore.bitPrint("Goodbye!\n")
			return 0
		elif choice == "L" or choice == "l":
			curentPlayer = self.loadGame()
			self.resumeGame(curentPlayer)
			return 1
		elif choice == "N" or choice == "n":
			self.newGame()
			return 1
		else:
			opsCore.bitPrint ("I am not sure what you want to do,\nwhy not pick something else?\n")
			raw_input("Press any key to continue...")
			return 1
	
	def newGame(self):
		pName = opsCore.clearPrintFile("ng", "mnu")
		
		opsCore.bitPrint("	Thanks for stopping by %s,\n\tperhaps we will see you in the future!" % pName)
		opsCore.bitPrint("	For now, back to the menuLoop!\n")
		
		raw_input("Press any key to restart...")
	
	def resumeGame(self, returningName):
		opsCore.bitPrint("\nWelcome back, %s" % returningName.getName())
		
		raw_input("\nYou recall where you were last...")
		
		#pass the player to the scene
		currentMap = mapCore.GameMap(returningName.getLastScene(), "scn")
		currentMap.enterCurrentScene()
				
		raw_input("Press any key to restart...")
	
	def loadGame(self):
		exists = 0
		while exists != 1:
			savedName = opsCore.clearPrintLine("What was your name again?\n")
			exists = opsCore.fileCheck(savedName, "sav")
		loadedPlayer = charaCore.PlayerCharacter(savedName)
		loadedPlayer.processStats()
		return loadedPlayer
	
	def saveGame(self, saveName):
		raw_input("Saving the game is curently unavailable.\n")
		raw_input("Press any key to restart...")