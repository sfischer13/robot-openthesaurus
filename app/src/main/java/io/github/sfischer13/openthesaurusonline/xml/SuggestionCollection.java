package io.github.sfischer13.openthesaurusonline.xml;


import java.util.ArrayList;
import java.util.List;

import io.github.sfischer13.openthesaurusonline.model.Term;

public class SuggestionCollection {
    private final String path;
    private final List<Term> terms;

    public SuggestionCollection(String path) {
        this.path = path;
        terms = new ArrayList<>();
    }

    public void add(Term term) {
        terms.add(term);
    }

    public String getPath() {
        return path;
    }

    public List<Term> getTerms() {
        return terms;
    }
}
