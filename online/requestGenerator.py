import argparse
import networkx as nx
import os
import numpy as np
import random as rnd
from math import hypot
import glob

MAX_VAL = 999999999
min_x, min_y, max_x, max_y = MAX_VAL, MAX_VAL, -MAX_VAL, -MAX_VAL
G, RUs = {}, {}
maxCUs = 0
maxRUs = 0
maxE = 0
R = []

parser = argparse.ArgumentParser(description='Genera un conjunto de peticiones de recursos fisicos.')
parser.add_argument("min_num_CUs", help="cantidad minima de CU solicitadas.", type=int)
parser.add_argument("max_num_CUs", help="cantidad maxima de CU solicitadas.", type=int)
parser.add_argument("min_num_RUs", help="cantidad minima de RU solicitadas.", type=int)
parser.add_argument("max_num_RUs", help="cantidad maxima de RU solicitadas.", type=int)
parser.add_argument("min_bandwith", help="restriccion de ancho de banda minimo para cualquier enlace.", type=int)
parser.add_argument("max_bandwith", help="restriccion de ancho de banda maximo para cualquier enlace.", type=int)
parser.add_argument("min_delay", help="restriccion de retardo minimo para cualquier enlace.", type=float)
parser.add_argument("max_delay", help="restriccion de retardo maximo para cualquier enlace.", type=float)
parser.add_argument("min_cu_prc", help="solicitud de procesamiento minimo para las CU solicitadas.", type=int)
parser.add_argument("max_cu_prc", help="solicitud de procesamiento maximo para las CU solicitadas.", type=int)
parser.add_argument("min_ru_prb", help="solicitud de bloques de recursos fisicos minimos para las RU solicitadas.", type=int)
parser.add_argument("max_ru_prb", help="solicitud de bloquees de recursos fisicos maximos para las RU solicitadas.", type=int)
parser.add_argument("min_ru_prc", help="solicitud de procesamiento minimo para las RU solicitadas.", type=int)
parser.add_argument("max_ru_prc", help="solicitud de procesamiento maximo para las RU solicitadas.", type=int)
parser.add_argument("output_path", help="directorio de salida donde se guardara el conjunto de peticiones generado.", default="requests")
parser.add_argument("num_requests", help="cantidad de peticiones solicitadas.", type=int, default=100)
parser.add_argument("--seed","-s", help="semilla para generar los numeros aleatorios.", type=int, default=-1)
parser.add_argument("--verbose","-v", help="muestra informacion sobre los procesos realizados.", action="store_true", default=False)

def isValid(x, y, G, RUs):
	for network in G:
		atLeastOne = False
		for i in RUs[network]:
			RU = G[network].nodes[i]
			if hypot(RU['x'] - x, RU['y'] - y) <= RU['theta']:
				atLeastOne = True
				break
		if atLeastOne is False:
			return False
	return True

args = parser.parse_args()

if args.seed == -1:
	args.seed = rnd.randint(1, MAX_VAL)

rnd.seed(args.seed)
np.random.seed(rnd.randint(1, MAX_VAL))

if(args.max_num_CUs > args.max_num_RUs):
	print("Error: the number of CUs must be less or equal to the number of RUs.")
	exit(0)

if args.verbose:
	print("Seed: {}".format(args.seed))

networks = glob.glob("./networks/*/*.gml".replace("/",os.path.sep))
# load each network to get min_x, min_y and max_x, max_y
for network in networks:
	if args.verbose:
		print(f'loading {network}')
	G[network] = nx.read_gml(network)
	rus = []
	for i in list(G[network].nodes):
		if G[network].nodes[i]['type'] == 1:
			rus.append(i)
			x, y = G[network].nodes[i]['x'], G[network].nodes[i]['y']
			if x < min_x:
				min_x = x
			if y < min_y:
				min_y = y
			if x > max_x:
				max_x = x
			if y > max_y:
				max_y = y
	RUs[network] = rus

