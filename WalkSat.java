import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

//probability chosen is 0.5
/*This class uses WalkSAT to find a solution for the given formula
 */
public class WalkSat{
	int c_sat=0;
	static List<Integer> satClausesNum= new ArrayList<Integer>();
	private Random random = new Random();
	private void solver(String inputFile) 
	{
        // Read DIMACS into list of clauses:
        List<Clause> clauses = null;
        double p= 0.5;
        int maxFlips=10000;
		try
		{
			clauses = Files.lines(Paths.get(inputFile))
			        .map(line -> line.trim().replaceAll("\\s+", " ").trim())
			        .filter(line -> line.endsWith(" 0"))
			        .map(Clause::new).collect(Collectors.toList());
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        ArrayList<Integer> symbols = new ArrayList<>();
        for (Clause clause: clauses)
        {
        	symbols.addAll(clause.symbols);
        }
        //setting number of variables in model manually and all positive
        List<Integer> model= new ArrayList<Integer>();
        for (int i=1; i<=40;i++)
        {
        	model.add(i);
        }
        
        if (process(clauses, symbols, model, p, maxFlips))
        {
        	satClauses(clauses, model);
        	System.out.println("Model: "+model);
        	satClausesNum.add(c_sat);
        	System.out.println("SAT");
        }
        else {
        	satClauses(clauses, model);
        	System.out.println("Model: "+model);
        	satClausesNum.add(c_sat);
        	System.out.println("UNSAT");
        }
        
    }
	/*this is the main processing function. it inputs clauses, symbols
	 * a randomized model, the probability needed for actions and 
	 * maximum number of flips which is 10,000
	 */
	
	private boolean process(List<Clause> clauses, List<Integer> symbols,
			List<Integer> model, double p, int maxFlips) 
	{
		for (int i = 0; i < maxFlips || maxFlips < 0; i++) {
			if(isSolved(clauses, model))
			{
				return true;
			}
			//find a random clause that is not satisfied 
			Clause clause= findRandomClause(clauses, model);
			//decide randomly the probability value to take action
			
			//if probability less than 0.5
			if (random.nextDouble() < p) {
				//find a random symbol in the current clause and flip
				//it in the model
				int index=findRandomSymbol(clause);
				int value=clause.symbols.get(index);
				int modValue=model.get(Math.abs(value)-1);
				model.set(Math.abs(value)-1, modValue*-1);
			}
			//if probability is higher than 0.5
			else {
				//find the symbol that will give the max number of sat
				//clauses if flipped
				model=flipWithMaxSATClauses(clause, clauses, model);
			}
		}
		if(isSolved(clauses, model))
		{
			return true;
		}
		return false;
		
	}
	
	//This function creates an array of unsatisified clauses and picks
	//one of them randomly
	public Clause findRandomClause(List<Clause>clauses, List<Integer>
	model)
	{	
		List<Clause> falseClauses= new ArrayList<Clause>();
		for (Clause clause:clauses)
		{
			clause.clauseSatisfied=false;
			for (int i:model)
			{
				if (clause.symbols.contains(i)) {
					clause.clauseSatisfied= true;
				}

			}
		}
		for (Clause clause:clauses)
		{
			if (!clause.clauseSatisfied)
			{
				falseClauses.add(clause);
			}
		}
		int index=random.nextInt(falseClauses.size());
		return falseClauses.get(index);
	}
	
	//this function returns an index of a randomly picked symbol
	//in the clause
	public int findRandomSymbol (Clause clause)
	{
		int index=random.nextInt(clause.symbols.size());
		return index;
	}
	
	//this calculates and outputs a model after finding which variable
	//to flip by finding which one returns max clauses to be true.
	public List<Integer> flipWithMaxSATClauses(Clause clause, 
			List<Clause> clauses, List<Integer> model)
	{
		List<Integer> currentModel=model;
		List<Integer> symbols= clause.symbols;
		int maxSATClauses=0;
		for (int symbol:symbols)
		{
			List<Integer> temp=model;
			temp.set(Math.abs(symbol)-1, 
					symbol*-1);
			int satClauses=0;
			for (Clause c:clauses)
			{
				if(satisfyClause(c, model))
				{
					satClauses+=1;
				}
			}
			if(satClauses>maxSATClauses)
			{
				currentModel=temp;
				maxSATClauses= satClauses;
				if(satClauses==clauses.size())
				{
					break;
				}
			}
			
		}
		return currentModel;	
	}
	
	//checks if the model satisfies a clause
	public boolean satisfyClause(Clause clause, List<Integer> model)
	{
		clause.clauseSatisfied=false;
		for (int i:model)
		{
			if (clause.symbols.contains(i)) {
				return true;
			}

		}
		return false;	
	}
	
	//checks if we reached a solution
	public boolean isSolved(List<Clause> clauses, List<Integer> model)
	{
		for (Clause clause:clauses)
		{
			clause.clauseSatisfied=false;
			for (int i:model)
			{
				if (clause.symbols.contains(i)) {
					clause.clauseSatisfied= true;
				}

			}
		}
		for (Clause clause:clauses)
		{
			if (!clause.clauseSatisfied)
			{
				return false;
			}
		}
		return true;
	}
	
	//calculates the number of satisified clauses
	public int satClauses(List<Clause> clauses, List<Integer> model)
	{
		for (Clause clause:clauses)
		{
			if (clause.clauseSatisfied)
			{
				c_sat+=1;
			}
		}
		return c_sat;
		
	}
	//averages the time taken
	public static double averageTime(List<Double> timelog) {
		int sum=0;
		for (double num:timelog)
		{
			sum+=num;
		}
		return sum/timelog.size();
		
	}
	
	public static void main(String[] args)
	{
		List<Double> timeLog= new ArrayList<Double>();
		//this runs for 10 times since it has randomization
		for (int i=1; i<=10; i++) {
		long startTime= System.nanoTime();
		//edit path to formula file
		new WalkSat().solver("C:\\Users\\AliAli\\eclipse-workspace"
				+ "\\SATsolvers\\src/f0040-08-u.cnf");
		
		long estimatedTime = System.nanoTime()-startTime;
		double ms = (double) estimatedTime/1000000;
		timeLog.add(ms);
		}
		System.out.println("Max number of SAT Clauses: "
				+Collections.max(satClausesNum));
		System.out.println("Average time: "+ 
				averageTime(timeLog));
	}
	
}