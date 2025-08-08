import os
import glob

fsize = 27 # texto eje y
lfsize = 14 # texto de los ticks
# f_x, f_y = 4.5, 3
f_x, f_y = 4.5, 4


correction = True
plot_figures = True

show_f1_avg = False
show_f3_avg = False

# show_f1_norm = False
# show_f3_norm = False
# show_acc = False
# show_time = False

show_f1_norm = False
show_f3_norm = False
show_acc = False
show_time = False

show_topologies = True

def holm_bonferroni( pvals ):
	pv = []
	m = len(pvals)
	for i in range(m):
		pv.append([i, pvals[i]])
	pv = sorted(pv, key=lambda x:x[1])
	alpha = 0.05
	result = [False] * m
	for i in range(m):
		idx = pv[i][0]
		p = pv[i][1]
		alpha_corrected = alpha / m
		if (correction and p < alpha_corrected) or (not correction and p < alpha):
			result[idx] = True
		m -= 1
	return result

def significancia_estadistica(allresults, df, isMinimization=False):
	import scipy.stats as st
	# print(df[allresults])
	medians = df[allresults].median().to_numpy()
	# print("Median: ", medians)
	means = df[allresults].mean().to_numpy()
	# print("Mean: ", means)
	best = 0
	n = len(allresults)
	contenders = [ i for i in range(0, n) ]
	for i in range(1, n):
		if isMinimization and ((medians[i] < medians[best]) or (medians[i] == medians[best] and means[i] < means[best])) or \
			not isMinimization and ((medians[i] > medians[best]) or (medians[i] == medians[best] and means[i] > means[best])):
			best = i
	contenders.remove(best)
	pvals = []
	for i in contenders:
		pvalue = 1
		try:
			if isMinimization:
				pvalue = st.mannwhitneyu( df[allresults[i]], df[allresults[best]], alternative='greater', nan_policy='omit' )[1] # For minimization
			else:
				pvalue = st.mannwhitneyu( df[allresults[i]], df[allresults[best]], alternative='less', nan_policy='omit' )[1] # For maximization
		except:
			pvalue = 1
		pvals.append(pvalue)
	return best, contenders, holm_bonferroni( pvals )

def set_colors(plt, bp, colors):
	for i, color in enumerate(colors):
		plt.setp(bp['boxes'][i], color=color)
		plt.setp(bp['medians'][i], color=color)
		plt.setp(bp['means'][i], markeredgecolor=color)
		plt.setp(bp['fliers'][i], markeredgecolor=color)

	idx = 0
	for i in range(0, len(bp['whiskers']), 2):
		plt.setp(bp['whiskers'][i], color=colors[idx])
		plt.setp(bp['whiskers'][i+1], color=colors[idx])
		plt.setp(bp['caps'][i], color=colors[idx])
		plt.setp(bp['caps'][i+1], color=colors[idx])
		idx += 1

def add_signf(ax, sign, plt, name):
	n = len(sign[1]) + 1
	y = 1.05
	is_log = name.count('time') > 0
	if is_log:
		yinit, y = ax.get_ylim()
		i = 10
		while i < y:
			i *= 10
		y = 2*i

	ax.plot([sign[0] + 1], [y], marker='o', markeredgecolor='k', markerfacecolor='k', markersize=7, clip_on=False )
	for i in range(n - 1):
		if sign[2][i]:
			ax.plot([sign[1][i] + 1], [y], marker='+', markeredgecolor='k', markerfacecolor='k', markersize=9, clip_on=False )
		else:
			ax.plot([sign[1][i] + 1], [y], marker='o', markeredgecolor='k', markerfacecolor='white', markersize=7, clip_on=False )
	startx, endx = ax.get_xlim()
	if is_log:
		plt.plot([startx, endx], [ y/2, y/2 ], c="k", linestyle="-")
	else:
		plt.plot([startx, endx], [ y-0.05, y-0.05 ], c="k", linestyle="-")
	ax.figure.set_size_inches(f_x,f_y)
	plt.savefig('figures/%s.pdf' % (name), dpi=300, bbox_inches='tight')

def getMetricsFileList(scenarioFolder):
	return glob.glob(os.path.join(scenarioFolder, "results", "*.metrics"))

def getName(filePath):
	from pathlib import Path
	fn = Path(filePath).stem.replace(".out","").split("_")
	vals = len(fn)
	if vals == 1 or (vals == 2 and fn[0] in ['EA', 'MA', 'GA']):
		return fn[0]
	else:
		return f'{fn[0]}{fn[1]}'

