package com.nazjara.model;

/**
 * Conversation memory strategies compared by this module.
 */
public enum MemoryType {
	/**
	 * Resend the last N messages verbatim as chat history.
	 */
	WINDOW,
	/**
	 * Store every message as a vector; retrieve only the most similar ones per request.
	 */
	VECTOR
}
