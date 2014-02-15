package edu.rutgers.MOST.optimization.solvers;

import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javassist.Modifier;
import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import edu.rutgers.MOST.config.LocalConfig;
import edu.rutgers.MOST.data.Solution;
import edu.rutgers.MOST.optimization.GDBB.GDBB;
import edu.rutgers.MOST.presentation.GraphicalInterface;
import edu.rutgers.MOST.presentation.GraphicalInterfaceConstants;
import edu.rutgers.MOST.presentation.ResizableDialog;
import edu.rutgers.MOST.presentation.Utilities;

public class GurobiSolver extends Solver {
	
	//static Logger log = Logger.getLogger(GurobiSolver.class);
	
	private static URLClassLoader classLoader = null;
	private static String gurobiPath = "";
	private Class<?> grbClass;		
	private Class<?> envClass;
	private Class<?> modelClass;
	private Object env = null;
	private Object model = null;
	private StringBuffer outputText = new StringBuffer();
	private StringBuffer errorText = new StringBuffer();
	ResizableDialog dialog = new ResizableDialog("Error", "Gurobi Solver Error", "Gurobi Solver Error");

	private ArrayList<Object> vars = new ArrayList<Object>();

	private static boolean isAbort = false;
	
	public static void main(String[] args) {
		//GurobiSolver gurobiSolver = new GurobiSolver("test.log");
	}
	public void setAbort(boolean isAbort) {
		GurobiSolver.isAbort = isAbort;
	}

