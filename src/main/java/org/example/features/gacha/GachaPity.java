package org.example.features.gacha;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GachaPity {
    private String userID;
    private List<GachaPull> pullHistory;
}
