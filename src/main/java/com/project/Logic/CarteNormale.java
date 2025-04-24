package com.project.Logic;

public class CarteNormale extends Carte {
    public CarteNormale(int numero, char couleur) {
        super(String.valueOf(numero), couleur);
        if (numero < 0 || numero > 9) {
            throw new IllegalArgumentException("donner un num entre 0 et 9");
        }
    }
}
