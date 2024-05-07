package com.fsryan.chess.pgn.fsm

/**
 * The base interface for a PGN Finite State Machine. This describes the
 * minimum interface any FSM may have.
 */
interface PGNFSM<out T: Any> {
    fun process(position: Int): PGNFSMResult<T>
}