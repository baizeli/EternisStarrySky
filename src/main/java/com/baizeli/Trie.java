package com.baizeli;

import java.text.MessageFormat;

public class Trie
{
	public static final short[] DEFAULT_DICTIONARY = {
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14,
		15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
	};
	private final short[] dictionary;
	public final Trie[] trie;
	public boolean word = false;

	public Trie(short[] dictionary)
	{
		this.dictionary = dictionary;
		int idx = -1;
		for (short s : this.dictionary)
			if (s > idx)
				idx = s;
		idx++;
		this.trie = new Trie[idx];
	}

	public void add(byte[] str, int pos, int len)
	{
		if (pos == str.length && len == 0)
		{
			this.word = true;
			return;
		}

		if (pos + len > str.length)
			throw new ArrayIndexOutOfBoundsException(MessageFormat.format("{0} + {1} > {2}", pos, len, str.length));

		int code = str[pos] & 0xFF;
		int idx;
		if (code >= this.dictionary.length || (idx = this.dictionary[code]) < 0)
			throw new IllegalArgumentException("Unsupported code in dictionary: " + code);

		if (this.trie[idx] == null)
			this.trie[idx] = new Trie(this.dictionary);
		this.trie[idx].add(str, pos + 1, len - 1);
	}

	public void add(byte[] str)
	{
		this.add(str, 0, str.length);
	}

	public int search(byte[] prefix, int ppos, int plen, byte[] str, int spos, int slen)
	{
		if (plen == 0 && this.word)
			return 0;
		if (slen == 0)
			return 0;

		int idx = -1;
		if (plen != 0)
		{
			int i = this.dictionary[prefix[ppos]];
			if (i < 0)
				return -1;
			if (this.trie[i] == null)
				return -1;
			idx = i;
			ppos++;
			plen--;
		}

		for (int i = 0; (i < this.trie.length) && (idx == -1); i++)
			if (this.trie[i] != null)
				idx = i;
		if (idx == -1)
			return -1;

		int code;
		for (code = 0; code < this.dictionary.length; code++)
			if (this.dictionary[code] == idx)
				break;

		str[spos] = (byte) code;
		int suf = this.trie[idx].search(prefix, ppos, plen, str, spos + 1, slen - 1);
		if (suf == -1)
			return -1;
		return suf + 1;
	}

	public void clear()
	{
		for (int i = 0; i < this.trie.length; i++)
			trie[i] = null;
	}
}
