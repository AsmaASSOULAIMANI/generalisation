import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class General {
	public static void main(String[] args) throws IloException {
		modele(5);

	}
	
	
	public  static void modele(double p  ) throws IloException {
		/*int  K =0;
		int M=0;
		int N=0;*/
		double [][] d = new double[10][2];
		double [][] delta = new double[10][2];
		double []u = new double[10];
		double [][][] c = new double[10][10][2];
		// OBJET CPLEX
		IloCplex cplex = new IloCplex();
		// LES VARIABLES DE DECISION
		IloNumVar[][][] x = new IloNumVar[10][10][2];
		for ( int i=0 ; i<10; i++) {
			for ( int j=0 ; j<10; j++) {
				for (int k = 0; k <2; k++) {
					x[i][j][k]= cplex.boolVar();
					
				}
			}
		}
		IloNumVar[][] z = new IloNumVar[10][2];
		for ( int j=0 ; j<10; j++) {
			for ( int k=0 ; k<2; k++) {
				z[j][k]= cplex.boolVar();
			}
		}
		IloNumVar[] y = new IloNumVar[10];
		for (int j = 0; j <10; j++) {
				y[j]= cplex.boolVar();
		}
		// La fonction objective 
		IloLinearNumExpr term1 = cplex.linearNumExpr();
		IloLinearNumExpr term2 = cplex.linearNumExpr();

		for (int i=0; i<10; i++) {
    		for (int j=0; j<10; j++) {
    			for (int k = 0; k < 2; k++) {
    				term1.addTerm(d[i][k]*c[i][j][k],x[i][j][k]);
				}
    		}
		}
		for (int j=0; j<10; j++) {
			for (int k = 0; k < 2; k++) {
				term2.addTerm(delta[j][k],z[j][k]);
			}
		}
		cplex.addMinimize(cplex.sum(term1,term2));
		// Les contraintes 
		/****Contrainte 1****/
		for (int i=0; i<10; i++) {
			for (int k = 0; k < 2; k++) {
				IloLinearNumExpr expr = cplex.linearNumExpr();
				for (int j=0; j<10; j++) {
					expr.addTerm(1.0, x[i][j][k]);
				}
				cplex.addEq(expr, 1.0);
			}	
		}
		/****Contrainte 2****/
		for (int j=0; j<10; j++) {
			IloLinearNumExpr expr = cplex.linearNumExpr();
			for (int k=0; k<2; k++) {
				expr.addTerm(1.0, z[j][k]);
			}	
			cplex.addLe(expr, 1.0);
		}
		/****Contrainte 3****/
		for (int i=0; i<10; i++) {
			for (int j=0; j<10; j++) {
				for (int k = 0; k < 2; k++) {
					cplex.addLe(x[i][j][k], z[j][k]);
				}
			}
		}
		/****Contrainte 4****/
		for (int j=0; j<10; j++) {
			for (int k = 0; k < 2; k++) {
				cplex.addLe(z[j][k], y[j]);
			}
		}		
		/****Contrainte 5****/
		IloLinearNumExpr expr = cplex.linearNumExpr();
		for (int j=0; j<10; j++) {
			expr.addTerm(1.0, y[j]);
		}
		cplex.addEq(expr, p);
		/****Contrainte 6****/
		for (int j=0; j<10; j++) {
			IloLinearNumExpr exprs = cplex.linearNumExpr();
			for (int i=0 ; i<10; i++) {
				for (int k = 0; k < 2; k++) {
					exprs.addTerm( d[i][k], x[i][j][k]);
				}
			}
			cplex.addLe(exprs,cplex.prod(u[j], y[j]));
		}
		///*******Résoudre le probléme*******\\\
		if (cplex.solve()) {
			
    		System.out.println("obj = "+cplex.getObjValue());
    	}
    	else {
    		System.out.println("problem not solved");
    	}
    	cplex.end();
	}
}
