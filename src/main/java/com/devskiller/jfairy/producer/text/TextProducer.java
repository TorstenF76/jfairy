/*
 * Copyright (c) 2013 Codearte
 */
package com.devskiller.jfairy.producer.text;

import static org.apache.commons.lang3.StringUtils.left;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.devskiller.jfairy.producer.BaseProducer;
import com.devskiller.jfairy.producer.util.TextUtils;

public class TextProducer implements Texts {

	private static final int DEFAULT_WORD_COUNT = 3;

	private static final int DEFAULT_WORD_COUNT_IN_SENTENCE = 3;

	private static final int DEFAULT_SENTENCE_COUNT = 3;

	private static final int SENTENCE_COUNT_PRECISION_MIN = 1;

	private static final int SENTENCE_COUNT_PRECISION_MAX = 3;

	private final TextProducerInternal textProducerInternal;

	private final BaseProducer baseProducer;

	private int limit = 0;

	@Inject
	public TextProducer(TextProducerInternal textProducerInternal, BaseProducer baseProducer) {
		this.textProducerInternal = textProducerInternal;
		this.baseProducer = baseProducer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.devskiller.jfairy.producer.text.TextProducer#limitedTo(int)
	 */
	@Override
	public Texts limitedTo(int limit) {
		this.limit = limit;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.devskiller.jfairy.producer.text.TextProducer#result(java.lang.String)
	 */
	@Override
	public String result(String result) {
		if (limit > 0) {
			return left(result, limit);
		} else {
			return result;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.devskiller.jfairy.producer.text.TextProducer#loremIpsum()
	 */
	@Override
	public String loremIpsum() {
		return result(textProducerInternal.loremIpsum());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.devskiller.jfairy.producer.text.TextProducer#text()
	 */
	@Override
	public String text() {
		return result(textProducerInternal.text());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.devskiller.jfairy.producer.text.TextProducer#word()
	 */
	@Override
	public String word() {
		return result(word(DEFAULT_WORD_COUNT));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.devskiller.jfairy.producer.text.TextProducer#word(int)
	 */
	@Override
	public String word(int count) {
		return result(textProducerInternal.cleanWords(count));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.devskiller.jfairy.producer.text.TextProducer#latinWord()
	 */
	@Override
	public String latinWord() {
		return result(latinWord(DEFAULT_WORD_COUNT));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.devskiller.jfairy.producer.text.TextProducer#latinWord(int)
	 */
	@Override
	public String latinWord(int count) {
		return result(textProducerInternal.cleanLatinWords(count));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.devskiller.jfairy.producer.text.TextProducer#latinSentence()
	 */
	@Override
	public String latinSentence() {
		return result(latinSentence(DEFAULT_SENTENCE_COUNT));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.devskiller.jfairy.producer.text.TextProducer#latinSentence(int)
	 */
	@Override
	public String latinSentence(int wordCount) {
		return result(textProducerInternal.latinSentence(wordCount));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.devskiller.jfairy.producer.text.TextProducer#sentence()
	 */
	@Override
	public String sentence() {
		return result(sentence(DEFAULT_WORD_COUNT_IN_SENTENCE));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.devskiller.jfairy.producer.text.TextProducer#sentence(int)
	 */
	@Override
	public String sentence(int wordCount) {
		return result(textProducerInternal.sentence(wordCount));

	}

	private List<String> sentences(int sentenceCount) {
		List<String> sentences = new ArrayList<String>(sentenceCount);
		for (int i = 0; i < sentenceCount; i++) {
			sentences.add(sentence());
		}
		return sentences;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.devskiller.jfairy.producer.text.TextProducer#paragraph()
	 */
	@Override
	public String paragraph() {
		return result(paragraph(DEFAULT_SENTENCE_COUNT));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.devskiller.jfairy.producer.text.TextProducer#paragraph(int)
	 */
	@Override
	public String paragraph(int sentenceCount) {
		return result(TextUtils.joinWithSpace(sentences(sentenceCount
				+ baseProducer.randomBetween(SENTENCE_COUNT_PRECISION_MIN, SENTENCE_COUNT_PRECISION_MAX))));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.devskiller.jfairy.producer.text.TextProducer#randomString(int)
	 */
	@Override
	public String randomString(int charsCount) {
		return textProducerInternal.randomString(charsCount);
	}
}
