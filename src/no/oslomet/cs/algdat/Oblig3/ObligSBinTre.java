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
          } else if(p.venstre != null){
              q.venstre = p.venstre;
              b.forelder = p.forelder;
              p = null;
          } else if(p.høyre != null) {
              q.venstre = p.høyre;
              b.forelder = p.forelder;
              p = null;
          }
      } else {
        if(b == null) {
            q.høyre = p.høyre;
            p = null;
        } else if(p.høyre != null){
            q.høyre = p.høyre;
            b.forelder = p.forelder;
            p = null;
        } else if(p.venstre != null) {
            q.høyre = p.venstre;
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
          if(r.høyre == null && r.venstre == null) {
              s.venstre = null;
              r = null;
          } else {
              r.høyre.forelder = r.forelder;
              s.venstre = r.høyre;
          }
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
      if(!(inneholder(verdi))) {
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

      if(tom()) {
          return;
      }

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
          antall--;
          endringer++;

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
    //fra riktig node
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

    Stack<Node> stack = new Stack<>();
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
      String ut = "[";  //Ferdi ut med klammer []
      String nåværendeGren = "";   //Den som holder på den nyeste grenen
      int tellerNåværende = 0;

      String lengsteGren = ""; //Den grenen som er lengst, inneholder kun node verdier
      int tellerLengste = 0;

      Node<T> forlattPosisjon;  //Noden du "hoppet av" for å finne en gren
      Node<T> p = rot;  //Den "vanlige" pekeren som går In-Order gjennom treet
      Node<T> q = rot;  //Pekeren som KUN blir brukt når vi beveger oss fra blad node til rot node

      if(tom()) return "[]";

      //Finner første node for In-Order
      while(p.venstre != null){
          p = p.venstre;
      }

      //Traverserer gjennom treet In-Order
      while(nesteInorden(p) != null){

        //Hvis det finnes en blad node så starter vi å følge grenen opp, tar med verdiene og merker der vi forlot.
        if(p.venstre == null && p.høyre == null){
            q = p;
            nåværendeGren += q.verdi;
            tellerNåværende++;

            //Går oppover grenen til rot
            while(q.forelder != null){
                q = q.forelder;
                nåværendeGren += q.verdi;
                tellerNåværende++;
            }

            //Sjekker om denne grenen er lenger enn vår lengste, og returnerer til blad noden vi var på tidligere
            if(tellerNåværende > tellerLengste){
                lengsteGren = nåværendeGren;
                tellerLengste = tellerNåværende;

                nåværendeGren = "";
                tellerNåværende = 0;
            }else{
                nåværendeGren = "";
                tellerNåværende = 0;
            }
        }
        p = nesteInorden(p);
      }

      //Lager fin ut string, går i motsatt rekkefølge gjennom lengste gren
      for(int i = lengsteGren.length() - 1; i >= 0;i--){
            ut += lengsteGren.charAt(i) + ", ";
      }

      //Klipper av ekstra ", " på slutten hvis det er der
      if(lengsteGren.length() > 1){
          ut = ut.substring(0, ut.length() - 2);
      }
      //Hvis det kun er en node
      else if(antall() == 1){
          ut += p.verdi;
      }

      return ut += "[]";
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
          StringBuilder sb = new StringBuilder();

          String sbFør = "[";
          while(ite.hasNext()){
            sb.append(sbFør);
            sbFør = ", ";
            sb.append(ite.next().verdi);
          }
          String sbEtter = "]";
          sb.append(sbEtter);
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
  
  public String bladnodeverdier() {
    if(tom()) return"[]";

    Node<T> p = rot;
    String ut = "[";

    while(p.venstre != null){
      p = p.venstre;
    }

    if(p.venstre == null && p.høyre == null){
      ut += p.verdi + ", ";
    }

    while(nesteInorden(p) != null){
      p = nesteInorden(p);

      //blad
      if(p.venstre == null && p.høyre == null){
        ut += p.verdi + ", ";
      }

    }
      ut = ut.substring(0, ut.length() - 2);
    return ut += "]";
  }
  
  public String postString() {
    if(tom()) return "";

    String ut = "[";
    Deque<Node> stack = new ArrayDeque<>();
    Node<T> p = rot;

    while(p.venstre != null || p.høyre != null){
        if(p.venstre != null){
            p = p.venstre;
        }else if (p.høyre != null){
            p = p.høyre;
        }
    }

    ut += p.verdi + ", ";

    while(true){
        if(p == rot){
            break;
        }
        if(p == p.forelder.høyre || p.forelder.høyre == null){
            p = p.forelder;
        }else {
            p = p.forelder.høyre;

            while(true){
                if(p.venstre != null) {
                    p = p.venstre;
                } else if(p.høyre != null) {
                    p = p.høyre;
                }else break;
            }
        }
        ut += p.verdi + ", ";
    }
    ut = ut.substring(0, ut.length() -2);
    return ut += "]";
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
    
    private BladnodeIterator(){
        if(tom()){
            p = p;
        }else{
            while(p.venstre != null || p.høyre != null){
                if(p.venstre != null){
                    p = p.venstre;
                }else if (p.høyre != null){
                    p = p.høyre;
                }
            }
        }
    }
    
    @Override
    public boolean hasNext()
    {
      return p != null;  // Denne skal ikke endres!
    }
    
    @Override
    public T next(){
     if(!hasNext()){
         throw new NoSuchElementException();
     }
     removeOK = true;



     T verdi = p.verdi;

     while(nesteInorden(p) != null){
         //blad
         if(p.venstre == null && p.høyre == null){
             verdi = p.verdi;
             q = p;
             p = nesteInorden(p);
             break;
         }
         p = nesteInorden(p);
     }
     p = nesteInorden(p);

     return verdi;
    }
    
    @Override
    public void remove() {
     if(!removeOK){
         throw  new IllegalStateException();
     }
     removeOK = false;

     if(q.forelder.venstre == q){
         q = q.forelder;
         q.venstre = null;
     }

     if(q.forelder.høyre == q){
        q = q.forelder;
        q.høyre = null;
     }

    }

  } // BladnodeIterator

  public static void main(String[] args) {
    ObligSBinTre<Character> tre = new ObligSBinTre<>(Comparator.naturalOrder());
    char[] verdier ="IATBHJCRSOFELKGDMPQN".toCharArray();
    for(char c : verdier) tre.leggInn(c);

    while(!tre.tom()){
        System.out.println(tre);
        tre.fjernHvis(x -> true);
    }


  }
} // ObligSBinTre