def initialize(path):
	algNames = []
	for filePath in glob.glob(os.path.join(path, "*", "*", "results", "*.metrics")):
		name = getName(filePath)
		if name not in algNames:
			algNames.append(name)
	return algNames

# topology: Barabasi-Albert [1, 50]
# topology: Internet-AS [51, 100]
# topology: Waxman [101, 150]
def getTopology(path):
	scenario = path.replace("\\","/").split("/")[-1]
	id = int(scenario.replace("scenario",""))
	if id <= 50:
		return "Barabasi-Albert"
	elif id <= 100:
		return "Transit-stub"
	elif id <= 150:
		return "Waxman"
	else:
		return "Not Found"

def main(args):
	import numpy as np
	import pandas as pd
	import matplotlib.pyplot as plt
	from matplotlib.patches import Polygon
	from matplotlib import rc

	alpha1, alpha2 = 0.5, 0.5

	plt.rcParams["font.family"] = "serif"
	plt.rcParams["font.serif"] = "Times New Roman"
	plt.rcParams['text.latex.preamble']="\\usepackage{bm}"
	rc('text', usetex=True)

	path = os.path.join('.', args.outputFolder)
	try:
		os.makedirs(path,exist_ok=True)
	except OSError as error:
		print("error al crear carpeta de resultados!!")
		return ;
	names = initialize(args.inputFolder)
	r = float(args.numRequest)
	topology_results = {'scenario':[],'topology':[],'algorithm':[],'f_global':[],'Doc_UoL':[],'acc_global':[],'time_avg':[],'max_g3':[],'max_f0':[]}
	for scenarioPath in glob.glob(os.path.join(args.inputFolder,'*','*')):
		topology = getTopology(scenarioPath)
		alg = {}
		fx,acc,Mf,Ma,g3 = 0,0,0,0,0
		for name in names:
			alg[f'{name}_loc'],alg[f'{name}_uol'],alg[f'{name}_acc'],alg[f'{name}_time'] = [],[],[],[]
		for filePath in getMetricsFileList(scenarioPath):
			name = getName(filePath)
			values = np.zeros(4,dtype=float)
			for line in open(f'{filePath}','r'):
				data = line.replace(",",".").split("\t")
				values += float(data[0]),float(data[1]),float(data[2]),float(data[3])
				if pd.isna(float(data[1])):
					print(filePath)
			alg[f'{name}_loc'].append(values[0]/r)
			alg[f'{name}_uol'].append(values[1]/r)
			fx = (values[0]/r) * (values[1]/r)
			if values[2] > r:
				print(filePath,values[2],r,len(open(f'{filePath}','r').readlines()))
			acc = values[2] / r
			g3 = fx * alpha1 + acc * alpha2
			alg[f'{name}_acc'].append(acc)
			alg[f'{name}_time'].append(values[3]/r)
			if fx > Mf:
				Mf = fx
			if g3 > Ma:
				Ma = g3
		for name in names:
			tot = len(alg[f'{name}_loc'])
			if tot != 0:
				if tot > 1:
					values = np.zeros(4,dtype=float)
					# 0: DoC, 1: UoL, 2: r, 3: time    4: f3
					for v in zip(alg[f'{name}_loc'],alg[f'{name}_uol'],alg[f'{name}_acc'],alg[f'{name}_time']):
						values += v
					values = values / tot
					time = values[3]
					acc = values[2]
					fx = values[0] * values[1]
				else:
					time = alg[f'{name}_time'][0]
					acc = alg[f'{name}_acc'][0]
					fx = alg[f'{name}_loc'][0] * alg[f'{name}_uol'][0]
				f3 = alpha1 * fx + alpha2 * acc

			else:#algoritmos que no tienen datos
				print(f'Missing data for algorithm \'{name}\' in scenario {scenarioPath}')
				fx = None
				acc = None
				f3 = None
				time = None

			topology_results['scenario'].append(scenarioPath)
			topology_results['topology'].append(topology)
			topology_results['algorithm'].append(name)
			topology_results['max_g3'].append(Ma)
			topology_results['max_f0'].append(Mf)
			topology_results['f_global'].append(f3)
			topology_results['Doc_UoL'].append(fx)
			topology_results['acc_global'].append(acc)
			topology_results['time_avg'].append(time)

	topology_df = pd.DataFrame.from_dict(topology_results)
	# guardar datos
	topology_df.to_csv(os.path.join(path,f'{args.outputName}_topology_values.csv'),sep='\t')
	# graficar datos
	print(names)
	
	#     0        1           2          3        4        5           6           7        8          9
	#    'EA'    'EAgre'    'GAf4'      'GRE'   'GREnew'   'MA'        'SRh'
	#    'EA'    'EAgre'    'GAf3'      'GAf4'  'GRE'      'GREnew'    'MA'        'SRh'
	# labels = [names[3],names[1],names[6],names[2],names[4],names[5]]
	labels = [names[4],names[7],names[5],names[1],names[3],names[2],names[6]]
	print(labels)

	# azul> #36648B rojo> #8B1A1A verde> #698B22
	# alg_labels  = ["$GR$","$EA$","$SR_{h}$","$GA$","$GR_{new}$","$MA$"]
	# colors      = ['k'   ,'k'   ,'k'       ,'k'   ,'#8B1A1A'   ,'#698B22']
	alg_labels  = ["$GR$","$SR_{h}$","$GR_{new}$","$EA$","$GA$","$GA^{*}$","$MA$"]
	colors      = ['k'   ,'k'       ,'#8B1A1A'   ,'k'   ,'k'   ,'k'       ,'#698B22']

	y_ticks_lbl = ['0.0','','0.2','','0.4','','0.6','','0.8','','1.0']
	y_ticks = [0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0]

	n = len(labels)

	meanpointprops = dict(marker='D', markerfacecolor='white', markersize=6, linestyle='none')
	flierprops = dict(marker='x', markerfacecolor='white', markersize=6, linestyle='none')
	out = args.outputName.split("_")
	g3,fx,acc_global,time_avg = [],[],[],[]

	for name in labels:
		g3.append(f'{name}_g3')
		fx.append(f'{name}_fx')
		acc_global.append(f'{name}_acc_global')
		time_avg.append(f'{name}_time_avg')
		topology_df['fx'] = topology_df['Doc_UoL'] / topology_df['max_f0']
		topology_df['g3'] = topology_df['f_global'] / topology_df['max_g3']

	topologies_columns = ['g3', 'fx', 'acc_global', 'time_avg']
	topologies_builder = ["Waxman", "Transit-stub", "Barabasi-Albert"]

	df_group = topology_df.groupby(['topology','algorithm'])
	dict_topologies = {}
	for topology in topologies_builder:
		dict_topologies[topology] = {'df':pd.DataFrame()}
		putTopologyColumn = True
		for algorithm in labels:
			styled_columns = {}
			if putTopologyColumn:
				list_columns = ['topology']
			else:
				list_columns = []
			for column in topologies_columns:
				new_name = f'{algorithm}_{column}'
				styled_columns[column] = new_name
				list_columns.append(new_name)
			values = df_group.get_group((topology, algorithm)).rename(columns=styled_columns)
			to_join = values[list_columns].reset_index(drop=True) #.set_index('topology', inplace=True)
			# print(to_join)
			dict_topologies[topology]['df'] = pd.concat([dict_topologies[topology]['df'], to_join], axis=1)
			if putTopologyColumn:
				dict_topologies[topology]['df'].reset_index(drop=True, inplace=True)
				putTopologyColumn = False

		dict_topologies[topology]['sign_g3'] = significancia_estadistica(g3, dict_topologies[topology]['df'])
		dict_topologies[topology]['sign_f0'] = significancia_estadistica(fx, dict_topologies[topology]['df'])
		dict_topologies[topology]['sign_acc_global'] = significancia_estadistica(acc_global, dict_topologies[topology]['df'])
		dict_topologies[topology]['sign_time_avg'] = significancia_estadistica(time_avg, dict_topologies[topology]['df'], isMinimization=True)
		print("-----------------------------------------------------------")
		print(dict_topologies[topology]['df'])
		print("sign g3")
		print(dict_topologies[topology]['sign_g3'])
		print("sign fx")
		print(dict_topologies[topology]['sign_f0'])
		print("sign acc")
		print(dict_topologies[topology]['sign_acc_global'])
		print("sign time")
		print(dict_topologies[topology]['sign_time_avg'])

		# plot normalized f_global
		ax, bp = dict_topologies[topology]['df'].boxplot(column=g3,showmeans=True,return_type='both',notch=False,vert=True,meanprops=meanpointprops,meanline=False,flierprops=flierprops)
		set_colors(plt, bp, colors)
		ax.tick_params(labelsize=lfsize)
		ax.set_xticks(range(1,n+1), labels=alg_labels)
		ax.set_ylabel("$f_{global}$", fontsize=fsize)
		ax.set_yticks(y_ticks)
		ax.set_yticklabels(y_ticks_lbl)
		ax.set_ylim(-0.05, 1.1)
		ax.set_axisbelow(True)
		ax.yaxis.grid(True, linestyle='-', which='major', color='lightgrey', alpha=0.5)
		ax.xaxis.grid(False)
		add_signf(ax, dict_topologies[topology]['sign_g3'], plt, f'{out[0]}_{topology}_norm_f3')
		if plot_figures:
			plt.show()
		
		# plot normalized fx_global
		ax, bp = dict_topologies[topology]['df'].boxplot(column=fx,showmeans=True,return_type='both',notch=False,vert=True,meanprops=meanpointprops,meanline=False,flierprops=flierprops)
		set_colors(plt, bp, colors)
		ax.tick_params(labelsize=lfsize)
		ax.set_xticks(range(1,n+1), labels=alg_labels)
		ax.set_ylabel("$DoC_{global} \cdot UoL_{global}$", fontsize=fsize-7)
		ax.set_yticks(y_ticks)
		ax.set_yticklabels(y_ticks_lbl)
		ax.set_ylim(-0.05, 1.1)
		ax.set_axisbelow(True)
		ax.yaxis.grid(True, linestyle='-', which='major', color='lightgrey', alpha=0.5)
		ax.xaxis.grid(False)
		add_signf(ax, dict_topologies[topology]['sign_f0'], plt, f'{out[0]}_{topology}_norm')
		if plot_figures:
			plt.show()

		# plot acc_global
		ax, bp = dict_topologies[topology]['df'].boxplot(column=acc_global,showmeans=True,return_type='both',notch=False,vert=True,meanprops=meanpointprops,meanline=False,flierprops=flierprops)
		set_colors(plt, bp, colors)
		ax.tick_params(labelsize=lfsize)
		ax.set_xticks(range(1,n+1), labels=alg_labels)
		ax.set_ylabel("$r_{global}$", fontsize=fsize)
		ax.set_yticks(y_ticks)
		ax.set_yticklabels(y_ticks_lbl)
		ax.set_ylim(-0.05, 1.1)
		ax.set_axisbelow(True)
		ax.yaxis.grid(True, linestyle='-', which='major', color='lightgrey', alpha=0.5)
		ax.xaxis.grid(False)
		add_signf(ax, dict_topologies[topology]['sign_acc_global'], plt, f'{out[0]}_{topology}_acc')
		if plot_figures:
			plt.show()

		# plot time_avg
		ax, bp = dict_topologies[topology]['df'].boxplot(column=time_avg,showmeans=True,return_type='both',notch=False,vert=True,meanprops=meanpointprops,meanline=False,flierprops=flierprops)
		set_colors(plt, bp, colors)
		ax.set_yscale("log")
		ax.tick_params(labelsize=lfsize)
		ax.set_xticks(range(1,n+1), labels=alg_labels)
		ax.set_ylabel('$T_{avg}\ (ms)$', fontsize=fsize-3)
		ax.set_axisbelow(True)
		ax.yaxis.grid(True, linestyle='-', which='major', color='lightgrey', alpha=0.5)
		ax.xaxis.grid(False)
		add_signf(ax, dict_topologies[topology]['sign_time_avg'], plt, f'{out[0]}_{topology}_time')
		if plot_figures:
			plt.show()

if __name__ == "__main__":
	import argparse

	parser = argparse.ArgumentParser(description='programa para agrupar los resultados obtenidos por tipo de topologia empleada para la version en linea del problema SCRDF')
	parser.add_argument("--inputFolder",'-i', default='./temp/', type=str, nargs='?', help="carpeta en la que se encuentran todos los escenarios")
	parser.add_argument("--numRequest",'-r', default=100, nargs='?', type=int, help="cantidad de peticiones por escenario")
	parser.add_argument("--outputFolder",'-o', default='./results/', nargs='?', type=str, help="carpeta donde se guardaran los resumenes de los resultados")
	parser.add_argument("--outputName",'-n', default='data', type=str, help="nombre del archivo que contendra los resultados")

	main(parser.parse_args())
