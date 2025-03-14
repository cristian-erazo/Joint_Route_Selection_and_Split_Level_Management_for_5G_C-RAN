import networkx as nx
import matplotlib.pyplot as plt
import os
import numpy as np
import random as rnd
import math as m
import sys

inf = 999999999
node_id = 0
G = None

def orient(p, q, r, G):
	return (G.nodes[q]['x'] - G.nodes[p]['x']) * (G.nodes[r]['y'] - G.nodes[p]['y']) - (G.nodes[r]['x'] - G.nodes[p]['x']) * (G.nodes[q]['y'] - G.nodes[p]['y'])

def sortX(val):
	global G
	return G.nodes[val]['x']

def convexHull():
	global G
	P = list(G.nodes)
	P.sort(key=sortX)
	Lupper = []
	Llower = []
	Lupper.append(P[0])
	Lupper.append(P[1]);
	size = len(P)
	for i in range(2, size):
		Lupper.append(P[i])
		s = len(Lupper)
		while s > 2:
			if orient(Lupper[-3], Lupper[-2], Lupper[-1], G) < 0:
				Lupper.remove(Lupper[-2])
				s+=-1
			else:
				break
	Llower.append(P[-1])
	Llower.append(P[-2])
	i = size-3
	while i >= 0:
		Llower.append(P[i])
		s = len(Llower)
		while s > 2:
			if orient(Llower[-3], Llower[-2], Llower[-1], G) < 0:
				Llower.remove(Llower[-2])
				s+= -1
			else:
				break
		i += -1
	Llower.remove(Llower[0])
	Llower.remove(Llower[-1])
	return Lupper + Llower

def Diff(li1, li2):
	return (list(set(li1) - set(li2)))

def addEdge(G, a, b, min_bandwith, max_bandwith, max_type=3, exists=False):
	from math import hypot
	if not exists:
		G.add_edge(a, b)
	G[a][b]['type'] = rnd.randint(1, max_type)
	max_bandwith_mid = max_bandwith / 2
	max_bandwith_trd = max_bandwith / 3
	if G[a][b]['type'] >= 3:
		G[a][b]['type'] = 3
		G[a][b]['bandwith'] = rnd.uniform(min_bandwith, max_bandwith_trd)
	elif G[a][b]['type'] == 2:
		G[a][b]['bandwith'] = rnd.uniform(max_bandwith_trd, max_bandwith_mid)
	else:
		G[a][b]['bandwith'] = rnd.uniform(max_bandwith_mid, max_bandwith)
	x, y = G.nodes[b]['x'] - G.nodes[a]['x'], G.nodes[b]['y'] - G.nodes[a]['y']
	G[a][b]['distance'] = hypot(x, y)

def setNodeParams(G, i, node_type, min_prc, max_prc, min_prb=None, max_prb=None, min_theta=None, max_theta=None):
	G.nodes[i]['type'] = node_type
	G.nodes[i]['prc'] = rnd.randint(min_prc, max_prc)
	if min_prb is not None and max_prb is not None:
		G.nodes[i]['prb'] = rnd.randint(min_prb, max_prb)
	if min_theta is not None and max_theta is not None:
		G.nodes[i]['theta'] = rnd.randint(min_theta, max_theta)

def addRUs(G, hl5_id, maxRUs, mid_x, mid_y, args):
	global node_id
	num_RUs = rnd.randint(1, maxRUs)
	min_prb, max_prb, min_theta, max_theta, min_prc, max_prc, min_bw, max_bw = args.min_ru_prb, args.max_ru_prb, args.min_ru_theta, args.max_ru_theta, args.min_du_prc, args.max_du_prc, args.min_bandwith, args.max_bandwith
	RUs = []
	for i in range(num_RUs):
		G.add_node(node_id)
		while True:
			dx = rnd.randint(1, 5)
			dy = rnd.randint(1, 5)
			# print(dx, dy)
			if rnd.random() < 0.5:
				G.nodes[node_id]['x'] = G.nodes[hl5_id]['x'] + rnd.randint(-1, dx)
			else:
				G.nodes[node_id]['x'] = G.nodes[hl5_id]['x'] + rnd.randint(-dx, 1)
			if rnd.random() < 0.5:
				G.nodes[node_id]['y'] = G.nodes[hl5_id]['y'] + rnd.randint(-1, dy)
			else:
				G.nodes[node_id]['y'] = G.nodes[hl5_id]['y'] + rnd.randint(-dy, 1)
			if G.nodes[node_id]['x'] != G.nodes[hl5_id]['x'] or G.nodes[node_id]['y'] != G.nodes[hl5_id]['y']:
				break
		G.nodes[node_id]["pos"] = (G.nodes[node_id]['x'], G.nodes[node_id]['y'])
		setNodeParams(G, node_id, 1, min_prc, max_prc, min_prb, max_prb, min_theta, max_theta) # mark as RU
		# connect RU to Hl5 node
		addEdge(G, node_id, hl5_id, min_bw, max_bw, 5)
		RUs.append(node_id)
		node_id += 1
	return num_RUs, RUs

