public class Classifier {

    //if true(poisonous) = p, if false(edible) = e
    public boolean specified;
    public boolean correct;

    Classifier(boolean s, boolean c) {
        specified = s;
        correct = c;
    }
}
