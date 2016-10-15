/*
   Copyright 2015-2016 Stefan Fischer

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

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
