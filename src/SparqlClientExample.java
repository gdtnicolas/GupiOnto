import java.util.*;

public class SparqlClientExample {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        SparqlClient sparqlClient = new SparqlClient("127.0.0.1:3030/space");

        String query = "ASK { ?s ?p ?o }";
        boolean serverIsUp = sparqlClient.ask(query);
        System.out.println("loutre : " + serverIsUp);
        if (serverIsUp) {
            System.out.println("server is UP");
            enrichissement(sparqlClient, "lieu naissance,Omar Sy");

        } else {
            System.out.println("service is DOWN");
        }
    }

    // le module doit parser la requete en fonction des vigules pour retrouver les mots clé
    private static ArrayList<WordWeight> getSynonyms(SparqlClient sparqlClient, String keyword) {
        String query = "PREFIX : <http://ontologies.alwaysdata.net/space#>\n" +
                "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX owl:  <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>\n" +
                "SELECT ?labels WHERE\n" +
                "{\n" +
                "   ?uri rdfs:label \"" + keyword + "\"@fr.\n" +
                "   ?uri rdfs:label ?labels.\n" +
                "   FILTER (lang(?labels)=\"fr\").\n" +
                "}\n";
        Iterable<Map<String, String>> results = sparqlClient.select(query);
        ArrayList<WordWeight> finalResults = new ArrayList<WordWeight>();
        //i=1 initialement au cas où un seul mot sorte, on divise par 2
        int i = 1;
        for (Map<String, String> result : results) {
            i++;
        }
        for (Map<String, String> result : results) {
            finalResults.add(new WordWeight(result.get("labels"), (float) 1 / i));
        }
        return finalResults;
    }

    private static ArrayList<WordWeight> getLinkPropertyRessources(SparqlClient sparqlClient, String keyword1, String keyword2) {
        String query = "PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX owl:  <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>\n" +
                "\n" +
                "SELECT ?labels\n" +
                "WHERE {\n" +
                "  ?ressourcekey1 rdfs:label \"" + keyword1 + "\"@fr.\n" +
                "\n" +
                "  ?ressourcekey2 rdfs:label \"" + keyword2 + "\".\n" +
                "  ?ressourcekey2 ?ressourcekey1 ?reponse.\n" +
                "  ?reponse rdfs:label ?labels.\n" +
                "}\n";
        Iterable<Map<String, String>> results = sparqlClient.select(query);
        ArrayList<WordWeight> finalResults = new ArrayList<WordWeight>();
        //i=1 initialement au cas où un seul mot sorte, on divise par 2
        int i = 1;
        for (Map<String, String> result : results) {
            i++;
        }
        for (Map<String, String> result : results) {
            finalResults.add(new WordWeight(result.get("labels"), (float) 1 / i));
        }
        return finalResults;
    }

    private static ArrayList<WordWeight> enrichissement(SparqlClient sparqlClient, String request) {
        String[] keywords = request.split(",");

        ArrayList<WordWeight> results = new ArrayList<WordWeight>();
        for (int i = 0; i < keywords.length; i++) {
            ArrayList<WordWeight> synonyms = getSynonyms(sparqlClient, keywords[i]);
            results.addAll(synonyms);
        }


        for (int i = 0; i < keywords.length; i++) {
            for (int j = i; j < keywords.length; j++){
                    ArrayList<WordWeight> links = getLinkPropertyRessources(sparqlClient, keywords[i], keywords[j]);
                    results.addAll(links);
                    results.addAll(links);
                    links = getLinkPropertyRessources(sparqlClient, keywords[j], keywords[i]);
                    results.addAll(links);
            }
        }
        //manipulation pour éviter tout duplicatat (les sets ne peuvent pas contenir des valeurs identiques)
        Set<WordWeight> set = new HashSet<WordWeight>(results);
        results.clear();
        results.addAll(set);
        return results;
    }
}
