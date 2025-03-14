import argparse
import numpy as np
import os
import glob
from pathlib import Path
import matplotlib.pyplot as plt

fsize = 27 # texto eje y
lfsize = 14 # texto de los ticks
# f_x, f_y = 4.5, 3
f_x, f_y = 4.5, 4

parser = argparse.ArgumentParser(description='programa para resumir los resultados de las metricas empleadas para la version en linea del problema SCRDF')
parser.add_argument("--inputFolder",'-i', type=str, required=True, help="carpeta en la que se encuentran todos los escenarios")
parser.add_argument("--numRequest",'-r', default=100, nargs='?', type=int, help="cantidad de peticiones por escenario")
parser.add_argument("--outputFolder",'-o', default='results', nargs='?', type=str, help="carpeta donde se guardaran los resumenes de los resultados")
parser.add_argument("--outputName",'-n', default='data', type=str, help="nombre del archivo que contendra los resultados")

correction = True
plot_figures = True

show_f1_avg = False
show_f3_avg = False

# show_f1_norm = False
# show_f3_norm = False
# show_acc = False
# show_time = False

show_f1_norm = True
show_f3_norm = True
show_acc = True
show_time = True

def holm_bonferroni( pvals ):
	pv = []
	m = len(pvals)
	for i in range(m):
		pv.append([i, pvals[i]])
	pv = sorted(pv, key=lambda x:x[1])
	# Determine significance
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
	# print(len(bp['boxes']),len(bp['whiskers']),len(bp['caps']),len(bp['medians']),len(bp['means']),len(bp['fliers']))
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
	fn = Path(filePath).stem.replace(".out","").split("_")
	vals = len(fn)
	if vals == 1 or (vals == 2 and fn[0] in ['EA', 'MA', 'GA']):
		return fn[0]
	else:
		return f'{fn[0]}{fn[1]}'

def initialize(path):
	data = {'Scenario':[],'Max_fx':[],'Max_f3':[]}
	algNames = []
	for filePath in glob.glob(os.path.join(path, "*", "results", "*.metrics")):
		name = getName(filePath)
		if name not in algNames:
			algNames.append(name)
			data[f'{name}_loc'] = []
			data[f'{name}_uol'] = []
			data[f'{name}_acc'] = []
			data[f'{name}_time'] = []
			data[f'{name}_f0'] = []
			data[f'{name}_f3'] = []
	return data, algNames