def is_connected(G):
	for x in G.nodes:
		if x != 0 and not nx.has_path(G, x, 0):
			return False
	return True

def getWaxman(N, beta, alpha, L, domain, max_iter=1000):
	global inf
	i = 0
	while i < max_iter:
		G = nx.waxman_graph(N, beta=beta, alpha=alpha, L=L, domain=domain, seed=rnd.randint(1, inf))
		if is_connected(G):
			return G
		i += 1
	return None

def getBarabasi(N, m, L, domain, scale=10, max_iter=1000):
	global inf
	i = 0
	while i < max_iter:
		G = nx.barabasi_albert_graph(N, m, seed=rnd.randint(1, inf))
		if is_connected(G):
			mid_x, mid_y = (domain[0] + domain[2]) // 2, (domain[1] + domain[3]) // 2
			pos = nx.spring_layout(G, k=L, scale=scale, center=[mid_x, mid_y])
			for node in G.nodes:
				G.nodes[node]['pos'] = pos[node]
			return G
		i += 1
	return None

def getTransitStub(N, L, domain, scale=10, max_iter=1000):
	global inf
	i = 0
	while i < max_iter:
		G = nx.random_internet_as_graph(N, seed=rnd.randint(1, inf))
		if is_connected(G):
			mid_x, mid_y = (domain[0] + domain[2]) // 2, (domain[1] + domain[3]) // 2
			pos = nx.spring_layout(G, k=L, scale=scale, center=[mid_x, mid_y])
			for node in G.nodes:
				G.nodes[node]['pos'] = pos[node]
			return G
		i += 1
	return None

def getSubGraph(G, n, N, beta, alpha, L, mid_x, mid_y, limit, scale, args):
	global node_id
	global inf
	min_x, max_x, min_y, max_y = inf, -inf, inf, -inf
	if G.nodes[n]['x'] >= mid_x and G.nodes[n]['y'] >= mid_y:
		domain = (G.nodes[n]['x'], G.nodes[n]['y'], G.nodes[n]['x'] + limit, G.nodes[n]['y'] + limit)
	elif G.nodes[n]['x'] >= mid_x and G.nodes[n]['y'] < mid_y:
		domain = (G.nodes[n]['x'], G.nodes[n]['y'] - limit, G.nodes[n]['x'] + limit, G.nodes[n]['y'])
	elif G.nodes[n]['x'] < mid_x and G.nodes[n]['y'] < mid_y:
		domain = (G.nodes[n]['x'] - limit, G.nodes[n]['y'] - limit, G.nodes[n]['x'], G.nodes[n]['y'])
	else:
		domain = (G.nodes[n]['x'] - limit, G.nodes[n]['y'], G.nodes[n]['x'], G.nodes[n]['y'] + limit)
	if args.graph_generator == "Waxman":
		subGraph = getWaxman(N, beta, alpha, L, domain)
	elif args.graph_generator == "Barabasi-Albert":
		subGraph = getBarabasi(N, 2, L, domain, scale=scale)
	elif args.graph_generator == "Internet-AS":
		subGraph = getTransitStub(N, L, domain, scale=scale)
	else:
		subGraph = None
	if subGraph:
		N1 = N - 1
		n1 = rnd.randint(0, N1)
		n2 = n1
		while n1 == n2:
			n2 = rnd.randint(0, N1)
		init = node_id
		for q in subGraph.nodes:
			G.add_node(node_id)
			G.nodes[node_id]["pos"] = (int(subGraph.nodes[q]["pos"][0]), int(subGraph.nodes[q]["pos"][1]))
			x, y = G.nodes[node_id]['pos']
			G.nodes[node_id]['x'] = x
			G.nodes[node_id]['y'] = y
			node_id += 1
			if x < min_x:
				min_x = x
			if x > max_x:
				max_x = x
			if y < min_y:
				min_y = y
			if y > max_y:
				max_y = y
		for s, e in subGraph.edges:
			addEdge(G, s + init, e + init, args.min_bandwith, args.max_bandwith)
		addEdge(G, n1 + init, n, args.min_bandwith, args.max_bandwith)
		addEdge(G, n2 + init, n, args.min_bandwith, args.max_bandwith)
		return subGraph, min_x, min_y, max_x, max_y, init
	return None, 0, 0, 0, 0, 0

