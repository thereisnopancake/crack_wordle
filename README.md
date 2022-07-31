# crack_wordle
This was a fun little sideproject during the wordle hype were I tried to crack wordles at a 100% success rate.


# how to crack_wordle for your personal daily wordle



# how to make the best darn algorithm.

1. generate your dictionary lists in your main function

`globalDict = generateDictionaryOfWordLengthX(5, "dictionary.txt");`  
`ArrayList<String> dict = generateDictionaryOfWordLengthX(5, "dictionary.txt");`

2. generate a random solution of words in your dictionary (solution is a static field in the class so you can directly set it - this subject to change)
`solution = generateRandomSolutionWord(dict);`

3. create a attempts loop and break if the solution is found, evaluate your word with evaluateWord():
`for(int attempts = 6; attempts > 0; attempts--) {
  guess = YourGenerateAGuessFunction
  
    if(guess.equals(solution)) {
      break;
  }
  letterColor[] correctLetters = evaluateWord(guess);
}
`
4. We are using a find Matching words function.