def main(args):
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
	results, names = initialize(args.inputFolder)
	r = float(args.numRequest)
	for scenarioPath in glob.glob(os.path.join(args.inputFolder,'*')):
		results['Scenario'].append(scenarioPath)
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
					results[f'{name}_loc'].append(values[0])
					results[f'{name}_uol'].append(values[1])
					time = values[3]
					acc = values[2]
					fx = values[0] * values[1]
				else:
					results[f'{name}_loc'].append(alg[f'{name}_loc'][0])
					results[f'{name}_uol'].append(alg[f'{name}_uol'][0])
					time = alg[f'{name}_time'][0]
					acc = alg[f'{name}_acc'][0]
					fx = alg[f'{name}_loc'][0] * alg[f'{name}_uol'][0]
				f3 = alpha1 * fx + alpha2 * acc
				results[f'{name}_time'].append(time)
				results[f'{name}_f0'].append(fx)
				results[f'{name}_acc'].append(acc)
				results[f'{name}_f3'].append( f3 )

			else:#algoritmos que no tienen datos
				print(f'Missing data for algorithm \'{name}\' in scenario {scenarioPath}')
				results[f'{name}_loc'].append(None)
				results[f'{name}_uol'].append(None)
				results[f'{name}_time'].append(None)
				results[f'{name}_f0'].append(None)
				results[f'{name}_acc'].append(None)
				results[f'{name}_f3'].append(None)

		results['Max_fx'].append(Mf)
		results['Max_f3'].append(Ma)
	df = pd.DataFrame.from_dict(results)
	# guardar datos
	df.to_csv(os.path.join(path,f'{args.outputName}.csv'),sep='\t')
	# graficar datos
	print(names)
	
	#     0        1           2          3        4        5           6           7        8          9
	#    'EA'    'EAgre'    'GAf4'      'GRE'   'GREnew'   'MA'        'SRh'
	#    'EA'    'EAgre'    'GAf3'      'GAf4'  'GRE'      'GREnew'    'MA'        'SRh'
	labels = [names[4],names[1],names[7],names[3],names[2],names[5],names[6]]
	#labels = [names[3],names[1],names[6],names[2],names[4],names[5]]
	print(labels)

	# azul> #36648B rojo> #8B1A1A verde> #698B22
	#alg_labels  = ["$GR$","$EA$","$SR_{h}$","$GA$","$GR_{new}$","$MA$"]
	#colors      = ['k'   ,'k'   ,'k'       ,'k'   ,'#8B1A1A'   ,'#698B22']
	alg_labels  = ["$GR$","$EA$","$SR_{h}$","$GA$","$GA^{*}$","$GR_{new}$","$MA$"]
	colors      = ['k'   ,'k'   ,'k'       ,'k'   ,'k'       ,'#8B1A1A'   ,'#698B22']

	y_ticks_lbl = ['0.0','','0.2','','0.4','','0.6','','0.8','','1.0']
	y_ticks = [0.0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0]

	nF,Fx,ACC,Tm,F3,nF3 = [],[],[],[],[],[]
	for name in labels:
		nF.append(f'{name}_Fx')
		Fx.append(f'{name}_f0')
		ACC.append(f'{name}_acc')
		Tm.append(f'{name}_time')
		F3.append(f'{name}_f3')
		nF3.append(f'{name}_F3')
		df[f'{name}_Fx'] = df[f'{name}_f0'] / df['Max_fx']
		df[f'{name}_F3'] = df[f'{name}_f3'] / df['Max_f3']

	signFx = significancia_estadistica(Fx, df)
	print("avgf0")
	print(signFx)
	signNF = significancia_estadistica(nF, df)
	print("norm")
	print(signNF)
	signF3 = significancia_estadistica(F3, df)
	print("avgf3")
	print(signF3)
	signNF3 = significancia_estadistica(nF3, df)
	print("normF3")
	print(signNF3)
	signAcc = significancia_estadistica(ACC, df)
	print("acc")
	print(signAcc)
	signTm = significancia_estadistica(Tm, df, isMinimization=True)
	print("time")
	print(signTm)
	# n = len(names)
	n = len(labels)

	meanpointprops = dict(marker='D', markerfacecolor='white', markersize=6, linestyle='none')
	flierprops = dict(marker='x', markerfacecolor='white', markersize=6, linestyle='none')
	out = args.outputName.split("_")

	if show_f3_avg:
		ax, bp = df.boxplot(column=F3,showmeans=True,return_type='both',notch=False,vert=True,meanprops=meanpointprops,meanline=False,flierprops=flierprops)
		set_colors(plt, bp, colors)
		ax.set_xlabel('Algorithms')
		ax.tick_params(labelsize=lfsize)
		ax.set_xticks(range(1,n+1), labels=alg_labels)
		ax.set_ylabel("$f_{global}$", fontsize=fsize)
		ax.set_yticks(y_ticks)
		ax.set_yticklabels(y_ticks_lbl)
		ax.set_ylim(-0.05, 1.1)
		ax.set_axisbelow(True)
		ax.yaxis.grid(True, linestyle='-', which='major', color='lightgrey', alpha=0.5)
		ax.xaxis.grid(False)
		if out[-1] == "manageable":
			add_signf(ax, signF3, plt, f'c1_{out[0]}_avg_f3')
		else:
			add_signf(ax, signF3, plt, f'c2_{out[0]}_avg_f3')
		if plot_figures:
			plt.show()

	if show_f1_avg:
		ax, bp = df.boxplot(column=Fx,showmeans=True,return_type='both',notch=False,vert=True,meanprops=meanpointprops,meanline=False,flierprops=flierprops)
		set_colors(plt, bp, colors)
		ax.set_xlabel('Algorithms')
		ax.tick_params(labelsize=lfsize)
		ax.set_xticks(range(1,n+1), labels=alg_labels)
		ax.set_ylabel("$DoC_{global} \cdot UoL_{global}$", fontsize=fsize)
		ax.set_yticks(y_ticks)
		ax.set_yticklabels(y_ticks_lbl)
		ax.set_ylim(-0.05, 1.1)
		ax.set_axisbelow(True)
		ax.yaxis.grid(True, linestyle='-', which='major', color='lightgrey', alpha=0.5)
		ax.xaxis.grid(False)
		if out[-1] == "manageable":
			add_signf(ax, signFx, plt, f'c1_{out[0]}_avg')
		else:
			add_signf(ax, signFx, plt, f'c2_{out[0]}_avg')
		if plot_figures:
			plt.show()

	if show_f3_norm:
		ax, bp = df.boxplot(column=nF3,showmeans=True,return_type='both',notch=False,vert=True,meanprops=meanpointprops,meanline=False,flierprops=flierprops)
		set_colors(plt, bp, colors)
		# ax.set_xlabel('Algoritmos evaluados')
		ax.tick_params(labelsize=lfsize)
		ax.set_xticks(range(1,n+1), labels=alg_labels)
		ax.set_ylabel("$f_{global}$", fontsize=fsize)
		ax.set_yticks(y_ticks)
		ax.set_yticklabels(y_ticks_lbl)
		ax.set_ylim(-0.05, 1.1)
		ax.set_axisbelow(True)
		ax.yaxis.grid(True, linestyle='-', which='major', color='lightgrey', alpha=0.5)
		ax.xaxis.grid(False)
		if out[-1] == "manageable":
			add_signf(ax, signNF3, plt, f'c1_{out[0]}_norm_f3')
		else:
			add_signf(ax, signNF3, plt, f'c2_{out[0]}_norm_f3')
		if plot_figures:
			plt.show()

	if show_f1_norm:
		ax, bp = df.boxplot(column=nF,showmeans=True,return_type='both',notch=False,vert=True,meanprops=meanpointprops,meanline=False,flierprops=flierprops)
		set_colors(plt, bp, colors)
		ax.tick_params(labelsize=lfsize)
		ax.set_xticks(range(1,n+1), labels=alg_labels)
		ax.set_ylabel("$DoC_{global} \cdot UoL_{global}$", fontsize=fsize)
		ax.set_yticks(y_ticks)
		ax.set_yticklabels(y_ticks_lbl)
		ax.set_ylim(-0.05, 1.1)
		ax.set_axisbelow(True)
		ax.yaxis.grid(True, linestyle='-', which='major', color='lightgrey', alpha=0.5)
		ax.xaxis.grid(False)
		if out[-1] == "manageable":
			add_signf(ax, signNF, plt, f'c1_{out[0]}_norm')
		else:
			add_signf(ax, signNF, plt, f'c2_{out[0]}_norm')
		if plot_figures:
			plt.show()

	if show_acc:
		ax, bp = df.boxplot(column=ACC,showmeans=True,return_type='both',notch=False,vert=True,meanprops=meanpointprops,meanline=False,flierprops=flierprops)
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
		if out[-1] == "manageable":
			add_signf(ax, signAcc, plt, f'c1_{out[0]}_acc')
		else:
			add_signf(ax, signAcc, plt, f'c2_{out[0]}_acc')
		if plot_figures:
			plt.show()

	if show_time:
		ax, bp = df.boxplot(column=Tm,showmeans=True,return_type='both',notch=False,vert=True,meanprops=meanpointprops,meanline=False,flierprops=flierprops)
		set_colors(plt, bp, colors)
		ax.set_yscale("log")
		ax.tick_params(labelsize=lfsize)
		ax.set_xticks(range(1,n+1), labels=alg_labels)
		ax.set_ylabel('$T_{avg}\ (ms)$', fontsize=fsize)
		ax.set_axisbelow(True)
		ax.yaxis.grid(True, linestyle='-', which='major', color='lightgrey', alpha=0.5)
		ax.xaxis.grid(False)
		if out[-1] == "manageable":
			add_signf(ax, signTm, plt, f'c1_{out[0]}_time')
		else:
			add_signf(ax, signTm, plt, f'c2_{out[0]}_time')
		if plot_figures:
			plt.show()

if __name__ == "__main__":
	main(parser.parse_args())
