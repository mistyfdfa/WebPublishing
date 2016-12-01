class PlayerCharacter:
	def __init__ (self, saveName):
		fileName = "sav\\" + saveName.lower() + ".sav"
		self.saveFile = open(fileName)
		self.pS = []
		self.lastScene = 0
		self.currentScene = 0
				
	def processStats(self):
		self.pS = self.saveFile.readlines()
		self.lastScene = self.pS.pop(0)

	def confirmStats(self):
		print self.pS,

	def setProgress(number):
		self.lastScene = self.currentScene
		self.currentScene = number

	def getCurrScene(self):
		return self.currentScene
	def getLastScene(self):
		return self.lastScene
	
	def getName(self):
		return self.pS[0]
	def getSex(self):
		return self.pS[1]
	def getLibido(self):
		return self.pS[2]
	def getHeight(self):
		return self.pS[3]
	def getWeight(self):
		return self.pS[4]
	def getBuild(self):
		return self.pS[5]
	def getHairColor(self):
		return self.pS[6]
	def getHairStyle(self):
		return self.pS[7]
	def getHorns(self):
		return self.pS[8]
	def getTail(self):
		return self.pS[9]
	def getEyeColor(self):
		return self.pS[10]
	def getPupilShape(self):
		return self.pS[11]
	def getEarShape(self):
		return self.pS[12]
	def getBust(self):
		return self.pS[13]
	def getHips(self):
		return self.pS[14]
	def getButt(self):
		return self.pS[15]
	def getG(self):
		return self.pS[16]
	
	def setName(self, newName):
		self.pS[0] = newName
	def setSex(self, newSex):
		self.pS[1] = newSex
	def setLibido(self, newLibido):
		self.pS[2] = newLibido
	def setHeight(self, newHeight):
		self.pS[3] = newHeight
	def setWeight(self, newWeight):
		self.pS[4] = newWeight
	def setBuild(self, newBuild):
		self.pS[5] = newBuild
	def setHairColor(self, newHC):
		self.pS[6] = newHC
	def setHairStyle(self, newHS):
		self.pS[7] = newHS
	def setHorns(self, newHorns):
		self.pS[8] = newHorns
	def setTail(self, newTail):
		self.pS[9] = newTail
	def setEyeColor(self, newEC):
		self.pS[10] = newEC
	def setPupilShape(self, newPS):
		self.pS[11] = newPS
	def setEarShape(self, newES):
		self.pS[12] = newES
	def setBust(self, newBust):
		self.pS[13] = newBust
	def setHips(self, newHips):
		self.pS[14] = newHips
	def setButt(self, newButt):
		self.pS[15] = newbutt
	def setG(self, newG):
		self.pS[16] = newG