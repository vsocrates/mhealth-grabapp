from random import shuffle

x = [i for i in range(10928)]
shuffle(x)

with open("orderpy.random", "w") as f:
	for num in x:
		f.write("%d\n" %  num)
