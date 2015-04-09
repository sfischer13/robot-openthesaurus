package io.github.sfischer13.openthesaurusonline.model;


public class Term {
    private String term = null;

    public Term(String term) {
        this.term = term;
    }

    @Override
    public String toString() {
        return term;
    }
}
