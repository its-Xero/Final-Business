package com.project.Logic;

public class Carte {
	private String valeur; // num , signe 
	private char couleur;// r , b , j ,v
	public Carte(String valeur, char couleur) {
		this.couleur=couleur;
		this.valeur=valeur;
	}

    public char getCouleur() {
        return couleur;
    }

    public String getValeur() {
        return valeur;
    }

        @Override
    public String toString(){
        return("la carte est: " +valeur+" " +couleur );
    }

    public void afficher() {
        System.out.println(toString());
    }
    public void setCouleur(char couleur) {
        this.couleur = couleur;
    }
    public void setValeur(String valeur){
        this.valeur = valeur;
    }

    public static void main(String[] args){
        CarteNormale c1=new CarteNormale(9, 'r');
        CarteAction c2=new CarteAction("Passer", 'b');
        CarteSpeciale c3=new CarteSpeciale("wild");

        c1.afficher();
        c2.afficher();
        c3.afficher();




    }
}