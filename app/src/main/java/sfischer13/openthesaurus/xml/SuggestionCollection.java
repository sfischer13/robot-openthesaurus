package sfischer13.openthesaurus.xml;


import java.util.ArrayList;
import java.util.List;

import sfischer13.openthesaurus.model.Term;

class SuggestionCollection {
    private final Type type;
    private final List<Term> terms;

    enum Type {
        SIMILAR("//similarterms/term"), SUB("//substringterms/term"), START("//startswithterms/term");

        private final String path;

        Type(String path) {
            this.path = path;
        }

        public String path() {
            return path;
        }
    }

    public SuggestionCollection(Type type) {
        this.type = type;
        terms = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type.path());
        for (Term aTerm : terms) {
            sb.append("\n");
            sb.append(aTerm.toString());
        }
        return sb.toString();
    }

    public void add(Term term) {
        terms.add(term);
    }

    public Type getType() {
        return type;
    }

    public List<Term> getTerms() {
        return terms;
    }
}
