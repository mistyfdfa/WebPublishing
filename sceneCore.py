import opsCore
import charaCore

#module for Scene Operations
class Scene:

	def enter(self, player_character):
		raw_input("This is a test.")
		exit(0)
		
	def tag(self):
		return 'base'

# Menus
class BadDestination:

	def enter(self, player_character):
		txt_spd = player_character.txtSpd()
		opsCore.bitPrint ("I am not sure what you want to do,", txt_spd)
		opsCore.bitPrint ("\nwhy not pick something else?\n", txt_spd)
		raw_input("Press any key to return...")
		
		return player_character.prevScene()
		
	def tag(self):
		return 'mnu bad'

class LoadGame:
	
	def enter(self, loaded_player):
		prev_scene_tag = self.resumeGame(loaded_player)
		txt_spd = loaded_player.txtSpd()
		
		opsCore.bitPrint("\nWelcome back, %s" % loaded_player.getName(), txt_spd)
		
		raw_input("\nYou recall where you were last...")
		
		# raw_input(loaded_player.getName() + "'s Last Scene: " + prev_scene_tag)
		
		return prev_scene_tag
		
	def tag(self):
		return 'mnu load'
	
	#returns player's last scene
	def resumeGame(self, loaded_player):
		return loaded_player.loadPlayer()

class NewGame:

	def enter(self, new_player):
		new_player.createPlayer()
		
		# raw_input("Player created!")
		
		return 'scn opening'

	def tag(self):
		return 'mnu new'
		
class Options:

	def enter(self, player_character):
		txt_spd = player_character.txtSpd()
		choice = opsCore.clearPrintFile("opts", "mnu", txt_spd).lower()
		
		if opsCore.isGUIOption(choice):
			return opsCore.getGUIScene(choice)
		elif choice == 'a':
			player_character.chooseTxtSpd()
			return player_character.prevScene()
		elif choice == 'l':
			return 'mnu load'
		else:
			return player_character.prevScene()				

	def tag(self):
		return 'mnu options'
		
class Quit:
	
	def enter(self, player_character):
		txt_spd = player_character.txtSpd()
		opsCore.bitPrint("Thanks for playing!\n", txt_spd)
		exit(1)
	
	def tag(self):
		return 'mnu quit'
	
class SaveGame:

	def enter(self, player_character):
		txt_spd = player_character.txtSpd()
		opsCore.bitPrint("Saving will overwrite previous save.", txt_spd)
		choice = raw_input("\nIs this okay?\n>").lower()
		if choice == 'y':
			player_character.savePlayer()
		else:
			pass
		
		return player_character.prevScene()
		
	def tag(self):
		return 'mnu save'
		
class StartGame:

	def enter(self, player_character):
		txt_spd = player_character.txtSpd()
		choice = opsCore.clearPrintFile("start", "mnu", txt_spd).lower()
		
		if opsCore.isGUIOption(choice):
			return opsCore.getGUIScene(choice)
		elif choice == "l":
			return 'mnu load'
		elif choice == "n":
			return 'mnu new'
		else:
			return 'mnu bad'
		
	def tag(self):
		return 'start'

#scenes		
class Opening:
	def enter(self, player_character):
		txt_spd = player_character.txtSpd()
		
		try:
			var_num = player_character.variant()
		except:
			var_num = opsCore.randomNum(0,2)
		
		introduction = "1s_var%i" % var_num		
		choice = opsCore.clearPrintFile(introduction, "scn", txt_spd).lower()
		
		if opsCore.isGUIOption(choice):
			return opsCore.getGUIScene(choice)
		elif choice == "a":
			return 'scn order_app'
		elif choice == "e":
			return 'scn order_ent'
		else:
			return 'scn death'
		
		return choice
		
	def tag(self):
		return 'scn opening'

class Death:

	def enter(self, player_character):
		txt_spd = player_character.txtSpd()
		choice = opsCore.clearPrintFile("lose", "mnu", txt_spd).lower()
		return choice
	
	def tag(self):
		return 'scn death'