package com.example.raffle_api.model;

import java.util.*;

public class ExtendedPromo extends Promo{

    private Map<Long, Prize> prizes;
    private Map<Long, Participant> participants;

    public ExtendedPromo(String name, String desc) {
        super(name, desc);
        prizes = new HashMap<>();
        participants = new HashMap<>();
    }

    public void addPrize(Prize prize) {
        prizes.put(prize.getId(), prize);
    }

    public void addParticipant(Participant participant) {
        participants.put(participant.getId(), participant);
    }

    public Participant getParticipant(long id) {
        return participants.get(id);
    }

    public void removeParticipant(long id) {
        participants.put(id, null);
    }

    public Prize getPrize(long id) {
        return prizes.get(id);
    }

    public void removePrize(long id) {
        prizes.put(id, null);
    }

    public List<Participant> getParticipants() {
        return new ArrayList<>(participants.values());
    }

    public List<Prize> getPrizes() {
        return new ArrayList<>(prizes.values());
    }


}
