import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

class wordle {
    public static ArrayList<String> availableWords;
    public static ArrayList<String> globalDict;
    public static String solution;
    public static void main(String[] args) throws Exception {     
        globalDict = generateDictionaryOfWordLengthX(5, "dictionary.txt");
        ArrayList<String> dict = globalDict; //we should clone this properly here... in case we ever remove smth.

        letterColor g = letterColor.green;
        letterColor y = letterColor.yellow;
        letterColor b = letterColor.black;
        int won = 0;

        //resin 
        //cloth
        //gawky
        //bumpy
        int trials = 1_000_000;
        


        won = 0;
        String guess = "";

        int[] attempts = new int[6];

        
        for(int i = 0; i < trials; i++) {
            dict = globalDict;
            solution = generateRandomSolutionWord(dict);
            for(int j = 6; j > 0; j--) {
                int wordIndex = new Random().nextInt(dict.size());
                if(dict.size() <= j) {
                    guess = dict.get(0);
                }
                else if(j == 6) {
                    guess = "resin";
                } else if(j == 5) {
                    guess = "cloth";
                }else if(j== 4) {
                    guess = "gawky";
                } else if(j== 3) {
                    guess = "bumpy";
                } else if(j== 2) {
                    guess = dict.get(wordIndex);
                }
                else {
                    guess = dict.get(wordIndex);
                }

                //if dictsize  == j just try everything out.

                if(guess.equals(solution)) {
                    won++;
                    attempts[6 - j] += 1;
                    break;
                }
                letterColor[] correctLetters = evaluateWord(guess);
                dict = findMatchingWords(guess, correctLetters, dict);
            }
        }



        float[] attemptsinPercent= new float[6];

        for(int i = 0; i < attemptsinPercent.length; i++) {
            attemptsinPercent[i] = (float) attempts[i] / (float) trials;
        }

        System.out.println(Arrays.toString(attempts));
        System.out.println("total correct: " + won);
        System.out.println("correct in %: " + ((float) won / (float) trials) * 100);
    }

    /**
     * generates a dictionary of the desired word length from a dictionary file
     * @param wordLength of all the words in the dictionary
     * @param dictionaryPath the path where your dictionary is saved be aware of unix / windows backslashes
     * @return An Array with words of length wordLength
     * @throws FileNotFoundException gets thrown if your file or directory does not exist
     * @throws IOException 
     */
    private static ArrayList<String> generateDictionaryOfWordLengthX(int wordLength, String dictionaryPath) throws FileNotFoundException, IOException {
        FileReader fileReader = new FileReader(dictionaryPath);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        
        ArrayList<String> dictionary = new ArrayList<String>();
        ArrayList<String> dictionaryLength5 = new ArrayList<String>();

        String line = "";
        while( (line = bufferedReader.readLine()) != null) {
            dictionary.add(line);
        }

        bufferedReader.close();
        for(int i = 0; i < dictionary.size(); i++) {
            if(dictionary.get(i).length() == wordLength) {
                dictionaryLength5.add(dictionary.get(i));
            }
        }

        availableWords = dictionaryLength5;
        return dictionaryLength5;
    }

    /**
     * Finds matching words based on evaluation of the entered word
     * e.g. if hellz is the input and the evaluation table is green, green, green, green, black then "hello" and "hells" are candidates
     * @param enteredWord is the Word you entered in your Wordl
     * @param evaluatedWord is the color scheme your entered word got
     * @param dict the dictionary you try to find words from. You can use the return of this function as input here to reduce your choices 
     * if you are entering several words.
     * @return all possibilities that are left with your word and current evaluation
     */
    public static ArrayList<String> findMatchingWords(String enteredWord, letterColor[] evaluatedWord, ArrayList<String> dict) {
        if(enteredWord.length() != evaluatedWord.length) {
            System.err.println("error wrong length");
            return null;
        }
        
        ArrayList<String> allAvailableWords = new ArrayList<String>();
        
        char[] word = enteredWord.toCharArray();
        boolean check = true;
    
        for (String string : dict) {
            for(int i = 0; i < word.length; i++) {
                if(evaluatedWord[i] == letterColor.green) {
                    if(string.charAt(i) != enteredWord.charAt(i)) {
                        check = false;
                    }
                } else if (evaluatedWord[i] == letterColor.yellow) {
                    //string contains the letter but not at this position
                    if(!string.contains(Character.toString(enteredWord.charAt(i))) || enteredWord.charAt(i) == string.charAt(i)) {
                        check = false;
                    }
                } else if(evaluatedWord[i] == letterColor.black) {
                    if(string.contains(Character.toString(enteredWord.charAt(i)))) {
                        check = false;
                    }
                }
            }

            if(check) {
                allAvailableWords.add(string);
            }
            check = true;
        }
        return allAvailableWords;
    }

    /**
     * the possible colors each letter can get
     * black - word does not contain the letter
     * yellow - word does contain letter but at different position
     * green - word does contain letter at thid position
     */
    public enum letterColor {
        green, black, yellow
    }

