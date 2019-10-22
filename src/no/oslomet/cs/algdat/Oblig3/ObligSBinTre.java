package no.oslomet.cs.algdat.Oblig3;

////////////////// ObligSBinTre /////////////////////////////////

import java.util.*;

public class ObligSBinTre<T> implements Beholder<T>
{
  private static final class Node<T>   // en indre nodeklasse
  {
    private T verdi;                   // nodens verdi
    private Node<T> venstre, høyre;    // venstre og høyre barn
    private Node<T> forelder;          // forelder

    // konstruktør
    private Node(T verdi, Node<T> v, Node<T> h, Node<T> forelder)
    {
      this.verdi = verdi;
      venstre = v; høyre = h;
      this.forelder = forelder;
    }

    private Node(T verdi, Node<T> forelder)  // konstruktør
    {
      this(verdi, null, null, forelder);
    }

    @Override
    public String toString(){ return "" + verdi;}

  } // class Node

  private Node<T> rot;                            // peker til rotnoden
  private int antall;                             // antall noder
  private int endringer;                          // antall endringer

  private final Comparator<? super T> comp;       // komparator

  public ObligSBinTre(Comparator<? super T> c)    // konstruktør
  {
    rot = null;
    antall = 0;
    comp = c;
  }

  //Oppgave 1
  @Override
  public boolean leggInn(T verdi)
  {
    Objects.requireNonNull(verdi, "Ulovlig med nullverdier!");

    Node<T> p = rot, q = null;               // p starter i roten
    int cmp = 0;                             // hjelpevariabel

    while (p != null)       // fortsetter til p er ute av treet
    {
      q = p;                                 // q er forelder til p
      cmp = comp.compare(verdi,p.verdi);     // bruker komparatoren
      p = cmp < 0 ? p.venstre : p.høyre;     // flytter p
    }

    // p er nå null, dvs. ute av treet, q er den siste vi passerte

    //Siden q er forrige node blir dette foreldren til p
    p = new Node<>(verdi,null,null, q);                  // oppretter en ny node

    if (q == null) rot = p;                  // p blir rotnode
    else if (cmp < 0) q.venstre = p;         // venstre barn til q
    else q.høyre = p;                        // høyre barn til q

    antall++;                                // én verdi mer i treet
    return true;                             // vellykket innlegging
  }
  
  @Override
  public boolean inneholder(T verdi)
  {
    if (verdi == null) return false;

    Node<T> p = rot;

    while (p != null)
    {
      int cmp = comp.compare(verdi, p.verdi);
      if (cmp < 0) p = p.venstre;
      else if (cmp > 0) p = p.høyre;
      else return true;
    }

    return false;
  }
  
