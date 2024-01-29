// Put screen location in @0
(LOOP)
@SCREEN
D=A
@0
M=D
// Jump to BLACK or WHITE if keyboard is pressed
(ISPRESSED)
@KBD
D=M
@BLACK
D;JGT
@WHITE
D;JEQ
@ISPRESSED
0;JMP
// Put -1 or 0 in @1 according to if keyboard is pressed or not
(BLACK)
@1
M=-1
@CHANGECOLOR
0;JMP
(WHITE)
@1   
M=0
@CHANGECOLOR
0;JMP
// Change all the screen color if keyboard is pressed
(CHANGECOLOR)
// Set D to be -1 or 0
@1
D=M
// Go to screen location and fill the pixel if needed
@0
A=M
M=D
// Increment the screen location that shows in @0
@0
M=M+1
// If not arrived to KBD location, keep change pixels
D=M
@KBD
D=A-D
@CHANGECOLOR
D;JGT
// Jump Back to start to keep checking the keyboard
@LOOP
0;JMP
