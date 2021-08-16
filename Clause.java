import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//For representing a clause. Each clause has an arrayList of symbols,
//and a boolean of whether it is satisfied or not.
public class Clause {

        List<Integer> symbols = new ArrayList<>();
        boolean clauseSatisfied = false;

        public Clause(String inputLine) {
            symbols.addAll(
                    Arrays.stream(inputLine
                            .substring(0, inputLine.length() - 2)
                            .trim().split("\\s+"))
                            .map(Integer::parseInt)
                            .collect(Collectors.toList()));
        }
        //not used since i decied not to do DPLL
       /* boolean isUnitClause() {
            return !clauseSatisfied && symbols.size() == 1;
        }*/
    }