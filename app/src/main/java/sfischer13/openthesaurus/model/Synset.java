/*
   Copyright 2015-2017 Stefan Fischer

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

package sfischer13.openthesaurus.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Synset implements Serializable {
    private final List<Term> terms;

    public Synset() {
        this.terms = new ArrayList<>();
    }

    public void add(Term term) {
        terms.add(term);
    }

    public List<Term> getTerms() {
        return terms;
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