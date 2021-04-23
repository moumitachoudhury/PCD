import os
import sys
i = int(sys.argv[1])
os.system("javac Main.java")
os.system("java Main {} 2>&1 | tee output/test.txt".format(i, i) )
