package org.example.features.gacha;

public enum GachaResultType {
    S_RANK(0.008,"S-RANK"),A_RANK(0.08,"A-RANK"),B_RANK(1 - 0.088,"B-RANK");

    final double rate;
    final String displayString;

    GachaResultType(double rate, String displayString){
        this.rate = rate;
        this.displayString = displayString;
    }
}
