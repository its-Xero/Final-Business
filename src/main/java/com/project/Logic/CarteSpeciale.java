package com.project.Logic;

public class CarteSpeciale extends Carte {
	public CarteSpeciale(String valeur) {
		super(valeur, 'N'); //N veut dire aucune couleur
        if(!valeur.equals("wild") && !valeur.equals("wildfour")){
			throw new IllegalArgumentException("Type non valide");
		}
	}
	//j'ai redefinie la methode psk ay tkhrjli null fi valeur
        @Override
	public void afficher() {
        System.out.println("Carte spéciale: " + getValeur());
    }
}
