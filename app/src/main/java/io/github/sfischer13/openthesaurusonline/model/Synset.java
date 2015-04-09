package io.github.sfischer13.openthesaurusonline.model;


import java.util.ArrayList;
import java.util.List;

public class Synset {
    private final List<Term> terms;

    public Synset() {
        this.terms = new ArrayList<>();
    }

    public void add(Term term) {
        terms.add(term);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (Term aTerm : terms) {
            sb.append(sep);
            sb.append(aTerm.toString());
            sep = "\n";
        }
        return sb.toString();
    }
}