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

  //Oppgave 5
  @Override
  public boolean fjern(T verdi)
  {
    // Programkode 5.2.8
    if (verdi == null) return false;  // treet har ingen nullverdier

    Node<T> p = rot, q = null;   // q skal være forelder til p

    while (p != null)            // leter etter verdi
    {
      int cmp = comp.compare(verdi,p.verdi);      // sammenligner
      if (cmp < 0) { q = p; p = p.venstre; }      // går til venstre
      else if (cmp > 0) { q = p; p = p.høyre; }   // går til høyre
      else break;    // den søkte verdien ligger i p
    }

    if (p == null) return false;   // finner ikke verdi

    if (p.venstre == null || p.høyre == null)  // Tilfelle 1) og 2)
    {
      Node<T> b = p.venstre != null ? p.venstre : p.høyre;  // b for barn
      if (p == rot){
        rot = b;
        p = null;
      } else if (p == q.venstre) {
          if(b == null) {
              q.venstre = p.venstre;
              p = null;
          } else {
              q.venstre = p.venstre;
              b.forelder = p.forelder;
              p = null;
          }
      } else {
        if(b == null) {
            q.høyre = p.høyre;
            p = null;
        } else {
            q.høyre = p.høyre;
            b.forelder = p.forelder;
            p = null;
        }
      }

    } else  // Tilfelle 3)
    {
      Node<T> s = p, r = p.høyre;   // finner neste i inorden
      while (r.venstre != null)
      {
        s = r;    // s er forelder til r
        r = r.venstre;
      }

      p.verdi = r.verdi;   // kopierer verdien i r til p

      if (s != p) {

          r.høyre.forelder = r.forelder;
        s.venstre = r.høyre;
      } else {
          if(r.høyre == null && r.venstre == null) {
              s.høyre = null;
              r = null;
          } else {
              r.høyre.forelder = r.forelder;
              s.høyre = r.høyre;
          }
      }
    }

    antall--;   // det er nå én node mindre i treet
    return true;
  }


  public int fjernAlle(T verdi)
  {
      if(tom()) {
          return 0;
      }
      Node<T> p = rot;
      Node<T> q = null;
      int fjernet = 0;

      while (p != null)            // leter etter verdi
      {
          int cmp = comp.compare(verdi,p.verdi);      // sammenligner
          if (cmp < 0) { q = p; p = p.venstre; }      // går til venstre
          else if (cmp > 0) { q = p; p = p.høyre; }   // går til høyre
          else break;    // den søkte verdien ligger i p
      }

      if(p == null) {
          return 0;
      }

      while(inneholder(verdi)) {
          if(p.venstre == null || p.høyre == null) {
              Node<T> b = p.venstre != null ? p.venstre : p.høyre;

              if (p == rot){
                  rot = b;
                  b.forelder = null;
              } else if (p == q.venstre) {
                  q.venstre = b;
                  p.forelder = null;
              } else {
                  q.høyre = b;
                  p.forelder = null;
              }

          } else  {
              Node<T> s = p, r = p.høyre;   // finner neste i inorden
              while (r.venstre != null)
              {
                  s = r;    // s er forelder til r
                  r = r.venstre;
              }

              p.verdi = r.verdi;   // kopierer verdien i r til p


              if(r.høyre == null && r.venstre == null) {
                  if(s.høyre == r) {
                      s.høyre = null;
                      break;
                  } else {
                      s.venstre = null;
                      break;
                  }
              }

              if(s != p && (r.høyre == null || r.venstre == null)) {
                  s.venstre = r.høyre;
                  r.høyre.forelder = s;
              }
              else {
                  s.høyre = r.høyre;
                  r.høyre.forelder = s;
              }
          }
          antall--;
          fjernet++;
      }

      return fjernet;
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
  public void nullstill() {
      java.util.Deque<Node<T>> fifo_queue = new java.util.ArrayDeque<Node<T>>();

      fifo_queue.addFirst(rot);
      Node<T> current = new Node<T>(null,null,null,null);


      while(fifo_queue.size() > 0) {

          current = fifo_queue.removeLast();

          if(current.venstre != null) {
              fifo_queue.addFirst(current.venstre);

          }
          if(current.høyre != null) {
              fifo_queue.addFirst(current.høyre);
          }
          current.verdi = null;
          current.forelder = null;
      }
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
    String ut = "[";

    Node p = rot;

    if(p == null) {
      return "[]";
    }

    while(p != null) {
      ut += p;
      if(p.venstre == null && p.høyre == null) {
        ut += "]";
        break;
      }
      else if(p.høyre == null) {
        p = p.venstre;
        ut += ", ";
      } else if(p.høyre != null){
        p = p.høyre;
        ut += ", ";
      }
    }
    return ut;
  }
  
  public String lengstGren(){
    throw new UnsupportedOperationException("Ikke kodet ennå!");
  }
  public String[] grener(){
    if(tom()) return new String[0];

    ArrayList<String> liste = new ArrayList<>();
    Stack<Node> stack = new Stack<>();
    Node<T> p = rot;
    Node<T> forrige = null;

    do{
      while(p != null){
        stack.push(p);
        p = p.venstre;
      }

      while(p == null && !stack.isEmpty()){
        p = stack.peek();

        if(p.venstre == null && p.høyre == null){
          Iterator<Node> ite = stack.iterator();
          StringBuffer sb = new StringBuffer();

          String prefix = "[";
          while(ite.hasNext()){
            sb.append(prefix);
            prefix = ", ";
            sb.append(ite.next().verdi);
          }
          String subfix = "]";
          sb.append(subfix);
          liste.add(sb.toString());
        }

        if(p.høyre == null || p.høyre == forrige){
          stack.pop();
          forrige = p;
          p = null;
        }else{
          p = p.høyre;
        }
      }
    } while(!stack.isEmpty());
    return liste.toArray(new String[0]);
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

  public static void main(String[] args) {
      int[] a = {6, 3, 9, 1, 5, 7, 10, 2, 4, 8, 11, 6, 8};
      ObligSBinTre<Integer> tre = new ObligSBinTre<>(Comparator.naturalOrder());
      for (int verdi : a) tre.leggInn(verdi);

      //System.out.println(tre.fjernAlle(4));
      System.out.println(tre);
      tre.fjern(6);
      System.out.println(tre);
    //  tre.fjernAlle(7);
   //   tre.fjern(8);
     // System.out.println("Antall : " + tre.antall);


     // System.out.println(tre + " " + tre.omvendtString());

     // tre.nullstill();


  }
} // ObligSBinTre