def make_graph(args):
	global inf
	global node_id
	global G
	RUs, DUs, CUs, both = [], [], [], []
	limit = 7
	max_L2, max_L3, max_L4, max_L5 = 5, 3, 3, 3
	max_RUs = args.num_RUs
	HL3_N, HL4_N, HL5_N = 4, 3, 3
	min_x,max_x,min_y,max_y = inf,-inf,inf,-inf
	HL4_Nodes, HL3_Nodes = [], []

	if args.graph_generator == "Waxman":
		HL2_N = 5
		G = getWaxman(HL2_N, 0.4, 0.25, max_L2, (0,0,5,5))
	elif args.graph_generator == "Barabasi-Albert":
		HL2_N = rnd.randint(4,5)
		G = getBarabasi(HL2_N, 3, max_L2, (0,0,0,0), 5)
	elif args.graph_generator == "Internet-AS":
		HL2_N = 4
		G = getTransitStub(HL2_N, max_L2, (0,0,0,0), 5)
	else:
		print("Debe ingresar un generador de grafos válido (Waxman, Barabasi-Albert, Internet-AS), valor ingresado:", args.graph_generator)
		exit()

	nH, nCUs, nDUs, nRUs = 0, 0, 0, 0
	for g in G.nodes:
		G.nodes[g]["pos"] = (int(G.nodes[g]["pos"][0]*5), int(G.nodes[g]["pos"][1]*5))
		if nCUs < args.num_CUs:
			setNodeParams(G, g, 2, args.min_cu_prc, args.max_cu_prc) # mark as CU
			CUs.append(g)
			nCUs += 1
		else:
			setNodeParams(G, g, 3, args.min_tr_prc, args.max_tr_prc) # mark as intermediate
			nH += 1
		x, y = G.nodes[g]['pos']
		if x < min_x:
			min_x = x
		if x > max_x:
			max_x = x
		if y < min_y:
			min_y = y
		if y > max_y:
			max_y = y
		G.nodes[g]['x'], G.nodes[g]['y'] = x, y
		node_id += 1
	for s, e in G.edges:
		addEdge(G, s, e, args.min_bandwith, args.max_bandwith, max_type=2, exists=True)

	hull = convexHull()
	mid_x, mid_y = (min_x + max_x) // 2, (min_y + max_y) // 2
	for h in hull:
		HL3, HL3_min_x, HL3_min_y, HL3_max_x, HL3_max_y, HL3_idx = getSubGraph(G, h, HL3_N, 0.4, 0.9, max_L3, mid_x, mid_y, limit, 3, args)
		if HL3:
			HL3_mid_x, HL3_mid_y = (HL3_min_x + HL3_max_x) // 2, (HL3_min_y + HL3_max_y) // 2
			for hl3 in HL3.nodes:
				hl3_id = hl3 + HL3_idx
				#if nCUs < args.num_CUs or nDUs < args.num_DUs:
				#	setNodeParams(G, hl3_id, 4, args.min_cu_prc, args.max_cu_prc, args.min_ru_prb, args.max_ru_prb, args.min_ru_theta, args.max_ru_theta) # mark as CU/DU
				#	both.append(hl3_id)
				#	nCUs += 1
				#	nDUs += 1
				#else:
				setNodeParams(G, hl3_id, 3, args.min_tr_prc, args.max_tr_prc) # mark as intermediate
				nH += 1
				HL3_Nodes.append(hl3_id)
				HL4, HL4_min_x, HL4_min_y, HL4_max_x, HL4_max_y, HL4_idx = getSubGraph(G, hl3_id, HL4_N, 0.4, 0.5, max_L4, HL3_mid_x, HL3_mid_y, limit, 3, args)
				if HL4:
					HL4_mid_x, HL4_mid_y = (HL4_min_x + HL4_max_x) // 2, (HL4_min_y + HL4_max_y) // 2
					for hl4 in HL4.nodes:
						hl4_id = hl4 + HL4_idx
						#if nDUs < args.num_DUs or rnd.random() < 0.15:
						#	setNodeParams(G, hl4_id, 3, args.min_du_prc, args.max_du_prc) # mark as DU
						#	DUs.append(hl4_id)
						#	nDUs += 1
						#else:
						setNodeParams(G, hl4_id, 3, args.min_tr_prc, args.max_tr_prc) # mark as intermediate
						nH += 1
						HL4_Nodes.append(hl4_id)
						HL5, HL5_min_x, HL5_min_y, HL5_max_x, HL5_max_y, HL5_idx = getSubGraph(G, hl4_id, HL5_N, 0.4, 0.7, max_L5, HL4_mid_x, HL4_mid_y, limit, 3, args)
						if HL5:
							HL5_mid_x, HL5_mid_y = (HL5_min_x + HL5_max_x) // 2, (HL5_min_y + HL5_max_y) // 2
							for hl5 in HL5.nodes:
								hl5_id = hl5 + HL5_idx
								setNodeParams(G, hl5_id, 3, args.min_tr_prc, args.max_tr_prc) # mark as intermediate
								nH += 1
							for hl5 in HL5.nodes:
								hl5_id = hl5 + HL5_idx
								tot, nodes_RUs = addRUs(G, hl5_id, max_RUs, G.nodes[hl5_id]["x"], G.nodes[hl5_id]["y"], args)
								nRUs += tot
								RUs += nodes_RUs
	if nCUs < args.num_CUs:
		miss = args.num_CUs - nCUs
		size_hl3 = len(HL3_Nodes)
		if size_hl3 > miss:
			both = np.random.choice(HL3_Nodes, size=miss, replace=False)
			nCUs += miss
			for i in both:
				setNodeParams(G, i, 4, args.min_cu_prc, args.max_cu_prc, args.min_ru_prb, args.max_ru_prb, args.min_ru_theta, args.max_ru_theta) # mark as CU/DU
			nH -= miss
		else:
			both = HL3_Nodes
			nH -= size_hl3
	size_hl4 = len(HL4_Nodes)
	if size_hl4 > args.num_DUs:
		DUs = np.random.choice(HL4_Nodes, size=args.num_DUs, replace=False)
		nDUs = args.num_DUs
		for i in DUs:
			setNodeParams(G, i, 3, args.min_du_prc, args.max_du_prc) # mark as DU
	else:
		DUs = HL4_Nodes
		nDUs = size_hl4
	nH -= nDUs
	return G, len(G.nodes), len(G.edges), nCUs, nDUs, nRUs, nH, RUs, DUs, CUs, both

