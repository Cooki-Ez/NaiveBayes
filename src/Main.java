import java.io.*;
import java.util.*;

public class Main {

    static List<String[]> fileToList(File csvFile) throws IOException {
        List<String[]> list = new ArrayList<>();
        FileReader fr = new FileReader(csvFile);
        BufferedReader br = new BufferedReader(fr);
        String line;
        String[] tempArr;
        while ((line = br.readLine()) != null) {
            tempArr = line.split(",");
            list.add(tempArr);
        }
        br.close();
        return list;
    }

    static Classifier probabilityDecision(String[] mushroom, //test sample
                                          List<String[]> mushroomList //train sample
    ) {
        // return true if decision is correct, otherwise false

        double probablyPoisonous; //variable for ratio for poisonous cases to all cases
        double probablyEdible;  //variable for ratio for edible cases to all cases


        double all = mushroomList.size(); //number of cases
        double allP = mushroomList.stream()//count how many mushrooms are poisonous
                .filter(x -> x[0].equals("p"))
                .count();
        double allE = mushroomList.stream()//count how many mushrooms are edible
                .filter(x -> x[0].equals("e"))
                .count();

        probablyPoisonous = allP/all;   //poisonous cases to all cases
        probablyEdible = allE/all;  //edible cases to all cases

        String[] decision = new String[] {"p", "e"};    //type of mushroom (either p or e)

        for (int j = 0; j < 2; j++) {   //check for poisonous(p) and edible(e) separately
            for (int i = 1; i < mushroom.length; i++) {

                int lambdaFinalI = i;
                int lambdaFinalJ = j;
                float tmp = mushroomList.stream()   //filter to get only one type of mushroom, count cases
                        .filter(x -> x[0].equals(decision[lambdaFinalJ]) && x[lambdaFinalI].equals(mushroom[lambdaFinalI]))
                        .count();

                if (decision[j].equals("p")) {  //if type is poisonous(p)
                    if (tmp == 0)
                        probablyPoisonous *= 1 / allP;  //smoothing
                    else
                        probablyPoisonous *= tmp / allP;    //number of poisonous to all cases
                }
                else {  //if type is edible(e)
                    if (tmp == 0)
                        probablyEdible *= 1 / allE; //smoothing
                    else
                        probablyEdible *= tmp / allE; //number of edible to all cases
                }
            }
        }
        System.out.println(probablyPoisonous + " " + probablyEdible);   //print probability

        boolean isPoison = probablyPoisonous > probablyEdible;  //if probability of poisonous is bigger than prob. of edibleness
                                                                // then boolean is true, false otherwise
        System.out.println(isPoison);   //print answer

        boolean isCorrect;  //is classified answer(output of program) correct

        if (isPoison && mushroom[0].equals("p"))    //if answer is poisonous and it is really poisonous
            isCorrect = true;
        else    //else if answer is edible and it is really edible
            isCorrect = !isPoison && mushroom[0].equals("e");

        System.out.println("Was classified correct: " + isCorrect);
        return new Classifier(isPoison, isCorrect);
    }



    public static void main(String[] args) throws IOException {
        File trainingSet = new File("agaricus-lepiota.data.txt");
        File testSet = new File("agaricus-lepiota.test.data.txt");
        List<String[]> trainList = fileToList(trainingSet);    //data from training set to list
        List<String[]> testList = fileToList(testSet);    //data from test set to list
        
        double truePoison = 0;  //variables for Classifier Evaluation
        double falsePoison = 0;
        double trueEdible = 0;
        double falseEdible = 0;

        for (String[] strings : testList) {
            Classifier result = probabilityDecision(strings, trainList);    //test for every mushroom from test sample
            if (result.specified) { //if classified as poisonous
                if (result.correct) //add 1 to true positive if actually correct
                    truePoison += 1;
                else
                    falsePoison += 1;   //add 1 to false positive if actually NOT correct
            } else {
                if (result.correct) //if classified as edible
                    trueEdible += 1;    //add 1 to true negative if actually correct
                else
                    falseEdible += 1;   //add 1 to false negative if actually correct
            }

        }

        double p = truePoison/(truePoison + falsePoison);   //precision
        double r = truePoison/(truePoison + falseEdible);   //recall

        System.out.println("Accuracy: " + (trueEdible + truePoison)/testList.size());
        System.out.println("Precision: " + p);
        System.out.println("Recall: " + r);
        System.out.println("F-measure: " + (2 * p * r)/(p + r));


    }

}
