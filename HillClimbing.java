import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Random;
/*This file uses Hill climbing with randomized walk to find a solution
 * for the given formula. 
 */

public class HillClimbing
{
	int c_sat=0;
	static List<Integer> satClausesNum= new ArrayList<Integer>();
	private void solver(String inputFile) 
	{
        // Read DIMACS into a 2d-array of clauses:
        List<Clause> clauses = null;
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
        //since i could not read these, i hardcoded the number of variables
        List<Integer> model= new ArrayList<Integer>();
        for (int i=1; i<=40;i++)
        {
        	model.add(i);
        }
        if (process(clauses, symbols, model))
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
	/*This is the main function, it inputs clauses, symbols, randomized
	 * model. It outputs a boolean of satisfying the formula or not.
	 */
	private boolean process(List<Clause> clauses, 
			List<Integer> symbols, List<Integer> model) 
	{
		int maxIterations = 10000;
		int iterations=0;
		while (isSolved(clauses, model) !=true && iterations<
				maxIterations)
		{
			for (Clause clause:clauses) {
				if(clause.clauseSatisfied==false)
				{//find a random literal in the unsatisifed clauses
				 // and match it in the model
					Random rand = new Random();
				    int index = rand.nextInt(clause.symbols.size()-1);
					int literal = clause.symbols.get(index);
					model.set(Math.abs(literal)-1, literal);
				}
			}
			iterations+=1;
		}
		if(isSolved(clauses, model))
		{
			return true;
		}
		return false;
	}
	//return whether we solved the formula using the current model
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
	//returns a number of satisfied clauses in the formula
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
	//only calculates the average time taken 
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
		for (int i=1; i<=10; i++) {
		long startTime= System.nanoTime();
		
		new HillClimbing().solver("C:\\Users\\AliAli\\"
				+ "eclipse-workspace\\SATsolvers\\src/f0040-08-u.cnf");
		
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