  @Override
  public boolean fjern(T verdi)
  {
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  
  public int fjernAlle(T verdi)
  {
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  
  @Override
  public int antall()
  {
    return antall;
  }

  //Oppgave 2
  public int antall(T verdi) {
    int forekomster = 0;
    Node<T> p = rot;

    if(verdi == null){
      return forekomster;
    }

    while(p != null){
      int compare = comp.compare(verdi, p.verdi);

      //Er verdien lavere og dermed på venstre side?
      if(compare < 0){
        p = p.venstre;
      }
      //Er verdien høyere og dermed på høyre side?
      else if(compare > 0){
        p = p.høyre;
      }
      //Funnet verdien, øker teller og lar søket fortsette på høyre side,
      //siden alle like tall må ligge på høyre side.
      else{
        forekomster++;
        p = p.høyre;
      }
    }
    return forekomster;
  }
  
  @Override
  public boolean tom()
  {
    return antall == 0;
  }
  
  @Override
  public void nullstill()
  {
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }


  private static <T> Node<T> nesteInorden(Node<T> p){
      Node<T> result = null;

      Node q = p.høyre;

      while(q != null){
        result = q;
        q = q.venstre;
      }

      if(result != null){
        return result;
      }

      q = p;

      while(q != null){
        if(q.forelder != null && q.forelder.venstre == q){
          return q.forelder;
        }
        q = q.forelder;
      }
      return null;
  }

  
  @Override
  public String toString() {
    if(tom()) return "[]";

    String ut = "[";
    Node<T> p = rot;

    //Finner den node lengst nede til venstre, slik at nesteInorden() starter
    //fra rikgit node
    while(p.venstre != null){
      p = p.venstre;
    }

    ut += p.verdi;

    while(nesteInorden(p) != null){
        p = nesteInorden(p);
        ut += ", " + p.verdi;
    }
    ut += "]";
    return ut;
  }

  //Traverserer iterativt gjennom treet i omvendt In-Order
  public String omvendtString() {
    if(tom()) return "[]";

    String ut = "[";

    Deque<Node> stack = new ArrayDeque<>();
    Node p = rot;

    //Finner siste In-Order Node
    for(; p.høyre != null; p = p.høyre)stack.push(p);

    while(true){

      ut += p.verdi;

      if(p.venstre != null){
        for(p = p.venstre; p.høyre != null; p = p.høyre){
          stack.push(p);
        }
      }
      else if(!stack.isEmpty()){
        p = stack.pop();
      }
      else break;

      ut += ", ";
    }

    ut += "]";
    return ut;
  }
  
  public String høyreGren(){
    //throw new UnsupportedOperationException("Ikke kodet ennå!");
    String ut = "[";
    int nivå = 0;
    int nesteNivå = 0;

    Node p = rot;


    if(p == null) {
      return "[]";
    }

    while(p != null) {
      ut += p + ", ";
      if(p.høyre == null) {
        p = p.venstre;
      } else {
        p = p.høyre;
      }
    }
    ut += "]";
    return ut;
  }
  
  public String lengstGren(){

    String ut = "[";

    ut += "]";
    return ut;

    //throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  public String[] grener() {
      String gren = "[";
      String[] grener = new String[10];
      int teller = 0;
      int hoppeOppTeller = 2;

      Node<T> p = rot;
      gren += p.verdi;

      while(true){

          if(p.venstre == null && p.høyre == null){
              gren += "]";
              grener[teller] = gren;
              teller++;

              while(p.forelder.høyre == null){
                  p = p.forelder;
                  hoppeOppTeller++;
              }
              gren = gren.substring(0, gren.length() - hoppeOppTeller);
              p = p.forelder.høyre;
              gren += p.verdi;
          }

          if(p.venstre != null){
              gren += p.venstre.verdi;
              p = p.venstre;
          }
          if(p.høyre != null){
              gren += p.høyre.verdi;
              p = p.høyre;
          }
      }
  }
  
  public String bladnodeverdier()
  {
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  
  public String postString()
  {
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  
  @Override
  public Iterator<T> iterator()
  {
    return new BladnodeIterator();
  }
  
  private class BladnodeIterator implements Iterator<T>
  {
    private Node<T> p = rot, q = null;
    private boolean removeOK = false;
    private int iteratorendringer = endringer;
    
    private BladnodeIterator()  // konstruktør
    {
      throw new UnsupportedOperationException("Ikke kodet ennå!");
    }
    
    @Override
    public boolean hasNext()
    {
      return p != null;  // Denne skal ikke endres!
    }
    
    @Override
    public T next()
    {
      throw new UnsupportedOperationException("Ikke kodet ennå!");
    }
    
    @Override
    public void remove()
    {
      throw new UnsupportedOperationException("Ikke kodet ennå!");
    }

  } // BladnodeIterator

  public static void main(String[] args){
        ObligSBinTre<Character> tre = new ObligSBinTre<>(Comparator.naturalOrder());
        char[] verdier = "IATBHJCRSOFELKGDMPQN".toCharArray();
        for(char c : verdier) tre.leggInn(c);

        String[] s = tre.grener();
        for(String gren : s) System.out.println(gren);
  }


} // ObligSBinTre