	public GurobiSolver() {
	//public GurobiSolver(String logName) {
		final ArrayList<Image> icons = new ArrayList<Image>(); 
		icons.add(new ImageIcon("etc/most16.jpg").getImage()); 
		icons.add(new ImageIcon("etc/most32.jpg").getImage());
		    	
		dialog.setIconImages(icons);
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(false);
		dialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE); 
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				dialog.setVisible(false);        	
			}
		});	
		dialog.OKButton.addActionListener(OKButtonActionListener);
    	
		outputText.append("Gurobi Solver constructor \n");
		try {
			//File gurobiJARFile = new File("C:\\gurobi500\\win64\\lib\\gurobi.jar");
			if (classLoader == null || !gurobiPath.equals(GraphicalInterface.getGurobiPath())) {
				gurobiPath = GraphicalInterface.getGurobiPath();
				File gurobiJARFile = new File(gurobiPath);
				classLoader = URLClassLoader.newInstance(new URL[]{ gurobiJARFile.toURI().toURL() });
			}
			// set path to JNI library for Linux programmatically, in same folder as gurobi.jar
			// see http://stackoverflow.com/questions/15961483/setting-djava-library-path-programmatically-or-alternatives
			if (System.getProperty("os.name").equals("Linux")) {
				String jniPath = GraphicalInterface.getGurobiPath().substring(0, GraphicalInterface.getGurobiPath().lastIndexOf("/"));
				System.setProperty("java.library.path", jniPath);
			}			
			grbClass = classLoader.loadClass("gurobi.GRB");
			outputText.append("gurobi.GRB loaded \n");
			
			//log.debug("creating Gurobi environment");
			
			envClass = classLoader.loadClass("gurobi.GRBEnv");
			outputText.append("gurobi.GRBEnv loaded \n");
//			Constructor<?> envConstr = envClass.getConstructor(new Class[]{ String.class });
//			env = envConstr.newInstance(new Object[]{ logName });
			Constructor<?> envConstr = envClass.getConstructor(new Class[]{});
			env = envConstr.newInstance(new Object[]{});
			
			//log.debug("setting Gurobi parameters");
			
			Class<?> grbDoubleParam = classLoader.loadClass("gurobi.GRB$DoubleParam");
			outputText.append("gurobi.GRB$DoubleParam loaded \n");
			Class<?> grbIntParam = classLoader.loadClass("gurobi.GRB$IntParam");
			outputText.append("gurobi.GRB$IntParam loaded \n");
			Enum[] grbDoubleParamConstants = (Enum[]) grbDoubleParam.getEnumConstants();
			Enum[] grbIntParamConstants = (Enum[]) grbIntParam.getEnumConstants();
			Method envSetDoubleMethod = envClass.getMethod("set", new Class[]{ grbDoubleParam, double.class });
			Method envSetIntMethod = envClass.getMethod("set", new Class[]{ grbIntParam, int.class });
			
			for (int i = 0; i < grbDoubleParamConstants.length; i++) {
				switch (grbDoubleParamConstants[i].name()) {
				case "FeasibilityTol":
					envSetDoubleMethod.invoke(env, new Object[]{ grbDoubleParamConstants[i], 1.0E-9 });
					break;
				case "IntFeasTol":
					envSetDoubleMethod.invoke(env, new Object[]{ grbDoubleParamConstants[i], 1.0E-9 });
					break;
				}
			}

			for (int i = 0; i < grbIntParamConstants.length; i++) {
				switch (grbIntParamConstants[i].name()) {
				case "OutputFlag":
					envSetIntMethod.invoke(env, new Object[]{ grbIntParamConstants[i], 0 });
					break;
				}
			}
			
			//log.debug("creating Gurobi Model");
			
			modelClass = classLoader.loadClass("gurobi.GRBModel");
			outputText.append("gurobi.GRBModel loaded \n");
			Constructor<?> modelConstr = modelClass.getConstructor(new Class[]{ envClass });
		    model = modelConstr.newInstance(new Object[]{ env });
			
			this.objType = ObjType.Minimize;				
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | ClassNotFoundException | MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			outputText.append(errors.toString());
			errorText.append(errors.toString());
			dialog.setErrorMessage(errorText.toString());
			dialog.setVisible(true);
		} catch (InvocationTargetException e) {
			try {
				Class<?> grbExceptionClass = classLoader.loadClass("gurobi.GRBException");
				if (grbExceptionClass.isInstance(e.getCause())) {
					Method grbExceptionGetErrorCodeMethod = grbExceptionClass.getMethod("getErrorCode", null);
					int errorCode = (int) grbExceptionGetErrorCodeMethod.invoke(e.getCause(), null);

					if (errorCode == grbClass.getDeclaredField("ERROR_NO_LICENSE").getInt(null)) {
						//					GraphicalInterface.getTextInput().setVisible(false);
						LocalConfig.getInstance().hasValidGurobiKey = false;
						GraphicalInterface.getTextInput().setVisible(false);
						Object[] options = {"    OK    "};
						int choice = JOptionPane.showOptionDialog(null, 
								"Error: No validation file - run 'grbgetkey' to refresh it.", 
								GraphicalInterfaceConstants.GUROBI_KEY_ERROR_TITLE, 
								JOptionPane.YES_NO_OPTION, 
								JOptionPane.QUESTION_MESSAGE, 
								null, options, options[0]);
						LocalConfig.getInstance().getOptimizationFilesList().clear();
						if (GraphicalInterface.getTextInput().getTimer().isRunning()) {
							GraphicalInterface.getTextInput().getTimer().stop();
						}
					}
					else {
						handleGurobiException();
					}
				}
				else
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				outputText.append(errors.toString());
				errorText.append(errors.toString());
				if (LocalConfig.getInstance().hasValidGurobiKey) {
					dialog.setErrorMessage(errorText.toString());
					dialog.setVisible(true);
				}				
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | HeadlessException | NoSuchFieldException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				StringWriter errors = new StringWriter();
				e1.printStackTrace(new PrintWriter(errors));
				outputText.append(errors.toString());
				errorText.append(errors.toString());
				if (LocalConfig.getInstance().hasValidGurobiKey) {
					dialog.setErrorMessage(errorText.toString());
					dialog.setVisible(true);
				}
			}
		} 
	}
	
	public void abort() {
		isAbort = true;
	}
	
	@Override
	public void addConstraint(Map<Integer, Double> map, ConType con,
			double value) {
		if (modelClass == null)
			return;
		
		try {
			Class<?> grbLinExprClass = classLoader.loadClass("gurobi.GRBLinExpr");
			Class<?> grbVarClass = classLoader.loadClass("gurobi.GRBVar");
			Object expr = grbLinExprClass.newInstance();
			Method exprAddTermMethod = grbLinExprClass.getMethod("addTerm", new Class[]{ double.class, grbVarClass });
			
			Set<Entry<Integer, Double>> s = map.entrySet();
			Iterator<Entry<Integer, Double>> it = s.iterator();
			while (it.hasNext()) {
				Map.Entry<Integer, Double> m = it.next();
				int key = m.getKey();
				Double v = m.getValue();
				exprAddTermMethod.invoke(expr, new Object[]{ v, this.vars.get(key) });
			}
			
			Method modelAddConstrMethod = modelClass.getMethod("addConstr", new Class[]{ grbLinExprClass, char.class, double.class, String.class});
			modelAddConstrMethod.invoke(model, new Object[]{expr, getGRBConType(con), value, null});			
		} catch (IllegalAccessException | ClassNotFoundException | InstantiationException | NoSuchMethodException | SecurityException | IllegalArgumentException | NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			outputText.append(errors.toString());
			errorText.append(errors.toString());
			if (LocalConfig.getInstance().hasValidGurobiKey) {
				dialog.setErrorMessage(errorText.toString());
				dialog.setVisible(true);
			}
		} catch (InvocationTargetException e) {
			handleGurobiException();
		}
	}

	@Override
	public void enable() {
		isAbort = false;
	}

	public void finalize() {
		// Not guaranteed to be invoked
		try {
			if (model != null) {
				Method modelDisposeMethod = modelClass.getMethod("dispose", null);
				modelDisposeMethod.invoke(model, null);
			}

			if (env != null) {
				Method envDisposeMethod = envClass.getMethod("dispose", null);
				envDisposeMethod.invoke(model, null);
			}
		} catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			outputText.append(errors.toString());
			errorText.append(errors.toString());
			if (LocalConfig.getInstance().hasValidGurobiKey) {
				dialog.setErrorMessage(errorText.toString());
				dialog.setVisible(true);
			}
		} catch (InvocationTargetException e) {
			handleGurobiException();
		}

	}

	private char getGRBConType(ConType type) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		switch (type) {
		case LESS_EQUAL:
			return grbClass.getDeclaredField("LESS_EQUAL").getChar(null);
		case EQUAL:
			return grbClass.getDeclaredField("EQUAL").getChar(null);
		case GREATER_EQUAL:
			return grbClass.getDeclaredField("GREATER_EQUAL").getChar(null);
		default:
			return grbClass.getDeclaredField("LESS_EQUAL").getChar(null);
		}
	}

	private int getGRBObjType(ObjType type) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		switch (type) {
		case Minimize:
			return grbClass.getDeclaredField("MINIMIZE").getInt(null);
		case Maximize:
			return grbClass.getDeclaredField("MAXIMIZE").getInt(null);
		default:
			return grbClass.getDeclaredField("MINIMIZE").getInt(null);
		}
	}

	private char getGRBVarType(VarType type) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		switch (type) {
		case CONTINUOUS:
			return grbClass.getDeclaredField("CONTINUOUS").getChar(null);
		case BINARY:
			return grbClass.getDeclaredField("BINARY").getChar(null);
		case INTEGER:
			return grbClass.getDeclaredField("INTEGER").getChar(null);
		case SEMICONT:
			return grbClass.getDeclaredField("SEMICONT").getChar(null);
		case SEMIINT:
			return grbClass.getDeclaredField("SEMIINT").getChar(null);
		default:
			return grbClass.getDeclaredField("CONTINUOUS").getChar(null);
		}
	}

	@Override
	public String getName() {
		return "GurobiSolver";
	}

	public ArrayList<Double> getSoln() {
		ArrayList<Double> soln = new ArrayList<Double>(vars.size());
		
		try {
			Class<?> grbDoubleAttr = classLoader.loadClass("gurobi.GRB$DoubleAttr");

			Enum[] grbDoubleAttrConstants = (Enum[]) grbDoubleAttr.getEnumConstants();
			Enum xAttr = null;
			for (int i = 0; i < grbDoubleAttrConstants.length; i++)
				if (grbDoubleAttrConstants[i].name() == "X") {
					xAttr = grbDoubleAttrConstants[i];
					break;
				}

			Class<?> varClass = classLoader.loadClass("gurobi.GRBVar");
			Method varGetMethod = varClass.getMethod("get", new Class[]{ grbDoubleAttr });
			
			if (this.vars.size() != 0) {
				for (int i = 0; i < this.vars.size(); i++) {
					soln.add((double) varGetMethod.invoke(this.vars.get(i), xAttr));
				}
			}
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			outputText.append(errors.toString());
			errorText.append(errors.toString());
			if (LocalConfig.getInstance().hasValidGurobiKey) {
				dialog.setErrorMessage(errorText.toString());
				dialog.setVisible(true);
			}
		} catch (InvocationTargetException e) {
			handleGurobiException();
		}
		
		return soln;
	}
	
	private void handleGurobiException() {
		Method envGetErrorMsgMethod;
		try {
			envGetErrorMsgMethod = envClass.getMethod("getErrorMsg", null);
			//log.error("Gurobi error: " + (String) envGetErrorMsgMethod.invoke(env, null));
		} catch (NoSuchMethodException | SecurityException | IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			outputText.append(errors.toString());
			errorText.append(errors.toString());
			if (LocalConfig.getInstance().hasValidGurobiKey) {
				dialog.setErrorMessage(errorText.toString());
				dialog.setVisible(true);
			}
		}
	}

	@Override
	public double optimize() {
		outputText.append("optimize \n");
		if (model == null) {
			outputText.append("model is null \n");
			writeLogFile(outputText);
			return Double.NaN;
		}
					
		try {			
			outputText.append(LocalConfig.getInstance().getModelName() + "\n");
//			Callback logic
			Method modelGetVarsMethod = modelClass.getMethod("getVars", null);
			final Object[] vars = (Object[]) modelGetVarsMethod.invoke(model, null);
	
			final Class<?> grbCallbackClass = classLoader.loadClass("gurobi.GRBCallback");
			outputText.append("gurobi.GRBCallback loaded \n");
			final Class<?> grbVarClass = classLoader.loadClass("gurobi.GRBVar");
			outputText.append("gurobi.GRBVar loaded \n");
			
			ProxyFactory factory = new ProxyFactory();
			factory.setSuperclass(grbCallbackClass);
			factory.setFilter(
					new MethodFilter() {
						@Override
						public boolean isHandled(Method method) {
							return Modifier.isAbstract(method.getModifiers());
						}
					}
					);

			MethodHandler handler = new MethodHandler() {
				@Override
				public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
					if (thisMethod.getName() == "callback") {
						if (isAbort) {
							try {
								Method grbCallbackAbortMethod = grbCallbackClass.getDeclaredMethod("abort", null);
								grbCallbackAbortMethod.setAccessible(true);
								grbCallbackAbortMethod.invoke(self, null);
							} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								StringWriter errors = new StringWriter();
								e.printStackTrace(new PrintWriter(errors));
								outputText.append(errors.toString());
								errorText.append(errors.toString());
								if (LocalConfig.getInstance().hasValidGurobiKey) {
									dialog.setErrorMessage(errorText.toString());
									dialog.setVisible(true);
								}
							} catch (InvocationTargetException e) {
								handleGurobiException();
							}
						}
						
						try {
							ClassLoader classLoader = grbCallbackClass.getClassLoader();

							Class grbClass = classLoader.loadClass("gurobi.GRB");
							outputText.append("gurobi.GRB loaded \n");
							Field[] asdf = grbClass.getDeclaredFields();
							
							Field whereField = grbCallbackClass.getDeclaredField("where");
							whereField.setAccessible(true);
							
							int where = whereField.getInt(self);
							if (where == grbClass.getDeclaredField("CB_MIPSOL").getInt(null)) {
								Method grbCallbackGetDoubleInfoMethod = grbCallbackClass.getDeclaredMethod("getDoubleInfo", new Class[]{ int.class });
								Method grbCallbackGetSolutionMethod = grbCallbackClass.getDeclaredMethod("getSolution", new Class[]{ Array.newInstance(grbVarClass, 0).getClass() });
								grbCallbackGetDoubleInfoMethod.setAccessible(true);
								grbCallbackGetSolutionMethod.setAccessible(true);

								Solution solution = new Solution((double) grbCallbackGetDoubleInfoMethod.invoke(self, new Object[]{ grbClass.getDeclaredField("CB_MIPSOL_OBJ").getInt(null) }), 
										(double[]) grbCallbackGetSolutionMethod.invoke(self, new Object[]{ vars }));
								GDBB.intermediateSolution.add(solution);
							}
						} catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | NoSuchMethodException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							StringWriter errors = new StringWriter();
							e.printStackTrace(new PrintWriter(errors));
							outputText.append(errors.toString());
							errorText.append(errors.toString());
							if (LocalConfig.getInstance().hasValidGurobiKey) {
								dialog.setErrorMessage(errorText.toString());
								dialog.setVisible(true);
							}
						} catch (InvocationTargetException e) {
							handleGurobiException();
						}
					}
					
					return null;
				}
			};
			
			Method modelSetCallbackMethod = modelClass.getMethod("setCallback", new Class[]{ grbCallbackClass });
			
			Object callback = factory.create(new Class[0], new Object[0], handler);
			modelSetCallbackMethod.invoke(model, new Object[]{ callback });
			
			Method modelOptimizeMethod = modelClass.getMethod("optimize", null);
			modelOptimizeMethod.invoke(model, null);
			
