# Test Game 2
# Probably a hot mess

# import modules
import gameCore

# function for starting a new game
session = gameCore.GameSession()
activeGame = 1
while activeGame: 
	activeGame = session.startEngine()