if __name__ == '__main__':
	import argparse
	from pathlib import Path
	import matplotlib as mpl

	parser = argparse.ArgumentParser(description='Genera las redes fisicas de acuerdo con la jerarquia de niveles de Telefonica')
	parser.add_argument("graph_generator", choices=['Barabasi-Albert','Waxman','Internet-AS'], help="generador de la topologia del grafo", type=str)
	parser.add_argument("num_CUs", help="cantidad máxima de nodos CU.", type=int)
	parser.add_argument("num_DUs", help="cantidad máxima de nodos DU.", type=int)
	parser.add_argument("num_RUs", help="cantidad máxima de nodos RU por nodo de nivel 5.", type=int)
	parser.add_argument("min_tr_prc", help="capacidad de procesamiento minimo para los nodos intermedios.", type=int)
	parser.add_argument("max_tr_prc", help="capacidad de procesamiento maximo para los nodos intermedios.", type=int)
	parser.add_argument("min_du_prc", help="capacidad de procesamiento minimo para los nodos DU.", type=int)
	parser.add_argument("max_du_prc", help="capacidad de procesamiento maximo para los nodos DU.", type=int)
	parser.add_argument("min_cu_prc", help="capacidad de procesamiento minimo para los nodos CU.", type=int)
	parser.add_argument("max_cu_prc", help="capacidad de procesamiento maximo para los nodos CU.", type=int)
	parser.add_argument("min_ru_prb", help="cantidad de bloques de recursos fisicos minimos para los nodos RU.", type=int)
	parser.add_argument("max_ru_prb", help="cantidad de bloquees de recursos fisicos maximos para los nodos RU.", type=int)
	parser.add_argument("min_ru_theta", help="minima area de cobertura para los nodos RU.", type=int)
	parser.add_argument("max_ru_theta", help="maxima area de cobertura para los nodos RU.", type=int)
	parser.add_argument("min_bandwith", help="ancho de banda minimo para cualquier enlace.", type=float)
	parser.add_argument("max_bandwith", help="ancho de banda maximo para cualquier enlace.", type=float)
	parser.add_argument("output_path", help="directorio de salida donde se guardara el grafo.", default="networks")
	parser.add_argument("--seed", "-s", help="semilla para generar los numeros aleatorios.", type=int, default=-1)
	parser.add_argument("--verbose", "-v", help="muestra informacion sobre los procesos realizados.", action="store_true")
	args = parser.parse_args()

	# set seed
	if args.seed == -1:
		args.seed = rnd.randint(1, inf)
	rnd.seed(args.seed)
	np.random.seed(rnd.randint(1, inf))
	if args.verbose:
		print("Seed: {}".format(args.seed))

	# make graph
	G, n, e, nCUs, nDUs, nRUs, nH, RUs, DUs, CUs, Both = make_graph(args)
	name = "s{}r{}d{}c{}t{}".format(args.seed, nRUs, nDUs, nCUs, nH)

	for i in G.nodes:
		if 'pos' in G.nodes[i]:
			del G.nodes[i]['pos']

	# export the graph
	args.output_path = args.output_path.replace("/", os.path.sep).replace("\\", os.path.sep)
	if args.verbose:
		print("Number of nodes: {} {}".format(n, len(G.nodes)))
		print("Number of edges: {} {}".format(e, len(G.edges)))
	if args.output_path == ".":
		pass
	else:
		if args.output_path[-1] != os.path.sep:
			args.output_path = "{}{}".format(args.output_path, os.path.sep)
		args.output_path = "{}{}{}".format(args.output_path, args.graph_generator, os.path.sep)
		if not os.path.exists(args.output_path):
			os.makedirs(args.output_path)
	out_path = "{}{}_Gn{}e{}.gml".format(args.output_path,name,n,e)
	nx.write_gml(G, out_path)
	print("Generated graph: {}".format(out_path))

	colors = []
	edge_color = []
	pos_xy = {}
	#labels = {}
	edges = list(G.edges)
	#for node in G.nodes:
	#	labels[node] = G.nodes[node]['prc']
	for i in G.nodes:
		pos_xy[i] = (G.nodes[i]['x'], G.nodes[i]['y'])
		# type = 0:ru, 1:du, 2:cu, 3:int, 4:cu/du
		if i in RUs: # RUs -> yellow
			colors.append('yellow')
		elif i in DUs: # DUs -> red
			colors.append('red')
		elif i in CUs: # CUs -> blue
			colors.append('blue')
		elif i in Both: # Both -> purple
			colors.append('purple')
		elif G.nodes[i]['type'] == 3: # Int -> green
			colors.append('green')
		else:
			print('Node type error!', G.node[i])
			colors.append('black')
	for edge in edges:
		i, j = edge
		edge_color.append((0.,0.,0., G[i][j]['bandwith'] / args.max_bandwith))
	cmap = mpl.colormaps['binary']
	nx.draw(G, pos=pos_xy,
		node_size=100,
		node_color=colors,
		edgelist=edges,
		edge_color=edge_color,
		edge_cmap=cmap,
		with_labels=False,
		font_size=10,
		#labels=labels
	)
	# draw the graph
	out_figure = "{}{}_Fig_n{}e{}.pdf".format(args.output_path,name,n,e)
	figure = plt.gcf()
	figure.set_size_inches(12,8)
	plt.savefig(out_figure, format="PDF", dpi=600)
	print("Generated figure: {}".format(out_figure))
	if args.verbose:
		plt.show()
