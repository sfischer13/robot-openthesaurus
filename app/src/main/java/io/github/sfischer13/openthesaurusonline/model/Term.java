package io.github.sfischer13.openthesaurusonline.model;


import java.io.Serializable;

public class Term implements Serializable {
    private String term = null;

    public Term(String term) {
        this.term = term;
    }

    // TODO; getter

    @Override
    public String toString() {
        return term;
    }
}
