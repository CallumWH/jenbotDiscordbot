package org.example.features.gacha;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class GachaPull {
    private GachaResultType gachaResultType;
    private LocalDateTime localDateTime;
    private String userID;
}
