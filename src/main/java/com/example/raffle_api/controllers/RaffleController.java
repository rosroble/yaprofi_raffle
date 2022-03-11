package com.example.raffle_api.controllers;

import com.example.raffle_api.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.*;

@RestController
@RequestMapping("/promo")
public class RaffleController {

    private Map<Long, Promo> activePromos;

    @PostConstruct
    private void init() {
        activePromos = new HashMap<>();
    }

    @PostMapping
    public ResponseEntity<Long> createPromo(@RequestBody Map<String, String> payload) {
        if (!payload.containsKey("name")) return ResponseEntity.badRequest().build();
        Promo newPromo = new ExtendedPromo(payload.get("name"), payload.get("description"));
        activePromos.put(newPromo.getId(), newPromo);
        return ResponseEntity.ok(newPromo.getId());
    }

    @GetMapping
    public ResponseEntity<?> getPromos() {
        List<Map<String, Object>> response = new ArrayList<>();
        for (Promo p: activePromos.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", p.getId());
            map.put("name", p.getName());
            map.put("description", p.getDescription());
            response.add(map);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExtendedPromo> getPromo(@PathVariable("id") long id) {
        return ResponseEntity.ok((ExtendedPromo) activePromos.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editPromo(@PathVariable("id") long id, @RequestBody Map<String, String> payload) {
        if (!activePromos.containsKey(id)) return ResponseEntity.notFound().build();
        if (payload.get("name") == null || payload.get("name").isEmpty()) return ResponseEntity.badRequest().build();
        Promo promo = activePromos.get(id);
        promo.setName(payload.get("name"));
        if (payload.containsKey("description")) promo.setDesc(payload.get("description"));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePromo(@PathVariable("id") long id) {
        if (!activePromos.containsKey(id)) return ResponseEntity.notFound().build();
        activePromos.put(id, null);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/participant")
    public ResponseEntity<?> addParticipant(@PathVariable("id") long id, @RequestBody Map<String, String> payload) {
        if (!activePromos.containsKey(id)) return ResponseEntity.notFound().build();
        if (!payload.containsKey("name")) return ResponseEntity.badRequest().build();

        ExtendedPromo promo = (ExtendedPromo) activePromos.get(id);
        Participant participant = new Participant(payload.get("name"));
        promo.addParticipant(participant);

        return ResponseEntity.ok(participant.getId());
    }

    @DeleteMapping("/{promoId}/participant/{participantId}")
    public ResponseEntity<?> deleteParticipant(@PathVariable("promoId") long promoId,
                                               @PathVariable("participantId") long participantId) {
        if (!activePromos.containsKey(promoId)) return ResponseEntity.notFound().build();
        ExtendedPromo promo = (ExtendedPromo) activePromos.get(promoId);
        Participant p = promo.getParticipant(participantId);
        if (p == null) return ResponseEntity.notFound().build();
        promo.removeParticipant(participantId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/prize")
    public ResponseEntity<?> addPrize(@PathVariable("id") long id, @RequestBody Map<String, String> payload) {
        if (!activePromos.containsKey(id)) return ResponseEntity.notFound().build();
        if (!payload.containsKey("description")) return ResponseEntity.badRequest().build();

        ExtendedPromo promo = (ExtendedPromo) activePromos.get(id);
        Prize prize = new Prize(payload.get("description"));
        promo.addPrize(prize);
        return ResponseEntity.ok(prize.getId());
    }

    @DeleteMapping("/{promoId}/prize/{prizeId}")
    public ResponseEntity<?> deletePrize(@PathVariable("promoId") long promoId,
                                               @PathVariable("prizeId") long prizeId) {
        if (!activePromos.containsKey(promoId)) return ResponseEntity.notFound().build();
        ExtendedPromo promo = (ExtendedPromo) activePromos.get(promoId);
        Prize p = promo.getPrize(prizeId);
        if (p == null) return ResponseEntity.notFound().build();
        promo.removePrize(prizeId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/raffle")
    public ResponseEntity<?> raffle(@PathVariable("id") long id) {
        if (!activePromos.containsKey(id)) ResponseEntity.notFound().build();
        ExtendedPromo promo = (ExtendedPromo) activePromos.get(id);
        List<Participant> participants = promo.getParticipants();
        Collections.shuffle(participants);
        List<Prize> prizes = promo.getPrizes();
        if (participants.size() != prizes.size()) return ResponseEntity.status(HttpStatus.CONFLICT).build();

        List<RuffleResult> results = new ArrayList<>();
        for (int i = 0; i < participants.size(); i++) {
            results.add(new RuffleResult(participants.get(i), prizes.get(i)));
        }
        return ResponseEntity.ok(results);
    }

}
