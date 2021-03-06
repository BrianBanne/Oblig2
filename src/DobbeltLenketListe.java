////////////////// class DobbeltLenketListe //////////////////////////////


import java.util.*;


public class DobbeltLenketListe<T> implements Liste<T> {

    // instansvariabler
    private Node<T> hode;          // peker til den første i listen
    private Node<T> hale;          // peker til den siste i listen
    private int antall;            // antall noder i listen
    private int endringer;         // antall endringer i listen

    public DobbeltLenketListe() {

    }

    public DobbeltLenketListe(T[] a) {
        Objects.requireNonNull(a, "Tabellen a er null!!");
        if (a.length == 0) {
            hode = hale = null;
            return;
        }

        for (T t : a) {
            if (t == null) continue;
            if (tom()) hode = hale = new Node<>(t);

            else {
                Node<T> temp = hale;
                hale = new Node<>(t, temp, null);
                temp.neste = hale;
            }
            antall++;
        }
    }


        public static <T> void sorter(Liste<T> liste, Comparator<? super T> c) {
            if (liste.tom()) {
                throw new NoSuchElementException("Listen er tom");
            }
            Iterator<T> iterator = liste.iterator();
            T min = iterator.next();
            //Finner kun minimumsverdi
            while (iterator.hasNext()){
                T verdi = iterator.next();
                if (c.compare(verdi, min) < 0) min = verdi;
            }
        }

    private Node<T> finnNode(int indeks) {
        Node<T> current;
        if (indeks < (antall / 2)) {
            current = hode;
            for (int i = 0; i < indeks; i++) {
                current = current.neste;
            }
        } else {
            current = hale;
            for (int i = antall; i > indeks + 1; i--) {
                current = current.forrige;
            }
        }
        return current;
    }

    private void fraTilKontroll(int fra, int til, int antall) {
        if (fra < 0) throw new IndexOutOfBoundsException("Ugydlig fra-verdi");
        if (fra > til) throw new IllegalArgumentException("Til verdien kan ikke være mindre enn fra!");
        if (til > antall) throw new IndexOutOfBoundsException("Ugydlig til-verdi");
    }

    public Liste<T> subliste(int fra, int til) {
        fraTilKontroll(fra, til, antall);
        DobbeltLenketListe<T> sublist = new DobbeltLenketListe<>();

        for (int i = fra; i < til; i++) {
            Node<T> node = finnNode(i);
            sublist.leggInn(node.verdi);
        }
        return sublist;
    }

    @Override
    public int antall() {
        return antall;
    }

    @Override
    public boolean tom() {
        return antall == 0;
    }

    @Override
    public boolean leggInn(T verdi) {
        Objects.requireNonNull(verdi, "Ugyldig verdi");
        if (tom()) {
            hode = hale = new Node<>(verdi);
        } else {
            Node<T> temp = hale;
            hale = new Node<>(verdi, temp, null);
            temp.neste = hale;
        }
        antall++;
        endringer++;
        return true;
    }

    @Override
    public void leggInn(int indeks, T verdi) {
        Objects.requireNonNull(verdi, "Ugyldig verdi");
        indeksKontroll(indeks, true);

        if (indeks == 0) {
            hode = new Node<>(verdi, null, hode);
            if (antall == 0) hale = hode;
            else hode.neste.forrige = hode;

        }
        else if (indeks == antall) {
            hale = hale.neste = new Node<>(verdi, hale, null);
        }
        else {
            Node<T> current = hode;
            for (int i = 1; i < indeks; i++) {
                current = current.neste;
            }
            Node<T> nyNode = new Node<>(verdi, current, current.neste);
            current.neste.forrige = nyNode;
            current.neste = nyNode;
        }
        antall++;
        endringer++;
    }

    @Override
    public boolean inneholder(T verdi) {
        return indeksTil(verdi) != -1;
    }

    @Override
    public T hent(int indeks) {
        indeksKontroll(indeks, false);
        return finnNode(indeks).verdi;
    }

    @Override
    public int indeksTil(T verdi) {
        boolean inneholderVerdi = false;
        int indeks = 0;
        Node<T> temp = hode;
        while (temp != null) {
            if (temp.verdi.equals(verdi)) {
                inneholderVerdi = true;
                break;
            }
            temp = temp.neste;
            indeks++;
        }
        return inneholderVerdi ? indeks : -1;
    }

    @Override
    public T oppdater(int indeks, T nyverdi) {
        indeksKontroll(indeks, false);
        Objects.requireNonNull(nyverdi);

        Node<T> origNode = finnNode(indeks);
        T temp = origNode.verdi;
        origNode.verdi = nyverdi;

        endringer++;
        return temp;
    }

