# My notes
# Phase 0
# Doing a Datastructures for the board using a 2D array, 1D array, Map, List w/ list
# When figuring out Rules for the MovesCalc, math is a friend
# Queen is bishop + rook 
# I simplified my movesCalc a lot after taking the exam and realizing how much I was copying over repeated code. 
# I found that for p1 the main issue I was running into was that I was forgetting to get rid of the piece at its start position when moving it.

# Phase 1
# Add Board ChessPiece[][] squares = new Chesspiece[8][8]
# addPiece & squares[posgetRow -1][posgetCol-1]-piece
# getPiece return square[posgetrow - 1][posgetcol - 1]
# When doing KnightCalc cont. not break and check if !inBounds

# Phase 3
# HTTP client asks server never other way 
# URL - uniform resource locator
# https:// byu.edu: 443/ ampi/city ?q=pro #3
# scheme   domain  port  path     parameters  anchor

# Phase 4
# SQL - declaritive language 
# install sql to get the tables running on different terminal 
# Make sure to check bycrypt on passwords and compare properly

# Phase 5
# Misunderstood how to implement observer
# Observer doesn't need to pass anything in other than game ID created with mapping 
# just prints default white board 

# Phase 6
# class UserGameCommand{} better to inheret 
# check that command messages can be used 
# Probably wiser to do a record b/c of emutability 
# Root client sends Make_move -> end moves if checkmate