//			Method modelWriteMethod = modelClass.getMethod("write", new Class[]{ String.class });
//			modelWriteMethod.invoke(model, new Object[]{ "model.lp" });
//			modelWriteMethod.invoke(model, new Object[]{ "model.mps" });
			
			Class<?> grbDoubleAttr = classLoader.loadClass("gurobi.GRB$DoubleAttr");
			outputText.append("gurobi.GRB$DoubleAttr loaded \n");
				
			Enum[] grbDoubleAttrConstants = (Enum[]) grbDoubleAttr.getEnumConstants();
			outputText.append(grbDoubleAttrConstants.toString() + "\n");
			Enum objValAttr = null;
			for (int i = 0; i < grbDoubleAttrConstants.length; i++)
				if (grbDoubleAttrConstants[i].name() == "ObjVal") {
					objValAttr = grbDoubleAttrConstants[i];
					break;
				}
			
			Method modelGetMethod = modelClass.getMethod("get", new Class[]{ grbDoubleAttr });
			outputText.append(modelGetMethod.invoke(model, new Object[]{ objValAttr }));
			writeLogFile(outputText);
			return (double) modelGetMethod.invoke(model, new Object[]{ objValAttr });
		} catch (IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | ClassNotFoundException | InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			outputText.append(errors.toString());
			errorText.append(errors.toString());
			if (LocalConfig.getInstance().hasValidGurobiKey) {
				dialog.setErrorMessage(errorText.toString());
				dialog.setVisible(true);
			}
		} catch (InvocationTargetException e) {
			handleGurobiException();
		}
		outputText.append("return NaN \n");
		writeLogFile(outputText);
		
		return Double.NaN;
	}

	public void setEnv(double timeLimit, int numThreads) {
		if (env == null)
			return;
		
		try {
			//log.debug("setting Gurobi parameters");
			
			Class<?> grbDoubleParam = classLoader.loadClass("gurobi.GRB$DoubleParam");
			Class<?> grbIntParam = classLoader.loadClass("gurobi.GRB$IntParam");	
			Enum[] grbDoubleParamConstants = (Enum[]) grbDoubleParam.getEnumConstants();
			Enum[] grbIntParamConstants = (Enum[]) grbIntParam.getEnumConstants();
			Method envSetDoubleMethod = envClass.getMethod("set", new Class[]{ grbDoubleParam, double.class });
			Method envSetIntMethod = envClass.getMethod("set", new Class[]{ grbIntParam, int.class });
			
			for (int i = 0; i < grbDoubleParamConstants.length; i++) {
				switch (grbDoubleParamConstants[i].name()) {
				case "Heuristics":
					envSetDoubleMethod.invoke(env, new Object[]{ grbDoubleParamConstants[i], 1.0 });
					break;
				case "ImproveStartGap":
					envSetDoubleMethod.invoke(env, new Object[]{ grbDoubleParamConstants[i], Double.POSITIVE_INFINITY });
					break;
				case "TimeLimit":
					envSetDoubleMethod.invoke(env, new Object[]{ grbDoubleParamConstants[i], timeLimit});
					break;
				}
			}

			for (int i = 0; i < grbIntParamConstants.length; i++) {
				switch (grbIntParamConstants[i].name()) {
				case "MIPFocus":
					envSetIntMethod.invoke(env, new Object[]{ grbIntParamConstants[i], 1 });
					break;
				case "Threads":
					envSetIntMethod.invoke(env, new Object[]{ grbIntParamConstants[i], numThreads });
					break;
				}
			}
		} catch (IllegalAccessException | ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			outputText.append(errors.toString());
			errorText.append(errors.toString());
			if (LocalConfig.getInstance().hasValidGurobiKey) {
				dialog.setErrorMessage(errorText.toString());
				dialog.setVisible(true);
			}
		} catch (InvocationTargetException e) {
			handleGurobiException();
		}
	}
	
	@Override
	public void setObj(Map<Integer, Double> map) {
		if (model == null)
			return;
		
		try {
			Class<?> grbLinExprClass = classLoader.loadClass("gurobi.GRBLinExpr");
			Class<?> grbVarClass = classLoader.loadClass("gurobi.GRBVar");
			Object expr = grbLinExprClass.newInstance();
			Method exprAddTermMethod = grbLinExprClass.getMethod("addTerm", new Class[]{ double.class, grbVarClass });

			Set<Entry<Integer, Double>> s = map.entrySet();
			Iterator<Entry<Integer, Double>> it = s.iterator();
			while (it.hasNext()) {
				Map.Entry<Integer, Double> m = it.next();
				int key = m.getKey();
				Double value = m.getValue();
				Object var = this.vars.get(key);
				exprAddTermMethod.invoke(expr, new Object[]{ value, var });

				//System.out.println("key = " + key + " value = " + value);
				//System.out.println("objType: " + this.objType);
			}

			Class<?> grbExprClass = classLoader.loadClass("gurobi.GRBExpr");
			Method modelSetObjectiveMethod = modelClass.getMethod("setObjective", new Class[]{ grbExprClass, int.class });
			modelSetObjectiveMethod.invoke(model, new Object[]{ expr, getGRBObjType(this.objType) });
		} catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException
				| SecurityException | NoSuchMethodException | InstantiationException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			outputText.append(errors.toString());
			errorText.append(errors.toString());
			if (LocalConfig.getInstance().hasValidGurobiKey) {
				dialog.setErrorMessage(errorText.toString());
				dialog.setVisible(true);
			}
		} catch (InvocationTargetException e) {
			handleGurobiException();
		}

	}

	@Override
	public void setObjType(ObjType objType) {
		this.objType = objType;
	}
	
	@Override
	public void setVar(String varName, VarType types, double lb, double ub) {
		if (model == null)
			return;
		
		try {
			if (varName != null && types != null) {
				Method modelAddVarMethod = modelClass.getMethod("addVar", 
						new Class[]{ double.class, double.class, double.class, char.class, String.class });

				Object var = modelAddVarMethod.invoke(this.model, new Object[]{lb, ub, 0.0, getGRBVarType(types), varName});
				this.vars.add(var);

				Method modelUpdateMethod = modelClass.getMethod("update", null);
				modelUpdateMethod.invoke(this.model, null);
			}
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			outputText.append(errors.toString());
			errorText.append(errors.toString());
			if (LocalConfig.getInstance().hasValidGurobiKey) {
				dialog.setErrorMessage(errorText.toString());
				dialog.setVisible(true);
			}
		} catch (InvocationTargetException e) {
			handleGurobiException();
		}
	}
	
	@Override
	public void setVars(VarType[] types, double[] lb, double[] ub) {
		try {
			Method modelAddVarMethod = modelClass.getMethod("addVar", 
					new Class[]{ double.class, double.class, double.class, char.class, String.class });

			if (types.length == lb.length && lb.length == ub.length) {
				for (int i = 0; i < lb.length; i++) {
					Object var = modelAddVarMethod.invoke(this.model, new Object[]{lb[i], ub[i], 0.0, getGRBVarType(types[i]), Integer.toString(i)});
					this.vars.add(var);
				}

				Method modelUpdateMethod = modelClass.getMethod("update", null);
				modelUpdateMethod.invoke(this.model, null);
			}
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			outputText.append(errors.toString());
			errorText.append(errors.toString());
			if (LocalConfig.getInstance().hasValidGurobiKey) {
				dialog.setErrorMessage(errorText.toString());
				dialog.setVisible(true);
			}
		} catch (InvocationTargetException e) {
			handleGurobiException();
		}
	}
	
	public void writeLogFile(StringBuffer outputText) {
		Writer writer = null;
		try {
			Utilities u = new Utilities();
			File file = new File(u.createLogFileName("gurobiSolver.log"));
			//File file = new File("gurobiSolver.log");
			writer = new BufferedWriter(new FileWriter(file));
			writer.write(outputText.toString());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	ActionListener OKButtonActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {	
			dialog.setVisible(false);
		}
	}; 

}
