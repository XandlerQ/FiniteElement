package problem;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;

public class ProblemLoader {
    public static Problem loadProblem() {
        Gson gson = new Gson();
        Problem problem = null;
        try {
            JsonReader reader = new JsonReader(new FileReader("problem.json"));
            problem = gson.fromJson(reader, Problem.class);
            if (incorrectParameters(problem)) {
                System.out.println("Incorrect parameters\n");
                return null;
            }
        }
        catch (Exception exception) {
            System.out.println(exception);
        }
        return problem;
    }

    private static boolean incorrectParameters(Problem problem) {
        return problem.body.shape.d <= 0
                || problem.body.shape.d >= 1
                || problem.body.shape.h <= 0
                || problem.body.shape.t <= 0
                || problem.body.material.lambda <= 0
                || problem.environment.alpha <= 0
                || problem.environment.T < 0;
    }
}
