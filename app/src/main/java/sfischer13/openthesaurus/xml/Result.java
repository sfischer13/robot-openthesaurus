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

package sfischer13.openthesaurus.xml;

import java.util.List;

import sfischer13.openthesaurus.model.Synset;

public class Result {
    private final List<Synset> synsets;
    private final List<SuggestionCollection> suggestions;

    public Result(List<Synset> synsets, List<SuggestionCollection> suggestions) {
        this.synsets = synsets;
        this.suggestions = suggestions;
    }

    public List<Synset> getSynsets() {
        return synsets;
    }

    public List<SuggestionCollection> getSuggestions() {
        return suggestions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        String sep = "";
        for (Synset aSynset : synsets) {
            sb.append(sep);
            sb.append(aSynset.toString());
            sep = "\n\n";
        }

        if (0 == sb.length())      {
            sep = "";
        }
        for (SuggestionCollection aSuggestion : suggestions) {
            sb.append(sep);
            sb.append(aSuggestion.toString());
            sep = "\n\n";
        }

        return sb.toString();
    }
}
