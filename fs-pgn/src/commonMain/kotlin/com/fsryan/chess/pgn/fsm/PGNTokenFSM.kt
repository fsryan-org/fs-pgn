package com.fsryan.chess.pgn.fsm

/**
 * PGN character data is organized as tokens. A token is a contiguous sequence
 * of characters that represents a basic semantic unit. Tokens _may_ be
 * separated from adjacent tokens by white space characters. (White space
 * characters include space, newline, and tab characters.) Some tokens are self
 * delimiting and do not require white space characters.
 */
internal interface PGNTokenFSM<out T: Any>: PGNFSM<T> {
}