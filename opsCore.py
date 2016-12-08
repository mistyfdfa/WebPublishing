import os
import sys
import time
import random

#verify existence of files
def fileCheck(name, type):
		try:
			file_name = type + "\\" + name.lower() + "." + type
			open(file_name)
			return 1
		except IOError:
			print "%s file not found. Please try again." % type
			return 0

def fileRead(name, type):
	file_name = type + "\\" + name.lower() + "." + type
	file = open(file_name)
	lines = file.readlines()
	cleanup(lines)
	file.close()
		
	return lines
	
def writeSavFile(name,prev_scene,variant):
	file_name = "sav\\" + name.lower() + ".sav"
	save_file = open(file_name, 'w')
	save_file.write("%s\n" % name)
	save_file.write("%s\n" % prev_scene)
	save_file.write("%i\n" % variant)
	save_file.write("%f\n" % txt_spd)
	save_file.close()	
	
def randomNum(lower, upper):
	return random.randint(lower, upper)

def cleanup(lines):
	for line in lines:
		line.strip(" ")
		line.strip("\n")
	return lines
		
#clear screen and prompt user			
def clearPrintFile(screen, type, txt_spd):
	os.system('cls')
	printGUI(screen)
	
	if fileCheck(screen, type):
		lines = fileRead(screen, type)
		for line in lines:
			bitPrint(line, txt_spd)
		printGUI('footer')
		return raw_input("\n> ")
	else:
		return "%s file failed to load." % type

def clearPrintLine(line, txt_spd):
	os.system('cls')
	#printGUI?
	bitPrint(line, txt_spd)
	return raw_input("\n> ")

def printGUI(which_gui):
	# eventually have smarter guis
	# if which_gui.startswith("scn"):
	#	raw_input("Have a scene!")
	#elif which_gui.startswith("mnu"):
	# 	raw_input("Have a menu!")
	
	if which_gui != "footer" and which_gui != "txtspd":
		which_gui="game" #for now, hard coded
	
	exists = fileCheck(which_gui, "gui")
	if exists == 1:
		lines = fileRead(which_gui, "gui")
		for line in lines:
			print line,
	else:
		exit(0)
		
def isGUIOption(choice):
	if choice == "s" or choice == "o" or choice == "q":
		return 1
	else:
		return 0

def getGUIScene(choice):
	if choice == "s":
		return 'mnu save'
	if choice == "o":
		return 'mnu options'
	if choice == "q":
		return 'mnu quit'

#text speed
def bitPrint(str, txt_spd):
	for c in str:
		sys.stdout.write(c)
		sys.stdout.flush()
		time.sleep(txt_spd)