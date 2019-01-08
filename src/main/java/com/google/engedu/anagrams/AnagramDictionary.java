/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.anagrams;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private Random random = new Random();

    //Good to declare it as a list here so the programmer can operate on the base implementation (List is an interface, not an implementation).
    //Ex) if using a linked list, they can still use your methods
    private List<String> wordList = new ArrayList<>(); //NOTE: Don't need to specify String to the right of the equals sign

    //Guarantees there will be no repetition. Also, don't need to compare to all words in dictionary when checking if equal
    private Set<String> wordSet = new HashSet<>();

    //Map allows you to find the anagrams faster (key of sorted letters ('opst') will give you all acronyms (ex, pots, tops...)
    private Map<String, List<String>> lettersToWord = new HashMap<>();

    private Map<Integer, List<String>> sizeToWords = new HashMap<>();

    private int wordLength = DEFAULT_WORD_LENGTH;

    //For logging
    private static final String TAG = "AnagramDictionary";

    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            wordList.add(word);
            wordSet.add(word);
            String sortedWord = sortLetters(word);
            int lengthOfWord = word.length();
            if(!sizeToWords.containsKey(lengthOfWord)){
                sizeToWords.put(lengthOfWord, new ArrayList<String>());
            }
            sizeToWords.get(lengthOfWord).add(word);
            if(!lettersToWord.containsKey(sortedWord)){
                lettersToWord.put(sortedWord, new ArrayList<String>());
            }
            lettersToWord.get(sortedWord).add(word);

        }
        //Log.i(TAG, "wordlist size: " + wordList.size()); //Checking that all words are added to the list
        //Log.i(TAG, "map for (opst) = " + lettersToWord.get("opst"));
        //Log.i(TAG, "iGW 'nonstop' = " + isGoodWord("nonstop", "post"));
        //Log.i(TAG, "iGW 'poster' = " + isGoodWord("poster", "post"));
        //Log.i(TAG, "iGW 'lamp post' = " + isGoodWord("lamp post", "post"));
        //Log.i(TAG, "iGW 'spots' = " + isGoodWord("spots", "post"));
        //Log.i(TAG, "iGW 'apostrophe' = " + isGoodWord("apostrophe", "post"));
    }

    public boolean isGoodWord(String word, String base) {
        //Check if word in wordSet

        //Check that word is not a substring of the base word
        return wordSet.contains(word) && !word.contains(base.toLowerCase());
    }

    //Returns all anagrams of 'word' in dictionary
    //Would return a LinkedList if it was important for the user to operate on a linked list
    public List<String> getAnagrams(String targetWord) {
        ArrayList<String> result = new ArrayList<String>();
        String targetWordSorted = sortLetters(targetWord);
        for(String word : wordList){
            String wordSorted = sortLetters(word);
            if(wordSorted.equals(targetWordSorted)){
                result.add(word); //Adds original word to array list (not the sorted word)
            }
        }
        return result;
    }

    public List<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<String>();
        for(char c = 'a'; c <= 'z'; c++){
            String newWord = word + c;
            result.addAll(getAnagrams(newWord));
        }
        //result.addAll(getAnagrams(word)); //To include results from previous game
        Log.i(TAG, "result = " + result);
        return result;
    }

    public String pickGoodStarterWord() {
        //Log.i(TAG, "sortLetters(post) = " + sortLetters("post")); //Test for sortLetters
        //Log.i(TAG, "getAnagrams(dog) = " + getAnagrams("dog")); //Test for getAnagrams(), which shows in Logcat as 'getAnagrams(dog = [dog,god]'

        //Select random word from dictionary
//        Random rand = new Random();
//        int index = rand.nextInt(wordList.size());
//        Log.i(TAG, "index = " + index);
//
//        //Iterate till word has at least MIN_NUM_ANAGRAMS anagrams
//        int numAnagrams = 0;
//        while (numAnagrams < MIN_NUM_ANAGRAMS) {
//            index++;
//            Log.i(TAG, "Number of anagrams = " + numAnagrams + "Min num anagrams = " + MIN_NUM_ANAGRAMS);
//            if (index == wordList.size()) {
//                index = 0;
//            }
//            numAnagrams = getAnagramsWithOneMoreLetter(wordList.get(index)).size();
//        }
//        return wordList.get(index);

        //Part 2
        //Select random word from dictionary
        Random rand = new Random();
        List<String> wordsOfLength = sizeToWords.get(wordLength);
        int index = rand.nextInt(wordList.size());
        Log.i(TAG, "index = " + index);

        //Iterate till word has at least MIN_NUM_ANAGRAMS anagrams
        int numAnagrams = 0;
        while (numAnagrams < MIN_NUM_ANAGRAMS) {
            index++;
            Log.i(TAG, "Number of anagrams = " + numAnagrams + "Min num anagrams = " + MIN_NUM_ANAGRAMS);
            if (index == wordList.size()) {
                index = 0;
            }
            numAnagrams = getAnagramsWithOneMoreLetter(wordsOfLength.get(index)).size();
        }
        wordLength++;
        return wordsOfLength.get(index);
    }

    //Returns a String with the letters 'word' in alphabetical order (i.e. pots --> opst)
    private String sortLetters (String word){
        char [] wordLetters = word.toCharArray();
        Arrays.sort(wordLetters);
        return new String (wordLetters);
    }
}
