package com.example.raffle_api.model;

public class RuffleResult {
    private Participant winner;
    private Prize prize;

    public RuffleResult(Participant winner, Prize prize) {
        this.winner = winner;
        this.prize = prize;
    }

    public Participant getWinner() {
        return winner;
    }

    public Prize getPrize() {
        return prize;
    }
}
