package com.example.demo;

import java.util.ArrayList;

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
    }

    public String getUser_name()                            { return user_name; }
    public String getMDP()                                  { return MDP; }
    public ArrayList<Integer> getScore()                    { return score; }
    public Joueur getJoueur()                               { return joueur; }

    public void setUser_name(String user_name)              { this.user_name = user_name; }
    public void setMDP(String MDP)                          { this.MDP = MDP; }
    public void setScore(ArrayList<Integer> score)          { this.score = score; }
    public void setJoueur(Joueur joueur)                    { this.joueur = joueur; }

}
