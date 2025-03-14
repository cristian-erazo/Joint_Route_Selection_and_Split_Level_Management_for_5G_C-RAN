import numpy as np
import os
import glob

MAX_VAL = 999999999

def getArrivalTimes(args, np_arrival):
	if args.probArrival == "Poisson":
		# tiempos de llegada (distribucion Poisson)
		return np.cumsum(np_arrival.poisson(1./args.arrival_lambda, args.numRequest),dtype=int)
	elif args.probArrival == "Exponential":
		# tiempos de llegada (distribucion Exponencial)
		return np.cumsum(np_arrival.exponential(scale=1./args.arrival_lambda,size=args.numRequest),dtype=int)
	elif args.probArrival == "Uniform":
		# tiempos de llegada (distribucion Uniforme)
		return np.cumsum(np_arrival.uniform(args.min, args.max, args.numRequest),dtype=int)
	else:
		# tiempos de llegada (distribucion Normal)
		return np.cumsum(np_arrival.normal(args.mu, args.sigma, args.numRequest),dtype=int)

def getUsageTimes(args, np_usage):
	if args.probUsage == "Exponential":
		# tiempos de uso de recursos (distribucion Exponencial)
		return np_usage.exponential(scale=args.usage_mean,size=args.numRequest).astype(int)
	elif args.probUsage == "Gamma":
		# tiempos de uso de recursos (distribucion Gamma)
		return np_usage.gamma(args.shape,scale=args.scale,size=args.numRequest).astype(int)
	elif args.probUsage == "Uniform":
		# tiempos de uso de recursos (distribucion Uniforme)
		return np_usage.uniform(args.MIN, args.MAX, args.numRequest).astype(int)
	else:
		# tiempos de uso de recursos (distribucion Normal)
		return np_usage.normal(args.MU, args.SIGMA, args.numRequest).astype(int)

def getRequestList(order,folder,requests,save=False,path=None):
	from pathlib import Path
	import shutil
	result = ''
	for i, idx in enumerate(order):
		if path is not None:
			newName = os.path.join('.', path, 'requests', f'{Path(requests[idx][0]).stem}_{requests[idx][1]}_{requests[idx][2]}.gml')
		else:
			newName = requests[idx][0], os.path.join(folder, 'requests', f'{Path(requests[idx][0]).stem}_{requests[idx][1]}_{requests[idx][2]}.gml')
		if save:
			shutil.copy(requests[idx][0], os.path.join(folder, 'requests', f'{Path(requests[idx][0]).stem}_{requests[idx][1]}_{requests[idx][2]}.gml'))
		result += newName.replace("\\","/") + ','
	return result

def generateScenario(folder, network, events, releases, requests, path):
	from pathlib import Path
	import shutil
	shutil.copy(network, folder)
	try:
		os.makedirs(os.path.join(folder,'requests'),exist_ok=True)
	except OSError as error:
		print("error al crear carpeta de escenarios!!")
		return ;
	file = open(os.path.join(folder,'scenario.conf'),"w")
	file.write(f'{os.path.join(".",path,Path(network).stem)}.gml\n')
	t_rel, t_arr = [x for x in releases], [x for x in events]
	x, y = 0,0
	n, m = len(t_rel), len(t_arr)
	while y < m:
		if x < n and t_arr[y] >= t_rel[x]:
			file.write(f'{t_rel[x]}\trelease\t{getRequestList(releases[t_rel[x]], folder, requests, False, path)[:-1]}\n')
			x += 1
		else:
			lista = ','.join(events[t_arr[y]][0])
			file.write(f'{t_arr[y]}\tadd\t{getRequestList(events[t_arr[y]][1], folder, requests, True, path)[:-1]}\t{lista}\n')
			y += 1
	while x < n:
		file.write(f'{t_rel[x]}\trelease\t{getRequestList(releases[t_rel[x]], folder, requests, False, path)[:-1]}\n')
		x += 1
	file.close()

def getSeed(seed_value):
	if seed_value == -1:
		seed_value = np.random.randint(1,MAX_VAL)
	return seed_value

