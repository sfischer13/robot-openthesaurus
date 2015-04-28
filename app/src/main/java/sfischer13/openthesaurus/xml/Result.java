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

    // TODO: new field
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (Synset aSynset : synsets) {
            sb.append(sep);
            sb.append(aSynset.toString());
            sep = "\n\n";
        }
        return sb.toString();
    }
}