    @Override
    public boolean fjern(T verdi) {
        if (verdi == null) return false;
        Node<T> temp = hode;
        while (temp != null) {
          if (temp.verdi.equals(verdi)) break;
            temp = temp.neste;
        }

        if (temp == null) return false;
        else if (temp == hode){
            if (antall== 1) hode = hale = null;
            else {
                hode = hode.neste;
                hode.forrige = null;
            }
        }
        else if (temp == hale) {
            hale = hale.forrige;
            hale.neste = null;
        }
        else {
            temp.forrige.neste = temp.neste;
            temp.neste.forrige = temp.forrige;
        }
        antall--;
        endringer++;

        return true;
    }

    @Override
    public T fjern(int indeks) {
        indeksKontroll(indeks, false);
        T verdi;
        if (indeks == 0) {
            verdi = hode.verdi;
            if (antall == 1) hode = hale = null;
            else {
                hode = hode.neste;
                hode.forrige = null;
            }
        } else {
            Node<T> pre = finnNode(indeks - 1);
            Node<T> q = pre.neste;
            verdi = q.verdi;

            if (q == hale) {
                hale = pre;
                hale.neste = null;
                hale.forrige = pre.forrige;
            } else {
                Node<T> post = q.neste;
                post.forrige = q.forrige;
                pre.neste = q.neste;
            }
        }
        antall--;
        endringer++;
        return verdi;
    }

    @Override
    public void nullstill() {
        //METODE 1
        for (int i = 1; i < antall; i++) {
            hode.verdi = null;
            hode.forrige = null;
            hode = hode.neste;
        }

        //Metode 2 ikke like effektiv
        /*int indeks = 0;
        while (!tom()) {
            fjern(0);
        }*/

        hode = hale = null;
        antall = 0;
        endringer++;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        Node<T> temp = hode;
        while (temp != null) {
            sb.append(temp.verdi).append(", ");
            temp = temp.neste;
        }
        if (!tom()) sb.deleteCharAt(sb.length() - 1).deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }

    public String omvendtString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        Node<T> temp = hale;

        while (temp != null) {
            sb.append(temp.verdi).append(", ");
            temp = temp.forrige;
        }
        if (!tom()) sb.deleteCharAt(sb.length() - 1).deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();

    }

    @Override
    public Iterator<T> iterator() {
        return new DobbeltLenketListeIterator();
    }

    public Iterator<T> iterator(int indeks) {
        indeksKontroll(indeks, false);
        return new DobbeltLenketListeIterator(indeks);
    }

    /**
     * Node class
     *
     * @param <T>
     */
    private static final class Node<T> {
        private T verdi;                   // nodens verdi
        private Node<T> forrige, neste;    // pekere

        private Node(T verdi, Node<T> forrige, Node<T> neste) {
            this.verdi = verdi;
            this.forrige = forrige;
            this.neste = neste;
        }

        private Node(T verdi) {
            this(verdi, null, null);
        }
    }

    private class DobbeltLenketListeIterator implements Iterator<T> {
        private Node<T> denne;
        private boolean fjernOK;
        private int iteratorendringer;

        private DobbeltLenketListeIterator() {
            denne = hode;     // p starter på den første i listen
            fjernOK = false;  // blir sann når next() kallesteller endringer
            iteratorendringer = endringer;  //
        }

        private DobbeltLenketListeIterator(int indeks) {
            denne = finnNode(indeks);
            fjernOK = false;  // blir sann når next() kalles
            iteratorendringer = endringer;  // teller endringer
        }

        @Override
        public boolean hasNext() {
            return denne != null;
        }

        @Override
        public T next() {
            if (iteratorendringer != endringer) {
                throw new ConcurrentModificationException("Endringer samsvarer ikke");
            }
            if (!hasNext()) {
                throw new NoSuchElementException("Ikke flere elementer i listen");
            }
            fjernOK = true;
            T verdi = denne.verdi;
            denne = denne.neste;
            return verdi;
        }

        @Override
        public void remove() {
            if (!fjernOK) throw new IllegalStateException("Ikke tillat å kalle metoden");
            if (iteratorendringer != endringer) {
                throw new ConcurrentModificationException("Endringer samsvarer ikke");
            }
            fjernOK = false;

            if (antall == 1) {
                hode = hale = null;
            }
            else if (denne == null) {
                hale = hale.forrige;
                hale.neste = null;
            }
            else if (denne.forrige == hode) {
                hode = hode.neste;
                hode.forrige = null;
            }
            else {
                denne.forrige.forrige.neste = denne.forrige.neste;
                denne.forrige = denne.forrige.forrige;
            }
            antall--;
            endringer++;
            iteratorendringer++;
        }
    }// class DobbeltLenketListeIterator
} // class DobbeltLenketListe


