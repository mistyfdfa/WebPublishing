import opsCore

#module for Scene Opeations
class Scene:

	def enter(self):
		raw_input("This is a test.")
		exit(0)

class Options is a Scene:

	def enter(self)
		opsCore.clearPrintLine("Welcome to the Options Screen!")
		