def main(args):
	seed_usage = getSeed(args.seed_usage)
	seed_arrival = getSeed(args.seed_arrival)
	np_usage = np.random.default_rng(seed_usage)
	np_arrival = np.random.default_rng(seed_arrival)
	print(f'{seed_usage=},{seed_arrival=}')
	seed = f'{seed_usage}_{seed_arrival}'
	phy_nets = glob.glob(os.path.join('networks', '*', "*.gml"))
	vn_nets_folder = glob.glob(os.path.join('requests', '*', '*'))
	path = os.path.join(args.outputFolder, f's_{seed}')
	try:
		os.makedirs(path,exist_ok=True)
	except OSError as error:
		print("error al crear carpeta de escenarios!!")
		return ;
	x = 0
	topology = None
	init = 0
	end = 1
	for network in phy_nets:
		t = str(network).replace("\\","/").split("/")[1]
		if topology is None:
			topology = t
			init = 1
		if topology != t:
			print( f'topology: {topology} [{init}, {end}]')
			init = x + 1
			topology = t
		for requests_folder in vn_nets_folder:
			path = os.path.join(args.outputFolder, f's_{seed}', f'scenario{x+1}')
			relativePath = os.path.join(f's_{seed}', f'scenario{x+1}')
			try:
				os.makedirs(path,exist_ok=True)
				os.makedirs(os.path.join(path, "results"),exist_ok=True)
			except OSError as error:
				print(f"error al crear carpeta de escenario {x+1}!!")
				return ;
			# Tiempos de llegada de peticiones
			t0 = getArrivalTimes(args,np_arrival)
			# Tiempos de uso de las peticiones
			tu = getUsageTimes(args,np_usage)
			t0 -= np.min(t0)
			# tiempo de liberacion de recursos
			tf = t0 + tu

			times = {}
			releases = {}
			requests = []
			for u in np.unique(t0):
				times[u]=([],[])

			for u in np.unique(tf):
				releases[u] = ([])

			u = 0
			request = glob.glob(os.path.join(requests_folder, '*.gml'))
			for i in range(args.numRequest):
				requests.append((request[i], t0[i], tf[i]))
				times[t0[i]][0].append(str(tu[i]))
				times[t0[i]][1].append(u)
				releases[tf[i]].append(u)
				u += 1

			generateScenario(path,network,times,releases,requests,relativePath)
			x += 1
		end = x
	print( f'topology: {topology} [{init}, {end}]')

if __name__ == "__main__":
	import argparse
	parser = argparse.ArgumentParser(description='generador de escenarios realistas para la version en linea del problema SCRDF')
	parser.add_argument("--seed_arrival", '-s1', nargs='?', type=int, default=-1, help="semilla para los numeros pseudoaleatorios (tiempos de llegada)")
	parser.add_argument("--seed_usage", '-s2', nargs='?', type=int, default=-1, help="semilla para los numeros pseudoaleatorios (tiempos de uso)")
	parser.add_argument("--numRequest",'-r', default=100, nargs=1, type=int, help="cantidad de peticiones a simular")
	parser.add_argument("--outputFolder",'-o', default=os.path.join('.','simulation'), nargs=1, type=str, help="carpeta de salida de los archivos de simulacion")

	parser.add_argument('--probArrival','-pR', default='Poisson', choices=['Poisson', 'Exponential', 'Uniform', 'Normal'], help="distribucion de probabilidad empleada para obtener los tiempos de llegada de las peticiones")
	parser.add_argument("--arrival_lambda",'-l', default=4./10., nargs='?', type=float, help="tasa de llegada de peticiones de recursos para distribucion exponencial (1/lambda)")
	parser.add_argument("--arrival_min",'-min', default=1, nargs='?', type=float, help="valor minimo para la distribucion uniforme (tiempo llegada)")
	parser.add_argument("--arrival_max",'-max', default=10, nargs='?', type=float, help="valor maximo para la distribucion uniforme (tiempo llegada)")
	parser.add_argument("--arrival_mu",'-mu', default=10, nargs='?', type=float, help="valor de media para la distribucion normal (tiempo llegada)")
	parser.add_argument("--arrival_sigma",'-sigma', default=2, nargs='?', type=float, help="valor de desviacion para la distribucion normal (tiempo llegada)")

	parser.add_argument("--probUsage",'-pU', default='Exponential', nargs='?', choices=['Exponential', 'Gamma', 'Uniform', 'Normal'], type=str, help="funcion de probabilidad para simular los tiempos de uso de recursos")
	parser.add_argument("--shape",'-sh', default=50, nargs='?', type=float, help="forma de la distribucion")
	parser.add_argument("--scale",'-sc', default=1, nargs='?', type=float, help="dispercion de la distribucion")
	parser.add_argument("--usage_min",'-MIN', default=40, nargs='?', type=float, help="valor minimo para la distribucion uniforme (tiempo  uso)")
	parser.add_argument("--usage_max",'-MAX', default=120, nargs='?', type=float, help="valor maximo para la distribucion uniforme (tiempo uso)")
	parser.add_argument("--usage_mu",'-MU', default=50, nargs='?', type=float, help="valor de media para la distribucion normal (tiempo uso)")
	parser.add_argument("--usage_sigma",'-SIGMA', default=2, nargs='?', type=float, help="valor de desviacion para la distribucion normal (tiempo uso)")
	parser.add_argument("--usage_mean",'-M', default=200, nargs='?', type=float, help="valor de media para la distribucion exponencial (tiempo uso)")

	main(parser.parse_args())
