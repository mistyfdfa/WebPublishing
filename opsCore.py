import os
import sys
import time
txtSpd = 0.02

def setTxtSpd(speed):
	txtSpd = speed

#verify existence of files
def fileCheck(name, type):
		try:
			fileName = type + "\\" + name.lower() + "." + type
			open(fileName)
			return 1
		except IOError:
			print "%s file not found. Please try again." % type
			return 0

def fileRead(name, type):
		fileName = type + "\\" + name.lower() + "." + type
		file = open(fileName)
		lines = file.readlines()
		file.close()
		return lines

#clear screen and prompt user			
def clearPrintFile(screen, type):
	os.system('cls')
	exists = fileCheck(screen, type)
	if exists == 1:
		lines = fileRead(screen, type)
		for line in lines:
			bitPrint(line)
		return raw_input("\n> ")
	else:
		return "%s file failed to load." % type

def clearPrintLine(line):
	os.system('cls')
	bitPrint(line)
	return raw_input("\n> ")
		
#text speed
def bitPrint(str):
	for c in str:
		sys.stdout.write(c)
		sys.stdout.flush()
		time.sleep(txtSpd) #TODO needs a variable for options