package com.example.demo;
import java.util.ArrayList;
import java.util.Comparator;
import static com.example.demo.Main.jeuEnCours;

public class Compte
{
    private String user_name;
    private String MDP;
    private ArrayList<Integer> score;
    private Joueur joueur;



    public Compte(String user_name, String MDP)
    {
        this.user_name = user_name;
        this.MDP = MDP;
        Joueur j = new Joueur();
        ArrayList<Integer> score = new ArrayList<>();
        this.setScore(score);
        this.setJoueur(j);
    }




    public String getUser_name()                            { return user_name; }
    public String getMDP()                                  { return MDP; }
    public ArrayList<Integer> getScore()                    { return score; }
    public Integer getScore(int index) {
        if (score != null && index >= 0 && index < score.size()) {
            return score.get(index);
        }
        return null;
    }
    public Joueur getJoueur()                               { return joueur; }


    public void setUser_name(String user_name)              { this.user_name = user_name; }
    public void setMDP(String MDP)                          { this.MDP = MDP; }
    public void setScore(ArrayList<Integer> score)          { this.score = score; }
    public void addScore(Integer nouveauScore) {
        if (nouveauScore >= 0) {
            this.score.add(nouveauScore);
            this.score.sort(Comparator.reverseOrder());
        }
    }
    public void setJoueur(Joueur joueur)                    { this.joueur = joueur; }

    public void addScore() {
        this.addScore(this.joueur.getScore_jeu());
        this.joueur.setScore_jeu(0);
        jeuEnCours = true;
    }
}