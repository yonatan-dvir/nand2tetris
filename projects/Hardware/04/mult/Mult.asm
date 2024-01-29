//init R2 to 0
@2
M=0
//if R0==0 set R2 to 0 and jump to END
@0
D=M
@END
D;JEQ
//if R0==0 set R2 to 0 and jump to END
@1
D=M
@END
D;JEQ
//while R1>0: R2 += R0, R1-=1
(LOOP)
@0
D=M
@2
M=M+D
@1
M=M-1
D=M
@LOOP
D;JGT
//End of mult
(END)
@END
0;JMP

