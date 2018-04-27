package com.devskiller.jfairy.producer.text;

import com.devskiller.jfairy.producer.unique.UniqueIgnore;

public interface Texts {

	@UniqueIgnore
	Texts limitedTo(int limit);

	String result(String result);

	String loremIpsum();

	String text();

	String word();

	String word(int count);

	String latinWord();

	String latinWord(int count);

	String latinSentence();

	String latinSentence(int wordCount);

	String sentence();

	String sentence(int wordCount);

	String paragraph();

	String paragraph(int sentenceCount);

	/**
	 * Generates random string with desired length
	 *
	 * @param charsCount
	 *            string length
	 * @return random string
	 */
	String randomString(int charsCount);

}