    /**
     * evaluates how close your word is to the solution
     * @param word your entered word
     * @return An array of letterColors where each entry corresponds to the status of a letter in your solution word.
     */
    public static letterColor[] evaluateWord(String word) {
        int limit = 5;
        if(word.length() != limit) {
            System.out.println("Word to short or long");
            return null;
        }
        

        if(!globalDict.contains(word)) {
            System.err.println(word + " is not valid");
            return null;
        }

        letterColor[] evaluation = new letterColor[limit];

        for(int i = 0; i < evaluation.length; i++) {
                evaluation[i] = letterColor.black;
    
        }
        
        for(int i = 0; i < evaluation.length; i++) {
            if(solution.charAt(i) == word.charAt(i)) {
                evaluation[i] = letterColor.green;
            }            
        }

        for(int i = 0; i < evaluation.length; i++) {
            if(solution.contains(Character.toString(word.charAt(i))) && solution.charAt(i) != word.charAt(i)) {
                evaluation[i] = letterColor.yellow;
            }
        }
        
        return evaluation;
    }

    /**
     * generates a random solution word
     * @param wordleDictionary which holds the solution word
     * @return the solution word as String.
     */
    public static String generateRandomSolutionWord(ArrayList<String> wordleDictionary) {
        int wordIndex = new Random().nextInt(wordleDictionary.size());
        solution = wordleDictionary.get(wordIndex);
        return solution;
    }

    /**
     * calculates the levenshtein distance between two words
     * @param wordOne
     * @param wordTwo
     * @return the levenshtein distance
     */
    public static int calculateLevenshteinDistanceOfTwoWords(String wordOne, String wordTwo) {
        int[][] levenshteinTable = new int[wordOne.length() + 1][wordTwo.length() + 1];

        for(int k = 0; k < levenshteinTable.length; k++ ) {
            levenshteinTable[k][0] = k;
        }
        for(int k = 0; k < levenshteinTable[0].length; k++ ) {
            levenshteinTable[0][k] = k;
        }

        for(int i = 1; i < levenshteinTable.length; i++) {
            for(int j = 1; j < levenshteinTable[i].length; j++) {
                int upleft = levenshteinTable[i - 1][j - 1];
                int up = levenshteinTable[i][j - 1];
                int left = levenshteinTable[i - 1][j];
                if(wordOne.charAt(i - 1) == wordTwo.charAt(j - 1)) {
                    upleft += 0;
                } else {   
                    upleft += 1;
                }
                up += 1;
                left += 1;

                levenshteinTable[i][j] = Math.min(upleft, Math.min(up, left));
            }
        }

        return levenshteinTable[levenshteinTable.length -1 ][levenshteinTable[0].length - 1];
    }

    /**
     * 
     * @param dictionary
     * @param highdistance
     * @return
     */
    public static String findWordWithLevenshteinDistance(ArrayList<String> dictionary, boolean highdistance) {
        String bestWord = "";
        int[] highestDistances = new int[dictionary.size()];
        int[] lowestDistances = new int[dictionary.size()];
        int highestDistance = -1;
        int lowestDistance = Integer.MAX_VALUE;
        for (int i = 0; i < dictionary.size(); i++) {
            for (int j = 0; j < dictionary.size(); j++) {
                int distance = calculateLevenshteinDistanceOfTwoWords(dictionary.get(i), dictionary.get(j));
                if(distance > highestDistance) {
                    highestDistance = distance;
                }
                if(distance < lowestDistance) {
                    lowestDistance = distance;
                }
            }
            highestDistances[i] = highestDistance;
            lowestDistances[i] = lowestDistance;
        }

        int max_distance = -1;
        int min_distance = Integer.MAX_VALUE;
        int max_distance_index = highestDistances[0];
        int min_distance_index = lowestDistances[0];
        for(int i = 0; i < highestDistances.length; i++) {
            if(highestDistances[i] > max_distance) {
                max_distance = highestDistances[i];
                max_distance_index = i;
            }
            if(lowestDistances[i] < min_distance) {
                min_distance = lowestDistances[i];
                min_distance_index = i;
            }
        }
        
        if(highdistance) {
            return dictionary.get(max_distance_index);
        } else {
            return dictionary.get(min_distance_index);
        }
    }


    /**
     * 
     * @param dictionary
     * @return
     */
    public static ArrayList<String> findWordsThatReduceDictionariesQuick(ArrayList<String> dictionary) {
        letterColor b = letterColor.black;

        dictionary.remove("stoae");

        letterColor[] greenWords = {b,b,b,b,b};
        ArrayList<String> bestWords = new ArrayList<String>();
        int size = Integer.MAX_VALUE;
        for (int i = 0; i < dictionary.size(); i++) {
            String word = dictionary.get(i);
            ArrayList<String> reducedDict = findMatchingWords(word, greenWords, dictionary);
            if(reducedDict.size() <  size) {
                size = reducedDict.size();
            }
        }   
        for (int i = 0; i < dictionary.size(); i++) {
            String word = dictionary.get(i);
            ArrayList<String> reducedDict = findMatchingWords(word, greenWords, dictionary);

            if(reducedDict.size() == size) {
                bestWords.add(word);
            }
        }



        return bestWords;
    }

    public void scoreWords() {
        //todo
        //use frequency table to score words and make sure to avoid overlap...
    }

}