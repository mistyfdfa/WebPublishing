import opsCore

class PlayerCharacter:
	def __init__ (self):
		self.prev_scene_tag = 'start'
		self.p_name = "Unititialized"
		self.variant_num = opsCore.randomNum(0,2)
		self.txt_spd = 0.02 #default

	def createPlayer(self):
		#self.p_name = opsCore.clearPrintLine("What is your name?\n")
		self.p_name = "trevor"
		
	def loadPlayer(self):
		self.p_name = opsCore.clearPrintLine("What is your name?\n")
		
		if opsCore.fileCheck(self.p_name, "sav"):
			self.player_data = opsCore.fileRead(self.p_name, "sav")
			self.prev_scene_tag = self.player_data.pop(0)
			self.variant_num = self.player_data.pop(0)
			self.txt_spd = self.player_data.pop(0)
			opsCore.setTxtSpd(self.txt_spd)
			
			return self.prev_scene_tag
		else:
			exit(1)
			
	def savePlayer(self):
		#TODO if sav file exists, append number to name
		#if opsCore.fileCheck(self.p_name, "sav"):
			# open and write to file
		opsCore.writeSavFile(self.p_name,self.prev_scene_tag,self.variant_num)
		
	def saveScene(self, passed_scene_tag):
		self.prev_scene_tag = passed_scene_tag
	
	def prevScene(self):
		return self.prev_scene_tag
	
	def getName(self):
		return self.p_name
			
	def variant(self):
		return self.variant_num

	def txtSpd(self):	
		return self.txt_spd
		
	def chooseTxtSpd(self):
		choice = opsCore.clearPrintFile("txtspd","mnu", self.txt_spd).lower()
		
		if choice == 's':
			opsCore.setTxtSpd(0.05)
		if choice == 'm':
			opsCore.setTxtSpd(0.025)
		if choice == 'f':
			opsCore.setTxtSpd(0.01)
		if choice == 'i':
			opsCore.setTxtSpd(0.00)