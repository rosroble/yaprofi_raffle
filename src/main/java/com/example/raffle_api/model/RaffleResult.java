package com.example.raffle_api.model;

public class RaffleResult {
    private Participant winner;
    private Prize prize;

    public RaffleResult(Participant winner, Prize prize) {
        this.winner = winner;
        this.prize = prize;
    }

    public Participant getWinner() {
        return winner;
    }

    public Prize getPrize() {
        return prize;
    }

    public static final String example =
        "[\n" +
                "  {\n" +
                "    “winner”: {\n" +
                "      “id”: number,\n" +
                "      “name”: “string”\n" +
                "    },\n" +
                "    “prize”: {\n" +
                "      “id”: number,\n" +
                "      “description”: “string”\n" +
                "    }\n" +
                "  },\n" +
                "  …\n" +
                "]";
}