# create the requests
for q in range(args.num_requests):#generar un grafo por cada peticion de recursos
	R.append(nx.Graph())
	K = R[q]
	CUs = int(args.min_num_CUs + (args.max_num_CUs - args.min_num_CUs) * rnd.random())
	nRUs = int(args.min_num_RUs + (args.max_num_RUs - args.min_num_RUs) * rnd.random())
	while CUs > nRUs:
		CUs = int(args.min_num_CUs + (args.max_num_CUs - args.min_num_CUs) * rnd.random())
		nRUs = int(args.min_num_RUs + (args.max_num_RUs - args.min_num_RUs) * rnd.random())
	n = CUs + nRUs
	e = 0
	if maxCUs < CUs:
		maxCUs = CUs
	if maxRUs < nRUs:
		maxRUs = nRUs
	#select the RUs and CUs
	txt = "Nodo: {} type: {}"
	vRUs = []
	for i in range(CUs):# set type to CU
		K.add_node(i)
		K.nodes[i]['type'] = 2
		K.nodes[i]['prc'] = int(args.min_cu_prc + (args.max_cu_prc - args.min_cu_prc) * rnd.random())
		if args.verbose:
			print(txt.format(i, "CU"))
	for i in range(CUs, n):# set type to RU
		K.add_node(i)
		K.nodes[i]['type'] = 1
		K.nodes[i]['prc'] = int(args.min_ru_prc + (args.max_ru_prc - args.min_ru_prc) * rnd.random())
		K.nodes[i]['ant'] = 1 #int(args.min_ru_ant + (args.max_ru_ant - args.min_ru_ant) * rnd.random())
		K.nodes[i]['prb'] = int(args.min_ru_prb + (args.max_ru_prb - args.min_ru_prb) * rnd.random())
		# validate that each point has at least one possible RU for each network
		while True:
			x, y = int(min_x + (max_x - min_x) * rnd.random()), int(min_y + (max_y - min_y) * rnd.random())
			if isValid(x, y, G, RUs):
				K.nodes[i]['x'] = x
				K.nodes[i]['y'] = y
				break
		vRUs.append(i)
		if args.verbose:
			print(txt.format(i, "RU"))
	x = 0
	np.random.shuffle(vRUs)
	for i in range(CUs):
		indx = 0
		total = nRUs - x
		if i + 1 < CUs:
			if rnd.random() < 0.4:
				j = rnd.randint(1, int(total/(CUs - i)))
			else:
				j = int(total/(CUs - i))
		else:
			j = total
		while indx < j:
			K.add_edge(vRUs[x],i)
			K[vRUs[x]][i]['bandwith'] = int(args.min_bandwith + ((args.max_bandwith - args.min_bandwith) * rnd.random()))
			K[vRUs[x]][i]['delay'] = int(args.min_delay + ((args.max_delay - args.min_delay) * rnd.random()))
			e += 1
			x += 1
			indx += 1
		if args.verbose:
			print("CU[{}] links to {} RUs.".format(i, j))
	if maxE < e:
		maxE = e
	if args.verbose:
		print("[{}] RUs: {} CUs: {} Number of edges: {}".format(q+1, nRUs, CUs, e))

if args.output_path == ".":
	pass
else:
	args.output_path = args.output_path.replace("/", os.path.sep).replace("\\", os.path.sep)

folder = "R{}CUs{}RUs{}E{}{}{}{}".format(args.num_requests,maxCUs,maxRUs,maxE,os.path.sep,args.seed,os.path.sep)
args.output_path = "{}{}{}".format(args.output_path,os.path.sep,folder)

if os.path.exists(args.output_path) is False:
	os.makedirs(args.output_path)

for q in range(args.num_requests):
	K = R[q]
	file = "{}R{}n{}e{}.{}".format(args.output_path,q+1,len(K.nodes),len(K.edges),"gml")
	nx.write_gml(K, file)
	if args.verbose:
		print("Generated graph: {}".format(file))

if args.verbose:
	print("Generated {} requests".format(args.num_requests))
