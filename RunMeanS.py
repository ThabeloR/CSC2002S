import os

def main():
    cmd = 'make runMeanFilterSerial argument="/home/thabelo/TSHTHA094_CSC2002S_PCP1/data/main.png /home/thabelo/TSHTHA094_CSC2002S_PCP1/data/mainout.png 3"'
    os.system(cmd)
    cmd = 'make runMeanFilterSerial argument="/home/thabelo/TSHTHA094_CSC2002S_PCP1/data/main.png /home/thabelo/TSHTHA094_CSC2002S_PCP1/data/mainout.png 11"'
    os.system(cmd)
    cmd = 'make runMeanFilterSerial argument="/home/thabelo/TSHTHA094_CSC2002S_PCP1/data/main.png /home/thabelo/TSHTHA094_CSC2002S_PCP1/data/mainout.png 15"'
    os.system(cmd)
    cmd = 'make runMeanFilterSerial argument="/home/thabelo/TSHTHA094_CSC2002S_PCP1/data/main.png /home/thabelo/TSHTHA094_CSC2002S_PCP1/data/mainout.png 25"'
    os.system(cmd)
    
main()